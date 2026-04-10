const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  publicPath:"/",
  devServer: {
    port: 8025,
    proxy: {
      '/api': {
        target: 'http://localhost:8026',
        changeOrigin: true
        // 不配置 pathRewrite，请求 /api/video/publish_video 将直接转发到后端 /api/video/publish_video
      }
    }
  }
})
