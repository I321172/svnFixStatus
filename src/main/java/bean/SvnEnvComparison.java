package bean;

import java.util.ArrayList;
import java.util.List;

public class SvnEnvComparison
{
    private String              path;
    private String              feature;
    private List<EnvActualInfo> envInfo = new ArrayList<EnvActualInfo>();

    public SvnEnvComparison()
    {

    }

    public SvnEnvComparison(String path)
    {
        setPath(path);
    }

    public String getPath()
    {
        return path;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setPath(String path)
    {
        this.path = path;
        feature = path.split("/")[4];
    }

    public List<EnvActualInfo> getEnvInfo()
    {
        return envInfo;
    }

    public void refreshEnvActualInfo()
    {
        envInfo.clear();
    }

    public void addEnvInfo(EnvActualInfo envInfo)
    {
        this.envInfo.add(envInfo);
    }

    public EnvActualInfo createEnvActualInfo(EnvEnum env)
    {
        EnvActualInfo actInfo = new EnvActualInfo(env);
        addEnvInfo(actInfo);
        return actInfo;
    }

    public class EnvActualInfo
    {
        private EnvEnum env;
        private long    envRevision;
        private boolean fix;
        private String  fixStatus = "NONE";

        public EnvActualInfo()
        {

        }

        public EnvActualInfo(EnvEnum env)
        {
            setEnv(env);
        }

        public EnvEnum getEnv()
        {
            return env;
        }

        public void setEnv(EnvEnum env)
        {
            this.env = env;
        }

        public long getEnvRevision()
        {
            return envRevision;
        }

        public void setEnvRevision(long envRevision)
        {
            this.envRevision = envRevision;
        }

        public boolean isFix()
        {
            return fix;
        }

        public void setFix(boolean isFixed)
        {
            this.fix = isFixed;
            if (isFixed)
            {
                fixStatus = "GotFixed";
            } else
            {
                fixStatus = "NotFixed";
            }
        }

        public String getFixStatus()
        {
            return fixStatus;
        }
    }

}
