% DisplayTag Tutorial
% DarrenWang

> 2014年从msn space存档中重新恢复出来！


<pre>
   写点儿东西真不容易，暂时写了这么些，先贴于此。
[b]DisplayTag Tutorial by DarrenWang[/b]
                   CopyRight June，2004:em510:
                   By DarrenWang，All Rights Reserved！

[b]【简介Introduction】[/b]
DisplayTag是一个开源的自定义标签库（Custom Tag lib），他提供了直接而有效的格式化web视图层数据的有效手段。你可以在现在流行的web应用的MVC模式中集成DisplayTag到View层，其提供的强大表格格式化功能一定会令你爱不释手。或许上面说的有些夸张了，但是DisplayTag在表格的格式化方面表现确实出色，当然，他也只能显示表格，视图层的大部分工作不就是使用表格来格式化数据嘛？！
 好了，让我们通过图片来看看他是一个什么样子吧!^_^
[img]http://displaytag.sourceforge.net/images/sample_snapshot.png[/img]
怎么样？是不是感觉不错那？如果答案是肯定的，那么你一定急着想自己试一试咯？！不要急，下面就让我们开始我们的DisplayTag之旅。

[b]【Hello DisplayTag】[/b]
既然是一个tutorial，所以，我们不想对像自定义标签的实现原理等进行解释，也就是说在此之前，我们假定你已经对自定义标签有一定的认识，当然，没有也无所谓，等这篇tutorial完成后，你估计就会了解的差不多了。
 呐，让我们从最简单的displaytag的使用开始，就跟你的第一个程序往往是从HelloWorld程序开始一样。
 先忽略其他的配置问题，我们的JSP文件的源代码如下：
[img]/user11/darrenwang/upload/2004639261011063.gif[/img]
实际上，除去初始化和数据准备等操作，生成表格的代码只有一行，那就是：
<display:table name="InfoList">
</display:table>
 而他生成的表格就是这样的：
[img]/user11/darrenwang/upload/200463927041199.gif[/img]
怎么样？是不是很简单那？简单的代码就可以生成如此漂亮的表格，你有理由不用嘛？（因为使用了Struts的LabelValueBean，所以表格上显示了原始的title，不用着急，后面我们将会说道如何修改成你所期待的样子）

[b]【配置configuration】[/b]
Ok，在我们运用DisplayTag之前，我们需要对他的使用环境进行一些配置，或许有些复杂，但是，如果你是一个WebApp老手的话，其实并不难。
 当然，在此之前，我们需要下载DisplayTag，当前的最新版本是displaytag-1.0-b3。你可以去SourceForge下载它，下载网址是：http://displaytag.sourceforge.net/download.html 。
 2．1 DisplayTag的类库，依赖库和TLD文件的添加
 解压下载下来的displaytag的压缩包，之后依次拷贝displaytag-1.0-b3.jar和lib目录下面的所有jar文件到你自己的WEBAPP_HOME/WEB-INF/lib目录下面，拷贝displaytag-11.tld，displaytag-12.tld和displaytag-el-12.tld到WEBAPP_HOME/WEB-INF目录下面。
 他的依赖库包括：commons-beanutils，commons-collections，commons-lang以及commons-logging。
 这里需要注意的问题就是，如果你连同Struts一起使用的话，DisplayTag的依赖库实际上都包括在Struts1.1的发布包中，你只需要将displaytag-1.0-b3.jar文件拷贝到你自己的WEBAPP_HOME/WEB-INF/lib目录下面就可以了。
 另外一个重要的问题就是，如果你的Struts1.1发布包中的commons lang包不是2.0版本或者更高版本的话，需要去Apache的Jakarta commons项目主页上下载2.0版本的commons-lang类库，并替换掉原来的commons-lang类库，否则，运行的时候将报错误并不能运行。
 2．2 web.xml的配置
 要使用DisplayTag提供的自定义标签，跟其他自定义标签的使用没有什么两样，同样，需要在web.xml文件中注册taglib，下面是笔者的web.xml文件中taglib注册的片断：
