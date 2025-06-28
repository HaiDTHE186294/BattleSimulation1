package equipment.model;
import effect.model.Effect;
import effect.service.EffectService;
import gamecore.GameContext;
import person.model.Person;
import person.model.Soldier;

import java.util.ArrayList;
import java.util.List;

public class Weapon extends AbstractEquipment {

    private String type; // Loại vũ khí, ví dụ: "sword", "bow", "axe"
    private List<Effect> effects;
    private EffectService effectService;

    public Weapon(String name, String type, int atk, EffectService effectService) {
        super(name, atk, 0); // Vũ khí chỉ tăng sức tấn công
        this.type = type;
        this.effectService = effectService;
        this.effects = new ArrayList<>();
        System.out.println("Created weapon: " + name + " (type: " + type + ") with ATK=" + atk);
    }

    public Weapon(String name, String type, int atk) {
        this(name, type, atk, null);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAtkPower() {
        return atk;
    }

    public void setAtkPower(int atkPower) {
        this.atk = atkPower;
    }

    public List<Effect> getEffects() {
        return new ArrayList<>(effects); // Return a copy to prevent external modification
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects != null ? new ArrayList<>(effects) : new ArrayList<>();
        System.out.println("Set " + this.effects.size() + " effects for weapon " + getName());
        for (Effect e : this.effects) {
            System.out.println(" - " + e.getName() + " (duration: " + e.getDuration() + ")");
        }
    }

    @Override
    public void action(Person target) {
        System.out.println("Weapon " + getName() + " performing action on " + target.getName());

        if (effects == null || effects.isEmpty()) {
            System.out.println("Weapon " + getName() + " has no effects.");
        } else {
            System.out.println("Weapon " + getName() + " has " + effects.size() + " effects: ");
            for (Effect effect : effects) {
                System.out.println(" - " + effect.getName() + " (duration: " + effect.getDuration() + ")");
            }
        }

        int damage = this.atk * 4;
        target.takeDamage(damage);
        System.out.println(getName() + " attacked " + target.getName() + " with " + damage);

    }

    @Override
    public int getBonusAtk() {
        return atk;
    }

    @Override
    public int getBonusDef() {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s (ATK+%d)", this.getName(), this.getAtkPower()));
        if (effects != null && !effects.isEmpty()) {
            sb.append(" [Effects: ");
            for (Effect e : effects) {
                sb.append(e.getName()).append("(").append(e.getDuration()).append(") ");
            }
            sb.append("]");
        }
        return sb.toString();
    }
}
