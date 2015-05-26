% BuildingMicroservices读书笔记与感悟
% 王福强
% 2015-05-13

loose coupling(interexchange) and high cohesion(autonomy), the most simple but long-lived rule for software development stand!!!

align service/functionality, person, team, organization, even bigger entities...


~~~
bounded context  -> atomic unit boundary -> compositional boundaries
~~~


> == Words That Make Sense In This Book ==
> 
> * trade short-term gain for long-term pain
> * Build if it's **unique** to what you do.
> * we can make a change to a single service and deploy it independently of the rest.






# Orchestration VS. Choreography

(终于tmd理解了这两个鸟单词在软件行业里的确切意思了...)

With orchestration, we rely on a central brain to guide and drive the process, much like the conductor in an orchestra. (the coordination way)

With choreography, we inform each part of the system of its job, and let it work out the details, like dancers all finding their way and reacting to others around them in a ballet. (the fire-and-forget way)

类似于计划经济 VS. 市场经济，或者中央集权 VS. 地方自治，the former can be implemented in synchronous RPCs way, and the latter in asynchronous event-driven way.

# Versioning

刚开始个人看法是， 在URI上不要明确使用version作为URI的一部分， 而采用通用的资源标志URI， 但允许通过参数来明确version， 如果不指定，则默认访问已经部署的最高版本的服务或者资源。这相当于COC的思想体现。

但后来想想，这样做（指采用默认访问latest版本服务的方式）其实存在问题， 如果客户端都采用默认不加版本的访问方式，一旦服务端升级造成不兼容， 那么客户端很大面积上会受影响。原则上，服务发布不应该影响面这么大！

所以， URI中明确version可能是更合适的方式， 由客户端或者服务访问端自己决定是否升级。

遵循一般Major.Minor.Patch的版本语意， 通常情况下， Minor版本以下的发布，对客户端来说应该是没影响的。 Major版本的发布，则可能牵扯同时存在多个版本服务实例/集群的情况。

基本原则应该是： **服务不管如何升级改造， 服务的访问者有权利决定是否升级，即是否使用新版本的服务； 但服务提供者有通知的义务。**


# 数据库

最大的共享状态， 也往往是scalability最大的障碍。 

本质上，如果根据实体边界将各个实体划分清晰， 状态也应该是屏蔽在实体内部， 这里唯一比较困难的一点就是， 数据库表之间的外键关系。 要达到按照实体划分清楚边界的目的， 需要打破外键（Breaking Foreign Key Relationships），由应用来管理schema之间的关系，这里的schema其实也就变成了domain实体之间的关系了。实体与实体之间（或者说服务与服务之间），各自状态内聚不外露(状态封装)， 只通过消息传递的方式进行交互。

## Transactional Boundary

并非所有服务都是职能单一的，很多时候多种服务是要协同工作的，这个时候往往就需要管理事务性的服务执行，通常情况下，会再设置独立的组合性质的服务来管理跨多个服务的事务。

单数据库结点即可满足需求的服务， 事务完全可以交给数据库来管理； 一旦跳出这个能力边界， 比如跨库或者跨服务， 就需要通过其它方式来管理事务。 

常见的手段有：

1. 数据补偿与订正；(the compensating/retry way)
2. 2PC或者相似思想的分布式事务管理方式; (the orchestration way)

等等。

## 报表与数据仓库

如果进行服务切分，且状态随服务而行，那么，原来的基于数据同步和数据仓库的思路就会受到冲击， 相当于前端业务开发理解并研发一套系统， 后端BI人员再理解一遍，并以他们认为的形式再研发一套系统， 实际上，就后端系统的服务压力来说， 直接由前端服务统一接管并服务之应该也未尝不可，应该不像之前预想的那么恐怖吧？！

报表也好， BI其它需求也罢， 本质上是根据需求聚合并分析数据(the aggregated view of data/state)， 直接捅数据库当然最直接，但通过统一封装的服务来做这种事情也未尝不可， latency？ 应该不是核心问题吧！

另外一种思路是， 服务状态可以开放订阅，需要状态事件变更的下游系统可以直接订阅数据，汇总到自己的存储，再借助存储的支持做分析。

本质上， 还是Orchestration和Choreography两种思路的体现， 非互斥关系， 完全可以根据具体场景组合使用。


# Deployment Artifacts

制作self-contained deployable microservices as artifacts

## Platform-Specific Artifacts
aka. technology-specific artifacts, like gem, jar, npm, etc..

## Operating System Artifacts

> One way to avoid the problems associated with technology-specific artifacts is to create artifacts that are native to the underlying operating system.

