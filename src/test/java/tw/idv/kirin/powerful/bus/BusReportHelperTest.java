package tw.idv.kirin.powerful.bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class BusReportHelperTest {
  @Before
  public void init() throws SQLException {
    H2DbHelper.getInstance().init();
    
    try( Connection connection = H2DbHelper.getConnection(); Statement stmt = connection.createStatement() ) {
      stmt.execute("TRUNCATE TABLE bus_report");
      stmt.execute("ALTER TABLE bus_report ALTER COLUMN id RESTART WITH 1");
    }
  }
  
  @After
  public void close() {
    H2DbHelper.getInstance().close();
  }
  
  @Test
  public void testLoad() throws SQLException {
    List<BusReport> reports = BusReportHelper.getInstance().getBusReports();
    Assert.assertNotNull(reports);
    Assert.assertTrue(reports.isEmpty());
    
    try(Connection connection = H2DbHelper.getConnection(); Statement stmt= connection.createStatement()) {
      stmt.execute("INSERT INTO bus_report (name, quantity) VALUES ( 'kirin01', 10)");
      stmt.execute("INSERT INTO bus_report (name, quantity) VALUES ( 'kirin02', 20)");
      stmt.execute("INSERT INTO bus_report (name, quantity) VALUES ( 'kirin03', 30)");
      
      reports = BusReportHelper.getInstance().getBusReports();
      Assert.assertNotNull(reports);
      Assert.assertEquals(3, reports.size());
      
      BusReport report = reports.get(0);
      Assert.assertNotNull(report);
      Assert.assertEquals(1, report.getId());
      Assert.assertEquals("kirin01",  report.getName());
      Assert.assertEquals(10,  report.getQuantity());
      
      
    }
  }
}
