package web;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import bean.EnvEnum;
import bean.SvnEnvComparison;
import bean.SvnEnvComparison.EnvActualInfo;
import bean.SvnInfoBean;

@Repository
public class SVNUtils
{
    private Logger              logger = Logger.getLogger(getClass());
    private final static String url    = "https://svn.successfactors.com/repos/";
    private SVNRepository       repository;

    public SVNUtils()
    {
        setupLibrary();
        try
        {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
        } catch (SVNException e)
        {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        @SuppressWarnings("deprecation")
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager("adamzhang", "cMpgSrdj");
        repository.setAuthenticationManager(authManager);
    }

    private static void setupLibrary()
    {
        DAVRepositoryFactory.setup();
    }

    public SvnInfoBean checkFixCodeInLatestBuildVersion(long checkInVersion) throws Exception
    {
        SvnInfoBean svnInfoBean = new SvnInfoBean();
        long start = getTime();
        log("Start to fetch SVN info for version:" + checkInVersion);
        @SuppressWarnings("unchecked")
        Collection<SVNLogEntry> logEntries = repository.log(new String[] { "" }, null, checkInVersion, checkInVersion,
                true, true);

        for (Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();)
        {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

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
        }
        log("SVN action takes " + getDuration(start));

        handleSvnEnvComparison(svnInfoBean);

        return svnInfoBean;
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

    private long getTime()
    {
        return System.currentTimeMillis();
    }

    private String getDuration(long start)
    {
        long dur = (getTime() - start) / 1000;
        return dur + " seconds";
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

    // public static void main(String args[]) throws Exception
    // {
    // SVNUtils tool = new SVNUtils();
    // tool.fetchBuildVersionInfo();
    // }

}
