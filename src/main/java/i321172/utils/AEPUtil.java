package i321172.utils;

import i321172.MyContext;
import i321172.web.CacheData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        return fetchAEPJob(null, null, null, null);
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
    public String fetchAEPJob(String username, String moduleFilter, String jobStatus, String envFilter)
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
        url.append("&max=20&offset=0");
        HttpClientUtil http = MyContext.getBean(HttpClientUtil.class);
        CacheData cache = MyContext.getBean(CacheData.class);
        http.addHeader("Cookie", cache.getAepCookie());
        String resp = http.fetchWeb(url.toString(), false);
        return resp;
    }

    private String getUsername(String username)
    {
        return (username == null || username.equals("Null")) ? this.username : username;
    }

    private String getPassword(String password)
    {
        return password == null || password.equals("Null") ? this.password : password;
    }

    private String getModuleFilter(String moduleFilter)
    {
        return moduleFilter == null || moduleFilter.equals("Null") ? this.moduleFilter : moduleFilter;
    }

    private String getJobStatus(String jobStatus)
    {
        return jobStatus == null || jobStatus.equals("Null") ? this.jobStatus : jobStatus;
    }

    private String getEnvFilter(String envFilter)
    {
        return envFilter == null || envFilter.equals("Null") ? this.envFilter : envFilter;
    }
}
