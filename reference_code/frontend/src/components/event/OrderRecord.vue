<template>
  <t-card v-loading="loading" :bordered="false" shadow class="card-with-margin">
  <t-space direction="vertical">
    <h1 class="title"> 预定情况：{{eventName}} </h1>
    <t-table
        row-key="index"
        :data="data"
        :columns="columns"
        stripe="true"
        bordered="true"
        hover="true"
        table-layout='fixed'
        size='medium'
        :pagination="pagination"
        show-header="true"
        cell-empty-content="-"
        resizable
        lazy-load
    >
    </t-table>
  </t-space>
    </t-card>
</template>

<script setup lang="jsx">
import {onMounted, ref} from 'vue';
import axios from "axios";

const eventName = ref('');

const eventId = sessionStorage.getItem('eventId');
// const eventId = 1;
const data = ref([]);
const total = ref(0);

const columns = ref([
  { colKey: 'id', title: '申请人ID', width: '100' },
  { colKey: 'applicant', title: '申请人', width: '100' },
  { colKey: 'session', title: '场次开始时间' },
  { colKey: 'seat', title: '预定票价',width: '150' },
  { colKey: 'info', title: '额外信息', ellipsis: true},
  { colKey: 'email', title: '邮箱地址', ellipsis: true },
  { colKey: 'createTime', title: '申请时间' },
]);

const loading = ref(false);
onMounted(() => {
  loading.value = true;
  axios.get(`/orderRecord/getEventOrderRecord/${eventId}`, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  })
      .then(response => {
        data.value = response.data.data.orderRecordList.map(item => ({
          id: item.user.id,
          applicant: item.user.name,
          session: item.eventSession.startTime.replace('T', ' '),
          seat: item.price===0?'免费':'￥'+item.price+" ("+item.seatId+")",
          info: JSON.parse(item.additionalInformation).map(obj => `${obj.name}：${obj.value} `),
          email: item.user.email,
          createTime: item.paymentTime.replace('T', ' '),
            }));
        eventName.value = response.data.data.event.name;
            loading.value = false;
            total.value = response.data.data.orderRecordList.length;
          }
      )
      .catch();
});

const pagination = {
  defaultCurrent: 1,
  defaultPageSize: 10,
  total:total.value,
};
</script>


<style scoped>
.card-with-margin {
  margin: 20px;
}

.spacing {
  height: 30px;
}

.title {
  margin: 50px 40px;
  font-size: 50px;
}

</style>