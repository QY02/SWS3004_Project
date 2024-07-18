<template >
  <t-loading size="small" :loading="loading" show-overlay>
  <div v-if="curEvents.length===0">
    <div style="display: flex; align-items: center;text-align: center;margin-left: 45%; margin-top: 10%">
      <error-circle-icon size="large"></error-circle-icon>
      <h1 style="color: #5e6066; font-size: large; margin-left: 10px;">No events</h1>
    </div>
  </div>
  <div v-else>
  <div id="event" v-loading="loading">
    <t-card
        v-for="(item,index) in curEvents"
        :key="index"
        :title="item['name']" :style="{ width: '445px' }"
        hover-shadow
        @click="clickEvent(item['id'], item['detailedDataLocation'])"
        lazy-load
    >
      <t-image
          :src="item['posterUrl']"
          :style="{ width: '395px', height: '210px' }"
          overlay-trigger="hover">
      </t-image>
    </t-card>
  </div>

  </div>
  </t-loading>
</template>

<script setup>

import {ErrorCircleIcon} from 'tdesign-icons-vue-next';
import axios from "axios";
import {ref} from "vue";
import router from "@/routers/index.js";

const token = sessionStorage.getItem('token')
const fullUserId = sessionStorage.getItem('fullUserId')

const loading = ref(true);

const events = ref([]);
const curEvents = ref([]);

axios.post(`/briefEvent/getList`, {}, {
  headers: {
    fullUserId: fullUserId,
    token: token
  }
}).then((response) => {
  events.value = response.data.data
      loading.value=false
  getSearchNew("");
    }).catch();

const clickEvent = (eventId, detailedDataLocation) => {
  sessionStorage.setItem('eventId', eventId)
  router.push(`/event?id=${detailedDataLocation}-${eventId}`);
};

function getSearchNew(message) {
  curEvents.value = events.value.filter(events => events['name'].includes(message));
}

defineExpose({getSearchNew});

</script>
<style scoped>
#event {
  display: flex;
  flex-wrap: wrap;
  flex-direction: row;
  gap: 20px;
  margin: 20px;

}
</style>