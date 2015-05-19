% BuildingMicroservices读书笔记与感悟
% 王福强
% 2015-05-13

loose coupling(interexchange) and high cohesion(autonomy).

bounded context  -> atomic unit boundary -> compositional boundaries

align service/functionality, person, team, organization, even bigger entities...

> trade short-term gain for long-term pain

> Build if it's **unique** to what you do.



# Orchestration VS. Choreography

(终于tmd理解了这两个鸟单词在软件行业里的确切意思了...)

With orchestration, we rely on a central brain to guide and drive the process, much like the conductor in an orchestra. (the coordination way)

With choreography, we inform each part of the system of its job, and let it work out the details, like dancers all finding their way and reacting to others around them in a ballet. (the fire-and-forget way)

类似于计划经济 VS. 市场经济，或者中央集权 VS. 地方自治，the former can be implemented in synchronous RPCs way, and the latter in asynchronous event-driven way.

# Versioning

~~个人看法是， 在URI上不要明确使用version作为URI的一部分， 而采用通用的资源标志URI， 但允许通过参数来明确version， 如果不指定，则默认访问已经部署的最高版本的服务或者资源。~~

~~这相当于COC的思想体现。~~

这样做（指采用默认访问latest版本服务的方式）其实存在问题， 如果客户端都采用默认不加版本的访问方式，一旦服务端升级造成不兼容， 那么客户端很大面积上会受影响。原则上，服务发布不应该影响面这么大！

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







# 单词与词组

1. inhibit,  [ɪn'hɪbɪt] , v., 抑制；阻止；使不能
    - e.g. Distributed Transaction cand inhibit scaling.
    - 区别于inhabit, v.居住于；占据；栖息
2. 









