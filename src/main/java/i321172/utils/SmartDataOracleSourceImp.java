package i321172.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import oracle.jdbc.pool.OracleDataSource;

import org.springframework.jdbc.datasource.SmartDataSource;

public class SmartDataOracleSourceImp extends OracleDataSource implements SmartDataSource
{

    private static final long serialVersionUID = 1L;

    public SmartDataOracleSourceImp() throws SQLException
    {
        super();
        // TODO Auto-generated constructor stub
    }

    private int      point    = 0;
    private int      count    = 0;
    private int      maxCount = 10;
    List<Connection> conns    = new ArrayList<Connection>();

    public java.sql.Connection getConnection() throws SQLException
    {
        Connection con = null;
        if (count < maxCount)
        {
            con = super.getConnection();
            conns.add(con);
            count++;
        }
        point = point % maxCount;
        return conns.get(point++);
    }

    public void closeAllConnections()
    {
        for (Connection con : conns)
        {
            try
            {
                if (!con.isClosed())
                {
                    con.close();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        conns.clear();
        point = 0;
        count = 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
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
