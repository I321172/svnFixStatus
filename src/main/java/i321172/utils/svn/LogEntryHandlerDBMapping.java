package i321172.utils.svn;

import i321172.MyContext;
import i321172.utils.dao.DBUtil;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

@Repository
public class LogEntryHandlerDBMapping implements ISVNLogEntryHandler
{
    private static SimpleDateFormat sdfdb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void handleLogEntry(SVNLogEntry logEntry) throws SVNException
    {
        Map<String, SVNLogEntryPath> map = logEntry.getChangedPaths();

        if (map != null && map.size() > 0)
        {
            getDBUtil().insertIntoRevision(logEntry.getRevision(), logEntry.getAuthor(),
                    sdfdb.format(logEntry.getDate()), logEntry.getMessage());
            for (String key : map.keySet())
            {
                SVNLogEntryPath entry = map.get(key);
                if (key.matches(".*.java") || key.endsWith(".js") || key.endsWith(".css"))
                {
                    getDBUtil().insertIntoFileInfo(entry.getPath(), logEntry.getRevision(), getType(entry.getType()),
                            entry.getCopyPath());
                }
            }
        }
    }

    public DBUtil getDBUtil()
    {
        return MyContext.getBean(DBUtil.class);
    }

    private String getType(char type)
    {
        switch (type)
        {
            case 'A':
                return "Add";
            case 'M':
                return "Modify";
            case 'D':
                return "Delete";
            default:
                return "Unknown";
        }
    }
}
