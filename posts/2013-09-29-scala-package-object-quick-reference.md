% scala package object quick reference
% 王福强

# 功效
定义在package object中的变量，方法，函数等都可以被这个package下所有的类定义所用！

# 步骤
假设我们想定义com.github.fujohnwang.demo这个package对应的package object，那么，我们可以：

## 第一步: 确定文件位置

在目录com/github/fujohnwang/demo下新建package.scala文件；

## 第二步：确定文件内容

文件定义中，前面的声明如下:

```scala
package com.github.fujohnwang;

package object demo{
  // ...
}
```

即package object的名称为当前包的名字(demo)， 而声明在放到上一级package中（这里为什么容易混淆，就是因为，实际上，当前的定义文件package.scala不是放在文件开头的package声明对应的目录下，而是当前package目录下）


## 第三步： 使用
在要使用package object中定义的变量，方法或者函数之前， `import com.github.fujohnwang.demo._`即可。