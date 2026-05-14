package vn.edu.tdtu.edocument.service.workflow;

import vn.edu.tdtu.edocument.model.Document;

public class Step1PersonalInfoState extends AbstractWorkflowState {
    @Override
    public int getStep() {
        return 1;
    }

    private static final java.util.regex.Pattern EMAIL_PATTERN =
            java.util.regex.Pattern.compile("^[\\w.+\\-]+@[\\w\\-]+\\.[\\w.]+$");
    private static final java.util.regex.Pattern PHONE_PATTERN =
            java.util.regex.Pattern.compile("^\\d{1,10}$");

    @Override
    public void next(DocumentWorkflowContext context) {
        Document.Builder builder = context.getBuilder();
        if (!hasText(builder.getApplicantName()) || !hasText(builder.getApplicantEmail()) || !hasText(builder.getApplicantPhone())) {
            throw new IllegalStateException("Buoc 1 chua day du thong tin ca nhan.");
        }
        if (!EMAIL_PATTERN.matcher(builder.getApplicantEmail().trim()).matches()) {
            throw new IllegalStateException("Email khong dung dinh dang (vi du: user@example.com).");
        }
        if (!PHONE_PATTERN.matcher(builder.getApplicantPhone().trim()).matches()) {
            throw new IllegalStateException("So dien thoai chi duoc chua so (0-9) va toi da 10 chu so.");
        }
        context.setState(new Step2AttachmentState());
    }
}
