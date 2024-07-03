<template>
  <el-backtop :right="100" :bottom="100"/>
  <el-affix ref="affix" :offset="55">

    <t-tabs class="container_home" :default-value="'最新'" @change="handlerChange" size="large" style="z-index: 1;">
      <t-tab-panel v-for="tab in TABS" :key="tab.title" :value="tab.title">
        <template #label style="margin-right: 5px">
          <t-icon :name="tab.icon"/>
          {{ tab.title }}
        </template>
        <!--        {{ tab.title }}-->
      </t-tab-panel>
    </t-tabs>


    <t-select-input
        v-model:inputValue="inputValue"
        :value="value"
        :allow-input="false"
        :placeholder="'筛选活动'"
        :tag-input-props="{ excessTagsDisplayType: 'scroll' }"
        :popup-props="popupProps"
        clearable
        multiple
        @tag-change="onTagChange"
        @input-change="onInputChange"
        class="custom_select"
    >
      <template #panel>
        <t-checkbox-group
            v-if="options.length"
            :value="checkboxValue"
            :options="options"

            class="tdesign-demo__panel-options-multiple"
            @change="onCheckedChange"
        />
        <div v-else class="tdesign-demo__select-empty-multiple">暂无数据</div>
      </template>
      <template #suffixIcon>
        <chevron-down-icon/>
      </template>
    </t-select-input>
    <!--    <a v-if="currentTab.title==='最新'">-->
    <!--      <HomeNew ref="childRef"/>-->
    <!--</a>-->
    <!--    <a v-else-if="currentTab.title==='热搜'">-->
    <!--      <HomeHot ref="childRef"/>-->
    <!--    </a>-->
    <!--    <a v-else-if="currentTab.title==='推荐'">-->
    <!--      <HomeRecommend ref="childRef"/>-->
    <!--    </a>-->
    <t-input class="customer" v-model="formData.search" clearable placeholder="请输入搜索内容  回车搜索"
             @enter="getSearch" size="large" auto-width style="z-index: 2; ">
      <template #prefix-icon>

        <search-icon/>
      </template>
    </t-input>

  </el-affix>
  <!--发布活动按钮-->
  <t-popup content="发布活动">
    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px; z-index: 999"
              @click="router.push('/applyEvent');">
      <template #icon>
        <add-icon/>
      </template>

    </t-button>
  </t-popup>
  <a v-if="currentTab.title==='最新'">
    <HomeNew ref="childRef"/>
  </a>
  <a v-else-if="currentTab.title==='热搜'">
    <HomeHot ref="childRef"/>
  </a>
  <a v-else-if="currentTab.title==='推荐'">
    <HomeRecommend ref="childRef"/>
  </a>
  <!--  <component :is="currentTab.component" :ref="childRef"></component>-->


</template>


<script setup>
import {computed, inject, reactive, ref, provide, getCurrentInstance} from "vue";
import {AddIcon, SearchIcon} from "tdesign-icons-vue-next";
import HomeHot from "@/components/home/HomeHot.vue";
import HomeNew from "@/components/home/HomeNew.vue";
import HomeRecommend from "@/components/home/HomeRecommend.vue";
sessionStorage.setItem('currentStep', 0)
const title = '标题';
const formData = reactive({
  search: '',
});
const childRef = ref(null);

const getSearch = (value) => {
  // alert(childRef.value)
  childRef.value.getSearchNew(value);

};
const TABS = [
  {
    title: "最新",
    component: HomeNew,
    icon: "home"
  },
  {
    title: "热搜",
    component: HomeHot,
    icon: "rocket"

  },

  {
    title: "推荐",
    component: HomeRecommend,
    icon: "star"
  },
  // {
  //   title: "GitHub",
  //   component: GitHubLogin
  // }
];

import {ChevronDownIcon} from 'tdesign-icons-vue-next';
import router from "@/routers/index.js";
import {EVENT_TYPE_value} from "@/constants/index.js";

