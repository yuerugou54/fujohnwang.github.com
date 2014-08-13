% 将tools.jar添加到sbt编译期classpath中
% fujohnwang
% 2012-07-31
__author: fujohnwang__

不是啥太有技术含量的问题，不过也纠缠的挺久，所以share一下吧，或许很有同路人那 

项目使用SBT编译， 使用了Java6之后才有的Attach API功能， 原来在Mac下编译，运行都没事儿，虽然挑换Java6跟Java7的JDK顺序的时候也出现过编译失败，说找不到com.sun.tools.attach._的情况，但没当回事儿，直接又调回到Java6了事。

今天为了协助一个TeamMember调研一个诡异的问题，让他在linux下重新编译项目，发现又报这个错误信息，才决定好好调教一下它。

第一步，将tools.jar通过-cp添加到sbt的启动脚本，failed；

第二步，直接写死本地路径的tools.jar到unmanagedJars， 搞定：


```scala
unmanagedJars in Compile += Attributed.blank(file("/usr/xxxx/../java/lib/tools.jar"))
```



打完收功？！ NO， 那不是洒家的风格！

第二步虽然可以跑了，但只是权宜之计， 有个明显的不足之处，即路径是写死的，换一个环境编译显然这段编译设置需要对应修改，不够独立，所以，老衲绞尽脑汁，疯查资料，反复实验，终究获得了一个令自己满意的结果…

# 第一版

```scala
unmanagedJars in Compile += Attributed.blank(file(System.getProperty("java.home")) / "lib" / "tools.jar")
```


其实那， 刚开始是想通过javaHome这个SettingKey来转换并获取编译期间使用的JAVA_HOME，但是，使用javaHome的问题就是，它是SettingKey[Option[File]]类型，Option的意思那就是，除非你明确设置了javaHome，否则它会是None，而一般情况下，我们不会去设置它，所以，用这东西不够周全，返璞归真那，咱就先用最土的方法搞定一个能work的，而且build environment independent的版本！

# 第二版

```scala
unmanagedJars in Compile <+= (javaHome) map (jh=> Attributed.blank(jh.getOrElse(file(System.getProperty("java.home"))) / "lib" / "tools.jar"))
```



复杂的东西其实并不复杂，通常都是由简单的东西组合而来（这话有哲理性，可我说的太挫了，看官见谅）。
第一版总算能independent了，可还有个问题，如果我们真的明确指定了javaHome这个设置并override了默认的编译用JDK，那第一版好像就不够严谨了，鉴于此，老衲再接再厉，终于搞出了第二版能work，又build environment independent的编译配置。

嗯，基本上，还算满意，收工！


