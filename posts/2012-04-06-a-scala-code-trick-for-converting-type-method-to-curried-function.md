% "A Scala Code Trick for Converting Type-Method to Curried Function"
% fujohnwang
% 2012-04-06

<pre>
scala> case class A(x:Int){ def f(y:Int)=x*y}
defined class A

scala> val af = A(2).f _
af: (Int) => Int = &lt;function1>

scala> af(3)
res0: Int = 6

scala> val af = (_: A).f _
af: (A) => (Int) => Int = &lt;function1>

scala> af(A(2))(3)
res4: Int = 6

</pre>

**TWO Points to pay attention to**

1. (_:A)  // any instance of Type A?!
2. .f _    // convert method to function, this I knew before ^_^

I Think This trick should be usedsparingly~
