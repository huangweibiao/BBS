package com.bbs.service;

import com.bbs.dto.LoginResponseVO;
import com.bbs.dto.RegisterRequestDTO;
import com.bbs.dto.UserVO;

/**
 * 用户服务接口
 * 定义用户相关的业务操作
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 注册后的用户信息
     */
    UserVO register(RegisterRequestDTO registerRequest);
    
    /**
     * 用户登录
     * @param username 用户名或邮箱
     * @param password 密码
     * @param ip 登录IP
     * @return 登录响应（包含Token）
     */
    LoginResponseVO login(String username, String password, String ip);
    
    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);
    
    /**
     * 更新用户资料
     * @param userId 用户ID
     * @param nickname 昵称（可选）
     * @param avatar 头像URL（可选）
     * @param email 邮箱（可选）
     * @return 更新后的用户信息
     */
    UserVO updateProfile(Long userId, String nickname, String avatar, String email);
    
    /**
     * 每日签到
     * @param userId 用户ID
     * @return 签到结果信息
     */
    String signIn(Long userId);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true:存在 false:不存在
     */
    boolean checkUsernameExists(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return true:存在 false:不存在
     */
    boolean checkEmailExists(String email);
}