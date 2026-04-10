<template>
  <div class="video-edit-container">
    <div class="main-content">
      <div class="top-bar">
        <div class="page-title">
          <h2>视频编辑</h2>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="saveVideo">
            <i class="el-icon-upload2"></i> 保存修改
          </el-button>
          <el-button @click="cancelEdit">
            <i class="el-icon-close"></i> 取消
          </el-button>
        </div>
      </div>
      <div class="edit-content">
        <el-row :gutter="24">
          <el-col :span="16">
            <el-card class="edit-card">
              <div slot="header" class="card-header">
                <span>基本信息</span>
              </div>
              <el-form
                  ref="videoForm"
                  :model="videoData"
                  :rules="formRules"
                  label-width="100px"
                  label-position="left">
                <el-form-item label="视频标题" prop="title">
                  <el-input
                      v-model="videoData.title"
                      placeholder="请输入视频标题"
                      maxlength="50"
                      show-word-limit
                  />
                </el-form-item>
                <el-form-item label="视频分类" prop="video_type">
                  <el-select
                      v-model="videoData.video_type"
                      placeholder="请选择分类"
                      style="width: 100%"
                  >
                    <el-option v-for="item in categories" :key="item.id" :label="item.type" :value="item.type"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="视频描述" prop="description">
                  <el-input
                      type="textarea"
                      v-model="videoData.description"
                      placeholder="请输入视频描述"
                      :rows="4"
                      maxlength="500"
                      show-word-limit/>
                </el-form-item>
                <el-form-item label="封面设置">
                  <div class="cover-upload">
                    <div class="cover-uploader">
                      <img v-if="videoData.coverUrl" :src="videoData.coverUrl" class="cover-image" />
                    </div>
                  </div>
                </el-form-item>
                <input
                    ref="videoInput"
                    type="file"
                    accept="video/*"
                    style="display: none"
                    @change="handleVideoChange">
                <input
                    ref="coverInput"
                    type="file"
                    accept="image/*"
                    style="display: none"
                    @change="handleCoverChange">
              </el-form>
            </el-card>
          </el-col>
          <!-- 右侧预览区域 -->
          <el-col :span="8">
            <el-card class="preview-card">
              <div slot="header" class="card-header">
                <span>视频预览</span>
              </div>
              <div class="video-preview">
                <div class="video-player">
                  <video
                      v-if="videoData.videoUrl"
                      :src="videoData.videoUrl"
                      controls
                      class="preview-video"
                  ></video>
                  <div v-else class="no-video">
                    <i class="el-icon-video-play" style="font-size: 48px; color: #909399;"></i>
                    <div style="margin-top: 16px; color: #909399;">暂无视频</div>
                  </div>
                </div>
                <div class="preview-info">
                  <h4 class="preview-title">{{ videoData.title || '视频标题' }}</h4>
                  <div class="preview-meta">
                    <span class="meta-item">
                      <i class="el-icon-view"></i>
                      {{ videoData.views || 0 }} 次点赞
                    </span>
                    <span class="meta-item">
                      <i class="el-icon-date"></i>
                      {{ formatDate(videoData.createTime) }}
                    </span>
                    <span class="meta-item" v-if="videoData.status=='PENDING'">
                      审核中
                    </span>
                  </div>
                </div>
              </div>
              <div class="video-replace" style="margin-top: 20px;">
                <el-button
                    type="primary"
                    size="small"
                    @click="replaceVideo"
                    icon="el-icon-refresh"
                >
                  替换视频
                </el-button>
                <el-button
                    type="primary"
                    size="small"
                    @click="replaceImg"
                    icon="el-icon-refresh"
                >
                  替换封面
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: 'VideoEdit',
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
      activeMenu: 'video-edit',
      categories: [],
      videoData: {
        id: 'VID20260203001',
        title: '犯泽先生第一集',
        description: '无',
        coverUrl: 'http://localhost:8026/imgs/1770044948573.png',
        videoUrl: 'http://localhost:8026/videos/1770044948573.mp4',
        views: 1234,
        createTime: "",
        status:"",
        video_dat:"",
        img_dat:"",
        video_type:null
      },
      imgBlock:{
        username:localStorage.getItem("username")
      },
      headerBlock:{
        auth:localStorage.getItem("token")
      },
      formRules: {
        title: [
          { required: true, message: '请输入视频标题', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        description: [
          { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
        ],
        video_type:[
          {
            required:true,message:'请选择分类',trigger:"blur"
          }
        ]
      }
    }
  },
  computed:{
    ...mapGetters(['get_username','get_token'])
  },
  methods: {
    get_video_type(){
      this.categories=[];
      this.$http({
        url:"/api/video/get_all_video_kinds",
        headers:{
          auth:this.get_token
        }
      }).then(res=>{
        for (let i = 0; i < res.data.dat.length; i++) {
          this.categories[i]={
            id:`"${res.data.dat[i].id}"`,
            type:res.data.dat[i].type
          }
        }
        this.categories=res.data.dat;
      });
    },
    replaceImg(){
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
          this.videoData.img_dat=dat;
          this.videoData.coverUrl = URL.createObjectURL(file);
          console.log(this.videoData.img_dat);
        };
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    handleVideoChange(event){
      console.log(event);
      const file = event.target.files[0]
      if (file) {
        const allowedTypes = ['video/mp4', 'video/avi', 'video/quicktime']
        const maxSize = 2 * 1024 * 1024 * 1024 // 2GB
        if (!allowedTypes.includes(file.type)) {
          this.$message.error('不支持的文件格式，请上传MP4、AVI或MOV格式的视频')
          return
        }
        if (file.size > maxSize) {
          this.$message.error('文件大小不能超过2GB')
          return
        }
        let file_reader=new FileReader();
        file_reader.onload=file_event=>{
          const base64_dat=file_event.target.result;
          this.videoData.video_dat=base64_dat;
          this.videoData.videoUrl = URL.createObjectURL(file);
          console.log(base64_dat);
        }
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
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
    saveVideo() {
      this.$refs.videoForm.validate((valid) => {
        if (valid) {
          this.$http({
            url:"/api/video/update_video",
            method:"POST",
            headers:{
              "auth":this.get_token,
              'Content-Type':"application/x-www-form-urlencoded"
            },
            data:{
              title:this.videoData.title,
              desc:this.videoData.description,
              img_dat:this.videoData.img_dat,
              video_dat:this.videoData.video_dat,
              id:this.videoData.id,
              username:this.get_username,
              video_type:this.videoData.video_type
            }
          }).then(res=>{
            if (res.data.result){
              this.$message({
                message: '视频保存成功',
                type: 'success'
              });
              this.$router.push({
                name:"user_center",
                params:{
                  index:"videos"
                }
              });
            }else {
              this.$message({
                message: '视频保存失败',
                type: 'error'
              })
            }
          });

        } else {
          this.$message.error('请完善表单信息');
        }
      })
    },
    cancelEdit() {
      this.$confirm('确定要取消编辑吗？未保存的内容将会丢失。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.push({
          name:"user_center",
          params:{
            index:"videos"
          }
        });
      }).catch(()=>{

      })
    },
    replaceVideo() {
      this.$refs.videoInput.click();
    },
  },
  mounted() {
    let dat=this.$router.currentRoute.params;
    console.log(dat);
    this.videoData.id=dat.video_msg.id;
    this.videoData.coverUrl=dat.video_msg.cover;
    this.videoData.createTime=dat.video_msg.createTime;
    this.videoData.videoUrl=dat.video_msg.videoUrl;
    this.videoData.views=dat.video_msg.views;
    this.videoData.status=dat.video_msg.status;
    this.videoData.title=dat.video_msg.title;
    this.videoData.video_type=dat.video_msg.video_type;
    this.videoData.description=dat.video_msg.desc;
    this.get_video_type();
    // 模拟加载视频数据
    // 这里应该从API获取数据
    // setTimeout(() => {
    //   this.videoData = {
    //     ...this.videoData,
    //     title: '示例视频标题',
    //     description: '这是一个示例视频的描述内容，可以在这里详细介绍视频的内容和特点。',
    //     category: 'life',
    //     tags: ['生活', '记录', '日常'],
    //     coverUrl: 'https://via.placeholder.com/1280x720',
    //     videoUrl: 'https://example.com/sample-video.mp4'
    //   }
    // }, 300)
  }
}
</script>

