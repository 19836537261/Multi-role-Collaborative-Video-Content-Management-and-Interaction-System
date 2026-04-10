<template>
  <div class="collection-detail">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button
            icon="el-icon-arrow-left"
            size="small"
            @click="back_to_center"
        >
          返回
        </el-button>
      </div>
      <div class="header-right">
        <el-button
            type="primary"
            size="small"
            icon="el-icon-plus"
            @click="load_video_data_when_click"
        >
          添加视频
        </el-button>
        <el-drawer
            title="视频列表"
            :visible.sync="drawer"
            :direction="direction"
            :before-close="handleClose" :size="'60%'">
          <div class="video-list-view">
            <div
                v-for="video in pre_classify_videos"
                :key="video.id"
                class="video-list-item"
            >
              <div class="list-thumbnail-wrapper">
                <img :src="video.coverImage" :alt="video.title" />
              </div>
              <div class="list-info-content">
                <h4 class="list-title">{{ video.title }}</h4>
                <p class="list-description">{{ video.description }}</p>
                <div class="list-meta-info">
                  <el-tag size="mini" type="info" v-if="video.status=='PENDING'">审核中</el-tag>
                  <div class="list-stats">
                    <span class="stat">{{ formatDate(video.createTime) }}</span>
                  </div>
                </div>
              </div>
              <div class="list-thumbnail-wrapper">
                <el-tooltip effect="dark" content="点击添加视频" placement="bottom">
                  <el-button class="el-icon-document-add" @click="add_attach_video(video)"></el-button>
                </el-tooltip>
              </div>
            </div>
            <!-- 空状态 -->
            <div class="empty-state" v-if="pre_classify_videos.length==0">
              <el-empty description="暂无视频"></el-empty>
            </div>
          </div>
        </el-drawer>
      </div>
    </div>

    <!-- 合集基本信息 -->
    <el-card class="collection-info-card">
      <div class="collection-basic-info">
        <!-- 合集封面 -->
        <div class="collection-cover">
          <img
              :src="collection.thumbnail"
              alt="合集封面"
              class="cover-image"
          >
        </div>
        <!-- 合集详情 -->
        <div class="collection-detail-info">
          <h2 class="collection-title">{{ collection.name }}</h2>
          <p class="collection-description">{{ collection.description }}</p>
          <div class="collection-meta">
            <div class="meta-item">
              <span class="meta-label">视频数量：</span>
              <span class="meta-value">{{ collection.videoCount }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">创建时间：</span>
              <span class="meta-value">{{ formatDate(collection.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">总点赞数：</span>
              <span class="meta-value">{{ collection.likeCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <!-- 视频列表部分 -->
    <el-card class="video-list-card">
      <div class="card-header">
        <h3 class="section-title">视频列表</h3>
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索视频标题"
              prefix-icon="el-icon-search"
              size="small"
              clearable
              style="width: 200px;"
          />
        </div>
      </div>
      <!-- 视频表格 -->
      <el-table
          :data="videoList"
          style="width: 100%"
          class="video-table"
          row-class-name="video-row"
      >
        <!-- 视频封面列 -->
        <el-table-column
            label="视频封面"
            width="120"
            align="center"
        >
          <template slot-scope="scope">
            <div class="video-cover-cell">
              <img
                  :src="scope.row.coverImage"
                  alt="视频封面"
                  class="video-cover"
              >
            </div>
          </template>
        </el-table-column>
        <!-- 视频标题列 -->
        <el-table-column
            prop="title"
            label="标题"
            min-width="200"
        >
          <template slot-scope="scope">
            <div class="video-title">
              {{ scope.row.title }}
<!--              <span class="video-status" :class="scope.row.statusClass">-->
<!--                {{ scope.row.status }}-->
<!--              </span>-->
            </div>
          </template>
        </el-table-column>
        <el-table-column
            prop="status"
            label="发布状态"
            min-width="200"
        >
          <template slot-scope="scope">
            <div class="video-title">
              <span class="video-status" :class="scope.row.statusClass">
                {{ scope.row.status }}
              </span>
            </div>
          </template>
        </el-table-column>
        <!-- 播放量列 -->
<!--        <el-table-column-->
<!--            prop="views"-->
<!--            label="播放量"-->
<!--            width="120"-->
<!--            align="center"-->
<!--        >-->
<!--          <template slot-scope="scope">-->
<!--            <div class="views-count">-->
<!--              <i class="el-icon-video-play"></i>-->
<!--              {{ scope.row.views }}-->
<!--            </div>-->
<!--          </template>-->
<!--        </el-table-column>-->
        <!-- 点赞数列 -->
        <el-table-column
            prop="likes"
            label="点赞数"
            width="120"
            align="center"
        >
          <template slot-scope="scope">
            <div class="likes-count">
              <i class="el-icon-thumb"></i>
              {{ scope.row.likeCount }}
            </div>
          </template>
        </el-table-column>
        <!-- 发布日期列 -->
        <el-table-column
            prop="publishDate"
            label="发布日期"
            width="120"
            align="center"
        >
          <template slot-scope="scope">
            <div class="likes-count">
              {{ formatDate(scope.row.createTime) }}
            </div>
          </template>
        </el-table-column>
        <!-- 操作列 -->
        <el-table-column
            label="操作"
            width="150"
            align="center"
            fixed="right"
        >
          <template slot-scope="scope">
<!--            <el-button-->
<!--                type="text"-->
<!--                size="small"-->
<!--                @click="handleView(scope.row)"-->
<!--            >-->
<!--              查看-->
<!--            </el-button>-->
            <el-button
                type="text"
                size="small"
                style="color: #F56C6C;"
                @click="handleRemove(scope.row)"
            >
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
<!--      <div class="pagination-wrapper">-->
<!--        <el-pagination-->
<!--            :current-page="pagination.currentPage"-->
<!--            :page-size="pagination.pageSize"-->
<!--            :total="pagination.total"-->
<!--            layout="total, sizes, prev, pager, next, jumper"-->
<!--            :page-sizes="[10]"-->
<!--            @size-change="handleSizeChange"-->
<!--            @current-change="handleCurrentChange"-->
<!--        />-->
<!--      </div>-->
    </el-card>
  </div>
</template>

<script>

import {mapGetters} from "vuex";

export default {
  name: 'CollectionEdit',
  created() {
    if (this.$router.currentRoute.params.collection){
      this.collection=this.$router.currentRoute.params.collection;
      this.collection.videoCount=this.$router.currentRoute.params.collection.videos.length;
      this.collection.totalViews=50;
      this.get_category_video();
    }
  },
  data() {
    return {
      collection: {
        thumbnail: 'https://example.com/cover.jpg',
        name: '村民vs僵尸合集',
        description: '包含所有村民vs僵尸系列视频，精彩刺激的冒险故事',
        videoCount: 15,
        createTime: '2026-01-15',
        likeCount: 0,
        id:""
      },
      // 搜索关键词
      searchKeyword: '',
      // 视频列表数据
      videoList: [
        {
          id: 1,
          cover: 'https://example.com/video1.jpg',
          title: '村民大战僵尸第一集：起源',
          status: '已发布',
          statusClass: 'status-published',
          duration: '12:34',
          views: '50000',
          likes: '2500',
          publishDate: '2026-01-15'
        }
      ],
      show_video_list:[],
      pre_classify_videos:[],
      // 分页数据
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 15
      },
      drawer: false,
      direction: 'rtl'
    }
  },
  computed:{
    ...mapGetters(['get_token','get_username']),
  },
  methods: {
    load_video_data_when_click(){
      this.get_pre_classify_videos();
      this.drawer=true;
    },
    get_pre_classify_videos(){
      this.$http({
        url:"/api/category/get_pre_classify_videos",
        headers:{
          auth:this.get_token
        },
        params:{
          username:this.get_username,
          category_id:this.collection.id
        }
      }).then(res=>{
        console.log(res.data);
        this.pre_classify_videos=res.data.dat;
      });
    },
    get_category_video(){
      this.$http({
        url:"/api/category/get_category_videos",
        headers:{
          auth:this.get_token
        },
        params:{
          category_id:this.collection.id
        }
      }).then(res=>{
        this.collection.likeCount=0;
        this.pagination.total=res.data.dat.length;
        this.videoList=res.data.dat;
        for (let i = 0; i < this.videoList.length; i++) {
          console.log(this.videoList[i].likeCount);
          this.collection.likeCount=this.videoList[i].likeCount+this.collection.likeCount;
          if (this.videoList[i].status=='PENDING')
          {
            this.videoList[i].status="审核中";
          }else if (this.videoList[i].status=='APPROVED')
          {
            this.videoList[i].status="审核通过";
          }else {
            this.videoList[i].status="审核未通过";
          }
        }
        console.log(this.collection.likeCount);
      });

    },
    add_attach_video(video){
      console.log(video);
      this.$http({
        url:"/api/category/attach_video",
        method:"POST",
        headers: {
          "auth": this.get_token,
          'Content-Type': "application/x-www-form-urlencoded"
        },
        data:{
          video_id:video.id,
          category_id:this.collection.id
        }
      }).then(res=>{
        if (res.data.result){
          this.drawer=false;
          this.$message.success("添加成功");
          this.get_category_video();
          this.get_pre_classify_videos();
        }else {
          this.$message.error("添加失败")
        }
      });
    },
    // handleSizeChange(val) {
    //   this.pagination.pageSize = val
    //   // 这里在实际使用中会触发重新加载数据
    // },
    // handleCurrentChange(val) {
    //   this.pagination.currentPage = val;
    //   this.format_videos();
    //   // 这里在实际使用中会触发重新加载数据
    // },

    handleView(video) {
      // 查看视频详情
      console.log('查看视频:', video)
    },

    handleRemove(video) {
      // 移除视频
      console.log('移除视频:', video);
      this.$http({
        url:"/api/category/detach_video",
        method:"POST",
        headers:{
          auth:this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        data:{
          category_id:this.collection.id,
          video_id:video.id
        }
      }).then(res=>{
        if (res.data.result)
        {
          this.get_category_video();
          this.get_pre_classify_videos();
          this.$message.success("移除成功");
        }else {
          this.$message.error("操作失败");
        }
      });
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    },
    back_to_center(){
      this.$router.push({
        name:"user_center",
        params:{
          index:"categories"
        }
      });
    },
    handleClose() {
      this.drawer=false;
    }
  }
}
</script>

<style lang="scss" scoped>
.collection-detail {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px 0;
}

.collection-info-card {
  margin-bottom: 20px;

  .collection-basic-info {
    display: flex;
    gap: 20px;

    @media (max-width: 768px) {
      flex-direction: column;
    }
  }

  .collection-cover {
    position: relative;
    width: 160px;
    height: 90px;
    flex-shrink: 0;

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 4px;
    }

    .cover-actions {
      position: absolute;
      bottom: 5px;
      right: 5px;
    }
  }

  .collection-detail-info {
    flex: 1;

    .collection-title {
      margin: 0 0 10px 0;
      font-size: 20px;
      color: #303133;
      font-weight: 600;
    }

    .collection-description {
      margin: 0 0 15px 0;
      color: #606266;
      line-height: 1.5;
    }

    .collection-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;

      .meta-item {
        .meta-label {
          color: #909399;
          font-size: 14px;
        }

        .meta-value {
          color: #303133;
          font-weight: 500;
        }
      }
    }
  }
}

.video-list-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      margin: 0;
      font-size: 16px;
      color: #303133;
      font-weight: 600;
    }
  }

  .video-cover-cell {
    position: relative;
    display: inline-block;

    .video-cover {
      width: 80px;
      height: 45px;
      object-fit: cover;
      border-radius: 4px;
    }

    .video-duration {
      position: absolute;
      bottom: 2px;
      right: 2px;
      background: rgba(0, 0, 0, 0.7);
      color: white;
      font-size: 12px;
      padding: 1px 4px;
      border-radius: 2px;
    }
  }

  .video-title {
    display: flex;
    align-items: center;
    gap: 8px;

    .video-status {
      font-size: 12px;
      padding: 1px 6px;
      border-radius: 10px;

      &.status-published {
        background-color: #e1f3d8;
        color: #67c23a;
      }

      &.status-reviewing {
        background-color: #fdf6ec;
        color: #e6a23c;
      }
    }
  }

  .views-count,
  .likes-count {
    display: flex;
    align-items: center;
    gap: 4px;

    i {
      color: #909399;
    }
  }

  .pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}

.video-table {
  ::v-deep .video-row {
    &:hover {
      background-color: #f5f7fa;
    }
  }
}
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
.empty-state {
  padding: 60px 0;
  text-align: center;
  background: #fafafa;
  border-radius: 8px;
}
</style>
