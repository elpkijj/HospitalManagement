package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hospitalmanagement.bean.User;
import com.google.gson.Gson;

public class LaunchActivity extends AppCompatActivity {
    public static User currentUser; // 静态变量存储当前登录用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch);

        // 获取从 LoginActivity 传递过来的用户信息
        if (getIntent().hasExtra("user")) {
            String userJson = getIntent().getStringExtra("user");
            currentUser = new Gson().fromJson(userJson, User.class);
        }

        // 设置窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.launch), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 点击门诊缴费卡片
        findViewById(R.id.outpatientPaymentCard).setOnClickListener(view -> {
            Intent intent = new Intent(LaunchActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        // 点击预约挂号卡片
        findViewById(R.id.appointmentRegistrationCard).setOnClickListener(view -> {
            Intent intent = new Intent(LaunchActivity.this, AppointmentActivity.class);
            startActivity(intent);
        });

        // 点击报告查询卡片
        findViewById(R.id.reportQueryCard).setOnClickListener(view -> {
            Intent intent = new Intent(LaunchActivity.this, MyInfoActivity.class);
            startActivity(intent);
        });

        // 点击返回按钮
        findViewById(R.id.returnLoginButton).setOnClickListener(view -> {
            Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 关闭当前页面，防止用户通过返回键再次进入该页面
        });
    }
}
