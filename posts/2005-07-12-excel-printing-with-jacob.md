% Excel-Printing With Jacob
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

在Java程序中直接对指定的excel文件进行打印，而不是打开excel后手动的进行打印，不知道有没有人遇到此种窘境？！首先Java1.4的JPS连PDF的打印好像都不太可能，其次，好像搜遍整个net也没有找到个好用的lib(以上观点或许有所偏颇，因为不经没有太多时间进行更深入的翻阅相关资料)，这就是促使偶继续求助于jacob的原因了，因为偶发现有人给出过用VB写exe，Java调用的类似解决思路。

其实以前就因为excel的问题求助于Samir有关Jacob的问题，而且也给出了一个圆满的解决。现在偶实在不好意思再次劳烦，所以，自己在以前的代码之上拼凑出了以下代码，呵呵，it works!


~~~~~~~ {.java}
public class JacobExcelPrinter {

	private ActiveXComponent excel;
	// excel workbooks 
	private Dispatch workbooks;
	// excel file varient
	private Variant workbook;

	public JacobExcelPrinter()
	{

	}
	/*
	* non-thread-safe
	*/
	public void print(String filename)
	{
	try
	{
	// start the Excel 
	excel = new ActiveXComponent("Excel.Application");
	// first time, we need set the excel to be invisible
	excel.setProperty("Visible",new Variant(false));
	// get workbooks
	workbooks = excel.getProperty("WorkBooks").toDispatch();
	workbook = Dispatch.callN(workbooks,"Open",new Object[]{filename});

	// Dispatch.call(Dispatch.get(workbook.toDispatch(),"ActiveSheet").toDispatch(),"PrintOut");
	Dispatch.call(Dispatch.get(workbook.toDispatch(),"Worksheets").toDispatch(),"PrintOut");
	}
	finally
	{
	// quit the excel application
	excel.invoke("Quit",new Variant[]{});
	// invoke the method to count down the numbers of the reference,
	// and release them one by one to kill the excel process finally.
	ComThread.Release();
	}

	}

	/**
	* @param args
	*/
	public static void main(String[] args) {
	JacobExcelPrinter printer = new JacobExcelPrinter();
	printer.print("c:/xx.xls");
	}

}


~~~~~~~

通过上面的ｕｔｉｌ类就可以直接发送某个文件到打印机打印了，应该不是唯一的解决方法，但是我的，呵呵，如果哪位有更好的解决方法，还望在网上与大家分享！！！

by Darren.Wang (2005-07-12)
