package com.example.hospitalmanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.utils.NetUtil;
import com.example.hospitalmanagement.bean.Depart;
import com.example.hospitalmanagement.bean.Doctor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class ChooseDoctorActivity extends AppCompatActivity {
    private String baseurl = Config.baseUrl + "ChooseDoctorServlet";
    public static Depart depart = null;
    private String result;
    private List<Doctor> doctors;
    private ListView doctorListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_doctor);

        int departid = 1;
        if (getIntent().hasExtra("depart")) {
            String departJson = getIntent().getStringExtra("depart");
            depart = new Gson().fromJson(departJson, Depart.class);
            departid = depart.getDepartid();
        } else if (getIntent().hasExtra("departid")) {
            departid = getIntent().getIntExtra("departid", 1);
        }

        baseurl = baseurl + "?departid=" + departid;

        doctorListView = findViewById(R.id.doctor_list_view);
        new Thread(() -> {
            result = NetUtil.requestDataByGet(baseurl);
            runOnUiThread(() -> {
                doctors = new Gson().fromJson(result, new TypeToken<List<Doctor>>() {}.getType());
                doctorListView.setAdapter(new DoctorListAdapter(doctors));
            });
        }).start();

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseDoctorActivity.this, AppointmentActivity.class);
            startActivity(intent);
        });
    }

    public class DoctorListAdapter extends BaseAdapter {
        private final List<Doctor> mDoctors;

        DoctorListAdapter(List<Doctor> doctors) {
            mDoctors = doctors;
        }

        @Override
        public int getCount() {
            return mDoctors.size();
        }

        @Override
        public Object getItem(int i) {
            return mDoctors.get(i);
        }

        @Override
        public long getItemId(int i) {
            return mDoctors.get(i).getDid();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.item_doctor_list_view, null);
            }

            ImageView dImg = view.findViewById(R.id.dimg);
            TextView dName = view.findViewById(R.id.dname);
            TextView dLevel = view.findViewById(R.id.dlevel);
            TextView dInfo = view.findViewById(R.id.dinfo);
            LinearLayout btnDDetail = view.findViewById(R.id.btn_ddetail);

            Doctor doctor = mDoctors.get(i);


            // 设置医生图片
            if ("男".equals(doctor.getSex())) {
                dImg.setImageResource(R.drawable.doctor_male);
            } else {
                dImg.setImageResource(R.drawable.doctor_female);
            }


            // 设置医生信息
            dName.setText(doctor.getDname());
            dLevel.setText(doctor.getDlevel());
            dInfo.setText(doctor.getDinfo());

            // 跳转到医生详情
            btnDDetail.setOnClickListener(view1 -> {
                Intent intent = new Intent(ChooseDoctorActivity.this, DoctorActivity.class);
                intent.putExtra("doctor", new Gson().toJson(doctor));
                startActivity(intent);
            });

            return view;
        }
    }
}
