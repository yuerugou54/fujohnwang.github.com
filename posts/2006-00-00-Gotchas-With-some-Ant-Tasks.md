% Gotchas With some Ant Tasks
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

这几天没啥好写的，随便抓个主题涂鸦一下，所以，暂且以Ant的一些Task的问题作为话题。

大部分的Ant Tasks在发行版附带的manual里面都会提供相应的sample build script，但是，如果你以葫芦画瓢的把这些build script片段copy到你的build文件的话，往往会导致你的build失败。

你会说了，靠，官方文档提供的还会有错？！唬人吧你？！

不信？！上眼瞧啦~

~~~~~~~ {.xml}
<exec dir="D:\InstallAnywhere 5.5 Standard\" executable="build.exe" output="buildresult.txt" error="builderror.txt">
	<arg value="＄{installAnywhere.credit.buildfile}"/>
</exec>
~~~~~~~

你说这段build script会不会成功那？！没有问题吧？！不过那，虽然理论上应该build Successfully，但，事实并非如此，不管你以这种方式运行什么命令，都会抛出该死的“...IOException:(.*),error=2”错误信息，不信你就试试，除非你采用如下方式来使用exec task：

~~~~~~~ {.xml}
<exec executable="D:\InstallAnywhere 5.5 Standard\build.exe" output="buildresult.txt" error="builderror.txt">
	<arg value="＄{installAnywhere.credit.buildfile}"/>
</exec>
~~~~~~~

看出差别没？！你指定命令所在的dir根本没有用，需要通过绝对路径指定executable！不要问我为什么，反正只有这样才能正确调用,I don't know why either.

平常工作中经常有scp操作吧？！那你看下面这个简单的task能否build成功那？！

~~~~~~~ {.xml}
<scp file="file location to scp" todir="user:password@server:/home/user"></scp>
~~~~~~~

铁定的，类似这样的build 失败信息你是吃定了：
<pre>
BUILD FAILED
...\build.xml:11: com.jcraft.jsch.JSchException: reject HostKey: yourServerIp
</pre>

不信，你看看ant的manual,他是不是告诉你这么用的？！

所以你现在也不得不信了吧？！还是听我的，在scp task的attribute里面添加一个trust="true"试一试吧！

类似的sshexec task也有这个问题，同样的解决方式。

其实如果你ant用的多了，这些小问题自然而然会冒出来烦你的，不过，google一下子，应该可以很快解决，呵呵，good luck今天就扯这么些