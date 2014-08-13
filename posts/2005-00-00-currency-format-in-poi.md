% 有关POI中对货币格式化的支持问题
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

这几天一直带人使用POI做excel部分的实现，所以，有些小的tips可以写写，以便自己或者大家以后可以借鉴一下。

# tip1
先说前几天的那个打印设置里面工作表tab里的顶端标题行的问题，当时让老迟给大家讲的时候，估计他没有完全搞透，所以，针对这个例子也就没有说出什么，其实也很简单，POI的quick guide里面那个Repeating Rows的例子就是列举这个问题。只需要调用HSSFWorkbook的setRepeatingRowsAndColumns()方法进行设置就可以了，其中：

参数1： 表示要对哪一个sheet进行设置；

参数2和3：表示要repeat的起始列和结束列；

参数4和5：表示要repeat的起始行和结束行。

参数2，3，4，5如果指定为－1的话，表示任意列或者行。

比如，要想让第9和第10行在打印的时候，能够在超出一页的时候可以依然保持标题行，可以像这样调用该方法：wb.setRepeatingRowsAndColumns(0,-1,-1,8,9);  

# tip2

如果不制定是横向打印还是竖向打印的话，excel会默认以竖向Vertical的形式打印，而我们却向横向打印，这个时候，可以使用HSSFPrintSetup类。假定要对yourSheet设置打印模式为横向打印，直接调用yourSheet.getPrintSetup().setLandscape(true);就可以了。呵呵，easy，right？其他情况类似处理。

# tip3

今天遇到的问题。中午询问大家工作量的时候，发现所有人在金额统计的栏中都是以美元符来表示金额的，但是这种情况让日本人看到肯定不行，应该用日元符来格式化金额，所以，今天下午在修改完顾客和申请检索部分的bug之后，对该问题进行了调研。结果就是POI的内置格式化中没有提供日元Currency的格式化格式，需要用custom的格式，但是，这个格式如何指定那？且看下面代码：

~~~~~~~ {.java}
     HSSFCellStyle style = workbook.createCellStyle();
     style.setDataFormat(workbook.createDataFormat().getFormat(customFormat));
~~~~~~~

其中，customFormat需要指定为类似于这样形式的字符串：[\u00a5]#,##0.00

另外需要注意的是，必须使用workbook.createDataFormat().getFormat(customFormat)这样的代码生成DataFormat，而不能直接调用HSSFDataFormat.getBuildinFormat(customFormat)的形式，因为后者是取得DataFormat的内置格式化格式，而以上形式则不是。

还有就是，以上形式目前只是针对日元符有用，对于其他Currency是否依然有效则不可知，比如，我在查找资料的时候，发现有人使用类似格式格式化欧元符的时候就好用。

Ok，就这么些了，其他的都是很common的东西，查一般的文档都可以找到。就此打住。