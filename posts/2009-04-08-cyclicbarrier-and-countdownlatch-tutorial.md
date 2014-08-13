% 我爱J普 之 CyclicBarrier And CountDownLatch Tutorial

说实在的，我其实是看别人的代码看得郁闷了，所以，写点儿东西舒缓一下，在农村看下象棋的时候，有那么一句话，叫做“跟臭棋娄子下棋，越下越臭 ”， 而我真的不想那样，只能自己时刻的keep alert，其实吧，你可能觉得我是太清高，好像就你写的代码好似的，不过那，先给各位看官罗列点儿“小菜 ”尝尝先：


~~~~~~~ {.java}
while(true){
	;
}

// ---------------------------

@Override
public void finalize() throws Throwable {
	something.destroy();
	// or
	something.close();
}
~~~~~~~

这些属于比较恶劣的情况，写今天这个主题那，是因为时至今日，还是看到了Thread.wait(), notify(), join()之类的直接底层API的使用，所以， 做回好人，也算是帮大家和我自己开拓思想吧（虽然都tmd不是很新了）！

先假设（对，不假设不知道下面东西怎么开始说），我们有这么一批Task，姑且叫Batch Task和RealTime Task，这批Task的执行有个简单的前提，就是只有当所有Batch Task执行完毕之后， RealTime Task才可以执行，为了实现这个要求，我们那一般来说，有两个选择，也就是我们标题上的那两个东西。

# CyclicBarrier First

CyclicBarrier就像个栅栏（好像是废话，人家名字就说明问题了嘛），它将拦截规定数目的线程执行，正常情况下， 只有当所有线程都完成工作到达这个栅栏之后，CyclicBarrier才会放行，让后面的逻辑得以执行。 简单点儿说，其实这东西挺适合解决我们刚才假设的问题场景的。

首先介绍最简单的“选手 ”，我们的BatchTask和RealtimeTask：

~~~~~~~ {.java}
public class BatchTask implements Runnable {

	public void run() {
		// TODO your batch task logic
	}
}

public class RealtimeTask implements Runnable {

	public void run() {
		// TODO your real-time task logic
	}
}

~~~~~~~

简单的不能再简单了，呵呵，别骂我哈，为啥这么简单，待会儿再说。 接下来是针对我们的假设所给出的使用CyclicBarrier的解决方案：

~~~~~~~ {.java}
public class CyclicBarrierTaskScheduler implements Runnable {
	
	private CyclicBarrier cyclicBarrier;
	private int           batchTaskNumbers;
	private int           realtimeTaskNumbers;
	
	// you can set an ExecutorService extenally
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	public void run() {
		// pre-validate on states of current object
		
		cyclicBarrier = new CyclicBarrier(getBatchTaskNumbers(), new Runnable(){
			public void run() {
				for(int i=0;i<getRealtimeTaskNumbers();i++)
				{
					getExecutor().execute(new RealtimeTask());
				}
			}});
		
		for(int i=0;i<getBatchTaskNumbers();i++)
		{
			getExecutor().execute(new Runnable(){
				public void run() {
					new BatchTask().run();
					try {
						getCyclicBarrier().await();
					} catch (InterruptedException e) {
						e.printStackTrace(); // process exception as per your need
					} catch (BrokenBarrierException e) {
						e.printStackTrace(); // process exception as per your need
					}
				}});
		}
		
	}
	
	public void shutdown()
	{
		if(getExecutor() != null)
		{
			getExecutor().shutdown();
			try {
				getExecutor().awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace(); // process exception as per your need
			}
		}
	}

	public CyclicBarrier getCyclicBarrier() {
		return cyclicBarrier;
	}

	public int getBatchTaskNumbers() {
		return batchTaskNumbers;
	}

	public void setBatchTaskNumbers(int batchTaskNumbers) {
		this.batchTaskNumbers = batchTaskNumbers;
	}

	public int getRealtimeTaskNumbers() {
		return realtimeTaskNumbers;
	}

