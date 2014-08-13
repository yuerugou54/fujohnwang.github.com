% Struts中的文件上传
% FuqiangWang
% 2005-06-18

Keywords:  Struts，upload，controller，上传文件长度限制

> NOTE: 2014年重新从msn space存档中恢复，可能存在格式错误

# 前言
在以前的文章中，我们提到struts对于文件上传有其自己的一套实现方案，这套方案对文件的上传操作进行了进一步的程序实现复杂度的封装，更加易于操作，可以极大的提高开发效率。
 
下面，就让我们来领略一下他到底是一个怎么样的实现。希望在读完本文之后，读者可以充分了解struts的文件上传功能的实现流程以及原

# 技术背景

 除去其他技术对于文件上传的实现技术不谈，但就java的web应用中文件上传的方式或者说实现上看，在struts提供的文件上传实现之前，一般有两种方式可以供开发者选择：

1.  第一种：从最底层的HTTP协议出发，使用手动编码对HTTP协议的request进行分析，根据分析所取得的协议的request内容，取得用户提交的表单中各个表单域的数据，包括普通的文本以及二进制形式的文件形式等。数据取得后，可以在服务器端进行所需要处理的操作。这种方式的缺点就是重复开发，一切编码都要从零开始，需要什么形式的上传请求，就针对该请求进行重新开发，不能保证开发效率。优点嘛，当然也有，那就是可以是开发者更加清晰的了解协议的规格以及具体实现，因为，只有清楚的了解了这些，才能开发出能对协议中request的内容进行正确解析的组件。
2. 第二种嘛，实际上只是本人所划分的，可以说成是在第一种的基础上进行的改进，特点就是，所有的上传实现都已经开发并实现为组件的形式，一般是javabeans的形式。用户只需要调用各个厂商提供的文件上传组件就可以了，其中使用最多的就是JspSmartUpload。缺点嘛，目前还不能说有什么缺点，因为这种方式将所有的实现逻辑都封装起来，可以极大的提高软件的复用度。实际上，struts提供的文件上传服务也要归到这一类。

实际上，在Struts架构中，完全可以使用JspSmartUpload之类的组件库来实现文件上传，但是，唯一的区别就是，Struts中的文件上传功能可以与struts架构更加紧密的结合。既然选择了使用struts架构进行开发，为什么还要排斥他提供的文件上传功能那？

# 安装和配置

要使用struts的文件上传功能，其实不用任何多余的文件安装或者是配置，只要你已经成功的配置好了struts1.1的开发环境。因为org.apache.struts.upload包已经集成到struts的类库中。下面是Struts的一般配置步骤：

1. 从Jakarta的官方网站下载Struts1.1的zip压缩包（当然是指windows平台下，其他平台可以下载相应的文件压缩包）
2. 拷贝压缩包中lib目录下的所有jar压缩包到欲使用Struts进行开发的web应用的\WEB-INF\lib目录下面。同样拷贝压缩包中lib目录下的*.tld文件到web应用的\WEB-INF目录下面，这些是标签库的索引文件。如果使用IDE，比如Jbuilder等，其实只要在新建Web Application的时候选择使用Struts架构进行开发的话，这些东西都会自动添加到该web应用的相应目录下方。
3. 在web.xml部署描述符中添加一下内容：

~~~~~~~ {.xml}
<servlet>
    <servlet-name>action</servlet-name>
    <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
    <init-param>
      <param-name>config</param-name>
      <param-value>/WEB-INF/struts-config.xml</param-value>
    </init-param>
    <init-param>
      <param-name>debug</param-name>
      <param-value>2</param-value>
    </init-param>
    <init-param>
      <param-name>detail</param-name>
      <param-value>2</param-value>
    </init-param>
    <load-on-startup>2</load-on-startup>
  </servlet>
//----------以上注册ActionServlet---------------------------------
<taglib>
    <taglib-uri>/tags/struts-bean</taglib-uri>
    <taglib-location>/WEB-INF/struts-bean.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>/tags/struts-html</taglib-uri>
    <taglib-location>/WEB-INF/struts-html.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>/tags/struts-logic</taglib-uri>
    <taglib-location>/WEB-INF/struts-logic.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>/tags/struts-nested</taglib-uri>
    <taglib-location>/WEB-INF/struts-nested.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>/tags/struts-tiles</taglib-uri>
    <taglib-location>/WEB-INF/struts-tiles.tld</taglib-location>
  </taglib>
//-------------以上注册Struts标签库------------------------
~~~~~~~

