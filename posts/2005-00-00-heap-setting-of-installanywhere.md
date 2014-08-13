% 在InstallAnyWhere中设置java程序启动的heap size
% FuqiangWang

> 2014年从msn space存档中重新恢复出来！

查过InstallAnywhere的文档之后发现，在installAnywhere的install task中，针对每一个LaunchAnywhere，都可以“Edit Properties”,在这里，可以制定java程序启动的heap size，属性为：

[b]lax.nl.java.option.java.heap.size.initial[/b]

 Defines the initial heap size for the installer that will be invoked. This number is always specified in bytes, not in kilobytes or megabytes, and is analogous to the VM parameter ‘-ms’ or ‘Xms’. The default is 16777216 (16 MB).
 
[b]lax.nl.java.option.java.heap.size.max[/b]

 Defines the maximum heap size in bytes for the installer that will be invoked. This number is always specified in bytes, not in kilobytes or megabytes, and is analogous to the VM parameter ‘-mx’ or ‘Xmx’. The default is 50331648 (48 MB).
 

所以，如果说要实现类似-Xms128m  -Xmx256m的功能，在InstallAnywhere中只要设置这两个属性就可以了。:em510: