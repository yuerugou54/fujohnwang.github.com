% Scala开发者的SpringBoot快速入门指南
% 王福强 - fujohnwang AT gmail DOTA com
% 2015-07-21

使用Scala开发SpringBoot应用， 采用Maven作为构建工具，基本可以从Java开发SpringBoot应用的模式中平滑地迁移过来， 但如果使用SBT作为构建工具，那么多少需要一些定制...

# 使用Maven构建Scala开发的SpringBoot应用

访问<http://start.spring.io/>， 填写必要的项目信息并选择必要的选项， 点击“Generate Project”下载预先生成的项目模板， 因为我只选择了web和actuator两个模块的依赖以及以jar的形式发布，所以得到如下的pom定义：


~~~~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>org.test</groupId>
	<artifactId>demo</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>demo</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.2.5.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
	

</project>

~~~~~~~

这是为Java项目准备的， 要使用Scala在这个项目模板的基础上进行开发，我们需要在pom中再添加点儿“佐料” ... 

首先， 使用Scala进行开发，自然需要能够对Scala代码进行编译啦，所以，我们需要添加一个Maven的Scala编译插件：

~~~~~~~ {.xml}
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.1</version>
                <executions>
                    <execution>
                        <id>compile-scala</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>add-source</goal>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>test-compile-scala</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>add-source</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <recompileMode>incremental</recompileMode>
                    <scalaVersion>${scala.version}</scalaVersion>
                    <args>
                        <arg>-deprecation</arg>
                    </args>
                    <jvmArgs>
                        <jvmArg>-Xms64m</jvmArg>
                        <jvmArg>-Xmx1024m</jvmArg>
                    </jvmArgs>
                </configuration>
            </plugin>
~~~~~~~


其次，我们需要添加Scala的相关依赖，所以， pom中还需要添加至少如下配置：


~~~~~~~ {.xml}
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.version}</version>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-compiler</artifactId>
            <version>${scala.version}</version>
        </dependency>
~~~~~~~

至此， 就可以在src/main/scala下添加相应的Scala代码进行SpringBoot应用的开发了， so easy~




# 使用SBT构建Scala开发的SpringBoot应用

SpringBoot团队预先为Maven的用户提供了很多现成的支持，这大大降低了SpringBoot的入门门槛， 比如你只要继承spring-boot-starter-parent， 然后添加必要的module就可以开始一个SpringBoot项目，但是，如果我们不用Maven作为构建工具，那么， 就得自己稍微折腾一下了...

我们发现， spring-boot-starter-parent的pom定义中它的parent是spring-boot-dependencies， 不过spring-boot-dependencies只是定义了一堆的dependencyManagement和pluginManagement, 这显然只是为了Maven项目的依赖声明和管理的方便，所以，我们可以先暂时忽略spring-boot-dependencies的pom内容（回头其实还是会来参考它定义了哪些依赖）， 还是回去看spring-boot-starter-parent的pom定义。

在构建过程中， spring-boot-starter-parent会过滤src/main/resources下的yml和properties配置文件， 这里我们可以先忽略(而且也不指望把配置放在这个位置，起码偶不建议这么做)。

总得看下来， spring-boot-starter-parent也没有什么有用的东西， 所以， 没办法， 既然所有的SpringBoot应用都从某个starter项目开始， 那我们从最基础的spring-boot-starter项目的pom定义开始挖掘吧！

这个时候，我们就可以发现一些端倪了， 通常情况下， 一个SpringBoot项目一定是需要依赖如下artifacts的：



~~~~~~~ {.xml}
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-autoconfigure</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>commons-logging</groupId>
                    <artifactId>commons-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <scope>runtime</scope>
        </dependency>
~~~~~~~



其中， spring-core和spring-boot很好理解， SpringBoot应用嘛， 肯定需要对spring-boot的依赖，而spring-boot又依赖于spring-core即spring框架本身， 为了SpringBoot应用发挥其快捷的特性， spring-boot-autoconfigure自然也是不应该少的。

剩下的logging啦， yaml啦， 就可以根据情况来了，虽然logging一般也不能少。

为了验证我们的猜测和推断，我们再来看看spring-boot-starter-web和spring-boot-starter-actuator等一系列其它的starter modules， 就会发现， spring-boot-starter是最小子集，所以，哗啦， 整个事情就明朗了。

