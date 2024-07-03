<template>
    <t-space style="display: flex; width: 100%;">
        <div>
            <div class="title">评论</div>
            <div class="line"></div>
        </div>
        <div style="margin-top: 30px; display: flex; justify-content: flex-end; margin-right:30px ;">
            <t-button @click="setVisible">评论</t-button>
        </div>
    </t-space>
    <div v-show="commentsData.length>0">
        <t-list :split="true">
            <t-list-item v-for="(item, index) in commentsData" :key="index">
                <template #content>
                    <div class="comment_card">
                        <t-space style="display: flex; align-items: baseline;">
                            <h2>{{ item.title }}</h2>
                            <div class="stars"><t-rate :default-value="item.score" allow-half disabled size="16" />
                            </div>
                          <div v-if="userId === item.publisherId"
                                style="margin-top: 30px; display: flex; justify-content: flex-end; margin-right:30px ;">
                                <t-tooltip content="删除该评论">
                                    <t-button theme="danger" @click="visibleDelete = true; deleteEventId = item.id"
                                        shape="square" variant="text">
                                        <template #icon>
                                            <DeleteIcon />
                                        </template>
                                    </t-button>
                                </t-tooltip>
                            </div>
                        </t-space>
                        <div class="comment_infos">
                            <p class="date-time">
                                {{`${dateToString(item.publishDate)}`}}
                            </p>
                            <p class="description">
                                {{ item.content }}
                            </p>
                        </div>
                      <t-space style="display: flex;width: fit-content;align-self:flex-end;">
                            <div class="author">
                                — {{ item.publisherId }} {{ item.publisherNames }}
                            </div>
                        </t-space>
                    </div>
                </template>
            </t-list-item>
        </t-list>
    </div>
    <div v-show="commentsData.length===0">
      <div style="display: flex; justify-content: center; align-items: center; flex-direction: column">
        <img src="https://tdesign.gtimg.com/pro-template/personal/nothing.png" alt="空"/>
        <p>{{ '暂无评论' }}</p>
      </div>
    </div>
    <div style="height: 40px;"></div>



    <t-dialog v-model:visible="visibleComment" attach="body" header="评论" destroy-on-close:true width="500px" placement="center"
        :confirm-btn="null" :cancel-btn="null">
        <template #body>
            <t-form ref="form" :rules="FORM_RULES" :data="commentForm" :colon="true" @reset="onReset"
                @submit="onSubmit">
                <t-form-item label="标题" name="title">
                    <t-input v-model="commentForm.title" placeholder="请输入标题" @enter="onEnter"></t-input>
                </t-form-item>
                <t-form-item label="评分" name="score">
                    <t-rate allowHalf v-model="commentForm.score"></t-rate>
                </t-form-item>
                <t-form-item label="内容" name="content">
                    <t-textarea v-model="commentForm.content" placeholder="请输入内容" :maxcharacter="200"
                        @enter="onEnter"></t-textarea>
                </t-form-item>
                <t-form-item>
                    <t-space size="medium">
                      <t-button theme="default" variant="base" @click="visibleComment = false"
                                :disabled="loading">取消</t-button>
                        <t-button theme="default" variant="base" type="reset" :disabled="loading">重置</t-button>
                      <t-button theme="primary" type="submit" :loading="loading">提交</t-button>

                      <!-- <t-button theme="default" variant="base" @click="handleClear">清空校验结果</t-button> -->
                    </t-space>
                </t-form-item>
            </t-form>
        </template>
    </t-dialog>

    <t-dialog v-model:visible="visibleDelete" header="确认删除" width="30%" placement="center" :cancel-btn=null :confirm-btn=null>
        <div style="margin-top: 40px; margin-bottom: -10px; display: flex; justify-content: flex-end;;">
            <t-space size="20px">
                <t-button theme="default" variant="base" :disabled="deleteLoading" type="reset"
                    @click="() => visibleDelete = false">取消</t-button>
              <t-button @click="deleteComment" :loading="deleteLoading">确定删除</t-button>
            </t-space>
        </div>
    </t-dialog>
</template>