<taglib>
    <taglib-uri>http://displaytag.sf.net</taglib-uri>
    <taglib-location>/WEB-INF/displaytag-11.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>http://displaytag.sf.net</taglib-uri>
    <taglib-location>/WEB-INF/displaytag-12.tld</taglib-location>
  </taglib>
  <taglib>
    <taglib-uri>http://displaytag.sf.net/el</taglib-uri>
    <taglib-location>/WEB-INF/displaytag-el-12.tld</taglib-location>
  </taglib>
 在这里有必要说明一下这三个tld之间的区别，这其实在DisplayTag的官方网站上有提到，这里只是重复一下：displaytag-11.tld 只是提供对JSP1.1规范的的支持，而displaytag-12.tld则提供了对JSP1.2规范的支持，最后的displaytag-el-12.tld除了提供跟displaytag-12.tld提供的特性之外，他提供对Expression Lanuage的支持。所以，为了在web应用移植于不同的app server的时候可以更少的修改文件，这里将所有的tld都添加在这里以便使用。
 配置完成taglib后，下面是可选择的配置项，如果你不需要的话，可以不进行配置，但建议还是配置他们为好。
 第一幅图中可以看到diaplaytag提供了数据的导出功能，如果说你的Table存在的页面被include在另一个页面中，比如如果你使用Struts的话，那么Tiles的使用就是这种情况，那么你需要为web.xml中添加filter，以便数据到处功能能够工作正常。
 首先，在web.xml中添加以下filter配置项（按照web.xml文件中各个elements的顺序规定，需要将<filter>元素添加在<servlet>前面，以下类似的情况请参考web.xml规范）：
 <filter>
<filter-name>ResponseOverrideFilter</filter-name>
<filter-class>org.displaytag.filter.ResponseOverrideFilter</filter-class>
</filter>
 其次，添加filter的映射：
 <filter-mapping>
<filter-name>ResponseOverrideFilter</filter-name>
<url-pattern>*.do</url-pattern>
</filter-mapping>
<filter-mapping>
<filter-name>ResponseOverrideFilter</filter-name>
<url-pattern>*.jsp</url-pattern>
</filter-mapping>
 这样，对于web.xml的配置基本就完成了。对于I18N在web.xml文件中进行配置的手段，将在后面提到，不归入此类。
 2．3属性文件的配置（displaytag.properties）
 DisplayTag提供了一个属性文件（displaytag.properties）来定义表格显示的时候提供的信息，比如分页显示或者导出数据等的提示信息等，但是因为这个属性文件默认的文件随jar文件一起发布而且是英文的，所以，我们需要对其进行定制以满足中文或者其他平台下的使用。
 要对这个属性文件进行定制，有三种途径：
 【1】使用<display:setProperty>标签，这个标签可以对单个的属性进行设置，也就是说如果要对整个的应用页面都进行定制的话，需要每个页面都使用这个标签并对每一个要定制的属性都使用它，这很明显不是太行得通，所以，displaytag还提供了下面得途径；
 【2】使用DisplayPropertiesLoaderServlet来初始化环境，这种方式方式可以对整个应用的属性进行定制，但是，笔者使用这种方式的时候报错，不过，还是将其在web.xml文件中的配置项列于此：
<servlet id="DisplayPropertiesLoaderServlet">
    <servlet-name>DisplayPropertiesLoaderServlet</servlet-name>
    <display-name>DisplayPropertiesLoaderServlet</display-name>
    <description>displaytag initialization servlet</description>
