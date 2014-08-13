% Struts实现探索
% 王福强

> 2014年从msn space存档中重新恢复出来！

Struts是一个基于Sun J2EE平台的MVC框架，主要是采用Servlet和JSP技术来实现的。现在，Struts是Apache软件基金会旗下Jakarta项目组的一部分。Struts能充分满足应用开发的需求，简单易用，敏捷迅速。Struts把Servlet、JSP、自定义标签和信息资源(message resources)整合到一个统一的框架中，开发人员利用其进行开发时不用再自己编码实现全套MVC模式，极大的提高开发效率，是一个非常不错的应用框架。采用Struts能开发出架构良好的基于MVC(Model-View-Controller)设计模式的应用程序。本文较为详细、深入地论述了该框架中主要组成部分实现的原理、方法及相互的关系，对Struts框架的理解和使用有一定的指导意义。

关键词: 架构，模式，Struts，MVC

<pre>
 引言
 当今应用于J2EE平台的web应用框架很多，象Cocoon，Turbine, Velocity, Struts等，这些框架各有特点，比如Cocoon主要使用XML和java技术进行内容发布，Velocity使用vm（Velocity的脚本）强化视图层应用等，而Struts则以其灵活的控制层实现，极好的可扩展性，易于与其他架构集成等特点独树一帜，虽然是一个开源项目，但它掀起的应用风暴却是不可小视的。
现在，Struts架构已经越来越流行，俨然要成为一个标准。从使用该架构的角度看，现在这方面的文章很多；但是如果从该架构如何实现角度来看，国内这方面的文章却很少，所以，下面我们会从Struts框架如何实现的角度来对其进行探索分析，比如其所使用的MVC系统模式和Model2架构，其控制器组件实现原理，视图层提供的支持等。鉴于笔者认识有限，可能部分有不妥之处，还望指正。
1．MVC模式和Model2架构
 要介绍Struts框架，不能不先从这两个概念开始谈起。
1．1 MVC模式
MVC模式即Model-View-Controller（模型－视图－控制器）模式的简称，从总体来看，大部分web应用的系统框架都是基于MVC模式实现的。在该模式中，通过模型，视图和控制器等角色将一个应用系统中的各个部分之间的耦合解脱并分割开来，从而提高系统的可扩展和可维护性，这也符合现代软件工程中有关模块间高内聚，低耦合的要求。下面是该模式的一个通用的结构图（图1）表示：
 
图1
在MVC模式中,控制器（Controller）组件用来定义系统的行为，将用户的请求映射到模型中的处理逻辑进行更新，并选择合适的视图返回给用户。它为每一种功能提供控制器。
 视图（View）组件用来描绘模型，向模型请求显示数据，并将每个用户请求发送到控制器，而控制器也可以选择合适的视图来显示。
 模型（Model）组件用来包装应用程序的状态以及处理逻辑，通过控制器来向视图提供数据更新，及时更新视图显示。
1．2 Model2架构
Model2是在MVC设计模式的指导下出现的一个Java Web应用开发使用的开发架构，主要特点是由JSP，Servlet和javabeans来实现MVC设计模式中的各个部件，具体就是由JSP对应实现MVC中的V（View），即视图部分；由Servlet对应实现MVC中的C（Controller），即控制器部分；由JavaBeans来对应实现MVC中的M（Modal），即模型部分。Model2的架构图如下所示（图2）：
 
图2
浏览器提交请求给Servlet控制器，Servlet调用模型（JavaBeans）与后面的EIS层进行交互，取得所需数据。然后，控制器将控制转到JSP视图，JSP视图使用JavaBeans模型进行视图更新，然后将更新后的视图返回给客户端浏览器。整个流程就是这样。
2．Struts系统架构
 Struts框架就是基于以上提供的模式和架构进行设计和实现的，这可以从其系统结构图（图3）中很清晰的看出：
Struts的系统结构图跟Model2的十分相似，毕竟，它就是在Model2的基础上实现出来的。下面，我们不会对架构图中的各个组件如何使用做过多描述，而是要对Struts框架如何实现这些来进行相应的探索。
 
