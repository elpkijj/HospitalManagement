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

@WebServlet(name = "MyInfoServlet", value = "/MyInfoServlet")
public class MyInfoServlet extends HttpServlet {
    private OrderDao orderDao = new OrderDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = response.getWriter();

        try {
            String uidParam = request.getParameter("uid");
            if (uidParam == null || uidParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writer.write("{\"error\": \"Missing or invalid user ID.\"}");
                return;
            }

            int uid = Integer.parseInt(uidParam);

            // 查询用户的订单
            List<Order> orders = orderDao.getOrdersByUserId(uid);
            if (orders == null || orders.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write("[]"); // 返回空列表
            } else {
                String jsonResult = new Gson().toJson(orders);
                response.setStatus(HttpServletResponse.SC_OK);
                writer.write(jsonResult);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writer.write("{\"error\": \"Invalid user ID format.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("{\"error\": \"Server error. Please try again later.\"}");
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
