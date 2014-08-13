% Using Java Reflection In a DSL-like Style

I know someone must have heard or known about a library named FEST-Reflect, and maybe you are just using it. It's an interesting library that let you write code of java reflection in a DSL-like style. For example:

~~~~~~~ {.java}
String name = method("get").withReturnType(String.class)  
                           .withParameterTypes(int.class)  
                           .in(names)  
                           .invoke(8);  
~~~~~~~

It's just another way to express same logic, but it give you another way to write your code which make your code more readable. If you want to write your code this way, of course, you can use FEST-reflect directly, but that's not why wrote words below, what I try to tell you is, you can implement such things yourself with little effort. I will draft a prototype to implement such a DSL-like style Java Reflection usage API, If you are interested, read on...

To enable the users to use our API in a DSL-like style like code sample above, it's easy to figure out 2 things:

1. We have to enable method chaining.
2. Static import feature after Java5 may be needed so that the code looks like a DSL syntax.

So first, we have to find out which operations or methods can be used.

We focus on only java reflection usage on Instance Method, since it's not a difficult thing to find out what we can do with Method, An abstraction can be given below:

~~~~~~~ {.java}
/** 
 * method("name").on(targetobject).withArgumentTypes(...).invoke(...); 
 *  
 * @author fujohnwang 
 * @since  1.0  
 */  
public interface IMethodDSL {  
      
    IMethodDSL on(Object target);  
      
    IMethodDSL withArgumentTypes(Class<?>... args);  
      
    IMethodDSL makeAccessible(boolean flag);  
      
    <T> T invoke(Object... args) throws InvocationTargetException;  
}  
~~~~~~~

We express the abstraction in an Java interface, except for the last “invoke ” method, other operations have a return type of the same interface, that's, IMethodDSL itself. That's the way we implement a method chaining behavior.

Since we have had the key abstraction, we can give it an implementation now, it looks like below:

~~~~~~~ {.java}
/** 
 * Default IMethodDSL implementation.<br> 
 *  
 * @author fujohnwang 
 * @since  1.0  
 * @see    ReflectDSL 
 * @see    IMethodDSL 
 */  
public class MethodDSL implements IMethodDSL {  
  
    private static final Logger logger = LoggerFactory.getLogger(MethodDSL.class);  
  
    private String methodName;  
    private Object target;  
    private Class<?>[] argTypes;  
    private boolean accessible;  
  
    public MethodDSL(String methodName) {  
        this.methodName = methodName;  
    }  
  
    public <T> T invoke(Object... args) throws InvocationTargetException {  
        checkInvokeDependencies();  
        try {  
            Method method = findQualifiedMethod(target.getClass());  
            if(this.accessible)  
            {  
                method.setAccessible(true);  
            }  
            /** 
             * usually, the caller will be required to declare proper return type of the method invocation, 
             * so cast to the type they declared is acceptable.  
             * the return type the caller declared should be the returnType they have assigned. 
             */  
            @SuppressWarnings("unchecked")  
            T result = (T)method.invoke(target, PreConditions.nullAsEmpty(args,Object.class));  
            return result;  
        } catch (SecurityException e) {  
            throw new InvocationTargetException(e);  
        } catch (NoSuchMethodException e) {  
            throw new InvocationTargetException(e);  
        } catch (IllegalArgumentException e) {  
            throw new InvocationTargetException(e);  
        } catch (IllegalAccessException e) {  
            throw new InvocationTargetException(e);  
        }  
    }  
  
    protected Method findQualifiedMethod(Class<?> clazz) throws SecurityException, NoSuchMethodException {  
        if(this.argTypes == null)  
        {  
            Method qualifiedMethod = null;  
            for(Method method : clazz.getDeclaredMethods())  
            {  
                if(method.getName().equals(this.methodName))  
                {  
                    if(qualifiedMethod != null)  
                    {  
                        throw new IllegalStateException("please provide arguments of method if you want to invoke on overloaded methods.");  
                    }  
                    qualifiedMethod = method;  
                }  
            }  
            if(qualifiedMethod == null)  
            {  
                throw new NoSuchMethodException();  
            }  
            return qualifiedMethod;  
        }  
        else  
        {  
            return clazz.getDeclaredMethod(this.methodName, this.argTypes);  
        }  
    }  
    /** 
     * usually, these information like "methodName", "target" are required, <br> 
     * but in case special situations, this method is declared protected so that in those situations,  
     * others can override this default behavior. 
     */  
    protected void checkInvokeDependencies() {  
        logger.info("the data is lazily bound before the real invocation of the method, check before invocation here.");  
        PreConditions.isNotEmpty(methodName);  
        PreConditions.isNotNull(target);  
    }  
  
    public IMethodDSL on(Object target) {  
        this.target = target;  
        return this;  
    }  
  
    public IMethodDSL withArgumentTypes(Class<?>... args) {  
        this.argTypes = args;  
        return this;  
    }  
  
    public IMethodDSL makeAccessible(boolean flag) {  
        this.accessible = flag;  
        return this;  
    }  
  
}  
~~~~~~~

The intermediate operations just accept the parameter value and “return this; ” to achieve method chaining, the key point is the last operation, that's, the “invoke ” method, this is where all of the real stuff take effect. We check the preconditions before invoking Java Reflection API, and then find proper Method with attribute the API user have passed in as chaining-method's parameter. At last, cast the invocation result to the type the users expect to get. That's all, very simple , isn't it?

To make API users to use this more DSL-likely, we need to offer a Factory-method for our MethodDSL, it looks like:

~~~~~~~ {.java}
public abstract class ReflectDSL {  
      
    public static IMethodDSL method(String methodName)  
    {  
        return new MethodDSL(methodName);  
    }  
    ...  
}  
~~~~~~~

With this, we can use our DSL-like Java Reflection API this way:

~~~~~~~ {.java}
import static ..ReflectionDSL.*;  
  
method("methodName")  
        .on(targetObject)  
        .withArgumentTypes(...)  
        .makeAccessable(true)  
        .invoke(...); 
~~~~~~~

Of course, some parameters are optional, like the makeAccessable() if it's a public method.

Until now, we have implement a simple DSL-like style java reflection AP for method, you can move on to provide such similar APIs for Field and Constructor and Static Method and so on.

The way we used to implement such DSL-like style API have some limitations or defects, for example, we bind intermediate method late and the final effect takes at last, that means, if users use our API in an improper way, they will not get warnings or errors until runtime. To fix this, we can use intermediate type for chaining methods, how? try it yourself and have fun ;-)

> NOTE
> 
> IMHO, this may not bring any benefits for you or your customers, it's just another way to write your code and make it more readable, make a balance to figure out whether it's proper to do it in your own situations.











