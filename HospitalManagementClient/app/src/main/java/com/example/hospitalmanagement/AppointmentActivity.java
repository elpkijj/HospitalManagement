package com.example.hospitalmanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.utils.NetUtil;
import com.example.hospitalmanagement.bean.Depart;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppointmentActivity extends AppCompatActivity {

    private String baseUrl = Config.baseUrl + "ChooseDepartServlet"; // 替换为你的实际 Servlet 路径
    private List<Depart> departs;
    private ListView departListView;
    private ExecutorService executorService; // 线程池
    private Handler handler; // 主线程 Handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // 初始化 UI
        departListView = findViewById(R.id.depart_list_view);

        // 初始化线程池和主线程 Handler
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // 加载科室数据
        loadDepartData();

        // 设置返回按钮点击事件
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(AppointmentActivity.this, LaunchActivity.class);
            startActivity(intent);
            finish(); // 关闭当前页面
        });
    }

    /**
     * 加载科室数据
     */
    private void loadDepartData() {
        executorService.execute(() -> {
            // 异步线程执行网络请求
            String result = NetUtil.requestDataByGet(baseUrl);
            Gson gson = new Gson();
            departs = gson.fromJson(result, new TypeToken<List<Depart>>() {}.getType());

            // 主线程更新 UI
            handler.post(() -> departListView.setAdapter(new DepartListAdapter(departs)));
        });
    }

    /**
     * 自定义适配器，用于显示科室列表
     */
    public class DepartListAdapter extends BaseAdapter {
        private List<Depart> mDeparts;

        DepartListAdapter(List<Depart> departs) {
            mDeparts = departs;
        }

        @Override
        public int getCount() {
            return mDeparts.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeparts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mDeparts.get(position).getDepartid();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_depart_list_view, null);
            }

            TextView departName = convertView.findViewById(R.id.depart_name_tv);
            departName.setText(mDeparts.get(position).getDepartname());

            // 设置点击事件跳转到医生选择界面
            departName.setOnClickListener(view -> {
                Intent intent = new Intent(AppointmentActivity.this, ChooseDoctorActivity.class);
                intent.putExtra("depart", new Gson().toJson(mDeparts.get(position)));
                startActivity(intent);
            });

            return convertView;
        }
    }
}
