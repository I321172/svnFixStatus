package i321172.web;

import i321172.MyContext;
import i321172.aop.log.LogAdvice;
import i321172.bean.SVNFileBean;
import i321172.bean.SvnInfoBean;
import i321172.utils.AEPUtil;
import i321172.utils.HttpClientUtil;
import i321172.utils.dao.DBUtil;
import i321172.utils.svn.AddEntryHandler;
import i321172.utils.svn.SVNUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RequestController
{
    private Logger logger = Logger.getLogger(getClass());

    @RequestMapping(value = "/show/fix")
    public String checkSVNFix(@RequestParam(value = "checkInVersion") String checkInVersion, Model model)
    {
        SvnInfoBean svnInfo = checkSVNFix(checkInVersion);
        model.addAttribute("svnInfo", svnInfo);
        return "svnfix";
    }

    @RequestMapping(value = "/show/fix/now")
    public String checkSVNFixNow(@RequestParam(value = "checkInVersion") String checkInVersion, Model model)
    {
        refreshTask();
        SvnInfoBean svnInfo = checkSVNFix(checkInVersion);
        model.addAttribute("svnInfo", svnInfo);
        return "svnfix";
    }

    @RequestMapping(value = "/show/svn/now")
    public String showSvnInfoFromSVN(@RequestParam(value = "author", defaultValue = "lguan") String author,
            @RequestParam(value = "before", defaultValue = "None") String end,
            @RequestParam(value = "after") String begin, Model model) throws Exception
    {
        SVNUtil svnUtil = MyContext.getBean(SVNUtil.class);
        AddEntryHandler handler = new AddEntryHandler(author, begin);
        if (!end.equals("None"))
        {
            handler.setEndDate(end);
        }
        svnUtil.fetchSVNLogEntry(handler);
        model.addAttribute("svnList", handler.getResultList());
        model.addAttribute("author", author);
        model.addAttribute("start", begin);
        model.addAttribute("end", end.equals("None") ? "Now" : end);
        model.addAttribute("size", handler.getResultList().size());
        return "svninfo";
    }

    @RequestMapping(value = "/show/svn")
    public String showSvnInfoFromDB(@RequestParam(value = "author", defaultValue = "lguan") String author,
            @RequestParam(value = "before", defaultValue = "None") String end,
            @RequestParam(value = "after") String begin,
            @RequestParam(value = "type", defaultValue = "Add") String type, Model model) throws Exception
    {
        DBUtil dbUtil = MyContext.getBean(DBUtil.class);
        List<SVNFileBean> resultList;
        end = end.equals("None") ? "Now" : end;
        if (type.equals("Add"))
        {
            resultList = dbUtil.getSVNInfoList(author, begin, end);
        } else
        {
            resultList = dbUtil.getSVNInfoList(author, begin, end, null);
        }
        model.addAttribute("svnList", resultList);
        model.addAttribute("author", author);
        model.addAttribute("start", begin);
        model.addAttribute("end", end);
        model.addAttribute("size", resultList.size());
        return "svninfo";
    }

    @RequestMapping(value = "/show/aep")
    public String fetchAEPRunningJobs(@RequestParam(value = "user", defaultValue = "Null") String username,
            @RequestParam(value = "module", defaultValue = "Null") String moduleFilter,
            @RequestParam(value = "status", defaultValue = "Null") String jobStatus,
            @RequestParam(value = "env", defaultValue = "Null") String envFilter, Model model) throws Exception
    {
        AEPUtil aep = MyContext.getBean(AEPUtil.class);
        String result = aep.fetchAEPJob(username, moduleFilter, jobStatus, envFilter);
        model.addAttribute("jobs", result);
        return "aeprun";
    }

    @RequestMapping(value = "/refresh/task")
    public String refreshTask(Model model) throws Exception
    {
        String msg = null;
        msg = "Schedule Task Triggered " + (refreshTask() ? "Success" : "Failure") + "! ";
        model.addAttribute("result", msg);
        return "result";
    }

    @RequestMapping(value = "/close/conn")
    public String refreshConnect(Model model) throws Exception
    {
        DBUtil util = MyContext.getBean("dbUtil", DBUtil.class);
        util.releaseConnectionPool();
        model.addAttribute("result", "Connection All Closed! Connection Pool Refreshed!");
        return "result";
    }

    @RequestMapping(value = "/show/status")
    public String showRequestStatus(Model model)
    {
        LogAdvice advice = MyContext.getBean("logAdvice", LogAdvice.class);
        String msg = "SVN Fix Status Visit Count: " + advice.getSvnCount();
        model.addAttribute("result", msg);
        return "result";
    }

    @RequestMapping(value = "/show/compile")
    public String fetchCompileError(@RequestParam(value = "url") String url, Model model) throws Exception
    {
        HttpClientUtil httpClient = MyContext.getBean(HttpClientUtil.class);
        url = getCompileUrl(url);
        String response = httpClient.fetchWeb(url, false);
        String result = null;
        if (response.contains("BUILD SUCCESSFUL"))
        {
            result = "BUILD SUCCESSFUL on " + url;
        } else
        {
            result = "BUILD FAILURE on " + url + "<br/>" + fetchCompileError(response);
        }
        model.addAttribute("utext", result);
        return "result";
    }

    private boolean refreshTask()
    {
        ScheduledTasks tasks = MyContext.getBean(ScheduledTasks.class);
        try
        {
            tasks.refreshEnvSFVersion();
        } catch (Exception e)
        {
            log(e.getMessage());
            return false;
        }
        return true;
    }

    private String getCompileUrl(String url)
    {
        if (!url.startsWith("http"))
            url = "http://autobuildmaster.mo.sap.corp:8080/job/Compile_uitests.purewebdriver_tree/" + url + "/console";
        return url;
    }

    private SvnInfoBean checkSVNFix(String checkInVersion)
    {
        SVNUtil svnUtil = MyContext.getBean(SVNUtil.class);
        long version = convertVersion(checkInVersion);

        CacheData cache = MyContext.getBean("cacheData", CacheData.class);
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
        return svnInfo;
    }

    private long convertVersion(String checkInVersion)
    {
        if (checkInVersion.matches("-?\\d+"))
        {
            return Long.parseLong(checkInVersion);
        } else
        {
            log("Invalid Number:" + checkInVersion + "; Set version as -1");
            return -1;
        }
    }

    private String fetchCompileError(String response)
    {
        String regex = "\\[javac\\].*";
        String code = "uitests.webdriver";
        boolean isContinue = false;
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(response);
        StringBuffer str = new StringBuffer();
        String line = null;
        while (mat.find())
        {
            line = mat.group();
            if (line.contains(code))
            {
                if (isError(line))
                {
                    str.append(line + "\n");
                    isContinue = true;
                } else
                {
                    isContinue = false;
                }
            } else if (isContinue)
            {
                str.append(line + "\n");
            }
        }
        String result = str.toString();
        if (result == null || result.length() == 0)
        {
            result = "Cannot fetch the compile error; See response for details:\n" + response;
        }
        result = "<pre>" + result + "</pre>";
        return result;
    }

    private boolean isError(String line)
    {
        String[] signs = { "error:" };
        for (String sign : signs)
        {
            if (line.contains(sign))
            {
                return true;
            }
        }
        return false;
    }

    private void log(String msg)
    {
        logger.info(msg);
    }

}
