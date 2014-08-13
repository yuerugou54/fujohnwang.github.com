% 闲话SpringAOP的应用(3)
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

上回对最基本的SpringAOP概念进行了阐述和应用举例，这回我们将对SpringAOP的某些概念做更深入的探索。

按照以前的ProxyFactoryBean来处理分布于多个地方的joinpoint无疑是一场恶梦，不是嘛？！因为那样的话，我们需要为每一个需要被代理的类手动配置ProxyFactoryBean和相应的Advice跟pointcut，这显然不是我们所希望的。

鉴于此，SpringAOP提供了其autoproxy机制，该机制基于bean factory在初始化bean后期会对其进行post process原理实现，而且，只能用于ApplicationContext，而不能用于普通的BeanFactory（起码对于SpringAOP这部分来说，不然也不叫autoproxy了，呵呵），因为BeanPostProcessor接口的实现只是在ApplicationContext初始化的时候才会自动加载，而普通的BeanFactory只能通过编程的方式调用之。

在Spring In Action和Spring Reference中提到了两种基本的auto proxy Creator，他们是BeanNameAutoProxyCreator和DefaultAdvisorAutoProxyCreator。当然，要使用他们提供的便利你必须使用ApplicationContext而不是普通的BeanFactory，否则他们就不会auto了。

对于BeanNameAutoProxyCreator来说，他可以通过正则表达式的方式指定多个要被Advised的bean的名字，而且，可以指定多个interceptorNames，包括普通的MethodInterceptor，Advice甚至是Advisors。但是，一个要注意的问题就是，对于符合正则表达式的存在于当前Context下的所有bean来说，如果指定的是interceptor或者是advice的话，那么他们可不管你要拦截那个方法或者不想拦截那个，只要bean里面声明的方法，他们都会不管三七二十一，统统进行拦截并返回proxy后的结果；相对来说，指定Advisor倒是可以更精确的指定aop的粒度，因为Advisor包含了Advice和Pointcut，所以，Advice也只会应用于pointcut所表述的方法之上。

或许，给出一个实例可以更好的理解该类的功能和如何使用。

假设我们的系统实现存在一个Dao层，而该层的Dao中一般都会存在更新数据库或者查询数据库的操作，那么现在因为某种原因，我们的系统在数据库交换方面存在瓶颈，我们需要知道到底问题出在什么地方，所以，我们实现了一个ProfileInterceptor用来统计方法的执行时间，下面是该类的简单实现：

~~~~~~~ {.java}
public class ProfileInterceptor implements MethodInterceptor {
private static final Logger logger = Logger.getLogger(ProfileInterceptor.class); 
public Object invoke(MethodInvocation invocation) throws Throwable {
long t = System.currentTimeMillis();
Object o = invocation.proceed();
t = System.currentTimeMillis() - t;
logger.info(invocation.getMethod().gatName()+”consumed:”+t);
return o;
}
~~~~~~~

当然，我们不会单单将这个ProfileInterceptor应用于单一的一个dao上面，我们需要对所有存在于dao层的dao实现进行检测，故此，我们定义我们需要的BeanNameAutoProxyCreator如下：

~~~~~~~ {.xml}
<bean id="profileInterceptor" class="net.darrenstudio.springaop.advice.ProfileInerceptor”></bean>

<bean id="profileAutoProxyCreator" class="org.springframework.aop.framework.
autoproxy.BeanNameAutoProxyProxyCreator">
<bean>
<property name="beanNames">
<list>
<value>*Dao</value>
</list>
</property>
<property name="interceptorNames">
<value> profileInterceptor </value>
</property>
</bean>
~~~~~~~

这样，在调用每个dao实现的时候，其所有的方法调用和执行情况就可以全部记录下来了，通过分析log就可以得出一定的结果并加以改进。如果说要对某些方法，比如查询方法（我们假设这类方法都是以query开头的），我们可以定义一个Advisor，比如以前提过的RegexpMethodPointcutAdvisor，并指定advice为ProfileInterceptor，pointcut的形式为query.*，最后使用该advisor代替直接将ProfileInterceptor提供给BeanNameAutoProxyCreator类的interceptorNames属性就可以了。而且，如果说以后系统成熟之后不想用profile功能了，直接在spring的配置文件中注释掉上面的配置信息就可以了。当然，以上功能要起作用，不要忘了你要用ApplicationContext而不是BeanFactory哦。

说过了BeanNameAutoProxyCreator，下面我们说说DefaultAdvisorAutoProxyCreator吧！这个比前者更加智能，你都不用指定要处理哪些beans，也不用指定那个advice要应用到那个bean上面，你唯一需要关心的就是要注意这个类只会处理advisor类型的interceptor类型，而不会关心你到底提供了多少正常的MethodInterceptor或者advice。

DefaultAdvisorAutoProxyCreator类就像一个网络爬虫，他会爬遍整个的applicationContext，找到定义于该context下的所有advisor，然后将这些查找到的advisor应用于context定义的所有bean，当然前提是你的bean符合advisor的pointcut定义。不要忘了，advisor不但有advice的引用，他同样也有pointcut的定义啊，所以通过advisor，autoproxyCreator完全可以决定将哪个advisor应用于哪个bean。而使用DefaultAdvisorAutoProxyCreator现在也变得如此简单，只要在applicationContext中配置一下该类的一个bean实例就可以了：

~~~~~~~ {.xml}
<bean id="autoProxyCreator" class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"/>
~~~~~~~

不过，该类方便是方便，但是cross-cutting的粒度却不好掌握，往往某些我们不希望应用aop的类却被proxy了，从而造成程序行为的不正常。打一个不会造成不正常行为的例子吧，就以上面的profile为例，虽然大部分dao实现我们会乐意去检测，但是，某些无关紧要的dao我们明明知道不会造成问题的，我们当然不会希望去徒劳的检测他们吧？！谁也不可否认类似情况的发生，profile还好一些，如果说牵扯到影响程序逻辑的cross-cutting行为的时候，很容易就可以造成程序的不正常行为发生。所以说，在使用该类auto proxy的时候，需要仔细考虑，谨慎使用之。为了能够更好的控制cross-cutting的粒度，完全可以使用BeanNameAutoProxyCreator，只要分情况指定targets和相应的advisors就可以了，当然了，又要多写配置了，呵呵

好了，先写这么些吧，有时间的话后面在说说spring提供的metadata的支持吧！















