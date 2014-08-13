% 谈java中的动态条件查询（Dynamic Criteria Query In Java）
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

偶然的机会，发现开发中的这个共通的问题---动态条件查询，故此决定结合自己当初的开发方式以及网上各种观点，对这个问题点作一个分析和总结，以供参考。
在我们的开发过程中，经常需要面对各种数据的查询需求，比如说检索顾客信息，根据业务视图抽取相关数据做成报表等等，而对于这些查询，有的时候查询条件是固定的，比如说检索所有的顾客；但有的时候则不然，查询条件会不固定，像用户可以根据需求选择相应的查询条件，比如这次要根据姓名查询，下次可能就会根据年龄段来查询等等，像这类问题，查询的处理就会比固定条件查询要复杂一些，所以，下面我们就对这种动态查询的情况做一个总结，以期引入更多观点来完善相应问题的解决方式。
为了说明各种方式的差异，我们需要一个实例来作为比较的标准，所以，假设我们拥有以下查询条件画面（因为只是实例，所以无论从画面还是表结构上都做了很大的简化，实际项目中要复杂的多）：

<pre>
-------------------------- ---
顾客姓名｜ ｜ ｜暧｜
-------------------------- ---
-------------------------
电话号码 ｜ ｜
-------------------------
+--------------------------------------+
| ［X］家庭固定电话 ［X］移动电话 |
| ［X］亲属电话1 ［X］亲属电话2 |
| ［X］工作地电话1 ［X］工作地电话2 | 
+--------------------------------------+
</pre>

# 查询需求

1. 用户可以输入顾客姓名进行查询，默认查询模式为模糊查询，如果用户点击［暧］按钮，可以在［暧］－［前］－［后］－［全］四种模式中选择，分别进行模糊查询，前向匹配查询，后向匹配查询和完全匹配查询；如果用户没有输入顾客姓名，则顾客姓名不加入查询条件；
2. 用户可以输入电话号码进行查询
	1. 用户只是输入电话号码，而没有选择下方group中的相应电话类型，则查询条件中不加入电话号码的条件限制；
	2. 用户没有输入电话号码，不管选择还是没有选择下方group中的相应电话类型，则查询条件不加入电话号码条件限制；
	3. 用户输入了电话号码，并选择了下方group中的最少一项电话类型，加入电话号码和电话类型的查询条件进行检索。
3. 如果用户没有添加任何查询条件，进行全检索。



# 查询需求的实现方式

## SQL语句的字符串拼接（SQL String Concatenation)

这种方式是从我大学时期做兼职时代就开始的一种实现方式，在我没有找到更好的解决方式之前，也是我解决类似问题的唯一方式，缺点自然不用多说，各种条件的判断纠缠在一起，后期维护很难；但是，如果后期不会加入太多新的查询条件的话，测试完成后就基本可以不用管了（对了，说到测试，这东西也很难测试的哦！）。
下面是对于实例画面给出的一个参考拼接结果，当然不是唯一的，仅作参考：

~~~~~~~ {.java}
StringBuffer criteria = new StringBuffer();
criteria.append("SELECT DISTINCT CustomerID FROM Mastercustomer as cust WHERE ");
int flag = 0;
String surnameKanji = model.getSurNameKanji();
if(!CustomerValidator.isBlank(surnameKanji))
{
switch(model.getSurNameKanjiFlag())
{
case CmpQueryState.LEFT_MATCH_STATE:
criteria.append("CUSTOMERNAME LIKE '"+surnameKanji+"%' AND ");
break;
case CmpQueryState.RIGHT_MATCH_STATE:
criteria.append("CUSTOMERNAME LIKE '%"+surnameKanji+"' AND ");
break;
case CmpQueryState.ALL_MATCH_STATE:
criteria.append("CUSTOMERNAME = '"+surnameKanji+"' AND ");
break;
case CmpQueryState.AMBIGUOUS_MATCH_STATE:
criteria.append("CUSTOMERNAME LIKE '%"+surnameKanji+"%' AND ");
break;
}
flag++;
}
String tel = model.getPhoneNum();
if(!CustomerValidator.isBlank(tel))
{
tel = telFormatter.format(tel);
int innerflag = 0;
criteria.append("( ");
if(model.isHomeTelSelected())
{
criteria.append("APPLHOMETEL = '"+tel+"' OR ");
innerflag++;
}
if(model.isKin1TelSelected())
{
criteria.append("KIN1HOMETEL = '"+tel+"' OR ");
innerflag++;
}
if(model.isKin2TelSelected())
{
criteria.append("KIN2HOMETEL = '"+tel+"' OR ");
innerflag++;
}
if(model.isOffice1TelSelected())
{
criteria.append("APPLWRK1TEL = '"+tel+"' OR ");
innerflag++;
}
if(model.isOffice2TelSelected())
{
criteria.append("APPLWRK2TEL = '"+tel+"' OR ");
innerflag++;
}
if(model.isMobile1Selected())
{
criteria.append("APPLMOBILE1TEL = '"+tel+"' OR ");
innerflag++;
}
//-----------DELETE USELESS WORDS---------------
if(innerflag == 0)
{
criteria.delete(criteria.length()-2,criteria.length());
}
else
{
criteria.delete(criteria.length()-3,criteria.length());
criteria.append(" ) AND ");
flag++;
}
}
// finally
if(flag == 0)
{
// In this way, the user select no query constraint field
// we need to delete the "WHERE" from the StringBuffer's end 
criteria.delete(criteria.length()-6 , criteria.length());
}
else
{
// here, the user select one or more query constraint field,
// we need to delete the "AND" from the StringBuffer's end
criteria.delete(criteria.length() - 4 , criteria.length());
}
return criteria.toString(); 
~~~~~~~

