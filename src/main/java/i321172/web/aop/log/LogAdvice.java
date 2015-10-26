package i321172.web.aop.log;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAdvice
{
    private Logger logger   = Logger.getLogger(getClass());
    private int    svnCount = 0;

    @Around(value = "i321172.web.aop.log.LogPointCut.logSvnAction(param)", argNames = "param")
    public Object logSvnAction(ProceedingJoinPoint pjp, long param) throws Throwable
    {
        log("Start to fetch SVN info for version:" + param);
        long start = getTime();
        Object result = pjp.proceed();
        log("SVN Operation Success which takes " + getDuration(start) + " on method ["
                + pjp.getSignature().toShortString() + "]");
        return result;
    }

    @Around(value = "i321172.web.aop.log.LogPointCut.logSvnStoreDBAction()")
    public void logSvnStoreDBAction(ProceedingJoinPoint pjp) throws Throwable
    {
        Object[] obj = pjp.getArgs();
        if (obj.length >= 3)
            log("Start to fetch SVN info from version:" + obj[1] + " to " + obj[2]);
        long time = getTime();
        pjp.proceed();
        log("SVN Operation Success which takes " + getDuration(time) + " on method ["
                + pjp.getSignature().toShortString() + "]");
    }

    @Around(value = "i321172.web.aop.log.LogPointCut.logHttpDuration(url)", argNames = "url")
    public Object logHttpDuration(ProceedingJoinPoint pjp, String url) throws Throwable
    {
        log("Start to fetch Http Info on " + url);
        long start = getTime();
        Object result = pjp.proceed();
        log("HTTP Operation Success which takes " + getDuration(start) + " on method ["
                + pjp.getSignature().toShortString() + "]");
        return result;
    }

    @Before(value = "i321172.web.aop.log.LogPointCut.countSVNVisit()")
    public void countSVNVisit()
    {
        log("Fetch SVN Fix Status; Count = " + ++svnCount);
    }

    @Before(value = "i321172.web.aop.log.LogPointCut.logScheduleTask()")
    public void logScheduleTask()
    {
        log("Start to Execute ScheduledTask!");
    }

    @After(value = "i321172.web.aop.log.LogPointCut.logScheduleTaskFetchEnv()")
    public void logScheduleTaskEnv()
    {
        log("All Envirionment Version are fetched!");
    }

    private long getTime()
    {
        return System.currentTimeMillis();
    }

    private String getDuration(long start)
    {
        long dur = (getTime() - start) / 1000;
        return dur / 60 + " minutes and " + dur % 60 + " seconds";
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

    public int getSvnCount()
    {
        return svnCount;
    }

}
