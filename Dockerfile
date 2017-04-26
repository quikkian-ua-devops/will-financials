FROM tomcat:8.5.13-jre8-alpine

RUN apk add --no-cache curl

RUN curl -k -o $CATALINA_HOME/lib/mysql-connector-java-5.1.36.jar http://nexus.kuali.org/service/local/repositories/central/content/mysql/mysql-connector-java/5.1.36/mysql-connector-java-5.1.36.jar

COPY kfs-web/target/kfs-web.war $CATALINA_HOME/webapps/kfs-dev.war

COPY kfs-core/src/main/config/demo/rice.keystore $CATALINA_HOME/conf/

EXPOSE 8080
