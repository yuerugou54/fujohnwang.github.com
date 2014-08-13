% 扩展Spring的依赖注入行为两例

前阵子“袜子 ”电话里随便聊了点儿有关在Spring里面如何扩展某些行为的话题, 其实, 这些话题本身没有什么技术含量, 完全是根据使用场景来权衡罢了, “袜子 ”心里肯定也已经有数了,不过,感觉就这两个话题来说说也挺好的, 因为跟阿九这阵子的路子有些吻合, 讲简单的东西,但一定要把简单的东西讲清楚, 讲架构当然更能吸引眼球,但我一直认为“The problem is not the design, it's the implementation. ”, 所以, 我还是愿意说些很简单,很基本的东西.

# 注入以Enum作为Key的Map依赖

在现有Spring框架的默认支持下,我们可以注入单独声明的Enum类型的依赖关系, 例如:

~~~~~~~ {.java}
public enum FixtureEnum {  
    FIXTURE_ONE, FIXTURE_TWO;  
}  
              
public class Sample{  
    private FixtureEnum fOne;  
    ...  
}  
~~~~~~~


~~~~~~~ {.xml}
<bean id="target" class="...Sample">  
    <property name="fOne" value="FIXTURE_ONE"/>  
</bean> 
~~~~~~~

我们也可以注入以String或者复杂对象类型作为key的Map:

~~~~~~~ {.xml}
<bean id="target" class="...">  
    <property name="mapping">  
        <map>  
            <entry key-ref="complexObject" value="anything"/>  
            <entry key="stringvalue" value-ref="..."/>  
        </map>  
    </property>  
</bean>  
  
<bean id="complexObject" class="...">  
</bean> 
~~~~~~~

可是,把这两个结合起来, 我们要注入以Enum作为Key的Map的话,可能默认的支持就帮不了什么大忙了, 如果我们声明一个Map依赖对象, 但它的Key是Enum类型的话:

~~~~~~~ {.java}
public class InjectionTarget {  
      
    @EnumKeyType(FixtureEnum.class)  
    private Map<FixtureEnum,String> mapping;  
  
    ... // setters or getters and other things  
}  
~~~~~~~

如果单单简单的定义依赖注入关系如下:

~~~~~~~ {.xml}
<bean id="target" class="...InjectionTarget">  
    <property name="mapping">  
        <map>  
            <entry key="FIXTURE_TWO" value="FIXTURE TWO"/>  
        </map>  
    </property>  
</bean>  
~~~~~~~

恐怕最终得到的不是一个Map<FixtureEnum,String>类型的Map,而是一个Map<String,String>类型的Map, 小沈阳语:为什么那?

Java5的Generic是Erase-Based, 这就意味着,运行期间无法获得Map的Key相关的Generic类型信息, 那么, Spring在做注入的时候,也就没法知道应该将String形式表达的依赖对象转换成什么类型, 只好保持原样啦, 所以,以通常形式表达的map注入,最终得到的就成了一个Map<String,..>类型的Map,而不是Map<Enum,..>类型的Map.那谁可能说了,那怎么其它复杂对象作为Key怎么没问题那? 原因很简单嘛, 你直接指定了对象的引用嘛,不服,你把对象类型直接写上试试?

那怎么解决这个问题那? 显然我们无法在运行期间通过反射之类的途径来获得Map的Key类型了,那么,我们就明确指定呗,如何明确指定那?可以考虑几种方式...

## 自定义FactoryBean
我们可以自定义一个FactoryBean来“生产 ”以Enum类型作为Key的Map,通过该自定义FactoryBean的某个Property类指定Key的Enum类型是什么, 就可以在“生产 ”过程中生成或者转换出相应的Map实例, Spring默认提供了一个MapFactoryBean,我们可以在这个父类的基础上做进一步的工作,说白了,就是直接根据明确指定的Enum类型将已经注入的Key值做一下转换, 之后,Map的Key就从String变成了指定的Enum类型, 一个实例代码可以实现如下:

~~~~~~~ {.java}
public class EnumKeyMapFactoryBean extends MapFactoryBean {  
      
