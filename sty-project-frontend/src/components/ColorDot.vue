<template>
  <div class="dot" :style="{background: props.color}"></div>
</template>

<script setup lang="ts">

const props = defineProps({
  color: {type: String, required: true}
})

</script>

<style scoped>
.dot {
  width: 10px;
  height: 10px;
  border-radius: 5px;
  display: inline-block;
}
</style>