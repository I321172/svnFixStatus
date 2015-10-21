package i321172.utils;

import javax.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("dbUtil")
public class DBUtil
{
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbc;

    public void releaseConnectionPool()
    {
        ((SmartDataMySqlSourceImp) jdbc.getDataSource()).closeAllConnectoins();
    }

    public long getNearestRevision(String begin)
    {
        return jdbc
                .queryForObject(
                        "select revision from revision where to_days(date)-to_days(?)<10 and to_days(date)-to_days(?)>0 order by date desc limit 1",
                        Integer.class, begin, begin);
    }
}
