package i321172.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.log4j.Logger;

public class HttpClientBean
{
    private static Logger       logger          = Logger.getLogger(HttpClientBean.class);
    private String              url;
    private Map<String, String> headers         = new HashMap<String, String>();
    private String              body;
    private String              methodType;
    private RequestConfig       requestConfig;
    private String              responseBody;
    private Map<String, String> responseHeaders = new HashMap<String, String>();

    public RequestConfig getRequestConfig()
    {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig)
    {
        this.requestConfig = requestConfig;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }

    public Map<String, String> getResponseHeaders()
    {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders)
    {
        this.responseHeaders = responseHeaders;
    }

    public String getBody()
    {
        return body;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public String getMethodType()
    {
        return methodType;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String toString()
    {
        return methodType + " on [" + url + "]";
    }

    public static class Builder
    {
        private Map<String, String> headers    = new HashMap<String, String>();
        private String              body;
        private String              methodType = "Get";
        private String              url;
        private boolean             isProxy    = true;
        private String              customProxy;
        private boolean             isRedirect = false;

        public Builder(String url)
        {
            setUrl(url);
        }

        public HttpClientBean build()
        {
            return new HttpClientBean(this);
        }

        public Builder setMethodType(String methodType)
        {
            this.methodType = methodType;
            return this;
        }

        public Builder setBody(String body)
        {
            this.body = body;
            return this;
        }

        public Builder addHeaders(String name, String value)
        {
            this.headers.put(name, value);
            return this;
        }

        public Builder setUrl(String url)
        {
            this.url = url;
            return this;
        }

        /**
         * Set handle redirect or not; default no redirect <br>
         * isNeedProxy is to set system proxy<br>
         * If set customProxy, it will override isNeedProxy<br>
         * isDisableRedirect default as true to capture the first response of
         * the request;
         * 
         * @return
         */
        public RequestConfig getRequestConfig(boolean isNeedProxy, String customProxy, boolean isRedirect)
        {
            /**
             * On rot, if without proxy[proxy.successfactors.com:8080], unknown
             * host exception occurs;<br>
             * if use rot IP, don't need proxy
             */
            RequestConfig requestConfig;
            if (isNeedProxy)
            {
                if (customProxy != null)
                {
                    requestConfig = RequestConfig.custom().setProxy(new HttpHost(customProxy)).build();
                    log("Set custom proxy to httpclient in SAP network: " + customProxy);
                } else
                {
                    // set system proxy
                    requestConfig = RequestConfig.custom().setProxy(getSystemProxy()).build();
                }
            } else
            {
                requestConfig = RequestConfig.custom().build();
                log(" Httpclient without proxy!");
            }

            if (!isRedirect)
            {
                requestConfig = RequestConfig.copy(requestConfig).setRedirectsEnabled(isRedirect)
                        .setCircularRedirectsAllowed(isRedirect).setRelativeRedirectsAllowed(isRedirect).build();
                log("Disable redirect in httpclient!");
            }
            return requestConfig;
        }

        /**
         * proxy:8080 origin one<br>
         * proxy.wdf.sap.corp:8080 not known
         * 
         * @return
         */
        private HttpHost getSystemProxy()
        {
            return new HttpHost("proxy", 8080);
        }

        public boolean isProxy()
        {
            return isProxy;
        }

        public Builder setProxy(boolean isProxy)
        {
            this.isProxy = isProxy;
            return this;
        }

        public String getCustomProxy()
        {
            return customProxy;
        }

        public Builder setCustomProxy(String customProxy)
        {
            this.customProxy = customProxy;
            setProxy(true);
            return this;
        }

        public boolean isRedirect()
        {
            return isRedirect;
        }

        public Builder setRedirect(boolean isRedirect)
        {
            this.isRedirect = isRedirect;
            return this;
        }
    }

    public HttpClientBean(Builder builder)
    {
        this.headers = builder.headers;
        this.body = builder.body;
        this.methodType = builder.methodType;
        this.url = builder.url;
        this.requestConfig = builder.getRequestConfig(builder.isProxy, builder.customProxy, builder.isRedirect);

    }

    private static void log(String msg)
    {
        logger.debug(msg);
    }
}
