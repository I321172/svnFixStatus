package i321172.utils.dao;

import i321172.bean.SVNFileBean;
import i321172.bean.SvnInfoBean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SVNMapper implements RowMapper<SVNFileBean>
{

    @Override
    public SVNFileBean mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        SVNFileBean svnFile = new SVNFileBean(rs.getString("filepath"));
        SvnInfoBean info = new SvnInfoBean();
        svnFile.setSvnInfo(info);
        info.setAuthor(rs.getString("author"));
        info.setRevision(rs.getLong("revision"));
        info.setCreateDate(rs.getString("date"));
        info.setComment(rs.getString("comment"));
        info.setType(rs.getString("type"));
        return svnFile;
    }

}
