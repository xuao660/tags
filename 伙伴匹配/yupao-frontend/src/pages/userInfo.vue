<template>
  <template v-if="editUser">
    <van-cell title="昵称" is-link :value="editUser.username" @click="toEditUser('username','昵称',editUser.username)"/>
    <van-cell title="账号" is-link :value="editUser.userAccount" />
    <van-cell title="头像" is-link >
      <img style="height: 48px" :src="editUser.avatarUrl">
    </van-cell>
    <van-cell title="性别" is-link :value="editUser.gender" @click="toEditUser('gender','性别',editUser.gender)"/>
    <van-cell title="电话"is-link  :value="editUser.phone" />
    <van-cell title="邮箱" is-link :value="editUser.email" />
    <van-cell title="创建时间" is-link :value="editUser.createTime" />

  </template>

</template>

<script setup lang="ts">

import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {showFailToast, showSuccessToast} from "vant";
import {getCurrentUser} from "../state/User";


const editUser = ref();
onMounted(async() =>{
  const res = await getCurrentUser();
  if(res){
    console.log("当前用户请求：",res)
    editUser.value = res;
    showSuccessToast("获取当前用户成功")
  }else{
    showFailToast("获取当前用户失败")
  }
});



const router = useRouter();
const toEditUser =(editKey: string, editName: string,editValue: string)=>{

  console.log('editKey',editKey)
  console.log('editValue',editValue)

  router.push({
    path:'/user/edit',
    query:{
      editKey,
      editName,
      editValue
    }
  })
}
</script>

<style scoped>

</style>
