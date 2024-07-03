<template>
  <t-card :bordered="false" shadow class="card-with-margin">
    <t-button class="button-right" size="medium" theme="primary" variant="base" @click="viewHistory">审核历史</t-button>
    <h1 class="title"> 审核 </h1>
    <t-divider/>
    <t-collapse borderless="true" expand-mutex>
      <t-collapse-panel value="0" header="筛选" >
        <t-space direction="vertical">
          <t-space direction="horizontal">
            <t-input placeholder="活动名称" v-model:value="filterData.eventName" clearable>
              <template #suffixIcon>
                <search-icon :style="{ cursor: 'pointer' }" />
              </template><t-button class="button-right" theme="default" size="small" variant="base" @click="viewHistory">审核历史</t-button>
            </t-input>
            <t-range-input v-model:value="filterData.priceRange" placeholder="请输入价格"
                           clearable
                           :tips="errorPriceTips"
                           :status="errorPriceTips ? 'error' : ''"
                           @change="onPriceChange">
              <template #suffixIcon>
                <money-icon :style="{ cursor: 'pointer' }" />
              </template>
            </t-range-input>
          </t-space>
          <t-space direction="horizontal">
            <t-input placeholder="地点" v-model:value="filterData.location" clearable>
              <template #suffixIcon>
                <search-icon :style="{ cursor: 'pointer' }" />
              </template>
            </t-input>
            <t-date-range-picker enable-time-picker allow-input clearable v-model:value="filterData.dateRange" />
          </t-space>
          <t-select
              :options="eventType"
              placeholder="请选择活动类型"
              v-model:value="filterData.eventType"
              multiple
          />
          <t-space direction="horizontal">
            <t-button @click="onResetFilter" theme="default">重置</t-button>
            <t-button @click="onSubmitFilter">查询</t-button>
          </t-space>
        </t-space>
      </t-collapse-panel>
    </t-collapse>
    <div class="spacing"></div>
    <t-space v-loading="loading" direction="vertical" class="centered">
      <div v-if="listData.length === 0" class="centered">
        结果为空
      </div>
      <t-list v-else :split="true">
        <t-list-item v-for="item in listData" :key="item.id">
          <t-list-item-meta :image="item.avatar" :title="item.title" :description="item.description.length > 16 ? item.description.substring(0, 16) + '...' : item.description" />
          <t-space direction="vertical">
            <t-text>{{ item.date }}</t-text>
            <t-text>{{ item.location }}</t-text>
          </t-space>
          <t-space direction="horizontal">
            <t-tag theme="primary" variant="light">{{ item.type }}</t-tag>
            <t-tag theme="warning" variant="light">{{ item.price }}</t-tag>
          </t-space>
          <template #action>
            <t-tooltip content="详情">
            <t-button variant="text" shape="square" @click="viewDetail(item)">
              <icon name="task-1" />
            </t-button>
            </t-tooltip>
            <t-tooltip content="通过">
            <t-button variant="text" shape="square" @click="onSuccess(item.id)">
              <icon name="check" color="green" />
            </t-button>
            </t-tooltip>
            <t-tooltip content="拒绝">
            <t-button variant="text" shape="square" @click="onDelete(item.id)">
              <icon name="close" color="red" />
            </t-button>
            </t-tooltip>
          </template>
        </t-list-item>
      </t-list>
    </t-space>
    <div class="spacing"></div>
    <t-pagination
        :total="filter_list_data ? filter_list_data.length : 0"
        :default-current="1"
        :default-page-size="pageSize"
        show-first-and-last-page-btn
        @current-change="onCurrentChange"
        @page-size-change="onPageSizeChange"
    />
    <div class="spacing"></div>
  </t-card>
  <t-dialog
      v-model:visible="deleteVisible"
      theme="danger"
      header="拒绝申请"
      :on-close="closeDelete"
      :cancel-btn="null"
      :confirm-btn="null"
      @confirm="onClickConfirm"
  >
    <t-input placeholder="请输入拒绝原因"
             clearable
             :tips="deleteTips"
             :status="deleteTips ? 'error' : ''"
             @change="onDeleteChange"/>
    <template #footer>
      <t-button theme="danger" @click="onClickConfirm">确定</t-button>
      <t-button @click="closeDelete">取消</t-button>
    </template>
  </t-dialog>
  <t-dialog
      v-model:visible="successVisible"
      theme="success"
      header="确认通过申请"
      :on-close="closeSuccess"
      @confirm="onSuccessClickConfirm"
  />
  <t-dialog
      width="auto"
      showOverlay
      preventScrollThrough
      header="审核历史"
      destroyOnClose
      :footer="false"
      v-model:visible="historyVisible"
  >
    <t-table
        style="width: max-content"
        row-key="index"
        :data="historyData"
        :columns="historyColumns"
        :stripe="stripe"
        :bordered="bordered"
        :hover="hover"
        :table-layout="tableLayout ? 'auto' : 'fixed'"
        :size="size"
        :pagination="historyPagination"
        :show-header="showHeader"
        cell-empty-content="-"
        lazy-load
    >
      <template #status="{ row }">
        <t-tag shape="round" :theme="statusNameListMap[row.status].theme" variant="light-outline">
          {{statusNameListMap[row.status].label}}
        </t-tag>
      </template>
    </t-table>
  </t-dialog>
  <t-drawer v-model:visible="detailVisible" :header="currentEvent.title" :confirm-btn="null">
    <approval-detail :event="currentEvent"></approval-detail>
  </t-drawer>
