FROM ubuntu
MAINTAINER Owen Wolf <owen@gowolf.io>
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get clean

VOLUME /temp
RUN apt-get install -y nano
ADD Nexus-v117.jar /Nexus-v117.jar
COPY /bin /bin
COPY /scripts /scripts
CMD ["sh", "-c", "java -jar /bin/Nexus-v117.jar > var/log/nexus.log"]
CMD java -jar /Nexus-v117.jar > var/log/jar.log