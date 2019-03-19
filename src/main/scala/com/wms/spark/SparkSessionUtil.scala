package com.wms.spark

import com.wms.core.utils.common.ResourceBundleUtil
import org.apache.spark.{SparkConf, TaskContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
  * Created by hasee on 2017/4/19.
  */
class SparkSessionUtil {
  def makeSparkSession(appName:String):SparkSession = {
    val sparkSession = SparkSession.builder().appName(appName)
      //.config("spark.driver.extraClassPath", ResourceBundleUtil.getString("default", "SPARK_EXTRACLASSPATH"))
      //.config("spark.executor.extraClassPath", ResourceBundleUtil.getString("default", "SPARK_EXTRACLASSPATH"))
      .config("spark.executor.memory","1024m")
      .master(ResourceBundleUtil.getString("application", "spark.master"))
      .getOrCreate()
    sparkSession
  }

  def makeStreamContext(appName:String,period:Long)={
    val sparkConf = new SparkConf().setMaster(ResourceBundleUtil.getString("application", "spark.master")).setAppName(appName)
    val streamingContext = new StreamingContext(sparkConf,Seconds(period))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> appName,
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("test")

    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    val lines = stream.map((record: ConsumerRecord[String, String]) => {
          System.out.println(record.key)
          System.out.println(record.value)
          (record.key, record.value)
      })
    val words = lines.map(_._2).flatMap(_.split(" ")).map(x => (x,1))
    words.reduceByKey(_+_).print()

    stream.foreachRDD { rdd =>
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      rdd.foreachPartition { iter =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }
      // some time later, after outputs have completed
      stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
    }
    streamingContext.start()
    streamingContext.awaitTermination()
    streamingContext
  }
}