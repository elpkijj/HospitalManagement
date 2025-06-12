package com.example.hospitalmanagement.servlet;

import com.example.hospitalmanagement.dao.OrderDao;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ConfirmAppointServlet", value = "/ConfirmAppointServlet")
public class ConfirmAppointServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("接收到预约确认请求");

        // 设置请求和响应的字符编码
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取请求参数
        String uidParam = request.getParameter("uid");
        String sidParam = request.getParameter("sid");

        // 校验参数
        if (uidParam == null || sidParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("缺少必要参数");
            return;
        }

        int uid;
        int sid;

        try {
            uid = Integer.parseInt(uidParam);
            sid = Integer.parseInt(sidParam);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("参数格式错误");
            return;
        }

        // 调用 DAO 层操作数据库
        OrderDao orderDao = new OrderDao();
        int result = orderDao.addOrder(sid, uid); // 将订单写入数据库

        // 响应结果
        PrintWriter writer = response.getWriter();

        if (result > 0) {
            response.setStatus(HttpServletResponse.SC_OK);
            writer.write("预约成功");
            System.out.println("预约成功: 用户ID=" + uid + ", 排班ID=" + sid);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writer.write("预约失败");
            System.out.println("预约失败: 用户ID=" + uid + ", 排班ID=" + sid);
        }

        writer.flush();
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
