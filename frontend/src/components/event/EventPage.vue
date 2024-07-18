<template>
  <div v-if="!loading && eventDetail.length !== 0">
    <div class="card">
      <div class="card-1">
        <t-image :src="photo" fit="fill" :style="{ width: '100%', height: '100%' }" shape="round" />
      </div>
      <div class="right">
        <div class="card-2">
          <h2>{{ eventDetail.name }}</h2>
        </div>
        <div class="card-3">
          <t-tag size="large">{{ titleDict[eventDetail.type] }}</t-tag>
        </div>
        <div class="bottom">
          <div class="card-4">
            <t-space size="24px">
              <!-- <div v-if="isFavorite"> -->
              <t-tooltip content="收藏">
                <t-button :loading="loading_favorite" shape="circle" theme="primary" @click="clickHeart()">
                  <template #icon>
                    <HeartFilledIcon v-show="isFavorite" />
                    <HeartIcon v-show="!isFavorite" />
                  </template>
                </t-button>
              </t-tooltip>
              <!-- </div>
            <div v-else>
              <t-button :loading="loading_favorite" shape="circle" theme="primary" @click="addFavorite()">
                <template #icon>
                  <HeartIcon />
                </template>
              </t-button>
            </div> -->
            </t-space>
          </div>
          <div class="card-4">
            <t-tooltip content="评分">
              <t-tag size="large" theme="primary"><template #icon>
                <StarFilledIcon />
              </template>{{ parentAverageScore }}</t-tag>
            </t-tooltip>
          </div>
          <t-badge count="试用" color="#00A870">
            <t-button @click="pushRouter('chatWithEvent')">与大模型聊天</t-button>
          </t-badge>
        </div>
      </div>
    </div>




    <el-affix :offset="55">
      <t-tabs :value="value_lable" size="large" @change="onTabChange" :affix-props="{ offsetTop: 150 }">
        <t-tab-panel value="events" label="活动场次"></t-tab-panel>
        <t-tab-panel value="about" label="活动细节"></t-tab-panel>
        <t-tab-panel value="price" label="活动票价"></t-tab-panel>
        <t-tab-panel value="policy" label="额外信息"></t-tab-panel>
        <t-tab-panel value="gallery" label="相关动态"></t-tab-panel>
        <t-tab-panel value="reviews" label="评论"></t-tab-panel>
      </t-tabs>
    </el-affix>


    <!-- event part -->
    <div :id="`${path}#events`" style="height: 60px;"></div>

    <!-- button part -->
    <t-space style="display: f2lex; width: 100%; margin-left: 24px;">
      <div>
        <div class="title">活动场次</div>
        <div class="line"></div>
      </div>
      <div style="margin-top: 30px; display: flex; justify-content: flex-end; margin-right:79px ;">
        <div v-if="show_event_type">
          <t-space>
            <!-- <t-button theme="primary" shape="square" variant="base" @click="onClickEventType(true)">
              <list-icon slot="icon" />
            </t-button> -->
            <!-- <t-button shape="square" variant="outline" @click="onClickEventType(false)">
              <table-icon slot="icon" />
            </t-button> -->
          </t-space>
        </div>
        <div v-else>
          <t-space>
            <t-button shape="square" variant="outline" @click="onClickEventType(true)">
              <list-icon slot="icon" />
            </t-button>
            <!-- <t-button theme="primary" shape="square" variant="base" @click="onClickEventType(false)">
              <table-icon slot="icon" />
            </t-button> -->
          </t-space>
        </div>
      </div>
    </t-space>

    <!-- detail part -->
    <el-card
      style="height: auto; padding: 5px;  max-width: 100% ; margin-right: 30px; margin-left: 30px; margin-bottom: 40px;">
      <div v-if="show_event_type"
        style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
        <t-space direction="vertical" style="margin-top: 20px; " v-show="sessionInformation.length > 0">
          <t-collapse style="width: 50vw">
            <t-collapse-panel v-for="(session, index) in sessionInformation"
              :header="`${dateToString(session.startTime)} - ${dateToString(session.endTime)} ${session.venue}`">
              <div class="choose-session-detail-div">
                <p v-if="session.registrationRequired" class="choose-session-detail-text">
                  {{
                    `报名时间: ${dateToString(session.registrationStartTime)} - ${dateToString(session.registrationEndTime)}`
                  }}</p>
                <p v-else class="choose-session-detail-text">无需报名</p>
                <p class="choose-session-detail-text">{{ `人数限制: ${session.minSize} - ${session.maxSize}` }}</p>
              </div>
            </t-collapse-panel>
          </t-collapse>
        </t-space>
        <br>
        <t-button @click="pushRouter('book')" v-show="sessionInformation.length > 0">前往报名</t-button>
        <div v-show="sessionInformation.length == 0">
          <p>暂无场次信息，敬请关注</p>
        </div>
        <!-- <EventTicketTable></EventTicketTable> -->
      </div>
      <div v-else>
        <EventTicketCalender></EventTicketCalender>
      </div>
    </el-card>
    <div style="height: 20px;"></div>



    <!-- about part -->
    <div :id="`${path}#about`" style="height: 36px;"></div>
    <div class="card" style="flex-direction: column;">
      <t-space style="display: flex; width: 100%;">
        <div>
          <div class="title">活动细节</div>
          <div class="line"></div>
        </div>
      </t-space>
      <div style="margin-left: 25px;margin-right: 25px; margin-bottom: 40px;white-space: pre-wrap;word-wrap: anywhere;">
        {{ eventDetail.content }}
      </div>
    </div>



    <!-- price part -->
    <div :id="`${path}#price`" style="height: 60px;"></div>
    <t-space style="display: flex; width: 100%; margin-left: 24px;">
      <div>
        <div class="title">活动票价</div>
        <div class="line"></div>
      </div>
    </t-space>
    <div class="ticket_card" style="margin-left: 55px; margin-top: -10px;">
      <p style="  color: rgba(7, 63, 216, 1); font-size: 18px; font-weight: 700; letter-spacing: 1px;">
        场次信息
      </p>
      <div v-show="sessionInformation.length > 0">
        <p style="  margin-top: 0.4rem; color: rgb(70, 73, 79);font-weight: 600;">
          
        </p>
        <p style=" margin-top: -5px; line-height: 1.625; color: rgb(70, 73, 79);">
        <div v-for="(session, index) in sessionInformation">
          <div class="choose-session-detail-div">
            <p v-if="session.registrationRequired" class="choose-session-detail-text">
            <p>场次{{ Number(index+1) }}: 
            {{
              `时间: ${dateToString(session.registrationStartTime)}`
            }} </p>
            </p>
            <p v-else class="choose-session-detail-text">
            <p>场次{{ Number(index+1) }}: 无需报名</p>
            </p>
          </div>
        </div>
        </p>
        <el-divider />
        <p style="  color: rgba(7, 63, 216, 1); font-size: 18px; font-weight: 700; letter-spacing: 1px;">
          价格标准
        </p>
        <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="eventDetail.highestPrice===0">
          不需要购买
        </p>
        <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="eventDetail.highestPrice>0&& eventDetail.highestPrice===eventDetail.lowestPrice">
          门票价格：¥{{ eventDetail.lowestPrice }}
        </p>
        <p style="  margin-top: 0.4rem; line-height: 1.625; color: rgb(70, 73, 79);;" v-show="eventDetail.highestPrice>0 && eventDetail.highestPrice!==eventDetail.lowestPrice">
          门票价格：¥{{ eventDetail.lowestPrice }} - ¥{{ eventDetail.highestPrice }}
        </p>
      </div>
      <div v-show="sessionInformation.length == 0">
        <p>暂无贩卖信息，敬请关注</p>
      </div>
    </div>
    <div style="height: 20px;"></div>
    <div style="height: 20px;"></div>



    <!-- policy part -->
    <div :id="`${path}#policy`" style="height: 36px;"></div>
    <div class="card" style="flex-direction: column;">
      <t-space style="display: flex; width: 100%;">
        <div>
          <div class="title">额外信息</div>
          <div class="line"></div>
        </div>
      </t-space>
      <div class="ticket_card" style="margin-left: 55px; margin-top: -10px;">
        <!-- <p style="  margin-top: 0.4rem; color: rgb(70, 73, 79);font-weight: 600;">
          Rules
        </p> -->
        <p v-if="eventDetail.eventPolicy" style="  margin-top: 0.4rem; line-height: 1.625;color: rgb(70, 73, 79);white-space: pre-wrap;word-wrap: anywhere;">
          {{ eventDetail.eventPolicy }}
        </p>
        <p v-else style="  margin-top: 0.4rem; line-height: 1.625;color: rgb(70, 73, 79);;">
          暂无
        </p>
      </div>
      <div style="height: 40px;"></div>
    </div>



    <!-- gallery part -->
    <div :id="`${path}#gallery`" style="height: 60px;"></div>
    <t-space style="display: flex; width: 100%; margin-left: 24px;">
      <div>
        <div class="title">相关动态</div>
        <div class="line"></div>
      </div>
    </t-space>

    <!-- <el-card
      style="padding: 5px; height: 390px ; max-width: 100% ; margin-right: 30px; margin-left: 30px; margin-bottom: 40px;">
      <el-carousel :interval="4000" type="card"  height="270px" width="100%" indicator-position="outside">
        <el-carousel-item v-for="item in 4" :key="item">
          <h3 text="2xl" justify="center">
            <t-image src="https://tdesign.gtimg.com/demo/demo-image-1.png" fit="fill"
              :style="{ width: '100%', height: '100%' }" shape="round" />
            </h3>
        </el-carousel-item>
      </el-carousel>
      <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
        <t-button @click="pushRouter('gallery')">前往动态</t-button>
      </div>
    </el-card> -->
    <GalleryPart></GalleryPart>
    <div style="height: 40px;"></div>


    <!-- review part -->
    <div :id="`${path}#reviews`" style="height: 36px;"></div>
    <div class="card" style="flex-direction: column;">
      <CommentPage @update:averageScore="parentAverageScore = $event"></CommentPage>
      <div style="height: 40px;"></div>
    </div>
    <!-- <TestPage></TestPage> -->
  </div>
  <div v-else>
    <SkeletonPage></SkeletonPage>
  </div>