4. 最后，在WEB-INF目录下面构建Struts-config.xml文件，将开发中所需要注册的资源添加进去就可以了。

至此，就可以着手进行文件上传的开发工作，不过，在此之前，让我们先了解一下在struts中进行文件上传的基本流程或者说简单原理。

# 原理及流程

文件上传的页面Form表单与其他的普通的Form表单有些不同，其中文件上传页面中定义的表单的enctype属性必须设置为multipart/form-data。虽然如此，但是对于表单的处理来说，使用struts进行处理依然是使用同一种处理方式，ActionForm传送数据到Action进行处理，可以说是以不变应万变！那么，具体是怎么一回事那？
 
在页面的表单中定义文件上传输入域，名称指定为符合规格的形式，比如定义一个文件输入域如下：<html:file property="fileOne"/>。其他表单域类似定义。然后，编写与此表单相对应的ActionForm类，其定义的所有属性都要与表单中定义的表单域一一对应，名称相同，而且该ActionForm必须符合JavaBeans规范，为各个属性定义相应的setter和getter方法。只不过，对于与文件输入域相对应的属性，其类型现在必须定义为org.apache.struts.upload. FormFile接口类型，而且相对应的setter和getter方法的参数以及返回类型也是一样处理。这些完成后，将该ActionForm注册到struts-config.xml文件中，然后开始编写用来处理请求的Action。在这里，Action取得上面的ActionForm，然后，象取得其他普通类型的属性一样取得FormFile类型的属性，然后，根据逻辑处理要求就可以对FormFile象文件一样进行操作了，具体可以参考FormFile接口的API参考。
 
以上就是struts中进行文件上传的基本流程。


# 简单实例实现
我们实现一个简单的文件上传流程，在Upload.jsp页面中定义一个文件上传表单，定义一个文本域，一个文件输入域，分别代表上传者姓名和所上传的文件。Upload.jsp页面的主体实现代码如下：

~~~~~~~ {.xml}
<html:form action="uploadAction.do" method="post" enctype="multipart/form-data">
To choose the File to be Uploaded:
<br><html:text property=”uploader”/>
<br><html:file property="fileOne"/><br>
<html:submit/><html:reset/>
</html:form>
~~~~~~~

页面完成后，需要定义与之对于的ActionForm，在应用中新建一个ActionForm，命名为UploadForm，定义uploader属性为String型，fileOne属性为FormFile型。与各个属性相对应的setter和getter方法以规范实现，这里不作赘述。代码可以参考附带文件。

ActionForm开发完成后，将其添加到struts-config.xml配置文件中以便于Action调用。在struts-config.xml文件中的< form-beans >元素下面添加以下内容：


~~~~~~~ {.xml}
<form-bean name="uploadForm"
             type="com.darrenstudio.guestbook.forms.UploadForm"/>
~~~~~~~

以上完成后，就可以开始进行处理上传请求的Action的开发了。我们定义Action类为UploadAction，扩展自org.apache.struts.action.Action类。在该Action中，我们通过取得ActionForm，然后判断，如果是UploadForm则进行逻辑操作，从UploadForm实例中取得上传者姓名以及上传的FormFile实例。然后，通过FormFile提供的处理方法，将FormFile实例所代表的文件另存为服务器上的一个同名文件中。最后，处理完成后将页面forward到UploadResult.jsp页面，显示上传信息，如果出错，则forward到error处理页面。UploadAction的部分代码实现如下，完整代码可参照附带源码文件：

~~~~~~~ {.java}
if (form instanceof UploadForm) {
UploadForm theForm = (UploadForm) form;
String uploader = theForm.getUploader();
FormFile file = theForm.getFileOne();
String BasePath = System.getProperty("user.dir")+File.separator;
InputStream ins = null;
OutputStream out = null;
try{
byte[] buffer = new byte[1024];
int bufferRead = 0;
if(!(file.getFileName()==null||file.getFileName().equals("")))
            {
                ins = file.getInputStream();
                out = new FileOutputStream(BasePath +
                                           EncodingBean.ISO2GBK(file1.
                    getFileName()));
                while ( (bufferRead = ins.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, buffer.length);
                }
                ins.close();
                out.close();
            }
file.destroy();
}catch(Exception e)
{
     e.printStackTrace();
     return actionMapping.findForward("failure");
}
return actionMapping.findForward("success");
}return null;   // will never happen in this sample
~~~~~~~

