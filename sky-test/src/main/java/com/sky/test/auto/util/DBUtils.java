package com.sky.test.auto.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
    private static DataSource dataSource;
    private static ThreadLocal<Connection> threadLocal=new ThreadLocal<>();

    static {
        try {
            Properties info = new Properties();
            InputStream in = DBUtils.class.getClassLoader().getResourceAsStream("db/db.properties");
            info.load(in);
            dataSource = DruidDataSourceFactory.createDataSource(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //对外提供获取连接池中获取连接的方法
    public static Connection getCon() {
        try {
            Connection connection = threadLocal.get();
            if (connection == null) {
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //对外提供关闭数据库连接池的方法，注意这里的关闭资源（归还连接池）不是真正关闭，而是归还给连接池
    public static void close(Connection con, PreparedStatement ps) {
        try {
            Connection connection = threadLocal.get();
            if (connection != null) {
                threadLocal.remove();
                //如果开启了事务的手工提交，操作完毕后，归还连接池前，要把事务的自动提交改为true
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
