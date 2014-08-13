% mysql多个slave使用同一个server id会怎样？
% fujohnwang
% 2011-05-31
__author: fujohnwang__

答案是： master会只保留一个slave的replication连接， 而把其它的replication连接给“咔嚓”掉，即便其他replication已经开始并处理了一段时间。


引出这个其实是周六EroX在某个部署环境下会莫名的， 无端的连接中断， 即使重启数次也还是那个熊样， 之前出现过这个原因， 但后期测试再没出现过，也就放下了，现在又出现，让我很是纳闷， 日志里有没有标志性的异常信息，很是丈二和尚摸不着头脑。

在让“小情歌”不断尝试的同时，我google啊google， 不停的变换搜索关键字， 反正是忘了换了多少次，依然无果。 实在没辙，再回头看代码， 发现一个debug的日志点， 就让小情歌改变日志级别重跑， 然后搜索日志， 发现果然是在接收replication事件的过程中收到了意想不到的EOF packet， 这tmd就怪了， 常见的几个出现EOF packet的场景都不可能在replication已经进行了一段时间的情况下出现啊， 继续google...

最终， 老天有眼， 让我看到了[这篇blog](http://hatemysql.com/2010/08/21/双slave的server_id相同的问题/)， 不知道是netty吞了连接关闭的异常信息还是咋的， 即使我们把日志级别调成了debug，却没有发现跟这篇blog中提到的错误消息相似的信息， 不过， 现象嘛，可实在太像了， 嘿嘿， 赶快查一下EroX的配置和master数据库其他slave的server id是否相同， 不查不知道，一查就知道， 原来真tmd是server id相同啊， 都是11， 回头想想， 之前测试的时候也出现过这个问题估计也是这么回事了， 你想啊， 测试的时候随便写个1， 11， 111之类的不很常见嘛...

如果大家想更加深入的了解这个问题的本质， 可以参考我们的DBA对这个问题的分析<http://www.penglixun.com/tech/database/mysql_multi_slave_same_serverid.html>， 或者参考以上博客...

GL & HF, wish you won't run into such stupid situation(wasting time instead of enjoying the weekend) ^_^
