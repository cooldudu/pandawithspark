package com.wms.core.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wms.core.utils.regexp.RegExp;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.userdetails.UserDetails;

import com.wms.core.annotation.MakeLog;
import com.wms.core.utils.common.ObjectUtils;
import com.wms.core.utils.common.StringUtil;

public class InterceptUtil {
    public static Object getMethodArg(ProceedingJoinPoint proceedingJoinPoint,
                                      int index) throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        Object[] args = proceedingJoinPoint.getArgs();
        return args[index - 1];
    }

    public static void setMethodArg(ProceedingJoinPoint proceedingJoinPoint,
                                    int index, Object value) throws IllegalArgumentException,
            SecurityException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Object[] args = proceedingJoinPoint.getArgs();
        args[index - 1] = value;
    }

    public static Object getActionField(
            ProceedingJoinPoint proceedingJoinPoint, String fieldName)
            throws IllegalArgumentException, SecurityException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        return proceedingJoinPoint.getTarget().getClass()
                .getMethod(StringUtil.queryGetMethodString(fieldName))
                .invoke(proceedingJoinPoint.getTarget());
    }

    public static void setActionField(ProceedingJoinPoint proceedingJoinPoint,
                                      String fieldName, Object... value) throws IllegalArgumentException,
            SecurityException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        proceedingJoinPoint
                .getTarget()
                .getClass()
                .getMethod(
                        StringUtil.querySetMethodString(fieldName),
                        proceedingJoinPoint
                                .getTarget()
                                .getClass()
                                .getMethod(
                                        StringUtil
                                                .queryGetMethodString(fieldName))
                                .getReturnType())
                .invoke(proceedingJoinPoint.getTarget(), value);

    }

    public static String getCurrentUserName(
            ProceedingJoinPoint proceedingJoinPoint)
            throws Exception {
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Principal) {
                Principal principal = (Principal) arg;
                return principal.getName();
            } else if (arg instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) arg;
                return userDetails.getUsername();
            }
        }
        return "";
    }

    public static String getOperaterDesc(ProceedingJoinPoint proceedingJoinPoint)
            throws Exception {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint
                .getSignature();
        Method method = signature.getMethod();
        MakeLog annotation = (MakeLog) method
                .getAnnotation(MakeLog.class);
        if (ObjectUtils.isNotEmpty(annotation)) {
            String logContent = annotation.logContent();
            try {
                Object[] args = proceedingJoinPoint.getArgs();
                String[] names = signature.getParameterNames();
                Map<String, Object> params = new HashMap<String, Object>();
                for (int i = 0; i < names.length; i++) {
                    params.put(names[i], args[i]);
                }
                String reg = "(?<=(?<!\\\\)\\$\\{)(.*?)(?=(?<!\\\\)\\})";
                RegExp re = new RegExp();
                List<String> list = re.find(reg, logContent);
                for (String key : list) {
                    Object value = null;
                    if (key.indexOf(".") == -1) {
                        value = params.get(key);
                    } else {
                        String[] keys = key.split("\\.");
                        value = params.get(keys[0]);
                        for (int j = 1; j < keys.length; j++) {
                            if (keys[j].indexOf("(") != -1 || keys[j].indexOf(")") != -1) {
                                throw new Exception("You needn't write () on function.");
                            }
                            Method tmp = value.getClass().getMethod(keys[j]);
                            value = tmp.invoke(value);
                        }
                    }
                    logContent = logContent.replace("${" + key + "}", value.toString());
                }
            } catch (Exception ex) {
            }
            return logContent;
        }
        return "";
    }

}
