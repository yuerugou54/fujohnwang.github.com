% ClassWorking With Spring Framework

# ClassWorking Utilities In Spring Framework

## org.springframework.core.GenericCollectionTypeResolver

> Helper class for determining element types of collections and maps. Mainly intended for usage within the framework, determining the target type of values to be added to a collection or map (to be able to attempt type conversion if appropriate). Only usable on Java 5. Use an appropriate JdkVersion check before calling this class, if a fallback for JDK 1.4 is desirable.

从javadoc中的说明就可以猜到，像getCollectionType(), getMapKeyType()和getMapValueType()之类的常用方法， 在这个类里是不会缺的。

org.springframework.core.GenericTypeResolver跟GenericCollectionTypeResolver类似， 只不过， 现在不是针对Collection类型， 而是针对一般的Generic类。

## org.springframework.core.annotation.AnnotationUtils

AnnotationUtils填补了默认的反射API中有关Annotation相关的几个功能，比如， 使用默认的反射API你只能读取当前class上的Annotation，而通过AnnotationUtils， 我们则可以获取当前class继承层次上的所有标注的Annotation， 更多信息可以参考该类的Javadoc.

<pre>
Tip

有关Annotation的继承， 这里可以总结几句：

1. 如果Annotation是标注于Interface之上的话， 不管该Annotation类型是否标注了@Inherited这个Annotation， 被标注的Interface的实现类都不会“继承” 这一Annotation。 也就是说，标注于Interface上的Annotation，通过反射API只能在这个Interface上才能读取到这一Annotation。

2. 如果Annotation是标注于Class之上的话， 分两种情况：

    - 如果当前的Annotation类型被标注了@Inherited，那么， 标注了当前Annotation的Class的子类会获得当前Annotation的“继承”， 也就是说， 即使子类没有标注这个Annotation，通过subClass.getClass().getAnnoation(currentAnnotation)也会获得这个Annotation所持有的元数据信息。

    - 如果这个Annotation没有被标注@Inherited，那么， 子类不会获得这种继承。

结合以上信息， 也就不难想到为什么Spring会倡导将Transactional之类的Annotation标注在具体的实现类上，而不是Interface之上了。
</pre>

## org.springframework.util.ClassUtils

在 org.apache.commons.lang.ClassUtils存在的前提下，有重新发明轮子之嫌， 不过， 还是有一些补足的， 用与不用完全看你个人口味啦。不过，千万别跟我提“绑定到XX框架”这样的论调， 除非你真的能够像古代那样做个自给自足的隐士！

##  org.springframework.util.MethodInvoker

> Helper class that allows for specifying a method to invoke in a declarative fashion, be it static or non-static. Usage: Specify "targetClass"/"targetMethod" or "targetObject"/"targetMethod", optionally specify arguments, prepare the invoker. Afterwards, you may invoke the method any number of times, obtaining the invocation result. Typically not used directly but via its subclasses MethodInvokingFactoryBean and MethodInvokingJobDetailFactoryBean.

主要适用于对某个确定的方法调用多次的场景， 如果每次都是调用不同的对象上的不同的方法的话， 需要提供多个该类的实例。 如果是在Spring容器内使用的话， 大多数时候会是使用MethodInvokingFactoryBean或者MethodInvokingJobDetailFactoryBean这样的子类。


##  org.springframework.util.ReflectionUtils

这个类主要对Java ReflectionAPI进行了封装， 虽然它的javadoc中声明仅限于框架内部使用，不过如果你不介意的话， 也可以拿来一用吧！不过，后果自负，哈

# 取得方法参数名称

通过Java标准Reflection API是取不到方法参数的名称的，要达到这个目的，除了可以通过读取class文件中的debug信息（可能在compile的时候没有启用）， 也可以使用类似ASM这样的类库获得， 不过， Spring framework中已经对读取方法参数名称这一功能进行了抽象和实现，我们可以直接拿过来用， 这一抽象接口为org.springframework.core.ParameterNameDiscoverer。

