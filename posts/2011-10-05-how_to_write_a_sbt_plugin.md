% How to write a SBT 0.10+ plugin?
% fujohnwang
% 2011-10-05
__author: fujohnwang__

To draft a SBT 0.10+ plugin, TWO parts should be taken into consideration:

1. the build file of the plugin;

2. the source code file or the definition files of the plugin;

 



# the build file of the plugin sample (build.sbt under project root)     

<pre>
sbtPlugin := true

name := "aspectj_sbt_plugin"

version := "0.0.1"

organization := "name.fujohnwang"

publishMavenStyle := true

scalacOptions := Seq("-deprecation", "-unchecked")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("org.aspectj" % "aspectjtools" % "1.6.11.RELEASE", "org.aspectj" % "aspectjrt" % "1.6.11.RELEASE","org.aspectj" % "aspectjweaver" % "1.6.11.RELEASE")
</pre>
 


#     the definition file of the plugin sample(src/main/scala/*.scala)       



```scala

import sbt._
import Keys._

object MyPlugin extends Plugin {

 val MyConfiguration = config("myconf")

 val mySetting = SettingKey[String]("my-setting")

 val myTask = TaskKey[Unit]("my-task", "task description")

 override lazy val settings = inConfig(MyConfiguration)(Seq(

 mySetting := "initial value for my-setting",

 myTask <<= (streams, mySetting, …) map{

 (s, ms, …)=> 

 // do what u want to do with the arguments

 },

 // other settings 

 ))

}

```

Note: U refer to each setting via their key and refer to their values via map from key.

 



#          Last Mile - How to use the plugin u have just finished?            


in your project, 2 places should be taken care about:

1- the "project/plugins.sbt"
<pre>
 resolvers += yourResolver   // help sbt to find out where your plugin is

 addSbtPlugin("name.fujohnwang" % "aspectj_sbt_plugin" % "0.0.1")   // declare to use your plugin
</pre>

2- build.sbt under the root path of your project(light configuration) or project/Build.scala(full configuration)

 usually, you can customize the settings of the plugin or add necessary dependencies in your build file(s), this is variable as per your usage scenarios. If default values are ok for you, nothing about plugin is needed to add to your build file.

 



#         What U can learn from the experience of writing a plugin?          


1- declare custom Configuration to enhance the modularity;

2- declare necessary SettingKey(s) to make your plugin flexible(which allows your users to customize the plugin)

3- each Keys(Setting or Task) can be initialized or implemented by <<= with other Key(s)

 

 

 

 

# References:

1. <https://github.com/harrah/xsbt/wiki/Plugins-Best-Practices>
2. <https://github.com/harrah/xsbt/wiki/sbt-0.10-plugins-list>
3. <http://eed3si9n.com/sbt-010-guide>
4. The AspectJ compiler API
5. typesafehub-sbt-aspectj plugin
