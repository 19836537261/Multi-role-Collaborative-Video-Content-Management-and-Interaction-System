<template>
  <div class="user-homepage">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <!-- Logo和网站名称 -->
        <div class="logo-section">
          <i class="el-icon-video-play logo-icon"></i>
          <span class="site-name">视频分享平台</span>
        </div>
        <!-- 搜索框 -->
        <div class="search-section">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索视频、创作者..."
              class="search-input"
              @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">
                <i class="el-icon-search"></i>
              </el-button>
            </template>
          </el-input>
        </div>
        <!-- 用户操作区 -->
        <div class="user-section">
          <div v-if="!isLoggedIn">
            <el-button type="primary" @click="handleLogin">登录/注册</el-button>
            <el-dialog title="登录/注册" :visible.sync="dialogFormVisible" append-to-body>
              <el-tabs v-model="activeName" @tab-click="switchLoginOrRegister">
                <el-tab-pane label="用户登录" name="login">
                  <el-form :model="form">
                    <el-form-item label="用户名" :label-width="formLabelWidth">
                      <el-input v-model="form.username" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" :label-width="formLabelWidth">
                      <el-input v-model="form.password" autocomplete="off" show-password></el-input>
                    </el-form-item>
                    <el-button @click="user_login">登录</el-button>
                    <el-button type="primary" @click="dialogFormVisible = false">取消</el-button>
                  </el-form>
                </el-tab-pane>
                <el-tab-pane label="用户注册" name="register">
                  <el-form :model="register_form">
                    <el-form-item label="用户名" :label-width="formLabelWidth">
                      <el-input v-model="register_form.username" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" :label-width="formLabelWidth">
                      <el-input v-model="register_form.password" autocomplete="off" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="确认密码" :label-width="formLabelWidth">
                      <el-input v-model="register_form.password_again" autocomplete="off" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱" :label-width="formLabelWidth">
                      <el-input v-model="register_form.email" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-form-item label="手机号" :label-width="formLabelWidth">
                      <el-input v-model="register_form.phone" autocomplete="off"></el-input>
                    </el-form-item>
                    <el-button @click="register_one_user">注册</el-button>
                    <el-button type="primary" @click="dialogFormVisible = false">取消</el-button>
                  </el-form>
                </el-tab-pane>
              </el-tabs>
            </el-dialog>
          </div>
          <el-dropdown v-else @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar" class="user-avatar">
                {{ userInfo.name.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userInfo.name }}</span>
              <i class="el-icon-arrow-down"></i>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i>个人中心
                </el-dropdown-item>
