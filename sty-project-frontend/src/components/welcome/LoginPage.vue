<template>
  <div style="text-align: center; margin: 0 3vw">
    <div style="margin-top: 150px">
      <div style="font-size: 25px; font-weight: bold">登录</div>
      <div style="font-size: 14px; color: grey">在进入系统前需要登录</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" ref="formRef" :rules="rules">
        <el-form-item prop="usernameOrEmail">
          <el-input v-model="form.usernameOrEmail" maxlength="20" type="text" placeholder="用户名/邮箱">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" maxlength="20" style="margin-top: 10px" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-row style="margin-top: 5px">
          <el-col :span="12" style="text-align: left">
            <el-form-item prop="remember">
              <el-checkbox v-model="form.remember" label="记住我"></el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="12" style="text-align: right">
            <el-link @click="router.push('/forget')">忘记密码?</el-link>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div style="margin-top: 4vh">
      <el-button @click="doLogin(formRef)" type="success" style="width: 270px" plain>
        立即登录
      </el-button>
    </div>
    <el-divider>
      <span style="color: grey; font-size: 13px">没有账号</span>
    </el-divider>
    <div>
      <el-button style="width: 270px" type="warning" @click="router.push('/register')" plain>
        注册账号
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import {User, Lock} from '@element-plus/icons-vue'
import router from "@/router";
import {login} from "@/net";
import type {FormInstance} from "element-plus";

interface LoginForm {
  usernameOrEmail: string
  password: string
  remember: boolean
}

const form = reactive<LoginForm>({
  usernameOrEmail: '',
  password: '',
  remember: false
})

const doLogin = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) login(form.usernameOrEmail, form.password, form.remember, () => {
      router.push('/index')
    })
  })

}

const rules = {
  usernameOrEmail: [
    {required: true, message: '请输入用户名'}
  ],
  password: [
    {required: true, message: '请输入密码'}
  ]
}

const formRef = ref<FormInstance>()


</script>

<style scoped>

</style>