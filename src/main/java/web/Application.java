package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer
{
    public static ApplicationContext context;

    public static void main(String[] args)
    {
        context = SpringApplication.run(Application.class, args);
        DBUtil dbUtil = context.getBean("dbUtil", DBUtil.class);
        CacheData cache = context.getBean("cacheData", CacheData.class);
        cache.setAllFeatures(dbUtil.getAllFeature());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        SpringApplicationBuilder builder = application.sources(Application.class);
        return builder;
    }

}
