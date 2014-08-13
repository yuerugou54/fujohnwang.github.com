% Cobar分区函数扩展项目简介(cobar-extra-rules)
% 陨石

# 背景

对于随时间线性增长的数据集， 我们希望按照时间段的形式对数据集进行分区， cobar提供的一些默认分区函数无法满足这类场景需求，所以在参考现有分区函数实现的基础上，开发了两个新的分区函数用于此类场景。

考虑到其他人可能也会用到，遂作为二方/三方包提供并发布到maven repository中。


# cobar-extra-rules项目

主要提供两个扩展函数， 即com.alibaba.cobar.route.function.PartitionByRange和com.alibaba.cobar.route.function.PartitionByDateTimeRange

## com.alibaba.cobar.route.function.PartitionByRange

以数字型Column字段为分区标准(内部可强制转型为Java的Long型)，通过单独的配置文件指定数字区间：

~~~~~~~ {.xml}
  <function name="func" class="com.alibaba.cobar.route.function.PartitionByRange">
    <property name="rangeDefinitionFile">${config_file_path}</property>
  </function>
~~~~~~~

${config_file_path}指向的分区规则配置文件内容类似于：

<pre>
0, 2014 = 0
2014, = 1
</pre>

具体配置规则为：

1. 每一行代表一条路由规则； （以#或者//开头的行为注释， 不会作为路由规则）;
2. 每一条路由规则以=分割， 左边为数字区间， 右边为data node序号(shard index);
3. 路由规则左边以逗号分割， 第一部分为开始， 第二部分为结束， 其中对于区间来说，第一部分是inclusive关系， 第二部分为exclusive关系，除了最后一条， 其它行都严格遵循这一规则；最后一行可以只指定开始部分， 逗号和第二部分都可以省略， 默认最后一条的结束边界是Long.MAX_VALUE;
4. 路由规则右边是data node序号，即如果在schema.xml中顺序定义了多个data node，那么， 这些data node将按照顺序被赋予从0到n的序号；

有了以上规则之后， PartitionByRange将基本以如下逻辑对数据库访问进行路由：

~~~~~~~
if($col_value >= $open  && $col_value < $end) return $node_index;
~~~~~~~


## com.alibaba.cobar.route.function.PartitionByDateTimeRange
PartitionByRange处理路由的时候，接收的是数字型的参数， 对于数据库中DateTime类型的参数，我们需要从String形式转换为Java里的强类型，所以，没法重用PartitionByRange， 故此开发了PartitionByDateTimeRange， 它接收符合DateTime格式化的String类型参数， 然后决定如何路由数据库访问。

PartitionByDateTimeRange也是通过一个外部规则文件来配置：

~~~~~~~ {.xml}
  <function name="func" class="com.alibaba.cobar.route.function.PartitionByDateTimeRange">
    <property name="rangeDefinitionFile">${config_file_path}</property>
  </function>
~~~~~~~

配置文件内容格式与PartitionByRange的稍微有些小变动，但大体规则相同， 只是从以数字作为区间的开始和结束， 变成以格式化的时间的字符串作为区间的开始和结束 比如：

<pre>
2014-06-27 01:11:51, 2014-07-27 01:11:51=0
2014-07-27 01:11:51, = 1
</pre>

同样，最后一条区间结束可以不写，代表开区间。

# 数据扩展与迁移

因为数据属性是线性增长，所以，只要在某个区间即将饱和之前， 添加新的data node就可以保证数据的横向扩展。 当然， 添加了新的data node，需要在规则文件中同样反映出来。

所以， 数据的扩展包括：

1. 添加新的数据库节点；
2. 更改cobar配置和路由规则；
3. 重启cobar ^[原则上， 应该可以动态变更路由规则和动态添加数据节点，但暂时没必要以复杂性来换取便捷，因为扩展行为频繁程度不会很高]；

至于数据的迁移，采用时间范围的分区，基本不需要数据迁移， 只有新数据的添加。




