package equipment.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import effect.model.BuffEffect;
import effect.model.BurnEffect;
import effect.model.Effect;
import equipment.model.Armor;
import equipment.model.IComponent;
import equipment.model.LuckyStone;
import equipment.model.Weapon;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentLoader {

    public static class EffectDef {
        public String type;
        public String name;
        public Integer duration;
        public Integer bonusAtk;
        public Integer bonusDef;
        public Integer damagePerTurn;
        // add more effect params here if needed
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquipmentDef {
        public String type;
        public String id;
        public String name;
        public String displayName;
        public Integer power;     // for Weapon
        public Integer defense;   // for Armor
        public Map<String, Object> wrapped; // for LuckyStone bọc trang bị khác
        public List<EffectDef> effects;     // List effect
    }

    /**
     * Parse một equipment object từ EquipmentDef.
     * Nếu là LuckyStone thì parse đệ quy trường "wrapped".
     * Gán effect nếu có.
     */
    private static IComponent parseComponent(EquipmentDef def, ObjectMapper mapper, Map<String, IComponent> equipmentMap) {
        List<Effect> effects = new ArrayList<>();
        if (def.effects != null) {
            for (EffectDef edef : def.effects) {
                Effect e = parseEffect(edef);
                if (e != null) effects.add(e);
            }
        }

        if ("Weapon".equalsIgnoreCase(def.type)) {
            Weapon weapon = new Weapon(def.name, def.displayName, def.power == null ? 0 : def.power);
            weapon.setEffects(effects);
            return weapon;
        }
        if ("Armor".equalsIgnoreCase(def.type)) {
            Armor armor = new Armor(def.name, def.displayName, def.defense == null ? 0 : def.defense);
            armor.setEffects(effects);
            return armor;
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
                    LuckyStone luckyStone = new LuckyStone(wrapped);
                    // LuckyStone có thể có hiệu ứng riêng
                    luckyStone.setEffects(effects);
                    return luckyStone;
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
        // Parse không phải LuckyStone trước
        for (EquipmentDef def : defs) {
            if (!"LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent c = parseComponent(def, mapper, map);
                if (c != null) map.put(def.id != null ? def.id : def.name, c);
            }
        }
        // Parse LuckyStone sau (có thể bọc các trang bị khác)
        for (EquipmentDef def : defs) {
            if ("LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent c = parseComponent(def, mapper, map);
                if (c != null) map.put(def.id != null ? def.id : def.name, c);
            }
        }
        return map;
    }

    private static Effect parseEffect(EffectDef def) {
        if ("BurnEffect".equalsIgnoreCase(def.type)) {
            return new BurnEffect(
                    def.duration != null ? def.duration : 1,
                    def.damagePerTurn != null ? def.damagePerTurn : 1
            );
        }
        if ("BuffEffect".equalsIgnoreCase(def.type)) {
            return new BuffEffect(
                    def.name != null ? def.name : "Buff",
                    def.duration != null ? def.duration : 1,
                    def.bonusAtk != null ? def.bonusAtk : 0,
                    def.bonusDef != null ? def.bonusDef : 0
            );
        }
        // Thêm các loại effect khác ở đây nếu có
        return null;
    }
}