package dialogue.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dialogue.model.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogueLoader {
    public static List<DialogueModel> loadFromJson(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = DialogueLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) throw new RuntimeException("Không tìm thấy file: " + resourceName);
        List<DialogueModel> models = mapper.readValue(is, new TypeReference<List<DialogueModel>>() {});

        // Ánh xạ ActionDef → DialogueAction
        for (DialogueModel model : models) {
            if (model.getOptions() != null) {
                for (DialogueOption option : model.getOptions()) {
                    List<DialogueAction> actionInstances = new ArrayList<>();
                    if (option.getActions() != null) {
                        for (ActionDef def : option.getActions()) {
                            DialogueAction a = ActionRegistry.createAction(def);
                            if (a != null) actionInstances.add(a);
                        }
                    }
                    option.setActionInstances(actionInstances);
                }
            }
        }
        return models;
    }
}