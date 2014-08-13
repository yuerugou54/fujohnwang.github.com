% 一个简单的自定义配置格式的Scala实现

本来想执行我Object As Configuration的理念， 不过google group上有人推荐我看看configgy， 看过之后， 觉得还是自己写一个简单些， So 马上动手...

配置格式先简单定义为：

<pre>
ident = value
groupId = {
 ident1 = value1
 ident2 = value2
 ...
}
</pre>

即可以配置为key-value的形式（以=或者:分割），或者group的形式（group暂时不考虑嵌套group的形式）。

定义一个Scala的Parser combinator如下：


~~~~~~~ {.scala .numberLines}
class BlockOrItemConfigurationParser extends JavaTokenParsers {

  def entry = ((item | block) +) ^^ {
    _.foldLeft(Map[String, Any]()) {
      (accum, i) =>
        i._2 match {
          case lst: List[(String, Any)] => accum ++ lst.foldLeft(Map[String, Any]()) {
            (ac, it) =>
              ac + ((i._1 + "." + it._1) -> it._2)
          }
          case x => accum + (i._1 -> x)
        }
    }
  }

  def block = ident ~ "=" ~ "{" ~ rep(item) ~ "}" ^^ {
    case k ~ "=" ~ "{" ~ v ~ "}" => (k -> v)
  }

  def item = ident ~ ("=" | ":") ~ value <~ opt(";") ^^ {
    case k ~ _ ~ v => (k -> v)
  }

  def value = stringLit | decimalNumber | floatingPointNumber | booleanLiteral | nullLiteral

  def stringLit = "\"" ~> string <~ "\"" ^^ {
    case s => s
  }

  def string = ("""([^"\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*""").r

  def booleanLiteral = "true" ^^^ true | "false" ^^^ false

  def nullLiteral = "null" ^^^ null
}
~~~~~~~

代码写的有点儿矬， 将就看吧，呵呵， 本来用的JavaTokenParsers.stringLiteral， 但后来发现他没有去掉引号", 所以，只能自己重新定义一个， 当然，代码直接拷贝它的。

有了parser之后， 就可以定义一个Configurator，比如：

~~~~~~~ {.scala .numberLines}
class SimpleConfigurator(configFile:File) {
   val p = new BlockOrItemConfigurationParser
   p.parseAll(p.entry, new CharSeqReader(FileUtils.readFileToString(configFile))) match{
        case p.Success(r, in)=> // use r to get parsed result and feed your program, hehe 
        case x=> throw new Exception("parse error:"+x)
   }
}
~~~~~~~

OK， That's it！Have Fun!