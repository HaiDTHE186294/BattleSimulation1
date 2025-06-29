package equipment.service;

import effect.model.BuffEffect;
import effect.model.BurnEffect;
import effect.model.HealEffect;
import effect.service.EffectService;
import equipment.model.Armor;
import equipment.model.IComponent;
import effect.model.Effect;
import equipment.model.LuckyStone;
import equipment.model.Weapon;
import observer.GameObserver;
import person.model.Soldier;

import java.util.List;
import java.util.function.Predicate;

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

        // �?p dụng effect từ vũ khí
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

    public static boolean isBuffItem(IComponent item) {
        return (item != null && anyEffectMatches(item, e -> e instanceof BuffEffect) || anyEffectMatches(item, e -> e instanceof HealEffect));
    }


    public static boolean isAttackItem(IComponent item) {
        return anyEffectMatches(item, e -> !(e instanceof BurnEffect));
    }

    private static boolean anyEffectMatches(IComponent item, Predicate<Effect> predicate) {
        if (item == null) return false;

        // LuckyStone: kiểm tra hiệu ứng của LuckyStone, rồi đệ quy vào wrapped item
        if (item instanceof LuckyStone l) {
            if (l.getEffects() != null && l.getEffects().stream().anyMatch(predicate)) return true;
            return l.getComponent() != null && anyEffectMatches((IComponent) l.getComponent(), predicate);
        }
        // Weapon hoặc Armor: kiểm tra hiệu ứng của chính item
        if (item instanceof Weapon w && w.getEffects() != null) {
            return w.getEffects().stream().anyMatch(predicate);
        }
        if (item instanceof Armor a && a.getEffects() != null) {
            return a.getEffects().stream().anyMatch(predicate);
        }
        return false;
    }

}
