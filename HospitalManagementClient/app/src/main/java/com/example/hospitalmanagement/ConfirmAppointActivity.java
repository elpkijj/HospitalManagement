package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.bean.Doctor;
import com.example.hospitalmanagement.bean.Schedule;
import com.example.hospitalmanagement.utils.NetUtil;
import com.google.gson.Gson;

public class ConfirmAppointActivity extends AppCompatActivity {
    private String baseUrl = Config.baseUrl + "ConfirmAppointServlet";
    private Schedule schedule;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appoint);

        // 获取参数
        String doctorJson = getIntent().getStringExtra("doctor");
        doctor = new Gson().fromJson(doctorJson, Doctor.class);

        String scheduleJson = getIntent().getStringExtra("schedule");
        schedule = new Gson().fromJson(scheduleJson, Schedule.class);

        // 显示预约信息
        TextView uname = findViewById(R.id.uname);
        TextView dname = findViewById(R.id.dname);
        TextView departname = findViewById(R.id.departname);
        TextView time = findViewById(R.id.time);
        TextView price = findViewById(R.id.price);

        // 从 LaunchActivity 获取用户信息
        uname.setText(LaunchActivity.currentUser.getUname());
        dname.setText(doctor.getDname());
        departname.setText(ChooseDoctorActivity.depart.getDepartname());
        time.setText(schedule.getTime());
        price.setText("￥" + schedule.getPrice());

        // 返回按钮逻辑
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            // 返回到医生详情界面
            Intent backIntent = new Intent(ConfirmAppointActivity.this, DoctorActivity.class);
            backIntent.putExtra("doctor", new Gson().toJson(doctor));
            startActivity(backIntent);
            finish(); // 结束当前活动
        });

        // 确认和取消按钮
        Button confirmIt = findViewById(R.id.confirmIt);
        Button cancelIt = findViewById(R.id.cancelIt);

        // 确认预约
        confirmIt.setOnClickListener(view -> {
            String finalUrl = baseUrl + "?sid=" + schedule.getSid() + "&uid=" + LaunchActivity.currentUser.getUid();
            Toast.makeText(getBaseContext(), "正在确认预约...", Toast.LENGTH_SHORT).show();

            new Thread(() -> {
                String result = NetUtil.requestDataByGet(finalUrl);
                runOnUiThread(() -> {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(getBaseContext(), "预约成功！", Toast.LENGTH_SHORT).show();
                        Intent launchIntent = new Intent(ConfirmAppointActivity.this, LaunchActivity.class);
                        launchIntent.putExtra("user", new Gson().toJson(LaunchActivity.currentUser));
                        startActivity(launchIntent);
                        finish(); // 返回主界面
                    } else {
                        Toast.makeText(getBaseContext(), "预约失败，请重试！", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        // 取消预约
        cancelIt.setOnClickListener(view -> {
            Toast.makeText(getBaseContext(), "预约已取消！", Toast.LENGTH_SHORT).show();
            Intent doctorIntent = new Intent(ConfirmAppointActivity.this, DoctorActivity.class);
            doctorIntent.putExtra("doctor", new Gson().toJson(doctor));
            startActivity(doctorIntent);
            finish(); // 返回医生界面
        });
    }
}
