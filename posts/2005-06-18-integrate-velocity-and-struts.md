% 实现Velocity和Struts的集成
% FuqiangWang

> 重新整理于2014年

<pre>
Velocity和Struts Framework都是Apache基金会Jakarta项目组中的开源项目，二者以其各自的特色赢得业界的肯定，前者以其强大的视图层模板功能，高效的开发效率等特色备受国外开发者的瞩目；后者也以其良好的架构，强大而设计合理的控制层实现而在国内国外广为流行。鉴于二者各自的实现侧重和优点，可以考虑结合二者来更为高效地开发架构良好且易于扩展的web应用，下面就让我们开始这两个Framework的集成之旅吧！

为什么要实现二者的集成？
 Velocity的优点：
作为一种通用的模板引擎，Velocity应用于许多用于对数据进行格式化并进行显示的java应用程序领域，或许以下几点可以是你选择Velocity的理由：
&#61548; 可以适应于许多应用程序领域的开发
&#61548; 为模板的设计者提供了一种简单而简洁的语法，大大减小了学习的难度
&#61548; 为程序员提供了一种简单的编程模型（将数据放入context然后使用相应模板格式化输出就可以了）
&#61548; 因为模板和相应的处理逻辑代码的开发是相互分离的，所以，可以对他们进行独立的开发和维护
&#61548; Velocity引擎可以简单的集成到任何的java应用环境，特别是servlet
&#61548; Velocity可以是模板访问context中的数据对象的任何公共方法
有关Velocity Frameworks的更多信息可以参考后面的Resources部分,因为无关本文的主题,所以这里不作赘述。
 Struts的优点：
从Struts1.0推出到现在的Struts1.1版本,Struts已经日趋完善,并且以其良好的架构和可扩展性等特色日益受到开发者和企业的重视,在Web应用领域应用越来越广,或许以下几点就是他如此流行的原因吧:
&#61548; 基于MVC体系架构进行设计实现,极大减少了各个组件之间的耦合性
&#61548; 倾向于Controller组件的实现框架,使其具有强大的流程控制功能
&#61548; 提供对I18N(国际化)的内在支持,提高整个Framework的适用性
&#61548; 提供的common Logging功能可以支持多种日志记录手段,包括JDK logging和Log4j等
&#61548; Struts1.1新加入的declarative式的异常处理功能,对异常处理提高了很好的灵活性
&#61548; 提供了很多功能强大的自定义标签库
&#61548; Struts1.1新加入的Validator机制,为Struts的客户端和服务器端数据验证提供了一种强大而灵活的手段
&#61548; 新支持的Tiles插件,为客户端视图的实现提供了新的选择
等等这些可能未能尽述,但应该说都是Struts现在得以如此流行的原因。有关Struts的更多信息请参考Resources部分。
 总结：一句话,使用Struts强大的控制功能和Velocity强大的视图模板功能就是我们希望将二者集成的最好原因。
如何实现二者的集成？
 原理：
要将Velocity集成到Struts框架中,首先需要对Struts框架进行分析。下面是Struts框架的整体略图(图1):
 
图1
 
可见, 在MVC系统架构的总体指导下, Struts为Model, Controller和View组件给出了一个清晰的实现。 在Struts框架中,视图部分的实现主要基于JSP技术,不管是自定义标签库还是Tiles等,都是在JSP的基础上实现的。因为我们要集成到Struts框架中的Velocity是强大的视图层实现框架,所以,我们会对Struts的View部分进行调整,以期Struts可以根据业务数据结合Velocity的模板进行格式化输出。

 对于JSP文件来说,通常是在底层由JSP Container对其进行编译成servlet, 在处理控制器发来视图格式化请求后,它会根据提供的业务数据结合JSP模板输出合适的视图界面。

 而对于Velocity框架来说,其模板是.vm后缀的文件名,为了使Struts能够将业务数据传给.vm模板文件并结合模板格式对数据进行格式化输出,原理跟JSP文件的处理相同,我们需要为.vm文件提供一个Handler, 对于所有由.vm处理的视图请求, 都需要通过该Handler进行处理。
 两个框架集成后的架构图描述如图2:
 
图2
 我们通过为.vm文件提供一个Handler来处理struts控制器发来的视图格式化请求,而且这种集成手段并不排斥JSP形式的视图手段,相反,它恰恰为原来的JSP视图手段提供另一种选择。所以,我们只需要实现一个Handler来处理.vm请求就可以将Velocity很好的集成到Struts Framework中去,并且通过该Handler, 还可以对Struts提供的所有资源进行访问。 那么如何来实现这个Handler那?实际上,我们无需自己实现这个Handler, Jakarta项目已经为我们提供了一个很好的实现----VeloctiyViewServlet。
  
 现有解决方案（VelocityTools）：
  Velocity在推出后,为更好的扩展Velocity在web应用领域和非web领域的应用,发起了一个子项目:VelocityTools子项目。
  VelocityTools的介绍
