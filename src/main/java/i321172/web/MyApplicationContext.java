package i321172.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

@Repository
public class MyApplicationContext implements ApplicationContextAware
{
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType)
    {
        return context.getBean(requiredType);
    }

    public static <T> T getBean(String name, Class<T> requiredType)
    {
        return context.getBean(name, requiredType);
    }

}
