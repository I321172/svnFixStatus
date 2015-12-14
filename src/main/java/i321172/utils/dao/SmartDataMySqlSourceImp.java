package i321172.utils.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.SmartDataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class SmartDataMySqlSourceImp extends MysqlDataSource implements SmartDataSource
{
    private Logger           logger   = Logger.getLogger(SmartDataMySqlSourceImp.class);
    private int              point    = 0;
    private int              count    = 0;
    private int              maxCount = 475;
    Map<Integer, Connection> conns    = new HashMap<Integer, Connection>();

    public Connection getConnection() throws SQLException
    {
        Connection con = null;
        if (count < maxCount)
        {
            con = super.getConnection();
            conns.put(count++, con);
        }
        point = point % maxCount;
        con = conns.get(point++);
        log("Get a new connection in the pool at point: " + (point - 1));
        return con;
    }

    private void log(String msg)
    {
        logger.debug(msg);
    }

    public void closeAllConnections()
    {
        logger.info("Close All Connections! Count = " + count);
        for (int i : conns.keySet())
        {
            try
            {
                if (!conns.get(i).isClosed())
                {
                    conns.get(i).close();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        conns = new HashMap<Integer, Connection>();
        point = 0;
        count = 0;
    }

    private static final long serialVersionUID = 1L;

    public int getMaxCount()
    {
        return maxCount;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean shouldClose(Connection con)
    {
        // TODO Auto-generated method stub
        return false;
    }

}