VelocityTools下设GenericTools, VelocityView和VelocityStruts三个工具集,而VelocityStruts工具集就是我们所需要的将Velocity集成到Struts Framework的工具集。该工具集主要是为了用来访问Struts框架所提供的各种资源。与在JSP中使用自定义标签来访问Struts的各种资源一样，VelocityTools提供一组View Tools来访问这些资源。View Tools只是一些具有public型的方法的java对象，在使用之前，这些tools将放入Velocity Context中，可以通过指定的标志键来访问这些工具，然后调用这些tools的方法。

在VelocityTools1.1中，主要包括7个主要的Tools，但是也有一个特殊的：SecureLinkTool，主要用来处理基于SSL协议的安全连接。下图给出了VelocityTools子项目各个工具集的结构情况(图3):
 
图3

  各个工具所实现的访问功能列表如下:
工具(Tool) 功能
ActionMessagesTool 用来处理Struts的Action Message,配置 scope为request
ErrorsTool 用来处理Struts的Error Messages,可以从message resources中的查找错误信息字符串,支持国际化信息,配置scope为request
FormTool 提供几种方法用来访问Struts应用的context中的formbeans
MessageTool 提供对Struts的国际化message resources的访问
StrutsLinkTool 提供对Struts的Actions和Forwards的访问支持
SecureLinkTool 提供对基于SSL协议的安全连接的支持,需要SSL Ext库的支持
TilesTool 提供能够跟Tiles进行交互的能力
ValidatorTool 提供能够跟Validator框架进行交互的能力,为表单验证生成动态javascript代码
 
为了使用VelocityStruts提供的各种工具来访问Struts框架的各种资源,使用VelocityView工具集提供的VelocityViewServlet来初始化VelocityStruts提供的工具集,并为以后的访问提供支持。
VelocityViewServlet扩展自HttpServlet, 在进行请求处理之前,需要进行资源的初始化操作。在其init()方法中,VelocityViewServlet分别调用initVelocity()和initToolbox()方法读取velocity.properties文件和toolbox.xml文件的内容对INIT_PROPS_KEY和TOOLBOX_KEY进行初始化,同时设置请求的ContentType和encoding。在此之后, VelocityViewServlet在处理所有.vm请求的时候就可以直接从Velocity Context中取得各个Tools来访问Struts框架提供的各种资源。
每一个由VelocityViewServlet来处理.vm请求都通过VelocityViewServlet的doRequest(request, response)方法进行,该方法通过将从context中取得的数据和提供的模板相结合为请求生成合适的视图。
VelocityViewServlet的类图简单表示如下(图4):
 
图4
 纵上,我们通过VelocityTools提供的VelocityViewServlet和VelocityStruts的各个工具集可以将Velocity很好的集成到Struts Framework中.
 下面让我们看看具体是如何做到的:
 实现集成：
  配置
   要使用Velocity Tools，我们需要首先配置它，下面是需要进行的配置：
&#61548; 下载Velocity Tools，笔者下载的是1.1rc版本，将含有VelocityStruts和VelocityView工具java类的jar文件放到web应用的WEB-INF/lib目录下；
&#61548; 下载Velocity，将Velocity-dep-1.3.1.jar文件添加到web应用的WEB-INF/lib目录下面；
&#61548; 在Web应用的部署描述文件web.xml中登记VelocityViewServlet类，为该servlet配置相应的参数，即org.apache.velocity.properties和org.apache.velocity.toolbox参数，下面是该类在web.xml文件中注册的示例片断：
<servlet>
    <servlet-name>velocity</servlet-name>
 <servlet-class>org.apache.velocity.tools.view.servlet.VelocityViewServlet</servlet-class>
    <init-param>
      <param-name>org.apache.velocity.properties</param-name>
      <param-value>/WEB-INF/velocity.properties</param-value>
    </init-param>
    <init-param>
      <param-name>org.apache.velocity.toolbox</param-name>
      <param-value>/WEB-INF/toolbox.xml</param-value>
    </init-param>
    <load-on-startup>10</load-on-startup>
  </servlet>
 为了使得所有的vm请求都交由该servlet处理，还需添加以下元素：<servlet-mapping>
     <servlet-name>velocity</servlet-name>
     <url-pattern>*.vm</url-pattern>
    </servlet-mapping>
&#61548; Toolbox.xml文件必须添加到应用中，一般放在WEB-INF目录下面，toolbox配置内容可以参考后面的Resources提供的实例文件；
&#61548; 添加velocity.properties文件到应用中，虽然VelocityTools官方网站上强调必须将该文件添加进来，但是，笔者认为这一步是可选的，因为如果不指定的话，velocity会使用默认的属性配置文件，这也可以从VelocityViewServlet的源代码中看出。
通过以上配置，集成环境搭建完成。
  实例
