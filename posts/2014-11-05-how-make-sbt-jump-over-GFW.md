% SBT免翻墙手册
% FuqiangWang

簡単です，直接覆盖(Override)当前项目build文件中的fullResolvers就行了， 指向自己想要指向的repo地址， oh yeah！

~~~~~~~ {.scala}
fullResolvers := Seq(
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "SBT Plugins Release" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/",
  "Typesafe Ivy Release" at "http://repo.typesafe.com/typesafe/ivy-releases/",
  "Typesafe Maven Release" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Central Maven Repository" at "https://repo1.maven.org/maven2/"
)
~~~~~~~


