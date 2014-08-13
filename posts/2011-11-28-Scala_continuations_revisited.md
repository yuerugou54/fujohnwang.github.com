% Scala Continuations Revisited
% fujohnwang
% 2011-11-28
 Scala Continuations Revisited

The continuation is all about __suspend__ and __resume__ semantic, the idea is simple, powerful, but hard to get, especially when you try to rewind your mind from threading world.

After Scala2.8, a kind of continuation is introduced, which is called **delimited continuation**, aka. _partial continuation_ or _composable continuation_, I had tried to understand scala's continuation support well before, but didn't get too much, So today, I try to do same thing again, here are some of the information that I regroup from former collections or new sources, I don't hope to understand it this time either, but at least I leave some footprint. :-)

> To understand Continuation and CPS concepts well, Jim Mcbeath's blog post on [Delimited Continuation](http://jim-mcbeath.blogspot.com/2010/08/delimited-continuations.html) is highly recommended to read!!!





---

# Classification Of Continuation
1. Full Continuation / First-Class Continuation
2. Delimited Continuations / Partial Continuation / Composable Continuation

---

# How to enable scala's continuation support?
1. enable compiler plugin
    - with compiler, `scalac -P:continuations:enable `
    - with REPL, `scala -P:continuations:enable`
2. import continuation library
    - `import scala.util.continuations._`

---

# Scala Continuation初瞥
<pre>
reset{
    shift{continuationRef=>
        // 1. do something
        // 2. execution the continuation captured.
        continuationRef()
    }
    continuationBody()
}
</pre>
**reset** will setup/demarcate the boundary of the delimited continuation while **shift** will capture the delimited continuation. In above code snippet, _continuationRef_ argument of shift is the captured DC which in fact is the _continuationBody()_ part of the code.

注意： continuation指的是reset界定中shift后面剩余的部分， 这里很容易让expression和statement搞糊涂, 比如：
<pre class="prettyprint">
reset {
    println("A")
    shift { k1: (Unit=>Unit) =>
        println("B")
        k1()
        println("C")
    }
    println("D")
    shift { k2: (Unit=>Unit) =>
        println("E")
        k2()
        println("F")
    }
    println("G")
}
</pre>
与
<pre class="prettyprint">
reset {
  1+ 
  shift { k: (Int=>Int) =>
    k(7)
  } + 1
}
</pre>
可能因为是代码写法的缘故，一开始很容易搞不清楚为什么第一个例子中`println("A")`不包含在continuation中并且自己单独先执行，而后面一个代码事例中却将开始的1＋算在continuation中，其实差别就是expression跟statement，原则上来讲， 是自己没有真正理解continuation的概念和scala的程序结构，呵呵


There are thus three types associated with shift:

* The type of the argument to pass to the continuation, which is the same as the syntactic return type of the shift in the source code.
* The type of the return from the continuation, which is the same as the return type of all of the code that follows the shift block in the source code (i.e. the type of the last value in the block of code between the shift block and the end of the function or reset block containing the shift block). This is called the untransformed return type.
* The type of the last value in the shift block, which becomes the type of the return value of the enclosing function or return block. This is called the transformed return type.

In the signature for shift, the above three types appear as A, B and C, respectively: `shift [A, B, C] (fun: ((A) ⇒ B) ⇒ C): A @scala.util.continuations.cpsParam[B,C]`

Here's where those types appear: `C = reset { ...; A = shift { k:(A=>B) => ...; C } ...; B }`


---

# yield or return
shift捕获当前reset界定的continuation，在shift中可以直接调用并执行捕获的continuation，也可以将该continuation交给其他组件而暂时不执行，比如放入某个queue中，由某个组件集中调度其执行。

* yield = return the captured continuation from shift directly without invoking it, just like a function or closure is returned as result value.
* return = invoke the captured continuation in the shift directly

除了在shift中捕获continuation并决定如何使用，我们还可以在shift中做其它事情，而不单单是为了捕获的continuation而“或者”，呵呵


---

# 潜在的可以简化的编程场景
1. doing asynchronous I/O using Java NIO
2. using executors and thread pools
3. handling cross-request control flow in web applications

---

