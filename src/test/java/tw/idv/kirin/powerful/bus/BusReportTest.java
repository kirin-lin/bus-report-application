package tw.idv.kirin.powerful.bus;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class BusReportTest {
  @Before
  public void init() throws SQLException {
    H2DbHelper.getInstance().init();

    try (Connection connection = H2DbHelper.getConnection();
        Statement stmt = connection.createStatement()) {
      stmt.execute("TRUNCATE TABLE bus_report");
      stmt.execute("ALTER TABLE bus_report ALTER COLUMN id RESTART WITH 1");
    }
  }

  @After
  public void close() {
    H2DbHelper.getInstance().close();
  }

  @Test
  public void testDelete() throws SQLException {
    try (Connection connection = H2DbHelper.getConnection();
        Statement stmt = connection.createStatement()) {

      stmt.execute("INSERT INTO bus_report (name, quantity) VALUES ( 'kirin01', 10)");
      stmt.execute("INSERT INTO bus_report (name, quantity) VALUES ( 'kirin02', 20)");
      
      
      
//      List<BusReport> busReport = BusReportHelper.getInstance().getContacts();
//      Assert.assertEquals(3, contacts.size());
      try( ResultSet rs = stmt.executeQuery("SELECT count(*) FROM bus_report")) {
        Assert.assertTrue("Count should resturn as least one row",
            rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        Assert.assertFalse(
            "Count not should resturn more than one row", rs.next());
      }
      
      final BusReport r = new BusReport(1);
      Assert.assertNotEquals(-1, r.getId());
      r.delete();
      Assert.assertEquals(-1, r.getId());
      Assert.assertEquals("kirin01", r.getName());
      Assert.assertEquals(10,  r.getQuantity());
      
//      contacts = ContactsHelper.getInstance().getContacts();
//      Assert.assertEquals(2,  contacts.size());
//      Assert.assertEquals(1L, contacts.get(0).getId());
//      Assert.assertEquals(3L, contacts.get(1).getId());
      try( ResultSet rs = stmt.executeQuery("SELECT count(*) FROM bus_report")) {
        Assert.assertTrue("Count should resturn as least one row",
            rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertFalse(
            "Count not should resturn more than one row", rs.next());
      }
    }
  }

  @Test
  public void testSave() throws SQLException {
    BusReport r = new BusReport();
    r.setName("Albert Attard");
    r.setStartDate("");
    r.setEndDate("");
    r.setQuantity(10);

    Assert.assertEquals(-1, r.getId());

    r.save();

    try (Connection connection = H2DbHelper.getConnection();
        Statement stmt = connection.createStatement()) {
      try (ResultSet rs = stmt
          .executeQuery("SELECT count(*) FROM bus_report")) {
        Assert.assertTrue("Count should resturn as least one row",
            rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertFalse(
            "Count not should resturn more than one row", rs.next());
      }

      try (ResultSet rs = stmt.executeQuery("SELECT * FROM bus_report")) {
        Assert.assertTrue("Count should resturn as least one row",
            rs.next());
        Assert.assertEquals(1, rs.getInt("id"));
        Assert.assertEquals("Albert Attard", rs.getString("name"));
        Assert.assertFalse(
            "Count not should resturn more than one row", rs.next());
      }
    }

    r.setName("Attard Albert");
    r.save();

    Assert.assertEquals(1, r.getId());
    Assert.assertEquals("Attard Albert", r.getName());
    Assert.assertEquals(10, r.getQuantity());

//    final List<Contact> contacts = ContactsHelper.getInstance()
//        .getContacts();
//    Assert.assertNotNull(contacts);
//    Assert.assertEquals(1, contacts.size());
  }

}
