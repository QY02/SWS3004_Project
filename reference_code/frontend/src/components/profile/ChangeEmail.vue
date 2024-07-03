<script setup>
import { computed, ref,reactive } from 'vue';
import { Input, MessagePlugin } from 'tdesign-vue-next';
import { useVModel } from "@vueuse/core";
import { LockOnIcon } from "tdesign-icons-vue-next";
import axios from 'axios';
const props = defineProps({
  visible: Boolean,
  old_email: String,
})
const emit = defineEmits(['update:visible'])
const visibleBody = useVModel(props, 'visible', emit)

const formData = reactive({
  old_email: props.old_email,
  new_email: '',
  code: '',
});


const FORM_RULES = ref({
  old_email: [{ disabled: true }],
  new_email: [{ required: true, message: '格式必须为邮箱', type: 'error' }],
  code: [{ required: true, message: '必须填写验证码', type: 'error' }],
});
const emailSuffix = ['@qq.com', '@163.com', '@gmail.com', '@mail.sustech.edu.cn', '@outlook.com'];
const emailOptions = computed(() => {
  const emailPrefix = formData.new_email.split('@')[0];
  if (!emailPrefix) return [];
  return emailSuffix.map((suffix) => emailPrefix + suffix);
});

const onReset = () => {
  MessagePlugin.success('重置成功');
};
const countDown = ref(0); // 倒计时变量，初始为 0 表示可点击状态
const throttle = (func) => {
  console.log("in")
  if (formData.new_email !== '') {
    console.log('a??')
    let inThrottle = false;
        if (!inThrottle) {
          // 执行函数
          console.log('inThrottle')
          countDown.value = 10;
          const timer = setInterval(() => {
            if (countDown.value > 0) {
              countDown.value--; // 每秒减一
            }
          }, 1000);
          func.apply(this);
          inThrottle = true;

          // 设置节流结束的定时器
          setTimeout(() => {
            inThrottle = false;
            countDown.value = 10; // 重置倒计时
          }, 10000);
        }
  } else {
    MessagePlugin.warning("邮箱不能为空");
  }
}

const sendCode = () => {
    axios.put(`/user/update/email`,{
      "id": sessionStorage.getItem('uid'),
      "email": formData.new_email
    }, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    }).then(() => {
      MessagePlugin.info("验证码已发送");
    }).catch(()=>{})
}

const updateEmail = ({validateResult, firstError}) => {
  if (validateResult === true) {
    axios.put(`/user/update/emailVerify`,{
      "id": sessionStorage.getItem('uid'),
      "email": formData.new_email,
      "code": formData.code
    }, {
      headers: {
        token: sessionStorage.getItem('token')
      }
    }).then(() => {
      MessagePlugin.info("已修改");
      visibleBody.value=false;
      location.reload()
    }).catch(()=>{})
  } else {
    console.log('错误: ', firstError, validateResult);
    MessagePlugin.warning(firstError);
  }
}

</script>

<template>
  <t-dialog v-model:visible="visibleBody" attach="body" header="修改邮箱" destroy-on-close:true width="450px" :cancel-btn=null
    :confirm-btn=null>
    <template #body>
      <t-space direction="vertical">
        <t-form ref="form" id="form" :data="formData" reset-type="initial" @reset="onReset" @submit="updateEmail"
          :rules="FORM_RULES" label-width="100px" label-align="right">
          <t-form-item name="old_email" label="旧邮箱">
            <t-input v-model="formData.old_email" :placeholder="old_email" disabled>
            </t-input>
          </t-form-item>
          <t-form-item name="new_email" label="新邮箱">
            <t-auto-complete v-model="formData.new_email" :options="emailOptions" filterable></t-auto-complete>
            <!-- <t-input v-model="formData.new_email" clearable:true placeholder="请输入新邮箱">
            </t-input> -->
          </t-form-item>
          <t-form-item name="code" label="验证码">
            <t-input v-model="formData.code" clearable:true placeholder="请输入验证码">
              <template #prefix-icon>
                <lock-on-icon />
              </template>
            </t-input>
            <t-button theme="default" variant="base" :disabled="countDown > 0"
              @click="throttle(sendCode)">{{ countDown > 0 ? `${countDown}秒` : '验证码' }}</t-button>
            <!-- <t-button theme="default" variant="base" @click="throttle(sendCode)">验证码</t-button> -->
          </t-form-item>
          <t-form-item>
            <t-space size="20px">
              <t-button theme="default" variant="base" type="reset">重置</t-button>
              <t-button theme="success" type="submit">提交</t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </t-space>
    </template>
  </t-dialog>
</template>

<style scoped></style>