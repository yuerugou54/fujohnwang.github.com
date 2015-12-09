% How To Shutdown Java Service Gracefully [Tutorial] 
% FuqiangWang


I will just talk about in my way, I don't care your tricky ways like `kill`  something.



First of all,  Let's define `Service`, U can refer to google guava's one, but Let's make it simple,  A Service can be started and stopped, sometimes, we would like to see the status of a service, so a `running` status will be exposed too. 

So A simple service looks like:


~~~~~~~ {.java}
class MyService{
	private volatile boolean running = false;
	public void start(){
		....
		running = true;
		while(running){
			...
		}
	}
	
	public boolean isRunning() {return running;}
	
	public void shutdown(){
		running = false;
	}
}
~~~~~~~

If we run this service in main thread, we don't have a chance to call shutdown(),  so we expose this Service via jmx, say ,  with springframework:

~~~~~~~ {.java}
@ManagedResource(...)
class MyService{
	...
	
	@ManagedOperation
	public void shutdown(){
		running = false;
	}
}
~~~~~~~

and configure spring to expose it:


~~~~~~~ {.xml}
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:mbean-export/>

</beans>
~~~~~~~

then, when we would like to shut it down, we call the shutdown() method via jmx. 

To make things simple, we usually will give a stop.sh under bin directory for ops,  such a stop.sh can call this shutdown() method via jmx with the help of attach API and management-agent.jar, as long as we wrap these functions into a helper main class:

~~~~~~~ {.java}
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;

/**
 * A Tool you can use to stop your service process gracefully via JMX locally and safely.
 *
 * Shutdown needs tools.jar in classpath, so to run this class, assign the path of tools.jar in your system in the shell script, it's a must prerequisite!
 *
 * @since 2014-10-24
 */
public class Shutdown {
    public static final String LOCAL_CONNECTOR_ADDRESS_URL = "com.sun.management.jmxremote.localConnectorAddress";

    /**
     * current process's pid
     */
    private String pid;
    /**
     * the managed bean's name of the top service that we will stop
     */
    private String mbeanName;
    /**
     * the operation name of the managed bean, usually named "stop", "shutdown", "destroy" without any parameters.
     */
    private String mbeanMethodName;

    protected JMXServiceURL getConnectorAddressAsPerPid(String pid) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        VirtualMachine vm = VirtualMachine.attach(pid);
        String connectorAddress = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS_URL);
        if (connectorAddress == null) {
            String agent = vm.getSystemProperties().getProperty("java.home") + File.separator + "lib" + File.separator + "management-agent.jar";
            vm.loadAgent(agent);
            connectorAddress = vm.getAgentProperties().getProperty(LOCAL_CONNECTOR_ADDRESS_URL);
        }
        return new JMXServiceURL(connectorAddress);
    }

    public Object execute() throws Throwable {
        validate(pid, "pid");
        validate(mbeanName, "mbeanName");
        validate(mbeanMethodName, "mbeanMethodName");

        JMXConnector jmxConnector = JMXConnectorFactory.newJMXConnector(getConnectorAddressAsPerPid(getPid()), null);
        jmxConnector.connect();
        try {
            MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
            return connection.invoke(ObjectName.getInstance(getMbeanName()), getMbeanMethodName(), null, null);
        } finally {
            jmxConnector.close();
        }
    }

    protected void validate(String property, String propertyName) {
        if (property == null || property.trim().isEmpty())
            throw new IllegalStateException("[" + propertyName + "] must be set");
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMbeanName() {
        return mbeanName;
    }

    public void setMbeanName(String mbeanName) {
        this.mbeanName = mbeanName;
    }

    public String getMbeanMethodName() {
        return mbeanMethodName;
    }

    public void setMbeanMethodName(String mbeanMethodName) {
        this.mbeanMethodName = mbeanMethodName;
    }

    /**
     * Shutdown shutdown = new Shutdown();
     * shutdown.setPid("7198");
     * shutdown.setMbeanName("com.sun.management:type=DiagnosticCommand");
     * shutdown.setMbeanMethodName("vmSystemProperties");
     * System.out.println(shutdown.execute());
     */
    public static void main(String[] args) throws Throwable {
        if (args.length != 3) {
            throw new Exception("usage: java ... Shutdown [pid] [mbean name] [mbean operation]");
        }

        Shutdown shutdown = new Shutdown();
        shutdown.setPid(args[0]);
        shutdown.setMbeanName(args[1]);
        shutdown.setMbeanMethodName(args[2]);
        shutdown.execute();
    }
}
~~~~~~~


then in the stop.sh, you can simple write:


~~~~~~~ {.bash}
#! /usr/bin/env bash

java -cp tools.jar:... com.wacai.csw.scheduler.controls.Shutdown [pid] [service mbean name] [shutdown method]
~~~~~~~

since the tools.jar is platform dependent, just point it to the location under your platform in the shell.

OK, now everything works perfectly and seamlessly.






