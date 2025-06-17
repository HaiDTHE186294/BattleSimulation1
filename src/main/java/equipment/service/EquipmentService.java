package equipment.service;

import equipment.model.Armor;
import equipment.model.IComponent;
import effect.model.Effect;
import equipment.model.Weapon;
import observer.GameObserver;
import person.model.Soldier;

public class EquipmentService {

    public void equip(Soldier soldier, IComponent item) {
        soldier.equip(item);
        triggerEffectOnEquip(soldier, item);
        notifyEquipmentChanged(soldier);
    }

    public void unequip(Soldier soldier, IComponent item) {
        soldier.unEquip(item);
        notifyEquipmentChanged(soldier);
    }

    private void triggerEffectOnEquip(Soldier soldier, IComponent item) {
        // Chỉ kích hoạt effect nếu item là Armor và có hiệu ứng
        if (item instanceof Armor armor) {
            for (Effect effect : armor.getEffects()) {
                soldier.addEffect(effect);
            }
        }
    }

    public void triggerActionOnEquip(Soldier target, IComponent item) {
        // Chỉ kích hoạt effect nếu item là Wp và có hiệu ứng
        if (item instanceof Weapon weapon) {
            weapon.action(target);
        }
    }

    private void notifyEquipmentChanged(Soldier soldier) {
        for (GameObserver obs : soldier.getObservers()) {
            obs.onEquipmentChanged(soldier);
        }
    }


}
