package dialogue.model;

@FunctionalInterface
public interface DialogueCondition {
    boolean evaluate(DialogueContext context);
}