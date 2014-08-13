% 使用Velocity模板生成枚举类
% FuqiangWang
% 2005/02/01

> 2014年从msn space存档中重新恢复出来！

今天临时要对原来完成的邮件监控程序进行改进，所以将手头上的证券化业务实现先放在一边。鉴于前者也是我实现的，故此改动很小，一会儿就完成了，有点儿空闲时间，所以浏览了一些技术文章，鬼使神差地就又摸起来了Velocity。呵呵，记得2003年底对他研究过一阵子，但是，工作上很少用，虽然是好东西，但是后来还是冷落了他，今天猛然一个念头，想起以前看过Eclipse有个能自动生成枚举类的插件，觉得挺方便，也就想起用Velocity也来做个原型，呵呵，下面是重新开始的历程。
 
使用模板引擎生成结果的思路基本就是：数据＋模板 >>>通过模板引擎处理>>>生成所需要格式的结果。
 
我们先从模板开始，下面是我的enum类模板：

~~~~~~~ 
 package packageName;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.enum.Enum;

public final class clazz extends Enum
{
#foreach( enum in enums )
 public static final clazz enum.toUpperCase() = new clazz("enum");
#end

 private clazz(String name) 
 {
      super(name);
    }
 
    public static clazz getEnum(String name) 
    {
      return (clazz)getEnum(clazz, name);
    }
 
    public static Map getEnumMap() 
    {
     return getEnumMap(clazz.class);
    }
 
    public static List getEnumList() 
    {
      return getEnumList(clazz.class);
    }
 
    public static Iterator iterator() 
    {
      return iterator(clazz.class);
    }
}
~~~~~~~

上面的模板很简单，有兴趣可以去查一下Velocity的VTL Reference，很容易理解。
 
 有了模板，我们需要提供数据，初步设想是需要调用者提供枚举类的包名，枚举类的类名，枚举类里各个枚举以及要输出的路径。输出路径简化为只提供Writer作为接口。所以，提供数据输入的接口方法的signature就初步定为：`public static void buildEnum(String pkg,String clazz,List enums,Writer writer)`

 到这里，我们先不管到底提供的数据是什么样子，数据输入接口确定后，我们下一步要做的就是将数据和模板融合在一起以得到我们所需要的结果。
 要merge模板和数据，我们简单将步骤总结为四步：

1. 初始化Velocity模板引擎；
2. 为模板准备数据，也可以说将数据导入context，以便Velocity将其与模板相融合；
3. 取得模板，呵呵，很容易想到啊；
4. 更容易想到，“万事具备”了嘛，当然就是将数据和模板merge一下咯

好了，说了这么些废话，还是用code说话吧：


~~~~~~~ {.java}
// 1.init Config
   InputStream ins = EnumBuilder.class.getResourceAsStream("v.properties");
   Properties prop = new Properties();
   prop.load(ins);
   Velocity.init(prop);
   // 2.prepare Data
   Context context = new VelocityContext();
   context.put("packageName",pkg);
   context.put("clazz",clazz);
   context.put("enums",enums);
   // 3. prepare template
   Template template = null;
            try 
            {
                template = Velocity.getTemplate("net/darrenwang/commons/demo/lang/enum/utils/enum.vm");
            }
            catch( ResourceNotFoundException rnfe )
            {
                System.out.println("Example : error : cannot find template ");
            }
            catch( ParseErrorException pee )
            {
                System.out.println("Example : Syntax error in template ");
            }
            
            // 4. merge the template and context
            if ( template != null)
                template.merge(context, writer);
~~~~~~~

好，以上就是所有的代码了，细节（象异常处理什么的）这里就不写了，大家都明白，是吧？！

 主要工作完成的差不多了，让我们调用我们的EnumBuilder看看:
 
