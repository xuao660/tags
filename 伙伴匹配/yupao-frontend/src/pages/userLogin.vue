<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="userAccount"
          name="userAccount"
          label="账号"
          placeholder="账号"
          :rules="[{ required: true, message: '请填写账号' }]"
      />
      <van-field
          v-model="userPassword"
          type="password"
          name="密码"
          label="密码"
          placeholder="密码"
          :rules="[{ required: true, message: '请填写密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<script setup>

import {useRoute,useRouter} from "vue-router";
import { onMounted , ref } from 'vue';
import myAxios from "../plugins/axios";
import {showSuccessToast,showFailToast} from "vant";

const router = useRouter();

const userAccount = ref('');
const userPassword = ref('');
const onSubmit = async() => {
  const res = await myAxios.post('/user/login',{
    userAccount:userAccount.value,
    userPassword:userPassword.value
  });
  console.log("登录请求：",res)
  if(res.code == 0 && res.data){
    showSuccessToast("登录成功")
    router.replace("/");
  }else{
    showFailToast("登录失败")
  }
};

</script>

<style scoped>

</style>