<!--                <el-dropdown-item command="upload">-->
<!--                  <i class="el-icon-upload"></i>发布视频-->
<!--                </el-dropdown-item>-->
<!--                <el-dropdown-item command="favorites">-->
<!--                  <i class="el-icon-star"></i>我的收藏-->
<!--                </el-dropdown-item>-->
<!--                <el-dropdown-item command="feedback">-->
<!--                  <i class="el-icon-chat-dot-round"></i>意见反馈-->
<!--                </el-dropdown-item>-->
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

        </div>

      </div>
    </header>
    <!-- 主要内容区 -->
    <main class="main-content">
      <!-- 分类导航 -->
      <section class="category-nav">
        <div class="category-container">
          <el-scrollbar>
            <div class="category-list">
              <el-button
                  v-for="category in categories"
                  :key="category.id"
                  :type="activeCategory === category.id ? 'primary' : ''"
                  class="category-btn"
                  @click="handleCategoryChange(category.id)"
              >
                {{ category.name }}
              </el-button>
            </div>
          </el-scrollbar>
        </div>
      </section>
      <!-- 轮播图/推荐区 -->
      <section class="banner-section">
        <el-carousel :interval="5000" height="300px">
          <el-carousel-item v-for="item in announces" :key="item.id">
            <div class="carousel-item" :style="{ backgroundImage: `url(${item.img_url})` }">
              <div class="carousel-content">
                <h3>{{ item.title }}</h3>
                <el-button type="primary" size="large" @click="pre_view(item)">查看详情</el-button>
                <el-dialog
                    title="提示"
                    :visible.sync="open_announcement_prew"
                    width="80%"
                    :before-close="close_prew" append-to-body>
                      <span>
                        <el-form :model="announcement_prew"
                                 label-width="100px"
                                 class="demo-ruleForm">
                          <el-form-item label="公告标题" prop="title">
                            <el-input v-model="announcement_prew.title"></el-input>
                          </el-form-item>
                          <el-form-item label="公告内容" prop="content">
                            <el-input v-model="announcement_prew.content" type="textarea"></el-input>
                          </el-form-item>
                          <el-form-item label="封面设置" :label-width="'120px'">
                            <div class="cover-upload">
                              <div class="cover-uploader">
                                <img v-if="announcement_prew.img_url" :src="announcement_prew.img_url" class="cover-image" />
                              </div>
                            </div>
                          </el-form-item>
                        </el-form>
                      </span>
                  <span slot="footer" class="dialog-footer">
                        <el-button @click="open_announcement_prew = false">关 闭</el-button>
                      </span>
                </el-dialog>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 视频列表 -->
      <section class="video-section">
        <div class="section-header">
          <h2 class="section-title">{{ getSectionTitle() }}</h2>
          <el-dropdown @command="handleSortChange">
            <el-button>
              排序方式: {{ sortOptions.find(opt => opt.value === sortBy)?.label }}
              <i class="el-icon-arrow-down"></i>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                    v-for="option in sortOptions"
                    :key="option.value"
                    :command="option.value">
                  {{ option.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        <div class="video-grid">
          <div
              v-for="video in videoList"
              :key="video.id"
              class="video-card"
              @click="handleVideoClick(video)">

            <div class="video-thumbnail" :style="{backgroundImage: `url(${video.coverImage})`}">

              <div class="video-views">

                <i class="el-icon-thumb"></i>
                {{ video.likeCount }}
              </div>
            </div>
            <div class="video-info">
              <h3 class="video-title">{{ video.title }}</h3>
              <div class="video-meta">
                <span class="uploader">{{ video.user.username }}</span>
                <span class="upload-time">{{formatDate(video.createTime) }}</span>
              </div>
              <div class="video-stats"></div>
            </div>
          </div>
        </div>
        <div class="load-more">
          <el-button
              :loading="loading"
              @click="loadMoreVideos"
              v-if="hasMore"
          >
            {{ loading ? '加载中...' : '加载更多' }}
          </el-button>
          <div v-else class="no-more">没有更多视频了</div>
        </div>
      </section>
    </main>

    <!-- 底部信息 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>专业的视频分享平台，为用户提供高质量的在线视频体验</p>
        </div>
        <div class="footer-section">
          <h4>联系我们</h4>
          <p>邮箱: contact@videoshare.com</p>
          <p>电话: 400-123-4567</p>
        </div>
        <div class="footer-section">
          <h4>关注我们</h4>
          <div class="social-links">
            <i class="el-icon-share"></i>
            <i class="el-icon-chat-line-round"></i>
            <i class="el-icon-mobile-phone"></i>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2026 视频分享平台 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script>
import {mapMutations,mapGetters} from "vuex";

export default {
  name: "UserIndex",
  data() {
    return {
      isLoggedIn: false,
      searchKeyword: '',
      activeCategory: 'recommend',
      activeCategoryIndex:0,
      sortBy: 'id',
      page: 1,
      loading: false,
      hasMore: true,
      dialogFormVisible:false,
      formLabelWidth: '120px',
      activeName:"login",
      form:{
        username:"",
        password:""
      },
      register_form:{
        username:"",
        password:"",
        password_again:"",
        email:"",
        phone:""
      },
      // 模拟数据
      userInfo: {
        name: '用户昵称',
        avatar: '',
        role:""
      },
      source_data:[],
      categories: [],
      sortOptions: [
        { value: 'id', label: '默认排序' },
        { value: 'create_time', label: '最新发布' },
        { value: 'like_count', label: '最多点赞' }

      ],
      videoList: [
        {
          id: 1,
          title: '这是一个示例视频标题，可能会比较长一些',
          coverImage: 'http://localhost:8026/imgs/1770799924246.png',
          username: '创作者A',
          createTimeTime: '2天前',
          likeCount: 1234
        },
        // 更多模拟视频数据...
      ],
      announces:[],
      open_announcement_prew:false,
      announcement_prew:{
        title:"",
        content:"",
        img_url:""
      }
    }
  },
  mounted() {
    this.$http({
      url:"/api/video/get_all_video_kinds"
    }).then(res=>{
      //console.log(res.data);
      this.source_data=res.data.dat;
      for (let i = 0; i < res.data.dat.length; i++) {
        this.categories.push({
          id:res.data.dat[i].type,
          name:res.data.dat[i].type,
          counter:0
        });
      }
      this.activeCategoryIndex=0;
      this.activeCategory=this.categories[this.activeCategoryIndex].id;
      this.loadVideos();
    });
  },
  created() {
    // console.log(this.get_token);
    // console.log(this.get_username);
    let expire_time=localStorage.getItem("expire_time");
    let now=new Date().getTime();
    if (expire_time<now){
      localStorage.clear();
      this.isLoggedIn=false;
    }else {
      if (this.get_token!=null&&this.get_username!=null&&this.get_token.length!=0&&this.get_username.length!=0){
        this.isLoggedIn=true;
        this.userInfo.name=this.get_username;
        this.userInfo.role=this.get_role;
      }else {
        this.isLoggedIn=false;
      }
    }
    this.get_announce_data();
  },
  computed:{
    ...mapGetters(['get_token','get_username','get_role'])
  },
  methods: {
    ...mapMutations(['set_msg']),
    get_announce_data(){
      this.$http({
        url:"/api/announcement/list_all_announce"
      }).then(res=>{
        this.announces=res.data.dat;
      });
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour:'2-digit',
        minute:'2-digit'
      })
    },
    register_one_user(){
      console.log(this.register_form);
      if (this.register_form.phone.length==0||this.register_form.email.length==0||this.register_form.password.length==0||this.register_form.password_again.length==0||this.register_form.username.length==0){
        this.$message.warning("信息填写不为空");
      }else if (this.register_form.password!=this.register_form.password_again)
      {
        this.$message.warning("密码不一致");
      }else {
        this.$http({
          url:"/api/user/add_user",
          method:"POST",
          headers:{
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            username:this.register_form.username,
            password:this.register_form.password,
            email:this.register_form.email,
            phone:this.register_form.phone
          }
        }).then(res=>{
          if (res.data.result){
            this.$message.success("注册成功，请登录");
            this.register_form.username="";
            this.register_form.phone="";
            this.register_form.email="";
            this.register_form.password="";
            this.register_form.password_again="";
            this.dialogFormVisible=false;
          }else {
            this.$message.error("注册失败");
          }
        });
      }
    },
    handleSearch() {
      console.log('搜索关键词:', this.searchKeyword);
    },
    switchLoginOrRegister(e){
      console.log(e);
    },

    handleLogin() {
      this.dialogFormVisible=true;
    },

    handleUserCommand(command) {
      console.log('用户操作:', command)
      this.$emit('user-command', command)
      if (command=='upload'){
        if (this.userInfo.role=='PUBLISHER'){
          this.$router.push('/video_publish');
        }else {
          if (this.userInfo.role=='USER'){
            this.$message({
              type:"error",
              message:"请先在个人中心申请视频发布者身份！"
            });
          }
        }
      }else if (command=="profile"){
        this.$router.push("/user_center");
      }else if (command=="logout"){
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 清除登录状态
          localStorage.clear();
          window.location.reload();
          this.$message.success('已退出登录')
        }).catch(() => {})
      }
    },
    handleCategoryChange(categoryId) {
      this.activeCategory = categoryId;
      for (let i = 0; i < this.categories.length; i++) {
        if (this.categories[i].id==this.activeCategory)
        {
          this.activeCategoryIndex=i;
          break;
        }
      }
      this.page = 1;
      this.categories[this.activeCategoryIndex].counter=0;
      this.loadVideos()
    },

    handleSortChange(sortType) {
      this.sortBy = sortType;
      console.log(sortType);
      this.page = 1
      this.categories[this.activeCategoryIndex].counter=0;
      this.loadVideos();
    },
    handleVideoClick(video) {
      if (this.get_token==null||this.get_token==undefined||this.get_token.length==0){
        this.$message.warning("登录后可查看视频");
        this.dialogFormVisible=true;
      }else {
        this.$router.push({
          name:"video_detail",
          params:{
            video_msg:video
          }
        });
      }

    },
    loadMoreVideos() {
      this.loading = true;
      console.log(this.categories[this.activeCategoryIndex]);
      this.$http({
        url:"/api/video/get_videos_by_type",
        method:"POST",
        headers:{
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          category:this.activeCategory,
          page:this.categories[this.activeCategoryIndex].counter,
          sort:this.sortBy
        }
      }).then(res=>{
        console.log(res.data);
        for (let i = 0; i < res.data.dat.length; i++) {
          this.videoList.push(res.data.dat[i]);
        }
        if (res.data.dat.length==0){
          this.hasMore=false;
        }
        this.categories[this.activeCategoryIndex].counter++;
      });
      this.loading=false;
    },
    loadVideos() {
      this.videoList=[];
      this.hasMore=true;
      for (let i = 0; i < this.categories.length; i++) {
        if (this.categories[i].id==this.activeCategory)
        {
          this.activeCategoryIndex=i;
          this.$http({
            url:"/api/video/get_videos_by_type",
            method:"POST",
            headers:{
              'Content-Type':"application/x-www-form-urlencoded"
            },
            data:{
              category:this.activeCategory,
              page:this.categories[this.activeCategoryIndex].counter,
              sort:this.sortBy
            }
          }).then(res=>{
            //console.log(res.data);
            this.videoList=res.data.dat;
            this.categories[this.activeCategoryIndex].counter++;
          });
          break;
        }else {
          this.categories[i].counter=0;
        }
      }
    },
    getSectionTitle() {
      const category = this.categories.find(cat => cat.id === this.activeCategory)
      return category ? category.name : '推荐视频'
    },
    user_login(){
      if (this.form.username.length==0||this.form.password.length==0){
        this.$message({
          type:"error",
          message:"登录信息不为空"
        });
      }else {
        this.$http({
          url:"/api/user/user_login",
          method:"POST",
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          data:{
            username:this.form.username,
            password:this.form.password
          }
        }).then(res=>{
          if (res.data.result){
            this.set_msg(res.data.dat);
            if (res.data.dat.role[0]=="USER"){
              this.set_msg(res.data.dat);
              this.userInfo.name=this.form.username;
              this.userInfo.role="USER";
              this.isLoggedIn=true;
              this.dialogFormVisible=false;
            }else if (res.data.dat.role[0]=="ADMIN"){
              this.$router.push("/");
            }else if (res.data.dat.role[0]=='PUBLISHER'){
              this.set_msg(res.data.dat);
              this.userInfo.name=this.form.username;
              this.userInfo.role="PUBLISHER";
              this.isLoggedIn=true;
              this.dialogFormVisible=false;
            }
          }
          else {
            this.$message({
              type:"warning",
              message:"登录失败"
            });
            this.loading=false;
          }
        });
      }
    },
    close_prew(){
      this.open_announcement_prew=false;
    },
    pre_view(announce_obj){
      console.log(announce_obj);
      this.open_announcement_prew=true;
      this.announcement_prew=announce_obj;
    }
  }
}
</script>

