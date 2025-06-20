package equipment.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import equipment.model.Armor;
import equipment.model.IComponent;
import equipment.model.LuckyStone;
import equipment.model.Weapon;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentLoader {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquipmentDef {
        public String type;
        public String id;
        public String name;
        public String displayName;
        public Integer power;     // for Weapon
        public Integer defense;   // for Armor
        public Map<String, Object> wrapped; // for LuckyStone bọc trang bị khác
    }

    /**
     * Parse một equipment object từ EquipmentDef.
     * Nếu là LuckyStone thì parse đệ quy trường "wrapped".
     */
    private static IComponent parseComponent(EquipmentDef def, ObjectMapper mapper, Map<String, IComponent> equipmentMap) {
        if ("Weapon".equalsIgnoreCase(def.type)) {
            return new Weapon(def.name, def.displayName, def.power == null ? 0 : def.power);
        }
        if ("Armor".equalsIgnoreCase(def.type)) {
            return new Armor(def.name, def.displayName, def.defense == null ? 0 : def.defense);
        }
        if ("LuckyStone".equalsIgnoreCase(def.type)) {
            if (def.wrapped != null) {
                // Ưu tiên lấy wrapped theo id hoặc name từ equipmentMap nếu có
                String wrappedId = def.wrapped.get("id") != null ? def.wrapped.get("id").toString() : null;
                IComponent wrapped = null;
                if (wrappedId != null && equipmentMap != null && equipmentMap.containsKey(wrappedId)) {
                    wrapped = equipmentMap.get(wrappedId);
                } else if (def.wrapped.get("name") != null && equipmentMap != null && equipmentMap.containsKey(def.wrapped.get("name").toString())) {
                    wrapped = equipmentMap.get(def.wrapped.get("name").toString());
                } else {
                    // fallback: parse thủ công
                    EquipmentDef wrappedDef = mapper.convertValue(def.wrapped, EquipmentDef.class);
                    wrapped = parseComponent(wrappedDef, mapper, equipmentMap);
                }
                if (wrapped != null) {
                    return new LuckyStone(wrapped);
                }
            }
        }
        return null;
    }

    public static Map<String, IComponent> loadEquipmentMap(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = EquipmentLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) throw new RuntimeException("Không tìm thấy file: " + resourceName);
        List<EquipmentDef> defs = mapper.readValue(is, new TypeReference<List<EquipmentDef>>() {});
        Map<String, IComponent> map = new HashMap<>();
        // 1. Parse tất cả non-LuckyStone
        for (EquipmentDef def : defs) {
            if (!"LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent c = parseComponent(def, mapper, map);
                if (c != null) map.put(def.id != null ? def.id : def.name, c);
            }
        }
        // 2. Parse LuckyStone (cần wrapped đã có trong map)
        for (EquipmentDef def : defs) {
            if ("LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent c = parseComponent(def, mapper, map);
                if (c != null) map.put(def.id != null ? def.id : def.name, c);
            }
        }
        return map;
    }
}