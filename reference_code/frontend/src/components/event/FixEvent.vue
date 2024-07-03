<template>
  <t-space direction="vertical" size="large" style="width: 100%">
    <div style="margin: 30px 40px;">
      <h1>修改活动</h1>
      <t-form ref="form" :data="formData" reset-type="initial" @reset="onReset" @submit="onSubmit" :rules="FORM_RULES">
        <div>
          <t-form-item label="标题" name="name">
            <t-input v-model="formData.name">标题</t-input>
          </t-form-item>

          <t-form-item label="简介" name="content">
            <t-textarea v-model="formData.content" placeholder="请简单描述项目内容" clearable/>
          </t-form-item>
          <t-form-item label="类型" name="type">
            <t-radio-group variant="primary-filled" v-model="formData.type">
              <t-radio-button
                  v-for="item in EVENT_TYPE_value_1"
                  :key="item.value"
                  :value="item.value"
              >
                {{ item.label }}
              </t-radio-button>
            </t-radio-group>
          </t-form-item>

          <t-form-item label="海报" name="poster">
            <t-upload
                ref="uploadRef"
                v-model="formData.poster"
                theme="image"
                accept="image/*"
                @fail="handleFail"
                @success="handleSuccess"
                :auto-upload="false"
                :show-image-file-name="true"
                :max="9"
                :abridge-name="[6, 6]"
                :size-limit="{ size: 10, unit: 'MB' }"
                tips="请选择单张图片文件上传,文件最大为10MB"
                :upload-button="null"
                :cancel-upload-button="null"
            >
            </t-upload>
          </t-form-item>
<!--          <t-form-item label="是否可见" name="visible_event">-->
<!--            <t-switch v-model="formData.visible" :label="['是', '否']"></t-switch>-->
<!--          </t-form-item>-->
          <t-form-item label="退换票规则" name="event_policy">
            <t-textarea v-model="formData.event_policy" placeholder="请简单描述项目退换票规则" clearable/>
          </t-form-item>
          <event-session v-model:sessionData="eventSessionData"></event-session>
        </div>

        <div>
          <t-form-item style="margin-top: 30px;display: flex; justify-content: right; align-items: center;">
            <t-space>
              <t-tooltip content="仅重置活动相关信息，场次信息不重置" theme="warning">
                <t-button variant="outline" type="reset">重置</t-button>
              </t-tooltip>
              <t-button theme="success" type="submit">提交</t-button>
            </t-space>
          </t-form-item>
        </div>

      </t-form>
    </div>
  </t-space>
</template>
<script setup>
import {onMounted, ref} from 'vue';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from "axios";
import EventSession from "@/components/event/EventSession.vue";
import router from "@/routers/index.js";
import {EVENT_TYPE_value_1} from "@/constants/index.js";
import {fileServerAxios} from "@/main.js";

// #### 数据 START ##########################################

const token = sessionStorage.getItem('token')
const uid = sessionStorage.getItem('uid')

const FORM_RULES = {
  name: [{required: true, message: '标题必填'}],
  content: [{required: true, message: '简介必填'}],
  type: [{required: true, message: '类型必填'}],
  poster: [{required: true, message: '必须上传海报'}],
};

const session = []
let loading = ref(false);
const uploadRef = ref();
const fileUrl = ref('');
const eventId = sessionStorage.getItem('eventId')
const formData = ref({
  name: '',
  content: '',
  type: 0,
  publisher_id: uid,
  poster: [],
  visible: true,
  event_policy: '',
  eventId: '',
});

// fetchSessionInformation()
const getEventDetail = () => {
  loading.value = true;
  axios.get(`/event/getEventDetail/${eventId}`, {
    headers: {
      token: token
    }
  }).then((response) => {
    formData.value.name = response.data.data.name
    formData.value.content = response.data.data.content
    formData.value.event_policy = response.data.data.event_policy
    formData.value.type = response.data.data.type
    loading.value = false;
  }).catch(() => {
  })
}
getEventDetail()


// #### 数据 END ##########################################

// #### 表单点击操作 START ##########################################

const handleFail = ({file}) => {
  MessagePlugin.error(`文件 ${file.name} 上传失败`);
};
const handleSuccess = (params) => {
  MessagePlugin.success("提交成功");//这个函数是在uploadFiles 成功调用之后才会调用
};

const eventSessionData = ref(session)

const onReset = () => {
  MessagePlugin.success('重置成功');
};
const sendEvent = async () => {
  const poster = formData.value.poster.map(file => file.name)
  eventSessionData.value.forEach((eventSession) => {
    eventSession.additional_information_required = eventSession.additional_information_required.map((information) => {
      return JSON.parse(information);
    });
    if (eventSession.seat_required && eventSession.registration_required) {
      const x = eventSession.seat_map_id.split('.');
      eventSession.seat_map_id = x[x.length - 2]
      console.log(eventSession)
    }else{
      // console.log('before', eventSession.seat_map_id)

      eventSession.seat_map_id = -1;
      // console.log('after', eventSession.seat_map_id)
      if(!eventSession.price_required){
        eventSession.price =0 ;
      }
    }
  })
  console.log(eventSessionData)
  formData.value.eventId = sessionStorage.getItem('eventId')
  await axios.post(`/event/update`, {
        "event": formData.value,
        "sessions": eventSessionData.value,
        "poster": poster
      }, {
        headers: {
          token: token
        }
      }
  ).then(response => {
        console.log(response)
        // MessagePlugin.success('提交成功');
        fileUrl.value = response.data.data.fileToken;
      }
  ).catch();
};
const onSubmit = async ({validateResult, firstError}) => {

  if (validateResult === true) {
    if (eventSessionData.value.length > 0) {
      await sendEvent();
      const formDataUpload = new FormData();
      formData.value.poster.forEach((file) => {
        formDataUpload.append('file', file.raw)
      })
      // console.log(formDataUpload)
      await fileServerAxios.post(`/file/uploadBatch`, formDataUpload,
          {
            headers: {
              'token': fileUrl.value,
              'Content-Type': 'multipart/form-data'
            },
          }).then(response => {
        console.log(JSON.stringify(response));
      }).catch(reason => {
        console.log(JSON.stringify(reason));
      });
      loading.value = false;
      await MessagePlugin.success('提交活动信息成功，等待审核');
      await router.push("/HomePage");
    } else {
      console.log('至少添加一个场次');
      await MessagePlugin.warning('至少添加一个场次');
    }

  } else {
    console.log('Errors: ', validateResult);
    await MessagePlugin.warning(firstError);
  }
};
// #### 表单点击操作 END ##########################################

</script>
<style scoped>

</style>
