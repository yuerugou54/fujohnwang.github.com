% ThreadSafety, Non-ThreadSafety 与 Stateless, Stateful有必然的对应关系吗？

“It depends. ”

我们可以把以上问题拆作两个问题来看：
 
首先， 一个stateless的对象一定就是线程安全（threadsafe）的吗？
 
正常情况来讲， 一个stateless对象基本上是线程安全的。你想啊，当前对象本身就没有共享的状态， 所有的操作基本上都限定（confine）到了方法的stack当中，这样的对象基本上就是线程安全的了。

但这只是说正常情况下， 相对应的，还有不正常的情况下。这些不正常的情况可能是， 不合适的对象引用的发布，或者引用了其它对象或者系统中的共享状态等。
 
例如，以下两种Stateless对象定义就不是线程安全的：

~~~~~~~ {.java}
public class StatelessOne{
	public void doSth(Map ctx)
	{
		// update ctx:Map to do something.
		// other operations confined in method stack
	}
}
		
 
public class StatelessTwo{
	public void doSth()
	{
		Object someState = ref.getState(...);
		Singleton ref = ...;
		ref.doSth(someState);
		// other operations confined in method stack
	}
}

~~~~~~~
		
对于StatelessOne来说，如果它是单线程环境下使用，那么这样定义对象是没有问题的；但如果是在多线程环境下使用，则需要对方法参数的访问进行同步，或者选用Java5之后引入的ConcurrentHashMap等Map实现类； 对于StatelessTwo来说， 为了避免线程安全问题，可以使用ref提供的客户端锁进行同步，或者其它同步方式。
 
所以， 一个stateless对象不一定就是线程安全的， 视情况而定（it depends）。
 
其次，一个statefull的对象就一定是线程不安全(Non-Threadsafe)的吗？
 
正常来讲，一个stateful的对象很容易造成线程安全问题，比如， 最初设计为在单线程环境下的对象被应用到了多线程环境下， 应该同步的操作忘记同步等， 但这并不意味着一个stateful的对象就一定不是线程安全的， 它只是需要我们更多的“关心”和“呵护”而已。
 

~~~~~~~ {.java}
public class StatefulOne{
	private Map dataHolder = new HashMap();
	
	public StatefulOne()
	{
		dataHolder.put(.., ..);
		dataHolder.put(.., ..);
	}
	
	public void doSth()
	{
		Object value = dataHolder.get(..);
		doSthWithValue(value);
		// only get/read operations with dataHolder
	}
}
~~~~~~~

		
对于StatefulOne对象定义来说，因为在发布之前对象即已经处于完备状态，且发布之后，对内部状态不进行更改，所以，它自身是线程安全的。但如果 我们不合适的添加某些状态暴露接口，那这种线程安全的状态就可能被打破，尝试为dataHolder添加对应的getter方法，或者在doSth()中 添加依据某个key对应的value的状态进行状态更新的操作。，然后你会想到哪些那？
 
实际上， 只要细心的“呵护”Stateful类型的对象，就可以得到一个线程安全的对象，并不意味着这个对象是stateful的，它就一定存在线程安全问题。
 
 
> Tip
> 
> 再思考， singleton, prototype和stateless, stateful对象之间有必然的对应关系吗？
