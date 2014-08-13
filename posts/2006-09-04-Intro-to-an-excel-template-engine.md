% Introduction 2 an excel template engine
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

今天在浏览Javaeye论坛时候无意间发现一个Excel模板引擎的实现---jXLS（http://jxls.sourceforge.net/），才想起其实早些时候自己也想过写这么一个东西，但是后来考虑到全面支持的复杂性，觉得不是一朝一夕可以搞定的，所以就转而求其次了，只是写一个简单的Template Method实现类，根据具体报表实现其相应的渲染部分。

因为如果说不用考虑太多的话，一个excel引擎很容易搞定，无非就是为excel的模版提供一种简单的expression,然后engine根据scan到的expression从数据context中抓取数据并填充expression所在位置，但是如果报表复杂性提高的话，实现这个engine就不那么容易了，比如loop logic的处理，数据显示的格式化如何关联，scan实现算法的选择等等，不过好在这些现在有人帮我们实现了（that's jXLS)，可以帮助我们剩下很多Money（Time is Money，呵呵）...

**Main Features**

* Using SQL queries directly in XLS templates
* Simple property access notation
* Full expression language support
* Complex object graph export
* Flexible collection export
* Flow-Control Tags support
* Dynamic grouping of data
* Export of a single collection into multiple worksheets
* Adjacent tables support!
* Complex formulas support
* Charts, Macros and many other Excel features in XLS template
* Dynamic Outlines
* Dynamic Columns Hiding
* Dynamic Cell Style processing through custom Processors
* JDBC ResultSet export
* Merged Cells support
* Multiple bean properties in a single cell

有了jXLS的支持加上原来的实现方式，现在你可以根据具体场景随心所欲的驰骋excel report field啦..