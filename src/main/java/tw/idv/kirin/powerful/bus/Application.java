package tw.idv.kirin.powerful.bus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

public class Application extends JFrame {

  private static final long serialVersionUID = -5930893585901522072L;
  private static final Logger LOGGER = LoggerFactory
      .getLogger(Application.class);

  private boolean ALLOW_COLUMN_SELECTION = false;
  private boolean ALLOW_ROW_SELECTION = true;

  private JTextField idTextField;
  private JTextField nameTextField;
  private JTextField quantityTextField;
  private JTextField salesNameTextField;
  private JRadioButton printMethod01RadioButton;
  private JRadioButton printMethod02RadioButton;
  private ButtonGroup printMethodButtonGroup;
  private JTextField startDateTextField;
  private JTextField endDateTextField;
  private JTextArea memoTextArea;

  private static final String printMethodOptions[] = { "每頁2張", "每頁8張" };

  private Action refreshAction;
  private Action newAction;
  private Action saveAction;
  private Action deleteAction;

  private Action openDirectoryAction;
  private Action openPdfAction;

  private DefaultTableModel busReportTableModel;
  private JTable busReportTable;
  private Vector busReportTableData;

  private BusReport selectedBusReport;

  public Application() {
    try {
      java.awt.Image img = ImageIO.read(getClass().getResource(
          "/icons/Program.png"));
      this.setIconImage(img);
    } catch (IOException e) {
      e.printStackTrace();
    }

    LOGGER.debug("this.getSize():" + getContentPane().getSize());
    initActions();
    initComponents();
    refreshBusReport();
    LOGGER.debug("FRAME height:" + String.valueOf(this.getBounds().height));
    LOGGER.debug("this.getSize():" + getContentPane().getSize());

  }

  private void refreshBusReport() {
    busReportTableModel.setRowCount(0);
    try {
      List<BusReport> activeBusReports = BusReportHelper.getInstance()
          .getLast50BusReports();
      for (BusReport report : activeBusReports) {
        busReportTableModel.addRow(new Object[] {
            String.valueOf(report.getId()), report.getName(),
            report.getSalesName(), String.valueOf(report.getQuantity()),
            report.getStartDate(), report.getEndDate() });

      }

      // 讓預設的結案報告為最新的報告，如果沒資抖，就設一個新的，以方便後續作業
      if (activeBusReports.size() > 0 && selectedBusReport == null) {
        setSelectedBusReport(activeBusReports.get(0));
      } else if (activeBusReports.size() == 0) {
        setSelectedBusReport(new BusReport());
      }
      // invoiceTableModel.fireTableChanged(null);

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  private void newBusReport() {
    BusReport report = new BusReport();
    setSelectedBusReport(report);

  }

  private void saveBusReport() {
    if (selectedBusReport != null) {
      selectedBusReport.setName(nameTextField.getText());
      if (quantityTextField.getText().isEmpty()) {
        selectedBusReport.setQuantity(0);
      } else {
        selectedBusReport.setQuantity(Integer.parseInt(quantityTextField
            .getText()));
      }
      if (printMethod01RadioButton.isSelected()) {
        selectedBusReport.setPrintMethod(1);
      }
      if (printMethod02RadioButton.isSelected()) {
        selectedBusReport.setPrintMethod(2);
      }
      selectedBusReport.setSalesName(salesNameTextField.getText());
      selectedBusReport.setStartDate(startDateTextField.getText());
      selectedBusReport.setEndDate(endDateTextField.getText());
    }

    try {
      selectedBusReport.save();
      idTextField.setText(String.valueOf(selectedBusReport.getId()));
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(this,
          "Failed to save selected item" + e.getMessage(), "Save",
          JOptionPane.WARNING_MESSAGE);
    } finally {
      refreshBusReport();
    }
  }

  private void deleteBusReport() {
    if (selectedBusReport != null) {
      if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this,
          "確定要刪除『" + selectedBusReport.getName() + "』?", "確認刪除",
          JOptionPane.YES_NO_OPTION)) {
        try {
          selectedBusReport.delete();
        } catch (final SQLException e) {
          JOptionPane.showMessageDialog(this,
              "無法刪除此結案報告", "Delete",
              JOptionPane.WARNING_MESSAGE);
        } finally {
          setSelectedBusReport(null);
          refreshBusReport();
        }
      }
    }
  }

