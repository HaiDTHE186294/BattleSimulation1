package dialogue.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dialogue.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogueLoader {

    /**
     * Load danh sách DialogueModel từ file JSON resource.
     * Đồng thời ánh xạ các ActionDef thành DialogueAction instance.
     */
    public static List<DialogueModel> loadFromJson(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = DialogueLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) throw new RuntimeException("Không tìm thấy file: " + resourceName);

        List<DialogueModel> models = mapper.readValue(is, new TypeReference<List<DialogueModel>>() {});
        mapActionsForAllOptions(models);
        return models;
    }

    /**
     * Ánh xạ các ActionDef thành DialogueAction cho toàn bộ các option trong các DialogueModel.
     */
    private static void mapActionsForAllOptions(List<DialogueModel> models) {
        for (DialogueModel model : models) {
            mapActionsForOptions(model.getOptions());
        }
    }

    /**
     * Ánh xạ các ActionDef thành DialogueAction cho từng option.
     */
    private static void mapActionsForOptions(List<DialogueOption> options) {
        if (options == null) return;
        for (DialogueOption option : options) {
            List<DialogueAction> actionInstances = mapActionDefsToActions(option.getActions());
            option.setActionInstances(actionInstances);
        }
    }

    /**
     * Chuyển đổi danh sách ActionDef thành DialogueAction.
     */
    private static List<DialogueAction> mapActionDefsToActions(List<ActionDef> defs) {
        List<DialogueAction> result = new ArrayList<>();
        if (defs == null) return result;
        for (ActionDef def : defs) {
            DialogueAction a = ActionRegistry.createAction(def);
            if (a != null) result.add(a);
        }
        return result;
    }
}