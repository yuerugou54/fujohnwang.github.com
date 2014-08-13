% Understanding SBT In My Way
% fujohnwang
% 2010-11-24
__author: fujohnwang__

__NOTE: this is for SBT0.7.x-0.9.x versions__

# SBT Quick Start
To use SBT, you have to download the sbt-launch-version.jar first, in order to use it easily in future, I create a shell called sbt as per the documentation of SBT setup introduction:

<pre>
java -Xmx512M -jar `dirname $0`/sbt-launch-0.7.4.jar "$@"
</pre>

I make a directory under workspace, call "scalafun", then "cd scalafun" and run sbt. Since it's the first time I use sbt, it ask me whether to create a project, I choose Y of course, then it ask for some information on the project I create before the project creation is done. The information contains the project name, organize name, sbt version or scala version, just type in your answer or use the default choices.

<blockquote>
when create project structure with sbt, it may loss the "project/build" directory where you should put your build file there, so usually after running sbt to setup the default project structure, you may have to create the "project/build" directory and corresponding build defintion files manully.
</blockquote>

After the project setup is done, firstly, we have to define our build file in sbt, this is done by creating a scala file under "project/build", the content may be:

```scala
import sbt._

class MySQLEventEmitter(info:ProjectInfo) extends DefaultProject(info){
 val jbossRepository        = "Jboss Repository" at "http://repository.jboss.org/nexus/content/groups/public"
 val jbossRepository2       = "jboss Maven2 repo" at "http://repository.jboss.com/maven2"
 ...
 
 val netty = "org.jboss.netty" % "netty" % "3.2.2.Final"
 val scalatest = "org.scalatest" % "scalatest" % "1.2" % "test"
 ...
}
```

   
As you can see, I add some repositories and dependencies as per sbt's scala method syntax, it's so easy to setup such a common project without any difficulty.

Once above job is done, we can add our scala source files into src/main/scala directory, the src directory structure follows the maven's convention(if you used maven before, more exactly, it should be ivy style).

After you have finished you code, you can run "sbt update clean compile run" in the command under the "scalafun" directory, or most of the time, I will just run "sbt" and then get into the interactive console of sbt, there, I just type "~compile" and then everything starts to rock, commands with "~" means "continual", so once I edit my source file and save it, the compile will be performed immediately then you can see whether your code is ok or not. Of course, the most important thing is you don't have to care about the result if you don't want to, but the compilation will always there until you interrupt it when you will.

<blockquote>
Although we have add dependencies in the build file, they are not available before we run "sbt update" or just "update" in sbt console, even we have maven2 local repository there. So when some compilation errors complain some classes are missing, most of the time, they are asking you to run "update" to fetch the dependencies.^_^
</blockquote>

OK, until now, I think most of the things work for us, but how SBT makes all of the things run smoothly?

# How SBT works

The most interested thing about SBT is that it conforms to Scala everywhere, If you used maven before or still using it, you know the build defintion in maven is defined in xml(pom.xml) or groovy after maven3, although maven are mostly used with java projects. But since SBT is mainly for scala projects, its build files are written in scala too.

SBT build artifacts are located at "$your_project_folder$/project" folder which contains:

- build.properties // your project information and scala version information
- build // folder contains build file defintiion
- \* boot // folder

Under "build" sub-folder, you can define build file in a format of scala. SBT defines a scala trait Project as build specification, so to create your own build file, you just create a scala class which extends Project trait of SBT, most of the time we will extend DefaultProject, just as we have done in section 1 - SBT Quick Start.

The build file can be placed under "project/build" directly, or if your build file depends on other dependencies, you can create a similar directory structure as top sbt project, that's, the "project/build" is also a sbt project. That's means you can create src/main/scala/ directory and place your build file there, or you can create "lib" directory and place your 3rd party dependencies there too, blablabla.

we can define different things in your Project defintion, whether val, lazy task or methods, we can add our own artifacts definitions or we can override the orginal ones.

for example, method "unmanagedClasspath" will return the classpaths of unmanaged libraries, we can override it to add more paths we want:


```scala
class MyProject(info: ProjectInfo) extends DefaultProject(info)
{
  override def unmanagedClasspath: PathFinder =
  {
    super.unmanagedClasspath +++ Path.fromFile("some path")
   }
}
```

   
Note that if you just write :

<pre>
super.unmanagedClasspath +++ "your extension classpath"
</pre>

exception may raise which looks like:
<pre>
java.lang.IllegalArgumentException: requirement failed: Path component 'your path' must not have forward slashes in it
</pre>

If you dive into the code of "Path.scala", you may find the reason, to save your ass, just do as I did in above code snippet.

We can find more methods that we can customize to our own needs in SBT documentations(http://code.google.com/p/simple-build-tool/wiki/DocumentationHome), more advance features can be found in the documenation too, like modules, cross-building, web application project types, etc. I will not elaborate on these here, you have to find them out yourself.

Enjoy SBT.
