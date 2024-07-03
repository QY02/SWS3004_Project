<template>
  <t-loading :loading="loading" text="加载中..." fullscreen/>
  <t-layout>
    <t-aside v-if="list.length>0">
      <t-space :break-line="true" class="card-with-margin scroll-container" align="center"
               :style="{height: 'calc(100vh - 96px)', 'overflow-y': 'scroll' }">
        <t-image
            v-for="item in list"
            :key="item.id"
            :src="item.img"
            :style="{ width: '120px', height: '120px' }"
            fit="cover"
            shape="round"
            :lazy="true"
            class="image-shadow image-with-margin"
            @click="selectMoment(item)"
        >
          <template #overlayContent>
            <Tag
                shape="mark"
                theme="primary"
                variant="light"
                :style="{ position: 'absolute', right: '8px', bottom: '8px', borderRadius: '4px' }"
            >
              @{{ item.name }}
            </Tag>
          </template>
        </t-image>
        <t-button shape="round" variant="outline" :disabled="noMoreImage" class="image-with-margin" @click="loadMore">
          点击加载更多
        </t-button>
      </t-space>
    </t-aside>

    <t-content :style="{ display: 'flex', 'justify-content': 'center'}">
      <div :style="{height: 'calc(100vh - 56px)', width: '1000px', 'overflow-y': 'scroll', 'scrollbar-width': 'none', '-ms-overflow-style': 'none'}">
        <t-space size="small" direction="vertical">
          <t-space class="card-with-margin">
            <t-radio-group variant="default-filled" default-value="1" @change="onTypeChange">
            <t-radio-button value="1">动态</t-radio-button>
            <t-radio-button value="2">我的发布</t-radio-button>
          </t-radio-group>
          <t-button theme="default" variant="outline" @click="showMsg">我的消息</t-button>
        </t-space>
        <t-alert v-if="list.length===0 && radioGroupValue==='2'" class="card-with-margin" theme="info" style="width:600px"
                 title="您还没有发送过动态" message="欢迎分享您的感受">
          <template #operation>
            <span @click="router.push('/newMoment');">新增动态</span>
          </template>
        </t-alert>
        <t-card v-if="list.length>0" class="card-with-margin" hoverShadow ref="cardRef">
        <t-space>
          <t-button variant="outline" theme="success" @click="showEvent">点击跳转相关活动：{{momentData.relatedEvent}}</t-button>
