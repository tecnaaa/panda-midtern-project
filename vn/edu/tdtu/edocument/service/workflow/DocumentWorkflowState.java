package vn.edu.tdtu.edocument.service.workflow;

public interface DocumentWorkflowState {
    int getStep();

    void next(DocumentWorkflowContext context);

    void submit(DocumentWorkflowContext context);
}
