package com.ecer.kafka.connect.oracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecer.kafka.connect.oracle.errorHandler.*;
import static com.ecer.kafka.connect.oracle.OracleConnectorSchema.SQL_REDO_FIELD;
import static com.ecer.kafka.connect.oracle.OracleConnectorSchema.SCN_FIELD;

//
//https://github.com/confluentinc/kafka-connect-jdbc/blob/master/src/main/java/io/confluent/connect/jdbc/sink/JdbcSinkTask.java

public class OracleSinkTask extends SinkTask {

	private static final Logger log = LoggerFactory.getLogger(OracleSinkTask.class);

	OracleSinkConfig config;
	private static Connection dbConn;

	public static Connection getThreadConnection() {
		return dbConn;
	}

	public static void closeDbConn() throws SQLException {
		dbConn.close();
	}

	@Override
	public void start(Map<String, String> map) {
		log.info("Starting JDBC Sink task");
		config = new OracleSinkConfig(map);
		try {
			dbConn = new OracleConnection().connectSink(config);
			dbConn.setAutoCommit(false); // 不启用自动提交事务
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		log.info("Stopping task");
		try {
			if (dbConn != null) {
				dbConn.close();
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void put(Collection<SinkRecord> records) {
		if (records.isEmpty()) {
			return;
		}

		if (log.isInfoEnabled()) {
			final SinkRecord first = records.iterator().next();
			final int recordsCount = records.size();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			log.info(String.format("[%s]收到 %s 条记录. kafka协调器:(%s-%s-%s). 写入归档数据库...", df.format(new Date()),
					recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()));
		}

		try {

			for (SinkRecord record : records) {

				String topic = record.topic();
				final Struct valueStruct = (Struct) record.value();
				// final boolean isDelete = isNull(valueStruct);
				Schema valueSchema = record.valueSchema();
				final Field field = valueSchema.field(SQL_REDO_FIELD);
				// Schema fieldSchema = field.schema();
				String sql = valueStruct.get(field).toString();

				if (log.isInfoEnabled()) {
					log.info(String.format("(%s-%s-%s)...", topic, record.kafkaPartition(), record.kafkaOffset()));
					log.info(sql);
				}

				try {
					OracleSqlUtils.executeCallableStmt(dbConn, sql);
				} catch (SQLException e) {
					// java.sql.SQLSyntaxErrorException: ORA-00933: SQL 命令未正确结束
					// java.sql.SQLSyntaxErrorException: ORA-00955: 名称已由现有对象使用
					log.error("原始执行错误：" + e.toString());
					log.error("参考信息：------------------------------");
					log.error(String.format("KafKa: (%s-%s-%s) @SCN:%s", topic, record.kafkaPartition(),
							record.kafkaOffset(), valueStruct.get(valueSchema.field(SCN_FIELD))));
					log.error(sql);
					log.error("------------------------------ 参考信息");

					DataHandler handler = SqlErrorHandlerFactory.FindHandlerBySql(sql);
					if (!isNull(handler)) {

						try {
							handler.HandlerSql(dbConn, sql);
						} catch (Exception eFix) {
							log.error("执行修复操作异常：" + eFix.getMessage());
						}

					} else {

						try {
							dbConn.rollback();
						} catch (SQLException sqle) {
							e.addSuppressed(sqle);
						} finally {
							// throw e;
							e.printStackTrace();
						}
					}

				}
			}

			if (dbConn != null) {
				dbConn.commit();
			}
		} catch (Exception syse) {
			log.error("put异常：" + syse.toString());
			syse.printStackTrace();
		}

	}

	private boolean isFixableSql(String sql) {
		return false;
	}

	@Override
	public String version() {
		return VersionUtil.getVersion();
	}

}