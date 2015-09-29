package web;

import java.io.InputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import bean.EnvEnum;

@Component
public class ScheduledTasks
{
    private Logger logger = Logger.getLogger(getClass());

    @Scheduled(fixedRate = 10000000)
    public void reportCurrentTime() throws Exception
    {
        log("Start to Fetch Environment Version in ScheduledTask!");
        fetchEnvVersionInfo();
    }

    private void fetchEnvVersionInfo() throws Exception
    {
        for (EnvEnum elem : EnvEnum.values())
        {
            elem.setVersionInfo(getSFVersion(elem.toVersionUrl(), elem.isNeedProxy()));
        }
    }

    private String getSFVersion(String queryUrl, boolean isProxy) throws Exception
    {
        long start = getTime();
        logger.info("Fetch info in " + queryUrl);
        HttpClient httpClient = new HttpClient();
        if (isProxy)
        {
            String proxyHost = "proxy";
            String proxyPort = "8080";

            int proxyPortInt = Integer.valueOf(proxyPort);
            httpClient.getHostConfiguration().setProxy(proxyHost, proxyPortInt);
        }
        GetMethod request = new GetMethod(queryUrl);
        httpClient.executeMethod(request);

        InputStream stream = request.getResponseBodyAsStream();
        log("HttpClient action takes " + getDuration(start));
        return IOUtils.toString(stream, "UTF-8");
    }

    private void log(String msg)
    {
        logger.info(msg);
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
}
