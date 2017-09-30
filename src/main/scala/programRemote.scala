package com.andyr
import akka.actor.{ActorSystem, Address, Deploy, Props}
import akka.remote.RemoteScope
import com.typesafe.config.ConfigFactory

//start this first
object RemoteActorsProgrammatically1 extends App {
  val actorSystem = ActorSystem("RemoteActorsProgramatically1",ConfigFactory.load("master")) // port of 2552
}
 // this first creates its own system on 2556, then simple actor on 2552 system, and sends messag to it from 2556
object RemoteActorsProgrammatically2 extends App {
  val actorSystem = ActorSystem("RemoteActorsProgramatically2",ConfigFactory.load("client1")) // port of 2556
  println("Creating actor from RemoteActorsProgramatically2")
  val address = Address("akka.tcp", "RemoteActorsProgramatically1", "127.0.0.1", 2552) // this gives the same
  val actor = actorSystem.actorOf(Props[SimpleActor].withDeploy(Deploy(scope = RemoteScope(address))), "remoteActor")
  actor ! "Checking"
}