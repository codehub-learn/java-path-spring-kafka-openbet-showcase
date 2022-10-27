#FROM bellsoft/liberica-runtime-container:jre
#FROM openjdk:11.0.10-jre-buster
#FROM bellsoft/liberica-openjdk-alpine:17
FROM bellsoft/liberica-openjre-debian:17

#RUN yum update && yum install -y curl # not needed in case of bash shell
#RUN apk update && apk add curl # alpine, alpaquita
#RUN apt-get update && apt-get install -y curl # debian

ENV KAFKA_VERSION 3.3.1
ENV SCALA_VERSION 2.13

RUN mkdir /tmp/kafka && \
    curl "https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" \
    -o /tmp/kafka/kafka.tgz && \
    mkdir /kafka && cd /kafka && \
    tar -xvzf /tmp/kafka/kafka.tgz --strip 1

RUN mkdir -p /data/kafka

COPY start-kafka.sh /usr/bin

RUN chmod +x /usr/bin/start-kafka.sh

CMD ["/usr/bin/start-kafka.sh"]
