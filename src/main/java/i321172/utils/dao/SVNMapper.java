package i321172.utils.dao;

import i321172.bean.SVNFileBean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SVNMapper implements RowMapper<SVNFileBean>
{

    @Override
    public SVNFileBean mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
