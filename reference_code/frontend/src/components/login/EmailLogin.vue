<template>
  <div style="width: 285px">
    <t-form ref="form" :data="formData" :rules="rules" :colon="true" :label-width="0" @reset="onReset"
            @submit="handleSubmit">
      <div style="margin-top: 18px">
        <!--     <div style="color: #2b54d9">ID</div>-->
        <t-form-item name="email" style="margin-bottom: 30px;">
          <t-input v-model="formData.email" clearable placeholder="请输入邮箱"

          >
            <template #prefix-icon>
              <desktop-icon/>
            </template>
          </t-input>
        </t-form-item>
      </div>

      <div style="display: flex;">
        <t-form-item name="code">
          <t-input v-model="formData.code" clearable placeholder="请输入验证码"
                   style="width: 226px; margin-bottom: 12px">
            <template #prefix-icon>
              <lock-on-icon/>
            </template>
          </t-input>
        </t-form-item>

        <t-form-item>
          <t-button style="margin-left: 10px;width: 50px;" theme="default" variant="base" :disabled="countDown > 0"
                    @click="throttle(handleVeri)">{{ countDown > 0 ? `${countDown}秒` : '验证码' }}
          </t-button>
<!--                    <t-button theme="primary" block @click="handleVeri" style="width: 50px; margin-left: 10px">验证码</t-button>-->
        </t-form-item>
      </div>
      <t-form-item>
        <t-button theme="primary" shape="round" type="submit" block style="height: 40px; margin-bottom: 8px">登录
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
const formData = reactive({
  email: '',
  code: '',
});

const onReset = () => {
  MessagePlugin.success('重置成功');
};
const rules = {
  email: [{required: true}, {validator: (v) => /[^@]+@[^@]+\.[a-zA-Z]{2,}$/.test(v), message: '格式错误'}],
  code: [{required: true}, {validator: (v) => /^[0-9]{6}$/.test(v), message: '验证码必须是6位数字'}],
};
const countDown = ref(0); // 倒计时变量，初始为 0 表示可点击状态

const throttle = (func) => {
  console.log("in")

  let inThrottle = false;
  countDown.value = 3;
  const timer = setInterval(() => {
    if (countDown.value > 0) {
      countDown.value--; // 每秒减一
      console.log(countDown.value)
    }
  }, 1000);
  if (!inThrottle) {
    // 执行函数
    func.apply(this);
    inThrottle = true;

    // 设置节流结束的定时器
    setTimeout(() => {
      inThrottle = false;
      countDown.value = 3; // 重置倒计时
    }, 10000);
  }

}

const handleSubmit = ({validateResult}) => {
  if (validateResult === true) {
    axios.post("/loginWithEmail", {
      //此接口返回完整用户信息
      email: formData.email,
      code: formData.code
    })
        .then((response) => {
          // alert(JSON.stringify(response.data.data))
          const rd = response.data.data.id;
          const type = response.data.data.type
          const token = response.data.data.password
          const themeColor = response.data.data.themeColor
          sessionStorage.setItem('primary-color', themeColor);
          sessionStorage.setItem('uid', rd);
          sessionStorage.setItem('username', response.data.data.name)
          sessionStorage.setItem('token', token);
          MessagePlugin.success("Welcome! " + rd);

          if (type === 0) {//管理员
            sessionStorage.setItem('role', 'admin');
            router.push("/admin/homepage");
          } else {//正常用户
            sessionStorage.setItem('role', 'user');
            router.push("/HomePage");
          }

        }).catch();
  } else {
    MessagePlugin.warning("请确保输入格式正确!")

  }
};
const handleVeri = () => {
  if (rules.email[1].validator(formData.email)) {
    axios.post(`/sendEmail/${formData.email}`, {}, {})
        .then(() => {
          MessagePlugin.info("已经发送验证码，请查收。");
        })
        .catch((error) => {
        });

  } else {
    MessagePlugin.warning("请输入正确的邮箱!");

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