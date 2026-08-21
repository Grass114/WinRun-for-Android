# WinRun for Android
⚡ Android 版“运行” — 像 Windows 一样快速启动应用、打开文件、访问网址。 支持中文应用名、文件浏览、实时目录列表，轻量无广告。

## 功能
- 输入应用名（中文）快速启动
- 输入包名打开应用
- 输入路径浏览文件（实时目录列表）
- 输入网址打开网页
- 文件选择器
- 目录浏览（支持返回上级，点击文件夹进入）
- 图片预览
- 音频播放（支持 mp3、wav、flac、m4a 等，可拖动进度条）
- 文档预览（txt、log、xml、json 等）
- 自适应分辨率
- 支持 Android 5.0+

## 编译
需要 Android SDK (Build Tools 34.0.0, Platform android-34)

### Windows 编译
javac -encoding UTF-8 -d . -cp %ANDROID_HOME%/platforms/android-34/android.jar src/com/bluecat114/run/MainActivity.java
jar -cf classes.jar com/bluecat114/run/*.class
%ANDROID_HOME%/build-tools/34.0.0/d8 --lib %ANDROID_HOME%/platforms/android-34/android.jar --output . classes.jar
%ANDROID_HOME%/build-tools/34.0.0/aapt2 compile -o compiled/ res/values/strings.xml
%ANDROID_HOME%/build-tools/34.0.0/aapt2 link -o app_base.apk -I %ANDROID_HOME%/platforms/android-34/android.jar --manifest AndroidManifest.xml compiled/values_strings.arsc.flat
jar -uf app_base.apk classes.dex
%ANDROID_HOME%/build-tools/34.0.0/apksigner sign --ks mykey.jks --ks-pass pass:yourpassword --min-sdk-version 21 app_base.apk
move app_base.apk app_final.apk
