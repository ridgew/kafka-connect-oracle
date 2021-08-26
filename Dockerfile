#FROM primetoninc/jdk:1.8
FROM mes.com/library/jdk:1.8

ARG NAME
ARG VERSION
ARG JAR_FILE

LABEL name=$NAME \
      version=$VERSION

# 设定时区
ENV TZ=Asia/Shanghai
RUN set -eux; \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime; \
    echo $TZ > /etc/timezone
	
# 新建目录
RUN mkdir -p /home/java-app/logs /home/java-app/sync
	
# 复制Kafka
COPY bin /home/java-app/bin

# 复制libs
COPY libs /home/java-app/libs

# 复制配置文件
COPY config /home/java-app/config

# 导入启动脚本
COPY docker-entrypoint.sh /home/java-app/docker-entrypoint.sh

# 导入JAR
COPY target/${JAR_FILE} /home/java-app/libs/app.jar

# 设置工作目录
WORKDIR /home/java-app/bin

#ENTRYPOINT ["/bin/bash"]
# java.ext.dirs指定的目录由ExtClassLoader加载器加载
ENTRYPOINT ["java","-Djava.ext.dirs=../libs/:$JAVA_HOME/jre/lib/ext","com.ecer.kafka.ConnectStandalone","../config/connect-oracle.properties","../config/OracleSourceConnector.properties","../config/OracleConnector-sink.properties"]

EXPOSE 8083