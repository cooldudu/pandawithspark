package com.wms.core.interceptor;

import java.text.SimpleDateFormat;

import com.wms.core.ui.AbstractController;
import org.apache.commons.logging.LogFactory;
import org.apache.spark.sql.SparkSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.wms.core.utils.common.ObjectUtils;
import com.wms.core.utils.datetime.DateTimeUtil;
import com.wms.spark.SparkSessionUtil;

@Aspect
@Order(20)
@Component
public class SparkSessionInterceptor {
    @Around("@annotation(com.wms.core.annotation.UseSparkSession)")
    public Object doSparkSession(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object returnValue = null;
        var signature = (MethodSignature) proceedingJoinPoint
                .getSignature();
        var controller = (AbstractController) proceedingJoinPoint
                .getTarget();
        var formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        var className = proceedingJoinPoint.getTarget().getClass()
                .getName();
        var methodName = signature.getMethod().getName();
        var currentTime = formatter.format(DateTimeUtil.getNow());
        controller.setSparkSession(new SparkSessionUtil()
                .makeSparkSession(className + methodName + currentTime));
        try {
            returnValue = proceedingJoinPoint.proceed();
        } catch (Exception ex) {
            ex.printStackTrace();
            LogFactory.getLog(SparkSession.class).error(ex.getMessage());
        } finally {
            if (ObjectUtils.isNotEmpty(controller.getSparkSession())) {
                controller.getSparkSession().close();
            }
        }
        return returnValue;
    }
}
