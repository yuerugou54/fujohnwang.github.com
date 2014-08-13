% How to "Play" Big - 记Playframework在大型网站架构下的实践探索
% 王福强


1. 部署与发布
	- mechinisms
		- git 
			- 生产环境每个节点上各scala应用共享maven本地库
			- 只需要pull必要指定的版本进行部署，可以及时回滚
		- rsync
			- 单台发布机进行checkout和编译， 不必要的传输量， 回滚需要重新传输同步
	- deployment web app
		- incremental deployment and rollback
	- Port Forwarding
		- 使用iptables， 不建议play使用1024以下端口，需要root权限

	
2. CDN in another way?!
	- Pros and cons of playframeowrk
	- play with netty inside is necessary, not enough 
		- async is good
		- one-stack-for-all is not good for large scale deployment
		- 防盗链/防攻击
		- 流量统计
		- 缓存调度
















