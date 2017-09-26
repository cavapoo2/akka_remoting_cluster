package com.andyr
import akka.actor.{Actor, ActorRef, Props,ActorLogging}
import com.andyr.ChatServer.{Connect, Disconnect, Disconnected, Message}
import akka.pattern.ask
import akka.pattern.pipe

import scala.concurrent.duration._
import akka.util.Timeout

object ChatClient {
  def props(chatServer: ActorRef) = Props(new ChatClient(chatServer))
}

class ChatClient(chatServer: ActorRef) extends Actor with ActorLogging{
  import context.dispatcher

  implicit val timeout = Timeout(5 seconds)

  override def preStart = {
    log.info("Client pre start")
    chatServer ! Connect
  }

  def receive = {
    case Disconnect =>
      log.info("Client Disconnect")
      (chatServer ? Disconnect).pipeTo(self)
    case Disconnected =>
      log.info("Client Disconnected")
      context.stop(self)
    case body : String =>
      log.info("Client String")
      chatServer ! Message(self, body)
    case msg : Message =>
      log.info("Client Message")
      println(s"Message from [${msg.author}] at [${msg.creationTimestamp}]: ${msg.body}")
  }
}
