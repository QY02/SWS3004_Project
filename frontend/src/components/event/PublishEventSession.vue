<template>
  <div class="t-table-demo__editable-row" style="width: 100%">
    <div>
      <t-button @click="onOpenAddDiag">
        <template #icon>
          <add-icon/>
        </template>
        Add a Session
      </t-button>
    </div>
    <br/>
    <t-table
        row-key="key"
        :columns="columns"
        :rules="FORM_RULES"
        :data="data"
        stripe:true
        bordered:true
        :pagination="pagination"
        lazy-load:true
        @validate="onValidate"
        empty="No data"
    >
      <template #operation="{ row }">
        <div class="operations">
          <t-link theme="primary" hover="color" :data-id="row.key" @click="onEdit(row.key)">Edit</t-link>
          <t-link theme="danger" hover="color" :data-id="row.key" @click="onDelete(row.key)">Delete</t-link>
        </div>
      </template>

    </t-table>
  </div>

  <t-dialog
      v-model:visible="visibleBody"
      :header="dialogHeader"
      placement="center"
      :width="'765px'"
      :cancel-btn=null
      :confirm-btn=null
  >
    <template #body>
      <t-space direction="vertical">
        <t-form
            ref="form"
            :data="Data"
            reset-type="initial"
            @reset="onReset"
            @submit="onSubmit"
            :rules="FORM_RULES"
            label-width="270px"
            label-align="right"
        >
          <t-form-item label="Register start time - Register end time" name="registration_time_range">
            <t-date-range-picker :enable-time-picker="true" clearable
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="Data.registration_time_range"
                                 :presets="presets"
                                 style="width: 420px;"
                                 placeholder="Please choose a date"
            />
          </t-form-item>
          <t-form-item label="Start time - End time" name="event_time_range">
            <t-date-range-picker :enable-time-picker="true" clearable
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="Data.event_time_range"
                                 :presets="presets"
                                 style="width: 420px;"
                                 placeholder="Please choose a date"
            />
          </t-form-item>

          <t-form-item label="SeatMap" name="seat_map_id">
            <t-cascader v-model="Data.seat_map_id" :options="seat_map_options" clearable style="width: 420px;"
                        placeholder="Please choose a seat map"/>
          </t-form-item>
          <t-form-item label="Venue" name="venue">
            <t-input v-model="Data.venue" style="width: 420px;" placeholder="Please input the venue">Venue</t-input>
          </t-form-item>
          <t-form-item class="confirm-reset-btns">
            <t-space size="small">
              <t-button theme="default" variant="base" type="reset">Reset</t-button>
              <t-button theme="success" type="submit">Submit</t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </t-space>
    </template>
  </t-dialog>
</template>

<script setup lang="jsx">
import {computed, onMounted, ref, watch} from 'vue';
import {DateRangePicker, Input, MessagePlugin} from 'tdesign-vue-next';
import {AddIcon} from "tdesign-icons-vue-next";
import {useVModel} from "@vueuse/core";
import dayjs from "dayjs";
import axios from "axios";

const fullUserId = sessionStorage.getItem('fullUserId');
const token = sessionStorage.getItem('token')

const INITIAL_DATA = {
  key: 0,
  registration_time_range: [],
  event_time_range: [],
  seat_map_id: '',
  venue: ''
}
const Data = ref({...INITIAL_DATA});
let state = 0 //0 -> add ; 1 -> modify
const props = defineProps({
  sessionData: Array
})
const emit = defineEmits(['update:sessionData'])

const data = useVModel(props, 'sessionData', emit)
watch(data, (newValue) => {
  if (Array.isArray(newValue)) {
    newValue.forEach((item, index) => {
      if (item && typeof item === 'object') {
        // 将每个元素的 key 属性设置为当前索引加一
        item.key = index + 1;
      } else {
        console.warn(`Item at index ${index} is not an object:`, item);
      }
    });
  } else {
    console.error('Expected data to be an array, but received:', newValue);
  }
});

const presets = ref({
  "Recent 7 days": [dayjs().format(), dayjs().add(7, 'day').format()],
  "Recent 3 days": [dayjs().format(), dayjs().add(3, 'day').format()],
  "Recent 1 days": [dayjs().format(), dayjs().add(1, 'day').format()],
});

const visibleBody = ref(false);
const form = ref(null)
// #### 数据 END ############
// ### 座位图 START ############

