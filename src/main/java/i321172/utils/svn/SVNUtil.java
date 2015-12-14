package i321172.utils.svn;

import i321172.MyContext;
import i321172.bean.EnvEnum;
import i321172.bean.SvnEnvComparison;
import i321172.bean.SvnInfoBean;
import i321172.bean.SvnEnvComparison.EnvActualInfo;
import i321172.utils.StringUtil;
import i321172.utils.dao.DBUtil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

@Service
public class SVNUtil
{
    private Logger        logger = Logger.getLogger(getClass());

    @Resource(name = "svnRepository")
    private SVNRepository repository;

    public SVNUtil()
    {}

    public SvnInfoBean checkFixCodeInLatestBuildVersion(long checkInVersion) throws Exception
    {
        SVNLogEntry logEntry = getSingleLogEntry(checkInVersion);

        SvnInfoBean svnInfoBean = new SvnInfoBean();
        svnInfoBean.setRevision(logEntry.getRevision());
        svnInfoBean.setAuthor(logEntry.getAuthor());
        svnInfoBean.setCreateDate(logEntry.getDate().toString());
        svnInfoBean.setComment(logEntry.getMessage());
        int changeSize = logEntry.getChangedPaths().size();
        log("Fetched " + changeSize + " SVN file changes");
        if (changeSize > 0)
        {
            Set<String> changedPathsSet = logEntry.getChangedPaths().keySet();

            for (Iterator<String> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();)
            {
                SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
                svnInfoBean.createSvnEnvCompaInstance(entryPath.getPath());
            }
        }

        handleSvnEnvComparison(svnInfoBean);

        return svnInfoBean;
    }

    private SVNLogEntry getSingleLogEntry(long checkInVersion) throws SVNException
    {
        @SuppressWarnings("unchecked")
        Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, checkInVersion, checkInVersion,
                true, true);
        return logEntries.iterator().next();
    }

    /**
     * SVN Action!
     * 
     * @param handler
     * @throws Exception
     */
    public void fetchSVNLogEntry(BasicEntryHandler handler) throws Exception
    {
        DBUtil dbUtil = MyContext.getBean(DBUtil.class);
        long startRevision = dbUtil.getNearestRevision(handler.getStartDateString());
        long endRevision = dbUtil.getNearestRevision(handler.getEndDateString());
        fetchSVNBasicLogEntry(handler, startRevision, endRevision);
    }

    /**
     * SVN Action!
     * 
     * @param handler
     * @param startRevision
     * @param endRevision
     * @throws Exception
     */
    public void fetchSVNBasicLogEntry(BasicEntryHandler handler, long startRevision, long endRevision) throws Exception
    {
        repository.log(handler.getSubPackage(), startRevision, endRevision, true, true, handler);
    }

    public void fetchSVNBasicLogEntry(LogEntryHandlerDBMapping handler, long startRevision, long endRevision)
            throws Exception
    {
        repository.log(new String[] {}, startRevision, endRevision, true, true, handler);
    }

    public boolean isRevisionAvailable(long revision)
    {
        try
        {
            getSingleLogEntry(revision);
            return true;
        } catch (Exception e)
        {
            logger.debug(e.getMessage());
            return false;
        }
    }

    public long getRevisionAvailable(long revision, long max)
    {
        if (revision >= max)
            return max;
        while (!isRevisionAvailable(revision))
        {
            log("Revision:" + revision + " is not available! Try to fetch " + ++revision);
        }
        return revision;
    }

    public long getLatestRevision() throws SVNException
    {
        return repository.getLatestRevision();
    }

    public void handleSvnEnvComparison(SvnInfoBean svnInfoBean)
    {
        svnInfoBean.refreshSvnEnvCompaList();
        for (SvnEnvComparison instance : svnInfoBean.getSvnEnvComparison())
        {
            for (EnvEnum env : EnvEnum.values())
            {
                EnvActualInfo actInfo = instance.createEnvActualInfo(env);
                String revision = this.getMatchedRevision(instance.getFeature(), env.getVersionInfo());
                if (revision != null)
                {
                    long envRevision = Long.parseLong(revision);
                    actInfo.setEnvRevision(envRevision);
                    if (envRevision > svnInfoBean.getRevision())
                    {
                        actInfo.setFix(true);
                    } else
                    {
                        actInfo.setFix(false);
                    }
                }
            }
        }
    }

    /**
     * @param feature
     *            Jar file name in sf-version.properties
     * @param source
     * @return
     */
    private String getMatchedRevision(String feature, String source)
    {
        String pattern = "(?<=" + feature + "\\.scm\\.version\\=).*\\d";
        return StringUtil.getMatchString(source, pattern);
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

}
