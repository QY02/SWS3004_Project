<template>
  <t-alert  style="margin: 20px 20% 0 20%" close >
    <span>1、本页面为测试功能，仅供测试使用，不保证准确性和稳定性。</span>
    <span>2、请勿发送任何个人隐私信息。请注意保护您的模型密钥。</span>
    <span>3、可能因网络波动造成信息丢失，敬请谅解。</span>
    <span>4、模型实际性能取决于第三方提供商，本站不对模型性能负责。</span>
  </t-alert>
  <div style="display: flex; justify-content: center; align-items: center;">
    <div v-loading="loading" style="width: 650px; background-color: #f0f0f0;
                    border-radius: 10px; box-shadow: 0 0 20px #a8b1c9"
         class="card-with-margin">
      <div style="text-align: center; line-height: 50px;font-weight: bold;font-size: large">
        Chat With Event (beta)
      </div>
      <div style="height: 55vh; overflow:auto; border-top: 1px solid #a8b1c9;" v-html="content"></div>
      <div style="padding: 18px 15px;">
        <t-space>
          <t-select
              v-model="chatModel"
              :options="chatModels"
              placeholder="请选择聊天模型"
          ></t-select>
          <t-input
              placeholder="请输入你的API KEY"
              type="password"
              v-model="api_key"
              :tips="apiTips"
              :status="apiTips ? 'error' : ''"
              :onChange="apiValidate"
          />
          <t-button @click="resetChat">开启新的对话</t-button>
          <t-tooltip content="下载聊天记录">
            <t-button shape="circle" theme="primary" @click="downloadTxtFile">
              <template #icon> <file-download-icon /></template>
            </t-button>
          </t-tooltip>
        </t-space>
      </div>
      <div style="position: relative;">
        <t-textarea
            v-model="text"
            placeholder="请输入"
            name="description"
            :autosize="{ minRows: 6, maxRows: 10 }"
        />
        <div style="position: absolute; bottom: 10px; right: 10px;">
          <t-button @click="send" :disabled="disableSend">发送</t-button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {ref, getCurrentInstance, onMounted} from 'vue';
import axios from 'axios';
import {MessagePlugin} from 'tdesign-vue-next';
import {FileDownloadIcon} from 'tdesign-icons-vue-next';
import MarkdownIt from 'markdown-it';

const eventId = sessionStorage.getItem('eventId');
const md = new MarkdownIt();
const renderMarkdown = (text) => {
  return md.render(text);
};

// const user = sessionStorage.getItem("uid") ? sessionStorage.getItem("uid") : '';//当前用户
const user = sessionStorage.getItem("uid") ? sessionStorage.getItem("uid") : '';//当前用户
const chatModel = ref('GPT3.5');//聊天对象
const chatModels = ref([
  {value: 'GPT3.5', label: 'OpenAI GPT-3.5 Turbo'},
  {value: 'GPT4', label: 'OpenAI GPT-4o'},
  {value: 'GOOGLE', label: 'Google Gemini'},
  {value: 'ZHIPU', label: '智谱AI'},
  {value: 'TONGYI', label: '通义千问'},
  {value: 'BAICHUAN', label: '百川智能'},
  // {value: 'WENXIN', label: '百度文心一言'},
  {value: 'TEST', label: '图灵机器人'},
]);
const chatModelLogos = ref([
  {value: 'GPT3.5', label: 'https://img0.baidu.com/it/u=807317164,677012324&fm=253&fmt=auto&app=138&f=PNG?w=500&h=500'},
  {value: 'GPT4', label: 'https://chatgptdemo.ai/wp-content/uploads/2023/08/cropped-chatgptdemo.png'},
  {value: 'GOOGLE', label: 'https://logowik.com/content/uploads/images/google-ai-gemini91216.logowik.com.webp'},
  {value: 'ZHIPU', label: 'https://k.sinaimg.cn/n/spider20230423/226/w750h276/20230423/2525-d42245b5d574b217e01e27328655db81.png/w300h300z1l10t10q100041.jpg'},
  {value: 'TONGYI', label: 'http://img3.downza.cn/capmobile/202401/180751-65ae3e77cab7a.png'},
  {value: 'TEST', label: 'https://img.freepik.com/free-vector/robotic-artificial-intelligence-technology-smart-lerning-from-bigdata_1150-48136.jpg?w=1480'},
  {value: 'BAICHUAN', label: 'https://gbres.dfcfw.com/Files/iimage/20231101/8BFA471C1B992DB77EC75577B3E9D5A1_w900h506.png'},
  {value: 'WENXIN', label: 'https://pp.myapp.com/ma_icon/0/icon_54318686_1713316741/256'},
]);
const api_key = ref('test');
const text = ref("");
const messages = ref([]);
const content = ref('<br>');
const appConfig = ref(getCurrentInstance().appContext.config.globalProperties).value;
const webSocketBaseUrl = appConfig.$webSocketBaseUrl;
let socketUrl = `${webSocketBaseUrl}/LLMserver/` + user;

