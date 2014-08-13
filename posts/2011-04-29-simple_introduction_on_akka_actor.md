% Simple Introduction On Akka Actor
% fujohnwang
% 2011-04-29
__author: fujohnwang__

Simple Introduction就是简单的介绍， 呵呵，所以， 这篇文字只是初级的浮于表面的泛泛之谈， 不要期望写的有多么事无巨细， 写得有多么深入人心，简单了解一下吧，感兴趣自己再找资料啃呗！

Akka Framework有一堆modules， 这里只扯它的Actor支持， 什么不知道Actor是啥？哦， 那先去看看Erlanga啦， Wikipedia啦， Scala Actor啦， 然后再回来看这篇文字，呵呵，因为我不会那么详细的告诉你Actor到底是个什么东西， ：）

# Actor Vs. Task
Actor和Task那， 可以说是Share Nothing/Message Passing Concurrency跟Shared State Concurrency的标志性 “建筑物” ， 要比较很细的话， 可能也是一家有一家的话要说， 我那只说一点我的观点， 各位看官要是看得不爽， 也别喷， 欢迎纠正，使得我也可以迷途知返嘛！

我的观点，简单来说， Actor可以维护多次逻辑执行的状态， 而Task则不维护这种逻辑多次执行的状态。 举例来说， 你把一个Callable或者Runnable提交给ExecutorService，并且当这个Task(Callable和Runnable算是Task的某些具体形式)执行完成后， 它的状态就此完结， 不会对下一个提交并执行的Task产生任何影响（当然， task与task之间通过某种全局的状态管理的情况除外）； 而Actor则不同， 一笔按照Actor的逻辑执行后， 大部分会对下一笔程序同样逻辑执行产生影响，因为它们的执行状态是通过Actor本身来维护的。 本质上，这种情况算是基于Task的并发建模与基于数据的并发建模的差异所造成的。

扯完这些，我们下面看本篇文字的主角-Akka Actor。

# Akka Actor Quick Start
Akka Actor是Scala平台上的一种Actor实现， 其它的还有Scala本身类库提供的默认Actor实现，Lift框架的Actor实现，等等， 但Akka相对于其它Actor实现来说， “忽悠” 的名声有点儿大（ 因为基本上所有Actor实现上， 一些基本的组件都会有的，只不过，它忽悠的也还好， 性能确实比Scala默认提供的Actor实现要好），所以这也就是为啥要简单介绍一下它的原因啦～

AkkaActor分两种， 一种叫Untyped Actor， 这种Actor就是常见的跟Erlang的ActorAPI实现和使用方式类似的那种， 而另一种叫Typed Actor, 它是通过API的强类型约束来声明和实现Actor， 后面会详细介绍它，我们先看Untyped Actor...

## UnTyped Actor
Untyped Actor的实现很简单，只要实现akka.actor.Actor这个trait就行：

```scala
class MyActor extends Actor{
     protected def receive = {
       case msg:String=> println("receives message:"+msg)
       case _=> println("unexpected message")
     }
}
```
    
一个Untyped Actor，只要实现receive方法就行了， Akka会有外围的调度框架来调度和执行它。知道了怎么定义一个Akka Actor， 那下面就是怎么创建和使用它了。

### Akka Actor的创建和启动
AkkaActor并不是直接实例化你的Actor定义并使用它，而是通过ActorRef来引用和管理你的Actor实例， 代码说话：

```scala
val myActor:ActorRef = actorOf[MyActor]
myActor.start     
```

akka.actor.Actor.actorOf工厂方法会根据你的Actor定义的类型来实例化和启动相应的Actor实例， 返回的Actor实例是ActorRef类型，而不是Actor类型，这个与Scala的Actor是不同的地方。（以上myActor定义的时候声明的类型是为了强调， 实际上因为有Scala的类型推导，此处的类型是可以省略的）
akka.actor.Actor.actorOf还有许多变体（重载）， 比如， 如果我们的Actor定义有构造方法参数：

```scala
class MyActor(id:String) extends Actor{
     protected def receive = {
       case msg:String=> println("receives message:"+msg)
       case _=> println("unexpected message")
     }
}
```
    