<script setup>
import { ref, reactive, computed, watch } from 'vue';
import { DeleteIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import axios from "axios";
const emits = defineEmits(['update:averageScore']);
const form = ref(null);
const userId = ref(sessionStorage.getItem('uid'))
const commentsData = ref([]);
const eventId = sessionStorage.getItem('eventId')
const token = sessionStorage.getItem('token')
const uid = sessionStorage.getItem('uid')
const commentForm = reactive({
    title: '',
    content: '',
    score: '',
})

const FORM_RULES = {
    title: [{ required: true, message: '标题必填' }],
    score: [{ required: true, message: '评分必填' }],
    content: [{ required: true, message: '内容必填' }],
};

const loading = ref(false); //add的loading
const deleteLoading = ref(false)
const visibleComment = ref(false)
const visibleDelete = ref(false)
const deleteEventId = ref(-1);

const onSubmit = ({ validateResult, firstError }) => {
    if (validateResult === true) {
        loading.value = true;
        addComment();
    } else {
        console.log(commentForm.score)
        console.log('Validate Errors: ', firstError, validateResult);
        MessagePlugin.warning(firstError);
    }
};

const setVisible = async () => {
    await handleClear();
    console.log('open')
    visibleComment.value = true;
};

const addComment = () => {
    axios.post(`/comment/add`, {
        "eventId": eventId,
        "publisherId": uid,
        "score": commentForm.score,
        "title": commentForm.title,
        "content": commentForm.content,
        // "publishDate": new Date(),
        "type": 0,
    }, {
        headers: {
            token: token
        }
    }).then((response) => {
        loading.value = false;
        visibleComment.value = false;
        commentForm.score = '';
        commentForm.title = ''
        commentForm.content = ''
        handleClear();
        getComment();
        MessagePlugin.success('提交成功');
    }).catch(() => { })
}

const handleClear = () => {
    form.value.clearValidate();
};

const dateToString = (date) => {
  const localeDateStringArray = date.toLocaleString().split('T');
  const result = `${localeDateStringArray[0]} ${localeDateStringArray[1]}`;
  return result;
}
const deleteComment = () => {
    deleteLoading.value = true;
    axios.get(`/comment/delete/${deleteEventId.value}`, {
        headers: {
            token: token
        }
    }).then((response) => {
        visibleDelete.value = false;
        getComment();
    }).catch(() => {
    }).finally(() => {
        deleteLoading.value = false;
    })

}


const averageScore = ref(-1)
const getComment = () => {
    axios.post(`/comment/getByEvent`,
        {
            eventId: eventId,
            type: 0,
        },
        {
            headers: {
                token: token
            }
        }
    ).then((response) => {
        commentsData.value = response.data.data
        // console.log(commentsData.value);
        computeAverageScore();
        // averageScore.value = computed(() => {
        //     if (commentsData.value.length === 0) return -1;
        //     const totalScore = commentsData.value.reduce((sum, item) => sum + item.score, 0);
        //     return totalScore / commentsData.value.length;
        // });
    }).catch(() => { })
}

const computeAverageScore = () => {
    if (commentsData.value.length !== 0) {
        const totalScore = commentsData.value.reduce((sum, item) => sum + item.score, 0);
        // console.log(totalScore)
        averageScore.value = (totalScore / commentsData.value.length).toFixed(1);
        console.log(totalScore / commentsData.value.length)
    }
}


watch(averageScore, (newAverageScore) => {
    emits('update:averageScore', newAverageScore);
});

const onReset = () => {
    MessagePlugin.success('重置成功');
};
// 禁用 Input 组件，按下 Enter 键时，触发 submit 事件
const onEnter = (_, { e }) => {
    e.preventDefault();
};

getComment();

</script>


<style scoped>
.title {
    color: #5e6066;
    font-size: 1.6em;
    font-weight: 700;
    margin: 30px;
    letter-spacing: 3.5px;
}

.line {
    width: 70px;
    height: 4px;
    margin-top: -15px;
    margin-bottom: 20px;
    margin-left: 30px;
    background-color: black;
}

.comment_card {
    display: flex;
  width: 100%;
    flex-direction: column;
    justify-content: space-between;
    background-color: rgba(255, 255, 255, 1);
    padding: 20px;
  white-space: pre-wrap;
  word-wrap: anywhere;
}

.price_card {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 20px;
}


.stars {
    display: flex;
    grid-gap: 0.125rem;
    gap: 0.125rem;
    margin-bottom: -10px;
    color: rgb(238, 203, 8);
}

.comment_infos {
    margin-top: 0.3rem;
}

.date-time {
    color: rgba(7, 63, 216, 1);
    font-size: 12px;
    font-weight: 600;
}

.description {
    margin-top: 0.4rem;
    line-height: 1.625;
    color: rgba(107, 114, 128, 1);
}

.author {
    margin-top: 1.3rem;
    font-size: 0.875rem;
    line-height: 1.25rem;
    color: rgba(107, 114, 128, 1);
}
</style>