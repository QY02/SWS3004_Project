<template>
  <div class="background">
    <div class="content">
      <h1 class="login-title">Login</h1>
      <div style="width: 285px">
        <t-form ref="form" :data="formData" :rules="rules" :colon="true" :label-width="0" @reset="onReset"
                @submit="handleSubmit">
          <div style="margin-top: 18px">
            <t-form-item name="account" style="margin-bottom: 30px;">
              <t-input v-model="formData.account" clearable placeholder="Please input user id or email">
                <template #prefix-icon>
                  <desktop-icon/>
                </template>
              </t-input>
            </t-form-item>
          </div>
          <t-form-item name="password">
            <t-input v-model="formData.password" type="password" clearable placeholder="Please input password"
                     style="margin-bottom: 12px">
              <template #prefix-icon>
                <lock-on-icon/>
              </template>
            </t-input>
          </t-form-item>
          <t-form-item>
            <t-button theme="primary" shape="round" type="submit" block style="height: 40px; margin-bottom: 8px">login
            </t-button>
          </t-form-item>
        </t-form>
        <p class="register-tip">
          Do not have an account?
          <router-link to="register"><span class="register-link">Register</span></router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive} from 'vue';
import {MessagePlugin} from 'tdesign-vue-next';
import {DesktopIcon, LockOnIcon} from 'tdesign-icons-vue-next';
import router from "@/routers/index.js";
import axios from "axios";

const formData = reactive({
  account: '',
  password: '',
});

const rules = {
  account: [{required: true, message: "User id or email is required"}],
  password: [{required: true, message: "Password is required"}, {
    validator: (v) => /^[a-zA-Z0-9/]+$/.test(v),
    message: 'Only numbers, characters, and slash are allowed'
  }],
};

const handleSubmit = ({validateResult}) => {
  let headers;
  if (formData.account.includes("@")) {
    headers = {
      email: formData.account
    }
  } else {
    headers = {
      fullUserId: formData.account
    }
  }
  if (validateResult === true) {
    axios.post("/login", {
      password: formData.password
    }, {headers: headers})
        .then((response) => {
          const name = response.data.data.name;
          const fullUserId = response.data.data.routingHash + response.data.data.id;
          const token = response.data.data.password;
          sessionStorage.setItem('name', name)
          sessionStorage.setItem('fullUserId', fullUserId)
          sessionStorage.setItem('token', token);
          MessagePlugin.success("Welcome! " + name);
          router.push("/home");
        });
  } else {
    MessagePlugin.warning("Please make sure the input format is correct!")
  }
};

</script>

<style scoped lang="less">
.background {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  background-image: linear-gradient(rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), url("@/assets/outer-home-background.jpg");
  background-repeat: no-repeat;
  background-size: cover;
}

.content {
  padding-bottom: 10%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-title {
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 35px;
  line-height: 1.2;
  text-align: center;
}

.register {
  &-tip {
    margin: 0;
    font-size: 10px;
    color: #747474;
    font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
    "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
  }

  &-link {
    margin-left: 1px;
    font-size: 11px;
    text-decoration: underline;
    text-decoration-color: teal;
    color: teal;
    cursor: pointer;
    font-weight: 800;
    font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
    "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
  }
}
</style>