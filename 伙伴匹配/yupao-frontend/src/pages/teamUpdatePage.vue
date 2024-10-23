<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="updateTeamData.teamName"
          name="teamName"
          label="队伍名称"
          placeholder="队伍名称"
          :rules="[{ required: true, message: '请输入队伍名' }]"
      />
      <van-field
          v-model="updateTeamData.description"
          rows = "3"
          autosize
          type="textarea"
          name="队伍描述"
          label="队伍描述"
          placeholder="队伍描述"
          :rules="[{ required: true, message: '请填写队伍描述' }]"
      />
      <van-field name="stepper" label="队伍人数">
        <template #input>
          <van-stepper v-model="updateTeamData.maxNum" max="10" min="3"/>
        </template>
      </van-field>


      <van-field
          is-link
          readonly
          name="datePicker"
          label="时间选择"
          :placeholder="updateTeamData.expireTime ?? '点击选择时间'"
          @click="showPicker = true"
      />
      <van-popup v-model:show="showPicker" position="bottom">
        <van-date-picker  v-model="updateTeamData.expireTime"
                          @confirm="showPicker = false"
                          type="datetime"
                          title="请选择过期时间"
                          @cancel="showPicker = false"
        />
      </van-popup>


      <van-field name="radio" label="单选框">
        <template #input>
          <van-radio-group v-model="updateTeamData.status" direction="horizontal">
            <van-radio name="1">公开</van-radio>
            <van-radio name="2">加密</van-radio>
            <van-radio name="3">私密</van-radio>
          </van-radio-group>
        </template>
      </van-field>

      <van-field
          v-if="updateTeamData.status === '2'"
          v-model="updateTeamData.password"
          type="password"
          name="队伍密码"
          label="队伍密码"
          placeholder="队伍密码"

          :rules="[{ required: true, message: '请填写队伍密码' }]"
      />
    </van-cell-group>
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>
<!--{{addTeamData}}-->
</template>

<script setup>
import {showFailToast, showToast} from 'vant';

import { ref,onMounted } from 'vue';
import myAxios from "../plugins/axios";
const result = ref('');
const showPicker = ref(false);
const onConfirm = ({selectedValues}) =>{
  initFormData.expireTime = selectedValues.join("-");
  showPicker.value = false;
};
const minDate = new Date(2020, 0, 1);
const maxDate = new Date(2025, 5, 1);
onMounted(async () =>{
  const res = await myAxios.get('/team/get',{
    id: 3
  })
  if(res?.code === 0){
    updateTeamData.value = res.data;

  }
  else{
    showFailToast("未获取到队伍信息");

  }
})
//自定义队伍添加对象
const updateTeamData = ref()


const  doSubmit = async() =>{
  const postData = {
    ...updateTeamData,
    status:Number(updateTeamData.status),
    expireTime: moment(updateTeamData.expireTime).format("YYYY-MM-DD")
  }
  const res = await myAxios.post("/team/add",postData)
  if(res?.code === 0){
    showToast('添加成功');
    router.push({
      path:'team',
      replace:true
    })
  }
}
</script>

<style scoped>

</style>
