package web;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import utils.HttpClientUtil;
import bean.EnvEnum;

@Component
public class ScheduledTasks
{
    @Scheduled(fixedRate = 3600000)
    public void refreshEnvSFVersion() throws Exception
    {
        fetchEnvVersionInfo();
    }

    private void fetchEnvVersionInfo() throws Exception
    {
        for (EnvEnum elem : EnvEnum.values())
        {
            elem.setVersionInfo(HttpClientUtil.getSFVersion(elem.toVersionUrl(), elem.isNeedProxy()));
        }
    }
}
