package com.glanz.intercept;

import com.alibaba.fastjson.JSON;
import com.glanz.intercept.annotation.DoIntercept;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author GlanzWen
 * @Description Something
 * @github
 */

@Aspect
@Component
public class DoInterceptPoint {

    private Logger logger = LoggerFactory.getLogger(DoInterceptPoint.class);


    @Pointcut("@annotation(com.glanz.intercept.annotation.DoIntercept)")
    public void aopPoint(){}

    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint pointJp) throws Throwable {
        Method method = this.getMethod(pointJp);

        DoIntercept doIntercept = method.getAnnotation(DoIntercept.class);

        String methodName = doIntercept.method();

        Method methodIntercept = getClass(pointJp).getMethod(methodName, method.getParameterTypes());
        Class<?> returnType = methodIntercept.getReturnType();

        if (!returnType.getName().equals("boolean")) {
            throw new RuntimeException("annotation @DoMethodExt set method：" + methodName + " returnType is not boolean");
        }

        boolean invoke = (boolean) methodIntercept.invoke(pointJp.getThis(), pointJp.getArgs());
        return invoke ? pointJp.proceed() : JSON.parseObject(doIntercept.returnJson(), method.getReturnType());
    }




    //获取方法
    private Method getMethod(JoinPoint jp) throws NoSuchMethodException {
        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        return jp.getTarget().getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
    }


    //获取类型
    private Class<? extends Object> getClass(JoinPoint jp) {
        return jp.getTarget().getClass();
    }
}
