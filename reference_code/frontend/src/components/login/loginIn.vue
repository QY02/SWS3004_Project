<template >
  <!--  <NavBarWithOnlyTitle v-if="showNavBar"></NavBarWithOnlyTitle>-->
  <div id="building">
    <div class="parent-container">
      <div class="form-container">
        <p class="title">Welcome!</p>
        <t-tabs :default-value="'ID'" @change="handlerChange" size="medium">
          <t-tab-panel v-for="tab in tabs" :key="tab.title" :value="tab.title" :label="tab.title">
            <!--        {{ tab.title }}-->
          </t-tab-panel>
        </t-tabs>

        <!--      <va-tabs v-model="value" grow>-->
        <!--        <template #tabs>-->
        <!--          <va-tab v-for="tab in tabs" :key="tab.title" :name="tab.title">-->
        <!--            {{ tab.title }}-->
        <!--          </va-tab>-->
        <!--        </template>-->

        <div class="demo-card">
          <!--        <t-card :title="title" hover-shadow :style="{ width: '340px' }">-->
          <!--          {{ infoMessage }}-->
          <!--          <template #actions>-->
          <!--            <a href="javascript:void(0)" @click="clickHandler">操作</a>-->
          <!--          </template>-->
          <!--        </t-card>-->
        </div>
        <t-card class="va-card" hover-shadow>
          <template class="va-card-content">
            <component :is="currentTab.component"></component>
            <p class="sign-up-label">
              还没有账户？
              <router-link to="/register"><span class="sign-up-link">注册</span></router-link>
            </p>
          </template>
        </t-card>
        <!--      </va-tabs>-->
      </div>
    </div>
  </div>
</template>

<script>
import {computed, ref, watchEffect} from "vue";
import {useRoute} from "vue-router";
import IDLogin from "./IDLogin.vue"
import EmailLogin from "./EmailLogin.vue"
import {MessagePlugin} from 'tdesign-vue-next';


const TABS = [
  {
    title: "ID",
    component: IDLogin

  },
  {
    title: "Email",
    component: EmailLogin
  },
  // {
  //   title: "GitHub",
  //   component: GitHubLogin
  // }
];
export default {

  setup() {

    sessionStorage.setItem('primary-color', '#154EC1')
    const clickHandler = () => {
      MessagePlugin.success('操作');
    };

    const showNavBar = ref(true);
    const tabs = TABS;
    const value = ref(TABS[0].title);

    const currentTab = computed(() => {
      // alert(currentTab)
      return tabs.find((tab) => tab.title === value.value);
    });
    const handlerChange = (newValue) => {
      value.value = newValue;
    };

    const route = useRoute()

    watchEffect(() => {
          showNavBar.value = route.matched.some((route) => route.name === "login");
        },
        // {immediate: true}
    );

    return {
      clickHandler,
      showNavBar,
      handlerChange,
      currentTab,
      value,
      tabs
    };
  }
};
</script>

<style scoped>
.parent-container {
  display: flex;
  justify-content: center; /* 水平居中 */
  //margin-right: 9%;
  margin-top: 7%;
  align-content: center; /* 垂直居中 */
}

.form-container {
  width: 400px;
  height: 500px;
  background-color: #fff;
  box-shadow: rgba(0, 0, 0, 0.35) 0 5px 15px;
  border-radius: 10px;
  box-sizing: border-box;
  padding: 20px 30px;
//margin-top: 50px; /* 与页面顶部的距离为50px */
}

.title {
  text-align: center;
  font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
  "Lucida Sans Unicode", Geneva, Verdana, sans-serif;
  margin: 10px 0 30px 0;
  font-size: 28px;
  font-weight: 800;
}


.sign-up-label {
  margin: 0;
  font-size: 10px;
  color: #747474;
  font-family: "Lucida Sans", "Lucida Sans Regular", "Lucida Grande",
  "Lucida Sans Unicode", Geneva, Verdana, sans-serif;;
  align-content: flex-end
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

.buttons-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  margin-top: 20px;
  gap: 15px;
}


.va-card {
  width: 340px;
  height: 320px;
}

.va-card-content {
  display: flex;
  flex-direction: column;
}

.background_login {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  background-image: linear-gradient(rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), url("@/assets/login.jpg");
  background-repeat: no-repeat;
  background-size: cover;
}
#building{
  //background:url("@/assets/login.jpg");
  background-image: linear-gradient(rgba(0,0,0, 0.1), rgba(0,0,0, 0.1)), url("@/assets/login.jpg");

  width:100%;
  height:100%;
  position:fixed;
  background-size:100% 100%;
}

</style>