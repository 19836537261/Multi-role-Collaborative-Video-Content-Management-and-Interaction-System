import Vue from 'vue'
import VueRouter from 'vue-router'
import ManagerLogin from "@/views/ManagerLogin";
import UserIndex from "@/views/UserIndex";
import VideoPublish from "@/views/VideoPublish";
import UserCenter from "@/views/UserCenter";
import VideoEdit from "@/views/VideoEdit";
import CollectionEdit from "@/views/CollectionEdit";
import VideoDetail from "@/views/VideoDetail";
import ManagerIndex from "@/manager_views/ManagerIndex";
import VideoDetailManager from "@/manager_views/VideoDetailManager";
import CollectionEditManager from "@/manager_views/CollectionEditManager";
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: ManagerLogin
  },
  {
    path: '/user_index',
    name: 'user_index',
    component: UserIndex
  },
  {
    path: '/manager_index',
    name: 'manager_index',
    component: ManagerIndex
  },
  {
    path:"/video_publish",
    name: "video_publish",
    component: VideoPublish
  },
  {
    path: "/user_center",
    name: "user_center",
    component: UserCenter
  },
  {
    path:"/video_edit",
    name: "video_edit",
    component: VideoEdit
  },
  {
    path: "/collection_edit",
    name:"collection_edit",
    component: CollectionEdit
  },
  {
    path:"/video_detail",
    name: "video_detail",
    component: VideoDetail
  },
  {
    path: "/video_detail_manager",
    name: "video_detail_manager",
    component: VideoDetailManager
  },
  {
    path: "/collection_audit",
    name: "collection_audit",
    component: CollectionEditManager
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
