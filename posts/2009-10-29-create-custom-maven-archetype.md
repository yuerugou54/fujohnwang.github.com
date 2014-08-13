% 自定义Maven archetype的创建

以下只是Maven自定义Archetype创建的简单流程以及期间可能碰到的问题的一些整理性内容，大部分内容从Maven的官方站点或者相关书籍中都可以找到。

关于Archetype是个什么东西，我想用过Maven的都不用说了， 没用过的也没关系， 简单来讲， 就是一堆预先设定好的项目结构文档， 有了这个东西，在新建同一类型的项目的时候，就不用又从头捣鼓一遍， 比如，从这个项目抓个log4j.xml改一下，到那个项目抓一个web.xml文件改一下等等。预先建立一个针对某种场景下使用的archetype， 用的时候只需要运行一条命令就所有东西都备齐了。就是这么个简单的东西， appfuse啦， springside啦，应该都类似于这种东西，体现形式不同而已。

# 创建Maven自定义Archetype一般流程

创建Maven自定义Archetype通常有两种方式， 为了便捷，我们选择create-from-project的方式，所以， 首先，我们会新建一个maven project，然后把杂七杂八的那些共用的东西都“摆放 ”在合适的位置上，这可能包括：

1. 将某些实例用Java代码放到src/main/java目录下；
2. 某些通用的配置文件放到src/main/resources目录下；
3. 如果是为基于j2ee的web应用搭建archetype，那可能又得在src/main/webapp下创建相应的文件和目录结构，等等...

当你感觉所有这些东西都准备齐活之后，就可以开始最后的工序了。

首先， 要创建archetype，当然是要先把maven的archetype plugin添加到pom.xml中啦：

~~~~~~~ {.xml}
<plugins>  
    <plugin>  
        <groupId>org.apache.maven.plugins<groupId>  
        <artifactId>maven-archetype-plugin<artifactId>  
        <version>2.0-alpha-4<version>  
    </plugin>  
...  
</plugins> 
~~~~~~~

之后， 就可以开个命令行窗口，进入当前project所在目录，敲下：

<pre>
$ mvn archetype:create-from-project 
</pre>

之后， 创建好的archetype就跑到了当前目录下的次一级目录target/generated-sources/archetype/ ，所以，我们接着敲键盘：

<pre>
$ cd target/generated-sources/archetype/  
$ mvn install
</pre>

将该创建好的archetype安装到本地的repository之后， 我们就可以使用它了：

<pre>
$ mvn archetype:generate -DarchetypeCatalog=local  
</pre>

如果使用m2eclipse，那在创建项目的时候，选择使用Default Local的Catalog，应该也可以看到并选择新安装到这个archetype。
安装到本地的repository只能自己用，所以，如果是比较通用的archetype，可以deploy到组织内部架设的maven repository服务上去。

# 可能遇到的一些问题

直接使用archetype:create-from-project 比起手工去打造一个archetype要便捷的多，但， 有时候偷懒会有些不靠谱，archetype:create-from-project 的某些行为可能会与我们最初的设想有出入，这个时候就需要我们在最终发布新创建的archetype之前，做有些后继处理，比如：

## Java文件对应的package被忽略

对于要包含到archetype的Java代码文件来说， archetype:create-from-project 之后， Java代码文件原来的package结构会被忽略掉，比如，对于如下类来说：`cn.spring21.app.defaults.controller.QuickStartController`

当archetype:create-from-project 之后， archetype中对应的文件就变成了： `QuickStartController`

这显然不是我们想要的结果（当然，对于不在乎这一结构的archetype来说则无所谓），为此，我们要对archetype:create-from-project 之后生成的结果文件进行定制。

首先， 我们编辑文件target/generated-sources/archetype/src/main/resources/META-INF/maven/archetype.xml ，它记载了archetype里都包含哪些文件和目录结构，将如下内容：

~~~~~~~ {.xml}
<sources>  
  <source>src/main/java/QuickStartController.java</source>  
</sources> 
~~~~~~~

变更为:

~~~~~~~ {.xml}
<sources>  
  <source>src/main/java/cn/spring21/app/defaults/controller/QuickStartController.java</source>  
</sources>  
~~~~~~~

然后，将以上文件和目录结构创建或者Copy到如下目录：

> target/generated-sources/archetype/src/main/resources/archetype-resources/src/main/java

所有这些做完之后，Java源代码文件结构就可以保持了。

### NOTE


虽然想要的Java源代码目录结构可以保持住了，不过， 还会有一个烦人的地方，如果我们在使用这个archetype创建新的project的时候，不明确指定packageName ，那maven会“自作多情 ”的把groupId作为Java文件的package，前缀到原来的package前面。 比如， 如果我原来java文件对应：`cn/spring21/app/defaults/controller/QuickStartController.java`

