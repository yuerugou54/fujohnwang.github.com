% 扶墙老师教你如何用pandoc写一本书
% 王福强 - fujohnwang AT gmail DOTA com
% 2018-03-28



# 一般做法

一般建议都是使用数字进行排序，然后使用`*.md`或者`*.markdown`的形式直接传入pandoc命令进行编译即可(`pandoc *.md > markdown_book.html`)

# 使用pandoc写书的不便之处

## 章节排序
`使用数字进行排序便于为pandoc输入文件的做法`带来的不便是，中间插入其他内容或者章节的时候需要很多调整：

```
01_preface.md
02_introduction.md
03_why_markdown_is_useful.md
04_limitations_of_markdown.md
05_conclusions.md
06_links.md
```

## 无法层次化内容

很多章节是嵌套有层次化的， 最直觉性的管理方式是使用目录， 但这样又会导致pandoc编译的时候资源引用错误，因为我们在编写的时候实际上使用的资源引用都是相对路径， 比如images/xx.png， 而如果我们嵌套了目录， 那么， 这个图片资源可能存在于chapter3/images/xx.png, 因为我们是在顶层目录执行pandoc命令的：

```
pandoc_compile_command.sh
preface.md
chapter1/
chapter2/
    images/
        xx.png
    section1.md
    section2.md
    ...
chapter3/
...
```

为了规避这种问题，我们要么就把所有md和images文件拉平(flatten)到同一个目录下面， 要么，就使用pandoc命令的时候，明确指定输入顺序, **但依然无法解决图片路径引用的在编写和编译期间的差异问题**:

```
➜  pandoc_book_starter git:(master) ✗ pandoc -s -N --toc --toc-depth=4 --self-contained -c ~/FuqiangWorks/templates/pandoc/ivy.css "index.md" > "index.html"
File images/ipfs.jpeg not found in resource path
```


# 我的想法

使用嵌套目录的形式管理层次化内容， 便于编写和管理， 在编译期间，使用某种filter机制， 将各个子目录的内容归并到同一个md文件， 同时修改图片引用地址， 最终从合并后的md文件进行编译。

filter主要做两个事情：

1. header缩进， 根据嵌套目录的层数决定
2. 资源路径的增添， 依然是根据嵌套目录的层数决定

处理后的内容合并入md统一交给pandoc编译即可。

当然， 我们依然可以结合pandoc命令行的明确指定多输入文件的方式进一步细化前后的附录内容，比如preface，附录，参考资料等。

# 最终方案

1. 创建一个build目录， 将源代码md及相关目录结构和资源都copy过去， 然后修改header缩进和images等资源路径
    - 关于图片路径的问题， 其实转换成绝对路径也可以，毕竟只对编译输出的最终结果markdown文件一次生成有效，不用长久保存， md源文件中的绝对路径保存md和image的资源引用相对路径即可。
    - 关于修改header缩进， 也可以省了， 因为markdown-pp支持include的时候对标题header进行缩进， 可以明确指定缩进几个层次
2. 使用markdown-pp处理自定义的index.mdpp，输出index.md
3. 使用pandoc处理index.md，输入html， pdf， epub等格式；

具体工程结构和使用可以参考已经开源到Github的实例项目模板： <https://github.com/keevol/pandoc_book_starter>

# 其他需要注意的地方(Gotchas)

1. 标题的上部分最好留存一两行， 不要`#标题`作为第一行， 否则pandoc在编译的时候很容易跟上一章或者上一节内容拼接的时候，格式上忽略放在第一行的标题；
2. 分别为pdf和epub格式的输出准备不同的title文件，便于输出符合特定格式的目录和内容；

