% A Big Piture On Concurrency

<pre>
3- Concurrency Share (Concurrency Made Easy)
    3.1 why concurrency?
        1 - 为了反应一些问题的逻辑结构（reflect the logical structures of some problems) 
        2 - 为了应对相互独立的多台物理设备（to manange multiple independent devices）
        3 - 通过同时在多部处理器上运行以题高性能（improve computing performance）
    
    3.1 the benefits to use concurrency
        3.1.1 reduce latency
            (divide and conquer, splitting one task into multiple smaller ones that's to be executed in concurrency makes it complete faster and therefore will reduce latency)
        3.1.2 hide latency (in task is blocking on some long-run operations, the concurrency can hide the long-run operations by swithc to do other tasks at the mean time)
            concurrency is not the only option to do this, other options including:
            3.1.2.1 nonblocking operations(eg. asynchronous IO)  
            3.1.2.2 an event-loop(poll()/select() on Unix)   
        3.1.3 increase throughtput (more tasks can be processed without wasting computing power)
    
    3.2 concurrency models
        3.2.1 Share-State Concurrency
            1- equipments or ways to control concurrency
                1 immutable
                2 confinement
                    thread-confinement
                        thread-specific storage (ThreadLocal in Java)
                    stack confinement
                        method confinement
                3 lock-based concurrency (locks and condition variables)
                    common gotchas
                        deadlocks (solution: resource re-ordering, interval retry)
                        livelocks 
                    tuning locks:
                        reduce time of holding locks
                        break glabal locks to fine-grained ones
                4 atomic variables(CAS)
                    Java5 java.util.concurrency.atomic.*
                5 STM (Software Transaction Memory)
                    concept
                        tx operations execute in their own thread in parallel, commit will cause one to be successful and others abort and retry.
                        
                    languages or platform that support STM:
                        Clojure
                        Concurrent Haskell
                        STM.NET 
            2- conclusion on this type concurrency
                1 perssimistic strategy (e.g. Lock, pin-loop, blocking) 
                    1 situation suitable 
                        - high contention situations
                2 optimistic strategy    (e.g. CAS(Compare And Swap/Set), STM)
                    1 situation suitable 
                        - low or middle contention situations
        3.2.2 Share-Nothing Concurrency / Message-Passing Concurrency( parallel processing)
            Actor Model Concurrency
                    (1) change its internal state,  
                    (2) send messages to peer actors,  
                    (3) create new actors, and/or  
                    (4) migrate to another computing host.
            
            representative languages
                Erlang
                Scala
            
            disadvantages
                not suitable when we really want to share something between co-workers(threads or processes)
                
                

        3.2.3    Dataflow/Declaritive Concurrency (can be under share-state concurrency too)
            1- single-assignment variable
            2- data-state trigger the process flow
 
</pre>

注： 只是一些点的记录和总结，最终成文时可能还会调整， 请持怀疑的态度阅读和采纳。