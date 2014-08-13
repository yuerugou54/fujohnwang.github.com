% Scala Pattern 之 Loan Pattern

Loan Pattern从字面上的来看，意思就是， “我 ”贷给“你 ”某样东西， 用完后还得还“我 ”。 在Scala里面，你可以使用这种模式来实现相应资源的管理和使用。这跟Spring框架里JdbcTemplate+相应Callback的实践很相似， 不过， 在表达上面， 用Scala要更简洁，明确。
因为Java7之后才能用到Closure， 所以，在此之前，Spring给出的Template Method Pattern + Callback的最佳实践， 是我们可以优雅的解决资源管理和使用的最好方式。Java7就好像这个问题的一个分水岭， 有了Java7，你会发现针对这个问题的支持变得那么资源充沛， 因为，Java7里有了Closure，我们再也不用求助于Callback接口和相应的匿名内部类了， 并且， Java7里还专门针对资源的自动管理提供了一种语法糖（Syntactic Sugar）， 使得这一问题的表达更简单， 例如：

~~~~~~~ {.java}
try (BufferedReader br = new BufferedReader(new FileReader(path)) {    
   return br.readLine();    
}  
~~~~~~~

不过， 说那么多，现在，我们还是得望“Java7 ”止渴，所以，不妨先看看Scala是如何来解决这一问题的。

如果要用Scala来解决这一问题，我们可以定义一个object如下：

~~~~~~~ {.scala}
package cn.spring21.scalapattern  
  
import java.io.Reader  
  
object LoanPattern {  
  
    def use[T <: Reader,O](resource:T)(func:T=>O){  
        try{  
            func(resource)  
        }finally{  
            resource.close  
        }  
    }  
} 
~~~~~~~

在LoanPattern对象中，我们定义了一个use方法， 它接受2个参数：

1. 第一个参数resource类型为T；
2. 第二个参数func为一个函数定义， 表明它接受一个T类型的参数，返回一个O类型的返回值；

这里有点儿“拗口 ”的就是方法定义use后面的[]中定义的内容， []用来声明参数类型， 类似于Java5之后引入的泛型(Generics)的概念， T <: Reader简单来讲，其实就是说， T类型是java.io.Reader的某种子类型， 从而， use方法定义中就可以把这个类型“贷 ”给接受T类型参数的函数， 并在函数使用完T类型的资源之后， 关闭它。

有了以上定义之后，我们就可以开始“爽 ”了， 我们可以如下的形式使用它：

~~~~~~~ {.scala}
import LoanPattern._  
import java.io._          
          
use(new BufferedReader(new FileReader("E:/MyNote.txt"))){  
    reader=>  
    var line:String = null  
    line = reader.readLine  
    while(line !=null)  
    {  
        println(line)  
        line = reader.readLine  
    }  
} 
~~~~~~~

import LoanPattern._类似于Java5的static import， 有了它，我们才可以如上的形式来使用use方法，这让它看起来像是语言固有的语法结构， 但实际上，在Scala中，它只是一种常见的对象方法定义而已。use接受2个参数， 第一个参数，我们传入了一个BufferedReader， 第二个则是一个function定义， 我们通过reader => 的形式明确指定了这个function接受的参数名称，然后就可以使用它来读取任何文件了。

> TIP
> 
> 看到这里，有人可能要骂了，这种烂代码也好意思拿出来？ 呵呵，实际上， 这里更多的是为了演示的目的， 如果只是简单的读取文件内容并打印，在Scala里面其实更简洁，也更常用的代码是：`println(scala.io.Source.fromFile("E:/MyNote.txt","UTF-8").getLines.mkString)`, 仅此一句话而已。

 
use的定义现在只能负责Reader及其子类相关的资源管理， 但我们可以扩展它，让它变得更加通用，例如：

~~~~~~~ {.scala}
def use2[T <: {def close();},O](resource:T)(func:T=>O){  
    try{  
        func(resource)  
    }finally{  
        resource.close  
    }  
}  
~~~~~~~

use2的定义比use要更进一步， 它可以接受任何定义了close()方法的类型的实例作为资源参数，比如InputStream, OutputStream, Reader, Writer等等，甚至Connection,只要这种类型定义了close()方法就行， 所以，有了它， 剩下的也只是根据情况提供相应的函数定义而已了：

~~~~~~~ {.scala}
val conn:Connection = dataSource.getConnection  
use2(conn){  
    val stat:Statement = conn.createStatement  
    use2(stat){  
        val rs:ResultSet = stat.executeQuery("...")  
        use2(rs){  
            ...// retrieve result data with rs  
        }  
    }  
} 
~~~~~~~

简单吧？强大吧？

---

Lift框架的作者给出了一条提炼Scala代码的建议，就是尽量减少代码的行数，所以， use或者use2方法可以进一步精炼一下：

~~~~~~~ {.scala}
def use[T<: Reader,O](resource:T)(func:T=>O){  
    try{func(resource)}finally{resource.close}  
} 
~~~~~~~

甚至于：

~~~~~~~ {.scala}
def use[T<: Reader,O](resource:T)(func:T=>O) = try{func(resource)}finally{resource.close} 
~~~~~~~

当然啦， 应该以尽量不影响代码的可读性为准，也不要让精简后的代码让人阅读起来很费劲为好。

---


Loan Pattern其实是很常见的Pattern， 本文主要关注如何用Scala来表达这一Pattern， 用Ruby之类支持Block/Lambda结构的动态语言当然也可以很容易的表达， 但这已经不是本文要阐述的了


































