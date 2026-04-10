<template>
  <div class="video-publish">
    <div class="publish-header">
      <h2 class="title">发布视频</h2>
      <p class="subtitle">与大家分享您的精彩内容</p>
    </div>
    <div class="publish-content">
      <el-form
          ref="publishForm"
          :model="publishForm"
          :rules="publishRules"
          label-width="100px"
          class="publish-form">
        <el-form-item label="视频文件" prop="videoFile" class="upload-section">
          <div class="upload-area" @click="triggerVideoUpload">
            <div v-if="!publishForm.videoFile" class="upload-placeholder">
              <i class="el-icon-upload"></i>
              <p>点击上传视频文件</p>
              <p class="upload-hint">支持MP4、AVI、MOV格式，最大2GB</p>
            </div>
            <div v-else class="upload-preview">
              <video
                  v-if="videoPreviewUrl"
                  :src="videoPreviewUrl"
                  controls
                  class="video-preview"
              ></video>
              <div class="file-info">
                <p class="file-name">{{ publishForm.videoFile.name }}</p>
                <p class="file-size">{{ formatFileSize(publishForm.videoFile.size) }}</p>
                <el-progress
                    v-if="uploadProgress > 0"
                    :percentage="uploadProgress"
                    :show-text="false"
                    class="upload-progress"
                ></el-progress>
                <div class="upload-actions">
                  <el-button
                      type="text"
                      @click.stop="removeVideoFile"
                      class="remove-btn"
                  >
                    移除
                  </el-button>
                  <el-button
                      type="text"
                      @click.stop="triggerVideoUpload"
                      class="change-btn"
                  >
                    更换
                  </el-button>
                </div>
              </div>
            </div>
            <input
                ref="videoInput"
                type="file"
                accept="video/*"
                style="display: none"
                @change="handleVideoChange">
          </div>
        </el-form-item>
        <el-form-item label="视频封面" prop="coverImage" class="cover-section">
          <div class="cover-upload">
            <div
                class="cover-preview"
                :style="{ backgroundImage: `url(${coverPreviewUrl})` }"
                @click="triggerCoverUpload"
            >
              <div v-if="!coverPreviewUrl" class="cover-placeholder">
                <i class="el-icon-picture"></i>
                <p>上传封面图</p>
              </div>
              <div class="cover-actions">
                <el-button
                    type="text"
                    @click.stop="triggerCoverUpload"
                    class="cover-btn"
                >
                  {{ coverPreviewUrl ? '更换' : '上传' }}
                </el-button>
                <el-button
                    v-if="coverPreviewUrl"
                    type="text"
                    @click.stop="removeCoverImage"
                    class="cover-btn remove"
                >
                  移除
                </el-button>
              </div>
            </div>
            <input
                ref="coverInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleCoverChange"
            >
            <p class="cover-hint">建议尺寸16:9，支持JPG、PNG格式</p>
          </div>
        </el-form-item>
        <el-form-item label="视频标题" prop="title">
          <el-input
              v-model="publishForm.title"
              placeholder="请输入视频标题（最多50个字）"
              maxlength="50"
              show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="视频分类" prop="video_type">
          <el-select
              v-model="publishForm.video_type"
              placeholder="请选择分类"
              style="width: 100%"
              @change="handleSelectChange"
          >
            <el-option v-for="item in categories" :key="item.id" :label="item.type" :value="item.type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="视频描述" prop="description">
          <el-input
              v-model="publishForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入视频描述（最多500个字）"
              maxlength="500"
              show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button
              type="primary"
              :loading="publishing"
              @click="handlePublish"
              class="publish-btn"
          >
            {{ publishing ? '发布中...' : '立即发布' }}
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 上传进度弹窗 -->
    <el-dialog
        title="视频上传中"
        :visible.sync="showUploadDialog"
        width="400px"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
    >
      <div class="upload-dialog-content">
        <el-progress
            :percentage="uploadProgress"
            :status="uploadStatus"
            class="upload-dialog-progress"
        ></el-progress>
        <p class="upload-status">{{ uploadStatusText }}</p>
        <div class="upload-dialog-actions" v-if="uploadStatus === 'success' || uploadStatus === 'exception'">
          <el-button type="primary" @click="showUploadDialog = false">确定</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: 'VideoPublish',
  created() {
    this.get_video_type();
  },
  data() {
    return {
      publishing: false,
      showUploadDialog: false,
      uploadProgress: 0,
      uploadStatus: '', // '' | 'success' | 'exception'
      uploadStatusText: '',
      tagInputVisible: false,
      tagInputValue: '',
      videoPreviewUrl: '',
      coverPreviewUrl: '',
      categories: [
      ],
      publishForm: {
        videoFile: null,
        coverImage: null,
        title: '',
        description: '',
        video_data:"",
        cover_image_data:'',
        video_type:null
      },
      publishRules: {
        videoFile: [
          { required: true, message: '请上传视频文件', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入视频标题', trigger: 'blur' },
          { min: 2, max: 50, message: '标题长度在2到50个字符', trigger: 'blur' }
        ],
        description: [
          { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
        ],
        video_type: [
          { required: true, message: '请选择视频类别', trigger: 'blur'}
        ]
      }
    }
  },
  computed:{
    ...mapGetters(['get_username','get_token','get_expire_time'])
  },
  beforeRouteEnter(to,from,next){
    let date=new Date();
    let compare=date.getTime();
    let token=localStorage.getItem("token");
    let expire_time=localStorage.getItem("expire_time");
    if (token==null||token.length==0){
      next("/user_index");
    }else if(compare>expire_time){
      next("/user_index");
    }
    else {
      next();
    }
  },
  methods: {
    handleSelectChange(){
      console.log(this.publishForm.video_type);
    },
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
    // 触发视频文件上传
    triggerVideoUpload() {
      this.$refs.videoInput.click()
    },
    // 处理视频文件选择
    handleVideoChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 验证文件类型和大小
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
          this.publishForm.video_data=base64_dat;
          this.publishForm.videoFile = file;
          this.videoPreviewUrl = URL.createObjectURL(file);
          // 触发表单验证
          this.$refs.publishForm.validateField('videoFile')
          console.log(base64_dat);
        }
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    // 移除视频文件
    removeVideoFile() {
      this.publishForm.videoFile = null
      this.videoPreviewUrl = ''
      this.$refs.videoInput.value = ''
    },

    // 触发封面图上传
    triggerCoverUpload() {
      this.$refs.coverInput.click()
    },

    // 处理封面图选择
    handleCoverChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 验证文件类型
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']

        if (!allowedTypes.includes(file.type)) {
          this.$message.error('不支持的文件格式，请上传JPG或PNG格式的图片')
          return
        }
        let file_reader=new FileReader();
        file_reader.onload=(e)=>{
          let dat=e.target.result;
          this.publishForm.cover_image_data=dat;
          this.publishForm.coverImage = file;
          this.coverPreviewUrl = URL.createObjectURL(file);
          console.log(this.publishForm.cover_image_data);
        };
        file_reader.onerror = (error) => {
          console.error('文件读取错误:', error);
          this.$message.error('文件读取失败，请重试');
        };
        file_reader.readAsDataURL(file);
      }
    },
    // 移除封面图
    removeCoverImage() {
      this.publishForm.coverImage = null
      this.coverPreviewUrl = ''
      this.$refs.coverInput.value = ''
    },
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    // 处理发布
    handlePublish() {
      this.$refs.publishForm.validate((valid) => {
        if (valid) {
          this.publishing = true
          this.showUploadDialog = true
          this.uploadProgress = 0
          this.uploadStatus = ''
          this.uploadStatusText = '准备上传...'
          this.realUpload();
        } else {
          this.$message.error('请完善表单信息')
          return false
        }
      })
    },
    // 模拟上传过程
    realUpload() {
      // console.log(this.publishForm.title);
      // console.log(this.publishForm.description);
      // console.log(this.publishForm.cover_image_data);
      // console.log(this.publishForm.video_data);
      console.log(this.publishForm.video_type);
      this.$http({
        url:"/api/video/publish_video",
        headers:{
          'auth':this.get_token,
          'Content-Type':"application/x-www-form-urlencoded"
        },
        method:"POST",
        data:{
          title:this.publishForm.title,
          description:this.publishForm.description,
          img_dat:this.publishForm.cover_image_data,
          video_dat:this.publishForm.video_data,
          username:this.get_username,
          video_type:this.publishForm.video_type
        }
      }).then(res=>{
        if (res.data.result){
          this.uploadProgress = 50;
          this.uploadStatus = 'success';
          this.uploadStatusText = '上传成功！';
          this.uploadProgress=100;
          this.publishing = false
          this.$message.success('视频发布成功！')
          this.showUploadDialog = false;
          this.$router.push({
            name:"user_center",
            params:{
              index:"videos"
            }
          });
        }else {
          this.uploadProgress = 50;
          this.uploadStatus = 'success';
          this.uploadStatusText = '上传失败！';

          this.uploadProgress=100;
          this.publishing = false
          this.$message.success('视频发布失败！')
          this.showUploadDialog = false;
        }
      });

      // const interval = setInterval(() => {
      //   this.uploadProgress += Math.floor(Math.random() * 5) + 1
      //   this.uploadStatusText = `上传中... ${this.uploadProgress}%`
      //   if (this.uploadProgress >= 100) {
      //     clearInterval(interval)
      //     this.uploadProgress = 100
      //     this.uploadStatus = 'success'
      //     this.uploadStatusText = '上传成功！'
      //     模拟处理完成
      //     setTimeout(() => {
      //       this.publishing = false
      //       this.$message.success('视频发布成功！')
      //       this.showUploadDialog = false
      //       //this.$router.push('/user/videos')
      //     }, 1000)
      //   }
      // }, 200)
    },

    // 保存草稿
    handleSaveDraft() {
      // 这里可以调用API保存草稿
      this.$message.success('草稿保存成功')
    },

    // 取消发布
    handleCancel() {
      this.$confirm('确定要取消发布吗？已填写的内容将不会保存。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.go(-1)
      })
    }
  },

  beforeDestroy() {
    // 清理URL对象
    if (this.videoPreviewUrl) {
      URL.revokeObjectURL(this.videoPreviewUrl)
    }
    if (this.coverPreviewUrl) {
      URL.revokeObjectURL(this.coverPreviewUrl)
    }
  }
}
</script>

