package com.example.hospitalmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private User user;
    private ListView paymentListView;
    private String result;
    private String baseUrl = Config.baseUrl + "PaymentServlet";
    private List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 获取当前登录用户
        user = LaunchActivity.currentUser;
        if (user == null) {
            Toast.makeText(this, "用户信息缺失，请重新登录！", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(PaymentActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // 初始化视图
        ImageView btnBack = findViewById(R.id.btn_back);
        paymentListView = findViewById(R.id.payment_list_view);

        // 返回按钮点击事件
        btnBack.setOnClickListener(view -> {
            Intent backIntent = new Intent(PaymentActivity.this, LaunchActivity.class);
            startActivity(backIntent);
        });

        // 请求用户支付信息
        String url = baseUrl + "?uid=" + user.getUid();
        new Thread(() -> {
            result = NetUtil.requestDataByGet(url);
            runOnUiThread(() -> {
                if (result != null && !result.isEmpty()) {
                    try {
                        orders = new Gson().fromJson(result, new TypeToken<List<Order>>() {}.getType());
                        paymentListView.setAdapter(new PaymentListAdapter(orders));
                    } catch (Exception e) {
                        Toast.makeText(this, "解析支付信息失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "加载支付信息失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    public class PaymentListAdapter extends BaseAdapter {
        private final List<Order> mOrders;

        PaymentListAdapter(List<Order> orders) {
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
                LayoutInflater layoutInflater = LayoutInflater.from(PaymentActivity.this);
                view = layoutInflater.inflate(R.layout.item_payment_list_view, null);
            }

            TextView departname = view.findViewById(R.id.departname);
            TextView price = view.findViewById(R.id.price);
            Button payButton = view.findViewById(R.id.pay_button);

            Order order = mOrders.get(i);
            departname.setText(order.getDepartname());
            price.setText("￥" + order.getPrice());

            // 支付按钮点击事件
            payButton.setOnClickListener(v -> {
                new Thread(() -> {
                    try {
                        URL url = new URL(baseUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        String data = "oid=" + order.getOid();
                        try (OutputStream os = connection.getOutputStream()) {
                            os.write(data.getBytes());
                            os.flush();
                        }

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            runOnUiThread(() -> {
                                Toast.makeText(PaymentActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
                                mOrders.remove(i);
                                notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "支付失败，请稍后重试！", Toast.LENGTH_SHORT).show());
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "支付失败，请稍后重试！", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }
                }).start();
            });

            return view;
        }
    }
}
