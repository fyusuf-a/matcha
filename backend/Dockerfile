FROM openjdk:18-alpine

RUN cd /tmp && \
	wget https://dlcdn.apache.org/maven/maven-3/3.8.7/binaries/apache-maven-3.8.7-bin.zip && \
	unzip apache-maven-3.8.7-bin.zip && \
	mv apache-maven-3.8.7 /opt/maven && \
	ln -s /opt/maven/bin/mvn /usr/bin/mvn && \
	rm apache-maven-3.8.7-bin.zip

WORKDIR /app

COPY pom.xml ./
RUN mvn clean package -DskipTests

COPY . ./
RUN mvn clean package -DskipTests

EXPOSE 4567

CMD [ "java", "-jar", "target/matcha-latest-jar-with-dependencies.jar" ]
