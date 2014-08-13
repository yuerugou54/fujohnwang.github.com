% "senseidb VS. Solr VS. elasticsearch (***Incomplete***)"
% fujohnwang
% 2012-02-27

比较的时候，主要关注以下几个方面：

1. Clustering
	- Scalability on Storage and Service
	- High Availability Considerations
2. Features
3. Flexibility


# 1. [Solr](http://lucene.apache.org/solr/)

很显然， Solr跟Lucene是一家，所以，对Lucene做了很多扩展，与lucene的集成也比较好，而且，业界貌似求稳的都会选择Solr来构建他们的搜索体系。

但SolrCloud才是最终的理想解决方案，而SolrCloud还没有production-ready。

下面是Solr相关的架构图：
![image](http://people.apache.org/~sgoeschl/presentations/solr/pix/solr-overview.png)
![solr architecture](http://www.ibm.com/developerworks/java/library/j-solr-update/search-architecture.gif)


## Features
Solr的首页上对自己的特性罗列阐述的很详细了，这里不再赘述。

## Pros & Cons
- Pros
	- 成熟且验证过的方案
	- 文档资料丰富
	- 社区活跃
	- plugin extension points
- Cons
	- 貌似体系比较庞杂， replication的架构扩展有稍许问题？！

## References
1. [New SolrCloud Design](http://wiki.apache.org/solr/NewSolrCloudDesign)
2. [Scaling Lucene and Solr](http://www.lucidimagination.com/content/scaling-lucene-and-solr)
3. [Turbocharging Solr Index Replication with BitTorrent](http://codeascraft.etsy.com/2012/01/23/solr-bittorrent-index-replication/)
	- funny and sparkling idea by introducing BitTorrent replication mechanism *****
4. [Distributed Searching](http://wiki.apache.org/solr/DistributedSearch)
5. [Carrot2-OSS framework for building search clustering engine](http://project.carrot2.org/)
	- Solr search results clustering is based on the Carrot2 real-time document clustering engine.
6. [Clustering Component](http://wiki.apache.org/solr/ClusteringComponent)
	- 结果集的分类
7. [New SolrCloud Design](http://wiki.apache.org/solr/NewSolrCloudDesign)
8. [SolrCloud](http://wiki.apache.org/solr/SolrCloud)
9. [UniqueKey](http://wiki.apache.org/solr/UniqueKey)
10. [Solr Near Realtime Search](http://wiki.apache.org/solr/NearRealtimeSearch)
	- will be added in Solr4, currently available in trunk
11. [Scaling Solr Indexing with SolrCloud, Hadoop and Behemoth](http://java.dzone.com/articles/scaling-solr-indexing)

---

# 2. [Senseidb](http://senseidb.com/overview.html)
![architecture of sensei](http://linkedin.github.com/sensei//images/sensei-architect.png)

## Features
1. 主要解决高速索引更新的问题;
	- 底层是zoie的“**2-swapping-in-memory-index + 1-on-disk-index**”索引结构支持
2. 需要定义schema;
3. 通过Gateway可以接入多种数据源;
4. 通过BQL或者REST API，甚至各种语言bindings进行数据查询；
5. 支持通过hadoop MR job批量更新数据索引； 

## Pros & Cons
- Pros
	- 高速索引更新
	- 多数据源接入
	- 灵活的访问接口
	- 与hadoop生态的集成
	- 优秀的分布式扩展能力
- Cons
	- static schema
	- application side versioning maitaining


## 为何没有直接用Solr？
摘录在John Wang的访谈片段：
	
	Sensei leverages Lucene.

	We weren’t able to leverage Solr because of the following requirements:

		* High update requirement, 10’s of thousands updates per second in to the system
		* Real distributed solution, current Solr’s distributed story has a SPOF at the master, and Solr Cloud is not yet completed.
		* Complex faceting support. Not just your standard terms based faceting. We needed to facet on social graph, dynamic time ranges and many other interesting faceting scenarios. Faceting behavior also needs to be highly customizable, which is not available via Solr.



##References
1. [Introducing SenseiDB 1.0: an open-source, distributed, realtime, semi-structured database](http://engineering.linkedin.com/open-source/introducing-senseidb-10-open-source-distributed-realtime-semi-structured-database)
2. [Sensei: distributed, realtime, semi-structured database](http://blog.sematext.com/2012/01/26/sensei-distributed-realtime-semi-structured-database/)



------

# 3. [elasticsearch](http://www.elasticsearch.org/)

很新，当前0.19RC3版本， 文档缺乏
![image](http://log.medcl.net/wp-content/uploads/2011/08/es-architecture.png)

不过， ES确实有很多值得喝彩的地方。


## Features

1. Schema-Free | Schemaless
2. feed index engine with JSON formatted documents
3. Query by Lucene based query string or JSON based query DSL over HTTP or Native Java;
4. shards and replicas, LB and routings 
5. cloud integration
6. multiple search types
7. multiple data sources integration with River
8. many more...

## Pros & Cons

- Pros
	- 许多灵活， 优秀的特性（见features列表）
	- 作者拥有多年在搜索领域的涉猎经验
	- senseidb的pros它也基本都有
- Cons
	- 文档不足
	- 后端没有大的商业机构支持

## References

1. [quick intro to elastic search](http://www.slideshare.net/medcl/elastic-search-quick-intro?from=ss_embed)
2. [Flume, Hive and realtime indexing via ElasticSearch](https://blog.mozilla.com/data/2010/12/30/flume-hive-and-realtime-indexing-via-elasticsearch-2/)
3. [The Future of Compass & ElasticSearch](http://www.kimchy.org/the_future_of_compass/)
4. [Elastic Search: Distributed, Lucene-based Search Engine](http://blog.sematext.com/2010/05/03/elastic-search-distributed-lucene/)
5. [ElasticSearch at berlinbuzzwords 2010](http://www.slideshare.net/elasticsearch/elasticsearch-at-berlinbuzzwords-2010)
6. [Elastic Search Vs. Apache Solr](http://www.slideshare.net/macrochen/elastic-search-apachesolr-10881377?from=ss_embed)
	- 这篇貌似倾向于ES比较多一些
7. [Your Data, Your Search](http://www.elasticsearch.org/blog/2010/02/12/yourdatayoursearch.html)
8. [Search Engine Time Machine](http://www.elasticsearch.org/blog/2010/02/16/searchengine_time_machine.html)
	- transient状态与持久化状态的结合， write behind策略
9. [NoSQL, Yes Search](http://www.elasticsearch.org/blog/2010/02/25/nosql_yessearch.html)
	- 多种数据源类型的平滑接入
10. [Geo Location and Search](http://www.elasticsearch.org/blog/2010/08/16/geo_location_and_search.html)
	- 基于geo进行排序的特性很新颖
11. [Zero Conf Persistency](http://www.elasticsearch.org/blog/2010/09/27/zero_conf_persistency.html)
	- Local Gateway (Local Storage | Local FileSystem)
12. [The River](http://www.elasticsearch.org/blog/2010/09/28/the_river.html)
	- ES里River的概念跟Senseidb里Gateway的概念相近， 是数据源通道的意思，可以根据不同的数据源给出不同的River实现，比如基于MysqlBinlog的River， 基于Hbase的River，或者[RabbitMQ River](http://www.elasticsearch.org/blog/2010/09/28/the_river_rabbitmq.html)，[CouchDB River](http://www.elasticsearch.org/blog/2010/09/28/the_river_searchable_couchdb.html) etc.
13. [Percolator](http://www.elasticsearch.org/blog/2011/02/08/percolator.html)
	- 这个Percolator是ES里的概念，不要跟Google的Percolator混淆
14. [Versioning](http://www.elasticsearch.org/blog/2011/02/08/versioning.html)
	- Optimistic Concurrency Control
15. [New Search Types](http://www.elasticsearch.org/blog/2011/03/24/new-search-types.html)
	- Introduce **count** and **scan** search types, the latter can be used to scroll large result set 
16. [Data Visualization with ElasticSearch and](http://www.elasticsearch.org/blog/2011/05/13/data-visualization-with-elasticsearch-and-protovis.html) [**Protovis**](http://vis.stanford.edu/protovis/)
17. [Distributed Diagram](http://www.elasticsearch.org/videos/2010/02/08/es-distributed-diagram.html) (Video)
18. [Road to a Distributed Search Engine](http://www.elasticsearch.org/videos/2011/08/09/road-to-a-distributed-searchengine-berlinbuzzwords.html) (Video)*****

---
# 4. Conclusion
1. All are based on Lucene. 
2. All are distributed. 
	- senseidb shards with multi-write?!
	- solr shards with master-slaves and slave pull strategy;
	- elasticsearch shards with primary-secondary push strategy;
3. All do partitioning in document granularity, All require some unique key for each document(optional for some situations);
4. Sensei is good at real-time index update; Solr is good at stable and wide adoption; Elasticsearch is good at flexible and good ideas;




# 其它参考文献
1. [Lily架构简介](http://www.spnguru.com/2011/02/lily%E6%9E%B6%E6%9E%84%E7%AE%80%E4%BB%8B/)
	- 在自己的lily node里实现了multiwrite + wal+ message queue的数据分发， 没有充分利用现有系统中各个组件/系统的能力(虽然是基于hbase的table实现的)， 部分上来讲把事情搞复杂了。


