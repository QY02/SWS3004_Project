<template>
  <!--  <h2 style="margin-left: 20px">我的预定</h2>-->
  <!--  <t-tag style="margin-left: 20px;height: 40px; margin-top: 15px;font-size: 20px" size="large" theme="success" variant="light">我的预定</t-tag>-->
  <t-loading size="small" :loading="loading" show-overlay>
    <div v-if="events.length===0">
      <div style="display: flex; align-items: center;text-align: center;margin-left: 45%; margin-top: 10%">
        <error-circle-icon size="large"></error-circle-icon>
        <h1 style="color: #5e6066; font-size: large; margin-left: 10px;">暂无活动</h1>
      </div>
    </div>
    <div v-else>
      <div id="MyOrderEvent">
        <t-list :split="true" stripe>
          <t-list-item v-for="(item, index) in unpaidEvent" :key="item" @click="clickEvent(index)">
            <div style="display: flex;">

              <!--          <t-list-item-meta :title="item.title" :description="item.content" style="display: flex; align-items: center;"/>-->
              <t-list-item-meta class="t-list-item-meta-description" :title="item.name"
                                :description="unpaidRecords[index].seatId"
                                style="display: flex; align-items: center;">
                <!--            <p class="t-list-item-meta-description">{{ item.content }}</p>-->
              </t-list-item-meta>
              <t-tag theme="primary" variant="light" style="display: flex; margin-left: 30px;">
                {{ unpaidRecords[index].submitTime.replace('T', ' ') }}
              </t-tag>
              <t-tag theme="success" variant="light" style="display: flex; margin-left: 30px;">
                活动开始时间： {{ unpaidRecords[index].eventSession.startTime.replace('T', ' ') }}
              </t-tag>
              <t-tag theme="warning" variant="light" style="display: flex; margin-left: 30px;">
                活动地点： {{ unpaidRecords[index].eventSession.venue.replace('T', ' ') }}
              </t-tag>
              <t-tag theme="danger" style="display: flex; margin-left: 30px;">
               未付款
              </t-tag>

            </div>
            <template #action>
              <t-popup content="前往付款">
              <t-button variant="text" shape="square" @click.stop="goToPay(index)">
                <arrow-right-icon/>
              </t-button>
              </t-popup>
            </template>


          </t-list-item>


          <t-list-item v-for="(item, index) in events" :key="item" @click="clickEvent(index)">
            <div style="display: flex;">

              <!--          <t-list-item-meta :title="item.title" :description="item.content" style="display: flex; align-items: center;"/>-->
              <t-list-item-meta class="t-list-item-meta-description" :title="item.name"
                                :description="records[index].seatId"
                                style="display: flex; align-items: center;">
                <!--            <p class="t-list-item-meta-description">{{ item.content }}</p>-->
              </t-list-item-meta>
              <t-tag theme="primary" variant="light" style="display: flex; margin-left: 30px;">
                {{ records[index].submitTime.replace('T', ' ') }}
              </t-tag>
              <t-tag theme="success" variant="light" style="display: flex; margin-left: 30px;">
                活动开始时间： {{ records[index].eventSession.startTime.replace('T', ' ') }}
              </t-tag>
              <t-tag theme="warning" variant="light" style="display: flex; margin-left: 30px;">
                活动地点： {{ records[index].eventSession.venue.replace('T', ' ') }}
              </t-tag>

            </div>
            <template #action>
              <t-button variant="text" shape="square" @click="clickEvent(index)">
                <arrow-right-icon/>
              </t-button>
            </template>


          </t-list-item>

        </t-list>
      </div>
    </div>
  </t-loading>
  <!--  <t-popup content="返回上一页">-->
  <!--    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px"-->
  <!--              @click="router.push('/user');">-->
  <!--      <template #icon>-->
  <!--        <rollback-icon/>-->
  <!--      </template>-->

  <!--    </t-button>-->
  <!--  </t-popup>-->
</template>

<script setup>