</template>



<script setup>
import {ref, onMounted, getCurrentInstance, nextTick} from 'vue';
import {Icon} from 'tdesign-icons-vue-next';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from 'axios';
import {SearchIcon, MoneyIcon} from 'tdesign-icons-vue-next';
import ApprovalDetail from './ApprovalDetail.vue';
import {avatarList} from "@/constants/index.js"
const appConfig = ref(getCurrentInstance().appContext.config.globalProperties).value;

// ###### 数据 开始 ######
// 审核列表数据
const audit_list_data = ref(null);
// 筛选的数据
const filter_list_data = ref(null);
// 当前页展示的数据
const listData = ref([]);
// 分页
const pageSize = ref(5);
const eventType = [
  {label: '全选', checkAll: true},
  {label: '讲座', value: 0},
  {label: '工作坊', value: 1},
  {label: '比赛', value: 2},
  {label: '表演', value: 3},
  {label: '展览', value: 4},
  {label: '论坛', value: 5},
  {label: '体育', value: 6},
  {label: '志愿', value: 7},
  {label: '学院', value: 8},
  {label: '沙龙', value: 9},
  {label: '培训', value: 10},
  {label: '社团', value: 11},
  {label: '其他', value: 12},
];
const eventTypeMapping = {
  0: '讲座',
  1: '工作坊',
  2: '比赛',
  3: '表演',
  4: '展览',
  5: '论坛',
  6: '体育',
  7: '志愿',
  8: '学院',
  9: '沙龙',
  10: '培训',
  11: '社团',
  12: '其他',
};

const eventTypeReverseMapping = Object.fromEntries(
    Object.entries(eventTypeMapping).map(([key, value]) => [value, Number(key)])
);

const mapEventType = (type) => {
  return eventTypeMapping[type];
};
// ###### 数据 结束 ######

// ###### 获取数据 开始 ######

const loading = ref(true);
onMounted(() => {
  loading.value = true;
  axios.get(`/admin/getAuditList/0`, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  })
      .then(response => {
            audit_list_data.value = response.data.data.map(item => ({
              id: item.id,
              title: item.name,
              description: item.content,
              date: item.startTime.replace("T", " "),
              location: item.location,
              price: item.lowestPrice===item.highestPrice && item.highestPrice===0?"免费":"￥"+item.lowestPrice + "起",
              lowestPrice: item.lowestPrice,
              highestPrice: item.highestPrice,
              policy: item.eventPolicy,
              type: mapEventType(item.type),
              status: item.status,
              publisherId: item.publisherId,
              publishDate: item.publishDate.replace("T", " "),
              avatar: avatarList[item.avatar]
            }));
            filter_list_data.value = audit_list_data.value;
            listData.value = filter_list_data.value.slice(0, pageSize.value);
            loading.value = false;
          }
      )
      .catch();
});
// ###### 获取数据 结束 ######

// ###### 查看审核历史 开始 ######
const stripe = ref(true);
const bordered = ref(true);
const hover = ref(false);
const tableLayout = ref(true);
const size = ref('small');
const showHeader = ref(true);

const statusNameListMap = {
  0: {label: '等待审批', theme: 'warning'},
  1: {label: '审批通过', theme: 'success'},
  2: {label: '审批失败', theme: 'danger'},
};

const historyVisible = ref(false);
const historyData = ref(null);

