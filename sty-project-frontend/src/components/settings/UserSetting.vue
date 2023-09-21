<template>
  <div style="display: flex; max-width: 950px; margin: auto" v-loading="loading1">
    <div class="setting-left">
      <Card
          :icon="User" title="账号信息设置"
          desc="在这里编辑您的账号个人信息"
      >
        <el-form label-position="top" style="margin: 0 10px 10px 10px" :rules="rules" :model="baseForm" ref="formRef0">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="10"/>
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :label="0">男</el-radio>
              >
              <el-radio :label="1">女</el-radio>
              >
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11" minlength="11"/>
          </el-form-item>
          <el-form-item label="QQ号">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="微信号" prop="wx">
            <el-input v-model="baseForm.wx" maxlength="20"/>
          </el-form-item>
          <el-form-item label="个人简介" prop="desc">
            <el-input v-model="baseForm.desc" maxlength="200" type="textarea" :rows="6"/>
          </el-form-item>
        </el-form>
        <div>
          <el-button
              style="margin-left: 10px"
              @click="doSaveBase(formRef0)"
              type="success" :icon="Select"
              :loading="loading2">保存用户信息
          </el-button>
        </div>
      </Card>
      <Card title="电子邮件设置" :icon="Message" desc="在这里设置您的账号个人信息" style="margin-top: 10px">
        <el-form @validate="onValidate" label-position="top" style="margin: 0 10px 10px 10px" :model="emailForm"
                 :rules="rules" ref="formRef1">
          <el-form-item label="电子邮件" prop="email">
            <el-input v-model="emailForm.email" placeholder="请先获取验证码"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input v-model="emailForm.code" placeholder="请获取验证码"/>
              </el-col>
              <el-col :span="6">
                <el-button :disabled="coldTime > 0" @click="sendCode" type="success" plain style="width: 100%">
                  {{ coldTime > 0 ? coldTime + '秒' : '请获取验证码' }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
        <div>
          <el-button
              style="margin-left: 10px"
              v-loading="loading3"
              @click="doSaveEmail(formRef1)"
              type="success" plain
              :icon="Refresh">更新电子邮件
          </el-button>
        </div>
      </Card>
    </div>
    <div class="setting-right">
      <el-affix :offset="75">
        <Card>
          <div style="text-align: center; padding: 5px 15px">
            <el-avatar :size="70" :src="userUseStore.avatarUrl"></el-avatar>
            <div style="margin: 5px 0">
              <el-upload :action="axios.defaults.baseURL + '/api/image/avatar'"
                         :show-file-list="false"
                         :before-upload="beforeAvatarUpload"
                         :on-success="uploadSuccess"
                         :headers="accessHeader()"
              >
                <el-button size="small" round>修改头像</el-button>
              </el-upload>
            </div>
            <div style="font-weight: bold">你好,{{ username }}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px; color: grey; padding: 10px">
            {{ desc || '这个用户很懒, 没有自我介绍' }}
          </div>
        </Card>
        <Card style="margin-top: 5px; font-size: 14px">
          <div>账号注册时间: {{ registerT }}</div>
          <div style="color: grey">欢迎加入我们学习论坛</div>
        </Card>
      </el-affix>
    </div>
  </div>
</template>

<script setup lang="ts">

import Card from "@/components/Card.vue";
import {
  Message, User, Select,
  Refresh
} from '@element-plus/icons-vue'
import userStore from '@/stores/counter'
import {storeToRefs} from "pinia";
import {computed, onMounted, reactive, ref} from "vue";
import type {FormItemProp, FormInstance, UploadRawFile, UploadFile, UploadFiles} from "element-plus";
import {accessHeader, get, post} from "@/net";
import {ElMessage} from "element-plus";
import axios from "axios";

const userUseStore = userStore()
const {username, registerTime, email} = storeToRefs(userUseStore)
const baseForm = reactive({
  username: '',
  gender: 1,
  phone: '',
  qq: '',
  wx: '',
  desc: ''
})
const desc = ref<string>()
const loading1 = ref<boolean>(true)
const loading2 = ref<boolean>(false)
const loading3 = ref<boolean>(false)
const emailIsValid = ref<boolean>(false)
const formRef1 = ref()
const formRef0 = ref()
const coldTime = ref<number>(0)

const emailForm = reactive({
  email: '',
  code: ''
})

const registerT = computed(() => {
  if (registerTime.value)
    return new Date(registerTime.value).toLocaleString()
  return ''
})


const usernameValidatePass = (rule: any, value: any, callback: any) => {
  if (value === '')
    callback(new Error('请输入用户名'))
  else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value))
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'))
  else callback()
}


const onValidate = (prop: FormItemProp, isValid: boolean) => {
  if (prop === 'email')
    emailIsValid.value = isValid
}

const rules = {
  username: [
    {validator: usernameValidatePass, trigger: ['blur', 'change']},
    {min: 2, max: 8, message: '用户名的长度必须在2-8个字符之间', trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法电子邮件地址', trigger: 'blur'}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'true'},
    {min: 6, max: 6, message: '请输入合法的6位数字验证码', trigger: ['blur', 'change']}
  ]
}


onMounted(() => {
  baseForm.username = username.value
  emailForm.email = email.value
  get('/api/user/details', (resp) => {
    baseForm.gender = resp.gender
    baseForm.wx = resp.wx
    baseForm.qq = resp.qq
    baseForm.phone = resp.phone
    baseForm.desc = resp.desc
    loading1.value = false
  })
})

const doSaveBase = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  loading2.value = true
  formEl.validate((isValid) => {
    if (isValid) {
      post('/api/user/save-details', baseForm,
          () => {
            userUseStore.username = baseForm.username
            desc.value = baseForm.desc
            ElMessage.success('更新信息成功')
            loading2.value = false
          }, (message) => {
            ElMessage.warning(message)
            loading2.value = false
          }
      )
    }
  })
}

const sendCode = () => {
  get(`api/auth/ask-code?email=${emailForm.email}&type=reset-email`, () => {
    coldTime.value = 60
    ElMessage.success(`验证码已发送至邮箱${emailForm.email}, 请注意查收`)
    setInterval(() => coldTime.value--, 1000)
  })
}

const doSaveEmail = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  loading3.value = true
  formEl.validate((isValid) => {
    if (isValid) {
      post('/api/user/save-email', emailForm,
          () => {
            userUseStore.email = emailForm.email
            ElMessage.success('更新信息成功')
            coldTime.value = 0
            loading3.value = false
            emailForm.code = ''
          }, (message) => {
            ElMessage.warning(message)
            loading3.value = false
          }
      )
    }
  })
}

const beforeAvatarUpload = (rawFile: UploadRawFile): boolean => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像只能为jpeg/png格式')
    return false
  } else if (rawFile.size / 1024 > 100) {
    ElMessage.error('头像大小不能大于100kb')
    return false
  }
  return true
}

const uploadSuccess = (response: any, _uploadFile: UploadFile, _uploadFiles: UploadFiles) => {
  ElMessage.success('头像上传成功')
  userUseStore.avatar = response.data

}

</script>


<style scoped>

.setting-left {
  flex: 1;
  margin: 20px;
}

.setting-right {
  width: 300px;
  margin: 20px 30px 30px 0;
}

</style>