<template>
  <t-space direction="vertical" size="medium" align="center">
    <h1 class="choose-seat-title">选择座位</h1>
    <div class="choose-seat-legend-div">
      <span>{{ `已选择：${bookingInformation.chosenSeat === null ? '无' : bookingInformation.chosenSeat}` }}</span>
      <t-space>
        <div style="display: flex; align-items: center">
          <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 512 512" width="1em" height="1em">
            <path fill="#0059de"
                  d="M176 80H336c44.2 0 80 35.8 80 80v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-70.7-57.3-128-128-128H176C105.3 32 48 89.3 48 160v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-44.2 35.8-80 80-80zM462.5 227.6c-7.1-2.3-14.6-3.6-22.5-3.6c-9.5 0-18.5 1.8-26.8 5.2c-24.1 9.7-41.8 32-44.7 58.8H143.6c-3-26.8-20.6-49.1-44.7-58.8C90.5 225.8 81.5 224 72 224c-7.9 0-15.4 1.3-22.5 3.6C20.7 237 0 264.1 0 296V432c0 26.5 21.5 48 48 48H96c20.9 0 38.7-13.4 45.3-32H370.7c6.6 18.6 24.4 32 45.3 32h48c26.5 0 48-21.5 48-48V296c0-31.9-20.7-59-49.5-68.4zM368 400H144V336h32H336h32v64zM96 400v32H48l0-136c0-13.3 10.7-24 24-24s24 10.7 24 24v40 64zM464 296V432H416V296c0-13.3 10.7-24 24-24s24 10.7 24 24z"/>
          </svg>
          ：可选
        </div>
        <div style="display: flex; align-items: center">
          <svg xmlns="http://www.w3.org/2000/svg"
               viewBox="0 0 512 512" width="1em" height="1em">
            <path fill="#0059de" style="opacity: 0.6;"
                  d="M192 32C121.3 32 64 89.3 64 160v66.7c18.6 6.6 32 24.4 32 45.3v80H416V272c0-20.9 13.4-38.7 32-45.3V160c0-70.7-57.3-128-128-128H192z"/>
            <path fill="#0059de"
                  d="M48 224c-26.5 0-48 21.5-48 48V448c0 17.7 14.3 32 32 32H64c17.7 0 32-14.3 32-32H416c0 17.7 14.3 32 32 32h32c17.7 0 32-14.3 32-32V272c0-26.5-21.5-48-48-48s-48 21.5-48 48v80H96V272c0-26.5-21.5-48-48-48z"/>
          </svg>
          ：已被占用
        </div>
        <div style="display: flex; align-items: center">
          <svg
              xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="1em" height="1em">
            <path fill="#0059de"
                  d="M64 32V192H96h32v32 96H384V224 192h32 32V32H64zm0 192H0V480H96V448H416v32h96V224H448 416v32 64 32H384 128 96V320 256 224H64z"/>
          </svg>
          ：已选
        </div>
      </t-space>
    </div>
    <t-loading :loading="fetchSeatMapStatus !== 1" :show-overlay="true">
      <div ref="seatChooserDiv" class="choose-seat-seat-div">
        <div ref="seatChooserInnerDiv">
          <div ref="seatChooserInnermostDiv" class="choose-seat-seat-innermost-div">
            <vue-draggable-resizable class="choose-seat-seat-chooser-draggable-resizable"
                                     :w="seatChooserWidth"
                                     :h="seatChooserHeight"
                                     :x="seatChooserX"
                                     :y="seatChooserY"
                                     :lock-aspect-ratio="true"
                                     :handles="[]"
                                     :onDragStart="handleDragAndResizeStart"
                                     :onResizeStart="handleDragAndResizeStart"
                                     :onDrag="handleDrag"
                                     :onResize="handleResize"
                                     @dragStop="handleDragStop"
                                     @resizeStop="handleResizeStop"
                                     @wheel="onWheel">
              <div class="choose-seat-inside-div">
                <div v-for="seat in seatMap.seats"
                     :style="`position: absolute; left: ${(seat.x / seatMap.size.width) * 100}%; top: ${(seat.y / seatMap.size.height) * 100}%; width: ${(10 / seatMap.size.width) * 100}%; height: ${(10 / seatMap.size.height) * 100}%; display: flex; justify-content: center; align-items: center;`">
                  <t-popconfirm theme="default" cancel-btn="取消">
                    <template #confirmBtn>
                      <t-button size="small" theme="primary" style="margin-left: 6px" @click="handleChoose(seat)">
                        {{ getConfirmBtnStatus(seat) }}
                      </t-button>
                    </template>
                    <template #content>
                      <p style="margin-top: 1px; margin-left: 10px; margin-bottom: 0; font-weight: bold; font-size: 18px">
                        {{ seat.id }}</p>
                      <p style="margin-left: 10px">{{ `类型: ${seat.type}` }}</p>
                      <p style="margin-bottom: 0; margin-left: 10px">{{ `价格: ${seat.price}` }}</p>
                    </template>
                    <template #icon>
                      <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 512 512" width="1.5em" height="1.5em">
                        <path fill="#0052d9"
                              d="M176 80H336c44.2 0 80 35.8 80 80v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-70.7-57.3-128-128-128H176C105.3 32 48 89.3 48 160v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-44.2 35.8-80 80-80zM462.5 227.6c-7.1-2.3-14.6-3.6-22.5-3.6c-9.5 0-18.5 1.8-26.8 5.2c-24.1 9.7-41.8 32-44.7 58.8H143.6c-3-26.8-20.6-49.1-44.7-58.8C90.5 225.8 81.5 224 72 224c-7.9 0-15.4 1.3-22.5 3.6C20.7 237 0 264.1 0 296V432c0 26.5 21.5 48 48 48H96c20.9 0 38.7-13.4 45.3-32H370.7c6.6 18.6 24.4 32 45.3 32h48c26.5 0 48-21.5 48-48V296c0-31.9-20.7-59-49.5-68.4zM368 400H144V336h32H336h32v64zM96 400v32H48l0-136c0-13.3 10.7-24 24-24s24 10.7 24 24v40 64zM464 296V432H416V296c0-13.3 10.7-24 24-24s24 10.7 24 24z"/>
                      </svg>
                    </template>
                    <svg v-if="!seat.availability" style="width: 70%; height: 70%" xmlns="http://www.w3.org/2000/svg"
                         viewBox="0 0 512 512">
                      <path :fill="seat.color" style="opacity: 0.6;"
                            d="M192 32C121.3 32 64 89.3 64 160v66.7c18.6 6.6 32 24.4 32 45.3v80H416V272c0-20.9 13.4-38.7 32-45.3V160c0-70.7-57.3-128-128-128H192z"/>
                      <path :fill="seat.color"
                            d="M48 224c-26.5 0-48 21.5-48 48V448c0 17.7 14.3 32 32 32H64c17.7 0 32-14.3 32-32H416c0 17.7 14.3 32 32 32h32c17.7 0 32-14.3 32-32V272c0-26.5-21.5-48-48-48s-48 21.5-48 48v80H96V272c0-26.5-21.5-48-48-48z"/>
                    </svg>
                    <svg v-else-if="seat.id === bookingInformation.chosenSeat" style="width: 70%; height: 70%"
                         xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512">
                      <path :fill="seat.color"
                            d="M64 32V192H96h32v32 96H384V224 192h32 32V32H64zm0 192H0V480H96V448H416v32h96V224H448 416v32 64 32H384 128 96V320 256 224H64z"/>
                    </svg>
                    <svg v-else style="width: 70%; height: 70%"
                         xmlns="http://www.w3.org/2000/svg"
                         viewBox="0 0 512 512">
                      <path :fill="seat.color"
                            d="M176 80H336c44.2 0 80 35.8 80 80v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-70.7-57.3-128-128-128H176C105.3 32 48 89.3 48 160v34.8c7.7-1.8 15.7-2.8 24-2.8s16.3 1 24 2.8V160c0-44.2 35.8-80 80-80zM462.5 227.6c-7.1-2.3-14.6-3.6-22.5-3.6c-9.5 0-18.5 1.8-26.8 5.2c-24.1 9.7-41.8 32-44.7 58.8H143.6c-3-26.8-20.6-49.1-44.7-58.8C90.5 225.8 81.5 224 72 224c-7.9 0-15.4 1.3-22.5 3.6C20.7 237 0 264.1 0 296V432c0 26.5 21.5 48 48 48H96c20.9 0 38.7-13.4 45.3-32H370.7c6.6 18.6 24.4 32 45.3 32h48c26.5 0 48-21.5 48-48V296c0-31.9-20.7-59-49.5-68.4zM368 400H144V336h32H336h32v64zM96 400v32H48l0-136c0-13.3 10.7-24 24-24s24 10.7 24 24v40 64zM464 296V432H416V296c0-13.3 10.7-24 24-24s24 10.7 24 24z"/>
                    </svg>
                  </t-popconfirm>
                </div>
              </div>
            </vue-draggable-resizable>
          </div>
        </div>
      </div>
    </t-loading>
    <t-space size="medium">
      <t-button theme="default" @click="currentStep--">上一步</t-button>
      <t-button @click="handleSubmit">下一步</t-button>
    </t-space>
  </t-space>
