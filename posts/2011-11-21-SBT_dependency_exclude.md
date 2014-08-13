% SBT Dependencies Exlusion Gotchas
% fujohnwang
% 2011-11-21

在SBT的[Quick Configuration Examples](https://github.com/harrah/xsbt/wiki/Quick-Configuration-Examples)最末尾发现个小惊喜：

```scala
// Exclude transitive dependencies, e.g., include log4j without including logging via jdmk, jmx, or jms.
libraryDependencies +=
"log4j" % "log4j" % "1.2.15" excludeAll(
    ExclusionRule(organization = "com.sun.jdmk"),
    ExclusionRule(organization = "com.sun.jmx"),
    ExclusionRule(organization = "javax.jms")
)
```

称其为小惊喜是因为，这个功能可以帮我去掉看起来比较丑陋的inline ivy xml配置：

<pre>
ivyXML :=
&lt;dependencies>
&lt;exclude module="junit"/>
&lt;exclude module="activation"/>
&lt;exclude module="jmxri"/>
&lt;exclude module="jmxtools"/>
&lt;exclude module="jms"/>
&lt;exclude module="mail"/>
&lt;/dependencies>
</pre>

但试过时候发现，不行，老报ModuleId没有exclude或者excludeAll方法，我就纳闷了。查了0.10.x的代码，确实没有，　但我用的是0.11.0啊，查了代码，明明是有呀，擦，先不管，升级SBT到0.11.1再说，升级之后，果然OK，我了个去，查了github上代码的history，发现了证据：

<pre>	
Sep 09, 2011
 
Support for simple exclusion rules in inline dependencies 
…
This support excluding a library from the dependency tree for a given
set of `ExclusionRule`s. There are two ways to achieve this:

- Using `organization` and `name` pairs:
val dep = "org" % "name" % "version" exclude("commons-codec", "commons-codec") exclude("org.slf4j", "slf4j-log4j")

- Using `ExclusionRule`:
val dep = "org" % "name" % "version" excludeAll(ExclusionRule("commons-codec", "commons-codec"), ExclusionRule("org.slf4j", "slf4j-log4j"))
</pre>

好吧，就是这样子，大家想用特定于ModuleId的依赖排除，记得升级到0.11.1吧！
