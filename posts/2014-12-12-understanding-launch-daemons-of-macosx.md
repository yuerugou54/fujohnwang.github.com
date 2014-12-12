% 了解LaunchDaemons
% 王福强

用了这么长时间Mac， 这基础性东西今天才去了解一下， 权做笔记。

# 简单介绍

使用plist文件（一种xml格式的文件，全称为*property list*）来定义， 放入几个指定的目录，具体目录的位置决定了这个plist对应的是一个Agent还是一个Daemon， Agent和Daemon的唯一区别是他们的存放位置，以及为谁服务， Agent只为当前登录的用户服务， Daemon则是为root或者指定的用户服务。

## 目录位置说明

1. ~/Library/LaunchAgents
2. /Library/LaunchAgents
3. /Library/LaunchDaemons
4. /System/Library/LaunchAgents
5. /System/Library/LaunchDaemons

一般情况下，不需要去动/System下的agents或者daemons。

## plist文件简单说明

一般关注几项主要的配置即可：

1. Label  - 标志名称
2. Program - 运行的程序是哪个
3. RunAtLoad - 是否在加载的同时启动


~~~~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
	<dict>
		<key>Label</key>
		<string>com.example.app</string>
		<key>Program</key>
		<string>/Users/Me/Scripts/cleanup.sh</string>
		<key>RunAtLoad</key>
		<true/>
	</dict>
</plist>
~~~~~~~

以上是一个最简版的plist配置实例

# 操作

plist只是配置，要执行这些配置，需要使用**launchctl**命令， 它运行你罗列信息， 加载，卸载，启动和关闭agents或者daemons

## 获取信息

~~~~~~~ {.bash}
sudo launchctl list
~~~~~~~

返回结果类似于：
<pre>
...
1230	-	com.apple.speech.speechsynthesisd
353		-	com.apple.security.cloudkeychainproxy3
255		-	com.apple.secd
-		0	com.apple.sbd
...
</pre>

第一列表示进程号，如果有在结果中罗列，但没有数字而只是一个横线，标志虽然已经loaded， 但没有运行；

第二列是上次退出的状态号(the last exit code), 0表示成功，正数表示错误退出， 负数表示收到信号后退出。

## 加载或者卸载(load / unload)


~~~~~~~ {.bash}
launchctl load ~/Library/LaunchAgents/com.example.app.plist
launchctl load -F ~/Library/LaunchAgents/com.example.app.plist   // 如果被disabled的话， 强制启动
~~~~~~~

例如Jenkins：

- Start Jenkins: 
	- `sudo launchctl load /Library/LaunchDaemons/org.jenkins-ci.plist`
- Stop Jenkins: 
	- `sudo launchctl unload /Library/LaunchDaemons/org.jenkins-ci.plist`
	
## 启动或者停止(start/stop)

agents或者daemons可以加载但不启动，所以，事后可以单独启动或者关闭：


~~~~~~~ {.bash}
launchctl start com.example.app
launchctl stop com.example.app
~~~~~~~


	
# References
1. [Daemons and Agents](https://developer.apple.com/library/mac/technotes/tn2083/_index.html) - 手册， 太jm详细了，就是看着挺繁琐...
2. [What is launchd?](http://launchd.info/)  - 说明简单明了，较全面
3. [Mac OS X: Launchd Is Cool](http://paul.annesley.cc/2012/09/mac-os-x-launchd-is-cool/)



