% pandoc中文pdf转换攻略
% 王福强

按照pandoc官方网站说明，下载[BasicTex](http://www.tug.org/mactex/morepackages.html)以及MacTeX-Additions， 不管三七二十一，一并装了，才占用几百M，在这个年月，"洒洒水"啦~

装完后，运行`pandoc --latex-engine=xelatex -V mainfont=Hei 统一投放平台规划与设想.md -o tek.pdf`, 发现tnd不给换行，太长的行直接截断，好啦，第一个症状出现了...

遂Google之， 发现了这篇<https://groups.google.com/forum/#!topic/pandoc-discuss/GIy4crum8II>, 但貌似还是没有答案，也不知道纠结了多久，在pandoc官网导航栏里，点了[[Extras](https://github.com/jgm/pandoc/wiki/Pandoc-Extras)], 寻摸， 突然发现接近页尾的地方有中文翻译的pandoc文档项目，有转换时用的命令行说明以及其他几个文档，点进<https://github.com/tzengyuxio/pages/blob/gh-pages/pandoc/convert.sh>,发现如下命令赫然在目:

```bash
pandoc pandoc.markdown -o pandoc-zhtw.pdf --toc --smart --template=pm-template --latex-engine=xelatex -V mainfont='LiHei Pro'
```

哈哈， 太爽了， 马上`git clone`这个项目到本地，查看输出的pdf，发现格式完全没有问题，我直接把模板拿过来用不就搞定了嘛，so:

```bash
pandoc --latex-engine=xelatex -V mainfont='LiHei Pro' --template=pm-template 统一投放平台规划与设想.md -o tek.pdf
```

But， Shit happens...

<pre>
pandoc: Error producing PDF from TeX source.
! LaTeX Error: File `titling.sty' not found.

Type X to quit or <RETURN> to proceed,
or enter new name. (Default extension: sty)

Enter file name:
! Emergency stop.
<read *>

l.65 \setlength
</pre>

没关系， 继续google， 嗯，貌似是某个Latex包没有， 发现CTAN有这么个包下载，可下下来也不会安装啊，应该有现成的包管理器吧？！ 搜来搜去，果然有，嘿嘿:

```bash
sudo tlmgr install titling
```

继续运行， 发现还缺“lastpage.sty”， 同理安装， 再运行， No news is good news, 命令运行成功， 马上查看运行结果，NND，内容是生成了，咋许多汉字变成了方框啊！！！

哦，对了， LiHei Pro这个字体貌似不是完全支持中文的字体，换换命令：`pandoc --latex-engine=xelatex -V mainfont=Hei --template=pm-template 统一投放平台规划与设想.md -o tek.pdf`, 换成黑体，运行后还是方框， fuck， 打开pm-template.latex，发现有字体设置的地方也是LiHei Pro，改之， 再运行， OH YEAH, 完美结果！

好啦， 最后总结一下：

1. 安装BasicTex;
2. 把<https://github.com/tzengyuxio/pages>项目中的pm-template.latex模板提前弄下来，将LiHei Pro字体改为黑体或者其它完全支持中文的字体；
3. 使用tlmgr包管理器把titling.sty和lastpage.sty两个依赖装上；
4. 运行pandoc生成pdf， 当然，不要忘了要使用到第二步更改过的template文件，并指定"--latex-engine=xelatex -V mainfont=Hei"

仅此文献给那些依然为相似问题而苦恼的兄弟姐妹们， 如果可以，记得请我吃饭，比如海宝同学，哈哈哈

---

**补充部分**

有些时候， 默认的repository下找不到相应的包，所以， 这个时候可以使用`sudo tlmgr --repository http://mirrors.ustc.edu.cn/CTAN/systems/texlive/tlnet install CJKfntef`类似的命令来安装，但是，因为授权协议之类问题， 即使指定repository也不一定找得到， 最后就得手动安装了。

首先运行`kpsewhich -var-value=TEXMFHOME`找出tex安装目录（我本地系统是在/Users/fujohnwang/Library/texmf）， 然后：
<pre>
sudo mkdir -p texmf/tex/latex/local/
sudo cp Downloads/CJKfntef.sty ~/Library/texmf/tex/latex/local
</pre>


> MMD， pandoc的markdown中如果插入图片的话，经常报错， 这个时候，检查一下图片是否是gif格式，如果是，那么将其转成png格式， 这真是一个大坑，老子耗费了差不多一天的时间来解决这个问题， 因为报错信息基本没参考价值， 就各种google， 各种装依赖尝试， 各种改模板，总之各种折腾， 最后是通过先转成latex中间格式，然后再使用xelatex从latex格式转pdf的log中才瞅出些许端倪，然后将gif改成png格式之后， 哇啦， 顺利通关！



