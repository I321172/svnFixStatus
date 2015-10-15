package i321172.utils;

import i321172.bean.CoverageBean;
import i321172.bean.CoverageMapper;
import i321172.bean.FeatureCoverage;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("dbUtil")
public class DBUtil
{
    @Resource(name = "jdbcTemplate")
    private JdbcTemplate        jdbc;
    private Logger              logger     = Logger.getLogger(getClass());
    private static final String packageSql = "select a.feature,a.packageName, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered from qaautocandrot_datPLT05.cctable_package_differdata a where a.feature= ? order by packageName";
    private static final String classSql   = "select * from(select a.feature,a.packageName,'' className, a.status,a.newTotalCoverage newCoverage,a.oldTotalCoverage oldCoverage, a.coverageDiffer coverageDiffer, a.newTotalLines, a.oldTotalLines,a.totallinesdiffer,a.newTotalLinesExecuted,a.oldTotalLinesExecuted,a.tobecovered   from qaautocandrot_datPLT05.cctable_package_differdata a union all select a.feature,a.packageName,b.className,b.status,b.newcoverage newCoverage, b.oldCoverage oldRate, b.coveragediffer coverageDiffer,  b.newTotalLines, b.oldtotallines,b.totallinesdiffer,b.newTotalLinesExecuted,b.oldTotalLinesExecuted,b.tobecovered from qaautocandrot_datPLT05.cctable_package_differdata a, qaautocandrot_datPLT05.cctable_class_differdata b where a.packageName = b.packageName) t1 where t1.feature= ? order by t1.packageName, t1.classname desc";

    public List<CoverageBean> queryList(String sql, String feature)
    {
        return queryList(sql, feature, false);
    }

    public List<CoverageBean> queryList(String sql, String feature, boolean onlyPackage)
    {
        return jdbc.query(sql, new Object[] { feature }, new CoverageMapper(onlyPackage));
    }

    public FeatureCoverage query(String sql, String feature)
    {
        FeatureCoverage center = new FeatureCoverage();
        center.setList(queryList(sql, feature));
        return center;
    }

    public FeatureCoverage queryPackage(String feature)
    {
        FeatureCoverage center = new FeatureCoverage();
        center.setOnlyPackagelist(queryList(packageSql, feature, true));
        return center;
    }

    public FeatureCoverage queryClass(String feature)
    {
        return query(classSql, feature);
    }

    public List<CoverageBean> queryClassList(String feature)
    {
        return queryList(classSql, feature);
    }

    public List<String> getAllFeature()
    {
        String sql = "select distinct feature from qaautocandrot_datPLT05.cctable_package_differdata";
        List<String> features = jdbc.queryForList(sql, String.class);
        log("Get All Feature list, size =" + features.size());
        return features;
    }

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