<servlet-class>org.displaytag.properties.DisplayPropertiesLoaderServlet</servlet-class>
    <init-param>
      <param-name>properties.filename</param-name>
      <param-value>/WEB-INF/displaytag.properties</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
 这种方法也是读取diaplaytag.properties中的属性配置对整个应该环境进行定制。
 最后，也就是就第三中方式，也是笔者最常用的方式，那就是：
 【3】新建一个diaplaytag.properties属性文件，向该文件中添加需要覆盖的或者需要另外定制的属性（具体有那些属性，DisplayTag网站提供了一个PDF格式的manual，上面有所有可以使用的属性的列表，因为太长，所以这里不作罗列）。这个文件的一个样本笔者将在后面的实例部分进行罗列。在准备好属性文件后，将其放到WEBAPP_HOME/WEB-INF/classes目录下面就可以了。我想这也比其他方式方便的多，另外，这种方式也是针对整个的WEB应用进行定制。
 以上就是配置文件的三种配置方式，第一种只能对单个属性单个页面进行，而后面两种方式可以针对整个的web应用，尤其是第三种方式，笔者尤其倡导。
 2．4 CSS和img的移植
 如果说你试着将页面中的这一句去掉的话：
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/screen.css" type="text/css" media="screen, print" />，或许你会看到不想看到的景象，或者说你看到的表格将与你所期待的大相径庭：
[img]/user11/darrenwang/upload/2004639444276758.gif[/img]
怎么样？与上面的简单实例相比，是不是淡色不少？！所以，要使得Displaytag提供最好的显示效果，或者说要显示正常，我们不但要保证服务器端的配置，同样的也要保证视图层的配置，比如说CSS和Images。
 故此，建议将下载的压缩包中的diaplaytag.war文件包中的css目录和img目录拷贝到你当前工作的WEBApp的根目录下面，这样，就可以避免以上的事情发生。当然，这只是可以正常显示的必要条件，像上面那样，如果页面中不引入css的话，同样会显示不正常。
 至此，你的Displaytag的配置就算完成了。怎么样？是不是有些繁琐那？！不过不用担心，与它所带给你的便捷和强大的功能相比，这算不了什么。
 好了，下面就让我们对它的一些概念进行一下探索吧！

[b]DisplayTag Tutorial by DarrenWang[/b]
                   CopyRight June，2004:em510:
                   By DarrenWang，All Rights Reserved！
接上回书说道，:em325:
[b]【displaytag提供的自定义标签说明】[/b]

