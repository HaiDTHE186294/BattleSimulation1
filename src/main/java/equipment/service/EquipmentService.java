package equipment.service;

import effect.service.EffectService;
import equipment.model.Armor;
import equipment.model.IComponent;
import effect.model.Effect;
import equipment.model.Weapon;
import observer.GameObserver;
import person.model.Soldier;

import java.util.List;

public class EquipmentService {

    private final EffectService effectService;

    public EquipmentService(EffectService effectService) {
        this.effectService = effectService;
    }

    public EquipmentService() {
        this(null);
    }

    public void equip(Soldier soldier, IComponent item) {
        soldier.equip(item);
        triggerEffectOnEquip(soldier, item);
        notifyEquipmentChanged(soldier);
    }

    public void unequipItemExceptArmor(Soldier soldier, IComponent item) {
        soldier.unEquip(item);
        notifyEquipmentChanged(soldier);
    }

    public void unequipArmor(Soldier soldier, Armor armor) {
        if (soldier.getEquipmentList().contains(armor)) {
            soldier.unEquip(armor);
            List<Effect> effects = armor.getEffects();
            for (Effect effect : effects) {
                soldier.removeEffect(effect);
            }
            notifyEquipmentChanged(soldier);
        }
    }

    private void triggerEffectOnEquip(Soldier soldier, IComponent item) {
        if (item instanceof Armor armor && effectService != null) {
            for (Effect effect : armor.getEffects()) {
                effectService.applyEffect(soldier, effect);
            }
        }
    }
    public void triggerActionOnEquip(Soldier target, IComponent item) {
        // Kích hoạt action của item
        item.action(target);

        // Áp dụng effect từ vũ khí
        if (item instanceof Weapon weapon && effectService != null) {
            for (Effect effect : weapon.getEffects()) {
                if (effect != null) {
                    effectService.applyEffect(target, effect);
                    System.out.println("Applied effect " + effect.getName() + " from " + weapon.getName() + " to " + target.getName());
                }
            }
        }
    }

    private void notifyEquipmentChanged(Soldier soldier) {
        for (GameObserver obs : soldier.getObservers()) {
            obs.onEquipmentChanged(soldier);
        }
    }


}
