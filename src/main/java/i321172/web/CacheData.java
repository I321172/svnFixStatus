package i321172.web;

import i321172.bean.SvnInfoBean;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class CacheData
{

    private Map<Long, SvnInfoBean> storedRevision = new LinkedHashMap<Long, SvnInfoBean>();

    @Value("${urlPrefix}")
    private String                 urlPrefix;

    @Value("${currentTime}")
    private String                 currentTime;

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
