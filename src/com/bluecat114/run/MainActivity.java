package com.bluecat114.run;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import android.content.res.Resources;
import android.util.TypedValue;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class MainActivity extends Activity {
    private EditText et;
    private Map<String, String> appMap;
    private LinearLayout fileListContainer;
    private ScrollView scrollView;
    private static final int FILE_SELECT_CODE = 100;

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 应用名映射
        appMap = new HashMap<>();
        appMap.put("微信", "com.tencent.mm");
        appMap.put("qq", "com.tencent.mobileqq");
        appMap.put("支付宝", "com.eg.android.AlipayGphone");
        appMap.put("淘宝", "com.taobao.taobao");
        appMap.put("抖音", "com.ss.android.ugc.aweme");
        appMap.put("设置", "com.android.settings");
        appMap.put("相机", "com.android.camera");
        appMap.put("浏览器", "com.android.browser");

        // 根容器
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setPadding(dp2px(16), dp2px(16), dp2px(16), dp2px(16));

        // 标题行
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
        title.setText("运行");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        title.setTextColor(Color.parseColor("#333333"));

        titleRow.addView(icon);
        titleRow.addView(title);

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

        // 文件列表区域
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

        // 底部行：输入框（权重2）、浏览按钮（包裹内容）、运行按钮（包裹内容）
        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setGravity(Gravity.CENTER_VERTICAL);

        // 输入框
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

        // 浏览按钮
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

        // 运行按钮
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

        // 设置布局参数
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f  // 权重2，占据更多空间
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

        // 组装视图
        root.addView(titleRow);
        root.addView(desc);
        root.addView(tipLayout);
        root.addView(scrollView);
        root.addView(bottomRow);
        setContentView(root);

        // 事件监听
        btnRun.setOnClickListener(v -> handleInput(et.getText().toString().trim()));
        et.setOnKeyListener((v, key, event) -> {
            if (key == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                handleInput(et.getText().toString().trim());
                return true;
            }
            return false;
        });

        // 实时文件列表显示
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

    // ---------- 文件选择器 ----------
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_SELECT_CODE);
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

    // ---------- 显示文件列表 ----------
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
            error.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }
        if (!dir.isDirectory()) {
            TextView error = new TextView(this);
            error.setText("不是目录: " + cleanPath);
            error.setTextColor(Color.RED);
            error.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }

        // 返回上级
        File parent = dir.getParentFile();
        if (parent != null && parent.exists() && parent.isDirectory()) {
            TextView upItem = new TextView(this);
            upItem.setText("📁 ..");
            upItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            upItem.setTextColor(Color.parseColor("#2196F3"));
            upItem.setPadding(dp2px(12), dp2px(10), dp2px(12), dp2px(10));
            upItem.setOnClickListener(v -> {
                String parentPath = parent.getAbsolutePath();
                et.setText(parentPath);
                showFileList(parentPath);
            });
            fileListContainer.addView(upItem);
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp2px(1)
            ));
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
            fileListContainer.addView(divider);
        }

        File[] files = dir.listFiles();
        if (files == null) {
            TextView error = new TextView(this);
            error.setText("无法读取目录（可能权限不足）");
            error.setTextColor(Color.RED);
            error.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            error.setPadding(dp2px(8), dp2px(8), dp2px(8), dp2px(8));
            fileListContainer.addView(error);
            return;
        }
        if (files.length == 0) {
            TextView empty = new TextView(this);
            empty.setText("(空文件夹)");
            empty.setTextColor(Color.GRAY);
            empty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
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
            TextView item = new TextView(this);
            String name = f.getName();
            if (f.isDirectory()) {
                name = "📁 " + name;
            } else {
                name = "📄 " + name;
            }
            item.setText(name);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            item.setTextColor(Color.BLACK);
            item.setPadding(dp2px(12), dp2px(10), dp2px(12), dp2px(10));
            item.setOnClickListener(v -> {
                if (f.isDirectory()) {
                    String newPath = f.getAbsolutePath();
                    et.setText(newPath);
                    showFileList(newPath);
                } else {
                    openFile(f.getAbsolutePath());
                }
            });
            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp2px(1)
            ));
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
            fileListContainer.addView(item);
            fileListContainer.addView(divider);
        }
    }

    private void clearFileList() {
        fileListContainer.removeAllViews();
    }

    // ---------- 输入处理 ----------
    private void handleInput(String input) {
        if (input.isEmpty()) {
            Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
            return;
        }

        if (input.startsWith("content://")) {
            openContentUri(input);
            return;
        }

        if (input.startsWith("/") || input.startsWith("file://") || input.contains("/sdcard/")) {
            String cleanPath = input;
            if (cleanPath.startsWith("file://")) cleanPath = cleanPath.substring(7);
            File file = new File(cleanPath);
            if (file.exists() && file.isFile()) {
                openFile(cleanPath);
            } else {
                showFileList(input);
            }
            return;
        }

        if (appMap.containsKey(input)) {
            String pkg = appMap.get(input);
            if (openApp(pkg)) return;
            Toast.makeText(this, "未找到: " + input, Toast.LENGTH_SHORT).show();
            return;
        }

        if (input.contains(".") && !input.contains(" ") &&
                !input.startsWith("http://") && !input.startsWith("https://")) {
            if (openApp(input)) return;
        }

        if (input.startsWith("http://") || input.startsWith("https://") ||
                input.contains(".com") || input.contains(".cn") || input.contains(".org")) {
            String url = input.startsWith("http") ? input : "http://" + input;
            openUrl(url);
            return;
        }

        openUrl("http://" + input);
    }

    // ---------- 打开 content URI ----------
    private void openContentUri(String uriString) {
        try {
            Uri uri = Uri.parse(uriString);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "打开文件"));
        } catch (Exception e) {
            Toast.makeText(this, "无法打开此内容: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- 打开应用 ----------
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

    // ---------- 打开网址 ----------
    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "无法打开网址: " + url, Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- 打开文件 ----------
    private void openFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在: " + path, Toast.LENGTH_SHORT).show();
                return;
            }
            if (file.isDirectory()) {
                return;
            }
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
            Toast.makeText(this, "无法打开文件: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}