package dialogue.controller;

import dialogue.service.DialogueObserver;
import dialogue.service.DialogueService;
import utils.BgmPlayer;

public abstract class DialogueController {
    protected final DialogueService service;

    public DialogueController(DialogueService service) {
        this.service = service;
    }

    public void start(String dialogueId) {
        playBgmForDialogue(dialogueId);
        service.startDialogue(dialogueId);
    }
//Test triển khai phương thức playBgmForDialogue
    private void playBgmForDialogue(String dialogueId) {
        String bgmPath;
        switch (dialogueId) {
            case "troll_soloWin":
                bgmPath = "sad.wav";
                break;
            default:
                bgmPath = "prepareBattle.wav";
                break;
        }
        BgmPlayer bgmPlayer = BgmPlayer.getInstance();
        bgmPlayer.play(bgmPath, true);
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