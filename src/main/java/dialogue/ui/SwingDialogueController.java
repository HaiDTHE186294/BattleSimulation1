package dialogue.ui;

import dialogue.controller.DialogueController;
import dialogue.service.DialogueService;

public class SwingDialogueController extends DialogueController {
    public SwingDialogueController(DialogueService service) {
        super(service);
    }

    @Override
    public void onUserInput(Object input) {
        if (input instanceof Integer idx) {
            chooseOption(idx);
        }
    }
}