代码完成之后，将该Action类，即UploadAction，添加到struts-config.xml文件中，在struts-config.xml文件中添加以下内容：


~~~~~~~ {.xml}
<action name="uploadForm"
          type="com.darrenstudio.guestbook.actions.UploadAction"
          validate="false"
          input="/upload.jsp"
          scope="request"
          path="/uploadAction">
   <forward name="success"
            path="/UploadResult.jsp"
            redirect="false"/>
   <forward name="failure"
            path="/ErrorPage.jsp"
            redirect="false"/>
</action>
~~~~~~~

需要注意的是：action的name属性应该与上面定义的FormBean的name属性的值一样，在这里都是“uploadFrom”。这里，还为这个action定义了两个Forward，即success和failure，当处理顺利完成后，forward走success；否则，走failure报错。

处理如果顺利完成，则页面跳转到UploadResult.jsp,在这里显示上传后的定制显示信息。而且，为了用户重新上传，还在这个页面中添加了一个重新到upload.jsp的link。该页面部分代码如下所示：

~~~~~~~ {.xml}
<pre>  
 <font color="red">Files Uploaded Successfully!</font>
 Uploader : <%=uploader%>
 FileName : <%=FileName%>
 FileLocationOnServer:<%=FilePath%>
</pre>
<br>
<html:link href="return2Upload.do">返回上传页面</html:link>
~~~~~~~

为了将连接归入struts架构下，为其在Struts-config.xml文件中添加一个action，内容如下：

~~~~~~~ {.xml}
<action path="/return2Upload"
          parameter="/upload.jsp"
          type="org.apache.struts.actions.ForwardAction"/>
~~~~~~~

关于ForwardAction的使用，在以前的《》一文中已经提到过。

至此，整个上传流程的开发完成，只要将该Web应用部署到相应的应用服务器上的规定目录下面就可以运行了。

# 深入阐述

在org.apache.struts.upload包中，有两个主要的接口：FormFile接口和MultipartRequestHandler接口。

前者在上面的实例中已经接触过，它代表从客户端上传上来的文件，也是在struts应用中在上传文件方面使用最多的接口或者说是类，通过它提供的各种文件操作方法，我们可以对于应用中它所代表的文件进行各种所需要的操作，比如取得从客户端上传的文件的文件名，或者取得文件大小，进而可以取得输入流，对该文件进行操作等，这在上面的实例中都可以涉及到。

后者，即MultipartRequestHandler接口，为struts应用提供一个标准接口，用来处理表单的enctype属性为“multipart/form-data”的文件上传操作。这个接口有一个ATTRIBUTE_MAX_LENGTH_EXCEEDED域，通过这个域，我们可以对客户端上传的文件的大小进行控制。下面，我们就对于如何控制客户端上传文件的大小进行简单描述。
在通常的web应用中，如果允许文件上传的话，也不是可以任意上传的，一般会对客户端上传的文件大小进行适当的限制，以便服务器可以负担，因为文件上传是很耗服务器资源的一件事情。

在struts中对文件的上传进行控制需要进行一下几个步骤:

1. 在struts-config.xml文件中注册一个controller，通过其maxFileSize属性设置对文件大小进行的限制；
2. 在FormBean的validate（）验证方法中进行检查，判断上传的文件大小是否超出controller所设置的大小，如果超出，则向ActionErrors中添加一个ActionError，然后返回input指定的页面；否则，返回的ActionErrors对象为空，处理交给Action处理；
3. 如果文件大小超出限制，需要在原上传页面中对文件没有允许上传进行信息提示。

下面就是在各个步骤中所涉及的具体实施内容：

1. 向struts-config.xml文件中添加以下内容即可完成Controller对象的注册工作：

~~~~~~~ {.xml}
<controller maxFileSize="2M" />
~~~~~~~

<pre>
注意，controller元素的父元素是<struts-config>,你可以在<struts-config>下面注册多个controller分别处理不同逻辑。
<controller>元素有多个属性，其中针对文件上传进行控制的属性只有bufferSize，maxFileSize，multipartClass和tempDir。
>bufferSize属性代表处理文件上传的文件缓冲区的大小，这个属性是可选的，默认的大小是4096；
>maxFileSize属性指定文件上传中所允许的最大上传文件大小，可以在数字后面添加“K”，“M”，“G”等来表明文件的大小数量级。也是可选属性，默认为250M，如果设置为-1，则表示对上传的文件大小没有限制。但是因为我们要对文件大小进行限制，所以，上面指定最大上传文件大小为2M。
>multipartClass属性指定被controller使用的MultipartRequestHandler类的全名，也是可选属性，默认为org.apache.struts.upload.CommonsMultipartRequestHandler。
>tempDir属性指定处理文件上传时候使用的临时工作目录，同样是可选属性。
</pre>

