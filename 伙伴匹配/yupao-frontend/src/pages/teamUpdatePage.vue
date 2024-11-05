<template>
  <van-form @submit="doSubmit">
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

      <van-field
          is-link
          readonly
          name="datePicker"
          label="时间选择"
          :placeholder="updateTeamData.expireTime ?? '点击选择时间'"
          @click="showPicker = true"
      />
      <van-popup v-model:show="showPicker" position="bottom">
        <van-date-picker
                          @confirm="onConfirm"
                          type="datetime"
                          title="请选择过期时间"
                          @cancel="showPicker = false"
        />
      </van-popup>


      <van-field name="radio" label="单选框">
        <template #input>
          <van-radio-group v-model="updateTeamData.status" direction="horizontal">
            <van-radio name="0">公开</van-radio>
            <van-radio name="1">私密</van-radio>
            <van-radio name="2">加密</van-radio>
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
<!--{{updateTeamData}}-->
</template>

<script setup>
import {showFailToast, showToast} from 'vant';

import { ref,onMounted } from 'vue';
import myAxios from "../plugins/axios";
const result = ref('');
import {useRoute} from "vue-router";

const showPicker = ref(false);
const onConfirm = ({selectedValues}) =>{
  console.log('selectedValues:',selectedValues.join("-"))
  updateTeamData.value.expireTime = selectedValues.join("-");
  showPicker.value = false;
};
const minDate = new Date(2020, 0, 1);
const maxDate = new Date(2025, 5, 1);

//自定义队伍添加对象
const route = useRoute();
const id = route.query.id;

onMounted(async () =>{
  const res = await myAxios.get('/team/get',{
    params:{
      id:id
    }
  })
  if(res?.code === 0){
    updateTeamData.value = res.data;
    updateTeamData.value.status = updateTeamData.value.status.toString();
    console.log(updateTeamData.value.expireTime)

  }
  else{
    showFailToast("未获取到队伍信息");

  }
})

const updateTeamData = ref({})


  const  doSubmit = async() =>{
    const postData = {
      ...updateTeamData.value,
      status:Number(updateTeamData.value.status),
      // expireTime: moment(updateTeamData.value.expireTime).format("YYYY-MM-DD")
    }
  console.log('postData:',postData)
    const res = await myAxios.post("/team/update",postData)
    if(res?.code === 0){
      showToast('更新成功');
      router.push({
        path:'team',
        replace:true
      })
    }
  }
</script>

<style scoped>

</style>
