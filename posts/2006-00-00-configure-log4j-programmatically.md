% 在程序中动态配置log4j(configure Log4j Programmatically)
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

很简单的一个小技巧，之所以贴出来是因为期间碰到一个很有味道的细节陷阱...

某种需求下，需要根据程序启动后所处的环境来初始化log4j，比如根据不同的环境配置将log4j的日志文件生成到指定的不同地方，这个时候，你如果使用xml或者properties文件将这些写死的话，无疑没有办法完成这样的需求，所以，我们需要寻求更具灵活性的方法。

通常你可以只要以下2行代码就可以完成log4j的初始化：

~~~~~~~ {.java}
URL resourceURL = ResourceManager.getResource("config/log4j.xml");
DOMConfigurator.configure(w3cDocument.getDocumentElement());
~~~~~~~

但是，这样的话，你的log文件生成位置就写死到log4j.xml中了，为了能够在程序中动态变更这个位置，或者其他配置信息，我们可以采取以下方式：

~~~~~~~ {.java}
URL resourceURL = ResourceManager.getResource("config/log4j.xml");
SAXBuilder saxBuilder = new SAXBuilder();
Document jdomDocument =    saxBuilder.build(resourceURL);
XPath xpath = XPath.newInstance("/log4j:configuration/appender[@name='file']/param[@name='File']/@value");
Attribute valueAttr = (Attribute)xpath.selectSingleNode(jdomDocument);
String winPath = new StringBuffer().append(Admin.getCreditPathUtils().getLogDirPath()).append("terminal.log").toString();
// If you set this platform independent path into jdom without any process, the File.Seperator will cause some problem.
valueAttr.setValue(FilenameUtils.separatorsToUnix(winPath));
DOMOutputter outputter = new  DOMOutputter();
org.w3c.dom.Document w3cDocument = outputter.output(jdomDocument);
DOMConfigurator.configure(w3cDocument.getDocumentElement());
~~~~~~~

也就是，通过DOM来加载配置，这样在配置之前就可以使用xpath查询相应位置并更改相应值。

it's just a piece of cake, but  gotchas still there.

只所以要对winPath进行处理是因为，如果你直接将文件路径pass进去的话，在DomConfigurator配置的时候就会抛出异常，因为你的路径已经不是你传进去的那个路径了，所以，之前需要对路径进行预处理，比如将window平台的File.separator － “\”替换为“/”，不这样，你就等着Exception吧，哈哈

这里牵扯出一个细节，如果你不注意的话，就会掉进去，其实你可以不用Commons IO里的这个FilenameUtils类，完全可以直接用String来替换掉“\”就可以，不过，如果你选择不对的话，Exception又会给你好看了，呵呵...

String提供了2个适用于我们现在场景的替换方法:String.replace和String.replaceAll，前者不使用Regex进行替换，所以，对于我们这里可以直接winpath.replace("\\","/")就可以；但是，如果要使用String.replaceAll的话，你就要小心了，必须winPath.replaceAll("\\\\","/"),也就是说，他是用Regex来进行替换的，相当于`Pattern.compile(regex).matcher(str).replaceAll(repl)`

而如果你还像replace那样传入“\\“，那么，sorry，俺不干，呵呵，虽然是经常用的String类，不过，不注意依然让你难堪...