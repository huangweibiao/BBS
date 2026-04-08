import { defineStore } from 'pinia'
import { getForums as getForumsApi } from '@/api/forum'

export const useForumStore = defineStore('forum', {
  state: () => ({
    forums: [],
    currentForum: null
  }),
  
  getters: {
    // 获取顶级版块
    topLevelForums: state => state.forums.filter(f => !f.parentId || f.parentId === 0),
    // 获取版块树
    forumTree: state => state.forums
  },
  
  actions: {
    // 获取版块列表
    async fetchForums() {
      try {
        const res = await getForumsApi()
        this.forums = res.data || []
        return this.forums
      } catch (error) {
        console.error('获取版块列表失败', error)
        return []
      }
    },
    
    // 设置当前版块
    setCurrentForum(forum) {
      this.currentForum = forum
    }
  }
})