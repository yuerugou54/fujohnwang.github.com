% Spring使用中的陷阱和诀窍（Spring Gochas and Tips）

本来想就这个写一系列的东西，不过， 很难拼凑足够数量的案例(自然也成不了书)，所以， 先暂且随手捡两个掰一掰， 一个黑的，一个红的...

# 陷阱(Gochas)

## 说事儿

话说PACMAN项目需要对DTC来的消息处理过程进行优化，这活儿到我头上了， 本来吧， 小事儿， 虽然数据量一下子从两三万蹿到100万左右， 首先咱可以提高worker threads的数量嘛， 实在不行， 咱再考虑分布， 但这是宏观上的， 这里的小陷阱存在于细节中， 有句话叫做“The problem isn't design; it's implementation.”， 说的就是这个道理， 不过，这有些扯远了...

程序重构完了，该测试了。 第一次启动Weblogic， 测了一轮， 有些地方需要持续修正， 程序部署之后， 重新启动Weblogic，当当， 异常来啦！

<pre>
javax.jms.JMSException: Duplicate durable subcription detected
        at com.tibco.tibjms.Tibjmsx.buildException(Tibjmsx.java:562)
        at com.tibco.tibjms.TibjmsSession._createConsumer(TibjmsSession.java:437)
        at com.tibco.tibjms.TibjmsSession._createConsumer(TibjmsSession.java:366)
        at com.tibco.tibjms.TibjmsSession.createDurableSubscriber(TibjmsSession.java:3950)
        at com.tibco.tibjms.TibjmsTopicSession.createDurableSubscriber(TibjmsTopicSession.java:114)
        at com.citigroup.posmgmt.util.consumer.JMSTopicConsumer.initialiseService(JMSTopicConsumer.java:105)
        at com.citigroup.posmgmt.util.consumer.DTCMsgConsumer.initService(DTCMsgConsumer.java:146)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:585)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeCustomInitMethod(AbstractAutowireCapableBeanFactory.java:1133)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1095)
        at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:396)
        at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:233)
        at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:145)
        at com.citigroup.posmgmt.ejb.AdminMsgHandler.startRealTimeConsumer(AdminMsgHandler.java:135)
        at com.citigroup.posmgmt.ejb.AdminMsgHandler.onMessage(AdminMsgHandler.java:68)
        at weblogic.ejb.container.internal.MDListener.execute(MDListener.java:429)
        at weblogic.ejb.container.internal.MDListener.transactionalOnMessage(MDListener.java:335)
        at weblogic.ejb.container.internal.MDListener.onMessage(MDListener.java:291)
        at weblogic.jms.client.JMSSession.onMessage(JMSSession.java:4072)
        at weblogic.jms.client.JMSSession.execute(JMSSession.java:3962)
        at weblogic.jms.client.JMSSession$UseForRunnable.run(JMSSession.java:4490)
        at weblogic.work.ExecuteRequestAdapter.execute(ExecuteRequestAdapter.java:21)
        at weblogic.kernel.ExecuteThread.execute(ExecuteThread.java:145)
        at weblogic.kernel.ExecuteThread.run(ExecuteThread.java:117)
</pre>


他娘的， 不能把？！怎么隐约记得之前好象测试其他地方的时候也遇到过类似的问题那？ 不过， 那时候也不知道怎么糊弄过去了， 看来不成， 确实哪里有问题啊！

EMS服务器还是人家NY的， 咱也上不去阿， 能上去也好看一下是不是其他连接在那里， 到底谁在捣乱， 发信给NY，告诉没权限， nnd， google吧， 围绕EMS和这个异常折腾了好是一通， 未果。 忘了都折腾啥了， 反正我是彻底的翻了一遍code和spring配置文件， destroy方法真真切切的在那里，我都挨个字母得对名称啊， 对完了也没发现有问题。

最后， 不管了， 出去溜达了一圈， 回头一想， tnnd，不会之前的代码没有触发对象销毁的行为吧？不然，这多出来的连接到底哪里来的？好像除了我也没人在做同样的事情啊！ 查入口类StartupManager， 得， 还真他娘的是这么回事， shutDownService（）方法只有抛出Exception的情况下才会被调用到， 正常情况下， 反而没有被调用， 靠， 这代码都跑了2年了， 这帮人居然没发现， 还好意思说， 代码都跑了两年了，没有遇到这样的问题啊， 这Batch从市场关闭到市场开启的近乎半天时间里， 你就算不关Connection， 那服务器那边也会按照超时给你“咔嚓”掉吧？这样的跑了两年当然没问题啦！

## 结论

生活在Spring容器中的对象， 他们的生命周期是由容器来管理的， 但是， 仅仅通过实现DisposableBean接口或者通过destroy-method配置项， 甚至通过JSR250的@PreDestroy标注， 容器并不会在程序退出的时候，自动调用这些对象销毁的回调，我们必须在程序的主入口类或者其它合适的位置明确告知Spring容器， “哥们， 我要下班了，你把那些家伙都收拾了吧！”， 之后， 你通过以上所有方式指定的对象销毁逻辑才会被调用，否则， 哼哼...

