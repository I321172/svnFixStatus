package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application
{
    public static ApplicationContext context;

    public static void main(String[] args)
    {
        context = SpringApplication.run(Application.class, args);
        DBUtil dbUtil = context.getBean("dbUtil", DBUtil.class);
        CacheData cache = context.getBean("cacheData", CacheData.class);
        cache.setAllFeatures(dbUtil.getAllFeature());
    }

}
