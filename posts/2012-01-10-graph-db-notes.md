% "Notes Of GraphDBs"
% fujohnwang
% 2012-01-10
# Basic Concepts

图存储 + 图运算 引申出一套生态系统， GraphDB属于存储序列， 图计算则以[google的pregel](http://www.slideshare.net/shatteredNirvana/pregel-a-system-for-largescale-graph-processing?from=ss_embed)为典型代表（使用BSP计算模型），还包括了[Hama](http://incubator.apache.org/hama/)，[Giraph](http://incubator.apache.org/giraph/)等图计算框架。本Note主要侧重GraphDB相关内容， 包括基本概念， 基本操作和访问， 存储结构等等。

A graph is composed of Nodes(Vertices) [with Properties] and Edges [with Labels]. 
![graph sample picture from wikipedia](http://upload.wikimedia.org/wikipedia/en/3/3a/GraphDatabase_PropertyGraph.png)

## Graph Types

1. directed graph（有向图） -> flow network 
	![](http://techportal.ibuildings.com/wp-content/uploads/2009/09/graph2.png)
2. undirected graph 

	![](http://techportal.ibuildings.com/wp-content/uploads/2009/09/graph1.png)
3. Mixed Graph(单向，双向， 无向边混合)
4. Multigraph (多边)
5. Weighted Graph(加权)
6. Property Graph
	- 对于Property Graph类型， 每个node和edge都可以有多组k-v形式的properties来附加更多信息， 另外， 每个edge可以有label， 用来表示不同类型的edge，这应该通常多见于multigraph中，即两个node之间存在多个edge的情形，而多个edge可以是不同类型的edge，比如like， follows等。 label和properties对于edge来说是不同的属性， 不要混淆。



## Basic Operations
The basic operations provided by a graph data structure G usually include:

* __adjacent(G, x, y)__: tests whether there is an edge from node x to node y.
* __neighbors(G, x)__: lists all nodes y such that there is an edge from x to y.
* __add(G, x, y)__: adds to G the edge from x to y, if it is not there.
* __delete(G, x, y)__: removes the edge from x to y, if it is there.
* __get_node_value(G, x)__: returns the value associated with the node x.
* __set_node_value(G, x, a)__: sets the value associated with the node x to a.

Structures that associate values to the edges usually also provide:

* __get_edge_value(G, x, y)__: returns the value associated to the edge (x,y).
* __set_edge_value(G, x, y, v)__: sets the value associated to the edge (x,y) to v.

## Graph-theoretic data structures
1. List structures
	- Incidence list
	- Adjacency list
2. Matrix structures
	- Incidence matrix
	- Adjacency matrix
	- Laplacian matrix or "Kirchhoff matrix" or "Admittance matrix" 
	- Distance matrix

![邻接矩阵OR邻接表](http://www.parallellabs.com/wp-content/uploads/2011/12/%E7%A8%80%E7%96%8F%E5%9B%BE%E5%92%8C%E7%A8%A0%E5%AF%86%E5%9B%BE%E7%9A%84%E9%82%BB%E6%8E%A5%E8%A1%A8%E4%B8%8E%E9%82%BB%E6%8E%A5%E7%9F%A9%E9%98%B5%E5%BD%A2%E5%BC%8F.bmp)

## 图论应用场景
- Task planning
- Scheduling
- Process assignation
- Routing
- Logistics
- League planning
- Pattern Recognition
- Dependency analysis
- Impact analysis
- Network flow 
	– Traffic analysis and optimization 
	– Delivery optimization
- Optimization of tasks

更多参考<http://www.slideshare.net/purbon/graph-t-6162024?from=ss_embed>

# Typical Products 
## [Neo4J](http://neo4j.org/)
Graph DB, AGPL3 license(_Sucks, I think_), java made, ACID, HA, M-S Caching Shards, Domain-specific, embeddable, REST, etc.

Neo4j的授权协议包括商业性质的授权和开源协议的授权，但开源协议授权是基于AGPL， 意味着如果要用neo4j，那你的产品也要开源，这对许多公司的产品来说是不可接受的， 另外， 许多高级特性只在商业版中才有，比如HA， backup等，没有了这些高级特性， neo4j也就剩下玩玩的资本了。

所以， 不再对neo4j做更多了解和涉猎， 只罗列部分资料，如果有人感兴趣可以看下：

1. [An Introductionto Neo4j](http://www.slideshare.net/slideshow/embed_code/9600796#) Slide
2. [Neo4j and real world apps](http://www.slideshare.net/peterneubauer/gdm-2011-neo4j-and-real-world-apps?from=ss_embed)

## [OrientDB](http://www.orientechnologies.com/orient-db.htm) 
<http://www.orientechnologies.com/orient-graphdb.htm>

Document-Graph DB, Apache2 License, Java Made, ACID Support, HA via replication, embeddable, REST&SQL-like access.

OrientDB实际上不是一个纯的GraphDB， 它是构建在DocumentDB的模型之上。 除了GraphDB， OrientDB还在基础的DocumentDB基础模型之上构建了KV, OO等类型的DB。

__References Of OrientDB__:

1. [OrientDB for real & Web App development](http://www.slideshare.net/lvca/orientdb-nosqlday?from=ss_embed)
2. [OrientDB the database for the Web](http://www.snoopal.com/documents/xO8QatV3gPyTAkWNXO1X57/OrientDB-the-database-for-the-Web)
3. <http://code.google.com/p/orient/wiki/GraphDatabase>
4. <http://code.google.com/p/orient/wiki/GraphEdTutorial>


## [FlockDB](https://github.com/twitter/flockdb)
twitter特定场景下的graphed产品， 并非力图实现所有的features，而只关注twitter自身或者网站常见的一些问题， 比如在twitter内部只用flockdb存储用户关系图以及二级索引等信息。

flocked存储采用node+edge一起存储的策略，而且针对一个edge，会按照forward和backward分别存储2条， 这种策略内聚性不错，但是否耦合性太紧，不便于扩展那？！（__更深层次的edge关联不好管理，尤其是引入分布式存储结构之后！__）

flockdb底层使用mysql作为存储， 使用twitter的gizzard分布式数据访问层来管理mysql分区集群， 在这之上， 才是实现的重头戏， 成为flapps服务集群， flapps是stateless的， 所以可以很容易的横向扩展，而m底层的mysql存储层在gizzard的帮助下也可以横向扩展，所以总的来说， flockdb的横向扩展能力是没啥问题的。

![https://github.com/twitter/flockdb/raw/master/doc/flockdb-layout.png](https://github.com/twitter/flockdb/raw/master/doc/flockdb-layout.png)

从使用场景上来看， flockdb更适合浅层次的遍历和查询(少于2个层次？！)， 而深层次的遍历估计会因为同时牵扯index的查询和数据的查询而性能急剧下降。（个人猜测）从这个角度来看， flockdb还真是一个twitter特定的产品，跟一般意义上的graphdb有很大的差异，即traversal能力的强弱。

flockdb使用thrift暴露远程服务，所以， 客户端可以使用多种语言实现，包括ruby， java， c/c++等， twitter内部应该用的是gizzmo（ruby客户端）。

结论： flockdb虽然速度和横向扩展能力不错，但graph模型和处理能力太弱，不一定适合“来往”的业务模型。

__References__

1. [Introducing FlockDB](http://engineering.twitter.com/2010/05/introducing-flockdb.html)
2. [Google Group of FlockDB](http://groups.google.com/group/flockdb)
3. [Twitter’s Database FlockDB](http://www.infotales.com/twitters-database-flockdb/)
4. [FlockDB and Graph Databases](http://nosql.mypopescu.com/post/4423840717/flockdb-and-graph-databases)
5. [FlockDB - What is it? And best cases for it uses](http://stackoverflow.com/questions/2629372/flockdb-what-is-it-and-best-cases-for-it-uses)
6. [Flockdb: Twitter’s powerful weapon](http://newsicare.wordpress.com/2010/10/10/flockdb-twitters-powerful-weapon/)

## [DEX](http://www.sparsity-technologies.com/dex)

C++ core, Java, .Net API, dual Personal-evaluation or Commercial license

__Labeled, Directed And Attributed Multigraph Model__

### Scenarios for DEX
- Social Networks
	- Twitter, Facebook, Linkedin, Flickr, Delicious, MySpace
- Information Networks
	- Bibliographical databases, Wikipedia, IMDB
- Security Networks & fraud detection
	- Economic transactions analysis
- Recommendation
	- ecommerce
- Media Analysis
	- Audiovisual content recommendation
- Physical Netorks
	- Logistics, Transport, Electrical, Telecom Networks
- Biological Networks

__Curiosity__

1. __基于磁盘的高容量存储是怎么做的？！__
2. 存储结构是什么样儿的？自定义？
3. 横向扩展能力如何？

__References__

1. <http://www.sparsity-technologies.com/downloads/dex_brochure.pdf>
2. [DEX 3.0 features: Graph algorithms](http://sparsity-technologies.com/blog/?p=97)
3. [Dex: Introduction](http://www.slideshare.net/SparsityTechnologies/dex-introduction?from=ss_embed)
4. [DEX: Seminar Tutorial](http://www.slideshare.net/SparsityTechnologies/dex-seminar-tutorial?from=ss_embed)

## [Infogrid](http://infogrid.org/)
MeshNetg公司开源产品， MeshNet提供商业支持。产品整体按照不同的关注点分为不同的子项目， 包括核心的graphdb， 关注cluster的grid， 关注存储的stores以及展现和工具支持，可以使用不同的存储方案， 包括mysql， FS， HDFS等。

__References__

1. [InfoGrid Core Ideas](http://www.slideshare.net/infogrid/info-grid-core-ideas)
2. [A Taste Of InfoGrid](http://www.slideshare.net/infogrid/a-taste-of-infogrid-1688328)
3. [The InfoGrid Graph DataBase](http://www.slideshare.net/infogrid/the-infogrid-graph-database)


## Other Ones…
1. InfiniteGraph
	- [Gluecon InfiniteGraph Presentation: Scaling the Social Graph in the Cloud](http://www.slideshare.net/infinitegraph/gluecon-infinite-graph-public-presentation)
2. [HypergraphDB](http://www.kobrix.com/hgdb.jsp) 
	- LGPL, Java Made ,ACID, P2P distribution and replication, storage on BDB
	- [HyperGraphDB:Data Management for Complex Systems](http://strangeloop2010.com/talk/presentation_file/14445/Iordanov-HyperGraphDB.pdf)
3. [TinkerGraph](https://github.com/tinkerpop/blueprints/wiki/TinkerGraph) - In-Memory Graph, Not for production.

# Tools & Support
TinkerPop公司在这个领域显然做到工作很出色！

1. [Blueprints](https://github.com/tinkerpop/blueprints)
![rexster-system-arch](https://github.com/tinkerpop/rexster/raw/master/doc/images/rexster-system-arch.png)
2. [Gremlin](https://github.com/tinkerpop/gremlin)
	- [The Pathology of Graph Databases](http://www.slideshare.net/slidarko/the-pathology-of-graph-databases?from=ss_embed) - Gremlin Graph Language 
3. [reXster](http://rexster.tinkerpop.com/)
4. [Prefuse](http://prefuse.org/)  - visualization tool


# 关于GraphDB的一些个人想法
个人感觉，GraphDB的服务层可以独立于存储层，只要对存储层做合理的抽象， 现在的许多存储方案都可以使用，包括常见的RDBMS，以及各种KV啦，ColumnOrientedDB啦，等等。 但数据的组织，应该参考GraphDB的服务层设计， 尽量贴近服务层，规避特定存储层在GraphDB的访问模式下的各种弊端，比如“索引的空间和时间消耗”.  

GraphDB强调索引的Vertices Adjacency能力， 即通过Vertex可以直接获得临近的Vertices，而不用像在RDBMS那样， 去B-Tree等索引中先查看索引，再做join的方式来获取相关vertices， 换句话说， 当前vertex本身已经是临近的vertices的索引。 

当然， GraphDB并不排斥其它索引形式，比如完全可以通过B-Tree, B+Tree或者lucency等手段对vertices或者edge的properties进行索引或者对root vertices set进行索引，以加快graph其它相关信息的检索和访问。

## 存储结构抽象和引申
首先， graph中核心元素的存储可以分开， 比如分为：

1. node存储（适合使用索引的存储）
2. edge存储（组合索引？！）
3. properties存储（比较适合用KV存储）

分开的好处是， 不同元素的存储方案选择比较灵活， 另外分区扩展也很容易，但也有弊端，比如构建索引的空间开销，以及搜索期间跨越不同存储的时间开销等。为了规避弊端， 可以根据某些指标（比如子图）将核心元素的存储分区做locality， 不过，这又进一步会引入跨分区遍历的开销…

反过来， 几种核心元素一起存储又会怎样？！ 想自然语言的语句似的将所有信息都包含进一个单元（比如句子）当然可以，绝对高内聚，但使用的适合可能就不太方便， 要做多级索引才能提高查询和计算速度，而索引增多，随着查找层次的加深，又会进一步降低性能，所以完全的高内聚的一起存储所有信息显然不是啥太明智的做法。

SNS的关系网络原则上并非十分密集，所以，采用相邻列表(Adjacency list)的结构来存储应该比较合适。 而使用相邻列表(Adjacency list)的话， node以及其关联的edge就可以一同存储了。 在这种情况下，即使node和edge的properties单独存储，也是很容易切割和扩展的。



## 值得关注的点
1. 横向扩展能力
2. 高可用性
	- 备份
	- 复制
	- 服务降级
	- 其它
3. 有哪些现成的图算法可用
4. 运维复杂度

## 使用场景分析
### 适合的场景
pending
### 不适合的场景
pending

# References
1. <http://en.wikipedia.org/wiki/Graph_database>
2. <http://en.wikipedia.org/wiki/Graph_(data_structure)>
3. <http://en.wikipedia.org/wiki/Graph_theory>
4. **[http://www.graph-database.org/](http://www.graph-database.org/)**
5. [On Building a Stupidly Fast Graph Database](http://blog.directededge.com/2009/02/27/on-building-a-stupidly-fast-graph-database/)
6. [Social networks in the database: using a graph database](http://blog.neo4j.org/2009/09/social-networks-in-database-using-graph.html)
7. [Graphs in the database: SQL meets social networks](http://techportal.ibuildings.com/2009/09/07/graphs-in-the-database-sql-meets-social-networks/) *****
8. [A Toolbox for High-Performance Graph Computation](http://www.cs.ucsb.edu/~gilbert/talks/ParLab15sep2011.pdf)
9. [The Deﬁnition ofGraphDB](http://www.slideshare.net/slideshow/embed_code/9600743#) Slide
10. [Large Scale Graph Processing](http://www.slideshare.net/doryokujin/largescale-graph-processingintroduction?from=ss_embed) Slide
11. [Giraph:Large-scalegraphprocessinginfrastructureonHadoop](http://www.slideshare.net/slideshow/embed_code/8525113#)
12. [Graph database super star](http://www.slideshare.net/andres_taylor/graph-database-super-star-8079303?from=ss_embed)
13. [Key-Key-ValueStoresforEfficientlyProcessingGraphDataintheCloud](http://www.slideshare.net/slideshow/embed_code/7656845#)
14. [Online Query Execution on Large Distributed Graphs](http://www.slideshare.net/samehmi/gdm-2011-talk?from=ss_embed)
15. [AHigh-PerformanceGraphDatabaseManagementSystem](http://www.slideshare.net/slideshow/embed_code/7649477#)
16. [Graph Databases introduction to rug-b](http://www.slideshare.net/purbon/graph-databases-introduction-to-rugb?from=ss_embed)
17. [NoSQL with Graphs](http://www.slideshare.net/ClaudioMartella/presentation-7398682?from=ss_embed)
18. [Connections to the Real World:Graph Databases and Applications](http://www.slideshare.net/ahzf/1st-uimgdb-connections-to-the-real-world?from=ss_embed)
19. [Ranking on Large-Scale Graphs with Rich Metadata](http://research.microsoft.com/pubs/147063/Tutorial%20-%2020110328-final.pdf) 
20. [Graph Databases: The Web of Data Storage Engines](http://www.slideshare.net/purbon/graph-databases-the-web-of-data-storage-engines?from=ss_embed)
21. [Graph Theory and Databases](http://www.slideshare.net/purbon/graph-t-6162024?from=ss_embed)
22. [Pregel: A System for Large-Scale Graph Processing](http://www.slideshare.net/shatteredNirvana/pregel-a-system-for-largescale-graph-processing?from=ss_embed) - 归属于图计算范畴
23. [5 Graph Databases to Consider](http://www.readwriteweb.com/cloud/2011/04/5-graph-databases-to-consider.php)
24. [GraphML](http://en.wikipedia.org/wiki/GraphML)
25. [The Graph Traversal Programming Pattern](http://www.slideshare.net/slidarko/graph-windycitydb2010?src=related_normal&rel=5213756) **** 
26. [Cassovary](http://engineering.twitter.com/2012/03/cassovary-big-graph-processing-library.html) ***** twitter new open sourced graph-processing library written in Scala










