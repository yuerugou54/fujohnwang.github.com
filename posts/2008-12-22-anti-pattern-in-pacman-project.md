%  PACMAN项目中的编码反模式

这一阵子正在处理一个jira issue, 不得不去抠原来的一些代码，加上原来做code review的一些条目，决定促成这篇blog文章。

以下的一些“所谓的”代码模式(或者说编码模式)都节选自PACMAN项目，但并不意味着它们仅存在于PACMAN项目中， 实际上，如果我们细心观察的话，许多地方都能发现这些似曾相识的代码模式，这段文字权当“抛砖引玉”，希望帮助大家发现并进而改进现有的或者可能存在的一些编码过程中的反模式。

> NOTE
> 实际上，这里的许多代码都可以工作，但作为一个perfectionist，某些瑕疵也是无法容忍的，或许这属于做事方式的不同，并没对错之分， 所以，以下观点仅属于个人观点，如果有有失偏颇之处，还请各位看官海涵则个...

# Anti-Pattern 1: 依赖查找几乎取代了依赖注入。

## 案例展示

先从大局入眼，首先进入眼帘的就是整个codebase中大量存在的对spring容器得getBean(..)方法调用:

~~~~~~~ {.java}
class TradeSupport
{
	GenericJMSPublisher tgenReqeustQueuePublisher = (GenericJMSPublisher) beanFactory.getBean("tgenReqeustQueuePublisher");
	...
	NeoAuditDao auditDao = (NeoAuditDao) beanFactory.getBean("NeoAuditDao");
	...
	blablabla...
}
~~~~~~~

这就好像还处于社会主义初级阶段一样，或许是当时的开发者并没充分了解IoC容器真正的威力之所在，也或许当时的开发者根本就没搞清楚IoC， 总之，出现这样的代码模式，绝对不是放着依赖注入不用，而非要“推陈出新”的去用IoC容器提供给你的依赖查找接口/能力， 如果真是这样的话，那还不如直接自己构建个Map用用岂不是更爽？还要更轻量级那！

## 如何处理

充分使用IoC容器给予我们的依赖注入能力，将所有的对象均交由IoC容器来管理。Spring容器接口暴露给我们的依赖查找接口， 正常情况下只需要使用一次，那就是在入口类中，当你发现codebase中充斥着大量的getBean(..)的时候，就该问问自己哪里出了问题。

对于案例中的TradeSupport来说，只需要在其类定义中声明其依赖即可，至于依赖的获取/初始化等工作，就交给IoC容器来做吧！

~~~~~~~ {.java}
class TradeSupport
{
	GenericJMSPublisher tgenReqeustQueuePublisher;
	...
	NeoAuditDao auditDao;
	...
	blablabla...
	// setters and getters or constructors, if necessary 
}
~~~~~~~

如果其他类又依赖于TradeSupport，那就“依葫芦画瓢”，在那些类中声明对TradeSupport的依赖，然后让IoC容器将TradeSupport注入给这些类，就是这么简单。

# Anti-Pattern 2: 手动管理资源的获取和释放

## 案例展示

当我刚加入PACMAN的时候，因为不能马上切入项目，所以，当时让我做了些code review的工作，在此过程中，发现最“严重”也最不该重复的模式， 就是，使用了Spring提供的HibernateDaoSupport后，却丝毫没有得到该类提供的任何优待，你不理解这个类存在的真谛也就算了， 既然使用了它，为何却在重复它要帮你避免的种种问题那？我想，HibernateDaoSupport这个时候“想死的心”都有了。

我想列位应该可以猜到我要揭示的这段代码反模式长成什么样子了：

~~~~~~~ {.java}
public class XXXDao extends HibernateDaoSupport
{
	public Object dataAccessMethod(...)
	{
		Session session = null;
		try{
			session = getSession(true);
			...// do data access with "session"
		}
		catch(Exception e)
		{
			// process exception
		}
		finally
		{
			if(null!=session) {
				releaseSession(session);
	    	}
		}
	}
	
	// ... other data access method definitions	
}
~~~~~~~

