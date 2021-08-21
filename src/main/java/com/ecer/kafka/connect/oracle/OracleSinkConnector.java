package com.ecer.kafka.connect.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//kafka之七 sinkTask
//https://blog.csdn.net/lisi1129/article/details/54744189

public class OracleSinkConnector extends SinkConnector{

    private OracleSinkConfig config;

    public void start(Map<String, String> map) {

        config = new OracleSinkConfig(map);    
        String dbName = config.getDbName();    
        if (dbName.equals("")){
          throw new ConnectException("Missing Db Name property");
        }
	}

	public Class<? extends Task> taskClass() {
		return OracleSinkTask.class;
	}

	public List<Map<String, String>> taskConfigs(int maxTasks) {
		ArrayList<Map<String,String>> configs = new ArrayList<>(1);
        configs.add(config.originalsStrings());
        return configs;
	}

	@Override
	public String version() {
        return VersionUtil.getVersion();
	}
    
	@Override
	public void stop() {
		if (OracleSinkTask.getThreadConnection()!=null){
            try {OracleSinkTask.closeDbConn();} catch (Exception e) {} 
          }
	}

    @Override
    public ConfigDef config() {
        return OracleSinkConfig.conf();
    }
}
