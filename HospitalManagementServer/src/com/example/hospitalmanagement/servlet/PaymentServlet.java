package com.example.hospitalmanagement.servlet;

import com.example.hospitalmanagement.bean.Order;
import com.example.hospitalmanagement.dao.OrderDao;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "PaymentServlet", value = "/PaymentServlet")
public class PaymentServlet extends HttpServlet {

    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求和响应的编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();

        try {
            // 获取请求参数中的用户 ID
            String uidParam = request.getParameter("uid");
            if (uidParam == null || uidParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("{\"error\": \"Missing or invalid user ID.\"}");
                return;
            }

            int uid = Integer.parseInt(uidParam);

            // 查询未支付订单
            List<Order> unpaidOrders = orderDao.getUnpaidOrdersByUserId(uid);
            if (unpaidOrders == null || unpaidOrders.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write("[]"); // 返回空列表
            } else {
                String jsonResult = new Gson().toJson(unpaidOrders);
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write(jsonResult);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"error\": \"Invalid user ID format.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"error\": \"Server error occurred. Please try again later.\"}");
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求和响应的编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();

        try {
            // 获取请求参数中的订单 ID
            String oid = request.getParameter("oid");
            if (oid == null || oid.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("{\"error\": \"Missing or invalid order ID.\"}");
                return;
            }

            // 调用 OrderDao 更新支付状态
            int result = orderDao.updateOrderToPaid(oid);
            if (result > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write("{\"message\": \"Payment successful.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("{\"error\": \"Failed to update payment status. Order not found or already paid.\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"error\": \"Server error occurred. Please try again later.\"}");
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
