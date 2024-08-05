import {UserType} from "../models/user";
import myAxios from "../plugins/axios";


let currentUser;

const setCurrentUserState = (user: UserType) =>{
    currentUser = user;
}
const getCurrentUserState = ():UserType =>{
    return currentUser;
}

export const getCurrentUser = async () => {

    //项目不大的时候，不缓存当前用户，可以每次页面跳转在路由守卫中远程获取当前用户
    // if(getCurrentUserState()){
    //     console.log('currentUser',currentUser)
    //     return currentUser;
    // }
    const res = await myAxios.get('/user/current');
    // @ts-ignore
    if(res.code === 0 && res.data){
        console.log('res',res.data)

        // setCurrentUserState(res.data);
        return res.data;
    }
    return null;
};
