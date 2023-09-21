import {defineStore} from 'pinia'
import axios from "axios";


interface User {
    username: string
    email: string
    role: string
    registerTime: Date | null
    avatar: string | null
}

export default defineStore('user', {
    state: (): User => {
        return {
            username: '',
            email: '',
            role: '',
            registerTime: null,
            avatar: null
        }
    },

    getters: {
        avatarUrl(): string | undefined {
            if (this.avatar) return `${axios.defaults.baseURL}/images${this.avatar}`
            else return 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
    },
    actions: {
        updateInfo(user: User) {
            this.username = user.username
            this.email = user.email
            this.role = user.role
            this.registerTime = user.registerTime
            this.avatar = user.avatar
        }
    }
})
