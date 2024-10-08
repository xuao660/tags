import labels from '../pages/labels.vue'
import team from '../pages/team.vue'
import user from '../pages/user.vue'
import index from '../pages/index.vue'
import search from '../pages/search.vue'
import userEdit from '../pages/userEdit.vue'
import userList from '../pages/userList.vue'
import userLogin from '../pages/userLogin.vue'

//定义路由信息
const routes = [
    { path: '/', component: index },
    { path: '/labels', component: labels },
    { path: '/team', component: team },
    { path: '/user', component: user },
    { path: '/search', component: search },
    { path: '/user/edit', component: userEdit },
    { path: '/user/list', component: userList },
    { path: '/user/login', component: userLogin },

]
//将当前route文件暴露出去，方便main.js引用
export default routes
