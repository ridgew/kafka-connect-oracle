package com.ecer.kafka.connect.oracle.errorHandler;

import java.util.regex.Matcher;
import static java.util.Objects.isNull;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecer.kafka.connect.oracle.OracleSqlUtils;

/**
 * 删除表错误处理
 */
public class DropTableErrorHandler extends RegexDataHandler {

    private static final Logger log = LoggerFactory.getLogger(DropTableErrorHandler.class);

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
        Matcher matcher = getPattern().matcher(sql);
        String table = null;
        while (matcher.find()) {
            table = matcher.group(1);
        }

        String fixSql = "DROP TABLE " + table;
        try {
            if (!isNull(table))
                OracleSqlUtils.executeCallableStmt(dbconn, fixSql);
                log.info("表删除操作已修复:" + table);
        } catch (SQLException e) {
            //e.printStackTrace();
            log.error("修复失败：" + fixSql + "\n" + e.getMessage());
        }
    }

}
