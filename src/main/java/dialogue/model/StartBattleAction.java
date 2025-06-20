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
        // Có thể gọi service thực sự ở đây nếu muốn
        GameController controller = new GameController();
        new MainFrame(controller);
    }
}