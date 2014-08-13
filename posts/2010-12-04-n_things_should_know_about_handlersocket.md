% N Things You should know about HandlerSocket
% fujohnwang
% 2010-12-04
__author: fujohnwang__

关于Mysql HandlerSocket Plugin你不得不知的几件事儿

# What? What's HandlerSocket?(啥?啥是HandlerSocket？)
As you may know, after Mysql5.1, plugin mechanism is introduced into the mysql server.With Plugins, we can extend or customize the functionalities or behaviors of Mysql server. While HandlerSocket is one custom plugin for Mysql, it enables you to access the underlying storage engines of Mysql server(currently, only InnoDB is supported), without any overhead on Sql parsing things, execution planning things, etc. As the author of HandlerSocket states, with HandlerSocket, you can achieve 750000 qps, sounds fantasitic ha? To find more details on HandlerSocket, refer to this original blog about HS. Of course, you can also refer to the official site of HS on the github where you can find almost anything and get you updated with the current development process of HS.

Oh, By the way, after you want to get started with HS, and you would like to read something useful on HS in Chinese, read this , hehe, one of my old fellow wrote it.

# Things You should know
OK, now, let's get to the topic today.

## Binlog is still available with Handlersocket
I am sorry I said binlog is not availabe when we use HandlerSocket to do data access, that's not true. In fact, HandlerSocket implements as a Handler in mysql, and that means a callback for binlog writing needs to be implemented, so HandlerSocket will still write binlogs in the process of data access. One word, HandlerSocket will write binlog in row-based format.
## What if I only want to update specific columns of a table instead of all?
When only want to update several columns, open an index with these several columns only. This is a trick, but it's necessary for you to know it.
## pst_id will be confined in each connection, it's mapped to prep_stat in HS codebase;
So as long as you keep the pst_id identical in one connection, the index will not be crashed. Most of the time, The client should take care of the pst_id/indexId generation and management
## QueryCache invalidation issue
Currently, HandlerSocket will not invalidate the query cache when updating the database, but the auther told me in the github forum that he would like to add this feature in the near futuer, maybe it will come soon; 

(__Update: the new version of HS has fixed this issure__)
## encoded string issue
When encoding the string as per the HS protocol, you have to convert a string to an array of bytes, but the protocol doesn't mention the conversion charset, as I have asked such a question in the forum of github, the author answered we should use the encoding of the target table.

As to the string encoding, another point I should mention, as the protocol states, the client should encoding column values before sending them to HS server, but since special bytes are kept(e.g. 0x00 as null, 0x09 as delimeter, etc.), so the protocol states that if the byte value is between 0x00 and 0x0f, special treat should be taken, that's, encoding these special bytes by prefixing 0x01 after bitwise 0x40. But as I know, most of the Java clients didn't pay attention to the encoding issue(Most of the time, I live in Java world, Although recently I starts to join Scala's one), so take care on this point when you try to pick up an Opensource Java client for HandlerSocket.

## Play around with HS via telnet
	HandlerSocket protocol is a small-sized text based protocol. Like memcached text protocol, you can use telnet to get rows through HandlerSocket.
	
So let's have some fun.

Firstly, let's create a table to play with:
<pre>
create table dw(
 id int(10) unsigned auto_increment not null, 
 value varchar(25), primary key(id)) engine=InnoDB;
 </pre>
 
Then, here is what we may do next:
<pre>
Example 1. 
fujohnwangs-MacBook-Pro:~ fujohnwang$ telnet 10.16.201.39 9999
Trying 10.16.201.39...
Connected to 10.16.201.39.
Escape character is '^]'.
P 0 test dw PRIMARY value   
0 1
0 + 2 1 darren
0 1
0 = 1 0 1 0 D
0 1 1
</pre>

Just for hint, the delimeter between value columns above is tab(0x09), not space, just as the protocol states. But in your first time, you may ignore this and cause unsuccessful interaction with HS.

## The Insert columns have nothing to do with the OpenIndex columns;
Let's say, we have a table which has 2 columns (id int unsigned PRIMARY KEY auth_increment, value VARCHAR(25)), since we define id as auto_increment, when insert records into this table, we may only care about the value column, so we may do:

<pre>
Example 2. 
P 0 test my_table PRIMARY value
// 0 1
0 + 1 stringvalue
// 1 1
</pre>

what? the error code is 1, not 0, it means there is something wrong. yes, Not like Update operations, the insert operation will ALWAYS insert columns from the 1st column to the last column of the table, even we only Open index with value column. In the above sample, the operation in fact try to insert stringvalue as int value which indeed will cause error.

<blockquote>
In fact, sometimes stringvalue will be converted to 0 instead of raising error. But the gotcha with insert is the key point here.
</blockquote>

## auto_increment indeed is the problem here with HS.
why? see item above. ^_^ Of course, most of the times, it's not a big deal as per HS's typical usage scenarios. In distributed environments, auth_increment is not prefered.

(__Update: auto_increment works now__)

## HandlerSocket doesn't support transaction
But the data changes are durable. Use HandlerSocket properly and visely in suitable scenarios.

## Why HandlerSocket is fast?
The original blog has explanation about this, here is just a conclusion:

1. much lower CPU usage (analysis with oprofile)
2. execute multiple requests in bulk on server side which cause low CPU/disk usage
3. custom effecient text-based communication protocol, at least more effecient than mysql's one

# Last Words On This
I don't think I can list everything here on HandlerSocket, since HS will evolve continuously, So stay tuned on HS and on this topic which I may update in the future.
