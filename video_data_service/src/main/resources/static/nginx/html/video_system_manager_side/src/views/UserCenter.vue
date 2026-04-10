<template>
  <div class="user-center-container">
    <!-- 顶部导航栏 -->
    <el-header class="main-header">
      <div class="header-content">
        <div class="logo-section">
          <span class="system-name">视频分享平台</span>
        </div>
        <div class="user-menu">
          <el-dropdown trigger="click" @command="handleUserCommand">
            <span class="user-info">
              <el-avatar
                  :size="36"
                  :src="userInfo.avatar"
                  class="user-avatar">
                {{ userInfo.username ? userInfo.username.charAt(0) : 'A' }}
              </el-avatar>
              <span class="username">{{ userInfo.username || 'admin' }}</span>
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="personal" icon="el-icon-user">
                返回首页
              </el-dropdown-item>
              <el-dropdown-item command="publish" icon="el-icon-video-camera" v-if="this.userInfo.role=='PUBLISHER'">
                发布视频
              </el-dropdown-item>
              <el-dropdown-item command="favorite" icon="el-icon-star">
                我的点赞
              </el-dropdown-item>
              <el-dropdown-item command="feedback" icon="el-icon-chat-line-round">
                意见反馈
              </el-dropdown-item>
              <el-dropdown-item divided command="logout" icon="el-icon-switch-button">
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <div class="main-content">
      <el-container>
        <!-- 左侧侧边栏 -->
        <el-aside width="220px" class="sidebar">
          <el-menu
              :default-active="activeMenu"
              class="user-center-menu"
              @select="handleMenuSelect"
          >
            <el-menu-item index="profile">
              <i class="el-icon-user-solid"></i>
              <span>个人信息</span>
            </el-menu-item>

            <el-menu-item index="videos" v-if="this.userInfo.role=='PUBLISHER'">
              <i class="el-icon-video-camera-solid"></i>
              <span>我的视频</span>
              <el-badge v-if="videoCount > 0" :value="videoCount" class="item" />
            </el-menu-item>

            <el-menu-item index="categories" v-if="this.userInfo.role=='PUBLISHER'">
              <i class="el-icon-setting"></i>
              <span>视频分类</span>
            </el-menu-item>

            <el-menu-item index="favorites">
              <i class="el-icon-star-on"></i>
              <span>我的点赞</span>
            </el-menu-item>

            <el-menu-item index="feedback">
              <i class="el-icon-chat-line-round"></i>
              <span>意见反馈</span>
            </el-menu-item>
            <el-menu-item index="apply" v-if="userInfo.role=='USER'">
              <i class="el-icon-chat-line-round"></i>
              <span>申请发布者身份</span>
            </el-menu-item>
          </el-menu>
          <!-- 发布视频按钮 -->
          <div class="publish-btn-section" v-if="this.userInfo.role=='PUBLISHER'">
            <el-button
                type="primary"
                icon="el-icon-video-camera"
                class="publish-btn"
                @click="$router.push('/video_publish')"
            >
              发布视频
            </el-button>
          </div>
        </el-aside>

        <!-- 右侧内容区 -->
        <el-main class="content-area">
          <!-- 个人信息模块 -->
          <div v-if="activeMenu === 'profile'" class="profile-section">
            <el-card class="profile-card">
              <div slot="header" class="card-header">
                <span>个人信息</span>
                <el-button
                    type="primary"
                    icon="el-icon-edit"
                    size="small"
                    @click="editProfile"
                >
                  编辑资料
                </el-button>
              </div>

              <div class="profile-info">
                <div class="avatar-section">
                  <el-avatar
                      :size="120"
                      :src="userInfo.avatar"
                      class="profile-avatar"
                  >
                    {{ userInfo.username ? userInfo.username.charAt(0) : 'A' }}
                  </el-avatar>
                </div>

                <div class="info-section">
                  <div class="info-item">
                    <span class="label">用户名：</span>
                    <span class="value">{{ userInfo.username }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">角色：</span>
                    <span class="value">{{ userInfo.role }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">邮箱：</span>
                    <span class="value">{{ userInfo.email }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">手机号：</span>
                    <span class="value">{{ userInfo.phone }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">注册时间：</span>
                    <span class="value">{{ formatDate(userInfo.createTime) }}</span>
                  </div>
                </div>
              </div>

              <div class="stats-section">
                <el-row :gutter="20">
                  <el-col :span="6">
                    <div class="stat-item">
                      <div class="stat-number">{{ userStats.videoCount }}</div>
                      <div class="stat-label">发布视频</div>
                    </div>
                  </el-col>
                  <el-col :span="6">
                    <div class="stat-item">
                      <div class="stat-number">{{ userStats.favoriteCount }}</div>
                      <div class="stat-label">获赞数</div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </div>
          <!-- 我的视频模块 -->
          <div v-else-if="activeMenu === 'videos'" class="videos-section">
            <el-card>
              <div slot="header">
                <span>{{ getMenuTitle(activeMenu) }}</span>
              </div>
              <div>
                <div class="section-header">
                  <h3>我的视频</h3>
                  <div class="header-actions">
                    <!--                <el-select-->
                    <!--                    v-model="sortBy"-->
                    <!--                    placeholder="排序方式"-->
                    <!--                    size="small"-->
                    <!--                    style="width: 120px; margin-right: 10px;">-->
                    <!--                  <el-option label="最新发布" value="latest"></el-option>-->
                    <!--                  <el-option label="最多点赞" value="likes"></el-option>-->
                    <!--                </el-select>-->
                    <el-button
                        type="primary"
                        icon="el-icon-video-camera"
                        @click="$router.push('/video_publish')">
                      发布新视频
                    </el-button>
                  </div>
                </div>
                <el-row :gutter="20" class="video-list">
                  <el-col
                      v-for="video in page_videos"
                      :key="video.id"
                      :xs="12"
                      :sm="8"
                      :md="6"
                      :lg="6"
                      :xl="4">
                    <el-card class="video-card" shadow="hover">
                      <div class="video-cover" @click="playVideo(video)">
                        <img :src="video.cover" alt="视频封面" />
                        <!--                    <div class="video-duration">{{ video.duration }}</div>-->
                        <div class="video-views">
                          <i class="el-icon-collection-tag"></i> {{ video.views }}
                        </div>
                      </div>
                      <div class="video-info">
                        <h4 class="video-title" :title="video.title">{{ video.title }}</h4>
                        <div class="video-meta">
                          <span class="video-time">{{ formatDate(video.createTime) }}</span>
                          <div class="video-actions">
                            <el-button
                                type="text"
                                icon="el-icon-edit"
                                @click="editVideo(video)"
                            ></el-button>
                            <el-button
                                type="text"
                                icon="el-icon-delete"
                                @click="deleteVideo(video)"
                            ></el-button>
                          </div>
                        </div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>

                <!-- 分页 -->
                <el-pagination
                    v-if="totalVideos > 0"
                    @current-change="handlePageChange"
                    :current-page="currentPage"
                    :page-size="pageSize"
                    :total="totalVideos"
                    layout="prev, pager, next, jumper"
                    class="pagination"
                >
                </el-pagination>
              </div>
            </el-card>
          </div>
          <div v-else-if="activeMenu === 'categories'" class="videos-section">
            <el-card>
              <div slot="header">
                <span>{{ getMenuTitle(activeMenu) }}</span>
              </div>
              <div>
                <div class="category-header">
                  <h3>视频分类</h3>
                  <div class="header-actions">
                    <div class="search-box">
                      <el-button
                          type="primary"
                          icon="el-icon-video-camera"
                          @click="add_new_category">
                        添加新分类
                      </el-button>
                      <el-dialog
                          title="添加分类"
                          :visible.sync="add_category_window"
                          width="60%"
                          :before-close="handleClose">
                        <el-form :model="category">
                          <el-form-item label="分类名" :label-width="formLabelWidth">
                            <el-input v-model="category.name" autocomplete="off"></el-input>
                          </el-form-item>
                          <el-form-item label="分类描述" :label-width="formLabelWidth">
                            <el-input type="textarea" v-model="category.desc" autocomplete="off"></el-input>
                          </el-form-item>
                          <el-form-item label="封面设置" :label-width="formLabelWidth">
                            <div class="cover-upload">
                              <div class="cover-uploader">
                                <img v-if="category.img_url" :src="category.img_url" class="cover-image" />
                              </div>
                              <el-button type="primary" @click="enable_diaglog">添加封面</el-button>
                            </div>
                          </el-form-item>
                          <input
                              ref="coverInput"
                              type="file"
                              accept="image/*"
                              style="display: none"
                              @change="handleCoverChange">
                        </el-form>
                        <span slot="footer" class="dialog-footer">
                      <el-button @click="add_category_window = false">取 消</el-button>
                      <el-button type="primary" @click="submit_add">添 加</el-button>
                    </span>
                      </el-dialog>
                    </div>
                  </div>
                </div>
                <div class="category-videos-container">
                  <div v-if="viewMode === 'grid'" class="video-grid-view">
                    <div v-if="categories.length > 0">
                      <el-row :gutter="20">
                        <el-col
                            v-for="video in categories"
                            :key="video.id"
                            :xs="12"
                            :sm="8"
                            :md="6"
                            :lg="6"
                            :xl="4"
                        >
                          <div class="video-card-grid">
                            <div class="video-thumbnail-wrapper">
                              <img :src="video.thumbnail" :alt="video.name" class="video-thumbnail" />
                              <div class="video-duration">{{ video.duration }}</div>
                              <div v-if="video.status=='PENDING'" class="featured-label">审核中</div>
                              <div v-else-if="video.status=='REJECTED'" class="featured-label" style="background-color: red;">审核失败</div>
                            </div>
                            <div class="video-info-grid">
                              <h4 class="video-title-grid">{{ video.name }}</h4>
                              <div class="video-meta-grid">
                            <span class="video-category-badge">
                              <el-tooltip class="item" effect="dark" content="点击修改分类信息" placement="bottom">
                                <el-button class="video-category-badge" @click="click_to_change_item(video)"><i class="el-icon-edit"></i></el-button>
                              </el-tooltip>
                              <el-tooltip class="item" effect="dark" content="点击查看分类详细信息" placement="bottom">
                                <el-button class="video-category-badge" @click="launchCover(video)"><i class="el-icon-view"></i></el-button>
                              </el-tooltip>
                            </span>
                                <span class="video-time">{{ formatDate(video.createTime) }}</span>
                              </div>
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    <!-- 空状态 -->
                    <div v-else class="empty-state">
                      <el-empty description="暂无分类，请添加分类">
                        <el-button type="primary" @click="add_new_category">添加分类</el-button>
                      </el-empty>
                    </div>
                  </div>
                  <!-- 列表视图 -->
                  <div v-else class="video-list-view">
                    <div v-if="categories.length > 0">
                      <div
                          v-for="video in categories"
                          :key="video.id"
                          class="video-list-item"
                      >
                        <div class="list-thumbnail-wrapper">
                          <img :src="video.thumbnail" :alt="video.name" />
                        </div>
                        <div class="list-info-content">
                          <h4 class="list-title">{{ video.name }}</h4>
                          <p class="list-description">{{ video.description }}</p>
                          <div class="list-meta-info">
                            <el-tag size="mini" type="info" v-if="video.status=='PENDING'">审核中</el-tag>
                            <div class="list-stats">
                              <span class="stat">{{ formatDate(video.createTime) }}</span>
                              <span class="video-category-badge">
                              <el-tooltip class="item" effect="dark" content="点击修改分类信息" placement="bottom">
                                <el-button class="video-category-badge" @click="click_to_change_item(video)"><i class="el-icon-edit"></i></el-button>
                              </el-tooltip>
                              <el-tooltip class="item" effect="dark" content="点击查看分类详细信息" placement="bottom">
                                <el-button class="video-category-badge" @click="launchCover(video)"><i class="el-icon-view"></i></el-button>
                              </el-tooltip>
                            </span>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- 空状态 -->
                    <div v-else class="empty-state">
                      <el-empty description="该分类下暂无视频">
                        <el-button type="primary" @click="resetCategory">查看全部视频</el-button>
                      </el-empty>
                    </div>
                  </div>
                  <el-dialog
                      title="修改分类"
                      :visible.sync="modify_category_window"
                      width="60%"
                      :before-close="close_change">
                    <el-form :model="pre_modify_category">
                      <el-form-item label="分类名" :label-width="formLabelWidth">
                        <el-input v-model="pre_modify_category.name" autocomplete="off"></el-input>
                      </el-form-item>
                      <el-form-item label="分类描述" :label-width="formLabelWidth">
                        <el-input type="textarea" v-model="pre_modify_category.desc" autocomplete="off"></el-input>
                      </el-form-item>
                      <el-form-item label="封面设置" :label-width="formLabelWidth">
                        <div class="cover-upload">
                          <div class="cover-uploader">
                            <img v-if="pre_modify_category.img_url" :src="pre_modify_category.img_url" class="cover-image" />
                          </div>
                          <el-button type="primary" @click="confirm_change">修改封面</el-button>
                        </div>

                      </el-form-item>
                      <input
                          ref="changeInput"
                          type="file"
                          accept="image/*"
                          style="display: none"
                          @change="imgChange">
                    </el-form>
                    <span slot="footer" class="dialog-footer">
                      <el-button @click="modify_category_window = false">取 消</el-button>
                      <el-button type="primary" @click="submit_change">修 改</el-button>
                    </span>
                  </el-dialog>

                  <!-- 视图切换按钮 -->
                  <div class="view-toggle">
                    <el-button-group>
                      <el-button
                          :type="viewMode === 'grid' ? 'primary' : 'default'"
                          size="small"
                          @click="viewMode = 'grid'"
                      >
                        <i class="el-icon-s-grid"></i> 网格
                      </el-button>
                      <el-button
                          :type="viewMode === 'list' ? 'primary' : 'default'"
                          size="small"
                          @click="viewMode = 'list'"
                      >
                        <i class="el-icon-s-order"></i> 列表
                      </el-button>
                    </el-button-group>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
          <div v-else-if="activeMenu==='favorites'">
            <el-card>
              <div slot="header">
                <span>{{ getMenuTitle(activeMenu) }}</span>
              </div>
              <div>

                <div class="empty-state" v-if="this.favorite_videos.length==0">
                  <el-empty description="暂无点赞视频">
                  </el-empty>
                </div>
                <div v-else>
                  <div class="section-header">
                    <h3>我的点赞</h3>
                    <div class="header-actions">
                    </div>
                  </div>
                  <el-row :gutter="20" class="video-list">
                    <el-col
                        v-for="video in page_favorite_videos"
                        :key="video.id"
                        :xs="12"
                        :sm="8"
                        :md="6"
                        :lg="6"
                        :xl="4">
                      <el-card class="video-card" shadow="hover">
                        <div class="video-cover" @click="playVideo1(video)">
                          <img :src="video.coverImage" alt="视频封面" />
                          <!--                    <div class="video-duration">{{ video.duration }}</div>-->
                          <div class="video-views">
                            <i class="el-icon-collection-tag"></i> {{ video.likeCount }}
                          </div>
                        </div>
                        <div class="video-info">
                          <h4 class="video-title" :title="video.title">{{ video.title }}</h4>
                          <div class="video-meta">
                            <span class="video-time">{{ formatDate(video.createTime) }}</span>
                          </div>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>

                  <!-- 分页 -->
                  <el-pagination
                      v-if="this.favorite_videos.length > 0"
                      @current-change="handlePageChange1"
                      :current-page="currentPage1"
                      :page-size="8"
                      :total="this.favorite_videos.length"
                      layout="prev, pager, next, jumper"
                      class="pagination"
                  >
                  </el-pagination>
                </div>
              </div>
            </el-card>
          </div>
          <div v-else-if="activeMenu=='apply'">
            <el-card>
              <div slot="header">
                <span>{{ getMenuTitle(activeMenu) }}</span>
              </div>
              <div>


                <div class="publisher-application">
                  <!-- 页面标题 -->
                  <div class="page-header">
                    <p class="page-description">请填写以下信息申请成为视频发布者</p>
                  </div>
                  <!-- 申请表单 -->
                  <el-card class="application-card" shadow="never">
                    <el-form ref="applicationForm" :model="formData" :rules="formRules" label-width="120px">
                      <!-- 基本信息区域 -->
                      <div class="form-section">
                        <h3 class="section-title">基本信息</h3>
                        <el-form-item label="姓名" prop="name">
                          <el-input
                              v-model="formData.name"
                              placeholder="请输入真实姓名"
                              style="width: 300px;"
                          ></el-input>
                        </el-form-item>
                        <el-form-item label="联系方式" prop="contact">
                          <el-input
                              v-model="formData.contact"
                              placeholder="请输入手机号或邮箱"
                              style="width: 300px;"
                          ></el-input>
                        </el-form-item>
                      </div>
                      <!-- 申请详情区域 -->
                      <div class="form-section">
                        <h3 class="section-title">申请详情</h3>
                        <el-form-item label="申请理由" prop="reason">
                          <el-input
                              v-model="formData.reason"
                              type="textarea"
                              :rows="4"
                              placeholder="请详细描述您申请成为视频发布者的理由"
                              style="width: 500px;"
                          ></el-input>
                          <div class="hint-text">请详细说明您的视频制作经验、内容方向等信息</div>
                        </el-form-item>
                      </div>
                      <!-- 申请协议 -->
                      <div class="agreement-section">
                        <el-checkbox v-model="formData.agreement" class="agreement-checkbox">
                          我已阅读并同意
                          <el-link type="primary" @click="showAgreementDialog">《视频发布者服务协议》</el-link>
                        </el-checkbox>
                      </div>

                      <!-- 提交按钮 -->
                      <div class="form-actions">
                        <el-button type="primary" @click="submitApplication" :loading="submitting">
                          提交申请
                        </el-button>
                        <el-button @click="resetForm">重置</el-button>
                      </div>
                    </el-form>
                  </el-card>

                  <!-- 申请协议对话框 -->
                  <el-dialog
                      title="视频发布者服务协议"
                      :visible.sync="agreementDialogVisible"
                      width="70%"
                  >
                    <div class="agreement-content">
                      <p>【协议内容】请在此处填写详细的视频发布者服务协议内容...</p>
                      <!-- 协议详细内容 -->
                    </div>
                    <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="agreementDialogVisible = false">已阅读并理解</el-button>
      </span>
                  </el-dialog>

                  <!-- 申请状态提示（已申请情况下显示） -->
                  <el-card class="status-card" shadow="never" v-if="applicationStatus">
                    <div class="status-header">
                      <h3>申请状态</h3>
                      <el-tag :type="statusTagType">{{ applicationStatus.text }}</el-tag>
                    </div>
                    <div class="status-details">
                      <p><strong>申请时间：</strong>{{ applicationStatus.createTime }}</p>
                      <p><strong>备注：</strong>{{ applicationStatus.responseText || '暂无' }}</p>
                    </div>
                  </el-card>
                </div>


              </div>
            </el-card>
          </div>
          <!-- 其他模块的占位内容 -->
          <div v-else class="other-section">
            <el-card>
              <div slot="header">
                <span>{{ getMenuTitle(activeMenu) }}</span>
              </div>
              <div class="placeholder-content">
                <i class="el-icon-info"></i>
                <p>{{ getMenuTitle(activeMenu) }}页面正在开发中...</p>
              </div>
            </el-card>
          </div>
        </el-main>
      </el-container>
    </div>
  </div>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";

export default {
  name: 'UserCenter',
  beforeRouteEnter(to,from,next){
    let date=new Date();
    let compare=date.getTime();
    let token=localStorage.getItem("token");
    let expire_time=localStorage.getItem("expire_time");
    if (token==null||token.length==0){
      next("/user_index");
    }else if(compare>expire_time){
      localStorage.clear();
      next("/user_index");
    }
    else {
      next();
    }
  },
  data() {
    return {
      modify_category_window:false,
      pre_modify_category:{
        id:"",
        name:"",
        desc:"",
        img_url:"",
        img_dat:""
      },
      activeMenu: 'profile',// 当前激活的菜单
      userInfo: {// 用户信息
        username: 'admin',
        role: '管理员',
        email: 'admin@example.com',
        avatar: '',
        phone:"19836537261",
        createTime:""
      },
      userStats: {
        videoCount: 15,
        favoriteCount: 1245
      },
      userVideos: [],
      page_videos:[],
      favorite_videos:[],
      page_favorite_videos:[],
      // 分页相关
      sortBy: 'latest',
      currentPage: 1,
      pageSize: 8,
      totalVideos: 25,
      currentPage1:1,
      // 视频数量
      videoCount: 15,
      categorySearch: '',
      viewMode: 'grid',
      // 示例视频数据（可以根据实际业务替换）
      categories: [
        {
          id: 101,
          name: 'Vue.js 3.0 新特性详解',
          description: '深度解析Vue.js 3.0的新特性，包括Composition API、性能优化等内容',
          status:"PENDING",
          thumbnail: 'https://picsum.photos/seed/vuejs/320/180',
          createTime: '2天前'
        },
        {
          id: 102,
          name: 'Vue.js 3.0 新特性详解',
          description: '深度解析Vue.js 3.0的新特性，包括Composition API、性能优化等内容',
          status:"PENDING",
          thumbnail: 'https://picsum.photos/seed/vuejs/320/180',
          createTime: '2天前'
        },
        // 可以添加更多示例视频...
      ],
      add_category_window:false,
      formLabelWidth: '120px',
      category:{
        name:"",
        desc:"",
        img_url:"",
        img_dat:""
      },



      formData: {
        name: '',
        contact: '',
        reason: '',
        agreement: false
      },

      // 验证规则
      formRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$|^[\w.-]+@[\w.-]+\.\w+$/, message: '请输入有效的手机号或邮箱', trigger: 'blur' }
        ],
        reason: [
          { required: true, message: '请输入申请理由', trigger: 'blur' },
          { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
        ]
      },

      // 对话框状态
      agreementDialogVisible: false,

      // 提交状态
      submitting: false,

      // 申请状态（模拟数据，实际应从后端获取）
      applicationStatus: null

    }
  },
  computed:{
    ...mapGetters(['get_username','get_token']),
    filteredVideos() {
      return this.categories;
    },
    statusTagType() {
      if (!this.applicationStatus) return 'info';
      const status = this.applicationStatus.status;
      if (status === 'APPROVED') return 'success';
      if (status === 'REJECTED') return 'danger';
      if (status === 'PENDING') return 'warning';
      return 'info';
    }
  },
  methods: {
    ...mapMutations(['decrypt']),
    get_favorite_videos(){
      this.$http({
        url:"/api/favorite/get_favorite_videos",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          username:this.get_username
        }
      }).then(res=>{
        console.log(res.data.dat);
        for (let i = 0; i < res.data.dat.length; i++) {
          this.favorite_videos.push(res.data.dat[i].video);
        }
        this.refresh_video_list1();
      });
    },
    playVideo1(video){
      this.$router.push({
        name:"video_detail",
        params:{
          video_msg:{
            id: video.id,
            title: video.title,
            description: video.description,
            videoUrl: video.videoUrl,
            coverImage: video.cover,
            createTime: video.createTime,
            likeCount: video.likeCount,
            viewCount: 0,
            duration: 0,
            user:{
              username:this.get_username
            }
          }
        }
      })
    },
    playVideo(video){
      console.log(video);
      this.$router.push({
        name:"video_detail",
        params:{
          video_msg:{
            id: video.id,
            title: video.title,
            description: video.desc,
            videoUrl: video.videoUrl,
            coverImage: video.cover,
            createTime: video.createTime,
            likeCount: video.views,
            viewCount: 0,
            duration: 0,
            user:{
              username:this.get_username
            }
          }
        }
      })
    },
    submit_change(){
      this.modify_category_window=false;
      console.log(this.pre_modify_category);
      this.$http({
        url:"/api/category/update_category",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          id:this.pre_modify_category.id,
          desc:this.pre_modify_category.desc,
          name:this.pre_modify_category.name,
          img_dat:this.pre_modify_category.img_dat
        }
      }).then(res=>{
        if (res.data.result){
          this.$message.success("修改成功");
          this.get_categories();
        }else {
          this.$message.error("修改失败");
        }
      });
    },
    imgChange(event){
      console.log(event);
      const file = event.target.files[0]
      if (file) {
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
        if (!allowedTypes.includes(file.type)) {
          this.$message.error('不支持的文件格式，请上传JPG或PNG格式的图片')
          return
        }
        let file_reader=new FileReader();
        file_reader.onload=(e)=>{
          let dat=e.target.result;
          this.pre_modify_category.img_dat=dat;
          this.pre_modify_category.img_url = URL.createObjectURL(file);
        };
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    click_to_change_item(obj){
      this.modify_category_window=true;
      this.pre_modify_category.desc=obj.description;
      this.pre_modify_category.name=obj.name;
      this.pre_modify_category.img_url=obj.thumbnail;
      this.pre_modify_category.id=obj.id;

    },
    confirm_change(){
      this.$refs.changeInput.click();
    },
    close_change(){
      this.modify_category_window=false;
    },
    get_user_profile(){
      this.$http({
        url:"/api/user/get_one_user",
        headers:{
          "auth":this.get_token
        },
        params:{
          username:this.get_username
        }
      }).then(res=>{
        //console.log(res.data);
        if (res.data.result){
          this.userInfo.username=res.data.dat.username;
          this.userInfo.role=res.data.dat.role[0];
          this.userInfo.phone=res.data.dat.phone;
          this.userInfo.email=res.data.dat.email;
          this.userInfo.createTime=res.data.dat.create_time;
        }
      });
    },
    get_user_videos(){
      this.$http({
        url:"/api/video/get_videos_by_user",
        headers:{
          "auth":this.get_token
        },
        params:{
          username:this.get_username
        }
      }).then(res=>{
        //console.log(res.data);
        let total_like_count=0;
        if (res.data.result){
          this.videoCount=res.data.size;
          for (let i = 0; i < res.data.dat.length; i++) {
            this.userVideos[i]={
              id: res.data.dat[i].id,
              title: res.data.dat[i].title,
              cover: res.data.dat[i].coverImage,
              views: res.data.dat[i].likeCount,
              createTime: res.data.dat[i].createTime,
              status: res.data.dat[i].status,
              videoUrl:res.data.dat[i].videoUrl,
              desc:res.data.dat[i].description,
              video_type:res.data.dat[i].video_type
            };
            total_like_count+=res.data.dat[i].likeCount;
          }
          this.refresh_video_list();
          this.userStats.videoCount=res.data.size;
          this.totalVideos=res.data.size;
          this.userStats.favoriteCount=total_like_count;
        }
      });
    },
    get_categories(){
      this.$http({
        url:"/api/category/get_category_by_user",
        headers:{
          auth:this.get_token
        },
        params:{
          username:localStorage.getItem("username")
        }
      }).then(res=>{
        //console.log(res.data);
        if (res.data.result){
          this.categories=res.data.dat;
        }
      });
    },
    refresh_video_list(){
      this.page_videos=[];
      //console.log("当前页面:"+this.currentPage);
      let load_video_nums=this.currentPage*8;
      let start_video_index=(this.currentPage-1)*8;
      let counter=0;
      for (let i =start_video_index; i < load_video_nums&&i<this.userVideos.length; i++) {
        this.page_videos[counter++]=this.userVideos[i];
      }
    },
    refresh_video_list1(){
      this.page_favorite_videos=[];
      //console.log("当前页面:"+this.currentPage);
      let load_video_nums=this.currentPage1*8;
      let start_video_index=(this.currentPage1-1)*8;
      let counter=0;
      for (let i =start_video_index; i < load_video_nums&&i<this.favorite_videos.length; i++) {
        this.page_favorite_videos[counter++]=this.favorite_videos[i];
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    // 菜单选择
    handleMenuSelect(index) {
      this.activeMenu = index
    },
    // 用户命令处理
    handleUserCommand(command) {
      switch (command) {
        case 'personal':
          this.$router.push("/user_index");
          //this.activeMenu = 'profile';
          break
        case 'publish':
          this.$router.push('/video_publish')
          break
        case 'favorite':
          this.activeMenu = 'favorites'
          break
        case 'feedback':
          this.activeMenu = 'feedback'
          break
        case 'apply':
          this.activeMenu = "apply"
          break
        case 'logout':
          this.logout()
          break
      }
    },
    // 编辑个人信息
    editProfile() {
      // this.$prompt('请输入新的昵称', '编辑资料', {
      //   confirmButtonText: '确定',
      //   cancelButtonText: '取消',
      //   inputValue: this.userInfo.role
      // }).then(({ value }) => {
      //   this.userInfo.role = value
      //   this.$message.success('昵称修改成功')
      // }).catch(() => {})
    },
    // 播放视频
    launchCover(video) {
      this.$router.push({
        name:"collection_edit",
        params:{
          collection:video
        }
      })
      console.log(video);
    },
    // 编辑视频
    editVideo(video) {
      //console.log(video);
      this.$router.push({
        name:"video_edit",
        params:{
          video_msg:video
        }
      })
    },
    // 删除视频
    deleteVideo(video) {
      this.$confirm('确定要删除这个视频吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log(video);
        this.$http({
          url:"/api/video/delete_video",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            id:video.id
          }
        }).then(res=>{
          if (res.data.dat.result){
            this.$message.success('视频删除成功');
            this.get_user_videos();
          }else {
            this.$message.error('视频删除失败')
          }
        });

      }).catch(() => {})
    },
    // 分页变化
    handlePageChange(page) {
      this.currentPage = page;
      console.log(page);
      this.refresh_video_list();
    },
    handlePageChange1(page) {
      this.currentPage1 = page;
      console.log(page);
      this.refresh_video_list1();
    },
    // 获取菜单标题
    getMenuTitle(menuKey) {
      const titles = {
        profile: '个人信息',
        videos: '我的视频',
        favorites: '我的点赞',
        categories: '视频分类',
        feedback: '意见反馈',
        apply: '申请发布者身份'
      }
      return titles[menuKey] || '未知页面'
    },

    // 退出登录
    logout() {
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
    },
    resetCategory() {
      this.activeCategory = 'all';
    },
    add_new_category(){
      this.add_category_window=true;
    },
    handleClose() {
      this.add_category_window=false;
    },
    enable_diaglog(){
      this.$refs.coverInput.click();
    },
    handleCoverChange(event){
      const file = event.target.files[0]
      if (file) {
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
        if (!allowedTypes.includes(file.type)) {
          this.$message.error('不支持的文件格式，请上传JPG或PNG格式的图片')
          return
        }
        let file_reader=new FileReader();
        file_reader.onload=(e)=>{
          let dat=e.target.result;
          this.category.img_dat=dat;
          this.category.img_url = URL.createObjectURL(file);
        };
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    submit_add(){
      this.$http({
        url:"/api/category/add_category",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          name:this.category.name,
          desc:this.category.desc,
          username:this.get_username,
          img_dat:this.category.img_dat
        }
      }).then(res=>{
        console.log(res.data);
        if (res.data.result){
          this.add_category_window=false;
          this.$message.success("类目添加成功");
          this.get_categories();
        }else {
          this.$message.error("类目添加失败")
        }
      });
    },
    // 显示协议对话框
    showAgreementDialog() {
      this.agreementDialogVisible = true;
    },

    // 提交申请
    submitApplication() {
      this.$refs.applicationForm.validate((valid) => {
        if (valid) {
          if (!this.formData.agreement) {
            this.$message.error('请先阅读并同意服务协议');
            return;
          }
          console.log(this.formData);
          this.submitting = true;
          this.$http({
            url:"/api/apply/add_submit",
            method:"POST",
            headers:{
              auth:this.get_token,
              'Content-Type':"application/x-www-form-urlencoded"
            },
            data:{
              name:this.formData.name,
              contact:this.formData.contact,
              reason:this.formData.reason,
              username:this.get_username
            }
          }).then(res=>{
            if (res.data.dat!=null){
              this.resetForm();
              this.$message.success('申请提交成功！');
              this.submitting = false;
              this.applicationStatus={
                status:res.data.dat.status,
                text:res.data.dat.status=='PENDING'?"审核中":"审核完成",
                responseText:res.data.dat.responseText,
                createTime:this.formatDate(res.data.dat.createTime)
              }
            }else {
              this.$message.error('申请提交失败！');
              this.submitting = false;
            }
          })
          // 模拟API调用延迟
          // setTimeout(() => {
          //   // 这里应该调用实际的API提交申请
          //   // 提交成功后显示状态卡片
          //   this.applicationStatus = {
          //     status: 'reviewing',
          //     text: '审核中',
          //     applyTime: new Date().toLocaleString(),
          //     remark: '您的申请已提交，我们将尽快审核，预计需要1-3个工作日。'
          //   };
          //
          //   this.$message.success('申请提交成功！');
          //   this.submitting = false;
          //
          //   // 重置表单
          //   this.resetForm();
          // }, 1500);
        } else {
          this.$message.error('请正确填写表单信息');
          return false;
        }
      });
    },
    get_apply_data(){
      let role=localStorage.getItem("role");
      if (role=="USER"){
        this.$http({
          url:"/api/apply/see_apply",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            username:this.get_username
          }
        }).then(res=>{
          this.applicationStatus=res.data.dat;
          if (res.data.dat!=null){
            this.applicationStatus.createTime=this.formatDate(res.data.dat.createTime);
            this.applicationStatus.text=res.data.dat.status=='PENDING'?"审核中":"审核完成";
          }
        });
      }
    },
    // 重置表单
    resetForm() {
      this.$refs.applicationForm.resetFields();
    }
  },
  mounted() {

  },
  created() {
    if (this.$router.currentRoute.params!=null&&this.$router.currentRoute.params.index=="videos"){
      this.activeMenu="videos";
    }else if (this.$router.currentRoute.params!=null&&this.$router.currentRoute.params.index=="categories"){
      this.activeMenu="categories";
    }
    this.get_user_profile();
    this.get_user_videos();
    this.get_categories();
    this.get_favorite_videos();
    this.get_apply_data();
    // this.loadUserData()
  }
}
</script>

<style scoped>
.user-center-container {
  height: 100vh;
  background-color: #f5f7fa;
}

/* 顶部导航栏样式 */
.main-header {
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.logo-section .system-name {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
}

.search-section {
  flex: 1;
  max-width: 400px;
  margin: 0 20px;
}

.search-input {
  width: 100%;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  margin-right: 8px;
}

.username {
  font-weight: 500;
  color: #333;
}

/* 侧边栏样式 */
.sidebar {
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  height: calc(100vh - 60px);
  padding-top: 20px;
}

.user-center-menu {
  border-right: none;
  padding: 0 10px;
}

.user-center-menu .el-menu-item {
  height: 50px;
  line-height: 50px;
  border-radius: 8px;
  margin: 5px 0;
}

.user-center-menu .el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409EFF;
}

.publish-btn-section {
  padding: 20px 10px 0;
  text-align: center;
}

.publish-btn {
  width: 100%;
}

/* 主内容区样式 */
.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

.content-area {
  padding: 20px;
  min-height: calc(100vh - 60px);
  overflow-y: auto;
}

/* 个人信息卡片样式 */
.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.profile-info {
  display: flex;
  padding: 20px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 200px;
  padding-right: 30px;
  border-right: 1px solid #eee;
}

.profile-avatar {
  margin-bottom: 15px;
  border: 3px solid #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.info-section {
  flex: 1;
  padding-left: 30px;
}

.info-item {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #666;
  width: 100px;
  display: inline-block;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

/* 统计数据样式 */
.stats-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.stat-item {
  text-align: center;
  padding: 15px 0;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

/* 视频列表样式 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #333;
}

.video-list {
  margin-bottom: 20px;
}

.video-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.video-card:hover {
  transform: translateY(-5px);
}

.video-cover {
  position: relative;
  cursor: pointer;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 10px;
}

.video-cover img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  transition: transform 0.3s;
}

.video-cover:hover img {
  transform: scale(1.05);
}

.video-duration {
  position: absolute;
  bottom: 5px;
  right: 5px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 12px;
}

.video-views {
  position: absolute;
  bottom: 5px;
  left: 5px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 12px;
}

.video-title {
  font-size: 14px;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  margin: 0 0 10px 0;
  color: #333;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.video-actions .el-button {
  padding: 0;
  margin-left: 8px;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  text-align: center;
}

/* 占位内容样式 */
.placeholder-content {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.placeholder-content i {
  font-size: 48px;
  margin-bottom: 20px;
  color: #ccc;
}

.placeholder-content p {
  font-size: 16px;
  margin: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
  }

  .avatar-section {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid #eee;
    padding-right: 0;
    padding-bottom: 20px;
    margin-bottom: 20px;
  }

  .info-section {
    padding-left: 0;
  }

  .sidebar {
    width: 100% !important;
    height: auto;
  }

  .user-center-menu {
    display: flex;
    overflow-x: auto;
  }

  .user-center-menu .el-menu-item {
    flex-shrink: 0;
  }
}
.video-category-section {
  padding: 20px;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.category-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.header-actions {
  display: flex;
  align-items: center;
}

.search-box {
  display: flex;
  align-items: center;
}

/* 分类标签容器 */
.category-tags-container {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 12px 0;
  border-bottom: 1px solid #eee;
}

.tags-scroll-wrapper {
  flex: 1;
  overflow-x: auto;
  scrollbar-width: none;
}

.tags-scroll-wrapper::-webkit-scrollbar {
  display: none;
}

.category-tags-list {
  display: flex;
  gap: 8px;
  padding-right: 12px;
}

.category-tag-item {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  padding-right: 10px;
}

.category-tag-item:hover {
  transform: translateY(-2px);
}

.video-count-badge {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  margin-left: 4px;
}

.tags-control {
  display: flex;
  gap: 4px;
  margin-left: 10px;
}

/* 视频网格视图 */
.category-videos-container {
  min-height: 400px;
}

.video-grid-view {
  margin-bottom: 20px;
}

.video-card-grid {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  height: 100%;
}

.video-card-grid:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.video-thumbnail-wrapper {
  position: relative;
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.video-thumbnail {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.video-card-grid:hover .video-thumbnail {
  transform: scale(1.05);
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.featured-label {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #ffc107;
  color: #212529;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.video-info-grid {
  padding: 12px;
}

.video-title-grid {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  height: 40px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta-grid {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.video-category-badge {
  background: #f0f9ff;
  color: #409EFF;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.video-time {
  font-size: 12px;
  color: #999;
}

.video-stats-grid {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #666;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-author-grid {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  border: 1px solid #eee;
}

.author-name {
  font-size: 13px;
  color: #666;
}

/* 视频列表视图 */
.video-list-view {
  margin-bottom: 20px;
}

.video-list-item {
  display: flex;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.video-list-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #409EFF;
}

.list-thumbnail-wrapper {
  position: relative;
  width: 160px;
  height: 90px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.list-thumbnail-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.list-info-content {
  flex: 1;
  padding: 0 16px;
}

.list-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.list-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 12px 0;
  line-height: 1.5;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.list-meta-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.list-stats {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.list-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.list-actions {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

/* 视图切换按钮 */
.view-toggle {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
  background: #fafafa;
  border-radius: 8px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .category-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .video-list-item {
    flex-direction: column;
  }

  .list-thumbnail-wrapper {
    width: 100%;
    height: 160px;
    margin-bottom: 12px;
  }

  .list-info-content {
    padding: 0;
    margin-bottom: 12px;
  }

  .list-actions {
    flex-direction: row;
    justify-content: flex-end;
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




.publisher-application {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 14px;
  color: #909399;
}

.application-card {
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 20px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}

.hint-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.agreement-section {
  margin: 30px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.agreement-checkbox {
  display: flex;
  align-items: center;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.status-card {
  margin-top: 30px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.status-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.status-details p {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.agreement-content {
  max-height: 400px;
  overflow-y: auto;
  line-height: 1.6;
  color: #606266;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .application-card {
    padding: 15px;
  }

  .form-actions {
    flex-direction: column;
    gap: 10px;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>
