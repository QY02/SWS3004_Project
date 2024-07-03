<template>
  <div class="input-information-main-div">
    <h1 class="input-information-title">预定信息</h1>
    <div class="container">
      <div class="descriptions-container">
        <t-descriptions column="1">
          <t-descriptions-item label="活动名称">{{ events.name }}</t-descriptions-item>
          <t-descriptions-item label="活动类型">
            <a v-if="events.type>=0&&events.type<=2">
              <t-tag theme="success" variant="light" style="margin-right: 20px">{{
                  EVENT_TYPE_MAP[events.type]
                }}
              </t-tag>
            </a>
            <a v-if="events.type>=3&&events.type<=5">
              <t-tag theme="primary" variant="light" style="margin-right: 20px">{{
                  EVENT_TYPE_MAP[events.type]
                }}
              </t-tag>
            </a>
            <a v-if="events.type>=6&&events.type<=8">
              <t-tag theme="danger" variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[events.type] }}</t-tag>
            </a>
            <a v-if="events.type>=9&&events.type<=12">
              <t-tag variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[events.type] }}</t-tag>
            </a>
            <!--        {{ events.type }}-->
          </t-descriptions-item>
          <t-descriptions-item label="该场次报名时间 ">
            {{
              `${dateToString(new Date(eventDetail.eventSession.registrationStartTime))} - ${dateToString(new Date(eventDetail.eventSession.registrationEndTime))}`
            }}
          </t-descriptions-item>

          <t-descriptions-item label="活动时间">
            {{
              `${dateToString(new Date(eventDetail.eventSession.startTime))} - ${dateToString(new Date(eventDetail.eventSession.endTime))}`
            }}

            <!--        {{`${dateToString(sessionInformation[chosenSession].startTime)} - ${dateToString(sessionInformation[chosenSession].startTime)}`}}-->
          </t-descriptions-item>
          <t-descriptions-item label="活动场地">{{
              eventDetail.eventSession.venue
            }}
          </t-descriptions-item>


          <t-descriptions-item label="座位" v-if="eventDetail.seatId">{{
              eventDetail.seatId
            }}
          </t-descriptions-item>


<!--          <t-descriptions-item label="座位" v-else> 无</t-descriptions-item>-->


          <t-descriptions-item label="价格" v-if="eventDetail.price">{{
              eventDetail.price
            }}
          </t-descriptions-item>

<!--          <t-descriptions-item label="价格" v-else> 无</t-descriptions-item>-->

        </t-descriptions>
      </div>
    </div>
  </div>
  <t-popup content="返回上一页">
    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px"
              @click="router.push('/user');">
      <template #icon>
        <rollback-icon/>
      </template>

    </t-button>
  </t-popup>
</template>
<script setup lang="ts">
import axios from "axios";
import {ref, watch} from "vue";
import {EVENT_TYPE_MAP} from "@/constants/index.js";

import {sessionInformation, bookingInformation} from '@/components/book/Steps.vue';
import {MessagePlugin} from "tdesign-vue-next";
import {RollbackIcon} from "tdesign-icons-vue-next";
import router from "@/routers/index.js";

const events = ref({});
const eventDetail = ref({
  'eventSession': {}
});
const index = sessionStorage.getItem('index');
let data = JSON.parse(index);
// alert(JSON.stringify(data))


events.value = data.event;
eventDetail.value = data;
// alert(JSON.stringify(eventDetail.value.price))
const dateToString = (date: Date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result: string = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}


</script>
<style scoped lang="less">
.container {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 100px;
  //align-items: center; /* 垂直居中 */
  //height: 100vh; /* 设置父容器高度为视口高度，以确保内容垂直居中 */
}

.descriptions-container {

  text-align: left; /* 将描述内容居中 */
}

.input-information {
  &-main-div {
    position: relative;
    top: 5vh;
  }

  &-button-div {
    display: flex;
    justify-content: center;
  }

  &-title {
    text-align: center;
    margin-top: 80px;
    font-size: 25px;
    line-height: 0;
  }
}
</style>