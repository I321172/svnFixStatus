package i321172.utils;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("dbUtil")
public class DBUtil
{
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbc;
    private Logger       logger = Logger.getLogger(getClass());

    public void releaseConnectionPool()
    {
        if (jdbc.getDataSource() instanceof SmartDataOracleSourceImp)
        {
            ((SmartDataOracleSourceImp) jdbc.getDataSource()).closeAllConnections();
        }
    }

    private void log(String msg)
    {
        logger.info(msg);
    }
}
