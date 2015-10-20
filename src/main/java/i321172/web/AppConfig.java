package i321172.web;

import i321172.utils.SmartDataMySqlSourceImp;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ImportResource("classpath*:properties-config.xml")
public class AppConfig
{
    @Bean(name = "dataSource")
    public DataSource getMysqlSource(@Value("${mysql.host}") String host, @Value("${mysql.name}") String databaseName,
            @Value("${mysql.user}") String user, @Value("${mysql.password}") String password,
            @Value("${mysql.port}") String port)
    {
        SmartDataMySqlSourceImp dataSource = new SmartDataMySqlSourceImp();
        dataSource.setServerName(host);
        dataSource.setDatabaseName(databaseName);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setPort(Integer.valueOf(port));
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(@Qualifier("dataSource") DataSource source)
    {
        return new JdbcTemplate(source);
    }
}
