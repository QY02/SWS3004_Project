<template>
  <div>
    <t-layout>
      <t-aside :width="isSidebarCollapsed ? '64px' : null">
        <t-menu class="side-nav" theme="light" value="home" :collapsed="isSidebarCollapsed">
          <template #logo>
            <Logo v-if="isSidebarCollapsed"/>
            <LogoFull v-else/>
          </template>
          <t-menu-item value="home" @click="handleNav('home')">
            <template #icon>
              <HomeIcon/>
            </template>
            Home
          </t-menu-item>
        </t-menu>
      </t-aside>
      <t-layout>
        <t-header>
          <t-head-menu class="header-menu">
            <template #logo>
              <div class="header-operate-left">
                <t-button theme="default" shape="square" variant="text" @click="changeCollapsed">
                  <ViewListIcon/>
                </t-button>
              </div>
            </template>
            <template #operations>
              <div class="operations-container">
                <t-tooltip placement="bottom" content="Logout">
                  <t-button theme="default" shape="square" variant="text" @click="logout">
                    <UserCircleIcon class="header-menu-icon"/>
                  </t-button>
                </t-tooltip>
              </div>
            </template>
          </t-head-menu>
        </t-header>
        <t-content>
          <t-layout class="content-layout">
            <t-content>
              <router-view></router-view>
            </t-content>
          </t-layout>
        </t-content>
      </t-layout>
    </t-layout>
  </div>
</template>

<script setup lang="ts">
import {HomeIcon, UserCircleIcon, ViewListIcon} from 'tdesign-icons-vue-next';
import config from '@/config/style.js';
import {onBeforeUnmount, onMounted, ref} from "vue";
import router from '@/routers';
import Logo from "@/assets/logo.svg";
import LogoFull from "@/assets/logo-full.svg";

let isSidebarCollapsed = ref(config.isSidebarCollapsed);
const sidebarElement = ref(null);
const contentElement = ref(null);

const updateWidth = () => {
  if (sidebarElement.value && contentElement.value) {
    const sidebarElementWidth = sidebarElement.value.offsetWidth;
    contentElement.value.style.width = 'calc(100vw - ' + sidebarElementWidth + 'px)';
  }
};

const resizeObserver = new ResizeObserver(() => {
  updateWidth();
});

onMounted(() => {
  sidebarElement.value = document.querySelector('.side-nav');
  contentElement.value = document.querySelector('.content-layout');
  window.addEventListener('resize', updateWidth);
  resizeObserver.observe(sidebarElement.value);
  updateWidth();
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateWidth);
  resizeObserver.unobserve(sidebarElement.value);
});

const changeCollapsed = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value;
};

const handleNav = (value: string) => {
  switch (value) {
    case 'home':
      router.push('/home');
      break;
  }
}

const logout = () => {
  sessionStorage.clear();
  router.push('/');
}
</script>

<style scoped>

</style>