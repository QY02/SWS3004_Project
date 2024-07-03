<template>
  <div style="display: flex; justify-content: center; align-items: center;">
    <div style="width: 650px; background-color: #f0f0f0;
                    border-radius: 10px; box-shadow: 0 0 20px #a8b1c9"
         class="card-with-margin">
      <div style="text-align: center; line-height: 50px;font-weight: bold;font-size: large">
        {{ chatPartnerName }}
      </div>
      <div style="height: 55vh; overflow:auto; border-top: 1px solid #a8b1c9;" v-html="content"></div>
      <div style="position: relative;">
        <t-textarea
            v-model="text"
            placeholder="请输入"
            name="description"
            :autosize="{ minRows: 6, maxRows: 10 }"
        />
        <div style="position: absolute; bottom: 10px; right: 10px;">
          <t-button @click="send">发送</t-button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import {ref, getCurrentInstance, onMounted} from 'vue';
import axios from 'axios';
import {MessagePlugin} from 'tdesign-vue-next';
import MarkdownIt from 'markdown-it';
import {avatarList} from '@/constants/index.js';


const md = new MarkdownIt();
const renderMarkdown = (text) => {
  return md.render(text);
};

const user = sessionStorage.getItem("uid") ? sessionStorage.getItem("uid") : ''; //当前用户
const chatPartner = ref(sessionStorage.getItem("chatUserId")); //聊天对象
const chatPartnerName = sessionStorage.getItem("chatUserName");
const chatPartnerAvatar = ref(null);
const userAvatar = ref(null);

const text = ref("");
const historyMessages = ref([]);
const content = ref('<br>');
const appConfig = ref(getCurrentInstance().appContext.config.globalProperties).value;
const webSocketBaseUrl = appConfig.$webSocketBaseUrl;
let socketUrl = `${webSocketBaseUrl}/chatserver/` + user;


onMounted(async () => {
  await initChat();
});

let socket;

const getHistoryMessage = async () => {
  try {
    await axios.post(`/chatMessage/get/${chatPartner.value}`, {},
        {
          headers: {
            'token': sessionStorage.getItem('token'),
          },
        })
        .then(response => {
          historyMessages.value = response.data.data.message;
          chatPartnerAvatar.value = avatarList[response.data.data.avatarMap[chatPartner.value]];
          userAvatar.value = avatarList[response.data.data.avatarMap[user]];
          for (let i = 0; i < historyMessages.value.length; i++) {
            if (historyMessages.value[i]['senderId'] !== chatPartner.value) {
              createContent(null, historyMessages.value[i]['senderName'], historyMessages.value[i]['content']);
            } else {
              createContent(historyMessages.value[i]['receiverName'], null, historyMessages.value[i]['content']);
            }
          }
        })
        .catch(() => {
          // Handle error here
        });
  } catch (error) {
    console.error(error);
  }
};

const initChat = async () => {
  try {

    if (socket != null) {
      socket.close();
      socket = null;
    }
    socket = new WebSocket(socketUrl);

    content.value = '<br>';

    socket.onopen = function () {
      content.value = '<br>';
      console.log("websocket已打开");
      getHistoryMessage();
    };

    socket.onmessage = function (msg) {
      console.log("收到数据====" + msg.data);
      if (msg.data === "USER ALREADY EXISTS") {
        // alert("用户已存在");
        // content.value = '<br>';
        // initChat();
        return;
      }
      createContent(chatPartner.value, null, JSON.parse(msg.data).text);
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


const send = () => {
  if (!chatPartner.value) {
    console.warn("请选择聊天对象");
    return;
  }
  if (!text.value) {
    console.warn("请输入内容");
    MessagePlugin.error("请输入内容");
  } else {
    if (typeof (WebSocket) == "undefined") {
      console.log("您的浏览器不支持WebSocket");
    } else {
      console.log("您的浏览器支持WebSocket");
      // {"from": "zhang", "to": "admin", "text": "聊天文本"}
      let message = {from: user, to: chatPartner.value, text: text.value}
      socket.send(JSON.stringify(message));
      createContent(null, user, text.value);
      text.value = '';
    }
  }
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
          <img src="${userAvatar.value}" style="object-fit: cover;" alt="chat">
        </span>
      </div>
    </div>`;
  } else if (remoteUser) {
    html = `<div class="el-row" style="padding: 5px 0">
      <div class="el-col el-col-2" style="text-align: right">
        <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px;">
          <img src="${chatPartnerAvatar.value}" style="object-fit: cover;" alt="chat">
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

