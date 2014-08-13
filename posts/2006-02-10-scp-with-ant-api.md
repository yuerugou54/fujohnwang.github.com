% Scp With Ant API programmatically
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

原来Credit项目在正式发布一个版本的时候，发布的正式安装文件都需要通过好几步才能上传到正式的下载服务器上（当然如果不是为了提高上传的速度，直接从本地拖到目的地也不是不可以），所以，需要在WinScp和SecureCRT之间来回切换，而且需要手工干预，故此就萌生了自动化这个上传过程的想法。正好这几日手头事情不多，故此，着手此事。
    
为了避免Reinvent the wheel的危险，当然一开始会google一下，如果谁也正在做或者已经做过此事，那你应该已经发现了解决这种问题的lib----JSch，因为我们需要scp嘛！不过，JSch只是提供了一些Sample Code，但对于初手或者说想短期完成一定工作的人来说，很明显的，研读code的时间不会很多（也或许是我懒，不愿读code的借口，hoho）。而且，他提供的Sample中大部分也是需要Swing等接口的交互，短期内也无法转化为我用，所以转而求助于Ant，因为我知道Ant提供有Scp的task，所以，底层肯定提供有实现该task的API可用。

在研究了后面参考资料的2，3之后，“达伦”就开始了他的Credit Release Upload Console的开发工作。

如果按照原来的上传流程手册进行的话，我在远程执行scp的时候遇到了问题，因为我无法在程序中自动根据server的反馈提供相应的response，比如如果SSH登录到一台远程机的话，我要在这台远程机器上执行scp user@server:/directory . 命令的话，他会提示需要输入password，但这个交互过程我不知道在ant的API中如何实现调用，可能SSHUserInfo是做这个工作的，但没有细看，因为返回的异常只是给出一个code 1，就表示错误，更多的信息一点儿没有，一看就知道没有戏，没有详细的StackTrace，我可猜不出来到底哪里出了问题，所以，最终转换了一下思路直接全部执行scp操作，而不是使用SSHExec来完成部分功能。

至于说界面什么的就不想赘述了，只是说一下在java如何使用Ant API执行Scp操作。

具体步骤可以简单总结为：

1. 构造一个Dummy的Project对象，然后project.init()；
2. 实例化一个Scp对象，调用其setProject方法，设置上面的Dummy Project；
3. 设置其他properties of Scp Task，比如 setAuthProperties(Scp scp)方法设置一些登录的认证信息，设置task的TaskName等；
4. 设置上传的File或者FileSet以及要上传到的目的地；
5. 调用scp.execute()执行；

这样我们可以得到下面类似的代码：


~~~~~~~ {.java}
	public void scp(File file) 
    { 
        Scp scp = getScpClient(); 
        setAuthProperties(scp); 
        // set copy task properties 
        scp.setFile(file.getAbsolutePath()); 
        scp.setTodir("username:password@mserver:/usr/local/credit"); 
        // execute the scp task 
        scp.execute(); 
    } 
    private void setAuthProperties(Scp scp) 
    { 
        // set SSH properties 
        scp.setHost("yourserver"); 
        scp.setUsername("yourusername"); 
        scp.setPassword("yourpwd"); 
        scp.setTrust(true); 
    } 
    private Scp getScpClient() 
    { 
        // create a dummy project object for the task 
        Project project = new Project(); 
        project.init(); 
        // create our Scp task and init it 
        Scp scp = new Scp(); 
        scp.setProject(project); 
        scp.setTaskName("scp"); 
        scp.setTaskType("scp"); 
        return scp; 
    }
~~~~~~~

当然为了让以上代码可用，你还需要将必须的jar包含到你的classpath中，这些jar包括ant.jar以及ant－jsch.jar，还有最终的jsch（suffix）.jar，因为ant的scp也是使用了JSch的API实现的。

有了这些，你可以随便从哪台机器scp文件到另一台机器了。


**REFERENCE**

1. JSch Samples
2. Invoking Apache Ant programmatically(http://www-128.ibm.com/developerworks/websphere/library/techarticles/0502_gawor/0502_gawor.html)
3. Ant Reference: Using Ant Tasks Outside of Ant
