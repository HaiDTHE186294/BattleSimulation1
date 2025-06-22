package dialogue.model;

import gamecore.GameController;
import ui.MainFrame;

public class StartBattleAction implements DialogueAction {

    private final String battleId;

    public StartBattleAction(String battleId) {
        this.battleId = battleId;
    }

    @Override
    public void execute(DialogueContext context) {
        context.setData("battleRequested", true);
        context.setData("battleId", battleId);
        GameController controller = new GameController(battleId);
        controller.startGame();
    }
}