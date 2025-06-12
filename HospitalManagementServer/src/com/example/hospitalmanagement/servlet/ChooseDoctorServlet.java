package com.example.hospitalmanagement.servlet;

import com.example.hospitalmanagement.bean.Doctor;
import com.example.hospitalmanagement.dao.DoctorDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ChooseDoctorServlet", value = "/ChooseDoctorServlet")
public class ChooseDoctorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Received request to fetch doctors by departid.");
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String departidParam = request.getParameter("departid");
        if (departidParam == null || departidParam.isEmpty()) {
            System.err.println("No departid provided in request.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid or missing departid parameter\"}");
            return;
        }

        int departid = 0;
        try {
            departid = Integer.parseInt(departidParam);
        } catch (NumberFormatException e) {
            System.err.println("Invalid departid format: " + departidParam);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid departid format\"}");
            return;
        }

        System.out.println("Fetching doctors for departid: " + departid);
        DoctorDao doctorDao = new DoctorDao();
        List<Doctor> doctors = doctorDao.selectDoctorsByDepartid(departid);

        if (doctors == null || doctors.isEmpty()) {
            System.out.println("No doctors found for departid: " + departid);
        } else {
            System.out.println("Doctors fetched: " + doctors);
        }

        PrintWriter writer = response.getWriter();
        String result = new Gson().toJson(doctors, new TypeToken<List<Doctor>>() {}.getType());
        response.setStatus(HttpServletResponse.SC_OK);
        writer.write(result);
        writer.flush();
        writer.close();

        System.out.println("Response sent: " + result);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
