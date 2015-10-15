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
    private Logger logger        = Logger.getLogger(getClass());
    private int    svnCount      = 0;
    private int    coverageCount = 0;

    @Around(value = "i321172.web.aop.log.LogPointCut.logSvnAction(param)", argNames = "param")
    public Object logSvnAction(ProceedingJoinPoint pjp, long param) throws Throwable
    {
        log("Start to fetch SVN info for version:" + param);
        long start = getTime();
        Object result = pjp.proceed();
        log("SVN action takes " + getDuration(start));
        return result;
    }

    @Before(value = "i321172.web.aop.log.LogPointCut.countSVNVisit()")
    public void countSVNVisit()
    {
        log("Fetch SVN Fix Status; Count = " + ++svnCount);
    }

    @Before(value = "i321172.web.aop.log.LogPointCut.countCodeCoverageVisit()")
    public void countCodeCoverageVisit()
    {
        log("Fetch Code Coverage; Count = " + ++coverageCount);
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
        return dur + " seconds";
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

    public int getSvnCount()
    {
        return svnCount;
    }

    public int getCoverageCount()
    {
        return coverageCount;
    }

}
