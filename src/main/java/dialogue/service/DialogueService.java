package dialogue.service;

import dialogue.model.DialogueAction;
import dialogue.model.DialogueContext;
import dialogue.model.DialogueModel;
import dialogue.model.DialogueOption;

import java.util.*;

public class DialogueService {
    private final Map<String, DialogueModel> dialogueMap;
    private DialogueModel currentDialogue;
    private final DialogueContext context;
    private final List<DialogueObserver> observers;

    private static DialogueService instance;
    public static DialogueService getInstance() {
        if (instance == null) throw new IllegalStateException("DialogueService not initialized!");
        return instance;
    }
    public static void init(List<DialogueModel> dialogues, DialogueContext context) {
        instance = new DialogueService(dialogues, context);
    }

    public DialogueService(List<DialogueModel> dialogues, DialogueContext context) {
        this.dialogueMap = new HashMap<>();
        this.context = context;
        this.observers = new ArrayList<>();

        for (DialogueModel d : dialogues) {
            dialogueMap.put(d.getId(), d);
        }
    }

    public void addObserver(DialogueObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DialogueObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (DialogueObserver observer : observers) {
            observer.onDialogueUpdated(this);
        }
    }

    public void startDialogue(String dialogueId) {
        DialogueModel dialogue = dialogueMap.get(dialogueId);
        if (dialogue != null && dialogue.isAvailable(context)) {
            currentDialogue = dialogue;
            notifyObservers();
        }
    }

    public void chooseOption(int index) {
        if (currentDialogue == null) return;

        List<DialogueOption> options = getCurrentOptions();
        if (index < 0 || index >= options.size()) return;

        DialogueOption selected = options.get(index);
        if (selected.getActionInstances() != null) {
            for (DialogueAction act : selected.getActionInstances()) {
                act.execute(context);
            }
        }

        String nextDialogueId = selected.getNextDialogueId();
        if (nextDialogueId == null || nextDialogueId.isEmpty()) {
            currentDialogue = null;
        } else {
            DialogueModel next = dialogueMap.get(nextDialogueId);
            currentDialogue = (next != null && next.isAvailable(context)) ? next : null;
        }

        notifyObservers();
    }

    public DialogueModel getCurrentDialogue() {
        return currentDialogue;
    }

    public List<DialogueOption> getCurrentOptions() {
        if (currentDialogue == null) return Collections.emptyList();
        return currentDialogue.getOptions().stream()
                .filter(option -> option.isAvailable(context))
                .toList();
    }

    public boolean isFinished() {
        return currentDialogue == null;
    }

    public DialogueContext getContext() {
        return context;
    }
}