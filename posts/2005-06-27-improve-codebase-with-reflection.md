% 使用反射（Reflection）改进项目代码架构

> 2014年从msn space存档中重新恢复出来！

前几天为了测试条形码的扫描，需要为他人提供一个生成的条形码图片，故此，在barbecue的基础上使用swt为项目组实现了一个简单的条形码生成GUI程序，其中使用了反射机制极大的简化和改进了代码结构，其后同时联想到报表部分的实现，感觉自己当时的实现十分的丑陋，故此，一并做一反思，以鉴后人。

OK，先从这个SWT的GUI程序说起，界面上我们提供1个文本编辑框作为要生成的条形码的输入，一个combo列表框显示所有的barbecue支持的条形码类型，最后当然是一个button，点击后生成条形码图片并给出提示信息。在界面初始化的时候，我们需要为列表框赋值使之可以提示用户要生产的条码类型，当然，最常见的实现就是直接一行一行的为期添加值，比如我们知道有Code128的条码，有EAN128的条码，有Code39的条码等等，那么就可以直接以cmbType.add(“Code124); cmbType.add(“EAN128”);…的方式实现我们的代码，当然大同小异的，我们也可以直接将所有的值先添加到一个List中，最后循环这个List为列表框赋值，以此来使代码更清晰，但是，其实这样并不是最好的实现，虽然简单，但是依然有改进的余地，因为首先，我们发现BarcodeFactory类里，针对不同的条码类型，它都会有一个createXXX()形式的方法与之对应，所以，我们可以直接使用反射来取得这些方法，并截取条码名称部分就可以了，而不需要hard code条码名称到代码中，以下是简单的实现片断：

~~~~~~~ {.java}
// combo Init

Matcher matcher = Pattern.compile("^create(.*)$").matcher("");

Class clazz = BarcodeFactory.class;

Method[] methods = clazz.getMethods();

for(int i=0;i<methods.length;i++)

{

       matcher.reset(methods[i].getName());

       if(matcher.matches())

       {

              this.cmbType.add(matcher.group(1));

       }

}
~~~~~~~

界面初始化完成之后，最主要的就是按钮的事件处理。检验等暂且不提，我先说我的实现思路，从Text中提取输入，从列表框中提取条码类型名称，然后根据条码名称判断应该调用BarcodeFactory的什么方法，最后生成条码图片并输出。不知道大家是否想过，即使知道了条码类型名称，那么如何调用BarcodeFactory的相应方法那？！（对于guru来说固然小儿科，但是以上只是为了提供思路，问题固然简单，依然需要表达清楚，不是吗？！）OK，答案就是反射（Reflection）。 我们通过循环便利BarcodeFactory的所有方法，在循环中会根据正则表达式来判断那个方面名称中可以find到条码类型名称形式的pattern，如果find（）方法返回true，我们就可以直接调用，进行后继处理后直接返回了，请看如下代码片断：


~~~~~~~ {.java}
Class clazz = BarcodeFactory.class;

Method[] methods = clazz.getMethods();

for(int i=0;i<methods.length;i++){
    Method m = methods[i];

    Matcher matcher = Pattern.compile(type+"$").matcher(m.getName());

    if(matcher.find()){

        StringBuffer filename = new StringBuffer();
        filename.append(System.getProperty("user.dir"));
        filename.append(File.separator);
        filename.append("data");
        filename.append(File.separator);
        filename.append(type);
        filename.append(".jpg");

        Barcode barcode = (Barcode)m.invoke(clazz,new Object[]{input});
        out = new FileOutputStream(filename.toString());
        BarcodeImageHandler.outputBarcodeAsJPEGImage(barcode, out);

        MessageBox msg = new MessageBox(BarCodeGenerator.this.getShell(),SWT.APPLICATION_MODAL|SWT.ICON_INFORMATION);
        msg.setText("Message");
        msg.setMessage("Barcode generated Successfully into data directory!\n["+filename.toString()+"]");
        msg.open();

        return;
    }
}
~~~~~~~

OK，以上就是条码生产程序的说明，以此为基点，我想自我检讨一下，因为相对于上面的实现来说，我为报表模块所做的最终集成实在是太糟糕了（因为牵扯到目前开发的系统，所以此处略去不相关的内容，只对技术点进行说明）

