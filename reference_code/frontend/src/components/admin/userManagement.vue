<template>
  <t-card class="card-with-margin">
    <!--  Form 整体 ----------------------------------------------------------------------------->
    <div style="display: flex;flex-direction: column; margin-left: 10px">

      <!--    表头 ---------------------------------------------------------------------------------->
      <div style="display: flex;justify-content: space-between;margin: 10px;align-items: center">
        <t-button @click="showAdd">
          <template #icon>
            <add-icon/>
          </template>
          添加新用户
        </t-button>
        <va-input
            v-model.number="perPage"
            type="number"
            placeholder="Items..."
            label="每页展示个数"
        />

        <va-input
            v-model.number="currentPage"
            type="number"
            placeholder="Page..."
            label="当前页面"
        />
        <va-input
            v-model="filter"
            class="sm:col-span-2 md:col-span-3"
            placeholder="搜索..."
        />

      </div>

      <!--    表格  ------------------------------------------------------------------>
      <va-data-table
          class="table-crud"
          :items="items"
          :columns="columns"
          :per-page="perPage"
          :current-page="currentPage"
          striped
          selectable
          :row-bind="getRowBind"
          v-model="selectedList"
          :filter="filter"
          @filtered="filtered = $event.items"
      >
        <template #cell(type)="{ value }">
          <div v-if="value==='0'||value==='管理员'">
            {{ '管理员' }}
          </div>
          <div v-else-if="value==='1'||value==='普通用户'">
            {{ '普通用户' }}
          </div>
        </template>
        <template #cell(actions)="{ rowIndex }">
          <va-button
              preset="plain"
              icon="edit"
              @click="openModalToEditItemById(rowIndex)"
          />
          <va-button
              v-if="items[rowIndex].id===uid"
              preset="plain"
              icon="delete"
              class="ml-3"
              color="danger"
              disabled
          />
          <va-button
              v-else
              preset="plain"
              icon="delete"
              class="ml-3"
              color="danger"
              @click="deleteItemById(rowIndex)"
          />
        </template>

        <template #bodyAppend>
          <tr>
            <td colspan="7">
              <div style="display: flex;
                             justify-content: center;
                              margin-top: 20px">
                <va-pagination v-model="currentPage" :pages="pages"/>
              </div>
            </td>
          </tr>
        </template>
      </va-data-table>

      <div style="margin-top: 20px">
        <t-button theme="danger"
                  @click="onButtonClickDelete"
                  :disabled="selectedList.length===0"
                  style="float: right;">
          <template #icon>
            <delete-icon/>
          </template>
          删除所选用户
        </t-button>
      </div>
    </div>
  </t-card>

  <!--    修改用户 dialog------------------------------------------------------------->
  <t-dialog
      class="modal-crud"
      v-model:visible="isEditing"
      header="编辑用户"
      placement="center"
      :cancel-btn="null"
      :confirm-btn="null"
  >
    <t-form ref="formRef2"
            :data="editedItem"
            :rules="rules"
            @reset="resetEditedItem"
            @submit="editItem"
    >
      <t-form-item label="姓名" name="name">
        <t-input v-model="editedItem.name"></t-input>
      </t-form-item>
      <t-form-item label="密码" name="password">
        <t-input v-model="editedItem.password" type="password"></t-input>
      </t-form-item>
      <t-form-item label="邮箱" name="email">
        <t-auto-complete v-model="editedItem.email" :options="emailOptionsEdit" filterable></t-auto-complete>
      </t-form-item>
      <t-form-item label="电话" name="phoneNumber">
        <t-input v-model="editedItem.phoneNumber"></t-input>
      </t-form-item>
      <t-form-item label="系别" name="department">
        <t-select v-model="editedItem.department" class="demo-select-base">
          <t-option v-for="(item, index) in DEPARTMENT_OPTIONS" :key="index" :value="item" :label="item">
            {{ item}}
          </t-option>
        </t-select>
      </t-form-item>
      <t-form-item class="confirm-reset-btns">
        <t-space size="small">
          <t-button theme="default" variant="base" type="reset">取消</t-button>
          <t-button theme="success" type="submit">提交</t-button>
          <!--              <t-button theme="default" variant="base" @click="handleClear">清空校验结果</t-button>-->
        </t-space>
      </t-form-item>
    </t-form>

  </t-dialog>

  <t-dialog
      class="modal-crud"
      v-model:visible="isAdding"
      header="添加用户"
      placement="center"
      :cancel-btn="null"
      :confirm-btn="null"
  >
    <t-form ref="formRef1"
            :data="createdItem"
            :rules="rules"
            @reset="resetCreatedItem"
            @submit="addNewItem"
    >
      <t-form-item name="id" label="学号">
        <t-input type="text"
                 v-model="createdItem.id"
                 placeholder="请输入8位学号"
                 :rules="[(v) => /^(\d{8})$/.test(v) || '请输入8位学号']"
        ></t-input>
      </t-form-item>
      <t-form-item label="用户名" name="name">
        <t-input type="text" clearable placeholder="请输入用户名"
                 v-model="createdItem.name"
        ></t-input>
      </t-form-item>
      <t-form-item name="password" label="密码">
        <t-input v-model="createdItem.password" type="password" clearable placeholder="请输入密码" >
          <template #prefix-icon>
            <lock-on-icon/>
          </template>
        </t-input>
      </t-form-item>
      <t-form-item name="email" label="邮箱">
        <t-auto-complete v-model="createdItem.email" :options="emailOptionsAdd"
                         filterable placeholder="请输入邮箱"></t-auto-complete>
      </t-form-item>
      <t-form-item name="phoneNumber" label="电话">
        <t-input type="text"
                 v-model="createdItem.phoneNumber"
                 placeholder="请输入电话号码"
        ></t-input>
      </t-form-item>
      <t-form-item label="用户类型" name="department">
        <t-select v-model="createdItem.type" class="demo-select-base">
          <t-option v-for="item in optionsState" :key="item" :value="item" :label="item">
            {{ item }}
          </t-option>
        </t-select>
      </t-form-item>
      <t-form-item label="系别" name="department">
        <t-select v-model="createdItem.department" class="demo-select-base">
          <t-option v-for="(item, index) in DEPARTMENT_OPTIONS" :key="index" :value="item" :label="item">
            {{ item }}
          </t-option>
        </t-select>
      </t-form-item>
      <t-form-item class="confirm-reset-btns">
        <t-space size="small">
          <t-button theme="default" variant="base" type="reset">取消</t-button>
          <t-button theme="success" type="submit">提交</t-button>
          <!--              <t-button theme="default" variant="base" @click="handleClear">清空校验结果</t-button>-->
        </t-space>
      </t-form-item>
    </t-form>
  </t-dialog>