~~~~~~~ {.java}
// 1.
  String pkg = "net.darrenwang.enum";// Package Name of the Class
  String clazz = "ColorEnum";        // Enum Class Name
  List enums = new ArrayList();      // Enum Names
  enums.add("Red");
  enums.add("Green");
  enums.add("Yellow");
  enums.add("Grey");
  // 2.
  Writer writer;
  try {
   writer = new FileWriter("ColorEnum.java");
//   writer = writer = new BufferedWriter(new OutputStreamWriter(System.out));
   EnumBuilder.buildEnum("net.darrenwang.enum","ColorEnum",enums,writer);
  } catch (IOException e) {
   e.printStackTrace();
  }
~~~~~~~

 哈哈，大功告成！不过，来之不易啊，有谁知道要让这么简单的东西出来，让我浪费了多少时间啊，呵呵，经验教训还是要总结di ，hoho
 
<pre>
NOTE：  实际上，在没有重新看Velocity的文档之前，我已经忘记了Velocity的Resource Loader策略问题，所以，在刚开始的Velocity引擎初始化的时候，和取得模板的时候，都直接制定Properties的文件名和模板文件名的时候，根本就找不到，大家都讨厌的异常自然就频频出来烦我咯。

  所以，我们有必要说一下Velocity的资源加载策略。Velocity有自己的资源加载策略：文件加载，classpath加载，jar加载和Datasource加载（FileResourceLoader，JarResourceLoader, ClasspathResourceLoader and DataSourceResourceLoader）。
  
  因为Velocity默认的配置文件velocity.properties（该文件位于org.apache.velocity.runtime.defaults包下面）中指定默认的加载策略是文件加载策略，所以，如果按照这种策略的话，要想正确加载模板文件，我们就要提供自己的配置文件来覆盖原来默认的配置项，比如file.resource.loader.path=F:\\eclipse\\workspace\\JakartaCommons\\bin\\net\\darrenwang\\commons\\demo\\lang\\enum\\utils
  
但是，这tmd不是太烦了嘛，如果我要把这个utils类拿到别的地方，岂不是要每次都配置这么长的路径，也太蠢了吧？！而且tmd刚开始调用Velocity.init(“velocity.properties”)的时候，同样会因为这个该死的加载策略而找不到我们自己的配置文件，所以，我才会退而求最common的getResourceAsStream，呵呵，故此，这样的初始化代码才会出现：
   InputStream ins = EnumBuilder.class.getResourceAsStream("v.properties");
   Properties prop = new Properties();
   prop.load(ins);
   Velocity.init(prop);
   
但是，Template可没有这么友好，你想再这样曲线救国的话，就不行咯，因为他只提供了String参数的方法，上面的方法当然就行不通了，那么，how？
 答案嘛，自然有，他既然提供了4中加载策略，当然不会是瞎忙活了，而那个ClasspathResourceLoader就是我们想要的东东，呵呵，这也是我上面的代码template = Velocity.getTemplate("net/darrenwang/commons/demo/lang/enum/utils/enum.vm");不会再抛出那该死的异常的原因，呵呵，而这所有的奥秘，也就在我们这个后来加载的v.properties中：
#######################################################################
#   Syntax: 1. resource.loader = <loader.alias>                       #
#           2. <loader.alias>.resource.loader.class = <loader.class>  #
#                *** THE 2 POINT IS A MUST  ***                      #
#######################################################################
resource.loader = classpath
classpath.resource.loader.description = Classpath Resource Loader
classpath.resource.loader.class = org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
classpath.resource.loader.path=.

 看到了吧，我们用classpath的加载策略，而这里面resource.loader=后面的名字时间上是可以随便起的，唯一起决定作用的是classpath.resource.loader.class = org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader。
 
配置语法我用自己的注释写在上面，个人认为也能够很好的表达我的意思了。

 好了，该说的都说完了，呵呵，就此打住吧，至于jar和dataSource方式的加载，有需要的话，自己查咯…
 
 另，Velocity在初始化的时候，会首先加载默认的配置，如果后来有新的配置，他会用后来的配置项覆盖默认的配置项，从而，我们提供的classpath方式的加载策略可以生效。

:em510:

         哦，对了，上面的模板中用到了lang的enum类，虽然tiger已经有了enum的支持，但是1.4不是还没有嘛，呵呵
</pre>

