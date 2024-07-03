<template>
  <el-card :bordered="false" shadow v-if="!editYes"
    style="display:flex;  align-items: center; justify-content: center; flex-direction: row ;padding-left: 30px; padding-right:30px; margin: 30px; height: 65vh;">
    <br>
    <div style="width: 100%;">
      <t-space align="center" :size="40">
        <t-avatar :image="avatar" size="100px" />
        <t-descriptions title="个人信息" :column="1">
          <t-descriptions-item label="名字">{{ formData.name }}</t-descriptions-item>
          <br>
          <t-descriptions-item label="电话号码">{{ formData.phoneNumber }}</t-descriptions-item>
          <t-descriptions-item label="邮箱">{{ formData.email }}</t-descriptions-item>
          <t-descriptions-item label="学院">{{ formData.department }}</t-descriptions-item>
          <t-descriptions-item label="我喜欢的活动类型">{{ temp }}</t-descriptions-item>
        </t-descriptions>
        <br>
        <br>
      </t-space>
    </div>
    <br>
    <!--    <div style="display: flex; justify-content: flex-end; align-items: center;">-->
    <div>

      <t-button style="" class="btn1" @click="() => {
        editYes = !editYes;
      }">
        修改个人信息
      </t-button>
      <t-tooltip content="退出登录">
        <t-button style="position: absolute; right: 50px; top: 50px" theme="default" variant="base" @click="logout">
          <logout-icon></logout-icon>
        </t-button>
      </t-tooltip>

      <ChangePassword></ChangePassword>
    </div>
  </el-card>

  <el-card :bordered="false" shadow v-else
    style=" align-items: center; justify-content: center; height: 85vh; margin: 30px;">
    <br>
    <br>
    <t-form ref="form" :data="formData" :rules="FORM_RULES" reset-type="initial" colon @reset="onReset"
      @submit="onSubmit" style="width: 80%; margin-left: 10%;">
      <t-form-item label="姓名" name="name">
        <t-input v-model="formData.name"></t-input>
      </t-form-item>

      <t-form-item label="学院" name="department">
        <t-select v-model="formData.department" :options="options1">
        </t-select>
      </t-form-item>

      <t-form-item label="邮箱" name="email">
        <t-input :disabled="true" v-model="formData.email"></t-input>
        <t-button @click="() => visibleEmail = true">修改邮箱</t-button>
      </t-form-item>

      <t-form-item label="电话" name="phoneNumber">
        <t-input v-model="formData.phoneNumber"></t-input>
      </t-form-item>
      <t-form-item label="感兴趣的活动类型" name="favTypes" :label-width="140">
        <t-checkbox-group v-model="formData.favTypes" :options="EVENT_TYPE_value_1">
        </t-checkbox-group>
      </t-form-item>


      <t-form-item label="头像" name="avatar">
        <t-space :size="30">
          <t-image :src="avatarList[formData.avatar]" v-model="formData.avatar"
            :style="{ width: '120px', height: '120px' }" />
          <t-button @click="() => visibleAvator = true">修改头像</t-button>
        </t-space>
        <!-- <t-upload v-model="formData.avatar"
                  action="https://service-bv448zsw-1257786608.gz.apigw.tencentcs.com/api/upload-demo" theme="image"
                  tips="请选择单张图片文件上传" accept="image/*">
        </t-upload> -->
      </t-form-item>

      <t-form-item>
        <t-space :size="20">
          <t-button theme="default" variant="base" :disabled="loadingg" @click="() => {
            editYes = !editYes;
          }">
            取消
          </t-button>
          <t-button theme="default" variant="base" type="reset" :disabled="load">重置</t-button>
          <t-button theme="success" type="submit" :loading="loadingg">提交</t-button>
        </t-space>
      </t-form-item>
    </t-form>
    <br>
  </el-card>
  <ChangeEmail v-model:visible="visibleEmail" v-model:old_email="formData.email"></ChangeEmail>
  <t-dialog v-model:visible="visibleAvator" attach="body" header="修改头像" destroy-on-close:true width="600px"
    :cancel-btn=null :confirm-btn=null>
    <template #body>
      <t-space v-for="(item, index) in avatarList" size="20px">
        <div style="margin: 20px;">
          <t-space direction="vertical">
            <t-image :src="item" :style="{ width: '120px', height: '120px' }" />
            <t-tag shape="mark" theme="primary" variant="light">选项{{ index + 1 }}</t-tag>
          </t-space>
        </div>
      </t-space>
      <t-radio-group :default-value="formData.avatar" @change="onChange">
        <t-radio value="0">选项1</t-radio>
        <t-radio value="1">选项2</t-radio>
        <t-radio value="2">选项3</t-radio>
        <t-radio value="3">选项4</t-radio>
        <t-radio value="4">选项5</t-radio>
        <t-radio value="5">选项6</t-radio>
      </t-radio-group>
      <div style="margin-top: 30px; display: flex; justify-content: flex-end; margin-right:40px ;">
        <t-space size="20px">
          <t-button @click="changeFormAvatar">确认选择</t-button>
        </t-space>
      </div>
    </template>
  </t-dialog>
</template>


<script setup>
import ChangePassword from "@/components/profile/ChangePassword.vue";
import ChangeEmail from "./ChangeEmail.vue";
import { ref, onMounted, reactive } from "vue";
import axios from "axios";
import { MessagePlugin } from 'tdesign-vue-next';
import router from "@/routers/index.js";
import { LogoutIcon } from "tdesign-icons-vue-next";
import { EVENT_TYPE_MAP, EVENT_TYPE_value_1 } from "../../constants/index.js";
import { form } from "../book/InputInformation.vue";

