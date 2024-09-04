package com.dct.config.datasource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @version 1.0
 * @Author Vic.Zhao
 * @Date 2024/5/31 00:38
 */
@Component
public class ClickHouseConfig {
    private static String clickhouseAddress;

    private static String clickhouseUsername;

    private static String clickhousePassword;

    private static String clickhouseDB;

    private static Integer clickhouseSocketTimeout;

    @Value("${spring.datasource.druid.ck.url}")
    public  void setClickhouseAddress(String address) {
        ClickHouseConfig.clickhouseAddress = address;
    }
    @Value("${spring.datasource.druid.ck.username}")
    public  void setClickhouseUsername(String username) {
        ClickHouseConfig.clickhouseUsername = username;
    }
    @Value("${spring.datasource.druid.ck.password}")
    public  void setClickhousePassword(String password) {
        ClickHouseConfig.clickhousePassword = password;
    }
    @Value("${spring.datasource.druid.ck.db}")
    public  void setClickhouseDB(String db) {
        ClickHouseConfig.clickhouseDB = db;
    }
    @Value("${spring.datasource.druid.ck.socketTimeout}")
    public  void setClickhouseSocketTimeout(Integer socketTimeout) {
        ClickHouseConfig.clickhouseSocketTimeout = socketTimeout;
    }

    public static Connection getConn() {
        ClickHouseConnection conn = null;
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser(clickhouseUsername);
        properties.setPassword(clickhousePassword);
        properties.setDatabase(clickhouseDB);
        properties.setSocketTimeout(clickhouseSocketTimeout);
        ClickHouseDataSource clickHouseDataSource = new ClickHouseDataSource(clickhouseAddress,properties);
        try {
            conn = clickHouseDataSource.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static Connection getConnection() throws SQLException{
        //从数据源中获取数据库连接
        return getConn();
    }
    public static void release(Connection conn, Statement st, ResultSet rs) {
        if (rs != null) {
            try {
                //关闭存储查询结果的ResultSet对象
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            rs = null;
        }
        if (st != null) {
            try {
                //关闭负责执行SQL命令的Statement对象
                st.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                //将Connection连接对象还给数据库连接池
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
