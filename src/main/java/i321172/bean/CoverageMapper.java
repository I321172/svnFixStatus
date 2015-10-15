package i321172.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class CoverageMapper implements RowMapper<CoverageBean>
{
    private boolean onlyPackage;

    public CoverageMapper()
    {

    }

    public CoverageMapper(boolean onlyPackage)
    {
        this.setOnlyPackage(onlyPackage);
    }

    @Override
    public CoverageBean mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        // TODO Auto-generated method stub
        CoverageBean covBean = new CoverageBean();
        covBean.setFeature(rs.getString("feature"));
        covBean.setPackageName(rs.getString("packagename"));
        if (!onlyPackage)
        {
            covBean.setClassName(rs.getString("classname"));
        }
        covBean.setNewTotalCoverage(rs.getString("newcoverage"));
        covBean.setOldTotalCoverage(rs.getString("oldcoverage"));
        covBean.setCoverageDiffer(rs.getString("coveragediffer"));
        covBean.setNewTotalLines(rs.getString("newtotallines"));
        covBean.setOldTotalLines(rs.getString("oldtotallines"));
        covBean.setTotallinesdiffer(rs.getString("totallinesdiffer"));
        covBean.setNewTotalLinesExecuted(rs.getString("newtotallinesexecuted"));
        covBean.setOldTotalLinesExecuted(rs.getString("oldtotallinesexecuted"));
        covBean.setToBeCovered(rs.getString("tobecovered"));
        covBean.verifyIsPackag();
        return covBean;
    }

    public boolean isOnlyPackage()
    {
        return onlyPackage;
    }

    public void setOnlyPackage(boolean onlyPackage)
    {
        this.onlyPackage = onlyPackage;
    }

}
