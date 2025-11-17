package com.emmanuel.user_service.aop;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.emmanuel.user_service..*(..)) && !within(com.emmanuel.user_service.security.JwtAuthenticationFilter)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    String methodName = methodSignature.getName();
    Object[] args = joinPoint.getArgs();

    log.info("Entering method: {} with arguments: {}", methodName, Arrays.toString(args));

    long startTime = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed(); // Execute the actual method
      long endTime = System.currentTimeMillis();
      log.info(
          "Exiting method: {} with result: {} | Execution time: {} ms",
          methodName,
          result,
          (endTime - startTime));
      return result;
    } catch (Throwable throwable) {
      log.error(
          "Exception in method: {} with message: {}",
          methodName,
          throwable.getMessage(),
          throwable);
      throw throwable;
    }
  }
}