</template>

<script setup>
import {ref, onMounted, computed, watch} from 'vue';
import axios from "axios";
import {useModal} from "vuestic-ui";
import {AddIcon, DeleteIcon} from "tdesign-icons-vue-next";
import {MessagePlugin} from "tdesign-vue-next";
import "vuestic-ui/css";
import {DEPARTMENT_OPTIONS, emailSuffix} from "@/constants/index.js";

// ###### 数据 START ########################
const token = sessionStorage.getItem('token')
const uid = sessionStorage.getItem('uid')
const defaultItem = {
  id: '',
  name: '',
  password: '',
  email: '',
  phoneNumber: '',
  type: '普通用户',
  department: '计算机系',
};
const isAdding = ref(false);
const isEditing = ref(false);
const editedItem = ref({...defaultItem});
const createdItem = ref({...defaultItem});
const selectedList = ref([]);
const filter = ref("");
const filtered = ref("");
const perPage = ref(10);
const currentPage = ref(1);
const optionsState = ["普通用户", "管理员"];
// ###### 数据 END ########################

const rules = {
  username: [{required: true}, {
    validator: (v) => /^([A-Za-z\u4e00-\u9fa5\s]){1,50}$/.test(v),
    message: '请输入中文、英文和空格，长度不超过50。'
  }],
  id: [{required: true}, {validator: (v) => /^(\d{8})$/.test(v), message: 'ID必须是8个字符'}],
  email: [{required: true}, {validator: (v) => /[^@]+@[^@]+\.[a-zA-Z]{2,}$/.test(v), message: '邮箱格式错误'}],
  phoneNumber: [{required: true}, {validator: (v) => /^(\d{11})?$/.test(v), message: '电话必须是11个字符'}],
  password: [{required: true}, {
    validator: (v) => /^[a-zA-Z0-9/*]{8,}$/.test(v),
    message: '只允许数字、字符和斜杠，最小长度为8'
  }],
};
const {confirm} = useModal()
const items = ref([]);
const columns = [
  // {key: 'try'},
  {key: 'id', sortable: true},
  {key: 'name', label: "名字", sortable: true},
  {key: 'email', label: "邮箱", sortable: true},
  {key: 'phoneNumber', label: "电话", sortable: true},
  {key: 'type', label: "用户类型", sortable: true},
  {key: 'department', label: "系别", width: 80},
  {key: 'actions', label: "操作", width: 80},
];

const emailOptionsEdit = computed(() => {
  const emailPrefix = editedItem.value.email.split('@')[0];
  if (!emailPrefix) return [];
  return emailSuffix.map((suffix) => emailPrefix + suffix);
});
const emailOptionsAdd = computed(() => {
  const emailPrefix = createdItem.value.email.split('@')[0];
  if (!emailPrefix) return [];
  return emailSuffix.map((suffix) => emailPrefix + suffix);
});

//######## 添加用户 START ###############################
const addNewItem = async ({validateResult, firstError}) => {//add 确认弹窗点击确定后执行的操作
  if (validateResult === true) {
    if (createdItem.value.type === '普通用户') {
      createdItem.value.type = 1
    } else if (createdItem.value.type === '管理员') {
      createdItem.value.type = 0
    } else {
      await MessagePlugin.error("用户类型非法");
      return
    }
    axios.post("/user/add", createdItem.value, {
      headers: {
        token: token,
      },
    }).then(() => {
      MessagePlugin.success("提交成功");
      location.reload()
    }).catch();
  } else {
    console.log('Errors: ', validateResult);
    await MessagePlugin.warning(firstError);
  }
};
const resetCreatedItem = () => {//恢复初始值，设置弹窗不显示
  createdItem.value = {...defaultItem};
  isAdding.value = false
};
const showAdd = () => {
  isAdding.value = true;
}
//######## 添加用户 END ###############################

// ######## 编辑用户 START #######################
const editItem = async ({validateResult, firstError}) => {
  if (validateResult === true) {
    if (editedItem.value.password === '********') {
      axios.put("/user/update/admin", {
        id: editedItem.value.id,
        name: editedItem.value.name,
        type: editedItem.value.type,
        email: editedItem.value.email,
        phoneNumber: editedItem.value.phoneNumber,
        department: editedItem.value.department,

      }, {
        headers: {
          token: token,
        },
      }).then(() => {
        MessagePlugin.success("提交成功");
        location.reload()
      }).catch();
    } else {
      // alert(JSON.stringify(editedItem.value.name))
      // alert(JSON.stringify(editedItemId.value))
      axios.put("/user/update/admin", {
        id: editedItem.value.id,
        name: editedItem.value.name,
        type: editedItem.value.type,
        password: editedItem.value.password,
        email: editedItem.value.email,
        phoneNumber: editedItem.value.phoneNumber,
        department: editedItem.value.department,

      }, {
        headers: {
          token: token,
        },
      }).then(() => {
        MessagePlugin.success("提交成功");
        location.reload()
      }).catch();
    }
  } else {
    console.log('Errors: ', validateResult);
    await MessagePlugin.warning(firstError);
  }
};

const openModalToEditItemById = async index => {
  // alert(JSON.stringify(items.value[index]))
  editedItem.value = {...items.value[index]};
  isEditing.value = true;
  editedItem.value.password = '********'
  // nowEdit.value={...editedItem.value}
};

const resetEditedItem = () => {//恢复初始值，设置弹窗不显示
  // editedItem.value={...nowEdit.value}
  isEditing.value = false;
};

const getRowBind = (row) => {
  if (row.type !== "1") {
    return {
      class: ["custom-class"]
    };
  }
};
// ######## 编辑用户 END #######################

// ##### 删除用户 START ################################
const deleteItemById = async (id) => {//单删
  const result = await confirm({
    message: "删除用户后，账号状态将变为‘已注销’",
    title: "确认删除所选用户",
    okText: "确认",
    cancelText: "取消",
  });
  if (result)
      // alert(items.value[id].id)
    axios.post(`/user/delete/${items.value[id].id}`, {}, {
      headers: {
        token: token,
      },
    }).then(() => {
      MessagePlugin.success("提交成功");
      location.reload()
    }).catch();
};
const onButtonClickDelete = async () => {//批量删除所选items
  const result = await confirm({
    message: "删除用户后，账号状态将变为‘已注销’",
    title: "确认删除所选用户",
    okText: "确认",
    cancelText: "取消",
  });

  if (result) {//暂时不能用
    await MessagePlugin.info("正在提交...");
    axios.post("/user/delete/batch",
        selectedList.value
            .filter(item => item.id !== uid)
            .map(item => item.id),
        {
          headers: {
            token: token,
          },
        }
    ).then(() => {
      MessagePlugin.success("提交成功");
      location.reload()
    }).catch();
  }
};
// ##### 删除用户 END ################################
// ##### 分页 START #########################################
const pages = computed(() => {
  return perPage.value && perPage.value !== 0
      ? Math.ceil(filtered.value.length / perPage.value)
      : filtered.value.length;
});

watch([perPage, filtered], () => {
      pages.value = perPage.value && perPage.value !== 0
          ? Math.ceil(filtered.value.length / perPage.value)
          : filtered.value.length;
    }
);
// ##### 分页 END #########################################

// ######## 拉取所有用户 START ########################
const getAllUsers = () => {//拿到初始数据进行展示
  axios.post("/user/getAll", {}, {
    headers: {
      token: token,
    },
  }).then(response => {
    items.value = response.data.data
    // alert(JSON.stringify(response.data.data))
  }).catch();
};
// ######## 拉取所有用户 END ########################

onMounted(() => {
  getAllUsers();
});
</script>

<style lang="scss" scoped>
.table-crud {
  --va-form-element-default-width: 0;

  .va-input {
    display: block;
  }

  &__slot {
    th {
      vertical-align: middle;
    }
  }
}

.modal-crud {
  .va-input {
    display: block;
  }
}

.card-with-margin {
  margin: 20px;
  height: max-content;
}

.confirm-reset-btns {
  display: flex;
  justify-content: right;
  align-items: center;
}
</style>