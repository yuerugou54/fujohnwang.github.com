% SBT免翻墙手册
% FuqiangWang

> 罢了， 从根儿上调整实在坑儿太难趟， 还是build.sbt里加一下resolvers吧！

<pre>
resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "Internal Maven Repository" at "http://[internal-host]/nexus/content/groups/public/"

externalResolvers := Resolver.withDefaultResolvers(resolvers.value, mavenCentral = false)
</pre>

resolvers保存我们自己添加的Resolver， 上面属于我们自己添加的有两个， 即本地maven库和公司内部库；

externalResolvers保存两部分来源的Resolver， 一个就是resolvers中的，另一个就是默认的Resolvers(ivy local和mavenCentral)， 我们希望依赖管理走本地maven库和公司内部库，所以， 通过覆盖原来的externalResolver的值来达到目的， 即上面配置中`mavenCentral = false`对应行。

没有加任何相关配置之前：

<pre>
> show external-resolvers
[info] ArrayBuffer(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), public: https://repo1.maven.org/maven2/)

> show resolvers
[info] List()
</pre>

加了以上配置之后：

<pre>
> show resolvers
[info] List(Local Maven Repository: file:///Users/yunshi/.m2/repository, Internal Maven Repository: http://repo.caimi-inc.com/nexus/content/groups/public/)
> show external-resolvers
[info] List(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), Local Maven Repository: file:///Users/yunshi/.m2/repository, Internal Maven Repository: http://.../nexus/content/groups/public/)
</pre>

没有覆盖externalResolver的情况：

<pre>
> show external-resolvers
[info] ArrayBuffer(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), public: https://repo1.maven.org/maven2/, Local Maven Repository: file:///Users/yunshi/.m2/repository, Internal Maven Repository: http://.../nexus/content/groups/public/)
</pre>

注意多了**public: https://repo1.maven.org/maven2/**

另外， externalResolvers + inter-project resolver形成fullResovers:
<pre>
> show full-resolvers
[info] List(Raw(ProjectResolver(inter-project, mapped: )), FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), Local Maven Repository: file:///Users/yunshi/.m2/repository, Internal Maven Repository: http://repo.caimi-inc.com/nexus/content/groups/public/)
</pre>

boot-resolvers如果不做下面的操作，原则上保持不变，默认是local(ivy) + typesafe-ivy-releases + maven central

到此为止。

----------------------------------------------

**以下方案的内容仅作参考， patterns部分无法满足全部需求， 建议不用！！！**

----------------------------------------------



# 初次查看项目resolver状态

<pre>
> show override-build-resolvers
[info] false

> show boot-resolvers
[info] Some(ArrayBuffer(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), URLRepository(typesafe-ivy-releases,Patterns(ivyPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), public: https://repo1.maven.org/maven2/))
[success] Total time: 0 s, completed Dec 3, 2014 5:32:48 PM
</pre>


# sbt启动参数配置

通过homebrew安装的sbt， 建议通过配置文件**/usr/local/etc/sbtopts**进行配置， 添加：

<pre>
-Dsbt.override.build.repos=true
</pre>

这将让sbt launcher使用**~/.sbt/repositories**中配置的repositories来替换sbt-launcher.jar中默认打包的配置， 所以，相应的我们要在~/.sbt/目录下新建一个repositories文件，然后添加自己想用的repositories配置：

<pre>
[repositories]
  local
  maven-local: file://~/.m2/repository
  typesafe-ivy-releases: https://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext], bootOnly
  community-plugins-ivy-releases: https://dl.bintray.com/sbt/sbt-plugin-releases/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]
  maven-repo-caimi: http://repo.caimi-inc.com/nexus/content/groups/public/
</pre>

这个相当于maven的~/.m2/settings.xml, ;-)

> 原<http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/>现在直接跳转到<https://dl.bintray.com/sbt/sbt-plugin-releases/>
> 
> sonatype nexus不支持ivy风格的repository proxying， 所以， 只好将typesafe和第三方sbt插件的ivy repository单独罗列，否则，直接放到一个proxy后面，只配置一个proxy的地址就够了。 原则上，maven2的repository尽量放到nexus上进行proxying。



# 再次查看项目resolver状态

<pre>
> show override-build-resolvers
[info] true

> show boot-resolvers
[info] Some(ArrayBuffer(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), maven-local: file://~/.m2/repository, URLRepository(typesafe-ivy-releases,Patterns(ivyPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), URLRepository(community-plugins-ivy-releases,Patterns(ivyPatterns=List(https://dl.bintray.com/sbt/sbt-plugin-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(https://dl.bintray.com/sbt/sbt-plugin-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), maven-repo-caimi: http://repo.caimi-inc.com/nexus/content/groups/public/))
</pre>



# 参考资料

1. [Proxy Repositories](http://www.scala-sbt.org/release/docs/Proxy-Repositories.html)
2. <http://stackoverflow.com/questions/10773319/sbt-doesnt-find-file-in-local-maven-repository-although-its-there>


