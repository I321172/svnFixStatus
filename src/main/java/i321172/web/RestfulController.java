package i321172.web;

import i321172.MyContext;
import i321172.utils.AEPUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestfulController
{

    @RequestMapping(value = "/show/aep")
    public String fetchAEPRunningJobs(@RequestParam(value = "user", defaultValue = "Null") String username,
            @RequestParam(value = "module", defaultValue = "Null") String moduleFilter,
            @RequestParam(value = "status", defaultValue = "Null") String jobStatus,
            @RequestParam(value = "env", defaultValue = "Null") String envFilter) throws Exception
    {
        AEPUtil aep = MyContext.getBean(AEPUtil.class);
        String result = aep.fetchAEPJob(username, moduleFilter, jobStatus, envFilter);
        return result;
    }

}
