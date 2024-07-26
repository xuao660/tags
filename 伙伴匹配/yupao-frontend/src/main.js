import { createApp } from 'vue'
import App from './App.vue'
import  { Icon,Button,NavBar,Tabbar, TabbarItem ,Search,Divider ,Tag, Col, Row  } from 'vant';
import * as VueRouter from 'vue-router'
//引用路由信息文件
import routes from "./config/route.ts"

const router = VueRouter.createRouter({
    history: VueRouter.createMemoryHistory(),
    routes,
})
//将app挂载到index.html上，类似把挂饰墙上
const app = createApp(App)
app.use(Button);
app.use(NavBar);
app.use(Icon);
app.use(Tabbar);
app.use(TabbarItem);
app.use(router)
app.use(Search);
app.use(Divider);
app.use(Tag);
app.use(Col);
app.use(Row);
app.mount('#app')
