<script setup>
import { ref } from 'vue';
import { Input, MessagePlugin } from 'tdesign-vue-next';
import { LockOnIcon } from "tdesign-icons-vue-next";
import axios from "axios";

const token = sessionStorage.getItem('token')
const uid = sessionStorage.getItem('uid')
const formData = ref({
  old_psw: '',
  new_psw_1: '',
  new_psw_2: '',
});


const changePsw = ({ validateResult, firstError }) => {
  if (validateResult === true) {
    axios.put("/user/update/pass", {
      "id": uid,
      "old_password": formData.value.old_psw,
      "new_password": formData.value.new_psw_2,
    }, {
      headers: {
        token: token,
      },
    }).then(() => {
      MessagePlugin.success('修改成功');
      location.reload()
    }).catch()
  } else {
    console.log('Validate Errors: ', firstError, validateResult);
    MessagePlugin.warning(firstError);
  }
}


const rePassword = (val) =>
  new Promise((resolve) => {
    const timer = setTimeout(() => {
      resolve(formData.value.new_psw_1 === val);
      clearTimeout(timer);
    });
  });
const passwordValidator = (val) => {
  if (val.length > 0 && val.length <= 4) {
    return { result: false, message: '太简单了！再开动一下你的小脑筋吧！', type: 'error' };
  }
  if (val.length > 4 && val.length < 8) {
    return { result: false, message: '还差一点点，就是一个完美的密码了！', type: 'warning' };
  }
  return { result: true, message: '太强了，你确定自己记得住吗！', type: 'success' };
};
const FORM_RULES = ref({
  old_psw: [{ required: true, message: '旧密码必填' }],
  new_psw_1: [
    { required: true, message: '新密码必填' },
    { validator: passwordValidator }
  ],
  new_psw_2: [
    { required: true, message: '新密码必填' },
    { validator: rePassword, message: '两次密码不一致' }
  ]
});
const onReset = () => {
  MessagePlugin.success('重置成功');
};

</script>

<template>
  <div>
    <t-form ref="form" id="form" :data="formData" reset-type="initial" @reset="onReset" @submit="changePsw"
      :rules="FORM_RULES" label-width="100px" label-align="right">
      <t-form-item name="old_psw" label="旧密码">
        <t-input v-model="formData.old_psw" type="password" clearable:true placeholder="请输入密码">
          <template #prefix-icon>
            <lock-on-icon />
          </template>
        </t-input>
      </t-form-item>
      <t-form-item name="new_psw_1" label="新密码">
        <t-input v-model="formData.new_psw_1" type="password" clearable:true placeholder="请输入密码">
          <template #prefix-icon>
            <lock-on-icon />
          </template>
        </t-input>
      </t-form-item>
      <t-form-item name="new_psw_2" label="确认新密码">
        <t-input v-model="formData.new_psw_2" type="password" clearable:true placeholder="请输入密码">
          <template #prefix-icon>
            <lock-on-icon />
          </template>
        </t-input>
      </t-form-item>

      <t-form-item>
        <t-space size="small">
          <t-button variant="outline" type="reset">重置</t-button>
          <t-button theme="success" type="submit">提交</t-button>
        </t-space>
      </t-form-item>
    </t-form>
  </div>
</template>

<style scoped></style>