图3
作为一个WEB应用的开发架构，Struts有其侧重点，虽然说从总的方面来说，他是基于MVC模式实现的，但是，Struts并非对Model，View和Controller三方面的组件都进行了实现，Struts架构更加侧重于Controller组件的实现，其次是View，而对于Model部分，可以说Struts并没有提供什么，只是为Model的调用实现了一个桥梁。
 所以，以下就从Struts对MVC三部分组件实现的侧重进行更详细的探索阐述。
2．1 Struts中Controller的实现
  在Struts中，所有的request都将通过ActionServlet进行流程控制，也就是说ActionServlet类负责所有的客户端请求的总控。但是如果将所有的控制逻辑都编码入ActionServlet，则势必造成ActionServlet的极难扩展，随着客户端请求种类增加，ActionServlet也需要随着这些进行修改，这与设计模式中强调的“开－闭”原则（Software entities should be open for extension, but closed for modification.）背道而驰，而且也是不可能走得通的一条路，所以，Struts架构在实现的时候，采用将控制逻辑配置到一个xml文件（即Struts-config.xml）中的做法来解决这个问题。ActionServlet在初始化的时候，首先会读取Struts-config.xml文件中的内容，然后通过相应的类对这些规则和资源进行wrap后存入ServletContext以备后用。在处理客户端请求的时候，ActionServlet查找已经在Servlet初始化时候解析完成的规则，根据查找到的匹配规则调用相应的Action对请求进行处理，而后根据规则选择对应的合适视图提供给客户端。
  以上的控制逻辑实现在Struts架构的实现中主要牵扯到的类是ActionServlet，RequestProcessor和Action类，以及ActionForward，ModuleConfig等。各个类之间的关系可以简略表示如下（图4）：
 
图4
 对于ActionServlet来说，它继承自javax.servlet.http.HttpServlet类，其主要的责任是：
【1】 接收客户端请求进行处理；
【2】 根据客户端请求具体信息将该请求映射到配置文件中规定的Action类进行处理；
【3】 如果需要，从请求中提取数据填充与页面表单相对应的ActionForm；
【4】 调用Action类的execute（）方法将业务处理完成后调用正确的视图响应客户端请求。
当然，作为HttpServlet的子类，ActionServlet同样具有初始化资源和最终清除所用资源的任务。
与其父类相同，ActionServlet在其生命周期内同样要执行init（）方法进行资源初始化，在这里，它读取Struts-config.xml文件中的内容，然后使用相应的wrapper类对文件中规定的控制规则进行wrap，这些wrapper类一般都组织在org.apache.struts.config包中。下面是Steve Ditlinger文章中所列的几个wrapper类的关系（图5）：
 
图5
 在Struts1.1中，ActionMappings和ActionMapping分别被ModuleConfig和ActionConfig所代替，但是关系实际上还是如此。
在init（）方法的资源初始化过程中，ActionServlet调用自身的几个模板方法对相应资源依次进行初始化，顺序可以使用UML Sequence图（图6）展示如下：
 