<!--          <t-button v-if="radioGroupValue === '2'" @click="editPost">-->
<!--            <template #icon>-->
<!--              <edit-icon/>-->
<!--            </template>-->
<!--            编辑-->
<!--          </t-button>-->
          <t-button v-if="radioGroupValue === '2'" @click="deletePost" theme="danger">
            <template #icon>
              <delete-icon/>
            </template>
            删除
          </t-button>
        </t-space>
        <h1>
        {{momentData.title}}
        </h1>
        <t-comment :author="momentData.userName" :datetime="momentData.publishDate.replace('T',' ')"
                   :content="momentData.content" style=" width:900px;white-space: pre-wrap;word-wrap: anywhere;">

          <template #avatar>
            <t-avatar v-if="momentData.publisherId===user" size="60px" :image="momentData.avatar"/>
            <t-popconfirm v-if="momentData.publisherId!==user" content="与ta聊天" :cancel-btn="null" @confirm="chat(momentData.publisherId,momentData.userName)">
              <t-avatar size="60px" :image="momentData.avatar"/>
            </t-popconfirm>
          </template>
          <template #actions>
            <t-space key="thumbUp" :size="10" @click="thumbUp">
              <t-icon name="thumb-up" :color="thumbUpColor"/>
              <span>{{momentData.upVote}}</span>
            </t-space>
            <t-space key="thumbDown" :size="10" @click="thumbDown">
              <t-icon name="thumb-down" :color="thumbDownColor"/>
              <span>{{momentData.downVote}}</span>
            </t-space>
            <t-space key="chat" :size="10" @click="viewComment">
              <t-icon name="chat"/>
              <span>评论</span>
            </t-space>
          </template>
        </t-comment>
          <t-divider />
        <video v-if="momentData.mediaType===true" :src="video" controls :style="{ width: 900 + 'px', height: 600+ 'px' }"/>
        <t-swiper v-if="momentData.mediaType===false"
            class="tdesign-demo-block--swiper"
            :autoplay="false"
            style="width:900px"
        >
          <t-swiper-item v-for="item in photoList" :key="0">
            <t-image-viewer :key="0" v-model:visible="photoPreviewVisible" :current="currentPic" :images="photoUrlList">
              <template #trigger>
            <t-image @click="photoPreviewVisible = true"
                     :style="{height:600+'px'}"
                :src=item
                fit="cover"
                shape="round"
            />
              </template>
            </t-image-viewer>
          </t-swiper-item>
        </t-swiper>
      </t-card>
       </t-space>
      </div>
    </t-content>
  </t-layout>
  <t-popup content="发布动态">
    <t-button shape="circle" theme="primary" size="large" style="z-index:100;position: fixed;right: 30px;bottom: 40px"
              @click="router.push('/newMoment');">
      <template #icon>
        <add-icon/>
      </template>
    </t-button>
  </t-popup>
  <t-drawer v-model:visible="commentVisible" header="评论区" :confirm-btn="null" :cancel-btn="null" size="42vw">
    <t-list :split="true" v-loading="commentLoading">
      <t-list-item v-for="(item, index) in commentsData" :key="index">
        <template #content>
          <t-comment style="margin-bottom: 5px;white-space: pre-wrap;word-wrap: anywhere;" :avatar="item.avatar" :author="item.author" :datetime="item.publishDate.replace('T',' ')"
                     :content="item.content">
            <template #actions>
              <t-space v-if="item.publisherId===user" key="delete" :size="6" @click="deleteComment(item)" style="color:red">
                <t-icon name="delete" />
                <span>删除</span>
              </t-space>
            </template>
          </t-comment>
        </template>
      </t-list-item>
    </t-list>
    <div style="background-color: #ffffff ;position: fixed;bottom:0;width: 40vw ;height: auto">
      <t-divider/>
      <t-comment>
        <template #content>
          <div class="form-container">
            <t-textarea v-model="replyData" placeholder="请输入内容"/>
            <t-button class="form-submit" @click="submitReply">发送</t-button>
          </div>
        </template>
      </t-comment>
    </div>
  </t-drawer>
  <t-dialog
      v-model:visible="deleteVisible"
      theme="danger"
      header="确认删除动态"
      :on-close="closeDelete"
      @confirm="onDeleteClickConfirm"
  />
  <t-dialog
      width="300px"
      showOverlay
      preventScrollThrough
      header="我的消息"
      destroyOnClose
      :footer="false"
      v-model:visible="msgVisible"
  >
    <t-list :split="true">
      <t-list-item v-for="item in unreadUser" :key="item.id">
        <t-list-item-meta :image="avatarList[item.iconId]" :title="item.name"/>
        <template #action>
          <t-button variant="text" shape="square" @click="chat(item.id,item.name)">
            <t-badge dot :count="2">
            <chat-icon/>
            </t-badge>
          </t-button>
        </template>
      </t-list-item>
      <t-list-item v-for="item in readUser" :key="item.id">
        <t-list-item-meta :image="avatarList[item.iconId]" :title="item.name"/>
        <template #action>
          <t-button variant="text" shape="square" @click="chat(item.id,item.name)">
            <chat-icon/>
          </t-button>
        </template>
      </t-list-item>
    </t-list>
  </t-dialog>
</template>

<script setup lang="jsx">
import {MessagePlugin, Tag} from 'tdesign-vue-next';
import {AddIcon, ChatIcon, DeleteIcon} from "tdesign-icons-vue-next";
import {onMounted, ref} from 'vue';
import router from "@/routers/index.js";
import axios from "axios";
import {fileServerAxios} from "@/main.js"
import {avatarList} from "@/constants/index.js"

const user = sessionStorage.getItem("uid") ? sessionStorage.getItem("uid") : '';//当前用户




// ###### 动态列表 开始 ######

const list = ref([]);// 左侧动态列表
const lastId = ref(-1);// 上一次请求的最后一个动态的id
const noMoreImage = ref(false);// 是否还有更多图片
const loading = ref(false);

