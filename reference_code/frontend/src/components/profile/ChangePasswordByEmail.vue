<script setup>
import {computed, getCurrentInstance, reactive, ref} from 'vue';
import {Input, MessagePlugin} from 'tdesign-vue-next';
import {LockOnIcon} from "tdesign-icons-vue-next";
import axios from "axios";
import {emailSuffix} from "@/constants/index.js";

// const globalProperties = getCurrentInstance().appContext.config.globalProperties;
// const apiBaseUrl = globalProperties.$apiBaseUrl;
const token = sessionStorage.getItem('token')
const uid = sessionStorage.getItem('uid')

// const props = defineProps({
//   visible: Boolean
// })
// const emit = defineEmits(['update:visible'])
// const visibleBody = useVModel(props,'visible',emit)

const formData = reactive({
    email:'',
    code:'',
    new_psw_1:'',
    new_psw_2:'',
});

const sendCode = () => {
  if (formData.email !=='') {
    axios.put("/user/forgetPass", {
      email: formData.email
    }, {
      headers: {
        token: token
      }
    }).then(() => {
          MessagePlugin.info("验证码已发送");
        }
    )
  }else {
  MessagePlugin.warning("邮箱不能为空");
}
}
const changePsw = ({validateResult, firstError}) => {
  if (validateResult === true) {
    axios.put("/user/forgetPass/emailVerify", {
      password: formData.new_psw_1,
      email:formData.email,
      code: formData.code
    }, {
      headers: {
        token: token
      },
    }).then(() => {
          MessagePlugin.success("修改成功");
          location.reload();
        })
  } else {
    console.log('Validate Errors: ', firstError, validateResult);
    MessagePlugin.warning(firstError);
  }
}


const rePassword = (val) =>
    new Promise((resolve) => {
      const timer = setTimeout(() => {
        resolve(formData.new_psw_1 === val);
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
  email: [{ required: true, message: '格式必须为邮箱', type: 'warning' }],
  code:[{required: true, message: '验证码必填'}],
  new_psw_1: [
      {required: true, message: '新密码必填'},
    { validator: passwordValidator }
  ],
  new_psw_2: [
      {required: true, message: '新密码必填'},
      { validator: rePassword, message: '两次密码不一致' }
  ]
});
const emailOptions = computed(() => {
  const emailPrefix = formData.email.split('@')[0];
  if (!emailPrefix) return [];
  return emailSuffix.map((suffix) => emailPrefix + suffix);
});
const onReset = () => {
  MessagePlugin.success('重置成功');
};
</script>

<template>
  <div>
        <t-form
            ref="form"
            id="form"
            :data="formData"
            reset-type="initial"
            @reset="onReset"
            @submit="changePsw"
            :rules="FORM_RULES"
            label-width="100px"
            label-align="right"
        >
          <t-form-item  name="email" label="邮箱">
            <t-auto-complete v-model="formData.email" :options="emailOptions" filterable:true></t-auto-complete>
          </t-form-item>

          <t-form-item name="code" label="验证码">
            <t-input v-model="formData.code" clearable:true placeholder="请输入验证码">
            </t-input>
            <t-button :disabled="formData.email===''"
                      theme="default" variant="base"
                      @click="sendCode"
            >验证码</t-button>
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

<style scoped>

</style>