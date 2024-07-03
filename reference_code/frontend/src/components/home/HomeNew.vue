<template >
  <t-loading size="small" :loading="loading" show-overlay>
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
        :title="item['name']"  :subtitle="item.content.length > 18 ? item.content.substring(0, 18) + '...' : item.content" :style="{ width: '445px' }"
        hover-shadow
        @click="clickEvent(item['id'])"
        lazy-load
    >

      <t-image
          :src="item['cover']"
          :style="{ width: '395px', height: '210px' }"
          overlay-trigger="hover">

      </t-image>


      <!--      <template #cover>-->
      <!--        &lt;!&ndash; Image with native lazy loading &ndash;&gt;-->
      <!--        <img :src="item.imageUrl" loading="lazy" alt="Event Image">-->
      <!--      </template>-->
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
        <!--            <t-button variant="text" shape="square">-->
        <!--              <more-icon/>-->
        <!--            </t-button>-->
        <!--          </div>-->
        <!--        </t-dropdown>-->
      </template>
      <template #footer>
        <t-row :align="'middle'" justify="center" style="gap: 24px;">
          <t-col flex="auto" style="display: inline-flex; justify-content: center;">
            <t-tooltip content="收藏">
            <t-button variant="text" shape="square" @click.stop="favEvent(item['id'])">

              <t-icon name="heart" :color="favColor[item['id']]"/>
            </t-button>
            </t-tooltip>
          </t-col>

          <t-col flex="auto" style="display: inline-flex; justify-content: center">
            <t-tooltip content="评论">
            <t-button variant="text" shape="square" @click.stop="clickComment(item['id'])">
              <chat-icon/>
            </t-button>
            </t-tooltip>
          </t-col>

          <t-col flex="auto" style="display: inline-flex; justify-content: center">
            <t-tooltip content="分享">
            <t-button variant="text" shape="square" @click.stop="clickShare(item['id'],item['name'])">
              <share-icon/>
            </t-button>
            </t-tooltip>
          </t-col>
        </t-row>
      </template>
    </t-card>
  </div>

  </div>
  </t-loading>
  <t-dialog
      v-model:visible="visible"
      body="自定义底部按钮，直接传入文字"
      placement="top"
      :confirm-btn="null"
      :cancel-btn="null"
      width="900px"
  >
    <a v-if="visible===true">
      <CommentPage></CommentPage>
    </a>

  </t-dialog>

</template>

<script setup>

import {BrowseIcon, ChatIcon, ErrorCircleIcon, HeartIcon, ShareIcon} from 'tdesign-icons-vue-next';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from "axios";
import {getCurrentInstance, ref} from "vue";
import router from "@/routers/index.js";
import {ENTITY_TYPE, EVENT_TYPE_MAP} from "@/constants/index.js";
import CommentPage from "@/components/event/CommentPage.vue";
import {fileServerAxios} from "@/main.js";


const visible = ref(false);
const loading = ref(true);
// alert(apiBaseUrl)
const top = '50px';


const cover = ref('https://tdesign.gtimg.com/site/source/card-demo.png');
const events = ref([]);
const attachToken = ref([]);
const tmpEvents = ref([]);
const curEvents = ref([]);
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
const favColor = ref({});



axios.post(`/event/getAllEvents`, {}, {
  params: {},
  headers: {
    token: sessionStorage.getItem('token')
  }
}).then((response) => {
      // alert(response)
      events.value = response.data.data.filter(events => events['status'] === 1)
      curEvents.value = events.value
      tmpEvents.value = events.value
      loading.value=false
      // alert(JSON.stringify(events.value))
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
        }).catch(() => {
        })
        // favColor.value[i] = isFavorite.value;

        // alert(id)

        axios.post(`/postAttachmentRelation/getAttachment`, {
          "entity_type": ENTITY_TYPE.EVENT,
          "entity_id": id,
          "attachment_type": 0,
        }, {
          params: {},
          headers: {
            token: sessionStorage.getItem('token')
          }
        }).then((response) => {
              attachToken.value = response.data.data['filePath']
              // alert(JSON.stringify(response.data.data))
              let attachToken1 = attachToken.value
              // alert(attachToken1)
              // 47.107.113.54:25572 文件服务器地址
              fileServerAxios.get(`/file/download`, {
                params: {},
                headers: {
                  token: attachToken1
                },
                responseType: 'blob'
              }).then((response) => {//
                    // alert(JSON.stringify(response.data))
                    // 将图片 URL 赋值给 cover 变量
                    const blob = new Blob([response.data], {type: 'application/octet-stream'});

                    // 创建一个 Blob 对象的 URL
                    const imageUrl = URL.createObjectURL(blob);
                    events.value[i]['cover'] = imageUrl
                    loading.value=false;
                    // 将图片 URL 赋值给 cover 变量
                    // cover.value = imageUrl;

                  })
                  .catch();
            }).catch();
      }
    }).catch();


// const eventType = inject('eventType')
const eventType = ref(sessionStorage.getItem('eventType'))
const typeValue = []  // Initialize with a default value

// Function to handle incoming messages from other tabs
window.addEventListener('setItem', () => {
  typeValue.value = sessionStorage.getItem('eventType').split(",").map(Number);//传进来的是个字符串！！！
  curEvents.value = events.value.filter(events => typeValue.value.indexOf(events['type'] + 1) !== -1);
  tmpEvents.value = events.value.filter(events => typeValue.value.includes(events['type'] + 1));
})

const clickComment = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  visible.value = true
}
const clickShare = (eventId, eventName) => {
  sessionStorage.setItem('eventId', eventId)
  sessionStorage.setItem('MomentName', eventName)
  router.push('/newMoment');
}
// alert(sessionStorage.getItem('MomentName'))
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
    })
        .then(() => {
          favColor.value[eventId] = 'red'
          MessagePlugin.success("收藏成功！");
        })
        .catch();
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
      MessagePlugin.success("取消收藏成功！");

    }).catch(() => {
    })
  }
};


function getSearchNew(message) {
  curEvents.value = tmpEvents.value.filter(events => events['content'].includes(message) || events['name'].includes(message));
  // curEvents.value = tmpEvents.value.filter(tmpEvents => tmpEvents['content'].includes(message) || tmpEvents['eventPolicy'].includes(message));
}

defineExpose({getSearchNew});
// 获取全局变量 $apiBaseUrl


// const {colors} = useColors();
// colors.primary = sessionStorage.getItem('primary-color')
// alert(colors.primary)


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