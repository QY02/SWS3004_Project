<template>
  <!--  <h2 style="margin-left: 20px">我的发布</h2>-->
  <!--  <t-tag style="margin-left: 20px;height: 40px; margin-top: 15px;font-size: 20px" size="large" theme="default" variant="light">我的发布</t-tag>-->

  <div v-if="curEvents.length === 0">
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
          :title="item['name']" :subtitle="item.content.length > 12 ? item.content.substring(0, 12) + '...' : item.content" :style="{ width: '445px' }" hover-shadow
          @click="clickEvent(item['id'],item['status'])">
        <t-image
            :src="item['cover']"
            :style="{ width: '395px', height: '210px' }"
            overlay-trigger="hover">

        </t-image>
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

          <a v-if="item['status']===0">
            <t-tag theme="primary" variant="light" style="margin-right: 20px">审核中</t-tag>
          </a>
          <a v-if="item['status']===1">
            <t-tag theme="success" variant="light" style="margin-right: 20px">审核通过</t-tag>
          </a>
          <a v-if="item['status']===2">
            <t-tag theme="danger" variant="light" style="margin-right: 20px">审核未通过</t-tag>
          </a>
          <!--            <t-button variant="text" shape="square">-->
          <!--              <more-icon/>-->
          <!--            </t-button>-->
          <!--          </div>-->
          <!--        </t-dropdown>-->
        </template>
        <template #footer>
          <div v-if="item['status']===2">
            <t-row :align="'middle'" justify="center">

              <t-col style="justify-content: center">
                <t-tooltip content="修改活动">
                  <t-button variant="text" shape="square" @click.stop="fixEvent(item['id'])">
                    <tools-icon/>
                  </t-button>
                </t-tooltip>
              </t-col>
            </t-row>
          </div>
          <div v-else-if="item['status']===0">
            <t-row :align="'middle'" justify="center">

              <t-col style="justify-content: center">

              </t-col>
            </t-row>
          </div>
          <div v-else>
            <t-row :align="'middle'" justify="center" style="gap: 24px;">
              <t-col flex="auto" style="display: inline-flex; justify-content: center;">
                <t-tooltip content="通知">
                  <t-button variant="text" shape="square"
                            @click.stop="()=>{visibleNotice=true; fetchSessionInformation(item['id']);}">
                    <t-icon name="send"/>
                  </t-button>
                </t-tooltip>
              </t-col>

              <t-col flex="auto" style="display: inline-flex; justify-content: center">
                <t-tooltip content="预定详情">
                  <t-button variant="text" shape="square" @click.stop="clickBook(item['id'])">
                    <usergroup-icon/>
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
                <t-tooltip content="分享活动">
                  <t-button variant="text" shape="square" @click.stop="clickShare(item['id'],item['name'])">
                    <share-icon/>
                  </t-button>
                </t-tooltip>
              </t-col>
            </t-row>
          </div>
        </template>
      </t-card>


      <t-dialog v-model:visible="visibleNotice" attach="body" header="向指定场次发布通知" destroy-on-close:true width="800px"
        :cancel-btn=null :confirm-btn=null>
        <template #body>
<!--          <t-space style="width: 100%">-->
            <t-alert theme="info" title="提示" message="该操作将向指定场次所有订阅者发布通通知" style="width: 100%;margin-bottom: 20px;margin-top: 10px">
            </t-alert>
<!--          </t-space>-->
          <t-loading :loading="loadingSession">
            <t-space direction="vertical">
              <t-form ref="form" :data="formData" reset-type="initial" @reset="onReset" @submit="sendNotice"
                :rules="FORM_RULES" label-width="100px" label-align="right">
                <t-form-item name="selectSection" label="选择场次">
                  <br>
                  <t-checkbox-group v-model="formData.selectSection" :options="sessionOptions"></t-checkbox-group>
                </t-form-item>
                <t-form-item name="title" label="标题">
                  <t-input v-model="formData.title" clearable:true placeholder="请输入标题">
                  </t-input>
                </t-form-item>
                <t-form-item name="content" label="内容">
                  <t-textarea v-model="formData.content" clearable:true placeholder="请输入内容">
                  </t-textarea>
                </t-form-item>
                <t-form-item>
                  <t-space size="20px">
                    <t-button theme="default" variant="base" type="reset" :disabled="loadingNotice">重置</t-button>
                    <t-button theme="success" type="submit" :loading="loadingNotice">提交</t-button>
                                            <!-- <t-button theme="default" variant="base" @click="handleClear">清空校验结果</t-button> -->
                  </t-space>
                </t-form-item>
              </t-form>
            </t-space>
          </t-loading>
        </template>
      </t-dialog>


  </div>
<!--  <t-popup content="返回上一页">-->
<!--    <t-button shape="circle" theme="primary" size="large" style="position: fixed;right: 30px;bottom: 40px"-->
<!--              @click="router.push('/user');">-->
<!--      <template #icon>-->
<!--        <rollback-icon/>-->
<!--      </template>-->

<!--    </t-button>-->
<!--  </t-popup>-->
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
</template>

<script setup>

import {
  ThumbUpIcon,
  ChatIcon,
  DataDisplayIcon,
  ShareIcon,
  MoreIcon,
  RollbackIcon,
  ErrorCircleIcon,
  ToolsIcon, UsergroupIcon
} from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import axios from "axios";
import { computed, defineComponent, getCurrentInstance, inject, ref, watch, reactive, onMounted } from "vue";
import router from "@/routers/index.js";
import {ENTITY_TYPE, EVENT_TYPE_MAP} from "@/constants/index.js";
import CommentPage from "@/components/event/CommentPage.vue";
import {fileServerAxios} from "@/main.js";
import { sessionInformation } from '@/components/book/Steps.vue';

