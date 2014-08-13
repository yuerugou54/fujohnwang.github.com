% JMS Durable Subscription Tutorial

过了近乎2个星期的倒班生活，现在都有点儿“恍如隔世”的感觉了，再加上老李临时给了个东西做，更是严重的“颠倒黑白”， 否则，这篇文字早就应该出来了，趁着今天可以偷懒，就在工作时间了结它吧！（别告我状啊，呵呵，偷偷di进村，打枪di不要...）

# Durable Subscription ^[中文通常翻译为“持久化订阅”或“持久订阅”]释义(What's Durable Subscription?)

对于什么是Point-to-Point(P2P)和Publish/Subscribe(Pub/Sub)，我就不用废话了，大家应该比我都清楚， 我就直接说Durable Subscription了。

对于通常的消息订阅来说， JMS Provider会对这类消息订阅者“一视同仁”，你来了，我就给你消息，你走了，我就不管你了。 当消息到达指定Topic之后，JMS Provider只会为已经连接并且订阅了该指定Topic的消息订阅者发送消息， 如果消息到达之后你恰好不在，那不好意思，你将接收不到这一消息。这就好像现在的商场促销活动，礼品(消息)有限，虽然你(相当于消息订阅者)也想获得相应的礼品， 但当发送礼品的时候你不在礼品派发现场的话，你将失去这一获得礼品(消息)的机会，因为商场可不会管你是何方神圣，对于JMS Provider来说， 也是同样道理，只要我(JMS Provider)派发消息的时候你不在，你收不到消息是你自己找的，跟我没有关系。 也就是说，JMS Provider不会“耗费脑筋”去记下谁还没有来接收消息，就跟商场不会纪录到底谁的礼品还没有来领取一样， 因为对于这种情况来说，耗费资源去这些不确定的client， 完全就是non-sense的，不是嘛? JMS Provider或者说商场，根本就不会知道谁会来领取消息或者礼品。

当我们转到Durable Subscription的时候，情况就完全不同了。如果消息订阅者通过Durable Subscription的方式来订阅消息， 那么JMS Provider将会费点儿脑筋来记下这个Durable Subscription的消息订阅者是谁，即使当消息到达之后，该Durable Subscription消息订阅者不在， JMS Provider也会保证， 该Durable Subscription消息订阅者重新回来之后，之前到达而该Durable Subscription消息订阅者还没有处理的消息，将被一个不少的发送给它。

Let's take something for example. 假设你经营一家旅游胜地的旅馆，对于来住宿的顾客来说，你可以将他们划分为两类：

* 散客，不事先预定你的旅店
    - 对于这类顾客来说，你也不会知道他来自哪里，姓甚名谁，只会在他们入住后临时为期分配房间并提供相应服务，一旦他们退房离开，旅馆方将不再保留与之相关的任何信息。
这是不是与通常的消息订阅者很像？
* 常客，或许都持有你旅馆的VIP卡
    - 对于这类顾客，一旦他们拥有了VIP卡，则意味着他们之前已经登记过，当他们再次光临的时候，根据VIP卡这一标志，你就可以确定他们上次入住的房间等信息， 这样就可以为他们提供相同的房间，相同的服务。直到他们主动注销VIP卡或者VIP卡期限到期， 你的旅馆将一直保留这类顾客的相关信息。而这恰好与Durable Subscription场景下，JMS Provider与Durable Subscription消息订阅者之间的关系很相似。
    
简单来说，区分Durable Subscription和Nondurable Subscription最明显的一个标志就是，JMS Provider是否会为消息订阅者保存相应的状态。 对于Durable Subscription来说，JMS Provider会根据消息订阅者提供的某种标志来为其保留相应状态， 就类似于那张VIP卡或者身份证，在使用JMS API进行Durable Subscription的编程的时候，消息订阅者必须通过某种方式来提供这种标志性信息，这是最需要我们关注的一点。

在了解了Durable Subscription的语义之后，我们马上来看一下如何使用JMS API进行实际的Durable Subscription编程， 并详细了解在JMS API中，我们可以通过什么途径为JMS Provider提供Durable Subscription消息订阅者的标志信息...

# 如何进行Durable Subscription(Durable Subscription How to)

我们以一个简化功能的类似Spring的SimpleMessageListenerContainer为例，来说明进行Durable Subscription的过程中应该注意的几个问题，下面是该类的代码：

~~~~~~~ {.java}
public class GenericSimpleMessageListenerContainer extends ServiceWithLifecycle {
    
    private static final transient Log logger = LogFactory.getLog(GenericSimpleMessageListenerContainer .class);
    
    private JndiTemplate      jndiTemplate;
    private String            connectionFactoryJndiName;
    private String            destinationJndiName;
    private ConnectionFactory connectionFactory;
    private Destination       destination;
    
