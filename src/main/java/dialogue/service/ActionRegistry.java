package dialogue.service;

import dialogue.model.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ActionRegistry {
    private static final Map<String, Function<ActionDef, DialogueAction>> registry = new HashMap<>();

    static {
        registry.put("startBattle", def -> new StartBattleAction(def.getBattleId()));
        // registry.put("giveItem", def -> new GiveItemAction(def.getItemId(), def.getAmount()));
        // Thêm action khác nếu muốn
    }

    public static DialogueAction createAction(ActionDef def) {
        Function<ActionDef, DialogueAction> factory = registry.get(def.getType());
        return (factory != null) ? factory.apply(def) : null;
    }
}