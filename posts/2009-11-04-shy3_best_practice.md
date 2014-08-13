% 关于钱磊在Shy3框架中使用的“动态URL”功能的小记
% fujohnwang
% 2009-11-04
__author: fujohnwang__

关于钱磊在Shy3框架中使用的“动态URL”功能的小记 

1. 数据状态存储和传递； 
    - stateless, easy horizontal scale 
    
2. 加密，解密； 
    - security， attack-proof. 
    
3. 压缩  192, 2k，> 2k 
    - 经验值， 小于192的时候， 压缩反而适得其反； 
    - 192 - 2k范围内压缩效果明显； 
    - 大于2k的范围， 写入secure cookie；

<pre>
------------------------以下摘自wikipedia-----------------------------

Query String Compatibility issues 

According to the HTTP specification: 

    Servers should be cautious about depending on URI (which includes URLs) lengths above 255 bytes, because some older client or proxy implementations may not properly support these lengths.[1] 

The HTML 3 specification declares that any attribute value (e.g. url in <a href="url">) cannot have more than 1024 characters[2] However, the HTML 4 specification omits this restriction.[3] 

he specification does not dictate a minimum or maximum URL length, but implementation varies by browser and version. For example, Internet Explorer does not support URLs that have more than 2083 characters.[4][5] There is no limit on the number of parameters in a URL; only the raw (as opposed to URL encoded) character length of the URL matters. Web servers may also impose limits on the length of the query string, depending on how the URL and query string is stored. 

The common workaround for these problems is to use POST instead of GET and store the parameters in the request body. The length limits on request bodies are typically much higher than those on URL length. For example, the limit on POST size, by default, is 2 MB on IIS 4.0 and 128 KB on IIS 5.0.[6] 
[edit]
</pre>
