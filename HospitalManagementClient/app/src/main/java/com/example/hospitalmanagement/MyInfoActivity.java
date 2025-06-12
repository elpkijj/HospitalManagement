package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalmanagement.bean.Order;
import com.example.hospitalmanagement.bean.User;
import com.example.hospitalmanagement.utils.NetUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class MyInfoActivity extends AppCompatActivity {
    private User user;
    private ListView myInfoListView;
    private String result;
    private String baseUrl = Config.baseUrl + "MyInfoServlet";
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        // 获取当前登录用户
        user = LaunchActivity.currentUser;
        if (user == null) {
            Toast.makeText(this, "用户信息缺失，请重新登录！", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(MyInfoActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // 初始化视图
        ImageView btnBack = findViewById(R.id.btn_back);
        myInfoListView = findViewById(R.id.myinfo_list_view);

        // 返回按钮点击事件
        btnBack.setOnClickListener(view -> {
            Intent backIntent = new Intent(MyInfoActivity.this, LaunchActivity.class);
            startActivity(backIntent);
        });

        // 请求用户订单信息
        baseUrl = baseUrl + "?uid=" + user.getUid();
        new Thread(() -> {
            result = NetUtil.requestDataByGet(baseUrl);
            runOnUiThread(() -> {
                if (result != null && !result.isEmpty()) {
                    orders = new Gson().fromJson(result, new TypeToken<List<Order>>() {}.getType());
                    myInfoListView.setAdapter(new MyInfoListAdapter(orders));
                } else {
                    Toast.makeText(this, "加载预约信息失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    public class MyInfoListAdapter extends BaseAdapter {
        private List<Order> mOrders;

        MyInfoListAdapter(List<Order> orders) {
            this.mOrders = orders;
        }

        @Override
        public int getCount() {
            return mOrders.size();
        }

        @Override
        public Object getItem(int i) {
            return mOrders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(MyInfoActivity.this);
                view = layoutInflater.inflate(R.layout.item_myinfo_list_view, null);
            }

            TextView time = view.findViewById(R.id.time);
            TextView departname = view.findViewById(R.id.departname);
            TextView dname = view.findViewById(R.id.dname);

            Order order = mOrders.get(i);
            time.setText(order.getTime());
            departname.setText(order.getDepartname());
            dname.setText(order.getDname());

            return view;
        }
    }
}
