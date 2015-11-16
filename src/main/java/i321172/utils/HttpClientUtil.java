package i321172.utils;

import i321172.bean.HttpClientBean;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.springframework.stereotype.Service;

@Service
public class HttpClientUtil
{
    private static Logger logger = Logger.getLogger(HttpClientUtil.class);

    /**
     * @param url
     * @param isProxy
     *            Default proxy : proxy:8080
     * @return
     * @throws Exception
     */
    public String fetchWebResponse(String url, boolean isProxy) throws Exception
    {
        return fetchWebResponse(new HttpClientBean.Builder(url).setProxy(isProxy).build());
    }

    public String fetchWebResponse(HttpClientBean httpBean) throws Exception
    {
        httpBean = fetchWeb(httpBean);
        return httpBean.getResponseBody();
    }

    public HttpClientBean fetchWeb(final HttpClientBean httpBean) throws IOException
    {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig httpConfig = httpBean.getRequestConfig();
        HttpRequestBase method = null;
        String methodType = httpBean.getMethodType();
        String queryUrl = httpBean.getUrl();
        switch (methodType)
        {
            case "Get":
                method = new HttpGet(queryUrl);
                break;

            case "Delete":
                method = new HttpDelete(queryUrl);
                break;

            case "Post":
                // headers.put("Content-Type",
                // "application/x-www-form-urlencoded");
                method = new HttpPost(queryUrl);
                ((HttpPost) method).setEntity(this.getHttpEntity(httpBean));
                break;
        }
        method.setConfig(httpConfig);
        addHeader(httpBean, method);
        log(httpBean.toString());
        CloseableHttpResponse response = httpClient.execute(method);
        log("HttpClient executed with status: " + response.getStatusLine().getStatusCode());
        Map<String, String> responseHeaders = convertResponseHeaders(response.getAllHeaders());
        httpBean.setResponseHeaders(responseHeaders);
        String responseBody = EntityUtils.toString(response.getEntity());
        log("HttpClient response: " + responseBody);
        httpBean.setResponseBody(responseBody);
        closeResponse(response);
        return httpBean;

    }

    private void addHeader(HttpClientBean httpBean, HttpRequestBase method)
    {
        Map<String, String> headers = httpBean.getHeaders();
        for (String header : headers.keySet())
        {
            method.addHeader(header, headers.get(header));
            log("Add header: " + header + " = " + headers.get(header));
        }
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
        HttpClientBean httpBean = new HttpClientBean.Builder(url).setProxy(isProxy).build();
        httpBean = fetchWeb(httpBean);
        Map<String, String> respHeaders = httpBean.getResponseHeaders();
        String cookie = respHeaders.get("Set-Cookie");
        if (isNull(cookie))
            return null;
        String result = null;
        if (cookie.contains(cookieName))
        {
            result = cookie.replaceAll(".*(" + cookieName + ".*?);.*", "$1");
        }
        return result;
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
            log(name + " = " + headerValue);
        }
        return result;
    }

    private boolean isNull(String text)
    {
        return text == null;
    }

    private HttpEntity getHttpEntity(HttpClientBean clientBean)
    {
        if (!isNull(clientBean.getBody()))
        {
            HttpEntity entity = new StringEntity(clientBean.getBody(), Charset.forName("UTF-8"));
            return entity;
        } else
        {
            return null;
        }
    }

    private void closeResponse(CloseableHttpResponse response)
    {
        try
        {
            response.close();
        } catch (IOException e)
        {
            this.log(e.getMessage());
        }
    }

    private void log(String msg)
    {
        logger.debug(msg);
    }

}
