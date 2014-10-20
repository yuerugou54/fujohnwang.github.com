% Publish SBT project to Maven repo(Nexus?)
% WangFuqiang

--阶段性反刍--

# 准备配置内容

build.sbt中需要添加发布到maven相应配置：
<pre>
publishMavenStyle := true

publishTo := {
  val nexus = "http://192.168.1.248:9111/nexus/content/repositories/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "snapshots")
  else
    Some("releases"  at nexus + "releases")
}

credentials += Credentials("${realm}", "${host}", "${username}", "${password}")
</pre>

对于Nexus来说，默认的realm是“Sonatype Nexus Repository Manager”，如果不确定的话， 可以通过命令`curl -X POST http://${port}:${port}/nexus/content/repositories/releases -v`来获得，或者直接不写username和password，待报错的时候获得相应信息，因为sbt使用WWW-AUthenticate这个header来确定basic auth的realm。

host参数只写IP或者域名，不需要添加port！

# 本地测试

可以通过`sbt publish-local`先本地测试一下， 发布到本地maven库中并检查是否有遗漏或者错误

# 正式发布

运行`sbt publish`, 等待结果...

# 关于Credentials
直接写到project的build文件当然可以，但还是放到独立的一台发布机上比较靠谱一些，所以，我们可以在project的build文件里只声明credential文件的位置：

<pre>
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_busymachines_snapshots"),
      credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_busymachines_releases")      
</pre>

然后在相应的credentials文件中在存放相应的credential信息：

<pre>
realm=Repository Archiva Managed snapshots Repository
host=archiva.busymachines.com
user=myuser
password=mypass
</pre>

# 其它参考
1. [Publishing artefacts to OSS Sonatype Nexus using SBT and Travis CI Here...](http://www.cakesolutions.net/teamblogs/publishing-artefacts-to-oss-sonatype-nexus-using-sbt-and-travis-ci-here...)

