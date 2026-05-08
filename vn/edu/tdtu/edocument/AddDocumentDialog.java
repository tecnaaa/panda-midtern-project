package vn.edu.tdtu.edocument;

import vn.edu.tdtu.edocument.model.Document;
import vn.edu.tdtu.edocument.service.DocumentProcessor;
import vn.edu.tdtu.edocument.service.workflow.DocumentWorkflowContext;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AddDocumentDialog extends JDialog {
    private JTextField txtApplicantName, txtApplicantEmail, txtApplicantPhone;
    private JTextField txtOfficerName, txtOfficerEmail, txtOfficerPhone;
    private JComboBox<String> cbDocumentType;
    private JTextField txtDigitalSignature;
    private JLabel lblFileName;
    private File selectedFile;

    private final JLabel lblStepTitle;
    private final CardLayout stepLayout;
    private final JPanel stepContainer;
    private final JButton btnBack;
    private final JButton btnNext;
    private final JButton btnSaveDraft;
    private final JButton btnSubmit;

    private final DocumentProcessor processor;
    private final MainSwingUI parent;
    private final DocumentWorkflowContext workflowContext;

    public AddDocumentDialog(MainSwingUI parent, DocumentProcessor processor) {
        super(parent, "Tiếp nhận hồ sơ mới", true);
        this.parent = parent;
        this.processor = processor;
        this.workflowContext = DocumentWorkflowContext.newDraft();

        setSize(520, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        lblStepTitle = new JLabel("Bước 1: Thông tin cá nhân");
        lblStepTitle.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        add(lblStepTitle, BorderLayout.NORTH);

        stepLayout = new CardLayout();
        stepContainer = new JPanel(stepLayout);
        stepContainer.add(buildStep1Panel(), "1");
        stepContainer.add(buildStep2Panel(), "2");
        stepContainer.add(buildStep3Panel(), "3");
        add(stepContainer, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Quay lại");
        btnNext = new JButton("Tiếp tục");
        btnSaveDraft = new JButton("Lưu nháp");
        btnSubmit = new JButton("Nộp hồ sơ");
        JButton btnCancel = new JButton("Hủy");

        btnPanel.add(btnBack);
        btnPanel.add(btnNext);
        btnPanel.add(btnSaveDraft);
        btnPanel.add(btnSubmit);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> goBack());
        btnNext.addActionListener(e -> goNext());
        btnSaveDraft.addActionListener(e -> saveDraft());
        btnSubmit.addActionListener(e -> submitAction());
        btnCancel.addActionListener(e -> dispose());

        updateStepUI();
    }

    private JPanel buildStep1Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = createFormPanel();

        txtApplicantName = createTextField();
        txtApplicantEmail = createTextField();
        txtApplicantPhone = createTextField();

        addFormRow(form, 0, "Tên người nộp:", txtApplicantName);
        addFormRow(form, 1, "Email người nộp:", txtApplicantEmail);
        addFormRow(form, 2, "SĐT người nộp:", txtApplicantPhone);

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel buildStep2Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = createFormPanel();

        txtOfficerName = createTextField();
        txtOfficerName.setText("Cán bộ trực ban");
        txtOfficerEmail = createTextField();
        txtOfficerEmail.setText("officer@tdtu.edu.vn");
        txtOfficerPhone = createTextField();
        txtOfficerPhone.setText("0123456789");

        addFormRow(form, 0, "Tên cán bộ:", txtOfficerName);
        addFormRow(form, 1, "Email cán bộ:", txtOfficerEmail);
        addFormRow(form, 2, "SĐT cán bộ:", txtOfficerPhone);

        cbDocumentType = new JComboBox<>(new String[]{"DON_XIN_PHEP", "BAO_CAO", "HO_SO_THUE"});
        cbDocumentType.setPreferredSize(new Dimension(240, 28));
        addFormRow(form, 3, "Loại hồ sơ:", cbDocumentType);

        JButton btnFile = new JButton("Chọn...");
        lblFileName = new JLabel("Chưa chọn");
        JPanel pFile = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pFile.add(btnFile);
        pFile.add(Box.createHorizontalStrut(8));
        pFile.add(lblFileName);
        addFormRow(form, 4, "Tập tin đính kèm:", pFile);

        panel.add(form, BorderLayout.NORTH);

        btnFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                lblFileName.setText(selectedFile.getName());
            }
        });

        return panel;
    }

    private JPanel buildStep3Panel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = createFormPanel();

        txtDigitalSignature = createTextField();
        addFormRow(form, 0, "Chữ ký số:", txtDigitalSignature);

        JTextArea hintArea = new JTextArea(
                "Bước xác nhận: hệ thống sẽ kiểm tra dữ liệu, lưu trữ và chuyển trạng thái khi nộp hồ sơ.");
        hintArea.setEditable(false);
        hintArea.setLineWrap(true);
        hintArea.setWrapStyleWord(true);
        hintArea.setBackground(form.getBackground());
        hintArea.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout());
        content.add(form, BorderLayout.NORTH);
        content.add(hintArea, BorderLayout.CENTER);

        panel.add(content, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return form;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(240, 28));
        return field;
    }

    private void addFormRow(JPanel form, int row, String labelText, JComponent field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.insets = new Insets(0, 0, 10, 12);
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.fill = GridBagConstraints.NONE;
        labelConstraints.weightx = 0;

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.insets = new Insets(0, 0, 10, 0);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1;

        form.add(new JLabel(labelText), labelConstraints);
        form.add(field, fieldConstraints);
    }

    private void goBack() {
        int currentStep = workflowContext.getCurrentStep();
        if (currentStep <= 1) {
            return;
        }
        workflowContext.previousStep();
        updateStepUI();
    }

    private void goNext() {
        try {
            syncCurrentStepData();
            workflowContext.nextStep();
            updateStepUI();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveDraft() {
        try {
            syncCurrentStepData();
            Document draft = workflowContext.saveDraft(processor);
            parent.addDocumentToList(draft);
            JOptionPane.showMessageDialog(this,
                    "Đã lưu nháp hồ sơ " + draft.id + " tại bước " + draft.currentStep,
                    "Lưu nháp thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể lưu nháp: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStepUI() {
        int step = workflowContext.getCurrentStep();
        stepLayout.show(stepContainer, String.valueOf(step));
        lblStepTitle.setText("Bước " + step + ": " + getStepLabel(step));

        btnBack.setEnabled(step > 1);
        btnNext.setVisible(step < 3);
        btnSubmit.setVisible(step == 3);
    }

    private String getStepLabel(int step) {
        if (step == 1) {
            return "Thông tin cá nhân";
        }
        if (step == 2) {
            return "Tải tài liệu";
        }
        return "Xác nhận và nộp";
    }

    private void syncCurrentStepData() {
        int step = workflowContext.getCurrentStep();
        if (step == 1) {
            workflowContext.updateStep1(
                    txtApplicantName.getText().trim(),
                    txtApplicantEmail.getText().trim(),
                    txtApplicantPhone.getText().trim()
            );
            return;
        }

        if (step == 2) {
            String filePath = "";
            String ext = "";
            long size = 0;

            if (selectedFile != null) {
                filePath = selectedFile.getAbsolutePath();
                size = selectedFile.length() / 1024;
                String name = selectedFile.getName();
                int lastDot = name.lastIndexOf('.');
                if (lastDot > 0) {
                    ext = name.substring(lastDot + 1);
                }
            } else {
                Document.Builder existingBuilder = workflowContext.getBuilder();
                filePath = existingBuilder.getFilePath() == null ? "" : existingBuilder.getFilePath();
                ext = existingBuilder.getFileExtension() == null ? "" : existingBuilder.getFileExtension();
                size = existingBuilder.getFileSizeKB();
            }

            workflowContext.updateStep2(
                    txtOfficerName.getText().trim(),
                    txtOfficerEmail.getText().trim(),
                    txtOfficerPhone.getText().trim(),
                    cbDocumentType.getSelectedItem().toString(),
                    filePath,
                    ext,
                    size
            );
            return;
        }

        workflowContext.updateStep3(txtDigitalSignature.getText().trim());
    }

    private void submitAction() {
        try {
            syncCurrentStepData();
            Document doc = workflowContext.submit(processor);
            if ("DANG_XET_DUYET".equals(doc.status)) {
                parent.addDocumentToList(doc);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hồ sơ không hợp lệ. Vui lòng kiểm tra lại log.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
        }
    }
}