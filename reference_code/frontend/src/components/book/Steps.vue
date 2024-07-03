<template>
  <div class="steps-main-div">

    <t-steps style="width: 70%" :current="stepCurrent" readonly @change="stepChange" v-show="bookingInformation.chosenSession!== null && sessionInformation[bookingInformation.chosenSession].seatMapId != -1">
      <t-step-item title="选择场次">
        <template #icon>
          <TimeIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
      <t-step-item title="填写信息">
        <template #icon>
          <VerifyIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
      <t-step-item title="选择座位">
        <template #icon>
          <svg class="t-icon t-icon-verify icon-margin" style="margin-top: 1px" xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 512 512" width="1.5em" height="1.5em">
            <path fill="currentColor"
              d="M176 80H336c44.2 0 80 35.8 80 80v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-70.7-57.3-128-128-128H176C105.3 32 48 89.3 48 160v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-44.2 35.8-80 80-80zM462.5 227.6c-7.1-2.3-14.6-3.6-22.5-3.6c-9.5 0-18.5 1.8-26.8 5.2c-24.1 9.7-41.8 32-44.7 58.8H143.6c-3-26.8-20.6-49.1-44.7-58.8C90.5 225.8 81.5 224 72 224c-7.9 0-15.4 1.3-22.5 3.6C20.7 237 0 264.1 0 296V432c0 26.5 21.5 48 48 48H96c20.9 0 38.7-13.4 45.3-32H370.7c6.6 18.6 24.4 32 45.3 32h48c26.5 0 48-21.5 48-48V296c0-31.9-20.7-59-49.5-68.4zM368 400H144V336h32H336h32v64zM96 400v32H48l0-136c0-13.3 10.7-24 24-24s24 10.7 24 24v40 64zM464 296V432H416V296c0-13.3 10.7-24 24-24s24 10.7 24 24z" />
          </svg>
        </template>
      </t-step-item>
      <t-step-item title="确认报名">
        <template #icon>
          <MoneyIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>

      <t-step-item title="完成" class="finish-step-item" >
        <template #icon>
          <CheckCircleIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
    </t-steps>

    <t-steps style="width: 70%" :current="stepCurrent" readonly @change="stepChange" v-show="bookingInformation.chosenSession=== null||(bookingInformation.chosenSession!== null && sessionInformation[bookingInformation.chosenSession].seatMapId == -1)">
      <t-step-item title="选择场次">
        <template #icon>
          <TimeIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
      <t-step-item title="填写信息">
        <template #icon>
          <VerifyIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
      <t-step-item title="确认报名">
        <template #icon>
          <MoneyIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>

      <t-step-item title="完成" class="finish-step-item">
        <template #icon>
          <CheckCircleIcon size="24" class="icon-margin" />
        </template>
      </t-step-item>
    </t-steps>


    <div v-show="currentStep === 0">
      <ChooseSession></ChooseSession>
    </div>
    <div v-show="currentStep === 1">
      <InputInformation></InputInformation>
    </div>
    <div v-show="currentStep === 2">
      <ChooseSeat></ChooseSeat>
    </div>
    <div v-show="currentStep === 3">
      <PayPage></PayPage>
    </div>
    <div v-show="currentStep === 4">
      <Finish></Finish>
    </div>
    <div v-show="currentStep === 5">
      <FailPage></FailPage>
    </div>
    <div class="steps-footer-div"></div>
  </div>
</template>

<script setup lang="ts">

import { onMounted, ref, watch } from "vue";
import { TimeIcon, VerifyIcon, CheckCircleIcon, MoneyIcon } from 'tdesign-icons-vue-next';
import ChooseSession from '@/components/book/ChooseSession.vue';
import ChooseSeat from '@/components/book/ChooseSeat.vue';
import InputInformation from '@/components/book/InputInformation.vue';
import PayPage from "./PayPage.vue";
import Finish from '@/components/book/Finish.vue';
import FailPage from '@/components/book/FailPage.vue';
import { MessagePlugin, NotifyPlugin } from "tdesign-vue-next";
import axios, { AxiosRequestConfig } from 'axios';
import { useRoute } from "vue-router";
import { globalProperties } from '@/main';

Object.assign(bookingInformation, {
  eventId: null,
  chosenSession: null,
  chosenSeat: null,
  additionalInformation: []
}
)