DisplayTag一共提供了五种标签用来显示显示表格，他们是<display:table>,<display:column>,<display:setProperty>,<display:caption>和<display:footer>。通过这几个标签的组合可以完成大部分表格的显示功能。下面，笔者将就各个标签情况做一阐述，其中将会包括其功能以及使用中可能遇到的问题。
3－1 <display:table>标签说明
DisplayTag标签库的顶层标签，用来显示整体的表格，通过从不同的scope中抽取数据并进行显示，根据Collection形式的数据中的属性标志来罗列数据。剩下的所有的diplaytag标签都嵌套于此标签之内。他所提供的主要功能包括：以CSV，XML和Excel形式导出数据；对于较长的数据，提供分页显示功能等等。
实例代码：
<display:table name="sessionScope.InfoList" pagesize="3" requestURI="" export="true">
</display:table>
 该标签有一系列的属性（Attribute），因为太多，这里仅就几个主要而常用的进行说明：

 Name属性：必须指定，表示scope中的数据标志，通过name来引用scope中的数据并进行显示。可以指定pageScope，requestScope，sessionScope和applicationScope。其中requestScope是缺省的scope，如果数据像request.setAttribute(“Infolist”,list)的形式放入requestScope，那么name属性可以直接像[name=“Infolist”]的形式指定。而像sessionScope的话，就要像上面的例子中那种形式指定了。

 Id属性：指定显示表格的唯一标志，在后面你可以通过yourID_rowNum的形式显示或者使用每行数据的行号。比如：如果指定id=”tableID”，那么，<%=tableID_rowNum%>就会输出数据的行号。而指定id的另一个作用就是，如果一个页面中有多个分页显示表格的话，指定id后，各个表格的分页就可以工作正常。

 Export属性：需要指定boolean型的值，如果指定export=true的话，表格显示完成后，下面会有一个输出项条目，指定数据导出的选项；否则，不显示数据导出条目。默认为false。
 
 Pagesize属性：指定每页最多显示的数据总数。如果要显示的数据记录很长的话，指定pagesize后，数据将按照pagesize属性指定的数目显示记录数，其他的数据将分多个页面显示。如果不指定该属性，所有数据将在一个页面显示。
 
 Class属性：指定表格显示所要使用的css风格，displaytag提供了ISIS，ITS，Mars，Simple，Report五种风格，默认是ISIS，也就是上面的黄色色调的风格。这些风格都是在screen.css文件中定义的，可以根据需要修改或者添加需要的风格。
 
 RequestURI属性：当表格需要数据导出，排序或者分页显示的时候，因为要提交给指定的URL处理，而这个属性就是做这个事情的。

 Sort属性：用来指定对数据进行排序的时候是对整个的数据list进行排序还是只对当前页面的数据进行排序。默认的不指定该属性的情况下，排序的时候只对当前页面数据进行排序；如果指定sort＝“list”的话，则可以对整个的数据list进行排序。
 OK，其他属性读者有兴趣或者需要的话，可以参考DiplayTag网站提供的manual。
 3－2 <display:column>标签说明
 顾名思义，该标签是用来显示表格中的一列，它只能嵌套在<display:table>标签中使用，显示decorator处理后的结果，如果没有指定decorator，则显示property属性指定的数据。
 代码实例：
 <display:table name="sessionScope.InfoList" pagesize="3" requestURI="" export="true" >
        <display:column property="label" title="ID NUM"/>
        <display:column property="value" title="VALUE">
        </display:column>
    </display:table>
 该标签有个特性，即如果以空元素的形式出现，则显示property属性指定的数据；否则，也就是不以空元素形式出现，那么如果两个元素中指定了数据，即使property指定的数据存在，也会以两个元素间的数据显示为准。
例如：<display:column property="value" title="VALUE"></display:column>或者<display:column property="value" title="VALUE"/>将按照value属性指定的数据进行显示，而<display:column property="value" title="VALUE">My Custom Value</display:column>将在每行只显示“My Custom Value“，而不是显示property=”value”所指定的数据。
 这个属性可以帮助你定制自己的列显示，后面将会提到某些实例中的使用。
 <display:column>标签的属性（Attribute）说明：

 Property属性：指定与该列显示数据相关联的property名称，该属性对应该行数据bean的属性，如果这列要显示bean的数据，column的这个属性是必须指定的。
 如：<display:column property="paramName"/>
 Title属性：该属性用来指定显示列的标题。如果不指定该属性，默认的使用property的名字做为该列的标题。这也就是我们第一个例子中两列的标题都是LabelValueBean的属性名的原因，我们只要为这两列指定需要的title就可以了。
 如：<display:column property="label" title="省份"/>
 Href属性和其关联属性：使用href属性动态构造当前列的各行数据的超连接。使用paramId来指定附在url字符串后面的参数名称，而使用paramName或者paramProperty来指定与paramId相关联的参数值。
 如：<display:column href="preUpdateAction.do" paramId="id" paramProperty="userId" >UPDATE DATA</display:column>将会生成类似于下面的url形式：
 http://XXX/youApp/preUpdateAction.do?id=132(假设该行的userId的值为132)
 Sortable属性和headerClass属性：DisplayTag还提供了一个很有特色的特性，那就是可以针对某一列的数据进行排序，而这只需要指定sortable属性为true，并指定其headerClass为sortable就可以了。
 如：<display:column property="label" sortable="true" headerClass="sortable">
    </display:column>
 这样，就可以通过点击该列的标题来排序该列的数据了。
 其中，sortable属性接受boolean值为合法属性值，而headerClass为string型的合法值。
 其他属性说明这里略去，请参考相关文档。

 3－3 <display:setProperty>标签说明
 使用这个标签可以对DisplayTag显示的表格的属性进行设置，但因为只能作用于单个的表格，所以，作用有限，一般用来处理个别的情况。该标签同样需要嵌套于<display:table>标签内使用。它只有两个属性：name和value。通过为指定的name设置相应的value来更改displaytag的默认属性。
