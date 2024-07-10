<script setup lang="ts">
import routers from '@/routers'

const go_to_login = () => {
  routers.push('/login');
}
const go_to_register = () => {
  routers.push('/register');
}
</script>

<template>
  <div class="background">
    <div class="content">
      <h1 class="outer-home-title">Welcome to the Events Booking Center</h1>
      <t-space>
        <t-button theme="primary" size="large" variant="base" @click="go_to_login">login</t-button>
        <t-button theme="primary" size="large" variant="base" @click="go_to_register">register</t-button>
      </t-space>
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
  height: 100%;
  width: 100%;
  background-image: linear-gradient(rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), url("@/assets/outer-home-background.jpg");
  background-repeat: no-repeat;
  background-size: cover;
}

.content {
  padding-bottom: 10%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.outer-home-title {
  margin-top: 0;
  margin-bottom: 10vh;
  font-size: 50px;
  line-height: 1.2;
  text-align: center;
}
</style>