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




