<template>

  <div>
    <div style="margin: 30px 20px">
      <el-steps :active="active" align-center finish-status="success">
        <el-step title="验证电子邮件"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style="text-align: center; margin: 0 30px;height: 100%" v-if="active === 0">
        <div style="margin-top: 80px">
          <div style="font-size: 25px;font-weight: bold">
            重置密码
          </div>
          <div style="font-size: 14px; color: grey">
            请输入重置密码的电子邮件地址
          </div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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
                <el-col :span="17">

                  <el-input v-model="form.code" placeholder="请输入验证码">
                    <template #prefix>
                      <el-icon>
                        <EditPen/>
                      </el-icon>
                    </template>
                  </el-input>

                </el-col>
                <el-col :span="7">
                  <el-button type="success" :disabled="!isEmailValid || coldTime > 0" @click="validateEmail">
                    {{ coldTime > 0 ? '请稍后' + coldTime + '秒' : '获取验证码' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button type="danger" style="width: 270px" plain @click="confirmReset(formRef)">开始重置密码</el-button>
        </div>
      </div>
    </transition>
    <transition name="el-fade-in-linear" mode="out-in">
      <div style=" margin: 0 30px;height: 100%" v-if="active === 1">
        <el-icon size="25px" style="cursor: pointer" @click="active ^= 1"><ArrowLeftBold /></el-icon>
        <div style="margin-top: 55px">
          <div style="font-size: 22px; text-align: center; font-weight: bold">
            重置密码
          </div>
          <div style="font-size: 14px; text-align: center; color: grey">
            请填写您的新密码，务必牢记，防止丢失
          </div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" :maxlength="16" placeholder="新密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="repeatPassword">
              <el-input v-model="form.repeatPassword" type="password" :maxlength="16" placeholder="重复新密码">
                <template #prefix>
                  <el-icon><Lock/></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
        <div style="margin-top: 70px; text-align: center">
          <el-button type="danger" style="width: 270px;" plain @click="doReset(formRef)">
            立即重置密码
          </el-button>
        </div>
      </div>
    </transition>
  </div>


</template>


<script setup lang="ts">
import {reactive, ref} from "vue";
import {Message, EditPen, ArrowLeftBold, Lock} from '@element-plus/icons-vue'
import type {FormItemProp, FormInstance} from "element-plus";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";


const formRef = ref()
const active = ref<number>(0)
const coldTime = ref<number>(0)
const form = reactive({
  email: '',
  code: '',
  password: '',
  repeatPassword: ''
})

const isEmailValid = ref(false)

const validateEmail = () => {
  get(`/api/auth/ask-code?email=${form.email}&type=reset`, (e: string) => {
    coldTime.value = 60
    ElMessage.success(`验证码已发送至邮箱${form.email}, 请注意查收`)
    setInterval(() => coldTime.value--, 1000)
  })
}

const passwordRepeatValidatePass = (rule: any, value: any, callback: any) => {
  if (value === '')
    callback(new Error('请输入密码'))
  else if (value !== form.password)
    callback(new Error('两次密码不一致'))
  else callback()
}

const onValidate = (prop: FormItemProp, isValid: boolean) => {
  if (prop === 'email')
    isEmailValid.value = isValid
}

const rules = {
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  repeatPassword: [
    {validator: passwordRepeatValidatePass, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法电子邮件地址', trigger: 'blur'}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'true'}
  ]
}

const confirmReset = (formEl: FormInstance | undefined) => {
  if (!formEl)
    return
  formEl.validate((isValid) => {
    if (isValid) {
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, () => {
        active.value ^= 1
      })
    }
    else ElMessage.warning('请填写合法验证信息')
  })
}


const doReset = (formEl: FormInstance | undefined) => {
  if (!formEl)
    return
  formEl.validate((isValid) => {
    if (isValid) {
      post('/api/auth/reset', {
        email: form.email,
        code: form.code,
        password: form.password
      }, () => {
        ElMessage.success('密码重置成功, 请重新登录')
        router.push('/')
      })
    } else {
      ElMessage.warning('请填写合法重置信息')
    }
  })
}

</script>


<style scoped>

</style>