% Event Driven Style API Design Instead of Old Procedure Style Ones

In fact, I found this is a common problem which often happens on novices, I admit that everyone will take a path to grow up, so I am not blaming someone or something, What I just want to do is to share some experiences or personal opinions on similar scenarios, so that others can benefit and prevent from such problems.

# Background of this topic

We are working on a project that uses Netty as our network layer framework, so that also means we accept Netty's API style and design philosophy, that's event-driven style API design.

I setup the whole code framework, then split the LUW to team members so that everyone can start to get down to their work details. But when I started to do some code review on other members' code, I found some“surprises” .

In order to keep the consistency of the whole code framework, I had designed the API in an event-driven style, but I found the interface which works as a contract between different logical layers had been modified into a style of old procedure ones.

To find out the story on this, let's start from the beginning.

# The original event-driven style API design demonstration

We have several layers in our design, a networking layer, a data processing layer and other layers. In the network layer, we receive data packets from event source, and then dispatch the events to the data processing layer which will process the event data as per different situations. With such a prerequisite, we can easily declare an interface as the abstraction to the behavior of processing event data, let's call it IEventProcessor:

~~~~~~~ {.java}
public interface IEventProcessor {  
       // TODO  
}
~~~~~~~

So what u will do with such an interface declaration to abstract proper behaviors as per the above scenario? I don't know what u will do(although I have known what someone had done), but I do design this interface this way:

~~~~~~~ {.java}
public interface IEventProcessor<E> {  
  
    void onBeginEvent(E event) throws Exception;  
  
    void onJoggingEvent(E event) throws Exception;  
  
    void onRunningEvent(E event) throws Exception;  
  
    void onSomeEventWeDontCare(E event) throws Exception;  
      
    void onOtherEventThatPossible(E event) throws Exception;  
}
~~~~~~~

It's not a big deal u may say, but such design is a proper one as per situation we have above. Why?

1. The event consumers(I mean different implementations of IEventProcessor) can only care about the events they are interested, and don't bother with other concerns;

2. Strong-typed API design setup a strong contract for different layers that stands aside each other. The implementations will know as much as they need to know by only the API that's exposed to them.

3. Different concerns are separated to different places. The event dispatching concern, the event-data marshalling concern and the event processing concern, all of them now are taken care of by different parts of the system.

4. In the period of event processing, if we still have other concerns to deal with, for example, monitoring concern, auditing concern and many more, we can separate them into different AOP aspect and manage them well.

Of course, I think we can find more benefits from such a style of API design. And I also think this is a clear and clean API design for our system, but someone does not.



# current procedure style API design demonstration

After code review of some team member's code, I found he not only break the top implementation class, but also break the original IEventProcessor abstraction. The IEventProcessor interface had been changed to:

~~~~~~~ {.java}
public interface IEventProcessor{  
    void onEvent(byte[] eventData, byte eventType) throws Exception;  
}  
~~~~~~~

and of course , an implementation is also given:

~~~~~~~ {.java}
public class EventProcessor implements IEventProcessor<byte[]> {  
    // fields declarations  
      
    public void onEvent(byte[] eventData, byte eventType) throws Exception {  
        // preconditions checking  
        byte[] event = ArrayUtils.subarray(eventData, 1, eventData.length);  
        switch (eventType) {  
            case EventTypes.EVENT_ONE:  
                processEventOne(event);  
                // auditing things  
                break;  
            case EventTypes.EVENT_TWO:  
                processEventTwo(event);  
                // auditing things  
                break;  
            case EventTypes.EVENT_THREE:  
                processEventThree(event);  
                // auditing things  
                break;  
            case EventTypes.EVENT_FOUR:  
                processEventFour(event);  
                // auditing things  
                break;  
            case EventTypes.EVENT_FIVE:  
                processEventFive(event);  
                // auditing things  
                break;  
            default:  
                break;  
        }  
        // other logic  
    }  
  
    private void processEventFive(byte[] event) {  
        // TODO Auto-generated method stub  
  
    }  
    private void processEventFour(byte[] event) {  
        // TODO Auto-generated method stub  
  
    }  
    private void processEventThree(byte[] event) {  
        // TODO Auto-generated method stub  
  
    }  
    private void processEventTwo(byte[] event) {  
        // TODO Auto-generated method stub  
  
    }  
    private void processEventOne(byte[] event) {  
        // TODO Auto-generated method stub  
  
    }  
} 
~~~~~~~

It may seem OKey to u, but believe me, you still have more to do with.

Here are some opinions of mine for this:

1. First of all, such a API design or directly say implementation breaks the strong-typed contract of original interface design, and turn to a weak typed one which use primitive type int to distinguish different event types. So as a result, every implementations should know which int values are legal and which ones are not. Since every implementations should know same concerns, every implementations may repeat same works too. If strong typed interface design is given, such redundant things or concerns should not appear. This is where strong-typed languages shine.

2. The implementation involves too much concerns which goes against basic software design principles. It has to take care of dispatching event, auditing event data, and processing event data, etc. A good thing is, the event data marshalling concern is taken care of by different component of the system as I had advised, but we still have too many concerns tangling together here.

3. Although different event types have been handled to different data processing methods, but a deadly problem is, these methods are declared to be private, which means we can't even extend the processing logic by inheriting this class. As a contrast, the original IEventProcessor interface design has even better extension ability.

I should say, this is the way we do things in old days, It does work, and can be refactored to have a better structure, but we can do it better. As to how, I think you can find it out or already do.

# What About U?

So What u will do in such scenarios? Which style do you like? Share your opinions and let us all know if you like.

I know this topic gets too much into the details of coding level concerns, but I do like such simple topics because you can always extend such topics and gain more if you do think it further from code level to framework level, event to architecture level.