可能一些地方还可以节俭，但你还是可以看出，这种方式是多么的复杂，不仅要维护条件的上下文，而且还要根据情况添加查询条件，我想你看到这么多的条件判断语句已经很faint了吧，呵呵，不过这还只是一个简单的查询页面，想想一个页面上几十甚至上百个的查询条件，这种方式恐怖你就可想而知了，开发效率，健壮性，可维护性，这些都是问题啊...

但，我想，有些时候，如果其他方式无法解决的话，这也只能是你的last resort了。

> NOTE：这种方式虽然复杂，但是同时也可以给你最大的灵活性，“路怎么走，你看着办咯”

另外，实际项目中，出于安全性考虑，最好对SQL进行escape，以防止SQL injection攻击等,原型就是原型，我们这里不可能面面具到的。


## iBatis的DynamicSQL支持

iBatis针对这种动态查询提供了一种DynamicSQL的支持，通过在其SQLMap中定义查询条件，减少抽取逻辑和程序之间的耦合，而且，这种SQL的组装是通过XML来完成的，通过合理的处理，相对于SQL语句拼接方式来说，开发效率上更胜一筹。

让我们来看一下相应于实例画面的查询，DynamicSQL是如何实现的吧：

~~~~~~~ {.xml}
<statement id="yourQuery" resultMap="yourRetMap">
SELECT DISTINCT CustomerID FROM Mastercustomer as cust
<dynamic prepend="WHERE">
<isNotEmpty property="customerName" prepend="AND">
<isEqual property="customerNameSearchMode" compareValue="0">CUSTOMERNAME LIKE '#customerName#%'</isEqual>
<isEqual property="customerNameSearchMode" compareValue="1">CUSTOMERNAME LIKE '%#customerName#'</isEqual>
<isEqual property="customerNameSearchMode" compareValue="2">CUSTOMERNAME = '#customerName#'</isEqual>
<isEqual property="customerNameSearchMode" compareValue="3">CUSTOMERNAME LIKE '%#customerName#%'</isEqual>
</isNotEmpty>
<isNotEmpty property="telNum" prepend="AND">
<iterate property="telTypeList" open="(" close=")" conjunction="OR">
telNumber=#telTypeList[]#
</iterate>
</isNotEmpty>
</dynamic>
</statement>
~~~~~~~

更多信息可以参考iBatis提供的Reference...

btw.个人还是很看中这种方式的

## Hibernate的(Detached)Criteria或者HQL拼接

如果你的系统当前的persistence层用的是Hibernate的话，那恭喜你，在你享有hibernate当前便利的前提下，针对这种动态查询问题，你还会享有hibernate提供的(Detached)Criteria或者HQL灵活拼接的支持。

只要你将相应的SearchContext中的查询条件设置到(Detached)Criteria中，那么你就可以直接取得你需要的查询结果就可以了，所有的什么拼接啦，查询结果组装啦什么乱七八糟的事情统统全免，是不是很惬意那？！不过，前提是你的系统persistence用的是hibernate，而且，实际上，(Detached)Criteria也不是万能药，对于复杂的查询，他也依然无能为力，所以，这个时候，不好意思，你还是的求助于字符串拼接的方式，不过，这回不是SQL的拼接了，而是HQL的拼接，不过原理是一样的，这里就不做赘述了，下面只是列出使用(Detached)Criteria的实现代码片段以供参考：

~~~~~~~ {.java}
DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Mastercustomer.class); 
// ... 根据情况取得相应的Criterion,如Criterion nameCriteria = Restrictions.eq("customerName",customerName);
detachedCriteria.add(nameCriteria);
if(notEmpty(telNum))
{
Disjunction disjunction = Restrictions.disjunction();
for（int i=0,size=telTypeList.size;i<size;i++)
{
disjuction.add(Restrictions.eq("telNum",telTypeList.get(i)));
}
detachedCriteria.add(disjunction);
}
Criteria criteria = detachedCriteria.getExecutableCriteria(session);
return criteria.list(); 
~~~~~~~


以上就是我本人对于这种动态查询条件相关问题解决方式的几点认识，如有谬误，还望指正。希望以上文字可以为大家解决相关问题提供一定的思路和解决问题的方向，如果大家还有什么更好的解决方式，也可以放到网上与大家共享，毕竟现在是互联网的时代 :->

# 篇后语

感谢Sun Java Forums 和javaEye Forum中的开发者共享他们的观点，使我能够可以了解更多相关信息并促成这篇文字的诞生，同时也要感谢万维网和google的支持，没有他们，我也无法顺利的形成这篇文字并将其与大家分享...

# 参考文献
1. 《iBatis SqlMap Developer Guide 2.0》
2. 《Hibernate Reference》
