<template>
      <div class="card-with-margin">
        <t-row :gutter="[16, 16]">
          <t-col
            v-for="product in productList"
            :key="product.index"
            :lg="3"
            :xs="5"
            :xl="3"
          >
            <product-card
              class="list-card-item"
              :product="product"
              @click="handleClickProduct(product)"
            />
          </t-col>

          <t-col style="margin: 10px">
          <t-card header-bordered hover-shadow>
            <template #title>
              <h1 style="font-size: 20px; margin: 10px">
                活动看板
              </h1>
            </template>
            <t-space :size="50" style="margin: 20px">
              <template #separator>
                <t-divider layout="vertical" style="height:100%" />
              </template>
              <t-statistic :animation="{
        valueFrom: 0,
        duration: 2000,
      }" title="总活动数" :value="infoList.event" unit="个"
                           :animation-start="start"/>
              <t-statistic :animation="{
        valueFrom: 0,
        duration: 2000,
      }" title="待审核活动数" :value="infoList.audit" unit="个" color="red"
                           :animation-start="start">
              </t-statistic>
            </t-space>
          </t-card>
          </t-col>

          <t-col style="margin: 10px">
            <t-card header-bordered hover-shadow>
              <template #title>
                <h1 style="font-size: 20px; margin: 10px">
                  用户数
                </h1>
              </template>
              <t-space :size="50"  align="center" style="margin: 20px">
                <t-icon name="usergroup" class="icon" />
                <t-statistic  :animation="{
        valueFrom: 0,
        duration: 2000,
      }"  :animation-start="start" title="总数" :value="infoList.user" unit="个" />
              </t-space>
            </t-card>
          </t-col>

          <t-col style="margin: 10px">
            <t-card header-bordered hover-shadow>
              <template #title>
                <h1 style="font-size: 20px; margin: 10px">
                  动态数
                </h1>
              </template>
              <t-space :size="50"  align="center" style="margin: 20px">
                <t-icon name="animation" class="icon" />
                <t-statistic  :animation="{
        valueFrom: 0,
        duration: 2000,
      }"  :animation-start="start" title="总数" :value="infoList.comment" unit="个" />
              </t-space>
            </t-card>
          </t-col>

          <t-col style="margin: 10px">
            <t-card header-bordered hover-shadow>
              <template #title>
                <h1 style="font-size: 20px; margin: 10px">
                  成交量
                </h1>
              </template>
              <t-space :size="50"  align="center" style="margin: 20px">
                <t-icon name="bill" class="icon" />
                <t-statistic  :animation="{
        valueFrom: 0,
        duration: 2000,
      }"  :animation-start="start" title="订单总数" :value="infoList.order" unit="笔" />
              </t-space>
            </t-card>
          </t-col>


        </t-row>
      </div>
</template>

<script>
export default {
  name: 'ListCard',
};
</script>

<script setup>
import {onMounted, ref} from 'vue';

import ProductCard from './components/product-card.vue';
import router from '@/routers';
import axios from "axios";

const productList = ref([
  {
    index: 1,
    name: '用户管理',
    description: '管理用户的相关信息',
    type: 1,},
  // {
  //   index: 2,
  //   name: '活动管理',
  //   description: '管理活动的相关信息',
  //   type: 2,
  // },
  {
    index: 3,
    name: '活动审核',
    description: '审核活动的相关信息',
    type: 3,},
  {
    index: 4,
    name: '动态管理',
    description: '审核动态内容',
    type: 4,},
  // {
  //   index: 5,
  //   name: '支付流水',
  //   description: '查看最近支付流水信息',
  //   type: 5,},
  {
    index: 6,
    name: '性能监控',
    description: '监控系统性能、资源使用',
    type: 6,},
]);

const infoList = ref(
    {
      event: 1,
      audit: 1,
      user: 1,
      comment: 1,
      order: 1,
    });
const start = ref(false);
onMounted(() => {
  axios.get(`/admin/homepage`, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  })
      .then(response => {
        infoList.value = response.data.data;
        start.value = true;
          }
      )
      .catch();
});

const handleClickProduct = (product) => {
  if (product.type === 1) {
    router.push('/admin/userManage');
  }
  // else if (product.type === 2) {
  //   router.push('/admin/eventManage');
  // }
  else if (product.type === 3) {
    router.push('/admin/approval');
  } else if (product.type === 4) {
    router.push('/admin/momentAudit');
  // } else if (product.type === 5) {
  //   router.push('/admin/payment');
  } else if (product.type === 6) {
    window.open('https://10.16.222.144:26202/a181d591', '_blank');
  }
};
</script>

<style lang="less" scoped>
.list-card {
  height: 100%;

  &-operation {
    display: flex;
    justify-content: space-between;
    margin-bottom: var(--td-comp-margin-xxl);

    .search-input {
      width: 360px;
    }
  }

  &-item {
    margin: 10px;
    padding: var(--td-comp-paddingTB-xl) var(--td-comp-paddingTB-xl);

    :deep(.t-card__header) {
      padding: 0;
    }

    :deep(.t-card__body) {
      padding: 0;
      margin-top: var(--td-comp-margin-xxl);
      margin-bottom: var(--td-comp-margin-xxl);
    }

    :deep(.t-card__footer) {
      padding: 0;
    }
  }

  &-pagination {
    padding: var(--td-comp-paddingTB-xl) var(--td-comp-paddingTB-xl);
  }

  &-loading {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.icon {
  font-size: 32px;
  color: var(--td-brand-color);
  background: var(--td-brand-color-light);
  border-radius: var(--td-radius-medium);
  padding: 12px;
}

.card-with-margin {
  margin: 20px;
}
</style>
