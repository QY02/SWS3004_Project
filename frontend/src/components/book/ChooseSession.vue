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
              <p v-if="session.registrationRequired" class="choose-session-detail-text">
                {{
                  `报名时间: ${dateToString(session.registrationStartTime)} - ${dateToString(session.registrationEndTime)}`
                }}</p>
              <p v-else class="choose-session-detail-text">无需报名</p>
              <p class="choose-session-detail-text">{{ `人数限制: ${session.minSize} - ${session.maxSize}` }}</p>
              <p class="choose-session-detail-text">{{ `当前人数: ${session.currentSize}` }}</p>
            </div>
          </t-collapse-panel>
        </t-collapse>
      </t-loading>
      <t-button>返回</t-button>
    </t-space>
  </div>
</template>

<script setup lang="ts">
import {
  bookingInformation,
  fetchSessionInformationStatus,
  sessionInformation,
  toNextStep
} from '@/components/book/Steps.vue';

const dateToString = (date: Date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result: string = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}

const choose = (index: number) => {
  bookingInformation.chosenSession = index;
  toNextStep();
}

const getChooseButtonStatus = (index: number) => {
  const timeNow = new Date();
  if (!sessionInformation[index].registrationRequired) {
    return [true, '无需报名', 'primary'];
  } else if (sessionInformation[index].registered) {
    return [true, '已报名', 'success'];
  } else if ((timeNow < sessionInformation[index].registrationStartTime) || (timeNow > sessionInformation[index].registrationEndTime)) {
    return [true, '不在报名时间段内', 'primary'];
  } else if (sessionInformation[index].currentSize >= sessionInformation[index].maxSize) {
    return [true, '容量已满', 'primary'];
  } else if (bookingInformation.chosenSession === index) {
    return [false, '已选择', 'success'];
  } else {
    return [false, '选择', 'primary'];
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
      flex-direction: column;
    }

    &-text {
      margin-top: 7px;
      margin-bottom: 7px;
    }
  }

  &-title {
    text-align: center;
    font-size: 25px;
    line-height: 0;
  }
}
</style>