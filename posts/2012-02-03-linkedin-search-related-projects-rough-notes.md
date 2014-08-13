% "Linkedin Search-Related Projects Rough Notes"
% fujohnwang
% 2012-02-03
# solr

- References
	- <http://codeascraft.etsy.com/2012/01/23/solr-bittorrent-index-replication/>
	- [The New SolrCloud: Overview](http://blog.sematext.com/2012/02/01/solrcloud-distributed-realtime-search/)

# [bobo](http://sna-projects.com/bobo/)
- a Faceted Search implementation written purely in Java, an extension of Apache Lucene.
- <http://cdc.tencent.com/?p=1401>
- complement unstructured data set (in lucene) with semi- or full-structured data set, that's, we can search all of the kinds of data sets
	

# [zoie](http://javasoze.github.com/zoie/)
- a real-time search and indexing system built on Apache Lucene
- sub-system of sensidb or backbone?!
- data provider -> indexs(ram or disk based) -> index reader factory -> query clients
	
# [cleo](http://sna-projects.com/cleo/)
- real-time typeahead and autocomplete services
- front tier(cache, web) + aggregator(1..1) + searching services(1..*)

# [senseidb](http://sna-projects.com/sensei)
	data sources -> gateways -{indexing}-> sensi node(s) -{load balancing}-> broker(s)-> query clients
	
- <http://engineering.linkedin.com/open-source/introducing-senseidb-10-open-source-distributed-realtime-semi-structured-database>

# In conclusion
	zoie -{make it clustered}-> sensidb -{make it SOA}-> cleo 