2. 在ActionForm的validate（）方法中进行文件大小是否超出规定大小进行检查，那么如何知道文件是否超出规定大小那？具体实现是这样的：


~~~~~~~ {.java}
ActionErrors errors = null;
Boolean maxLengthExceeded = (Boolean)            request.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue()))
{
    errors = new ActionErrors();
    errors.add(“FileSizeExceeded”, new ActionError("maxLengthExceeded"));
}
return errors;
~~~~~~~

在该章开始我们就提到过，我们是通过MultipartRequestHandler类的ATTRIBUTE_MAX_LENGTH_EXCEEDED域进行文件大小限制的。当读入一个multipart类型的request并且上传的文件最大长度超出实现规定的话，ServletRequest的这个属性将会被设置，否则，如果文件长度没有超出规定，则这个属性就不会被设置，该属性返回类型为Boolean型。所以，从上面的代码中我们可以看出，我们可以通过取得request的这个属性，然后判断其是否为空，并且其返回的对于boolean值是否为true来判断文件是否超出规定长度。如果，判断得到超出规定长度，则向ActionErrors对象中加入文件长度超出限制的ActionError；否则，直接就是返回null的ActionErrors对象。可以看出，文件长度比较等逻辑实现复杂度在struts中都已经隐藏了。

3. 如果文件超出规定长度，需要在原来的上传初始页面中提示错误信息，下面是struts的sample中提供的代码实现，但不是唯一的：


~~~~~~~ {.xml}
<logic:present name="<%= Action.ERROR_KEY %>" scope="request">
    <%
        ActionErrors errors = (ActionErrors) request.getAttribute(Action.ERROR_KEY);
        //note that this error is created in the validate() method of UploadForm
        Iterator iterator = errors.get(UploadForm.ERROR_PROPERTY_MAX_LENGTH_EXCEEDED);
        //there''s only one possible error in this
        ActionError error = (ActionError) iterator.next();
        pageContext.setAttribute("maxlength.error", error, PageContext.REQUEST_SCOPE);
    %>
</logic:present>
<!-- If there was an error, print it out -->
<logic:present name="maxlength.error" scope="request">
    <font color="red"><bean:message name="maxlength.error" property="key" /></font>
</logic:present>
<logic:notPresent name="maxlength.error" scope="request">
    Note that the maximum allowed size of an uploaded file for this application is two megabytes.
    See the /WEB-INF/struts-config.xml file for this application to change it.
</logic:notPresent>
~~~~~~~

至此，对于上传文件大小进行限制的实现就可以完成了。

对于org.apache.struts.upload包中的其他类，如果读者感兴趣，可以参考Struts官方网站所提供的API参考。

# 可能遇到的问题

实际上，使用struts提供的文件上传十分简单，根本无需太多关注具体实现的逻辑复杂度。所以，可能遇到问题应该很少。笔者初次使用的时候遇到的问题就是忘记指定上传表单的enctype属性的值，也就是应该在jsp页面的将表单的enctype设定为“multipart/form-data”类型，希望读者不会犯与笔者同样的错误。

另外，笔者在此之前在网上看到有的人会问对于多个文件在struts中如何上传，其实这也不是十分复杂，只需要扩展上面的例子就可以了。在表单中添加其他的需要数量的文件输入域，指定合适的名称。在相应的ActionForm中为这些文件输入域定义与之对应的属性和setter，getter方法，当然属性类型为FormFile，方法的参数和返回类型同样。剩下唯一需要做的就是在Action中取得ActionForm的实例，使用ActionForm取得各个已经上传的文件，然后根据需要进行处理。唯一需要注意的就是处理多个文件上传时候，用户可能不会选择全部的文件输入域，所以要对空的属性进行判断，以免程序存在缺陷。

# 结束语

至此，针对struts中如何实现文件上传的内容阐述就结束了。

由于笔者对于struts还是初次接触，所以有些方面还不是十分的了解，如果本文中有什么不妥之处，还希望读者热心指正。也希望本文所谈到的内容能够为大家提供一定的帮助。

# 参考文献
1. Apache Jakarta Struts User Guide
2. Struts Upload API Reference
3. Struts-upload.war附带文档以及源码









