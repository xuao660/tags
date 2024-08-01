// Set config defaults when creating the instance
import axios from "axios";


//定义全局请求头，把axios想象成一个类，myAxios是我们自定义的实例
const myAxios = axios.create({
    baseURL: 'http://localhost:8080/api'
});

// 添加请求拦截器
axios.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    console.log('发送请求：',config.url)
    return config;
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
});

// 添加响应拦截器
axios.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    console.log('接收响应：',response.data)

    return response;
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});



export default myAxios