    private MessageListener   messageListener;
    
    private Connection        sharedConnection;
    private Session           session;
    private MessageConsumer   messageConsumer;
    
    /*
     * set a non-empty durableSubscriptionName to perform durable subscription
     */
    private String            durableSubscriptionName;
    /*
     * to identify durable subscriber plus durableSubscriptionName
     */
    private String            clientId;
    
    public GenericSimpleMessageListenerContainer()
    {
	
    }
    public GenericSimpleMessageListenerContainer(JndiTemplate jt)
    {
		this.jndiTemplate = jt;
    }
    
    protected void doStart()
    {
	Validate.notNull(getMessageListener());
	
	setupConnectionFactoryIfNecessary(jndiTemplate);
	setupDestinationIfNecessary(jndiTemplate);
	
	try {
	    setupSharedConnectionIfNecessary();
	    session = getSharedConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	    if(StringUtils.isNotEmpty(getDurableSubscriptionName()))
	    {
		if(logger.isInfoEnabled())
		{
		    logger.info("create durable subsriber with name:"+getDurableSubscriptionName());
		}
		messageConsumer = session.createDurableSubscriber((Topic)getDestination(), getDurableSubscriptionName());
	    }
	    else
	    {
		if(logger.isInfoEnabled())
		{
		    logger.info("create generic Message Consumer.");
		}
		messageConsumer = session.createConsumer(getDestination());
	    }
	    messageConsumer.setMessageListener(getMessageListener());
	    getSharedConnection().start();
	    
	    if(logger.isInfoEnabled())
	    {
		logger.info("The Connection to deliver messages is Started now!");
	    }
	    
	} catch (JMSException e) {
	    logger.error("failed to start Message listener container!!!\n");
                    JmsUtils.closeMessageConsumer(getMessageConsumer());
	    JmsUtils.closeSession(getSession());
	    JmsUtils.closeConnection(getSharedConnection());
	    throw JmsUtils.convertJmsAccessException(e);
	}
	
    }
    
    protected void doStop()
    {
	try {
	    getSharedConnection().stop();
	} catch (JMSException e) {
	    logger.warn("failed to stop connection of delivering jms message.\n");
	    logger.warn(ExceptionUtils.getFullStackTrace(e));
	}
	JmsUtils.closeMessageConsumer(getMessageConsumer());
	JmsUtils.closeSession(getSession());
	JmsUtils.closeConnection(getSharedConnection());
    }
    /**
     * After creating connection from ConnectionFactory, 
     * we will check whether we can set clientId for the created connection, 
     * If a pre-configured client Id exists, we will not try to set our clientId;
     * otherwise we will set our custom client Id if it's not empty.<br>
     * 
     * The try-catch(IllegalStateException) is also for checking whether the jms provider has pre-configured a client Id for the connections it creates.<br>
     * If a pre-configured client id does exist, we will let it be, so after catching such exception, we just log it in WARN level to notify us.<br>
     *  
     * @throws JMSException
     */
    private void setupSharedConnectionIfNecessary() throws JMSException {
	if (getSharedConnection() == null) 
	{
	    setSharedConnection(getConnectionFactory().createConnection());
	    
	    String preConfiguredClientId = getSharedConnection().getClientID();
	    if(StringUtils.isEmpty(preConfiguredClientId) && StringUtils.isNotEmpty(clientId))
	    {
		try
		{
		    getSharedConnection().setClientID(clientId);
		    if(logger.isInfoEnabled())
		    {
			logger.info("set up JMS Connection with Client Id:"+clientId);
		    }
		}
		catch(IllegalStateException e)
		{
		    logger.warn("A pre-configured client id exists, durable subscriber will use this client id and ignore external setted client id.");
		    logger.warn("pre-configured client id:"+preConfiguredClientId);
		    logger.warn("external setted client id:"+clientId);
		}
	    }
	}
    }
    private void setupDestinationIfNecessary(JndiTemplate jndiTemplate) {
	if(getDestination() == null)
	{
	    Validate.notEmpty(getDestinationJndiName());
	    
	    try {
		setDestination((Destination)jndiTemplate.lookup(getDestinationJndiName()));
	    } catch (NamingException e) {
		throw new RuntimeException("failed to lookup destination via JNDI with jndiName:"+getDestinationJndiName());
	    }
	}
    }
    private void setupConnectionFactoryIfNecessary(JndiTemplate jndiTemplate) {
	if(getConnectionFactory() == null)
	{
	    Validate.notEmpty(getConnectionFactoryJndiName());
	    
	    try {
		setConnectionFactory((ConnectionFactory)jndiTemplate.lookup(getConnectionFactoryJndiName()));
	    } catch (NamingException e) {
		throw new RuntimeException("failed to lookup ConnectionFactory via JNDI with jndiName:"+getConnectionFactoryJndiName());
	    }
	}
    }

