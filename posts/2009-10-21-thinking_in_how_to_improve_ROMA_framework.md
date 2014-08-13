% ROMA框架潜在改进点思考
% fujohnwang
% 2009-10-21
__author: fujohnwang__

以下只是个人对ROMA框架现有条件下可能存在的一些需要改进的点的看法， 不代表任何褒贬之意。

# 关于ROMA现有表单处理方式是否合理的思考
对于表单的处理来说， 细想一下， 其实，百分之八九十的场景通常是， 首先显示表单给用户， 然后当用户提交表单之后，再处理之前显示的表单， 虽然显示表单和处理提交表单是两个请求， 但它们实际上可以看作是一个整体， 很少有只需要单独处理表单提交或者表单显示的场景。所以， SpringMVC最初版本中提供的SimpleFormController实际上可以看作是表单处理的典范之作。就连RodJohnson本人也曾经提过， 在推出了基于Annotation的Controller之后， 之前基于接口继承关系的Controller形式不再提倡使用，但有一个例外，那就是SimpleFormController。

有些时候，松散耦合是好的东西，但有些时候， 内聚的形式却更优。没有哪一种形式在所有场景下都是最好的。对于表单的处理来说，现在ROMA中松散的处理结构就存在值得商榷的地方。 现在的问题就是，虽然你可以自由的编写controller的逻辑，但却失去了合理约束的好处。因为现在， 你既没有获得继承层次上的重用，也没获取组合方式的重用， 所有的东西都是从头来做。 如果能够给你的Controller施加合理而有益的约束， 又不失测试，开发等方面的灵活性，那这样的约束是可以接受的，甚至是应该受到欢迎的。

在表单处理的过程中， 应该将显示表单页面的信息， 表单提交成功后转向的页面信息， 数据绑定背后对应的数据对象的信息等，设置为整个过程的属性，而不是单一方法的属性。因为这些属性是整个过程中都要统一用到的，而不单单是表单显示阶段或 者表单提交阶段独自使用到的。 打个比方， 假设显示表单页面对应的url为showForm.html， 这一信息不单会在用户请求表单页面显示的时候用到， 而且，在提交后，表单处理阶段出现数据绑定或者数据验证错误的时候，也会用到，这个时候，你通常会将错误信息显示在之前的页面上，以表示表单提交失败，需 要用户修正错误然后重新提交。 而如果表单处理的两个阶段打散到对应不同controller方法或者url来对应，那这样的信息就会重复存在，从而给开发人员带来繁琐之感。

总之，个人看法就是，在处理表单这一场景下，将共同的逻辑內聚到共同类，然后让开发人员在实现表单提交的时候，直接扩展这一共同类，并实现小部分特定的逻辑即可。 合适的场景，选用合适的装备，仅此而已，没有普适的原则。

# 关于按照COC的理念，现有视图目录组织结构是否合理的思考
现有的视图目录结构类似于：

<pre>
Java代码  
/WEB-INF/  
    views/  
        namespace/  
            view/  
                controller/  
                    *.vm  
            widget/  
                controller/  
                    *.vm          
 </pre>
 
这样的组织结构考虑到了namespace下view和widget的紧密关系， 但忽视了ROMA核心理念中COC倡导的namespace/controller/action这样的层进结构在视图组织中的一致性。 也就是说， 现在的视图目录组织结构只是考虑了并列的横向问题， 但忽视了直线序列上的竖向问题。

鉴于现在的目录组织结构namespace下混杂view，widget以及controller path等因素的现状， 我建议将view, widget, layout的概念并列化，而将namspace/controller/action的概念序列化，内敛入并列化之后的view, widget和layout的概念中， 从而有了如下的目录结构组织方式：

<pre>
Java代码  
/WEB-INF/  
    views/  
    widgets/  
    layouts/  
</pre>
 
其中， views, widgets和layouts目录下，按照namespace/controller/action的顺序持有相同的目录结构序列，从而在更高层次上保持了namespace下view, widget和layout的并列关系， 例如：

<pre>
Java代码  
/WEB-INF/  
    views/  
        namespace1/  
            controller1/  
                *.vm  
    widgets/  
        namespace1/  
            controller1/  
                *.vm  
    layouts/  
        namespace1/  
            controller1/  
                *.vm  
</pre> 

这样的好处在于， 对于大部分时间都只关注views的开发着来说，现在的namespace下直接找controller对应的目录也比较直观，不再有那种中间有一个“ view ”目录的隔阂感。 而且，可以通过相应工具提供的某种视图形式来merge这种目录结构的显示，比如， 展示成原来那种namespace下紧密的view, widget, layout形式。

# 关于现有ROMA框架中声明式数据验证的思考
实际上， 现有的声明式数据验证数据文件的格式问题， 在框架的设计者和许多开发人员当中早就不是什么秘密了。只不过考虑到兼容性问题，所以这个问题一托再托。 不过，既然问题存在，那就有必要分析一下，以期改进也好， 接受教训也好。

首先来看一下现有ROMA框架中声明式数据验证文件的常见格式：
<pre>
Xml代码  
&lt;form&gt;   
     &lt; group   name = "groupToUse" >   
         &lt; group   name = "subGroup"   ref = "subGroup" />   
     &lt; group >   
     &lt; group   name = "subGroup" >   
         &lt; field  ... > </ field >   
         &lt; field  ... > </ field >   
         &lt; group   name = "subsub" >   
             &lt; field  ... > </ field >   
         &lt;/ group >   
     &lt;/ group >   
