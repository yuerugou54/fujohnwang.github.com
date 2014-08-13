% "初探Hadoop MapReduce NextGen|MRv2|YARN(工作上下文紧急切换， 该篇待续)"
% fujohnwang
% 2011-12-16
> 人家年初提出来的想法，我年末才刚开始看， 惭愧…

# Architecture

1. further separation of concerns on JobTracker 
	- resource management concern
	- job scheduling/monitoring concern
2. not limited to MapReduce pattern, multiple different parallel programming patterns can be used too, like master-workers, MPI, iterative monads, etc. This is a more generic distributed parallel programming framework.

# Components

1. ResourceManager
	- Scheduler
	- ApplicationManager
2. NodeManager
3. ApplicationMaster



# Interesting projects built on hadoop

1. <http://www.lilyproject.org/lily/index.html> - LILY - SMART DATA, AT SCALE, MADE EASY


# Hadoop in multiple com

##Hadoop @ 百度
	1. 透明压缩
		- linux idle class调度
		- 异步block级别压缩
	
	2. 资源隔离 
		- cgroup






# References

1. <http://developer.yahoo.com/blogs/hadoop/posts/2011/02/mapreduce-nextgen/>
2. <http://developer.yahoo.com/blogs/hadoop/posts/2011/03/mapreduce-nextgen-scheduler/>
3. <http://www.mesosproject.org/>
4. <http://developer.yahoo.com/blogs/hadoop/posts/2011/02/capacity-scheduler/>
5. <http://wiki.apache.org/hadoop/SupportingProjects>







