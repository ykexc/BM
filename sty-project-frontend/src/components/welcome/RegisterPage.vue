<template>
  <div style="margin: 0 20px; text-align: center">
    <div style="margin-top: 100px">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px; color: grey">欢迎注册我们平台,请填写下方相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" ref="formRef" @validate="onValidate" :rules="rules">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="8" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="16" type="password" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="passwordRepeat">
          <el-input v-model="form.passwordRepeat" maxlength="16" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="电子邮件地址">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="18">
              <el-input v-model="form.code" maxlength="6" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon>
                    <EditPen/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="6">
              <el-button @click="validateEmail" type="success" :disabled="!emailIsValid || coldTime > 0">
                {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : "获取验证码" }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
    </div>
    <div style="margin-top: 80px">
      <el-button style="width: 270px" type="warning" plain @click="doRegister(formRef)">
        立即注册
      </el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px; line-height: 15px; color: grey">已有账号</span>
      <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import {User, Lock, Message, EditPen} from '@element-plus/icons-vue'
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
import router from "@/router";
import type {FormInstance, FormItemProp} from "element-plus";

const form = reactive({
  username: '',
  password: '',
  passwordRepeat: '',
  email: '',
  code: ''
})

const coldTime = ref<number>(0)

const emailIsValid = ref<boolean>(false)

const onValidate = (prop: FormItemProp, isValid: boolean) => {
  if (prop === 'email')
    emailIsValid.value = isValid
}

const validateEmail = () => {

  get(`/api/auth/ask-code?email=${form.email}&type=register`, (e: string) => {
    coldTime.value = 60
    ElMessage.success(`验证码已发送至邮箱${form.email}, 请注意查收`)
    setInterval(() => coldTime.value--, 1000)
  })
}

const usernameValidatePass = (rule: any, value: any, callback: any) => {
  if (value === '')
    callback(new Error('请输入用户名'))
  else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value))
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  else callback()
}

const passwordRepeatValidatePass = (rule: any, value: any, callback: any) => {
  if (value === '')
    callback(new Error('请输入密码'))
  else if (value !== form.password)
    callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [
    {validator: usernameValidatePass, trigger: ['blur', 'change']},
    {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  passwordRepeat: [
    {validator: passwordRepeatValidatePass, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法电子邮件地址', trigger: 'blur'}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'true'},
  ]
}

const doRegister = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((isValid) => {
    if (isValid) {
      post('/api/auth/register', {
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code
      }, () => {
        ElMessage.success('注册成功!')
        router.push('/')
      })
    } else {
      ElMessage.warning('请填写注册信息')
    }
  })
}


const formRef = ref()

</script>

<style scoped>

</style>