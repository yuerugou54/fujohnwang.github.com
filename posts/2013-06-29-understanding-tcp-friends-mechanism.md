% 了解TCP Friends机制
% 王福强

在今年的ADC试讲会上听日子哥在其PPT中提到tcp friends，之前没有了解过，遂打算简单了解一下， 而且感觉这东西可能对早先几年我的那个"实现一个基于网络层面抽象的actor库""的念想的实现有些好处，只可惜当时只是想简单的通过loopback走，却不知道有这东东...

tcp stack层面的短路， 比loopback更近一步的减少了不必要的开销

loopback是避免了网路上的开销，而tcp friends则是进一步的减少了内核tcp stack层面的开销；

使用网络层面的抽象来实现actor， 一个好处就是可以使得本地actor跟远程actor之间的差异透明化； 另外一个就是调度可以直接走NIO的API， 并利用网络层的一些基础设施和功能，比如buffer， 流控等；最最根本的是， 相对于akka actor的实现，偶一直很怀念最早scala类库里的那个actor实现， 不管是内存语意上跟jvm的规定相吻合也好，还是actor定义和行为控制也罢； 







<blockquote>
如果把net.ipv4.tcp_{rmem/wmem}的配置写成超过4GB的配置，Linux的TCP实现就可能因为溢出而拒绝发出任何数据——这个bug在RHEL6和upstream里都有！如果backport了TCP friends，那么1GB的配置就会出现问题了！

---http://tweets.seraph.me/search/%E6%B7%98%E5%AE%9D%E5%86%85%E6%A0%B8%E7%BB%84
</blockquote>

# 参考
1. [TCP friends](http://lwn.net/Articles/511254/)
2. [net-tcp: TCP/IP stack bypass for loopback connections](http://lwn.net/Articles/511079/)
3. <http://kernel.taobao.org/index.php/%E5%86%85%E6%A0%B8%E6%9C%88%E6%8A%A52012-08>










