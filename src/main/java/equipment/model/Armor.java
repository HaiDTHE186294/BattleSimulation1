package equipment.model;

import effect.model.Effect;
import person.model.Person;

import java.util.ArrayList;
import java.util.List;

public class Armor extends AbstractEquipment {

    private String armorType; // Ví dụ: "shield", "helmet", "chestplate"
    private List<Effect> effects = new ArrayList<>();

    public Armor(String name, String armorType, int defPower) {
        super(name, 0, defPower); // Chỉ tăng DEF
        this.armorType = armorType;
    }

    public String getArmorType() {
        return armorType;
    }

    public void setArmorType(String armorType) {
        this.armorType = armorType;
    }

    public int getDefPower() {
        return def;
    }

    public void setDefPower(int defPower) {
        this.def = defPower;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    /**
     * Kích hoạt hiệu ứng của giáp khi bị tấn công hoặc phòng thủ (tùy theo design).
     */
    @Override
    public void action(Person user) {
        System.out.println(getName() + " kích hoạt hiệu ứng bảo hộ cho " + user.getName());

        for (Effect effect : effects) {
            effect.apply(user); // Có thể là hồi máu, tăng kháng, tạo lá chắn...
        }
    }

    @Override
    public int getBonusAtk() {
        return 0;
    }

    @Override
    public int getBonusDef() {
        return def;
    }


}
