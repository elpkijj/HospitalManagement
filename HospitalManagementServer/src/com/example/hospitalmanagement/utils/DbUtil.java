package com.example.hospitalmanagement.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DbUtil {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static String DRIVER;

    // 使用 ThreadLocal 管理每个线程的数据库连接
    private static final ThreadLocal<Connection> THREAD_LOCAL = new ThreadLocal<>();

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DbUtil.class.getResourceAsStream("/database.properties")) {
            if (inputStream == null) {
                throw new RuntimeException("配置文件未找到！");
            }
            properties.load(inputStream);

            // 加载数据库配置
            URL = properties.getProperty("db.url");
            USERNAME = properties.getProperty("db.username");
            PASSWORD = properties.getProperty("db.password");
            DRIVER = properties.getProperty("db.driver");

            // 加载驱动
            Class.forName(DRIVER);
        } catch (IOException e) {
            System.out.println("加载配置文件失败！");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载失败！");
            e.printStackTrace();
        }
    }

    // 获取数据库连接
    public static Connection getConnection() {
        Connection connection = THREAD_LOCAL.get();
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                THREAD_LOCAL.set(connection); // 将连接绑定到当前线程
            }
        } catch (SQLException e) {
            System.out.println("数据库连接失败！");
            e.printStackTrace();
        }
        return connection;
    }

    // 开启事务
    public static void begin() {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false); // 设置手动提交
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 提交事务
    public static void commit() {
        Connection connection = getConnection();
        try {
            connection.commit(); // 提交事务
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(); // 提交后关闭连接
        }
    }

    // 回滚事务
    public static void rollback() {
        Connection connection = getConnection();
        try {
            connection.rollback(); // 回滚事务
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(); // 回滚后关闭连接
        }
    }

    // 关闭当前线程的数据库连接
    private static void closeConnection() {
        Connection connection = THREAD_LOCAL.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                THREAD_LOCAL.remove(); // 从 ThreadLocal 中移除连接
            }
        }
    }

    // 关闭数据库资源
    public static void closeAll(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
