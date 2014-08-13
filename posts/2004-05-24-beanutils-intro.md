% 随笔20040524（Beanutils）
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

上午看了一些Hibernate的faq等文档，时间过的也真快，一不留神就中午了。

下午leader先开了一个会，讨论SFA的CM模块的实现问题，只是让我们先看一下截屏图，星期三再讨论，呵呵，公司想自己做这个CRM，但是好像情况并不乐观，而且是否能跟日立达成汉化改造的协议还难说，所以leader只是打算让我们组的人先从abstract层面了解一下，抽象一些共通的部品。开完会，我也没有去钻研截屏图，呵呵，是不是有些不敬业啊？！:em211:不过，话说回来了，那些东西其实也没有多少花露水，明天研究一下吧！仿制，埃...leader都实说了，自己没有数据源，做这些没有什么东西，所以他也没有让我们从头做到尾，免得到时候日立那边下来，这些都白做，也打击我们的积极性。
  
因为近来又看了些hibernate的documentation，所以联想到VO在各个层之间的数据传递问题，就想起以前看过的commons beanutils，所以重新扫描了一遍，部分内容如下：

首先需要说明的是PropertyUtils类，这个类提供了对javabean和动态bean的属性进行访问的各种方法，因为beanutils将bean的property分为simple，indexed和mapped三种，所以，这个类提供了get/setSimpleProperty(...),get/setIndexedProperty(...),和get/setMappedProperty(...)等方法分别针对各种property的访问。另外，除了以上这些property访问方法，还有get/setNestedProperty(...)和get/setProperty(...)方法，前两个针对bean的property依然是bean的property的访问问题，后者则是通用的property访问方式。下面是简短的代码实例：

~~~~~~~ {.java}
Employee employee = ...;
String city = (String) PropertyUtils.getProperty(employee,
      "subordinate[3].address(home).city");
~~~~~~~

其他的property访问方法的parameter跟这个代码实例类似，如果还不明白可以参考BeanUtils的API。

除了以上方法，PropertyUtils还提供了copyProperty（...)等静态方法，可以查阅API 文档以取得更多信息。


其次我们要说的就是DynaBean和DynaClass接口了，我们可以通过为DynaClass的实现类提供DynaProperty类型的数组作为参数，调用其newInstance（）方法返回相应的DynaBean实例，之后，初始化该生成的bean并进行相应的处理。

下面是代码实例：

~~~~~~~ {.java}
 DynaProperty[] props = new DynaProperty[]{
        new DynaProperty("address", java.util.Map.class),
        new DynaProperty("subordinate", mypackage.Employee[].class),
        new DynaProperty("firstName", String.class),
        new DynaProperty("lastName",  String.class)
      };
BasicDynaClass dynaClass = new BasicDynaClass("employee", null, props);
DynaBean employee = dynaClass.newInstance();
employee.set("address", new HashMap());
employee.set("subordinate", new mypackage.Employee[0]);
employee.set("firstName", "Fred");
employee.set("lastName", "Flintstone");
~~~~~~~

DynaBean和DynaClass的实现类分别可以归纳为【BasicDynaBean,ResultSetIterator,WrapDynaBean】和【BasicDynaClass, ResultSetDynaClass, RowSetDynaClass, WrapDynaClass】两组。其中，BasicDynaBean和BasicDynaClass在上面的代码中已经演示过了，剩下的尤其属ResultSetDynaClass, RowSetDynaClass两个类偶认为最为实用。他们可以对SQL查询后返回的ResultSet进行wrap，这个在web应用中很有用，你可以再也不用就数据在各层之间传递而写无数的相应的VO beans了。不过，二者也存在一定的区别。ResultSetDynaClass只能在保持数据库连接的情况下构造DynaBean并进行处理，这在很大程度上限制了其应用，因为不管怎么样，你应该将connection尽快的返回pool中而不是一直占有它，否则资源将很快的耗尽，这一点显而易见。
 
不过，RowSetDynaClass则可以在数据库连接关闭的情况下返回数据bean并处理，它将data拷贝到memory，所以这种方法是以性能和内存的消耗为代价的，不过，这种情况对于web应用来说，却是很划的来。
 
下面我们看看Beanutils文档提供的代码实例，不妨做一个比较：


~~~~~~~ {.java}
Connection conn = ...;
  Statement stmt = conn.createStatement();
  ResultSet rs = stmt.executeQuery
    ("select account_id, name from customers");
  Iterator rows = (new ResultSetDynaClass(rs)).iterator();
  while (rows.hasNext()) {
    DynaBean row = (DynaBean) rows.next();
    System.out.println("Account number is " +
                       row.get("account_id") +
                       " and name is " + row.get("name"));
  }
  rs.close();
  stmt.close();
~~~~~~~


~~~~~~~ {.java}
Connection conn = ...;  // Acquire connection from pool
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT ...");
    RowSetDynaClass rsdc = new RowSetDynaClass(rs);
    rs.close();
    stmt.close();
    ...;                    // Return connection to pool
    List rows = rsdc.getRows();
    ...;                   // Process the rows as desired
~~~~~~~

是不是后者更适合web应用那？！

除了上面的一些实现类，还剩下了WrapDynaBean和WrapDynaClass类，他们同样相应的实现了DynaBean和DynaClass接口，作用就是：如果你愿意所以的bean访问（）都通过beanutils包的PropertyUtils类方式来访问，那么你可以通过这两个类对现有的bean进行wrap，例如：

~~~~~~~ {.java}
MyBean bean = ...;
DynaBean wrapper = new WrapDynaBean(bean);
String firstName = wrapper.get("firstName");
~~~~~~~

虽然在这里看不到WrapDynaClass的影子，不过不用担心，实际上在程序内部已经构造了它的实例，只是我们无须关心而已。

以上差不多对beanutils提供 的主要功能类说的差不多了吧，不过，还有一点点儿，呵呵，那就是BeanUtils和ConvertUtils类以及Converter接口。

其实那，beanutils包的本来目的就是由BeanUtils撑大纲的，如果了解struts的历史，也应该知道这些，或许它的doc提供的这段代码可以很好的解析其原来的用意吧：

~~~~~~~ {.java}
    HttpServletRequest request = ...;
    MyBean bean = ...;
    HashMap map = new HashMap();
    Enumeration names = request.getParameterNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      map.put(name, request.getParameterValues(name));
    }
    BeanUtils.populate(bean, map);
~~~~~~~

另外，它底层会使用ConvertUtils进行各种数据类型的cast，而ConvertUtils提供的一些方法也可以直接使用，但是因为版本更新和未来的考虑，并不提倡直接使用它提供的一些方法，而是推荐实现自己的转换类，但前提是implements Converter接口，呵呵

还有就是如果考虑到I18N的问题，也就是Locale-Aware,可以使用org.apache.commons.beanutils.locale包中提供的相应类，因为缺省的org.apache.commons.beanutils包中提供的类不支持locale-aware。
   
最好要说的是，在org.apache.commons.beanutils.converters包下面提供了许多的converter类，至于怎么使用我就想赘述了，只要会查API doc，使用他们就不会有太多问题。
   
ok，写的我头疼脖子酸，休息先....













