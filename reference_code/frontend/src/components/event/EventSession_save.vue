<template>
  <div class="t-table-demo__editable-row" style="width: 100%">
    <div>
      <t-button @click="onOpenAddDiag">
        <template #icon>
          <add-icon/>
        </template>
        添加新场次
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
    >
      <template #operation="{ row }">
        <div class="operations">
          <t-link theme="primary" hover="color" :data-id="row.key" @click="onEdit(row.key)">编辑</t-link>
          <t-link theme="danger" hover="color" :data-id="row.key" @click="onDelete(row.key)">删除</t-link>
        </div>
      </template>

    </t-table>
  </div>

  <t-dialog
      v-model:visible="visibleBody"
      header="请填写场次信息"
      placement="center"
      :width="DIAG_WIDTH"
      :cancel-btn=null
      :confirm-btn=null
  >
    <template #body>
      <t-space direction="vertical">
        <t-form
            ref="form"
            id="form"
            :data="newData"
            reset-type="initial"
            @reset="onReset"
            @submit="onAdd"
            :rules="FORM_RULES"
            label-width="200px"
            label-align="right"
        >
          <t-form-item label="是否需要报名" name="registration_required">
            <t-switch v-model="newData.registration_required" :label="['是', '否']"></t-switch>
          </t-form-item>
          <t-form-item label="报名开始时间-报名结束时间" name="registration_time_range">
            <t-date-range-picker enable-time-picker=true allow-input=true clearable=true
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="newData.registration_time_range"
                                 :disabled="!newData.registration_required"
                                 :presets="presets"
            />
          </t-form-item>
          <t-form-item label="开始时间-结束时间" name="event_time_range">
            <t-date-range-picker enable-time-picker=true allow-input=true clearable=true
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="newData.event_time_range"
                                 :presets="presets"
            />
          </t-form-item>

          <t-form-item label="人数" name="count_range_of_people">
            <t-range-input v-model="newData.count_range_of_people" :placeholder="['最小值','最大值']"/>
          </t-form-item>
          <t-form-item label="座位图" name="seat_map_id">
            <t-cascader v-model="newData.seat_map_id" :options="seat_map_options" clearable/>
          </t-form-item>
          <t-form-item label="地址" name="venue">
            <t-input v-model="newData.venue">地址</t-input>
          </t-form-item>
          <t-form-item label="地图" name="location">
            <t-button @click="()=>{chooseLocationDialogVisible = true;Data=newData}" variant="outline">
              <template #icon>
                <MapInformation2Icon/>
              </template>
              {{ newData.location === null ? "选择位置" : `${newData.location[0]}, ${newData.location[1]}` }}
            </t-button>
          </t-form-item>
          <t-form-item label="是否可见" name="visible">
            <t-switch v-model="newData.visible" :label="['是', '否']"></t-switch>
          </t-form-item>

          <t-form-item class="confirm-reset-btns">
            <t-space size="small">
              <t-button theme="success" type="submit">提交</t-button>
              <t-button theme="default" variant="base" type="reset">重置</t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </t-space>
    </template>
  </t-dialog>

  <t-dialog
      v-model:visible="visibleBodyModify"
      header="编辑该场次信息"
      placement="center"
      :width="DIAG_WIDTH"
      :cancel-btn=null
      :confirm-btn=null
  >
    <template #body>
      <t-space direction="vertical">
        <t-form
            :data="modifyData"
            :rules="FORM_RULES"
            label-width="200px"
            label-align="right"
            @submit="onModify(modifyData.key)"
        >
          <t-form-item label="是否需要报名" name="registration_required">
            <t-switch v-model="modifyData.registration_required" :label="['是', '否']"></t-switch>
          </t-form-item>
          <t-form-item label="报名开始时间-报名结束时间" name="registration_time_range">
            <t-date-range-picker enable-time-picker=true allow-input=true clearable=true
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="modifyData.registration_time_range"
                                 :disabled="!modifyData.registration_required"
                                 :presets="presets"
            />
          </t-form-item>
          <t-form-item label="开始时间-结束时间" name="event_time_range">
            <t-date-range-picker enable-time-picker=true allow-input=true clearable=true
                                 :disable-date="{ before: dayjs().subtract(1, 'day').format() }"
                                 v-model="modifyData.event_time_range"
                                 :presets="presets"
            />
          </t-form-item>

          <t-form-item label="人数" name="count_range_of_people">
            <t-range-input v-model="modifyData.count_range_of_people" :placeholder="['最小值','最大值']"/>
          </t-form-item>
          <t-form-item label="座位图" name="seat_map_id">
            <t-cascader v-model="modifyData.seat_map_id" :options="seat_map_options" clearable/>
          </t-form-item>
          <t-form-item label="地址" name="venue">
            <t-input v-model="modifyData.venue">地址</t-input>
          </t-form-item>
          <t-form-item label="地图" name="location">
            <t-button @click="()=>{chooseLocationDialogVisible = true;Data=modifyData}" theme="default"
                      variant="outline">
              <template #icon>
                <MapInformation2Icon/>
              </template>
              {{ modifyData.location === null ? "选择位置" : `${modifyData.location[0]}, ${modifyData.location[1]}` }}
            </t-button>
          </t-form-item>
          <t-form-item label="是否可见" name="visible">
            <t-switch v-model="modifyData.visible" :label="['是', '否']"></t-switch>
          </t-form-item>
          <t-form-item class="confirm-reset-btns">
            <t-space size="small">
              <t-button theme="success" type="submit">提交</t-button>
              <t-button theme="default" variant="base" type="reset">重置</t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </t-space>
    </template>
  </t-dialog>
  <t-dialog v-model:visible="chooseLocationDialogVisible" placement="center" width="50vw" header="选择位置"
            :onConfirm="handleChooseLocationConfirm" :onClose="handleChooseLocationCancel" @opened="showMap">
    <div id="mapContainer" class="choose-location-map-div">
      <div class="search-place-div">
        <input class="search-place-input" v-model="searchPlaceInputValue" placeholder="搜索地点"
               id="searchPlaceInput"></input>
        <div class="search-place-result-div" id="searchPlaceResultDiv"></div>
      </div>
    </div>
  </t-dialog>