图6
各个方法所要处理的事务可以简略阐述为：
1. 调用initInternal()来准备MessageBundle；
2. 调用initOther()方法装载web.xml文件中的参数以便对ActionServlet进行控制；
3. 调用InitServlet()方法初始化servlet本身，比如URL映射，taglib等，并且还会注册xml文件所用到的DTD以便后面使用对xml文件进行验证；
4. 调用initModuleConfig()方法装载并初始化config参数所指定的Struts默认模块配置数据，解析后的数据会存入ModuleConfig类，然后放入ServletContext；
5. 调用initModuleMessageResources()方法初始化相应模块的MessageResource并存入ServletContext()；
6. 调用initModuleDataSources()方法初始化配置文件中指定的数据源，如果没有，则跳过这一步；
7. 调用initModulePlugIns()方法装载并初始化Struts配置文件所指定的插件。
在Struts1.1中因为引入了多个模块的概念，所以，如果配置文件中指定了多个模块，则初始化方法会从initModuleConfig（）方法到initModulePlugIns（）方法循环执行以对各个模块进行初始化。
在init（）方法中资源初始化完成之后，ActionServlet就进入请求处理准备状态。一旦客户端请求到达，ActionServlet会调用其process（）方法来处理请求。这是process方法的定义：
protected void process(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
      {
         RequestUtils.selectModule(request, getServletContext());
            getRequestProcessor(getModuleConfig(request)).process(request, response);
     }
  可见，ActionServlet会首先根据请求信息选择合适的应用模块，而后根据模块中在初始化的时候所解析的信息取得一个RequestProcessor类的实例，并且调用其process方法来处理请求，至此，所有的请求处理交给RequestProcessor的实例来处理，RequestProcessor会根据处理结果选择合适的视图提供给客户端，这将在后面提到。显然，Struts的设计者使用了Proxy模式来实现这种处理结构。
  在ActionServlet的生命周期中，当不在进行请求处理之时，ActionServlet会调用destroy（）方法释放所有使用到的资源，跟资源初始化时候相反，这里就不在阐述。
  既然ActionServlet会将处理转交给RequestProcessor类处理，下面就对该类进行一下其实现探索。
   先看一下RequestProcessor的简略类图：
 
  可见，RequestProcessor的实现使用了模板设计模式，这主要是为了以后扩展的需要，只要根据需要实现继承实现相应的方法就行了，现成的实例就是上面关系类图中的TilesRequestProcessor。
  该类的主要作用就是实现在ActionServlet接收到请求后需要处理的请求的处理逻辑。处理逻辑的入口就是process（）方法，该方法按照一定的顺序处理请求，这个顺序是一定的，如下Sequence图所示（图7）：
    
首先，process（）方法调用processMultipart(request)方法处理ContentType为multipart/form-data并且为POST方法提交的请求，返回一个MultipartRequestWrapper类的实例用来处理这种类型的request；如果请求为GET方法提交或者ContentType不是以上类型，则方法返回原来的request；
其次，调用processPath(request, response)方法处理请求的路径信息；
以下依次为：
&#61548; 调用processLocale(request, response)方法判断用户请求的locale信息，然后将一个Locale对象实例存入用户的Session；
&#61548; 调用processContent(request, response)方法判断用户请求的contentType类型以及编码，默认的是text/html类型，可以在JSP页面中重新设定请求的contentType和encoding；
&#61548; 调用processNoCache(request, response)方法决定noCache属性十分设定为true，依次用来决定是否要阻止浏览器缓冲返回的页面；
&#61548; 调用processPreprocess(request, response)方法，这个方法只是简单的返回true，表示处理可以继续进行；这个方法主要的好处就是，当实现一个该类的子类的时候，可以在这里添加需要的请求处理逻辑，依次来决定是否要继续处理或者停止；
&#61548; 调用processMapping(request, response, path)方法查找用来处理请求的ActionMapping，如果没有找到，则返回客户端一个错误的回应；
&#61548; 调用processRoles(request, response, mapping)方法检查客户请求的roles，如果没有合适的role，返回错误信息给客户端；
&#61548; 调用processActionForm(request, response, mapping)方法检查是否有与ActionMapping相关联的ActionForm，如果查找到，则使用现有实例，最终使用与Aciton相关的键值存入合适的请求scope中；
&#61548; 调用processPopulate(request, response, form, mapping)方法，从request的信息中为相关的ActionForm填充属性；
&#61548; 调用processValidate(request, response, form, mapping)方法，如果validate属性设置为true，则调用ActionForm的validate（）方法严重表单，如果有错误，返回input属性指定的页面显示错误信息；否则，处理继续；
&#61548; 调用processForward(request, response, mapping)和processInclude(request, response, mapping)方法，如果设置了actionMapping的forward或者include属性，则调用RequestDispatcher类的forward（）或者include（）方法处理请求；
&#61548; 调用processActionCreate(request, response, mapping)方法创建或者查找一个Action实例用来处理请求，如果action的缓存中有现成的实例，使用该实例；否则，重新创建新的实例并存入缓存；
&#61548; 调用processActionPerform(request, response, action, form, mapping)方法来调用Action的execute（）方法处理请求，并且使用try/catch块来wrap execute（）方法，以便requestProcessor可以处理exception；
最后，调用processActionForward(request, response, forward)方法根据Action执行后返回的ActionForward类的实例决定是否redirect或者forward到指定视图；
至此，RequestProcessor完成一个请求的处理周期，其他请求类似的流出处理。
可以了解的是，除了准备一些处理需要的信息之外，RequestProcessor类同样通过Proxy设计模式的实现将处理逻辑交给了Action类。

Action类的主要职责就是辅助ActionForm进行必要的数据有效性检查，执行必要的业务逻辑，为请求准备必要的返回数据。它可以看作是控制器与模型交互的一条桥梁，之所以这么说是因为，不提倡直接在Action中实现所有的业务逻辑实现，包括数据库连接，更新等。因为这样势必造成较强的耦合性，不易复用。
另外，struts的设计者将Action设计为可以同时为多个请求可用，也就是说一个Action实例可以被用来处理多个请求，而不是针对某个特定请求的，所以，在编程实现的时候，最好是不要在Action中定义与某个特定请求状态相关的变量；如果某些资源需要保护，最好是控制为同步访问。
Action类在其主要的方法execute（）中实现所有的处理逻辑，根据处理结果会返回相应的ActionForward对象给RequestProcessor，后者根据这些对象处理后面的事务，这在RequestProcessor的process（）方法中已经介绍过了。
总之，Action类是实际上的处理逻辑实现部分，只不过在使用的时候需要注意某些问题而已，这是由Struts的实现决定的。
根据各种需要，Struts的设计者还为Action类实现了几个子类来满足不同的需要，这几个类之间的关系如图8所示。
这几个子类包含在org.apache.struts.actions包中，主要完成一些特殊的工作，这里不作赘述，可以参考Struts的相关文档。
这样，控制器的整个控制流程的实现就可以从这三个主要的类的实现中可窥一斑，这几个类的关系也可以从上面的类图中很好的看出。

 
图8

下面让我们对Struts的视图部分即View部分进行相应的探索。
2．2 Struts中View的实现
 在Struts架构中，视图组件的功能就是为客户端提供动态内容。与平常的基于Model2架构开发的视图一样，实际上Struts的视图实现也是基于JSP技术的。但是，Struts在JSP的基础上开发的一套组件却可以是开发者更加关注于商业需求上，而不用过多的纠缠于底层的如何实现上，而且，Struts的极易扩展性还使他可以同现在流行的JSF（java Server Faces）技术，以及国外流行的基于Apache Jakarta项目的更关注视图部分实现的Velocity视图架构相结合，从而为开发者以及项目主管提供更多的选择。
 放下JSF和Velocity先不谈，让我们先对Struts架构提供的几个基于JSP技术的视图组件的实现进行必要的探索。（对于JSF和Velocity框架，如果读者感兴趣可以参考附录提供的文献）
 组成Struts架构的视图组件总的来说可以包含ActionForm，ActionError, Tiles，Validator以及Struts提供的一组TagLib。这些组件提供了对于国际化（I18N），用户输入取得，验证以及错误处理等功能的支持。下面是对这几个Struts提供的注意视图组件实现进行探索剖析。
 2．2．1 ActionForm的实现
 作为web应用，一般都要从客户端取得用户输入，然后从请求对象中抽取表单输入域信息，对表单数据进行有效性检验，然后交给服务器端进行业务处理，如果表单数据验证有错误，还要将错误信息返回给客户端并将没有错误的表单输入域数据返回并显示。这些工作对于web应用的开发者来说都是不可避免的。为了对这些任务进行复用，并且减轻开发者的负担，同时提高开发效率，Struts架构提供了ActionForm组件来处理以上事务。
 ActionForm是一个可序列化的抽象类，实际上可以看作是一个特殊的javabean，通过下面的类图可以清楚的看出其结构：
 
 作为一个特殊的javabean，ActionForm中定义的所有属性会对应于请求表单中的某个表单输入域（当然，使用一个ActionForm来处理web应用中的所有表单输入也是可以的），在表单提交后，ActionForm会抽取request对象中的表单输入域信息填充与表单输入域相对应的自身属性，之后在调用validate方法对数据进行有效性检验的时候，如果没有错误，则ActionForm会放入指定的scope中，而后交给Action处理；否则，将错误信息返回客户端，并返回配置文件中指定的input属性所指定的页面。ActionForm的这个生命周期可以使用UMLActivity图更详细，更清楚的表示出来（图9）：
 
图9
 开发者在实现自己的ActionForm的时候都要继承其父类――抽象类ActionForm，并且要定义与表单输入域相对应的属性，在validate方法中实现验证逻辑，在reset方法中重置表单数据，对于只有几个页面的应用来说，这可能不算什么，但是如果应用大一些，表单页面成百上千，那会怎样？如果为每个表单页面都手写一个FormBean，那工作量是不可想象的。所以，Struts的设计者为开发者实现了几个特殊的ActionForm子类，以满足不同的开发需要，他们之间的关系如下：
 
 实现DynaActionForm子类系来为开发者可以手动在配置文件中添加动态Form的能力，不仅可以减少工作量，而且如果因为需求变更等原因也可以不用修改代码，直接修改配置文件就可以了；实现ValidatorForm子类系来为开发者提供动态表单验证功能，这要在validator部分提到。
 总之，Struts架构使用ActionForm来处理表单数据抽取，验证，错误信息回调等所有以前开发中所不可避免而冗余的事务，对这一切进行合理封装，大大简化了开发效率以及减少了开发的出错率，提高了软件的开发质量。
 2．2．2 ActionErrors的实现及其功能
  在前面的ActionForm部分，我们提到其validate方法实现数据有效性检验，而这个方法返回的就是ActionErrors对象。Struts中使用ActionErrors来作为错误信息的wrapper类，它可以包含多个ActionError对象。客户端通过读取ActionErrors中的信息来返回相应的错误信息给用户。ActionErrors是一个狭义的概念，所以struts1.1中有引入了两个更普遍的概念：ActionMessages和ActionMessage。他们之间的关系如类图（图10）所示。
  他们都实现了java.io.Serializable接口，从而可以实现序列化。通过将ActionMessages和ActionErrors放入request，客户端可以使用相应的JSP自定义标签读取信息并显示。Struts提供了<html:errors/>实现这个基本功能。

 
图10
  其实这几个类的实现十分简单，主要是实现序列化，并且提供某些访问接口而已。从上面的类图关系中可以清楚的看出，所以不在赘述。
 2．2．3 Tiles的实现
 Tiles组件是Struts1.1新引入的一个插件组件，它为页面视图的组件化开发提供了一种很好的选择。其主要原理是根据JSP提供的<include>机制来实现页面的动态组合，从而达到动态页面的组件开发。Tiles原本不是Struts提供的视图组件，只是在1.1版本后才作为Plugin加入进来。基于Struts提供的PlugIn机制，Tiles组件实现了org.apache.struts.tiles.TilesPlugin
类来将自己作为一个插件加入到Struts中，并且扩展RequestProcessor，实现org.apache.struts.tiles.TilesRequestProcessor来处理与Tiles相关的请求处理。限于篇幅所限，不再对其实现做深入探索，因为其原理就是上面所说的include机制，以及struts提供的PlugIn机制。更多信息可以参考Tiles WEB站点。
 2．2．4 Validator的实现
 同Tiles组件相似，validator组件同样是以插件的形式加入到struts1.1中的。其基本原理是基于Commons Validator项目的实现机制，只不过在集成的时候，实现了org.apache.struts.validator.ValidatorPlugIn
类将其集成到Struts架构中，有关Commons－Validator项目实现的情况，在龚永生所发表的《通用验证系统》一文中有十分详尽的描述，这篇文章发表在IBM的developerworks网站上，这是其链接：http://www-900.ibm.com/developerWorks/cn/java/l-commons-validator/。
  上面在ActionForm部分曾经提到过其ValidatorForm子类系是使用validator来进行数据验证的，事实上也正是如此，validator可以使得ActionForm不需要将验证逻辑硬编码到validate方法中，而是通过validator来处理数据有效性检查。通过在validator的配置文件中登记需要验证的表单和规则，就可以对相应的表单进行验证，而且还可以对验证逻辑进行扩展，这大大减少了实现之间的耦合度，提高了软件复用度。
 2．2．5 TagLib的实现
  Struts架构在发布的时候，提供了5个自定义标签库，他们分别是Bean Tags，HTML Tags，Logic Tags，Nested Tags和Tiles Tags。对于这些标签库来说，实际上没有什么新的东西，早在JSP中就有<jsp:useBean/>，<jsp:setProperty/>等已经定义的标签，现在Struts只是根据规范实现了一些与Struts可以更好工作的自定义标签库，至于自定义标签是如何实现的，这里不想过多牵扯，读者可以参考servlet API或者后面提供的相关参考文献。
2．3 Struts中Model的实现
实际上，Struts并没有提供确切的Model组件实现方式，而这也是应该这样做的，因为现在已经有许多现成的Model组件实现技术，包括J2EE的EJB，JDO等技术。 Struts架构并不限定你使用某一项Model组件实现技术，只要可以集成到Struts中，所有可以实现Model的技术都可以使用，比如，对于小型或者中型的应用，后台可以使用JavaBeans来实现Model组件，通常使用DTO（Data Transfer Object）,DAO（Data Access Object）等模式来实现。 而对于大型的应用，一般就是EJB来实现后台的Model组件，包括使用Entity Bean进行数据状态维护，使用Session Bean实现业务逻辑等。
 总之，鉴于手头上的项目条件，使用Struts架构，你有足够的弹性来选择使用什么样的Model实现技术。
结束语
 综上，我们开始对Struts基于的MVC模式和Model2架构进行了简单的介绍，在此基础上，我们给出了Struts框架的结构图，因为该框架中各个组件实现的侧重不同，我们着重对控制器组件的实现做了一定程度的分析，并且发现其较多使用模板模式和代理模式进行设计，以便可以将来更容易对其进行扩展。最后，我们还对View组件的实现进行了一定的介绍和分析，而对于Model组件，我们只是提供了简单的介绍，这是基于Struts框架对Model实现所提供的支持而如此处理的。以下是本文内容的简单总结：
1. MVC模式和Model2架构的简介
2. Struts框架的实现分析
&#61548; Struts中Controller组件的实现分析
&#61548; Struts中View组件的实现分析
&#61548; Struts中Model组件的实现分析

希望以上的内容能够使您觉得有所帮助，可以帮助您更好的了解Struts架构。笔者也是接触Struts不久，故此本文中可能有的部分说法不是太妥当，还望读者不吝指正。
感谢我的同事们在此文完成期间所给予的鼓励和帮助，为完成此文他们提供了许多建议和支持，在此再次表达我的谢意。
参考文献
1．《Programming Jakarta Struts》，O''Reilly & Associates, Inc，By Chuck Cavaness
2．《java与模式》，阎宏，电子工业出版社
3．Struts User Guide，http://jakarta.apache.org/struts/index.html
4．Steve Ditlinger  ，《Mix protocols transparently in Struts 》
5．《JSP标志库编程指南》，电子工业出版社，【美】Simon Brown著
6．《Java Server Page技术参考》，中国铁道出版社，林上人，林上杰著
7．在IBM developerworks网站可以找到更多有关Struts的技术文章，还有Tiles的教程。
8. 有关Cocoon，Turbine，Velocity架构的信息，可以参考Jakarta官方网站有关资料
</pre>












