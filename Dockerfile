# syntax=docker/dockerfile:1.4

FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
# KB nexus credentials/mirrors come from a BuildKit secret so settings.xml never lands in a layer.
# oracle:sdoapi is in no reachable repository; supply it with
#   --build-context oracle-jars=$HOME/.m2/repository/oracle/sdoapi/11.2.0
RUN --mount=type=secret,id=maven_settings,target=/root/.m2/settings.xml,required=true \
    --mount=type=cache,target=/root/.m2/repository \
    --mount=type=bind,from=oracle-jars,target=/oracle-jars \
    mvn -B -q install:install-file -Dfile=/oracle-jars/sdoapi-11.2.0.jar \
        -DgroupId=oracle -DartifactId=sdoapi -Dversion=11.2.0 -Dpackaging=jar \
    && mvn -B -DskipTests package \
    && cp target/cop3-backend.war /build/cop3-backend.war

# Tomcat 9: the app uses javax.servlet, which Tomcat 10+ does not provide.
FROM tomcat:9.0-jre11-temurin
RUN apt-get update && apt-get install -y --no-install-recommends gettext-base && rm -rf /var/lib/apt/lists/*
COPY --from=build /build/cop3-backend.war /opt/cop/cop3-backend.war
COPY docker/logback.xml /opt/cop/logback.xml
COPY docker/conf/ /opt/cop/conf/
ENV CONF_TEMPLATE_DIR=/opt/cop/conf
COPY docker/cop.xml /usr/local/tomcat/conf/Catalina/localhost/cop.xml
COPY --chmod=755 docker/entrypoint.sh /usr/local/bin/entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["entrypoint.sh"]
CMD ["catalina.sh", "run"]
