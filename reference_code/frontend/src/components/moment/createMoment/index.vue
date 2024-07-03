<template>
  <t-form
      v-loading="loading"
      ref="form"
      class="base-form"
      :data="formData"
      :rules="FORM_RULES"
      label-align="top"
      :label-width="100"
      @reset="onReset"
      @submit="onSubmit"
  >
    <div class="form-basic-container">
      <div class="form-basic-item">
        <div class="form-basic-container-title"> 新建动态</div>
        <t-form-item label="标题" name="name" @keydown.enter.prevent>
          <t-input v-model="formData.name" placeholder="请输入内容"/>
        </t-form-item>
        <t-form-item label="内容" name="comment">
          <t-textarea v-model="formData.comment" :height="200" placeholder="请输入内容"/>
        </t-form-item>
        <t-form-item label="相关活动 (活动号-活动名)" name="event" @keydown.enter.prevent>
          <t-select-input
              :value="selectValue"
              :popup-visible="popupVisible"
              :popup-props="{ overlayInnerStyle: { padding: '6px' } }"
              placeholder="请选择活动"
              clearable
              allow-input
              @popup-visible-change="onPopupVisibleChange"
              @clear="onClear"
              @input-change="onInputChange"
          >
            <template #panel>
              <ul class="tdesign-demo__select-input-ul-single">
                <li v-for="item in events" :key="item.value" @click="() => onOptionClick(item)">
                  {{ item.label }}
                </li>
              </ul>
            </template>
            <template #suffixIcon>
              <chevron-down-icon/>
            </template>
          </t-select-input>
        </t-form-item>
        <t-form-item label="媒体类型" name="type">
          <t-radio-group default-value="1" @change="changeType">
            <t-radio-button value="1">图片</t-radio-button>
            <t-radio-button value="2">视频</t-radio-button>
          </t-radio-group>
        </t-form-item>
        <t-upload
            ref="uploadRef"
            v-if="mediaType===1"
            v-model="formData.files"
            placeholder="支持批量上传图片文件"
            theme="image-flow"
            accept="image/*"
            multiple
            @success="onSuccessUpload"
            :auto-upload="false"
            :show-image-file-name="true"
            :max="9"
            :abridge-name="[6, 6]"
            :upload-button="null"
            :cancel-upload-button="null"
        >
        </t-upload>
        <t-space>
          <t-upload
              v-if="mediaType===2"
              v-model="formData.files"
              placeholder="仅能上传一个视频mp4文件"
              :auto-upload="false"
              accept=".mp4"
              @success="onSuccessUpload"
              :abridge-name="[10, 8]"
              :upload-button="null"
          />
          <t-upload
              v-if="mediaType===2"
              v-model="videoCover"
              placeholder="上传视频封面"
              :auto-upload="false"
              accept="image/*"
              @success="onSuccessUpload"
              :abridge-name="[10, 8]"
              :upload-button="null"
          />
        </t-space>
      </div>
    </div>

    <div class="form-submit-container">
      <div class="form-submit-sub">
        <div class="form-submit-left">

          <t-button type="reset" style="margin: 5px" theme="default" variant="base">
            取消
          </t-button>
          <t-button theme="success" style="margin: 5px" type="submit">
            提交
          </t-button>
        </div>
      </div>
    </div>
  </t-form>
</template>


<script setup lang="ts">
import {MessagePlugin, SubmitContext, UploadProps, SelectInputProps} from 'tdesign-vue-next';
import {onMounted, ref} from 'vue';
import {FORM_RULES, INITIAL_DATA} from './constants';
import router from "@/routers/index.js";
import {ChevronDownIcon} from 'tdesign-icons-vue-next';
import axios, {AxiosRequestConfig} from "axios";
import {fileServerAxios} from "@/main.js"

// ###### 表单整体行为 开始 ######
const formData = ref({...INITIAL_DATA});
const mediaType = ref(1);// 1: 图片 2: 视频

const changeType = (value: string) => {
  formData.value.files = [];
  mediaType.value = Number(value);
  videoCover.value = [];
};

const onReset = () => {
  router.push('/moments');
};

const fileUrl = ref('');
// 视频封面
const videoCover = ref([]);

