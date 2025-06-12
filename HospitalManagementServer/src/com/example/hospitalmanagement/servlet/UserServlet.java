package com.example.hospitalmanagement.servlet;

import com.example.hospitalmanagement.bean.User;
import com.example.hospitalmanagement.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users") // 定义访问路径
public class UserServlet extends HttpServlet {

    private UserDao userDao = new UserDao(); // 创建 UserDao 实例

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 获取请求参数 uid，用于查询特定用户
        String uid = request.getParameter("uid");

        // 参数校验：检查 uid 是否为空
        if (uid == null || uid.trim().isEmpty()) {
            out.println("<html><body>");
            out.println("<h1>Error: User ID is required</h1>");
            out.println("</body></html>");
            return;
        }

        try {
            // 测试 UserDao 的 select 方法
            User user = userDao.select(uid);

            out.println("<html><body>");
            if (user != null && user.getUname() != null) { // 验证用户是否存在
                // 如果查询到用户信息，展示用户数据
                out.println("<h1>User Details</h1>");
                out.println("<p><strong>ID:</strong> " + user.getUid() + "</p>");
                out.println("<p><strong>Username:</strong> " + user.getUname() + "</p>");
                out.println("<p><strong>Password:</strong> " + user.getUpsw() + "</p>");
            } else {
                // 如果查询不到用户，返回提示
                out.println("<h1>No User Found</h1>");
                out.println("<p>User with ID " + uid + " does not exist.</p>");
            }
            out.println("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h1>Error: " + e.getMessage() + "</h1>");
        }
    }
}
