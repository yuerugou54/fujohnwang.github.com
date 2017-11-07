% 不提Serverless你就过时了？ Are You Kidding Me？
% 王福强 - fujohnwang AT gmail DOTA com
% 2017-11-06

Serverless跟微服务有啥必然联系吗？ 没有！

k8s 和docker跟微服务有啥必然联系吗？ 可以有，也可以没有！

devops跟微服务有什么关系吗？ 可以有，也可以没有，但没有可能比较难受！


Serverless是什么？ Serverless其实本质上就是过去的插件式开发， 换个名字你就当成个宝儿了？ java.util.ServiceLoader用过不？各个IDE的插件装过没？将原来独立进程管理的功能项给你搞个容器往里塞，其实玩得不就是另一个层面的IoC吗？ 抽取通用逻辑，为特定逻辑提供开发接口，仅此而已， Serverless个毛，还不是起个新名头装旧酒！ 

那么Serverless跟微服务有必然联系吗？ 没有！ 你要硬说Serverless的方式来开发微服务，那是你的事儿， 我最多建议在PaaS层玩玩Serverless。

docker也好，k8s也罢， 本质上是为了提高资源利用率， 没有他们，你要搞微服务还是可以搞，难道离了docker和k8s不用活啦？ 鸟~

所谓“花开两朵，各表一枝”，  微服务的生态，可以构建在多个枝头上，不是只有docker一个枝头一朵花。 所以， 对于微服务来说，k8s和docker， 可以有，也可以没有！

我没有那么正统的devops理论基础和深刻认识，我对devops就取两个点用：

1. 文化， 团队成员既要有owner意识，也要有“向前一步”的勇气；
2. 基础设施， CI/CD，流程管控，能用则用，不能用就往后放；

所以，你说devops跟微服务有没有关系， 没有，意味着你上微服务是在各项条件不成熟的情况下上的（为什么我不说）， 有， 那么devops起码在文化和基础设施层面会给你营造的微服务体系和生态提供更多的支持。 所以说， 可以有， 也可以没有， 但没有可能比较难受。

有些时候， 你可能知道很多概念和buzzwords， 只不过你可能还是说不出个丁是丁卯是卯， 还好，你有扶墙老师， 可以偶尔辨证施治， 😯，不，辨证施“知”一下，关注扶墙老师说，帮你解惑 ；）

![](https://mmbiz.qpic.cn/mmbiz_png/iaS2Xlrbu6AvzdFSegKhhvJLADkIY7h3CZUHAyu5AcxOC7lBd5FOqMjHt0M41zQZtiaba4CibF4WIdZ845KKy9f5w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

---



![](images/fqtec-logo-h.png)

**福强科技**（https://www.keevol.com）旨在为企业提供个性化的非标准化**技术和管理**咨询服务， 帮助客户解决实际问题和痛点，帮助客户组织和团队成长， 如果您正头疼组织和团队在技术和管理方面的问题，欢迎来函洽谈 , Email: <fq@keevol.com> 









