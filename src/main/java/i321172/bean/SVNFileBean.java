package i321172.bean;

import java.util.ArrayList;
import java.util.List;

public class SVNFileBean
{
    private String       className;
    private SvnInfoBean  svnInfo;
    private List<String> content = new ArrayList<String>();

    public SVNFileBean(String className)
    {
        setClassName(className);
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public SvnInfoBean getSvnInfo()
    {
        return svnInfo;
    }

    public void setSvnInfo(SvnInfoBean svnInfo)
    {
        this.svnInfo = svnInfo;
    }

    public List<String> getContent()
    {
        return content;
    }

    public void addContent(String content)
    {
        this.content.add(content);
    }

}
