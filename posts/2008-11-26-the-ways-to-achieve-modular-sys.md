% The Ways To Achieve Your Modular System

进CSTS(大连)之后，当前的项目的frontend是用Swing实现的，这本来没啥惊奇的，但是，时间长了却发现一个比较现实的问题。本来以为当前的GUI的framework只是我们team用的，后来才发现，另一个Team开发的功能也要挂在这个应用框架之上，当两个team的发布时间不同的时候，问题就来了，我们Team要做Code Freeze的时候，需要同时协调另一个Team是否继续让他们提交代码，因为同时在一个Branch上开发，同时提交到相同的codebase，或许通过配置管理可以解决这样的冲突，但是，为什么不一开始就通过模块化系统的设计，来分离这样比较独立的功能模块那？这就促使我想要提一提标题所给出的这个话题... 

但是，即使是仅关注桌面应用的开发这一块儿，即使是仅关注Java平台上桌面应用这一块儿，也会发现，整个话题牵扯的范围依然过于宽泛了，根本就不是三言两语可以说清楚的，即使是三言两语说出来的，也可能只是皮毛而已。 而且现在感觉自己各方面状态不好，所以，这个话题暂且搁置了吧，不过，把开始的构思先放这里，或许以后某一天，我可以从这里继续... 

<pre>
==The Ways To Achieve Your Modular System==
Phase 1: 
1-Interface + Classloader custom solutions 
2-The service discovery mechanism in Java.(SPI) 
  eg. JDBC drivers, providers for sounds api. 
3-NetBeans Platform Modules (as if the LookupAPI is based on 2) 
  3.1 Spring RCP 
Phase 2: 
4-OSGi and JSR277 or JSR294??? 
  4.1 Eclipse Plugin Modules (Eclipse RCP) 
  4.2 Spring DM 
5-... 

Reference: 
1)-http://www.javaranch.com/journal/200607/Journal200607.jsp?print=on#a1 
2)-http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider 
3)-http://mockus.org/optimum/JavaTutorial/mustang/sound/SPI-intro.html    (not too necessary) 
4)-http://dvbcentral.sourceforge.net/netbeans-runtime.html 
5)-http://netbeans.dzone.com/news/from-pain-gain-swing-and-netbe 
6)-<<Rich Client Programming:Plugging into the NetBeans Platform>> 
</pre>

即使是从现在的结构也可以看出来，要参考的资料还是很多的，现在的参考列表还只是个开始而已。不过，要是其他人对这个话题感兴趣，倒是可以继续哈，我想之前这样的话题也应该有人提过了吧，没有google过，但能够从总体上对java平台上的桌面应用开发中的架构思考的资料，应该还是比较令人期待的吧？！
