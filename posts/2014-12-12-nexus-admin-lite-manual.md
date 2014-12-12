% Nexus Maven Repository Manager管理手册
% 王福强


# 配置

## 服务端口和访问路径

${NEXUS}/conf/nexus.properties配置文件中可以调整如下选项：

1. nexus的访问端口，默认8081
2. nexus服务的绑定IP， 默认0.0.0.0， 即绑定到所有的NIC；
3. web应用的context路径，默认/nexus
4. nexus运行期间数据，包括repo的路径等

一般情况下不需要修改该配置文件，使用默认项即可， 除非端口被占用，换换绑定端口。

## 启动参数调整

nexus的默认JVM参数设置的比较小，生产环境下基本不够用，经常OOM, permGen爆掉等情况，所以，需要调整默认的JVM参数。

启动参数等调整需要修改$NEXUS_HOME/bin/jsw/conf/目录下的wrapper.conf文件， *wrapper.java.initmemory*和*wrapper.java.maxmemory*是设置heap size的快捷配置项， 但最大只能设置4G，即使设置>4G，启动的时候依然是4G， 如果要使用更大的heap，需要通过**wrapper.java.additional.{sequence}**配置项， 比如：

<pre>
wrapper.java.additional.1=-XX:MaxPermSize=512M
wrapper.java.additional.2=-Djava.io.tmpdir=./tmp
wrapper.java.additional.3=-Djava.net.preferIPv4Stack=true
wrapper.java.additional.4=-Dcom.sun.jndi.ldap.connect.pool.protocol="plain ssl"
wrapper.java.additional.4.stripquotes=TRUE
wrapper.java.additional.5=-Xms16G
wrapper.java.additional.6=-Xmx16G
</pre>

> NOTE
> 
> 我们使用wrapper.java.additional配置项来设置JVM的heap size， 切记把原来的*wrapper.java.initmemory*和*wrapper.java.maxmemory*注释掉！！！

这里，我们主要是对JVM的PermSize和heap size进行了调整， 如果有必要，可以添加其他更多JVM配置。


## 监控

第一步，在${NEXUS_HOME}/bin/jsw/conf/目录下的wrapper.conf文件中添加：

<pre>
wrapper.app.parameter.3=./conf/jetty-jmx.xml
</pre>

第二步， 在${NEXUS_HOME}/conf/nexus.properties中添加:

<pre>
jmx-host=192.168.3.190jmx-port=1099
</pre>

监控端口开放之后，通过jvisualvm或者jconsole直接连接监控即可。

# 启动

The startup script **nexus** supports the common service commands start, stop, restart, status, console and dump.

~~~~~~~ {.bash}
./bin/nexus start
~~~~~~~



# 常见问题

## 访问maven-metadata.xml慢

### 原因描述

1. <http://maven.40175.n5.nabble.com/Getting-maven-metadata-xml-for-a-group-very-slow-td5768650.html>
2. <https://support.sonatype.com/entries/25884097-Troubleshooting-slow-maven-metadata-xml-download-speeds>

### 解决方案

通过设置routing可以缓解<http://books.sonatype.com/nexus-book/reference/confignx-sect-managing-routes.html>

即指定哪些group或者artifact的元信息要指向哪个remote repo，这样nexus可以更快的找到这些元信息，而不用都扫一遍，都扫一遍的代价就是，遇到慢的remote repo，整体反应就是僵死。 默认nexus没有添加详细的routing信息。

<pre>
.*/org/apache/maven/.*     -> Central
</pre>




# http://central是怎么回事？


~~~~~~~ {.xml}
<settings>
  <mirrors>
    <mirror>
      <!--This sends everything else to /public -->
      <id>nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://localhost:8081/nexus/content/groups/public</url>
    </mirror>
  </mirrors>
  <profiles>
    <profile>
      <id>nexus</id>
      <!--Enable snapshots for the built in central repo to direct -->
      <!--all requests to nexus via the mirror -->
      <repositories>
        <repository>
          <id>central</id>
          <url>http://central</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </repository>
      </repositories>
     <pluginRepositories>
        <pluginRepository>
          <id>central</id>
          <url>http://central</url>
          <releases><enabled>true</enabled></releases>
          <snapshots><enabled>true</enabled></snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
  <activeProfiles>
    <!--make the profile active all the time -->
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
~~~~~~~


settings.xml文件中， profile元素下配置的**http://central**是一个bogus URL, 会被mirror配置的URL覆盖， 指向mirror指定的url。  其实， 直接写具体的URL也是可以的。






