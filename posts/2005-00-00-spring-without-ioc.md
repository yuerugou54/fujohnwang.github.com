% Spring Without IoC
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

或许有的时候你并不想用spring的beanFactory，比如，你目前不做j2EE，而是做一般的java应用，并且，项目也已经启动了，并没有使用spring的IoC支持，这种情况下，你是否会怀念spring中提供的种种便利那？！

其实没有了spring的BeanFactory，你同样可以很容易的使用spring的种种便利，不要忘了，spring的BeanFactory组装的也仅仅是普通的pojo而已，现在只是通过一样的方式构造并使用它们就可以了。

比如，我们要在应用中使用spring提供的发送mail的API的支持，那么现在我们现在就可以这样写：

~~~~~~~ {.java}
JavaMailSender mailSender = new JavaMailSenderImpl();
((JavaMailSenderImpl)mailSender).setHost("mail.livedoor.cn");
((JavaMailSenderImpl)mailSender).setUsername("wfq@livedoor.cn");
((JavaMailSenderImpl)mailSender).setPassword("458524033931");
// IF your Mail Server request SMTP authentication, you must set properties below,
// Otherwise, mail sending will fail.
Properties prop = new Properties();
prop.setProperty("mail.smtp.auth","true");
((JavaMailSenderImpl)mailSender).setJavaMailProperties(prop);

mailSender.send(new MimeMessagePreparator(){
public void prepare(MimeMessage message) throws Exception
{
MimeMessageHelper helper = new MimeMessageHelper(message);
helper.setTo("wfq@livedoor.cn");
helper.setFrom("wfq@livedoor.cn");
helper.setSubject("Spring Mail Test Without IoC");
helper.setText("Spring Mail is cool!");
}
});
~~~~~~~

或者，我们仅仅为了拦截某个对象的方法，而不是很普遍，那么我们也可以这样：

~~~~~~~ {.java}
BusinessImpl impl = new BusinessImpl();
BusinessBeforeAdvice advice = new BusinessBeforeAdvice();
// construct advisor in Regexp Way
RegexpMethodPointcutAdvisor advisor = new RegexpMethodPointcutAdvisor();
advisor.setAdvice(advice);
advisor.setPattern(".*doSomething.*");

ProxyFactory factory = new ProxyFactory();
factory.setTarget(impl);
factory.addAdvisor(advisor);
IBusiness bo = (IBusiness)factory.getProxy();
bo.doSomething();
~~~~~~~

怎么样？！是不是很容易那？

仅仅是这几天实在想不出写些什么，故此闲话些许。
