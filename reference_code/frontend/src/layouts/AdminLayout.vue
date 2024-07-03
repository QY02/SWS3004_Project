<template>
    <div>
      <t-layout>
        <t-aside :width="isSidebarCollapsed ? '64px' : null">
          <t-menu class="side-nav" theme="light" :value='highlightItem' :collapsed="isSidebarCollapsed">
            <template #logo>
              <Logo v-if="isSidebarCollapsed"/>
              <LogoFull v-else/>
            </template>
            <t-menu-item value="home" @click="handleNav('home')">
              <template #icon>
                <HomeIcon/>
              </template>
              首页
            </t-menu-item>
            <t-menu-item value="userManage" @click="handleNav('userManage')">
              <template #icon>
                <UserAvatarIcon/>
              </template>
              用户管理
            </t-menu-item>
            <t-menu-item value="approval" @click="handleNav('approval')">
              <template #icon>
                <ListIcon/>
              </template>
              活动审核
            </t-menu-item>
            <t-menu-item value="momentAudit" @click="handleNav('momentAudit')">
              <template #icon>
                <ChatBubbleErrorIcon/>
              </template>
              动态管理
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
                  <t-tooltip placement="bottom" content="退出登录">
                    <t-button theme="default" shape="square" variant="text" @click="handleNav('user')">
                      <LogoutIcon class="header-menu-icon"/>
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
  import {
    UserCircleIcon,
    HomeIcon,
    ViewListIcon,
    UserAvatarIcon, ChatBubbleErrorIcon, Calendar1Icon, ListIcon, LogoutIcon
  } from 'tdesign-icons-vue-next';
  import config from '@/config/style.js';
  import {onMounted, onBeforeUnmount, ref} from "vue";
  import router from '@/routers';
  import Logo from "@/assets/logo.svg";
  import LogoFull from "@/assets/logo-full.svg";
  import {MessagePlugin} from "tdesign-vue-next";
  
  const highlightItem = ref('home')
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
    highlightItem.value = value
    switch (value) {
      case 'home':
        router.push('/admin/homepage');
        break;
      case 'userManage':
        router.push('/admin/userManage');
        break;
      case 'approval':
        router.push('/admin/approval');
        break;
      case 'momentAudit':
        router.push('/admin/momentAudit');
        break;
      case 'user':
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('role');
        MessagePlugin.success('退出登录成功！');
        router.push('/login');
        break;
    }
  }
  </script>
  
  <style scoped>
  
  </style>