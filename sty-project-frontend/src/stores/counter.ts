import {defineStore} from 'pinia'


interface User {
    username: string
    email: string
    role: string
    registerTime: Date | null
}

export default defineStore('user', {
    state: (): User => {
        return {
            username: '',
            email: '',
            role: '',
            registerTime: null
        }
    },
    actions: {
      updateInfo(user: User) {
          this.username = user.username
          this.email = user.email
          this.role = user.role
          this.registerTime = user.registerTime
      }
    }
})
