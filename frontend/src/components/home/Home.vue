<template>
  <el-backtop :right="100" :bottom="100"/>
  <el-affix ref="affix" :offset="55">

    <t-tabs class="container_home" :default-value="'Event'" size="large" style="z-index: 1;">
      <t-tab-panel v-for="tab in TABS" :key="tab.title" :value="tab.title">
        <template #label style="margin-right: 5px">
          <t-icon :name="tab.icon"/>
          {{ tab.title }}
        </template>
      </t-tab-panel>
    </t-tabs>


    <t-input class="customer" v-model="formData.search" clearable placeholder="Search"
             @enter="getSearch" size="large" auto-width style="z-index: 2; ">
      <template #prefix-icon>

        <search-icon/>
      </template>
    </t-input>

  </el-affix>
  <t-popup content="Publish event">
    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px; z-index: 999"
              @click="router.push('/publishEvent');">
      <template #icon>
        <add-icon/>
      </template>

    </t-button>
  </t-popup>
  <HomeContent ref="childRef"/>


</template>


<script setup>
import {reactive, ref} from "vue";
import {AddIcon, SearchIcon} from "tdesign-icons-vue-next";
import HomeContent from "@/components/home/HomeContent.vue";
import router from "@/routers/index.js";

sessionStorage.setItem('currentStep', 0)
const formData = reactive({
  search: '',
});
const childRef = ref(null);

const getSearch = (value) => {
  childRef.value.getSearchNew(value);

};
const TABS = [
  {
    title: "Events",
    component: HomeContent,
    icon: "home"
  }
];
</script>


<style scoped>

.container_home {
  position: relative;
}

.customer {
  position: absolute;
  width: 300px;
  top: 10px;
  right: 20px;
}

.custom_select {
  position: absolute;
  width: 350px;
  top: 15px;
  right: 340px;
  z-index: 2;
}

.tdesign-demo__panel-options-multiple {
  width: 100%;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.tdesign-demo__panel-options-multiple .t-checkbox {
  display: flex;
  border-radius: 3px;
  line-height: 22px;
  cursor: pointer;
  padding: 3px 8px;
  color: var(--td-text-color-primary);
  transition: background-color 0.2s linear;
  white-space: nowrap;
  word-wrap: normal;
  overflow: hidden;
  text-overflow: ellipsis;
  margin: 0;
}

.tdesign-demo__panel-options-multiple .t-checkbox:hover {
  background-color: var(--td-bg-color-container-hover);
}
</style>