% 并发编程： 理论，框架与实践
% 王福强

_应在宽邀请为其团队分享该topic_


1. 为什么需要并发， 又为什么需要并发控制？
	- 充分利用某些资源
	- 避免状态破坏
	- more？！
	
2. 资源并发访问的策略
	- 悲观策略
		- lock based concurrency(theory)
		- java.util.concurrent (framework)
		- 锁还是不锁，这是个问题， 锁多还是锁少，也是个问题(practice)
	- 乐观策略
		- lock free concurrency(theory) : CAS
		- disruptor (framework)
		- 并发度高，还是并发度低的时候使用，这是个问题(practice)
3. 	我拆我拆我拆拆拆
	- task-based concurrency (theory)
		- Runnable | Callable(model)
		- Executor|ExecutorService(framework)
	- data-based concurrency(theory)
		- Actor (model)
		- Akka(framework)

4. 从个人到群体，从单机到分布式
	- divide and conquer
		- map reduce pattern
		- master-worker pattern
	- swarm framework(move computation instead of data)
	
5. 从软件到硬件
	- GPU
		- CUDA, jcuda, scuda
		- floating point computation, e.g. image reader and processing
	- PPU
		- 物理计算

6. want to know more ?
	- agent
	- STM
	- data flow