const avatar = ref('https://tdesign.gtimg.com/site/avatar.jpg')
const avatarList = ['https://avatars.githubusercontent.com/pengyyyyy',
  'https://tdesign.gtimg.com/site/avatar.jpg',
  'https://avatars.githubusercontent.com/LeeJim',
  'https://avatars.githubusercontent.com/u/7361184?v=4',
  'https://avatars.githubusercontent.com/pattybaby110',
  'https://avatars.githubusercontent.com/chaishi']


const visibleEmail = ref(false)
const visibleAvator = ref(false)

const info = ref({});
const favType = ref({});

const showModalCode = ref(false)
const logout = () => {
  sessionStorage.removeItem('token');
  sessionStorage.removeItem('role');
  MessagePlugin.success('退出登录成功！');
  router.push('/login');
}

const tempAvatar = ref(0)
const onChange = (checkedValues) => {
  console.log('checkedValues:', checkedValues);
  tempAvatar.value = checkedValues;
};

const changeFormAvatar = () => {
  formData.avatar = tempAvatar.value;
  console.log(formData.avatar)
  visibleAvator.value = false;
}

const loadingg = ref(false)
axios.post(`/user/get/${sessionStorage.getItem('uid')}`, {}, {
  headers: {
    token: sessionStorage.getItem('token'),
  },
})
  .then((response) => {
    info.value = response.data.data;
    console.log(info.value.iconId);
    avatar.value = avatarList[info.value.iconId-1];//抹去注销头像
    formData.avatar = info.value.iconId-1;//抹去注销头像
    formData.name = info.value.name;
    formData.email = info.value.email;
    formData.phoneNumber = info.value.phoneNumber;
    formData.department = info.value.department;
  })
  .catch(() => {
  })
const FORM_RULES = ref({
  name: [{ required: true, message: '名字不可为空' }],
  favTypes: [{
    required: true,
    message: '请选择1-12个感兴趣的活动类型',
  }],
  phoneNumber: [
    { telnumber: true, message: '请输入正确的手机号码' }
  ],
  department: [{ required: true, message: '学院不可为空' },],
  email: [{ required: true, message: '学院不可为空' },],
  avatar: [{ required: true, message: '头像不可为空' },]
});
let temp = ref();

axios.post(`/user/getUserFavoriteType`, {
  "userId": sessionStorage.getItem('uid'),
}, {
  headers: {
    token: sessionStorage.getItem('token'),
  },
})
  .then((response) => {
    favType.value = response.data.data;
    temp.value = JSON.stringify(favType.value)
    // alert(temp.value)

    temp.value = temp.value.substring(1, temp.value.length - 1)
    temp.value = temp.value.split(',')
    temp.value = temp.value.map(Number);
    // alert(temp.value)
    formData.favTypes = temp.value
    temp.value = temp.value.map(type => EVENT_TYPE_MAP[type]).join(', ')

    // alert(typeof temp[0])
    // alert(temp.value)
  })
  .catch(() => {
  })
  
  const options1 = [
  { label: '计算机系', value: '计算机系' },
  { label: '物理系', value: '物理系' },
  { label: '数学系', value: '数学系' },

];

const formData = reactive({
  name: '',
  email: '',
  phoneNumber: '',
  department: '',
  avatar: '',
  favTypes: '',
});


const onReset = () => {
  MessagePlugin.success('重置成功');
};

const submitInfo = () => {
  loadingg.value = true
  axios.put(`/user/update`, {
    "id": sessionStorage.getItem('uid'),
    "name": formData.name,
    "phoneNumber": formData.phoneNumber,
    "department": formData.department,
    "iconId": Number(formData.avatar) + 1,//跨过注销头像
  }, {
    headers: {
      token: sessionStorage.getItem('token'),
    },
  }).then(() => {
    avatar.value = avatarList[formData.avatar]
    axios.post("/user/changeUserFavoriteType", {
      "userId": sessionStorage.getItem('uid'),
      "favType": formData.favTypes,
    }, {
      headers: {
        token: sessionStorage.getItem('token'),
      },
    })
      .then(() => {
        MessagePlugin.success('提交成功');
        temp.value = formData.favTypes.map(type => EVENT_TYPE_MAP[type]).join(', ')
        editYes.value = false;
        loadingg.value = false;
      })
      .catch(error => {
      });

  }).catch(() => {
    loadingg.value = false;
  })
}

const onSubmit = ({ validateResult, firstError }) => {
  // alert(formData.favTypes)
  if (validateResult === true) {
    if (formData.favTypes.length <= 0 || formData.favTypes.length > 12) {
      MessagePlugin.warning("请选择1-12个感兴趣的活动类型!");
    } else {
      submitInfo()
    }
  } else {
    console.log('Errors: ', validateResult);
    MessagePlugin.warning(firstError);
  }

};


const editYes = ref(false);
</script>


<style scoped>
.inform {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  /* 控制元素之间的间距 */
}

.px-2 {
  font-size: large;
  font-weight: bolder;
}

.github-login-button {
  border-radius: 10px;
  box-sizing: border-box;
  padding: 5px 10px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  gap: 5px;
  border: 2px solid #747474;
}

.personalInformationForm {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.btn1 {
  margin-left: 20px;
  flex-grow: 1;
  width: 120px;
}

.btn {
  margin-left: 20px;
  flex-grow: 1;
}

.button-container {
  display: flex;
  justify-content: space-around;
  /* 按钮之间平均分配空间 */
  align-items: center;
  /* 垂直居中对齐按钮 */
  margin-top: 20px;
  /* 与上面的组件保持一定距离 */
}
</style>