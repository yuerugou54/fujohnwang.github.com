% How to use SSH in Java Programmatically
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

在Java程序中使用SSH（How to use SSH in Java Programmatically)

---by Darren.Wang

这个标题不知道能不能表达我的意思，实际上我只是想总结一下可以通过哪些方式或者途径来达到在Java程序中使用SSH相关功能（任务）的目的。前几天有更多free time，所以，为了简化credit的管理工具正式版的发布上传过程，简单实现了一个基于SWT界面的上传应用程序，要完成的功能其实也很简单，但是为了提高上传速度和数据传输的安全性，所以，上传分成几个阶段同时使用SSH来保证上传过程的安全性，之于说上传的阶段等细节不属于我今天要描述的重点，重点是如何在Java中使用SSH，尤其是远程登录到Linux，并执行Shell命令。

现从同事的一个需求说起，他手头的任务中包括检查某台Linux机器的磁盘空间等情况，并随同Email发送。当然别人也给他提出了多种解决方法，不能说不好，但在我看了与程序的集成性上面差一些，所以我觉得给他写一个Utility（坐我旁边，不帮都不行，呵呵）。

实际上，实现原理很简单，直接SSH登录那台Linux机器执行df命令就可以了，其他信息，像当前目录拥有的文件列表等，运行ls
-ls命令，这些我想大家都很清楚，那么在Java中我们是如何实现类似功能的那？！

可能有人以前做过类似功能，那他一定听说过JSch或者说OpenSSH（当然，我们会用他的Java实现安ganymed），对，我们也只是为JSch提供了一个简单的Wrapper而已。

先来看看这个Wrapper是什么样子的吧，然后我再详细说一下这个程序的设计和实现细节：

~~~~~~~ {.java}
package org.darrenstudio.ssh;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.darrenstudio.ssh.callback.SSHExecCallback;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHExecutor {
private JSch jsch;
private Session session;
private boolean login;

public synchronized void login(SSHLoginOptions options) throws SSHExecException
{
try
{
if(jsch == null)
jsch =new JSch();
session = jsch.getSession(options.getUsername(),options.getHost(),options.getPort());
session.setPassword(options.getPassword());
Properties prop = new Properties();
prop.setProperty("StrictHostKeyChecking","no");//StrictHostKeyChecking: ask | yes | no
session.setConfig(prop);
session.connect();
login = true;
}
catch(Exception e)
{
throw new SSHExecException(e);
}
}

public synchronized void execute(String command,SSHExecCallback callback) throws SSHExecException
{
if(!login)
throw new SSHExecException("login first before executing the remote command!");
Validate.notEmpty(command);
Channel channel = null;
try
{
channel =session.openChannel("exec");
((ChannelExec)channel).setCommand(command);
InputStream in=channel.getInputStream();
// OutputStream out=channel.getOutputStream();
InputStream err = ((ChannelExec)channel).getErrStream();

// to retrieve the interactive password request information, this pty is a must
((ChannelExec)channel).setPty(true);

channel.connect();

byte[] tmp=new byte[2048];
while(true)
{
while(in.available() > 0)
{
int i=in.read(tmp, 0, 2048);
String line = new String(tmp, 0, i);
callback.dumpConsole(line);
}

while(err.available() > 0)
{
int size = err.read(tmp,0,2048);
String line = new String(tmp,0,size);
callback.dumpErrStream(line);
}

if(channel.isClosed())
{
int exitStatus = channel.getExitStatus();
if(exitStatus != 0)
throw new SSHExecException("Error Exit Status with Value:"+exitStatus);
break;
}
try{Thread.sleep(1000);}catch(Exception ee){}
}
}
catch(Exception e)
{
throw new SSHExecException(e);
}
finally
{
if(channel != null)
{
channel.disconnect();
channel = null;
}
}

}

public synchronized void dispose()
{
if(session != null)
{
session.disconnect();
session = null;
}
login = false;
}
}
~~~~~~~

我们给出一个Executor，他负责为我们执行Shell命令，他首先要求我们登录到要执行命令的Linux机器（即login方法），然后，如果登录成功，client端就可以调用execute方法来执行相应的shell命令，执行后，在finally中dispose掉该Executor。

对于login方法来说，因为需要提供login相关信息，而且这些信息参数较多，3－4个，当然，相对来说也不是很多，但是，我们还是采用将他们归并到一个参数类的做法（我想Effective Java大家都读过），这就有了我们的SSHLoginOptions类：

