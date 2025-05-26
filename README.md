# CMP跨平台AI助手
基于Compose Multiplatform跨平台技术，构建的面向Android和IOS平台的AI驱动的功能合集应用。
目前已开发的功能模块：
* 社交圈文案生成器，根据主题和风格，自动生成文案，避免发朋友圈，抖音等没用文案灵感，到处借鉴。
* 卡路里计算器，根据相册图片或者调动相机拍照，交由AI大模型分析，计算每道菜的重量和对应卡路里，帮助用户跟踪自身热量摄入。
* 英语口语练习，根据系统自带的语音识别能力，将用户语音当作英语对话模型输入，练习英语。

## AI模型选取
基础对话api选用deepseek官方的api，多模态如图片识别能力，则是选取集成了字节的doubao-vision大模型。

## 应用运行截图
### 首页
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_android_main_page.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_ios_main_page.png" alt="Image 2" style="width: 45%;">
</div>

### 设置页面
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_android_settings.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_ios_settings.png" alt="Image 2" style="width: 45%;">
</div>

### 社交圈文案生成
<div style="display: flex; justify-content: center;">
  <img src="/screenshots/pic_android_social_copywriting.png" alt="Image 1" style="width: 45%; margin-right: 5%;">
  <img src="/screenshots/pic_ios_social_copywriting.png" alt="Image 2" style="width: 45%;">
</div>

### 食物卡路里计算
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
