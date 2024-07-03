<template>
  <div class="main-div-finish">
    <t-space direction="vertical" align="center" style="gap: 1px">
      <CheckCircleIcon size="100px" style="color: #009800"/>
      <h1 class="success-prompt-finish">预订成功</h1>
      <t-space>
        <t-button @click="pushRouter('user')">前往个人页</t-button>
        <t-button @click="pushRouter('HomePage')">返回首页</t-button>
      </t-space>
    </t-space>
  </div>
</template>

<script setup>
import router from '@/routers';
import {CheckCircleIcon} from 'tdesign-icons-vue-next';
import {watch} from "vue";
import axios from "axios";
import {sessionInformation, bookingInformation, currentStep} from '@/components/book/Steps.vue';
const pushRouter = (value) => {
  sessionStorage.setItem('currentStep', 0)
  switch (value) {
    case 'HomePage':
      router.push('/HomePage');
      break;
    case 'user':
      router.push('/user');
      break;
  }
}
const notify = () => {
  console.log("yes")
  axios.post(`/notification/reserveSession/${sessionInformation[bookingInformation.chosenSession].eventSessionId}`, {}, {
    headers: {
      token: sessionStorage.getItem('token')
    }
  }).then((response) => {
    console.log(response)
  }).catch()
}
// onUnmounted(() => notify())
watch(currentStep, (newValue) => {
  if (newValue === 4) {
    notify()
  }
})
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