用行话来说就是， 如果你使用的是BeanFactory类型的IoC容器，那么，你最好在容器构造完成后， 保险起见，顺便加上以下这样的代码片断:

~~~~~~~ {.java}
Runtime.getRuntime().addShutdownHook(new Thread(){
		@Override
		public void run() {
		    if(beanFactory != null){
			beanFactory.destroySingletons();
		    }
		}
    });
	
~~~~~~~

如果你使用的是ApplicationContext类型的IoC容器，那么， 与上面类似

~~~~~~~ {.java}
ApplicationContext ctx = ...;
((AbstractApplicationContext)ctx).registerShutdownHook();
~~~~~~~

当然了， 道理实际上跟上面BeanFactory对应的处理差不多。

这样的处理虽然不能保证100%的安全，不过，只要你不是太粗鲁的对待你的应用程序，比如kill -9或者直接System.halt()之类， 这道安全网还是必要的。

> NOTE
> 
> 这个问题实际上早就写入了《Spring揭密》之中，只不过， 既然书还没出来，那就再重复一下这一言论吧！

# 诀窍(Tips)

## 说事儿

还是跟JMS连接有关， 话说纽约DDI Team某天突然发彪，说， “要连接到我们的EMS服务器订阅消息，必须通过用户和密码验证， 那种不验证就使用我们服务的日子已经一去不复返了。”

收到旨意之后， 你得赶快行动啊， 不然，下个release拿不到trades信息可就是你自己的问题了， 还好啦， 现在的Subscriber已经提供了这样的支持， 直接设置相应的username和pasword就行了。 不过那，如果你是通过JmsTemplate或者MessageListenerContainer来发送或者接受消息的话， 你该怎么做哪？！ 也只是通过为JmsTemplate或者MessageListenerContainer提供连接的用户名和密码设置就行了？呵呵，倒是找找看， 你是否能找到相应的setter方法吧。

## 结论

摘录一段别人的代码如下:

~~~~~~~ {.java}
if(String.valueOf(userName).trim().length()>0) {
    topicConn = connFactory.createTopicConnection(userName,password);
} else {
    topicConn = connFactory.createTopicConnection();
}
~~~~~~~

如果你觉得JmsTemplate或者MessageListenerContainer在实现的时候也是这样处理的，那么， 在上面的那个场景中，你将浪费某些精力和时间去寻找这个方向上的答案， 但即使你去查代码，估计你也不会有啥收获。实际上，针对这个问题， Spring框架已经提供了对应的解决方案， 但是， 解决问题的角度不同而已。

Spring提供了一个UserCredentialsConnectionFactoryAdapter， 通过它，对你提供给JmsTemplate或者MessageListenerContainer使用的ConnectionFactory稍微“打扮”一下就行了， 例如：

~~~~~~~ {.java}
ConnectionFactory cf = (ConnectionFactory) ctx.lookup(connectionFactoryJndiName);

UserCredentialsConnectionFactoryAdapter connectionFactoryAdaptor = new UserCredentialsConnectionFactoryAdapter();
connectionFactoryAdaptor.setTargetConnectionFactory(cf);
connectionFactoryAdaptor.setUsername("ddi_admin");
connectionFactoryAdaptor.setPassword("ddi_admin");
... // ***
JmsTemplate jmsTemplate = new JmsTemplate(connectionFactoryAdaptor);
~~~~~~~

现在， 验证信息可以生效了。 说白了， 还是包装，包装，再包装，最终都是为了“偷梁换柱”！

## CAUTION

如果是使用JmsTemplate来发送消息，为了避免JmsTemplate内部不断创建新的连接， 保险起见， 可以使用SingleConnectionFactory对ConnectionFactory再包装一层（当然了，如果拿到的ConnectionFactory早已配置了连接池来控制资源的使用，也可以不用这么做），例如：


~~~~~~~ {.java}
ConnectionFactory cf = (ConnectionFactory) ctx.lookup(connectionFactoryJndiName);

UserCredentialsConnectionFactoryAdapter connectionFactoryAdaptor = new UserCredentialsConnectionFactoryAdapter();
connectionFactoryAdaptor.setTargetConnectionFactory(cf);
connectionFactoryAdaptor.setUsername("ddi_admin");
connectionFactoryAdaptor.setPassword("ddi_admin");

final SingleConnectionFactory singleCf = new SingleConnectionFactory(connectionFactoryAdaptor); 
										  
	Runtime.getRuntime().addShutdownHook(new Thread() {
	    @Override
	    public void run() {
		try {
		    singleCf.destroy();
		} catch (Exception e) {
		    logger.error("failed to destroy single connection factory.\n");
		    logger.error(ExceptionUtils.getFullStackTrace(e));
		}
	    }
	});
	
JmsTemplate jmsTemplate = new JmsTemplate(singleCf);
~~~~~~~

安全第一， 安全第一...

Get to Know Spring And Have Fun With Spring.