&lt;/ form >   
</pre>
 
最初的考虑可能是为了能够把对象的继承和嵌套结构映射到这种基于group的定义规则中，因为可以想象， 每一个表单数据提交后，通常在服务器端都会有相应的数据对象与之对应。 初衷是好的， 出发点也是对的，但在操作的过程中，走错一步，然后就让后面“ 满盘皆罗嗦 ”了。

实际上， 要完成这一设想， 对应的数据验证文件格式大体可以罗列为如下形式：
<pre>
Xml代码  
&lt; forms >   
     &lt; form   name = "groupToUse" >   
         &lt; group   name = "subGroup"   ref = "subGroup" />   
     &lt; form >   
     &lt; group   name = "subGroup" >   
         &lt; field  ... > </ field >   
         &lt; field  ... > </ field >   
         &lt; group   name = "subsub" >   
             &lt; field  ... > </ field >   
         &lt;/ group >   
     &lt;/ group >   
&lt;/ forms >   
</pre> 
也就是说，第一层对应类似spring中commandBean概念的，应该是一个form，如果对应form的commandBean有进一步的属性定 义，并且是复杂对象类型，这时候才使用内嵌的group元素来定义进一步的验证规则，如果简单属性，则直接在form元素下声明field即可。 而原来将本质上为form的元素声明为group， 不但犯了本质上的错误， 更使得后期的演化越加举步维艰。而“ 增加顶层group支持 ”的需求，实际上就是为了解决group＋group无法区分或者无法很好的表达form+group这一的逻辑结构的问题。 这就是为什么在实现“ 增加顶层group支持 ”的需求的时候，我暂时为group元素添加了一个asForm属性，用于合理区分作为form的元素和作为group的元素。

以上问题现暂时告段落，下面谈一下操作和使用层面的些许个人看法。

在ROMA中，使用现有的数据验证支持，你需要维护三个位置的信息：

controller定义中方法上标注的@Form Annotation的group属性名称；

forms.xml中group的数据验证规则定义；

显示表单的试图文件中绑定group的信息；

对于开发者来说，多少应该感到不爽。毕竟， 维护两个位置还可接受，三个位置就有些多了， 而且， 1－1的映射容易理解， 同时考虑1－1－1的映射就有些烦人了。虽然基于forms.xml的方案有其自身的背景和出发点， 但如果能够兼顾考虑开发人员的感受，或许更好。

要简化这一繁琐， 实际上存在一个尴尬的地方， 如果showForm和submitForm的处理是作为一个整体来走的话， 使用那一组验证规则只需要在对象级别指定一次就好了， 这样， 就可以省去试图中group名称的同步， 因为，在showForm的时候，可以将对象级别的元数据信息提取并传递给试图，而不用像现在松散结构下，需要人工明确指定来关联。

对于这个问题来说， 验证的元数据是放在独立的forms.xml中，还是放在Annotation中， 无关本质， 唯一的痛处，可能还是把表单的处理给打散了，导致能统一处理的地方不能统一，只能分散并重复。

# 关于URIBroker的思考
URIBroker的初衷是为了避免因为页面内链接的变更导致大批相关页面都要变动的局面。实际上， URIBroker起到一个隔离层的功效。从这一点来说， URIBroker功不可没，不过， 从个人角度来看，如果能够稍微改进一些， 则可以让这把小刀子更亮，更快。

先来看现在URIBroker的使用场景。 在一个视图模板中，因为uribroker已经成为vm的内嵌对象，所以，为了表达一个链接，我们通常这么写：

Java代码  

```java
$uribroker.subscription.subcontroller.setTarget( "search.htm" )  
```
 
当视图渲染完成后， 以上链接地址就可能变为“ http://host:port/contextpath/subscription/subcontroller/search.htm ”， 因为URIBroker会从一个外部的配置文件uri.xml中读取配置信息，形如：

<pre>
Xml代码  
&lt; uri-config >     
      
     &lt; uri   name = "rootContext" >   
         &lt; contextPath > / </ contextPath >   
     &lt;/ uri >   
    ...  
      
     &lt; uri   name = "subscription"   extends = "rootContext" >   
         &lt; namespace > /subscription </ namespace >   
     &lt;/ uri >   
     &lt; uri   name = "subcontroller"   extends = "subscription" >   
         &lt; componentPath > /subcontroller </ componentPath >   
     &lt;/ uri >   
    ...  
&lt;/ uri-config >   
</pre>
 
当路径需要变更的时候， 只修改uri.xml文件一个地方，所以使用$uribroker生成链接地址的视图模板都会得到统一的更新。

可是对照一下使用$uribroker表述的路径，和uri.xml中表述的内容，很容易发现，许多信息都是重复出现的， 如果按照COC的理念， 这些相同的信息完全可以推导出来，而不用明确再去配置一遍， 理想的情况是，只有路径信息和$uribroker所表述的路径不一致的时候，明确在uri.xml中配置这种路径信息才是合理而有效的。

那么， 是不是可以这样那？！ 当$uribroker生成路径信息的时候， 首先去检查uri.xml中有没有对应当前路径的配置项，如果有，则使用之，如果没有，则把$uribroker中指定的路径作为最终输出。 这样，就可以极大减少配置量，降低开发人员的负担。实现过程中是否有困难，这里不做考虑，从为开发人员考虑，和提高生产率的角度来说，个人认为，这样的需求是合理的。
