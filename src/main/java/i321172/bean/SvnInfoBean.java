package i321172.bean;

import i321172.bean.SvnEnvComparison.EnvActualInfo;

import java.util.ArrayList;
import java.util.List;

public class SvnInfoBean
{
    private long                   revision;
    private String                 author;
    private String                 createDate;
    private String                 comment;
    private List<SvnEnvComparison> svnEnvComparison = new ArrayList<SvnEnvComparison>();
    private boolean                valuable;

    public SvnInfoBean()
    {

    }

    public SvnInfoBean(long revision, String author, String createDate)
    {
        setRevision(revision);
        setAuthor(author);
        setCreateDate(createDate);
    }

    public long getRevision()
    {
        return revision;
    }

    public void setRevision(long revision)
    {
        this.revision = revision;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(String createDate)
    {
        this.createDate = createDate;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public List<SvnEnvComparison> getSvnEnvComparison()
    {
        return svnEnvComparison;
    }

    public void addSvnEnvComparison(SvnEnvComparison svnEnvComparison)
    {
        this.svnEnvComparison.add(svnEnvComparison);
    }

    public SvnEnvComparison createSvnEnvCompaInstance(String path)
    {
        SvnEnvComparison instance = new SvnEnvComparison(path);
        addSvnEnvComparison(instance);
        return instance;
    }

    /**
     * Refresh actual info to regain from sf-version.properties
     */
    public void refreshSvnEnvCompaList()
    {
        for (SvnEnvComparison envCom : svnEnvComparison)
        {
            envCom.refreshEnvActualInfo();
        }
    }

    public void verifyValuable()
    {
        for (SvnEnvComparison comp : svnEnvComparison)
        {
            for (EnvActualInfo envInfo : comp.getEnvInfo())
            {
                if (!envInfo.getFixStatus().equals("NONE"))
                {
                    this.valuable = true;
                    return;
                }
            }
        }
    }

    public boolean isValuable()
    {
        return valuable;
    }

}
