% Logging Framework Explained (afoo.me)
% 王福强

源于@实仙 生产环境碰到的一个日志问题，遂写一写...

# logging framework溯源

蛮荒时代，我们用System.out.println

工业时代，我们有了log4j

要是所有人都使用log4j也就好了，其实不同的人还是会有不同的选择，所以，有人为了屏蔽这种mess，就开始封装，比如，我写个wrapper，通过配置来决定是使用古老的System.out.println还是使用工业装备log4j。 

好啦，现在我们就可以从两个角度来看待logging啦：

1. A logging facade or logging API
	- slf4j
	- jakarta commongs logging(jcl)
2. A logging implementation
	- logback
	- log4j
	- jdk logging

logging facade/API实际上就是我们写的那些个wrapper，而logging implementation你懂的哼，System.out.println也算～

不过，我把这问题简化的太厉害了，其实，还有很多因素和原因才导致今天这种局面，比如， 我们可以将软件系统划分为两类，一类就跟游戏里的灵兽一样，虽然有固定的功能，但一般不会独立开疆扩土，必须依附于某个游戏人物，这类东东就是我们写的或者引用的第二方或者第三方依赖，比如类库，框架等； 第二类就比较独立了，我喜欢将其称为Endpoint型系统， 有独立主权，可以单独运作，可能会有一些灵兽型的依赖，但用哪些灵兽则可以自己说了算，总之，就是老子是老大，一起围着我来转。 对于“灵兽“型系统，因为它一般要依附于Endpoint型系统，但依附于谁它不知道，所以，宿主用的什么logging设施也就更无从知晓了，但是它自己又要用log来调试自己，而这些log最终却要由Endpoint系统来处理，如果它用不同的log implementation，那显然调性不合嘛，配合不默契咋打怪？！ 所以，对于灵兽型系统来讲，最好是它只使用某个抽象接口和协议来写下日志需求，而最终这些日志的输出和控制，则由最终依附的Endpoint系统来决定。 一句话， 对灵兽型系统，用Logging facade/API; 对Endpoint型系统，行使自己选择和使用logging implementation的权利(当然，对Enpoint系统来说，他当然也可以使用logging facade/API，不用非要把自己绑死到一个logging implementation上去嘛)


# 梳理SLF4J

SLF4J是个logging facade/logging API, 从名字上也看的出来嘛， “Simple Logging Facade for Java”

只要是个logging facade/logging API， 它就要考虑一个问题， 最终的日志该由谁或者说哪个logging implementation来负责, 用专业术语来讲就是，binding关系怎么确定！

这里有两种方式：

1. 动态绑定/运行期绑定 (dynamic/runtime binding)
2. 静态绑定(static binding)

jakarta commons logging这个logging facade/api框架用的前者，即动态绑定，搞出一堆问题，所以slf4j的作者针对这些问题，就搞出了个用静态绑定的slf4j。 

那slf4j的静态绑定是怎么一个结构那？ 各位看官请看：

<pre>
		xxx-over-slf4j   (adapt/bridge)
			|
		    \/
		slf4j API
		/    |    \  
	  /      |      \
logback  slf4j-log4j  slf4j-jdk   (binding)
			|			|
		  log4j        java.util.logging			

PS. logback is an implemenation with binder inside
</pre>

SLF4J提供了一堆jar包，初来乍到的一看到估计就晕了，别啊， 其实挺简单的 - 所有xxx-over-slf4j命名的jar包和slf4j-api包其实都可以划归到logging facade/api这个类别里，这些jar包的目的就是为程序提供logging接口， 只不过， 每个人口味不同，有得喜欢用jcl，有得喜欢用log4j的api，为了弥合这种差异(其实就是保持兼容并让遗留系统可以平滑迁移)， SLF4J就提供了这些桥接或者适配器，你愿意用哪种logging facade/logging api 用就好了，最终我都给你导到slf4j api上去！

好啦， 既然所有的都导到slf4j api，那下一步就该决定用哪种logging implementation了，毕竟slf4j-api也是个logging facade/api嘛，人家不干底层的脏活儿累活儿。 要选择谁来干活儿， 需要为slf4j指定用哪个binder， 比如用log4j，就要指定一个用于log4j的binder； 用java.util.logging，就要指定一个用于jul的binder； 好在这些都有现成的，slf4j都提供了，其实就是下面那一堆命名形式类似于slf4j-XXX的jar包， 比如选择log4j作为最终的logging implementation,那么我们就将slf4j-log4j.jar包放入classpath，这样，它就会把log4j和slf4j-api关联起来， slf4j-api就知道，噢， 我要用log4j输出和管理日志（当然， 这个时候log4j也要在classpath里咯，binder和implemenation要成对儿出现）。

logback是个特例，原则上来讲它是个logging implementation，不过， 它自己提供了slf4j的binder实现，而且，它还就直接用slf4j-api作为它的logging facade/api,所以，一看这小子就是个集成度比较高的主儿，不过，通过放大镜看看，其实，它还是分成了2层，一层是logging api, 一层是logging implementation!



#  参考
1. <http://stackoverflow.com/questions/3222895/why-is-commons-logging-believed-to-be-unpopular>
2. [Taxonomy of class loader problems encountered when using Jakarta Commons Logging](http://articles.qos.ch/classloader.html)
3. [Think again before adopting the commons-logging API](http://articles.qos.ch/thinkAgain.html)
4. [Commons Logging was my fault](http://radio-weblogs.com/0122027/2003/08/15.html)
5. <http://commons.apache.org/proper/commons-logging/guide.html#Configuration>
6. [SLF4J Vs JCL / Dynamic Binding Vs Static Binding](http://v4forums.wordpress.com/2008/12/27/slf4j-vs-jcl-dynamic-binding-vs-static-binding/)
7. [Dynamic Binding or Late Binding](http://geekexplains.blogspot.com/2008/06/dynamic-binding-vs-static-binding-in.html)
8. [commons-logging classloader pain](http://wrschneider.blogspot.com/2005/06/commons-logging-classloader-pain_18.html)

