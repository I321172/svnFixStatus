package i321172.utils.svn;

import i321172.bean.SVNFileBean;
import i321172.bean.SvnInfoBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

/**
 * @author I321172 <br>
 *         1. Have a startDate <br>
 *         2. Must be a java file<br>
 *         3. return a list<br>
 */
public class BasicEntryHandler implements ISVNLogEntryHandler
{
    private List<SVNFileBean>       result       = new ArrayList<SVNFileBean>();
    private Date                    startDate;
    private Date                    endDate;
    private static String[]         subPackage   = {
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/aaf/tests/system/",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/aaf/tests/systemUltra",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/systemUltra/regression",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/systemUltra/sanity",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/system/regression",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/system/sanity" };
    private String                  fileRegex    = "PLT.*.java";
    /**
     * True: Check in File must in the subPackage
     */
    private boolean                 verifyFolder = false;
    private static SimpleDateFormat sdf          = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdfo         = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfdb        = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BasicEntryHandler()
    {

    }

    public BasicEntryHandler(String start) throws ParseException
    {
        this.setStartDate(start);
    }

    @Override
    public void handleLogEntry(SVNLogEntry logEntry) throws SVNException
    {
        if (dateCondition(logEntry.getDate()) && logEntryCondition(logEntry))
        {
            Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
            if (map != null && map.size() > 0)
            {
                for (String key : map.keySet())
                {
                    SVNLogEntryPath entry = map.get(key);
                    if (checkInFileCondition(entry))
                    {
                        SVNFileBean svnFile = new SVNFileBean(entry.getPath());
                        svnFile.setSvnInfo(new SvnInfoBean(logEntry.getRevision(), logEntry.getAuthor(), sdfdb
                                .format(logEntry.getDate()), entry.getType()));
                        result.add(svnFile);
                    }
                }
            }
        }
    }

    /**
     * Should after beginDate and before endDate
     * 
     * @return
     * @throws ParseException
     */
    private boolean dateCondition(Date svnDate)
    {
        return svnDate.after(getStartDate()) && svnDate.before(getEndDate());
    }

    private boolean fileNameCondition(SVNLogEntryPath entry)
    {
        String path = entry.getPath();
        boolean result = false;
        if (isVerifyFolder())
        {
            result = isInPackage(path);
            if (!result)
            {
                return result;
            }
        }
        path = path.substring(path.lastIndexOf("/") + 1);
        result = path.matches(getFileRegex());
        return result;
    }

    private boolean isInPackage(String path)
    {
        if (subPackage == null || subPackage.length == 0)
        {
            // not check
            return true;
        } else
        {
            for (String pack : subPackage)
            {
                if (path.contains(pack))
                    return true;
            }
            return false;
        }
    }

    /**
     * Should be override by sub class if necessary<br>
     * One LogEntry may have several check in file Applied in Sub Instance
     * 
     * @return
     */
    protected boolean logEntryCondition(SVNLogEntry logEntry)
    {
        return true;
    }

    /**
     * Should be override by sub class if necessary<br>
     * 
     * @return
     */
    protected boolean checkInFileCondition(SVNLogEntryPath entry)
    {
        return fileNameCondition(entry);
    }

    public List<SVNFileBean> getResultList()
    {
        return result;
    }

    public void clearResultList()
    {
        result.clear();
    }

    private Date getStartDate()
    {
        if (startDate == null)
        {
            try
            {
                setStartDate("20120101");
            } catch (ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return startDate;
    }

    private Date getEndDate()
    {
        return (endDate != null) ? endDate : new Date();
    }

    public String getStartDateString() throws ParseException
    {
        return sdfo.format(getStartDate());
    }

    public String getEndDateString()
    {
        return sdfo.format(getEndDate());
    }

    public void setStartDate(String startDate) throws ParseException
    {
        this.startDate = sdf.parse(startDate);
    }

    public void setEndDate(String endDate) throws ParseException
    {
        this.endDate = sdf.parse(endDate);
    }

    public String getFileRegex()
    {
        return fileRegex;
    }

    public void setFileRegex(String fileRegex)
    {
        this.fileRegex = fileRegex;
    }

    public String[] getSubPackage()
    {
        return subPackage;
    }

    public void setSubPackage(String[] packages)
    {
        subPackage = packages;
    }

    public boolean isVerifyFolder()
    {
        return verifyFolder;
    }

    /**
     * True: Check in File must in the subPackage
     * 
     * @param verifyFolder
     */
    public void setVerifyFolder(boolean verifyFolder)
    {
        this.verifyFolder = verifyFolder;
    }

}
