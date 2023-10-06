import {createRouter, createWebHistory} from 'vue-router'
import {authorized} from "@/net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/components/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/components/welcome/RegisterPage.vue')
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/components/welcome/ForgetPage.vue')
                },

            ]
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue'),
            children: [

                {
                    path: '',
                    name: 'topic-list',
                    component: () => import('@/components/forum/TopicList.vue')
                },

                {
                    path: 'user-setting',
                    name: 'user-setting',
                    component: () => import('@/components/settings/UserSetting.vue')
                },
                {
                    path: 'privacy-setting',
                    name: 'privacy-setting',
                    component: () => import('@/components/settings/PrivacySetting.vue')
                },
            ]
        }
    ]
})


router.beforeEach((to, _from, next) => {
    const isUnauthorized: boolean = authorized()
    if (to?.name?.toString().startsWith('welcome') && isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && !isUnauthorized) {
        next('/')
    } else next()
})


export default router
