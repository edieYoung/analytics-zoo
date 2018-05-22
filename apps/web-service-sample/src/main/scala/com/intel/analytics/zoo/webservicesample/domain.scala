package com.intel.analytics.zoo.webservicesample

import org.slf4j.LoggerFactory
import com.intel.analytics.bigdl.optim.LocalPredictor
import com.intel.analytics.zoo.models.common.ZooModel
import com.intel.analytics.bigdl.nn.abstractnn.Activity
import akka.stream.Materializer
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContextExecutor
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.model.HttpMethods
import java.io.ByteArrayInputStream
import java.io.BufferedInputStream
import java.io.ObjectInputStream
import com.intel.analytics.bigdl.nn.abstractnn.AbstractModule

object Paths {
  val IMAGE_CLASSIFICATION_IMAGENET_ALEXNET = "https://s3-ap-southeast-1.amazonaws.com/analytics-zoo-models/imageclassification/imagenet/analytics-zoo_alexnet_imagenet_0.1.0"
}

case class Models(implicit val system: ActorSystem, implicit val executor: ExecutionContextExecutor, implicit val materializer: Materializer) extends Supportive with HttpClientAble {
  override val logger = LoggerFactory.getLogger(getClass)

  val IMAGE_CLASSIFICATION_IMAGENET_MODEL_ALEXNET = timing("loadModelAndCreatePredictor for alexnet")(loadModelFromHttp(Paths.IMAGE_CLASSIFICATION_IMAGENET_ALEXNET))

  def loadModelAndCreatePredictor[T](path: String) = {
    logger.info(s"load model from $path")
    val model = ZooModel.loadModel[Float](path)
    logger.info(s"loaded model as $model")
    val localPredictor = LocalPredictor(model)
    logger.info(s"created localPredictor as $localPredictor")
    localPredictor
  }

  def loadModelFromHttp[T](uri: String) = {
    val request = HttpRequest(method = HttpMethods.GET, uri = Uri(uri))
    val model = process(request) match {
      case Some(bytes) => {
        val inputStream = new ByteArrayInputStream(bytes)
        val bufferedInputStream = new BufferedInputStream(inputStream)
        val objectInputStream = new ObjectInputStream(bufferedInputStream)
        val module = objectInputStream.readObject().asInstanceOf[AbstractModule[Activity, Activity, T]]
        val model = module.asInstanceOf[ZooModel[Activity, Activity, T]]
        logger.info(s"model loaded as $model")
        Some(model)
      }
      case None => None
    }

  }
}

trait Supportive {
  val logger = LoggerFactory.getLogger(getClass)

  def timing[T](name: String)(f: => T): T = {
    val begin = System.currentTimeMillis
    val result = f
    val end = System.currentTimeMillis
    val cost = (end - begin) / 1000
    logger.info(s"$name cost for $cost s.")
    result
  }
}