当我code review整个dao的package之后，发现通篇皆如此，看来示范的力量还真不容小觑那。

## 如何解决

为了避免手动管理资源的获取和释放所带来的种种问题，以及统一数据访问过程中异常处理(和其他原因)，很显然，Spring在其数据访问抽象层中为我们提供了HibernateTemplate工具类， 以次帮助我们摆脱之前的种种尴尬局面，如果你了解了这些，并且也知道HibernateDaoSupport父类提供了HibernateTemplate的支持，那么，要摆脱上面反模式的“魔咒”就易如反掌了:

~~~~~~~ {.java}
public class XXXDao extends HibernateDaoSupport
{
	public Object dataAccessMethod(...)
	{
		getHibernateTemplate().anyTemplateMethodYouNeed(..);
	}
	
	// ... other data access method definitions	
}
~~~~~~~

简单，优雅多了，不是吗？事情本该如此。

# Anti-Pattern 3: 忽略Object对象几个基本方法的覆写（Override）

## 案例展示

做Java开发，难免要跟JavaBeans打交道，所以定义一个简单的JavaBeans对大家来说那可能是相当的惬意：

~~~~~~~ {.java}
public class MockValueObject
{
	private String propertyOne;
	private int    propertyTwo;
	....
	
	// setters and getters and other methods defintions
	
	// ... do we miss sth?
}
~~~~~~~

定义了对象的Property对应的setters和getters方法，以及其他的一些逻辑方法之后，我们是不是就“打完收功”了那？ 如果是这样的话，那恭喜你，你中奖了！

实际上，革命尚未成功，同志们还需努力啊！出于种种原因的考虑(不知道的话，先去找本Effective Java看看)，我们起码应该覆写(Override)Object的equals, hashCode以及toString方法， Effective Java中的长篇累牍并非吃干饭的。当你调试程序的时候，只能看到一个个莫名其妙的数字的时候，又没有想到toString方法哪？！当你将对象放入Hash当中，再次取出来的时候，发现对象并非自己当初预想的那个，或者其间性能低下的时候，你又有没有想到equals和hashCode那？！

##  如何解决

看似简单的东西，却并非不重要。即使再懒，调用一下Jakarta Commons Lang提供的现有得toStringBuilder, HashCodeBuilder， EqualsBuilder等工具类，也费不了多少时辰吧？！ 再不行，直接用Eclipse等IDE自带的对应功能，或者装一个commons4e插件，总行了吧？！不想敲打键盘，点几下鼠标呗！

If it makes your life a little easier, Why u don't want to do this?

# Anti-Pattern 4: 宽泛的全局事件处理机制取代细粒度，强类型的事件处理

##  案例展示

确切的讲，这个案例不应该算是PACMAN的，PACMAN仅仅是个“受害者”。 PACMAN中用到一个组件叫做Blotter，这个Blotter可不一般，按小蒯的说法， “那简直是所有bug的根源”，小蒯用了两年这个组件当然是感同身受了，我还没咋摸清出这个blotter的习性，所以，不做太多评论，毕竟，这个组件也早就停止开发了， 挖太多的“陈谷子烂芝麻”出来也没太大意思，就挑一个第一眼看上去就印象深刻的来说吧！

Blotter说白了就是一个Swing的JTable的替代品，我不知道当时开发这个组件的Team是如何想的，或者是否真的遇到了某些问题，而必须要用一个泛泛的全局的事件管理器来管理blotter的几乎所有事件，从表格的选取到相关的一些资源释放的事件，全部使用同一个类来管理。 在Blotter中，要监听Blotter的事件，那么，用String型的key来标注事件类型，然后注册到全局的ApplicationActionManager上去:


~~~~~~~ {.java}
String actionName = ...;
ApplicationActionManager.getInstance().subscribeToAction(actionListener, actionName);
~~~~~~~

其中， actionListener是一个ApplicationActionListener实例，在这个actionListener中，你可以根据你注册的actionName来监听自己感兴趣的事件：

