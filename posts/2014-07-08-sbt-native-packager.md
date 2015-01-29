% sbt-native-packager快速上手
% WangFuqiang

插件项目地址: <http://www.scala-sbt.org/sbt-native-packager>

# 安装

`project/plugins.sbt`中添加：

> addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.4")

build.sbt文件中添加:
<pre>
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

...

packageArchetype.java_application
</pre>

> java_application - Defines packaging of your project with a start script and automatic PATH additions
> java_server - Defines packaging of your project with automatic service start scripts (supports System V + init.d).

# 使用

运行`sbt stage`可以在路径`target/universal/stage/`下查看要发布的文件目录结构， 其中包含bin, lib等目录

> NOTE: 如果bin目录没有出现，可能因为当前sbt项目包含多个main class，这个时候，可以在build.sbt中明确指定使用哪个mainClass， 比如:
> mainClass in (Compile) := Some("com...Main")


运行`sbt dist`发布相应的packages

其它可以用的tasks包括：

* universal:packageBin - Generates a universal zip file
* universal:packageZipTarball - Generates a universal tgz file
* debian:packageBin - Generates a deb
* docker:publishLocal - Builds a Docker image using the local Docker server
* rpm:packageBin - Generates an rpm
* universal::packageOsxDmg - Generates a DMG file with the same contents as the universal zip/tgz.
* windows:packageBin - Generates an MSI

# 添加配置文件

配置文件有两种：
1. jvm相应的配置，比如虚拟机参数，这些会包含到自动生成的启动脚本中；
2. 应用特定的配置文件，比如spring xml， application.conf， logback.xml等

创建`src/universal/conf`目录，并将这些文件添加进去

> 默认情况下，  `src/universal`目录会被包含到发布包中， 所以像lisence, readme之类的文件也可以放进去一并发布。

## JVM相应配置
新建`src/universal/conf/jvmopts`文件，其中， jvmopts文件中保存了自定义的虚拟机参数，如果要让这些参数在发布后的script启动脚本中生效，我们需要在build.sbt中添加配置:

> bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

## 命令行参数指定应用配置
比如logback或者typesafe config等库可以通过启动参数指定配置(`-Dconfig.file=...`), 为了把这些配置文件的位置也添加到启动脚本， 我们需要在build.sbt中添加配置:

> bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/app.config""""

当然了，假设我们不通过命令行参数使用这些配置文件，那么我们完全可以通过相对路径来加载指定目录conf下的配置文件。

# 覆盖默认脚本
如果自动打包的启动脚本有bug或者其它原因， 可以覆盖之， 参考<http://www.scala-sbt.org/sbt-native-packager/GettingStartedApplications/OverridingTemplates.html>, 不过一般情况下不建议完全覆盖和替换。


# 添加自定义随同发布文件和目录


~~~~~~~ {.scala}
import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

// ...

mappings in Universal += file("ReleaseNote.md") -> "ReleaseNote.md"

mappings in Universal ++= directory("conf")

mappings in Universal ++= directory("agent")
~~~~~~~




