<style scoped>
.video-publish {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  min-height: 100vh;
}

.publish-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eaeaea;
}

.title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.subtitle {
  color: #909399;
  font-size: 14px;
}

.publish-content {
  padding: 0 20px;
}

.publish-form {
  max-width: 700px;
  margin: 0 auto;
}

/* 上传区域样式 */
.upload-section,
.cover-section {
  margin-bottom: 30px;
}

.upload-area {
  border: 2px dashed #dcdfe6;
  border-radius: 6px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
  background-color: #fafafa;
}

.upload-area:hover {
  border-color: #409EFF;
}

.upload-placeholder i {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-placeholder p {
  margin: 8px 0;
  color: #606266;
}

.upload-hint {
  font-size: 12px;
  color: #909399;
}

.upload-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.video-preview {
  max-width: 300px;
  max-height: 200px;
  border-radius: 4px;
  background-color: #000;
}

.file-info {
  flex: 1;
  text-align: left;
}

.file-name {
  font-weight: 500;
  margin-bottom: 8px;
  word-break: break-all;
}

.file-size {
  color: #909399;
  font-size: 12px;
  margin-bottom: 10px;
}

.upload-progress {
  margin-bottom: 10px;
}

.upload-actions {
  display: flex;
  gap: 10px;
}

.remove-btn,
.change-btn {
  padding: 0;
  font-size: 12px;
}

/* 封面图样式 */
.cover-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 10px;
}

.cover-preview {
  width: 200px;
  height: 112px; /* 16:9 比例 */
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.cover-preview:hover .cover-actions {
  opacity: 1;
}

.cover-placeholder {
  text-align: center;
  color: #c0c4cc;
}

.cover-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

.cover-actions {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  padding: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.cover-btn {
  color: #fff;
  padding: 4px 8px;
  font-size: 12px;
}

.cover-btn.remove {
  color: #f56c6c;
}

.cover-hint {
  font-size: 12px;
  color: #909399;
  margin: 0;
}

.publish-options .el-checkbox {
  margin-right: 20px;
}

/* 操作按钮 */
.form-actions {
  text-align: center;
  margin-top: 40px;
}

.publish-btn {
  padding: 12px 30px;
}

/* 上传弹窗样式 */
.upload-dialog-content {
  text-align: center;
}

.upload-dialog-progress {
  margin-bottom: 20px;
}

.upload-status {
  color: #606266;
  margin-bottom: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .video-publish {
    padding: 10px;
  }

  .publish-content {
    padding: 0 10px;
  }

  .upload-preview {
    flex-direction: column;
    text-align: center;
  }

  .file-info {
    text-align: center;
  }

  .publish-options .el-checkbox {
    display: block;
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>
