% Netty Framework Tips And Gotchas

*target on netty 3.x*

# Tips of Netty

1. annotation doesn't effect anything, pipeline factory do!
2. always provide your own PipelineFactory so that others can see your pipeline overview; add java doc(@see) in your ChannelHandler to point to the PipelineFactory definition class for further documentation;
3. prevent re-invent the wheels that has been available;(Since Netty has provided lot of available ChannelHandler implementations we can use.)
4. pay attention to event-driven attribute of Netty, simply put, the "messageReceived" method will be invoked multiple times, so take care of the state of your data carefully.
5. use LoggingHandler to debug.
    - LoggingHandler use JDK logging as default logging facility. If we want to change to use other ones, we need invoke InternalLoggingFactory.setDefaultFactory(..) before any netty classes are loaded. for example, if we want to use slf4j logging facility: `InternalLoggingFactory.setDefaultFactory(new Slf4JLoggingFactory();  `
6. You have to invoke channel.close() in another thread other than the IOWorker thread.

~~~~~~~ {.java}
public void exceptionCaught(final ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {  
            new Thread(){  
                public void run(){  
                    ctx.getChannel().close().addListener(new ChannelFutureListener() {  
                        public void operationComplete(ChannelFuture future) throws Exception {  
                            future.awaitUninterruptibly();  
                            getBoostrap().releaseExternalResources();  
                            logger.debug("shutdown");  
                        }  
                    });  
                }  
            }.start();  
    }  
~~~~~~~

7. use IdleStateHandler and IdleStateAwareChannelHandler together to achieve some functionalities like connection status checking, long-push server mocks, etc.
    - NOTE: use a global Timer to share between all of you IdleStateHandlers instead of creating each for them. A Timer can suffice because of its implementation mechanism(TimerWheel).
 
8. always set “connectTimeoutMillis” connection option to achieve timeout return. without this option, future.awaitUninterruptibly(timeoutValue) means less.
9. More...

# Gotchas of Netty


1. 在使用ChannelBuffer的readBytes和getBytes的时候要注意index的意义不同.
    - 例 如: 当前ChannelBuffer中有10个byte, 你通过readBytes读取了4个, 然后, 想查看一下下一个byte的值, 那么,你可以通过readByte()方法, 然后resetReaderIndex(); 或者, 你可以通过getByte(4)来peek这个值, 这里要注意的就是, getByte传入的index是最初的ChannelBuffer开始位置进行计算, 而不是剩余的bytes的位置进行计算. 即不是getByte(0).
2. @ChannelPipelineCoverage("one") Annotation doesn’t mean too much. It only works as a tip, nothing more. If you think you mark a ChannelHandler with “one” and Netty will use the ChannelHandler as a prototype, then you are wrong. It’s still your responsibility to ensure the scope semantics of singleton and prototype.
3. 在ChannelHandler中， 通过exceptionCaught方法再次将异常抛出以期望更上层来处理是没有前途的。 因为这种情况下抛出的异常将只是由DefaultChannelPipeline记录一条warning的日志，仅此而已， 你无法进一步插足该异常的处理。另选方案可能是， 在exceptionCaught方法中将要抛出以交给其他对象处理的异常放入某个共享状态中， 比如某个queue， 然后， 对这些异常感兴趣的对象可以对该队列进行轮询以处理之。
4. More...





