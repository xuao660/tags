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
import {useRoute} from "vue-router";
import {ref} from "vue";
const route = useRoute();
const editUser = ref({
  editKey:route.query.editKey,
  editName:route.query.editName,
  editValue:route.query.editValue,

})
const onSubmit = (values) => {
  console.log('submit', values);
};
</script>

<style scoped>

</style>
