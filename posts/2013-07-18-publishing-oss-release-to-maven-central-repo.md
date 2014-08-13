% 发布开源项目到Maven Central
% 王福强

过程比较繁琐曲折，其实按照[官方文档](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide)一步一步做，大部分情况下是可以搞定的， 不过，为了方便后面查阅，简单整理为此文档。

# 总体路线概述
直接发布到central repo其实是不行的，所以，其实我们是发布到sonatype的repository， 然后sonatype的repository会定期把我们的artifacts同步到maven的central repo中。

基本上，如果我们的项目是第一次发布到sonatype，那么需要申请一个账号，并提交New Project的Ticket等待审批，审批之后，就可以开始准备发布项目所需的各项要求了。当然，因为Ticket审批需要2个工作日，那么，其实可以在这段时间就把准备工作做掉。

初次发布，POM文件需要规范为sonatype要求的格式，该添加的element还是要添加一下，另外， 因为需要对要发布的artifacts做签名，也需要安装一下GnuPGP，并将public key上传到指定的key server上去(hkp://pool.sks-keyservers.net)， 并在.m2/settings.xml中添加相应的passphrase配置。

所有这些事儿搞定之后，就可以执行命令开始发布， 总体流程大体如此。


# 申请账号并提交Ticket
这一步没啥难度，参考官方文档的说明即可：

1. 到https://issues.sonatype.org/申请账号
2. 到<https://issues.sonatype.org/browse/OSSRH>通过'+Create Issue'创建新的Ticket， 类型为New Project, 其它信息按照人家要求填写就行了

Ticket提交完了就等着就好了，人家说了要2个工作日才能审批完成，运气好估计会快点儿吧， 偶第一次因为SB网速，提交了2个相同的Ticket...

# 规范POM
这些element人家说了，必须要：

```
<modelVersion>
<groupId>
<artifactId>
<version>
<packaging>
<name>
<description>
<url>
<licenses>
<scm><url>
<scm><connection>
<developers>
```

另外， 除了二进制jar包，相对应的-sources.jar和-javadoc.jar也是必须的，所有这些jar以及pom.xml都需要通过PGP签名，所以，我们需要在pom.xml里添加相应的maven插件来帮助我们搞定这些：

```
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-source-plugin</artifactId>
				<executions>
					<execution>
						<id>attach-sources</id>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-javadoc-plugin</artifactId>
				<configuration>
					<charset>${file_encoding}</charset>
					<encoding>${file_encoding}</encoding>
				</configuration>
				<executions>
					<execution>
						<id>attach-javadocs</id>
						<goals>
							<goal>jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
```

我们不想任何时候都做pgp签名，这样会比较耗费时间，一般只是在发布的时候才执行pgp前面，所以，我们将maven的pgp插件放入pom.xml里单独的一个profile中：

```
	<profiles>
		<profile>
			<id>release-sign-artifacts</id>
			<activation>
				<property>
					<name>performRelease</name>
					<value>true</value>
				</property>
			</activation>
			<build>
				<plugins>
					<plugin>
						<groupId>org.apache.maven.plugins</groupId>
						<artifactId>maven-gpg-plugin</artifactId>
						<version>1.1</version>
						<executions>
							<execution>
								<id>sign-artifacts</id>
								<phase>verify</phase>
								<goals>
									<goal>sign</goal>
								</goals>
							</execution>
						</executions>
					</plugin>
				</plugins>
			</build>
		</profile>
	</profiles>
```

除此之外， 我们的pom应该继承自sonatype oss:

```
  <parent>
    <groupId>org.sonatype.oss</groupId>
    <artifactId>oss-parent</artifactId>
    <version>7</version>
  </parent>
```

并且，有关SCM的信息需要严格填写， maven将在发布的时候自动checkout并打标发布。


# 配置PGP
如果发布机上没有安装，需要安装一下， 俺本地因为是mac，所以直接下了pgptools，一键安装， very easy～

安装后，创建一个pgp key，并发布到指定的key server - `hkp://pool.sks-keyservers.net`， 因为pgptools默认是发布到gnu的key server，所以，需要在PGP Keychain Access.app的Preferences里将key server选择为hkp://pool.sks-keyservers.net

所有这些搞定之后，在~/.m2/settings.xml中添加：

```
<settings>
  ...
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
  </servers>
  ...
</settings>
```

用户名和密码在一开始已经申请过了，填上即可。

更详细步骤参看<https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven>

# 发布SNAPSHOT版本

```bash
$ mvn clean deploy
```

# 发布Staging版本

```bash
$ mvn release:clean
$ mvn release:prepare
$ mvn release:perform
```

期间会提示需要输入pgp key的passphrase，照办就是了。

哥的Ticket当天没批下来，所以，运行会告诉我没有权限上传artifacts:
<pre>
 [ERROR] Failed to execute goal org.apache.maven.plugins:maven-deploy-plugin:2.5:deploy (default-deploy) on project cobar-client: Failed to deploy artifacts: Could not transfer artifact com.alibaba.cobar:cobar-client:jar:1.0.5 from/to sonatype-nexus-staging (https://oss.sonatype.org/service/local/staging/deploy/maven2/): Access denied to: https://oss.sonatype.org/service/local/staging/deploy/maven2/com/alibaba/cobar/cobar-client/1.0.5/cobar-client-1.0.5.jar -> [Help 1]
</pre>

没办法，等吧～

# 正式发布
访问web地址：<https://oss.sonatype.org/>

登陆， 选择左边栏里的"Staging Repositories", 然后点Close按钮，sonatype会做响应的validation，通过的话，就可以点Release发布啦，如果不通过，或者人工检查有问题，就先Drop，并重新做Staging发布， 知道搞定之后，再行发布。 没Release之前随便搞， Release之后就板上钉钉了。


# 总结

第一次搞需要拢的东西比较多，而且很久没搞Maven了(俺现在搞SBT比较多，u know的)， 所以，搞的期间偶尔出点儿小问题刺挠刺挠我，都是小事儿，总体上来讲， 官方文档还是比较靠谱的。


# 参考资料
1. [官方文档](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide)
2. <http://datumedge.blogspot.com/2012/05/publishing-from-github-to-maven-central.html>
3. <https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven>