如：<display:setProperty name="basic.msg.empty_list" value="无记录可供显示" />或者<display:setProperty name="basic.msg.empty_list">无记录可供显示</display:setProperty>
这些属性的name可以参考DisplayTag网站提供的TagReference文档，具体网址是
http://displaytag.sourceforge.net/tagreference-displaytag-12.html。
 3－4 <display:caption>标签说明
 这个标签比较简单，就是完成html里面的<caption>标签所完成的功能。可以在表格的上方显示指定的自定义表头。
 代码实例：
 <display:caption>
        <font size="7">
            Caption of The Table
        </font>
</display:caption>
 效果如下所示：
[img]/user11/darrenwang/upload/20046317343872597.gif[/img]
3－5 <display:footer>标签说明
 与<display:caption>标签相对应，这个标签用来显示表格的表尾。按照其实现的需求，该标签应该像在上图中那样在表尾显示，但是上图是在Jbuilder中抓下来的，显示正常，但是，如果在IE中，表尾文字将会在表头之上（我想，这应该是IE的问题）。如下图所示：
[img]/user11/darrenwang/upload/20046317354615797.gif[/img]
至此，DisplayTag的五种标签就简单介绍完了，但是我们不想就此打住，下面，笔者将对DisplayTag的某些特性做进一步的探索。

[b]【displaytag高级特性】[/b]
有些时候，Collection中提供的数据或许不是我们想要的形式，比如，货币字段，在DataObject中或许只是存为int或者long甚至BigDecimal等形式，但是，显示的时候，我们不想以这种形式显示在页面上，这个时候，我们就需要借助DisplayTag提供的Decorator特性。Decorator可以帮助我们在显示数据之前对相应的数据进行格式化，然后再返回格式化后的结果进行显示。下面我们就DisplayTag提供的column decorator和table decorator 进行简单的剖析并列举其使用场合。
4－1 colunm decorator阐述
 Column Decorator所能实现的格式化功能只能针对单一的列进行，他所适用的情况包括对货币，时间，数字等类型进行统一格式化的情况，这样，就可以在应用中的所有表格中都能够使用该decorator进行格式化，大大提高了其可服用度。缺点嘛，从笔者个人的使用情况来看，column decorator不能对处于同一行的其他列的数据进行引用，对某些情况下的格式化很不方便，但这不属于Displaytag的问题，这些可以通过稍后将提到的Table decorator来实现。
 要使用column decorator，需要实现DisplayTag库中的ColumnDecorator接口，这个接口位于org.displaytag.decorator.ColumnDecorator。同时，实现静态的decorate（）方法。在这个方法中对要格式化列的数据进行格式化操作。或许这么说有些抽象，让我们来看一个例子。
 这个实例其实是DisplayTag自带的，我在这里只是进行简单的讲解。
 代码如下：
 import java.util.Date;
import org.apache.commons.lang.time.FastDateFormat;
import org.displaytag.decorator.ColumnDecorator;

