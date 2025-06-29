package equipment.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import effect.model.BuffEffect;
import effect.model.BurnEffect;
import effect.model.HealEffect;
import effect.model.Effect;
import effect.service.EffectService;
import equipment.model.Armor;
import equipment.model.IComponent;
import equipment.model.LuckyStone;
import equipment.model.Weapon;

import java.io.InputStream;
import java.util.*;

/**
 * Loader for equipment and effects from resource definitions.
 */
public class EquipmentLoader {
    private static EffectService effectService;

    public static void setEffectService(EffectService service) {
        effectService = service;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EffectDef {
        public String type;
        public String name;
        public Integer duration;
        public Integer bonusAtk;
        public Integer bonusDef;
        public Integer damagePerTurn;
        public Integer healQuantity;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquipmentDef {
        public String type;
        public String id;
        public String name;
        public String displayName;
        public Integer power;     // For Weapon
        public Integer defense;   // For Armor
        public Map<String, Object> wrapped; // For LuckyStone wrapping another equipment
        public List<EffectDef> effects;     // List of effects
    }

    /**
     * Main method to load equipment map from a resource file.
     * @param resourceName Name of the resource file in classpath.
     * @return Map of equipment, keyed by id (or name if id is null)
     * @throws Exception if resource not found or parsing fails.
     */
    public static Map<String, IComponent> loadEquipmentMap(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = EquipmentLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) throw new RuntimeException("Resource not found: " + resourceName);

        List<EquipmentDef> defs = mapper.readValue(is, new TypeReference<List<EquipmentDef>>() {});
        Map<String, IComponent> equipmentMap = new HashMap<>();

        // First, parse all non-LuckyStone equipment
        for (EquipmentDef def : defs) {
            if (!"LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent component = parseComponent(def, mapper, equipmentMap);
                if (component != null) {
                    equipmentMap.put(def.id != null ? def.id : def.name, component);
                }
            }
        }
        // Then, parse LuckyStone (may wrap others)
        for (EquipmentDef def : defs) {
            if ("LuckyStone".equalsIgnoreCase(def.type)) {
                IComponent component = parseComponent(def, mapper, equipmentMap);
                if (component != null) {
                    equipmentMap.put(def.id != null ? def.id : def.name, component);
                }
            }
        }
        return equipmentMap;
    }

    /**
     * Parse a single equipment component from definition.
     * Recursively parses wrapped LuckyStone if present.
     */
    private static IComponent parseComponent(EquipmentDef def, ObjectMapper mapper, Map<String, IComponent> equipmentMap) {
        List<Effect> effects = parseEffects(def.effects);

        switch (def.type != null ? def.type : "") {
            case "Weapon":
            case "weapon":
                Weapon weapon = new Weapon(
                        def.displayName,
                        def.id,
                        def.power != null ? def.power : 0,
                        effectService
                );
                weapon.setEffects(effects);
                return weapon;

            case "Armor":
            case "armor":
                Armor armor = new Armor(
                        def.name,
                        def.displayName,
                        def.defense != null ? def.defense : 0
                );
                armor.setEffects(effects);
                return armor;

            case "LuckyStone":
            case "luckystone":
                if (def.wrapped != null) {
                    IComponent wrapped = resolveWrappedComponent(def.wrapped, mapper, equipmentMap);
                    if (wrapped != null) {
                        LuckyStone luckyStone = new LuckyStone(wrapped, effectService);
                        luckyStone.setEffects(effects);
                        return luckyStone;
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * Parse list of effects from effect definitions.
     */
    private static List<Effect> parseEffects(List<EffectDef> defs) {
        if (defs == null || defs.isEmpty()) return Collections.emptyList();
        List<Effect> effects = new ArrayList<>();
        for (EffectDef edef : defs) {
            Effect effect = parseEffect(edef);
            if (effect != null) {
                effects.add(effect.copyEffect());
                System.out.println(effect.toString());
            }
        }
        return effects;
    }

    /**
     * Parse a single effect from its definition.
     */
    private static Effect parseEffect(EffectDef def) {
        if (def == null || def.type == null) return null;
        switch (def.type) {
            case "BurnEffect":
                return new BurnEffect(
                        def.duration != null ? def.duration : 1,
                        def.damagePerTurn != null ? def.damagePerTurn : 1
                );
            case "BuffEffect":
                return new BuffEffect(
                        def.name != null ? def.name : "Buff",
                        def.duration != null ? def.duration : 1,
                        def.bonusAtk != null ? def.bonusAtk : 0,
                        def.bonusDef != null ? def.bonusDef : 0
                );
            case "HealEffect":
                return new HealEffect(
                        def.duration != null ? def.duration : 1,
                        def.healQuantity != null ? def.healQuantity : 1
                );
            default:
                return null;
        }
    }

    /**
     * Resolve the wrapped component for a LuckyStone, either from map or by parsing recursively.
     */
    private static IComponent resolveWrappedComponent(Map<String, Object> wrappedDef, ObjectMapper mapper, Map<String, IComponent> equipmentMap) {
        String wrappedId = wrappedDef.get("id") != null ? wrappedDef.get("id").toString() : null;
        if (wrappedId != null && equipmentMap.containsKey(wrappedId)) {
            return equipmentMap.get(wrappedId);
        }
        String wrappedName = wrappedDef.get("name") != null ? wrappedDef.get("name").toString() : null;
        if (wrappedName != null && equipmentMap.containsKey(wrappedName)) {
            return equipmentMap.get(wrappedName);
        }
        // Fallback: parse manually from definition
        EquipmentDef nestedDef = mapper.convertValue(wrappedDef, EquipmentDef.class);
        return parseComponent(nestedDef, mapper, equipmentMap);
    }
}