那么， 可以使用以下形式来实例化和启动Actor:

```scala
val myActor:ActorRef = actorOf(new Actor("my actor's id"))
myActor.start     
```

更多构造方式可以参阅Akka的Scaladoc， 此处有在线版本: http://akka.io/api/1.0/akka-actor/

	Tip
	Actor的实例化和启动也可以以chaining的方式一次搞定:
		val myActor:ActorRef = actorOf(new Actor("my actor's id")).start   
     
### Akka Actor的使用
Actor是以消息传递(Message Passing)的方式来实现并发的，所以， 使用Actor基本上就可以简化为Actor间的发送消息， 与大部分Actor一样， AkkaActor提供了以下几种消息发送方式:

- ! - 异步消息发送.  (!在这里读“bang”， 单一的!表示fire-and-forget形式的异步消息发送，该方法会即可返回而不用等消息处理完毕， 属于最常见的actor操作，也是效率更高的。 异步意味着系统的各个部分和资源可以得到更有效的利用.)
- !! - 异步消息发送并且同步等待. (bangbang可以表述为fire-and-wait, 消息也是异步处理，但知道消息处理完成之前， !!方法不会返回。 显然这种方式效率不如!的形式， 最好是迫不得已的特定场景下才选用它。)
- !!! - 异步发送消息并返回Future.  (bangbangbang与我们executor.submit返回一个future类似， 它发送完消息之后即可返回一个future， 拿到这个future之后，用户自己来决定等待还是先做其它事情。可以描述为fire-and-reply-asynchronously.)

为了说明以上说明场景，假设我们有这么一个Actor，它收到数字类型的消息，则直接异步处理； 收到字符串形式的消息， 则将其变为大写，并返回， 该Actor定义如下：

```scala
class MyActor extends Actor{
  protected def receive = {
    case n:Int => println("receives number:"+n)
    case msg:String=> self.reply(msg.toUpperCase)
    case _=> println("unexpected message")
  }
}
```
     
现在，要Actor开工:

```scala
val myActor:ActorRef = actorOf[MyActor].start
myActor ! 1                            //receives number:1
println((myActor !! "darren").getOrElse("impossible"))        // DARREN
val future = myActor !!! "darren"
// do something else
future.await
future.result.asInstanceOf[Option[String]] match{
   case Some(str)=> println("converted string:"+str)  // converted string:DARREN
   case None => println("impossible")
}
```
     
See？ Easy And Fun! 就这么简单， 别在问我其它的了，我也不知道 ：-）

### Stop or KillTheActor
Actor干完活， 不能让它在哪儿浪费资源不是？！ 要关闭/停止Actor， 可以采用多种形式， 最常见的就是直接掉actor.stop, 其它几种方式还有:

1- Actor接收到特定消息后主动退出. 

```scala
case object ShutdownSignal

class MyActor extends Actor{
  protected def receive = {
 case ShutdownSignal => exit
 case _=> ...
  }
}
```
     
2- 喂毒药.  每次定义个ShutdownSignal啥的， 还得每次都在Actor实现的时候处理， 真麻烦， 时不？ 其实，可以不这么干， 直接喂你的Actor， 毒死它，哈哈.... 只要`myActor ! akka.actor.PoisonPill`， 你懂的...

3- 通过ActorRegistry统一关闭. `Actor.registry.shutdownAll`， 你也懂的

### Lifecycle Callbacks
除了启动和关闭Actor， Akka Actor还有其它几个有关其生命周期的回掉方法:

- preStart
- postStop
- preRestart
- postRestart

具体在啥时候触发，方法名已经很明了了， 至于用来干什么，也不用多说吧？！做做资源初始化啦， 资源清理啦，这是最常见的用途咯

### Other Features
Akka Actor还有些比较有趣的特性，比如运行期间的Actor逻辑互换， 即在运行期间，将当前的处理逻辑替换为新的处理逻辑， 这可以通过Hotswap消息或者become方法来实现， 以Hotswap消息为例(摘自Akka Actor文档):

```scala
actor ! HotSwap( self => {
  case message => self.reply("hotswapped body")
})
```
     
