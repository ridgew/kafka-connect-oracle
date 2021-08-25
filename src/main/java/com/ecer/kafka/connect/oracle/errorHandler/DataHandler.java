package com.ecer.kafka.connect.oracle.errorHandler;

import java.sql.Connection;

public interface DataHandler {
    /**
     * 判断是否匹配当前的SQL
     * @param sql
     * @return 匹配与否
     */
    boolean Match(String sql);

    /**
     * 根据数据连接处理当前的SQL
     * @param dbConn
     * @param sql
     */
    void HandlerSql(Connection dbConn, String sql);
}
