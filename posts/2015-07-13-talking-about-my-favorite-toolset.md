% 说说我日常把玩的小工具
% FuqiangWang - fujohnwang AT gmail DOTA com
% 2015-07-13

> 工欲善其事， 必先利其器

今天看到[这篇文章](http://liqi.io/jianghong/)，受其启发，遂也晒一下自己在[挖财](http://wwww.wacai.com)之后日常使用的一些小工具， 希望可以给大家一些借鉴...

# 硬件

## 手机

iphone5s， 没办法，哥当年SB啊， 买的合约机， 还tmd得用够三年， SB的一塌糊涂， 不要学我， 不过反正时间也差不多了， 等6s出来好了...

## Macbook Air 11寸

对于我现在的工作来说， 11寸的air可以很好的满足需求， 轻便性是我现在追求的主要特性， 尤其是现在很多是文案性的工作。

不过年前也用过很长时间的黑苹果， 8核16G用起来还是很爽的， 尤其是写代码的时候， 完全无卡顿。 现在Java程序一开多了，还是会怀念这台黑苹果。

## 显示器

我使用的是29寸的AOC宽屏显示器， 通常用来写Scala代码的时候外接air作为triggered execution的实时输出，或者画架构图的时候使用， 因为我认为大屏幕可以不限制我的思维宽度，总之， 还是很爽的。


## 键盘

我使用的也是Matias Mini Tactile Pro这款有线的Mac专用机械键盘，一开始买的时候感觉审美上还没有perfectly满足哥的胃口， 但日常敲打起来，手感和感受还是不错的，当然， 黑苹果转让之后， 使用这款键盘的机会就少了， 现在更多还是使用air的自带键盘。

## 卡片机

不是单反啦， 类似树莓派的卡片电脑， 不过我的是pcduino， 现在主要作为我的private git server， 作为一些非公开资料的备份机， 当然， 也会通过它分享一些面向公司内部的资料， 比走github pages快得多。

## mini PC

买了一款Mr. NUC的mini pc， 主要是用来跑windows系统， 因为有些时候工作上的东西对windows平台的绑定太强了， 而且很多软件也是windows版本更好，甚至只有windows版本。

当然， 这里还有一个黑暗的原因， 哥偶尔毒瘾犯了，需要打打DOTA解解闷 ;)

# 软件

## IntelliJ IDEA

写Java代码的不二之选， 自从抛弃Eclipse投入IDEA的怀抱就从来没有想过要回去。而且， 让我更加喜爱有加的是， 相对于其它IDE， IDEA的Scala插件和相关支持（比如SBT）我认为都是最好的（邓大叔的Netbeans插件也不错，但很遗憾偶不用Netbeans）， 所以， 对于写代码来说， 哥现在是IDEA的死忠。

## Mou

现在写代码少了，写文档多了， 架构规范， 邮件沟通， 周报， 介绍性文案等等， md ^[请允许我自我陶醉的认为你能够理解这里两个字母的双关性]， 使用Mou绝对是最爽的。

## yWorks yed

Windows下有visio， Mac下其实有OmniGraffle, 不过， 后者实在tnd太贵了， 某一段时间又特别装逼要用正版， 就只好找其它的alternatives， 辗转反侧良久， 最终定下来使用这款yed， 一开始也老jb不爽了， 后台静下心来打理和调整， 现在使用起来得心应手， 让我再回头去用OmniGraffle之类很牛逼的软件，反而不如现在使用yed效率高， 瞧，下面这副架构图就是俺使用yed画的：

![](images/configuration-and-target-environments.png)


## Chrome插件

现在基本上是使用Chrome作为主要浏览器， Safari哥只有要登录中银的时候才会偶尔开一下。

Chrome哥常用的插件有：

1. 红杏， [刀哥](http://hongjiang.info)介绍的， 做什么用你懂的哼
2. Evernote Web Clipper， 有些看到不错的文字，但又不想自己归档的，就直接用它来切了；（说实话，基本不会再去看）
3. [goqr](https://github.com/fujohnwang/goqr), 自己写的一个二维码生成的插件， 算是吃自己的狗食吧。
4. OneTab, Chrome开很多tab的时候内存消耗太大， 有些又看不过来的时候，就先onetab一下， 等一个topic相关的tabs过的差不多了，再重新点开；
5. Markdown Here， 现在用的少了， 原来使用web mail的时候， 使用markdown语法写完邮件，只要点击一下这个插件，马上华丽的生成HTML格式的邮件；

## Dash

花了银子的， 但花得绝对值， 从此查文档不用非得联网，要加快码字速度也不用非得再安装一个TextExpander之类的软件， Dash集文档管理和snippet管理于一身， 一器在手，码字，码代码均无忧。

## 其它小工具

1. ngrok，一个做代理的小软件， 像调试和开发微信公众号之类的功能， 再用不用为了一个域名和服务器而发愁了；
2. pandoc， 哥严重喜欢的一款markdown编译器，绝对是haskell语言写出来的杀手级工具， 偶托管在github pages上的个人博客<http://afoo.me>就是基于pandoc静态编译出来的！
3. speedtest_cli.py， 一个网速测试小工具
4. bfg， 与其去搞清楚`git filter-branch`那纷杂的用法， 还不如使用bfg一个简单的命令搞定： `bfg --delete-files ${欲删除的文件名pattern}  my-repo.git`
5. plantuml, 其它图都使用yed搞定了， 只有sequence图有点儿不太好画， 所以， 选来选去找到这个小东东，bravo~

# 小结

工具， 对于我来说只是提高工作效率和工作情趣一个因素， 一个理想的工作环境就更好了：一条又长又宽的设计简约的木桌， 一把舒适的椅子， 加上安静少人打扰的空间， 要求不高，但这就是我要的理想工作环境，你的理想工作环境又是什么呢？

