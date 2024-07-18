<template>
  <div class="main-div-finish">
    <t-space direction="vertical" align="center" style="gap: 1px">
      <CheckCircleIcon size="100px" style="color: #009800"/>
      <h1 class="success-prompt-finish">Book success</h1>
      <t-space>
        <t-button>View order</t-button>
        <t-button @click="router.push('/home')">Back to home</t-button>
      </t-space>
    </t-space>
  </div>
</template>

<script setup>
import {CheckCircleIcon} from 'tdesign-icons-vue-next';
import router from "@/routers/index.js";
</script>

<style scoped lang="less">
.main-div-finish {
  position: relative;
  top: 25vh;
}

.success-prompt-finish {
  font-size: 25px;
  line-height: 0.1;
}
</style>