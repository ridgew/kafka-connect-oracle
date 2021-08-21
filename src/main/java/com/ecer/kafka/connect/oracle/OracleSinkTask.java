package com.ecer.kafka.connect.oracle;

import static com.ecer.kafka.connect.oracle.OracleConnectorSchema.SQL_REDO_FIELD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.protocol.types.Struct;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.InternalSinkRecord;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//
//https://github.com/confluentinc/kafka-connect-jdbc/blob/master/src/main/java/io/confluent/connect/jdbc/sink/JdbcSinkTask.java


public class OracleSinkTask  extends SinkTask {

	private static final Logger log = LoggerFactory.getLogger(OracleSinkTask.class);

	// ErrantRecordReporter reporter;
	// DatabaseDialect dialect;
	OracleSinkConfig config;
	// JdbcDbWriter writer;
	// int remainingRetries;

	//private  Connection conn = null;
	public String shcema;
	//private  JdbcDbWriter writer;
	private static Connection dbConn;

	public static Connection getThreadConnection() {
		return dbConn;
	}

	public static void closeDbConn() throws SQLException{
		dbConn.close();
	  }

	@Override
    public void start(Map<String, String> map) {

		log.info("Starting JDBC Sink task");
		config = new OracleSinkConfig(map);

		try {

			dbConn = new OracleConnection().connectSink(config);

		} catch (SQLException e) {
			e.printStackTrace();
		}
        
    }

	@Override
	public void stop() {
		log.info("Stopping task");
		try {            
			if (dbConn!=null){              
			  dbConn.close();
			}
		  } catch (SQLException e) {log.error(e.getMessage());}
	}


	@Override
	public void put(Collection<SinkRecord> records) {
	  if (records.isEmpty()) {
		return;
	  }

	  final SinkRecord first = records.iterator().next();
	  final int recordsCount = records.size();
	  System.out.println(String.format("收到 %s 条记录. kafka协调器:(%s-%s-%s). 写入归档数据库...", recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset() ));
     
	//   log.debug("Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the database...",
	// 	  recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()
	//   );

	  for (SinkRecord record : records) {
		InternalSinkRecord sinkRecord = (InternalSinkRecord)record;
		Struct valueStruct = (Struct)sinkRecord.value();
		String redoSql = valueStruct.get(SQL_REDO_FIELD).toString();
		System.out.println(redoSql);
	  }

	}

	@Override
	public String version() {
		return  VersionUtil.getVersion();
	}
   
}