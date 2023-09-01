<template>
  <div style="margin: auto; max-width: 600px" v-loading="loading">
    <div style="margin-top: 20px" v-loading="loading">
      <Card :icon="Setting" title="隐私设置" desc="在这里可以设置您的隐私可见性">
        <div class="checkbox-list">
          <el-checkbox
              @change="doChange('phone', privacyForm.phone)" v-model="privacyForm.phone"
          >公开展示我的手机号
          </el-checkbox>
          <el-checkbox
              v-model="privacyForm.email"
              @change="doChange('email', privacyForm.email)"
          >公开展示我的电子邮件地址
          </el-checkbox>
          <el-checkbox
              v-model="privacyForm.wx"
              @change="doChange('wx', privacyForm.wx)"
          >公开展示我的微信号
          </el-checkbox>
          <el-checkbox
              v-model="privacyForm.qq"
              @change="doChange('qq', privacyForm.qq)"
          >公开展示我的QQ号
          </el-checkbox>
          <el-checkbox
              v-model="privacyForm.gender"
              @change="doChange('gender', privacyForm.gender)"
          >公开展示我的性别
          </el-checkbox>
        </div>
      </Card>
      <Card style="margin: 20px 0" :icon="Setting" title="修改密码" desc="在这里修改密码">
        <el-form
            label-width="100"
            style="margin: 20px"
            :rules="rules"
            ref="formRef"
            :model="changePasswordForm"
            @validate="onValidate"
        >
          <el-form-item label="当前密码" prop="password">
            <el-input type="password" v-model="changePasswordForm.password" placeholder="当前密码"
                      maxlength="16" :prefix-icon="Lock"></el-input>
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input type="password" v-model="changePasswordForm.newPassword" placeholder="新密码"
                      maxlength="16" :prefix-icon="Lock"></el-input>
          </el-form-item>
          <el-form-item label="重复新密码" prop="repeatNewPassword">
            <el-input type="password" v-model="changePasswordForm.repeatNewPassword" placeholder="重复新密码"
                      maxlength="16" :prefix-icon="Lock"></el-input>
          </el-form-item>
          <div style="text-align: center">
            <el-button :disabled="!passwordIsValid" @click="doChangePassword(formRef)" :icon="Switch" type="success">
              立即重置密码
            </el-button>
          </div>
        </el-form>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {Lock, Setting, Switch} from '@element-plus/icons-vue'
import Card from "@/components/Card.vue";
import {onBeforeMount, reactive, ref} from "vue";
import type {FormItemProp, FormInstance} from "element-plus";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";
const passwordIsValid = ref<boolean>(false)
const formRef = ref()
const changePasswordForm = reactive({
  password: '',
  newPassword: '',
  repeatNewPassword: ''
})
const loading = ref<boolean>(true)
const privacyForm = reactive({
  phone: false,
  email: false,
  wx: false,
  qq: false,
  gender: false
})

onBeforeMount(() => {
  get('/api/user/privacy', (e) => {
    privacyForm.phone = e.phone
    privacyForm.email = e.email
    privacyForm.wx = e.wx
    privacyForm.qq = e.qq
    privacyForm.gender = e.gender
    loading.value = false
  })
})




type Type = 'phone' | 'email' | 'qq' | 'wx' | 'gender'

const doChange = (type: Type, flag: boolean) => {
  post('/api/user/save-privacy', {
    type: type,
    flag: flag
  }, () => {
    ElMessage.success('保存信息成功')
  }, (message) => {
    ElMessage.warning(message)
  })
}


const onValidate = (prop: FormItemProp, isValid: boolean) => {
  if (prop === 'password')
    passwordIsValid.value = isValid
}

const repeatPasswordValidator = (rule: any, value: any, callback: any) => {
  if (value == '')
    callback(new Error('请重复输入新密码'))
  else if (changePasswordForm.newPassword !== changePasswordForm.repeatNewPassword)
    callback(new Error('请输入同样的新密码'))
  else callback()
}

const doChangePassword = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(isValid => {
    if (isValid) {
      post('/api/user/change-password', {
        password: changePasswordForm.password,
        newPassword: changePasswordForm.newPassword
      }, () => {
        ElMessage.success('更新密码成功')
        formRef.value.resetFields()
      }, (message) => {
        ElMessage.warning(message)
      })
    }
  })
}

const rules = {
  password: [
    {required: true, message: '请输入当前密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码不合法', trigger: ['blur', 'change']}
  ],
  newPassword: [
    {required: true, message: '请输入新密码', trigger: 'blur'},
    {min: 6, max: 20, message: '密码不合法', trigger: ['blur', 'change']}
  ],
  repeatNewPassword: [
    {validator: repeatPasswordValidator, trigger: ['blur', 'change']}
  ]
}
</script>


<style scoped>
.checkbox-list {
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>