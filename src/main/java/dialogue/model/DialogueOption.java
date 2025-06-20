package dialogue.model;

import java.util.List;

public class DialogueOption {
    private String text;
    private String nextDialogueId;
    private List<ActionDef> actions; // Để Jackson parse từ JSON
    private transient List<DialogueAction> actionInstances; // Chỉ dùng ở runtime
    private List<DialogueCondition> conditions;

    // Getters/setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getNextDialogueId() { return nextDialogueId; }
    public void setNextDialogueId(String nextDialogueId) { this.nextDialogueId = nextDialogueId; }

    public List<ActionDef> getActions() { return actions; }
    public void setActions(List<ActionDef> actions) { this.actions = actions; }

    public List<DialogueAction> getActionInstances() { return actionInstances; }
    public void setActionInstances(List<DialogueAction> actionInstances) { this.actionInstances = actionInstances; }

    // Hàm kiểm tra option có khả dụng không
    public boolean isAvailable(DialogueContext context) {
        if (conditions == null || conditions.isEmpty()) return true;
        return conditions.stream().allMatch(cond -> cond.evaluate(context));
    }

}