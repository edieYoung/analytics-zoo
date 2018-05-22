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

case class Models(modelHome: String) extends Supportive {
  override val logger = LoggerFactory.getLogger(getClass)
  
  val PATH_IMAGE_CLASSIFICATION_IMAGENET_ALEXNET = s"$modelHome/imageclassification/imagenet/analytics-zoo_alexnet_imagenet_0.1.0"

  val MODEL_IMAGE_CLASSIFICATION_IMAGENET_ALEXNET = timing("loadModelAndCreatePredictor for alexnet")(loadModelAndCreatePredictor(PATH_IMAGE_CLASSIFICATION_IMAGENET_ALEXNET))

  def loadModelAndCreatePredictor[T](path: String) = {
    logger.info(s"load model from $path")
    val model = ZooModel.loadModel[Float](path)
    logger.info(s"loaded model as $model")
    val localPredictor = LocalPredictor(model)
    logger.info(s"created localPredictor as $localPredictor")
    localPredictor
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