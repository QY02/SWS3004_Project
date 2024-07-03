<template>
  <div class="input-information-main-div">
    <h1 class="input-information-title">预定信息</h1>
    <t-descriptions v-show="bookingInformation.seatPrice>0" column="1">
      <t-descriptions-item label="活动名称">{{ eventDetail.name }}</t-descriptions-item>
      <t-descriptions-item label="该场次报名时间">
        {{ `${dateToString(sessionInformation[chosenSession].registrationStartTime)} -
        ${dateToString(sessionInformation[chosenSession].registrationEndTime)}` }}
      </t-descriptions-item>
      <t-descriptions-item label="活动时间">
        {{ `${dateToString(sessionInformation[chosenSession].startTime)} -
        ${dateToString(sessionInformation[chosenSession].startTime)}` }}
      </t-descriptions-item>
      <t-descriptions-item label="活动场地">{{
        sessionInformation[chosenSession].venue
      }}
      </t-descriptions-item>
      <t-descriptions-item label="座位" v-if="bookingInformation.chosenSeat && bookingInformation.chosenSeat!=='门票'">{{ bookingInformation.chosenSeat }}</t-descriptions-item>
      <t-descriptions-item label="价格">{{ bookingInformation.seatPrice }}</t-descriptions-item>
    </t-descriptions>
    <t-descriptions v-show="bookingInformation.seatPrice===0" column="1">
      <t-descriptions-item label="活动名称">{{ eventDetail.name }}</t-descriptions-item>
      <t-descriptions-item label="该场次报名时间">
        {{ `${dateToString(sessionInformation[chosenSession].registrationStartTime)} -
        ${dateToString(sessionInformation[chosenSession].registrationEndTime)}` }}
      </t-descriptions-item>
      <t-descriptions-item label="活动时间">
        {{ `${dateToString(sessionInformation[chosenSession].startTime)} -
        ${dateToString(sessionInformation[chosenSession].startTime)}` }}
      </t-descriptions-item>
      <t-descriptions-item label="活动场地">{{
          sessionInformation[chosenSession].venue
        }}
      </t-descriptions-item>
      <t-descriptions-item label="座位" v-if="bookingInformation.chosenSeat">{{ bookingInformation.chosenSeat }}</t-descriptions-item>
    </t-descriptions>
    <br>
    <div class="input-information-button-div">
      <t-space size="medium">
        <t-button theme="default" @click="backToPrev" disabled="loadingPay">上一步</t-button>
        <t-button @click="prePay" :loading="loadingPay" v-if="bookingInformation.seatPrice>0">前往付款</t-button>
        <t-button @click="bookEvent" :loading="loadingSubmit" v-else>确认报名</t-button>
      </t-space>
    </div>
  </div>

</template>
<script setup lang="ts">
import axios, { AxiosRequestConfig } from 'axios';
import { currentStep, submitData, toNextStep } from '@/components/book/Steps.vue';
import { onMounted, onUnmounted, reactive, Ref, ref, watch } from "vue";
import { sessionInformation, bookingInformation } from '@/components/book/Steps.vue';
import { MessagePlugin, NotifyPlugin } from "tdesign-vue-next";

const dateToString = (date: Date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result: string = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}

const loadingPay = ref(false)
const loadingSubmit = ref(false)
const eventDetail = ref({
  name: ''
})
const getEventDetail = () => {
  axios.get(`/event/getEventDetail/${sessionStorage.getItem('eventId')}`, {
    headers: {
      token: sessionStorage.getItem('token')
    }
  }).then((response) => {
    eventDetail.value = response.data.data
  }).catch(() => {
  })
}

getEventDetail();
const result = ref(0);
const orderId = ref(-1)
const prePay = () => {
  loadingPay.value = true
  console.log('startPay')
  if(bookingInformation.chosenSeat===null){
    bookingInformation.chosenSeat = "门票"
  }
  console.log(bookingInformation.chosenSeat)
  console.log(bookingInformation.chosenSeat)
  axios.post("/orderRecord/prePay", {
    eventId: bookingInformation.eventId,
    eventSessionId: sessionInformation[bookingInformation.chosenSession].eventSessionId,
    seatId: bookingInformation.chosenSeat,
    price: bookingInformation.seatPrice,
    additionalInformation: JSON.stringify(bookingInformation.additionalInformation.map(item => ({
      name: item.name,
      nameEng: item.nameEng,
      value: item.value
    })))
  }, { headers: { token: sessionStorage.getItem('token') } } )
    .then((response) => {
      orderId.value = response.data.data;
      MessagePlugin.success('提交支付信息成功');
      // alert(orderId.value)
      // 构建跳转的 URL
      sessionStorage.setItem('pay', 1)
      let targetUrl = axios.defaults.baseURL + `/orderRecord/pay/${orderId.value}?token=${sessionStorage.getItem('token')}`;
      loadingPay.value = false
      // 将当前页面跳转到目标 URL
      window.location.href = targetUrl;
      // startPolling();
      // currentStep.value++;
    })
    .catch((error) => {
      loadingPay.value = false;
    })}
    console.log('endpay')

const bookEvent = () => {
  loadingSubmit.value = true
  console.log('startPay')
  axios.post("/event/submitBookingData", {
    eventId: bookingInformation.eventId,
    eventSessionId: sessionInformation[bookingInformation.chosenSession].eventSessionId,
    seatId: bookingInformation.chosenSeat,
    additionalInformation: JSON.stringify(bookingInformation.additionalInformation.map(item => ({
      name: item.name,
      nameEng: item.nameEng,
      value: item.value
    })))
  }, { headers: { token: sessionStorage.getItem('token') } } )
    .then((response) => {
      MessagePlugin.success('提交预定成功');
      loadingSubmit.value = false;
      toNextStep();
    })
    .catch((error) => {
      loadingSubmit.value = false;
    })
    console.log('endpay')
}