public class LongDateWrapper
    implements ColumnDecorator
{

    private FastDateFormat dateFormat;

    public LongDateWrapper()
    {
        dateFormat = FastDateFormat.getInstance("MM/dd/yyyy HH:mm:ss");
    }

    public final String decorate(Object columnValue)
    {
        Date date = (Date)columnValue;
        return dateFormat.format(date);
    }
}
该实例要对当前列的Date对象进行某种形式的格式化操作（实际上是MM/dd/yyyy HH:mm:ss的形式，这可以在code中看到），所以他在格式化方法decorate中提取该列的Date对象，并进行cast，然后使用commons-lang包中的FastDateFormat类对该对象进行格式化后返回String形式的结果。
 与上原理所述，只要实现一个implemets了ColumnDecorator的类，并override自己相应的decorate（）方法就可以了。另外，为了提高性能，最好是将初始化的操作放到该类的构造函数中，否则，当iterate数据记录的时候还要初始化资源，那性能可想而知不会高到那里去的。
 4－2 table decorator阐述
 Table Decorator笔者使用的更多一些，他可以对整个表格的输出在显示之前进行格式化。表格在显示的时候，每次iterate到一行记录的时候，都会首先查询decorator中是否实现了对各个列的数据对象进行格式化的方法，如果有，则调用这些方法对当前数据对象进行格式化，然后返回格式化后的结果进行显示；否则，直接返回当前数据对象进行显示。
 如果说原始的数据对象不能够满足你数据显示需要的话，table Decorator就可以帮你忙。上面已经谈到过column Decorator遇到的问题：不能引用同行的数据对象，而现在table decorator就可以，你可以结合同一行的数据对当前行进行格式化，这在比如设置连接的时候要为连接设置不同的参数情况下特别有用。稍后实例将会有说阐明。
 要编写Table decorator，首先需要继承DisplayTag包中的TableDecorator类，该类的确切位置是：org.displaytag.decorator.TableDecorator。之后，因为我们要可以对没一列都能进行格式化，所以，针对每一个要格式化的字段，只要想javabean的属性getter方法那样，实现每个字段的getter方法，并在该方法中实现针对该字段的格式化逻辑。
 实例代码：
 import org.displaytag.decorator.TableDecorator;
import org.apache.commons.beanutils.*;

public class UserTableDecorator extends TableDecorator
{
    public UserTableDecorator()
    {
    }
    public String getUserId()
    {
        Object obj = this.getCurrentRowObject();
        DynaBean row = (DynaBean)obj;
        String deco = "<input type=checkbox name=userId value="+row.get("userId")+">"+row.get("userId");
        return deco;
    }
}
 这是笔者后面实例中将会用到的一个TableDecorator，他是用来实现表格显示的时候能够生成便于用户选择的checkbox，以便用户可以选择一条或者多条数据进行删除或者其他操作。他生成的界面类似于：
[img]/user11/darrenwang/upload/2004631737127283.gif[/img]
在这里，因为ValueObject的UserID字段只是返回一个String型的数据，这不能满足我们要显示checkbox的需要，所以，按照Javabean的getter方法形式，我们实现了public String getUserId()方法，并在这个方法中实现了将数据格式化为checkbox相关形式的操作。其中，只要取得了当前行的数据对象并cast成正确的对象类型，就可以调用该对象的方法使用同一行的其他列数据了。
    4－3 其他，像表格的嵌套和表格的排序，表格的总结行的添加等功能，希望读者能够自己研读DisplayTag Samples的代码。
 至此，有关Displaytag的decorator特性就算说完了。到现在为止，我们只是说了一些原理和简单的代码实例，为了给大家一个应用的意识，下面笔者就自己实现的一个简单工程做一个简单的描述，以期给大家一个更深的认识。
------------------------------------------------------------------------

[b]DisplayTag Tutorial by DarrenWang[/b]
                   CopyRight June，2004
                   By DarrenWang，All Rights Reserved！
------------------------------------------------------
话接上回：

[b]【table运行实例】[/b]

笔者原来做过几个有关Displaytag的demo，但是都只是研习用的，为了给出一个实际情况下的例子，笔者就前阵子的一个面试题给出一个用户管理流程的实现。当时要求用Hibernate跟persistence层交互，因为这跟我们的主题没有多大关系，所以，我们采用将数据以DynaBean的形式放入session来模拟数据的处理。
5－1运行环境说明
 IDE：Jbuilder X
 AppServer：Tomcat 4.0.6
 Framework：Struts1.1(with Tiles and Validator)
 Other: DisplayTag(这个当然要有了，^_^)，commons-beanutils，commons-lang2
 另外在进行实例的进一步剖析之前，建议使用以下的目录结构：
