% jekyll风格到pandoc风格的转换(Transform)
% fujohnwang
% 2013-03-10

__两种思路__

1. 直接新起一个应用
	- 托管服务器是主要问题
	- 可以顺便玩玩Playframework
		- 新Post的发布方式
			- 手动本地执行pandoc转换
				- 定时扫描或者使用Java7+的WatchService使新Post可被访问
			- 服务器端实时部署和转换
				- linux下应该有pandoc，not a problem
				- 比较理想的方式
		- 不方便的地方
			- 不管是哪一种，如果post相关的资源多， 部署上传稍微会有些繁琐，单个文件最简单，但是特殊情况

2. 在现有github page的基础设施上改造
	- 旧有Posts的迁移流程梳理
		- jekyll template metadata extraction (store for later usage)
		- jekyll markdown to pandoc markdown transformation
			- YAML Front Header to pandoc markdown header 
			- paragraph adjustment
		- pandoc markdown to 1st-stage html
		- 1st-stage html to 2nd-stage html
			- add YAML Front Header back but without part of the headers before,say `permalink`
			- resource paths adjustment
		- 其它工作
			- 调整layout模版
			- 调整index.html模版		
			
	- 新Posts发布流程
		- draft markdown document in pandoc markdown syntax
		- transform pandoc markdown document to html
			- including 1st-stage and 2nd-stage html transformation
			- 第一阶段转换区别于就有Posts转换的是，需要根据当前日期生成目标文件名，旧有的Posts转换前，文件名中已经包含了相应日期
			
	- 其他说明
		- 因为有些文档写完后不想以博客的形式向外发布，所以，新的post采用本地转换然后人工决定发布与否的方式，源码与github page项目分离， 只发布最终转换后的html文档
		- 结合使用了DNSPod和Github Page的DNS服务和设置
		- pandoc转换采用了自定义的template
		- 添加了google站具相应监控


__Why I feels so fucked-me in this process?__

wandering among solutions that all can help have this thing done, all tried, all quit but finally turn to scala, the one I am familier with and also would like to write code in it, although I know some other tools are more proper.