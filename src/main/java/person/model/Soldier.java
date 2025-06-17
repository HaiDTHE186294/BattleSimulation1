package person.model;

import effect.model.Effect;
import equipment.model.IComponent;
import equipment.model.StatModifier;
import equipment.model.Weapon;
import observer.GameObserver;

import java.util.ArrayList;
import java.util.List;

public class Soldier extends Person {

    private int baseAtk;
    private int baseDef;
    private final List<IComponent> equipmentList;
    private final List<GameObserver> observers;
    private final List<Effect> activeEffects; // Giới hạn số lượng trang bị

    public Soldier(String name, int health, PersonStatus status, int baseAtk, int baseDef) {
        super(name, health, status);
        this.baseAtk = baseAtk;
        this.baseDef = baseDef;
        this.equipmentList = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.activeEffects = new ArrayList<>();
    }


    // Observer methods
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    protected void notifyEquipmentChanged() {
        for (GameObserver obs : observers) {
            obs.onEquipmentChanged(this);
        }
    }

    protected void notifyHealthChanged() {
        for (GameObserver obs : observers) {
            obs.onHealthChanged(this);
        }
    }

    // Equipment handling
    public void equip(IComponent item) {
        equipmentList.add(item);
        notifyEquipmentChanged();
    }

    public void unEquip(IComponent item) {
        if (equipmentList.remove(item)) {
            notifyEquipmentChanged();
        }
    }

    public List<IComponent> getEquipmentList() {
        return equipmentList;
    }

    // Stat calculation
    public int getTotalAtk() {
        int bonus = 0;
        for (IComponent item : equipmentList) {
            if (item instanceof StatModifier statItem) {
                bonus += statItem.getBonusAtk();
            }
        }
        return baseAtk + bonus;
    }

    public int getTotalDef() {
        int bonus = 0;
        for (IComponent item : equipmentList) {
            if (item instanceof StatModifier statItem) {
                bonus += statItem.getBonusDef();
            }
        }
        return baseDef + bonus;
    }

    // React to command
    @Override
    public void react(String order) {
        switch (order) {
            case "Attack" -> System.out.println(getName() + " đang tấn công với " + getTotalAtk() + " sát thương.");
            case "Defend" -> System.out.println(getName() + " phòng thủ với " + getTotalDef() + " giáp.");
            default -> System.out.println(getName() + " chưa biết làm gì.");
        }
    }

    // Getters/Setters
    public int getBaseAtk() {
        return baseAtk;
    }

    public void setBaseAtk(int baseAtk) {
        this.baseAtk = baseAtk;
    }

    public int getBaseDef() {
        return baseDef;
    }

    public void setBaseDef(int baseDef) {
        this.baseDef = baseDef;
    }

    public GameObserver[] getObservers() {
        return observers.toArray(new GameObserver[0]);
    }

    public boolean isAlive() {
        return getStatus() == PersonStatus.ALIVE || getStatus() == PersonStatus.INJURED;
    }

    public List<Weapon> getWeapons() {
        return equipmentList.stream()
                .filter(i -> i instanceof Weapon)
                .map(i -> (Weapon) i)
                .toList();
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }
}
