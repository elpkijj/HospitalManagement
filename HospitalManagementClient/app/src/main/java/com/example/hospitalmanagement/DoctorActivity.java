package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.bean.Doctor;
import com.example.hospitalmanagement.bean.Schedule;
import com.example.hospitalmanagement.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DoctorActivity extends AppCompatActivity {

    private Doctor doctor;
    private String baseUrl = Config.baseUrl + "ChooseTimeServlet";
    private List<Schedule> schedules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        // 获取传递过来的医生信息
        String doctorJson = getIntent().getStringExtra("doctor");
        doctor = new Gson().fromJson(doctorJson, Doctor.class);

        Log.e("DoctorActivity", "传来的数据：" + doctor);

        // 绑定控件
        ImageView dimg = findViewById(R.id.dimg);
        TextView dname = findViewById(R.id.dname);
        TextView dlevel = findViewById(R.id.dlevel);
        TextView dinfo = findViewById(R.id.dinfo);
        TextView ddetail = findViewById(R.id.ddetail);
        LinearLayout scheduleContainer = findViewById(R.id.scheduleContainer);

        // 设置医生信息
        if ("男".equals(doctor.getSex())) {
            dimg.setImageResource(R.drawable.doctor_male);
        } else {
            dimg.setImageResource(R.drawable.doctor_female);
        }

        dname.setText(doctor.getDname());
        dlevel.setText(doctor.getDlevel());
        dinfo.setText(doctor.getDinfo());
        ddetail.setText(doctor.getDdetail());

        // 返回按钮逻辑
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(DoctorActivity.this, ChooseDoctorActivity.class);
            intent.putExtra("departid", doctor.getDepartid());
            startActivity(intent);
        });

        // 动态加载排班详情
        new Thread(() -> {
            String result = NetUtil.requestDataByGet(baseUrl + "?did=" + doctor.getDid());
            schedules = new Gson().fromJson(result, new TypeToken<List<Schedule>>() {}.getType());

            runOnUiThread(() -> {
                for (Schedule schedule : schedules) {
                    View itemView = LayoutInflater.from(this).inflate(R.layout.item_time_list_view, scheduleContainer, false);
                    TextView time = itemView.findViewById(R.id.time);
                    TextView price = itemView.findViewById(R.id.price);

                    time.setText(schedule.getTime());
                    price.setText("￥" + schedule.getPrice());

                    // 添加点击事件
                    itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(DoctorActivity.this, ConfirmAppointActivity.class);
                        intent.putExtra("schedule", new Gson().toJson(schedule));
                        intent.putExtra("doctor", new Gson().toJson(doctor));
                        startActivity(intent);
                    });

                    // 添加到容器中
                    scheduleContainer.addView(itemView);
                }
            });
        }).start();
    }
}
