import {createApp} from 'vue';
import App from './App.vue';
import router from './routers';
import TDesign from 'tdesign-vue-next';
import 'tdesign-vue-next/es/style/index.css';
import './style/layout.less';
import "vue-draggable-resizable/style.css";

const app = createApp(App);
app.use(TDesign);
app.use(router);
app.mount('#app');
