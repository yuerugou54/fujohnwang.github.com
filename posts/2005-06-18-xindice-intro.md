% Xindice随笔
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

作为本地xml数据库的一种，xindice遵循XML:DB API标准，给出了相当于Core Level 1水平的API功能实现。XML:DB API之相对于NXD（Native XML Database）来说，就相当于JDBC之相当于RDBMS。

通过xindice，我们可以通过他来保存xml文档数据，通过其提供的各种服务来对文档数据进行管理。其中，我们可以通过XPathQueryService服务来查询xml数据库中的xml文档数据，通过XUpdateQueryService服务来对xml进行更新，等等。

下面只是简单罗列一些XUpdateQueryService提供的XUpdate命令，处于时间考虑，如果有时间，可以写一些有关xindice本地xml数据库的更详细的文档，另外，developerworks网站有一篇说明xindice的介绍性质的文章，如要参考，可以search一下（在xml section）。

<pre>
[b]XUpdate Commands[/b]
xupdate:insert-before Inserts a new node in document order before the selected node
xupdate:insert-after Inserts a new node in document order after the selected node
xupdate:update Replaces all child nodes of the selected node with the specified nodes
xupdate:append Appends the specified node to the content of the selected node
xupdate:remove Remove the selected node
xupdate:rename Renames the selected node
xupdate:variable Defines a variable containing a node list that can be reused in later operations

[b]XUpdate Node Construction [/b] 
xupdate:element Creates a new element in the document
xupdate:attribute Creates a new attribute node associated with an xupdate:element
xupdate:text Creates a text content node in the document
xupdate:processing-instruction Creates a processing instruction node in the document
xupdate:comment Creates a new comment node in the document
</pre>


下面是xindice提供的文档中给出的一个简单的XUpdate命令实例：

~~~~~~~ {.xml}
Basic XUpdate Insert Command

<xupdate:modifications version="1.0"
        xmlns:xupdate="http://www.xmldb.org/xupdate">
   <xupdate:insert-after select="/addresses/address[1]" >
      
      <xupdate:element name="address">
         <xupdate:attribute name="id">2</xupdate:attribute>
         <fullname>John Smith</fullname>
         <born day=''2'' month=''12'' year=''1974''/>
         <country>Germany</country>
     </xupdate:element>
   </xupdate:insert-after>
</xupdate:modifications>
~~~~~~~

在程序中，可以通过以下类似代码调用该XUpdate命令对文档进行更新：

~~~~~~~ {.java}
String xupdate = 刚才的XUpdate命令内容；
XUpdateQueryService service =(XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
service.update(xupdate);
...
//其他操作，比如释放资源
~~~~~~~

因为更新命令中包括了insert，delete和update等功能，所以，其他的管理可以以上面类似的形式实现，通过为XUpdateQueryService提供相应的XUpdate命令就可以了。
至于查询操作，步骤类似，只要从Collection中取得XPathQueryService服务，然后为其指定查询用的Xpath表达式，最后对结果（Resource）进行处理。

Xindice发布包中附带有一个AddressBook应用，以用来掩饰基于Xindice的web应用实现模型，但其提供的Ant编译脚本有问题，没有指定编译时候的classpath，所以，在现在的机器上不能编译成功，最后，通过为该脚本添加了以下内容才使编译通过：

即在build.xml文件开始添加


~~~~~~~ {.xml}
<path  id="project.classpath">
   <fileset dir="E:\APIs">
     <include name="**/*.jar"/>
   </fileset>  
   <fileset dir="E:\xindice\java\lib">
     <include name="**/*.jar"/>
   </fileset>   
 </path>
~~~~~~~

然后在compile目标的javac任务中为其指定classpathref为project.classpath即可，编译完成后生成Addressbook.war文件，可以部署到App Server（我部署在resin2.1.1 App服务器上）。
稍后会考虑使用struts对该Webapp进行重构，因为相对于现在基于Command模式实现的实例，struts能更清晰的表达控制流程。