当actor收到这个消息之后，就会使用新的处理逻辑来处理后继的消息。这个新的block会被放到最stack的最上面， 本着“自己的屁股自己擦”的原则，是不是用完后要处理掉那？ become和unbecome可能更能说明这个问题， 有创建就有销毁嘛！

```scala
class Swapper extends Actor {
 def receive = {
   case Swap =>
     println("Hi")
     become {
       case Swap =>
         println("Ho")
         unbecome // resets the latest 'become' (just for fun)
     }
 }
}
```
    
Hi， Ho， Hi，Ho...

## Typed Actor
Typed Actor就是通过强类型的接口约束以及其POJO形式的实现而表现出来的一种Actor形式。Akka框架通过AOP对这些POJO实现相应的接口方法进行拦截和代理， 从而实现跟UntypeActor类似的异步消息处理。 反正方法调用本来也是消息传递的一种嘛。

Typed Actor是根据接口的方法定义的Signature来决定处理语义的， 比如是异步的消息处理，还是异步接收同步返回， 以以下接口为例：

```scala
interface RegistrationService {
  void register(User user, Credentials cred)
  User getUserFor(String username)
}
```
    
返回值为void的方法， 相当于Untyped Actor的！方法， 而有返回值的， 则相当于！！， 至于跟!!!相当的方法定义，就得返回特定的akka.dispatch.Future类型咯。

Typed Actor个人没有啥喜好，所以不做赘述， 感兴趣可以参考 官方文档 。

# Dispatcher
所有的AkkaActor创建之后，如果没有特殊设置，都会使用Dispatchers$globalExecutorBasedEventDrivenDispatcher$进行运行调度， 从运行日志就可以看出:
<pre>
14:27:19.659 [main] DEBUG a.d.Dispatchers$globalExecutorBasedEventDrivenDispatcher$ 
   - Starting up Dispatchers$globalExecutorBasedEventDrivenDispatcher$[akka:event-driven:dispatcher:global]
 with throughput [5]
 </pre>
 
但很多时候， 所有的Actor使用同一个Dispatcher是远远不够的。

要定制相应Actor的运行调度， 可以为其设置特定的Dispatcher实现， 常见的Dispatcher实现在akka.dispatch包下， 如:

- ExecutorBasedEventDrivenDispatcher
- ExecutorBasedEventDrivenWorkStealingDispatcher
- HawtDispatcher
- 
可以通过两种方式为我们的Actor指定自己的Dispatcher， 第一种， 直接在Actor定义中指定：

```scala
class MyActor extends Actor{
 self.dispatcher = ...
}
```
   
使用哪种现有的Dispatcher实现或者自己实现， 可以根据自己的当前环境来决定。 另一种， 可以在Actor的ActorRef上设置:

```scala
val myActor = actorOf(..)
myActor.dispatcher = ...
myActor.start
```

只要在Actor启动前设置即可， 个人倾向于这种方式。

# Fault Tolerance Facility
为了保证系统的健壮性， Akka提供了类似于Erlang的Actor Supervisor的策略， 那个图我就不贴了，就是按照层次， 上层监控下层，如果下层有Actor挂掉了， 那么由上层的Supervisor来决定是重启这个Actor那，还是重启Supervisor当前监控的所有Actor， 所谓的OneForOne和AllForOne策略。

基于Supervisor的策略的基本原则是Let it crash, 当目标Actor crash掉之后， 要么重启它或者相关的actors， 要么正常关闭它。所以， 有几点主要要素要考虑在内， 其一， 需要监控哪些条件来判断目标的Actor有没有crash， 这通常是通过Exception来确定的；其二， 当监控到目标Actor crash之后， 是要重启它，还是直接关闭它？ 其三， 当要重启它的时候，是只重其它那，还是重启跟它相关的所有Actors？ 本着这几个要素，Akka 提供了相应的设施来界定这些关注点。

要实现基于Supervisor的Fault Tolerance管理， Akka中通常采用所谓的声明式的实现方式， 以下是常见的代码：

```scala
val supervisor = Supervisor(
  SupervisorConfig(
    AllForOneStrategy(List(classOf[Exception]), 3, 1000),
    Supervise(
      actorOf[MyActor1],
      Permanent) ::
    Supervise(
      actorOf[MyActor2],
      Permanent) ::
    Nil))   
```

