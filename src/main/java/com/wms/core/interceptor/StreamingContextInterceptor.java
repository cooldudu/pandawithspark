package com.wms.core.interceptor;

import com.wms.Application;
import com.wms.core.ui.AbstractController;
import com.wms.core.utils.common.ObjectUtils;
import com.wms.core.utils.datetime.DateTimeUtil;
import com.wms.spark.SparkSessionUtil;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.sql.SparkSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Aspect
@Order(20)
@Component
public class StreamingContextInterceptor {
    @Around("@annotation(com.wms.core.annotation.UseSparkStreamingContext)")
    public Object doSparkSession(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object returnValue = null;
        var signature = (MethodSignature) proceedingJoinPoint
                .getSignature();
        var controller = (Application) proceedingJoinPoint
                .getTarget();
        var formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        var className = proceedingJoinPoint.getTarget().getClass()
                .getName();
        var methodName = signature.getMethod().getName();
        var currentTime = formatter.format(DateTimeUtil.getNow());
        var sc = new SparkSessionUtil().makeStreamContext(className + methodName + currentTime,10L);
        try {
            returnValue = proceedingJoinPoint.proceed();
        } catch (Exception ex) {
            sc.stop(true);
            ex.printStackTrace();
            LogFactory.getLog(SparkSession.class).error(ex.getMessage());
        } finally {

        }
        return returnValue;
    }
}
