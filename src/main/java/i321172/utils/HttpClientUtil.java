package i321172.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service()
@Scope("prototype")
public class HttpClientUtil
{
    private Map<String, String> headers    = new HashMap<String, String>();
    private String              body;
    private Logger              logger     = Logger.getLogger(getClass());
    private String              methodType = "Get";

    /**
     * Default with proxy:8080
     */
    public String fetchWeb(String url) throws Exception
    {
        return fetchWeb(url, null);
    }

    public String fetchWeb(String url, String customProxy) throws Exception
    {
        return fetchWeb(url, true, customProxy);
    }

    public String fetchWeb(String queryUrl, boolean isProxy) throws Exception
    {
        long start = getTime();
        StringBuffer msg = new StringBuffer("Fetch info in " + queryUrl);
        String response = this.fetchWeb(queryUrl, isProxy, null);
        msg.append("HttpClient action takes " + getDuration(start));
        logger.info(msg.toString());
        return response;
    }

    public String fetchWeb(String url, boolean isProxy, String customProxy) throws Exception
    {
        return fetchWeb(url, isProxy, customProxy, true);
    }

    public String fetchWeb(String url, boolean isNeedProxy, String customProxy, boolean isDisableRedirect)
            throws Exception
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig httpConfig = getRequestConfig(isNeedProxy, customProxy, isDisableRedirect);
        HttpRequestBase method = null;
        switch (methodType)
        {
            case "Get":
                method = new HttpGet(url);
                break;

            case "Delete":
                method = new HttpDelete(url);
                break;

            case "Post":
                // headers.put("Content-Type",
                // "application/x-www-form-urlencoded");
                method = new HttpPost(url);
                ((HttpPost) method).setEntity(this.getHttpEntity());
                break;
        }
        method.setConfig(httpConfig);
        addHeader(method);
        log(methodType + " on [" + url + "]");

        CloseableHttpResponse response = httpClient.execute(method);
        log("HttpClient executed with status: " + response.getStatusLine().getStatusCode());

        String resposne = EntityUtils.toString(response.getEntity());
        try
        {
            response.close();
            httpClient.close();
        } catch (Exception e)
        {
            log(e.getMessage());
        }
        return resposne;
    }

    private void addHeader(HttpRequestBase method)
    {
        for (String header : headers.keySet())
        {
            method.addHeader(header, headers.get(header));
            log("Add header: " + header + " = " + headers.get(header));
        }
    }

    public void addHeader(String name, String value)
    {
        headers.put(name, value);
    }

    /**
     * Set handle redirect or not; default no redirect <br>
     * isNeedProxy is to set system proxy<br>
     * If set customProxy, it will override isNeedProxy<br>
     * isDisableRedirect default as true to capture the first response of the
     * request;
     * 
     * @return
     */
    private RequestConfig getRequestConfig(boolean isNeedProxy, String customProxy, boolean isDisableRedirect)
    {
        /**
         * On rot, if without proxy[proxy.successfactors.com:8080], unknown host
         * exception occurs;<br>
         * if use rot IP, don't need proxy
         */
        RequestConfig config = null;
        if (isNeedProxy)
        {
            if (!isNull(customProxy))
            {
                config = RequestConfig.custom().setProxy(new HttpHost(customProxy)).build();
                log("Set custom proxy to httpclient in SAP network: " + customProxy);
            } else
            {
                // set system proxy
                config = RequestConfig.custom().setProxy(getSystemProxy()).build();
            }
        } else
        {
            config = RequestConfig.custom().build();
            log(" Httpclient without proxy!");
        }

        if (isDisableRedirect)
        {
            config = RequestConfig.copy(config).setRedirectsEnabled(false).setCircularRedirectsAllowed(false)
                    .setRelativeRedirectsAllowed(false).build();
            log("Disable redirect in httpclient!");
        }
        return config;
    }

    /**
     * Append Default Proxy :proxy:8080
     * 
     * @param url
     * @param cookieName
     * @return
     * @throws Exception
     */
    public String getCookieValue(String url, String cookieName) throws Exception
    {
        return getCookieValue(url, true, cookieName);
    }

    public String getCookieValue(String url, boolean isProxy, String cookieName) throws Exception
    {
        Map<String, String> respHeaders = getHttpResponseHeader(url, isProxy);
        String cookie = respHeaders.get("Cookie");
        if (isNull(cookie))
            return null;
        String result = null;
        if (cookie.contains(cookieName))
        {
            result = cookie.replaceAll(".*(" + cookieName + ".*?);.*", "$1");
        }
        return result;
    }

    public Map<String, String> getHttpResponseHeader(String url, boolean isProxy) throws Exception
    {
        return getHttpResponseHeader(url, isProxy, null, true);
    }

    public Map<String, String> getHttpResponseHeader(String url, boolean isProxy, String customProxy,
            boolean isDisableRedirect) throws Exception
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig httpConfig = getRequestConfig(isProxy, customProxy, isDisableRedirect);
        HttpGet method = new HttpGet(url);
        method.setConfig(httpConfig);
        addHeader(method);
        log(methodType + " on [" + url + "]");

        CloseableHttpResponse response = httpClient.execute(method);
        log("HttpClient executed with status: " + response.getStatusLine().getStatusCode());
        return convertResponseHeaders(response.getAllHeaders());
    }

    private Map<String, String> convertResponseHeaders(Header[] headers)
    {
        Map<String, String> result = new HashMap<String, String>();
        for (Header header : headers)
        {
            String name = header.getName();
            String headerValue = result.get(name);
            if (headerValue == null)
            {
                headerValue = header.getValue();
            } else
            {
                headerValue += "; " + header.getValue();
            }
            result.put(name, headerValue);
        }
        return result;
    }

    private boolean isNull(String text)
    {
        return text == null;
    }

    private HttpEntity getHttpEntity() throws UnsupportedEncodingException
    {
        if (!isNull(body))
        {
            HttpEntity entity = new StringEntity(body, Charset.forName("UTF-8"));
            return entity;
        } else
        {
            return null;
        }
    }

    public void setHttpEntity(String body)
    {
        this.body = body;
    }

    private HttpHost getSystemProxy()
    {
        return new HttpHost("proxy", 8080);
    }

    private void log(String msg)
    {
        logger.debug(msg);
    }

    public void setMethodType(String method)
    {
        this.methodType = method;
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
