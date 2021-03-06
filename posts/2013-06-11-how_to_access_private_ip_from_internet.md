% 教你如何使用卡片电脑对外提供网络服务
% 王福强

**Tags**: 私有IP(Private IP), DHCP, NAT, Router, 动态IP(Dynamic IP), DNS, Dynamic DNS

买个卡片电脑总得让它排上点儿用场，对吧？ 耗电低，当个服务器挂着对外提供点儿web服务啥的是个比较普通又能够快速想到的用途，挂个博客或者小站啥的，还是比较可行的嘛，所以，开工～

# 私有IP的壁垒
假设家里的网络结构是这样的：

<pre>
猫 -> 路由器(router) -> (pc, laptop, card computer, ipad, smartphone...)
</pre>

那么，不出意外的话，我们的卡片电脑会被分配一个私有IP(遵循RFC1918)， 这个私有IP的边界被限定在路由器后面，从而外部网络实际上是无法访问到我们的卡片电脑的， 我们遇到第一个障碍 ：（

<blockquote>
外延

本地家庭网络内各个设备的本地私有IP实际上是路由器根据DHCP自动分配的， 通常是动态的IP， 我们也可以手动配置静态IP。
</blockquote>


# Public IP的巧取豪夺
既然我们的卡片电脑获得的私有IP无法被外部访问并对外进行服务，那么我们能不能给它搞一个Public IP那？ 实际上，ISP本来就分配了一个Public IP给我们，只是在我们的本地网络拓扑中，被路由器(router)使用了，如果我们不用路由器，直接让卡片电脑通过猫猫来联网的话，外部就可以通过ISP分配给我们的Public IP访问我们的卡片电脑了，所以，问题解决了。

# 命名服务让生活更美好
你是愿意让外部的访问者（甚至包括你自己）每次记住IP地址的数字串来访问卡片电脑，还是更愿意只记住一个容易记住的名字？ 我想，大多数人会选择后者吧！

我们可以为我们的卡片电脑配置hostname，但是，hostname的机制太狭隘了，没法从更大范围上通过它来访问目标机器，所以，我们需要求助DNS， 这tmd才是global的那！

注册一个域名(Domain Name), 在域名服务商的name server上将域名和卡片电脑使用的Public IP的映射关系进行配置，搞定之后，等合适的时间间隔之后(域名记录的分发需要时间，你懂的，对吧？！)， 我们就可以直接让外部通过域名访问我们的卡片电脑啦～


# Dynamic IP之痒
Shit Happens～

某一时刻，或许是其他访问者告诉我们，通过域名无法访问我们的卡片电脑了，或者也可能是我们自己发现无法访问的， 到底是哪里出了问题那？

我们从DNS的name server开始查，发现name server没事儿； 那是我们自己的卡片电脑挂了？ 也没事儿啊！ 那么，是映射记录被人改了？ 我擦， 怎么卡片电脑的Public IP跟原来配置的不一样了那？！

原来， ISP分配给我们的Public IP是有租期(lease)的，过了租期，ISP可能让我们继续续租（即让我们继续持有原来的IP），也可能给我们换一个IP, 这种情况下，我们通过域名访问的就是原来的IP啦，而我们的卡片电脑现在持有的却是另一个IP，当然就访问不到咯！

那么咋整那？！使用动态DNS服务(Dynamic DNS, DDNS)呗！

偶的域名是godaddy注册的，但没稀的用他家的name server，改用了[DNSPod](https://www.dnspod.cn/)， 所以，可以通过API来更改域名记录， 大体上，这个过程是这样的：卡片电脑上部署一个daemon程序， 该程序定期检查Public IP，如果发现跟原来持有的不同，则通过DNSPod的API接口更新对应域名的A记录，这样， 不管Public IP怎么变，我们都可以及时的更新域名记录，从而保证通过域名访问畅通无阻！

<blockquote>
当然啦， 域名记录的更新间隔，以及域名记录的分发时间，都会多少影响服务访问的可用性，这显然不是啥大问题，俺们又不是在搞啥商业高可用网站，是吧？
</blockquote>

# 路由器，我们不能失去你～
卡片电脑直接通过猫猫联网倒是解决了自己的生存出路，可是，其他设备咋活啊？ 人家的pc啦， laptop啦， ipad啦，也都需要上网啊，总得给人家条活路不是？ 群众的呼声是不能忽视的，我们还得把路由器接回来！

将路由器接回来之后， 路由器就持有了ISP分配给我们的Public IP， 而为我们的所有本地设备分配了相应的Private IP，出路的问题解决了，我们需要继续解决卡片电脑的访客通道问题， 这个时候，就该让NAT ([Network Address Translation](https://en.wikipedia.org/wiki/Network_address_translation))出场了！

NAT简单来讲就是把内部的Private IP跟外部的Public IP做一层关系映射，映射关系的确定可以是通过端口，也可以通过IP或者其它多种方式， 一般的路由器现在都有NAT软件和相应的功能， 只要设置一下就可以了，具体如何设置可以参考自己的路由器说明书，这里不做赘述。

NAT配置好之后，就剩下一个问题了，即Public IP的获取。 我们用来获取Public IP的daemon程序是部署在卡片电脑上的，而卡片电脑现在分配到的是本地私有IP，`ifconfig`在这里是帮不了什么忙的， 不过有几种思路供参考：

1. 通过查找本地路由表信息，然后解析出相应的路由记录中的Public IP信息: `netstat -nr`
2. 访问相应的显示IP信息的web站点，然后提取，比如[Chinaz站长工具中的IP查询功能](http://ip.chinaz.com/)
3. 我最中意的方法， `nc ns1.dnspod.net 6666`, DNSPod Rocks!

使用第三种方式，就可以维持原有基础设置不变，而又能够提供DDNS记录的更新啦，哈，基本搞定！

# The Final Picture

在整个pipeline中，几个关键设备或者角色是需要我们着重关注的：

1. 路由器+NAT, 保证可以让外部访问者可以访问到内部的服务；
2. DNS和DDNS， 保证以统一的命名来访问同一服务， 不管服务器的IP如何变化
3. 本地的daemon程序， 这个是无论动态IP如何变化，我们都能对外提供服务的基础关键组件，虽然简单，但不可或缺。



# References

其实参考了很多资料，也咨询了些人(比如李晓波同学)， 但过去了这么长时间，才想起这个东西该落实到文字记录一下，所以，之前参考了哪些资料不可考了，这里就不罗列了，不过，要是想简单了解一下NAT和路由器，可以参考这篇[What Is A NAT Router?](http://networking.nitecruzr.net/2005/05/what-is-nat-router.html)

# 相关项目

1. [xip.io](http://xip.io/)
2. [ngrok](https://ngrok.com/)








