% Excel-Launching With Jacob
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

今天发现原来的JacobExcelLauncher类有问题，不确定问题到底出在哪里，但是如果当前页面有一个该类的实例的话，从该页面重新打开一个页面，在这个新打开的页面中，重新实例化一个该类的实例，那么在新打开的页面中使用这个新的实例启动Excel文件后，原来页面中的launcher在启动Excel文件的时候就会一闪之后而没有任何反应。

分析代码后估计是两个实例指向了同一个automation（具体原因确实无从考证，因为偶对于微软的程序相关的东西实在不甚了了），所以前一个实例指向的automation被改动或者破坏了，从而导致以上问题。

所以，直接改为直接静态实例化，并将调用方法同时改为static的，这样反而减少了调用时候实例化的繁琐，只要最后在系统的Shell dispose的时候调用JacobExcelLauncher的release方法就可以了。

附上改动后的程序代码：


~~~~~~~ {.java}
/**

* @author Darren.Wang

* 2005/03/17

* Thanks to Samir(who is in Australia), without his help , I can't complete this work.

*/

public class JacobExcelLauncher {

// excel automation

private static ActiveXComponent excel;


static

{

// start the Excel

excel = new ActiveXComponent("Excel.Application");

// first time, we need set the excel to be invisible

excel.setProperty("Visible",new Variant(false));

}

/*

* Launch Excel file with this method,

* it can be invoked many times

*/

public static void launch(String fileName)

{

// now make it appear,for we have set it to be invisible before

excel.setProperty("Visible",new Variant(true));

// open the excel file

Dispatch.callN(excel.getProperty("WorkBooks").toDispatch(),"Open",new Object[]{fileName});

}


/*

* Release the resources and kill the excel process

*/

public static void release()

{

// quit the excel application

excel.invoke("Quit",new Variant[]{});

// invoke the method to count down the numbers of the reference,

// and release them one by one to kill the excel process finally.

ComThread.Release();

}

}
~~~~~~~
