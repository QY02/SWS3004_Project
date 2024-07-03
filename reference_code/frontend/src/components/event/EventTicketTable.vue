<template>
    <div style="height: 25px;"></div>
    <div style="height: 700px;">
        <!-- 1. 此处代码有效，勿删！支持语法糖 filter-value.sync ， 支持非受控属性 defaultFilterValue -->
        <!-- 2. 其中，filterIcon 用于自定义筛选图标，支持渲染函数 props.filterIcon，支持插槽 filterIcon。 -->
        <!-- 3. filterRow={() => null}，则不会显示过滤行 -->
        <!-- <t-table
        rowKey='key'
        :columns="columns"
        :data="data"
        :filter-value.sync="filterValue"
        :filterIcon="filterIcon"
      >
        <template #filterRow>自定义过滤行信息</template>
</t-table> -->

        <!-- 1. v-model:filter-value 等同于 filter-value + filter-change -->
        <!-- 2. :filter-row="null" 用于隐藏过滤结果行 -->
        <!-- 3. <template #filterRow><p>这是自定义的过滤结果行</p></template> ，可使用插槽完全自定义结果行内容-->
        <!-- 4. :attach="getAttach" 统一控制浮层挂载元素 -->
        <!-- 5. 每一列自定义不同筛选图标：
        <template #filterIcon="{ col, colIndex }">
          <div><FilterIcon /> {{ colIndex }}</div>
        </template>
      -->
        <t-table row-key="key" 
            :columns="columns" 
            :data="data" 
            :filter-value="filterValue" 
            resiable
            bordered
            rowHeight="10"
            max-height="650"
            lazy-load @filter-change="onFilterChange">
            <template #operation="{ row }">
               <t-button large
               >
               买票
                </t-button>
            </template>
            <!-- <template #createTime="{ row }">
                <div style=" display: flex; align-items: center; height: 60px;">
                    {{ row.createTime }}
                </div>
            </template> -->
            <template #event="{ row }" style="display: flex; white-space: pre-wrap;">
                <div style=" display: flex; align-items: center;  white-space: pre-wrap; height: 30px;">
                    {{ row.time }}
                    <br>
                    <div style="font-weight: 300;">{{ row.place }}</div>
                </div>
            </template>
        </t-table>
    </div>
</template>

<script setup lang="jsx">
import { ref, computed } from 'vue';
import { DateRangePickerPanel } from 'tdesign-vue-next';
import { ErrorCircleFilledIcon, CheckCircleFilledIcon, CloseCircleFilledIcon } from 'tdesign-icons-vue-next';
import isNumber from 'lodash/isNumber';
import React from 'react';

const initData = new Array(10).fill(null).map((_, i) => ({
    key: String(i + 1),
    applicant: ['贾明', '张三', '王芳'][i % 3],
    time: ['6:15PM','6:15PM','6:15PM'][i % 3],
    place: ['Stockholm, SEFriends Arena','Stockholm, SEFriends Arena'][i % 2],
    createTime: ['2022-01-01', '2022-02-01', '2022-03-01', '2022-04-01', '2022-05-01'][i % 4],
}));

const align = ref('left');

const onEmailChange = (val, ctx) => {
    console.log(val, ctx);
};

const columns = computed(() => [
    {
        title: '活动时间',
        colKey: 'createTime',
        width: 200,
        // 用于查看同时存在排序和过滤时的图标显示是否正常
        // sorter: true,
        // 自定义过滤组件：日期过滤配置，请确保自定义组件包含 value 和 onChange 属性
        filter: {
            component: DateRangePickerPanel,
            props: {
                firstDayOfWeek: 7,
            },
            style: { fontSize: '14px'},
            classNames: 'custom-class-name',
            attrs: { 'data-type': 'date-range-picker' },
            // 是否显示重置取消按钮，一般情况不需要显示
            // 日期范围是一个组件，重置时需赋值为 []
            resetValue: [],
        },
    },
    { colKey: 'time', title: '时间', width: 100},
    { colKey: 'event', title: '详情', foot: '-' },
    { colKey: 'venue', title: '地点', foot: '-' },
    { colKey: 'operation', title: '操作', width: 100},
]);

const filterValue = ref({ channel: [] });
const data = ref([...initData]);

const request = (filters) => {
    const timer = setTimeout(() => {
        clearTimeout(timer);
        const newData = initData.filter((item) => {
            let result = true;
            if (isNumber(filters.status)) {
                result = item.status === filters.status;
            }
            if (result && filters.channel && filters.channel.length) {
                result = filters.channel.includes(item.channel);
            }
            if (result && filters.email) {
                result = item.email.indexOf(filters.email) !== -1;
            }
            if (result && filters.createTime && filters.createTime.length) {
                result = item.createTime === filters.createTime;
            }
            return result;
        });
        data.value = newData;
    }, 100);
};

const onFilterChange = (filters, ctx) => {
    console.log('filter-change', filters, ctx);
    filterValue.value = {
        ...filters,
        createTime: filters.createTime || [],
        channel: filters.channel || [],
    };
    console.log(filters);
    request(filters);
};

const setFilters = () => {
    filterValue.value = {};
    data.value = [...initData];
};

// const getAttach = () => document.body;
</script>
<style scoped>
.table-operations {
    margin-bottom: 16px;
}

.table-operations>button {
    margin-right: 8px;
}

.t-demo__style .t-table .custom-third-class-name > td {
  background-color: var(--td-brand-color-light);
  font-weight: bold;
}

.t-demo__style .t-table td.last-column-class-name {
  color: orange;
}

.t-table td.custom-cell-class-name {
  color: orange;
  font-weight: bold;
}
.t-table__row-filter-inner {
  display: flex;
  align-items: center;
  justify-content: center;
}
.t-table__row-filter-inner svg {
  margin-right: 8px;
}
</style>