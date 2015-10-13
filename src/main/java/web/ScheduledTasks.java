package web;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import utils.HttpClientUtil;
import bean.EnvEnum;

@Component
public class ScheduledTasks
{
    @Scheduled(fixedRate = 9900000)
    public void refreshEnvSFVersion() throws Exception
    {
        fetchEnvVersionInfo();
    }

    private void fetchEnvVersionInfo() throws Exception
    {
        HttpClientUtil httpUtil = new HttpClientUtil();
        for (EnvEnum elem : EnvEnum.values())
        {
            elem.setVersionInfo(httpUtil.getSFVersion(elem.toVersionUrl(), elem.isNeedProxy()));
        }
    }
}