# multitasking with scala continuations
The key different with threading model is that multitasking with continuations is coorperitive while multitasking with threads is preemptive.
So the key point to make multitasking with continuation works is we need to offer a custom scheduler for the continuations execution and coordination.

<pre>
reset {
	shift{
		cont:(()=>Unit) => {
			connect() // synchronous
			startDump() // synchronous
			scheduler.register(cont) // yield without execution immediately since the io might be not ready.
		}
	}
	readBinlogEventsAndProcess()
}

scheduler.selectAndNotify
</pre>

---

# 值得参考的项目
1. Swarm
    - <http://vimeo.com/6614042>
    - <http://www.earldouglas.com/actor-based-continuations-with-akka-and-swarm/>
2. Akka's DataFlow and NIO actor implementations
3. Jim McBeath的blog posts和github项目(Scoroutine & NIOServer)
4. Loft - <https://github.com/rschildmeijer/loft>


---

# 一些模式与规则
1. "All code inside the reset, minus the shift expressions, is turned into continuations"
2. reset的返回值类型跟shift的返回值类型相同？！ - "the value of the evaluated reset block is the value of the last expression in the shift block that gets executed within that reset block"
3. "__The gist of CPS is that we don't use return__. Rather than calling a subroutine and having it return to us, as is the case in the normal Direct Style, we pass a continuation to the subroutine for it to execute when it is done."
4. "In every method where we call a subroutine using CPS, that call is always the very last thing in the method."
5. “Any method that includes shift must be marked as CPS, and any method that calls a CPS method must be marked as CPS, until you reach the enclosing reset call.”
6.

---

# Limitations
1. Using return statements in a CPS function is unlikely to do what you expect, and may cause type mismatch compiler errors, so you should not use them.
2. When using an if statement, you may get an error like this: `Foo.scala:21: error: then and else parts must both be cps code or neither of them`
3. The compiler plugin does not handle try blocks, so you can't catch exceptions within CPS code. Those exceptions will be propagated out to the enclosing reset block and can be caught there - unless the continuation is suspended and executed later, in which case any exceptions would be propagated to the reset block of the code doing that later execution.
4. You need to be careful when using looping constructs. As [Tiark says](http://scala-programming-language.1934581.n4.nabble.com/scala-Tail-Recursive-Calls-possible-inside-CPS-td2221188.html#a2226758),
`Capturing delimited continuations inside a while loop turns the loop basically into a general recursive function.`
You can follow the above link for details, but basically each invocation of shift within a looping construct allocates another stack frame, so after "looping" many times you will likely get a StackOverflowError.
5. Some looping constructs can not be used with a shift inside them. To quote Tiark again: `In a reset block you can do anything, but shifts are not allowed everywhere. The limitation is that everything on the call path between a shift and its enclosing reset must be "shift-aware". That rules out the regular foreach, map and filter methods because they know nothing about continuations, so they can't call closures containing shift.`


---

# Read More...
1. My Question On Quora - <http://www.quora.com/How-does-Scala-continuation-work-and-has-there-been-some-successful-system-built-on-it>
2. **highly recommended** - <http://jim-mcbeath.blogspot.com/2010/08/delimited-continuations.html>
3. <http://ebruchez.blogspot.com/2011/09/continuations-in-scala.html>
4. Scala continuations and NIO meet JVM coroutines - <http://skillsmatter.com/podcast/scala/scala-continuations>
5. Swarm Discussion On Lambda the Ultimate - <http://lambda-the-ultimate.org/node/3626>
6. <http://www.slideshare.net/openbala/continuations>
7. <http://biosimilarity.blogspot.com/2011/01/rabbitmq-monadically.html>
8. <http://suereth.blogspot.com/2010/03/how-you-should-think-about-delimited.html>
9. <http://www.earldouglas.com/asynchronous-network-io-with-scala-continuations/>
10. <http://www.infoq.com/articles/deft-loft>
11. <http://lampsvn.epfl.ch/trac/scala/browser/compiler-plugins/continuations/trunk/doc/examples/continuations/Test17Webserver.scala>
12. <http://lampsvn.epfl.ch/trac/scala/browser/compiler-plugins/continuations/trunk/doc/examples/continuations>