</template>

<script setup lang="ts">
import {currentStep, toNextStep} from '@/components/book/Steps.vue';
import {onMounted, reactive, Ref, ref, watch} from "vue";
import {sessionInformation, bookingInformation} from '@/components/book/Steps.vue';
import {MessagePlugin, NotifyPlugin} from "tdesign-vue-next";
import {checkForm} from '@/components/book/InputInformation.vue';
import axios, {AxiosRequestConfig} from "axios";

const seatMap = reactive({
  size: {
    width: 80,
    height: 60
  },
  seats: [{id: 'A1', type: '普通', price: 100, availability: true, x: 0, y: 0, color: '#0059de'},
    {id: 'A2', type: '普通', price: 100, availability: true, x: 10, y: 0, color: '#0059de'},
    {id: 'A3', type: '普通', price: 100, availability: true, x: 20, y: 0, color: '#0059de'},
    {id: 'A4', type: '普通', price: 100, availability: true, x: 30, y: 0, color: '#0059de'},
    {id: 'A5', type: '普通', price: 100, availability: true, x: 40, y: 0, color: '#0059de'},
    {id: 'A6', type: '普通', price: 100, availability: true, x: 50, y: 0, color: '#0059de'},
    {id: 'A7', type: '普通', price: 100, availability: true, x: 60, y: 0, color: '#0059de'},
    {id: 'A8', type: '普通', price: 100, availability: false, x: 70, y: 0, color: '#0059de'},
    {id: 'B1', type: '普通', price: 100, availability: true, x: 0, y: 10, color: '#0059de'},
    {id: 'B2', type: '普通', price: 100, availability: false, x: 10, y: 10, color: '#0059de'},
    {id: 'B3', type: 'VIP', price: 150, availability: true, x: 20, y: 10, color: '#ff8800'},
    {id: 'B4', type: 'VIP', price: 150, availability: false, x: 30, y: 10, color: '#ff8800'},
    {id: 'B5', type: 'VIP', price: 150, availability: true, x: 40, y: 10, color: '#ff8800'},
    {id: 'B6', type: 'VIP', price: 150, availability: true, x: 50, y: 10, color: '#ff8800'},
    {id: 'B7', type: '普通', price: 100, availability: true, x: 60, y: 10, color: '#0059de'},
    {id: 'B8', type: '普通', price: 100, availability: true, x: 70, y: 10, color: '#0059de'},
    {id: 'C1', type: '普通', price: 100, availability: true, x: 0, y: 20, color: '#0059de'},
    {id: 'C2', type: '普通', price: 100, availability: true, x: 10, y: 20, color: '#0059de'},
    {id: 'C3', type: '普通', price: 100, availability: true, x: 20, y: 20, color: '#0059de'},
    {id: 'C4', type: '普通', price: 100, availability: true, x: 30, y: 20, color: '#0059de'},
    {id: 'C5', type: '普通', price: 100, availability: true, x: 40, y: 20, color: '#0059de'},
    {id: 'C6', type: '普通', price: 100, availability: true, x: 50, y: 20, color: '#0059de'},
    {id: 'C7', type: '普通', price: 100, availability: true, x: 60, y: 20, color: '#0059de'},
    {id: 'C8', type: '普通', price: 100, availability: true, x: 70, y: 20, color: '#0059de'},
    {id: 'D1', type: '普通', price: 100, availability: true, x: 0, y: 30, color: '#0059de'},
    {id: 'D2', type: '普通', price: 100, availability: true, x: 10, y: 30, color: '#0059de'},
    {id: 'D3', type: '普通', price: 100, availability: true, x: 20, y: 30, color: '#0059de'},
    {id: 'D4', type: '普通', price: 100, availability: true, x: 30, y: 30, color: '#0059de'},
    {id: 'D5', type: '普通', price: 100, availability: true, x: 40, y: 30, color: '#0059de'},
    {id: 'D6', type: '普通', price: 100, availability: true, x: 50, y: 30, color: '#0059de'},
    {id: 'D7', type: '普通', price: 100, availability: true, x: 60, y: 30, color: '#0059de'},
    {id: 'D8', type: '普通', price: 100, availability: true, x: 70, y: 30, color: '#0059de'},
    {id: 'E1', type: '普通', price: 100, availability: true, x: 0, y: 40, color: '#0059de'},
    {id: 'E2', type: '普通', price: 100, availability: true, x: 10, y: 40, color: '#0059de'},
    {id: 'E3', type: '普通', price: 100, availability: true, x: 20, y: 40, color: '#0059de'},
    {id: 'E4', type: '普通', price: 100, availability: true, x: 30, y: 40, color: '#0059de'},
    {id: 'E5', type: '普通', price: 100, availability: true, x: 40, y: 40, color: '#0059de'},
    {id: 'E6', type: '普通', price: 100, availability: true, x: 50, y: 40, color: '#0059de'},
    {id: 'E7', type: '普通', price: 100, availability: true, x: 60, y: 40, color: '#0059de'},
    {id: 'E8', type: '普通', price: 100, availability: true, x: 70, y: 40, color: '#0059de'},
    {id: 'F1', type: '普通', price: 100, availability: true, x: 0, y: 50, color: '#0059de'},
    {id: 'F2', type: '普通', price: 100, availability: true, x: 10, y: 50, color: '#0059de'},
    {id: 'F3', type: '普通', price: 100, availability: true, x: 20, y: 50, color: '#0059de'},
    {id: 'F4', type: '普通', price: 100, availability: true, x: 30, y: 50, color: '#0059de'},
    {id: 'F5', type: '普通', price: 100, availability: true, x: 40, y: 50, color: '#0059de'},
    {id: 'F6', type: '普通', price: 100, availability: true, x: 50, y: 50, color: '#0059de'},
    {id: 'F7', type: '普通', price: 100, availability: true, x: 60, y: 50, color: '#0059de'},
    {id: 'F8', type: '普通', price: 100, availability: true, x: 70, y: 50, color: '#0059de'}
  ]
});

