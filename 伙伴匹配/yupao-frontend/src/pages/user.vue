<template>
  <van-cell title="当前用户" :value="user?.username" />
  <van-cell title="修改个人信息" is-link to="/user/info" />
  <van-cell title="我创建的队伍" is-link to="/team/create" />
  <van-cell title="我加入的队伍" is-link to="/team/join" />
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../state/User";
import {showFailToast, showSuccessToast} from "vant";

const user = ref();
onMounted(async() =>{
  const res = await getCurrentUser();
  if(res){
    console.log("当前用户请求：",res)
    user.value = res;
    showSuccessToast("获取当前用户成功")
  }else{
    showFailToast("获取当前用户失败")
  }
});
</script>

<style scoped>

</style>
