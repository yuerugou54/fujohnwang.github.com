% SBT释疑
% 王福强

# project子目录与当前项目目录的关系

> In sbt, any .scala file in the project/ directory can be used as a library in your build.

可以简单认为当前项目目录下的project目录是一个lib目录，这个project目录下任何*.scala文件定义的东西，都是当前项目的build.sbt中可以依赖使用的。

即build.sbt  ---dependsOn---> project/*.scala，所以， 很多build.sbt中如果定义之后感觉混乱的东西，可以抽取到这些文件中定义然后引用。

project目录内可以再嵌套project， 相当于俄罗斯娃娃那样， 每一个都是合法的sbt项目。