const disableSend = ref(false);
const loading = ref(false);

const apiTips = ref('');
const apiValidate = () => {
  if (api_key.value==='') {
    apiTips.value = 'API KEY不能为空';
  }else {
    apiTips.value = '';
  }
};


onMounted(async () => {
  await initChat();
});

let socket;

const initChat = async () => {
  try {
    loading.value = false;
    if (socket != null) {
      socket.close();
      socket = null;
    }
    socket = new WebSocket(socketUrl);

    content.value = '<br>';
    messages.value = [];
    disableSend.value = false;

    socket.onopen = function () {
      console.log("websocket已打开");
      createContent(chatModel, null, "你好，我是校园活动与娱乐中心的AI助手。你有什么问题需要帮助吗？")
    };

    socket.onmessage = function (msg) {
      console.log("收到数据====" + msg.data);
      if (msg.data === "USER ALREADY EXISTS") {
        console.log("用户已存在");
        content.value = '<br>';
        initChat();
        return;
      }
      if (msg.data === "对不起，请求失败，请稍后重试") {
        console.log("请求失败");
        disableSend.value = true;
      }
      messages.value.push({role: "ai", content: msg.data});
      createContent(chatModel.value, null, msg.data);
      loading.value = false;
    };

    socket.onclose = function () {
      console.log("websocket已关闭");
    };

    socket.onerror = function () {
      console.log("websocket发生了错误");
    };
  } catch (error) {
    console.error(error);
  }
};

const resetChat = () => {
  loading.value = false;
  content.value = '<br>';
  messages.value = [];
  disableSend.value = false;
  createContent(chatModel, null, "你好，我是校园活动与娱乐中心的AI助手。你有什么问题需要帮助吗？")
};

const send = () => {
  if (!chatModel.value) {
    console.warn("请选择聊天对象");
    return;
  }
  if (!api_key.value) {
    apiTips.value = 'API KEY不能为空';
    console.warn("请输入API KEY");
    return;
  }
  if (!text.value) {
    console.warn("请输入内容");
    MessagePlugin.error("请输入内容");
  } else {
    if (typeof (WebSocket) == "undefined") {
      console.log("您的浏览器不支持WebSocket");
    } else {
      loading.value = true;
      console.log("您的浏览器支持WebSocket");
      let message = {session_ID: user,
        history: messages.value,
        query: text.value,
        LLM_type: chatModel.value,
        api_key: api_key.value,
        tmp_event: eventId};
      socket.send(JSON.stringify(message));
      messages.value.push({role: "user", content: text.value});
      createContent(null, user, text.value);
      text.value = '';
    }
  }
};

const downloadTxtFile = () => {
  const currentTime = new Date().toLocaleString();
  let textContent = "保存时间：" + currentTime + "\n\n" + "聊天记录：\n";

  messages.value.forEach((item) => {
    textContent += item.role === 'ai' ? 'AI: ' : 'USER: ';
    textContent += item.content + "\n";
  });
  const blob = new Blob([textContent], {type: 'text/plain'});
  const a = document.createElement('a');
  const url = window.URL.createObjectURL(blob);
  a.href = url;
  a.download = 'chat_history_' + currentTime + '.txt';
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
};


const createContent = (remoteUser, nowUser, text) => {
  let html;
  text = renderMarkdown(text);
  if (nowUser) {
    html = `<div class="el-row" style="padding: 5px 0">
      <div class="el-col el-col-22" style="text-align: right; padding-right: 10px">
        <div class="tip leftmsg">${text}</div>
      </div>
      <div class="el-col el-col-2">
        <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px;">
          <img src="https://cdn.pixabay.com/photo/2016/11/30/18/14/chat-1873543_1280.png" style="object-fit: cover;" alt="chat">
        </span>
      </div>
    </div>`;
  } else if (remoteUser) {
    html = `<div class="el-row" style="padding: 5px 0">
      <div class="el-col el-col-2" style="text-align: right">
        <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px;">
          <img src="${chatModelLogos.value.find(item => item.value === chatModel.value).label}" style="object-fit: cover;" alt="chat">
        </span>
      </div>
      <div class="el-col el-col-22" style="padding-left: 10px">
        <div class="tip rightmsg">${text}</div>
      </div>
    </div>`;
  }
  content.value += html;
};
</script>

<style>
.tip {
  padding: 0 10px;
  border-radius: 10px;
  display: inline-block;
  font-weight: bold;
}


.rightmsg {
  background-color: #fdfdfd;
  margin-right: 20%;
  text-align: left;
}

.card-with-margin {
  margin: 20px;
}

.leftmsg {
  background-color: #94ea68;
  margin-left: 20%;
  text-align: left;
}
</style>

