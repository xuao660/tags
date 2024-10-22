<template>
  <van-search v-model="keyword" placeholder="搜索队伍"   @search="onSearch" @cancel="onCancel"/>

  <van-button type="primary" @click="createTeam">创建队伍</van-button>

  <team-card :team-list="teamList">
    <div id="teamPage">
    </div>
  </team-card>
  <van-empty v-if="!teamList || teamList.length <1 " description="内容为空"></van-empty>
</template>

<script setup lang="ts">
import myAxios from "../plugins/axios.js";
import {showToast} from "vant";
import {ref,onMounted} from "vue";
import {useRouter} from "vue-router";
import {TeamType} from "../models/team.d.ts";


const router = useRouter();

const keyword = ref('');
const teamList = ref([]);
// const teamQuery = ref<TeamType>();
const createTeam = () =>{
  router.push({
    path:"/team/add"
  })
}
//页面加载时执行
onMounted(()=>{
  console.log('222')
  listTeam();

})
/**
 * 搜索队伍
 * @param val
 */
const listTeam = async(val = '') => {
  const res = await myAxios.post(`/team/list`, {
    stringText:val
  })
  teamList.value = res.data;

  if (res?.code == 0){
    keyword.value = ''; // 重置搜索输入框

  }
  else{

    showToast(res.description);

  }
}
//队伍搜索关键词
const onSearch = (val) => {
  listTeam(val);

};
const onCancel = () => showToast('取消');
</script>

<style scoped>

</style>
