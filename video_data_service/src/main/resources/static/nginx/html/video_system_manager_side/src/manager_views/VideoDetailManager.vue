<template>
  <div class="video-detail-page">
    <!-- 顶部导航和返回按钮 -->
    <div class="page-header">
      <el-button
          icon="el-icon-arrow-left"
          type="text"
          @click="goBack"
          class="back-button"
      >
        返回视频列表
      </el-button>
    </div>
    <div class="video-detail-layout">
      <!-- 左侧主内容区 -->
      <div class="video-main-content">
        <!-- 视频播放器区域 -->
        <div class="video-player-container">
          <div class="video-title-section">
            <h1 class="video-title">{{ video.title }}</h1>
          </div>

          <div class="video-player-wrapper">
            <video
                ref="videoPlayer"
                :src="video.videoUrl"
                controls
                class="main-video-player"
                preload="metadata"
                @play="onVideoPlay"
                @pause="onVideoPause"
                @ended="onVideoEnded"
                poster="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYwMCIgaGVpZ2h0PSI5MDAiIHZpZXdCb3g9IjAgMCAxNjAwIDkwMCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjE2MDAiIGhlaWdodD0iOTAwIiBmaWxsPSIjMDAwIi8+CjxwYXRoIGQ9Ik02NTAgNDUwTDQwMCA2MDBWNTAwTDY1MCA0NTBaIiBmaWxsPSIjMzMzIi8+CjxjaXJjbGUgY3g9IjgwMCIgY3k9IjQ1MCIgcj0iNTAiIGZpbGw9IiMzMzMiLz4KPC9zdmc+"
            >
              您的浏览器不支持视频播放，请升级浏览器或使用Chrome、Firefox等现代浏览器。
            </video>
          </div>
          <!-- 视频交互操作栏 -->
          <div class="video-actions-bar">
            <div class="video-meta-info">
              <div class="video-upload-time">
                <i class="el-icon-time"></i>
                <span>{{ formatUploadTime(video.createTime) }}</span>
              </div>
            </div>
          </div>

          <!-- 视频描述 -->
          <div class="video-description-section" v-if="video.description">
            <h3 class="section-title">视频描述</h3>
            <p class="description-content">{{ video.description }}</p>
          </div>
        <div class="comments-section">
          <div class="comments-header">
            <h3 class="section-title">
              评论 <span class="comments-count">{{ comments.length }}</span>
            </h3>
          </div>
          <!-- 评论列表 -->
          <div class="comments-list" v-if="sortedComments.length > 0">
            <div
                v-for="comment in sortedComments"
                :key="comment.id"
                class="comment-item"
            >
              <div class="comment-avatar">
                <el-avatar
                    :size="36"
                    :src="comment.user.username.charAt(0)"
                >
                  {{ getInitials(comment.user.username || '用户') }}
                </el-avatar>
              </div>
              <div class="comment-content">
                <div class="comment-header">
                  <div class="comment-user-info">
                    <span class="comment-user-name">{{ comment.user.username || '用户' }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
              </div>
              <div>
                <el-button class="delete_comment_style" @click="delete_comment(comment)">删除</el-button>
              </div>

            </div>
          </div>

          <!-- 无评论时的提示 -->
          <div class="no-comments" v-else>
            <div class="no-comments-icon">
              <i class="el-icon-chat-line-square"></i>
            </div>
            <p class="no-comments-text">暂无评论！</p>
          </div>
        </div>


      </div>
    </div>
  </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  created() {
    let video_dat=this.$router.currentRoute.params;
    if (video_dat!=null){
      this.video=video_dat.video_msg;
      this.op=video_dat.op;
    }
    this.loadComments();
  },
  computed:{
    ...mapGetters(['get_token','get_username']),
    sortedComments() {
      // 根据排序选项对评论进行排序
      return [...this.comments].sort((a, b) => {
        if (this.sortBy === 'time_desc') {
          return new Date(b.createTime) - new Date(a.createTime);
        } else if (this.sortBy === 'time_asc') {
          return new Date(a.createTime) - new Date(b.createTime);
        } else if (this.sortBy === 'likes_desc') {
          return (b.likeCount || 0) - (a.likeCount || 0);
        }
        return 0;
      });
    }
  },
  name: "VideoDetailManager",
  data() {
    return{
      video: {
        id: null,
        title: '',
        description: '',
        videoUrl: '',
        coverImage: '',
        createTime: '',
        likeCount: 0,
        viewCount: 0,
        duration: 0,
        author: {
          id: null,
          name: '',
          avatar: '',
          followerCount: 0
        }
      },
      comments: [],
      op:""
    }
  },
  methods:{
    goBack() {
      if (this.op=="view_video"){
        this.$router.push({
          name:"manager_index",
          params:{
            select_menu:"video-audit"
          }
        })
      }else if (this.op=="view_collection_video"){
        this.$router.push({
          name:"manager_index",
          params:{
            select_menu:"column-audit"
          }
        })
      }
      else {
        this.$router.go(-1);
      }
    },
    onVideoPlay() {
      console.log('视频开始播放');
    },
    onVideoPause() {
      console.log('视频暂停');
    },
    onVideoEnded() {
      console.log('视频播放结束');
    },
    formatUploadTime(timeString) {
      if (!timeString) return '';

      const date = new Date(timeString);
      const now = new Date();
      const diff = Math.floor((now - date) / 1000);

      if (diff < 60) return '刚刚';
      if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`;
      if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`;
      if (diff < 2592000) return `${Math.floor(diff / 86400)}天前`;

      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },
    formatTime(timeString) {
      if (!timeString) return '';

      const date = new Date(timeString);
      const now = new Date();
      const diff = Math.floor((now - date) / 1000);

      if (diff < 60) return '刚刚';
      if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`;
      if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`;

      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    getInitials(name) {
      if (!name) return '?';
      return name.charAt(0).toUpperCase();
    },
    delete_comment(comment){
      console.log(comment);
      this.$http({
        url:"/api/comment/delete_comment",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          id:comment.id,
          username:this.get_username
        }
      }).then(res=>{
        if (res.data.result){
          this.$message.success("操作成功");
          this.loadComments();
        }else {
          this.$message.error("操作失败")
        }
      });
    },
    loadComments() {
      this.$http({
        url:"/api/comment/get_all_comments_by_video_id/"+this.video.id,
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          username:this.video.user.username
        }
      }).then(res=>{
        this.comments=res.data.dat;
        console.log(res.data);
      });
    }
  }
}
</script>

