<template>
  <user-card :user-list="userList"></user-card>
  <van-empty v-if="!userList || userList.length <1 " description="内容为空"></van-empty>

</template>

<script setup lang="ts">


import {useRoute} from "vue-router";
import { onMounted , ref } from 'vue';
import myAxios from "../plugins/axios";
import UserCard from "../components/UserCard.vue";

const route = useRoute();
//js 的解构赋值语法，从 route.query 对象中提取 tags 属性的值
const userList = ref([]);
//前端get请求传递数组数据一种方法
// @ts-ignore


onMounted(async () => {

  const userListData = await myAxios.get('/user/recommand', {
    params: {
      pageNum : 90200 ,
      pageSize : 10
    }
  }).then(function (response) {
    console.log('/user/searchUserByTags success', response);
    return response.data;
  })
      .catch(function (error) {
        console.log('/user/searchUserByTags error', error);
      });

  if(userListData){
    console.log('userListData',userListData);

    userList.value = userListData;
  }
})



const mockUser = [{
  id :'10001',
  username: 'xxx',
  plantCode:'10921',
  userAccount:'xxx',
  avatarUrl:'https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg',
  profile:'大家好，我是小明！我今年只有六岁，但我已经是这个班级里的宇宙大BOSS了！我的爱好是吃糖、玩泥巴，还有让老师头疼！我最厉害的技能是每天都能睡过头，还能在课堂上打呼噜。虽然我小，但我的梦想很大——我要成为世界上最牛的冰淇淋品尝家！谢谢大家！记得给我多点糖哦！',
  gender:'xxx',
  phone:'xxx',
  email:'xxx',
  createTime:Date,
  // tags:tags
},{
  id :'10002',
  username: 'yyy',
  plantCode:'10922',
  userAccount:'yyy',
  profile:'大家好，我叫小月！今天是我的生日，我已经长大一岁啦！我最喜欢的事情是吃蛋糕和玩电子游戏。我还喜欢和朋友们一起玩耍，特别是在公园里荡秋千。我希望今年长大后可以学会骑自行车，然后去探险。谢谢大家来参加我的生日派对，希望大家今天都玩得开心',
  avatarUrl:'https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg',
  gender:'yyy',
  phone:'yy',
  email:'yy',
  createTime:Date,
  // tags:tags
}]
</script>

<style scoped>

</style>
