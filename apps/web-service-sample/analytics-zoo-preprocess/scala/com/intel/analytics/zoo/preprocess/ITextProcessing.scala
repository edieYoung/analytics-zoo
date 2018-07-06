package com.intel.analytics.zoo.preprocess

import java.util.{List => JList}
import java.lang.{Float => JFloat}
import java.lang.{Integer => JInt}
import java.util.{Map => JMap}


import scala.collection.mutable.{Map => MMap}
import com.intel.analytics.zoo.pipeline.inference.JTensor

import scala.collection.mutable.ArrayBuffer


import scala.collection.JavaConverters._
import collection.JavaConversions._



abstract class ITextProcessing extends TextProcessing {
  def tokenize(text: String): JList[String] = {
    doTokenize(text).asJava
  }
  def stopWords(tokens: JList[String], stopWordsCount: JInt): JList[String] = {
    doStopWords(tokens.asScala.toList, stopWordsCount.asInstanceOf[Int]).asJava
  }
  def stopWords(tokens: JList[String], stopWords: JList[String]): JList[String] = {
    doStopWords(tokens.asScala.toList,stopWords.asScala.toList).asJava
  }
  def shaping(tokens: JList[String], sequenceLength: JInt): JList[String] = {
    doShaping(tokens.asScala.toList,sequenceLength.asInstanceOf[Int]).asJava
  }
  def loadEmbedding(embDir: String): JMap[String, JList[JFloat]] = {
    val tempMap = doLoadEmbedding(embDir)
    val JavaMap = MMap[String, JList[JFloat]]()

    tempMap.keys.foreach( ( key ) => {
      val tempArray = ArrayBuffer[JFloat]()
      for(tempFloat <- tempMap(key)){
        tempArray.add(tempFloat.asInstanceOf[JFloat])
      }
      JavaMap.put(key, tempArray.toArray.toList.asJava)
    })
    JavaMap.toMap.asInstanceOf[JMap[String, JList[JFloat]]]

  }

  def vectorize(tokens: JList[String], embeddingMap: JMap[String, JList[JFloat]]): JList[JList[JFloat]] = {
    doVectorize(tokens.asScala.toList,embeddingMap.asInstanceOf[Map[String, List[Float]]]).asInstanceOf[JList[JList[JFloat]]]
    //java map to scala map
    val scalaMap = MMap[String, List[Float]]()
    embeddingMap.keys.foreach( key => {
      val tempArray = ArrayBuffer[Float]()
      for (tmpFloat <- embeddingMap(key)) {
        tempArray.add(tmpFloat.asInstanceOf[Float])
      }
      scalaMap.put(key, tempArray.toArray.toList)
    })

    val tempOutput = doVectorize(tokens.asScala.toList, scalaMap.toMap)

    //scala map to java map
    val output = ArrayBuffer[JList[JFloat]]()
    for (tempList <- tempOutput) {
      val javaArray = ArrayBuffer[JFloat]()
      for(tempFloat <- tempList){
        javaArray.add(tempFloat.asInstanceOf[JFloat])
      }
      output.add(javaArray.toArray.toList.asJava)
    }
    output.toArray.toList.asJava
  }

  def preprocess(text: String): JTensor={
    val inputList = doPreprocess(text)
    val tempArray = ArrayBuffer[JList[JFloat]]()
    for (tempList <- inputList) {
      val javaList = ArrayBuffer[JFloat]()
      for(tempFloat <- tempList) {
        javaList.add(tempFloat.asInstanceOf[JFloat])
      }
      tempArray.add(javaList.toArray.toList.asJava)
    }

    val input = tempArray.toArray.toList.asJava
    val data = input.flatten
    val shape = List(input.size().asInstanceOf[JInt],input.get(0).length.asInstanceOf[JInt])
    val tensorInput = new JTensor(data.asJava, shape.asJava)
    tensorInput
  }

}
