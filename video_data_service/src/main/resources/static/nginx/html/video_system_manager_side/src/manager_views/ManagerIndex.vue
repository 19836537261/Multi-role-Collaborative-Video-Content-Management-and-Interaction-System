<template>
  <div class="admin-container">
    <!-- 顶部导航栏 -->
    <div class="admin-navbar">
      <el-menu
          :default-active="activeMenu"
          class="el-menu-admin"
          mode="horizontal"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          @select="handleMenuSelect"
      >
        <!-- 平台标题 -->
        <div class="navbar-brand">
          <i class="el-icon-monitor"></i>
          <span>视频分享平台管理端</span>
        </div>

        <!-- 审核管理 -->
        <el-submenu index="audit">
          <template #title>
            <i class="el-icon-document-checked"></i>
            <span>审核管理</span>
          </template>
          <el-menu-item index="video-audit">视频审核</el-menu-item>
          <el-menu-item index="column-audit">栏目审核</el-menu-item>
          <el-menu-item index="user-apply">用户申请审核</el-menu-item>
        </el-submenu>

        <!-- 系统管理 -->
        <el-submenu index="system">
          <template #title>
            <i class="el-icon-setting"></i>
            <span>系统管理</span>
          </template>
          <el-menu-item index="types">类目管理</el-menu-item>
          <el-menu-item index="announcement">公告管理</el-menu-item>
          <el-menu-item index="audit-log">操作日志</el-menu-item>
          <el-menu-item index="config">系统配置</el-menu-item>
        </el-submenu>

        <!-- 统计分析 -->
        <el-submenu index="statistics">
          <template #title>
            <i class="el-icon-data-analysis"></i>
            <span>统计分析</span>
          </template>
          <el-menu-item index="video-stats">数据分析</el-menu-item>
        </el-submenu>

        <!-- 右侧用户信息 -->
        <div class="navbar-right">
          <el-dropdown @command="handleUserCommand" trigger="click">
            <span class="user-info">
              <el-avatar :size="30" :src="userAvatar" class="user-avatar">
                <i class="el-icon-user-solid"></i>
              </el-avatar>
              <span class="username">{{ userName }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>

                <el-dropdown-item command="setting">
                  <i class="el-icon-setting"></i>
                  账户设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <!-- 时间显示 -->
          <div class="time-display">
            <span>{{ currentTime }}</span>
          </div>
        </div>
      </el-menu>
    </div>
    <!-- 主内容区域 -->
    <div class="admin-main">
      <div class="content-wrapper">
        <!-- 动态卡片内容 -->
        <transition name="fade" mode="out-in">
          <div v-if="activeContent" :key="activeMenu" class="card-container">
            <!-- 视频审核卡片 -->
            <el-card v-if="activeMenu === 'video-audit'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-video-play"></i> 视频审核</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-refresh" @click="refreshData">
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>
              <el-table :data="videoList" stripe style="width: 100%">
                <el-table-column prop="id" label="ID" width="80"></el-table-column>
                <el-table-column prop="title" label="视频标题" width="200"></el-table-column>
                <el-table-column prop="createTime" label="上传时间" width="180"></el-table-column>
                <el-table-column prop="status" label="状态" width="120">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)" size="small">
                      {{ scope.row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button-group>
                      <el-button size="mini" type="success" @click="approveVideo(scope.row)">
                        通过
                      </el-button>
                      <el-button size="mini" type="danger" @click="rejectVideo(scope.row)">
                        拒绝
                      </el-button>
                      <el-button size="mini" type="primary" @click="viewVideo(scope.row)">
                        预览
                      </el-button>
                    </el-button-group>
                  </template>
                </el-table-column>
              </el-table>
              <div class="pagination-wrapper">
                <el-pagination
                    :current-page="currentPage"
                    :page-size="pageSize"
                    :page-sizes="[10, 20, 30, 40,50]"
                    :total="totalItems"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
                ></el-pagination>
              </div>
            </el-card>
            <!-- 栏目审核卡片 -->
            <el-card v-else-if="activeMenu === 'column-audit'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-folder"></i> 栏目审核</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-refresh" @click="get_categories">
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>
              <div v-if="categoryList.length>0">
                <el-table :data="categoryList" stripe style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80"></el-table-column>
                  <el-table-column prop="name" label="栏目标题" width="200"></el-table-column>
                  <el-table-column prop="create_time" label="上传时间" width="180"></el-table-column>
                  <el-table-column prop="status" label="状态" width="120">
                    <template #default="scope">
                      <el-tag :type="getStatusType(scope.row.status)" size="small">
                        {{ scope.row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="200">
                    <template #default="scope">
                      <el-button-group>
                        <el-button size="mini" type="success" @click="approveCategory(scope.row)">通过</el-button>
                        <el-button size="mini" type="danger" @click="rejectCategory(scope.row)">拒绝</el-button>
                        <el-button size="mini" type="primary" @click="viewCategory(scope.row)">预览</el-button>
                      </el-button-group>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pagination-wrapper">
                  <el-pagination
                      :current-page="currentCategoryPage"
                      :page-size="categoryPageSize"
                      :page-sizes="[10, 20, 30, 40,50]"
                      :total="categoryItems"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleCategorySizeChange"
                      @current-change="handleCategoryCurrentChange"
                  ></el-pagination>
                </div>
              </div>
              <div class="empty-state" v-else>
                <el-empty description="暂无待审核栏目"></el-empty>
              </div>
            </el-card>
            <!-- 用户申请审核卡片 -->
            <el-card v-else-if="activeMenu === 'user-apply'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-user"></i> 用户申请审核</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-refresh" @click="get_applies">
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>
              <div v-if="applyList.length>0">
                <el-table :data="applyList" stripe style="width: 100%">
                  <el-table-column prop="id" label="ID" width="80"></el-table-column>
                  <el-table-column prop="real_name" label="用户姓名" width="200"></el-table-column>
                  <el-table-column prop="createTime" label="申请时间" width="180"></el-table-column>
                  <el-table-column prop="status" label="状态" width="120">
                    <template #default="scope">
                      <el-tag :type="getStatusType(scope.row.status)" size="small">
                        {{ scope.row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="200">
                    <template #default="scope">
                      <el-button-group>
                        <el-button size="mini" type="success" @click="approveApply(scope.row)">通过</el-button>
                        <el-button size="mini" type="danger" @click="rejectApply(scope.row)">拒绝</el-button>
                        <el-button size="mini" type="primary" @click="viewApply(scope.row)">预览</el-button>
                      </el-button-group>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pagination-wrapper">
                  <el-pagination
                      :current-page="currentApplyPage"
                      :page-size="applyPageSize"
                      :page-sizes="[10, 20, 30, 40,50]"
                      :total="applyItems"
                      layout="total, sizes, prev, pager, next, jumper"
                      @size-change="handleApplySizeChange"
                      @current-change="handleApplyCurrentChange"
                  ></el-pagination>
                </div>
              </div>
              <div class="empty-state" v-else>
                <el-empty description="暂无用户申请">
                </el-empty>
              </div>

              <el-dialog title="详情" :visible.sync="open_detail" width="80%">
                <el-form :model="apply" label-width="120px">
                  <!-- 基本信息区域 -->
                  <div class="form-section">
                    <h3 class="section-title">基本信息</h3>
                    <el-form-item label="姓名" prop="name">
                      <el-input
                          v-model="apply.real_name"
                          placeholder="请输入真实姓名"
                          style="width: 300px;"
                      ></el-input>
                    </el-form-item>
                    <el-form-item label="联系方式" prop="contact">
                      <el-input
                          v-model="apply.contact"
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
                          v-model="apply.applicationReason"
                          type="textarea"
                          :rows="4"
                          placeholder="请详细描述您申请成为视频发布者的理由"
                          style="width: 500px;"
                      ></el-input>
                      <div class="hint-text">请详细说明您的视频制作经验、内容方向等信息</div>
                    </el-form-item>
                  </div>
                </el-form>
                <div slot="footer" class="dialog-footer">
                  <el-button @click="open_detail = false">取 消</el-button>
                  <el-button type="primary" @click="open_detail = false">确 定</el-button>
                </div>
              </el-dialog>

            </el-card>

            <!-- 公告管理卡片 -->
            <el-card v-else-if="activeMenu === 'announcement'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-megaphone"></i> 公告管理</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-plus" @click="addAnnouncement">
                      新增公告
                    </el-button>
                    <el-dialog
                        title="提示"
                        :visible.sync="open_announcement"
                        width="80%"
                        :before-close="handleAnnouncementClose">
                      <span>
                        <el-form :model="announcement" :rules="announcement_rule" ref="announcement" label-width="100px" class="demo-ruleForm">
                          <el-form-item label="公告标题" prop="title">
                            <el-input v-model="announcement.title"></el-input>
                          </el-form-item>
                          <el-form-item label="公告内容" prop="content">
                            <el-input v-model="announcement.content" type="textarea"></el-input>
                          </el-form-item>
                          <el-form-item label="封面设置" :label-width="'120px'">
                            <div class="cover-upload">
                              <div class="cover-uploader">
                                <img v-if="announcement.img_url" :src="announcement.img_url" class="cover-image" />
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
                      </span>
                      <span slot="footer" class="dialog-footer">
                        <el-button @click="open_announcement = false">取 消</el-button>
                        <el-button type="primary" @click="confirm_add">添 加</el-button>
                      </span>
                    </el-dialog>
                  </div>
                </div>
              </template>
              <el-table
                  :data="announceList"
                  style="width: 100%" v-if="announceList.length>0">
                <el-table-column
                    prop="title"
                    label="主题"
                    width="300">
                </el-table-column>
                <el-table-column
                    prop="content"
                    label="内容"
                    width="450">
                </el-table-column>
                <el-table-column
                    prop="createTime"
                    label="创建日期"
                    width="180">
                </el-table-column>
                <el-table-column
                    fixed="right"
                    label="操作"
                    width="100">
                  <template slot-scope="scope">
                    <el-button @click="pre_view(scope.row)" type="text" size="small">预览</el-button>
                    <el-button @click="delete_one(scope.row)" type="text" size="small">删除</el-button>
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
                  </template>
                </el-table-column>
              </el-table>
              <div class="empty-state" v-else>
                <el-empty description="暂无公告">
                  <el-button type="primary" @click="open_announcement=true">创建公告</el-button>
                </el-empty>
              </div>
            </el-card>

            <!-- 操作日志卡片 -->
            <el-card v-else-if="activeMenu === 'audit-log'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-notebook-2"></i> 操作日志</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-refresh" @click="get_logs">
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>
              <el-table
                  :data="logList"
                  style="width: 100%" v-if="logList.length>0">
                <el-table-column
                    prop="id"
                    label="编号"
                    width="60">
                </el-table-column>
                <el-table-column
                    prop="content"
                    label="内容"
                    width="1000">
                </el-table-column>
                <el-table-column
                    prop="createTime"
                    label="创建日期"
                    width="300">
                </el-table-column>
              </el-table>
              <div class="empty-state" v-else>
                <el-empty description="暂无操作日志"></el-empty>
              </div>
            </el-card>
            <el-card v-else-if="activeMenu === 'types'" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-notebook-2"></i> 类目管理</h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-plus" @click="open_add=true">
                      添加类目
                    </el-button>
                  </div>
                </div>
                <el-dialog
                    title="提示"
                    :visible.sync="open_add"
                    width="80%" append-to-body>
                      <span>
                        <el-form :model="form_dat"
                                 label-width="100px"
                                 class="demo-ruleForm">
                          <el-form-item label="名称" prop="title">
                            <el-input v-model="form_dat.type"></el-input>
                          </el-form-item>
                        </el-form>
                      </span>
                      <span slot="footer" class="dialog-footer">
                        <el-button @click="add_new_kind">添 加</el-button>
                      </span>
                </el-dialog>
              </template>
              <el-table
                  :data="typeList"
                  style="width: 100%" v-if="typeList.length>0">
                <el-table-column
                    prop="id"
                    label="编号"
                    width="60">
                </el-table-column>
                <el-table-column
                    prop="type"
                    label="类目"
                    width="1000">
                </el-table-column>
                <el-table-column
                    fixed="right"
                    label="操作"
                    width="100">
                  <template slot-scope="scope">
                    <el-button @click="handleClick(scope.row)" type="text" size="small">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <div class="empty-state" v-else>
                <el-empty description="暂无操作日志"></el-empty>
              </div>
            </el-card>
            <!-- 统计分析卡片 -->
            <el-card v-else-if="['video-stats', 'user-stats', 'platform-stats'].includes(activeMenu)" class="content-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3>
                    <i class="el-icon-data-analysis"></i>
                    {{ activeMenu === 'video-stats' ? '视频数据分析' : activeMenu === 'user-stats' ? '用户数据分析' : '平台数据分析' }}
                  </h3>
                  <div class="card-actions">
                    <el-button type="primary" size="small" icon="el-icon-refresh">
                      刷新
                    </el-button>
                  </div>
                </div>
              </template>

              <!-- 统计图表区域 -->
              <div class="stats-container">
                <div class="stats-row">
                  <el-card class="stat-card" shadow="never">
                    <div class="stat-item">
                      <div class="stat-label">今日新增视频</div>
                      <div class="stat-value">{{ stats.todayVideos }}</div>
                      <div class="stat-trend">
                        <i class="el-icon-top" style="color: #67c23a;"></i>
                        <span style="color: #67c23a;">+12%</span>
                      </div>
                    </div>
                  </el-card>

                  <el-card class="stat-card" shadow="never">
                    <div class="stat-item">
                      <div class="stat-label">总视频数量</div>
                      <div class="stat-value">{{ stats.totalVideos }}</div>
                    </div>
                  </el-card>

                  <el-card class="stat-card" shadow="never">
                    <div class="stat-item">
                      <div class="stat-label">总用户数量</div>
                      <div class="stat-value">{{ stats.totalUsers }}</div>
                    </div>
                  </el-card>
                </div>

                <!-- 图表区域 -->
                <div class="chart-row">
                  <el-card class="chart-card" shadow="never">
                    <template #header>
                      <span>视频上传趋势</span>
                    </template>
                    <div class="chart-placeholder">
                      <div class="fake-chart">
                        <div class="fake-bar" v-for="i in 7" :key="i" :style="{ height: `${Math.random() * 100 + 50}px` }"></div>
                      </div>
                    </div>
                  </el-card>

                  <el-card class="chart-card" shadow="never">
                    <template #header>
                      <span>用户活跃度</span>
                    </template>
                    <div class="chart-placeholder">
                      <div class="fake-line-chart"></div>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-card>

            <!-- 默认欢迎卡片 -->
            <el-card v-else class="content-card welcome-card" shadow="hover">
              <template #header>
                <div class="card-header">
                  <h3><i class="el-icon-s-home"></i> 管理端首页</h3>
                </div>
              </template>
              <div class="welcome-content">
                <div class="welcome-header">
                  <h1>欢迎回来，{{ userName }}！</h1>
                  <p>今天是 {{ currentFullDate }}，祝您工作愉快！</p>
                </div>

                <div class="quick-stats">
                  <el-row :gutter="20">
                    <el-col :span="6">
                      <el-card shadow="hover" class="quick-stat-card">
                        <div class="quick-stat-item">
                          <i class="el-icon-warning-outline stat-icon pending-icon"></i>
                          <div class="quick-stat-content">
                            <div class="quick-stat-value">12</div>
                            <div class="quick-stat-label">待审核视频</div>
                          </div>
                        </div>
                      </el-card>
                    </el-col>
                    <el-col :span="6">
                      <el-card shadow="hover" class="quick-stat-card">
                        <div class="quick-stat-item">
                          <i class="el-icon-video-camera stat-icon video-icon"></i>
                          <div class="quick-stat-content">
                            <div class="quick-stat-value">89</div>
                            <div class="quick-stat-label">今日上传视频</div>
                          </div>
                        </div>
                      </el-card>
                    </el-col>
                  </el-row>
                </div>
                <div class="quick-actions">
                  <h3>快捷操作</h3>
                  <el-row :gutter="20">
                    <el-col :span="8">
                      <div @click="page_to_video_view">
                        <el-card shadow="hover" class="quick-action-card" @click="handleMenuSelect('video-audit')">
                          <i class="el-icon-document-checked action-icon"></i>
                          <div class="action-title">视频审核</div>
                          <div class="action-desc">审核用户上传的视频内容</div>
                        </el-card>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <div @click="page_to_announcement">
                        <el-card shadow="hover" class="quick-action-card">
                          <i class="el-icon-megaphone action-icon"></i>
                          <div class="action-title">发布公告</div>
                          <div class="action-desc">向用户发布平台公告</div>
                        </el-card>
                      </div>
                    </el-col>
                    <el-col :span="8">
                      <el-card shadow="hover" class="quick-action-card" @click="handleMenuSelect('video-stats')">
                        <i class="el-icon-data-analysis action-icon"></i>
                        <div class="action-title">查看数据</div>
                        <div class="action-desc">查看平台数据统计</div>
                      </el-card>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </el-card>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import {mapGetters, mapMutations} from "vuex";

export default {
  name: "ManagerIndex",
  beforeRouteEnter(to,from,next){
    let date=new Date();
    let compare=date.getTime();
    let token=localStorage.getItem("token");
    let expire_time=localStorage.getItem("expire_time");
    if (token==null||token.length==0){
      next("/");
    }else if(compare>expire_time){
      localStorage.clear();
      next("/");
    }
    else {
      next();
    }
  },
  computed:{
    ...mapGetters(['get_token','get_username'])
  },
  data() {
    return {
      open_add:false,
      form_dat:{
        type:""
      },
      activeMenu: "config",
      activeContent: true,
      userName: "管理员",
      userAvatar: "",
      searchKeyword: "",
      currentTime: "11:43",
      currentFullDate: "2026年2月15日 星期日",
      statsDateRange: "",

      // 分页数据
      currentPage: 1,
      apply:{
        real_name:"",
        contact:"",
        applicationReason:""
      },

      currentCategoryPage:1,
      categoryPageSize:10,
      categoryItems:102,

      currentApplyPage:1,
      applyPageSize:10,
      applyItems:102,

      pageSize: 10,
      totalItems: 102,
      sourceVideoList:[],
      applyList:[],
      // 视频审核数据
      videoList: [
        {
          id: 1,
          title: "如何学习Vue.js",
          createTime: "2026-02-15 10:30:25",
          status: "待审核"
        },
        {
          id: 2,
          title: "JavaScript高级技巧",
          createTime: "2026-02-14 16:45:30",
          status: "已通过"
        },
        {
          id: 3,
          title: "CSS动画实战",
          createTime: "2026-02-14 14:20:15",
          status: "已拒绝"
        }
      ],
      categoryList:[],
      open_detail:false,
      sourceCategoryList:[],
      sourceApplyList:[],
      announceList:[],
      logList:[],
      // 统计数据
      stats: {
        todayVideos: 89,
        activeUsers: 1245,
        totalVideos: 15342,
        totalUsers: 89231
      },
      open_announcement:false,
      open_announcement_prew:false,
      announcement_prew:{
        title:"",
        content:"",
        img_url:""
      },
      announcement:{
        title:"",
        content:"",
        img_dat:"",
        img_url:""
      },
      announcement_rule:{
        title: [
          { required: true, message: '请输入公告标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入公告内容', trigger: 'blur' }
        ]
      },
      typeList:[]
    };
  },
  created() {
    this.updateTime();
    // 设置定时器，每分钟更新时间
    setInterval(this.updateTime, 60000);
    this.get_announce();
    this.get_videos();
    this.get_categories();
    this.get_applies();
    this.get_logs();
    this.get_types();
    let receive_dat=this.$router.currentRoute.params;
    if (receive_dat!=null){
      console.log("recv:",{"dat":receive_dat})
      this.activeMenu=receive_dat.select_menu;
    }
  },
  methods: {
    ...mapMutations(['clear_msg']),
    handleClick(type){
      console.log(type);
      this.$http({
        url:"/api/video/delete_type",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          type:type.type,
          username:this.get_username
        }
      }).then(res=>{
        if (res.data.result){
          this.$message.success("操作成功");
          this.get_types();
          this.open_add=false;
        }else {
          this.$message.error("操作失败")
        }
      });
    },
    add_new_kind(){
      //console.log(this.form_dat.type);
      this.$http({
        url:"/api/video/add_new_type",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          type:this.form_dat.type,
          username:this.get_username
        }
      }).then(res=>{
        if (res.data.result){
          this.$message.success("操作成功");
          this.get_types();
          this.open_add=false;
        }else {
          this.$message.error("操作失败")
        }
      });
    },
    get_types(){
      this.$http({
        url:"/api/video/get_all_video_kinds"
      }).then(res=>{
        this.typeList=res.data.dat;
        for (let i = 0; i < res.data.dat.length; i++) {
          this.typeList[i].id=i;
        }
      });
    },
    get_logs(){
      this.$http({
        url:"/api/user/get_audit_by_admin",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          username:this.get_username
        }
      }).then(res=>{
        this.logList=res.data.dat;
      });
    },
    approveApply(apply){
      //console.log(apply);
      this.$confirm(`确定通过申请吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$http({
          url:"/api/apply/audit_one_apply",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:1,
            id:apply.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            apply.status = '已通过';
            this.$message({
              type: 'success',
              message: '操作成功'
            });
            this.get_applies();
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },
    rejectApply(apply){
      this.$confirm(`确定拒绝通过该申请吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url:"/api/apply/audit_one_apply",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:0,
            id:apply.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            apply.status = '已拒绝';
            this.$message({
              type: 'success',
              message: '操作成功'
            });
            this.get_applies();
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },
    viewApply(apply){
      this.open_detail=true;
      this.apply=apply;
      console.log(apply);
    },
    close_prew(){
      this.open_announcement_prew=false;
    },
    pre_view(announce_obj){
      console.log(announce_obj);
      this.open_announcement_prew=true;
      this.announcement_prew=announce_obj;
    },
    delete_one(announce_obj){
      console.log(announce_obj);
      let username=this.get_username;//localStorage.getItem("username");
      let token=this.get_token;
      this.$http({
        url:"/api/announcement/delete_one_announce",
        method:"POST",
        headers:{
          auth:token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          username:username,
          id:announce_obj.id
        }
      }).then(res=>{
        if (res.data.result){
          this.get_announce();
          this.$message.success("删除成功");
        }else {
          this.$message.error("删除失败");
        }
      });
    },
    get_videos(){
      this.$http({
        url:"/api/video/audit_all_videos",
        headers:{
          auth:this.get_token
        }
      }).then(res=>{
        //console.log(res.data.dat);
        this.sourceVideoList=res.data.dat;
        this.totalItems=this.sourceVideoList.length;
        this.format_video_pages(this.sourceVideoList);
      });
    },
    format_video_pages(video_list){
      let current_max_size=this.currentPage*this.pageSize;
      let current_min_size=(this.currentPage-1)*this.pageSize;
      this.videoList=[];
      let counter=0;
      for (let i = current_min_size; i < video_list.length&&i<current_max_size; i++) {
        this.videoList[counter]=video_list[i];
        if (video_list[i].status=='PENDING'||video_list[i].status=='待审核'){
          this.videoList[counter].status="待审核";
        }else if (video_list[i].status=='REJECTED'||video_list[i].status=='已拒绝'){
          this.videoList[counter].status="已拒绝";
        }else {
          this.videoList[counter].status="已通过";
        }
        counter++;
      }
    },
    get_applies(){
      this.$http({
        url:"/api/apply/audit_all_apply",
        method:"POST",
        headers:{
          auth:this.get_token
        }
      }).then(res=>{
        //console.log(res.data.dat);
        this.sourceApplyList=res.data.dat;
        this.applyItems=this.sourceApplyList.length;
        this.format_apply_pages(this.sourceApplyList);
      });
    },
    get_categories(){
      this.$http({
        url:"/api/category/audit_all_categories",
        headers:{
          auth:this.get_token
        }
      }).then(res=>{
        //console.log(res.data.dat);
        this.sourceCategoryList=res.data.dat;
        this.categoryItems=this.sourceCategoryList.length;
        this.format_category_pages(this.sourceCategoryList);
      });
    },
    format_apply_pages(apply_list){
      let current_max_size=this.currentApplyPage*this.applyPageSize;
      let current_min_size=(this.currentApplyPage-1)*this.applyPageSize;
      this.applyList=[];
      let counter=0;
      for (let i = current_min_size; i < apply_list.length&&i<current_max_size; i++) {
        this.applyList[counter]=apply_list[i];
        if (apply_list[i].status=='PENDING'||apply_list[i].status=='待审核'){
          this.applyList[counter].status="待审核";
        }else if (apply_list[i].status=='REJECTED'||apply_list[i].status=='已拒绝'){
          this.applyList[counter].status="已拒绝";
        }else {
          this.applyList[counter].status="已通过";
        }
        counter++;
      }
    },
    format_category_pages(category_list){
      let current_max_size=this.currentCategoryPage*this.categoryPageSize;
      let current_min_size=(this.currentCategoryPage-1)*this.categoryPageSize;
      this.categoryList=[];
      let counter=0;
      for (let i = current_min_size; i < category_list.length&&i<current_max_size; i++) {
        this.categoryList[counter]=category_list[i];
        if (category_list[i].status=='PENDING'||category_list[i].status=='待审核'){
          this.categoryList[counter].status="待审核";
        }else if (category_list[i].status=='REJECTED'||category_list[i].status=='已拒绝'){
          this.categoryList[counter].status="已拒绝";
        }else {
          this.categoryList[counter].status="已通过";
        }
        counter++;
      }
    },
    get_announce(){
      let username=this.get_username;//localStorage.getItem("username");
      let token=this.get_token;
      this.$http({
        url:"/api/announcement/get_announce_by_admin",
        method:"POST",
        headers:{
          auth:token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          username:username
        }
      }).then(res=>{
        console.log(res.data.dat);
        this.announceList=res.data.dat;
        for (let i = 0; i < this.announceList.length; i++) {
          this.announceList[i].createTime=this.formatDate(this.announceList[i].createTime);
        }
      });
    },
    handleAnnouncementClose(){
      this.open_announcement=false;
    },
    page_to_announcement(){
      this.activeMenu="announcement";
    },
    page_to_video_view(){
      this.activeMenu="video-audit";
    },
    updateTime() {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      this.currentTime = `${hours}:${minutes}`;

      // 更新完整日期
      const year = now.getFullYear();
      const month = now.getMonth() + 1;
      const date = now.getDate();
      const day = now.getDay();
      const dayNames = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];

      this.currentFullDate = `${year}年${month}月${date}日 ${dayNames[day]}`;
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
    // 处理菜单选择
    handleMenuSelect(index) {
      this.activeMenu = index;
      // 触发内容切换动画
      this.activeContent = false;
      setTimeout(() => {
        this.activeContent = true;
      }, 50);

      console.log('切换到菜单:', index);
      // 这里可以添加加载对应数据的逻辑
    },

    // 处理用户命令
    handleUserCommand(command) {
      switch (command) {
        case 'profile':
          this.$message({
            type: 'info',
            message: '打开个人中心'
          });
          break;
        case 'setting':
          this.$message({
            type: 'info',
            message: '打开账户设置'
          });
          break;
        case 'help':
          this.$message({
            type: 'info',
            message: '打开帮助中心'
          });
          break;
        case 'logout':
          this.handleLogout();
          break;
      }
    },

    // 处理搜索
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$message({
          type: 'info',
          message: `搜索关键词: ${this.searchKeyword}`
        });
        // 这里可以添加搜索逻辑
      }
    },

    // 处理退出登录
    handleLogout() {
      this.$confirm('是否退出登录?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.clear_msg();
        this.$message({
          type: 'success',
          message: '已退出登录'
        });
        this.$router.push("/");
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消'
        });
      });
    },

    // 视频审核相关方法
    getStatusType(status) {
      const statusMap = {
        '待审核': 'warning',
        '已通过': 'success',
        '已拒绝': 'danger'
      };
      return statusMap[status] || 'info';
    },

    approveVideo(video) {
      console.log(video);
      this.$confirm(`确定通过视频 "${video.title}" 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$http({
          url:"/api/video/audit_one_video",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:1,
            id:video.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            video.status = '已通过';

            this.$message({
              type: 'success',
              message: '视频已通过审核'
            });
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },

    rejectVideo(video) {
      this.$confirm(`确定拒绝视频 "${video.title}" 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url:"/api/video/audit_one_video",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:0,
            id:video.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            video.status = '已拒绝';
            this.$message({
              type: 'success',
              message: '视频已拒绝通过审核'
            });
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },

    viewVideo(video) {
      this.$router.push({
        name:"video_detail_manager",
        params:{
          video_msg:video,
          op:"view_video"
        }
      });
    },
    confirm_add(){
      this.$refs.announcement.validate((valid)=>{
        if (valid){
          this.$http({
            url:"/api/announcement/add_announce",
            method:"POST",
            headers:{
              'Content-Type':"application/x-www-form-urlencoded",
              auth:this.get_token
            },
            data:{
              token:this.get_token,
              title:this.announcement.title,
              content:this.announcement.content,
              img_dat:this.announcement.img_dat
            }
          }).then(res=>{
            if (res.data.result){
              this.$message.success("添加成功");
              this.get_announce();
            }else {
              this.$message.error("添加失败");
            }
          });
          this.open_announcement=false;
        }
      });


    },

    refreshData() {
      this.get_videos();
      this.$message({
        type: 'success',
        message: '数据已刷新'
      });
    },

    addAnnouncement() {
      this.open_announcement=true;
    },

    // 分页相关方法
    handleSizeChange(val) {
      this.pageSize = val;
      this.loadData();
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.loadData();
    },

    loadData() {
      // 这里可以添加加载数据的逻辑
      // console.log(`加载第${this.currentPage}页，每页${this.pageSize}条数据`);
      this.format_video_pages(this.sourceVideoList);
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
          this.announcement.img_dat=dat;
          this.announcement.img_url = URL.createObjectURL(file);
        };
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    approveCategory(category){
      this.$confirm(`确定通过栏目 "${category.name}" 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        this.$http({
          url:"/api/category/audit_one_category",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:1,
            id:category.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            category.status = '已通过';
            this.$message({
              type: 'success',
              message: '栏目已通过审核'
            });
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },
    rejectCategory(category){
      this.$confirm(`确定拒绝栏目 "${category.name}" 吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url:"/api/category/audit_one_category",
          method:"POST",
          headers:{
            auth:this.get_token,
            'Content-Type':"application/x-www-form-urlencoded"
          },
          data:{
            status:0,
            id:category.id,
            username:this.get_username
          }
        }).then(res=>{
          if (res.data.result){
            category.status = '已拒绝';
            this.$message({
              type: 'success',
              message: '操作成功'
            });
          }else {
            this.$message.error("操作失败");
          }
        });
      }).catch(() => {});
    },
    viewCategory(category){
      this.$router.push({
        name:"collection_audit",
        params:{
          collection:category
        }
      })
    },
    handleCategorySizeChange(val) {
      this.categoryPageSize = val;
      this.loadCategoryData();
    },

    handleCategoryCurrentChange(val) {
      this.currentCategoryPage = val;
      this.loadCategoryData();
    },
    loadCategoryData(){
      this.format_category_pages(this.sourceCategoryList);
    },
    handleApplySizeChange(val) {
      this.applyPageSize = val;
      this.loadApplyData();
    },

    handleApplyCurrentChange(val) {
      this.currentApplyPage = val;
      this.loadApplyData();
    },
    loadApplyData(){
      this.format_apply_pages(this.sourceApplyList);
    }
  }
};
</script>

