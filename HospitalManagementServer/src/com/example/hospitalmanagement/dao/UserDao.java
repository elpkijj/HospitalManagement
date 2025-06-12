package com.example.hospitalmanagement.dao;

import com.example.hospitalmanagement.bean.User;
import com.example.hospitalmanagement.utils.DbUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class UserDao {
    private QueryRunner queryRunner = new QueryRunner();

    //通过uid查询信息
    public User select(String uid) {
        User user = new User();
        try {
            user = queryRunner.query(DbUtil.getConnection(),"select * from user where uid=?",new BeanHandler<User>(User.class),uid);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
    //增加用户
    public int addUser(User user){
        int res = 0;
        try {
            res = queryRunner.update(DbUtil.getConnection(),"INSERT INTO user values(?,?,?)",user.getUid(),user.getUname(),user.getUpsw());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }


    //验证登录
    public User login(String uid,String upsw){
        User user = select(uid);
        if(user!=null && user.getUpsw().equals(upsw)){   // 该账号存在，能查询到用户 且 密码正确
            return user;
        }
        else return null;
    }

}