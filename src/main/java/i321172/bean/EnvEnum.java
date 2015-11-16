package i321172.bean;

public enum EnvEnum
{
    QAAUTOCAND("https://qaautocand.sflab.ondemand.com/sf-version.properties", "proxy.wdf.sap.corp:8080"), QAAUTOCANDTOMCAT(
            "https://qaautocand-tomcat.lab-rot.ondemand.com/sf-version.properties", "proxy.wdf.sap.corp:8080"), QACAND(
            "https://qacand.sflab.ondemand.com/sf-version.properties", true), LASTSUCCESSBUILD(
            "http://jenkins.successfactors.com/job/publish-release-trunk/lastSuccessfulBuild/artifact/sf-version.properties/*view*/",
            false);

    EnvEnum(String versionUrl, boolean needProxy)
    {
        this.versionUrl = versionUrl;
        this.needProxy = needProxy;
    }

    EnvEnum(String versionUrl, String customProxy)
    {
        this.versionUrl = versionUrl;
        this.customProxy = customProxy;
        needProxy = true;
    }

    private boolean needProxy;
    private String  customProxy;

    private String  versionUrl;
    private String  versionInfo;

    public String toVersionUrl()
    {
        return versionUrl;
    }

    public boolean isNeedProxy()
    {
        return needProxy;
    }

    public String getProxy()
    {
        if (isNeedProxy())
        {
            if (customProxy != null)
                return customProxy;
            else
                return getDefaultProxy();
        }
        return null;
    }

    private String getDefaultProxy()
    {
        return "proxy:8080";
    }

    public String getVersionInfo()
    {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo)
    {
        this.versionInfo = versionInfo;
    }
}
