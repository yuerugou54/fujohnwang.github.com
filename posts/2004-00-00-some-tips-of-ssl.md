% 有关SSL的一些tips 
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

回大连后几天一直忙，所以没有时间写blog，今天忙里偷闲，写写回大连后的第一篇blog...
   
这一天到两天我在搞SSL，在客户端程序里面实现https连接，以实现用户认证信息的安全传输。因为原来一直做web方面的，现在转到c/s模式有些不适应，而且，以前对java security也没有深入过，所以，现在有些费力。

开始是使用URLConnection和HttpsURLConnection做，但是当时因为证书的问题一直没有能够成功，也查了国外各个网站和forum，大部分说得都差不多，呵呵，就是不成功阿，后来转向Apache 的httpClient，用它先实现了简单的客户端基于http的通信，当转向https的时候同样遇到不可信任证书的问题，后来通过他的custom功能才可以解决。不过，现在对于整个知识的完整性还是欠缺，有时间的话，需要好好研究一下java的security部分。

下面是在resin应用服务器中配置ssl的脚本配置片断，列于下，只作记录, 需要两步：

1. 添加security provider：`<security-provider id='com.sun.net.ssl.internal.ssl.Provider'/>`
2. 配置ssl端口和keystore等

~~~~~~~ {.xml}
<http port=8443>
 <ssl>true</ssl>
 <key-store-type>jks</key-store-type>
 <key-store-file>file://D:/resin-2.1.9/keys/private.keystore</key-store-file>
 <key-store-password>darrenwang</key-store-password>
</http>
~~~~~~~

这些可以在resin的conf里面找到实例，另外，其实要使之ssl运行，在此之前需要配置JSSE或者是OpenSSL，但是因为笔者使用了jdk1.4.x，而jdk1.4.x已经集成了JSSE，所以这些可以省略，如果使用jdk1.3或者以前的jdk，需要单独下载JSSE，并按照JSSE提供的步骤进行JSSE的安装。

然后，通过keytool为keystore加入相应的证书。

最后，就可以使用HttpClient来访问server端程序了，比如servlet等。

下面是使用HttpClient进行https连接的demo代码片断：

~~~~~~~ {.java}
Protocol myhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 8443);
Protocol.registerProtocol("https",myhttps);

HttpClient client = new HttpClient();
client.getHostConfiguration().setHost("127.0.0.1", 8443, myhttps);

PostMethod method = new PostMethod("/examples/basic/servlet/UserAuthServlet";
//PostMethod method = new PostMethod("::URL::http://localhost:8080/examples/basic/servlet/UserAuthServlet" ;
NameValuePair nameParam = new NameValuePair("username",name);
NameValuePair pwdParam = new NameValuePair("psssword",pwd);
method.setRequestBody(new NameValuePair[]{nameParam,pwdParam});

method.setDoAuthentication(false);

int statusCode = client.executeMethod(method);
if(statusCode==-1)
{
throw new Exception("[Post method execute Exception Failed!]";
}
//get Response info
InputStream ins = method.getResponseBodyAsStream();
BufferedReader in = new BufferedReader(new InputStreamReader(ins));
StringBuffer resultBuffer = new StringBuffer();
String line;
while((line = in.readLine())!=null)
{
resultBuffer.append(line);
}
in.close();
ins.close();
method.releaseConnection();
~~~~~~~

OK，先写这些，以后有时间将对SSL以及java security方面进行更多阐述。