bookingInformation.eventId = Number(sessionStorage.getItem('eventId'));



const stepChange = (current: number, previous: number) => {
  if ((previous !== 3) && (current !== 3)) {
    if (!((previous === 0) && (bookingInformation.chosenSession === null))) {
      console.log(currentStep.value)
      currentStep.value = current;
      console.log(current)
    } else {
      MessagePlugin.warning('请选择一个场次');
    }
  }
}

const removeClickableOnFinishStepItem = () => {
  const parentDiv = document.querySelector('.finish-step-item');
  if (parentDiv && parentDiv.firstElementChild) {
    parentDiv.firstElementChild.classList.remove('t-steps-item--clickable');
  }
}
const fetchSessionInformation = async () => {
  fetchSessionInformationStatus.value = 0;
  try {
    let response = await axios.post("/event/getEventSessionsByEventId", { eventId: bookingInformation.eventId }, { headers: { token: sessionStorage.getItem('token') } } as AxiosRequestConfig);
    const dataConverted = response.data.data.map((item) => ({
      ...item,
      startTime: new Date(item.startTime),
      endTime: new Date(item.endTime),
      registrationStartTime: new Date(item.registrationStartTime),
      registrationEndTime: new Date(item.registrationEndTime),
      price: item.price,
      location: item.location.split(",").map(Number)
    } as Session));
    sessionInformation.length = 0;
    sessionInformation.push(...dataConverted);
    response = await axios.post("/orderRecord/getMyOrderRecord", {
      eventId: bookingInformation.eventId,
      mode: 0
    }, { headers: { token: sessionStorage.getItem('token') } } as AxiosRequestConfig);
    const registeredEventSessionIdArray = response.data.data;
    sessionInformation.forEach(session => {
      session.registered = registeredEventSessionIdArray.includes(session.eventSessionId);
    })
    fetchSessionInformationStatus.value = 1;
  }
  catch (error) {
    fetchSessionInformationStatus.value = -1;
    // if (error.response) {
    //   await NotifyPlugin.error({title: error.response.data.msg});
    // } else {
    //   await NotifyPlugin.error({title: error.message});
    // }
  }
}

onMounted(() => {
  removeClickableOnFinishStepItem();
  fetchSessionInformation()
});

watch(
  () => window.location.href,
  (newUrl, oldUrl) => {
    console.log('change')
  }
);

watch(currentStep, (newValue) => {
  sessionStorage.setItem('currentStep', newValue);
  if(bookingInformation.chosenSession!== null && sessionInformation[bookingInformation.chosenSession].seatMapId == -1){
    if(newValue>=3){
      console.log(newValue)
      console.log(currentStep)
      stepCurrent.value = newValue-1
      console.log(currentStep)
    }
  }
  else{
    stepCurrent.value = newValue
  }
})

</script>

<script lang="ts">
import { reactive, ref } from "vue";
import { FormRule, MessagePlugin, NotifyPlugin } from "tdesign-vue-next";
import axios, { AxiosRequestConfig } from "axios";
import { globalProperties } from '@/main';
import { useRoute } from "vue-router";


export let currentStep = ref(parseInt(sessionStorage.getItem('currentStep')));
let stepCurrent =  ref(parseInt(sessionStorage.getItem('currentStep')));
if ((currentStep.value != 3) && (currentStep.value != 4) && (currentStep.value != 5)) {
  currentStep.value = 0;
}


export function toNextStep() {
  currentStep.value++;
}

export let fetchSessionInformationStatus = ref(0);

export interface AdditionalInformationItem {
  name: string;
  nameEng: string;
  required: boolean;
  rules: FormRule[];
  value: string;
}

interface BookingInformation {
  eventId: number;
  chosenSession: number;
  chosenSeat: string;
  additionalInformation: AdditionalInformationItem[];
  seatPrice: number;
}

export const bookingInformation: BookingInformation = reactive({
  eventId: null,
  chosenSession: null,
  chosenSeat: null,
  additionalInformation: [
    {
      name: '手机号',
      nameEng: 'phoneNumber',
      required: true,
      rules: [
        {
          telnumber: true,
          message: '请输入正确的手机号码'
        }
      ],
      value: ''
    },
    {
      name: '书院',
      nameEng: 'college',
      required: true,
      rules: null,
      value: ''
    }]
})

