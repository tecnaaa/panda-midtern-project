package vn.edu.tdtu.edocument;

import vn.edu.tdtu.edocument.model.Document;
import vn.edu.tdtu.edocument.service.DocumentProcessor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class MainSwingUI extends JFrame {
    private JTextArea consoleArea;
    private JTable documentTable;
    private DefaultTableModel tableModel;
    private DocumentProcessor processor;
    private List<Document> documentList;

    public MainSwingUI() {
        processor = new DocumentProcessor();
        documentList = new ArrayList<>();

        setTitle("Hệ thống Quản lý Hồ sơ Điện tử - v2.0");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Mã hồ sơ", "Người nộp", "Loại hồ sơ", "Trạng thái", "Bước", "Tập tin"};
        tableModel = new DefaultTableModel(columnNames, 0);
        documentTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(documentTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hồ sơ hệ thống"));

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(30, 30, 30));
        consoleArea.setForeground(Color.GREEN);
        consoleArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane logScrollPane = new JScrollPane(consoleArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Hệ thống Log/Thông báo"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, logScrollPane);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm mới hồ sơ");
        JButton btnResumeDraft = new JButton("Tiếp tục hồ sơ nháp");
        JButton btnClear = new JButton("Xóa Log");
        toolBar.add(btnAdd);
        toolBar.add(btnResumeDraft);
        toolBar.add(btnClear);
        add(toolBar, BorderLayout.NORTH);

        redirectSystemStreams();
        loadExistingDocuments();
        refreshTable();

        btnAdd.addActionListener(e -> {
            AddDocumentDialog dialog = new AddDocumentDialog(this, processor);
            dialog.setVisible(true);
            refreshTable();
        });

        btnClear.addActionListener(e -> consoleArea.setText(""));
        btnResumeDraft.addActionListener(e -> resumeSelectedDraft());

        documentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && documentTable.getSelectedRow() != -1) {
                int selectedRow = documentTable.getSelectedRow();
                String docId = tableModel.getValueAt(selectedRow, 0).toString();
                
                for (Document doc : documentList) {
                    if (doc.id.equals(docId)) {
                        System.out.println("\n--- CHI TIẾT HỒ SƠ: " + doc.id + " ---");
                        System.out.println("Người nộp: " + doc.applicantName + " | Email: " + doc.applicantEmail + " | SĐT: " + doc.applicantPhone);
                        System.out.println("Kênh TB người nộp: " + doc.applicantNotificationChannels);
                        System.out.println("Cán bộ tiếp nhận: " + doc.officerName + " | Email: " + doc.officerEmail + " | SĐT: " + doc.officerPhone);
                        System.out.println("Kênh TB cán bộ: " + doc.officerNotificationChannels);
                        System.out.println("Loại hồ sơ: " + doc.documentType);
                        System.out.println("Đường dẫn tệp: " + doc.filePath + " (" + doc.fileSizeKB + " KB)");
                        System.out.println("Chữ ký số: " + doc.digitalSignature);
                        System.out.println("Trạng thái hiện tại: " + doc.status);
                        System.out.println("----------------------------------------\n");
                        break;
                    }
                }
            }
        });
    }

    private void loadExistingDocuments() {
        documentList.clear();
        documentList.addAll(processor.loadDocuments());
    }

    public void addDocumentToList(Document doc) {
        for (int i = 0; i < documentList.size(); i++) {
            if (documentList.get(i).id.equals(doc.id)) {
                documentList.set(i, doc);
                refreshTable();
                return;
            }
        }
        documentList.add(doc);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Document doc : documentList) {
            tableModel.addRow(new Object[]{
                doc.id, doc.applicantName, doc.documentType, doc.status, doc.currentStep, doc.fileExtension
            });
        }
    }

    private void resumeSelectedDraft() {
        int selectedRow = documentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hồ sơ nháp để tiếp tục.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String docId = tableModel.getValueAt(selectedRow, 0).toString();
        Document selectedDocument = findDocumentById(docId);
        if (selectedDocument == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hồ sơ đã chọn.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!"LUU_NHAP".equals(selectedDocument.status)) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể tiếp tục hồ sơ ở trạng thái LUU_NHAP.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddDocumentDialog dialog = new AddDocumentDialog(this, processor, selectedDocument);
        dialog.setVisible(true);
        refreshTable();
    }

    private Document findDocumentById(String id) {
        for (Document doc : documentList) {
            if (doc.id.equals(id)) {
                return doc;
            }
        }
        return null;
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override public void write(int b) { updateTextArea(String.valueOf((char) b)); }
            @Override public void write(byte[] b, int off, int len) { updateTextArea(new String(b, off, len)); }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(text);
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainSwingUI().setVisible(true));
    }
}