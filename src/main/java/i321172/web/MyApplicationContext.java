package i321172.web;

import i321172.utils.DBUtil;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

@Repository
public class MyApplicationContext implements ApplicationContextAware
{
    public static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
        DBUtil dbUtil = context.getBean("dbUtil", DBUtil.class);
        CacheData cache = context.getBean("cacheData", CacheData.class);
        cache.setAllFeatures(dbUtil.getAllFeature());
    }
}
