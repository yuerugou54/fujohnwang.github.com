% Explore The Scheduling of Scala Actors
% fujohnwang
% 2010-12-25
__author: fujohnwang__

We know that Scala provides 2 kinds of actors, that's, “Thread Based Actors” and “Event Based Actors” , usually, they are not replacable. You most of the time will use “Thread Based Actors” in scenarios with busy messages and use “Event Based Actos” in scenarios with not so busy messages. The reason is that if in your scenarios that high volumn messages will be emitted quickly without time intervals, “Thread Based Actors” will process these messages in their pre-allocated threads until the messages are done, without any context-switch. But for “Event Based Actors” , the messages are not guranteed to be alwauys processed in one thread, but maybe dispatched to different threads for different messages, clearly, context switch cost may cause performance penalty here. In low or moderate pressure, this is ok, but for high volume pressure, “Thread Based Actors” will excel in theory.

But to know only these differences between “Thread Based Actors” and “Event Based Actors” won't help too much. If we want to gain more peformance improvements from Scala Actors, we have to dive into the scheduling of scala actors to find more...

# Default Scheduler of Scala Actor
Usually, when we create actors as most of the code demonstrated:

```scala
import scala.actors._, Actor._

val myActor = actor{
 ...
}
```
   
A ForkJoinScheduler with 4 threads will working behind to support the running of our actors created this way. I do think if you are not writing toy code, the 4 threads can't suffice. So if you find your applications sucks after switching to Scala actors, don't worry, it should be if you don't tuning any thing about Scala Actor.

Let's move on to find out how to tuning the scheduling of scala actors so that they can meet our needs.

# Customize The Schedulers of Scala Actors
Since Scala Actors are running in JVM, they can't escape from the basic facilities of JVM and fly in their own sky. That's, to implement Scala Actors so that they can support new concurrency model, we still have to use JVM's threading model as the fundation. For Scala Actors, the threading things are abstracted and hidden after IScheduler. Each tasks or events or messages will be submitted and processed by some IScheduler instance finally. You make the IScheduler work well, you make Scala Actors work well.

## Tuning Global Scheduler
Each Actors that's created by using Actor.actor{..} helper method will use the default global scheduler as their scheduler. That's exactly the ForkJoinScheduler with 4 threads. Most of the time , 4 is not enough, to adjust it, We have 2 ways:

1- Replace the default scheduler in your code like this:

`Scheduler.impl = new ForkJoinScheduler(coreSize, maxSize, deamon, fair)`

Of course, since IScheduler trait has many other implementations, like SingleThreadedScheduler or ResizableThreadPoolScheduler, you can choose to use them as per your needs, as long as you do understand how to use them,:-)

2- We can also configure the pool size of the default scheduler by VM properties, e.g:

`java -Dactors.corePoolSize=4 -Dactors.maxPoolSize=10 ...`

Simple and easy, ha?

## Tuning Specific Schedulers

I hope your application is simple, so that one default scheduler will suffice, but most of the time, it's not true, for example, If I want to implement an SEDA style application with different actors stand in different staages, use only the default scheduler will not help too much. Simply put, you can't tuning each stage specifically in the point of theading pool. In such situations, we have to customize the schedulers of each scala actors specifically.

We can't implement different Actors for different uses with different schedulers by override the “scheduler” method of them, here is an example:

```scala
class MyActor extends Actor {
  import scala.actors.scheduler._

  def act() {
    println("MyActor exit with = " + scheduler)
  }

  override def scheduler(): IScheduler = new ForkJoinScheduler(Runtime.getRuntime.availableProcessors, Runtime.getRuntime.availableProcessors * 2, false, true)
}
```
    
As long as you know how to estimate how many threads should be used in each stages, you know how to override the “scheduler” method to provide proper IScheduler instances.

# Conclusion

After Scala 2.8, Scala Actors are much easier to extend, but it's still not perfect, that's why other actor implemenations on scala platform exist, like Akka Actor and Lift Actor. One of the key point of a successful Actor library is that how they schedule the running of their actors, Erlang Actors are running in their own VM, so they can be scheduled in their way, but Scala Actors are running in JVM, so they have to be scheduled in threading model. Although this is a limitation, but improvements still can be done.

Although Scala Actors are based on JVM's threading model, but I think they do know how to balance the pros and cons on the design and implementation of Scala actors. For example, I found that although I can adjust the pool size of the schedulers, but I can't change the thread names in the pool, that's annoying when trying to find thread problems in our applications. But in the other hand, Since Actor are a higher level concurrency model, scala actor implementation should make the threading things transparent to the users. It's a hard tradeoff, for now, we still have to touch the threads to gain a better result, but who knows, maybe in the future, we won't bother about it any more.