要使用SBT来构建SpringBoot应用，只要依照spring-boot-starter来添加必要的依赖就可以了！ 剩下的其它依赖，只要根据情况添加即可！


~~~~~~~ {.scala}

libraryDependencies += "org.springframework" % "spring-core" % "4.1.6.RELEASE" exclude("commons-logging", "commons-logging")

libraryDependencies += "org.springframework.boot" % "spring-boot" % "1.2.4.RELEASE"

libraryDependencies += "org.springframework.boot" % "spring-boot-autoconfigure" % "1.2.4.RELEASE"

libraryDependencies += "org.springframework.boot" % "spring-boot-starter-logging" % "1.2.4.RELEASE"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.14"

~~~~~~~

之后，我们只要用Scala来写相应的Controller以及服务啦


~~~~~~~ {.scala}
class Message {
  @BeanProperty
  var value: String = _
}


@RestController
class HelloController {

  @Value("${hello.message}")
  @BeanProperty
  var helloMessage: String = _

  @RequestMapping(value = Array("/hello"), method = Array(RequestMethod.GET))
  @ResponseBody
  def hello(): Message = {
    val message = new Message
    message.value = helloMessage
    message
  }
}
~~~~~~~

> NOTE
> 
> 上面的消息类我们也可以使用case class, 但注意也要添加@BeanProperty以便json的序列化可以正常完成： `case class Messsage(@BeanProperty var value:String)`



## 基于SBT的SpringBoot应用的发布

SpringBoot提供了一个插件spring-boot-maven-plugin用来将SpringBoot应用打包为按照一定格式组织的可执行的jar包， 这个功能很赞！

BUT， 哥比较懒，虽然我也可以把那个逻辑剥离出来弄成一个SBT插件 ^[参考SpringBoot的Reference文档的60. Supporting other build systems一章， 自定义一个Repackager]， 但哥决定还是变通一下， 将当前SpringBoot项目直接使用[sbt-native-packager](https://github.com/sbt/sbt-native-packager)打包发布好了， 启停脚本也可以自动生成。

所以，我们的build.sbt配置文件中还会再多添加几行配置：


~~~~~~~ {.scala}
mainClass := Some("com.example.Hello")

mappings in Universal += file("LICENSE") -> "LICENSE"

mappings in Universal ++= directory("config")
~~~~~~~

最终，一个完整的SBT项目build.sbt示例性配置文件如下：


~~~~~~~ {.scala}
import sbt._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.packager.MappingsHelper._

lazy val root = project in file(".") enablePlugins(JavaAppPackaging) settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

name := "springboot-scala-with-sbt"

organization := "com.github.fujohnwang"

version := "0.0.1-SNAPSHOT"

publishMavenStyle := true

publishTo := Some(Resolver.file("local .m2 repository",  new File(Path.userHome.absolutePath+"/.m2/repository")))

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "Central Maven Repository" at "http://repo1.maven.org/maven2/"

scalacOptions := Seq("-deprecation", "-unchecked","-optimise")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.11.7"

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.slf4j" % "jcl-over-slf4j" % "1.7.12"

libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.12"

libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "3.1.2"

libraryDependencies += "org.springframework" % "spring-core" % "4.1.6.RELEASE" exclude("commons-logging", "commons-logging")

libraryDependencies += "org.springframework.boot" % "spring-boot" % "1.2.4.RELEASE"

libraryDependencies += "org.springframework.boot" % "spring-boot-autoconfigure" % "1.2.4.RELEASE"

libraryDependencies += "org.springframework.boot" % "spring-boot-starter-logging" % "1.2.4.RELEASE"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.14"

libraryDependencies += "org.springframework.boot" % "spring-boot-starter-actuator" % "1.2.4.RELEASE"

libraryDependencies += "org.springframework.boot" % "spring-boot-starter-web" % "1.2.4.RELEASE"

mainClass := Some("com.example.Hello")

mappings in Universal += file("LICENSE") -> "LICENSE"

mappings in Universal ++= directory("conf")

~~~~~~~

OK，that's it, happy hacking spring boot with scala and sbt!









