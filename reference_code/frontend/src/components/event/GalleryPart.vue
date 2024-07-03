<template>
    <t-loading :loading="loading">
        <el-card
            style="padding: 5px ; max-width: 100% ; margin-right: 30px; margin-left: 30px; margin-bottom: 40px;">
            <el-carousel :interval="4000" type="card" height="270px" width="100%" indicator-position="outside" v-show="list.length>0">
                <el-carousel-item v-for="item in list" :key="item.id">
                    <h3 text="2xl" justify="center">
                        <!-- <t-image src="https://tdesign.gtimg.com/demo/demo-image-1.png" fit="fill"
              :style="{ width: '100%', height: '100%' }" shape="round" /> -->
                        <t-image :src="item.img" :style="{ width: '100%', height: '100%' }" fit="cover" shape="round"
                            :lazy="true" class="image-shadow image-with-margin">
                        </t-image>
                    </h3>
                </el-carousel-item>
            </el-carousel>
            <div style="display: flex; flex-direction: column; justify-content: center; align-items: center;">
                <t-button @click="pushRouter('moments')">前往动态</t-button>
            </div>
        </el-card>
    </t-loading>
</template>

<script setup>
import router from '@/routers';
import { getCurrentInstance, ref, onMounted } from 'vue';
import axios from "axios";
import { fileServerAxios } from "@/main.js"
const pushRouter = (value) => {
    switch (value) {
        case 'moments':
            router.push('/moments');
            break;
    }
}

const list = ref([]);// 左侧动态列表
const loading = ref(false);
const getMomentBatch = async (id) => {
    try {
        loading.value = true;
        let radioGroupValue = ref('1');
        const response = await axios.get(`/comment/getEventMoment/${sessionStorage.getItem('eventId')}`, {
            headers: {
                token: sessionStorage.getItem('token'),
            }
        });
        // console.log(response)
        for (let i = 0; i < response.data.data.length; i++) {
            const fileServerResponse = await fileServerAxios.get(`/file/download`, {
                responseType: 'blob',
                headers: {
                    token: response.data.data[i].attachment,
                }
            });
            const image = fileServerResponse.data;
            list.value.push({
                id: response.data.data[i].comment_id,
                img: image,
                name: response.data.data[i].publisher_id,
            });
        }
        // console.log(list)
        loading.value = false;
    } catch (error) {
    }
};

onMounted(async () => {
    await getMomentBatch(-1);
});


</script>