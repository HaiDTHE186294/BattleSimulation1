package dialogue.model;

@FunctionalInterface
public interface DialogueAction {
    void execute(DialogueContext context);
}