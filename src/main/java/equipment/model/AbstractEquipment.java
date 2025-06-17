package equipment.model;

import person.model.Person;

public abstract class AbstractEquipment implements IComponent, StatModifier {
    protected String name;
    protected int atk;
    protected int def;

    public AbstractEquipment(String name, int atk, int def) {
        this.name = name;
        this.atk = atk;
        this.def = def;
    }

    public String getName() {
        return name;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    // Phương thức trừu tượng để hành động của trang bị
    @Override
    public abstract void action(Person target);

    @Override
    public int getAttackModifier() {
        return atk;
    }

    @Override
    public int getDefenseModifier() {
        return def;
    }
}
