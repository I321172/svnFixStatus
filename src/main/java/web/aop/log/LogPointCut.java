package web.aop.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogPointCut
{
    
    @Pointcut("execution(public web.SVNUtil.check*(..))" )
    public void logSvnAction() { }

}