	public void setRealtimeTaskNumbers(int realtimeTaskNumbers) {
		this.realtimeTaskNumbers = realtimeTaskNumbers;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
	public static void main(String[] args)
	{
		CyclicBarrierTaskScheduler taskScheduler = new CyclicBarrierTaskScheduler();
		taskScheduler.setBatchTaskNumbers(10);
		taskScheduler.setRealtimeTaskNumbers(15);
		
		try
		{
			taskScheduler.run();
		}
		finally
		{
			taskScheduler.shutdown();
		}
	}
}

~~~~~~~

CyclicBarrier一共有两个构造方法（Constructor）, 一个就是我们刚刚使用的: `CyclicBarrier(int parties, Runnable barrierAction) `, 两个参数：

* parties是说，我（CyclicBarrier）可以阻挡多少个线程执行，只有当这些数目的线程都到达之后，我（CyclicBarrier）才放行；
* barrierAction是当所有的线程成功突破CyclicBarrier的封锁之后执行的Runnable；

有了这些信息，回头来看CyclicBarrierTaskScheduler中run()方法的逻辑：

我们首先根据batchTaskNumbers的数目来构建一个CyclicBarrier实例（实际代码里记得提前检查一下这个数量）， 这里的batchTaskNumbers也就是第一个参数parties的值，也就是说，只有当这些Batch Task执行完成之后，我们才会执行第二个参数提供的Runnable，所以不难猜到， 在CyclicBarrier的构造方法的第二个参数里，我们会执行所有的RealtimeTask。

有了CyclicBarrier的实例之后，我们需要通过某种方式告知这个CyclicBarrier都有哪些线程已经执行完成并到达了CyclicBarrier设定的边界（其实就是个计数）， 这个是通过CyclicBarrier的await()方法来完成的，所以也就有了接下来这段代码：

~~~~~~~ {.java}
for(int i=0;i<getBatchTaskNumbers();i++)
	{
		getExecutor().execute(new Runnable(){
			public void run() {
				new BatchTask().run();
				try {
					getCyclicBarrier().await();
				} catch (InterruptedException e) {
					e.printStackTrace(); // process exception as per your need
				} catch (BrokenBarrierException e) {
					e.printStackTrace(); // process exception as per your need
				}
			}});
	}
~~~~~~~

在这里，我们也一共提交了batchTaskNumbers这些数量的Batch Task给Executor执行，在每一个提交的Runnable里， 当每一个BatchTask执行完毕之后，我们都会调用getCyclicBarrier().await()来通知CyclicBarrier“我做完了哈 ”， 当所有这些提交的Task都执行完毕之后，CyclicBarrier就会数一数然后跟batchTaskNumbers对比一下， “哦，都做完了哈，那我让通过构造方法第二个参数Runnable开始跑了哈 ”

咋样？到这里，我们的目标算是基本达成了吧？不过，同样是这个目标，也同样是使用CyclicBarrier，我们还可以使用CyclicBarrier的另一个构造方法来达成。 看官上眼啦！

~~~~~~~ {.java}
public void run() {
		// pre-validate on states of current object
		
		cyclicBarrier = new CyclicBarrier(getBatchTaskNumbers()+1);
		
		for(int i=0;i<getBatchTaskNumbers();i++)
		{
			getExecutor().execute(new Runnable(){
				public void run() {
					new BatchTask().run();
					try {
						getCyclicBarrier().await();
					} catch (InterruptedException e) {
						e.printStackTrace(); // process exception as per your need
					} catch (BrokenBarrierException e) {
						e.printStackTrace(); // process exception as per your need
					}
				}});
		}
		
		try {
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			// process exception as per your needs
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// process exception as per your needs
			e.printStackTrace();
		}
		
		for(int i=0;i<getRealtimeTaskNumbers();i++)
		{
			getExecutor().execute(new RealtimeTask());
		}
	}
~~~~~~~

我们只看run()方法这部分，现在，我们使用只有一个参数的CyclicBarrier构造函数来构造CyclicBarrier实例， 但是，这回传入的parties数量则是在原来batchTaskNumbers的基础上加1， 当提交了所有batchTaskNumbers数量的Batch Task执行之后， 我们在当前线程调用了同一个CyclicBarrier实例的await()方法，凑上这个，正好就是当初构造CyclicBarrier时候传入的parties的数量。 所以，一样的效果，当这个CyclicBarrier被成功突破之后，当前线程中await()后面的提交并执行Realtime Task的逻辑才会执行。

关于CyclicBarrier我们就先“广播 ”到这里，下面是CountDownLatch上场时间...

# CountDownLatch:切，这有啥，我也行！

CountDownLatch，问其名，就知道它干啥的，不就个计数门闩嘛，呵呵，more or less， 这个CountDownLatch吧，跟CyclicBarrier差不多啦， 也是接收个计数，然后在某个线程里面await()住，也就是闩住这个线程的执行，之后，其他线程就可以通过countDown()来减少计数，当计数减少为0 的时候， 被闩住的那个线程就会被放行啦。

拿到我们假设的那个问题场景下来说，就是，我先通过CountDownLatch的await()暂停一下，让所有batchTaskNumbers数量的Batch Task都执行完， 然后采取执行RealtimeTask，不过，为了能够让CountDownLatch的await()不会一直暂停在那里不动，我们会在每一个Batch Task执行完成华，减少CountDownLatch的计数， 用代码说话就是：

~~~~~~~ {.java}
public class CountDownLatchTaskScheduler implements Runnable {
	
