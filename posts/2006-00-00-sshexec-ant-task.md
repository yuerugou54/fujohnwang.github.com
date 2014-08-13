% 扩展Ant Task之sshexec
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

随着日方系统管理安全级别的提高，原来自动上传文件的crUploader(自己用RCP写的一个小工具)不能用了，而又不想再返回到原来那种WinScp和SecureCRT手动干预的年代，所以，转而让Ant帮我把上传，执行command，发送邮件等事情全部搞定，不过因为现在执行command的时候都需要指定用户用sudo来执行，所以，其间需要一个手动干预输入password的过程，而Ant提供的默认sshexec又不支持自动给你添入password这个动作，所以相当于将我的后继流程拦腰斩断，实在是如鲠在喉，就因为它，我就得分别手动上传并发送邮件，所以，今天痛下决心，铲除这个刺芒，so here we go...

要扩展ant task，通常都是extends org.apache.tools.ant.Task这个类，不过，既然ant已经提供了ssh的相关类，所以，我们还是extends org.apache.tools.ant.taskdefs.optional.ssh.SSHBase类比较好，呵呵，这样我们就有了：

~~~~~~~ {.java}
public class SshExecWithInteractivePassword extends SSHBase
~~~~~~~

为了有助于理解，后面会给出一些代码片段，不过在此之前，我想先说一下原理比较好哈，其实也很简单，sshexec默认只是打开一个ChannelExec类型的Channel，然后为其设置要执行的command以及command执行后的输出dump到哪里，如果出错，那就抛出BuildException；说白了就是如果执行成功了，把输出打印给你，否则报错；但我们则需要除了输出信息之外，还要通过判断输出，来输入相应信息，也就是sudo后提示的password，鉴于此，我们只需要检测跟Channel相关联的InputStream，如果发现password提示，则向Channel相关联的OutputStream中write相应的password就可以了，easy，right？！

以下是Task的execute()方法的代码：

~~~~~~~ {.java}
public void execute() throws BuildException {
// super.execute();
if(getHost() == null)
{
throw new BuildException("Host is required.");
}
if(getUserInfo().getName() == null)
{
throw new BuildException("Username is required.");
}
if(getUserInfo().getKeyfile() == null && getUserInfo().getPassword() == null)
{
throw new BuildException("Password or Keyfile is required.");
}
if(command == null)
{
throw new BuildException("Command is required.");
}
Session session = null;
Channel channel = null;
try
{
session = openSession();
channel = session.openChannel("exec");
((ChannelExec) channel).setPty(true);
((ChannelExec)channel).setCommand(command);
channel.connect();
executeInChannel(channel);
}
catch(Exception e)
{
throw new BuildException(e);
}
finally
{
dispose(session,channel);
}
}
~~~~~~~

很粗糙的实现，在`executeInChannel(channel)`方法中，我们检测输出并输入所要求的输入（哈哈，有点儿别扭，不过，相对于Channel来说，它的输入，就是我们的输出），类似于：

~~~~~~~ {.java}
InputStream in = channel.getInputStream();
OutputStream out = channel.getOutputStream();
InputStream err = ((ChannelExec) channel).getErrStream();
byte tmp[] = new byte[2048];
do {
while (in.available() > 0) {
int i = in.read(tmp, 0, 2048);
String line = new String(tmp, 0, i);
log(line+"\n");
if(i>0 && PASSWORD_PATTERN.matcher(line).find())
{
out.write(super.getUserInfo().getPassword().getBytes());
out.write("\n".getBytes());
out.flush();
break;
}
}
while(err.available() > 0)
{
int i = in.read(tmp, 0, 2048);
String line = new String(tmp, 0, i);
log(line+"\n");
}
if (channel.isClosed()) {
int exitStatus = channel.getExitStatus();
if (exitStatus != 0) {
StringBuffer exp = new StringBuffer();
exp.append("Error Exit Status with Value:").append(
exitStatus).append("\n\n");
throw new BuildException(exp.toString());
}
break;
}
try {
Thread.sleep(1000L);
} catch (Exception exception) {
}
} while (true);
~~~~~~~

呵呵，我知道代码很烂，不过将就看吧，我也不会为了这么个简单的小功能去费那个劳神子，一切OK之后，打包成jar，扔到ANT-HOME/lib下，这样你就可以使用它了：

~~~~~~~ {.xml}
<taskdef name="dsshexec" classname="org.darrenstudio.extension.anttasks.SshExecWithInteractivePassword"/>
<dsshexec host="server" username="user" password="pwd" command="sudo cp /home/wfq/result.txt /usr/local/" trust="true"/>
~~~~~~~

注意哦，上面的情况可不是普遍适用的，只是给你一个类似问题的提示而已，使用的话，可能要根据环境adapt一下,OK,打完收工！

By the Way：上次那个Gotches是我的疏忽，敲错一个字母，应该是Gotchas,意即“I've got you"，不过，一般字典上查不到