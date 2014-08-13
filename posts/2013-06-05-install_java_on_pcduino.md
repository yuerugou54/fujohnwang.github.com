% pcDuino上安装Java运行环境
% 王福强

之前pcDuino刚拿到手的时候装过，经历了很多的曲折，昨天重新装了Kernal和ubuntu的系统更新，真tmd顺，这还不是最爽的，最爽的是，装Java运行环境也tmd很顺，人品真好，哈哈

# How to...

新的ubuntu更新之后默认安装了OpenJDK，而且是6，不爽，所以，决定重新安装Oracle的， 因为是卡片电脑，所以需要装[Java Embedded](http://www.oracle.com/technetwork/java/embedded/index.html)版本的jre，遂下载ejre-7u21-fcs-b11-linux-arm-vfp-server_headless-04_apr_2013.gz， 随便找个目录解压：


```bash
tar xvf ejre-7u21-fcs-b11-linux-arm-vfp-server_headless-04_apr_2013.gz
```

<blockquote>
$ sudo apt-get install -y libc6-armel libsfgcc1

这一步不知道是否必须，反正我是运行了
</blockquote>

之后，__重启__！！！

成果展示:

<pre>
ubuntu@ubuntu:~$ ./java/ejre1.7.0_21/bin/java -version
java version "1.7.0_21"
Java(TM) SE Embedded Runtime Environment (build 1.7.0_21-b11, headless)
Java HotSpot(TM) Embedded Server VM (build 23.21-b01, mixed mode)
</pre>

当然了，也可以将安装路径添加到bash的配置文件里：

<pre>
ubuntu@ubuntu:~$ cat .bash_profile
export JAVA_HOME=~/java/ejre1.7.0_21
export PATH=$JAVA_HOME/bin:$PATH
</pre>

至此，基本搞定， 后面可以开始玩动态DNS绑定，并部署个Play应用玩玩了...

<blockquote>
BTW. 如果闲麻烦，可以直接运行apt-get install arduino最简单，只可惜安装的是openjdk6, :)
</blockquote>

# References
1. <https://github.com/cjdaly/napkin/wiki/Server-on-pcduino>
2. <http://bradsmc.blogspot.com/2013/04/installing-java-runtime-environment-on.html>