在此实例中,提供一个简单的信息编辑流程的实现。与纯粹的基于Struts的解决方案不同的是,我们的视图实现除了入口index.jsp使用jsp实现外,其他两个视图都是使用.vm格式实现,而且他们可以很好的工作,这也说明了上面的结构图,即二者并非排斥,而是可以一起很好的工作。
 
以上是给出的该实例基本流程, index.jsp为整个实例的入口,连接提交给AddressAction处理,在AddressAction中将原始数据放入AddressBean然后页面重定向到showAddress.vm文件,该文件显示信息列表,如果要编辑信息,点击连接后通过ForwardAction将页面重定向到editAddress.vm,在此可以对信息进行编辑,编辑完成后提交表单给EditAddressAction处理,处理完成后重新将页面定向到showAddress.vm文件显示编辑后的信息。
与完全基于Struts的解决方案不同的是, Forward的目的地都是.vm文件,Struts-config.xml文件的内容片断如下:
  <form-beans>
    <form-bean name="addressActionForm" type="org.dwstudio.velstruts.forms.AddressActionForm" />
  </form-beans><global-forwards><forward name="showAddress" redirect="true" path="/addressapp/showAddress.vm" contextRelative="true" /></global-forwards>
  <action-mappings>
    <action type="org.dwstudio.velstruts.actions.AddressAction" scope="request" path="/addressAction">
      <forward name="showAddress" path="/addressapp/showAddress.vm" redirect="false" />
    </action>
    <action type="org.apache.struts.actions.ForwardAction" parameter="/addressapp/editAddress.vm" path="/toEditAddressAction" />
    <action name="addressActionForm" type="org.dwstudio.velstruts.actions.EditAddressAction" validate="false" input="editAddress.vm" scope="request" path="/editAddressAction" />
  </action-mappings>
<message-resources parameter="org.dwstudio.velstruts.VelStrutsResource" />
在页面中,我们使用VTL(Velocity模板语言)对数据进行格式化输出,以showAddress.vm文件为例:
<html>
<head>
<title>My first Velocity and Struts Integration Application!</title>
</head>
<body>
here is some information from Address in session!
<table>
 <tr>
  <td>Title of AddressInfo</td>
  <td>$!Address.title</td>
 </tr>
 <tr>
  <td>Publisher</td>
  <td>$!Address.publisher</td>
 </tr>
 <tr>
  <td>Email of Publisher</td>
  <td>$!Address.email</td>
 </tr>
 <tr>
  <td>Content of AddressInfo</td>
  <td>$!Address.content</td>
 </tr>
 <tr>
  <td>$text.get("foreignLanguage")</td>
  <td>
  #foreach($item in $!Address.foreignLanguage)
   &nbsp;$!item<br>
  #end
  </td>
 </tr>
</table>
<br>
<a href="$link.setAction("toEditAddressAction.do")">To Edit the Address>>>!</a>
</body>
</html>
在这里我们直接使用VelocityTools提供的工具访问Struts提供的资源,与JSP相比,使用VTL对于程序员来说更具亲和力,而且模板也看起来更简洁。
整个实例的Web App已经打包在Resources部分,可以下载并供参考。
总结
本文中,我们对于如何将Velocity和Struts这两个Framework集成到一起做了较为全面的阐述,从两个Frameworks的优点,到实现集成的原理以及现有解决方案,最后,在给出如何配置集成所需要的开发环境的集成上给出了一个简单的实例。
通过将Velocity和Struts两个Framework进行集成之后,可以极大的分离业务逻辑和试图逻辑的开发工作,程序员和页面设计者可以在约定好访问接口之后,互补干扰的进行开发工作,极大的提高开发效率。只要试过,我相信你就会喜欢这种方式。
参考资料（Resources）
&#61548; 《VelocityStruts  User  Guide》，来自VelocityTools官方网站
http://jakarta.apache.org/velocity/tools/struts/userguide.html
&#61548; 《Client and server-side templating with Velocity》，by Sing Li 
From IBM developerworks网站
http://www-106.ibm.com/developerworks/library/j-velocity/
&#61548; 《Start up the Velocity Template Engine》by Geir Magnusson Jr.
From  Java World网站
&#61548; 有关Struts Framework的更多信息，可以参考Jakarta项目组Struts项目的相关资料
&#61548; 可以访问Jakarta项目组的官方网站了解有关Velocity Framework的更多信息
&#61548; Toolbox.xml文件实例下载
&#61548; 整个WebApp的源代码下载
</pre>