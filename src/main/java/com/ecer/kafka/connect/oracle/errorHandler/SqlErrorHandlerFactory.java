package com.ecer.kafka.connect.oracle.errorHandler;

public final class SqlErrorHandlerFactory {

    public static DataHandler FindHandlerBySql(String sql) {

        //Drop表错误
        DropTableErrorHandler handle = new DropTableErrorHandler("drop table ([\\w]+)([\\w\\W]+)AS\\s\"([^\"]+)\"",
                null);
        if (handle.Match(sql)) {
            return handle;
        }

        return null;
    }
}
