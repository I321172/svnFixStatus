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

    }
}
