package dialogue.model;

import java.util.ArrayList;
import java.util.List;

public class DialogueModel {
    private String id;
    private String speaker;
    private String text;
    private List<DialogueOption> options;
    private List<DialogueCondition> conditions;
    private List<DialogueAction> actions;

    public DialogueModel() {
        this.options = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public DialogueModel(String id, String speaker, String text,
                         List<DialogueOption> options,
                         List<DialogueCondition> conditions,
                         List<DialogueAction> actions) {
        this.id = id;
        this.speaker = speaker;
        this.text = text;
        this.options = options != null ? options : new ArrayList<>();
        this.conditions = conditions != null ? conditions : new ArrayList<>();
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSpeaker() { return speaker; }
    public void setSpeaker(String speaker) { this.speaker = speaker; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<DialogueOption> getOptions() { return options; }
    public void setOptions(List<DialogueOption> options) {
        this.options = options != null ? options : new ArrayList<>();
    }

    public List<DialogueCondition> getConditions() { return conditions; }
    public void setConditions(List<DialogueCondition> conditions) {
        this.conditions = conditions != null ? conditions : new ArrayList<>();
    }

    public List<DialogueAction> getActions() { return actions; }
    public void setActions(List<DialogueAction> actions) {
        this.actions = actions != null ? actions : new ArrayList<>();
    }

    public boolean isAvailable(DialogueContext context) {
        if (conditions == null || conditions.isEmpty()) return true;
        return conditions.stream().allMatch(cond -> cond.evaluate(context));
    }
}