</template>

<script setup>
import {computed, onUnmounted, ref, watch} from 'vue';
import {DateRangePicker, Input, MessagePlugin, NotifyPlugin, RangeInput, Switch} from 'tdesign-vue-next';
import {AddIcon, MapInformation2Icon} from "tdesign-icons-vue-next";
import {useVModel} from "@vueuse/core";
import dayjs from "dayjs";
import {AMap} from "@/main";
import {ADDITIONAL_INFO_MAP} from "@/constants/index.js";

// #### 数据 START ############
const DIAG_WIDTH="600px"
const INITIAL_DATA = {
  key: 0,
  registration_required: false,
  registration_time_range: [],
  event_time_range: [],
  min_cnt: 10,
  max_cnt: 200,
  seat_map_id: '',
  venue: '',
  location: null,
  additional_information_required: [],
  visible: false,
}
const newData = ref({...INITIAL_DATA});
const modifyData = ref({...INITIAL_DATA});
const Data = ref({...INITIAL_DATA});
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
// 座位图
const seat_map_options = ref([
  {
    label: '选项一',
    value: '1',
    children: [
      {
        label: '子选项一',
        value: '1.1',
      },
      {
        label: '子选项二',
        value: '1.2',
      },
      {
        label: '子选项三',
        value: '1.3',
      },
    ],
  },
  {
    label: '选项二',
    value: '2',
    children: [
      {
        label: '子选项一',
        value: '2.1',
      },
      {
        label: '子选项二',
        value: '2.2',
      },
    ],
  },
])
const presets = ref({
  最近7天: [dayjs().format(), dayjs().add(7, 'day').format()],
  最近3天: [dayjs().format(), dayjs().add(3, 'day').format()],
  最近1天: [dayjs().format(), dayjs().add(1, 'day').format()],
});

