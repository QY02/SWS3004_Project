<template>
  <div class="input-information-main-div">
    <h1 class="input-information-title">信息填写</h1>
    <div v-show="additionalInformation.length === 0" class="input-information-no-information-div">
      <h1 class="input-information-title">无需填写额外信息</h1>
      <div class="input-information-button-div">
        <t-space size="medium">
          <t-button theme="default" @click="currentStep--">上一步</t-button>
          <t-button theme="primary" @click="chooseNext">下一步</t-button>
        </t-space>
      </div>
    </div>
    <t-form v-show="additionalInformation.length !== 0" ref="form" :rules="FORM_RULES" :data="formData" :colon="true" @submit="onSubmit">
      <t-form-item v-for="information in additionalInformation" :label="information.name" :name="information.nameEng">
        <t-input v-model="formData[information.nameEng]" :placeholder="`请输入${information.name}`"></t-input>
      </t-form-item>
      <div class="input-information-button-div">
        <t-space size="medium">
          <t-button theme="default" @click="currentStep--">上一步</t-button>
          <t-button type="submit">下一步</t-button>
        </t-space>
      </div>
    </t-form>
  </div>
</template>

<script setup lang="ts">
import {currentStep, toNextStep} from '@/components/book/Steps.vue';
import {sessionInformation, AdditionalInformationItem, bookingInformation} from '@/components/book/Steps.vue';
import {nextTick, reactive, ref, UnwrapNestedRefs, watch, watchEffect} from "vue";
import {FormProps, FormRule, MessagePlugin} from "tdesign-vue-next";

let additionalInformation: UnwrapNestedRefs<AdditionalInformationItem[]> = reactive(bookingInformation.additionalInformation);

let formData: UnwrapNestedRefs<FormProps['data']> = reactive(additionalInformation.reduce((acc, item) => {
  acc[item.nameEng] = item.value;
  return acc;
}, {} as Record<string, string>));

let FORM_RULES: UnwrapNestedRefs<FormProps['rules']> = reactive(additionalInformation.reduce((acc, item) => {
  acc[item.nameEng] = [
    {
      required: item.required,
      message: `${item.name}必填`,
    },
  ];
  if (item.rules !== null) {
    acc[item.nameEng].push(...(item.rules as FormRule[]));
  }
  return acc;
}, {} as Record<string, Array<FormRule>>));

watch(() => bookingInformation.chosenSession, (newSession, oldSession) => {
  if ((oldSession === null) || (newSession === null) || (sessionInformation[oldSession].additionalInformationRequired !== sessionInformation[newSession].additionalInformationRequired)) {
    bookingInformation.additionalInformation = JSON.parse(sessionInformation[newSession].additionalInformationRequired);
    additionalInformation.length = 0;
    additionalInformation.push(...bookingInformation.additionalInformation);
    Object.keys(formData).forEach(key => delete formData[key]);
    Object.assign(formData, additionalInformation.reduce((acc, item) => {
      acc[item.nameEng] = item.value;
      return acc;
    }, {} as Record<string, string>));
    Object.keys(FORM_RULES).forEach(key => delete FORM_RULES[key]);
    Object.assign(FORM_RULES, additionalInformation.reduce((acc, item) => {
      acc[item.nameEng] = [
        {
          required: item.required,
          message: `${item.name}必填`,
        },
      ];
      if (item.rules !== null) {
        acc[item.nameEng].push(...(item.rules as FormRule[]));
      }
      return acc;
    }, {} as Record<string, Array<FormRule>>));
    form.value.validate({showErrorMessage: false}).then(() => form.value.clearValidate());
  }
});

const onSubmit: FormProps['onSubmit'] = ({validateResult, firstError}) => {
  if (validateResult === true) {
    for (let i = 0; i < bookingInformation.additionalInformation.length; i++) {
      bookingInformation.additionalInformation[i].value = formData[bookingInformation.additionalInformation[i].nameEng];
    }
    chooseNext();
  } else {
    MessagePlugin.warning(firstError);
  }
};

const chooseNext = ()=>{
  // console.log("priny")
  // console.log(sessionInformation[bookingInformation.chosenSession].seatMapId)
  if(sessionInformation[bookingInformation.chosenSession].seatMapId != -1){
    toNextStep();
  }
  else{
    currentStep.value = currentStep.value + 2;
  }
}
</script>

<script lang="ts" >
import {ref} from "vue";
import {MessagePlugin} from "tdesign-vue-next";

export const form = ref(null);

export function checkForm() {
  return form.value.validate().then((validateResult) => {
    if (validateResult && Object.keys(validateResult).length) {
      const firstErrorArray = Object.values(validateResult)[0];
      let firstError = null;
      for (let i = 0; i < firstErrorArray.length; i++) {
        if (firstErrorArray[i].result === false) {
          firstError = firstErrorArray[i].message;
        }
      }
      MessagePlugin.warning(`请完成信息填写${firstError !== null ? `：${firstError}` : ''}`);
      return false;
    } else {
      return true;
    }
  });
}
</script>

<style scoped lang="less">
.input-information {
  &-main-div {
    position: relative;
    top: 5vh;
  }

  &-button-div {
    display: flex;
    justify-content: center;
  }

  &-title {
    text-align: center;
    font-size: 25px;
    line-height: 0;
  }

  &-no-information-div {
    display: flex;
    flex-direction: column;
    position: relative;
    justify-content: space-between;
    top: 2vh;
    height: 20vh;
  }
}
</style>