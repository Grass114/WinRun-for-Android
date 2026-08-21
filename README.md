# WinRun For Android
⚡ Android 版“运行” — 像 Windows 一样快速启动应用、打开文件、访问网址。 支持中文应用名、文件浏览、实时目录列表，轻量无广告。

[![Android](https://img.shields.io/badge/Android-5.0%2B-brightgreen)](https://developer.android.com)
[![Version](https://img.shields.io/badge/version-1.3-blue)](https://github.com/Grass114/WinRun-for-Android/releases)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## 功能
- 输入中文应用名打开应用（如“微信”）
- 输入应用包名打开应用（如“com.android.settings”）
- 输入路径浏览文件（如“/sdcard/”）
- 输入网址打开网页（如“www.baidu.com”）
- 图片预览
- 音频播放（mp3、wav、flac 等）

## 编译
需要 Android SDK (Build Tools 34.0.0, Platform 34) 和 Java JDK 8+。

### v1.3 及以上（Gradle）
git clone https://github.com/Grass114/WinRun-for-Android.git
cd WinRun-for-Android
gradlew.bat build

APK 位置：`app\build\outputs\apk\debug\app-debug.apk`

> 如果 `gradlew.bat` 不存在，先执行 `gradle wrapper`


### v1.0~v1.2（纯命令行）
javac -encoding UTF-8 -d . -cp %ANDROID_HOME%/platforms/android-34/android.jar src/com/bluecat114/run/MainActivity.java
jar -cf classes.jar com/bluecat114/run/*.class
%ANDROID_HOME%/build-tools/34.0.0/d8 --lib %ANDROID_HOME%/platforms/android-34/android.jar --output . classes.jar
%ANDROID_HOME%/build-tools/34.0.0/aapt2 compile -o compiled/ res/values/strings.xml
%ANDROID_HOME%/build-tools/34.0.0/aapt2 link -o app_base.apk -I %ANDROID_HOME%/platforms/android-34/android.jar --manifest AndroidManifest.xml compiled/values_strings.arsc.flat
jar -uf app_base.apk classes.dex
%ANDROID_HOME%/build-tools/34.0.0/apksigner sign --ks mykey.jks --ks-pass pass:yourpassword --min-sdk-version 21 app_base.apk
move app_base.apk app_final.apk

## 开源协议
MIT License © 2026 BlueCat114

## 开发者

- **BlueCat114** ([GitHub](https://github.com/Grass114))
- 项目地址：[Grass114/WinRun-for-Android](https://github.com/Grass114/WinRun-for-Android)
