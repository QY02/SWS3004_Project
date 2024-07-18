<template>
  <div class="input-information-main-div">
    <h1 class="input-information-title">Input information</h1>
    <t-form ref="form" :rules="FORM_RULES" :data="formData" :colon="true" @submit="onSubmit">
      <t-form-item v-for="information in additionalInformation" :label="information.name" :name="information.nameEng">
        <t-input v-model="formData[information.nameEng]" :placeholder="`Please input ${information.name}`"></t-input>
      </t-form-item>
      <div class="input-information-button-div">
        <t-space size="medium">
          <t-button @click="currentStep--">Back</t-button>
          <t-button type="submit">Next</t-button>
        </t-space>
      </div>
    </t-form>
  </div>
</template>

<script setup lang="ts">
import {
  AdditionalInformationItem,
  bookingInformation,
  currentStep,
  toNextStep
} from '@/components/event/book/Steps.vue';
import {reactive, ref, UnwrapNestedRefs} from "vue";
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
      message: `${item.name} required`,
    },
  ];
  if (item.rules !== null) {
    acc[item.nameEng].push(...(item.rules as FormRule[]));
  }
  return acc;
}, {} as Record<string, Array<FormRule>>));

const onSubmit: FormProps['onSubmit'] = ({validateResult, firstError}) => {
  if (validateResult === true) {
    for (let i = 0; i < bookingInformation.additionalInformation.length; i++) {
      bookingInformation.additionalInformation[i].value = formData[bookingInformation.additionalInformation[i].nameEng];
    }
    toNextStep();
  } else {
    MessagePlugin.warning(firstError);
  }
};
</script>

<script lang="ts">
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
      MessagePlugin.warning(`Please input information ${firstError !== null ? `：${firstError}` : ''}`);
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
}
</style>