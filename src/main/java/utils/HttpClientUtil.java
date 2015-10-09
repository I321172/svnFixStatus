package utils;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class HttpClientUtil
{
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    public static String getSFVersion(String queryUrl, boolean isProxy) throws Exception
    {
        long start = getTime();
        String msg = "Fetch info in " + queryUrl;
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
        msg += "HttpClient action takes " + getDuration(start);
        logger.info(msg);
        return IOUtils.toString(stream, "UTF-8");
    }

    private static long getTime()
    {
        return System.currentTimeMillis();
    }

    private static String getDuration(long start)
    {
        long dur = (getTime() - start) / 1000;
        return dur + " seconds";
    }

}