<style scoped>
.video-edit-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  width: 240px;
  background-color: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  position: fixed;
  height: 100vh;
  z-index: 1000;
}

.system-title {
  padding: 20px;
  border-bottom: 1px solid #e6e6e6;
}

.system-title h2 {
  margin: 0 0 16px 0;
  color: #409EFF;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-size: 14px;
  color: #666;
}

.side-menu {
  border-right: none;
  margin-top: 10px;
}

.side-menu >>> .el-menu-item {
  height: 56px;
  line-height: 56px;
}

.side-menu >>> .el-menu-item i {
  margin-right: 12px;
}

.main-content {
  flex: 1;
  margin-left: 0px;
  min-height: 100vh;
}

.top-bar {
  background-color: #fff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title h2 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.video-id {
  font-size: 12px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.edit-content {
  padding: 0 24px 24px;
}

.edit-card, .preview-card, .history-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.card-header {
  font-weight: 600;
  color: #333;
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

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #8c939d;
}

.cover-icon {
  font-size: 36px;
  margin-bottom: 8px;
}

.cover-hint {
  font-size: 12px;
  margin-top: 8px;
  color: #999;
}

.video-preview {
  background-color: #f8f9fa;
  border-radius: 6px;
  overflow: hidden;
}

.preview-video {
  width: 100%;
  height: 200px;
  object-fit: cover;
  background-color: #000;
}

.no-video {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.preview-info {
  padding: 16px;
}

.preview-title {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 12px;
  color: #666;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.video-replace {
  display: flex;
  gap: 12px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-action {
  font-weight: 500;
}

.record-user {
  color: #666;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar {
    width: 200px;
  }

  .main-content {
    margin-left: 0px;
  }
}

@media (max-width: 992px) {
  .video-edit-container {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: auto;
    position: relative;
    margin-bottom: 0;
  }

  .main-content {
    margin-left: 0;
  }

  .edit-content {
    padding: 16px;
  }
}
</style>
