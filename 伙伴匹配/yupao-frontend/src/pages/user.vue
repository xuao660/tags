<template>
  <van-cell title="昵称" is-link :value="editUser.username"/>
  <van-cell title="账号" is-link :value="editUser.userAccount" />
  <van-cell title="头像" is-link >
    <img style="height: 48px" :src="editUser.avatarUrl">
  </van-cell>
  <van-cell title="性别" is-link :value="editUser.gender" @click="toEditUser('gender','性别',editUser.gender)"/>
  <van-cell title="电话"is-link  :value="editUser.phone" />
  <van-cell title="邮箱" is-link :value="editUser.email" />
  <van-cell title="创建时间" is-link :value="editUser.createTime.toISOString()" />

</template>

<script setup lang="ts">

import {useRouter} from "vue-router";

const editUser = {
  id :"1",
  username: "谢谢哦",
  userAccount:'xxa',
  avatarUrl:'https://fastly.jsdelivr.net/npm/@vant/assets/logo.png',
  gender:'男',
  phone:'1235',
  email:'12@33qq.com',
  createTime:new Date()
}
const router = useRouter();
const toEditUser =(editKey: string, editName: string,editValue: string)=>{
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
