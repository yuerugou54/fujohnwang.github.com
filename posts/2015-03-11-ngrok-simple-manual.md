% ngrok简明手册
% 王福强
% 2015-03-11

使用ngrok， 可以让我们本机的各种服务通过公网对外服务，即使我们本机在NAT后面只是持有一个内网IP， 小工具， 大作用...


# 暴露本地HTTP服务

<pre class=".pre-scrollable">
$ ngrok 80
</pre>

输出：

<pre class=".pre-scrollable">
ngrok

Tunnel Status                 online
Version                       1.3/1.3
Forwarding                    http://3a4bfceb.ngrok.com -> 127.0.0.1:80
Forwarding                    https://3a4bfceb.ngrok.com -> 127.0.0.1:80
Web Interface                 http://127.0.0.1:4040
# Conn                        0
Avg Conn Time                 0.00ms
</pre>

现在，任何人都可以通过`http://3a4bfceb.ngrok.com`(根据情况变化)访问我们的http服务了。

# 暴露需要授权访问的HTTP服务

<pre class=".pre-scrollable">
$ ngrok -httpauth="helmet:12345" 80
</pre>

用户要访问， 需要提交与我们启动ngrok的时候指定的用户名和密码相匹配的信息。

# 自己指定子域名

默认情况下， ngrok会随机分配一个子域名给我们，比如`3a4bfceb.ngrok.com`, 但我们也可以指定自己想要的子域名， 比如：`afoo.ngrok.com`， 为了达到这个目的，我们可以：

<pre class=".pre-scrollable">
ngrok -subdomain=afoo 80
</pre>

# 使用自己的域名

付费服务， 简单来讲就是让你的域名指向ngrok.com， 然后在ngrok.com上配置相应地信息，告诉它如果有域名访问是这个的，给我路由到某个ngrok暴露的服务上去。

在这种情况下， ngrok暴露本地服务的时候要使用`hostname`选项：

<pre class=".pre-scrollable">
$ ngrok -hostname dev.example.com 80
</pre>

有了这个功能，其实你就不用动态DNS了，两者可以达到类似的目的。

# 参考文档

<https://ngrok.com/docs>



