% 使用SBT构建可交互的命令行应用程序(Build Interactive Command Line Application With SBT)
% fujohnwang
% 2012-07-17


# 楔子

很多时候，受限于生产环境端口访问等限制， 我们没法通过一些方便的图形化界面的工具来监控和维护生产环境部署的应用， 为了规避这个问题，通常会随应用一起发布一些部署到本地的命令行工具程序或者脚本， 而如果这些命令行工具程序能够提供像大多数Termianl都有的命令自动补全(auto-completion)或者历史信息(history)等功能的话，那就更爽了。

SBT0.12.0之后提供了构建tab-completion可交互命令行程序的支持(从SBT一贯的发展来看，该功能只能算是一种锦上添花，不过，也进一步验证了SBT在架构设计上优秀的一致性和可扩展性)，这篇文字将简单回顾和汇总使用SBT构建可交互命令行程序相关细节，以期有所参考(好脑瓜子也不如烂笔头嘛！)。

#  Quick Start

1. 安装giter8 (<https://github.com/n8han/giter8>)， 如何安装参考该项目的README，有详细的介绍，这里不做赘述；
2. 执行`g8 harrah/command-line-app`命令，构建scaffold工程，该giter8模板功能是sbt作者harrah构建的，所有命令行应用可以使用这个模板工程作为起点；
3. 如果本地没有安装sbt(<https://github.com/harrah/xsbt>)，首先安装sbt， 在mac下，直接`brew install sbt`就可以搞定，其他环境参考sbt wiki说明<https://github.com/harrah/xsbt/wiki/Getting-Started-Setup>；
4. `cd $command-line-app$`, 因为项目的group id和artifact id等可以定制，这里使用__$command-line-app$__作为本地scaffold项目目录;
5. `sbt publish-local`, 发布该项目到本地repo；
6. `sbt @$command-line-app$/src/main/conscript/command-line-application/launchconfig`

<pre>
[info] Hello There!
> hello 
&lt;name>
> hello darren
[info] Hello darren!
> hello
[error] Expected whitespace character
[error] hello
[error]      ^
> 
</pre>

从以上运行结果可以看到，通过tab可以获取命令行程序可接受的命令以及参数形式，如果输入错误命令或者命令执行条件不满足，sbt将告知用户相应错误提示信息， cool， ha？！

# How it works?!

这里牵扯sbt两个主要的internal功能， 第一个就是我们这次主要关注的tab-completion功能，另一个则是sbt的launcher功能。

sbt launcher可以根据指定的launch specification来抓取发布到maven或者ivy repo的artifacts(当然，并不限于maven或者ivy)，artifacts如果依赖其他artifacts，sbt launcher将一并抓取到本地的ivy cache，之后， sbt launcher将根据launch specification中指定的main class启动指定的应用。

这样说有些抽象，就以command-line-app这个scaffold工程来讲吧，我们执行`sbt publish-local`之后，实际上是将其发布到了本地的repo中，在它的launch specification配置文件中，我们可以看到：
<pre>
[scala]
  version: auto
[app]
  org: org.example
  name: command-line-application
  version: 0.1-SNAPSHOT
  class: org.example.Main
  components: xsbti
[repositories]
  local
  maven-central
  typesafe-ivy-releases: http://repo.typesafe.com/typesafe/ivy-releases/, [organization]/[module]/[revision]/[type]s/[artifact](-[classifier]).[ext]
</pre>

像启动时要使用的scala version， 启动哪个artifact(org + name + version)， 使用哪个main函数(class)， 从哪些repo中去加载指定的artifact，在这个launch speficiation的配置中都有罗列。sbt launcher程序只需要根据这些信息来工作就足够了。

launch specification中配置的class需要符合一定的规则，简单来讲，就是指定的类需要实现`xsbti.AppMain` trait:

```scala
package org.example

import sbt._
import complete._
import DefaultParsers._

final class Main extends xsbti.AppMain
{
   /** Defines the entry point for the application.
   * The call to `initialState` sets up the application.
   * The call to runLogged starts command processing.
   */
   def run(configuration: xsbti.AppConfiguration): xsbti.MainResult =
      MainLoop.runLogged( initialState(configuration) )

   /** Sets up the application by constructing an initial State instance with the 
   * supported commands and initial commands to run.
   *
   * http://harrah.github.com/xsbt/latest/api/sbt/State%24.html
   */
   def initialState(configuration: xsbti.AppConfiguration): State =
   {
         // These are the commands that the application supports.
      val commandDefinitions = hello +: BasicCommands.allBasicCommands

         // These are the commands that are run when the application starts up.
         // "iflast shell" will drop to the interactive prompt if no arguments are
         //    provided on the command line
      val commandsToRun =
         "hello There" +: "iflast shell" +: configuration.arguments.map(_.trim)

      State( configuration, commandDefinitions, Set.empty,
         None, commandsToRun, State.newHistory,
         AttributeMap.empty, initialGlobalLogging, State.Continue )
   }

   /** A sample command that says hello to its argument.
   * The first argument to Command provides the command name.
   * The second is a function State => Parser[T], providing the parser+tab completion.
   * The third argument is a function (State, T) => State that accepts the result of parsing
   *  and transforms the application state (that is, it does the work).
   *
   * See also https://github.com/harrah/xsbt/wiki/Commands
   */
   def hello = Command("hello")(state => helloParser)( helloAction )

   def helloParser: Parser[String] =
      token(Space) ~> token(NotSpace, "<name>")

   def helloAction(state: State, name: String): State = {
      state.log.info( "Hello %s!".format(name) )
      state
   }

   /** Configures logging to log to a temporary backing file as well as to the console. 
   * An application would need to do more here to customize the logging level and
   * provide access to the backing file (like sbt's last command and logLevel setting).*/
   def initialGlobalLogging: GlobalLogging =
      GlobalLogging.initial(MainLogging.globalDefault _, java.io.File.createTempFile("app", "log"))
}
```

该trait的实现类只需要实现`def run (configuration: AppConfiguration): MainResult`方法， 它接收AppConfiguration参数，返回MainResult结果， 
MainResult主要有两种，即重启或者退出(Exit)，一般会选择退出，不过特殊情况下也可以选择重启，比如如果我们依赖的运行环境不满足，我们可以调整配置，降级也好，升级也好， 要求重启当前应用，在启动成功后，要求退出的情况下，再行退出。

以上实现类看起来纷繁复杂，其实很简单，基本原则就是：
1. 实现xsbt.AppMain的run方法；
2. 添加自定的commands
	
至于其他细节，都是可以暂时忽略的。

在上例中，只添加了一个自定的命令 - hello:

```scalaval commandDefinitions = hello +: BasicCommands.allBasicCommands
…

def hello = Command("hello")(state => helloParser)( helloAction )

def helloParser: Parser[String] = token(Space) ~> token(NotSpace, "<name>")

def helloAction(state: State, name: String): State = {
  state.log.info( "Hello %s!".format(name) )
  state
}```


下面我们来看每一个命令(command)是如何定义的吧！

#  SBT Command定义详解
原则上， SBT Command 定义[这里](https://github.com/harrah/xsbt/wiki/Commands)有详细的介绍，我只简单说一下。

一个命令定义由三部分组成(非严格意义上的，严格定义见sbt的wiki)：
1. 命令的名称，比如hello；
2. 命令使用的parser，用来验证命令语法以及转换命令数据到合适的数据结构；
3. 命令执行的action，用来定义具体的执行逻辑；

有了这个前提，再来看hello命令的定义就比较容易理解了吧？ `def hello = Command("hello")(state => helloParser)( helloAction )`

command的action定义接收parser转换的结果，然后执行相应的逻辑， 其定义为`(State, T) => State`, 对于hello的action定义来讲，就是`(State, String)=> State`：

```scaladef helloAction(state: State, name: String): State = {
  state.log.info( "Hello %s!".format(name) )
  state
}```


一个command，可以没有参数， 接收一个参数，或者接收多个参数， Command对象提供了相应的工厂方法， 具体参考[这里](https://github.com/harrah/xsbt/wiki/Commands)。

那么， 这个Command的action中，T或者说hello action的String参数是从哪里来的那？ 这其实就是parser combinator的功劳了。

# SBT Parser Combinator详解

Parser Combinator允许将一个个小的Parser组装成一个或者多个大的Parser， command的定义中，第二部分接收一个`State => Parser[T]`类型的函数定义， 用来解析和验证命令的语法，并将最终的解析结果传递给对应的action使用。 tab-completion的功能，实际上就是parser的功能。

有关如何解析输入，[wiki这里](https://github.com/harrah/xsbt/wiki/Parsing-Input)有详细的介绍，读过之后就了然了。

结合我们的command-line-app实例来说:


```scaladef helloParser: Parser[String] = token(Space) ~> token(NotSpace, "<name>")```

helloParser接收以空格分割的一个字符串形式的参数， token用于提示输入的格式或者需求。
complete.DefaultParsers中预先定义了许多Parser可是在定义自己的Parser的时候可以使用。


# Conclusion

简单来讲，有了Sbt的支持，构建一个可交互的命令行程序只需要做一个事情，即根据需要添加相应的Command定义到愿意支持的命令列表中，之后，发布到相应repo，然后本地使用sbt launcher来部署和执行即可。

现在，我们只需要关注相应command定义的语法(Parser)和逻辑(Action)定义就行了！

# 参考实例
除了harrah/command-line-application.g8这个脚手架模板项目，为了更好的理解基于sbt的命令行应用程序，以下项目也是很好的参考：

* [harrah/completion-demo](https://github.com/harrah/completion-demo)
	- 随harrah在scala day 2012上的演讲一起发布的实例工程项目
* [mpilquist/cjmx](https://github.com/mpilquist/cjmx)
	- 可以attach到本地JVM通过JMX对应用进行监控的命令行程序

题外话，其实像<https://github.com/zhongl/HouseMD>这种应用，完全可以构建在sbt的命令行功能之上， 跟cjmx这种类似。

# deployment made easy with conscript

每次分发和部署launch specification配置，并设置命令行环境和alias会很繁琐， 而且对个人用户来讲， 构建统一的repo也不现实，即使是local的repo也不方便保存， 随着github的兴起，我习惯于把一些项目托管到github，这有个好处，尤其是使用了sbt构建的应用程序，因为有[conscript](https://github.com/n8han/conscript)这个工具，它可以帮助我们将这些应用程序从github的repo安装到本地，并自动配置好使用环境。 有了conscript，像g8, pictureshow等等，都可以一个命令安装和更新，甚是方便。

# 更多应用场景畅想
tab-completion这个功能其实倒没啥好畅想的， 能直接想到的就是可以attach到JVM，并交互执行相应命令来监控目标应用的管理脚本或者说客户端；不过sbt launcher倒是可以畅想无限。

试想一下，如果把它当做部署的agent，每台服务器都预先部署一个sbt launcher， 那么，就可以构建一套和谐的开发，部署，运维的生态环境啦。
三者通过标准的maven repo或者ivy repo做桥梁， 开发发布artifact到repo， sbt launcher作为部署agent从repo拉取并部署相应的artifact到本地安装并部署和启动， 依赖管理也好，部署规范也好，都tmd的统一了，多好啊，哈哈，只可惜，在scala还是小众的情况下，这些也只是我意淫罢了，或许真有这么一天也说不定～

# 参考文档
* [Creating Command Line Applications Using sbt](https://github.com/harrah/xsbt/wiki/Command-Line-Applications)
* [Launcher Specification](https://github.com/harrah/xsbt/wiki/Launcher)
* <http://skillsmatter.com/podcast/scala/tab-completion-parser-combinators> ****推荐观看


	

