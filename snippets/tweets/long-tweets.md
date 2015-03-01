% 长笔记
% 王福强
% 2015.03.01


　　　
# Initialization On-Demand Holder Idiom Demonstration


~~~~~~~ {.java}
public class Singleton {     
	private Singleton() {         
		// initialize this class
	}
    	private static class SingletonHolder {         
		private static final Singleton instance = new Singleton();
   	}
    	public static Singleton getInstance() {         
		return SingletonHolder.instance;
    	}
}
~~~~~~~

# 通过java.awt.Robot的createScreenCapture截屏

~~~~~~~ {.java}
public static void captureScreen(String fileName) throws Exception {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle screenRectangle = new Rectangle(screenSize);
    Robot robot = new Robot();
    BufferedImage image = robot.createScreenCapture(screenRectangle);
    ImageIO.write(image, "png", new File(fileName));
}
~~~~~~~

# 英雄无敌III中玩随机地图和元素族的方法

在游戏所在的文件夹中建立一个名为 H3BLADE.EXE 的空文件，
然后再在 Data 子文件夹中建立H3AB_BMP.LOD、H3AB_SPR.LOD、H3AB_AHD.SND、H3AB_AHD.VID 这几个空文件，这样就可以玩到随机地图了，
而且还能在没有安装末日之刃的情况下使用元素族。
建立空文件的方法有很多，最简单的是利用windows自带的记事本（notepad），什么内容也不用输，直接使用“另存为”命令，
文件类型定为“所有文件（\*.\*）”，文件名要完整输入（如 3BLADE.EXE ），保存就行了。
然后复制该空文件，修改文件名生成新文件（事先要将“设置-->文件夹选项-->察看-->隐藏已知文件类型的扩展名”的勾去掉，才能修改后缀名）。 

# Linux 负载查询和uptime命令解释

在Linux系统中，uptime、w、top等命令都会有系统平均负载load average的输出，那么什么是系统平均负载呢？
系统平均负载被定义为在特定时间间隔内运行队列中的平均进程数。如果一个进程满足以下条件则其就会位于运行队列中：

   1. 它没有在等待I/O操作的结果
   2. 它没有主动进入等待状态(也就是没有调用'wait')
   3. 没有被停止(例如：等待终止)

例如：

[root@www2 init.d]# uptime
> 7:51pm up 2 days, 5:43, 2 users, load average: 8.13, 5.90, 4.94

命令输出的最后内容表示在过去的1、5、15分钟内运行队列中的平均进程数量。
一般来说只要每个CPU的当前活动进程数不大于3那么系统的性能就是良好的，如果每个CPU的任务数大于5，那么就表示这台机器的性能有严重问题。对于上面的例子来说，假设系统有两个CPU，那么其每个CPU的当前任务数为：8.13/2=4.065。这表示该系统的性能是可以接受的。

# What usually a web framework will offer?

* AJAX
* Authentication
* Authorization
* Caching
* Data Sanitization
* Data Validation
* Templating
* URL mapping or rewriting

[from] <http://net.tutsplus.com/tutorials/other/15-most-important-considerations-when-choosing-a-web-development-framework/>

# The Elements of Programming Style摘录

   - 把代码写清楚，别耍小聪明。
   - 想干什么，讲的简单点、直接点。
   - 只要有可能，使用库函数。
   - 避免使用太多的临时变量。
   - ”效率“不是牺牲清晰性的理由。
   - 让机器去干那些脏活。
   - 重复的表达式应该换成函数调用。
   - 加上括号、避免歧义。
   - 不要使用含糊不清的变量名。
   - 把不必要的分支去掉。
   - 使用语言的好特性，不要使用那些糟糕的特性。
   - 该用逻辑表达式的时候，不要使用过多的条件分支。
   - 如果逻辑表达式不好理解，就试着做下变形。
   - 选择让程序更简洁的数据表达形式。
   - 先用伪代码写，再翻译成你使用的语言。
   - 模块化。使用过程和函数。

   - 只要你能保证程序的可读性，能不用 goto 就别用 。
   - 不要给糟糕的代码打补丁 - 重写就是了。
   - 把大的程序分成一小片一小片来写，分块测试。
   - 使用递归程序来处理递归定义的数据结构。
   - 正确和错误的输入数据都要测试。
   - 确保输入不会超出程序的限制。
   - 依靠文件结束来终止输入，而不是依赖一个记数。
   - 把文件结束作为一个输入状态来处理。
   - 识别出错误的输入；如果有可能就修复它。
   - 让输入数据很容易构造出来，让输出数据不言自明。
   - 使用统一的输入格式。
   - 让输入容易校对。
   - 如有可能，提供更自由的输入格式。
   - 使用输入提示，允许使用默认值。并把它们显示出来。
   - 把输入输出放到子程序里。
   - 确保所有的变量在使用前都有初始化。
   - 不要因为一个 bug 而停止不前。
   - 打开编译程序的调试选项。
   - 常量结构用数据声明初始化，变量结构用执行代码初始化。
   - 小心 off-by-one 错误。
   - 当循环中有多个跳出点时要小心。
   - 如果什么都不做，那么也要优雅的表现出这个意思。
   - 用边界值测试程序。
   - 手工检查一些答案。
   - 防御式编程 - 为不可能的情况写几句代码。
   - 10.0 乘 0.1 很难保证永远是 1.0 。
   - 7/8 等于 0 ，而 7.0/8.0 不等于 0 。
   - 不要直接判断两个浮点数相等。
   - 先做对，再弄快。
   - 先使其可靠，再让其更快。
   - 先把代码弄干净，再让它变快。
   - 别为了获得一丁点“性能”就牺牲掉整洁。
   - 让编译器做些简单的优化。
   - 不要过分追求重用代码；下次用的时候重新组织一下即可。
   - 确保特殊的情况是真的特殊。
   - 保持简洁以获得速度。
   - 不要死磕代码来加快速度 - 找个更好的算法。
   - 用工具分析你的程序。在做“性能”改进前先评测一下。
   - 确保注释和代码一致。
   - 不要在注释里仅仅重复代码 - 让每处注释都有价值。
   - 不要给糟糕的代码做注释 - 应该重写它。
   - 给变量都起个有意义的名字。
   - 把程序重新整理一下，让阅读代码的人更容易理解。
   - 为你的数据布局写一个文档。
   - 不要过分注释。 

# 如何理解HyerThread?

As AMD quotes it:
> So – cores are like bikes, threads are the riders. Running more threads increases throughput for applications as long as you have available cores. If you have threads waiting to be scheduled and no available cores – you have a bottleneck.

> Think of SMT and HT as a tandem bike.  Yes it can move two riders, but not as quickly or efficiently as two separate bikes.

# 系统监控的目标

- Disk Throughput
- Disk Storage
- CPU
- Memorry
- Network