而创建项目的时候， 指定“-DgroupId=cn.spring21 ”，那么，最后生成的项目中， 对应的Java文件就跑到了“cn/spring21/cn/spring21/app/defaults/controller/QuickStartController.java ”，stupid哈

要解决这个问题， 可以在使用archetype创建project的时候，明确将packageName指定为空：

> mvn archetype:create -DarchetypeGroupId=archetypeGroupId -DarchetypeArtifactId=archetypeArtifactId -DarchetypeVersion=0.0.2-SNAPSHOT -DgroupId=cn.spring21 -DartifactId=sample -Dversion=0.0.1-SNAPSHOT -DpackageName=

唯一不爽的就是命令行实在实在太长了。

## 多余文件的清理

有时候， archetype:create-from-project 会把一些不必要的文件和目录结构也包含到最终的archetype中， 我们可以通过编辑文件target/generated-sources/archetype/src/main/resources/META-INF/maven/archetype-metadata.xml 来清理这些不必要包含的文件和目录结构。

比方说，如果我们是使用EclipseIDE来搭建要做成archetype的project的话，一些EcilpseIDE特有的文件和目录，比 如.settings目录啦， .classpath/.project文件啦，都会被copy到archetype的目录下，这时候，我们就可以把archetype- metadata.xml文件中对应这些文件和目录的<fileset>元素删除掉来清理它们。

## 二进制文件的损坏
如果某些二进制文件要包含到archetype当中， 比如， 常见的图片文件， 我们需要编辑archetype.xml文件中对应这些文件的<resource>元素配置，为这些元素添加filtered="false" 的属性， 否则的话， 当使用这个archetype来创建新的工程的时候，这些二进制文件就会“废废 ”了，原因嘛，还是因为maven太“自作多情 ”，呵呵，居然把图片当Velocity模板来看待。
有关这个问题，也可以参考这个JIRA issue 来了解更多细节。

# 最初草稿

since the "archetype:create-from-project" is the simplest way to create an archetype, most of the time, it's the way we will resort to , but in order to make it work for us properly, or say, to make it work as we want it to, we need to do some custom work after running the command "mvn archetype:create-from-project", here is some tips you may need.

> NOTE
> 
> first of all, we should come to the same point that all of the artifacts generated locate at "target/generated-sources/archetype" folder after running "mvn archetype:create-from-project" command.

---

for java code we want to include into the archetype, the default behavior from "archetype:create-from-project" is that it will ignore the package structure, e.g. if we have a source folder with package structure , below:
cn.spring21.app.defaults.controller.QuickStartController
but with the default behavior from "archetype:create-from-project", the final structure will be only java file left. that's,
QuickStartController
that's not what we want, we want to keep the package structure as it is. so we need to customize this.
go to "target/generated-sources/archetype/src/main/resources/META-INF/maven/archetype.xml", change following part:

~~~~~~~ {.xml}
<sources>
  <source>src/main/java/QuickStartController.java</source>
</sources>
~~~~~~~

TO:

~~~~~~~ {.xml}
<sources>
  <source>src/main/java/cn/spring21/app/defaults/controller/QuickStartController.java</source>
</sources>
~~~~~~~

then, copy or create such folder structure under :

> target/generated-sources/archetype/src/main/resources/archetype-resources/src/main/java

after all of this, the original folder structure will be retained.

> NOTE
> 
> when creating a project with our archetype above, in order to create a source folder with default package we assigned, the "packageName" environment attribute must be assigned to empty, so command seems like : `mvn archetype:create -DarchetypeGroupId=archetypeGroupId -DarchetypeArtifactId=archetypeArtifactId -DarchetypeVersion=0.0.2-SNAPSHOT -DgroupId=cn.spring21 -DartifactId=sample -Dversion=0.0.1-SNAPSHOT -DpackageName=`, pay attention to the last -DpackageName= , no value is assigned.


---

sometimes, "archetype:create-from-project" will include other unnecessary filess into the final package, we can clean this by editing "target/generated-sources/archetype/src/main/resources/META-INF/maven/archetype-metadata.xml", for example, if we set up a project for archetype in Eclipse IDE, several files specific to Eclipse IDE will be included, these files can be removed by deleting "fileset" elements which are setup for them.

---

for binary files, e.g. images, we need to edit archetype.xml entry for these resources, and add "filtered="false"" attribute to their <resource> elements. Otherwise , when create a project with this archetype, the binary files will be corrupted. Because velocity try to process it.
See JIRA issure from http://jira.codehaus.org/browse/ARCHETYPE-19 for this problem.


