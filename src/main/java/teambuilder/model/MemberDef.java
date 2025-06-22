package teambuilder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDef {
    public String name;
    public int hp;
    public String status;
    public int atk;
    public int def;
    public List<Map<String, Object>> equipment;

    @Override
    public String toString() {
        return name + " (HP:" + hp + ", ATK:" + atk + ", DEF:" + def + ")";
    }
}