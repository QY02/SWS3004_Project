<template>
    <!-- <div height="300px">
        <t-calendar controllerConfig isShowWeekendDefault />
    </div> -->

<t-calendar style="height: 680px;">
    <template #cellAppend="data" >
    <div v-if="getShow(data)" class="cell-append-demo-outer">
        <t-tag theme="success" variant="light" size="small" class="activeTag" style="width: 100%">
        {{ data.mode == 'month' ? '我们的纪念日' : '我们的纪念月' }}
        </t-tag>
        <t-tag theme="warning" variant="light" size="small" class="activeTag" style="width: 100%">
        {{ data.mode == 'month' ? '家庭聚会' : '家庭聚会' }}
        </t-tag>
    </div>
    </template>
    <!-- <template #cell="cellData">
        <div :style="{ height: '5px', background: '#5e6066' }">
            {{ cellData.date }}
            {{ JSON.stringify(cellData) }}
        </div>
    </template> -->
</t-calendar>
</template>


<script setup>
import dayjs from 'dayjs';

const getShow = (data) =>
data.mode === 'month'
    ? dayjs().format('YYYY-MM-DD') === data.formattedDate
    : data.date.getMonth() === new Date().getMonth();
</script>
  