const getMomentBatch = async (id) => {
  try {
    loading.value = true;
    const response = await axios.get(`/comment/getMomentBatch/${id}/${radioGroupValue.value}`, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    if (response.data.data.length <= 20) {
      noMoreImage.value = true;
    }
    for (let i = 0; i < response.data.data.length; i++) {
      const fileServerResponse = await fileServerAxios.get(`/file/download`, {
        responseType: 'blob',
        headers: {
          token: response.data.data[i].attachment,
        }
      });
      const image = fileServerResponse.data;
      list.value.push({
        id: response.data.data[i].comment_id,
        img: image,
        name: response.data.data[i].publisher_id,
      });
    }
    if (list.value.length > 0) {
      lastId.value = response.data.data[response.data.data.length - 1].comment_id;
    }
    loading.value = false;
  } catch (error) {
  }
};

onMounted(async () => {
  await getUnreadMsg();
  await getMomentBatch(-1);
  await selectMoment(list.value[0]);
});

const loadMore = async () => {
  await getMomentBatch(lastId.value);
};

// ###### 动态列表 结束 ######

// ###### 消息 开始 ######

const unreadUser = ref([]);
const readUser = ref([]);

const getUnreadMsg = async () => {
  try {
    unreadUser.value = [];
    readUser.value = [];
    const response = await axios.get(`/chatMessage/getUnread`, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
      unreadUser.value = response.data.data.unread;
      readUser.value = response.data.data.read;
  } catch (error) {
  }
};

// ###### 消息 结束 ######


// ###### 动态详情 开始 ######

const cardRef = ref(null);
const currentPic = ref(0);

const momentData = ref({
  id: 'A',
  avatar: 'https://tdesign.gtimg.com/site/avatar.jpg',
  title: '示例标题',
  userName: '示例评论作者',
  publisherId: '示例评论作者ID',
  publishDate: '示例时间',
  content: '示例评论内容。',
  relatedEvent: '示例活动名称',
  eventId: '示例活动ID',
  upVote: 0,
  downVote: 0,
  mediaType: 0,
  mediaUrl: [],
});

const video = ref('');
const photoList = ref([]);
const photoUrlList = ref([]);
const photoPreviewVisible = ref(false);

const showEvent = () => {
  sessionStorage.setItem('eventId', momentData.value.eventId);
  router.push('/event');
};

const selectMoment = async (item) => {
  try {
    currentPic.value = 0;
    loading.value = true;
    photoList.value = [];
    photoUrlList.value = [];
    video.value = '';
    photoPreviewVisible.value = false;
    const response = await axios.get(`/comment/getMomentById?commentId=${item.id}`, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    });
    response.data.data.avatar = avatarList[response.data.data.avatar];
    momentData.value = response.data.data;
    const response2 = await axios.get(`/blog/get/${item.id}`, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    });
    thumbUpColor.value = response2.data.data.voteType === 1 ? 'red' : 'grey';
    thumbDownColor.value = response2.data.data.voteType === -1 ? 'blue' : 'grey';
    if (momentData.value.mediaType === false) {
      for (let i = 0; i < momentData.value.mediaUrl.length; i++) {
        const fileServerResponse = await fileServerAxios.get(`/file/download`, {
          responseType: 'blob',
          headers: {
            token: momentData.value.mediaUrl[i],
          }
        });
        const image = fileServerResponse.data;
        photoList.value.push(image);
        photoUrlList.value.push(URL.createObjectURL(image));
      }
    } else {
      const fileServerResponse = await fileServerAxios.get(`/file/download`, {
        responseType: 'blob',
        headers: {
          token: momentData.value.mediaUrl[0],
        }
      });
      video.value = URL.createObjectURL(fileServerResponse.data);
    }
    loading.value = false;
  } catch (error) {
  }
};

let radioGroupValue = ref('1');// 1: 动态 2: 我的发布
const onTypeChange = async (checkedValues) => {
  radioGroupValue.value = checkedValues;
  list.value = [];
  lastId.value = -1;
  noMoreImage.value = false;
  await getMomentBatch(-1);
  if (list.value.length > 0) {
    await selectMoment(list.value[0]);
  }
};

const editPost = () => {
  console.log('Edit post');
};

const deleteVisible = ref(false);