const cover = 'https://tdesign.gtimg.com/site/source/card-demo.png';
const events = ref([]);
const attachToken = ref([]);
const tmpEvents = ref([]);
const curEvents = ref([]);
const favColor = ref({});
const loading = ref(false);
const visible = ref(false);
const fetchSessionInformation = async (id) => {
  try {
    loadingSession.value = true
    let response = await axios.post("/event/getEventSessionsByEventId", { eventId: id }, { headers: { token: sessionStorage.getItem('token') } });
    // sessionOptions.value = response.data.data.map((item) => (
    //     `活动时间：${dateToString(new Date(item.startTime))} - ${dateToString(new Date(item.endTime))}`
    // ));
    sessionOptions.value = response.data.data.map((item, index) => ({
      label: `${dateToString(new Date(item.startTime))} - ${dateToString(new Date(item.endTime))}`,
      value: index
    }));
    handleClear();
    loadingSession.value = false
  } catch (error) {
    handleClear();
    loadingSession.value = false
  }
}

const form = ref(null);
const formData = reactive({
  title: '',
  content: '',
  selectSection: [],
});

const publicNotic = async(id) => {
  formData.title = ''
  formData.content = ''
  formData.selectSection = []
  visibleNotice.value = true;
  fetchSessionInformation(id);
}


const handleClear = () => {
    form.value.clearValidate();
};

const publisherId = sessionStorage.getItem('uid')
axios.get(`/event/getMyPost/${publisherId}`, {
  params: {},
  headers: {
    token: sessionStorage.getItem('token')
  }
})
  .then((response) => {
    // alert(response)
    events.value = response.data.data
    curEvents.value = events.value
    tmpEvents.value = events.value
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
              loading.value = false;
              // 将图片 URL 赋值给 cover 变量
              // cover.value = imageUrl;

                  }).catch();
            }).catch();
      }
    }).catch();

const clickComment = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  visible.value = true
}

const clickBook = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  router.push('/orderRecord');
}

const fixEvent = (eventId) => {
  sessionStorage.setItem('eventId', eventId)
  router.push('/fixEvent')
}
const clickShare = (eventId, eventName) => {
  sessionStorage.setItem('eventId', eventId)
  sessionStorage.setItem('MomentName', eventName)
  router.push('/newMoment');
}


const clickEvent = (eventId, status) => {
  if (status === 1) {//除了审核未通过
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

        })
        .catch((error) => {
          if (error.response) {
            // 请求已发出，但服务器响应的状态码不在 2xx 范围内
            MessagePlugin.warning(error.response.data.msg);
          } else {
            // 一些错误是在设置请求的时候触发
            MessagePlugin.warning(error.message);
          }
        });
  }
};
// const eventType = inject('eventType')
const eventType = ref(sessionStorage.getItem('eventType'))

const typeValue = ref([]);  // Initialize with a default value

// Function to handle incoming messages from other tabs
window.addEventListener('setItem', () => {
  typeValue.value = sessionStorage.getItem('eventType');
  curEvents.value = events.value.filter(events => typeValue.value.includes(events['type'] + 1));
  tmpEvents.value = events.value.filter(events => typeValue.value.includes(events['type'] + 1));
  // alert(JSON.stringify(tmpEvents.value))
  // events.value=[]
  // alert(typeValue.value)
})

function getSearchNew(message) {
  // alert(JSON.stringify(tmpEvents.value))

  // curEvents.value =event.content.includes(searchText.value) || event.title.includes(searchText.value)
  // alert(message)
  curEvents.value = tmpEvents.value.filter(events => events['content'].includes(message) || events['name'].includes(message));
  // curEvents.value = tmpEvents.value.filter(tmpEvents => tmpEvents['content'].includes(message) || tmpEvents['eventPolicy'].includes(message));
}

defineExpose({ getSearchNew });

const FORM_RULES = ref({
  title: [{required: true, message: '标题不可为空'}],
  content: [{
    required: true,
    message: '内容不可为空',
  }],
  selectSection: [
    {required: true, message: '场次不可为空'}
  ]
});

const visibleNotice = ref(false);
const loadingNotice = ref(false);
const loadingSession = ref(false);
const sessionOptions = ref([]);


const onReset = () => {
  MessagePlugin.success('重置成功');
};

const sendNotice = ({ validateResult, firstError }) => {
  if (validateResult === true) {
    loadingNotice.value = true
    // console.log([...formData.selectSection])
    axios.post(`/notification/sessions`, {
    }, {
      headers: {
        token: sessionStorage.getItem('token')
      },
      params: {
        "title": formData.title,
        "content": formData.content,
        "selectSection": [...formData.selectSection]
      }
    }).then(() => {
      MessagePlugin.info("已发送");
      visibleNotice.value = false;
      loadingNotice.value = false;
    }).catch(() => {
      loadingNotice.value = false; })
  } else {
    console.log('错误: ', firstError, validateResult);
    MessagePlugin.warning(firstError);
  }
}
const dateToString = (date) => {
  const dayNameArray = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
  const dayName = dayNameArray[date.getDay()];
  const localeDateStringArray = date.toLocaleString().split(' ');
  const result = `${localeDateStringArray[0]} ${dayName} ${localeDateStringArray[1]}`;
  return result;
}



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
</style>