let seatChooserWidth;
let seatChooserHeight;
if ((window.innerWidth * 0.45 * (seatMap.size.height / seatMap.size.width)) <= (window.innerHeight * 0.5 - window.innerWidth * 0.05)) {
  seatChooserWidth = ref(window.innerWidth * 0.45);
  seatChooserHeight = ref(seatChooserWidth.value * (seatMap.size.height / seatMap.size.width));
} else {
  seatChooserHeight = ref(window.innerHeight * 0.5 - window.innerWidth * 0.05);
  seatChooserWidth = ref(seatChooserHeight.value * (seatMap.size.width / seatMap.size.height));
}

const seatChooserX = ref((window.innerWidth * 0.5 - seatChooserWidth.value) / 2);
const seatChooserY = ref((window.innerHeight * 0.5 - seatChooserHeight.value) / 2);

const seatChooserDiv: Ref<HTMLElement> = ref(null);
const seatChooserInnerDiv: Ref<HTMLElement> = ref(null);
const seatChooserInnermostDiv: Ref<HTMLElement> = ref(null);

let seatChooserDraggableResizable: HTMLElement = null;
onMounted(() => {
  seatChooserDraggableResizable = document.querySelector('.choose-seat-seat-chooser-draggable-resizable');
  transform = seatChooserDraggableResizable.style.transform.match(/-?\d+(\.\d+)?/g).map(Number);
  originalX = transform[0];
  originalY = transform[1];
});
let transform: number[] = null;
let originalX: number = null;
let originalY: number = null;
let originalScrollLeft: number = null;
let originalScrollTop: number = null;
let lastX: number = null;
let lastY: number = null;
let lastDirectionX: boolean = null;
let lastDirectionY: boolean = null;
const handleDrag = (x, y) => {
  const seatChooserDivCurrentWidth = seatChooserDiv.value.offsetWidth;
  const seatChooserDivCurrentHeight = seatChooserDiv.value.offsetHeight;
  seatChooserInnerDiv.value.style.width = `${x < 0 ? (seatChooserDivCurrentWidth - x) : 0}px`;
  seatChooserInnerDiv.value.style.height = `${y < 0 ? (seatChooserDivCurrentHeight - y) : 0}px`;
  seatChooserInnermostDiv.value.style.left = `${Math.max(-x, 0)}px`;
  seatChooserInnermostDiv.value.style.top = `${Math.max(-y, 0)}px`;

  if (x < 0) {
    seatChooserDiv.value.scrollLeft = originalX < 0 ? (originalScrollLeft + originalX - x) : (originalScrollLeft - x);
  } else if (lastX < 0) {
    seatChooserDiv.value.scrollLeft = originalX < 0 ? (originalScrollLeft + originalX) : originalScrollLeft;
  }
  if (y < 0) {
    seatChooserDiv.value.scrollTop = originalY < 0 ? (originalScrollTop + originalY - y) : (originalScrollTop - y);
  } else if (lastY < 0) {
    seatChooserDiv.value.scrollTop = originalY < 0 ? (originalScrollTop + originalY) : originalScrollTop;
  }

  if ((x !== lastX) && lastX !== null) {
    if ((lastDirectionX !== null) && (lastDirectionX !== (x > lastX))) {
      originalX = lastX;
      originalScrollLeft = seatChooserDiv.value.scrollLeft;
    }
    lastDirectionX = (x > lastX);
  }
  if ((y !== lastY) && lastY !== null) {
    if ((lastDirectionY !== null) && (lastDirectionY !== (y > lastY))) {
      originalY = lastY;
      originalScrollTop = seatChooserDiv.value.scrollTop;
    }
    lastDirectionY = (y > lastY);
  }
  lastX = x;
  lastY = y;
  updateScrollbar();
}

