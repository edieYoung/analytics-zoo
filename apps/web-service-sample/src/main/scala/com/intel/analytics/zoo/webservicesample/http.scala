package com.intel.analytics.zoo.webservicesample

import org.slf4j.LoggerFactory
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.respondWithHeaders
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.Await
import akka.http.scaladsl.Http
import scala.concurrent.duration.Duration
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling.Unmarshal

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

trait HttpClientAble {
  val logger = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def process(request: HttpRequest): Option[Array[Byte]] = {
    logger.debug(s"[HttpClientAble] to handle http request: ${request.method.value} ${request.uri}")
    val future = Http().singleRequest(request)
    val response = Await.result(future, Duration.Inf)
    response.status.intValue() / 100 match {
      case 2 => Some(Await.result(Unmarshal(response.entity).to[Array[Byte]], Duration.Inf))
      case _ => None
    }
  }
}