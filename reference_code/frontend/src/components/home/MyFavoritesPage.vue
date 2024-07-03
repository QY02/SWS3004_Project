<template>
<!--  <h2 style="margin-left: 20px">我的收藏</h2>-->
<!--  <t-tag style="margin-left: 20px;height: 40px; margin-top: 15px;font-size: 20px" size="large" theme="warning" variant="light">我的收藏</t-tag>-->
  <div v-if="curEvents.length===0">
    <div style="display: flex; align-items: center;text-align: center;margin-left: 45%; margin-top: 10%">
      <error-circle-icon size="large"></error-circle-icon>
      <h1 style="color: #5e6066; font-size: large; margin-left: 10px;">暂无活动</h1>
    </div>
  </div>
  <div v-else>
    <div id="event" v-loading="loading">
    <t-card
        v-for="(item,index) in curEvents"
        :key="index"
        :title="item['name']" :subtitle="item.content.length > 18 ? item.content.substring(0, 18) + '...' : item.content" :style="{ width: '445px' }" hover-shadow
        @click="clickEvent(item['id'])">
      <template #actions>
        <!--        <t-dropdown :options="options" :min-column-width="112" @click="clickHandler">-->
        <!--          <div class="tdesign-demo-dropdown-trigger">-->

        <a v-if="item['type']>=0&&item['type']<=2">
          <t-tag theme="success" variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[item['type']] }}</t-tag>
        </a>
        <a v-if="item['type']>=3&&item['type']<=5">
          <t-tag theme="primary" variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[item['type']] }}</t-tag>
        </a>
        <a v-if="item['type']>=6&&item['type']<=8">
          <t-tag theme="danger" variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[item['type']] }}</t-tag>
        </a>
        <a v-if="item['type']>=9&&item['type']<=12">
          <t-tag variant="light" style="margin-right: 20px">{{ EVENT_TYPE_MAP[item['type']] }}</t-tag>
        </a>
        <!--        {{item['visitTime'].replace("T"," ")}}-->
        <!--            <t-button variant="text" shape="square">-->
        <!--              <more-icon/>-->
        <!--            </t-button>-->
        <!--          </div>-->
        <!--        </t-dropdown>-->
      </template>
<!--      <template #footer>-->
<!--        <t-row :align="'middle'" justify="center" style="gap: 24px;">-->
<!--          <t-col flex="auto" style="display: inline-flex; justify-content: center;">-->
<!--            <t-popup content="收藏活动">-->
<!--              <t-button variant="text" shape="square" @click.stop="favEvent(item['id'])">-->

<!--                <t-icon name="heart" :color="favColor[item['id']]"/>-->
<!--              </t-button>-->
<!--            </t-popup>-->
<!--          </t-col>-->

<!--          <t-col flex="auto" style="display: inline-flex; justify-content: center">-->
<!--            <t-popup content="评论">-->
<!--              <t-button variant="text" shape="square" @click.stop="clickComment(item['id'])">-->
<!--                <chat-icon/>-->
<!--              </t-button>-->
<!--            </t-popup>-->
<!--          </t-col>-->

<!--          <t-col flex="auto" style="display: inline-flex; justify-content: center">-->
<!--            <t-popup content="分享活动">-->
<!--              <t-button variant="text" shape="square" @click.stop="clickShare(item['id'],item['name'])">-->
<!--                <share-icon/>-->
<!--              </t-button>-->
<!--            </t-popup>-->
<!--          </t-col>-->
<!--        </t-row>-->
<!--      </template>-->
    </t-card>

    </div>
  </div>
  <t-dialog
      v-model:visible="visible"
      header="评论"
      body="自定义底部按钮，直接传入文字"
      :top="'50px'"
      :confirm-btn="null"
      :cancel-btn="null"
  >
    <a v-if="visible===true">
      <CommentPage></CommentPage>
    </a>

  </t-dialog>
<!--  <t-popup content="返回上一页">-->
<!--    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px"-->
<!--              @click="router.push('/user');">-->
<!--      <template #icon>-->
<!--        <rollback-icon/>-->
<!--      </template>-->

