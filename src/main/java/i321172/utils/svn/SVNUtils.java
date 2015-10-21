package i321172.utils.svn;

import i321172.bean.EnvEnum;
import i321172.bean.SvnEnvComparison;
import i321172.bean.SvnInfoBean;
import i321172.bean.SvnEnvComparison.EnvActualInfo;
import i321172.utils.DBUtil;
import i321172.web.MyApplicationContext;

import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.io.SVNRepository;

@Service
public class SVNUtils
{
    private Logger                logger      = Logger.getLogger(getClass());

    @Resource(name = "svnRepository")
    private SVNRepository         repository;
    private final static String[] subPackages = {
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/systemUltra/regression",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/systemUltra/sanity",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/system/regression",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/saf/tests/system/sanity",
            "V4/branches/offshore/uitests.purewebdriver/src/java/com/successfactors/aaf/tests/system" };

    public SVNUtils()
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

    public void getSVNBasicLogEntry(long endRevision, BasicEntryHandler handler) throws Exception
    {
        long startRevision = MyApplicationContext.context.getBean(DBUtil.class).getNearestRevision(
                handler.getStartDateString());
        repository.log(subPackages, startRevision, endRevision, true, true, handler);
        handler.getResultList();
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
        return getMatchString(source, pattern);
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

    private String getMatchString(String source, String pattern)
    {
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(source);

        if (matcher.find())
        {
            return source.substring(matcher.start(), matcher.end());
        }

        return null;
    }

}
