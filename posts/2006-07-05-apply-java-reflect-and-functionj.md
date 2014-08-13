% 反射及FunctionJ的应用（Apply Java Reflection and FunctionJ）
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

不知道大家在日常开发过程中有没有碰到类似的场景，即根据一个条件，后面会跟着多个（通常是两个）处理逻辑。我想，或多或少的会碰到这样的场景吧！反正，我回头想想，加上近期工作，也不少地方遇到了，所以，就想总结一下类似问题，能够列为best practice当然就更好了。

以近来做的一个报表为例吧，这个报表逻辑很简单，根据顾客支付利息的方式是先期支付还是后期支付或者是按月支付方式来打印不同的报表样式，这样总结到上面一般的情况就是，根据支付方式这一个条件，后继处理需要首先根据这种方式取得相应的报表样式（处理1），然后根据这种支付方式实现相应的报表render逻辑（处理2）。

伪代码的形式可能是：
<pre>
// get paymethod in your own way
if(paymethod==1)
{
//get template for paymethod 1
// render report for paymethod 1
}
if(paymethod == 2)
{
// get template for paymethod 2
// render report for paymethod2
}
// ...etc.
</pre>

我想这个是最一般的实现了，但是，最一般的实现通常也是最有改进余地的实现，所以，让我们来看一下如何对这个进行改进吧！
我们的目的是去除代码中的条件判断，而实现这个目的，我这里给出2个实现途径：

1. 使用反射（java reflection）支持
2. 使用FunctionJ的支持

不过，在此之前，我还是先说一下配置问题吧，为了简单，我们直接将条件和相应的处理映射放到properties文件中，这样，我们有了类似于如下内容的mapping-config.xml：
<pre>
1=templateLocation4PayMethod1,renderReport4PayMethod1
2=templateLocation4PayMethod2,renderReport4PayMethod2
...
</pre>

其中，等号前面是以payMethod的值作为key，出于其他情况考虑，你也可以使用其他的作为key，只要保证唯一性就可以；等号后面第一项为对应的模板的位置，第二项为对应的报表render方法，这个方法通常在实现类里实现。

（注：以上配置也可以通过Jakarta commons Configuration来实现或者自己写一个实现也可以）

东风有了之后，我们开始行船啦。

在主程序逻辑中，我们现在就不需要if判断语句了，让我们来看这种条件判断是如何去除的吧！

# 使用反射的方式(异常处理这里忽略）

~~~~~~~ {.java}
Properties mapping = new Properties();
mapping.load(getClass().getResourceAsStream("mapping-config.properties");
// get PayMethod in your own Way
// get Properties with your PayMethod as Key to Properties
InputStream templateIns = getClass().getResourceAsStream(properties[0]);// get Template For payMethod
String renderMethod = properties[1];
Method method = getClass().getDeclaredMethod(renderMethod,new Class[]{InputStream.class,other.class});
method.invoke(this,new Object[]{templateIns,otherObjects});// invoke render method for corresponding paymethod
// DONE!
~~~~~~~

这里省略掉一些读取配置信息的代码，只保留了可以说明问题的部分，在这里，我们首先load配置文件，然后，根据取得的PayMethod取得相应的Property值（这里要对取得的值进行相应处理，因为2个值是以一个字符串的形式返回的），之后，根据取得的模板位置取得模板，根据取得的方法名取得Method实例，最后，反射调用Method就可以了，只是，传入的参数要根据你的实现来决定。

（注：取得多个Property值最简单的方式就是用String的split方法来分割，你可以根据情况给出一个通用的实现，extends java.util.Properties类或者delegate it）

# 使用FunctionJ的方式
这个小东西很早以前就在TSS上看到过，不过，没有怎么用，因为给我的感觉他虽然提出了一些概念性的东西，但是，实现的话，实际上也只是在reflection基础上进行的封装，不过，如果你不想用reflection或者你想体验一下functionJ提供给你的功能的话，这部分你可能会感兴趣。（项目位置在http://functionalj.sourceforge.net/）

加载配置文件和加载模板的部分是相同的，我们只是列出方法调用部分的代码以示差别：

~~~~~~~ {.java}
// ...same as above codes
String renderMethod = properties[1];
Function f = new InstanceFunction(CurrentClass.class, renderMethod, this);
f.addParameters(new Object[]{parameters}).call();
// DONE!
~~~~~~~

怎么样？！没啥大的差别吧，呵呵，其实他的大部分实现类都是扩展的ReflectionFunction，这就是我为什么说他也仅仅是对reflection进行了一下封装。不过，你可以通过fuctionJ了解一些概念性的东西。

OK，我们通过以上两种方式都去除了多重的条件判断，目的达到，虽然看起来没有省去太多代码，不过，如果你的条件判断语句很多的话，你就不会这么说了，而且，这种方式的可扩展性很好，不妨试试？！:-)