    private Class<? extends Enum<?>> enumType;  
    private EnumKeyConversionSupport conversionSupport = new EnumKeyConversionSupport();  
    @Override  
    protected Object createInstance() {  
        return conversionSupport.convert(super.createInstance(), enumType);  
    }  
  
    public void setEnumType(Class<? extends Enum<?>> enumType) {  
        this.enumType = enumType;  
    }  
  
    public Class<? extends Enum<?>> getEnumType() {  
        return enumType;  
    }  
      
} 
~~~~~~~

super.createInstance()返回的是最初的Map实例,我们通过EnumKeyConversionSupport这个类和明确指定的Enum类型进行一下转换, 就可以获得最终想要的Map实例了. EnumKeyMapFactoryBean的适用看起来如下:

~~~~~~~ {.xml}
<bean id="ekMap" class="cn.spring21.sandbox.springext.EnumKeyMapFactoryBean">  
    <property name="enumType" value="cn.spring21.sandbox.springext.FixtureEnum"/>  
    <property name="sourceMap">  
        <map>  
            <entry key="FIXTURE_ONE" value="anything"/>  
            <entry key="FIXTURE_TWO" value="anything"/>  
        </map>  
    </property>  
</bean> 
~~~~~~~

ekMap现在的Key就是我们最终想要的FixtureEnum类型.

> NOTE
> 
> 如果感觉上面的配置方式很繁琐,可以考虑自定义XML Schema类简化配置,类似于spring的util命名空间提供的简化配置形式.

## 自定义BeanPostProcessort
自定义FactoryBean的形式当然可以达成目的,不过, 使用上来看,可能不是太方便,毕竟,每次遇到这样的情况都需要声明那么一个FactoryBean的bean定义, 而且,配置的形式也不是那么简洁,本着“精益求精 ”的精神,我们是不是可以想一下,还可以有更好的方法那?
要明确指定Map的Key的类型是Enum类型,不一定非要通过XML配置的形式,我们还可以使用Annotation,通过为相应的Map标注某一表明了Key的Enum类型的Annotation, 我们同样可以获得Key的Enum类型信息,例如,我们可以声明某一Annotation如下:

~~~~~~~ {.java}
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface EnumKeyType {  
    Class<?> value();  
} 
~~~~~~~

然后在遇到适用Enum作为Key的Map的情况下,就可以通过这一Annotation对这样的Map进行标注:

~~~~~~~ {.java}
public class InjectionTarget {  
      
    @EnumKeyType(FixtureEnum.class)  
    private Map<FixtureEnum,String> mapping;  
  
    ...   
} 
~~~~~~~

这样,虽然我们无法在运行期间获得Map的Key的Generic类型信息,但可以通过Annotation来获得,不过, 光标注一下,Spring可不会聪明到马上知道你标注这么个Annotation要干嘛,我们得写点儿东西让Spring知道遇到这个 Annotation该干点儿什么事情, 所以,可以定义一个BeanPostProcessor来做这个事情,例如:

~~~~~~~ {.java}
public class EnumKeyMapBeanPostProcessor implements BeanPostProcessor {  
  
    protected static final transient Logger logger = LoggerFactory.getLogger(EnumKeyMapBeanPostProcessor.class);  
      
    private EnumKeyConversionSupport conversionSupport = new EnumKeyConversionSupport();  
      
    @Override  
    public Object postProcessAfterInitialization(Object bean, String beanName)  
            throws BeansException {  
        Field[] fields = bean.getClass().getDeclaredFields();  
        for(Field field:fields)  
        {  
            if(field.isAnnotationPresent(EnumKeyType.class))  
            {  
                try {  
                    convertKeyType(field,bean);  
                } catch (Exception e) {  
                    logger.warn("failed to do map key convert.\n{}",e);  
                }  
            }  
        }  
        return bean;  
    }  
  