~~~~~~~ {.java}
public class YourActionListener implements ApplicationActionListener

    public void doAction(final String actionName, final Object obj) {
		if (StringUtils.equals(actionName, Blotter.SELECTION_ACTION)
			&& (obj == settlementViewBlotter)) {
			...
		}
		if (obj == financeViewBlotter) {
		    if (StringUtils.contains(actionName, ApplicationActionManager.VIEW_CHANGED)) {
			...
		}
    }
    
    public void subscribeToActions() {}

}
~~~~~~~

即使是Table相关的事件，你依然需要如此，对于TableSelection之类事件来说，在Blotter里面，简直就是“生不如死”啊，一点儿存在的价值都没有，呵呵。 有强类型的，细粒度的事件监听器不用，转而使用宽泛的没有类型安全保证的模型， 真不知道该说是进步那还是倒退那！

## 如何解决

既然JTable已经提供了各种事件监听器， 包括MouseListener, MouseMotionListener, ListSelectionListener等等，而且你也是对JTable进行的封装， 那就重用这些标准的基础设施吧，这样大家都好过一些，无论是对API的设计者来说，还是使用者来说，都是如此，尤其是对后者，否则，他还得去重新了解你的习性，:-)


# Anti-Pattern 5: 不做深思熟虑的接口定义

## ApplicationActionListener的定义

### 案例展示

这个话题最初是从上一个话题引申出来的，确切的来说，是从ApplicationActionListener的接口定义引伸出来的。 从ApplicationActionListener的定义中，我们发现，它定义了两个方法：

~~~~~~~ {.java}
public interface ApplicationActionListener 
{
	void doAction(String s, Object obj);
	void subscribeToActions();
}
~~~~~~~

对于doAction来说，没有什么好说的，而subscribeToActions方法的定义，则多少有些多余了。不能说不经过脑子，但绝对是没有进一步得去思考。纵观JDK中各种事件监听器的定义， 有谁发现哪一个Listner中定义过“强制”注册当前Listener的方法哪？！No，没有！而现在的ApplicationActionListener鼓励的是什么哪？！

~~~~~~~ {.java}
public class XXActionListner implements ApplicationActionListener
{
	public XXActionListner()
	{
		subscribeToActions();
	}

	void doAction(String s, Object obj){...}
	
	void subscribeToActions(){
		ApplicationActionManager.getInstance().subscribeToAction(this, actionName);
	}
}
~~~~~~~

既然你每次都在构造方法中调用这个方法来注册Listener，既然每次只是在这个方法中调用ApplicationActionManager的subscribeToAction方法， 那为啥你不直接在构造方法里面调用ApplicationActionManager的subscribeToAction方法那？！多定义这么一个方法并不会带给你什么好处，倒是有些“脱裤子放屁--多此一举”的感觉！

### 如何解决

直接砍掉subscribeToActions()的方法定义，从而简化接口定义如下：


~~~~~~~ {.java}
public interface ApplicationActionListener 
{
	void doAction(String s, Object obj);
}
~~~~~~~

如果你还是习惯在构造方法里面注册该Listener的话，那么在构造方法里直接调用:

~~~~~~~ {.java}
ApplicationActionManager.getInstance().subscribeToAction(this, actionName);
~~~~~~~

类似的代码就好了，不过，既然我现在倡导外部化你的代码，那么，在该Listener外部合适的位置调用如上类似代码来注册该Listener或许更合适一些。这样，Listener的注册和释放都归于一处进行统一管理，可以更加容易的避免资源泄漏之类的问题。 要知道， 当初的subscribeToActions()定义，只是明确了一张牌，剩下的那张牌哪？！不得而知。

## WorkflowI的定义

### 案例展示
呵呵，或许是命名习惯不同，让我定义的话，我或许会将这个接口命名为IWorkflow，不过，这是次要的，主要问题在于这个接口的定义与其语义存在很大的差距，或者说，并没有很好的表现其语义。 为什么这么说那？！先看该接口的定义：

~~~~~~~ {.java}
public interface WorkflowI {

    public void execute(TransactionEntity obj) throws Exception;

