package com.wms.spark

import com.wms.core.utils.common.ResourceBundleUtil;
import org.apache.spark.sql.SparkSession

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
}