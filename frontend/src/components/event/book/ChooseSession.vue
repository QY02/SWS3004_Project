<template>
  <div class="choose-session-main-div">
    <t-space direction="vertical" align="center">
      <h1 class="choose-session-title">Choose session</h1>
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
              <p class="choose-session-detail-text">
                {{
                  `Register time: ${dateToString(session.registrationStartTime)} - ${dateToString(session.registrationEndTime)}`
                }}</p>
            </div>
          </t-collapse-panel>
        </t-collapse>
      </t-loading>
      <t-button @click="back">Back</t-button>
    </t-space>
  </div>
</template>

<script setup lang="ts">
import {
  bookingInformation,
  fetchSessionInformationStatus,
  sessionInformation,
  toNextStep
} from '@/components/event/book/Steps.vue';

const dateToString = (date) => {
  if ((date !== undefined) && (date !== null)) {
    const dayNameArray = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    const dayName = dayNameArray[date.getDay()];
    const localeDateStringArray = date.toLocaleString().split(' ');
    return `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  } else {
    return date;
  }
}

const back = () => {
  window.history.back();
}

const choose = (index: number) => {
  bookingInformation.chosenSession = index;
  toNextStep();
}

const getChooseButtonStatus = (index: number) => {
  const timeNow = new Date();
  if (sessionInformation[index].registered) {
    return [true, 'Registered', 'success'];
  } else if ((timeNow < sessionInformation[index].registrationStartTime) || (timeNow > sessionInformation[index].registrationEndTime)) {
    return [true, 'Not within the registration period', 'primary'];
  } else if (bookingInformation.chosenSession === index) {
    return [false, 'Selected', 'success'];
  } else {
    return [false, 'Select', 'primary'];
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