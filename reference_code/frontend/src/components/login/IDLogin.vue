<template>

  <div style="width: 285px">
    <t-form ref="form" :data="formData" :rules="rules" :colon="true" :label-width="0" @reset="onReset"
            @submit="handleSubmit">
      <div style="margin-top: 18px">
        <!--     <div style="color: #2b54d9">ID</div>-->
        <t-form-item name="account" style="margin-bottom: 30px;">
          <t-input v-model="formData.account" clearable placeholder="请输入账户名">
            <template #prefix-icon>
              <desktop-icon/>
            </template>
          </t-input>
        </t-form-item>
      </div>
      <!--      <div style="color: #2b54d9">密码</div>-->
      <t-form-item name="password">
        <t-input v-model="formData.password" type="password" clearable placeholder="请输入密码"
                 style="margin-bottom: 12px">
          <template #prefix-icon>
            <lock-on-icon/>
          </template>
        </t-input>
      </t-form-item>

      <t-form-item>
        <t-button theme="primary" shape="round" type="submit" block style="height: 40px; margin-bottom: 8px"
                  :loading="loadingg">登录
        </t-button>
      </t-form-item>
    </t-form>
  </div>
</template>

<script setup>
import {getCurrentInstance, inject, reactive, ref} from 'vue';
import {MessagePlugin} from 'tdesign-vue-next';
import {DesktopIcon, LockOnIcon} from 'tdesign-icons-vue-next';
import axios from "axios";
import router from "@/routers";
// import { ref} from "vue";

const formData = reactive({
  account: '',
  password: '',
});

const onReset = () => {
  MessagePlugin.success('重置成功');
};

const loadingg = ref(false);
const rules = {
  // account: [{ required: true }, { validator: (v) => /^(\d{8})$/.test(v) , message: 'ID must be 8 digits' }],
  account: [{required: true}, {validator: (v) => /^(\d{8})$/.test(v), message: 'ID 必须为8个数字'}],
  // description: [{ validator: (val) => val.length < 10, message: '不能超过 20 个字，中文长度等于英文长度' }],
  password: [{required: true}, {
    validator: (v) => /^[a-zA-Z0-9/]{8,}$/.test(v),
    message: '只允许数字、字符和斜杠，最小长度为8'
  }],
};

const handleSubmit = ({validateResult}) => {
  if (validateResult === true) {
    loadingg.value = true
    axios.post("/login", {
      id: formData.account,
      password: formData.password
    })
        .then((response) => {
          loadingg.value = false;
          // alert(JSON.stringify(response.data.data))
          const rd = response.data.data.id;
          const type = response.data.data.type
          const token = response.data.data.password
          const themeColor = response.data.data.themeColor
          sessionStorage.setItem('primary-color', themeColor);

          sessionStorage.setItem('uid', rd);
          sessionStorage.setItem('token', token);
          sessionStorage.setItem('username', response.data.data.name)

          // alert(sessionStorage.getItem('username'))
          MessagePlugin.success("Welcome! " + rd);
          // alert(sessionStorage.getItem('token'));
          if (type === 0) {//管理员
            sessionStorage.setItem('role', 'admin');
            router.push("/admin/homepage");
          } else {//正常用户
            sessionStorage.setItem('role', 'user');
            router.push("/HomePage");
          }
        }).catch(() => {
      loadingg.value = false;
      // MessagePlugin.warning("密码或者用户名错误!")

    })
  } else {
    MessagePlugin.warning("请确保输入格式正确!")
    // alert('lll')
  }

};

</script>

<style scoped>
.form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 18px;
  margin-bottom: 15px;
}

.form-btn {
  padding: 10px 15px;
  font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
  "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
  border-radius: 20px;
  border: 0 !important;
  outline: 0 !important;
  background: teal;
  color: white;
  cursor: pointer;
  box-shadow: rgba(0, 0, 0, 0.24) 0 3px 8px;
}


.form-btn:active {
  box-shadow: none;
}
</style>