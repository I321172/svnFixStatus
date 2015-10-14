package web;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import web.aop.log.LogAdvice;
import bean.CoverageBean;
import bean.FeatureCoverage;
import bean.SvnInfoBean;

@Controller
public class RequestController
{
    private Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String showClassInfo(@RequestParam(value = "feature", defaultValue = "RBP") String feature,
            @RequestParam(value = "only", defaultValue = "Package") String onlyPackage, Model model)
    {
        FeatureCoverage featureCov;
        if (onlyPackage.equals("Package"))
        {
            featureCov = getFeature(feature, false);
            model.addAttribute("list", featureCov.getOnlyPackagelist());
            model.addAttribute("urlPrefix", getCoverageFilePrefix());
        } else
        {
            featureCov = getFeature(feature, true);
            model.addAttribute("list", featureCov.getList());
            model.addAttribute("urlPrefix", getCoverageFilePrefix());
        }
        return "showcoverage";
    }

    @RequestMapping(value = "/show/fix")
    public String checkSVNFix(@RequestParam(value = "checkInVersion") String checkInVersion, Model model)
    {
        SVNUtils svnUtil = MyApplicationContext.context.getBean(SVNUtils.class);
        long version = getCheckInVersion(checkInVersion);

        CacheData cache = MyApplicationContext.context.getBean("cacheData", CacheData.class);
        SvnInfoBean svnInfo = cache.getStoredSvnInfo(version);
        if (svnInfo == null)
        {
            try
            {
                svnInfo = svnUtil.checkFixCodeInLatestBuildVersion(version);
                svnInfo.verifyValuable();
                if (svnInfo.isValuable())
                {
                    cache.putStoredRevision(version, svnInfo);
                }
            } catch (Exception e)
            {
                log("SVN fetch version failed by " + e.getMessage());
            }
        } else
        {
            svnUtil.handleSvnEnvComparison(svnInfo);
            log("Get cached SVN Info; Revision: " + version);
        }

        model.addAttribute("svnInfo", svnInfo);
        return "svn";
    }

    private int getCheckInVersion(String checkInVersion)
    {
        if (checkInVersion.matches("\\d+"))
        {
            return Integer.parseInt(checkInVersion);
        } else
        {
            log("Invalid Number:" + checkInVersion + "; Set version as 1");
            return 1;
        }
    }

    @RequestMapping(value = "/show/aep")
    public String fetchAEPRunningJobs(Model model)
    {
        return "aeprun";
    }

    @RequestMapping(value = "/test")
    public String test(Model model)
    {
        model.addAttribute("list", getTest());
        model.addAttribute("urlPrefix", getCoverageFilePrefix());
        return "showcoverage";
    }

    private String getCoverageFilePrefix()
    {
        CacheData cache = MyApplicationContext.context.getBean("cacheData", CacheData.class);
        return cache.getCoverageFilePrefix();
    }

    @RequestMapping(value = "/refresh/task")
    public String refreshTask(Model model) throws Exception
    {
        ScheduledTasks tasks = MyApplicationContext.context.getBean(ScheduledTasks.class);
        tasks.refreshEnvSFVersion();
        model.addAttribute("result", "Schedule Task Triggered Success! ");
        return "refresh";
    }

    @RequestMapping(value = "/refresh/conn")
    public String refreshConnect(Model model) throws Exception
    {
        DBUtil util = MyApplicationContext.context.getBean("dbUtil", DBUtil.class);
        util.releaseConnectionPool();
        model.addAttribute("result", "Connection All Closed! Connection Pool Refreshed!");
        return "refresh";
    }

    @RequestMapping(value = "/show/status")
    public String showRequestStatus(Model model) throws Exception
    {
        LogAdvice advice = MyApplicationContext.context.getBean("logAdvice", LogAdvice.class);
        String msg = "SVN Fix Status Visit Count: " + advice.getSvnCount() + "; Code Covereage Visit Count: "
                + advice.getCoverageCount();
        model.addAttribute("result", msg);
        return "refresh";
    }

    private FeatureCoverage getFeature(String feature, boolean fetchAll)
    {
        CacheData cache = MyApplicationContext.context.getBean("cacheData", CacheData.class);
        DBUtil util = MyApplicationContext.context.getBean("dbUtil", DBUtil.class);
        FeatureCoverage featureData = cache.getFeatureData(feature);
        if (featureData == null)
        {
            if (fetchAll)
            {
                log("Fetch class and package data for Feature:" + feature);
                featureData = util.queryClass(feature);
            } else
            {
                log("Fetch only package data for Feature:" + feature);
                featureData = util.queryPackage(feature);
            }
            cache.putFeatureData(feature, featureData);
        } else
        {
            log("Fetch cached Feature:" + feature);
            if (fetchAll && featureData.getList().isEmpty())
            {
                log("Fetch class data for Feature:" + feature);
                featureData.setList(util.queryClassList(feature));
            }
        }
        return featureData;
    }

    private List<CoverageBean> getTest()
    {
        List<CoverageBean> list = new ArrayList<CoverageBean>();
        for (int i = 0; i < 3; i++)
        {
            CoverageBean bean = new CoverageBean();
            bean.setClassName("classname" + i);
            bean.setNewTotalCoverage("50" + i);
            bean.setNewTotalLines("60" + i);
            list.add(bean);
        }
        return list;
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

}
