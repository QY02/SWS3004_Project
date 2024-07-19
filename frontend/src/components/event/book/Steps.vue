<template>
  <div class="steps-main-div">
    <t-steps style="width: 70%" :current="currentStep" :readonly="currentStep===3" @change="stepChange">
      <t-step-item title="Choose session">
        <template #icon>
          <TimeIcon size="24" class="icon-margin"/>
        </template>
      </t-step-item>
      <t-step-item title="Input information">
        <template #icon>
          <VerifyIcon size="24" class="icon-margin"/>
        </template>
      </t-step-item>
      <t-step-item title="Choose seat">
        <template #icon>
          <svg class="t-icon t-icon-verify icon-margin" style="margin-top: 1px" xmlns="http://www.w3.org/2000/svg"
               viewBox="0 0 512 512" width="1.5em" height="1.5em">
            <path fill="currentColor"
                  d="M176 80H336c44.2 0 80 35.8 80 80v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-70.7-57.3-128-128-128H176C105.3 32 48 89.3 48 160v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-44.2 35.8-80 80-80zM462.5 227.6c-7.1-2.3-14.6-3.6-22.5-3.6c-9.5 0-18.5 1.8-26.8 5.2c-24.1 9.7-41.8 32-44.7 58.8H143.6c-3-26.8-20.6-49.1-44.7-58.8C90.5 225.8 81.5 224 72 224c-7.9 0-15.4 1.3-22.5 3.6C20.7 237 0 264.1 0 296V432c0 26.5 21.5 48 48 48H96c20.9 0 38.7-13.4 45.3-32H370.7c6.6 18.6 24.4 32 45.3 32h48c26.5 0 48-21.5 48-48V296c0-31.9-20.7-59-49.5-68.4zM368 400H144V336h32H336h32v64zM96 400v32H48l0-136c0-13.3 10.7-24 24-24s24 10.7 24 24v40 64zM464 296V432H416V296c0-13.3 10.7-24 24-24s24 10.7 24 24z"/>
          </svg>
        </template>
      </t-step-item>
      <t-step-item title="Finish" class="finish-step-item">
        <template #icon>
          <CheckCircleIcon size="24" class="icon-margin"/>
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
      <Finish></Finish>
    </div>
    <div class="steps-footer-div"></div>
  </div>
</template>

<script setup lang="ts">

import {onMounted, ref} from "vue";
import {CheckCircleIcon, TimeIcon, VerifyIcon} from 'tdesign-icons-vue-next';
import ChooseSession from '@/components/event/book/ChooseSession.vue';
import ChooseSeat from '@/components/event/book/ChooseSeat.vue';
import InputInformation from '@/components/event/book/InputInformation.vue';
import Finish from '@/components/event/book/Finish.vue';
import {MessagePlugin, NotifyPlugin} from "tdesign-vue-next";
import axios, {AxiosRequestConfig} from 'axios';
import {useRoute} from "vue-router";

const fullUserId = sessionStorage.getItem('fullUserId')
const token = sessionStorage.getItem('token')

const stepChange = (current: number, previous: number) => {
  if ((previous !== 3) && (current !== 3)) {
    if (!((previous === 0) && (bookingInformation.chosenSession === null))) {
      currentStep.value = current;
    } else {
      MessagePlugin.warning('Please choose a session');
    }
  }
}

const removeClickableOnFinishStepItem = () => {
  const parentDiv = document.querySelector('.finish-step-item');
  if (parentDiv && parentDiv.firstElementChild) {
    parentDiv.firstElementChild.classList.remove('t-steps-item--clickable');
  }
}

const route = useRoute();
const eventIdList = (route.query.id as string).split("-");
const eventDetailedDataLocation = eventIdList[0];
const eventId = eventIdList[1];

