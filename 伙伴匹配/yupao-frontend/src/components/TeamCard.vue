<template>
  <div id="teamCardList">
    <van-card
        v-for="team in teamProps.teamList"
        :desc=team.description
        :title="`${team.teamName}`"
        :thumb="four"
    >
      <template #tags>
        <van-tag plain type="primary" style="margin-top: 8px;margin-right: 8px">{{teamStatusEnum[team.status]}}</van-tag>
      </template>

      <template #bottom>
        <div>
          {{'最大人数：'+team.maxNum}}
        </div>
        <div>
          {{'过期时间：'+team.expireTime}}
        </div>
      </template>
      <template #footer>
        <van-button type="primary" size="mini" @click="doJoinTeam(team.id)">加入队伍</van-button>
        <van-button v-if="team.captainId === currentUser?.id" type="primary" size="mini" @click="doUpdateTeam(team.id)">更新队伍</van-button>

      </template>
    </van-card>

  </div>
  {{currentUser}}
<!--  {{team.captainId}}-->

</template>

<script setup lang="ts">
import {defineProps, onMounted,ref} from 'vue';
import { teamStatusEnum } from "../constants/teamStatusEnum.ts";
import {TeamType} from "../models/team";
import {useRouter} from "vue-router";
import four from "../assets/four.jpg";
import myAxios from "../plugins/axios.js";
import {showFailToast, showToast} from "vant";
import {getCurrentUser} from "../state/User";

const router = useRouter();
const doJoinTeam = async (id:number) =>{
  const res = await myAxios.post("/team/join",{"teamId":id});
  if(res?.code === 0){
    showToast('加入队伍成功');
  }
  else{
    showToast('加入队伍失败,'+(res.description ? `，${res.description}` : res.message));

  }

}
const doUpdateTeam = async(id:number) =>{

}
interface TeamCardListProps {
  teamList:TeamType[];
}

const teamProps = defineProps<TeamCardListProps>();

// 获取当前登录用户
const currentUser  = ref();
onMounted(async()=>{
  const user = await getCurrentUser();
  if (user) {
    console.log("当前用户：", currentUser);
    currentUser.value = user;
  } else {
    showFailToast("用户未登录");
    router.push('/user/login');
  }

})

</script>

<style scoped>
 #teamCardList :deep(.van-image__img) {
   height:128px;
   width: 90px;
   object-fit: unset;

 }
</style>