const visibleBody = ref(false);
const visibleBodyModify = ref(false);
// #### 数据 END ############


// ########表格 START ############
const columns = computed(() => [
  {
    title: '序号', colKey: 'key',
    width: 60, align: 'center',
    fixed: 'left',
    cell: (h, {rowIndex}) => {
      return rowIndex + 1;
    },
  },
  {
    title: '是否需要报名', colKey: 'registration_required',
    width: 120, align: 'center',
    cell: (h, {row}) => row.registration_required === true ? '是' : '否',
  },
  {
    title: '报名时间', colKey: 'registration_time_range',
    width: 330, align: 'center',
    cell: (h, {row}) => {
      let re = "无需预约"
      if (row.registration_required) {
        const displayValue = row.registration_time_range;
        re = `${displayValue[0]} ~ ${displayValue[1]}`;
      }
      return re
    },
  },
  {
    title: '活动时间', colKey: 'event_time_range',
    width: 330, align: 'center',
    cell: (h, {row}) => {
      const displayValue = row.event_time_range;
      return `${displayValue[0]} ~ ${displayValue[1]}`
    },
  },
  {
    title: '人数范围', colKey: 'count_range_of_people',
    width: 130, align: 'center',
    cell: (h, {row}) => {
      const displayValue = row.count_range_of_people;
      return `${displayValue[0]} ~ ${displayValue[1]}`
    },
  },
  {
    title: '座位', colKey: 'seat_map_id',
    width: 100, align: 'center',
  },
  {
    title: '地点', colKey: 'venue',
    width: 200, align: 'center',
    ellipsis: true,
  },
  {
    title: '地图', colKey: 'location',
    width: 200, align: 'center',
    cell: (h, {row}) => {
      const displayValue = row.location;
      return `(${displayValue[0]} , ${displayValue[1]})`
    },
  },
  {
    title: '所需额外信息', colKey: 'additional_information_required',
    width: 200, align: 'center',
    cell: (h, {row}) => {
      let re = "无";
      const additional_information_required = row.additional_information_required;
      let displayValue = [];

      console.log('Type of additional_information_required:', typeof additional_information_required);
      console.log(additional_information_required);

      if (Array.isArray(additional_information_required)) {
        if (additional_information_required.length > 0) {
          additional_information_required.forEach((x) => {
            console.log('x', x);

            // 使用 ADDITIONAL_INFO_MAP 进行映射
            let label = ADDITIONAL_INFO_MAP[x];
            if (label) {
              displayValue.push(label);
            }
          });

          re = `${displayValue.join(', ')}`;
        }
      }

      return re;
    }
  },
  {
    title: '是否可见', colKey: 'visible',
    align: 'center',
    cell: (h, {row}) => row.visible === true ? '是' : '否',
  },
  {
    title: '操作栏',
    colKey: 'operation',
    width: 150, fixed: "right"
  },
]);
const pagination = computed(() => ({
  defaultCurrent: 1,
  defaultPageSize: 5,
  total: data.value.length
}));
// ########表格 END ############

// ##### Form START #############
const count_range_of_peopleValidator = (val) => {
  // 将输入的字符串转化为数字
  const [first, second] = [Data.value.min_cnt,Data.value.max_cnt]

  if (isNaN(first) || first <= 0) {
    return {result: false, message: '最小值应该为正数', type: 'error'};
  }
  if (isNaN(second) || second <= 0) {
    return {result: false, message: '最大值应该为正数', type: 'error'};
  }

  // 检查第二个数字是否大于等于第一个数字
  if (second < first) {
    return {result: false, message: '最大值必须大于等于最小值', type: 'error'};
  }

  return {result: true};
};
const FORM_RULES = ref({
  registration_time_range: [{required: computed(() => newData.value.registration_required), message: '报名时间必填'}],
  event_time_range: [{required: true, message: '活动时间必填'}],
  count_range_of_people: [
    {required: true, message: '人数必填'},
    {validator: count_range_of_peopleValidator},
  ],
  venue: [{required: true, message: '地址必填'}],
  location: [{required: true, message: '地址必填'}],
  seat_map_id: [{required: true, message: '座位图必选'}],
});
// ##### Form END #############