ParameterNameDiscoverer主要有三个实现类：

1. LocalVariableTableParameterNameDiscoverer
2. PrioritizedParameterNameDiscoverer
3. AspectJAdviceParameterNameDiscoverer

其中， LocalVariableTableParameterNameDiscoverer为最常用的实现类， 它会使用ObjectWeb的ASM来读取方法参数名称。

> WARNING
> 
> LocalVariableTableParameterNameDiscoverer只能发现具体实现类的Class上的方法参数名称，对于接口类型的Class上的方法参数名称， 需要另寻他路，比如扫描接口的实现类并匹配相应方法，然后读取实现类相应方法的参数名称 ，或者直接通过Annotation标注相应的方法参数， 然后通过反射API读取等等。

> NOTE
> 
> Codehaus的[ParaNamer](http://paranamer.codehaus.org/)可以完成同样的任务

PrioritizedParameterNameDiscoverer纯粹是借助于它所持有的多个ParameterNameDiscoverer来完成方法参数名称的获取工作，它会按照顺序请求它所持有的ParameterNameDiscoverer来完成每一个ParameterNameDiscoverer本应完成的任务。

AspectJAdviceParameterNameDiscoverer主要用于集成了AspectJ的SpringAOP Advice实现类， 通过它的Javadoc可以获得进一步的详细信息。

以下是LocalVariableTableParameterNameDiscoverer和ParaNamer的简单使用代码示例：

~~~~~~~ {.java}
Class<SubscriptionServiceImpl> clazz = SubscriptionServiceImpl.class;
Method method = ReflectionUtils.findMethod(clazz, "listAllSubscription",new Class<?>[]{String.class, Integer.class,Date.class,Date.class} );

ParameterNameDiscoverer pmDiscoverer = new LocalVariableTableParameterNameDiscoverer();
String[] parameterNames = pmDiscoverer.getParameterNames(method);

//        BytecodeReadingParanamer reader = new BytecodeReadingParanamer();
//        String[] parameterNames = reader.lookupParameterNames(method);

for(String parameterName : parameterNames)
{
    System.out.println(parameterName);
}    
~~~~~~~

# classpath auto scanning 功能

我们知道，Spring2.5之后引入了类路径的自动扫描功能，Spring框架为这一功能提供了很好的扩展点，我们可以通过BeanNameGenerator或者自定义的org.springframework.core.type.filter.TypeFilter来定制自动扫描的行为， 比如：

~~~~~~~ {.xml}
<context:component-scan base-package="..." name-generator="..">
 	<context:exclude-filter type="annotation" expression="..."/>
 	<context:include-filter type="annotation" expression="..."/>
 </context:component-scan>
~~~~~~~

除此之外，我们实际上还可以较为独立的形式来使用Spring框架提供的这一类路径自动扫描功能，或者说， class的元数据信息读取功能。 这一功能的核心类为MetadataReader， 以下代码给出了使用这一核心类的简单示例代码：


~~~~~~~ {.java}
ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
// or new SimpleMetadataReaderFactory()
MetadataReader reader = metadataReaderFactory.getMetadataReader("cn/spring21/code/samples/spring/controller/SimpleController");
AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
ClassMetadata clazzMD = reader.getClassMetadata();
System.out.println(annotationMD.hasAnnotation("org.springframework.stereotype.Component"));
System.out.println(clazzMD.getClassName());
~~~~~~~

ResourcePatternResolver主要用于加载相应的Resources， 这里就是class文件， MetadataReaderFactory用于构造相应的MetadataReader，它有两个实现类，即SimpleMetadataReaderFactory和CachingMetadataReaderFactory。通过相应MetadataReaderFactory实现类获得可用的MetadataReader之后，就可以根据MetadataReader的getAnnotationMetadata()和getClassMetadata()返回的值对象来提取自己需要的信息了。








