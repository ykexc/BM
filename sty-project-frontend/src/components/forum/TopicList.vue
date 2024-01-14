<template>

  <div style="display: flex; margin: 20px auto; gap: 20px; max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon>
            <EditPen/>
          </el-icon>
          点击发表主题...
        </div>
      </light-card>
      <light-card style="height: 10px; margin-top: 10px"></light-card>
      <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 10px">
        <light-card style="height: 150px" v-for="item in 10"></light-card>
      </div>

    </div>
    <div style="width: 280px">
      <div style="position: sticky; top: 20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon>
              <CollectionTag/>
            </el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px; margin: 10px; color: grey">
            《王者荣耀》是由腾讯游戏天美工作室群开发并运营在Android、IOS平台上的MOBA类国产手游
            ，于2015年11月26日在Android、iOS平台上正式公测，游戏曾经使用名称有《英雄战迹》、
            《王者联盟》。《王者荣耀》的外服版本为《传说对决》（Arena Of Valor）。
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon>
              <Calendar/>
            </el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <Weather :data="weather"/>
        </light-card>
        <light-card>
          <div class="info-text">
            <div>当前日期</div>
            <div>{{ today }}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px; margin-top: 10px; color: grey">
          <el-icon>
            <link/>
          </el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid; grid-template-columns: repeat(2, 1fr); grid-gap: 10px; margin-top: 10px">

        </div>
      </div>
    </div>
    <TopicEditor
        @success="editor = false"
        @close="editor = false"
         :show="editor"/>
  </div>


</template>


<script setup lang="ts">
import {CollectionTag, Calendar, EditPen} from '@element-plus/icons-vue'
import LightCard from "@/components/LightCard.vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";


const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()}  日`
})

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})

const editor = ref(false)

navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时,请检查是否开启或网络是否正常')
  get(`/api/forum/weather?longitude=116.40529&latitude=39.90499`, data => {
    Object.assign(weather, data)
    weather.success = true
  })
}, {
  timeout: 6000,
  enableHighAccuracy: true
})


</script>

<style scoped lang="less">
.info-text {
  display: flex;
  justify-content: space-between;
  color: #fd4072;
  font-size: 14px;
}


.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;
  color: var(--el-border-color);

  &:hover {
    cursor: pointer;
  }


}

</style>