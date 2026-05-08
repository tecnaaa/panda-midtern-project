package vn.edu.tdtu.edocument.service.workflow;

abstract class AbstractWorkflowState implements DocumentWorkflowState {

    protected boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public void submit(DocumentWorkflowContext context) {
        throw new IllegalStateException("Ban can hoan tat cac buoc truoc khi nop ho so.");
    }
}