const handleResize = (ignore, x, y) => {
  handleDrag(x, y);
}

const handleDragAndResizeStart = () => {
  transform = seatChooserDraggableResizable.style.transform.match(/-?\d+(\.\d+)?/g).map(Number);
  originalX = transform[0];
  originalY = transform[1];
  lastX = transform[0];
  lastY = transform[1];
  originalScrollLeft = seatChooserDiv.value.scrollLeft;
  originalScrollTop = seatChooserDiv.value.scrollTop;
}

const handleDragStop = (x, y) => {
  seatChooserX.value = x;
  seatChooserY.value = y;
  originalScrollLeft = null;
  originalScrollTop = null;
  lastX = null;
  lastY = null;
  lastDirectionX = null;
  lastDirectionY = null;
}

const handleResizeStop = (x, y, w, h) => {
  seatChooserWidth.value = w;
  seatChooserHeight.value = h;
  handleDragStop(x, y);
}

const updateScrollbar = () => {
  const temp = document.createElement('span');
  seatChooserDiv.value.appendChild(temp);
  setTimeout(() => {
    seatChooserDiv.value.removeChild(temp);
  }, 0);
}

const getConfirmBtnStatus = (seat) => {
  if (!seat.availability) {
    return '不可选';
  } else {
    return bookingInformation.chosenSeat !== seat.id ? '选择' : '已选择';
  }
}