[img]/user11/darrenwang/upload/20046321185982226.gif[/img]
按照配置部分所述，建议将压缩包中的displaytag.war中的css目录和img目录都copy到当前的web应用根目录下面。另外，为所有的JavaScript文件建立单独的目录js（这里存放dTree的js类库），以便统一管理。因为我们使用到了Tiles，所以，拷贝整个Struts的Tiles应用中的layouts目录到当前应用目录。剩下的就是为工程用到的图片新建project-image目录，该名称可以变化，不要与img重复就可以。
 另外一个目录事init目录，笔者用来存放init.jsp文件以及其他初始化作用的文件，init.jsp文件的内容将在后面提到。
 最后，为每一个应用模块建立他们自己的目录，如memo和users，这些目录下面再根据需要细分其他子目录。
 Ok，环境就说到这里。
 5－2实例讲解
 该实例其实很简单，说白了也就是一个CRUD操作，就是管理员可以实现对用户的创建，读取，更新和删除等功能。不过，MIS要处理的不就是这这些嘛，呵呵，ok，let’s begin。
 因为什么都说的话太过繁琐，所以，笔者仅就整个流程和个别需要说明的内容做简单的描述，如果有兴趣读代码的话，可以email索取。
 在管理员进入管理页面之前，我们需要一个Action来读取用户列表，以便管理员处理，所以，笔者定义了PreUserListAction，在这里，笔者构造了两个DynaBean并存入样例数据，然后转入管理员页面。
 PreUserListAction的execute（）方法的代码片断：
 HttpSession session = httpServletRequest.getSession();
        List userList = new ArrayList();
        DynaProperty[] props = new DynaProperty[]{new DynaProperty("userId",String.class),new DynaProperty("type",String.class),new DynaProperty("userName",String.class),new DynaProperty("passWord",String.class)};
        BasicDynaClass userClass = new BasicDynaClass("user",null,props);
        try{
            DynaBean user1 = userClass.newInstance();
            user1.set("userId","00001");
            user1.set("type","admin");
            user1.set("userName","Darren");
            user1.set("passWord","112345");
            userList.add(user1);

            DynaBean user2 = userClass.newInstance();
            user2.set("userId","00050");
            user2.set("type","plain");
            user2.set("userName","susan");
            user2.set("passWord","2125465");
            userList.add(user2);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        session.setAttribute("UserList",userList);
        return actionMapping.findForward("suc");
 在转入用户管理页面后，管理员可以看到类似于下面的管理界面：
[img]/user11/darrenwang/upload/20046321194589246.gif[/img]
这个页面就是使用DisplayTag生成的表格，至于其生成，上面说的已经足够了，唯一需要说明的一点就是最后一列，他同样是通过<display:column>构造的，不同之处在于：
<display:column href="preUpdateAction.do" paramId="userId" paramProperty="userId" >
            UPDATE
</display:column>
 这一列使用了上面的href属性，指定该行记录更新的URL，唯一可能遇到的问题是，指定其为Struts的action 的时候，要使用以上的*.do形式，而以/preUpdateAction.do或者/preUpdateAction的形式指定的话，都不能找到处理的地址（虽然Struts的<html:link>可以）。
 在这里，管理员可以选取要删除的用户，点击“删除用户记录”按钮删除用户；或者要更新某个用户的信息的时候，点击最后一列的UPDATE连接，进去更新页面修改用户信息；又或者点击“AddUser”连接进入用户添加页面来添加用户。其中，用户添加页面和更新页面很相象，唯一的不同是前者表单中没有任何信息，后者则表单中已经包含了用户原来的信息。下面是用户添加页面的样子：
[img]/user11/darrenwang/upload/20046321202050447.gif[/img]
最后，所有的这些删除，新建和更新用户信息的功能实现笔者都在org.apache.struts.actions.DispatchAction的子类UserOptDispatchAction中实现了，因为代码太长，这里仅贴出代码实现的框架吧。[img]/user11/darrenwang/upload/2004632121372760.gif[/img]
至此，整个流程就算完成了，是不是很简单？！呵呵，本来就简单嘛，还用你说。hoho
      哦，对了，忘了说了，在每一个使用DisplayTag的页面，我们都会包含一个init.jsp页面，具体代码是在页头<%@ include file="/init/init.jsp"%>。Init.jsp页面导入已经在web.xml注册的各种taglib，以供页面使用。他的内容是：
 <%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page import="org.displaytag.sample.*, java.util.*,
                 org.displaytag.tags.TableTag"%>
<% TableTag.checkCommonsLang(); %>
 当然，读者也可以根据自己的考虑来实现这种形式的页面include，以提高页面的复用。

 除了使用DisplayTag外，该应用也使用了Tiles对这个应用的界面风格进行了统一，并且使用validator框架进行验证等，但这些不是这里的重点，所以不作赘述。

[b]【可能遇到的问题】[/b]

这是笔者在使用displaytag的时候遇到的或者是想到的一些问题，作为tips，列于下，以供参考。
 6－1 进行分页显示的时候，虽然第一页可以正常显示，而且分页条目也显示出来，但是，点击连接的时候不能显示下其他数据，可能显示找不到该页面等错误。
 这种情况多是因为没有在意数据存放的scope造成的，也是笔者刚开始使用的时候碰到的第一个比较伤脑筋的问题，其实当时只是为了试验，图方便而且也没过多在意，所有的数据都放在requestScope中了，而实际上，像分页这种情况，最合适的是应该将数据放入sessionScope中。否则，就会出现上面的情况。
 解决方法：将数据collection放入sessionScope中，指定<display:table>的name属性为sessionScope.yourInfoCollection的形式。
 6－2 点击页面连接或者分页连接的时候，页面跳转到开始页面，或者跳转的页面根本不是应该显示的页面，甚至使你有些莫名其妙。
 这种情况多是因为你使用了包含include机制的页面，包括Struts提供的Tiles功能。像这种情况下，应该为这些连接指定确定的连接，而不是默认的。或许解释的有些使你丈二和尚摸不着头脑了，别急，看下面的解决方法。
 解决方法：通过为<display:table>标签添加默认情况下不指定的requestURI属性，如果说你也不确定确切的连接的话，可以只这样写：requestURI=””, and that‘s enough。想要了解更清楚一些，看DisplayTag的FAQ，第三条就是。
 6－3这种情况应该算特殊情况，如果你不用Jbuilder的话，应该不会遇到，该情况跟6－2的情况有些类似，但是Jbuilder浏览器的问题，就是点击分页连接的时候，页面跳到登陆的页面。这不是displaytag的问题，即使你加了requestURI=””也是一样，应该是Jbuilder浏览器的问题，所以，建议在IE或者netscape下测试。

[b]【结束语】[/b]

这篇文章定位为一篇tutorial，意在于与大家共享。没有多少高深的理论，笔者也尽量将问题说的简单，尽量的step by step。如果大家通过Google或者其他途径了解并阅读了他，并且觉得好的话，希望给一些鼓励的话，或者发张电子贺卡之类也可以，以资鼓励。
 另外，我要感谢我现在的公司－江苏国光信息产业股份有限公司，虽然没有多少项目给我做，但是却给了我更多的时间，让我能够写这么些技术文章，与大家共享。

参考文献：
1． DisplayTag官方网站提供有各种相关的文档，http://displaytag.sourceforge.net
2． DisplayTag的FAQ或许有你所需要的答案，http://displaytag.sourceforge.net/faq.html
</pre>