const onDeleteClickConfirm = async () => {
  try {
    loading.value = true;
    await axios.delete(`/comment/deleteMoment/${momentData.value.id}`, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    list.value = [];
    lastId.value = -1;
    noMoreImage.value = false;
    await getMomentBatch(-1);
    await selectMoment(list.value[0]);
    loading.value = false;
    deleteVisible.value = false;
    await MessagePlugin.success('删除成功');
  } catch (error) {
  }
};

const deletePost = () => {
  deleteVisible.value = true;
};

const closeDelete = () => {
  deleteVisible.value = false;
};

const msgVisible = ref(false);

const showMsg = () => {
  msgVisible.value = true;
};

const chat = (id, name) => {
  sessionStorage.setItem('chatUserId', id);
  sessionStorage.setItem('chatUserName', name);
  router.push('/chat');
};

// ###### 动态详情 结束 ######

// ###### 点赞 开始 ######

const thumbUpColor = ref('grey');
const thumbDownColor = ref('grey');

const thumbUp = async () => {
  try {
    loading.value = true;
    if (thumbUpColor.value === 'grey') {
      thumbUpColor.value = 'red';
      await axios.get(`/blog/change/${momentData.value.id}/${user}/1`, {
        headers: {
          token: sessionStorage.getItem('token'),
        }
      });
      if (thumbDownColor.value === 'blue') {
        thumbDownColor.value = 'grey';
        momentData.value.downVote--;
      }
      momentData.value.upVote++;
    } else {
      thumbUpColor.value = 'grey';
      await axios.get(`/blog/change/${momentData.value.id}/${user}/0`, {
        headers: {
          token: sessionStorage.getItem('token'),
        }
      });
      momentData.value.upVote--;
    }
    loading.value = false;
  } catch (error) {
  }
};

const thumbDown = async () => {
  try {
    loading.value = true;
    if (thumbDownColor.value === 'grey') {
      thumbDownColor.value = 'blue';
      await axios.get(`/blog/change/${momentData.value.id}/${user}/-1`, {
        headers: {
          token: sessionStorage.getItem('token'),
        }
      });
      if (thumbUpColor.value === 'red') {
        thumbUpColor.value = 'grey';
        momentData.value.upVote--;
      }
      momentData.value.downVote++;
    } else {
      thumbDownColor.value = 'grey';
      await axios.get(`/blog/change/${momentData.value.id}/${user}/0`, {
        headers: {
          token: sessionStorage.getItem('token'),
        }
      });
      momentData.value.downVote--;
    }
    loading.value = false;
  } catch (error) {
  }
};

// ###### 点赞 结束 ######

// ###### 评论区 开始 ######
// 评论区是否可见
const commentVisible = ref(false);
const commentLoading = ref(false);

const viewComment = async () => {
  try {
    const response = await axios.get(`/reply/getByComment/${momentData.value.id}`, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    response.data.data.forEach((item) => {
      item.avatar = avatarList[item.avatar];
    });
    commentsData.value = response.data.data;
    commentVisible.value = true;
  } catch (error) {
  }
};

const deleteComment = async (item) => {
  try {
    commentLoading.value = true;
    await axios.delete(`/reply/delete/${item.id}`, {
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    await viewComment();
    commentLoading.value = false;
    MessagePlugin.success('删除成功');
  } catch (error) {
  }
};

const commentsData = ref([
  {
    id: 'A',
    avatar: 'https://tdesign.gtimg.com/site/avatar.jpg',
    publisherId: '评论作者ID',
    author: '评论作者名A',
    publishDate: '今天16:38',
    content: '评论作者名A写的评论内容。',
  },
]);


const replyData = ref('');
const submitReply = async () => {
  if (replyData.value === '') {
    MessagePlugin.error('请输入内容');
    return;
  }
  commentLoading.value = true;
  await axios.post(`/reply/add`, {
    commentId: momentData.value.id,
    publisherId: user,
    content: replyData.value,
    publishDate: new Date(),
    upVote: 0,
    downVote: 0,
  }, {
    headers: {
      token: sessionStorage.getItem('token'),
    }
  }).catch();
  replyData.value = '';
  await viewComment();
  commentLoading.value = false;
};

// ###### 评论区 结束 ######

</script>


<style scoped>

.card-with-margin {
  margin: 10px 20px;
  height: max-content;
  display: flex;
}

.image-with-margin {
  margin: 5px 10px;
  height: max-content;
  display: flex;
}

.spacing {
  height: 30px; /* 调整这个值以改变间距大小 */
}

.image-shadow {
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.5);
  transition: box-shadow 0.3s ease;
}

.image-shadow:hover {
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.8);
}

.form-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-bottom: 20px;

  .form-submit {
    margin-top: 8px;
  }
}

h1 {
  font-size: 24px;
  font-weight: bold;
  margin: 40px;
}

&::-webkit-scrollbar {
 display: none;
}
</style>
