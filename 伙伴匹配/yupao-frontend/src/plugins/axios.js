// Set config defaults when creating the instance
import axios from "axios";
import {useRouter} from "vue-router";

//定义全局请求头，把axios想象成一个类，myAxios是我们自定义的实例
const myAxios = axios.create({
    baseURL: 'http://127.0.0.1:8080/api'

});
myAxios.defaults.withCredentials = true;
// 添加请求拦截器
myAxios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    console.log('发送请求：',config.url)
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
myAxios.interceptors.response.use(function (response) {
    const router = useRouter();

    if(response.data ){
        // 对响应数据做点什么
        console.log('接收响应：',response.data)
        if(response.data.code === 40100){
            router.push({
                path : "/user/login"
            });
        }
    }


    return response.data;
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});



export default myAxios
