package com.example.hospitalmanagement.dao;

import com.example.hospitalmanagement.bean.Order;
import com.example.hospitalmanagement.utils.DbUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderDao {
    private QueryRunner queryRunner = new QueryRunner();

    /**
     * 添加订单到数据库
     *
     * @param sid 排班 ID
     * @param uid 用户 ID
     * @return 影响的行数（成功返回 1，失败返回 0）
     */
    public int addOrder(int sid, int uid) {
        String oid = sid + "#" + uid; // 拼接订单 ID
        int res = 0;

        System.out.println("添加的订单 ID：" + oid);

        try (Connection connection = DbUtil.getConnection()) {
            if (connection == null) {
                System.err.println("数据库连接为空！");
                return 0;
            }
            res = queryRunner.update(connection, "INSERT INTO myOrder (oid, sid, uid, pay) VALUES (?, ?, ?, 0)", oid, sid, uid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }

    /**
     * 查询指定用户的所有订单
     *
     * @param uid 用户 ID
     * @return 订单列表
     */
    public List<Order> getOrdersByUserId(int uid) {
        List<Order> orders = null;

        try (Connection connection = DbUtil.getConnection()) {
            if (connection == null) {
                System.err.println("数据库连接为空！");
                return null;
            }
            String query = "SELECT o.oid, o.sid, o.uid, s.time, d.departname, doc.dname, s.price, o.pay " +
                    "FROM myOrder o " +
                    "JOIN schedule s ON o.sid = s.sid " +
                    "JOIN doctor doc ON s.did = doc.did " +
                    "JOIN depart d ON doc.departid = d.departid " +
                    "WHERE o.uid = ?";
            System.out.println("执行查询：" + query + "，用户 ID = " + uid);

            orders = queryRunner.query(connection, query, new BeanListHandler<>(Order.class), uid);

            if (orders == null || orders.isEmpty()) {
                System.out.println("未找到用户 ID = " + uid + " 的订单");
            } else {
                System.out.println("找到订单：" + orders);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * 查询指定用户的未支付订单
     *
     * @param uid 用户 ID
     * @return 未支付订单列表
     */
    public List<Order> getUnpaidOrdersByUserId(int uid) {
        List<Order> orders = null;

        try (Connection connection = DbUtil.getConnection()) {
            if (connection == null) {
                System.err.println("数据库连接为空！");
                return null;
            }
            String query = "SELECT o.oid, o.sid, o.uid, s.time, d.departname, doc.dname, s.price, o.pay " +
                    "FROM myOrder o " +
                    "JOIN schedule s ON o.sid = s.sid " +
                    "JOIN doctor doc ON s.did = doc.did " +
                    "JOIN depart d ON doc.departid = d.departid " +
                    "WHERE o.uid = ? AND o.pay = 0";
            System.out.println("执行查询：" + query + "，用户 ID = " + uid);

            orders = queryRunner.query(connection, query, new BeanListHandler<>(Order.class), uid);

            if (orders == null || orders.isEmpty()) {
                System.out.println("未找到用户 ID = " + uid + " 的未支付订单");
            } else {
                System.out.println("找到未支付订单：" + orders);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * 更新指定订单为已支付状态
     *
     * @param oid 订单 ID
     * @return 影响的行数（成功返回 1，失败返回 0）
     */
    public int updateOrderToPaid(String oid) {
        int res = 0;

        try (Connection connection = DbUtil.getConnection()) {
            if (connection == null) {
                System.err.println("数据库连接为空！");
                return 0;
            }
            res = queryRunner.update(connection, "UPDATE myOrder SET pay = 1 WHERE oid = ?", oid);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return res;
    }
}
