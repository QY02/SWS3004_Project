<template>
  <!--  <NavBarWithOnlyTitle></NavBarWithOnlyTitle>-->
  <div id="building">
    <div class="parent-container">
      <div class="form-container">
        <p class="title">注册</p>
        <t-form ref="form" :data="formData" :rules="rules" :colon="true" :label-width="5" @reset="onReset"
                @submit="handleSubmit" style="margin-top: 40px">
          <t-form-item name="username">
            <t-input type="text" clearable placeholder="请输入用户名"
                     v-model="formData.username"
                     label="用户名"
                     required-mark
            ></t-input>
          </t-form-item>

          <t-form-item name="id">
            <t-input type="text"
                     v-model="formData.id"
                     placeholder="请输入8位学号"
                     :rules="[(v) => /^(\d{8})$/.test(v) || '请输入8位学号']"
                     label="学号"
                     required-mark
            ></t-input>
          </t-form-item>

          <t-form-item name="department" style="margin-left: 20px">
            <t-select v-model="formData.department" :options="options1" label="学院">

<!--              <t-option key="Computer Science" label="Computer Science" value="Computer Science"/>-->
<!--              <t-option key="Engineering" label="Engineering" value="Engineering"/>-->
<!--              <t-option key="Mathematics" label="Mathematics" value="Mathematics"/>-->
<!--              <t-option key="Biology" label="Biology" value="Biology"/>-->
<!--              <t-option key="Physics" label="Physics" value="Physics"/>-->
<!--              <t-option key="Chemistry" label="Chemistry" value="Chemistry"/>-->
<!--              &lt;!&ndash;              :options="['Computer Science','Engineering','Mathematics','Biology','Physics','Chemistry']"&ndash;&gt;-->
              required-mark

            </t-select>
          </t-form-item>

          <t-form-item name="email">
            <t-auto-complete v-model="formData.email" :input-props="{ label: '邮箱' }" :options="emailOptions"
                             filterable placeholder="请输入邮箱"></t-auto-complete>
          </t-form-item>

          <t-form-item name="phoneNumber">
            <t-input type="text"
                     v-model="formData.phoneNumber"
                     placeholder="请输入电话号码"
                     label="电话"
            ></t-input>
          </t-form-item>


          <t-form-item name="password">
            <t-input v-model="formData.password" type="password" clearable placeholder="请输入密码" label="密码">
              <template #prefix-icon>
                <lock-on-icon/>
              </template>
            </t-input>
          </t-form-item>

          <t-form-item name="password2">
            <t-input v-model="formData.password2" type="password" clearable placeholder="请再次输入密码" label="确认密码">
              <template #prefix-icon>
                <lock-on-icon/>
              </template>
            </t-input>
          </t-form-item>
          <t-form-item label="感兴趣的活动类型" name="favType" :label-width="140">
            <t-checkbox-group v-model="formData.favType" :options="courseOptions"></t-checkbox-group>
          </t-form-item>
          <!--          <t-checkbox-group v-model="checked" :options="['选项一', '选项二', '选项三']" name="city"></t-checkbox-group>-->
          <!--        <t-value v-slot="isPasswordVisible" :default-value="false">-->
          <!--          <t-input-->
          <!--              v-model="formData.password"-->
          <!--              :type="isPasswordVisible.value ? 'text' : 'password'"-->
          <!--              label="Password"-->
          <!--              placeholder="Enter your password"-->
          <!--              :rules="[(v) => /^[a-zA-Z0-9/]{8,}$/.test(v) || 'Only numbers, characters, and slash are allowed, minimum length is 8']"-->
          <!--              @click-append-inner="isPasswordVisible.value = !isPasswordVisible.value"-->
          <!--              required-mark-->
          <!--          >-->
          <!--            <template #appendInner>-->
          <!--              <va-icon-->
          <!--                  :name="isPasswordVisible.value ? 'visibility_off' : 'visibility'"-->
          <!--                  size="small"-->
          <!--                  color="primary"-->
          <!--              />-->
          <!--            </template>-->
          <!--          </t-input>-->
          <!--        </t-value>-->
          <!--        <va-value v-slot="isPasswordVisible" :default-value="false">-->
          <!--          <t-input-->
          <!--              v-model="password2"-->
          <!--              :type="isPasswordVisible.value ? 'text' : 'password'"-->
          <!--              label="Password check"-->
          <!--              placeholder="Enter your password again"-->
          <!--              :rules="[(v) => v===password || 'Different from the last password']"-->
          <!--              @click-append-inner="isPasswordVisible.value = !isPasswordVisible.value"-->
          <!--              required-mark-->
          <!--          >-->
          <!--            <template #appendInner>-->
          <!--              <va-icon-->
          <!--                  :name="isPasswordVisible.value ? 'visibility_off' : 'visibility'"-->
          <!--                  size="small"-->
          <!--                  color="primary"-->
          <!--              />-->
          <!--            </template>-->
          <!--          </t-input>-->
          <!--        </va-value>-->
          <t-form-item>
            <t-button theme="primary" shape="round" type="submit" block
                      style="height: 40px; margin-bottom: 8px;margin-top: 20px">注册
            </t-button>
          </t-form-item>
          <!--        <t-button type="submit" class="form-btn" color="priamry" size="small" :disabled="!isValid"-->
          <!--                  @click="handleSubmit">Register-->
          <!--        </t-button>-->
        </t-form>
        <p class="sign-up-label">
          已经有账户了？
          <router-link to="Login"><span class="sign-up-link">登录</span></router-link>
        </p>
      </div>
    </div>

    <t-dialog
        v-model:visible="visible"
        header="请输入验证码"
        body="自定义底部按钮，直接传入文字"
        :confirm-btn="null"
        :cancel-btn="null"
        :close-on-overlay-click="false"
    >
      <t-form ref="form1" :data="formData1" :rules="rules1" :colon="true" :label-width="5" @reset="onReset"
              @submit="handleOK"
      >
        <t-form-item name="code">
          <t-input
              v-model="formData1.code"
              placeholder="输入验证码"
          >
          </t-input>
        </t-form-item>
        <div style="display: flex;">
          <t-form-item>
            <t-button theme="#f2f3ff" block @click="close" style="width: 60px;margin-right: 20px;margin-left: 240px">
              取消
            </t-button>
          </t-form-item>

          <t-form-item>
            <t-button theme="primary" type="submit" block style="width: 80px;margin-right: 0px">验证</t-button>
          </t-form-item>
        </div>
      </t-form>
    </t-dialog>
    <!--  modal-->
    <!--  <va-modal-->
    <!--      v-model="showModal"-->
    <!--      size="large"-->
    <!--      hide-default-actions-->
    <!--      close-button-->

    <!--  >-->
    <!--    <va-form ref="emailVeriRef" style="display: flex;flex-direction: column;gap: 10px">-->
    <!--      <h5 class="va-h5" style="color: grey">-->
    <!--        Verify Your E-mail-->
    <!--      </h5>-->
    <!--      <t-input-->
    <!--          v-model="code"-->
    <!--          type="text"-->
    <!--          class="input"-->
    <!--          placeholder="Enter the verification code"-->
    <!--          label="verification code"-->
    <!--          :rules="[(v) => /^[0-9]{6}$/.test(v) || 'Code must be a six-digit number']"-->
    <!--      ></t-input>-->
    <!--      <va-button-->
    <!--          :disabled="!isValidemailVeri"-->
    <!--          @click="handleOK"-->
    <!--      >-->
    <!--        Verify-->
    <!--      </va-button>-->
    <!--    </va-form>-->

    <!--  </va-modal>-->
  </div>
