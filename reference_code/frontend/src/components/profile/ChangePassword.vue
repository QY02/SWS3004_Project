<template>
  <t-button
      class="btn"
      theme="default"
      variant="base"
      @click="()=>visibleBody=true"
  >
    修改密码
  </t-button>


<t-dialog
    v-model:visible="visibleBody"
    attach="body"
    header="修改密码"
    destroy-on-close:true
    width="500px"
    :cancel-btn=null
    :confirm-btn=null
>
<template #body>
        <t-tabs :default-value="TABS[0].title" @change="handlerChange" size="medium">
          <t-tab-panel v-for="tab in TABS" :key="tab.title" :value="tab.title" :label="tab.title">
          </t-tab-panel>
        </t-tabs>
          <div class="va-card-content">
            <component :is="currentTab.component"></component>
          </div>
</template>
</t-dialog>
</template>

<script setup>
import {computed, ref,} from "vue";
import ChangePasswordByOld from "./ChangePasswordByOld.vue"
import ChangePasswordByEmail from "./ChangePasswordByEmail.vue"
import {createRouter, createWebHistory} from "vue-router";
import router from "@/routers/index.js";
import {LogoutIcon} from "tdesign-icons-vue-next";
const visibleBody = ref(false)

const TABS = [
  {
    title: "通过旧密码",
    component: ChangePasswordByOld

  },
  {
    title: "通过邮箱",
    component: ChangePasswordByEmail
  },
];

const value = ref(TABS[0].title);

const currentTab = computed(() => {
      return TABS.find((tab) => tab.title === value.value);
});
const handlerChange = (newValue) => {
      value.value = newValue;
};
</script>

<style scoped>

.va-card-content {
  display: flex;
  flex-direction: column;
  margin: 30px;
}
.btn{
  margin-left: 20px;
  width: 120px;
}
</style>