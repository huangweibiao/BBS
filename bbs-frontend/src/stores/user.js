import { defineStore } from 'pinia'
import { login as loginApi, register as registerApi, getUserProfile, signIn as signInApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),
  
  getters: {
    isLoggedIn: state => !!state.token,
    userId: state => state.userInfo?.id,
    username: state => state.userInfo?.username,
    nickname: state => state.userInfo?.nickname,
    avatar: state => state.userInfo?.avatar,
    role: state => state.userInfo?.role,  // 0普通用户 1版主 2管理员
    isAdmin: state => state.userInfo?.role >= 2,
    isModerator: state => state.userInfo?.role >= 1
  },
  
  actions: {
    // 用户登录
    async login(credentials) {
      const res = await loginApi(credentials)
      this.token = res.data.token
      this.userInfo = res.data
      localStorage.setItem('token', this.token)
      return res
    },
    
    // 用户注册
    async register(data) {
      const res = await registerApi(data)
      return res
    },
    
    // 获取用户信息
    async fetchUserInfo() {
      if (!this.token) return null
      try {
        const res = await getUserProfile()
        this.userInfo = res.data
        return res.data
      } catch (error) {
        this.logout()
        return null
      }
    },
    
    // 签到
    async signIn() {
      const res = await signInApi()
      // 签到成功后刷新用户信息
      await this.fetchUserInfo()
      return res
    },
    
    // 登出
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})