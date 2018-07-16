package com.wms.core.interceptor;

import com.wms.core.utils.common.ObjectUtils;
import com.wms.core.utils.common.StaticData;
import com.wms.core.utils.datetime.DateTimeUtil;
import com.wms.core.utils.request.MacUtil;
import com.wms.core.utils.request.RequestUtil;
import com.wms.user.domain.Log;
import com.wms.user.domain.LogRepo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import scala.Option;

import java.lang.reflect.Method;
import java.sql.Timestamp;

@Aspect
@Component
public class MakeLogInterceptot {
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object doExecute(ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        Object returnValue = null;
        Timestamp begin = null;
        Timestamp end = null;
        Class<?> clazz = null;
        Method method = null;
        String userName = InterceptUtil.getCurrentUserName(proceedingJoinPoint);
        if (StaticData.LOG_ENABLED && !StringUtils.isEmpty(userName)) {
            MethodSignature signature = (MethodSignature) proceedingJoinPoint
                    .getSignature();
            clazz = proceedingJoinPoint.getTarget().getClass();
            method = signature.getMethod();
            begin = new Timestamp(DateTimeUtil.getNow().getTime());
        }
        returnValue = proceedingJoinPoint.proceed();
        if (StaticData.LOG_ENABLED && ObjectUtils.isNotEmpty(userName)) {
            end = new Timestamp(DateTimeUtil.getNow().getTime());
            long spend = end.getTime() - begin.getTime();
            ServerWebExchange request = null;
            for (Object object : proceedingJoinPoint.getArgs()) {
                if (object instanceof ServerWebExchange) {
                    request = (ServerWebExchange) object;
                }
            }
            if (ObjectUtils.isNotEmpty(returnValue)
                    && ObjectUtils.isNotEmpty(request)) {
                if(ObjectUtils.isNotEmpty(InterceptUtil.getOperaterDesc(proceedingJoinPoint))) {
                    Log log = new Log(begin, end, RequestUtil.getBrowser(request), RequestUtil.getOs(request),
                            spend, clazz.getName(), method.getName(), MacUtil.getIpAddr(request),
                            userName, InterceptUtil.getOperaterDesc(proceedingJoinPoint), Option.empty());
                    new LogRepo().insertLog(log).block();
                }
            }
        }
        return returnValue;
    }

}
