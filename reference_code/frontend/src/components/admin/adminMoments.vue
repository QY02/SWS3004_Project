<template>
  <t-loading :loading="loading" text="加载中..." fullscreen />
  <t-layout>
    <t-aside>
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
      <t-card class="card-with-margin" hoverShadow>
        <t-space>
          <t-button @click="deletePost" theme="danger">
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
            <t-avatar size="60px" :image="momentData.avatar"/>
<!--            <t-popconfirm v-if="momentData.publisherId!==user" content="与ta聊天" :cancel-btn="null" @confirm="chat(momentData.publisherId,momentData.userName)">-->
<!--              <t-avatar size="60px" :image="momentData.avatar"/>-->
<!--            </t-popconfirm>-->
          </template>
          <template #actions>
            <t-space key="thumbUp" :size="10">
              <t-icon name="thumb-up" color="grey"/>
              <span>{{momentData.upVote}}</span>
            </t-space>
            <t-space key="thumbDown" :size="10">
              <t-icon name="thumb-down" color="grey"/>
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
  <t-drawer v-model:visible="commentVisible" header="评论区" :confirm-btn="null" :cancel-btn="null" size="42vw">
    <t-list :split="true" v-loading="commentLoading">
      <t-list-item v-for="(item, index) in commentsData" :key="index">
        <template #content>
          <t-comment style="margin-bottom: 5px;white-space: pre-wrap;word-wrap: anywhere;" :avatar="item.avatar" :author="item.author" :datetime="item.publishDate.replace('T',' ')"
                     :content="item.content">
            <template #actions>
              <t-space key="delete" :size="6" @click="deleteComment(item)" style="color: red">
                <t-icon name="delete" />
                <span>删除</span>
              </t-space>
            </template>
          </t-comment>
        </template>
      </t-list-item>
    </t-list>
  </t-drawer>
  <t-dialog
      v-model:visible="deleteVisible"
      theme="danger"
      header="删除违规动态"
      :on-close="closeDelete"
      :cancel-btn="null"
      :confirm-btn="null"
      @confirm="onDeleteClickConfirm"
  >
    <t-input placeholder="请输入删除原因"
             clearable
             :tips="deleteTips"
             :status="deleteTips ? 'error' : ''"
             @change="onDeleteChange"/>
    <template #footer>
      <t-button @click="closeDelete" theme="default">取消</t-button>
      <t-button theme="danger" @click="onDeleteClickConfirm">确定</t-button>
    </template>
  </t-dialog>
  <t-dialog
      v-model:visible="commentDeleteVisible"
      theme="danger"
      header="删除违规评论"
      :on-close="closeCommentDelete"
      :cancel-btn="null"
      :confirm-btn="null"
      @confirm="onCommentDeleteClickConfirm"
  >
    <t-input placeholder="请输入删除原因"
             clearable
             :tips="commentDeleteTips"
             :status="commentDeleteTips ? 'error' : ''"
             @change="onCommentDeleteChange"/>
    <template #footer>
      <t-button @click="closeCommentDelete" theme="default">取消</t-button>
      <t-button theme="danger" @click="onCommentDeleteClickConfirm">确定</t-button>
    </template>
  </t-dialog>
</template>

<script setup lang="jsx">
import {Tag} from 'tdesign-vue-next';
import {DeleteIcon} from "tdesign-icons-vue-next";
import {onMounted, ref} from 'vue';
import router from "@/routers/index.js";
import axios from "axios";
import { fileServerAxios } from "@/main.js"
import {MessagePlugin} from "tdesign-vue-next";
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
    const response = await axios.get(`/comment/getMomentBatch/${id}/1`, {
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

onMounted(async() => {
  await getMomentBatch(-1);
  await selectMoment(list.value[0]);
});

const loadMore = async () => {
  await getMomentBatch(lastId.value);
};

// ###### 动态列表 结束 ######

// ###### 动态详情 开始 ######

const cardRef = ref(null);

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
  sessionStorage.setItem('eventId',momentData.value.eventId);
  router.push('/event');
};

const selectMoment = async (item) => {
  try {
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
    }else {
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

const deleteVisible = ref(false);

const onDeleteClickConfirm = async () => {
  try {
    if (deleteTips.value === '请输入删除原因') {
      return;
    }
    loading.value = true;
    await axios.post(`/comment/deleteMomentByAdmin`, {
      momentId: momentData.value.id,
      deleteReason: deleteReason,
    },{
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

const deleteTips = ref('请输入删除原因');
let deleteReason = ref('');

const onDeleteChange = (value) => {
  deleteTips.value = value ? '' : '请输入删除原因';
  deleteReason = value;
};

const chat = (id,name) => {
  sessionStorage.setItem('chatUserId',id);
  sessionStorage.setItem('chatUserName',name);
  router.push('/chat');
};

// ###### 动态详情 结束 ######

// ###### 评论区 开始 ######
// 评论区是否可见
const commentVisible = ref(false);
const commentLoading = ref(false);

const commentDeleteVisible = ref(false);
const commentDeleteTips = ref('请输入删除原因');
let commentDeleteReason = ref('');
let deleteCommentId = ref('');

const onCommentDeleteClickConfirm = async () => {
  try {
    if (commentDeleteTips.value === '请输入删除原因') {
      return;
    }
    commentLoading.value = true;
    await axios.post(`/reply/deleteByAdmin`, {
      replyId: deleteCommentId.value,
      deleteReason: commentDeleteReason,
    },{
      headers: {
        token: sessionStorage.getItem('token'),
      }
    });
    await viewComment();
    commentLoading.value = false;
    commentDeleteVisible.value = false;
    deleteCommentId.value = '';
    await MessagePlugin.success('删除成功');
  } catch (error) {
  }
};

const closeCommentDelete = () => {
  commentDeleteVisible.value = false;
};

const onCommentDeleteChange = (value) => {
  commentDeleteTips.value = value ? '' : '请输入删除原因';
  commentDeleteReason = value;
};

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
  deleteCommentId.value = item.id;
  commentDeleteVisible.value = true;
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

h1 {
  font-size: 24px;
  font-weight: bold;
  margin: 40px;
}

&::-webkit-scrollbar {
  display: none;
}
</style>