</template>



<script setup>
// import TestPage from './testPage.vue';
import GalleryPart from './GalleryPart.vue';
import { sessionInformation, currentStep } from '@/components/book/Steps.vue';
import { HeartIcon, HeartFilledIcon, ListIcon, TableIcon, StarFilledIcon, DiscountIcon } from 'tdesign-icons-vue-next';
import { computed, getCurrentInstance, ref, onMounted } from 'vue';
import axios from "axios";
import { fileServerAxios } from "@/main.js"
import get from 'lodash/get';
import EventTicketCalender from './EventTicketCalender.vue';
import ChooseSession from '../book/ChooseSession.vue';
import router from '@/routers';
import SkeletonPage from './SkeletonPage.vue';
import CommentPage from './CommentPage.vue';

sessionStorage.setItem('currentStep', 0)
currentStep.value = 0;
const value_lable = ref('events');

const show_event_type = ref(true);

const loading = ref(true);
const loading_favorite = ref(false);

const onClickEventType = (value) => {
  show_event_type.value = value;
};

const onTabChange = (newValue) => {
  value_lable.value = newValue;
  navigateToTab(newValue);
}

const navigateToTab = (tabName) => {
  window.location.href = `##${tabName}`; // 在链接中添加锚点路径并跳转
  window.scrollTo(0, window.scrollY + 55);
};

