import {createApp} from 'vue';
import App from './App.vue';
import router from './routers';
import TDesign, {MessagePlugin} from 'tdesign-vue-next';
import 'tdesign-vue-next/es/style/index.css';
import './style/layout.less';
import "vue-draggable-resizable/style.css";
import axios from "axios";
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import VueDraggableResizable from 'vue-draggable-resizable';

const app = createApp(App);
app.use(TDesign);
app.use(router);
app.use(ElementPlus);
app.component("vue-draggable-resizable", VueDraggableResizable);
app.mount('#app');

axios.defaults.baseURL = 'http://aad092aaf80b94cf998d0facf27d6975-1447862158.us-east-1.elb.amazonaws.com:25670'
axios.interceptors.response.use(function (response) {
    if (response.status !== 200) {
        return Promise.reject(response);
    }
    return response;
}, function (error) {
    if (error) {
        if (error.response !== undefined && (error.response.data.msg === "Invalid token" || error.response.data.msg === "Invalid fullUserId" || error.response.data.msg === "Verification failed")) {
            if (router.currentRoute.value.path !== '/' && router.currentRoute.value.path !== '/login') {
                MessagePlugin.error("Login status is invalid, please log in first")
                router.push('/login')
            }
        } else {
            if (error.response) {
                MessagePlugin.error(error.response.data.msg)
            } else {
                MessagePlugin.error(error.message)
            }
        }
    }
    return Promise.reject(error);
});

const fileServerAxios = axios.create({
    baseURL: 'http://aad092aaf80b94cf998d0facf27d6975-1447862158.us-east-1.elb.amazonaws.com:25670',
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