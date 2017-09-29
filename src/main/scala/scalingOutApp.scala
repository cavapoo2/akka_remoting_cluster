package com.andyr
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object ScalingOutWorker extends App {
  if (args.size != 1)
    {
      println("enter a 1 or 2 to choose config file")
      System.exit(1)
    }
  println("using:" + args(0))
  val actorSystem = ActorSystem("WorkerActorSystem",ConfigFactory.load("worker" + args(0))) //gives this port of 2554
  implicit val dispatcher = actorSystem.dispatcher

  val selection = actorSystem.actorSelection("akka.tcp://MasterActorSystem@127.0.0.1:2552/user/masterActor")
  selection.resolveOne(3 seconds).onSuccess {
    case masterActor : ActorRef =>
      println("We got the ActorRef for the master actor")
      val pool = RoundRobinPool(10)
      val workerPool = actorSystem.actorOf(Props[WorkerActor].withRouter(pool), "workerActor")
      masterActor ! RegisterWorker(workerPool)
  }
}


object ScalingOutMaster extends App {
  val actorSystem = ActorSystem("MasterActorSystem",ConfigFactory.load("master")) //this has port of 2552
  val masterActor = actorSystem.actorOf(Props[MasterActor], "masterActor")

  (1 to 100).foreach(i => {
    masterActor ! Work(s"$i")
    Thread.sleep(5000) //Simulates sending work to the master actor every 5 seconds
  })

}