export interface Session {
  eventSessionId: number;
  startTime: Date;
  endTime: Date;
  registrationRequired: boolean;
  registrationStartTime: Date;
  registrationEndTime: Date;
  minSize: number;
  maxSize: number;
  currentSize: number;
  seatMapId: number;
  price: number;
  venue: string;
  location: number[];
  additionalInformationRequired: string;
  registered: boolean;
}

export let sessionInformation: Session[] = reactive([{
  eventSessionId: 1,
  startTime: new Date(2024, 3, 1, 8),
  endTime: new Date(2024, 3, 1, 10),
  registrationRequired: true,
  registrationStartTime: new Date(2024, 2, 25, 10),
  registrationEndTime: new Date(2024, 2, 27, 0),
  minSize: 10,
  currentSize: 20,
  maxSize: 100,
  seatMapId: 1,
  price: 0,
  venue: '三教107',
  location: [113.997, 22.596],
  additionalInformationRequired: '[{"name": "手机号", "nameEng": "phoneNumber", "required": true, "rules": [{"telnumber": true ,"message": "请输入正确的手机号码"}], "value": ""}, {"name": "书院", "nameEng": "college", "required": true, "rules": null, "value": ""}]',
  registered: false
},
{
  eventSessionId: 2,
  startTime: new Date(2024, 3, 1, 10),
  endTime: new Date(2024, 3, 1, 12),
  registrationRequired: false,
  registrationStartTime: null,
  registrationEndTime: null,
  minSize: 5,
  maxSize: 500,
  currentSize: 100,
  seatMapId: 1,
  price: 0,
  venue: '三教107',
  location: [113.997, 22.596],
  additionalInformationRequired: '[{"name": "手机号", "nameEng": "phoneNumber", "required": true, "rules": [{"telnumber": true ,"message": "请输入正确的手机号码"}], "value": ""}, {"name": "书院", "nameEng": "college", "required": true, "rules": null, "value": ""}]',
  registered: false
},
{
  eventSessionId: 3,
  startTime: new Date(2024, 3, 2, 8),
  endTime: new Date(2024, 3, 2, 10),
  registrationRequired: true,
  registrationStartTime: new Date(2024, 2, 26, 10),
  registrationEndTime: new Date(2024, 2, 28, 0),
  minSize: 20,
  maxSize: 80,
  currentSize: 60,
  seatMapId: 1,
  price: 0,
  venue: '一教111',
  location: [113.997, 22.596],
  additionalInformationRequired: '[{"name": "手机号", "nameEng": "phoneNumber", "required": true, "rules": [{"telnumber": true ,"message": "请输入正确的手机号码"}], "value": ""}, {"name": "书院", "nameEng": "college", "required": true, "rules": null, "value": ""}]',
  registered: true
},
{
  eventSessionId: 4,
  startTime: new Date(2024, 3, 2, 8),
  endTime: new Date(2024, 3, 2, 10),
  registrationRequired: true,
  registrationStartTime: new Date(2024, 2, 26, 10),
  registrationEndTime: new Date(2024, 12, 28, 0),
  minSize: 20,
  maxSize: 80,
  currentSize: 60,
  seatMapId: 1,
  price: 0,
  venue: '一教111',
  location: '',
  additionalInformationRequired: '[{"name": "手机号", "nameEng": "phoneNumber", "required": true, "rules": [{"telnumber": true ,"message": "请输入正确的手机号码"}], "value": ""}, {"name": "书院", "nameEng": "college", "required": true, "rules": null, "value": ""}]',
  registered: false
}])

export const submitData = async () => {
  axios.post("/event/submitBookingData", {
    eventId: bookingInformation.eventId,
    eventSessionId: sessionInformation[bookingInformation.chosenSession].eventSessionId,
    seatId: bookingInformation.chosenSeat,
    additionalInformation: JSON.stringify(bookingInformation.additionalInformation.map(item => ({
      name: item.name,
      nameEng: item.nameEng,
      value: item.value
    })))
  }, { headers: { token: sessionStorage.getItem('token') } } as AxiosRequestConfig).then(() => {
    MessagePlugin.success('提交成功');
    currentStep.value++;
  })
    .catch(error => { })
}

</script>

<style scoped lang="less">
.steps {
  &-main-div {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    top: 5vh;
  }

  &-footer-div {
    height: 10vh;
  }
}
</style>