</template>

<script setup>
// import {getCurrentInstance, ref} from "vue";
// import axios from "axios";
// import NavBarWithOnlyTitle from "@/components/layouts/NavBarWithOnlyTitle.vue";
// // import {useForm, useToast} from "vuestic-ui";

import {computed, getCurrentInstance, inject, reactive, ref} from "vue";
import {LockOnIcon} from "tdesign-icons-vue-next";
import axios from "axios";
import {MessagePlugin} from "tdesign-vue-next";
import router from "@/routers";
import {emailSuffix, EVENT_TYPE_value_1} from "@/constants/index.js";

// const apiUrl = inject('$API_URL');
// const globalProperties = getCurrentInstance().appContext.config.globalProperties;
// const apiBaseUrl = globalProperties.$apiBaseUrl;
// axios.defaults.baseURL = apiBaseUrl;

const emailOptions = computed(() => {
  const emailPrefix = formData.email.split('@')[0];
  if (!emailPrefix) return [];
  return emailSuffix.map((suffix) => emailPrefix + suffix);
});
const rules = {
  username: [{required: true}, {
    validator: (v) => /^([A-Za-z\u4e00-\u9fa5\s]){1,50}$/.test(v),
    message: '请输入中文、英文和空格，长度不超过50。'
  }],
  id: [{required: true}, {validator: (v) => /^(\d{8})$/.test(v), message: 'ID必须是8个字符'}],
  email: [{required: true}, {validator: (v) => /[^@]+@[^@]+\.[a-zA-Z]{2,}$/.test(v), message: '邮箱格式错误'}],
  phoneNumber: [{required: true}, {validator: (v) => /^(\d{11})?$/.test(v), message: '电话必须是11个字符'}],

  password: [{required: true}, {
    validator: (v) => /^[a-zA-Z0-9/]{8,}$/.test(v),
    message: '只允许数字、字符和斜杠，最小长度为8'
  }],
  password2: [{required: true}, {
    validator: (v) => v === formData.password,
    message: '与上面的密码不一样'
  }],
  favType: [{required: true},
    {
      validator: (v) => v.length <= 12,
      message: '请选择1-12个感兴趣的活动类型',
      type: 'warning'
    }],
};
const rules1 = {
  code: [{required: true}, {validator: (v) => /^[0-9]{6}$/.test(v), message: '验证码必须是6个数字'}]
}
const onReset = () => {
  MessagePlugin.success('重置成功');
};
const options1 = [
  { label: '计算机系', value: '计算机系' },
  { label: '物理系', value: '物理系' },
  { label: '数学系', value: '数学系' },

];
// const code = ref("");
const formData = reactive({
  username: '',
  password: '',
  password2: '',
  department: '',
  id: '',
  email: '',
  phoneNumber: '',
  code: '',
  favType: [],
});
const courseOptions = EVENT_TYPE_value_1
const formData1 = reactive({
  code: '',
});
const visible = ref(false);

