<template>
    <div class="secondary-notification">
      <t-tabs v-model="tabValue">
        <t-tab-panel v-for="(tab, tabIndex) in TAB_LIST" :key="tabIndex" :value="tab.value" :label="tab.label">
          <t-list v-if="msgDataList.length > 0" class="secondary-msg-list" :split="true">
            <t-list-item v-for="(item, index) in msgDataList" :key="index">
              <div :class="['content', { unread: (item.status===0) }]" style="display: flex;margin-left: 7px;">
                <t-tag size="medium" :theme="NOTIFICATION_THEMES[item.type]" variant="light">
                  {{ NOTIFICATION_TYPES[item.type] }}
                </t-tag>
<!--                <t-list-item-meta :title="item.title"-->
<!--                                  :description="item.content"-->
<!--                                  style="margin-left: 20px;"-->
<!--                />-->
                <div style="display: flex;margin-left: 7px;flex-direction: column;">
                  <div style="font-size: medium; font-weight: bold">
                    {{item.title}}
                  </div>
                  <p>
                    {{item.content}}
                  </p>
                </div>
              </div>
              <template #action>
                <span class="msg-date">{{ item.notifyTime }}</span>
                <div class="msg-action">
                  <t-tooltip
                      class="set-read-icon"
                      :overlay-style="{ margin: '6px' }"
                      :content="item.status===0 ? '设为已读' : '设为未读'"
                  >
                    <span class="msg-action-icon" @click="setReadStatus(item)">
                      <t-icon v-if="item.status===0" name="check-circle" size="16px" style="color: green"/>
                      <t-icon v-else name="close-rectangle" style="color: darkorange"/>
                    </span>
                  </t-tooltip>
                  <t-tooltip content="删除" :overlay-style="{ margin: '6px' }">
                    <span @click="handleClickDeleteBtn(item)">
                      <t-icon name="delete" size="16px" style="color: red"/>
                    </span>
                  </t-tooltip>
                </div>
              </template>
            </t-list-item>
          </t-list>
          <div v-else class="secondary-msg-list__empty-list">
            <img src="https://tdesign.gtimg.com/pro-template/personal/nothing.png" alt="空"/>
            <p>{{ '空' }}</p>
          </div>
        </t-tab-panel>
      </t-tabs>
    </div>
    <t-dialog
        v-model:visible="visible"
        header="删除通知"
        :body="`确认删除通知：${selectedItem && selectedItem.content}吗？`"
        :on-confirm="deleteMsg"
    />
</template>

<script setup>
import {computed, onMounted, ref} from 'vue';
import axios from "axios";
import { NOTIFICATION_TYPES, NOTIFICATION_THEMES } from '@/constants';

const token = sessionStorage.getItem('token')

const TAB_LIST = [
  {
    label: '全部消息',
    value: 'msgData',
  },
  {
    label: '未读消息',
    value: 'unreadMsg',
  },
  {
    label: '已读消息',
    value: 'readMsg',
  },
];

const tabValue = ref('msgData');

const visible = ref(false);
const selectedItem = ref();

const msgData = ref([]);
const getNotice = () => {
  axios.get(`/notification/all`, {
    headers: {
      token: token
    },
  }).then(response => {
    console.log(response)
    msgData.value = response.data.data
    // alert(JSON.stringify(msgData.value))
  }).catch();
}
onMounted(() => {
  getNotice(); // 调用 getNotice 方法
})
const unreadMsg = ref(computed(() => {
  return msgData.value.filter((item) => item.status === 0); // 过滤出 status 为 true 的消息
}));

const readMsg = ref(computed(() => {
  return msgData.value.filter((item) => item.status === 1); // 过滤出 status 为 false 的消息
}));
const msgDataList = computed(() => {
  // 在这里编写计算属性的逻辑
  if (tabValue.value === 'msgData') return msgData.value;
  if (tabValue.value === 'unreadMsg') return unreadMsg.value;
  if (tabValue.value === 'readMsg') return readMsg.value;
  return [];
});


const handleClickDeleteBtn = (item) => {
  visible.value = true;
  selectedItem.value = item;
};

const setReadStatus = (item) => {
  axios.put(`/notification/changeStatus`, {}, {
    headers: {
      token: token
    },
    params: {
      "notificationId": item.id,
      "read": item.status === 0
    }
  }).then(() => {
    // alert(item.status === 0)
    const changeMsg = msgData.value;
    changeMsg.forEach((e) => {
      if (e.id === item.id) {
        e.status = (e.status + 1) % 2;
      }
    });
    msgData.value = changeMsg;
    // location.reload()
  }).catch();

};

const deleteMsg = () => {
  const item = selectedItem.value;
  // alert(item.id)
  axios.delete(`/notification/delete/${item.id}`, {
    headers: {
      token: token
    },
  }).then(() => {
    const changeMsg = msgData.value;
    changeMsg.forEach((e, index) => {
      if (e.id === item.id) {
        changeMsg.splice(index, 1);
      }
    });
    visible.value = false;
    msgData.value = changeMsg;
    // location.reload()
  }).catch();

};
</script>

<style lang="less" scoped>
.secondary-notification {
  margin: 30px;
  background-color: var(--td-bg-color-container);
  border-radius: var(--td-radius-medium);
  padding: var(--td-comp-paddingTB-xxl) var(--td-comp-paddingLR-xxl);

  .t-tabs__content {
    padding-top: 0;
  }
}


.secondary-msg-list {
  height: 70vh;

  .t-list-item {
    cursor: pointer;
    padding: var(--td-comp-paddingTB-l) 0;
    transition: 0.2s linear;

    &:hover {
      background-color: var(--td-bg-color-container-hover);

      .msg-date {
        display: none;
      }

      .msg-action {
        display: flex;
        align-items: center;

        &-icon {
          display: flex;
          align-items: center;
        }
      }
    }

    :deep(.t-tag) {
      margin-right: var(--td-comp-margin-l);
    }

    .t-tag.t-size-s {
      margin-right: var(--td-comp-margin-s);
      margin-left: 0;
    }
  }

  .content {
    font: var(--td-font-body-medium);
    color: var(--td-text-color-placeholder);
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: pre-line;
    word-wrap: anywhere;
  }

  .unread {
    color: var(--td-text-color-primary);
  }

  .msg-action {
    display: none;
    margin-right: var(--td-comp-margin-xxl);
    transition: 0.2s linear;

    .set-read-icon {
      margin-right: var(--td-comp-margin-l);
    }
  }

  &__empty-list {
    min-height: 443px;
    padding-top: 170px;
    text-align: center;
    color: var(--td-text-color-primary);
  }
}
</style>
