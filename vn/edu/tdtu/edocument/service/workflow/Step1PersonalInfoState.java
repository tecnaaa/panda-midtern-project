package vn.edu.tdtu.edocument.service.workflow;

import vn.edu.tdtu.edocument.model.Document;

public class Step1PersonalInfoState extends AbstractWorkflowState {
    @Override
    public int getStep() {
        return 1;
    }

    @Override
    public void next(DocumentWorkflowContext context) {
        Document.Builder builder = context.getBuilder();
        if (!hasText(builder.getApplicantName()) || !hasText(builder.getApplicantEmail()) || !hasText(builder.getApplicantPhone())) {
            throw new IllegalStateException("Buoc 1 chua day du thong tin ca nhan.");
        }
        context.setState(new Step2AttachmentState());
    }
}
