package com.andyr
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object LookingUpActorSelection extends App {
  val actorSystem = ActorSystem("LookingUpActors",ConfigFactory.load("client1")) //give it a port of 2552
  implicit val dispatcher = actorSystem.dispatcher

  val selection = actorSystem.actorSelection("akka.tcp://LookingUpRemoteActors@127.0.0.1:2553/user/remoteActor")
  selection ! "test"

  selection.resolveOne(3 seconds).onSuccess {
    case actorRef : ActorRef =>
      println("We got an ActorRef")
      actorRef ! "test"
  }
}


object LookingUpRemoteActors extends App {
  val actorSystem = ActorSystem("LookingUpRemoteActors",ConfigFactory.load("server")) //give it a port of 2553
  actorSystem.actorOf(Props[SimpleActor], "remoteActor")
}