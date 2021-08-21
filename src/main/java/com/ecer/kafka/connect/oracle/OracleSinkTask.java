package com.ecer.kafka.connect.oracle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
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

		// initWriter();
		// remainingRetries = config.maxRetries;
		// try {
		//    reporter = context.errantRecordReporter();
		// } catch (NoSuchMethodError | NoClassDefFoundError e) {
		// // Will occur in Connect runtimes earlier than 2.6
		//    reporter = null;
		// }

		//  try {
		// 		DbPool.init(pro);
		// 		writer =new JdbcDbWriter();
		// 	} catch (PropertyVetoException e1) {
		// 		e1.printStackTrace();
		// 		LOG.info("数据库配置异常=====");
		// 	}		
        
    }

	// void initWriter() {
	// 	if (config.dialectName != null && !config.dialectName.trim().isEmpty()) {
	// 	  dialect = DatabaseDialects.create(config.dialectName, config);
	// 	} else {
	// 	  dialect = DatabaseDialects.findBestFor(config.connectionUrl, config);
	// 	}
	// 	final DbStructure dbStructure = new DbStructure(dialect);
	// 	log.info("Initializing writer using SQL dialect: {}", dialect.getClass().getSimpleName());
	// 	writer = new JdbcDbWriter(config, dialect, dbStructure);
	//   }

	@Override
	public void stop() {

		log.info("Stopping task");

		// try {
		//   writer.closeQuietly();
		// } 
		// finally {
		// 	try {
		// 		if (dialect != null) {
		// 		  dialect.close();
		// 		}
		// 	} catch (Throwable t) {
		// 		log.warn("Error while closing the {} dialect: ", dialect.name(), t);
		// 	} finally {
		// 		dialect = null;
		// 	}
		// }
		
	}


	@Override
	public void put(Collection<SinkRecord> records) {
	  if (records.isEmpty()) {
		return;
	  }
	  final SinkRecord first = records.iterator().next();
	  final int recordsCount = records.size();
	  log.debug(
		  "Received {} records. First record kafka coordinates:({}-{}-{}). Writing them to the "
		  + "database...",
		  recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset()
	  );

	  System.out.println(String.format("Received %s records. First record kafka coordinates:(%s-%s-%s). Writing them to the database...", recordsCount, first.topic(), first.kafkaPartition(), first.kafkaOffset() ));
      

	//   try {
	// 	writer.write(records);
	//   } catch (TableAlterOrCreateException tace) {
	// 	if (reporter != null) {
	// 	  unrollAndRetry(records);
	// 	} else {
	// 	  throw tace;
	// 	}
	//   } catch (SQLException sqle) {
	// 	log.warn(
	// 		"Write of {} records failed, remainingRetries={}",
	// 		records.size(),
	// 		remainingRetries,
	// 		sqle
	// 	);

	// 	int totalExceptions = 0;
	// 	for (Throwable e :sqle) {
	// 	  totalExceptions++;
	// 	}

	// 	SQLException sqlAllMessagesException = getAllMessagesException(sqle);
	// 	if (remainingRetries > 0) {
	// 	  writer.closeQuietly();
	// 	  initWriter();
	// 	  remainingRetries--;
	// 	  context.timeout(config.retryBackoffMs);
	// 	  throw new RetriableException(sqlAllMessagesException);
	// 	} else {
	// 	  if (reporter != null) {
	// 		unrollAndRetry(records);
	// 	  } else {
	// 		log.error(
	// 			"Failing task after exhausting retries; "
	// 				+ "encountered {} exceptions on last write attempt. "
	// 				+ "For complete details on each exception, please enable DEBUG logging.",
	// 			totalExceptions);
	// 		int exceptionCount = 1;
	// 		for (Throwable e : sqle) {
	// 		  log.debug("Exception {}:", exceptionCount++, e);
	// 		}
	// 		throw new ConnectException(sqlAllMessagesException);
	// 	  }
	// 	}
	//   }
	//   remainingRetries = config.maxRetries;

	}
  
	private void unrollAndRetry(Collection<SinkRecord> records) {
	//   writer.closeQuietly();
	//   for (SinkRecord record : records) {
	// 	try {
	// 	  writer.write(Collections.singletonList(record));
	// 	} catch (TableAlterOrCreateException tace) {
	// 	  reporter.report(record, tace);
	// 	  writer.closeQuietly();
	// 	} catch (SQLException sqle) {
	// 	  SQLException sqlAllMessagesException = getAllMessagesException(sqle);
	// 	  reporter.report(record, sqlAllMessagesException);
	// 	  writer.closeQuietly();
	// 	}
	//   }
	}

	private SQLException getAllMessagesException(SQLException sqle) {
		String sqleAllMessages = "Exception chain:" + System.lineSeparator();
		for (Throwable e : sqle) {
		  sqleAllMessages += e + System.lineSeparator();
		}
		SQLException sqlAllMessagesException = new SQLException(sqleAllMessages);
		sqlAllMessagesException.setNextException(sqle);
		return sqlAllMessagesException;
	  }

	  // @Override
	// public void flush(Map<TopicPartition, OffsetAndMetadata> map) {
	// 	LOG.info("================flush Map start................===========================================================");
	// }

	public void putMysql(Collection<SinkRecord> sinkRecords) {
		if(sinkRecords.isEmpty()){
			return;
		}

		// try {
				
        //     //writer.write(sinkRecords,shcema,email);
		// } 
        // catch (SQLException | IOException e) {
		// 	try {
		// 		EmailUtil.init(Constant_Global.STMP, Constant_Global.EMAILUSER, Constant_Global.EMAILPASSWD, Constant_Global.EMAILTITAL, Constant_Global.EMAILADREE, email);
		// 		EmailUtil.send(" kafka sink 数据写入有问题 ");
		// 	} catch (MessagingException e1) {
		// 		e1.printStackTrace();
		// 	}
		//     throw new JDBCConntorException("数据写入有问题");
		// }
        		
	}

	@Override
	public String version() {
		return  new OracleSinkConnector().version();
	}
   
}