const handleChoose = (seat) => {
  if (seat.availability) {
    if (seat.id !== bookingInformation.chosenSeat) {
      bookingInformation.chosenSeat = seat.id;
      bookingInformation.seatPrice = seat.price;
    } else {
      bookingInformation.chosenSeat = null;
    }
  }
}

const onWheel = (event) => {
  event.preventDefault();
  const change = event.deltaY < 0 ? 20 : -20;
  if (seatChooserWidth.value + change >= 100) {
    handleDragAndResizeStart();
    let heightChange = change * (seatMap.size.height / seatMap.size.width);
    seatChooserWidth.value += change;
    seatChooserHeight.value += heightChange;
    seatChooserX.value -= change / 2;
    seatChooserY.value -= heightChange / 2;
    handleDrag(seatChooserX.value, seatChooserY.value);
  }
};

const handleSubmit = async () => {
  let result = await checkForm();
  if (bookingInformation.chosenSeat === null) {
    await MessagePlugin.warning('请选择一个座位');
    result = false;
  }
  if (result) {
    toNextStep();
    // await submitData();
  }
}

let fetchSeatMapStatus = ref(0);

const fetchSessionInformation = async () => {
  fetchSeatMapStatus.value = 0;
  axios.post("/seatMap/getSeatMapWithSeatsById", {seatMapId: sessionInformation[bookingInformation.chosenSession].seatMapId}, {headers: {token: sessionStorage.getItem('token')}} as AxiosRequestConfig).then(response => {
    Object.keys(seatMap).forEach(key => delete seatMap[key]);
    Object.assign(seatMap, response.data.data.detailedData);
    fetchSeatMapStatus.value = 1;
  }).catch(error => {
    fetchSeatMapStatus.value = -1;
    if (error.response) {
      NotifyPlugin.error({title: error.response.data.msg});
    } else {
      NotifyPlugin.error({title: error.message});
    }
  })
}

