import {createApp, reactive, ref} from 'vue';
import App from './App.vue';
import router from './routers';
import TDesign, {MessagePlugin} from 'tdesign-vue-next';
import 'tdesign-vue-next/es/style/index.css';
import './style/layout.less';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import VueDraggableResizable from 'vue-draggable-resizable';
import "vue-draggable-resizable/style.css";
import {createVuestic, createIconsConfig} from "vuestic-ui";
import "material-design-icons-iconfont/dist/material-design-icons.min.css";

import axios from 'axios';
import AMapLoader from "@amap/amap-jsapi-loader";

const app = createApp(App);
app.use(ElementPlus);
app.use(TDesign);
app.use(
    createVuestic({
        config: {
            icons: createIconsConfig({
                aliases: [
                    {
                        name: "bell",
                        color: "#FFD43A",
                        to: "fa4-bell",
                    },
                    {
                        name: "ru",
                        to: "flag-icon-ru small",
                    },
                ],
                fonts: [
                    {
                        name: "fa4-{iconName}",
                        resolve: ({iconName}) => ({class: `fa fa-${iconName}`}),
                    },
                    {
                        name: "flag-icon-{countryCode} {flagSize}",
                        resolve: ({countryCode, flagSize}) => ({
                            class: `flag-icon flag-icon-${countryCode} flag-icon-${flagSize}`,
                        }),
                    },
                ],
            }),
            // ...
        },
    })
)


axios.defaults.baseURL = 'http://47.107.113.54:25571'
// axios.defaults.baseURL = 'http://localhost:8083'
// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // console.log(response)
    if (response.status !== 200) {
        return Promise.reject(response);
    }
    return response;
}, function (error) {
    if (error) {
        if(error.response!==undefined && (error.response.data.msg ==="Please login"||error.message ==="Please login")) {
            if (router.currentRoute.value.path === '/'||router.currentRoute.value.path === '/login') {
            } else {
                MessagePlugin.error("登录状态失效，请先登陆")
                router.push('/login')
            }
        } else {
            // 响应错误
            if (error.response) {
                MessagePlugin.error(error.response.data.msg)
            } else {
                MessagePlugin.error(error.message)
            }
        }
    }
    return Promise.reject(error);
});

window._AMapSecurityConfig = {
    serviceHost: "http://47.107.113.54:25571/_AMapService",
    // serviceHost: "http://localhost:8083/_AMapService",

};

export const AMap = ref(null);

AMapLoader.load({
    key: "[input your key]",
    version: "2.0",
    plugins: ["AMap.Scale", "AMap.ToolBar", "AMap.ControlBar", "AMap.MapType", "AMap.PlaceSearch", "AMap.AutoComplete"],
}).then((AMapInstance) => {
    AMap.value = AMapInstance;
}).catch((e) => {
    console.log(e);
});

app.use(router);

app.config.globalProperties.$webSocketBaseUrl = 'ws://47.107.113.54:25571';
app.component("vue-draggable-resizable", VueDraggableResizable);
app.mount('#app');

export const globalProperties = reactive({
    webSocketBaseUrl: 'ws://47.107.113.54:25571', token: 'a'
    // webSocketBaseUrl: 'ws://localhost:8083', token: 'a'

});



const fileServerAxios = axios.create({
    baseURL: 'http://10.16.222.144:25572',
});

fileServerAxios.interceptors.response.use(function (response) {
    return response;
}, function (error) {
    if (error.response) {
        MessagePlugin.error(error.response.data.msg);
    } else {
        MessagePlugin.error(error.message);
    }
    return Promise.reject(error);
});

export { fileServerAxios };
