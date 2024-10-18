<template>
  <team-card :team-list="teamList"></team-card>
  <van-empty v-if="!teamList || teamList.length <1 " description="内容为空"></van-empty>
</template>

<script>
import myAxios from "../plugins/axios";
import {Toast} from "vant";

export default {
  name: "team"
}
const teamList = ref([]);
//页面加载时执行
onMounted(async()=>{
  const res = await myAxios.get(`/team/list`)
  if (res?.code == 0){
    teamList.value = res.data;
  }
  else{
    Toast("加载队伍列表失败")
  }
})
</script>

<style scoped>

</style>
