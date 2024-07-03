<template>
  <UserInformation></UserInformation>
  <t-tabs class="container_home" :default-value="'历史记录'" @change="handlerChange" size="medium" style="z-index: 1;">
    <t-tab-panel v-for="tab in TABS" :key="tab.title" :value="tab.title">
      <template #label style="margin-right: 5px">
        <t-icon :name="tab.icon"/>
        {{ tab.title }}
      </template>
      <!--        {{ tab.title }}-->
    </t-tab-panel>
  </t-tabs>

  <a v-if="currentTab.title==='历史记录'">
    <UserHistory ref="childRef"/>
  </a>
  <a v-else-if="currentTab.title==='预约记录'">
    <MyOrderRecords ref="childRef"/>
  </a>
  <a v-else-if="currentTab.title==='我的收藏'">
    <MyFavoritesPage ref="childRef"/>
  </a>
  <a v-else-if="currentTab.title==='我的发布'">
    <UserPublishedPage ref="childRef"/>
  </a>
</template>


<script setup>
import UserInformation from "@/components/profile/userInformation.vue";
import {Tag} from "tdesign-vue-next";
import HomeNew from "@/components/home/HomeNew.vue";
import HomeHot from "@/components/home/HomeHot.vue";
import HomeRecommend from "@/components/home/HomeRecommend.vue";
import UserHistory from "@/components/home/UserHistory.vue";
import MyOrderRecords from "@/components/home/MyOrderRecords.vue";
import MyFavoritesPage from "@/components/home/MyFavoritesPage.vue";
import UserPublishedPage from "@/components/home/UserPublishedPage.vue";
import {computed, ref} from "vue";
import {sessionInformation, bookingInformation, currentStep} from '@/components/book/Steps.vue';
sessionStorage.setItem('currentStep', 0);
currentStep.value = 0
const TABS = [
  {
    title: "历史记录",
    component: UserHistory,
    icon: "home"
  },
  {
    title: "预约记录",
    component: MyOrderRecords,
    icon: "rocket"

  },

  {
    title: "我的收藏",
    component: MyFavoritesPage,
    icon: "star"
  },
  {
    title: "我的发布",
    component: UserPublishedPage,
    icon: "share"
  },
  // {
  //   title: "GitHub",
  //   component: GitHubLogin
  // }
];
const value_tab = ref(TABS[0].title);
const handlerChange = (newValue) => {
  value_tab.value = newValue;
};

const currentTab = computed(() => {
  // alert(currentTab)
  return TABS.find((tab) => tab.title === value_tab.value);
});
</script>



<style>
.inform {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  /* 控制元素之间的间距 */
}

.px-2 {
  font-size: large;
  font-weight: bolder;
}

.github-login-button {
  border-radius: 10px;
  box-sizing: border-box;
  padding: 5px 10px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  gap: 5px;
  border: 2px solid #747474;
}
.container_home {
  position: relative;
}
.personalInformationForm {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
</style>