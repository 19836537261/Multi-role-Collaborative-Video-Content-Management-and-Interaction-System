import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from "element-ui";
import 'element-ui/lib/theme-chalk/index.css'
import axios from "axios";//进行前后端交互的工具库
Vue.config.productionTip = false
Vue.prototype.$http=axios;
Vue.use(ElementUI)
new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
