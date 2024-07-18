<template>
  <t-space direction="vertical" size="large" style="width: 100%">
    <div style="margin: 30px 40px;">
      <h1>Publish new event</h1>
      <t-form ref="form" :data="formData" reset-type="initial" @reset="onReset" @submit="onSubmit" :rules="FORM_RULES">
        <div>
          <t-form-item label="Title" name="name">
            <t-input v-model="formData.name" placeholder="Please input a title">Title</t-input>
          </t-form-item>
          <t-form-item label="Introduction" name="content">
            <t-textarea v-model="formData.content" placeholder="Please enter a brief introduction" clearable/>
          </t-form-item>
          <t-form-item label="Poster" name="poster">
            <t-input v-model="formData.poster" placeholder="Please enter URL of the poster">Poster</t-input>
          </t-form-item>
          <event-session v-model:sessionData="eventSessionData"></event-session>
        </div>

        <div>
          <t-form-item style="margin-top: 30px;display: flex; justify-content: right; align-items: center;">
            <t-space>
              <t-tooltip content="Only reset event information, not reset event session" theme="warning">
                <t-button variant="outline" type="reset">Reset</t-button>
              </t-tooltip>
              <t-button theme="success" type="submit">Submit</t-button>
            </t-space>
          </t-form-item>
        </div>
      </t-form>
    </div>
  </t-space>
</template>
<script setup>
import {ref} from 'vue';
import {MessagePlugin} from 'tdesign-vue-next';
import axios from "axios";
import router from "@/routers/index.js";
import EventSession from "@/components/event/PublishEventSession.vue";

// #### 数据 START ##########################################

const token = sessionStorage.getItem('token')
const fullUserId = sessionStorage.getItem('fullUserId')

const FORM_RULES = {
  name: [{required: true, message: 'Title is required'}],
  content: [{required: true, message: 'Introduction is required'}],
  poster: [{required: true, message: 'Poster URL is required'}],
};

const session = []
let loading = ref(false);

const formData = ref({
  name: '',
  content: '',
  poster: ''
});


// #### 数据 END ##########################################

// #### 表单点击操作 START ##########################################

const eventSessionData = ref(session)

const onReset = () => {
  MessagePlugin.success('Reset success');
};
const sendEvent = async () => {
  let eventSessionDataSubmit = [];
  eventSessionData.value.forEach((eventSession) => {
    const x = eventSession.seat_map_id.split('.');
    eventSessionDataSubmit.push({
      registrationStartTime: eventSession.registration_time_range[0],
      registrationEndTime: eventSession.registration_time_range[1],
      startTime: eventSession.event_time_range[0],
      endTime: eventSession.event_time_range[1],
      seatMapTemplateId: x[x.length - 2],
      venue: eventSession.venue
    })
  })
  await axios.post(`/publishEvent`, {
        "name": formData.value.name,
        "content": formData.value.content,
        "sessions": eventSessionDataSubmit,
      }, {
        headers: {
          fullUserId: fullUserId,
          token: token
        }
      }
  ).then(response => {
        console.log(response)
      }
  ).catch();
};
const onSubmit = async ({validateResult, firstError, e}) => {
  e.preventDefault();
  if (validateResult === true) {
    if (eventSessionData.value.length > 0) {
      await sendEvent();
      await MessagePlugin.success('Submit success');
      await router.push("/home");
    } else {
      await MessagePlugin.warning('You need to add at least one event session');
    }

  } else {
    await MessagePlugin.warning(firstError);
  }
};
// #### 表单点击操作 END ##########################################

</script>
<style scoped>

</style>
