<template>

  <div class="card">
    <div class="card-header" v-if="props.title">
      <div>
        <el-icon style="margin-right: 3px; translate: 0 2px">
          <component :is="props.icon"/>
        </el-icon>
        {{props.title}}
      </div>
      <div>{{props.desc}}</div>
    </div>
    <slot/>
  </div>


</template>

<script setup lang="ts">

const props = defineProps({
  icon: {type: Object, required: false},
  title: {type: String, required: false},
  desc: {type: String, required: false}
})
</script>

<style scoped>

.card {
  border-radius: 5px;
  border: solid 1px var(--el-border-color);
  background-color: var(--el-bg-color);
  box-sizing: border-box;
  min-height: 20px;
  padding: 10px;

}

.card-header {
  border-bottom: solid 1px var(--el-border-color);
  padding-bottom: 5px;
  margin-bottom: 10px;

  &>:first-child {
    font-size: 18px;
    font-weight: bold;
  }

  &>:last-child {
    font-size: 13px;
    color: grey;
  }
}

</style>