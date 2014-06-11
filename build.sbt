name := "ddd-free-test"

scalaVersion := "2.10.4"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.0-M7",
  "org.scalaz" %% "scalaz-concurrent" % "7.1.0-M7",
  "com.chuusai" % "shapeless_2.10.4" % "2.0.0",
  "com.amazonaws" % "aws-java-sdk" % "1.7.8.1",
  "com.jolbox" % "bonecp" % "0.8.0.RELEASE",
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",  
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.5",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.1.0",
  "javax.mail" % "mail" % "1.4.7",
  "javax.activation" % "activation" % "1.1.1",
  "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test"
)

