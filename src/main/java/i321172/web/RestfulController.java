package i321172.web;

import java.util.Iterator;
import java.util.Map.Entry;

import i321172.MyContext;
import i321172.utils.AEPUtil;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode main = mapper.readTree(result);
        for (Iterator<Entry<String, JsonNode>> i = main.fields(); i.hasNext();)
        {
            Entry<String, JsonNode> current = i.next();
            System.out.print(current.getKey() + "\t");
            System.out.println(current.getValue().toString());
        }
        return null;
    }

}
