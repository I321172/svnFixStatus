package i321172.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import i321172.MyContext;
import i321172.bean.HttpClientBean;
import i321172.web.CacheData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class AEPUtil
{
    @Value("${aep.url}")
    private String aepUrl;
    @Value("${aep.username}")
    private String username;
    @Value("${aep.password}")
    private String password;
    @Value("${aep.modulefilter}")
    private String moduleFilter;
    @Value("${aep.jobstatus}")
    private String jobStatus;
    @Value("${aep.envfilter}")
    private String envFilter;

    public String fetchAEPLoginCookie(String username, String password) throws Exception
    {
        HttpClientUtil http = MyContext.getBean(HttpClientUtil.class);
        String value = http.getCookieValue(aepUrl + "/aep/auth/signIn?username=" + getUsername(username) + "&password="
                + getPassword(password), false, "JSESSION");
        return value;
    }

    /**
     * Set null to get default value
     * 
     * @return
     * @throws Exception
     */
    public String fetchAEPJob() throws Exception
    {
        return fetchAEPJobString(null, null, null, null);
    }

    /**
     * All params null to get default value
     * 
     * @param username
     * @param moduleFilter
     * @param jobStatus
     * @param envFilter
     * @return
     * @throws Exception
     */
    public String fetchAEPJobString(String username, String moduleFilter, String jobStatus, String envFilter)
            throws Exception
    {
        StringBuffer url = new StringBuffer(aepUrl);
        url.append("/aep/jobInstance/jobRuns/?format=json&sort=id&order=desc&username=");
        url.append(getUsername(username));
        url.append("&modulesJobInstancesFilter=");
        url.append(getModuleFilter(moduleFilter));
        url.append("&jobStatuses=");
        url.append(getJobStatus(jobStatus));
        url.append("&invocationTypes=ARP,Cron,FailureReRun,Manual,Missed&environmentDropDownFilter=");
        url.append(getEnvFilter(envFilter));
        url.append("&max=40&offset=0");
        HttpClientUtil http = MyContext.getBean(HttpClientUtil.class);
        CacheData cache = MyContext.getBean(CacheData.class);
        HttpClientBean httpBean = new HttpClientBean.Builder(url.toString()).setProxy(false)
                .addHeaders("Cookie", cache.getAepCookie()).build();
        http.fetchWeb(httpBean);
        return httpBean.getResponseBody();
    }

    public JsonNode fetchAEPJobJsonTree(String username, String moduleFilter, String jobStatus, String envFilter)
            throws Exception
    {
        String jsonString = fetchAEPJobString(username, moduleFilter, jobStatus, envFilter);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonString);
        ArrayNode jArrays = mapper.createArrayNode();
        ObjectNode resultNode = mapper.createObjectNode();
        for (Iterator<String> i = jsonNode.fieldNames(); i.hasNext();)
        {
            String fieldName = i.next();
            if (jsonNode.findValue(fieldName).isArray())
            {
                Set<String> jobName = new HashSet<String>();
                for (Iterator<JsonNode> ele = jsonNode.findValue(fieldName).elements(); ele.hasNext();)
                {
                    JsonNode cur = ele.next();
                    String name = cur.get("jobName").asText();
                    if (!jobName.contains(name))
                    {
                        jobName.add(name);
                        jArrays.add(cur);
                    }
                }
                resultNode.put("actualCount", jArrays.size());
                resultNode.set(fieldName, jArrays);
            } else
            {
                resultNode.put(fieldName, jsonNode.findValue(fieldName).asText());
            }
        }
        return resultNode;
    }

    private String getUsername(String username)
    {
        return StringUtil.isNullOrEmpty(username) ? this.username : username;
    }

    private String getPassword(String password)
    {
        return StringUtil.isNullOrEmpty(password) ? this.password : password;
    }

    private String getModuleFilter(String moduleFilter)
    {
        return StringUtil.isNullOrEmpty(moduleFilter) ? this.moduleFilter : moduleFilter;
    }

    private String getJobStatus(String jobStatus) throws UnsupportedEncodingException
    {
        return StringUtil.isNullOrEmpty(jobStatus) ? this.jobStatus : URLEncoder.encode(jobStatus, "utf-8");
    }

    private String getEnvFilter(String envFilter)
    {
        return StringUtil.isNullOrEmpty(envFilter) ? this.envFilter : envFilter;
    }

}
