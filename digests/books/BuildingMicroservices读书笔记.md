% BuildingMicroservices读书笔记与感悟
% 王福强
% 2015-05-13

loose coupling(interexchange) and high cohesion(autonomy).

bounded context  -> atomic unit boundary -> compositional boundaries

> trade short-term gain for long-term pain

> Build if it's **unique** to what you do.


# Orchestration VS. Choreography

(终于tmd理解了这两个鸟单词在软件行业里的确切意思了...)

With orchestration, we rely on a central brain to guide and drive the process, much like the conductor in an orchestra.

With choreography, we inform each part of the system of its job, and let it work out the details, like dancers all finding their way and reacting to others around them in a ballet.

like 计划经济 VS. 市场经济， the former can be implemented in synchronous RPCs way, and the latter in asynchronous event-driven way.

# Versioning

~~个人看法是， 在URI上不要明确使用version作为URI的一部分， 而采用通用的资源标志URI， 但允许通过参数来明确version， 如果不指定，则默认访问已经部署的最高版本的服务或者资源。~~

~~这相当于COC的思想体现。~~

这样做（指采用默认访问latest版本服务的方式）其实存在问题， 如果客户端都采用默认不加版本的访问方式，一旦服务端升级造成不兼容， 那么客户端很大面积上会受影响。原则上，服务发布不应该影响面这么大！

所以， URI中明确version可能是更合适的方式， 由客户端或者服务访问端自己决定是否升级。

遵循一般Major.Minor.Patch的版本语意， 通常情况下， Minor版本以下的发布，对客户端来说应该是没影响的。 Major版本的发布，则可能牵扯同时存在多个版本服务实例/集群的情况。

个人认为的基本原则是： **服务不管如何升级改造， 服务的访问者有权利决定是否升级，即是否使用新版本的服务**。