const close = () => {
  visible.value = false;
};

// const onConfirm = () => {
//   alert(formData1.code)
//   visible.value = false;
//   alert('跳转支付~');
// };
//   const {init} = useToast();
//   const {isValid, validate} = useForm('formRef')
//   const {isValid: isValidemailVeri, validate: validateemailVeri} = useForm('emailVeriRef')
//
//   const username = ref("");
//   const password = ref("");
//   const password2 = ref("");
//   const department = ref("Computer Science");
//   const id = ref("");
//   const email = ref("");
//   const phoneNumber = ref(null)
//   const code = ref("")
//
//   const showModal = ref(false)
//


const handleSubmit = ({validateResult}) => {
  // alert(formData.favType)
  if (validateResult === true) {
    const data = {
      "id": formData.id,
      "name": formData.username,
      "department": formData.department,
      "password": formData.password,
      "email": formData.email,
      "phoneNumber": formData.phoneNumber,
      "twoFactorAuthentication": false,
      // "favType": formData.favType,
      // "githubUserId": sessionStorage.getItem('git-id'),
      // "githubUserName": sessionStorage.getItem('git-name'),
    }
    // alert(formData.favType)
    axios.post("/register", {
      "user": data,
      "favType": formData.favType,
    })
        .then(() => {
          MessagePlugin.info("已发送验证码，请查收");
          visible.value = true;
        })
        .catch(error => {
        });
  } else {
    MessagePlugin.warning("输入格式错误")
    // alert('lll')
  }

};
const handleOK = ({validateResult}) => {
  // alert(formData1.code)
  if (validateResult === true) {
    axios.post("/registerEmailVerify", {
      "email": formData.email,
      "code": formData1.code
    }).then(() => {
          MessagePlugin.success("账号创建成功！");
          visible.value = !visible.value
          router.push("/login");
        })
        .catch((error) => {
        });
  } else {
    MessagePlugin.warning("输入格式错误")
  }
};
</script>


<style scoped>
.parent-container {
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  margin-top: 50px;
}

.form-container {
  width: 500px;
  background-color: #fff;
  box-shadow: rgba(0, 0, 0, 0.35) 0 5px 15px;
  border-radius: 10px;
  box-sizing: border-box;
  padding: 20px 30px;
  margin-top: 10px; /* 与页面顶部的距离为50px */
}

.title {
  text-align: center;
  font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
  "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
  margin: 10px 0 30px 0;
  font-size: 28px;
  font-weight: 800;
}

#building {
  //background: url("@/assets/login.jpg"); background-image: linear-gradient(rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.1)), url("@/assets/login.jpg"); width: 100%; height: 100%; position: fixed; background-size: 100% 100%;
}

.sign-up-label {
  margin: 0;
  font-size: 10px;
  color: #747474;
  font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
  "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
}

.sign-up-link {
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

</style>