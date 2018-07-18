package com.wms.core.ui;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Scope;

@Scope(value = "prototype")
public class AbstractController {
    private SparkSession sparkSession;

    public SparkSession getSparkSession() {
        return sparkSession;
    }

    public void setSparkSession(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }
}
