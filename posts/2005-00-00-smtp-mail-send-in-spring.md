% 在spring中发信的时候
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

以前使用spring发信的时候，只是简单的为MailSender设置host,username和password属性就OK，但是今天却遇到了发信失败异常的一个553ReturnCode，而且日志中主要的信息也都是tmd的问号，搞得我很郁闷，不知道问题出在什么地方。后来问过系统部是否在MailServer上加了什么限制，系统部说只是添加了一个smtp认证，原来如此啊！所以，在MailSender的配置属性中加入了一个properties属性搞定。


~~~~~~~ {.xml}
<bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
<property name="host">
<value>mail.livedoor.cn</value>
</property>
<property name="username">
<value>XXX</value>
</property>
<property name="password">
<value>XXX</value>
</property>
<property name="port">
<value>25</value>
</property>
<property name="javaMailProperties">
<props>
<prop key="mail.smtp.auth">true</prop>
</props>
</property>
</bean>
~~~~~~~
