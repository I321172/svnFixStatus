package i321172.web;

import i321172.MyContext;
import i321172.aop.log.LogAdvice;
import i321172.utils.AEPUtil;
import i321172.utils.dao.DBUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RestfulController
{

    @RequestMapping(value = "/aep")
    public String fetchAEPRunningJobs(@RequestParam(value = "user", defaultValue = "Null") String username,
            @RequestParam(value = "module", defaultValue = "Null") String moduleFilter,
            @RequestParam(value = "status", defaultValue = "Null") String jobStatus,
            @RequestParam(value = "env", defaultValue = "Null") String envFilter) throws Exception
    {
        AEPUtil aep = MyContext.getBean(AEPUtil.class);
        String result = aep.fetchAEPJobString(username, moduleFilter, jobStatus, envFilter);
        return result;
    }

    @RequestMapping(value = "/show/status")
    public String showRequestStatus()
    {
        LogAdvice advice = MyContext.getBean("logAdvice", LogAdvice.class);
        String msg = "SVN Fix Status Visit Count: " + advice.getSvnCount();
        return msg;
    }

    @RequestMapping(value = "/close/conn")
    public String refreshConnect() throws Exception
    {
        DBUtil util = MyContext.getBean("dbUtil", DBUtil.class);
        util.releaseConnectionPool();
        return "Connection All Closed! Connection Pool Refreshed!";
    }
}
