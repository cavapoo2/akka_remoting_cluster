import sbt._
import Keys._
object Dependencies {
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.1"
  val akka_remote = "com.typesafe.akka" %% "akka-remote" % "2.5.3"
  val akka_cluster = "com.typesafe.akka" %% "akka-cluster" % "2.5.4"
  val akka_cluster_tools = "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.4"
}
