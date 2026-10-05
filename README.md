# 📷 OfflineLens

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Language" />
  <img src="https://img.shields.io/badge/Engine-Google_ML_Kit-4285F4?style=flat-square&logo=google&logoColor=white" alt="Engine" />
  <img src="https://img.shields.io/badge/Build-GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white" alt="Build" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat-square" alt="License" />
</p>

<p align="center">
  <b>一款极致轻量、彻底离线、毫秒级解析的 Android 二维码扫描工具。</b><br>
  <i>零网络权限依赖 · 纯本地硬件级解码 · 极简交互体验</i>
</p>

---

## ✨ 核心特性

- ⚡ **毫秒级离线解析**：内置 Google ML Kit 离线解码模型，无需联网、零接口延迟，对准瞬间即完成识别。
- 🛡️ **纯粹安全隐私**：全应用不申请 `INTERNET` 网络权限，杜绝任何数据回传与隐私追踪，代码完全透明开源。
- 🎯 **智能场景识别**：
  - **网址（URL）**：一键安全跳转默认浏览器或复制链接。
  - **Wi-Fi 配置**：自动提取 SSID 与连接密码，支持一键复制。
  - **纯文本 / 数据**：支持剪贴板快速归档与解析展示。
- 🎨 **极简视界**：沉浸式全屏取景流，搭配精简操作逻辑与定制防误触交互。

---

## 🛠 技术架构

- **语言**：Kotlin
- **相机底层**：Android Jetpack `CameraX` (Preview + ImageAnalysis)
- **解码引擎**：Google ML Kit (`barcode-scanning` 离线套件)
- **CI / CD**：GitHub Actions 全自动化云端编译与构建
- **最低兼容**：Android 5.0 (API 21) 及以上

---

## 👤 作者与致谢

- **原创开发者**：[@imzzzxh](https://github.com/imzzzxh)
- **设计签名**：`OfflineLens · Designed by imzzzxh`
- **技术支持与致谢**：
  - [Google ML Kit](https://developers.google.com/ml-kit)（离线扫码引擎）
  - [Android Jetpack CameraX](https://developer.android.com/training/camerax)（相机控制）
  - [Kotlin](https://kotlinlang.org/)（核心开发语言）
  - [GitHub Actions](https://github.com/features/actions)（自动化云端构建）

---

## 📄 开源许可

本项目遵循 [MIT License](LICENSE) 开源协议。