Supervisor有一个company object提供了工厂方法来创建和启动一个Supervisor实例，即以上代码种的supervisor， 该工厂方法接受一个SupervisorConfig参数， 而SupervisorConfig参数可以定义Fault Handling的策略，以及要对哪些actor进行监控， 在以上代码中， AllForOneStrategy(List(classOf[Exception]), 3, 1000)定义了当捕获到目标Actor抛出Exception之后， 重启当前supervisor监控的所有actors， 并且最多重启3次， 时间不超出1秒钟。该项配置阐述了我们之前提到的第一和第三要素。SupervisorConfig第二个参数是接受了一组Supervise的List， 每个Supervise定义一个被当前supervisor监控的actor信息， 比如要监控的actor实例引用，以及该actor crash掉之后， 只要一直重启还是只是正常关闭它， 这阐述了我们之前提到的第二个要素。

通过Supervisor创建Supervisor， 最终创建后的实例是已经start的， 不需要显式的start； 不过， 如果需要控制实例创建的时机以及start的时机， 可以使用akka.actor.SupervisorFactory类:

```scala
val factory = SupervisorFactory(
   SupervisorConfig(
     AllForOneStrategy(OneForOne, 3, 10, List(classOf[Exception]),
     Supervise(
       myFirstActor,
       Permanent) ::
     Supervise(
       mySecondActor,
       Permanent) ::
     Nil))  
val supervisor = factory.newInstance
supervisor.start // start up all managed server 
```
   
不过，大多数情况下，使用Supervisor就OK啦。

以下是一个完整的实例， 可以一窥声明式的Fault Handling是多么的简单和容易理解：

```scala
class MyActor extends Actor {
 // self.setLifeCycle(akka.config.Supervision.permanent)
      override def postRestart(reason: Throwable) {
        println("restart is done.")
      }
      protected def receive = {
        case n: Int => println("receives number:" + n)
        case msg: String => self.reply(msg.toUpperCase)
        case _ => throw new Exception("xxxx")
      }
    }
    import akka.config.Supervision._
    val myActor: ActorRef = actorOf[MyActor].start
    val supervisor = Supervisor(SupervisorConfig(
      AllForOneStrategy(List(classOf[Exception]), 3, 1000),
      Supervise(
        myActor,
        Permanent) ::
        Nil))
 // supervisor.link(myActor)
    myActor ! Array[Byte](0x00)
    myActor ! 111

    TimeUnit.SECONDS.sleep(5)

    supervisor.shutdown
    myActor.stop
```
   
在myActor ! Array[Byte](0x00)之后， myActor会抛出异常， 但因为有supervisor监控它并重启了它，所以myActor ! 111可以正常处理。 如果不使用声明式的fault handling， 那么需要在supervisor以及被监控actor内部各自声明一些钩子， 以上代码实例中被注视的代码算是一部分， 使用了声明式的方式，这些就都不需要了。

要了解fault handling更多信息，包括编程式的fault handling方式， 可以参考 这里 .

# Remote Actor
使用就不扯了，简单说一下原理，不过我自己瞎猜的，没看它代码，呵呵，
RemoteActor要实现起来其实很简单， 直接对ActorRef做Proxy， 发送端结点与接收端结点采用不同的逻辑实现， 即接收端结点起相应的TCP服务接收请求， 然后对接收到的请求进行分类并分发给相应的本地Actor， 如果是异步消息，则返回；否则等处理完后， 再将处理结果通过TCP连接发送回发送端结点；发送端结点的Proxy直接发起TCP连接到目标接收端， 之后，只要本地Actor收到请求，就通过该TCP连接将消息发送给目标端TCP Server，server处理逻辑前面已经说了。

在这里， 如果设计好的话， tcp的交互协议可以扩展， tcp的通信序列化协议也可以扩展， 比如， 针对Akka不支持protostuff的现状， 插入自定义的protostuff序列化装置就很好嘛，哈哈

# Conclusion

AkkaActor相对于其它的实现来说更成熟一些， 但没有最好，只有更好，希望Scala平台的Actor更加成熟，更加快速，更加强大...
