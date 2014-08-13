% "twitter_util_eval_is_cool"
% fujohnwang
% 2012-03-28

You define a scala trait:


```scala
trait ServerConfig extends (() => Server) {
  var port: Int = 9999
  var timeout: Option[Duration] = None

  def apply(): Server = new Server(port, timeout, ...)
}
```



You define a new implementation for the trait in a scala source code file which will be used as configuration file:


```scala
new ServerConfig {
  port = 12345
  timeout = 250.milliseconds
}
```

Then you can use Eval to bind your configuration and bootstrap together:


```scala
  val eval = new Eval()
  val config: ServerConfig = eval[ServerConfig](new File("..."))
  val server: Server = config()
  server.start()
```


Now it's done!

Neat and cool, typesafe-proof configuration with scala as configuration DSL! Don't you like it?! ^_^

__References__

1. [Why Config?](http://robey.lag.net//2012/03/26/why-config.html)
2. <https://github.com/twitter/util>
