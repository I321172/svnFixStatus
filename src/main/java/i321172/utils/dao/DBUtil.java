package i321172.utils.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import i321172.MyContext;
import i321172.bean.Entry;
import i321172.bean.SVNFileBean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service("dbUtil")
public class DBUtil
{
    private Logger       logger = Logger.getLogger(getClass());
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jdbc;

    public void releaseConnectionPool()
    {
        SmartDataMySqlSourceImp dataSource = MyContext.getBean(SmartDataMySqlSourceImp.class);
        dataSource.closeAllConnectoins();
    }

    public long getNearestRevision(String begin)
    {
        String sql = "select revision from revision where to_days(date)-to_days(?)<0 order by date desc limit 1";
        return queryForLong(sql, begin);
    }

    public void execute(String sql)
    {
        try
        {
            jdbc.execute(sql);
        } catch (DataAccessException dae)
        {
            if (dae.getMessage().contains("Duplicate entry"))
            {
                logger.debug("Handle Duplicate Primary Key");
            } else
            {
                throw dae;
            }
        }
    }

    public void storeToDB(List<SVNFileBean> svnFiles)
    {
        for (SVNFileBean svnFile : svnFiles)
        {
            insertIntoFileInfo(svnFile.getClassName(), svnFile.getSvnInfo().getRevision(), svnFile.getSvnInfo()
                    .getType(), svnFile.getCopyPath());
            insertIntoRevision(svnFile.getSvnInfo().getRevision(), svnFile.getSvnInfo().getAuthor(), svnFile
                    .getSvnInfo().getCreateDate(), svnFile.getSvnInfo().getComment());
        }
    }

    public void insertIntoRevision(long revision, String author, String date, String comment)
    {
        String sql = getRevisionInsertSql(revision, author, date, comment);
        execute(sql);
    }

    public void insertIntoFileInfo(String filePath, long revision, String type, String copyPath)
    {
        String sql = getfileInfoInsertSql(filePath, revision, type, copyPath);
        execute(sql);
    }

    private String getfileInfoInsertSql(String filePath, long revision, String type, String copyPath)
    {
        return "insert into fileinfo set filePath='" + filePath + "',revision=" + revision + ",type='" + type
                + "',copypath='" + copyPath + "'";
    }

    private String getRevisionInsertSql(long revision, String author, String date, String comment)
    {
        StringBuffer buff = new StringBuffer("insert into revision set author='" + author + "',revision=" + revision);
        if (date != null && !date.equals(""))
        {
            buff.append(",date='" + date + "'");
        }
        if (comment != null)
        {
            comment = comment.replace("'", "").replace("\\", "SLASH");
            if (comment.length() > 1000)
            {
                logger.debug("Comment too long for revision:" + revision);
                comment = comment.substring(0, 999);
            }
            buff.append(",comment='" + comment + "'");
        }
        return buff.toString();
    }

    private String queryForObject(String sql, Object... args)
    {
        return jdbc.queryForObject(sql, String.class, args);
    }

    private long queryForLong(String sql, Object... args)
    {
        String result = queryForObject(sql, args);
        if (result == null)
            return 0;
        else
            return Long.parseLong(result);
    }

    public long getLatestRevision()
    {
        String sql = "select max(revision) from revision";
        return queryForLong(sql);
    }

    public List<SVNFileBean> getSVNInfoList(String author, String begin, String end)
    {
        return getSVNInfoList(author, begin, end, "type='Add' and (f.copypath is null or f.copypath='null')");
    }

    public List<SVNFileBean> getSVNInfoList(String author, String begin, String end, String externalCondition)
    {
        StringBuffer sql = new StringBuffer(
                "select r.revision,r.author,r.date,r.comment,f.filepath,f.type,f.copypath from revision as r inner join fileinfo as f on r.revision=f.revision where r.author=? and r.date>?");
        if (end != null && !end.equals("Now"))
            sql.append(" and r.date)<'" + end + "'");
        if (externalCondition != null)
            sql.append(" and " + externalCondition);
        sql.append(" order by r.date desc");
        return jdbc.query(sql.toString(), new Object[] { author, begin }, new SVNMapper());
    }

    public void getActiveConnections()
    {
        SmartDataMySqlSourceImp dataSource = MyContext.getBean(SmartDataMySqlSourceImp.class);
        for (int i = 0; i < dataSource.getMaxCount(); i++)
        {
            execute("show global variables like 'wait_timeout'");
        }
    }

    public List<Entry> getAEPDefinitions()
    {
        String sql = "select name,id from aepdef";

        return jdbc.query(sql, new RowMapper<Entry>()
        {
            @Override
            public Entry mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                // TODO Auto-generated method stub
                Entry entry = new Entry();
                entry.setKey(rs.getString("name"));
                entry.setValue(rs.getString("id"));
                return entry;
            }
        });
    }

    public int insertIntoAEPDef(Map<String, String> entrys)
    {
        StringBuffer sql = new StringBuffer("insert into aepdef values (");
        for (String name : entrys.keySet())
        {
            sql.append(name);
            sql.append(",");
            sql.append(entrys.get(name));
            sql.append(")");
        }
        return jdbc.update(sql.toString());
    }

    /**
     * Insert into filename table if filename not exists
     */
    public int getLatestFilePathToNamePair()
    {
        String sql = "insert into filename(filepath,filename) select filepath,substring_index(filepath,'/',-1) from fileinfo where filepath not in (select filepath from filename)";
        return jdbc.update(sql);
    }

    /**
     * Update filename table to check whether the file is deleted
     */
    public int getUpdatedDeletedFile()
    {
        String sql = "update filename set invalid=true where (invalid is null or invalid =false) and filepath in (select filepath from (select filepath,type from (select filepath,type from fileinfo order by revision desc) as t1 group by filepath) as t2 where type='Delete')";
        return jdbc.update(sql);
    }

    /**
     * Update filename table to check whether deleted file are used again
     * 
     * @return
     */
    public int getUpdatedUnDeletedFile()
    {
        String sql = "update filename set invalid=false where invalid=true and filepath not in (select filepath from (select filepath,type from (select filepath,type from fileinfo order by revision desc) as t1 group by filepath) as t2 where type='Delete')";
        return jdbc.update(sql);
    }

}
