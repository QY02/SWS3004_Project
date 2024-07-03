<script setup lang="ts">
import routers from '@/routers'
const go_to_login = () => {
  routers.push('/login');
}
</script>

<template>
  <div class="background">
    <div class="content">
      <h1 class="outer-home-title">南方科技大学活动中心</h1>
      <h1 class="outer-home-title-eng">Welcome to the SUSTech Events Center</h1>
      <t-button theme="primary" size="large" variant="base" @click="go_to_login" style="margin-top: 200px">登录</t-button>
    </div>
  </div>
</template>

<script lang="ts">
</script>

<style scoped lang="less">
.background {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  background-image: linear-gradient(rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), url("@/assets/outer-home-background.jpg");
  background-repeat: no-repeat;
  background-size: cover;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: var(--td-text-color-primary);
}

.outer-home-title {
  margin-top:2%;
  margin-bottom: 0;
  font-size: 50px;
  line-height: 1.2;
  text-align: center;

  &-eng {
    margin-top: 20px;
    font-size: 25px;
    line-height: 1.2;
    text-align: center;
  }
}
</style>