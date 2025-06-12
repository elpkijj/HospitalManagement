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

public class LoginActivity extends AppCompatActivity {
    private EditText userIDField; // 输入账号的 EditText
    private EditText passwordField; // 输入密码的 EditText
    private Button loginButton; // 登录按钮
    private Button registerButton; // 注册按钮
    private String baseUrl = Config.baseUrl + "LoginServlet"; // 登录接口地址
    private User user = null; // 用户对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化控件
        initView();

        // 登录按钮点击事件
        loginButton.setOnClickListener(v -> {
            String uid = userIDField.getText().toString();
            String upsw = passwordField.getText().toString();

            // 校验输入是否为空
            if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(upsw)) {
                Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            // 发送登录请求
            new Thread(() -> {
                String result = requestDataByPost(baseUrl, uid, upsw);
                if (user != null) { // 登录成功
                    runOnUiThread(() -> {
                        Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, LaunchActivity.class);
                        intent.putExtra("user", new Gson().toJson(user));
                        startActivity(intent);
                        finish(); // 关闭当前页面
                    });
                } else { // 登录失败
                    runOnUiThread(() -> Toast.makeText(this, "账号或密码错误！", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        // 注册按钮点击事件
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        userIDField = findViewById(R.id.userIDField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
    }

    /**
     * 发送登录请求
     *
     * @param urlString 请求地址
     * @param uid       用户名
     * @param upsw      密码
     * @return 服务器返回的结果
     */
    private String requestDataByPost(String urlString, String uid, String upsw) {
        String result = null;
        try {
            Log.i("LoginActivity", "Request URL: " + urlString); // 打印请求 URL
            Log.i("LoginActivity", "Request Data: uid=" + uid + "&upsw=" + upsw); // 打印请求数据

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(30000); // 设置超时时间
            connection.setRequestMethod("POST"); // 请求方法 POST

            // 设置输入输出
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false); // 禁用缓存

            // 连接服务器
            connection.connect();

            // 构造请求数据
            String data = "uid=" + URLEncoder.encode(uid, "UTF-8") +
                    "&upsw=" + URLEncoder.encode(upsw, "UTF-8");

            // 发送数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();

            // 获取响应
            int responseCode = connection.getResponseCode();
            Log.i("LoginActivity", "Response Code: " + responseCode); // 打印响应码

            if (responseCode == HttpURLConnection.HTTP_OK) {
                result = NetUtil.streamToString(connection.getInputStream());
                Log.i("LoginActivity", "Response: " + result); // 打印响应结果
                user = new Gson().fromJson(result, User.class); // 解析 JSON 数据为 User 对象
            } else {
                Log.e("LoginActivity", "Server returned non-OK response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            Log.e("LoginActivity", "Error in requestDataByPost: ", e);
        }
        return result;
    }

}
