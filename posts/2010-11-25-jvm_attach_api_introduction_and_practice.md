% JVM Attach API Introducation And Practice
% fujohnwang
% 2010-11-25
__author: fujohnwang__


My Journey of Attach API started from a simple tool to monitor and manage our application in production as a complementary way(of course, we have our own monitoring and mangement platform).This inspired me to escalate the usage scenarios of the Attach API. That's why this piece of words are here.

# What's Attach API and What It can do?
The Attach API was introduced after Java6(Mustang), it allows us to attach our application(can be a simple tool as I have, or with other purpose) to another target application running in JVM. The instant idea is that U can add new functionalities to ur java applications even they have been started. That's true, although it's not exactly(because you still have to plan for the whole architecture things of your applications).

People say, “One picture is better than a thousand of words” , So here is a picture I made for u, ;-)

<img src="../images/AttachAPIDemo.png"/>

As you see, “Target App” and “Attach API Use App” are in diferent boxes, that's, running in different JVM instances. Even when “Target App” has been started for a while, “Attach API Use App” still can“put” an agent into it to run, here “YourAgent” in the picture. Isn't it simple? yeah, the idea is simple, but you may ask, what kind of agent? What's the format of “YourAgent” in the picture? read on...

#  Mate of Attach API [1] - Java Agent

The Agents the Attach API use are standard java agent, see javadoc of JDK package “java.lang.instrument” , the package description gives a elaberate explanation on the java agent.

Simply put, a java agent is a jar, which has the MANIFEST.MF defined one or two entries that identify which class definition(s) is the main running entry. If you want to implement a java agent which will be used with “-javaagent:youragent.jar” command line option, which will run before the main method of your application, you should add an entry in MANIFEST.MF of your jar which of course has packaged in the class you want it to be the main entry:

<pre>
Premain-Class: xx.xx.AgentDefinitionClass
</pre>

If you want to implement a java agent which will be executed after the VM has started, add such similar entry:

<pre>
Agent-Class: xx.xx.AgentDefinitionClass
</pre>

That's all? No...

The entry methods defined in java agent have no common “public static void main(String args[])” method sinature, there is a little difference. If your agent definition is only aimed for running before the main method of your application, you can just define a “premain” method as the execution entry:

<pre>
public static void premain(String agentArgs, Instrumentation inst)
</pre>
   
you can leave out the second argument also:

<pre>
public static void premain(String agentArgs)
</pre>
   
If your agent is aimed to be run after the target application has been started, you should add a “agentmain” method into your agent defintion class:

<pre>
public static void agentmain(String agentArgs, Instrumentation inst)
</pre>

or if you don't need the second argument:

<pre>
public static void agentmain(String agentArgs)
</pre>

At last, If you want your agent to be run in both timing, you of course can define both “premain” and “agentmain” together inside too.

That's all for java agent, more or less, if you still don't get it, read the package description of java.lang.instrument for further information.(U do should read it to know what you can do in both “premain” and“agentmain” methods, or how they are loaded into the JVM, etc.)

# Attach API Family Hierarchy
The family of Attach API is small, so it's not too hard to find out how it work.

There are 2 packages and 7 classes for Attach API with 3 exception classes, you can find details here . We mainly focus on 2 classes, “VirtualMachine” and “AttachProvider” , as you guess, since“AttachProvider” is under a spi package, its implementations will be responsible for providing different attach providers as per different operating systems. An attach provider will provide a“VirtualMachine” implementation as per its OS type. JVMs for different OS will provide different “AttachProvider” and corresponding “VirtualMachine” implementations.

Currently, we can get a simple class hierachy for this two:
<pre>
AttachProvider                                                     VirtualMachine
                       |                                                                     |
             HotSpotAttachProvider                                                  HotSpotVirtualMachine
                   /   |   \                                                          /      |       \
WindowsAttachProvider  |   LinuxAttachProvider                         LinuxVirtualMachine   |    WindowsVirtualMachine               
              MacosxAttachProvider                                                  MacosxVirtualMachine

</pre>
   
With This picture in mind, let's go further to find out what the two abstractions can do.

“VirtualMachine” is the one class will interactive with when we use the AttachAPI, with “VirtualMachine” , we can attach our application to another target java application, and we can load agent into the target java application after attached to it. A conventional usage pattern looks like this:

<pre>
VirtualMachine vm = VirtualMachine.attach("18244");
try {
    vm.loadAgent("/../agent.jar");
    // System.out.println(vm.getAgentProperties().get("XXX"));
} finally {
    vm.detach();
}
</pre>
   
