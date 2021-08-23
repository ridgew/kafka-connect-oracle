package com.ecer.kafka.connect.oracle;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.config.ConfigDef.Importance;

import java.util.Map;


public class OracleSinkConfig extends AbstractConfig {

  public enum InsertMode {
    INSERT,
    UPSERT,
    UPDATE;

  }

  public enum PrimaryKeyMode {
    NONE,
    KAFKA,
    RECORD_KEY,
    RECORD_VALUE;
  }

  public static final String DB_NAME_ALIAS = "db.name.alias";
  public static final String TOPIC_CONFIG = "topic";
  public static final String DB_NAME_CONFIG = "db.name";
  public static final String DB_HOST_NAME_CONFIG = "db.hostname";
  public static final String DB_PORT_CONFIG = "db.port";
  public static final String DB_USER_CONFIG = "db.user";
  public static final String DB_USER_PASSWORD_CONFIG = "db.user.password";

  public static final String BATCH_SIZE = "batch.size";
  private static final int BATCH_SIZE_DEFAULT = 3000;
  private static final String BATCH_SIZE_DOC =
      "Specifies how many records to attempt to batch together for insertion into the destination"
      + " table, when possible.";

  public static final String DELETE_ENABLED = "delete.enabled";
  private static final String DELETE_ENABLED_DOC =
      "Whether to treat ``null`` record values as deletes. Requires ``pk.mode`` "
      + "to be ``record_key``.";

  public static final String INSERT_MODE = "insert.mode";
  private static final String INSERT_MODE_DEFAULT = "insert";
  private static final String INSERT_MODE_DOC =
          "The insertion mode to use. Supported modes are:\n"
          + "``insert``\n"
          + "    Use standard SQL ``INSERT`` statements.\n"
          + "``upsert``\n"
          + "    Use the appropriate upsert semantics for the target database if it is supported by "
          + "the connector, e.g. ``INSERT OR IGNORE``.\n"
          + "``update``\n"
          + "    Use the appropriate update semantics for the target database if it is supported by "
          + "the connector, e.g. ``UPDATE``.";

  public final boolean deleteEnabled;
  public final int batchSize;
  public final InsertMode insertMode;

  public OracleSinkConfig(ConfigDef config, Map<String, String> parsedConfig) {
    super(config, parsedConfig);

    deleteEnabled = getBoolean(DELETE_ENABLED);
    batchSize = getInt(BATCH_SIZE);
    insertMode = InsertMode.valueOf(getString(INSERT_MODE).toUpperCase());

  }

  public OracleSinkConfig(Map<String, String> parsedConfig) {
    this(conf(), parsedConfig);
  }

  public static ConfigDef conf() {
    return new ConfigDef()
        .define(DB_NAME_ALIAS, Type.STRING, Importance.HIGH, "Db Name Alias")
        .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, "Topic")
        .define(DB_NAME_CONFIG, Type.STRING, Importance.HIGH, "Db Name")
        .define(DB_HOST_NAME_CONFIG,Type.STRING,Importance.HIGH,"Db HostName")
        .define(DB_PORT_CONFIG,Type.INT,Importance.HIGH,"Db Port")
        .define(DB_USER_CONFIG,Type.STRING,Importance.HIGH,"Db User")
        .define(DB_USER_PASSWORD_CONFIG,Type.STRING,Importance.HIGH,"Db User Password")
        .define(DELETE_ENABLED, Type.BOOLEAN, Importance.HIGH, DELETE_ENABLED_DOC)
        .define(BATCH_SIZE, Type.INT, Importance.MEDIUM, BATCH_SIZE_DOC)
        .define(INSERT_MODE, Type.STRING,INSERT_MODE_DEFAULT, Importance.MEDIUM, INSERT_MODE_DOC)
        ;
  }

  public String getDbNameAlias(){ return this.getString(DB_NAME_ALIAS);}
  public String getTopic(){ return this.getString(TOPIC_CONFIG);}
  public String getDbName(){ return this.getString(DB_NAME_CONFIG);}
  public String getDbHostName(){return this.getString(DB_HOST_NAME_CONFIG);}
  public int getDbPort(){return this.getInt(DB_PORT_CONFIG);}
  public String getDbUser(){return this.getString(DB_USER_CONFIG);}
  public String getDbUserPassword(){return this.getString(DB_USER_PASSWORD_CONFIG);}

}
