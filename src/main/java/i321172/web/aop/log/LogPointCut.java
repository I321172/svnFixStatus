package i321172.web.aop.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogPointCut
{
    @Pointcut(value = "execution(* *.web.SVNUtils.check*(..)) && args(param)")
    public void logSvnAction(long param)
    {}

    @Pointcut(value = "execution(* *.web.Request*.checkSVN*(..))")
    public void countSVNVisit()
    {}

    @Pointcut(value = "execution(* *.web.Request*.showClass*(..))")
    public void countCodeCoverageVisit()
    {}

    @Pointcut(value = "execution(* *.web.Sche*.refresh*(..))")
    public void logScheduleTask()
    {}

    @Pointcut(value = "execution(* *.web.Sche*.refreshEnv*())")
    public void logScheduleTaskFetchEnv()
    {}

}
