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
    private List<SVNFileBean>       result = new ArrayList<SVNFileBean>();
    private Date                    startDate;
    private static SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd");

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
        if (logEntry.getDate().after(startDate) && logEntryCondition(logEntry))
        {
            Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();
            if (map != null && map.size() > 0)
            {
                for (String key : map.keySet())
                {
                    SVNLogEntryPath entry = map.get(key);
                    if (entry.getPath().matches(".*\\.java") && checkInFileCondition(entry))
                    {
                        SVNFileBean svnFile = new SVNFileBean(entry.getPath());
                        svnFile.setSvnInfo(new SvnInfoBean(logEntry.getRevision(), logEntry.getAuthor(), logEntry
                                .getDate().toString()));
                        result.add(svnFile);
                    }
                }
            }
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
        return true;
    }

    public List<SVNFileBean> getResultList()
    {
        return result;
    }

    public Date getStartDate() throws ParseException
    {
        if (startDate == null)
        {
            setStartDate("2015-01-01");
        }
        return startDate;
    }

    public String getStartDateString() throws ParseException
    {
        return sdf.format(getStartDate());
    }

    public void setStartDate(String startDate) throws ParseException
    {
        this.startDate = sdf.parse(startDate);
    }

}
