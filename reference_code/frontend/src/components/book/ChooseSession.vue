<template>
  <div class="choose-session-main-div">
    <t-space direction="vertical" align="center">
      <h1 class="choose-session-title">选择场次</h1>
      <t-loading :loading="fetchSessionInformationStatus !== 1" :show-overlay="true">
        <t-collapse style="width: 50vw">
          <t-collapse-panel v-for="(session, index) in sessionInformation"
                            :header="`${dateToString(session.startTime)} - ${dateToString(session.endTime)} ${session.venue}`">
            <template #headerRightContent>
              <t-button :disabled="getChooseButtonStatus(index)[0]"
                        :theme="getChooseButtonStatus(index)[2]"
                        @click="choose(index)">{{ getChooseButtonStatus(index)[1] }}
              </t-button>
            </template>
            <div class="choose-session-detail-div">
              <div class="choose-session-detail-text-div">
                <p v-if="session.registrationRequired" class="choose-session-detail-text">
                  {{
                    `报名时间: ${dateToString(session.registrationStartTime)} - ${dateToString(session.registrationEndTime)}`
                  }}</p>
                <p v-else class="choose-session-detail-text">无需报名</p>
                <p class="choose-session-detail-text">{{ `人数限制: ${session.minSize} - ${session.maxSize}` }}</p>
                <p class="choose-session-detail-text">{{ `当前人数: ${session.currentSize}` }}</p>
              </div>
              <t-button theme="default" variant="outline" @click="showMapDialog(index)">
                <template #icon>
                  <MapInformation2Icon/>
                </template>
                查看地图
              </t-button>
            </div>
          </t-collapse-panel>
        </t-collapse>
      </t-loading>
      <t-button @click="router.push('/event')">返回</t-button>
    </t-space>
  </div>
  <t-dialog v-model:visible="mapDialogVisible" placement="center" width="50vw" :header="mapDialogHeader"
            :confirm-btn="null"
            :cancel-btn="{content: '关闭', theme: 'primary'}" @opened="showMap">
    <div id="mapContainer" class="choose-session-map-div"></div>
  </t-dialog>
</template>

<script setup lang="ts">
import {
  sessionInformation,
  bookingInformation,
  toNextStep,
  fetchSessionInformationStatus,
  currentStep
} from '@/components/book/Steps.vue';
import {AMap} from "@/main";
import {onUnmounted, ref} from "vue";
import {MapInformation2Icon} from "tdesign-icons-vue-next";
import {NotifyPlugin} from "tdesign-vue-next";
import router from '@/routers';

let map = null;
let mapScale = null;
let mapControlBar = null;
let mapToolBar = null;
let mapType = null;
let mapMarker = null;
const mapDialogVisible = ref(false);
const mapDialogHeader = ref("");

const dateToString = (date: Date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result: string = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}


const choose = (index: number) => {
  bookingInformation.chosenSession = index;
  if(sessionInformation[index].seatMapId!=-1){
    toNextStep();
  }
  else{
    bookingInformation.seatPrice = sessionInformation[index].price;
    // bookingInformation.chosenSeat = "活动门票"
    toNextStep();
  }
}

const getChooseButtonStatus = (index: number) => {
  const timeNow = new Date();
  if (!sessionInformation[index].registrationRequired) {
    return [true, '无需报名', 'primary'];
  } else if (sessionInformation[index].registered) {
    return [true, '已报名', 'success'];
  } else if ((timeNow < sessionInformation[index].registrationStartTime) || (timeNow > sessionInformation[index].registrationEndTime)) {
    return [true, '不在报名时间段内', 'default'];
  } else if (sessionInformation[index].currentSize >= sessionInformation[index].maxSize) {
    return [true, '容量已满', 'warning'];
  } else if (bookingInformation.chosenSession === index) {
    return [false, '已选择', 'success'];
  } else {
    return [false, '选择', 'primary'];
  }
}

onUnmounted(() => {
  map?.destroy();
});

let currentSessionIndexShownInMap: number = null;

const showMapDialog = (index: number) => {
  currentSessionIndexShownInMap = index;
  mapDialogVisible.value = true;
}

const showMap = () => {
  if (AMap.value !== null) {
    mapDialogHeader.value = sessionInformation[currentSessionIndexShownInMap].venue;
    if (map === null) {
      map = new AMap.value.Map("mapContainer", {
        viewMode: "3D",
        zoom: 17,
        center: sessionInformation[currentSessionIndexShownInMap].location,
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
          top: '10px',
          left: '100px',
        }
      });
      mapMarker = new AMap.value.Marker({
        icon: "https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png",
        position: sessionInformation[currentSessionIndexShownInMap].location,
        offset: new AMap.value.Pixel(-11, -35),
        clickable: false
      });
      map.on('complete', function () {
        map.addControl(mapType);
        map.add(mapMarker);
        mapMarker.setLabel({
          content: sessionInformation[currentSessionIndexShownInMap].venue,
          direction: "top",
          offset: new AMap.value.Pixel(-13, -8)
        });
      });
    } else {
      map.setCenter(sessionInformation[currentSessionIndexShownInMap].location);
      mapMarker.setPosition(sessionInformation[currentSessionIndexShownInMap].location);
      mapMarker.setLabel({
        content: sessionInformation[currentSessionIndexShownInMap].venue
      });
    }
  }
  else {
    NotifyPlugin.info({ title: "地图模块加载中，请稍后" })
  }
}
</script>

<style scoped lang="less">
.choose-session {
  &-main-div {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    top: 5vh;
  }

  &-detail {
    &-div {
      display: flex;
      flex-wrap: nowrap;
      flex-direction: row;
      justify-content: space-between;
      align-items: center;
    }

    &-text-div {
      display: flex;
      flex-direction: column;
    }

    &-text {
      margin-top: 7px;
      margin-bottom: 7px;
    }
  }

  &-map-div {
    height: 50vh;
  }

  &-title {
    text-align: center;
    font-size: 25px;
    line-height: 0;
  }
}
</style>