bookingInformation.eventId = Number(eventId);
const fetchSessionInformation = async () => {
  fetchSessionInformationStatus.value = 0;
  try {
    let response = await axios.post("/detailedEvent/getEventSessionListByEventId", {id: bookingInformation.eventId}, {
      headers: {
        fullUserId: fullUserId,
        token: token,
        eventRoutingIndex: eventDetailedDataLocation
      }
    } as AxiosRequestConfig);
    const dataConverted = response.data.data.map((item: Session) => ({
      ...item,
      startTime: new Date(item.startTime),
      endTime: new Date(item.endTime),
      registrationStartTime: new Date(item.registrationStartTime),
      registrationEndTime: new Date(item.registrationEndTime)
    } as Session));
    sessionInformation.length = 0;
    sessionInformation.push(...dataConverted);
    response = await axios.post("/orderRecord/get", {
      eventId: bookingInformation.eventId,
      detailedDataLocation: eventDetailedDataLocation
    }, {headers: {fullUserId: fullUserId, token: token}} as AxiosRequestConfig);
    const registeredEventSessionIdArray = response.data.data.map((item) => item.eventSessionId);
    sessionInformation.forEach(session => {
      session.registered = !!registeredEventSessionIdArray.includes(session.eventSessionId);
    })
    fetchSessionInformationStatus.value = 1;
  } catch (error) {
    fetchSessionInformationStatus.value = -1;
  }
}

onMounted(() => {
  removeClickableOnFinishStepItem();
  fetchSessionInformation();
});

</script>

<script lang="ts">
import {reactive, ref} from "vue";
import {FormRule, MessagePlugin, NotifyPlugin} from "tdesign-vue-next";
// import axios, {AxiosRequestConfig} from "axios";
import {useRoute} from "vue-router";


export let currentStep = ref(0);

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
}

export const bookingInformation: BookingInformation = reactive({
  eventId: null,
  chosenSession: null,
  chosenSeat: null,
  additionalInformation: [
    {
      name: 'Phone',
      nameEng: 'phoneNumber',
      required: true,
      rules: [
        {
          telnumber: true,
          message: 'Please input the correct phone number'
        }
      ],
      value: ''
    },
    {
      name: 'College',
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
  registrationStartTime: Date;
  registrationEndTime: Date;
  seatMapId: number;
  venue: string;
  registered: boolean;
}

export let sessionInformation: Session[] = reactive([{
  eventSessionId: 1,
  startTime: new Date(2024, 3, 1, 8),
  endTime: new Date(2024, 3, 1, 10),
  registrationStartTime: new Date(2024, 2, 25, 10),
  registrationEndTime: new Date(2024, 2, 27, 0),
  seatMapId: 1,
  venue: '三教107',
  registered: false
},
  {
    eventSessionId: 2,
    startTime: new Date(2024, 3, 1, 10),
    endTime: new Date(2024, 3, 1, 12),
    registrationStartTime: null,
    registrationEndTime: null,
    seatMapId: 1,
    venue: '三教107',
    registered: false
  },
  {
    eventSessionId: 3,
    startTime: new Date(2024, 3, 2, 8),
    endTime: new Date(2024, 3, 2, 10),
    registrationStartTime: new Date(2024, 2, 26, 10),
    registrationEndTime: new Date(2024, 2, 28, 0),
    seatMapId: 1,
    venue: '一教111',
    registered: true
  }])

const fullUserId = sessionStorage.getItem('fullUserId')
const token = sessionStorage.getItem('token')

export const submitData = async () => {
  axios.post("/book", {
    eventId: bookingInformation.eventId,
    eventSessionId: sessionInformation[bookingInformation.chosenSession].eventSessionId,
    seatId: bookingInformation.chosenSeat,
    additionalInformation: JSON.stringify(bookingInformation.additionalInformation.map(item => ({
      name: item.name,
      nameEng: item.nameEng,
      value: item.value
    })))
  }, {
    headers: {
      fullUserId: fullUserId,
      token: token,
      eventRoutingIndex: sessionStorage.getItem('eventDetailedDataLocation')
    }
  } as AxiosRequestConfig).then(() => {
    MessagePlugin.success('Submit success');
    currentStep.value++;
  }).catch()
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