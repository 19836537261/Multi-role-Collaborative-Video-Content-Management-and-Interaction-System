import Vue from "vue";
export default {
    state:{
        role:localStorage.getItem("role")==null?"":localStorage.getItem("role"),
        expire_time:localStorage.getItem("expire_time")==null?"":localStorage.getItem("expire_time"),
        key:localStorage.getItem("key")==null?"":localStorage.getItem("key"),
        token:localStorage.getItem("token")==null?"":localStorage.getItem("token"),
        username:localStorage.getItem("username")==null?"":localStorage.getItem("username")
    },
    getters:{
        get_token(state){
            return state.token;
        },
        get_username(state){
            return state.username;
        },
        get_role(state){
            return state.role;
        },
        get_expire_time(state){
            return state.expire_time;
        }
    },
    mutations:{
        set_msg(state,payload){
            Vue.set(state,"role",payload.role[0]);
            localStorage.setItem("role",payload.role[0]);
            Vue.set(state,"expire_time",payload.expire_time);
            localStorage.setItem("expire_time",payload.expire_time);
            Vue.set(state,"token",payload.token);
            localStorage.setItem("token",payload.token);
            Vue.set(state,"key",payload.key);
            localStorage.setItem("key",payload.key);
            Vue.set(state,"username",payload.username);
            localStorage.setItem("username",payload.username);
        },
        clear_msg(){
            localStorage.clear();
        }
    }
}
