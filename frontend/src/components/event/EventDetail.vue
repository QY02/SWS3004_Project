<template>
  <div>
    <div class="card">
      <div class="card-1">
        <t-image :src="eventDetail.posterUrl" fit="fill" :style="{ width: '100%', height: '100%' }" shape="round"/>
      </div>
      <div class="right">
        <div class="card-2">
          <h2 style="overflow-wrap: break-word">{{ eventDetail.name }}</h2>
          <h3 style="overflow-wrap: break-word">{{ eventDetail.content }}</h3>
        </div>
      </div>
    </div>

    <el-affix :offset="55">
      <t-tabs :value="value_lable" size="large" :affix-props="{ offsetTop: 150 }">
        <t-tab-panel value="events" label="Event sessions"></t-tab-panel>
      </t-tabs>
    </el-affix>

    <!-- button part -->
    <t-space style="display: f2lex; width: 100%; margin-left: 24px;">
      <div>
        <div class="title">Event sessions</div>
        <div class="line"></div>
      </div>
    </t-space>

    <!-- detail part -->
    <el-card
      style="height: auto; padding: 5px;  max-width: 100% ; margin-right: 30px; margin-left: 30px; margin-bottom: 40px;">
      <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
        <t-space direction="vertical" style="margin-top: 20px; " v-show="sessionInformation.length > 0">
          <t-collapse style="width: 50vw">
            <t-collapse-panel v-for="(session) in sessionInformation"
              :header="`${dateToString(session.startTime)} - ${dateToString(session.endTime)} ${session.venue}`">
              <div class="choose-session-detail-div">
                <p class="choose-session-detail-text">
                  {{
                    `Register time: ${dateToString(session.registrationStartTime)} - ${dateToString(session.registrationEndTime)}`
                  }}</p>
              </div>
            </t-collapse-panel>
          </t-collapse>
        </t-space>
        <br>
        <t-button @click="router.push(`/book?id=${eventDetailedDataLocation}-${eventId}`)"
                  v-show="sessionInformation.length > 0">Go to register
        </t-button>
        <div v-show="sessionInformation.length === 0">
          <p>There is currently no session information available. Please stay tuned!</p>
        </div>
      </div>
    </el-card>
    <div style="height: 20px;"></div>
  </div>
</template>

<script setup>
import {currentStep, sessionInformation} from '@/components/event/book/Steps.vue';
import {onMounted, ref} from 'vue';
import axios from "axios";
import router from '@/routers';
import {useRoute} from "vue-router";

sessionStorage.setItem('currentStep', 0)
currentStep.value = 0;
const value_lable = ref('events');

const loading = ref(true);

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

const fullUserId = sessionStorage.getItem('fullUserId')
const token = sessionStorage.getItem('token')

const route = useRoute();
const eventIdList = route.query.id.split("-");
const eventDetailedDataLocation = ref(eventIdList[0]);
sessionStorage.setItem("eventDetailedDataLocation", eventDetailedDataLocation.value);
const eventId = ref(eventIdList[1]);
const eventDetail = ref({})

const getEventDetail = () => {
  loading.value = true;
  axios.post("/detailedEvent/getEventByEventId", {
    id: eventId.value
  }, {
    headers: {
      fullUserId: fullUserId,
      token: token,
      eventRoutingIndex: eventDetailedDataLocation.value
    }
  }).then((response) => {
    eventDetail.value = response.data.data
    loading.value = false;
  }).catch(() => { })
}

const fetchSessionInformation = async () => {
  try {
    let response = await axios.post("/detailedEvent/getEventSessionListByEventId", {id: eventId.value},
        {
          headers:
              {
                fullUserId: fullUserId,
                token: token,
                eventRoutingIndex: eventDetailedDataLocation.value
              }
        });
    const dataConverted = response.data.data.map((item) => ({
      ...item,
      startTime: new Date(item.startTime),
      endTime: new Date(item.endTime),
      registrationStartTime: new Date(item.registrationStartTime),
      registrationEndTime: new Date(item.registrationEndTime)
    }));
    sessionInformation.length = 0;
    sessionInformation.push(...dataConverted);
    console.log("sessionInformation" + sessionInformation.length)
  }
  catch (error) {
  }
}

onMounted(() => {
  getEventDetail();
  fetchSessionInformation();
});
</script>

<style lang="css" scoped>
.card {
  --gray: rgba(229, 231, 235, 1);
  display: flex;
  grid-gap: 1.25rem;
  gap: 1.25rem;
  background-color: rgba(255, 255, 255, 1);
  padding: 1.5rem;
}

.card-1 {
  max-height: 60vh;
  width: 40%;
  margin-right: 15px;
  border-radius: 0.75rem;
  background-color: var(--gray);
}

.right {
  display: flex;
  flex: 1 1 0%;
  flex-direction: column;
  grid-gap: 1.25rem;
  gap: 1.25rem;
}

.card-2 {
  height: 3.5rem;
  width: 50vw;
  border-radius: 1rem;
}

@keyframes pulse {
  to {
    opacity: .2;
  }
}

button#scrollButton {
  position: absolute;
  bottom: 20px;
  right: 20px;
  transform: translateY(0);
  transition: transform 0.3s ease;
}

button#scrollButton.fixed {
  position: fixed;
  bottom: auto;
  top: 20px;
}


.title {
  color: #5e6066;
  font-size: 1.6em;
  font-weight: 700;
  margin: 30px;
  letter-spacing: 3.5px;
}

.line {
  width: 70px;
  height: 4px;
  margin-top: -15px;
  margin-bottom: 20px;
  margin-left: 30px;
  background-color: black;
}
</style>
<style scoped>
.el-carousel__item h3 {
  color: #475669;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
  text-align: center;
}
</style>