Usually, we attach to the target application by the process id (here our application's pid is 18244), as the first line of the code states. If no exceptions are thrown after attaching, we can load our agent by“loadAgent(..)” method. At last, remember to detach from the target vm when things are done.

So until now, if “VirtualMachine” can do everything for us in such scenarios, what “AttachProvider” does? In fact, when we call attach method of “VirtualMachine” , underneath “AttachProvider” will be the real worker. A prototype code may seem like:

<pre>
// VirtualMachine vm = VirtualMachine.attach("pid");

VirtualMachine vm = null;
List<AttachProvider> providers = AttachProvider.providers();
for (AttachProvider provider : providers) {
    try {
        vm = provider.attachVirtualMachine("pid");
        break;
    } catch (Exception e) {
        // do sth. necessary
        continue;
    }
}

</pre>   
   
Just a lookup to find out which provider is qualified and return a corresponding “VirtualMachine” instance.

Now, I think you should have know something about JVM Attach API, but all of the above is theory, let's get our hands dirty to write some code.

# Code Speaks - A Simple Usage Demo
In this sample, we will connect to the target application via JMX to dump an overview information of the application status. Since we just registered Mbeans onto Platform MBeanServer and didn't expose MBeans remotely, we have to connect to the Platform MBeanServer locally. To do this, we have to resort to the AttachAPI after Java6, and luckily, we are using Java6 ;-)

	Tip
	This things we are doing is a prototype monitoring application, you can escalate it to do more things.
	
Suppose we have defined our MBeans and registered them into JMX Platform MbeanSever:

<pre>
MBeanServer defaultMBeanServer = ManagementFactory.getPlatformMBeanServer();
for (Resource handle : resources) {
    ObjectName oname = ..;
    Monitor monitor = new Monitor();
    monitor.setSomeAttrbutes(..);
    defaultMBeanServer.registerMBean(monitor, oname);
}
</pre>   

After the target application is started, these Mbeans will be there for us.
To connect to MBean Server locally, we resort Attach API to get the local JMX address first, then operate the MBeans with standard JMX API, here is the code:

<pre>
public class App
{
  public static void main(String[] args)
    throws Exception
  {
    if (args.length < 1) {
      System.err.println("you must provide the pid and mbean domain name of the running application to monitor it.");
      System.exit(-1);
    }
    String domainName = "Erosa";
    if (args.length == 2) {
      domainName = args[1];
    }
    String localJmxAddress = ConnectorAddressLink.importFrom(Integer.valueOf(args[0]).intValue());
    if (localJmxAddress == null) {
      VirtualMachine vm = VirtualMachine.attach(args[0]);
      String connectorAddress = vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
      if (connectorAddress == null) {
        String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
        vm.loadAgent(agent);
        connectorAddress = vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
      }
      if (connectorAddress == null) {
        System.err.println("Failed to get Local JMX Address by Pid. Exit Without Further Processing.");
        System.exit(-1);
      }
      localJmxAddress = connectorAddress;
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    JMXConnector connector = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(localJmxAddress), null);
    try {
      connector.connect();
      MBeanServerConnection connection = connector.getMBeanServerConnection();
      Set set = connection.queryMBeans(new ObjectName(domainName + ":*"), null);
      for (ObjectInstance ins : set) {
        System.out.println("==================================================================");

        ObjectName name = ins.getObjectName();

        System.out.println("MBean : " + name.toString());

        MBeanInfo beaninfo = connection.getMBeanInfo(name);
        MBeanAttributeInfo[] attributes = beaninfo.getAttributes();
        System.out.println("-----------Attributes------------");
        for (MBeanAttributeInfo attr : attributes) {
          System.out.println(attr.getName() + ": " + connection.getAttribute(name, attr.getName()));
        }
        MBeanOperationInfo[] operations = beaninfo.getOperations();
        System.out.println("-----------Operations------------");
        for (MBeanOperationInfo operation : operations) {
          System.out.println("operation: " + operation.getName() + " " + Arrays.toString(operation.getSignature()));
          if (operation.getName().startsWith("dump")) {
            System.out.println(connection.invoke(name, operation.getName(), new Object[0], new String[0]));
          }
        }
      }
      System.out.println("exit jmx client.");
    }
    finally
    {
      connector.close();
      reader.close();
    }
  }
}
</pre>
   
we try “ConnectorAddressLink.importFrom” first to get the local JMX address , if failed, we turn to use Attach API. That's, we attach to the target application by pid, and then check agent property`com.sun.management.jmxremote.localConnectorAddress` to see if it exists, if not, load management agent from JDK's lib folder and then get agent property`com.sun.management.jmxremote.localConnectorAddress` again, If no errors, this time we will get the local JMX address of the target application. Whe local JMX address is fetched, we use standard JMX API to pull necessray information we want.

That's the whole thing, read the code again and again to let you know this well if you cann't figure out what's going on here,^_^

# Further Thoughts On Usage Scenarios
Attach API has more usage scenarios and many products have been using it for a long time, for example, most of the profiling tools, or JConsole. But I think more on application usage scenarios here.

With the support of Attach API, we can separate different concerns of our applications more clearly. The main modules can mainly focus on the important parts of the system, and support modules can be added later on. For example, if we don't have enough resources to improve the monitoring and management concerns of our applications, we can simply add necessary functions for this and place an extension point for future. When we have enough resources, we can improve this part by attaching new monitoring and management functionalities to the original target applications.

I draft a simple picture before:

<img src="../images/AttachAPIEscalate.png"/>

Different modules have separate boundaries bewteen them, and they can be developed, deployed and maintained separately too. If we are feed of JMX's complicated API, we can set up our own Monitoring Context and access it in our own way. For example, we can embed a Jetty inside of our agents and interactive with it to construct a new platform for Monitoring and management of applications. If original monitoring and management platforms exist, the integration can be done within the agent only, it will not impact the target application, even pollute its API.

I think you can find more useful usage scenarios for Attach API and for yourself. Good luck and have fun with Attach API.

# 参考文档
1. The Attach API. <http://blogs.sun.com/CoreJavaTechTips/entry/the_attach_api>
2. Javadoc of Attach API. <http://download.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html>
3. Using Mustang's Attach API. <http://blogs.sun.com/sundararajan/entry/using_mustang_s_attach_api>
4. Sun JVM Attach API. <http://ayufox.javaeye.com/blog/655761>

---
[1] 除了JavaAgent， 还有JVMTI等相关技术







