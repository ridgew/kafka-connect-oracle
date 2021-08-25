FROM openjdk:8-alpine

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

# 新建用户java-app
RUN set -eux; \
    addgroup --gid 1000 java-app; \
    adduser -S -u 1000 -g java-app -h /home/java-app/ -s /bin/sh -D java-app; \
    mkdir -p /home/java-app/libs /home/java-app/etc /home/java-app/jmx-ssl /home/java-app/logs /home/java-app/tmp /home/java-app/jmx-exporter/libs /home/java-app/jmx-exporter/etc; \
    chown -R java-app:java-app /home/java-app

# 复制Kafka
COPY --chown=java-app:java-app bin /home/java-app/bin

# 复制libs
COPY --chown=java-app:java-app libs /home/java-app/libs

# 复制配置文件
COPY --chown=java-app:java-app config /home/java-app/config

# 导入启动脚本
COPY --chown=java-app:java-app docker-entrypoint.sh /home/java-app/docker-entrypoint.sh

# 导入JAR
COPY --chown=java-app:java-app target/${JAR_FILE} /home/java-app/libs/app.jar

USER java-app


#ENTRYPOINT ["/bin/sh"]
ENTRYPOINT ["java","-jar","/home/java-app/libs/app.jar"]
#ENTRYPOINT ["/home/java-app/docker-entrypoint.sh"]

EXPOSE 8083