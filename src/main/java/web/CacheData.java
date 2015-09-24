package web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import bean.FeatureCoverage;
import bean.SvnInfoBean;

@Repository
public class CacheData
{
    private List<String>                 allFeatures    = new ArrayList<String>();
    private Map<String, FeatureCoverage> featureData    = new HashMap<String, FeatureCoverage>();

    private Map<Long, SvnInfoBean>       storedRevision = new LinkedHashMap<Long, SvnInfoBean>();

    @Value("${urlPrefix}")
    private String                       urlPrefix;

    @Value("${currentTime}")
    private String                       currentTime;

    public List<String> getAllFeatures()
    {
        return allFeatures;
    }

    public void setAllFeatures(List<String> allFeatures)
    {
        this.allFeatures = allFeatures;
    }

    public Map<String, FeatureCoverage> getFeatureData()
    {
        return featureData;
    }

    public void setFeatureData(Map<String, FeatureCoverage> featureData)
    {
        this.featureData = featureData;
    }

    public void putFeatureData(String feature, FeatureCoverage data)
    {
        featureData.put(feature, data);
    }

    public FeatureCoverage getFeatureData(String feature)
    {
        return featureData.get(feature);
    }

    public String getUrlPrefix()
    {
        return urlPrefix;
    }

    public String getCurrentTime()
    {
        return currentTime;
    }

    public String getCoverageFilePrefix()
    {
        return getUrlPrefix() + getCurrentTime();
    }

    public Map<Long, SvnInfoBean> getStoredRevision()
    {
        return storedRevision;
    }

    public int getStoredRevisionSize()
    {
        return storedRevision.keySet().size();
    }

    /**
     * Only cache 15 element since not want to store a lot of Revision
     * 
     * @param revision
     * @param svnInfoBean
     */
    public void putStoredRevision(Long revision, SvnInfoBean svnInfoBean)
    {
        if (getStoredRevisionSize() < 16)
        {
            storedRevision.put(revision, svnInfoBean);
        } else
        {
            if (!storedRevision.containsKey(revision))
            {
                removeFirstStoredRevision();
                storedRevision.put(revision, svnInfoBean);
            }
        }
    }

    public void removeFirstStoredRevision()
    {
        Iterator<Long> ite = storedRevision.keySet().iterator();
        if (ite.hasNext())
        {
            storedRevision.remove(ite.next());
        }
    }

    public SvnInfoBean getStoredSvnInfo(Long revision)
    {
        return storedRevision.get(revision);
    }

}
