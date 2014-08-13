% Head First The Big Picture Of DNS
% 王福强

学术不精，平日多有耳闻却又熟视无睹， 借资料大扫荡之机，重新梳理整个DNS体系的大图景，以期将来有据可查可参可考...

# DNS基本概念铺垫
对以下域名进行解剖：
<pre>
www.howstuffworks.com
</pre>

- com: 一级域名或者称为顶级域名(top-level domain/first-class domain)部分
- howstuffworks: 二级域名部分
- www: 主机(host)部分

# DNS生态体系中牵扯的机构及其职能
顶级域名/一级域名由IANA^[Internet Assigned Number Authority, it's operated by the Internet Corporation for Assigned Names and Numbers (ICANN)]统一进行管理和分配， 这些顶级域名/一级域名包括我们耳熟能详的：

1. .com
2. .net
3. .org
4. .edu
5. etc. etc.

"These top-level domains are controlled by the IANA in what's called the Root Zone Database"

二级域名归属于相应的一级页面下面， 二级域名由域名注册商(registrar)进行分配， 每个域名注册商可以获取一个或者多个一级域名下二级域名的注册权，比如registrar1只能负责.com下二级域名的注册，而registrar2则可以负责.net, .org多个一级域名下二级域名的注册。 每个域名注册商在用户注册/申请二级域名的时候，需要跟ICANN的InterNIC服务进行交互，该服务将保证同一顶级域名下的二级域名的唯一性， 注册成功的二级域名将入whois database.

Network Solutions, Inc. (NSI) was one of the first registrars, and today companies like GoDaddy.com offer domain registration in addition to many other Web site and domain management services.


# 典型流程和基础设施说明
当我们在浏览器中输入域名进行服务访问的时候，浏览器会调用相应的系统程序，比如gethostname或者getaddrinfo等，来进行DNS查询，以便找到域名对应的服务器的IP地址是多少，在拿到相应的IP地址之后再发送服务请求进行访问。

那到哪里去查询域名信息与IP地址的映射关系那？ 一般情况下， 我们可以在网络设置里选择手动添加DNS Server地址或者通过DHCP(Dynamic Host Configuration Protocol)自动从ISP获得相应的DNS Server地址。

<blockquote>
NOTE

在Linux或者Unix下， 使用的DNS Server地址可以在resolv.conf中找到，但resolv.conf的内容是自动生成的，一般认为更改无效，要手动变更自己想用的DNS Server是哪个，可以编辑/etc/hosts进行域名/主机名到IP地址的映射关系的添加。
</blockquote>

在知道了要查询的DNS Server是哪个之后， DNS请求就根据相应的协议发送给了DNS Server，DNS Server将首先检查本地缓存中是否存在当前查询的域名与其IP地址的对应关系，如果有则直接返回，如果没有，则查询Root DNS Server，因为每一个DNS Server都会知道至少一个Root DNS Server, Root DNS Server将根据查询域名的一级域名是什么，告诉发起查询的DNS Server哪些其它的DNS Server可能包含相应的二级域名与其IP地址的映射关系信息，之后当前发起查询的DNS Server再向这些DNS Server进行查询，如果在这些返回的DNS Server处查询到相应的映射信息，则缓存到本地然后返回，否则，报给客户端查找不到的错误信息^[我们这里只是简单描述了大体流程，实际流程中很多细节没有牵涉，包括结构，协议，算法等等]。


我们实际上可以通过nslookup命令(适用于linux或者unix系统)来检查某些域名相关信息，包括它对应的IP地址是什么:

<pre>
$ nslookup www.howstuffworks.com // 默认相当于-type=A,即A记录
$ nslookup -type=ns www.howstuffworks.com //Query the NS Record using -query=ns
$ nslookup -type=any www.howstuffworks.com // View available DNS records using -query=any
$ nslookup 209.132.183.181  OR $ host -a IP/DomainName // Reverse DNS lookup, 
$ nslookup redhat.com ns1.redhat.com // Using Specific DNS server(here, ns1.redhat.com)
$ nslookup -debug redhat.com  // Enabling debug mode using -debug
</pre>

我们可以通过type参数指定要查询的域名相关信息，见上例， 其中，如果不指定type则默认返回A记录(A Record)对应的信息， 简单来讲就是域名与IP地址的对应信息。关于nslookup使用的更多信息可以参考<http://www.thegeekstuff.com/2012/07/nslookup-examples/>

如果是在MacOS X系统下， 除了使用命令行形式的nslookup命令，也可以使用/Application/Utilities/Network Utility图形化工具来达成相同的目的。

## 域名注册商与DNS Server
如果我们在某个域名注册商处注册了一个域名， 一般情况下， 域名注册商会有相应的DNS服务让我们来指定注册成功后的域名应该绑定到哪个IP地址上， 即域名注册商会有相应的DNS Server对外提供服务，该DNS Server被认为是authoritive的，原则上，它是应该在Root DNS Server是有在册记录的^_^， 只有这样，当查询应用程序访问和查询我们注册的域名的时候， 才能通过Root DNS Server的查询结果告知发起查询的DNS Server到注册商的DNS Server上来查询到对应的结果。

域名注册商的DNS Server或许不错，不过，我们也可以不使用域名注册商提供的DNS Server， 比如DNSPod的口碑不错又是免费的，我们想使用DNSPod的服务，那么，我们就可以通过域名注册商的后台管理程序，将域名的解析权转给DNSPod的DNS Server来处理， 而在DNSPod的后台管理程序中设置相应的A记录啦，CNAME记录啦， 等等。 

<blockquote>
NOTE
 
DNS Server一般运行BIND软件对外提供服务。
</blockquote>

那A记录， CNAME之类到底有哪些，又都代表什么意思那？ 

## 域名的记录类型(record types of domain names)
1. Host(A Record) 
	- This is the basic mapping of IP address to host name, the essential component for any domain name.
2. Canonical Name (CNAME) 
	- This is an alias for your domain. Anyone accessing that alias will be automatically directed to the server indicated in the A record.
3. Mail Exchanger (MX)
	- This maps e-mail traffic to a specific server. It could indicate another host name or an IP address. For example, people who use Google for the e-mail for their domain will create an MX record that points to ghs.google.com.
4. Name Server (NS)
	- This contains the name server information for the zone. If you configure this, your server will let other DNS servers know that yours is the ultimate authority (SOA) for your domain when caching lookup information on your domain from other DNS servers around the world.
5. start of Authority (SOA)
	- This is one larger record at the beginning of every zone file with the primary name server for the zone and some other information. If your registrar or hosting company is running your DNS server, you won't need to manage this. If you're managing your own DNS, Microsoft's support information has a helpful article on the structure of a DNS SOA Record.

这里是来自DNSPod的通俗解释：
![dns_records_type_explained_by_dnspod](images/dns_records_type_explained_by_dnspod.png)

# 更多域名与IP的故事
一个域名对应一个IP， 这个就不需要多说了吧？ 在域名的A记录中指定对应的IP就行了。

## 一个域名对应多个IP

大型网站一个域名后面通常都会有多台服务器提供相应服务，而且往往会在DNS层面做负载均衡，这种情况下，需要设置一个域名对应到多个IP。

根据实现的不同，配置方式可以是不同的，比如在BIND中，是可以在一条A记录中指定一个对应的IP列表。 

## 多个域名对应一个IP

如果一台服务器上起了多个服务，而又想使用不同的域名来访问这些不同的服务，那么， 就会需要设置多个域名对应一个IP。

一般情况下，只要设置不同域名的A记录指向这台服务器的IP就可以了。

<blockquote>
一台服务器上的多个服务一般跑在不同的端口上， 在访问的时候，在域名后指定端口即可，不需要在DNS配置中指定。

当然，像在DNSPod中，也可以IP和端口一起指定， 参考https://support.dnspod.cn/Kb/showarticle/tsid/35/。
</blockquote>

## 多个域名对应多个IP

有了上面几个故事，这里还需要再细说嘛？

# Dynamic DNS

对于没有静态IP(static ip)的机器来说， 如果想一直让外部通过固定的域名访问，通常需要Dynamic DNS服务支持。

原则上与一般意义上的DNS没有太大差别，唯一多出来的一个装备是， 在拥有动态IP的本地机器上需要安装一个软件或者脚本 - dynamic DNS client，可以检测本地机器的IP变更，然后将变更通知DynamicDNS服务器去更新相应zone file中的A记录。

![how_dynamic_dns_works - 图片来自easyDNS](images/how_dynamic_dns_works.gif)  

Dynamic DNS中更新频率^[由TTL来控制，比如2分钟]以及其它DNS服务的更新传播速度决定了本地机器上的服务的可持续性和可用性。

# 参考资料
1. 《用TCP/IP进行网际互连 - 第一卷》
2. [How Domain Name Servers Work](http://computer.howstuffworks.com/dns.htm)
3. <http://www.iana.org/>
4. <http://dnslookup.me/dynamic-dns/>
5. [CNAME记录](https://support.dnspod.cn/Kb/showarticle/tsid/32/)
6. [A记录](https://support.dnspod.cn/Kb/showarticle/tsid/30/)
7. [DNSPod问题向导](https://support.dnspod.cn/Kb/guide)
8. easyDNS support documents





