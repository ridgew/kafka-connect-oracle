package com.ecer.kafka.connect.oracle.errorHandler;

import java.util.regex.Matcher;

import static java.util.Objects.isNull;

import com.ecer.kafka.connect.oracle.OracleSqlUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 删除表错误处理 
 */ 
public class DropTableErrorHandler extends RegexDataHandler {

    public DropTableErrorHandler(final String regex, final Object exceptionHandler) {
        super(regex);
    }

    protected DropTableErrorHandler(String hexRegex) {
        super(hexRegex);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean Match(String sql) {
        Matcher matcher = getPattern().matcher(sql);
        return matcher.find();
    }

    @Override
    public void HandlerSql(Connection dbconn, String sql) {
        // TODO Auto-generated method stub
        Matcher matcher = getPattern().matcher(sql);
        String table = null;
        while (matcher.find()) {
            table = matcher.group(1);
        }

        try {
            if (!isNull(table))
                OracleSqlUtils.executeCallableStmt(dbconn, "drop table " + table);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