const onSubmit = async (ctx: SubmitContext) => {
  if (!allEvents.some(event => event.value === Number(formData.value.event.value))) {
    await MessagePlugin.error('请选择一个有效的活动');
    return;
  }
  if (formData.value.files.length === 0) {
    await MessagePlugin.error('请上传文件');
    return;
  }
  if (mediaType.value === 2 && videoCover.value.length === 0) {
    await MessagePlugin.error('请上传视频封面');
    return;
  }
  if (ctx.validateResult === true) {
    loading.value = true;
    if (mediaType.value === 2) {
      formData.value.files.push(videoCover.value[0]);
    }
    await sendEvent();
    const formDataUpload = new FormData();
    formData.value.files.forEach((file) => {
      formDataUpload.append('file', file.raw)
    })
    await fileServerAxios.post(`/file/uploadBatch`, formDataUpload,
        {
          headers: {
            'token':
            fileUrl.value,
            'Content-Type': 'multipart/form-data'
          },
        })
        .then(response => {
          // console.log(JSON.stringify(response));
        })
        .catch(reason => {
          console.log(JSON.stringify(reason));
        });
    loading.value = false;
    await MessagePlugin.success('提交成功');
    await router.push('/moments');
  }
};

const sendEvent = async () => {
  await axios.post(`/comment/createMoment`, {
    title: formData.value.name,
    content: formData.value.comment,
    eventId: formData.value.event.value,
    files: formData.value.files.map(file => file.name),
    type: mediaType.value,
  }, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  } as AxiosRequestConfig)
      .then(response => {
        fileUrl.value = response.data.data.fileToken;
      })
      .catch();
};

// ###### 表单整体行为 结束 ######

// ###### 上传文件辅助 开始 ######


const onSuccessUpload = (res: any) => {
  console.log(res);
};

function getCurrentDate(needTime = false) {
  const d = new Date();
  let month = d.getMonth() + 1;
  month = month < 10 ? Number(`0${month}`) : month;
  const date = `${d.getFullYear()}-${month}-${d.getDate()}`;
  const time = `${d.getHours()}:${d.getMinutes()}:${d.getSeconds()}`;
  if (needTime) return [date, time].join(' ');
  return date;
}

// res.url 图片地址；res.uploadTime 文件上传时间；res.error 上传失败的原因
function formatResponse(res: any) {
  // 响应结果添加上传时间字段，用于 UI 显示
  res.uploadTime = getCurrentDate();
  return res;
}

// ###### 上传文件辅助 结束 ######


// ###### 选择活动 开始 ######

let loading = ref(false);
const allEvents = [];
let events = ref([]);

onMounted(() => {
  loading.value = true;
  formData.value.files = [];
  videoCover.value = [];
  axios.post(`/event/getAllEvents`, {}, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  } as AxiosRequestConfig)
      .then(response => {
        allEvents.push(...response.data.data.map((item: any) => ({
          label: item.id+"-"+item.name,
          value: item.id,
        })));
        loading.value = false;
      })
      .catch();
});
const selectValue = ref<{
  label: string;
  value: number;
}>();
if (sessionStorage.getItem('MomentName') != null) {
  // alert(sessionStorage.getItem('MomentName'))
  // alert(Number(sessionStorage.getItem('eventId')))
  selectValue.value={ label: sessionStorage.getItem('eventId')+"-"+sessionStorage.getItem('MomentName'), value: Number(sessionStorage.getItem('eventId'))}
  formData.value.event.value= sessionStorage.getItem('eventId')
  sessionStorage.removeItem('MomentName')
  sessionStorage.removeItem('eventId')
}
const popupVisible = ref(false);
const onOptionClick = (item: { label: string; value: number }) => {
  selectValue.value = item;
  formData.value.event = item as any;
  // 选中后立即关闭浮层
  popupVisible.value = false;
};
const onClear: SelectInputProps['onClear'] = () => {
  selectValue.value = undefined;
  formData.value.event = undefined;
};
const onPopupVisibleChange: SelectInputProps['onPopupVisibleChange'] = (val, context) => {
  popupVisible.value = val;
};
const onInputChange: SelectInputProps['onInputChange'] = (val, context) => {
  if (!val) {
    events.value = [];
    return;
  }
  events.value = allEvents.filter(event => event.label.includes(val));
};

// ###### 选择活动 结束 ######


</script>

<style lang="less" scoped>
@import './index.less';
</style>
