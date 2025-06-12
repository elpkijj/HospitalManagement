package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.utils.NetUtil;
import com.example.hospitalmanagement.bean.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameField; // 用户名输入框
    private EditText userIDField;   // 用户 ID 输入框
    private EditText passwordField; // 密码输入框
    private EditText passwordConfirm; // 确认密码输入框
    private Button submitButton;   // 提交按钮
    private Button returnLoginButton; // 返回按钮

    private String uname;
    private String uid;
    private String upsw;
    private String reupsw;
    private String TAG = "RegisterActivity";

    private User user = null; // 用于存储返回的用户对象
    private String baseUrl = Config.baseUrl + "RegisterServlet"; // 注册接口地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView(); // 初始化控件
        initEvent(); // 初始化事件
    }

    /**
     * 初始化控件
     */
    private void initView() {
        usernameField = findViewById(R.id.usernameField);
        userIDField = findViewById(R.id.userIDField);
        passwordField = findViewById(R.id.passwordField);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        submitButton = findViewById(R.id.SubmitButton);
        returnLoginButton = findViewById(R.id.returnLoginButton);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        // 提交按钮点击事件
        submitButton.setOnClickListener(view -> {
            uname = usernameField.getText().toString();
            uid = userIDField.getText().toString();
            upsw = passwordField.getText().toString();
            reupsw = passwordConfirm.getText().toString();

            // 校验输入内容
            if (TextUtils.isEmpty(uname) || TextUtils.isEmpty(uid) || TextUtils.isEmpty(upsw) || TextUtils.isEmpty(reupsw)) {
                Toast.makeText(getBaseContext(), "所填信息不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!upsw.equals(reupsw)) {
                Toast.makeText(getBaseContext(), "两次输入的密码不一致！", Toast.LENGTH_SHORT).show();
                return;
            }

            // 发送注册请求
            new Thread(() -> {
                String result = requestDataByPost(baseUrl);

                if (user != null) {
                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                        toLoginActivity(); // 跳转到登录页面
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), "账号已存在！", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        });

        // 返回按钮点击事件
        returnLoginButton.setOnClickListener(view -> toLoginActivity());
    }

    /**
     * 跳转到登录页面
     */
    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // 关闭当前页面
    }

    /**
     * 发送注册请求
     *
     * @param urlString 请求地址
     * @return 服务器返回的结果
     */
    private String requestDataByPost(String urlString) {
        String result = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000); // 设置超时时间
            connection.setRequestMethod("POST"); // 设置请求方法 POST

            // 设置输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false); // 禁用缓存
            connection.connect();

            // 构造请求数据
            String data = "uname=" + URLEncoder.encode(uname, "UTF-8") +
                    "&uid=" + URLEncoder.encode(uid, "UTF-8") +
                    "&upsw=" + URLEncoder.encode(upsw, "UTF-8");

            // 发送数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = NetUtil.streamToString(connection.getInputStream());
                user = new Gson().fromJson(result, User.class); // 解析 JSON 数据为 User 对象
            } else {
                Log.e(TAG, "Response Code: " + responseCode);
            }
            connection.disconnect();
            return result;
        } catch (IOException e) {
            Log.e(TAG, "Error in requestDataByPost: ", e);
        }
        return null;
    }
}
