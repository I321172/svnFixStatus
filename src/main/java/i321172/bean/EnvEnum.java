package i321172.bean;

public enum EnvEnum
{
    Qaautocand("https://qaautocand.sflab.ondemand.com/sf-version.properties", true), Qacand(
            "https://qacand.sflab.ondemand.com/sf-version.properties", true), LastSuccessBuild(
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
