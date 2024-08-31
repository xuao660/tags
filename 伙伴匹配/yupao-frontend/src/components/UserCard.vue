<template>

  <van-card
      v-for="user in userProps.userList"
      :desc=user.tags
      :title="`${user.username}(${user.planetCode})`"
      thumb="../assets/vue.svg"
  >
    <template #tags>
      <van-tag plain type="primary" v-for="tag in JSON.parse(user.tags)" style="margin-top: 8px;margin-right: 8px">{{tag}}</van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系他</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import {  defineProps } from 'vue';

import {UserType} from "../models/user";

interface UserCardListProps {
  userList:UserType[];
}

const userProps = defineProps<UserCardListProps>();

</script>

<style scoped>

</style>