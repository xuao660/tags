<template>
<!-- <van-nav-bar></van-nav-bar> 是页面上方通用组件-->
  <van-nav-bar
      title="标题"
      left-text=""
      right-text="按钮"
      left-arrow
      @click-left="onClickLeft"
      @click-right="onClickRight"
  >
  <!-- 插槽 可以自定义右上角内容 搜索框或者文字等-->
    <template #right>
      <van-icon name="search" size="18" />
    </template>
  </van-nav-bar>
  <!-- <van-nav-bar></van-nav-bar> 是页面上方通用组件-->


<!--  不同的页面上下组件相同，根据点击下方tabbar切换展示的页面-->
  <div id="content">
<router-view></router-view>

  </div>

<!-- 页面下方通用组件。v-model 指令会将 <van-tabbar> 组件的 name 属性绑定到 active 变量-->
  <van-tabbar route @change="onChange">
    <van-tabbar-item replace to="/user/login" icon="home-o" name="index">登录</van-tabbar-item>
    <van-tabbar-item replace to="/" icon="home-o" name="index">主页</van-tabbar-item>
    <van-tabbar-item replace to="/labels" icon="search" name="labels">标签</van-tabbar-item>
    <van-tabbar-item replace to="/team" icon="friends-o" name="team">队伍</van-tabbar-item>
    <van-tabbar-item replace to="/user" icon="setting-o" name="user"> 个人</van-tabbar-item>
  </van-tabbar>


</template>

<script setup>
import { showToast } from 'vant';
import {  useRouter } from 'vue-router'

const router = useRouter()
const onClickLeft = () => {
  router.back();
};
const onClickRight = () => {
  router.push('/search')

}
//v-model="active" 控制高亮。通过监听active值的变化 修改高亮按钮
// const onChange = (index) => showToast(`标签 ${index}`);
</script>

<style scoped>

</style>
