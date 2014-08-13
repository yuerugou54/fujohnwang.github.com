% 有关Maven编译DeprecatedAPI失败的问题

  在项目代码里用了sun.misc.Signal和sun.misc.SignalHandler， 自己的Mac下编译没问题， 在别人的Windows下编译报"警告：sun.misc.Signal 是 Sun 的专用 API，可能会在未来版本中删除"， 警告也就算了， 还tmd编译失败， `maven -e`打出来的异常是：

~~~~~~~ {.java}
[INFO] Trace  
org.apache.maven.BuildFailureException: Compilation failure  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:715)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.java:556)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:535)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalAndHandleFailures(DefaultLifecycleExecutor.java:387)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeTaskSegments(DefaultLifecycleExecutor.java:348)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.execute(DefaultLifecycleExecutor.java:180)  
        at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:328)  
        at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:138)  
        at org.apache.maven.cli.MavenCli.main(MavenCli.java:362)  
        at org.apache.maven.cli.compat.CompatibleMain.main(CompatibleMain.java:60)  
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)  
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)  
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)  
        at java.lang.reflect.Method.invoke(Method.java:597)  
        at org.codehaus.classworlds.Launcher.launchEnhanced(Launcher.java:315)  
        at org.codehaus.classworlds.Launcher.launch(Launcher.java:255)  
        at org.codehaus.classworlds.Launcher.mainWithExitCode(Launcher.java:430)  
        at org.codehaus.classworlds.Launcher.main(Launcher.java:375)  
Caused by: org.apache.maven.plugin.CompilationFailureException: Compilation failure  
        at org.apache.maven.plugin.AbstractCompilerMojo.execute(AbstractCompilerMojo.java:516)  
        at org.apache.maven.plugin.CompilerMojo.execute(CompilerMojo.java:114)  
        at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:490)  
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:694)  
        ... 17 more 
~~~~~~~

看到这个我估计就是maven的问题了， 搜了一下， 居然发现“钱总”在部门的team blog里发了篇有关这个问题的blog ，真tmd悲剧，早知道也不用费那么些周折搜来搜去了。

恍然记得平台另一个框架里也用了sun.misc包里的东西，就问了下他们怎么编译过的， 最后的答案是直接把类拷贝出来了 ：-（ 这也行...

这里是钱总那里讨过来的解决方法：

~~~~~~~ {.xml}
<plugin>   
    <groupId>org.apache.maven.plugins</groupId>   
    <artifactId>maven-compiler-plugin</artifactId>   
    <version>2.3.1</version>   
    <configuration>   
        <optimize>true</optimize>   
    </configuration>   
    <dependencies>   
        <dependency>   
        <groupId>org.codehaus.plexus</groupId>   
        <artifactId>plexus-compiler-javac</artifactId>   
        <version>1.8.1-patch</version>   
            </dependency>   
    </dependencies>   
</plugin>
~~~~~~~

其实最简单的方法就是别用中文的locale，哈哈








