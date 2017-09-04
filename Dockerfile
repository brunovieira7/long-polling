# base Tomcat image to build Docker image from
FROM tomcat:8.5.14-jre8

MAINTAINER Jane Doe <janedoe@mail.com>

# Tomcat directory to copy Java agent files to
#ENV NEWRELIC_HOME /usr/local/tomcat/newrelic

RUN rm -rf /usr/local/tomcat/webapps/ROOT

# copy application war from target to Tomcat webapps
COPY target/*.war /usr/local/tomcat/webapps

# copy Java agent files from target to Tomcat NEWRELIC_HOME
#COPY target/newrelic ${NEWRELIC_HOME}

# update Java agent yml with license_key and app_name and copy to image  
#COPY src/main/newrelic/newrelic.yml ${NEWRELIC_HOME}