const viewHistory = async () => {
  try {
    const response = await axios.get('/admin/getAuditList/1,2', {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    historyData.value = response.data.data.map(item => ({
      name: item.name,
      startTime: item.startTime.replace("T", " "),
      location: item.location,
      lowestPrice: item.lowestPrice,
      type: mapEventType(item.type),
      status: item.status,
      publisherId: item.publisherId,
      publishDate: item.publishDate.replace("T", " "),
    }));
    historyPagination.value.total = historyData.value.length;
    historyVisible.value = true;
  } catch (error) {
    console.error(error);
  }
};


const historyColumns = ref([
  {colKey: 'name', title: '活动名称'},
  {colKey: 'status', title: '申请状态',},
  {colKey: 'publishDate', title: '申请时间'},
  {colKey: 'publisherId', title: '申请人ID'},
  {colKey: 'type', title: '活动类型'},
  {colKey: 'startTime', title: '活动时间'},
  {colKey: 'location', title: '活动地点'},
]);

let historyPagination = ref({
  defaultCurrent: 1,
  defaultPageSize: 10,
  total: 0,
});
// ###### 查看审核历史 结束 ######

// ###### 搜索 开始 ######
// 重置表单
const filterData = ref({
  eventName: '',
  priceRange: [],
  location: '',
  eventType: [],
  dateRange: [],
});

// 价格验证
const errorPriceTips = ref('');
const onPriceChange = (value) => {
  if (isNaN(value[0]) && value[0] !== undefined || isNaN(value[1]) && value[1] !== undefined) {
    errorPriceTips.value = '输入必须是数字';
  } else if (value[0] > value[1] && value[0] !== undefined && value[1] !== undefined && value[0] !== "" && value[1] !== "" || value[0] < 0 || value[1] < 0) {
    errorPriceTips.value = '价格范围不合法';
  } else {
    errorPriceTips.value = '';
  }
};

const onResetFilter = () => {
  filterData.value = {
    eventName: '',
    priceRange: [],
    location: '',
    eventType: [],
    dateRange: [],
  };
  nextTick(() => {
    filter_list_data.value = [...audit_list_data.value];
    listData.value = [...filter_list_data.value];
  });
};


const onSubmitFilter = () => {
  const {eventName, priceRange, location, eventType, dateRange} = filterData.value;
  console.log('eventType', eventType);
  filter_list_data.value = audit_list_data.value.filter(item => {
    if (eventName && !item.title.includes(eventName)) {
      return false;
    }
    let real_price;
    if (item.price === "免费") {
      real_price = 0;
    } else {
      real_price = item.price.slice(1, -1);
    }
    if (priceRange.length >0 && (real_price < priceRange[0] || real_price > priceRange[1])) {
      return false;
    }
    if (location && !item.location.includes(location)) {
      return false;
    }
    if (eventType.length > 0 && !eventType.includes(eventTypeReverseMapping[item.type])) {
      return false;
    }
    return !(dateRange.length > 0 && (item.date < dateRange[0] || item.date > dateRange[1]));

  });
  listData.value = filter_list_data.value.slice(0, pageSize.value);
};

// ###### 搜索 结束 ######

// ###### 列表 开始 ######
const avatarUrl = 'https://tdesign.gtimg.com/site/avatar.jpg';
// ###### 列表 结束 ######

// ###### 分页 开始 ######
const onPageSizeChange = (pageInfo) => {
  pageSize.value = pageInfo;
  listData.value = filter_list_data.value.slice(0, pageSize.value);
};

const onCurrentChange = (index) => {
  const start = (index - 1) * pageSize.value;
  const end = start + pageSize.value;
  listData.value = filter_list_data.value.slice(start, end);
};

// ###### 分页 结束 ######

// ###### 对话框 开始 ######
const currentEvent = ref({});
const deleteVisible = ref(false);
const successVisible = ref(false);
const detailVisible = ref(false);
const deleteTips = ref('请输入拒绝原因');
let currentEventId = ref(null);
let deleteReason = ref('');

const onDeleteChange = (value) => {
  deleteTips.value = value ? '' : '请输入拒绝原因';
  deleteReason = value;
};

const onDelete = (eventId) => {
  deleteVisible.value = true;
  currentEventId.value = eventId;
};

const viewDetail = (event) => {
  detailVisible.value = true;
  currentEvent.value = event
};

const onSuccess = (eventId) => {
  successVisible.value = true;
  currentEventId.value = eventId;
};

const confirm = async () => {
  try {
    const status = deleteReason === "" ? 1 : 2;
    await axios.post('/admin/changeAudit', {
      eventId: currentEventId.value,
      status: status,
      reason: deleteReason,
    }, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    MessagePlugin.success('操作成功');
  } catch (error) {
  }
};


const onClickConfirm = () => {
  if (deleteTips.value === '请输入拒绝原因') {
    return;
  }
  confirm();
  deleteVisible.value = false;
  location.reload();
};

const onSuccessClickConfirm = () => {
  deleteReason = "";
  confirm();
  successVisible.value = false;
  location.reload();
};

const closeSuccess = () => {
  successVisible.value = false;
};

const closeDelete = () => {
  deleteVisible.value = false;
};
// ###### 对话框 结束 ######
</script>

<style scoped>

.card-with-margin {
  margin: 20px;
}

.centered {
  width: 98%;
  align-items: center;
}

.spacing {
  height: 30px;
}

.title {
  margin: 50px 40px;
  font-size: 50px;
}

.button-right {
  position: absolute;
  top: 10px;
  right: 10px;
}

</style>
