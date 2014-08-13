% Java Daemon Control

As to desktop or normal Java applications, we can easily know when we should shutdown the application or not, because users have explicit ways to do this, for example, for a Swing Application, usually a "CLOSE" menu or tool-bar item will be available, or directly click the "X" icon on the left/right top of the window. But for a Java application that will be run as a server process(which don't need interactive behavior), what we do?

# Old Days Solutions

Directly “kill -9 ” ? Of course, that's a way, but that's too brutal.

A Java process that will be run as a server process usually will be sent to OS's background to run, that's called “daemon ” on Unix and “service ” on Windows. A Simple way to control the life-cycle of a Java daemon is to start a loop and wait for user input, like this:

~~~~~~~ {.java}
BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));  
String line = null;  
do {  
    line = reader.readLine();  
    if (line != null && line.equals("quit")) {  
        break; // and exit gracefully  
    }  
}  
while (true);  
  
// clean up and exit  
~~~~~~~
 
This solution is better than just start an infinite loop and do nothing, because the latter solution will occupy all of your CPU's power(I have seen such a stupid solution and it does exist). But this is still not a best one, although users can interactive with it, the process itself have no way to notify the process to exit. That's, you can control it from outside, but can't do it from inside.

Another mostly used solution is, start a TCP server socket and listen control requests, when termination control request is received, a loop based on control flag will break and exit. This is similar to above solution, just with another communication channel. ^[To use this solution, you'd better add authentication to your control service so that others with malicious purpose will not hurt you.]

A common pattern for both solution can be found,that's, set up a control flag to start a loop with, and then wait for other channels to change the control flag. This can be formulated as:

~~~~~~~ {.java}
boolean running = true;  
  
while(running)  
{  
    // do Sth.  
}  
// exit
~~~~~~~

As to how to change the control flag, there are two solutions presented, can you find more?

# Alternatives Available Today
Old Days Solutions have their pros and cons, today more solutions are available for us.

The first one is Jakarta Commons Daemon . It provides a Java daemon solution with native Jsvc and Procrun support.

The second and third are Java Service Wrapper and yajsw , The former is a commerce solution now, and the latter is an open source one which has some works based on the former(there is a possibility that I misunderstand its introduction).

Other Solutions are Akuma , Start-Stop-Daemon, or Classword , but I don't get patience to read their document. If you are interested in them,follow the links I provide or google them.

# My Choice

I choose to combine shell and “sun.misc.Signal ” and “sun.misc.SignalHandler ” to achieve Java daemon, because they are much simpler to me to understand them and use them. The Shell will take care of running-in-background stuff:

```bash
#!/bin/sh  
java -cp your_class_path com.domain.main_class <&- &  
pid=$!  
echo ${pid} > mydaemon.pid  
```
 
And “sun.misc.Signal ” and “sun.misc.SignalHandler ” will take care of controlling the life-cycle of the program.
About “sun.misc.Signal ” and “sun.misc.SignalHandler ” , you can find more information at 参考文档 , here I just simply introduce how to use them together to achieve asynchronous communication between processes or just internally in a same process.

The concept of “sun.misc.Signal ” and “sun.misc.SignalHandler ” is simple

## Signal

Signal is the signal that u will send to SignalHandler to process, so you can create a Signal just like instantiate a simple value object:

~~~~~~~ {.java}
Signal sig = new Signal("USR1");  
~~~~~~~
 
The signal names you pass to “Signal ” conform a pattern, that's, remove the prefix "SIG" from the name of the standard signals that's used by JVM. For example, if you want to send “SIGINT ” , then you create “Signal ” instance with name of “INT ” ; If you want to send “SIGTERM ” , you create “Signal ” instance with name of “TERM ” :

~~~~~~~ {.java}
Signal interactiveSignal = new Signal("INT");  
Signal terminationSignal = new Signal("TERM");
~~~~~~~
 
Fucking Simple, right?

After you have a Signal, you can send it out by using “Signal ” class's “raise ” method:

~~~~~~~ {.java}
Signal.raise(sig);   
Signal.raise(interactiveSignal);   
Signal.raise(terminationSignal);   
~~~~~~~




## SignalHandler

As the name indicates, “SignalHandler ” will take the responsibility of handling the “Signal ” s.
You implements your own signal handlers by implementing the “SignalHandler ” interface. It has only one method:

~~~~~~~ {.java}
public class MySigHandler implements SignalHandler {  
  
    public void handle(Signal sig) {  
        // ...  
    }  
}  
~~~~~~~
 
It's fucking simple too.

## Glue them together
After you get both “Signal ” and “SignalHandler ” of your own, you should link them together to make it work. This is by “Signal ” class's static method “handle ” :

~~~~~~~ {.java}
MySigHandler sigHandler = new MySigHandler();  
Signal.handle(sig, sigHandler);  
Signal.handle(interactiveSignal, sigHandler);  
Signal.handle(terminationSignal, sigHandler);  
~~~~~~~
 
Now as long as you add them to your java programs and send proper signals to it, the pairs of “Signal ” and “SignalHandler ” will work for you.

You have 2 ways to send signals to your program's process:

1. Use Signal.raise() internally.  
    - This can help to coordinate application's internal state and help to control the life-cycle internally. For example, as long as internal worker thread dies, it can send out a signal, when signal hander finds that all other the worker threads die, it can change the control flag of the whole process and terminate it gracefully.
2. Send Signal from other processes.  
    - directly send out supported OS signal via shell scripts: `kill -s SIGUSR1 <pid of the process>`, combining the pid you get in before shell, this works perfectly.

The only cons to use “Signal ” and “SignalHandler ” is, they are both restricted API which are not guaranteed.

# 参考文档

1. Revelations on Java signal handling and termination . http://www.ibm.com/developerworks/java/library/i-signalhandling/ .
2. 基于OS信号实现Java异步通知 . http://kenwublog.com/java-asynchronous-notify-based-on-signal .
3. Java Daemon . http://barelyenough.org/blog/2005/03/java-daemon/ .












