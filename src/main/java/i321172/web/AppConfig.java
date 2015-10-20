package i321172.web;

import i321172.utils.SmartDataMySqlSourceImp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

@Configuration
@ImportResource("classpath*:properties-config.xml")
public class AppConfig
{

    private Logger logger = Logger.getLogger(getClass());

    @Bean(name = "dataSource")
    public DataSource getMysqlSource(@Value("${mysql.url}") String url, @Value("${mysql.user}") String user,
            @Value("${mysql.password}") String password)
    {
        SmartDataMySqlSourceImp dataSource = new SmartDataMySqlSourceImp();
        dataSource.setUrl(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "jdbcTemplate")
    public JdbcTemplate getJdbcTemplate(@Qualifier("dataSource") DataSource source)
    {
        return new JdbcTemplate(source);
    }

    // private String svnUrl = "https://svn.successfactors.com/repos/";
    // private String svnUser = "lguan";
    // private String svnPwd = "TLrVfAQ";
    /* "adamzhang", "cMpgSrdj" */
    @Bean(name = "svnRepository")
    public SVNRepository authenticateSVNRepository(@Value("${svn.url}") String url,
            @Value("${svn.user}") String username, @Value("${svn.pwd}") String password)
    {
        setupLibrary();
        SVNRepository repository = null;
        try
        {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = getAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);
        } catch (SVNException svne)
        {
            log("error while creating an SVNRepository for the location '" + url + "': " + svne.getMessage());
        }
        return repository;
    }

    private ISVNAuthenticationManager getAuthenticationManager(String username, String password)
    {
        return SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
    }

    private static void setupLibrary()
    {
        DAVRepositoryFactory.setup();
    }

    private void log(String msg)
    {
        logger.info(msg);
    }
}
