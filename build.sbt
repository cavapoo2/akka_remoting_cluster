import Resolvers._
import Dependencies._
// factor out common settings into a sequence
lazy val buildSettings = Seq(
 organization := "com.andyr",
 version := "0.1.0",
 scalaVersion := "2.12.2"
)
// Sub-project specific dependencies
lazy val commonDeps = Seq(
 scalatest % Test,
 akka_remote,
 akka_cluster,
 akka_cluster_tools
)
lazy val root = (project in file(".")).
  //aggregate(foo,bar).
  //dependsOn(foo,bar).
  settings(buildSettings: _*).
 settings(
 mainClass in assembly := Some("com.andyr.ChatClientApplication"),
  resolvers := oracleResolvers,
  libraryDependencies ++= commonDeps 
 )
//needed since seems like an issue with sbt run and loading a wav file
//so this will spawn a seprate jvm. one for sbt the other for the 
//running program. (I think)
fork in run := true
//lazy val foo = (project in file("Foo"))  
//lazy val bar = (project in file("Bar"))
