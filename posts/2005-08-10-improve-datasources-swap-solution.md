% 对双数据源互换实现的改进（improve to 2-dataSource swapping）
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！


好久没有写blog了，随便找一个话题吧！就从原来的那个双数据源互换的问题说起吧！

下图是当时的实现演示（图片来自SpringIDE的BeanView）：



当时是使用了Spring提供的org.springframework.jdbc.datasource.DelegatingDataSource类，使他拥有两个数据源的引用，默认的targetDataSource指向第一个数据源，当ThrowsAdvice捕获异常之后，重新替换掉targetDataSource指向的数据源引用。我不确定这种方式是否是最好的，但是好像某些情况下不是很可靠，所以，后来改为了以下的实现形式：



现在我们采用Spring提供的org.springframework.aop.target.HotSwappableTargetSource类，构造的时候就直接让其默认为第一个数据源，bean的配置类似于：

~~~~~~~ {.xml}
<bean id="hotSwapTarget" class="org.springframework.aop.target.HotSwappableTargetSource">
	<constructor-arg><ref bean="dataSourcePrimary"/></constructor-arg>
</bean>
~~~~~~~

有了TargetSource之后，我们就可以使用ProxyFactoryBean将其包装起来，给外层引用了，但是在这之前，我们要实现我们的主要功能，即某一个数据源崩溃之后，我们要马上切换到可用的数据源:

~~~~~~~ {.java}
targetSource.swap(isPrimaryUsed?slave:primary);
isPrimaryUsed = !isPrimaryUsed;
~~~~~~~

这样，我们所有需要准备的都ok了，最后要做的就是组装TargetSource和Advice了。下面就简单得罗列部分配置，或许更能帮助大家了解SpringAop的使用：

~~~~~~~ {.xml}
<bean id="swapThrowsAdvice" class="com.livedoor.finance.credit.ivr.aspects.SwapSourceThrowsAdvice">
	<property name="primary"><ref bean="dataSourcePrimary"/></property>
	<property name="slave"><ref bean="dataSourceSecondary"/></property>
	<property name="targetSource"><ref bean="hotSwapTarget"/></property>
</bean>
 
<bean id="swapThrowsAdvisor" class="org.springframework.aop.support.RegexpMethodPointcutAdvisor">
	<property name="advice"><ref local="swapThrowsAdvice"/></property>
	<property name="pattern"><value>.*getConnection.*</value></property>
</bean>
 
<bean id="dataSource" class="org.springframework.aop.framework.ProxyFactoryBean">
	<property name="targetSource">
		<ref bean="hotSwapTarget"/>
	</property>

	<property name="interceptorNames">
		<list>
			<value>swapThrowsAdvisor</value>
		</list>
	</property>
</bean>
~~~~~~~

这样，最终外层所引用的dataSource就可以在某个数据源崩溃之后，直接切换到可用的备用数据源，而这些都在Proxy内部屏蔽掉了，客户端无需关心这些。

好了，今天就扯这些，呵呵
