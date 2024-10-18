<template>

  <van-card
      v-for="team in teamProps.teamList"
      :desc=team.description
      :title="`${team.teamName}(${team.expireTime})`"
      :price=team.maxNum

  >
    <template #tags>
      <van-tag plain type="primary" style="margin-top: 8px;margin-right: 8px">{{teamStatusEnum[team.status]}}</van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系他</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import {  defineProps } from 'vue';

import {TeamType} from "../models/team";

interface TeamCardListProps {
  teamList:TeamType[];
}

const teamProps = defineProps<TeamCardListProps>();

</script>

<style scoped>

</style>