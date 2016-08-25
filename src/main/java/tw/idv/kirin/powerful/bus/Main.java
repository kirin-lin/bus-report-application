package tw.idv.kirin.powerful.bus;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    H2DbHelper.getInstance().init();
    H2DbHelper.getInstance().registerShutdownHook();

    File photoDir = new File("photo");
    LOGGER.debug("檢查存放照片的資料夾: " + photoDir.toString());
    if (!photoDir.exists()) {
      if (photoDir.mkdir()) {
        System.out.println("建立成功");
      } else {
        System.out.println("!!建立失敗");
      }
    } else {
      LOGGER.debug("存放照片的資料夾已存在");
    }



    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        LOGGER.debug("Starting Application");
        Application app = new Application();
        app.setTitle("公車結案報表");
        //app.setSize(800, 600);
        app.setSize(1000, 750);
        
        app.setLocationRelativeTo(null);
        app.setDefaultCloseOperation(Application.EXIT_ON_CLOSE);
        app.setVisible(true);
        LOGGER.debug("frame size in main:" + app.getBounds().getSize() );

      }

    });
  }

}
