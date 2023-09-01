<template>
  <div style="margin: auto; max-width: 600px">
    <div style="margin-top: 20px">
      <Card :icon="Setting" title="隐私设置" desc="在这里可以设置您的隐私可见性">
        <div class="checkbox-list">
          <el-checkbox>公开展示我的手机号</el-checkbox>
          <el-checkbox>公开展示我的电子邮件地址</el-checkbox>
          <el-checkbox>公开展示我的微信号</el-checkbox>
          <el-checkbox>公开展示我的QQ号</el-checkbox>
          <el-checkbox>公开展示我的性别</el-checkbox>
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
import {reactive, ref} from "vue";
import type {FormItemProp, FormInstance} from "element-plus";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const passwordIsValid = ref<boolean>(false)
const formRef = ref()
const changePasswordForm = reactive({
  password: '',
  newPassword: '',
  repeatNewPassword: ''
})

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