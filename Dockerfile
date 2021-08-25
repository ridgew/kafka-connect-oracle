FROM primetoninc/jdk:1.8
ENV kafka.logs.dir /home/java-app/logs 

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
RUN mkdir -p /home/java-app/logs 
	
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
ENTRYPOINT ["java","-Djava.ext.dirs=../libs/","com.ecer.kafka.ConnectStandalone","../config/connect-standalone.properties","../config/OracleSourceConnector.properties","../config/OracleConnector-sink.properties"]

EXPOSE 8083