// #####  表单操作 START ###############
const onOpenDiag = (x) => {
  if (x){

  }else{
    modifyData.value = data.value.find(k => k.key === id)
    visibleBodyModify.value = true
  }
  visibleBodyModify
}
const onEdit = (id) => {
  modifyData.value = data.value.find(k => k.key === id)
  visibleBodyModify.value = true
};
const onAdd = ({validateResult, firstError}) => {
  if (validateResult === true) {
    data.value = [...data.value, newData.value]
    // alert(JSON.stringify(newData.value))
    newData.value = {...INITIAL_DATA}
    // alert(JSON.stringify(newData.value))
    visibleBody.value = false;
    MessagePlugin.success('提交成功');
  } else {
    console.log('Validate Errors: ', firstError, validateResult);
    MessagePlugin.warning(firstError);
  }
}
const onModify = (id) => {
  // 找到要修改的数据在 data.value 中的索引
  const index = data.value.findIndex((item) => item.key === id);

  if (index !== -1) {
    const updatedData = [...data.value];
    updatedData[index] = modifyData.value;
    data.value = updatedData;
    visibleBodyModify.value = false
    MessagePlugin.success('修改成功');
  } else {
    MessagePlugin.error('未找到要修改的行');
  }
};


const onDelete = (id) => {
  const index = data.value.findIndex((item) => item.key === id);
  if (index !== -1) {
    const temp = [...data.value];
    temp.splice(index, 1);
    data.value = temp;
    visibleBodyModify.value = false
    MessagePlugin.success('删除成功');
  } else {
    MessagePlugin.error('未找到要删除的行');
  }
};


function onValidate(params) {
  console.log('Event Table Data Validate:', params);
}


const onReset = () => {
  newData.value.location = null;
  MessagePlugin.success('重置成功');
};
// #####  表单操作 END ###############


// #### AMap  START#####
const chooseLocationDialogVisible = ref(false);

let map = null;
let mapScale = null;
let mapToolBar = null;
let mapControlBar = null;
let mapType = null;
let mapMarker = null;
const searchPlaceInputValue = ref("");
let mapAutoComplete = null;
let mapPlaceSearch = null;
const showMap = () => {
  if (AMap.value !== null) {
    if (map === null) {
      map = new AMap.value.Map("mapContainer", {
        viewMode: "3D",
        zoom: 17,
        center: Data.location === null ? [113.997, 22.596] : Data.location,
      });
      mapScale = new AMap.value.Scale();
      mapToolBar = new AMap.value.ToolBar({
        position: {
          top: '110px',
          right: '40px'
        }
      });
      mapControlBar = new AMap.value.ControlBar({
        position: {
          top: '10px',
          right: '10px',
        }
      });
      map.addControl(mapScale);
      map.addControl(mapToolBar);
      map.addControl(mapControlBar);
      mapType = new AMap.value.MapType({
        defaultType: 0,
        position: {
          bottom: '110px',
          right: '10px',
        }
      });
      mapMarker = new AMap.value.Marker({
        icon: "https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png",
        position: map.getCenter(),
        offset: new AMap.value.Pixel(-11, -35),
        clickable: false,
        draggable: true
      });
      mapMarker.on("dragend", function () {
        setMapMarkerPosition();
      });
      mapAutoComplete = new AMap.value.AutoComplete({
        input: "searchPlaceInput",
        output: "searchPlaceResultDiv"
      });
      mapPlaceSearch = new AMap.value.PlaceSearch({
        map: map
      });
      mapPlaceSearch.on("markerClick", function (e) {
        setMapMarkerPosition(e.marker.getPosition());
      });
      mapAutoComplete.on("select", function (e) {
        mapPlaceSearch.setCity(e.poi.adcode);
        mapPlaceSearch.search(e.poi.name);
      });
      map.on('complete', function () {
        map.addControl(mapType);
        map.add(mapMarker);
        mapMarker.setLabel({
          content: `${mapMarker.getPosition().getLng()}, ${mapMarker.getPosition().getLat()}`,
          direction: "top",
          offset: new AMap.value.Pixel(-13, -8)
        });
        map.on('click', function (e) {
          setMapMarkerPosition(e.lnglat);
        });
        map.on('moveend', () => {
          mapMarker.setLabel({
            direction: "top",
            offset: new AMap.value.Pixel(-13, -8)
          });
        });
      });
    } else {
      map.setZoomAndCenter(17, Data.location === null ? [113.997, 22.596] : Data.location);
      mapMarker.setPosition(Data.location === null ? [113.997, 22.596] : Data.location);
      mapMarker.setLabel({
        content: `${mapMarker.getPosition().getLng()}, ${mapMarker.getPosition().getLat()}`
      });
    }
  } else {
    NotifyPlugin.info({title: "地图模块加载中，请稍后"})
  }

}

