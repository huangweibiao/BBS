package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.common.JwtUtils;
import com.bbs.common.RedisUtils;
import com.bbs.dto.LoginResponseVO;
import com.bbs.dto.RegisterRequestDTO;
import com.bbs.dto.UserVO;
import com.bbs.entity.User;
import com.bbs.mapper.UserMapper;
import com.bbs.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 实现用户注册、登录、资料更新等业务逻辑
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private JwtUtils jwtUtils;
    
    @Resource
    private RedisUtils redisUtils;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * MD5加密（模拟，实际生产应使用BCrypt）
     */
    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequestDTO registerRequest) {
        // 检查用户名是否存在
        if (checkUsernameExists(registerRequest.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (checkEmailExists(registerRequest.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(md5(registerRequest.getPassword()));
        user.setNickname(registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail());
        user.setRole(0);  // 默认普通用户
        user.setPoints(0);
        user.setPostCount(0);
        user.setReplyCount(0);
        user.setStatus(1);  // 正常状态
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 插入数据库
        userMapper.insert(user);
        
        return UserVO.fromEntity(user);
    }
    
    @Override
    public LoginResponseVO login(String username, String password, String ip) {
        // 根据用户名或邮箱查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .or()
                .eq(User::getEmail, username);
        User user = userMapper.selectOne(wrapper);
        
        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        if (!md5(password).equals(user.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        
        // 将Token存入Redis（24小时过期）
        String tokenKey = "token:" + user.getId();
        redisUtils.set(tokenKey, token, 24, TimeUnit.HOURS);
        
        // 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 构建响应
        LoginResponseVO response = new LoginResponseVO();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setRole(user.getRole());
        response.setPoints(user.getPoints());
        
        return response;
    }
    
    @Override
    public UserVO getUserById(Long userId) {
        // 先从Redis获取
        String userKey = "user:" + userId;
        Object cachedUser = redisUtils.get(userKey);
        if (cachedUser != null) {
            return (UserVO) cachedUser;
        }
        
        // 从数据库获取
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        UserVO vo = UserVO.fromEntity(user);
        
        // 存入Redis（24小时过期）
        redisUtils.set(userKey, vo, 24, TimeUnit.HOURS);
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(Long userId, String nickname, String avatar, String email) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 如果修改了邮箱，检查邮箱是否已被占用
        if (email != null && !email.equals(user.getEmail())) {
            if (checkEmailExists(email)) {
                throw new BusinessException("邮箱已被占用");
            }
            user.setEmail(email);
        }
        
        // 更新字段
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.updateById(user);
        
        // 清除Redis缓存
        redisUtils.delete("user:" + userId);
        
        return UserVO.fromEntity(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String signIn(Long userId) {
        // 检查今天是否已签到
        String signKey = "sign:" + userId + ":" + LocalDate.now();
        Boolean hasSign = redisUtils.hasKey(signKey);
        
        if (hasSign != null && hasSign) {
            throw new BusinessException("今日已签到");
        }
        
        // 签到成功，增加积分
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setPoints(user.getPoints() + 1);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 记录签到（24小时后过期）
        redisUtils.setEx(signKey, "1", 86400);
        
        // 清除用户缓存
        redisUtils.delete("user:" + userId);
        
        return "签到成功，积分+1，当前积分：" + user.getPoints();
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username)
        );
        return count > 0;
    }
    
    @Override
    public boolean checkEmailExists(String email) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email)
        );
        return count > 0;
    }
}