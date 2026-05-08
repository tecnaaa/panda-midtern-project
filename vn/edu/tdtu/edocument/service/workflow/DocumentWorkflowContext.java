package vn.edu.tdtu.edocument.service.workflow;

import vn.edu.tdtu.edocument.model.Document;
import vn.edu.tdtu.edocument.service.DocumentProcessor;

public class DocumentWorkflowContext {
    private final Document.Builder builder;
    private DocumentWorkflowState state;

    private DocumentWorkflowContext(Document.Builder builder, DocumentWorkflowState state) {
        this.builder = builder;
        this.state = state;
    }

    public static DocumentWorkflowContext newDraft() {
        Document.Builder builder = Document.builder()
                .status("LUU_NHAP")
                .currentStep(1);
        return new DocumentWorkflowContext(builder, new Step1PersonalInfoState());
    }

    public static DocumentWorkflowContext fromDraft(Document draft) {
        Document.Builder builder = draft.toBuilder();
        int step = draft.currentStep;
        DocumentWorkflowState state;

        if (step <= 1) {
            state = new Step1PersonalInfoState();
        } else if (step == 2) {
            state = new Step2AttachmentState();
        } else {
            state = new Step3ConfirmationState();
        }

        return new DocumentWorkflowContext(builder, state);
    }

    public Document.Builder getBuilder() {
        return builder;
    }

    void setState(DocumentWorkflowState state) {
        this.state = state;
    }

    public int getCurrentStep() {
        return state.getStep();
    }

    public void updateStep1(String applicantName, String applicantEmail, String applicantPhone) {
        builder.applicantInfo(applicantName, applicantEmail, applicantPhone);
    }

    public void updateStep2(String officerName, String officerEmail, String officerPhone,
                            String documentType, String filePath, String fileExtension, long fileSizeKB) {
        builder.officerInfo(officerName, officerEmail, officerPhone)
                .attachment(documentType, filePath, fileExtension, fileSizeKB);
    }

    public void updateStep3(String digitalSignature) {
        builder.digitalSignature(digitalSignature);
    }

    public void nextStep() {
        state.next(this);
    }

    public void previousStep() {
        int step = state.getStep();
        if (step <= 1) {
            return;
        }
        if (step == 3) {
            state = new Step2AttachmentState();
        } else {
            state = new Step1PersonalInfoState();
        }
    }

    public Document saveDraft(DocumentProcessor processor) {
        Document draft = builder.status("LUU_NHAP")
                .currentStep(state.getStep())
                .build();
        processor.saveDraft(draft);
        return draft;
    }

    public Document submit(DocumentProcessor processor) {
        state.submit(this);
        Document submission = builder.status("MOI_TAO")
                .currentStep(3)
                .build();
        processor.process(submission);
        return submission;
    }
}