import {
  ThumbUpIcon,
  ChatIcon,
  ShareIcon,
  MoreIcon,
  ArrowRightIcon,
  RollbackIcon,
  ErrorCircleIcon
} from 'tdesign-icons-vue-next';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from "axios";
import {computed, defineComponent, getCurrentInstance, inject, ref, watch} from "vue";
import router from "@/routers/index.js";
import heart from "tdesign-icons-vue-next/lib/components/heart.js";
import {sessionInformation, bookingInformation, currentStep} from '@/components/book/Steps.vue';
const cover = 'https://tdesign.gtimg.com/site/source/card-demo.png';
const events = ref([]);
const tmpEvents = ref([]);
const curEvents = ref([]);
const records = ref([]);
const loading = ref(true);

const options = [
  {
    content: '操作一',
    value: 1,
  },
  {
    content: '操作二',
    value: 2,
  },
];

// 获取全局变量 $apiBaseUrl

const publisherId = sessionStorage.getItem('uid')
axios.post(`/orderRecord/getMyOrderRecord`, {
  "mode": 3,
}, {
  params: {},

  headers: {
    token: sessionStorage.getItem('token')
  }
})
    .then((response) => {
      // alert(response)
      events.value = response.data.data.map(item => item.event);
      records.value = response.data.data;
      loading.value = false;
      // alert(JSON.stringify(response.data.data))
      // alert(JSON.stringify(events.value))

      // for (let i = 0; i < events.value.length; i++) {//获取每个活动的海报
      //   let id = events.value[i]['id'];
      //   // alert(id)
      // }
    })
    .catch((error) => {
    });

const unpaidEvent = ref([])
const unpaidRecords = ref([]);
axios.post(`/orderRecord/getUnpaidOrderRecord`, {
  "mode": 3,
}, {
  params: {},
  headers: {
    token: sessionStorage.getItem('token')
  }
})
    .then((response) => {
      // alert(response)
      unpaidEvent.value = response.data.data.map(item => item.event);
      unpaidRecords.value = response.data.data;

      // alert(JSON.stringify(response.data.data))
      // for (let i = 0; i < events.value.length; i++) {//获取每个活动的海报
      //   let id = events.value[i]['id'];
      //   // alert(id)
      // }
    })
    .catch((error) => {
    });


const clickHandler = (data) => {
  MessagePlugin.success(`选中【${data.content}】 `);
};
const clickEvent = (index) => {
  // console.log(index)
  // console.log(records.value[index].id)
  console.log(records.value[index].id)
  // MessagePlugin.success(`${sessionStorage.getItem('uid')} 选中【${index}】`);
  // alert(typeof records.value)
  sessionStorage.setItem('index', JSON.stringify(records.value[index]))
  router.push('/OrderRecordDetails');

};
const goToPay = (index) => {
  console.log(index)
  // MessagePlugin.success(`${sessionStorage.getItem('uid')} 选中【${index}】`);
  // alert(typeof records.value)
  router.push('/book');
  console.log(unpaidRecords.value[index].id) //6
  console.log( axios.defaults.baseURL + `/orderRecord/pay/${unpaidRecords.value[index].id}?token=${sessionStorage.getItem('token')}`)
  let targetUrl = axios.defaults.baseURL + `/orderRecord/pay/${unpaidRecords.value[index].id}?token=${sessionStorage.getItem('token')}`;
    // 将当前页面跳转到目标 URL
  console.log(targetUrl)
  window.location.href = targetUrl;
};
// const eventType = inject('eventType')
const eventType = ref(sessionStorage.getItem('eventType'))

const typeValue = ref([]);  // Initialize with a default value

function getSearchNew(message) {
  // alert(JSON.stringify(tmpEvents.value))

  // curEvents.value =event.content.includes(searchText.value) || event.title.includes(searchText.value)
  // alert(message)
  curEvents.value = tmpEvents.value.filter(events => events['content'].includes(message) || events['name'].includes(message));
  // curEvents.value = tmpEvents.value.filter(tmpEvents => tmpEvents['content'].includes(message) || tmpEvents['eventPolicy'].includes(message));
}

defineExpose({getSearchNew});

</script>
<style scoped>
#MyOrderEvent {

  gap: 20px;
  margin: 20px;

}

.t-list-item-meta-description {
  max-width: 700px;
  /* 设置description的最大宽度 */
  overflow: auto;
  /* 超出部分隐藏 */
  text-overflow: ellipsis;
  /* 文本溢出显示省略号 */
  word-wrap: initial;
  /* 强制换行 */
}
</style>