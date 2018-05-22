package com.intel.analytics.zoo.webservicesample

import org.slf4j.LoggerFactory
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

//to package:     sbt assembly
//to run in sbt:  sbt run
//to run in jar:  java -jar /...jar
object Main extends App with HttpServerAble {
  override val logger = LoggerFactory.getLogger(getClass)

  val name = "analytics-zoo-web-service-sample"
  val actorSystemName = s"${name}-actor-system"
  val interface = "0.0.0.0"
  val httpPort = 10080

  override implicit val system = ActorSystem(actorSystemName)
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()(system)

  val route = commonRoute(name) ~
    (post & path("textclassification" / "predict") & extract(_.request.entity.contentType) & entity(as[String])) { (contentType, content) =>
      complete(content)
    } ~
    (post & path("imageclassification" / "predict") & extract(_.request.entity.contentType) & entity(as[String])) { (contentType, content) =>
      complete(content)
    }
    
  val models = Models()
  logger.info(s"all models successfully loaded.")
  
  Http().bindAndHandle(route, interface, httpPort)
  logger.info(s"web service ${name} successfully started at ${interface}:${httpPort}.")
}