  private ImageIcon load(final String name) {
    return new ImageIcon(getClass().getResource("/icons/" + name + ".png"));
  }

  private void initActions() {
    refreshAction = new AbstractAction("Refresh", load("Refresh")) {
      private static final long serialVersionUID = 7573537222039055715L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        refreshBusReport();
      }
    };

    newAction = new AbstractAction("New", load("New")) {
      private static final long serialVersionUID = 39402394060879678L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        newBusReport();
      }
    };

    saveAction = new AbstractAction("Save", load("Save")) {
      private static final long serialVersionUID = 3151744204386109789L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        saveBusReport();
      }
    };

    deleteAction = new AbstractAction("Delete", load("Delete")) {
      private static final long serialVersionUID = -3865627438398974682L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        deleteBusReport();
      }
    };

    openDirectoryAction = new AbstractAction("OpenDirectory", load("Folder")) {
      private static final long serialVersionUID = -9109476232953806637L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        openDirectory();
      }
    };

    openPdfAction = new AbstractAction("OpenPdf", load("Pdf")) {
      private static final long serialVersionUID = 1968187892504465547L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        try {
          openPdf();
        } catch (IOException | DocumentException e1) {
          e1.printStackTrace();
        }
      }
    };
  }

  private void initComponents() {
    // 最簡單的方式，但是中間的區域會有被遮蔽的問題
    // add(createToolBar(), BorderLayout.BEFORE_FIRST_LINE);
    // add(createEditor(), BorderLayout.CENTER);
    // add(createTablePane(), BorderLayout.AFTER_LAST_LINE);

    // 原因可能是因為 JScrollPane 佔太多空間，把它的空間指定好，程式就ok了
    add(createToolBar(), BorderLayout.BEFORE_FIRST_LINE);
    JPanel editorPanel = createEditor();
    // editorPanel.setPreferredSize(new Dimension(640, 150));
    add(editorPanel, BorderLayout.CENTER);
    JScrollPane tablePane = createTablePane();
    LOGGER.debug("frame width: " + this.getBounds().height);
    tablePane.setPreferredSize(new Dimension(640, 400));
    add(tablePane, BorderLayout.SOUTH);

  }

  private JComponent createToolBar() {
    final JToolBar toolBar = new JToolBar();
    // toolBar.add(refreshAction);
    // toolBar.addSeparator();
    toolBar.add(newAction);
    toolBar.add(saveAction);
    toolBar.addSeparator();
    toolBar.add(deleteAction);
    toolBar.addSeparator();
    toolBar.add(openDirectoryAction);
    toolBar.add(openPdfAction);

    return toolBar;
  }

  private JPanel createEditor() {
    final JPanel panel = new JPanel(new GridBagLayout());
    // panel.setPreferredSize(getMaximumSize());
    GridBagConstraints constraints;

    // Id
    constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("Id"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.gridwidth = 5;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    // constraints.anchor = GridBagConstraints.
    idTextField = new JTextField();
    idTextField.setEditable(false);
    panel.add(idTextField, constraints);

    // Name
    constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.ipadx = 30;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("名稱"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 1;
    constraints.gridwidth = 5;
    constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    nameTextField = new JTextField("");
    panel.add(nameTextField, constraints);

    // PrintMethod
    constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("列印方式："), constraints);

    printMethodButtonGroup = new ButtonGroup();
    Action action1 = new AbstractAction("每頁2張") {
      /**
       * 
       */
      private static final long serialVersionUID = 918638442223168289L;

      public void actionPerformed(ActionEvent evt) {
        if (selectedBusReport != null) {
          selectedBusReport.setPrintMethod(1);
        }
      }
    };
    Action action2 = new AbstractAction("每頁8張") {
      /**
       * 
       */
      private static final long serialVersionUID = 6809975727163142792L;

      public void actionPerformed(ActionEvent evt) {
        if (selectedBusReport != null) {
          selectedBusReport.setPrintMethod(2);
        }
      }
    };

    printMethod01RadioButton = new JRadioButton(action1);
    printMethod02RadioButton = new JRadioButton(action2);
    // JPanel radioPanel = new JPanel(new GridLayout(0, 1));
    JPanel radioPanel = new JPanel();
    radioPanel.setLayout(new FlowLayout());

    radioPanel.add(printMethod01RadioButton);
    radioPanel.add(printMethod02RadioButton);
    printMethodButtonGroup.add(printMethod01RadioButton);
    printMethodButtonGroup.add(printMethod02RadioButton);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 2;
    constraints.gridwidth = 5;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    // constraints.fill = GridBagConstraints.BOTH; // 設定這個值會使元件置中
    constraints.anchor = GridBagConstraints.WEST;
    // printMethodTextField = new JTextField();
    // panel.add(printMethodTextField, constraints);
    panel.add(radioPanel, constraints);

    // quantity
    constraints = new GridBagConstraints();
    constraints.gridy = 3;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("面數"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 3;
    constraints.gridwidth = 5;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    quantityTextField = new JTextField();
    panel.add(quantityTextField, constraints);

    // salesName
    constraints = new GridBagConstraints();
    constraints.gridy = 4;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("承辦業務"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 4;
    constraints.gridwidth = 5;
    constraints.weightx = 1;
    constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    salesNameTextField = new JTextField();
    panel.add(salesNameTextField, constraints);

    // 委刊期間(開始)
    constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 5;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("委刊期間(開始)"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 5;
    constraints.gridwidth = 5;
    constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    startDateTextField = new JTextField("");
    panel.add(startDateTextField, constraints);

    // 委刊期間(結束)
    constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 6;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.WEST;
    // constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("委刊期間(結束)"), constraints);

    constraints.gridx = 1;
    constraints.gridy = 6;
    constraints.gridwidth = 5;
    constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.BOTH;
    endDateTextField = new JTextField("");
    panel.add(endDateTextField, constraints);

    // memo
    constraints = new GridBagConstraints();
    constraints.gridy = 7;
    constraints.gridwidth = 1;
    // constraints.weightx = 1;
    // constraints.weighty = 1;
    constraints.anchor = GridBagConstraints.WEST;
    constraints.insets = new Insets(2, 2, 2, 2);
    panel.add(new JLabel("備註"), constraints);

    constraints = new GridBagConstraints();
    constraints.gridx = 1;
    constraints.gridy = 7;
    constraints.gridwidth = 5;
    constraints.weightx = 1;
    constraints.weighty = 1;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.fill = GridBagConstraints.BOTH;
    // salesNameTextField = new JTextField();
    // panel.add(salesNameTextField, constraints);
    memoTextArea = new JTextArea();
    panel.add(memoTextArea, constraints);

    return panel;
  }

  private JScrollPane createTablePane() {
    busReportTableModel = new DefaultTableModel() {
      private static final long serialVersionUID = -2014863772137825207L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;// This causes all cells to be not editable
      }
    };
    busReportTable = new JTable(busReportTableModel);

    busReportTableModel.addColumn("id");
    busReportTableModel.addColumn("委刊內容");
    busReportTableModel.addColumn("承辦業務");
    busReportTableModel.addColumn("面數");
    busReportTableModel.addColumn("委刊開始");
    busReportTableModel.addColumn("委刊結束");

    busReportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    if (ALLOW_ROW_SELECTION) { // true by default
      ListSelectionModel rowSM = busReportTable.getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          // Ignore extra messages.
          if (e.getValueIsAdjusting())
            return;

          ListSelectionModel lsm = (ListSelectionModel) e.getSource();
          if (lsm.isSelectionEmpty()) {
            System.out.println("No rows are selected.");
          } else {
            int selectedRow = lsm.getMinSelectionIndex();
            System.out.println("Row " + selectedRow + " is now selected.");
            String bus_report_id = (String) busReportTable.getValueAt(
                selectedRow, 0);
            System.out.println("bus_report_id " + bus_report_id
                + " is now selected.");
            int brid = Integer.parseInt((String) busReportTable.getValueAt(
                selectedRow, 0));
            setSelectedBusReport(new BusReport(brid));

          }
        }
      });
    } else {
      busReportTable.setRowSelectionAllowed(false);
    }

    // 設定第一欄寬度
    busReportTable.getColumnModel().getColumn(0).setPreferredWidth(5);

    return new JScrollPane(busReportTable);
  }

  private void setSelectedBusReport(BusReport report) {
    if (report == null) {
      idTextField.setText("");
      nameTextField.setText("");
      quantityTextField.setText("");
      salesNameTextField.setText("");
      startDateTextField.setText("");
      endDateTextField.setText("");
      memoTextArea.setText("");
      this.selectedBusReport = null;
    } else {

      if (report.getId() > 0) {
        idTextField.setText(String.valueOf(report.getId()));
      } else {
        idTextField.setText("未存檔新資料");
      }
      nameTextField.setText(report.getName());

      if (report.getQuantity() == 0) {
        quantityTextField.setText("");
      } else {
        quantityTextField.setText(String.valueOf(report.getQuantity()));
      }
      memoTextArea.setText(report.getMemo());

      salesNameTextField.setText(report.getSalesName());

      startDateTextField.setText(report.getStartDate());
      endDateTextField.setText(report.getEndDate());

      if (report.getPrintMethod() == 1) {
        printMethod01RadioButton.setSelected(true);
      } else if (report.getPrintMethod() == 2) {
        printMethod02RadioButton.setSelected(true);
      } else {
        printMethod01RadioButton.setSelected(true);
      }

      this.selectedBusReport = report;
      // refreshBusReport();
    }
  }

  private void openDirectory() {
    if (selectedBusReport == null) {
      JOptionPane.showMessageDialog(this, "尚未選擇報表，請選擇資料", "錯誤訊息",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (selectedBusReport.getId() == -1) {
      JOptionPane.showMessageDialog(this, "尚未存檔，請儲存資料", "錯誤訊息",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    saveBusReport(); // 開啟目錄前先存檔

    File f = new File("photo/" + String.valueOf(selectedBusReport.getId()));
    LOGGER.debug(f.getAbsolutePath());

    if (!f.exists()) {
      if (f.mkdir()) {
        LOGGER.debug("建立專案目錄成功");
      } else {
        LOGGER.debug("建立專案目錄失敗");
      }
    }
    String[] cmd = new String[2];
    cmd[0] = "explorer.exe";
    cmd[1] = f.getAbsolutePath();
    Runtime rt = Runtime.getRuntime();
    try {
      rt.exec(cmd);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openPdf() throws IOException, DocumentException {

    if (selectedBusReport == null) {
      JOptionPane.showMessageDialog(this, "尚未選擇報表，請選擇資料", "錯誤訊息",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (selectedBusReport.getId() == -1) {
      JOptionPane.showMessageDialog(this, "尚未存檔，請儲存資料", "錯誤訊息",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (selectedBusReport.getPhotoFileList().size() == 0) {
      JOptionPane.showMessageDialog(this, "尚無公車照片，請先行上傳照片檔", "錯誤訊息",
          JOptionPane.ERROR_MESSAGE);
      openDirectory();
      return;
    }

    saveBusReport(); // 產生 pdf 前，先行存檔

    // String[] cmd = new String[2];
    // cmd[0] = "rundll32 url.dll,FileProtocolHandler ";
    // cmd[1] = f.getAbsolutePath();
    String cmd = "rundll32 url.dll,FileProtocolHandler "
        + selectedBusReport.createPdfReport().getAbsolutePath();
    Runtime rt = Runtime.getRuntime();
    try {
      rt.exec(cmd);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
