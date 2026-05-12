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
        DocumentWorkflowState state = resolveStateFromStep(draft.currentStep);
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

    public void updateStep1(String applicantName, String applicantEmail, String applicantPhone, String applicantNotificationChannels) {
        builder.applicantInfo(applicantName, applicantEmail, applicantPhone, applicantNotificationChannels);
    }

    public void updateStep2(String officerName, String officerEmail, String officerPhone,
                            String officerNotificationChannels, String documentType,
                            String filePath, String fileExtension, long fileSizeKB) {
        builder.officerInfo(officerName, officerEmail, officerPhone, officerNotificationChannels)
                .attachment(documentType, filePath, fileExtension, fileSizeKB);
    }

    public void updateStep3(String digitalSignature) {
        builder.digitalSignature(digitalSignature);
    }

    public void nextStep() {
        state.next(this);
    }

    public void previousStep() {
        if (state.getStep() <= 1) {
            return;
        }
        state = resolveStateFromStep(state.getStep() - 1);
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

    private static DocumentWorkflowState resolveStateFromStep(int step) {
        if (step <= 1) {
            return new Step1PersonalInfoState();
        }
        if (step == 2) {
            return new Step2AttachmentState();
        }
        return new Step3ConfirmationState();
    }
}
