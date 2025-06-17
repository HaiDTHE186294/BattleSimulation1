package equipment.model;
import effect.model.Effect;
import person.model.Person;
import person.model.Soldier;

import java.util.ArrayList;
import java.util.List;

public class Weapon extends AbstractEquipment {

    private String type; // Loại vũ khí, ví dụ: "sword", "bow", "axe"
    private List<Effect> effects = new ArrayList<>();


    public Weapon(String name, String type, int atk) {
        super(name, atk, 0); // Vũ khí chỉ tăng sức tấn công
        this.type = type;
        this.effects = new ArrayList<>();
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
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    @Override
    public void action(Person target) {
        int damage = this.atk * 10;
        target.takeDamage(damage);
        System.out.println(getName() + " đã tấn công " + target.getName() + " với sức mạnh " + damage);
    }

    @Override
    public int getBonusAtk() {
        return atk;
    }

    @Override
    public int getBonusDef() {
        return 0;
    }

}
