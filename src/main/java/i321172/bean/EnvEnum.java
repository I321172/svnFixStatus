package i321172.bean;

public enum EnvEnum
{
    QAAUTOCAND("https://qaautocand.sflab.ondemand.com/sf-version.properties", true), QAAUTOCANDTOMCAT(
            "https://qaautocand-tomcat.lab-rot.ondemand.com/sf-version.properties", true), QACAND(
            "https://qacand.sflab.ondemand.com/sf-version.properties", true), LASTSUCCESSBUILD(
            "http://jenkins.successfactors.com/job/publish-release-trunk/lastSuccessfulBuild/artifact/sf-version.properties/*view*/",
            false);

    EnvEnum(String versionUrl, boolean needProxy)
    {
        this.versionUrl = versionUrl;
        this.needProxy = needProxy;
    }

    private boolean needProxy;

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

    public String getVersionInfo()
    {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo)
    {
        this.versionInfo = versionInfo;
    }
}