<style scoped>
.admin-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

/* 导航栏样式 */
.admin-navbar {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.el-menu-admin {
  display: flex;
  align-items: center;
  min-height: 60px;
  padding: 0 20px;
}

.navbar-brand {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  margin-right: 40px;
  display: flex;
  align-items: center;
}

.navbar-brand i {
  font-size: 20px;
  margin-right: 8px;
}

.navbar-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  width: 200px;
}

.search-box .el-input {
  border-radius: 20px;
}

.user-info {
  color: #bfcbd9;
  cursor: pointer;
  padding: 0 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info:hover {
  color: #409EFF;
}

.user-avatar {
  background-color: #409EFF;
  color: #fff;
}

.username {
  font-size: 14px;
}

.time-display {
  color: #bfcbd9;
  font-size: 14px;
  padding: 0 10px;
}

/* 菜单项样式调整 */
.el-menu-item,
.el-submenu__title {
  height: 60px;
  line-height: 60px;
  border-bottom: 3px solid transparent;
}

.el-menu-item.is-active {
  border-bottom-color: #409EFF;
  background-color: #263445 !important;
}

/* 主内容区域 */
.admin-main {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

.card-container {
  min-height: calc(100vh - 120px);
}

.content-card {
  margin-bottom: 20px;
  min-height: 600px;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}

.card-actions {
  display: flex;
  gap: 10px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

/* 统计卡片样式 */
.stats-container {
  padding: 10px 0;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
}

.stat-item {
  padding: 15px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 图表区域样式 */
.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.chart-card {
  border-radius: 8px;
  height: 300px;
}

.chart-placeholder {
  height: 240px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.fake-chart {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  height: 200px;
  width: 100%;
}

.fake-bar {
  flex: 1;
  background: linear-gradient(to top, #409EFF, #79bbff);
  border-radius: 4px;
  min-height: 20px;
}

.fake-line-chart {
  width: 100%;
  height: 200px;
  background: linear-gradient(180deg, #f0f9ff 0%, #e6f7ff 100%);
  border-radius: 4px;
  position: relative;
}

.fake-line-chart::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 2px;
  background: #409EFF;
  transform: translateY(-50%);
}

/* 欢迎卡片样式 */
.welcome-card {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
}

.welcome-content {
  padding: 20px;
}

.welcome-header {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-header h1 {
  color: #303133;
  margin-bottom: 10px;
}

.welcome-header p {
  color: #606266;
  font-size: 16px;
}

/* 快速统计卡片 */
.quick-stats {
  margin-bottom: 40px;
}

.quick-stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.quick-stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1) !important;
}

.quick-stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  font-size: 32px;
}

.pending-icon {
  color: #e6a23c;
}

.user-icon {
  color: #409EFF;
}

.video-icon {
  color: #67c23a;
}

.comment-icon {
  color: #f56c6c;
}

.quick-stat-content {
  flex: 1;
}

.quick-stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.quick-stat-label {
  font-size: 14px;
  color: #909399;
}

/* 快捷操作卡片 */
.quick-actions {
  margin-top: 30px;
}

.quick-actions h3 {
  color: #303133;
  margin-bottom: 20px;
  padding-left: 10px;
}

.quick-action-card {
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  padding: 25px 15px;
}

.quick-action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1) !important;
}

.action-icon {
  font-size: 36px;
  color: #409EFF;
  margin-bottom: 15px;
}

.action-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.action-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
}

/* 分页样式 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式调整 */
@media screen and (max-width: 1200px) {
  .navbar-brand span {
    display: none;
  }

  .navbar-brand {
    margin-right: 10px;
  }

  .search-box {
    width: 150px;
  }

  .chart-row {
    grid-template-columns: 1fr;
  }
}

@media screen and (max-width: 768px) {
  .admin-main {
    padding: 10px;
  }

  .navbar-right {
    gap: 10px;
  }

  .search-box {
    display: none;
  }

  .username {
    display: none;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-stats .el-col {
    margin-bottom: 15px;
  }

  .quick-actions .el-col {
    margin-bottom: 15px;
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
