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

    EffectService effectService;

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
        // Chỉ kích hoạt effect nếu item là Armor và có hiệu ứng
        if (item instanceof Armor armor) {
            for (Effect effect : armor.getEffects()) {
                soldier.addEffect(effect);
            }
        }
    }

    public void triggerActionOnEquip(Soldier target, IComponent item) {
        // Chỉ kích hoạt effect nếu item là Wp và có hiệu ứng
        item.action(target);
    }

    private void notifyEquipmentChanged(Soldier soldier) {
        for (GameObserver obs : soldier.getObservers()) {
            obs.onEquipmentChanged(soldier);
        }
    }


}