const backToPrev = ()=>{
  if(sessionInformation[bookingInformation.chosenSession].seatMapId!=-1){
    currentStep.value--;
  }
  else{
    currentStep.value -=2;
  }
}

const payResult = ref(0);

let intervalId = null;
let timeoutId = null;

const startPolling = () => {
  if (timeoutId) {
    clearTimeout(timeoutId);
    timeoutId = null; // 清除后将timeoutId设为null
  }
  // 定义轮询函数
  const poll = () => {
    if (payResult.value === 1) { // 当外部条件满足时
      clearInterval(intervalId); // 清除间隔定时器
      return; // 结束轮询
    }

    // 每10秒请求一次
    // getPayResult(); // 执行轮询请求
  };

  // 每10秒请求一次
  intervalId = setInterval(poll, 10000);
  // 10分钟后停止轮询
  timeoutId = setTimeout(() => {
    clearInterval(intervalId);
  }, 10 * 60 * 1000); // 10分钟
};

const currentUrl = ref(window.location.href);
const timestamp = ref(null);
const formattedDateTime = ref(null);

const checkUrl = () => {
  console.log('checking')
  // if (currentUrl.value !== 'http://localhost:5173/book') {
  if (currentUrl.value !== 'http://47.107.113.54:25577/book') {
    console.log('differnet')
    const urlParams = new URLSearchParams(currentUrl.value.split('?')[1]); // 获取查询参数部分
    const newTimestamp = urlParams.get('timestamp'); // 获取timestamp参数的值
    orderId.value = parseInt(urlParams.get('out_trade_no'));
    console.log(orderId.value)
    timestamp.value = newTimestamp; // 更新timestamp的值

    if (newTimestamp) {
      const dateObj = new Date(newTimestamp.replace(/\+/g, ' ')); // 将带有+号的时间戳字符串转换成Date对象
      formattedDateTime.value = formatDate(dateObj); // 转换成指定格式的日期时间字符串
      console.log(formattedDateTime.value)
      axios.post(`/orderRecord/payResult`, {
        'orderId': orderId.value,
        'time': formattedDateTime.value,
        'result': 1
      }, {
        headers: {
          token: sessionStorage.getItem('token')
        }
      }).then((response) => {
        console.log('next')
        MessagePlugin.success('支付成功');
        sessionStorage.setItem('pay', 0)
        currentStep.value = 4;
        // window.location.href = 'http://localhost:5173/book';
        window.location.href = 'http://47.107.113.54:25577/book'
      }).catch(() => {
      })
    }
  } else {
    console.log(currentStep.value == 3)
    if (sessionStorage.getItem('pay') == '1' && currentStep.value == 3) {
      currentStep.value += 2;
      sessionStorage.setItem('currentStep', currentStep.value);
    }
    else if (currentStep.value == 3) {
      currentStep.value = 0;
      sessionStorage.setItem('currentStep', 0);
    }
  }
}

onMounted(() => {
  checkUrl();
  // watch(currentUrl, (newUrl, oldUrl) => {
  //   console.log('inini')
  //   const urlParams = new URLSearchParams(window.location.search);
  //   const newTimestamp = urlParams.get('timestamp');
  //   // const urlParams = new URLSearchParams(newUrl.split('?')[1]); // 获取查询参数部分
  //   // const newTimestamp = urlParams.get('timestamp'); // 获取timestamp参数的值
  //   timestamp.value = newTimestamp; // 更新timestamp的值

  //   if (newTimestamp) {
  //     const dateObj = new Date(newTimestamp.replace(/\+/g, ' ')); // 将带有+号的时间戳字符串转换成Date对象
  //     formattedDateTime.value = formatDate(dateObj); // 转换成指定格式的日期时间字符串
  //     axios.post(`/orderRecord/payResult`, {
  //       'orderId': parseInt(sessionStorage.getItem('orderId')),
  //       'time': formattedDateTime.value,
  //       'result': 1
  //     }, {
  //       headers: {
  //         token: sessionStorage.getItem('token')
  //       }
  //     }).then((response) => {
  //       console.log('next')
  //       MessagePlugin.success('支付成功');
  //       toNextStep();
  //       window.location.href = 'http://localhost:5173/book';
  //     }).catch(() => {
  //     })
  //   }
  //   // if (newUrl !== 'http://localhost:5173/book') {
  //   //   const urlParams = new URLSearchParams(newUrl.split('?')[1]); // 获取查询参数部分
  //   //   const newId = urlParams.get('id'); // 获取id参数的值
  //   //   id.value = newId; // 更新id的值
  //   // }
  // });
});

const formatDate = (dateObj) => {
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const seconds = String(dateObj.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

onUnmounted(() => {
  if (timeoutId) {
    clearTimeout(timeoutId);
    timeoutId = null;
  }
});

// function getPayResult() {
//   axios.post("/orderRecord/getPayResultById", {id: orderId},
//       {headers: {token: sessionStorage.getItem('token')}} as AxiosRequestConfig)
//       .then(response => {
//         payResult.value = response.data.data;
//       })
//       .catch(error => {
//       });
// }

const chosenSession = ref(0);
watch(
  () => bookingInformation.chosenSession,
  (newSession, oldSession) => {
    chosenSession.value = newSession
  }
);

</script>

<style scoped lang="less">
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
    font-size: 25px;
    line-height: 0;
  }
}
</style>