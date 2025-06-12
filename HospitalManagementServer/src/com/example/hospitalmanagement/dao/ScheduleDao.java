package com.example.hospitalmanagement.dao;

import com.example.hospitalmanagement.bean.Doctor;
import com.example.hospitalmanagement.bean.Schedule;
import com.example.hospitalmanagement.utils.DbUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class ScheduleDao {
    private QueryRunner queryRunner = new QueryRunner();

    public List<Schedule> selectchedulesByDid(int did){
        List<Schedule> schedules = null;
        try {
            schedules = queryRunner.query(DbUtil.getConnection(),"select * from schedule where did=?;",new BeanListHandler<Schedule>(Schedule.class),did);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }


}