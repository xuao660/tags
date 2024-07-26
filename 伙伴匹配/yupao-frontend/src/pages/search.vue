<template>
  <form action="/">
    <van-search
        v-model="searchText"
        show-action
        placeholder="请输入搜索标签"
        @search="onSearch"
        @cancel="onCancel"
    />
  </form>


<!--  已选中的标签-->
  <van-row :gutter="[8, 8]">
    <van-col  v-for="tag in activeIds">
      <van-tag  closeable size="small" type="primary" @close="tagClose(tag)">
        {{tag}}
      </van-tag>
    </van-col>

  </van-row>

<!--  分割线-->
  <van-divider>已选中标签</van-divider>

<!--  全部标签-->
  <van-tree-select
      v-model:active-id="activeIds"
      v-model:main-active-index="activeIndex"
      :items="tagList"
  />

</template>

<script setup>
import { ref } from 'vue';
import { showToast } from 'vant';
const initTagList = [
  {
    text: '浙江',
    children: [
      { text: '杭州', id: '杭州' },
      { text: '温州', id: '温州' },
      { text: '宁波', id: '宁波' },
    ],
  },
  {
    text: '江苏',
    children: [
      { text: '南京', id: '南京' },
      { text: '无锡', id: '无锡' },
      { text: '徐州', id: '徐州' },
      { text: '芜湖', id: '芜湖' },

    ],
  },
  { text: '福建',
    children : [
      { text: '福州', id: '福州' },

    ],
  },
];
const tagList = ref(initTagList);
const searchText = ref('');
const onSearch = (val) =>{
  // console.log(tagList.value.flatMap(parentTag => parentTag.children))
  // console.log(searchText.value.includes('杭州'))
  tagList.value = initTagList;
  console.log(tagList.value)

  tagList.value = tagList.value.flatMap(parentTag => parentTag.children).filter(item => {
    return searchText.value.includes(item.text)
  })
  console.log(searchText.value.includes('杭州'))

};
const onCancel = () => showToast('取消');

const tagClose = (tag) => {
  activeIds.value = activeIds.value.filter(item => {
    if(item === tag){
      return false;
    }
    return true;
  })
};


const activeIds = ref([]);
const activeIndex = ref(0);

</script>

<style scoped>

</style>
