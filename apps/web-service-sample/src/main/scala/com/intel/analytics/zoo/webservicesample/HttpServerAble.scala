package com.intel.analytics.zoo.webservicesample

import scala.concurrent.ExecutionContextExecutor

import org.slf4j.LoggerFactory

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.respondWithHeaders

trait HttpServerAble {
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def commonRoute(serviceName: String) =
    path("") {
      complete("welcome to " + serviceName)
    }
}
