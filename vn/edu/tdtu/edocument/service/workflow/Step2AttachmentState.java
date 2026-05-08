package vn.edu.tdtu.edocument.service.workflow;

import vn.edu.tdtu.edocument.model.Document;

public class Step2AttachmentState extends AbstractWorkflowState {
    @Override
    public int getStep() {
        return 2;
    }

    @Override
    public void next(DocumentWorkflowContext context) {
        Document.Builder builder = context.getBuilder();
        if (!hasText(builder.getOfficerName()) || !hasText(builder.getOfficerEmail()) || !hasText(builder.getOfficerPhone())) {
            throw new IllegalStateException("Buoc 2 chua day du thong tin can bo tiep nhan.");
        }

        if (!hasText(builder.getDocumentType()) || !hasText(builder.getFilePath()) || !hasText(builder.getFileExtension())) {
            throw new IllegalStateException("Buoc 2 chua day du thong tin tai lieu dinh kem.");
        }

        context.setState(new Step3ConfirmationState());
    }
}
