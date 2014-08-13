% Understanding Typeclass In Scala The Easy Way
% FuqiangWang

typeclass并不神秘， 本质上， 它只是某种定义了一系列行为模式的契约。 这与OO中的抽象概念是类似的，比如Java中的Interface，其实跟FP中的typeclass基本类似， 如果理解了OO中的interface，那么，理解FP中的typeclass也没有太大问题。

在FP中， 数据和行为是分开思考的，行为只是对数据的一系列转换(即体现在Function这一概念上), 如果很多数据类型可能都需要有某些共通的行为，那么就可以定义typeclass来约束这些数据类型， 是他们符合某种行为契约。 

假设我们有Integer和String两种数据类型，而我们认为这两种类型内部都应该是可以相互比较的，那么，我们可以分别为其定义相应的**可比较**行为逻辑实现：

<pre>
class Integer{
	def compare(another: Integer): Integer  = {...}
}

class String {
	def compare(another:String) : Integer = {...}
}
</pre>

但既然这种**可比较**行为是共通的，我们就可以抽取到某种类型中定义，并让想有这些行为的类型遵守这种行为契约，那么，在OO中就变成了：

<pre>
interface Comparable<T>{
	int compareTo(T o);
}

class Integer implements Comparable<Integer> {...}
class String implements Comparable<String>{...}
</pre>

这里， Comparable就是某种typeclass，用于定义可比较的行为契约。 而Integer和String则是这个typeclass的Type Instance（注意，跟对象实例-object instance相区分）， 如果后面还有其它类型也想要符合这种行为契约，那么只需要将自己实现为这个typeclass的一个Type Instance即可。

---

在Java中， Interface是体现typeclass的理想装备，在Scala中，我们则需要使用无状态，无方法实现的Trait来定义typeclass（typeclass只定义行为契约，remember？）。

所以，在Scala中， 如何定义typeclass就比较容易理解了：

1. 第一步，使用trait定义typeclass行为契约： `trait Comparable[T]{ def compareTo(o:T):Int}`
2. 第二步，定义typeclass的Type Instance，在OO里，也就是定义我们的实现类： `class String extends Comparable[String]{...}`
3. 第三步，使用typeclass声明Parameter， 使用Type Instance的实例传入Argument: `def compare[T](a:Comparable[T], b:Comparable[T]):Int`, 使用：`compare[String](1,2)`

如果还想定义其它类使之符合typeclass的行为契约，只要继承或者实现相应的trait或者interface就可以了， fucking easy, ha?

现在， 好戏上场了。

前面所有的例子都是有了typeclass声明，然后再有相应的Type Instance实现， 但是， 如果我们某些类早就已经有了，但这些类在声明的时候有没有实现我们的typeclass声明定义的行为接口，想要他们符合typeclass定义的行为契约，我们该怎么办？也就是说，木已成舟，我们总不能把这些现成的类再塞回他娘的肚子里去不是？

这个时候， Scala的Implicit Resolution特性就可以派上用场了。 

假设我们有类型A， 它之前没有实现Comarable，现在我们想要给它加上这个typeclass的约束， 使其可以被比较，那么，按照上面的第三步，我们应该这样来声明这个使用场景:

```scala
def compare(a:A, b:A)：Int
```

可是， A并没有实现Comparable，所以上面的声明显然不能工作，不过，我们可以稍微改进一下:

```scala
def compare(a:A, b:A)(implicit handler: ComparableTypeClassAdaptor[A]):Int 
```

这里的`ComparableTypeClassAdaptor[A]`可以根据传入的A的类型来提供Comparable约束的行为，比如：

```scala
class ATypeClassAdaptor extends ComparableTypeClassAdaptor[A]{
	def makeItComparable(a:A) = new Comparable[A]{def compare(o:A):Int = {实现逻辑}}
}
```

那么，只要我们在调用的时候，将相应的`ATypeClassAdaptor[A]`的实现作为implicit parameter传入就可以完成typeclass的约束:

```scala
def compare(a:A, b:A)(implicit val handler:ATypeClassAdaptor[A]) = handler.makeItComparable(a).compare(b))

implicit handler = new ATypeClassAdaptor
val a1 : A = _
val a2 : A = _
compare(a1, a2)
```

只不过，这种场景按照二八原则应该属于二的那一部分，而大部分blog post或者书上却把这东西讲得跟八那部分似的。

好啦， 关于typeclass就扯这么多，各位看官尽兴，手里有砖的也可以拍出来 ；）















