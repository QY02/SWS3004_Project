<template>
  <t-card hoverShadow theme="poster2" >
    <template #avatar>
      <t-avatar size="56px">
        <template #icon>
          <user-avatar-icon v-if="product.type === 1" />
          <calendar-icon v-if="product.type === 2" />
          <list-icon v-if="product.type === 3" />
          <chat-bubble-error-icon v-if="product.type === 4" />
          <money-icon v-if="product.type === 5" />
          <cpu-icon v-if="product.type === 6" />
        </template>
      </t-avatar>
    </template>
    <template #content>
      <p class="list-card-item_detail--name">{{ product.name }}</p>
      <p class="list-card-item_detail--desc">{{ product.description }}</p>
    </template>
  </t-card>
</template>
<script setup lang="ts">
import {
  CalendarIcon,
  MoneyIcon,
  ChatBubbleErrorIcon,
  ListIcon,
  UserAvatarIcon,
  CpuIcon,
} from 'tdesign-icons-vue-next';
import type { PropType } from 'vue';

export interface CardProductType {
  type: number;
  isSetup: boolean;
  description: string;
  name: string;
}

// eslint-disable-next-line
defineProps({
  product: {
    type: Object as PropType<CardProductType>,
  },
});

</script>

<style lang="less" scoped>
.list-card-item {
  display: flex;
  flex-direction: column;
  cursor: pointer;

  &_detail {
    min-height: 140px;

    &--name {
      margin-bottom: var(--td-comp-margin-s);
      font: var(--td-font-title-large);
      color: var(--td-text-color-primary);
    }

    &--desc {
      color: var(--td-text-color-secondary);
      font: var(--td-font-body-medium);
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
}
</style>
