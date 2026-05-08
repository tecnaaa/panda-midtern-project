package vn.edu.tdtu.edocument.service.workflow;

import vn.edu.tdtu.edocument.model.Document;

public class Step3ConfirmationState extends AbstractWorkflowState {
    @Override
    public int getStep() {
        return 3;
    }

    @Override
    public void next(DocumentWorkflowContext context) {
        throw new IllegalStateException("Ban dang o buoc cuoi. Hay nop ho so.");
    }

    @Override
    public void submit(DocumentWorkflowContext context) {
        Document.Builder builder = context.getBuilder();
        if (!hasText(builder.getDigitalSignature())) {
            throw new IllegalStateException("Buoc 3 can chu ky so truoc khi nop ho so.");
        }
    }
}
