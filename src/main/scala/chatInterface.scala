package com.andyr
import akka.actor.{Actor, ActorRef, Props, ActorLogging}
import com.andyr.ChatServer.Disconnect
import scala.io.StdIn._

object ChatClientInterface {
  case object Check

  def props(chatClient: ActorRef) = Props(new ChatClientInterface(chatClient))
}

class ChatClientInterface(chatClient: ActorRef) extends Actor with ActorLogging {
  import ChatClientInterface._
  //import context._

  override def preStart() = {
    println("You are logged in. Please type and press enter to send messages. Type 'DISCONNECT' to log out.")
    log.info("Interface pre start")
    self ! Check
  }

  def receive = {
    case Check =>
      readLine() match {
        case "DISCONNECT" =>
          chatClient ! Disconnect
          println("Disconnecting...")
          context.stop(self)
        case msg:String =>
          if (!msg.isEmpty) {
            log.info(s"$msg,$chatClient")
            chatClient ! msg
          }
          self ! Check
          //context.system.scheduler.scheduleOnce(2 second,self, Check)
        case _ =>
          log.info("Nothing..")
          self ! Check
          //context.system.scheduler.scheduleOnce(5 second,self, Check)
      }
  }
}
