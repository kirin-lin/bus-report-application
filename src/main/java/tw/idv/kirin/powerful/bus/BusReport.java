package tw.idv.kirin.powerful.bus;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class BusReport {
  private static final Logger LOGGER = LoggerFactory.getLogger(BusReport.class);
  private int id = -1;
  private String name;
  private int reportType;
  private int quantity;
  private String startDate;
  private String endDate;
  private String salesName;
  private int printMethod;
  private int status;
  private int stage;
  private String log;
  private String memo;
  private String brief;
  private int creator;
  private int cTime;
  private int modifier;
  private int mTime;

  private ArrayList<File> photoList;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getReportType() {
    return reportType;
  }

  public void setReportType(int reportType) {
    this.reportType = reportType;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getSalesName() {
    return salesName;
  }

  public void setSalesName(String salesName) {
    this.salesName = salesName;
  }

  public int getPrintMethod() {
    return printMethod;
  }

  public void setPrintMethod(int printMethod) {
    this.printMethod = printMethod;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getStage() {
    return stage;
  }

  public void setStage(int stage) {
    this.stage = stage;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public String getBrief() {
    return brief;
  }

  public void setBrief(String brief) {
    this.brief = brief;
  }

  public int getCreator() {
    return creator;
  }

  public void setCreator(int creator) {
    this.creator = creator;
  }

  public int getcTime() {
    return cTime;
  }

  public void setcTime(int cTime) {
    this.cTime = cTime;
  }

  public int getModifier() {
    return modifier;
  }

  public void setModifier(int modifier) {
    this.modifier = modifier;
  }

  public int getmTime() {
    return mTime;
  }

  public void setmTime(int mTime) {
    this.mTime = mTime;
  }

  public File getCoverImageFile() {
    String photoPath = "photo/" + String.valueOf(getId());
    // java.awt.Image coverImage = new ImageIcon(getClass().getResource(pdfPath
    // + "/" + "000.jpg")).getImage();
    // 不懂下2行為啥不行
    // LOGGER.debug( getClass().getResource(photoPath + "/"
    // +"000.jpg").getPath() );
    // System.out.println(getClass().getResource(photoPath + "/"
    // +"000.jpg").getPath());
    // com.itextpdf.text.Image img = new com.itextpdf.text.Image(coverImage);
    // com.itextpdf.text.Image img =
    // com.itextpdf.text.Image.getInstance(photoPath
    // + "/" + "000.jpg");
    try {
      URL aURL = new URL("http://example.com:80/docs/books/tutorial"
          + "/index.html?name=networking#DOWNLOADING");
      System.out.println(aURL.getPath());
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    File coverImageFile = new File(photoPath + "/" + "000.jpg");
    LOGGER.debug(coverImageFile.getAbsolutePath());
    if (!coverImageFile.exists()) {
      coverImageFile = new File(photoPath + "/" + "000.JPG");
    }
    return coverImageFile;

  }

  public ArrayList<File> getPhotoFileList() {
    if (this.getId() == -1) {
      return null;
    }
    String photoPath = "photo/" + String.valueOf(getId());

    File directory = new File(photoPath);
    File[] files = directory.listFiles(new JpgFileFilter());
    ArrayList<File> fileList = new ArrayList<File>();
    int i = 0;
    for (File f : files) {
      if (!f.getName().equalsIgnoreCase("000.jpg")
          && !f.getName().equalsIgnoreCase("000.jpeg")) {
        i++;
        LOGGER.debug("(" + i + ")" + "filename:" + f.getName());
        fileList.add(f);
      }
    }

    return fileList;
  }

  public BusReport() {
    // super();
  }

  public BusReport(int id) {
    super();
    this.id = id;
    String sql = "SELECT * FROM bus_report WHERE id = " + id;
    try (Connection connection = H2DbHelper.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {
      rs.next();
      this.setName(rs.getString("name"));
      this.setQuantity(rs.getInt("quantity"));
      this.setPrintMethod(rs.getInt("print_method"));
      this.setSalesName(rs.getString("sales_name"));
      this.setStartDate(rs.getString("start_date"));
      this.setEndDate(rs.getString("end_date"));
    } catch (SQLException e) {
    }
  }

  public void delete() throws SQLException {
    final String sql = "DELETE FROM bus_report WHERE id = ?";
    try (Connection connection = H2DbHelper.getConnection();
        PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setLong(1, id);
      pstmt.execute();
      id = -1;
    }
  }

  public void save() throws SQLException {
    try (Connection connection = H2DbHelper.getConnection()) {
      if (id == -1) {
        final String sql = " INSERT INTO bus_report (name, quantity, print_method, sales_name, start_date, end_date) VALUES(?, ?, ?, ?, ?, ?)";
        LOGGER.debug(sql);
        try (PreparedStatement pstmt = connection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {
          pstmt.setString(1, name);
          pstmt.setInt(2, quantity);
          pstmt.setInt(3, printMethod);
          pstmt.setString(4, salesName);
          pstmt.setString(5, startDate);
          pstmt.setString(6, endDate);

          // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          // try {
          // Date sDate = sdf.parse(startDate);
          // pstmt.setDate(5, new java.sql.Date(sDate.getTime()));
          // } catch (ParseException e) {
          // // TODO Auto-generated catch block
          // pstmt.setDate(5, null);
          // e.printStackTrace();
          // }
          //
          // Date eDate;
          // try {
          // eDate = sdf.parse(endDate);
          // pstmt.setDate(6, new java.sql.Date(eDate.getTime()));
          // } catch (ParseException e) {
          // pstmt.setDate(6, null);
          // // TODO Auto-generated catch block
          // e.printStackTrace();
          // }

          pstmt.execute();

          try (ResultSet rs = pstmt.getGeneratedKeys()) {
            rs.next();
            id = rs.getInt(1);
          }

          File f = new File("photo/" + String.valueOf(id));
          if (!f.exists()) {
            if (f.mkdir()) {
              LOGGER.debug("建立專案目錄成功");
            } else {
              LOGGER.debug("建立專案目錄失敗");
            }
          }
        }
      } else {
        final String sql = " UPDATE bus_report set name = ?, quantity = ?, print_method = ?, sales_name = ?, start_date = ?, end_date = ? WHERE id = ?";
        LOGGER.debug(sql);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
          pstmt.setString(1, name);
          pstmt.setInt(2, quantity);
          pstmt.setInt(3, printMethod);
          pstmt.setString(4, salesName);
          pstmt.setString(5, startDate);
          pstmt.setString(6, endDate);

          // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          // try {
          // Date sDate = sdf.parse(startDate);
          // pstmt.setDate(5, new java.sql.Date(sDate.getTime()));
          // } catch (ParseException e) {
          // // TODO Auto-generated catch block
          // pstmt.setDate(5, null);
          // e.printStackTrace();
          // }
          //
          // Date eDate;
          // try {
          // eDate = sdf.parse(endDate);
          // pstmt.setDate(6, new java.sql.Date(eDate.getTime()));
          // } catch (ParseException e) {
          // pstmt.setDate(6, null);
          // // TODO Auto-generated catch block
          // e.printStackTrace();
          // }

          LOGGER.debug("printMethod:" + String.valueOf(printMethod));
          pstmt.setLong(7, id);
          pstmt.execute();

        }
      }
    }
  }

  @Override
  public String toString() {
    final StringBuilder formatted = new StringBuilder();
    if (id == -1) {
      formatted.append("[No Id]");
    } else {
      formatted.append("[").append(id).append("]");
    }

    if (name == null) {
      formatted.append("no name");

    } else {
      formatted.append(name);
    }
    if (printMethod != 0) {
      formatted.append(name);
    } else {
      formatted.append("no printMethod");
    }

    return formatted.toString();
  }

  public PdfPTable getInfoTable() throws SQLException, DocumentException,
      IOException {
    PdfPTable table = new PdfPTable(new float[] { 1, 1, 1, 1, 1, 1 });

    BaseFont bfChinese = BaseFont.createFont("MSung-Light", "UniCNS-UCS2-H",
        BaseFont.NOT_EMBEDDED);
    // BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
    // BaseFont.NOT_EMBEDDED);
    // BaseFont bfChinese = BaseFont.createFont("MSung-Light","UniCNS-UCS2-H",
    // BaseFont.NOT_EMBEDDED);

    Font fontChinese16 = new Font(bfChinese, 16, Font.NORMAL);

    BaseColor colorTitle = new BaseColor(0, 229, 230);

    table.setWidthPercentage(100f);
    table.getDefaultCell().setPadding(3);
    table.getDefaultCell().setUseAscender(true);
    table.getDefaultCell().setUseDescender(true);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
    // table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    // table.addCell(day.toString());
    // table.getDefaultCell().setColspan(1);
    // table.getDefaultCell().setBackgroundColor(colorTitle);
    // table.addCell(new Phrase("委刊客戶", fontChinese16));
    // table.getDefaultCell().setColspan(2);
    // table.getDefaultCell().setBackgroundColor(null);
    // table.addCell(new Phrase("", fontChinese16));
    // table.getDefaultCell().setColspan(1);
    // table.getDefaultCell().setBackgroundColor(colorTitle);
    // table.addCell(new Phrase("統一編號", fontChinese16));
    // table.getDefaultCell().setColspan(2);
    // table.getDefaultCell().setBackgroundColor(null);
    // table.addCell(new Phrase("", fontChinese16));
    //
    // table.getDefaultCell().setColspan(1);
    // table.getDefaultCell().setBackgroundColor(colorTitle);
    // table.addCell(new Phrase("委刊地址", fontChinese16));
    // table.getDefaultCell().setColspan(2);
    // table.getDefaultCell().setBackgroundColor(null);
    // table.addCell(new Phrase("", fontChinese16));
    // table.getDefaultCell().setColspan(1);
    // table.getDefaultCell().setBackgroundColor(colorTitle);
    // table.addCell(new Phrase("電話", fontChinese16));
    // table.getDefaultCell().setColspan(2);
    // table.getDefaultCell().setBackgroundColor(null);
    // table.addCell(new Phrase("", fontChinese16));

    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(colorTitle);
    table.addCell(new Phrase("委刊內容", fontChinese16));
    table.getDefaultCell().setColspan(5);
    table.getDefaultCell().setBackgroundColor(null);
    table.addCell(new Phrase(this.getName(), fontChinese16));

    // table.getDefaultCell().setColspan(1);
    // table.getDefaultCell().setBackgroundColor(colorTitle);
    // table.addCell(new Phrase("廣告規格", fontChinese16));
    // table.getDefaultCell().setColspan(2);
    // table.getDefaultCell().setBackgroundColor(null);
    // table.addCell(new Phrase("", fontChinese16));
    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(colorTitle);
    table.addCell(new Phrase("承辦業務", fontChinese16));
    table.getDefaultCell().setColspan(2);
    table.getDefaultCell().setBackgroundColor(null);
    table.addCell(new Phrase(getSalesName(), fontChinese16));
    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(colorTitle);
    table.addCell(new Phrase("面數", fontChinese16));
    table.getDefaultCell().setColspan(2);
    table.getDefaultCell().setBackgroundColor(null);
    table.addCell(new Phrase(String.valueOf(getQuantity()), fontChinese16));

    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(colorTitle);
    table.addCell(new Phrase("委刊期間", fontChinese16));
    table.getDefaultCell().setColspan(5);
    table.getDefaultCell().setBackgroundColor(null);
    String period = "";
    if (!getStartDate().isEmpty() && !getEndDate().isEmpty()) {
      period = getStartDate() + " ~ " + getEndDate();
    }
    table.addCell(new Phrase(period, fontChinese16));

    table.getDefaultCell().setColspan(1);
    table.getDefaultCell().setBackgroundColor(colorTitle);
    table.addCell(new Phrase("附註", fontChinese16));
    table.getDefaultCell().setColspan(5);
    table.getDefaultCell().setBackgroundColor(null);
    table.addCell(new Phrase(this.getMemo(), fontChinese16));

    return table;
  }

  public PdfPTable getBusTable() throws SQLException, DocumentException,
      IOException {
    Pattern pattern = Pattern.compile("-");

    PdfPTable table = new PdfPTable(new float[] { 1.5f, 2, 2.5f, 1.5f, 2, 2.5f,
        1.5f, 2, 2.5f, 1.5f, 2, 2.5f });

    // BaseFont bfChinese = BaseFont.createFont("MSungStd-Light",
    // "UniCNS-UCS2-H",
    // BaseFont.NOT_EMBEDDED);
    // BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
    // BaseFont.NOT_EMBEDDED);
    BaseFont bfChinese = BaseFont.createFont("MSung-Light", "UniCNS-UCS2-H",
        BaseFont.NOT_EMBEDDED);
    // Font fontChinese16 = new Font(bfChinese, 16, Font.NORMAL);
    Font fontChinese12 = new Font(bfChinese, 12, Font.NORMAL);

    String pdfPath = "photo/" + String.valueOf(getId());

    ArrayList<File> photoFileList = getPhotoFileList();
    LOGGER.debug("photoFileList size: " + photoFileList.size());

    table.setWidthPercentage(100f);
    table.getDefaultCell().setPadding(3);
    table.getDefaultCell().setUseAscender(true);
    table.getDefaultCell().setUseDescender(true);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

    for (int i = 0; i < 4; i++) {
      table.addCell(new Phrase("編號", fontChinese12));
      table.addCell(new Phrase("路線", fontChinese12));
      table.addCell(new Phrase("車號", fontChinese12));
    }

    int serial = 0;
    Phrase busRoute, busNumber;
    for (int i = 0; i < 25; i++) {
      for (int j = 0; j < 4; j++) {
        serial = 25 * j + i + 1;
        table.addCell(String.valueOf(serial));
        // table.addCell(String.valueOf(25 * j + i + 1));
        if (photoFileList.size() >= serial) {
          // table.addCell(files[index].getName());
          // String[] result = pattern.split(files[serial].getName());
          String[] result = pattern.split(photoFileList.get(serial - 1)
              .getName());
          busRoute = new Phrase(result[0], fontChinese12);
          busNumber = new Phrase(result[1], fontChinese12);
          table.addCell(busRoute);
          table.addCell(busNumber);
          // table.addCell(result[0]);
          // table.addCell(result[1]);
        } else {
          table.addCell("");
          table.addCell("");
        }
      }
    }
    return table;
  }

  // public PdfPTable getPhotoTable() throws SQLException, DocumentException,
  // IOException {
  // return null;
  // }

  public File createPdfReport() throws DocumentException, IOException {
    String pdfPath = "photo/" + String.valueOf(getId());
    String pdfName = pdfPath + "/" + "上刊清冊" + "_" + getName() + ".pdf";
    File pdfFile = new File(pdfName);
    LOGGER.debug("pdfPath: " + pdfPath);
    LOGGER.debug("pdfName: " + pdfName);

    String filename = pdfFile.getAbsolutePath();

    Document document = new Document();
    // PdfWriter.getInstance(document, new FileOutputStream(filename));
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(
          filename));
    } catch (FileNotFoundException e) {
      LOGGER.debug("Error: " + e.getMessage());
    } catch (DocumentException e) {

      LOGGER.debug("Error: " + e.getMessage());
      e.printStackTrace();
    }
    document.open();

    BaseColor bgColor = new BaseColor(0, 229, 230);
    BaseFont bfChinese = BaseFont.createFont("MSung-Light", "UniCNS-UCS2-H",
        BaseFont.NOT_EMBEDDED);
    // Font fontChinese64 = new Font(bfChinese, 64, Font.NORMAL);
    Font fontChinese32 = new Font(bfChinese, 32, Font.BOLD);
    Font fontChinese16 = new Font(bfChinese, 16, Font.NORMAL);
    Font fontChinese12 = new Font(bfChinese, 12, Font.NORMAL);
    Font fontReportName = new Font(bfChinese, 32, Font.BOLD, new BaseColor(0,
        0, 0));
    Font fontReportType = new Font(bfChinese, 64, Font.BOLD, new BaseColor(255,
        0, 0));

    document.add(new Paragraph(" "));
    document.add(new Paragraph(" "));

    PdfPTable decoTable = new PdfPTable(new float[] { 1, 1 });
    decoTable.setWidthPercentage(100f);
    decoTable.getDefaultCell().setColspan(1);
    decoTable.getDefaultCell().setBackgroundColor(bgColor);
    decoTable.getDefaultCell().setFixedHeight(50);
    decoTable.getDefaultCell().setBorderColor(bgColor);
    decoTable.addCell(new Phrase("  "));
    decoTable.addCell(new Phrase(" "));
    document.add(decoTable);

    // byte gradient[] = new byte[256];
    // for (int i = 0; i < 256; i++)
    // gradient[i] = (byte) i;
    // com.itextpdf.text.Image img1 = com.itextpdf.text.Image.getInstance(256,
    // 1,
    // 1, 8, gradient);
    // // img1.scaleAbsolute(500, 50);
    // img1.setAlignment(Element.ALIGN_CENTER);
    // document.add(img1);
    //
    // PdfTemplate template = writer.getDirectContent().createTemplate(120, 80);
    // template.setColorFill(BaseColor.BLUE);
    // template.rectangle(0, 0, 500, 50);
    // template.fill();
    // writer.releaseTemplate(template);
    // Image img4 = Image.getInstance(template);
    // // img4.scaleAbsolute(500, 50);
    // img4.setAlignment(Element.ALIGN_CENTER);
    // // img4.scaleAbsolute(500, 50);
    // // img4.scaleToFit(500, 50);
    // document.add(img4);
    // // document.add(Image.getInstance(template) );
    //
    // PdfContentByte cb = writer.getDirectContent();
    // cb.saveState();
    // cb.setColorStroke(new BaseColor(255, 0, 0));
    // cb.setColorFill(new BaseColor(255, 0, 0));
    // cb.rectangle(20, 0, 500, 50);
    // // cb.stroke();
    // cb.fill();
    // cb.restoreState();

    Paragraph paragraph;
    paragraph = new Paragraph(this.getName(), fontReportName);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraph);

    paragraph = new Paragraph("上刊清冊", fontReportType);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraph);

    document.add(new Paragraph(" "));
    document.add(decoTable);

    document.add(new Paragraph(" "));
    document.add(new Paragraph(" "));

    // com.itextpdf.text.Image img2 = com.itextpdf.text.Image.getInstance(256,
    // 1,
    // 1, 8, new byte[256]);
    // img2.scaleAbsolute(500, 50);
    // img2.setAlignment(Element.ALIGN_CENTER);
    // document.add(img2);

    File coverImageFile = getCoverImageFile();
    if (coverImageFile.exists()) {
      com.itextpdf.text.Image coverImage = com.itextpdf.text.Image
          .getInstance(coverImageFile.getAbsolutePath());
      if (coverImage.isJpeg()) {
        // 這一行不一定ok，如果遇到 height >> width 的狀況，會超出頁面
        // coverImage.scaleAbsoluteWidth(400);
        coverImage.scaleToFit(400, 300);
        coverImage.setAlignment(Element.ALIGN_CENTER);
        document.add(coverImage);
      }
    }

    // File logoImageFile = new File("photo/1/powerful.png");
    // if(logoImageFile.exists()) {
    // com.itextpdf.text.Image logoImage = com.itextpdf.text.Image
    // .getInstance(logoImageFile.getAbsolutePath());
    // logoImage.setAbsolutePosition(
    // (PageSize.A4.getWidth() - logoImage.getScaledWidth()) / 2,
    // 0);
    // document.add(logoImage);
    // }
    
    
//    // 這樣寫不能運作
//    java.awt.Image logoImg = new ImageIcon(getClass().getResource("icons/powerful.png")).getImage();
//    com.itextpdf.text.Image logoImage = com.itextpdf.text.Image.getInstance(logoImg);

    
    document.newPage();
    paragraph = new Paragraph("車體廣告刊出完成報告", fontChinese32);
    paragraph.setAlignment(Element.ALIGN_CENTER);
    document.add(paragraph);
    document.add(new Paragraph(" "));
    try {
      // PdfPTable table;
      // table = this.getInfoTable();
      // PdfPTableEvent event = new AlternatingBackground();
      // table.setTableEvent(event);
      document.add(this.getInfoTable());
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // document.add(Chunk.NEWLINE); // 這個會空很大一段
    document.add(new Paragraph(" "));
    document.add(new Paragraph(" "));
    try {
      // PdfPTable table;
      // table = this.getBusTable();
      // PdfPTableEvent event = new AlternatingBackground();
      // table.setTableEvent(event);
      document.add(this.getBusTable());
    } catch (SQLException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    ArrayList<File> photoFileList = getPhotoFileList();

    // 公車照片的部份
    Pattern pattern = Pattern.compile("-");
    int busCount = 0;
    int pageCount = 0;

    switch (getPrintMethod()) {
    case 2:
      int totalPage = (int) Math.ceil((double) photoFileList.size() / 8);
      LOGGER.debug("totalPage:" + totalPage);

      for (int i = 0; i < totalPage; i++) {
        document.newPage();
        for (int j = 0; j < 4; j++) {
          PdfPTable photoTable = new PdfPTable(new float[] { 1, 1 });
          photoTable.setWidthPercentage(85f);
          photoTable.getDefaultCell().setColspan(1);
          photoTable.getDefaultCell().setBorderColor(
              new BaseColor(255, 255, 255));
          int leftIndex = i * 8 + j * 2;
          int rightIndex = i * 8 + j * 2 + 1;
          File leftFile;
          File rightFile;
          Image leftImage = null;
          Image rightImage = null;

          if (leftIndex < photoFileList.size()) {
            busCount++;
            leftFile = photoFileList.get(leftIndex);
            leftImage = Image.getInstance(String.format(leftFile
                .getAbsolutePath()));
            String[] result = pattern.split(leftFile.getName());
            paragraph = new Paragraph("編號： " + String.valueOf(busCount)
                + "     路線： " + result[0] + "     車號： " + result[1],
                fontChinese12);
            photoTable.addCell(paragraph);

          }

          if (rightIndex < photoFileList.size()) {
            busCount++;
            rightFile = photoFileList.get(rightIndex);
            rightImage = Image.getInstance(String.format(rightFile
                .getAbsolutePath()));
            String[] result = pattern.split(rightFile.getName());
            paragraph = new Paragraph("編號： " + String.valueOf(busCount)
                + "     路線： " + result[0] + "     車號： " + result[1],
                fontChinese12);
            photoTable.addCell(paragraph);
          }

          // 在 table 中，scaleToFit 好像沒用，他會自動調整圖片至欄寬
          // leftImage.scaleToFit(80, 60);
          // rightImage.scaleToFit(80, 60);

          photoTable.addCell(leftImage);
          photoTable.addCell(rightImage);

          document.add(photoTable);
        }
      }

      break;
    case 1:
    default:
      document.newPage();
      for (File f : photoFileList) {
        pageCount++;
        busCount++;
        document.add(new Paragraph(" "));
        Image photoImage = Image
            .getInstance(String.format(f.getAbsolutePath()));
        String[] result = pattern.split(f.getName());
        paragraph = new Paragraph("編號： " + String.valueOf(busCount)
            + "     路線： " + result[0] + "     車號： " + result[1], fontChinese16);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        photoImage.scaleToFit(500, 300);
        photoImage.setAlignment(Element.ALIGN_CENTER);
        document.add(photoImage);
        document.add(new Paragraph(" "));
        if (pageCount % 2 == 0)
          document.newPage();

      }
      break;
    }

    LOGGER.debug(String.valueOf(PageSize.A4.getWidth()));
    LOGGER.debug(String.valueOf(PageSize.A4.getHeight()));
    // step 5
    document.close();

    return pdfFile;
  }

  class JpgFileFilter implements FileFilter {
    public boolean accept(File f) {
      if (f.getName().endsWith(".jpg"))
        return true;
      if (f.getName().endsWith(".jpeg"))
        return true;
      if (f.getName().endsWith(".JPG"))
        return true;
      if (f.getName().endsWith(".JPEG"))
        return true;
      return false;
    }
  }
}