<style scoped>
.video-detail-page {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 40px;
}

.page-header {
  background-color: white;
  padding: 12px 24px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.back-button {
  color: #606266;
  font-size: 14px;
}

.back-button:hover {
  color: #409EFF;
}

.video-detail-layout {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  gap: 24px;
}

.video-main-content {
  flex: 1;
  min-width: 0;
}

.video-sidebar {
  width: 320px;
  flex-shrink: 0;
}

/* 视频播放器容器 */
.video-player-container {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.video-title-section {
  padding: 20px 24px 0;
}

.video-title {
  font-size: 22px;
  color: #303133;
  margin: 0;
  line-height: 1.4;
  font-weight: 500;
}

.video-player-wrapper {
  position: relative;
  padding-top: 56.25%; /* 16:9 比例 */
  background-color: #000;
}

.main-video-player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  outline: none;
}

/* 视频交互操作栏 */
.video-actions-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e4e7ed;
}

.video-meta-info {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.video-views,
.video-upload-time {
  display: flex;
  align-items: center;
  gap: 6px;
}

.video-action-buttons {
  display: flex;
  gap: 12px;
}

.like-button,
.comment-button {
  display: flex;
  align-items: center;
  gap: 6px;
}

.liked-button {
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
}

.liked-button:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.like-count,
.comment-count {
  font-weight: 500;
  margin-left: 2px;
}

/* 视频描述 */
.video-description-section {
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  text-align: left;
}

.section-title {
  font-size: 16px;
  color: #303133;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.description-content {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  white-space: pre-line;
}

/* 作者信息 */
.video-author-section {
  padding: 20px 24px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.author-avatar {
  background-color: #409EFF;
  color: white;
}

.author-details {
  flex: 1;
  text-align: left;
}

.author-name {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 4px;
}

.author-subtitle {
  font-size: 14px;
  color: #909399;
}

/* 评论区 */
.comments-section {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comments-count {
  color: #409EFF;
  font-weight: 500;
}

.sort-button {
  color: #606266;
  font-size: 14px;
}

/* 发布评论区域 */
.post-comment-section {
  margin-bottom: 30px;
  background-color: #f9f9f9;
  border-radius: 6px;
  padding: 16px;
}

.comment-input >>> .el-textarea__inner {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 14px;
}

.comment-submit-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  gap: 12px;
}

.cancel-button {
  color: #606266;
}

.submit-button {
  min-width: 80px;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.comment-item {
  display: flex;
  gap: 16px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.comment-header {
  margin-bottom: 8px;
}

.comment-user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-user-name {
  font-size: 15px;
  color: #303133;
  font-weight: 500;
}

.comment-time {
  font-size: 13px;
  color: #909399;
}

.comment-text {
  color: #303133;
  line-height: 1.6;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.comment-like-button,
.comment-reply-button {
  color: #606266;
  font-size: 13px;
  padding: 0;
  min-height: auto;
}

.comment-liked {
  color: #409EFF;
}

/* 回复输入框 */
.reply-input-section {
  margin-top: 12px;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.reply-input >>> .el-textarea__inner {
  font-size: 13px;
  padding: 8px 12px;
}

.reply-submit-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
  gap: 8px;
}

.cancel-reply-button,
.submit-reply-button {
  font-size: 13px;
  padding: 4px 12px;
}

/* 回复列表 */
.replies-list {
  margin-top: 16px;
  padding-left: 20px;
  border-left: 2px solid #e4e7ed;
}

.reply-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-avatar {
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
  min-width: 0;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.reply-user-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.reply-time {
  font-size: 12px;
  color: #909399;
}

.reply-text {
  color: #606266;
  line-height: 1.5;
  font-size: 14px;
}

/* 无评论提示 */
.no-comments {
  text-align: center;
  padding: 60px 0;
}

.no-comments-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.no-comments-text {
  color: #909399;
  font-size: 15px;
  margin: 0;
}

/* 侧边栏相关推荐 */
.video-sidebar {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.sidebar-title {
  font-size: 18px;
  color: #303133;
  margin: 0 0 20px 0;
  font-weight: 500;
}

.recommended-videos {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recommended-video-item {
  display: flex;
  gap: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  padding: 8px;
  border-radius: 4px;
}

.recommended-video-item:hover {
  background-color: #f5f7fa;
}

.recommended-thumbnail {
  position: relative;
  width: 120px;
  height: 68px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
}

.recommended-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-duration {
  position: absolute;
  bottom: 4px;
  right: 4px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 2px;
}

.recommended-info {
  flex: 1;
  min-width: 0;
}

.recommended-title {
  font-size: 14px;
  color: #303133;
  margin: 0 0 8px 0;
  line-height: 1.4;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recommended-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recommended-author {
  font-size: 12px;
  color: #606266;
}

.recommended-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .video-detail-layout {
    flex-direction: column;
  }

  .video-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .video-actions-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .video-action-buttons {
    width: 100%;
    justify-content: flex-start;
  }

  .page-header {
    padding: 12px 16px;
  }

  .video-detail-layout {
    padding: 0 16px;
  }

  .video-title {
    font-size: 18px;
  }
}
.delete_comment_style{
  border-style: none;
}
</style>