    protected void convertKeyType(Field field,Object bean) throws Exception {  
        EnumKeyType eType= field.getAnnotation(EnumKeyType.class);  
        Class<?> clazz = eType.value();  
        field.setAccessible(true);  
        Object map = field.get(bean);  
        if(Map.class.isAssignableFrom(map.getClass()) && clazz != null)  
        {  
            Map<Object, Object> result = conversionSupport.convert(map, clazz);  
            field.set(bean, result);  
        }  
    }  
  
    @Override  
    public Object postProcessBeforeInitialization(Object bean, String beanName)  
            throws BeansException {  
        return bean;  
    }  
  
}  
~~~~~~~

只要将这个EnumKeyMapBeanPostProcessor注册到Spring的ApplicationContext, 那么,之后要注入以Enum作为Key的Map的时候,只要简单的使用EnumKeyType标注一下这些Map就可以了,一劳多得, 如果应用中有多处需要这样的Map注入,使用这种方式显然要比适用自定义的FactoryBean要省事不少.

## 自定义PropertyEditor?
我们知道, Spring内部在做类型转换的时候,会使用一些默认注册的PropertyEditor来做类型转换,而且,也允许我们注册自定义的PropertyEditor, 那么, 自然而然的,我们会想到提供一个针对这种情况的自定义PropertyEditor实现,那么,是否可行那? 如果感兴趣的话, 你可以试一下,呵呵

# 注入容器中某一类型所有依赖对象
默认情况下,我们可以通过Spring的XML配置文件中的<list>或者<set>等元素为某一个对象注入一组依赖对象,只要我们能够确定容器中的哪些bean定义应该纳入这组依赖对象就行,例如:

~~~~~~~ {.java}
public class InjectionTarget {  
      
    private List<T> collection;  
    ...  
} 
~~~~~~~


~~~~~~~ {.xml}
<bean id="it" class="...InjectionTarget">  
    <property name="collection">  
        <list>  
            <ref bean="t1"/>  
            <ref bean="t2"/>  
            ...  
        </list>  
    </property>  
</bean>  
  
<bean id="t1" class="..."/>  
<bean id="t2" class="..."/>  
...  
~~~~~~~

可是,大部分情况下,list里面都是同一类型的依赖对象(你要混合元素类型,那是你的事情),每次添加一个这样类型的依赖对象,就需要配置文件里添加一 个bean定义,然后<list>处改一下,很是烦躁,是吧? 我们可以通过某些方式来简化这种场景下的配置或者消除它,例如...

## 自定义FactoryBean
我们可以自定义一个FactoryBean,让它替我们自动去容器里查找指定类型的一组依赖对象,然后,我们只要把这个FactoryBean挂接到依赖这组依赖对象的bean定义上就行了. 要让自定义的FactoryBean能够查找容器中指定类型的对象,我们可以让它实现ApplicationContextAware接口(这个接口能做啥事儿我就不多说了):


~~~~~~~ {.java}
public class CollectionInjectionFactoryBean implements FactoryBean,ApplicationContextAware {  
  
    private ApplicationContext applicationContext;  
    private Class<?> componentType;  
      
    @Override  
    public Object getObject() throws Exception {  
        @SuppressWarnings("unchecked")  
        Map<String,Object> map = this.applicationContext.getBeansOfType(getComponentType());  
        if(map == null || map.isEmpty())  
        {  
            return Collections.EMPTY_LIST;  
        }  
        return map.values();  
    }  
    @SuppressWarnings("unchecked")  
    @Override  
    public Class getObjectType() {  
        return Collection.class;  
    }  
  
    @Override  
    public boolean isSingleton() {  
        return false;  
    }  
  
    @Override  
    public void setApplicationContext(ApplicationContext arg0)  
            throws BeansException {  
        this.applicationContext = arg0;   
    }  
  
    public void setComponentType(Class<?> componentType) {  
        this.componentType = componentType;  
    }  
  
    public Class<?> getComponentType() {  
        return componentType;  
    }  
  
} 
~~~~~~~

有了它之后,如果你想为某个对象注入一族A类型的依赖对象,那么就定义一个CollectionInjectionFactoryBean,并指定它的componentType为A; 如果想注入一族B类型的依赖对象,就指定它的componentType为B,依此类推.例如:

