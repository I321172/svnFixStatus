package i321172.web;

import i321172.bean.EnvEnum;
import i321172.utils.HttpClientUtil;
import i321172.utils.dao.DBUtil;
import i321172.utils.svn.LogEntryHandlerDBMapping;
import i321172.utils.svn.SVNUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks
{
    @Scheduled(fixedDelay = 9900000)
    public void refreshEnvSFVersion() throws Exception
    {
        HttpClientUtil httpUtil = MyApplicationContext.getBean(HttpClientUtil.class);
        for (EnvEnum elem : EnvEnum.values())
        {
            elem.setVersionInfo(httpUtil.fetchWeb(elem.toVersionUrl(), elem.isNeedProxy()));
        }
    }

    /**
     * Each connection timeout is 8 hours (28800 s), throw
     * CommunicationsException: The last packet successfully received from the
     * server was xxx milliseconds ago if two action exceed 8 hours
     * 
     * @throws Exception
     */
    @Scheduled(fixedDelay = 28000000)
    public void refreshDBConnection() throws Exception
    {
        DBUtil dbUtil = MyApplicationContext.getBean(DBUtil.class);
        dbUtil.activeConnections();
    }

    /**
     * Provided when DB data is empty, start to fetch data from SVN
     * 
     * @throws Exception
     */
    // @Scheduled(fixedDelay = 5000)
    public void refreshSVNCheckIns() throws Exception
    {
        DBUtil dbUtil = MyApplicationContext.getBean(DBUtil.class);
        SVNUtil svnUtil = MyApplicationContext.getBean(SVNUtil.class);
        LogEntryHandlerDBMapping logHander = MyApplicationContext.getBean(LogEntryHandlerDBMapping.class);
        long start = dbUtil.getLatestRevision();
        long max = svnUtil.getLatestRevision();
        if (start < max)
        {
            start = svnUtil.getRevisionAvailable(start + 1, max);
            long end = svnUtil.getRevisionAvailable(start + 499, max);
            svnUtil.fetchSVNBasicLogEntry(logHander, start, end);
        }
    }

    /**
     * Regular fetch after DB data is near latest
     * 
     * @throws Exception
     */
    @Scheduled(fixedDelay = 3600000)
    public void refreshSVNLatestCheckIns() throws Exception
    {
        DBUtil dbUtil = MyApplicationContext.getBean(DBUtil.class);
        SVNUtil svnUtil = MyApplicationContext.getBean(SVNUtil.class);
        LogEntryHandlerDBMapping logHander = MyApplicationContext.getBean(LogEntryHandlerDBMapping.class);
        long start = dbUtil.getLatestRevision();
        long max = svnUtil.getLatestRevision();
        if (start < max)
        {
            start = svnUtil.getRevisionAvailable(start + 1, max);
            svnUtil.fetchSVNBasicLogEntry(logHander, start, max);
        }
    }
}
