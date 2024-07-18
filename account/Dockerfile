FROM maven:3.6.3-jdk-8 as build

COPY . /src
WORKDIR /src
RUN mvn install


FROM glassfish:4.1-jdk8 as prod

ARG WARNAME=account
ARG MYSQLUSER=csimsoftwww 
ARG MYSQLPASS=csimsoftwwwpass
ARG MYSQLSERVER=127.0.0.1
ARG MYSQLPORT=3306
ARG SERVERPORT=1357
ARG SERVERPORT2=8181
ARG ADMINPORT=4848


RUN curl -L -o /usr/local/glassfish4/glassfish/domains/domain1/lib/mysql-connector-java-8.0.18.jar https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.18/mysql-connector-java-8.0.18.jar

COPY --from=build /src/target/account.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/"$WARNAME".war
COPY ./rlmsign /usr/local/bin/rlmsign

RUN asadmin start-domain domain1 && \
    asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource --maxwait=600000 --leakreclaim true --leaktimeout 302 --statementtimeout 300 --statementleaktimeout 301 --statementleakreclaim true --maxpoolsize 64 --steadypoolsize 16\
    --restype javax.sql.DataSource --property user="$MYSQLUSER":password="$MYSQLPASS":serverName="$MYSQLSERVER":portNumber="$MYSQLPORT":databaseName=csimsoftdb:characterEncoding=UTF-8:useSSL=false:allowPublicKeyRetrieval=true MysqlConnPool &&\
    asadmin create-jdbc-resource --connectionpoolid MysqlConnPool jdbc/csimsoftdb

RUN sed -i "s/port=\"8080\" name=\"http-listener-1\"/port=\"$SERVERPORT\" name=\"http-listener-1\"/g" /usr/local/glassfish4/glassfish/domains/domain1/config/domain.xml
RUN sed -i "s/port=\"8181\" name=\"http-listener-2\"/port=\"$SERVERPORT2\" name=\"http-listener-2\"/g" /usr/local/glassfish4/glassfish/domains/domain1/config/domain.xml
RUN sed -i "s/port=\"4848\" name=\"admin-listener\"/port=\"$ADMINPORT\" name=\"admin-listener\"/g" /usr/local/glassfish4/glassfish/domains/domain1/config/domain.xml
