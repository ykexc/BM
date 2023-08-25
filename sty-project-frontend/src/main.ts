import { createApp } from 'vue'
import { createPinia } from 'pinia'
import axios from "axios";
import App from './App.vue'
import router from './router'
import 'element-plus/theme-chalk/dark/css-vars.css'
import 'element-plus/dist/index.css'
const app = createApp(App)
axios.defaults.baseURL = 'http://localhost:3000'
app.use(createPinia())
app.use(router)

app.mount('#app')
