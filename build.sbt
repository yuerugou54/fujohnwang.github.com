name := "site-builder"

version := "1.0"

scalaVersion := "2.12.4"

scalacOptions := Seq("-deprecation", "-unchecked")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.1"

libraryDependencies += "net.java.dev.rome" % "rome" % "1.0.0"

mainClass := Some("SiteBuilder")