~~~~~~~ {.java}
public class SSHLoginOptions implements Serializable {

private static final long serialVersionUID = -8018206086412607771L;

private String host;
private String username;
private String password;
private int port = 22;

public SSHLoginOptions(String host,String username,String password)
{
this(host,username,password,22);
}
public SSHLoginOptions(String host,String username,String password,int port)
{
this.host = host;
this.username = username;
this.password = password;
this.port = port;
}

public String getHost() {
return host;
}

public void setHost(String host) {
this.host = host;
}

public String getPassword() {
return password;
}

public void setPassword(String password) {
this.password = password;
}

public int getPort() {
return port;
}

public void setPort(int port) {
this.port = port;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String toString() {
return new ToStringBuilder(this).append("host", host).append(
"username", username).append("password", password).append(
"port", port).toString();
}
}
~~~~~~~

如果登录不成功的话(可能因为网络不通等原因），我们需要抛出一个异常以便告诉Client端该事件，并终止以下步骤，所以，我们采用抛出自定义的SSHExecException：

~~~~~~~ {.java}
public class SSHExecException extends NestableException {

private static final long serialVersionUID = -2804917566444475128L;

public SSHExecException(String cause)
{
super(cause);
}
public SSHExecException(Throwable t)
{
super(t);
}
public SSHExecException(String cause,Throwable t)
{
super(cause,t);
}
}
~~~~~~~

（这里定义为一个Checked异常，实际上，感觉定义为unchecked异常更恰当一些，因为如果失败，Client端也做不了什么）

登录成功后，login标志被置为true，这样execute方法才可以被调用。

execute方法有两个参数，第一个参数为String类型，表示将被执行Shell命令；第二个参数较为特殊，他是我们自己定义的一个接口：

~~~~~~~ {.java}
public interface SSHExecCallback {
	void dumpConsole(String line);
	void dumpErrStream(String errline);
}
~~~~~~~

这个接口的实现负责处理Linux的正常输出和Error输出，至于说如何处理这些输出，你可以按照自己的需要给出自己的实现，比如，只是简单的打印到控制台：

~~~~~~~ {.java}
public class DefaultSSHExecCallback implements SSHExecCallback {

	public void dumpConsole(String line) {
	System.out.println(line);
	}

	public void dumpErrStream(String errline) {
	System.err.println(errline);
	}
｝
~~~~~~~

在execute方法一开始，我们会检查是否登录成功，如果没有，那同样会抛出SSHExecException，以示说该类没有为Shell命令的执行准备好相应的状态，从而阻止随后的不安全操作。
之后，我们会打开一个Exec Channel，通过这个Channel来执行Shell命令，这可以很容易的从Executor的源码中看出来，如果执行过程中出现异常，我们会抛给Client端我们的自定义异常，当然，不管执行成功或者失败与否，我们都会关掉该Channel以释放连接，否则，主程序会挂在那里。在execute方法中，唯一需要关注的一个地方就是((ChannelExec)channel).setPty(true);这一句，如果没有他，那你的控制台将什么东西都没有，你将得不到任何想要的信息。

为了说命令执行完成后释放资源，我们给出一个dispose方法，这也是很自然的，这里不再赘述。

下面是该类的一个TestCase，大家可以很容易看出该类的使用，很简单。

~~~~~~~ {.java}
public class SSHExecutorTest extends TestCase {

	private SSHExecutor executor;

	public static void main(String[] args) {
	junit.textui.TestRunner.run(SSHExecutorTest.class);
	}

	protected void setUp() throws Exception {
	super.setUp();
	executor = new SSHExecutor();
	SSHLoginOptions loginOptions = new SSHLoginOptions("m.livedoor.cn","root","zxcv1234");
	executor.login(loginOptions);
	}

	protected void tearDown() throws Exception {
	super.tearDown();
	executor.dispose();
	executor = null;
	}

	public void testExecuteWithCommandUname() throws SSHExecException
	{
	String command = "uname";
	GenericSSHExecCallback callback = new GenericSSHExecCallback();
	executor.execute(command,callback);
	assertEquals("The Operating System of m.livedoor.cn should be Linux","Linux",StringUtils.trimToEmpty(callback.getOutput()));
	}
}
~~~~~~~

至此，我们的Wrapper类就算完成了，让我们回过头来看看，我们能归纳出什么东西。

到目前为止，我所可以提供的相关信息有两类，一类就是执行Scp相关操作，一类就是基于SSH的Shell命令的执行，那么要完成这两类功能，现在有什么东西可以让我们避免去重新发明轮子那？！

对于Scp相关任务来说，除了前面blog曾经提到过的通过Ant来实现外，你也可以通过JSch来完成，不要忘了，Ant的Scp Task也是通过JSch完成的，除此之外，OpenSSH的Java实现---ganymed也可以很容易的实现scp功能，而且，代码也看起来很简洁：

~~~~~~~ {.java}
/**
* @author darrenwang
* @since 1.0
*/
public class SCPExecutor {
	private Connection connection;
	private boolean login;

	public synchronized void login(SSHLoginOptions options) throws SSHExecException
	{
		try
		{
			connection = new Connection(options.getHost());
			connection.connect();
			login = connection.authenticateWithPassword(options.getUsername(),options.getPassword());
			if(!login)
			throw new SSHExecException("Authentication failed.");
		}
		catch(Exception e)
		{
			throw new SSHExecException(e);
		}
	}

	public synchronized void doScp(File file, String todir) throws SSHExecException
	{
		if(!login)
			throw new SSHExecException("login() first before executing the scp task!");
		try
		{
			SCPClient client = connection.createSCPClient();
			client.put(file.getAbsolutePath(),todir);
		}
		catch(Exception e)
		{
			throw new SSHExecException(e);
		}
	}
	public synchronized void dispose()
	{
		if(connection != null)
		{
			connection.close();
			connection = null;
		}
		login = false;
	}
}
~~~~~~~


除了Scp，那剩下很大一部分任务可能都是基于SSH的Shell命令执行啦，这个同样，你可以通过Ant，JSch和ganymed来实现，这里就不做赘述了，因为通过上面的SSHExecutor和以前的blog，你可以很容易的给出实现。

OK，今天就写这些了，《地海传说》，我来啦...