% SBT免翻墙手册
% FuqiangWang


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


