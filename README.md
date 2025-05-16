# CMP跨平台AI助手
基于Compose Multiplatform跨平台技术，构建多个AI驱动的应用场景。

## AI模型选取
基础对话api选用deepseek官方api，多模态能力介入了字节的豆包支持。

## 应用部分截图
### 首页
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_android_mainpage.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_ios_mainpage.png" alt="Image 2" style="width: 45%;">
</div>

### 文案生成
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_android_copy_writing.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_ios_copy_writing.png" alt="Image 2" style="width: 45%;">
</div>

### 卡路里识别
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_ios_calorie.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_android_calorie.png" alt="Image 2" style="width: 45%;">
</div>

TODO LIST:
* 集成语音转文字，完善英语聊天功能

> 目前android端系统自带方法已经完善，但是国产系统能力未知，IOS端调研中。
> 若机型依赖度太高，考虑转换为在线模式，上传音频即可，由云端返回识别结果。

* 采用古怪的方案完成了拍照临时图片的选取传输，后续优化：

> 可以自实现整个图片的选取和传输流程，更进一步可以实现相机功能