const OPTIONS = [
  // 全选
  {label: '全选', checkAll: true},
  {label: '讲座', value: 1,},
  {label: '工作坊', value: 2,},
  {label: '比赛', value: 3},
  {label: '表演', value: 4},
  {label: '展览', value: 5},
  {label: '论坛', value: 6,},
  {label: '体育', value: 7},
  {label: '志愿', value: 8},
  {label: '学院', value: 9},
  {label: '沙龙', value: 10,},
  {label: '培训', value: 11},
  {label: '社团', value: 12},
  {label: '其他', value: 13},

];
function deepCopy(obj) {
  if (typeof obj !== 'object' || obj === null) {
    return obj; // 如果是基本数据类型或者null，则直接返回
  }

  let newObj = Array.isArray(obj) ? [] : {}; // 创建一个新的对象或数组

  Object.keys(obj).forEach(key => {
    newObj[key] = deepCopy(obj[key]); // 递归地进行深拷贝
  });

  return newObj;
}

// 使用示例
let originalObj = { a: 1, b: { c: 2 } };
let copiedObj = deepCopy(originalObj);

const inputValue = ref('');
// 全量数据
let options = ref([...OPTIONS]);
// alert(EVENT_TYPE_value)
// let EVENT_TYPE_value_copy=EVENT_TYPE_value;
let value = ref(EVENT_TYPE_value);

const popupProps = ref({
  overlayInnerClassName: ['narrow-scrollbar'],
  overlayInnerStyle: {
    maxHeight: '280px',
    overflowY: 'auto',
    overscrollBehavior: 'contain',
    padding: '6px',
  },
});
const value_tab = ref(TABS[0].title);
const handlerChange = (newValue) => {
  value_tab.value = newValue;
  value.value=[
    {label: '讲座', value: 1,},
    {label: '工作坊', value: 2,},
    {label: '比赛', value: 3},
    {label: '表演', value: 4},
    {label: '展览', value: 5},
    {label: '论坛', value: 6,},
    {label: '体育', value: 7},
    {label: '志愿', value: 8},
    {label: '学院', value: 9},
    {label: '沙龙', value: 10,},
    {label: '培训', value: 11},
    {label: '社团', value: 12},
    {label: '其他', value: 13},
  ];

};
const currentTab = computed(() => {
  // alert(currentTab)
  return TABS.find((tab) => tab.title === value_tab.value);
});
// const bc = new BroadcastChannel('eventTypeChannel');
// alert(bc)
function resetSetItem(key, newVal) {
  if (key === 'eventType') {
    // 创建一个StorageEvent事件
    const newStorageEvent = document.createEvent('StorageEvent');
    const storage = {
      setItem: function (k, val) {
        sessionStorage.setItem(k, val);
        // 初始化创建的事件
        newStorageEvent.initStorageEvent('setItem', false, false, k, null, val, null, null);
        // 派发对象
        window.dispatchEvent(newStorageEvent)
        formData.search = '';//搜索清空
      }
    }
    return storage.setItem(key, newVal);
  }
}

const checkboxValue = computed(() => {
  const arr = ref([]);
  const list = value.value;
  // 此处不使用 forEach，减少函数迭代
  for (let i = 0, len = list.length; i < len; i++) {
    list[i].value && arr.value.push(list[i].value);
  }
  // sessionStorage.setItem('eventType', arr.value);
  resetSetItem('eventType', arr.value);

  return arr.value;
});
// 直接 checkboxgroup 组件渲染输出下拉选项
const onCheckedChange = (val, {current, type}) => {
  // alert(current);
  // current 不存在，则表示操作全选
  if (!current) {
    value.value = type === 'check' ? options.value.slice(1) : [];
    return;
  }
  // 普通操作
  if (type === 'check') {
    const option = options.value.find((t) => t.value === current);
    value.value.push(option);
  } else {
    value.value = value.value.filter((v) => v.value !== current);
  }
};

// 可以根据触发来源，自由定制标签变化时的筛选器行为
const onTagChange = (currentTags, context) => {
  // alert(currentTags);
  const {trigger, index, item} = context;
  if (trigger === 'clear') {
    value.value = [];
  }
  if (['tag-remove', 'backspace'].includes(trigger)) {
    value.value.splice(index, 1);
  }
  // 如果允许创建新条目

};
const onInputChange = (val, context) => {
  console.log(val, context);
};
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