	private CountDownLatch latch;
	private int            batchTaskNumbers;
	private int            realtimeTaskNumbers;
	
	// you can set an ExecutorService extenally
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	public void run() {
		// pre-validate on states of current object
		latch = new CountDownLatch(getBatchTaskNumbers());
		
		for(int i=0;i<getBatchTaskNumbers();i++)
		{
			getExecutor().execute(new Runnable(){
				public void run() {
					new BatchTask().run();
					
					getLatch().countDown();
				}});
		}
		try {
			getLatch().await();
		} catch (InterruptedException e) {
			// process exception as per your needs
			e.printStackTrace();
		}
		
		for(int i=0;i<getRealtimeTaskNumbers();i++)
		{
			getExecutor().execute(new RealtimeTask());
		}
		
	}
	
	public void shutdown()
	{
		if(getExecutor() != null)
		{
			getExecutor().shutdown();
			try {
				getExecutor().awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace(); // process exception as per your need
			}
		}
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public int getBatchTaskNumbers() {
		return batchTaskNumbers;
	}

	public void setBatchTaskNumbers(int batchTaskNumbers) {
		this.batchTaskNumbers = batchTaskNumbers;
	}

	public int getRealtimeTaskNumbers() {
		return realtimeTaskNumbers;
	}

	public void setRealtimeTaskNumbers(int realtimeTaskNumbers) {
		this.realtimeTaskNumbers = realtimeTaskNumbers;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}
	
	public static void main(String[] args)
	{
		CountDownLatchTaskScheduler taskScheduler = new CountDownLatchTaskScheduler();
		taskScheduler.setBatchTaskNumbers(10);
		taskScheduler.setRealtimeTaskNumbers(15);
		
		try
		{
			taskScheduler.run();
		}
		finally
		{
			taskScheduler.shutdown();
		}
	}
}
~~~~~~~

我们根据batchTaskNumbers的数量构建了一个CountDownLatch，然后提交Batch Task执行，之后，通过CountDownLatch的await()方法等待所有这些Batch Task执行完毕，然后再接着执行后面的逻辑。 至于await()如何知道什么才不await()了，当然就是当每一个Batch Task执行之后都countDown()之后啦。

另外，我们还可以换一个角度来看待或者说使用CountDownLatch，当然，这个跟我们的假设场景并没啥关系了，纯粹是CountDownLatch相关的内容。 我们上面是在主要的执行线程里面await()，然后在其他执行线程里面countDown;反过来，我们也可以在主要的执行线程里面countDown，然后在执行线程里面await()， 这个时候，那些await的执行线程就好比一匹匹急欲冲出栅栏的赛马，当主要线程里countDown一声枪响之后，这些await的执行线程才会开始执行。

比如，我们为新的CountDownLatchTaskScheduler再添加一个CountDownLatch，这个CountDownLatch将 负责控制RealtimeTask，只有CountDownLatch的计数减少到0之后，这些RealtimeTask才可以才是执行， 这实际上又做了一遍“代码清单 3 ”里那个CountDownLatch的工作，对我们当前假设场景没啥太大意义，不过，两个对比这看,或许也还可以:


~~~~~~~ {.java}
public class CountDownLatchTaskScheduler implements Runnable {
	
