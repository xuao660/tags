<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="editUser.editValue"
          :name="editUser.editKey"
          :label="editUser.editName"
          :placeholder="`请输入${editUser.editName}`"
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
import {ref} from "vue";
import {showSuccessToast,showFailToast} from "vant";
import {getCurrentUser} from "../state/User";
import myAxios from "../plugins/axios";

const route = useRoute();
const router = useRouter();

const editUser = ref({
  editKey:route.query.editKey,
  editName:route.query.editName,
  editValue:route.query.editValue,

})
console.log('editValue:', editUser.value.editValue);



const onSubmit = async (values) => {
  const currentUser = await getCurrentUser();
  if (currentUser) {
    console.log("当前用户：", currentUser);
  } else {
    showFailToast("用户未登录");
    router.push('/user/login');
  }
  const res = await myAxios.post('/user/update', {
    id: currentUser.id,
    [editUser.value.editKey]:editUser.value.editValue
  });
  if(res.code === 0 && res.data >0){

    showSuccessToast("修改成功");
    router.back();

  }else{
    showFailToast("修改失败")
  }
};
</script>

<style scoped>

</style>
