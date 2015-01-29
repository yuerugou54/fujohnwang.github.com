% 创建纯基于SBT的Play工程

使用typesafe activator当然可以，但是这鸟东西每次都要升级， 而且哥一般都是直接命令行走SBT， 即使是使用activator创建了工程，也会一并将创建的工程下所有activator相关的东西都删掉，免得提交到版本控制系统。

因为Play使用SBT编译，所以，Play工程本身也就是一个标准的SBT工程，做多加上Play插件，所以，我等可以从头干净的自己创建一个Play工程...

# 为SBT工程添加Play插件

在`project/plugins.sbt`配置中添加:

~~~~~~~ {.scala}
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")
~~~~~~~

sbt-plugin的版本随play的发布改为最新的或者自己合适的版本即可。

# 明确指定项目使用的SBT版本

在`project/build.properties`中配置, 比如: `sbt.version=0.13.5`

> 注意
> 
> sbt-plugin 2.3.7等都严格依赖sbt0.13.5，所以， 版本一定要对应上

# 在build文件中启用Play插件

在build.sbt中


~~~~~~~ {.scala}
name := "my-first-app"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)
~~~~~~~


OK, 按照一般的SBT项目来编译即可。


# 补充

以上只是基本配置， 实际上，我们还会添加更多配置项，比如， 在`project/plugins.sbt`中，我们可能会配置更多插件:

~~~~~~~ {.scala}
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("git://github.com/jrudolph/sbt-dependency-graph.git")

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.6")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.2")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.1")

addSbtPlugin("de.johoop" % "sbt-testng-plugin" % "3.0.2")
~~~~~~~

而在build.sbt中：

~~~~~~~ {.scala}
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import com.typesafe.sbt.packager.MappingsHelper._

organization := "com.github.fujohnwang"

name := """my-app-name"""

version := "1.0.6-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalaVersion := "2.11.1"

fullResolvers := Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "WaCai Internal Repository" at "http://repo.caimi-inc.com/content/groups/public/",
  "SBT Plugins Release" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/",
  "Typesafe Ivy Release" at "http://repo.typesafe.com/typesafe/ivy-releases/",
  "Typesafe Maven Release" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Central Maven Repository" at "https://repo1.maven.org/maven2/"
)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "mysql" % "mysql-connector-java" % "5.1.32",
  "redis.clients" % "jedis" % "2.5.2"
)

mappings in Universal += file("ReleaseNote.md") -> "ReleaseNote.md"

mappings in Universal ++= directory("agents")
~~~~~~~

> 注意
> 
> play的sbt-plugin的依赖的sbt native packager是0.7.4!

