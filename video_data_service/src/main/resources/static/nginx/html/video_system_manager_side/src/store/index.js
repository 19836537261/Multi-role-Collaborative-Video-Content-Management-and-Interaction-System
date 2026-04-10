import Vue from 'vue'
import Vuex from 'vuex'
import UserModule from "@/store/modules/userModule";
import VideoModule from "@/store/modules/videoModule";
import UtilModule from "@/store/modules/utilModule";
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    UserModule,
    VideoModule,
    UtilModule
  }
})