	private CountDownLatch latch;
	
	private CountDownLatch signalLatch = new CountDownLatch(1);
	
	private int            batchTaskNumbers;
	private int            realtimeTaskNumbers;
	
	// you can set an ExecutorService extenally
	private ExecutorService executor = Executors.newFixedThreadPool(10);

	public void run() {
		// pre-validate on states of current object
		latch = new CountDownLatch(getBatchTaskNumbers());
		
		for(int i=0;i<getBatchTaskNumbers();i++)
		{
			getExecutor().execute(new Runnable(){
				public void run() {
					new BatchTask().run();
					
					getLatch().countDown();
				}});
		}
		
		for(int i=0;i<getRealtimeTaskNumbers();i++)
		{
			getExecutor().execute(new Runnable(){

				public void run() {
					try {
						getSignalLatch().await();
					} catch (InterruptedException e) {
						// process exception as per your needs
					}
					
					new RealtimeTask().run();
				}
				
			});
		}
		
		try {
			getLatch().await();
		} catch (InterruptedException e) {
			// process exception as per your needs
			e.printStackTrace();
		}
		
		getSignalLatch().countDown();
	}
	
	...
}
~~~~~~~

你可以看到，即使我们很早就提交了RealtimeTask给Executor执行，但只有当countDown()号令发出之后，这些RealtimeTask才会真正开始执行，此前，它们必须等！

针对CountDownLatch就说这些。

# 最后要说的话

现在回过头来说BatchTask和RealtimeTask定义过于简单的问题，实际上，这样定义这两个类是想让各位看官只关注每一个Task类型本该 关心的事情， 至于这些Task如何协调调度执行，则剥离到更外层去； 另一个原因就是，我们现在看到的跟CountDownLatch和CyclicBarrier有关的Samples代码都是直接传入一个 CountDownLatch和CyclicBarrier的共享实例给每一个Task, 例如：

~~~~~~~ {.java}
public class BatchTask implements Runnable {

	private CyclicBarrier cyclicBarrier;
	
	public BatchTask(CyclicBarrier cyclicBarrier)
	{
		this.cyclicBarrier = cyclicBarrier;
	}
	
	public void run() {
		// TODO your batch task logic
		
		try {
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
~~~~~~~

那么，当我转向讲解CountDownLatch方式的时候，就得再将这个BatchTask的定义改成如下的样子：

~~~~~~~ {.java}
public class BatchTask implements Runnable {

	private CountDownLatch latch;
	
	public BatchTask(CountDownLatch latch)
	{
		this.latch = latch;
	}
	public void run() {
		// TODO your batch task logic
		latch.countDown();
	}
}
~~~~~~~

对于RealtimeTask也是一个道理，你写代码的时候，决定用啥了之后当然不太会变，但是，我要是也这么干，来回折腾还不得烦死阿，呵呵， 而且，把这些东西剥离或者说外部化到这些Task定义之外，好像要更“干净 ” ^[知道大连话“干净 ”咋说不？呵呵] ，也更好理解一下吧？！

OK， 打完收工，睡觉...





