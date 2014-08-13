% 使用jacob来launch Excel文件
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

这几天一直在调查怎么在java程序中启动excel文件，但是进展一直不大，查遍网络各个角落，网上各个论坛，全都是千篇一律的，根本没有有创意一点儿的解决方法，呵呵，不过，黄天不负有心人，终于让我在yahoo的jacob新闻组中找到了一线生机，且让我慢慢道来。

 问题的背景是这样的，在我们的credit项目的back office管理工具中，现在有一个事件模块需要处理excel报表，当然报表生成使用的是POI，这在前面的blog中已经提到过了，但是，报表生成之后，日方希望能够马上启动已经生成的excel文件并显示出来，不过，有个条件，就是不管启动多少个excel文件，最终的excel进程只能有一个。（这个意图我和我的leader开始都理解错了，我们一直以为是在一个workbook中启动多个workbook显示，这个也是弯路之一吧。）

 Ok，问题来了，刚开始那我也使用了最基本的Runtime来处理这个问题，但是，这个明显不能满足需求，也想过跟日本人探讨是否取消以上条件，因为这样可以更容易处理（是十分的容易，呵呵）。不过，最终的结果是还要处理以上的条件，所以，下面的历程就开始了。
 
 首先，我在sun的forum中搜索了所有跟excel有关的帖子，也或多或少的发现了一下解决方法，但大部分都是使用Runtime来启动的，既然如此，我也多多少少试验了一下Runtime各种方法，比如：
 
Runtime.getRuntime().exec(“cmd /c start ”+fileName);
或者Runtime.getRuntime().exec(“cmd /c ”+fileName);
甚至Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler c:/workbook2.xls");等等，

虽然可以顺利的启动excel文件但是都不能满足日方的要求，所以，最终我还是要转到其他的方案。

其实那，我在之前就发现了jacob，但是，我对于com不是很熟悉，因为我真的是一个纯粹的java developer呵呵，所以，我对他有抵触情绪，能不用尽量不用，虽然我隐约感觉道他正是我要找的东西。当然，最终，我也硬着头皮钻进去看看如何使其为我所用。

我在yahoo的jacob新闻组中发了帖子，请求帮助，最终收到了Samir的答复，在这里再次谢谢Samir的帮助。他开始给我发了一个类，但是好像不是很顺利，所以，我请求他发一个完整的实例给我，他也在百忙之中给予了我帮助（他祖籍印度，现在移民澳大利亚，职位是架构师）。下面是他给我的原始类：

~~~~~~~ {.java}
import com.jacob.activeX.ActiveXComponent;

import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

import com.jacob.com.Variant;

 

/**

* @author Samir Kumar Mishra

*/

public class ExcelTest {

    ActiveXComponent excel;
    Dispatch excelApp;
    Dispatch workBooks;
    Variant wBooks;

    public static void main(String[] args) {
        ExcelTest excelTest = new ExcelTest();
        //excelTest.print();
      
        // This method can be called in loop to open as many worksheet youwant within the same excel instance.
        excelTest.addWorkBook("C:/workbook.xls");// Open1st Excel file.
        excelTest.addWorkBook("C:/workbook2.xls");// Open 2nd Excelfile.
        excelTest.release(); // Use this method to close Excel.
    }
    public ExcelTest() {
        excel = new ActiveXComponent("Excel.Application");
//        excelApp = excel.getObject();
        excel.setProperty("Visible", new Variant(true));

        workBooks = excel.getProperty("WorkBooks").toDispatch();

    }
    public void addWorkBook(String fileName) {
        wBooks = Dispatch.callN(workBooks, "Open", new Object[] {fileName});
    }
    public void release() {
        excel.invoke("Quit", new Variant[] {});
    }
}
~~~~~~~

现在我们可以多次调用addWorkbook方法来启动多个excel文件了，完成任务了不是嘛？！呵呵，少安毋躁，怎么windows的任务管理器的进程中那个excel在java程序推出后还是没有消失那？！我问了Samir，他说现在的进程应该已经不在java的管理范围之列了，我也同意，因为现在的excel进程应该属于本地的了。但是这样交工肯定不行，而我高中同学秦春雷的提醒促使我再次钻进jacob的文档，呵呵，而最终也发现了问题的所在。

下面是我对Samir的类进行改进后的成果：

~~~~~~~ {.java}
package com.livedoor.finance.credit.admin.business.event.util;

import com.jacob.com.Variant;
import com.jacob.com.Dispatch;
import com.jacob.com.ComThread;
import com.jacob.activeX.ActiveXComponent;

/**
 * @author Darren.Wang
 * 2005/03/17
 *  Thanks to Samir(who is in Australia), without his help , I can''t complete this work.
 */
public class JacobExcelLauncher {
    // excel automation
    private ActiveXComponent excel;
    // excel workbooks 
    private Dispatch workbooks;
    // excel file varient
    private Variant workbook;
    
    /*
     * constructor  of the JacobExcelLauncher
     * @author Darren.Wang
     */ 
    public JacobExcelLauncher()
    {
        // start the Excel 
        excel = new ActiveXComponent("Excel.Application");
        // first time, we need set the excel to be invisible
        excel.setProperty("Visible",new Variant(false));
        // get workbooks
        workbooks = excel.getProperty("WorkBooks").toDispatch();
    }
    
    /*
     *  Launch Excel file with this method, 
     *  it can be invoked many times
     */
    public void launch(String fileName)
    {
        // now make it appear
        excel.setProperty("Visible",new Variant(true));
        // open the excel file
        workbook = Dispatch.callN(workbooks,"Open",new Object[]{fileName});
    }
    
    /*
     * Release the resources and kill the excel process
     */
    public void release()
    {
        // quit the excel application
        excel.invoke("Quit",new Variant[]{});
        // invoke the method to count down the numbers of the reference,
        // and release them one by one to kill the excel process finally.
        ComThread.Release();
    }
    
    public static void main(String[] args) {
        JacobExcelLauncher launcher = new JacobExcelLauncher();
        launcher.launch("c:/workbook.xls");
        launcher.launch("c:/workbook2.xls");
        launcher.release();
    }
}
~~~~~~~

发现区别了嘛，对，就在release方法中，我加入了ComThread.Release()，十分简单不是嘛？！
但是这个结果的得到却不是一帆风顺的。Com的资源释放方式好像跟java的gc有很大的不同，他好像是针对引用计数来处理资源释放的，所以，ComThread.Release()的作用就一个挨着一个的清空引用计数，知道所有的引用的release完成为止，而最终excel进程也可以被kill掉了。

好了，大功告成，打完收工，呵呵

Thanks to Samir，thank you very much，you have given me so much help.
Thanks to 秦春雷2，呵呵，虽然你给我的代码不是太能跑起来，hoho

注：如果说其他情况下要用Runtime的话，记得要将fileName用引号引起来，否则对于文件名中有中文或者日文字符，甚至空格的文件名，运行时候会出问题，根本不会起作用。