const setMapMarkerPosition = (position) => {
  if (position) {
    mapMarker.setPosition(position);
  }
  mapMarker.setLabel({
    content: `${mapMarker.getPosition().getLng()}, ${mapMarker.getPosition().getLat()}`,
  });
  setTimeout(() => {
    mapMarker.setLabel({
      direction: "top",
      offset: new AMap.value.Pixel(-13, -8)
    });
  }, 0);
}

onUnmounted(() => {
  map?.destroy();
});

const handleChooseLocationConfirm = () => {
  const currentMarkerPosition = mapMarker.getPosition();
  if (currentMarkerPosition !== null) {
    Data.location = currentMarkerPosition.toArray();
  }
  chooseLocationDialogVisible.value = false;
  mapPlaceSearch.clear();
  searchPlaceInputValue.value = "";
}

const handleChooseLocationCancel = () => {
  mapPlaceSearch.clear();
  searchPlaceInputValue.value = "";
}
// #### AMap  END #####
</script>
<style lang="css" scoped>
.operations {
  display: flex;
  //justify-content: space-around;
  gap: 10px;
}

.choose-location-map-div {
  display: flex;
  height: 50vh;
}

.search-place-div {
  position: relative;
  top: 10px;
  left: 10px;
  z-index: 99999;
}

.search-place-input {
  width: 150px;
  height: 32px;
  border-width: 1px;
  border-style: solid;
  border-radius: 3px;
  border-color: rgba(255, 255, 255, 0);
  box-shadow: 0 0 4px 1px rgba(0, 0, 0, .2);
  padding: 0 8px;
  background-color: #fff;
  outline: none;
  color: rgba(0, 0, 0, 0.9);
  font: 14px / 22px PingFang SC, Microsoft YaHei, Arial Regula;
  box-sizing: border-box;
  transition: border cubic-bezier(0.38, 0, 0.24, 1) 0.2s, box-shadow cubic-bezier(0.38, 0, 0.24, 1) 0.2s;
}

.search-place-input:hover {
  border-color: #0052d9;
}

.search-place-input:focus {
  z-index: 1;
  border-color: #0052d9;
  box-shadow: 0 0 0 2px #d9e1ff;
}

.search-place-result-div {
  position: relative;
  top: 3px;
  background-color: #fff;
  border-radius: 3px;
  box-shadow: 0 0 4px 1px rgba(0, 0, 0, .2);
}

.confirm-reset-btns {
  display: flex;
  justify-content: right;
  align-items: center;
}
</style>
