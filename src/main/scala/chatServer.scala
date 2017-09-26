package com.andyr
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory

object ChatServer {
  case object Connect
  case object Disconnect
  case object Disconnected
  case class Message(author: ActorRef, body: String, creationTimestamp : Long = System.currentTimeMillis())

  def props = Props(new ChatServer())
}

class ChatServer extends Actor with ActorLogging{
  import ChatServer._
  var onlineClients = Set.empty[ActorRef]

  def receive = {
    case Connect =>
      log.info("Sever Connect")
      onlineClients += sender
      context.watch(sender)
    case Disconnect =>
      log.info("Sever Disconnect")
      onlineClients -= sender
      context.unwatch(sender)
      sender ! Disconnected
    case Terminated(ref) =>
      log.info("Sever Terminated")
      onlineClients -= ref
    case msg: Message =>
     // log.info("Sever Message")
      onlineClients.filter(_ != sender).foreach(_ ! msg)
  }
}
//run this via sbt run and choose the server program
object ChatServerApplication extends App {
  val actorSystem = ActorSystem("ChatServer",ConfigFactory.load("server"))
  println(actorSystem.name)
  actorSystem.actorOf(ChatServer.props, "chatServer")

  //println(actorSystem./("test"))
}
