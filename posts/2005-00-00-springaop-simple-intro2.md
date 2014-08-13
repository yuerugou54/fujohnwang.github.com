% 闲话SpringAOP的应用(2)
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

blog接上回...

昨天其实我都不知道写了些什么，呵呵，或许只是为了提一下那个event generator idiom 吧，不过，今天我可是想好好说说SpringAOP了。

我们先以我刚刚完成的那个双数据源互换的问题作为切入点来引出话题吧。

要想在primary的数据源crash掉之后，马上就可以切换到secondary数据源，我们需要在spring的配置文件中定义两个数据源，就是primaryDataSource和secondaryDataSource 。但是，为了屏蔽掉这两个数据源的差异性，我们需要提供一个屏蔽层，以便DAO或者说客户端在使用的时候不需要处理二者的差别性，而Spring提供的DelegatingDataSource类（org.springframework.jdbc.datasource包下面）恰好给我们提供了方便，而客户端也只需要跟这单一的数据源打交道就可以了。

所以，第一步，我们定义SwappableDataSource，它扩展DelegatingDataSource，可以指定当前的targetDataSource, 并且，我们为其提供primaryDataSource和色condaryDataSource的引用，而默认的targetDataSource就使用primaryDataSource。SwappableDataSource类的定义简单列举如下：

~~~~~~~ {.java}
public class SwappableDataSource extends DelegatingDataSource {
//
private DataSource primaryDataSource;
//
private DataSource secondaryDataSource;
//
private boolean isPrimaryCrashed = false;
// 
/*
* Swap the Datasouce between supplied 2 dataSources
*/
public void swap()
{
isPrimaryCrashed = !isPrimaryCrashed;
if(isPrimaryCrashed)
{
setTargetDataSource(this.getSecondaryDataSource());
}
else
{
setTargetDataSource(this.getPrimaryDataSource());
}
}


/**
* @return Returns the primaryDataSource.
*/
public DataSource getPrimaryDataSource() {
return primaryDataSource;
}
/**
* @param primaryDataSource The primaryDataSource to set.
*/
public void setPrimaryDataSource(DataSource primaryDataSource) {
this.primaryDataSource = primaryDataSource;
}
/**
* @return Returns the secondaryDataSource.
*/
public DataSource getSecondaryDataSource() {
return secondaryDataSource;
}
/**
* @param secondaryDataSource The secondaryDataSource to set.
*/
public void setSecondaryDataSource(DataSource secondaryDataSource) {
this.secondaryDataSource = secondaryDataSource;
}
}
~~~~~~~

当然，完成了类的定义后，我们需要在spring的config文件中配置它，下面是配置该bean的片段:

~~~~~~~ {.xml}
<bean id="dataSourceTarget" class="com.livedoor.finance.credit.monitor.dao.datasource.SwappableDataSource">
    <property name="targetDataSource">
        <ref bean="dataSourcePrimary"/>
    </property>
    <property name="primaryDataSource">
        <ref bean="dataSourcePrimary"/>
    </property>
    <property name="secondaryDataSource">
        <ref bean="dataSourceSecondary"/>
    </property>
</bean>
~~~~~~~

这个类提供了一个swap的公共方法，用来处理数据源的互换，这个方法的调用将在后面提到。

下面，我们有了数据源的屏蔽，并且也提供了swap的逻辑，那么，在那里调用那？或者说，当数据库crash掉之后，我们在那里通知SwappableDataSource来swap到备用的dataSource那？答案是在数据库crash后抛出异常的时候处理，而这里就需要用的AOP概念里面的Throws Advice。

与其他AOP的实现相同，SpringAOP提供了基本的Before Advice，AfterReturnningAdvice，Around Advice（在spring里使用aop alliance的interceptor概念）和Throws Advice，当然Introduction也提供了，但是我们现在用不上。在这几个Advice类型中，ThrowsAdvice显然就是我们所需要的东西，它可以在Exception发生的时候被激发，并调用其相应的实现逻辑。
在SpringAOP中，要实现org.springframework.aop.ThrowsAdvice接口，该接口是一个标志接口，没有提供具体的方法，但是，实现该接口的类必须提供一下至少一个方法的实现：
`void afterThrowing(Throwable throwable)`
和`void afterThrowing(Method method, Object[] args, Object target,Throwable throwable)`

在这里，我们需要后者，所以，因为我们要跟被Advised对象打交道，呵呵，所以，我们定义我们的DatabaseCrashThrowsAdvice如下：

~~~~~~~ {.java}
public class DatabaseCrashThrowsAdvice implements ThrowsAdvice
{
/*
* Intercept The DataSouce''s getConnection method after a SQLException occured.
* @author Darren.Wang
* 2005-3-18 11:15:11
*/
public void afterThrowing(Method m, Object[] args, Object target,SQLException exp)
{
logger.warn("Database crashed or can''t get connection from DB!",exp);
((SwappableDataSource)target).swap();
logger.warn("After the exception, We have swap to the other Datasource!");
}
}
~~~~~~~

