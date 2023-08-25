import axios from "axios";
import {ElMessage} from "element-plus";

const authItemName = "authorize"

const accessHeader = () => {
    return {
        'Authorization': `Bearer ${takeAccessToken()}`
    }
}

const defaultError = (error: Error) => {
    console.error(error)
    ElMessage.error('发生了一些错误，请联系管理员')
}

const defaultFailure = (message: string, status: number, url: string) => {
    console.warn(`请求地址: ${url}, 状态码: ${status}, 错误信息: ${message}`)
    ElMessage.warning(message)
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if (!str) return null
    const authObj = JSON.parse(str)
    if (authObj.expire <= new Date()) {
        deleteAccessToken()
        ElMessage.warning("登录状态已过期，请重新登录！")
        return null
    }
    return authObj.token
}

function storeAccessToken(remember: boolean, token: string, expire: Date) {
    const authObj = {
        token: token,
        expire: expire
    }
    const str = JSON.stringify(authObj)
    if (remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function internalPost(url: string, data: any, headers: any, success: (e: any) => void, failure: (msg: string, code: number, url: string) => void, error = defaultError) {
    axios.post(url, data, {headers: headers}).then(({data}) => {
        if (data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}

function internalGet(url: string, headers: any, success: (e: any) => void, failure: (msg: string, code: number, url: string) => void, error = defaultError) {
    axios.get(url, {headers: headers}).then(({data}) => {
        if (data.code === 200)
            success(data.data)
        else
            failure(data.message, data.code, url)
    }).catch(err => error(err))
}

function login(username: string, password: string, remember: boolean, success: (e: any) => void, failure = defaultFailure) {
    internalPost('/api/auth/login', {
        username: username,
        password: password
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(remember, data.token, data.expire)
        ElMessage.success(`登录成功，欢迎 ${data.username} 来到我们的系统`)
        success(data)
    }, failure)
}

function post(url: string, data: any, success: (e: any) => void, failure = defaultFailure) {
    internalPost(url, data, accessHeader(), success, failure)
}

function logout(success: () => void, failure = defaultFailure) {
    get('/api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success(`退出登录成功，欢迎您再次使用`)
        success()
    }, failure)
}

function get(url: string, success: (e: any) => void, failure?: any) {
    internalGet(url, accessHeader(), success, failure ? failure : defaultFailure)
}

function unauthorized() {
    return !takeAccessToken()
}

export {post, get, login, logout, unauthorized}
