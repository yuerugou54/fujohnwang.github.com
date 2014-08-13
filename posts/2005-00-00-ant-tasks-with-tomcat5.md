% Ant Tasks Supplied With Tomcat5
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

对于tomcat5来说，因为catalina已经提供了管理所需要的ant任务，所以，可以再外面通过ant来管理tomcat的webapp的部署等工作。

下面是如何定义和使用这些ant任务的脚本表示：

1. 定义一个tomcatTask.properties文件，文件中按照ant的taskdef任务文件定义的内容格式来定义以下内容：

<pre>
deploy=org.apache.catalina.ant.DeployTask
 undeploy=org.apache.catalina.ant.UndeployTask
 remove=org.apache.catalina.ant.RemoveTask
 reload=org.apache.catalina.ant.ReloadTask
 start=org.apache.catalina.ant.StartTask
 stop=org.apache.catalina.ant.StopTask
 list=org.apache.catalina.ant.ListTask
</pre>

> 注：【taskName=Task的实现class】是ant的taskdef规定的格式，当然，1和2完全可以按照每个task都对应自己的taskdef。

2. 在ant的构建脚本build.xml中定义tomcat管理所用的Tasks

~~~~~~~ {.xml}
<taskdef file="tomcatTasks.properties">
  <classpath>
   <pathelement path="${tomcat.home}/server/lib/catalina-ant.jar"/>
  </classpath>
 </taskdef>
~~~~~~~

3. Tasks定义完成后，就可以定义tomcat管理的target来对tomcat进行管理了：

~~~~~~~ {.xml}
    <target name="install" description="Install application in Tomcat" depends="war">
        <deploy url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"
            path="/${webapp.name}"
            war="file:${dist.dir}/${webapp.name}.war"/>
    </target>

    <target name="remove" description="Remove application from Tomcat">
        <undeploy url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"
            path="/${webapp.name}"/>
    </target>

    <target name="reload" description="Reload application in Tomcat">
        <reload url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"
            path="/${webapp.name}"/>
    </target>

    <target name="start" description="Start Tomcat application">
        <start url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"
            path="/${webapp.name}"/>
    </target>

    <target name="stop" description="Stop Tomcat application">
        <stop url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"
            path="/${webapp.name}"/>
    </target>

    <target name="list" description="List Tomcat applications">
        <list url="${tomcat.manager.url}"
            username="${tomcat.manager.username}"
            password="${tomcat.manager.password}"/>
    </target>
~~~~~~~

4. 另外，上面的task中时间上用到了与tomcat相关的一些property，所以，我们在build.properties中定义他们，当然也可以在其他地方定义他们，但总之，在用之前，需要定义这些properties。

<pre>
tomcat.manager.url=http://localhost:8080/manager
tomcat.manager.username=admin(对应你自己的username)
tomcat.manager.password=admin(对应你自己的password)
</pre>

之后，在build.xml的开始部分，定义<property file="build.properties"/>将这些properties引入。

OK，以上就是使用tomcat5提供的ant任务对其进行管理的Overview，仅供Reference。