    @SuppressWarnings("unchecked")
    public List validate(TransactionEntity passObj) throws Exception;

    // public void dbPersist(TransactionEntity passObj) throws Exception;
    public void cachePersist(TransactionEntity passObj) throws Exception;

    public void publish(TransactionEntity passObj) throws Exception;
}
~~~~~~~

按理说，该接口定义是要规定一个TransactionEntity对象的处理流程，但是，很明显，虽然该接口定义了多个处理方法，但是，没有任何标志可以说明，TransactionEntity到底要以一个什么样的顺序被处理。 应该说，该接口的设计者初衷是好的，但是，并没有进一步得去完善这个接口的定义，很显然，当前的接口定义无法表现Workflow的概念。

### 如何解决

使用Template Method Pattern来强化Workflow的语义：

~~~~~~~ {.java}
public abstract class AbstractWorkflow {
    public void execute(TransactionEntity obj) throws Exception
    {
		List txList = validate(obj);
		for(TransactionEntity tx:txList)
		{
		    cachePersist(tx);
		    publish(tx);
		}
    }
    @SuppressWarnings("unchecked")
    protected abstract List validate(TransactionEntity passObj) throws Exception;

    protected abstract void cachePersist(TransactionEntity passObj) throws Exception;

    protected abstract void publish(TransactionEntity passObj) throws Exception;
}
~~~~~~~

现在，我们的父类可以清晰的表现workflow的语义，子类只需要实现相应的处理方法即可，处理流程在父类中已经被强化。 当然了，execute方法内部的处理流程可以根据需要进行进一步得优化，比如添加并行处理支持等，但它的主要目的在这里是明确的。
当然了，如果出于扩展性考虑，比如，有可能Workflow的逻辑会变化，那么，声明一个更抽象的接口也是可以的，比如:

~~~~~~~ {.java}
public interface IWorkflow{
	void execute(TransactionEntity obj) throws Exception;
}
~~~~~~~

不过那，“如无必要，勿增实体”，还是万事开头从简单的开始吧！实在不行，咱extends现有父类还不行啊！

# Anti-Pattern 6: 在构造方法中调用生命周期管理方法

## 案例展示

这样的代码在PACMAN或者其他地方经常出现，通常这些代码都会在构造方法中调用当前对象的生命周期管理方法，以TangosolCacheClient为例:

~~~~~~~ {.java}
public class TangosolCacheClient
{
	public TangosolCacheClient()
	{
		...
		startService();
		...
	}
	
	public void startService() throws XXException
	{
		...
	}
	
	public void stopService() throws Exception
	{
		...
	}
}
~~~~~~~

对象初始化的时候的一些同步问题，在《Java Concurrency In Practice》一书中有所描述， 我就不再这里赘述了，除去这个原因不谈，试想一下， 这样的做法是否真的合适那？我们打个比方，构造方法没有执行完成以前的对象，就好像那还在娘胎里的小孩儿一样， 虽然孩子还没出生，也能在娘胎里开始蹦达，不过，这能算是他/她一生的开始吗？起码我不这么认为。 对于对象来说，也是如此，在“娘胎”里就startService好像不像那么回事吧？！让它出了“娘胎”在真正的开始蹦达不好吗？


## 如何解决

如果你使用Spring的IoC容器来管理对象，那么使用init-method和destroy-method来指定相应的生命周期管理方法，比如：

~~~~~~~ {.xml}
<bean id=".." class="...TangosolCacheClient" init-method="startService" destroy-method="stopService">

</bean>
~~~~~~~

这样的话，Spring容器将在对象构建完成后，帮我们调用startService方法来启动服务，当应用退出的时候，帮我们调用stopService方法来停止服务。

即使你不用Spring的IoC容器，或者说不想在容器启动的时候启动服务，那你也可以在应用中合适的地方，调用状态完备的TangosolCacheClient对象实例的startService或者stopService方法， 时机和地点随情况而定，但总的原则却是，在对象构建完成并处于状态完备的情况下来做这些事情（这是个人建议）。