这里我们拦截的是SQLException，而不是DataAccessResourceFailureException，因为我们现在是在DataSource这一层进行拦截，而不是dao层，只所以如此，是因为这样更简单有效，也是同事给出的意见，虽然我本人认为直接在Dao层拦截DataAccessResourceFailureException更好一些。不过，上面的实现有所简化，因为并非所有的SQLException都表示数据库连接失败，所以，要达到更好的或者说更精确，还需要根据不同的数据库vendor来分析ErrorCode和SQLState，这里就不作赘述了。

完成了Advice的定义，我们需要在spring的配置文件中配置之：

~~~~~~~ {.xml}
<bean id="dbThrowsAdvice" class="com.livedoor.finance.credit.monitor.advice.DatabaseCrashThrowsAdvice">
</bean>
~~~~~~~

完成了以上两项之后，基本的工作就算完成了，后面要做的就是如何将他们bind到一起，在springAOP中是这样做的：

配置Advisor，当然这步可以直接免掉，直接用ProxyFactoryBean指定一切，不过还是按部就班的好。Advisor是SpringAOP里面的概念，其他AOP实现并没有该概念，可以说是SpringAOP特有的，用它来将Advice和PointCut组织在一起（Spring uses the term advisor for an object representing an aspect, including both an advice and a pointcut targeting it to specific joinpoints――引自Spring Reference）。我们可以自己定义Advisor不过这在一般情况下是没有必要的，springAOP提供的几个实现类已经足够我们使用了，比如NameMatchMethodPointcutAdvisor和RegexpMethodPointcutAdvisor。

我们这里使用后者－RegexpMethodPointcutAdvisor ，且看配置：

~~~~~~~ {.java}
<bean id="advisor" class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
<property name="advice">
<ref local="dbThrowsAdvice"/>
</property> 
<property name="pattern"> 
<value>.*\.getConnection.*</value> 
</property> 
</bean>
~~~~~~~

要注意的一点就是，使用该advisor需要在classpath中包含jakarta－ORO，因为pattern的解释是用的该类库。

好了，现在Advice和pointcut都准备好了，怎么将他们与具体的被拦截对象绑定到一起那？使用ProxyFactoryBean。该类用来生成并返回相应的proxy对象，将要拦截的对象和相应的advice给他，剩下的就不用你管了。

该类同样是一个bean，呵呵，最常用的几个属性罗列如下：

* Target－要拦截的对象，advised Object
* proxyInterfaces－要代理的接口列表，以list形式提供，因为springAOP使用的是jdk1.3以后提供的dynamic Proxy机制实现的aop，所以，需要指定要拦截类实现的接口，如果说advised Object没有实现任何接口，那么springAOP会使用cglib类处理，但是该方法只限于遗留代码，不提倡使用；
* interceptorNames－加载到advisedObject之上的advice，advisor或者interceptor的列表；

其他属性可以参考Spring的javadoc。根据以上说明，我们以如下形式定义我们的proxyFactoryBean

~~~~~~~ {.java}
<bean id="dataSource" class="org.springframework.aop.framework.ProxyFactoryBean">
<property name="proxyInterfaces">
<value>javax.sql.DataSource</value>
</property>
<property name="target">
<ref bean="dataSourceTarget"/>
</property>
<property name="interceptorNames">
<list>
<value>advisor</value>
</list>
</property>
</bean>
~~~~~~~

这样，在beanFactory中取得的dataSource就是已经被advised之后的数据源了，当数据源的getConnection方法抛出SQLException之后，我们的ThrowsAdvice会被激发并调用SwappableDataSource的swap方法来取得可用的数据源，保证数据存储的持续进行。

现在的dataSource对于调用者来说，跟普通的数据源没有什么两样，比如：

~~~~~~~ {.java}
<bean id="mailDao" class="com.livedoor.finance.credit.monitor.dao.MailStateUpdateDao">
<property name="dataSource">
<ref bean="dataSource"/>
</property>
…
</bean>
~~~~~~~

OK，所有的东西都在上面了，希望这个或许可以给出一个使用SpringAOP的简单蓝图，呵呵，不过，有没有人想过那，现在只是advise单个的对象，而且ProxyFactoryBean的target的属性也只能提供单一的advise Target，如果说，我们的joinPoints分散在多个类甚至多个方法中，我们又要如何处理那？不至于要我们为每一个要advise的对象都手动配置一个ProxyFactoryBean吧？！苍天那，大地啊，不会吧？！呵呵，当然不会啦。

使用AutoProxy啊，什么autoproxy不知道是什么，那稍后在说吧，我可要吃饭了，大家都走了，就剩下我一个，我也闪先…

且听下回慢慢道来…