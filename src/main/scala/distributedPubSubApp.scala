package com.andyr
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

object DistributedPubSubApplication extends App {
  if (args.size != 1)
    {
      println("enter 1 or 2 to choose config file")
      System.exit(1)
    }

  val actorSystem = ActorSystem("ClusterSystem",ConfigFactory.load("shard"+args(0)))
  val cluster = Cluster(actorSystem)

  val notificationSubscriber = actorSystem.actorOf(Props[NotificationSubscriber])
  val notificationPublisher = actorSystem.actorOf(Props[NotificationPublisher])

  val clusterAddress = cluster.selfUniqueAddress
  val notification = Notification(s"Sent from $clusterAddress", "Test!")

  import actorSystem.dispatcher
  actorSystem.scheduler.schedule(Random.nextInt(5) seconds, 5 seconds, notificationPublisher, notification)
}