首先，报表有许多种，20甚至30种都有可能，然后根据不同的类型，调用不同的对话框，最终根据输入的条件生成excel报表。刚开始我给出了一个接口，不如IReportGenerator,该接口给出了一个generate（）的通用方法以及setXXX（）方法（用来设置报表生成条件），然后告知大家在自己的报表实现类中implements这个接口；同时，我也给出了一个类型安全的枚举类，比如EReportType，用来保存和区分报表类型。在此基础上，用户选择了要生产的报表类型之后，我就可以调用条件输入对话框，传入相应的枚举类，然后在用户点击OK之后，就会判断枚举类的类型，然后根据这个类型来初始化相应的IReportGenerator实现类，最终直接调用generator.generate()方法就可以了。

现在，看我的实现，你就会发现最ugly的实现是什么样子的，呵呵

~~~~~~~ {.java}
EReportType type;

IReportGenerator generator;
// Other jobs to do
…
If（EReportType. TYPE1 == type）{
        generator = new SampleGenerator1();
}else if(EReportType. TYPE2 == type){
        generator = new SampleGenerator2();
}else if(EReportType.TYPE3 == type){
        generator = new SampleGenerator3();
}else if(…){

}

…// other operations

generator.generate();

…// left operations
~~~~~~~

怎么样，是不是有同感那？！呵呵，OK，知耻而后勇，让我们重构这段实现吧！

为了可以更清楚地说明我的意图，我将分2步来实施这次重构：

第一步，我们将不同IReportGenerato接口的实现类移出来，放到一个Map中，Map的key暂时定为EReportType的所有枚举值，而相应的value就直接是key所指定的枚举类的报表实现的class name。

~~~~~~~ {.java}
rptMapping.put(EReportType.TYPE1,com.darrenstudio.report.SampleReport1.class);
…// others
~~~~~~~

 (Map实现可以参考使用commons collection提供的实现类)

 这样，所有的映射都放到了这个Map之中，在我们的对话框OK按钮按下之后，我们就可以直接以这样的方式来实现：

~~~~~~~ {.java}
EReportType type;

IReportGenerator generator;

// Other jobs to do

…

Class clazz = (Class)getReportMapping().get(type);

generator = (IReportGenerator)clazz.newInstance();

generator.generate();

…// left works
~~~~~~~

怎么样？！这样是不是简洁多了那？！并且可维护性也有所提高，添加新的报表类型的时候，只要在Mapping的Map中添加新的key-value就可以了。但是这还不能算最好的结构，因为，在添加新的key-value的时候，我们需要修改代码！所以让我们更进一步！！！

第二步，我们把上面的映射拿出来，放到一个properties映射文件中，


~~~~~~~ {.java}
EReportType.TYPE1 = com.darrenstudio.report.SampleReport1.class
EReportType.TYPE2 = com.darrenstudio.report.SampleReport2.class
…
~~~~~~~

之后，我们把配置文件的内容读进来：

~~~~~~~ {.java}
Properties prop = new Properties();

InputStream ins = XXX.class.getResourceAsStream(“xx.properties”);

prop.load(ins);
~~~~~~~

万事俱备之后，以下的重构后的代码就出现了：

~~~~~~~ {.java}
EReportType type;

IReportGenerator generator;

// Other jobs to do

…

String clazzName = prop.getProperty(“type的class name”);

Class clazz = Class.forName（clazzName）;

generator = (IReportGenerator)clazz.newInstance();

generator.generate();

…// left works
~~~~~~~

(以上事例中的异常处理全部已经略去)

至此，我们的重构才可以算是公德圆满，既很好的改良了代码结构，而且也极大地改进了系统的可维护性和可扩展性，比如如果有新的报表类型，我们只要在配置文件中添加新的枚举类和新的实现类的映射就可以了！！！

综上，如果在系统的实现中适当的使用java语言的反射（Reflection）机制，我们可以极大改进代码架构。但是，因为反射本身的原因，其使用的场合在使用的时候也需权衡。

王福强(Darren.Wang)于2005年6月27日
