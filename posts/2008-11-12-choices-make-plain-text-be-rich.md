% 普通文本的“麻雀变凤凰”之路

整个标题整得有些过于夸张了（这种行为好像是称作“标题党 ”吧？！）， 呵呵，实际上，仅仅是因为无意间翻了翻“搞头书 ” ^[《Programming Ruby》 2nd Edition] ， 发现个叫“BlueCloth ”的Gem库可以将普通文本标记转换成HTML格式， 从而牵连出自己脑海中的其它几种完成类似功能的工具选择，故此借机汇聚一下， 当然了，更希望是“抛砖引玉 ”，各位同仁如果还有更多的选择，不妨尽情添附，以(之)享整个社区。

对于文档之类的编写工作来说，大家或许早就习惯了MS Word之类工具倡导的“所见即所得(WYSIWYG) ”编写方式， 属于这一类的工具很多，包括:

1. Microsoft Word
2. OpenOffice Writer
3. DreamWeaver(我不知道把它罗列在这里是否合适，呵呵)
4. Buzzword(http://buzzword.acrobat.com)

但是，所有这些并不能代表整个世界，当我们透过镜子的这一面看过去的时候，就会发现， 在镜子的另一面，“所见即所得 ”并不像我们最初所想象的那样处处受欢迎， 因为在这里，“基于标记文本的文字编写方式 ”才是统治镜子另一面的那个“人 ”。 :-)

我们不妨将“基于标记文本的文字编写方式 ”划分为两类，第一类不妨称其为“基于普通文本的标记文本 ”， 第二类不妨称其为“基于XML格式的标记文本 ”，下面我们分别对这两类标记文本方式进行细谈...


# 基于普通文本的标记文本

通常情况下，“基于普通文本的标记文本 ”都是在普通文本中间“穿插 ”一些具有特殊意义的标记文本， 然后通过某种能够识别这些预先定义的标记文本的工具，对这些普通文本进行处理或者说转换，最终输出成各种格式的文档，比如最常见的就是输出成HTML文件。

## WIKI
属于此种范畴的“人选 ”，第一个当属“WIKI ”，大名鼎鼎的Wikipedia（http://en.wikipedia.org/wiki/Main_Page）想必大家都已经早有耳闻了。 WIKI形式的文档就是，编辑的时候以普通文本形式进行编辑，而显示的时候，则根据普通文本内容中嵌入的各种标记进行格式化输出成HTML进行显示。对WIKI的总体情况，笔者其实不是很熟悉， 好像各家WIKI都有各自都有的标记文本语法(Syntax)，而笔者充其量也只是早些年前自己装过“MoinMoin Wiki(http://moinmoin.wikiwikiweb.de/) ”，自己把玩了一把而已。

WIKI的实现产品很多，基于各种编程语言的WIKI都会各自涌现一些优秀的产品，像MoinMoin Wiki就是使用Python语言开发的， 除此之外，还有使用Java语言开发的XWiki，使用Perl开发的最早的Wiki应用WikiWikiWeb等等。 你可以从http://en.wikipedia.org/wiki/Wiki_software获取更多使用其他语言开发的Wiki软件的相关信息。

还记得前阵子在图灵的论坛里，刘江老师发起一个倡导大家通过Wiki合作写书的想法，可见Wiki并非浪得虚名，仅是说说而已了。

## Tex/LaTex

笔者想当年因为经济条件没有考研，所以也就没有真正验证过，只是听说，只要是读过研究生写过论文的， 好像大多数都接触过LaTeX（http://www.latex-project.org/），它属于TeX的改进版，但同样属于基于普通标记文本的文字编写方式。 LaTeX好像对各种公式的表达支持很好，不过，对于中文的支持来说，好像要诉诸于CTeX之类的本地化版本，但这都是笔者道听途说， 如果您对这种基于TeX/LaTeX的编辑方式感兴趣，倒是应该自己验证一下。俺此行的目的，仅仅是为了罗列选择项而已。

这里有一个摘自LaTeX网站的实例，或许能够让我们有一个感性的认识。如果编写如下源文本：

~~~~~~~ {.latex}
\documentclass{article}
\title{Cartesian closed categories and the price of eggs}
\author{Jane Doe}
\date{September 1994}
\begin{document}
   \maketitle
   Hello world!
\end{document}
~~~~~~~

那么，通过LaTeX处理后，将输出类似如下形式的显示效果(下面的输出无法完全反映效果，可以参考LaTeX网站上的样子)：

<pre>
Cartesian closed categories and the price of eggs




Jane Doe
September 1994

Hello world!
</pre>


## BlueCloth/RedCloth

前面说了，这篇文字是由BlueCloth（http://www.deveiate.org/projects/BlueCloth/）引出的， 这是一个基于Markdown(http://daringfireball.net/projects/markdown/)标记语法的Ruby Gem库， 使用它，我们可以在Ruby程序中将符合Markdown标记语法的普通文本转换成HTML格式的文本以供显示。

在Ruby世界里，有一个BlueCloth的同胞兄弟，叫做RedCloth，它也是一个Ruby的Gem库，同样用于将普通的标记文本转换成HTML格式的文本， 但是，RedCloth所遵循的标记语法与BlueCloth是不一样的，它遵循的是Textism(http://www.textism.com/tools/textile/)所规定的标记语法。
## A9text

这个是自己兄弟阿九的开源项目，主要基于Javascript实现，采用自己的标记文本语法，同样采用的是基于普通标记文本格式。 A9Text可以让你在本机方便的进行文档编写，而不用去操心各种格式的编排之类的琐事，你可以从http://trydofor.iteye.com/获取更多A9Text的信息。

## 其他...

我想，许多思想都是相似的，在其他的计算机语言或者说技术社区中，应该也存在许多基于同一思想的软件或者说产品， 只是，俺不是超人，不可能去一一认识，列位如若晓得，不妨道明，也让俺开阔些眼界。

# 基于XML格式的标记文本

这部分其实只是想说说DocBook，因为笔者现在已经习惯了基于docbook的文档编写方式，谁让俺没能精通Word的使用那，呵呵。 最初决定使用docbook的时候，是记得好像OReilly内部是使用docbook作为内部格式的，后来才发现，当初没有充分考虑国情， 因为国内还是用Word进行排版的，好在使用Docbook的好处就是，我只需要关注内容就可以了，至于最终的输出格式， 可以视情况而定，我可以将同样一份docbook输出成单页的HTML，多页的HTML，PDF等格式，甚至于如果我们能够知道Word文档的内部结构， 也可以写一个转换程序，将同一份docbook文档直接转换成Word文档。

实际上，这篇文字就是使用docbook编写的，通过Velocity项目提供的Docbook Framework，只需要少许几步， 就可以在Eclipse中搭建一个完备的文档编写工程，如果不考虑交换的话，只是自己编写一些东西，离了word，其实也满逍遥的。

# 小结

或许真的就跟Linkin Park乐队在一首歌的结尾所说的那样，“Difference Is Good ”， 即使是基于相同的理念或者说思想，但各个产品之间还是会存在些许差异，或者是标记语法的差异，或者是实现语音的差异，等等。 这就跟经常见到的语言之争，框架之争一样，或许都只是个人喜好决定的，也正是如此，才会让整个世界变得如此斑斓。

但话又说回来了，有时候，选择太多了也不好，或许会让大家选择的很累，这个时候，过多的差异反而成为了问题， 能够一统天下的方式，或许又会更受欢迎，谁又知道那？！