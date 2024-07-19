FROM openjdk:17-alpine

RUN apk update && apk upgrade && \
apk add \
maven

RUN mkdir /code

COPY . /code

RUN cd /code && \
mvn package && \
mkdir /opt/app && \
mv /code/target/charity-0.0.1-SNAPSHOT.jar /opt/app && \
cd / && \
rm -r /code

RUN apk del \
maven

EXPOSE 8080

WORKDIR /opt/app

CMD java -jar charity-0.0.1-SNAPSHOT.jar