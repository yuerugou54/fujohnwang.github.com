% 关于SEO友好的中文URL编码解码的调研
% 千任

总的来讲， 现在百度和google都支持使用utf8编码的URL，所以，大的层面来看，在网站中使用中文URL，从SEO层面来看是OK的，也会有好的效果，这里只是详细调研一下落实过程中的一些小的容易坑人的细节。

# Wrong Way

使用java.net.URLEncoder对__整体__URL进行编码和解码，实验代码：


```scala
val encodedUrl = java.net.URLEncoder.encode("http://afoo.me/抗震设计.html", "utf-8")
println(s"encoded url = $encodedUrl")

val decodedUrl = java.net.URLDecoder.decode(encodedUrl, "utf-8")
println(s"decodedUrl=$decodedUrl")
```

运行结果是：
<pre>
encoded url = http%3A%2F%2Fafoo.me%2F%E6%8A%97%E9%9C%87%E8%AE%BE%E8%AE%A1.html
decodedUrl=http://afoo.me/抗震设计.html
</pre>

初看起来没问题， 但是，编码结果实际上是错误的，正确的编码结果是：
<pre>
http://afoo.me/抗震设计.html => http://afoo.me/%E6%8A%97%E9%9C%87%E8%AE%BE%E8%AE%A1.html
</pre>

java.net.URLEncoder并没有遵循rfc1738中的规定， 将URL中不同部分不该编码的字符也进行了编码。

# 正确做法
实际上，我们应该将URL分区域(part)进行编码和解码！

但是，陷阱依然存在！

```scala
println(java.net.URLEncoder.encode("空格 plus+", "utf-8"))
```

我们得到的是：<pre>
%E7%A9%BA%E6%A0%BC+plus%2B
</pre>

但实际上， 空格和加号在path部分和在query部分的编码方式是不同的！

引用参考资料连接中的一句话：
<blockquote>
For HTTP URLs, a space in a path fragment part has to be encoded to "%20" (not, absolutely not "+"), while the "+" character in the path fragment part can be left unencoded.

Now in the query part, spaces may be encoded to either "+" (for backwards compatibility: do not try to search for it in the URI standard) or "%20" while the "+" character (as a result of this ambiguity) has to be escaped to "%2B".
</blockquote>

根据java.net.URLEncoder的javadoc，这个类只能用来编码URL中query部分。

本来想自己写了，google了下，发现可以用URI类， 小实验了一把：

```scala
val uri = new URI("http", "afoo.me", "/抗震 +设计.html", null, null)
val encodeduri = uri.toASCIIString
println(encodeduri)  // what we want
println(uri.toString)                // just for display,not for our use
println(URI.create(encodeduri).getRawPath) // raw, not decoded
println(URI.create(encodeduri).getPath) // decoded
```

结果：
<pre>
http://afoo.me/%E6%8A%97%E9%9C%87%20+%E8%AE%BE%E8%AE%A1.html
http://afoo.me/抗震%20+设计.html
/%E6%8A%97%E9%9C%87%20+%E8%AE%BE%E8%AE%A1.html
/抗震 +设计.html
</pre>

好吧，简单看来，对于我们只关注path部分能够编码解码正确的需求来讲，看来是可以用的。

# References
1. [SEO优化中的中文URL处理优化](http://youhua.tui18.com/201302/2636881.html)
2. [What every web developer must know about URL encoding](http://blog.lunatech.com/2009/02/03/what-every-web-developer-must-know-about-url-encoding)
3. [Url Encode/Decode online](http://www.url-encode-decode.com/)
4. [UrlRewriteFilter](http://tuckey.org/urlrewrite/)
	- A Java Web Filter for any compliant web application servers (such as Tomcat, JBoss, Jetty or Resin), which allows you to rewrite URLs before they get to your code. It is a very powerful tool just like Apache's mod_rewrite.
5. <https://github.com/dispatch/reboot/issues/23>
6. <http://stackoverflow.com/questions/4571346/how-to-encode-url-to-avoid-special-characters-in-java>