	// getters and setters...
}
~~~~~~~


如果你在使用Spring 2.x之前的版本而又不能升级，那么这个类可以“凑合”用一下(因为它的功能并不完备，比如没有添加MessageSelector以及多线程等功能支持)， 如果可能，还是建议你使用Spring 2.x之后引入的SimpleMessageListenerContainer或者DefaultMessageListenerContainer，当然了，这些属于题外话，使用JMS API进行Durable Subscription编程与通常的方式没有太多差异，只要搞清楚一下两点，剩下的基本就不会有太大问题了。

## Client Id

JMS规定了两种Administered Object，即ConnnectionFactory和Destination，所以，“万物伊始”，我们得先将这两个东西从JNDI上拿下来， GenericSimpleMessageListenerContainer提供了两种方式，要么你在外面获取到这两个东西， 然后直接注入给他；要么你就传一个JndiTemplate， 然后注入这两个东西对应的Jndi名称。

有了ConnectionFactory，我们可以通过它创建到相应JMS Provider的连接；有了Destination，我们才知道该去哪里接收消息，我想这个很容易理解， 这里需要着重说明的是ConnectionFactory。

我们已经说过， 要进行Durable Subscription，客户端必须提供某种类似VIP卡或者身份证之类的标志，在JMS中，Client Id的存在即是因为如此。 将Client Id称作Connection Id或许更好理解，它与JMS的Connection相“挂钩”，当一个JMS Connection被创建之后， 它有两种方式获得它的Client Id:

* 通过ConnectionFactory自动获得  
    - 既然ConnectionFactory属于Administered Object， 那么在各个JMS Provider中部署相应ConnectionFactory的时候， 我们就可以设定通过ConnectionFactory创建Connection的时候，是否要为创建的Connection设定Client Id， 以及该设定什么样的Client Id， 而具体设定方式可能需要参考各个JMS Provider各自的文档。
* 客户端程序自定义设定
    - 在Connection被创建之后，并且没有进行任何其他操作之前，客户端程序可以为其设定自定义的Client Id，不过，如果该Connection已经被ConnectionFactory预先设定了Client Id的话， connection..setClientID(clientId)将会抛出JMS的IllegalStateException。

所以，在setupSharedConnectionIfNecessary()方法中，你会发现，我们会事先检查ConnectinFactory是否已经预先设定过Client Id，如果没有并且客户端程序持有注入的非空的Client Id， 那么我们才会为Connection设定自定义的Client Id。

> CAUTION
> 
> 连接到JMS Provider进行Durable Subscription的多个Connection不可以拥有相同的Client Id，否则也会被IllegalStateException伺候！

## Subscriber Name

单凭Client Id还不足以唯一标志某一个Durable Subscription，就跟我凭一个身份证，可以预定多个房间一样。 同一个连接里，你可以创建多个MessageConsumer去订阅不同Topic的消息，如果下回回来，你只想继续接受某一个Topic消息的话，JMS Provider如何知道是哪一个？ 所以，为了区分同一个Connection中不同的Durable Subscription，我们还需要进一步的标志物，这就是Subscriber Name！

~~~~~~~ {.java}
messageConsumer = session.createDurableSubscriber((Topic)getDestination(), getDurableSubscriptionName());
~~~~~~~

通过Session创建DurableSubscriber的时候，我们要为其提供一个Durable Subscriber Name，这是与普通订阅最基本的区别:

~~~~~~~ {.java}
messageConsumer = session.createConsumer(getDestination());
~~~~~~~

有了SubscriberName之后，下回，当我们重新连接然后使用相同的SubscriberName创建消息订阅的时候，JMS Provider就会知道将哪一个Durable Subscription使用的Topic中的消息进行传送了。

> NOTE
> 
> 创建MessageConsumer的时候可以同时设定相应的Message Selector， 另外进行异步消息接收的时候，需要为MessageConsumer设定相应的MessageListener， 最后，调用connection.start()方法告知JMS Provider开始进行消息传送，这里只是简单提及一下，我向大家比我更清楚。

# 小结

Connection级别的Client Id和创建MessageConsumer时候的Subscriber Name唯一标志一个Durable Subscription，这是在JMS中成功进行Durable Subscription的前提(当然，要是JMS Provider过于“山寨”，或许也不成)。

基本上，个人觉得在Durable Subscription中要提的就这些了。 有关JMS更多信息，可以参考JMS规范以及各个JMS Provider提供的文档。

倾听了'袜子'和Marvel在这一问题上的论点之后，才有了写下这段文字的想法， Thanks to you both!
 
BTW. 以上纯属个人观点，如果有误，还望各位看官指出。




