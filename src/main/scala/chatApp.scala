package com.andyr
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.config.ConfigFactory
import scala.util.{Success,Failure}

//use sbt assembly and run this via java - jar assemblyname.jar 1
//note only use 2 clients here, add more client confs if need more
import scala.concurrent.duration._

object ChatClientApplication {
  def main(args: Array[String]) {
    if (args.length != 1) {
      println("must enter a port number between 1 to 10")
      System.exit(1)
    }
    val confile = "client" + args(0)
    val actorSystem = ActorSystem("ChatServer", ConfigFactory.load(confile))
    implicit val dispatcher = actorSystem.dispatcher
    val chatServerAddress = "akka.tcp://ChatServer@127.0.0.1:2553/user/chatServer"
    val f = actorSystem.actorSelection(chatServerAddress).resolveOne(3 seconds)
    f onComplete {
      case Success(chatServer: ActorRef) =>
        val client = actorSystem.actorOf(ChatClient.props(chatServer), "chatClient" + args(0))
        actorSystem.actorOf(ChatClientInterface.props(client), "chatClientInterface")
      case Success(x) => println(s"Failed $x")
      case Failure(e) => println(s"Failed e $e")
    }
  }
}