<!--    </t-button>-->
<!--  </t-popup>-->
</template>

<script setup>

import {
  ThumbUpIcon,
  ChatIcon,
  ShareIcon,
  MoreIcon,
  AddIcon,
  RollbackIcon,
  ErrorCircleIcon
} from 'tdesign-icons-vue-next';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from "axios";
import {getCurrentInstance, ref} from "vue";
import router from "@/routers/index.js";
import {EVENT_TYPE_MAP} from "@/constants/index.js";
import CommentPage from "@/components/event/CommentPage.vue";
// 获取全局变量 $apiBaseUrl

const cover = 'https://tdesign.gtimg.com/site/source/card-demo.png';
const events = ref([]);
const attachToken = ref([]);
const tmpEvents = ref([]);
const curEvents = ref([]);
const favColor = ref({});
const loading = ref(false);
const visible = ref(false);
const options = [
  {
    content: '操作一',
    value: 1,
  },
  {
    content: '操作二',
    value: 2,
  },
];

axios.post(`/favorite/getByUserId`, {
  "userId": sessionStorage.getItem('uid')
}, {
  params: {},
  headers: {
    token: sessionStorage.getItem('token')
  }
})
    .then((response) => {
      // alert(response)
      events.value = response.data.data
      curEvents.value = response.data.data
      for (let i = 0; i < events.value.length; i++) {//获取每个活动的海报
        // for (let i = 0; i < 1; i++) {//获取每个活动的海报
        let id = events.value[i]['id'];
        axios.post(`/favorite/isFavorite`, {
          "eventId": id,
          "userId": sessionStorage.getItem('uid')
        }, {
          headers: {
            token: sessionStorage.getItem('token')
          }
        }).then((response) => {
          // alert(JSON.stringify(response.data.data))
          if (response.data.data === 1) {
            favColor.value[id] = 'red'
          } else {
            favColor.value[id] = 'black'
          }
        }).catch();
      }
    }).catch();

const clickComment = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  visible.value = true
}
const clickShare = (eventId, eventName) => {
  sessionStorage.setItem('eventId', eventId)
  sessionStorage.setItem('MomentName', eventName)
  router.push('/newMoment');
}
const favEvent = (eventId) => {
  // MessagePlugin.success(`${sessionStorage.getItem('uid')} 喜欢【${eventId}】`);
  if (favColor.value[eventId] === 'black') {//妹收藏过
    axios.post(`/favorite/add`, {
      "eventId": eventId,
      "userId": sessionStorage.getItem('uid'),
    }, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    }).then(() => {
          favColor.value[eventId] = 'red'
          MessagePlugin.success("收藏成功");
        }).catch();
  } else {
    // alert('hhh')
    axios.post(`/favorite/delete`, {
      "eventId": eventId,
      "userId": sessionStorage.getItem('uid')
    }, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    }).then((response) => {
      favColor.value[eventId] = 'black'
      MessagePlugin.success("取消收藏成功");
    }).catch()
  }
};
const clickEvent = (eventId) => {
  // MessagePlugin.success(`${sessionStorage.getItem('uid')} 选中【${eventId}】`);
  sessionStorage.setItem('eventId', eventId)
  router.push('/event');
  // router.push('/event');
  axios.post(`/history/add`, {
    "eventId": eventId,
    "userId": sessionStorage.getItem('uid')
  }, {
    params: {},
    headers: {
      token: sessionStorage.getItem('token')
    }
  }).then((response) => {}).catch();
};

// const eventType = inject('eventType')
const eventType = ref(sessionStorage.getItem('eventType'))

const typeValue = ref([]);  // Initialize with a default value

// Function to handle incoming messages from other tabs
window.addEventListener('setItem', () => {
  typeValue.value = sessionStorage.getItem('eventType');
  curEvents.value = events.value.filter(events => typeValue.value.includes(events['type'] + 1));
  tmpEvents.value = events.value.filter(events => typeValue.value.includes(events['type'] + 1));
})

function getSearchNew(message) {
  curEvents.value = tmpEvents.value.filter(events => events['content'].includes(message) || events['name'].includes(message));
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