say, rpm for redhat/centos, deb for debian/ubuntu, msi for windows

减少支持的操作系统类型的数量以避免无必要的资源投入。

制作自定义的Image景象，减少不必要的provisioning过程和重复（One approach to reducing this spin-up time is to create a virtual machine image that bakes in some of the common dependencies we use）。

在组织层次不足以支撑和cover新技术栈的情况下，不建议使用docker之类的容器Image技术，虽然属于发布的artifact的形式之一，但牵扯到周边系统的工作也会需要变更，包括人员，工作流程等。 "先进的，往往也是落后的"！

# Service-to-Host Mapping

单节点部署多服务的情况，主要需要考虑资源隔离的问题。

单节点部署单一服务的情况，主要需要解决资源利用率的问题。

关注那句老话：`threads don't scale, processes do.`

## Virtualization

> * Type 1 virtualization refers to technology where the VMs run directly on hardware, not on top of another operating system.
> * Type 2 virtualization, AWS, VMWare, VSphere, Xen, and KVM...

> if you don’t trust the code you are running, don’t expect that you can run it in a container and be safe. If you need that sort of isolation, you’ll need to consider using virtual machines instead.


"After many years of working in this space, I am convinced that the most sensible way to trigger any deployment is via a single, parameterizable command-line call.", 实际上， 命令行只是一种表现形式， 你当然可以把核心功能用shell脚本来写，但核心功能只要集中管理之后， 命令行也好， 操作页面也罢，都只是核心功能的一种延伸形式罢了。

# Test

as long as you understand that test design and performing help you to design and implement your services/systems better, you will find the way to do the test properly.

So, I skip this section.

# Monitoring

我坚持"No News Is The Good News"原则！ 

1. Alert是后继流程的起点，and **Identity** Matters here! 
    - Including `Correlation ID`s
    - artifactId is not enough, mix everything about environment together like host, IDC, etc.
2. Interconnections smooth the process!  
3. Convention and Consistency saves your ass too!
4. 跳出单机思维， 构建平台生态圈！ 
    - > monitor the small things, and use aggregation to see the bigger picture. 
    - > away from systems specialized to do just one thing, and toward generic event processing systems that allow you to look at your system in a more holistic way.
    - logs(kibana), system metrics(dashboard which can help drill things down, like [Graphite](http://graphite.wikidot.com/) or zabbix)， application metrics(JMX, [dropwizard/metrics](https://github.com/dropwizard/metrics))

实行‘计划经济’， 去他妈的‘市场经济’，这里根本不需要后者！

## 监控的内与外

大部分情况下我们会关注身体内部的情况(各种agents，各种logs, 各种metrics，诸如此类)，但实际上，我们最好同时也对别人怎么看我们有一个起码基础的认识， 比如使用synthetic monitoring之类的手段来模拟用户访问，看看我们的提供是否正常。

# Security

## Authentication & Authorization

跟哥整合挖财内网帐号体系和系统的思路相似， 偶的基本思路就是， “确定Identity， 让遵循Identity规范的系统在基本前提下根据各自情况flurish(especially, fine-grained authorization)”

> tradeoffs between Shiro and Spring Security with authorization framework.


# System Design 

> * Any organization that designs a system (defined more broadly here than just information systems)will inevitably produce a design whose structure is a copy of the organization’s communicationstructure.

> * Adapting to Communication Pathways

组织结构也要“高内聚，低耦合”。 

> Service Ownership

我们实行PO制度，也是同样的道理, 职责不清，纠缠扯皮的事儿就多。




# 工具参考
1. [fpm - Effing Package Management](https://github.com/jordansissel/fpm/wiki)
2. [Packer is a tool for creating identical machine images for multiple platforms from a single source configuration.](https://www.packer.io/)




# 单词与词组学习/复习

1. inhibit,  [ɪn'hɪbɪt] , v., 抑制；阻止；使不能
    - e.g. Distributed Transaction cand inhibit scaling.
    - 区别于inhabit, v.居住于；占据；栖息
    - 可以引申联想到prohibitive
2. perilous, ['perələs], adj.危险的；冒险的
    - The snow and the blackout combined to make motoring perilous - 大雪加上灯火管制使得车辆行驶成了危险的事。
3. lull, [lʌl], v.使安静；使入睡；哄骗；平息, n.暂停；间歇
    - The mother lulled the baby to sleep.
    - They lulled me into a false sense of security.

# 相关文章

1. [Introduction to Microservices](http://nginx.com/blog/introduction-to-microservices/)






