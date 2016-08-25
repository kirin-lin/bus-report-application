package tw.idv.kirin.powerful.bus;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.flyway.core.Flyway;

public class H2DbHelper {
  private static final Logger LOGGER = LoggerFactory
      .getLogger(H2DbHelper.class);
  
  private static final H2DbHelper INSTANCE = new H2DbHelper();

  public static H2DbHelper getInstance() {
    return H2DbHelper.INSTANCE;
  }

  private BasicDataSource ds;

  private H2DbHelper() {

  }

  public void init() {
    //H2DbHelper.LOGGER.debug("Loading properties");
        
    LOGGER.debug("Creating the datasource");
    //String dbClass = "org.h2.Driver";
    //String dbUrl = "jdbc:mysql://192.168.1.5/clptc";
    //String dbUsername = "root";
    //String dbPassword = "cArbOn12";
    ds = new BasicDataSource();
    ds.setDriverClassName("org.h2.Driver");
    ds.setUrl("jdbc:h2:target/powerful");
    ds.setUsername("sa");
    ds.setPassword("");
    
    
    LOGGER.debug("Executing Fly (database migration");
    Flyway flyway = new Flyway();
    flyway.setDataSource(ds);
    flyway.migrate();
    
  }

  public void close() {
    if (ds != null) {
      try {
        LOGGER.debug("Closing the data source");
        ds.close();
      } catch (SQLException e) {
        LOGGER.error("Failed to close the data source", e);
      }
    }
  }
  
  public void registerShutdownHook() {
    Runtime.getRuntime().addShutdownHook( new Thread( new Runnable() {
      @Override
      public void run() {
        close();
      }
    }));
  }

  
  public DataSource getDataSource() {
    return ds;
  }
  
  public static Connection getConnection() throws SQLException {
    return getInstance().getDataSource().getConnection();
  }
}
