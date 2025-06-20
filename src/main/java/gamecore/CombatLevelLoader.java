package gamecore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import equipment.model.IComponent;
import equipment.model.Weapon;
import person.model.PersonStatus;
import person.model.Soldier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CombatLevelLoader {

    public static class EnemyTeamDef {
        public String id;
        public String name;
        public List<EnemyDef> members;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EnemyDef {
        public String name;
        public int hp;
        public String status;
        public int atk;
        public int def;
        public List<EquipmentDef> equipment;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EquipmentDef {
        public String type; // "Weapon", v.v.
        public String name;
        public String displayName;
        public int power;
        // Có thể mở rộng thêm LuckyStone, Armor...
    }

    /** Load toàn bộ danh sách các team địch từ file json */
    public static List<EnemyTeamDef> loadEnemyTeams(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = CombatLevelLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) throw new RuntimeException("Không tìm thấy file: " + resourceName);
        return mapper.readValue(is, new TypeReference<List<EnemyTeamDef>>() {});
    }

    /** Lấy danh sách Soldier của 1 team địch từ EnemyTeamDef */
    public static List<Soldier> toSoldierList(EnemyTeamDef def) {
        List<Soldier> soldiers = new ArrayList<>();
        if (def == null || def.members == null) return soldiers;
        for (EnemyDef e : def.members) {
            Soldier s = new Soldier(
                    e.name,
                    e.hp,
                    PersonStatus.valueOf(e.status),
                    e.atk,
                    e.def
            );
            if (e.equipment != null) {
                for (EquipmentDef eq : e.equipment) {
                    IComponent comp = null;
                    if ("Weapon".equals(eq.type)) {
                        comp = new Weapon(eq.name, eq.displayName, eq.power);
                    }
                    // Có thể xử lý thêm các loại khác ở đây
                    if (comp != null) s.equip(comp);
                }
            }
            soldiers.add(s);
        }
        return soldiers;
    }

    /** Lấy 1 team địch theo id từ list */
    public static EnemyTeamDef findTeamById(List<EnemyTeamDef> teams, String teamId) {
        for (EnemyTeamDef t : teams) {
            if (t.id.equals(teamId)) return t;
        }
        return null;
    }
}