<style scoped>
.user-homepage {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 头部样式 */
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  height: 64px;
}

.logo-section {
  display: flex;
  align-items: center;
  margin-right: 40px;
}

.logo-icon {
  font-size: 28px;
  color: #409EFF;
  margin-right: 8px;
}

.site-name {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.search-section {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.user-section {
  margin-left: auto;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-avatar {
  margin-right: 8px;
  background-color: #409EFF;
}

.user-name {
  margin-right: 4px;
  color: #333;
}

/* 主要内容样式 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 分类导航 */
.category-nav {
  margin-bottom: 20px;
}

.category-container {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
}

.category-list {
  display: flex;
  gap: 8px;
}

.category-btn {
  white-space: nowrap;
}

/* 轮播图 */
.banner-section {
  margin-bottom: 30px;
}

.carousel-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding-left: 40px;
}

.carousel-content h3 {
  color: #fff;
  font-size: 28px;
  margin-bottom: 8px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.carousel-content p {
  color: #fff;
  font-size: 16px;
  margin-bottom: 20px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

/* 视频区域 */
.video-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  color: #333;
  font-size: 24px;
}

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.video-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  background: #fff;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.video-thumbnail {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
  overflow: hidden;
}

.video-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-views {
  position: absolute;
  bottom: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 12px;
}

.video-title {
  font-size: 14px;
  line-height: 1.4;
  margin: 0 0 8px 0;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #999;
}

.uploader {
  color: #666;
}

.video-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20px 0;
}

.no-more {
  color: #999;
  font-size: 14px;
}

/* 底部样式 */
.footer {
  background: #333;
  color: #fff;
  margin-top: 40px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
}

.footer-section h4 {
  margin-bottom: 16px;
  color: #fff;
}

.footer-section p {
  margin: 8px 0;
  color: #ccc;
  line-height: 1.6;
}

.social-links {
  display: flex;
  gap: 16px;
  font-size: 20px;
}

.social-links i {
  cursor: pointer;
  transition: color 0.3s;
}

.social-links i:hover {
  color: #409EFF;
}

.footer-bottom {
  border-top: 1px solid #555;
  text-align: center;
  padding: 20px;
  color: #ccc;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 16px;
  }

  .logo-section {
    margin-right: 20px;
  }

  .site-name {
    font-size: 18px;
  }

  .search-section {
    margin: 0 20px;
  }

  .main-content {
    padding: 16px;
  }

  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .carousel-content h3 {
    font-size: 20px;
  }

  .carousel-content p {
    font-size: 14px;
  }
}
.cover-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 320px;
  height: 180px;
}
.cover-uploader:hover {
  border-color: #409EFF;
}

.cover-image {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}
</style>
