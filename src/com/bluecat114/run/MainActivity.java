package com.bluecat114.run;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private EditText et;
    private Map<String, String> appMap;
    private LinearLayout fileListContainer;
    private ScrollView scrollView;
    private static final int FILE_SELECT_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }

        appMap = new HashMap<>();
        appMap.put("微信", "com.tencent.mm");
        appMap.put("qq", "com.tencent.mobileqq");
        appMap.put("支付宝", "com.eg.android.AlipayGphone");
        appMap.put("淘宝", "com.taobao.taobao");
        appMap.put("抖音", "com.ss.android.ugc.aweme");
        appMap.put("设置", "com.android.settings");
        appMap.put("相机", "com.android.camera");
        appMap.put("浏览器", "com.android.browser");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
        setContentView(root);

        // 标题行（带三点菜单）
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getApplicationInfo().loadIcon(getPackageManager()));
        int iconSize = dp2px(32);
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        icon.setPadding(0, 0, dp2px(8), 0);

        TextView title = new TextView(this);
        title.setText("Win 运行");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        title.setTextColor(Color.parseColor("#333333"));

        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 1, 1.0f));

        TextView menuDot = new TextView(this);
        menuDot.setText("⋮");
        menuDot.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        menuDot.setTextColor(Color.parseColor("#333333"));
        menuDot.setPadding(dp2px(12), 0, 0, 0);
        menuDot.setOnClickListener(v -> {
            Toast.makeText(this, "v1.3", Toast.LENGTH_SHORT).show();
        });

        titleRow.addView(icon);
        titleRow.addView(title);
        titleRow.addView(spacer);
        titleRow.addView(menuDot);

        // 说明文字
        TextView desc = new TextView(this);
        desc.setText("Android 将根据你所输入的名称，为你打开相应的程序、文件夹、文档或 Internet 资源。");
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        desc.setTextColor(Color.parseColor("#888888"));
        desc.setGravity(Gravity.START);
        desc.setPadding(0, dp2px(4), 0, dp2px(8));

        // 快速访问提示
        LinearLayout tipLayout = new LinearLayout(this);
        tipLayout.setOrientation(LinearLayout.VERTICAL);
        tipLayout.setPadding(0, dp2px(4), 0, dp2px(12));

        TextView tipText = new TextView(this);
        tipText.setText("如果你不会使用，你可以点击下面的路径");
        tipText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tipText.setTextColor(Color.parseColor("#888888"));
        tipText.setGravity(Gravity.START);

        TextView pathView = new TextView(this);
        pathView.setText("/storage/emulated/0/");
        pathView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        pathView.setTextColor(Color.parseColor("#1976D2"));
        pathView.setGravity(Gravity.START);
        pathView.setPadding(0, dp2px(4), 0, 0);
        pathView.setOnClickListener(v -> {
            String target = "/storage/emulated/0/";
            et.setText(target);
            showFileList(target);
        });

        tipLayout.addView(tipText);
        tipLayout.addView(pathView);

        // 文件列表
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1.0f
        ));
        scrollView.setPadding(0, dp2px(8), 0, dp2px(8));

        fileListContainer = new LinearLayout(this);
        fileListContainer.setOrientation(LinearLayout.VERTICAL);
        fileListContainer.setGravity(Gravity.START);
        scrollView.addView(fileListContainer);

        // 底部行
        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        et = new EditText(this);
        et.setHint("输入路径 / 应用名 / 包名 / 网址…");
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        et.setSingleLine(true);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.parseColor("#999999"));
        GradientDrawable etBg = new GradientDrawable();
        etBg.setShape(GradientDrawable.RECTANGLE);
        etBg.setCornerRadius(dp2px(20));
        etBg.setColor(Color.parseColor("#F5F5F5"));
        et.setBackground(etBg);
        et.setPadding(dp2px(20), dp2px(16), dp2px(20), dp2px(16));

        Button btnBrowse = new Button(this);
        btnBrowse.setText("浏览...");
        btnBrowse.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        btnBrowse.setTextColor(Color.WHITE);
        GradientDrawable browseBg = new GradientDrawable();
        browseBg.setShape(GradientDrawable.RECTANGLE);
        browseBg.setCornerRadius(dp2px(20));
        browseBg.setColor(Color.parseColor("#757575"));
        btnBrowse.setBackground(browseBg);
        btnBrowse.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
        btnBrowse.setOnClickListener(v -> openFilePicker());

        Button btnRun = new Button(this);
        btnRun.setText("运 行");
        btnRun.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        btnRun.setTextColor(Color.WHITE);
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setShape(GradientDrawable.RECTANGLE);
        btnBg.setCornerRadius(dp2px(20));
        btnBg.setColor(Color.parseColor("#1976D2"));
        btnRun.setBackground(btnBg);
        btnRun.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));

        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f
        );
        etParams.setMargins(0, 0, dp2px(8), 0);
        et.setLayoutParams(etParams);

        LinearLayout.LayoutParams browseParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        browseParams.setMargins(0, 0, dp2px(8), 0);
        btnBrowse.setLayoutParams(browseParams);

        LinearLayout.LayoutParams runParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnRun.setLayoutParams(runParams);

        bottomRow.addView(et);
        bottomRow.addView(btnBrowse);
        bottomRow.addView(btnRun);

        root.addView(titleRow);
        root.addView(desc);
        root.addView(tipLayout);
        root.addView(scrollView);
        root.addView(bottomRow);

        btnRun.setOnClickListener(v -> handleInput(et.getText().toString().trim()));

        et.setOnKeyListener((v, key, event) -> {
            if (key == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                handleInput(et.getText().toString().trim());
                return true;
            }
            return false;
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                if (input.startsWith("/") || input.startsWith("file://") || input.contains("/sdcard/")) {
                    showFileList(input);
                } else {
                    clearFileList();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // ========== 文件选择器 ==========
    private void openFilePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, FILE_SELECT_CODE);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_SELECT_CODE);
            } catch (Exception e2) {
                Toast.makeText(this, "无法打开文件选择器", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                et.setText(uri.toString());
                clearFileList();
                Toast.makeText(this, "已选择文件，点击运行打开", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ========== 文件列表 ==========
    private void showFileList(String path) {
        fileListContainer.removeAllViews();

        String cleanPath = path;
        if (cleanPath.startsWith("file://")) {
            cleanPath = cleanPath.substring(7);
        }
        File dir = new File(cleanPath);
        if (!dir.exists()) {
            TextView error = new TextView(this);
            error.setText("路径不存在: " + cleanPath);
            error.setTextColor(Color.RED);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }
        if (!dir.isDirectory()) {
            TextView error = new TextView(this);
            error.setText("不是目录: " + cleanPath);
            error.setTextColor(Color.RED);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }

        File parent = dir.getParentFile();
        if (parent != null && parent.exists() && parent.isDirectory()) {
            LinearLayout row = createFileRow("📁 ..", Color.parseColor("#2196F3"));
            row.setOnClickListener(v -> {
                String parentPath = parent.getAbsolutePath();
                et.setText(parentPath);
                showFileList(parentPath);
            });
            fileListContainer.addView(row);
            addSpacer();
        }

        File[] files = dir.listFiles();
        if (files == null) {
            TextView error = new TextView(this);
            error.setText("无法读取目录（可能权限不足）");
            error.setTextColor(Color.RED);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }
        if (files.length == 0) {
            TextView empty = new TextView(this);
            empty.setText("(空文件夹)");
            empty.setTextColor(Color.GRAY);
            empty.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(empty);
            return;
        }

        Arrays.sort(files, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            return f1.getName().compareToIgnoreCase(f2.getName());
        });

        for (File f : files) {
            String name = f.getName();
            String ext = getFileExtension(name);
            String iconPrefix = "📄 ";
            if (f.isDirectory()) {
                iconPrefix = "📁 ";
            } else if (isImageFile(ext)) {
                iconPrefix = "🖼️ ";
            } else if (isAudioFile(ext)) {
                iconPrefix = "🎵 ";
            } else if (isTextFile(ext)) {
                iconPrefix = "📝 ";
            } else if (ext.equals("apk")) {
                iconPrefix = "📦 ";
            } else if (ext.equals("pdf")) {
                iconPrefix = "📕 ";
            } else if (ext.equals("zip") || ext.equals("rar") || ext.equals("7z")) {
                iconPrefix = "📦 ";
            }

            boolean canPreview = isImageFile(ext) || isAudioFile(ext) || isDocumentFile(ext);
            LinearLayout row = createFileRow(iconPrefix + name, Color.BLACK);

            if (f.isDirectory() || !canPreview) {
                row.setOnClickListener(v -> {
                    if (f.isDirectory()) {
                        String newPath = f.getAbsolutePath();
                        et.setText(newPath);
                        showFileList(newPath);
                    } else {
                        openFile(f);
                    }
                });
            } else {
                row.setOnClickListener(v -> openFile(f));
            }

            if (canPreview && !f.isDirectory()) {
                Button previewBtn = new Button(this);
                previewBtn.setText("📷 预览");
                previewBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                previewBtn.setTextColor(Color.WHITE);
                GradientDrawable btnBg = new GradientDrawable();
                btnBg.setShape(GradientDrawable.RECTANGLE);
                btnBg.setCornerRadius(dp2px(12));
                btnBg.setColor(Color.parseColor("#FF9800"));
                previewBtn.setBackground(btnBg);
                previewBtn.setPadding(dp2px(12), dp2px(4), dp2px(12), dp2px(4));
                previewBtn.setOnClickListener(v -> previewFile(f));
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                btnParams.setMargins(dp2px(8), 0, 0, 0);
                previewBtn.setLayoutParams(btnParams);
                row.addView(previewBtn);
            }

            fileListContainer.addView(row);
            addSpacer();
        }
    }

    private LinearLayout createFileRow(String text, int textColor) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp2px(12), dp2px(10), dp2px(12), dp2px(10));

        GradientDrawable border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setCornerRadius(dp2px(8));
        border.setStroke(dp2px(1), Color.parseColor("#DDDDDD"));
        border.setColor(Color.WHITE);
        row.setBackground(border);
        row.setElevation(dp2px(2));

        TextView item = new TextView(this);
        item.setText(text);
        item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        item.setTextColor(textColor);
        item.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        row.addView(item);
        return row;
    }

    private void addSpacer() {
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp2px(6)));
        fileListContainer.addView(spacer);
    }

    private void clearFileList() {
        fileListContainer.removeAllViews();
    }

    // ========== 预览文件 ==========
    private void previewFile(File file) {
        String ext = getFileExtension(file.getName()).toLowerCase();

        if (isImageFile(ext)) {
            previewImage(file);
        } else if (isAudioFile(ext)) {
            playAudio(file);
        } else if (isDocumentFile(ext)) {
            previewDocument(file);
        } else {
            Toast.makeText(this, "该文件类型暂不支持预览", Toast.LENGTH_SHORT).show();
        }
    }

    private void previewImage(File file) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LinearLayout rootLayout = new LinearLayout(this);
            rootLayout.setOrientation(LinearLayout.VERTICAL);
            rootLayout.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
            rootLayout.setBackgroundColor(Color.WHITE);

            TextView fileName = new TextView(this);
            fileName.setText("🖼️ " + file.getName());
            fileName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            fileName.setTextColor(Color.parseColor("#333333"));
            fileName.setPadding(0, 0, 0, dp2px(12));
            rootLayout.addView(fileName);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            imageView.setAdjustViewBounds(true);
            imageView.setMaxWidth(dp2px(450));
            imageView.setMaxHeight(dp2px(600));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setBackgroundColor(Color.parseColor("#F5F5F5"));
            imageView.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));

            ScrollView scrollView = new ScrollView(this);
            scrollView.addView(imageView);
            rootLayout.addView(scrollView);

            builder.setView(rootLayout)
                    .setPositiveButton("打开", (dialog, which) -> openFile(file))
                    .setNegativeButton("关闭", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "无法预览图片", Toast.LENGTH_SHORT).show();
        }
    }

    private void previewDocument(File file) {
        try {
            if (file.length() > 1024 * 1024) {
                Toast.makeText(this, "文档过大，建议使用专业应用打开", Toast.LENGTH_SHORT).show();
                openFile(file);
                return;
            }

            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 300) {
                content.append(line).append("\n");
                lineCount++;
            }
            reader.close();

            if (content.length() == 0) {
                Toast.makeText(this, "文档内容为空", Toast.LENGTH_SHORT).show();
                return;
            }

            TextView textView = new TextView(this);
            textView.setText(content.toString());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setTextColor(Color.BLACK);
            textView.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));
            textView.setTypeface(android.graphics.Typeface.MONOSPACE);

            ScrollView scrollView = new ScrollView(this);
            scrollView.addView(textView);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(file.getName())
                    .setView(scrollView)
                    .setPositiveButton("打开", (dialog, which) -> openFile(file))
                    .setNegativeButton("关闭", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "无法预览文档: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null) {
                intent.setType(mimeType);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "播放音频"));
        } catch (Exception e) {
            Toast.makeText(this, "无法播放音频", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null) {
                intent.setType(mimeType);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "打开文件"));
        } catch (Exception e) {
            Toast.makeText(this, "无法打开文件", Toast.LENGTH_SHORT).show();
        }
    }

    // ========== 工具方法 ==========
    private String getFileExtension(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    private boolean isImageFile(String ext) {
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") ||
                ext.equals("gif") || ext.equals("bmp") || ext.equals("webp") ||
                ext.equals("heic") || ext.equals("heif");
    }

    private boolean isAudioFile(String ext) {
        return ext.equals("mp3") || ext.equals("wav") || ext.equals("flac") ||
                ext.equals("aac") || ext.equals("m4a") || ext.equals("wma") ||
                ext.equals("ogg") || ext.equals("opus") || ext.equals("amr");
    }

    private boolean isDocumentFile(String ext) {
        return ext.equals("txt") || ext.equals("log") || ext.equals("xml") ||
                ext.equals("json") || ext.equals("html") || ext.equals("css") ||
                ext.equals("js") || ext.equals("md") || ext.equals("cfg") ||
                ext.equals("conf") || ext.equals("sh") || ext.equals("bat") ||
                ext.equals("properties") || ext.equals("java") || ext.equals("c") ||
                ext.equals("cpp") || ext.equals("h") || ext.equals("py") ||
                ext.equals("php") || ext.equals("rb") || ext.equals("go") ||
                ext.equals("rs") || ext.equals("swift") || ext.equals("kt");
    }

    private boolean isTextFile(String ext) {
        return isDocumentFile(ext);
    }

    // ========== 核心输入处理 ==========
    private void handleInput(String input) {
        if (input.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. 文件路径
        if (input.startsWith("/") || input.startsWith("file://") || input.contains("/sdcard/")) {
            String cleanPath = input;
            if (cleanPath.startsWith("file://")) {
                cleanPath = cleanPath.substring(7);
            }
            File file = new File(cleanPath);
            if (file.exists() && file.isFile()) {
                openFile(file);
            } else if (file.exists() && file.isDirectory()) {
                showFileList(cleanPath);
                Toast.makeText(this, "已加载目录: " + cleanPath, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "路径不存在: " + cleanPath, Toast.LENGTH_SHORT).show();
                showFileList(cleanPath);
            }
            return;
        }

        // 2. 应用名映射
        if (appMap.containsKey(input)) {
            String pkg = appMap.get(input);
            if (openApp(pkg)) return;
            Toast.makeText(this, "未找到: " + input, Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 包名
        if (input.contains(".") && !input.contains(" ") &&
                !input.startsWith("http://") && !input.startsWith("https://")) {
            if (openApp(input)) return;
        }

        // 4. 网址
        if (input.startsWith("http://") || input.startsWith("https://") ||
                input.contains(".com") || input.contains(".cn") || input.contains(".org")) {
            String url = input.startsWith("http") ? input : "http://" + input;
            openUrl(url);
            return;
        }

        // 5. 兜底：尝试作为网址打开
        openUrl("http://" + input);
    }

    private boolean openApp(String pkg) {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
            if (intent != null) {
                startActivity(intent);
                Toast.makeText(this, "正在打开: " + pkg, Toast.LENGTH_SHORT).show();
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "无法打开网址", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要存储权限才能浏览文件", Toast.LENGTH_SHORT).show();
            }
        }
    }
}