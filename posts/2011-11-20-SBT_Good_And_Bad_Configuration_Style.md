% SBT 0.11.x Good And Bad Configuration Style
% fujohnwang
% 2011-11-20

```scala
object Bad extends Build { 
	libraryDependencies += "junit" % "junit" % "4.8" % "test"
}
```


```scala
object Good extends Build{ 
	lazy val root = Project("root", file(".")) settings( libraryDependencies += "junit" % "junit" % "4.8" % "test" )
}
```


