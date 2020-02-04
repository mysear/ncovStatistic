#设置基础镜像,如果本地没有该镜像，会从Docker.io服务器pull镜像
FROM centos:centos7
# OS环境配置
RUN yum install -y wget

#安装maven
RUN wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
RUN tar xzvf apache-maven-3.6.3-bin.tar.gz
RUN cp -R apache-maven-3.6.3 /usr/local/bin
RUN export PATH=apache-maven-3.6.3/bin:$PATH
RUN export PATH=/usr/local/bin/apache-maven-3.6.3/bin:$PATH
RUN ln -s /usr/local/bin/apache-maven-3.6.3/bin/mvn /usr/local/bin/mvn
RUN echo $PATH

#安装JDK
RUN mkdir -p /root/jdk
#RUN wget -P /root/jdk --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u141-b15/336fa29ff2bb4ef291e347e091f7f4a7/jdk-8u141-linux-x64.tar.gz"
RUN wget -P /root/jdk http://lf-resource.oss-cn-huhehaote.aliyuncs.com/jdk-8u201-linux-x64.tar.gz
RUN tar -zxf /root/jdk/jdk-8u201-linux-x64.tar.gz
RUN ls -alh

#创建app目录,保存我们的代码
RUN mkdir -p /root/ncov-stats
COPY ./src /root/ncov-stats/src
COPY ./pom.xml /root/ncov-stats/pom.xml

#设置环境变量，支持中文乱码
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV JAVA_HOME /jdk1.8.0_201
ENV PATH $JAVA_HOME/bin:$PATH
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib

#WORKDIR 相当于cd
WORKDIR /root/ncov-stats

#执行mvn打包命令
RUN mvn clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

#暴露container的端口
EXPOSE 10001

#运行命令
RUN echo $PATH

#WORKDIR 相当于cd
WORKDIR /root/ncov-stats/target
ENTRYPOINT ["java", "-Xmx100m", "-jar","-Dloader.path=.,libs","ncov-stats-1.0.0.jar","--name='ncov-stats'"]
