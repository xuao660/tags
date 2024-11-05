<template>
  <van-form @submit="onSubmit">
    <van-cell-group inset>
      <van-field
          v-model="addTeamData.teamName"
          name="teamName"
          label="队伍名称"
          placeholder="队伍名称"
          :rules="[{ required: true, message: '请输入队伍名' }]"
      />
      <van-field
          v-model="addTeamData.description"
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
          <van-stepper v-model="addTeamData.maxNum" max="10" min="3"/>
        </template>
      </van-field>


      <van-field
          is-link
          readonly
          name="datePicker"
          label="时间选择"
          :placeholder="addTeamData.expireTime ?? '点击选择时间'"
          @click="showPicker = true"
      />
      <van-popup v-model:show="showPicker" position="bottom">
        <van-date-picker  v-model="addTeamData.expireTime"
                          @confirm="showPicker = false"
                          type="datetime"
                          title="请选择过期时间"
                          @cancel="showPicker = false"
        />
      </van-popup>


      <van-field name="radio" label="单选框">
        <template #input>
          <van-radio-group v-model="addTeamData.status" direction="horizontal">
            <van-radio name="1">公开</van-radio>
            <van-radio name="2">加密</van-radio>
            <van-radio name="3">私密</van-radio>
          </van-radio-group>
        </template>
      </van-field>

      <van-field
          v-if="addTeamData.status === '2'"
          v-model="addTeamData.password"
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
import { showToast } from 'vant';

import { ref } from 'vue';
const result = ref('');
const showPicker = ref(false);
const onConfirm = ({selectedValues}) =>{
  console.log('selectedValues:'+selectedValues.join("-"))
  addTeamData.expireTime = selectedValues.join("-");
  showPicker.value = false;
};
const minDate = new Date(2020, 0, 1);
const maxDate = new Date(2025, 5, 1);
const intTeam = {
  "captainId": 3,
  "description": "测试描述 ",
  "expireTime":[2024,10,10],
  "maxNum": "10",
  "password": "",
  "status": 0,
  "userID":3,
  "teamName": "测试小队2"
}
//自定义队伍添加对象
const addTeamData = ref(intTeam)
const  doSubmit = async() =>{
  const postData = {
    ...addTeamData,
    status:Number(addTeamData.status),
    expireTime: moment(addTeamData.expireTime).format("YYYY-MM-DD")
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
