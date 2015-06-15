% 在SBT中如何`mvn dependency:tree`
% FuqiangWang - fujohnwang AT gmail DOTA com
% 2015-06-15


# 快速答案

使用[sbt-dependency-graph](https://github.com/jrudolph/sbt-dependency-graph)插件！

# 如何使用？

我想项目的README中已经说明的很清楚了，所以，这里我只是简单复述一下...

一般情况下，扶墙老师更愿意将项目相关的配置都“内聚”到项目自己的配置中， 不过， 其实像依赖分析这种关注点， 一个它不是项目的功能依赖， 另外一个，它又是很通用的功能，所以， 最合适的做法就是把它配置成global的plugins。

在SBT 0.13.x中，`~/.sbt/0.13/plugins/`目录可以认为是一个标准的SBT项目， 这个项目的配置文件中配置的插件即认为是全局的global plugins， 所以我们在`~/.sbt/0.13/plugins/plugins.sbt`中添加如下配置：

~~~~~~~ {.scala}
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
~~~~~~~

一般的插件，这样就OK了，但是sbt-dependency-graph插件还有点儿设置需要配置，所以，还要在`~/.sbt/0.13/global.sbt`中添加如下内容：

~~~~~~~ {.scala}
net.virtualvoid.sbt.graph.Plugin.graphSettings
~~~~~~~

以上两步都搞定之后，就可以执行如下命令来分析当前项目的依赖关系了：

~~~
sbt dependency-graph 
~~~

当然， sbt-dependency-graph还有其它功能特性， 比如将依赖分析结果导出为.graphml文件然后在yEd中打开查看这样的功能， 更多可以参考项目的README说明。


