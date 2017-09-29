package com.andyr
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.actor._
import akka.util.Timeout
import akka.routing.RoundRobinPool
import akka.pattern.ask
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

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
  def jobQueue(jobs:List[Int]): Unit = {
    //should send a message to see if there are any workers first. if so then send them jobs
    //alternatively the job could be queued by the master until workers are available
    implicit val timeout = Timeout(5 seconds)
    if (!jobs.isEmpty)
    {
      val j = jobs.head
      val f = masterActor ? Work(s"$j")
      f.onComplete {
        case Success(value) => {
          value match {
            case NoWorkers =>
              println("NoWorkers yet")
              Thread.sleep(5000) // just simulate some delay
              jobQueue(jobs)
            case WorkersAvail =>
              println("Workers Avail")
              jobQueue(jobs.tail)
          }
        }
        case Failure(e) => println(s"Fail $e")
      }
    }
  }
  //TODO create another jobQueue func that uses Await.result as well
  jobQueue((1 to 100).toList)

}