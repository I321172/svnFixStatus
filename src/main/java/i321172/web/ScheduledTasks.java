package i321172.web;

import i321172.bean.EnvEnum;
import i321172.utils.HttpClientUtil;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks
{
    @Scheduled(fixedRate = 9900000)
    public void refreshEnvSFVersion() throws Exception
    {
        HttpClientUtil httpUtil = MyApplicationContext.context.getBean(HttpClientUtil.class);
        for (EnvEnum elem : EnvEnum.values())
        {
            elem.setVersionInfo(httpUtil.getSFVersion(elem.toVersionUrl(), elem.isNeedProxy()));
        }
    }

}
