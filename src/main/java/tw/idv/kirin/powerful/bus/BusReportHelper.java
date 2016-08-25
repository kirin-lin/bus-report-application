package tw.idv.kirin.powerful.bus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BusReportHelper {
  private static final BusReportHelper INSTANCE = new BusReportHelper();

  public static BusReportHelper getInstance() {
    return INSTANCE;
  }

  private BusReportHelper() {
  }

  public List<BusReport> getBusReports() throws SQLException {
    List<BusReport> reports = new ArrayList<>();

    String sql = "SELECT * FROM bus_report ORDER By id";
    try (Connection connection = H2DbHelper.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        final BusReport report = new BusReport();
        report.setId(rs.getInt("id"));
        report.setName(rs.getString("name"));
        report.setQuantity(rs.getInt("quantity"));
        report.setSalesName(rs.getString("sales_name"));
        report.setStartDate(rs.getString("start_date"));
        report.setEndDate(rs.getString("end_date"));
        reports.add(report);
      }
    }
    return reports;
  }
  
  public List<BusReport> getLast50BusReports() throws SQLException {
    List<BusReport> reports = new ArrayList<>();

    String sql = "SELECT * FROM bus_report ";
    sql = sql + " ORDER By id DESC ";
    sql = sql + " LIMIT 50 ";
    try (Connection connection = H2DbHelper.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        final BusReport report = new BusReport();
        report.setId(rs.getInt("id"));
        report.setName(rs.getString("name"));
        report.setQuantity(rs.getInt("quantity"));
        report.setSalesName(rs.getString("sales_name"));
        report.setStartDate(rs.getString("start_date"));
        report.setEndDate(rs.getString("end_date"));
        reports.add(report);
      }
    }
    return reports;
  }
}
