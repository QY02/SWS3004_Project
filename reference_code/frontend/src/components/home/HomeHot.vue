<template>
  <t-loading size="small" :loading="loading" show-overlay>
  <div v-if="curEvents.length===0">
    <div style="display: flex; align-items: center;text-align: center;margin-left: 45%; margin-top: 10%">
      <error-circle-icon size="large"></error-circle-icon>
      <h1 style="color: #5e6066; font-size: large; margin-left: 10px;">暂无活动</h1>
    </div>
  </div>
  <div v-else>
    <div id="event">

      <t-list :split="true" hover-shallow>
        <t-list-item v-for="(item,index) in curEvents" :key="item" @click="clickEvent(item['id'])">
          <div style="display: flex;">
            <div v-if="index+1<=3">
              <t-tag theme="danger" variant="light" style="margin-right: 20px">{{ index + 1 }}
                <template #icon>
                  <t-icon name="rocket"/>
                </template>
              </t-tag>
            </div>
            <div v-else>
              <t-tag theme="primary" variant="light" style="margin-right: 40px">{{ index + 1 }}</t-tag>
            </div>
            <t-image
                :src="item.cover"
                fit="cover"
                :style="{ width: '250px', height: '120px'}"
                position="left"
                style="margin-right: 50px"
            />

            <!--          <t-list-item-meta :title="item.title" :description="item.content" style="display: flex; align-items: center;"/>-->
            <t-list-item-meta class="t-list-item-meta-description" :title="item.name" :description="item.content.length > 140 ? item.content.substring(0, 140) + '...' : item.content"
                              style="display: flex; align-items: center;">
              <!--            <p class="t-list-item-meta-description">{{ item.content }}</p>-->
            </t-list-item-meta>
          </div>
          <template #action>
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

            <t-tooltip content="详情">
            <t-button variant="text" shape="square" @click="clickEvent(item['id'])">
              <arrow-right-icon/>
            </t-button>
            </t-tooltip>
          </template>


        </t-list-item>

      </t-list>
    </div>
  </div>
  </t-loading>
</template>

<script setup>
import {ArrowRightIcon, ErrorCircleIcon} from 'tdesign-icons-vue-next';
import {MessagePlugin} from "tdesign-vue-next";
import rocket from "tdesign-icons-vue-next/lib/components/rocket.js";
import {getCurrentInstance, ref} from "vue";
import axios from "axios";
import router from "@/routers/index.js";
import {ENTITY_TYPE, EVENT_TYPE_MAP} from "@/constants/index.js";
import {fileServerAxios} from "@/main.js";


// const getEvent = (item) => {
//   MessagePlugin.success(`选中【${item.title}】`);
// };


const cover = 'https://tdesign.gtimg.com/site/source/card-demo.png';
const events = ref([]);
const attachToken = ref([]);
const loading = ref(true);
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

axios.post(`/event/getHotEvents`, {}, {
  params: {},
  headers: {
    token: sessionStorage.getItem('token')
  }
})
    .then((response) => {
      // alert(response)
      events.value = response.data.data.filter(events => events['status'] === 1).slice(0, 15)
      curEvents.value = events.value
      tmpEvents.value = events.value
      loading.value=false
      // alert(JSON.stringify(events.value))
      for (let i = 0; i < events.value.length; i++) {//获取每个活动的海报
        // for (let i = 0; i < 1; i++) {//获取每个活动的海报
        let id = events.value[i]['id'];

        axios.post(`/postAttachmentRelation/getAttachment`, {
          "entity_type": ENTITY_TYPE.EVENT,
          "entity_id": id,
          "attachment_type": 0,
        }, {
          params: {},
          headers: {
            token: sessionStorage.getItem('token')
          }
        })
            .then((response) => {
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
              })
                  .then((response) => {//
                    // alert(JSON.stringify(response.data))
                    // 将图片 URL 赋值给 cover 变量
                    const blob = new Blob([response.data], {type: 'application/octet-stream'});

                    // 创建一个 Blob 对象的 URL
                    const imageUrl = URL.createObjectURL(blob);
                    events.value[i]['cover'] = imageUrl
                    loading.value=false;
                    // 将图片 URL 赋值给 cover 变量
                    // cover.value = imageUrl;

                  }).catch();
            }).catch();
        // events.value[i].imageUrl =
        // alert(id)
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
  })
      .then((response) => {

      }).catch();
};



function getSearchNew(message) {
  // alert(JSON.stringify(tmpEvents.value))

  // curEvents.value =event.content.includes(searchText.value) || event.title.includes(searchText.value)
  // alert(message)
  curEvents.value = tmpEvents.value.filter(events => events['content'].includes(message) || events['name'].includes(message));
  // curEvents.value = tmpEvents.value.filter(tmpEvents => tmpEvents['content'].includes(message) || tmpEvents['eventPolicy'].includes(message));
}

defineExpose({getSearchNew});
// 获取全局变量 $apiBaseUrl

</script>

<style scoped>
#event {
  margin: 10px;
}

.t-list-item-meta-description {
  max-width: 700px; /* 设置description的最大宽度 */
  overflow: auto; /* 超出部分隐藏 */
  text-overflow: ellipsis; /* 文本溢出显示省略号 */
  word-wrap: initial; /* 强制换行 */
}
</style>