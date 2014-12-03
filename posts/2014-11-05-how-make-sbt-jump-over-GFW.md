% SBT免翻墙手册
% FuqiangWang

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



# 查看项目resolver状态

<pre>
> show full-resolvers
[info] List(Local Maven Repository: file:///Users/yunshi/.m2/repository, WaCai Internal Repository: http://repo.caimi-inc.com/nexus/content/groups/public/, Central Maven Repository: https://repo1.maven.org/maven2/, Typesafe Maven Release: http://repo.typesafe.com/typesafe/maven-releases/, SBT Plugins Release: http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/, Typesafe Ivy Release: http://repo.typesafe.com/typesafe/ivy-releases/)
[success] Total time: 0 s, completed Dec 3, 2014 5:30:30 PM

> show boot-resolvers
[info] Some(ArrayBuffer(FileRepository(local,FileConfiguration(true,None),Patterns(ivyPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(${ivy.home}/local/[organisation]/[module]/(scala_[scalaVersion]/)(sbt_[sbtVersion]/)[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), URLRepository(typesafe-ivy-releases,Patterns(ivyPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), artifactPatterns=List(https://repo.typesafe.com/typesafe/ivy-releases/[organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]), isMavenCompatible=false, descriptorOptional=false, skipConsistencyCheck=false)), public: https://repo1.maven.org/maven2/))
[success] Total time: 0 s, completed Dec 3, 2014 5:32:48 PM

> show override-build-resolvers
[info] false
</pre>


# 参考资料

1. [Proxy Repositories](http://www.scala-sbt.org/release/docs/Proxy-Repositories.html)
2. <http://stackoverflow.com/questions/10773319/sbt-doesnt-find-file-in-local-maven-repository-although-its-there>


