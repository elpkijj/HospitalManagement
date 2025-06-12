package com.example.hospitalmanagement.dao;

import com.example.hospitalmanagement.bean.Doctor;
import com.example.hospitalmanagement.utils.DbUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DoctorDao {
    private QueryRunner queryRunner = new QueryRunner();

    public List<Doctor> selectDoctorsByDepartid(int departid) {
        List<Doctor> doctors = null;
        Connection connection = null;

        try {
            connection = DbUtil.getConnection();
            if (connection == null) {
                System.err.println("Database connection is null!");
                return null;
            }

            // Modified query to join with depart table
            String query = "SELECT d.did, d.dname, d.dlevel, d.dinfo, d.departid, dp.departname, d.sex, d.ddetail " +
                    "FROM doctor d " +
                    "LEFT JOIN depart dp ON d.departid = dp.departid " +
                    "WHERE d.departid = ?";
            System.out.println("Executing query: " + query + " with departid = " + departid);

            doctors = queryRunner.query(connection, query, new BeanListHandler<>(Doctor.class), departid);

            if (doctors == null || doctors.isEmpty()) {
                System.out.println("No doctors found for departid = " + departid);
            } else {
                System.out.println("Doctors found: " + doctors);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DbUtil.closeAll(connection, null, null);
        }
        return doctors;
    }
}