watch(() => bookingInformation.chosenSession, (newSession, oldSession) => {
  bookingInformation.chosenSeat = null;
  if ((oldSession === null) || (newSession === null) || (sessionInformation[oldSession].seatMapId !== sessionInformation[newSession].seatMapId)) {
    if(sessionInformation[newSession].seatMapId!=-1){
      fetchSessionInformation();
    }
  }
})
</script>

<style scoped lang="less">
.choose-seat {
  &-title {
    text-align: center;
    font-size: 25px;
    line-height: 0;
    margin-top: 25px;
    margin-bottom: 10px;
  }

  &-legend-div {
    font-size: 18px;
    font-weight: bold;
    height: 50px;
    width: 50vw;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 2px solid #0052d9;
    border-radius: 10px;
    box-sizing: border-box;
    padding-left: 1vw;
    padding-right: 1vw;
  }

  &-seat {
    &-div {
      position: relative;
      height: 50vh;
      width: 50vw;
      border: 2px solid #0052d9;
      border-radius: 10px;
      overflow: auto;
    }

    &-innermost-div {
      position: absolute;
      top: 0;
      left: 0;
    }

    &-chooser-draggable-resizable {
      border: 2px solid #0052d9;
      border-radius: 10px;
    }
  }

  &-inside-div {
    width: 100%;
    height: 100%;
  }
}
</style>