const seat_map_options = ref([])
const getSeatMap = async () => {
  await axios.get(`/seatMapTemplate/getAllSeatMapTemplateName`, {
        headers: {
          fullUserId: fullUserId,
          token: token
        }
      }
  ).then(response => {
        console.log("Fetch seat map data success", response)
        seat_map_options.value = response.data.data
      }
  ).catch();
}
onMounted(() => getSeatMap())

// ########表格 START ############
const columns = computed(() => [
  {
    title: 'Index', colKey: 'key',
    width: 80, align: 'center',
    fixed: 'left',
    cell: (h, {rowIndex}) => {
      return rowIndex + 1;
    },
  },
  {
    title: 'Register time', colKey: 'registration_time_range',
    width: 330, align: 'center',
    cell: (h, {row}) => {
      const displayValue = row.registration_time_range;
      return `${displayValue[0]} ~ ${displayValue[1]}`;
    },
  },
  {
    title: 'Event session time', colKey: 'event_time_range',
    width: 330, align: 'center',
    cell: (h, {row}) => {
      const displayValue = row.event_time_range;
      return `${displayValue[0]} ~ ${displayValue[1]}`
    },
  },
  {
    title: 'Seat map', colKey: 'seat_map_id',
    width: 200, align: 'center',
    cell: (h, {row}) => {
      const x = row.seat_map_id.split('.');
      const displayValue = x[x.length - 1]
      return `${displayValue}`
    },
  },
  {
    title: 'Venue', colKey: 'venue',
    width: 200, align: 'center',
    ellipsis: true,
  },
  {
    title: 'Operations',
    colKey: 'operation',
    width: 150, fixed: "right"
  },
]);
const pagination = computed(() => ({
  defaultCurrent: 1,
  defaultPageSize: 5,
  total: data.value.length,
  totalContent: false,
  pageSizeOptions: [
    {label: "5 items per page", value: 5},
    {label: "10 items per page", value: 10},
    {label: "20 items per page", value: 20},
    {label: "50 items per page", value: 50}
  ]
}));
// ########表格 END ############

// ##### Form START #############
const FORM_RULES = ref({
  registration_time_range: [{required: true, message: 'Register time is required'}],
  event_time_range: [{required: true, message: 'Event session time is required'}],
  venue: [{required: true, message: 'Venue is required'}],
  seat_map_id: [{
    required: true,
    message: 'Seat map is required'
  }],
});
const dialogHeader = ref('Please input event session information');

// ##### Form END #############


// #####  表单操作 START ###############
const onOpenAddDiag = async () => {
  await handleClear();
  Data.value = {...INITIAL_DATA}
  state = 0
  visibleBody.value = true
}
const onEdit = async (id) => {
  await handleClear();
  Data.value = await {...data.value.find(k => k.key === id)}
  state = 1
  visibleBody.value = true
};
const onSubmit = async ({validateResult, firstError, e}) => {
  e.preventDefault();
  if (validateResult === true) {
    if (state === 0) {
      data.value = [...data.value, Data.value]
      Data.value = {...INITIAL_DATA}
      await MessagePlugin.success('Create event session information success');
    } else {
      const id = Data.value.key
      // 找到要修改的数据在 data.value 中的索引
      const index = data.value.findIndex((item) => item.key === id);
      if (index !== -1) {
        const updatedData = [...data.value];
        updatedData[index] = {...Data.value};
        data.value = updatedData;
        await MessagePlugin.success('Edit success');
      } else {
        await MessagePlugin.error('Cannot find the line to edit');
      }
    }
    // await handleClear();
    visibleBody.value = false
  } else {
    console.log('Validate Errors: ', firstError, validateResult);
    await MessagePlugin.warning(firstError);
  }
}

const onDelete = async (id) => {
  const index = data.value.findIndex((item) => item.key === id);
  if (index !== -1) {
    const temp = [...data.value];
    temp.splice(index, 1);
    data.value = temp;
    // visibleBody.value = false
    await MessagePlugin.success('Delete success');
  } else {
    await MessagePlugin.error('Cannot find the line to delete');
  }
};


function onValidate(params) {
  console.log('Event Table Data Validate:', params);
}

const onReset = async () => {
  if (state === 0) {
    Data.value = {...INITIAL_DATA}
  } else {
    const key = await Data.value.key
    Data.value = {...INITIAL_DATA}
    Data.value.key = key
  }
  await MessagePlugin.success('Reset success');
};
const handleClear = () => {
  form.value.clearValidate();
};
// #####  表单操作 END ###############

</script>
<style lang="css" scoped>
.operations {
  display: flex;
  gap: 10px;
}

.confirm-reset-btns {
  display: flex;
  justify-content: right;
  align-items: center;
}
</style>
