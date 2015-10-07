package web.aop.log;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAdvice
{
    private Logger logger = Logger.getLogger(getClass());

    @Around("web.aop.log.LogPointCut.logSvnAction()")
    public void logSvnAction(ProceedingJoinPoint pjp) throws Throwable
    {
        long start = getTime();
        pjp.proceed();
        log("SVN action takes " + getDuration(start));
    }

    private long getTime()
    {
        return System.currentTimeMillis();
    }

    private String getDuration(long start)
    {
        long dur = (getTime() - start) / 1000;
        return dur + " seconds";
    }

    private void log(String msg)
    {
        logger.info(msg);
    }
}
