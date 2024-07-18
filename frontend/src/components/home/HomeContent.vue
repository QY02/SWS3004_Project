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
        @click="clickEvent(item['id'])"
        lazy-load
    >
      <t-image
          :src="item['cover']"
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

const visible = ref(false);
const loading = ref(true);
const top = '50px';


const cover = ref('https://tdesign.gtimg.com/site/source/card-demo.png');
const events = ref([]);
const attachToken = ref([]);
const tmpEvents = ref([]);
const curEvents = ref([]);
const favColor = ref({});

axios.post(`/briefEvent/getList`, {}, {
  headers: {
    fullUserId: fullUserId,
    token: token
  }
}).then((response) => {
  events.value = response.data.data
      loading.value=false
      for (let i = 0; i < events.value.length; i++) {//获取每个活动的海报
        let id = events.value[i]['id'];
      }
    }).catch();

const clickEvent = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  router.push('/event');
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
.tdesign-demo-image-viewer__ui-image {
  width: 100%;
  height: 100%;
  display: inline-flex;
  position: relative;
  justify-content: center;
  align-items: center;
  border-radius: var(--td-radius-small);
  overflow: hidden;
}

.tdesign-demo-image-viewer__ui-image--hover {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: var(--td-text-color-anti);
  line-height: 22px;
  transition: 0.2s;
}

.tdesign-demo-image-viewer__ui-image:hover .tdesign-demo-image-viewer__ui-image--hover {
  opacity: 1;
  cursor: pointer;
}

.tdesign-demo-image-viewer__ui-image--img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 100%;
  cursor: pointer;
  position: absolute;
}

.tdesign-demo-image-viewer__ui-image--footer {
  padding: 0 16px;
  height: 56px;
  width: 100%;
  line-height: 56px;
  font-size: 16px;
  position: absolute;
  bottom: 0;
  color: var(--td-text-color-anti);
  background-image: linear-gradient(0deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0) 100%);
  display: flex;
  box-sizing: border-box;
}

.tdesign-demo-image-viewer__ui-image--title {
  flex: 1;
}

.tdesign-demo-popup__reference {
  margin-left: 16px;
}

.tdesign-demo-image-viewer__ui-image--icons .tdesign-demo-icon {
  cursor: pointer;
}

.tdesign-demo-image-viewer__base {
  width: 160px;
  height: 160px;
  margin: 10px;
  border: 4px solid var(--td-bg-color-secondarycontainer);
  border-radius: var(--td-radius-medium);
}
</style>