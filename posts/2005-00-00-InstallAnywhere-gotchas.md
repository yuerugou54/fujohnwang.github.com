% 有关InstallAnyWhere的几个问题
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

 因为finance项目需要为end-user提供安装程序，所以，需要对日方提出的InstallAnyWhere方案进行investigate，下面是一些情况。
 
ZeroG公司的InstallAnyWhere实际上都是商业产品，现在他的InstallAnyWhere有4个版本：Enterprise Edition (v6.1)，Standard Edition (v6.1)，Mac OS X Edition 和Now! (v5.5) 。在这几个版本中，现在只有InstallAnyWhere5.5Now！是free的，而且没有installer的期限限制，其他几个版本的trial版本虽然都不限制使用期限，但是用这些trial版本编译后的installer都只能有3天的期限，如果编译完三天后依然要用，必须重新编译installer。

虽然Now！(v5.5)没有installer的期限限制，但是因为他是free的而且ZeroG可是不想Now！来冲淡其他三个版本的市场，所以，Now！所能提供的安装程序制作功能是十分有限的，在Add Actions面板中，只有几个可以的安装面板，仅仅可以提供最基础最简单的安装程序制作，而且Rules也只是提供了Check platform一个。这些都不算，只要能够制作一个可以运行的installer就是我们所需要的，现在最大的问题就是，因为最终的finance产品是面向日本的，所以最终编译成的installer必须面向日本客户，也就是页面信息必须是日本语，但是现在Now!(v5.5)版本并不提供Locale功能，这个功能只在enterprise和standard版本中提供，所以现在编译成的installer面板上的所有信息都是英文提示的，这很明显不能满足需求。那这个问题如何解决那？因为Now！并不提供add locale的action，所以，不可能在Advanced Editor指定该Action来改变默认的locale信息。但是，我们也不可能将InstallAnyWhere的全部类文件进行反编译然后修改后再重新编译以改变现有InstallAnyWhere的功能限制。

不过，通过search InstallAnyWhere5.5Now！的安装目录以及文档中提供的一些零散的信息，我最终找到不算很正大光明的解决方法，呵呵，或许这就是中国人的小聪明吧！我的InstallAnyWhere安装在D:\InstallAnyWhere5.5Now目录下面，在他的子目录D:\InstallAnyWhere5.5Now\resource\i18nresources下面，你可以看到许多locale的信息文件，分别按照ISO标准后缀提供。现在，既然Now！默认的编译使用的是custom_en的resourceBundle，那么我把这个ResourceBundle删除是不是就会逼迫Now！编译器编译的时候使用现在locale下的resourcebundle那？但是，结果并非如此，按钮等组件的提示信息不是expected的中文，而是什么也没有，而且按钮变形。那么我又试着将custom_ja的ResourceBundle改名为custom_en，编译运行后，ok，现在安装程序的信息都是日文的了，大功告成！，其实，刚开始的时候，因为默认的installer的背景图片等都是不变的，所以也通过寻找该图片并替换而将需要的图片换上。

另外，刚开始的时候，Now！在进行installer的打包过程中，可以指定他生成附带VM和不附带VM的installer，但是，单独运行或者web运行附带VM的installer的时候，都会提示GUI错误，并且中止installer的运行。实际上，在通读了InstallAnyWhere的文档后，就会发现与上面的locale问题类似，Now！因为已经将某些功能限制了，所以，要对某些地方进行调整以适应实际上的使用。通过去InstallAnyWhere的官方网站下载合适的Add-ons,在这里是vm 的pack，将他们下载后放入D:\InstallAnyWhere5.5Now\resource\installer_vms目录并重新启动installanywhere就可以解决这个问题，之所以如此是因为Now！只是自己附带了一个1。3的jre，并且还是不提供I18N的，只要现在一个支持I18N的jre就可以了。

以上是在installAnyWhere5.5Now！的investigation过程中需要解决的两个主要问题，其他问题不是很难解决而且，如果不需要太高级的安装feature，Now！所提供的几个Actions就够用了，不需要太多的时间就可以制作出需要的installer。

OK，就此打住


----------------------------------------------


**InstallAnyWhere5.5Now!之后话**

上回话说道Now！版本的一些问题，以及最大限度的在其限制之上扩展所需要的功能，而且，今天也确实将finance打包为可以安装的installer，并且在自己机器上可以安装后正常运行，但是现在又有了新的问题，同样是因为Now！版本的limitation所引起的。


因为现在的后台管理系统使用的是SWT开发，所以，运行的时候需要本地运行库的支持，所以，制作安装程序的时候需要为每一个end-user安装本地运行库，问题就出在Now！版本不提供Custom code功能，你不能够通过定制扩展他的安装过程，也就是说，现在所有的安装文件全都安装到指定位置的一个主目录下面，而不能在安装的同时指定某些文件根据实际情况安装到其他的指定目录下，这恰恰就将SWT运行的dll连接库的安装孤立起来。虽然针对这个情况探索了几条路径来解决这个问题，但是到目前为止依然无法解决，虽然ZeroG的网站提供了所需要的Add-ons，但是不能在Now！版本中使用。

针对以上情况，如果说需要对产品的安装进行更多自定义的控制的话，
Now!版本的限制是他很难满足现有商品的安装制作，除非产品的安装不需要太多安装的控制，否则，建议其他方式，比如购买Enterprise版本或者Standard版本，或者考虑其他开源的installer产品。

现在的情况是，安装制作后的installer可以在现在的机器上正常安装，安装后的程序也可以正常运行，但，发布到其他相对swt的裸机上，不能保证安装后的程序可以正常运行。

以上