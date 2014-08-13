% 闲话spring在系统实现中的应用
% FuqiangWang
% 2005-02-25

> 2014年从msn space存档中重新恢复出来！

 前阵子逮到一点儿时间，就零零散散地看了几章《Expert one on one J2EE design and development》，从Rod的所言所语深有感触。对于java应用的开发也就有了进一步的认识，尤其是持久层的一些理念，从而工作中也尽量应用这些理念以加深印象，提高软件开发效率和质量。
 

近几天在完成了credit的子工程MailMonitor之后，credit web方面又有一个要做job schedule的子项目，功能更先前的MailMonitor差不多，所以leader让我给这个子项目先搭建一个框架，以便web方面可以在这个框架下实现最终的功能。

鉴于前阵子一直在尝试将spring运用于项目的开发，所以这次也不例外，我又采用了spring，呵呵，谁让他让我来搭建这个框架那，:em221:，题外话，说是实在的，spring好用，这是勿庸置疑的，但是，配置文件嘛，就不是那么easy了，要协调和配置那么些业务对象，稍一不留神就会出错，虽然我现在用的业务对象 并不多，但是也明显感到配置这些业务对象的繁琐，不过，这边儿繁琐了，对于业务对象的实现却有很大的简化，尤其是spring对于持久层技术的支持，使你可以明显感觉到在使用了spring后，实现持久层逻辑的简单加快捷。所以说，还是老话题，在将metadata放在类文件中还是说写到配置文件中，这完全取决你，因为在这两种方式之间明显有一个权衡点，如何去找到你可以接收的权衡点，it‘s all up to you！

初步分析总体流程之后，我发现可以将其总结为简单的三步：

1. 生成发送给B公司的csv文件；
2. 对该csv数据文件进行pgp加密；
3. 对加密完的csv文件进行压缩

在完成了以上的工作之后，就可以将压缩文件作为附件attach到邮件之中并发送，整个流程也就这么简单。但是，简单归简单，我们依然可以做出简洁漂亮的设计并参照实现之。

其实，所有的工作无非就是要发送邮件，以上的三步最终都是为发送mail做准备，至于这三步到底如何实现，我们可以不去关心，我们所关注的只是发送mail，所以，首先我们要提出一个接口，标志我们的mail发送逻辑，姑且将该接口命名为ICSVMailSender，下面是其代码：


~~~~~~~ {.java}
package com.livedoor.finance.credit.csvmail.bo;

/**
 * @author Darren.Wang
 * 2005-2-24 11:11:11
 */
public interface ICSVMailSender {
 void sendCSV();
}
~~~~~~~

针对该接口，我们给出一个抽象的实现：

~~~~~~~ {.java}
public abstract class AbstractCSVMailSender implements ICSVMailSender{
…….
public final void sendCSV() {
1. 生成csv文件
2. 对csv文件进行加密
3. 对加密后的csv文件进行压缩以便发送
4. 将加密后的csv以附件形式attach到mail中并发送
}
 /**
  * Hook Method
  * do db query and generate CSV file in this method
  * @return String csv data
  */
 protected abstract String createCSV();
 
 /**
  * Hook Method
  * do encription with original string type csv data,and return encripted data
  * @param originalStr
  * @return byte[],encripted data in byte[] array type
  */
 protected abstract byte[] encryptCSV(String originalStr);
 
 /**
  * Hook Method
  * zip the encripted csv data into a file, so that the mail can send it as an attachement
  * @param encryptedData
  * @return File, zipFile after zip
  */
 protected abstract File zipCSV(byte[] encryptedData);
}
~~~~~~~

其实，对于明眼人来说，以上代码一出，则马上就会认出，呵呵，这不明显一个template模式的应用嘛，right，呵呵，就是它，在Rod的《Expert ….》一书中也着重提过该设计模式的应用，而且spring中应用该模式的例子也挺多，比如JdbcTemlate等，鉴于这次所要求的功能，我认为将template设计模式应用于此再合适不过了。在以上模板的基础上，我们可以自由实现以上三个抽象方法来提供最终的数据，你是用jdbc还是hibernate还是其他，我们不关心，这些自由选择的权利我们都下放给具体的实现者，这不是很好嘛！

有了以上的基础，我们的实现也变得很简单,只要继承类并实现三个方法所要实现的业务逻辑就可以了，比如：

~~~~~~~ {.java}
public class CSVMailSenderImpl extends AbstractCSVMailSender
{
 /* (non-Javadoc)
  * @see com.livedoor.finance.credit.csvmail.bo.AbstractCSVMailSender#createCSV()
  */
 protected String createCSV() {
 …
 }
 /* (non-Javadoc)
  * @see com.livedoor.finance.credit.csvmail.bo.AbstractCSVMailSender#encryptCSV(java.lang.String)
  */
 protected byte[] encryptCSV(String originalStr) {
 …
 }
 /* (non-Javadoc)
  * @see com.livedoor.finance.credit.csvmail.bo.AbstractCSVMailSender#zipCSV(byte[])
  */
 protected File zipCSV(byte[] encryptedData) {
 …
}
}
~~~~~~~

这样，我们总的业务框架就完成了，在此基础上，我们要实现job schedule，而这在有了spring之后，变得是如此的简单，因为他对于quartz和timer都进行了有效的封装，我们只需要配置一下配置文件就可以全部搞定，而在具体的job实现类里面，我们只要实例化ICSVMailSender的某个具体实现类，并调用其sendCSV（）方法就可以了。是不是很便捷那？！

下面是spring-config.xml的部分片段，以供参考：

~~~~~~~ {.xml}
 ...
<bean name="mailJob" class="org.springframework.scheduling.quartz.JobDetailBean">
  <property name="jobClass">
   <value>com.livedoor.finance.credit.csvmail.job.CSVMailJob</value>
  </property>
  <property name="jobDataAsMap">
   <map>
    <entry key="csvMailSender">
     <ref bean="csvMailSender"/>
    </entry>
   </map>
  </property>
 </bean>
<bean id="cronTrigger" class="org.springframework.scheduling.quartz.CronTriggerBean">
  <property name="jobDetail">
   <ref bean="mailJob"/>
  </property>
  <property name="cronExpression">
   <value>0 0 3 * * ?</value>
  </property>
 </bean>
 
<bean name="schedulerFactory" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
  <property name="triggers">
   <list>
    <ref bean="cronTrigger"/>
   </list>
  </property>
 </bean>
~~~~~~~

 除了以上所述之外，还有一点可以值得一提的就是spring提供的邮件发送功能，我们既然要给b公司发送数据邮件，当然邮件的发送逻辑是不可少的啦，所以，在AbstractCSVMailSender模板类的sendCSV（）方法中，我们还要实现邮件发送逻辑，而这些，都是通过spring提供的mail封装来完成的，同时，因为日方提供了邮件模板，要根据模板设置邮件内容，所以我又引入了velocity，而spring对其也同样有所支持，呵呵，到此看来，这些东西真的是结合的天衣无缝了。
 
 对于数据邮件的发送部分，偶在这里就不作详述了，不过大家如果有兴趣的话，可以参考Matt Raible的一篇blog《Sending Velocity－based Email with Spring》，上面对整个流程阐述的很明了，另外，读一下spring的javadoc也挺好，对于邮件发送部分，spring只是提供了几个包装类，很容易就可以搞清楚各个类的左右和他们之间的关系，而这对于你以后应用spring来说更会得心应手。
 
 最后，我们不妨用一个UML的class图来阐明这个简单的框架实现吧 -  [img]http://images.blogcn.com/2005/2/25/12/darrenwang,2005022523923.jpg[/img]






