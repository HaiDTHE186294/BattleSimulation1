package dialogue.controller;

import dialogue.service.DialogueObserver;
import dialogue.service.DialogueService;

public abstract class DialogueController {
    protected final DialogueService service;

    public DialogueController(DialogueService service) {
        this.service = service;
    }

    public void start(String dialogueId) {
        service.startDialogue(dialogueId);
    }

    public void chooseOption(int index) {
        service.chooseOption(index);
    }

    public void addObserver(DialogueObserver observer) {
        service.addObserver(observer);
    }

    public void removeObserver(DialogueObserver observer) {
        service.removeObserver(observer);
    }

    public abstract void onUserInput(Object input);
}