const titleDict = {
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

const dateToString = (date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}

const attachmentPath = ref('');
const photo = ref('');
const photoUrl = ref('https://tdesign.gtimg.com/demo/demo-image-1.png');
const getAttachment = () => {
  axios.get(`/event/getPhotoById?eventId=${eventId}`, {
    headers: {
      token: token
    }
  }).then((response) => {
    attachmentPath.value = response.data.data
    // console.log(attachmentPath.value)
    if (attachmentPath.value !== null && attachmentPath.value !== undefined && attachmentPath.value !== '')
      getPhoto();
  }).catch(() => { })
}
const getPhoto = async () => {
  try {
    if (attachmentPath.value) {
      // console.log('yes')
      const fileServerResponse = await fileServerAxios.get(`/file/download`, {
        responseType: 'blob',
        headers: {
          token: attachmentPath.value,
        }
      });
      const image = fileServerResponse.data;
      photo.value = image;
    }
  } catch (error) {

  }
  // fileServerAxios.get(`/file/download`, {
  //   responseType: 'blob',
  //   headers: {
  //     token: attachmentPath,
  //   }
  // }).then((fileServerResponse) => {
  //   const image = fileServerResponse.data;
  //   photo.value=image;
  //   photoUrl.value=URL.createObjectURL(image);
  // }).catch(() => { })
}


const pushRouter = (value) => {
  switch (value) {
    case 'gallery':
      router.push('/gallery');
      break;
    case 'book':
      router.push('/book');
      break;
    case 'chatWithEvent':
      router.push('/chatWithEvent');
      break;
  }
}


const token = sessionStorage.getItem('token')
const eventId = sessionStorage.getItem('eventId')
const uid = sessionStorage.getItem('uid')
const eventDetail = ref({})
const isFavorite = ref(false);
const parentAverageScore = ref(0);

const addHistory = () => {
  axios.post(`/history/add`, {
    "eventId": eventId,
    "userId": uid
  }, {
    params: {},
    headers: {
      token: sessionStorage.getItem('token')
    }
  })
    .then((response) => { })
    .catch((error) => {
    });
}

const getEventDetail = () => {
  loading.value = true;
  axios.get(`/event/getEventDetail/${eventId}`, {
    headers: {
      token: token
    }
  }).then((response) => {
    eventDetail.value = response.data.data
    addHistory();
    getFavorite();
    loading.value = false;
  }).catch(() => { })
}

const getFavorite = () => {
  axios.post(`/favorite/isFavorite`, {
    "eventId": eventId,
    "userId": uid
  }, {
    headers: {
      token: token
    }
  }).then((response) => {
    isFavorite.value = response.data.data
  }).catch(() => { })
}

const clickHeart = () => {
  if (!isFavorite.value) {
    addFavorite()
  }
  else {
    deleteFavorite()
  }
}

const addFavorite = () => {
  loading_favorite.value = true
  axios.post(`/favorite/add`, {
    "eventId": eventId,
    "userId": uid
  }, {
    headers: {
      token: token
    }
  }).then((response) => {
    isFavorite.value = true
    console.log("add: " + isFavorite.value)
    loading_favorite.value = false
  }).catch(() => { loading_favorite.value = false })
}

const deleteFavorite = () => {
  loading_favorite.value = true
  axios.post(`/favorite/delete`, {
    "eventId": eventId,
    "userId": uid
  }, {
    headers: {
      token: token
    }
  }).then((response) => {
    isFavorite.value = false
    console.log(isFavorite.value)
    loading_favorite.value = false
  }).catch(() => { loading_favorite.value = false })
}

const fetchSessionInformation = async () => {
  try {
    let response = await axios.post("/event/getEventSessionsByEventId", { eventId: sessionStorage.getItem('eventId') }, { headers: { token: sessionStorage.getItem('token') } });
    const dataConverted = response.data.data.map((item) => ({
      ...item,
      startTime: new Date(item.startTime),
      endTime: new Date(item.endTime),
      registrationStartTime: new Date(item.registrationStartTime),
      registrationEndTime: new Date(item.registrationEndTime),
      location: item.location.split(",").map(Number)
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
  getAttachment();
  fetchSessionInformation();
});

defineExpose({ navigateToTab });



const { appContext } = getCurrentInstance();
const path = computed(() => get(appContext, '$route.path', ''));

</script>

<style lang="css" scoped>
.anchor-demo {
  border: 1px solid transparent;
  padding: 20px;
  margin: -20px;
}

.anchor-demo:target {
  border-color: #1890ff;
}

.event {
  margin-left: 25px;
  margin-right: 25px;
  display: flex;
  flex-direction: row;
  gap: 10px;
}

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
  width: 100%;
  border-radius: 1rem;
}

.card-3 {
  width: 100%;
  border-radius: 1rem;
}

.bottom {
  margin-top: auto;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 5%;
}

.card-4 {
  height: 2rem;
  border-radius: 9999px;
}

.card-5 {
  height: 2rem;
  width: 100%;
  border-radius: 9999px;
  background-color: var(--gray);
}


@keyframes pulse {
  to {
    opacity: .2;
  }
}


.container {
  position: relative;
}

.content {
  height: 2000px;
  /* 用于模拟页面滚动 */
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


.comment_card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background-color: rgba(255, 255, 255, 1);
  padding: 20px;
}

.price_card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
}


.stars {
  display: flex;
  grid-gap: 0.125rem;
  gap: 0.125rem;
  margin-bottom: -10px;
  color: rgb(238, 203, 8);
}

.comment_infos {
  margin-top: 1rem;
}

.date-time {
  color: rgba(7, 63, 216, 1);
  font-size: 12px;
  font-weight: 600;
}

.description {
  margin-top: 0.4rem;
  line-height: 1.625;
  color: rgba(107, 114, 128, 1);
}

.author {
  margin-top: 1.3rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  color: rgba(107, 114, 128, 1);
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

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}
</style>