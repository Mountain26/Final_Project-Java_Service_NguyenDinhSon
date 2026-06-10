package ra.edu.finalproject_javaservice.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("within(ra.edu.finalproject_javaservice.controller..*)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        log.info("REQUEST {} args={}", method, Arrays.deepToString(joinPoint.getArgs()));
        try {
            Object result = joinPoint.proceed();
            log.info("RESPONSE {} in {} ms payload={}", method, System.currentTimeMillis() - start, result);
            return result;
        } catch (Throwable ex) {
            log.error("EXCEPTION {} in {} ms: {}", method, System.currentTimeMillis() - start, ex.toString());
            throw ex;
        }
    }
}
