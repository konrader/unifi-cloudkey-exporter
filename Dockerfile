FROM openjdk:12-alpine
MAINTAINER Konrad Eriksson <konrad@konraderiksson.com>

ADD target/unifi-cloudkey-exporter-*.jar /unifi-cloudkey-exporter.jar

ENTRYPOINT ["java", "-jar", "/unifi-cloudkey-exporter.jar"]