~~~~~~~ {.xml}
<bean id="target" ..>  
    <property name=".." ref="collectionInjectionFB"/>  
</bean>  
          
<bean id="collectionInjectionFB" class="cn.spring21.sandbox.springext.CollectionInjectionFactoryBean">  
    <property name="componentType" value="...AType"/>  
</bean>  
~~~~~~~

如果应用里这种场景不多,那使用这种自定义FactoryBean的方式还可以将就一下,但多的话,那也依然减少不了多少配置,这个时候,可以考虑下面这种方式.

## 自定义BeanPostProcessor
如果可能,开发人员肯定不愿在java代码与配置文件之间切换,最好是只关注Java代码文件,这也就是为啥Annotation很受开发人员欢迎的原因之一. 所以,如果某个对象的属性需要注入一组依赖对象,那么,最好的方式就是直接在Java代码中直接标注这种依赖关系,鉴于此,我们定义一用于此目的的Annotation如下:

~~~~~~~ {.java}
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface InjectCollectionOf {  
    Class<?> value();  
}  
~~~~~~~

有了该Annotation之后,我们就可以在对象中需要注入一组依赖对象的Property处标注该Annotation:

~~~~~~~ {.java}
public class InjectionTarget {  
      
    @InjectCollectionOf(SomeType.class)  
    private Collection<SomeType> collection;  
    ...  
}  
~~~~~~~

为了让容器按照我们的旨意行事,我们最后需要提供一个自定义的BeanPostProcessor实现,如下所示:

~~~~~~~ {.java}
public class CollectionInjectionBeanPostProcessor implements BeanPostProcessor,ApplicationContextAware {  
  
    private static final Logger logger = LoggerFactory.getLogger(CollectionInjectionBeanPostProcessor.class);  
      
    private ApplicationContext applicationContext;  
      
    @Override  
    public Object postProcessAfterInitialization(Object bean, String beanName)  
            throws BeansException {  
          
        Field[] fields = bean.getClass().getDeclaredFields();  
        for(Field field:fields)  
        {  
            if(field.isAnnotationPresent(InjectCollectionOf.class))  
            {  
                Class<?> componentType = field.getAnnotation(InjectCollectionOf.class).value();  
                if(componentType == null)  
                {  
                    continue;  
                }  
                @SuppressWarnings("unchecked")  
                Map<String,Object> componentCandidates = this.applicationContext.getBeansOfType(componentType);  
                if(componentCandidates != null && !componentCandidates.isEmpty()){  
                    field.setAccessible(true);  
                    try {  
                        field.set(bean,componentCandidates.values());  
                    } catch (IllegalArgumentException e) {  
                        logger.warn("argument is not a collection.\n{}",ExceptionUtils.getFullStackTrace(e));  
                    } catch (IllegalAccessException e) {  
                        logger.warn(ExceptionUtils.getFullStackTrace(e));  
                    }  
                }  
            }  
        }  
        return bean;  
    }  
  
    @Override  
    public Object postProcessBeforeInitialization(Object arg0, String arg1)  
            throws BeansException {  
        return arg0;  
    }  
  
    @Override  
    public void setApplicationContext(ApplicationContext arg0)  
            throws BeansException {  
        this.applicationContext = arg0;  
    }  
  
}  
~~~~~~~

实现原理上跟自定义的FactoryBean差不多,无非就是多了Annotation检测相关逻辑, 最后,只要将这个自定义的BeanPostProcessor注册到容器, 所有标注了@InjectCollectionOf的Property就可以被正确的注入了:

~~~~~~~ {.xml}
<bean id="target" class="..InjectionTarget">  
    ...  
</bean>  
  
<bean class="...CollectionInjectionBeanPostProcessor"/>  
~~~~~~~

如果需要针对Collection的明确子类型的类似注入需求, 依葫芦画瓢就可以了.

# 结束语
无论是设计还是实现,都是在各种因素之间进行权衡, 没有普遍适用的设计方案,也没有普遍适用的实现方案, 因时因地而权衡吧! 经济学第一原则不是“People face tradeoffs ”嘛, 其实哪里都一样.










