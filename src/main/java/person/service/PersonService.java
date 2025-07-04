package person.service;
import effect.model.Effect;
import effect.service.EffectService;
import equipment.model.IComponent;
import equipment.model.StatModifier;
import observer.GameObserver;
import person.model.Person;
import person.model.Soldier;

public class PersonService {

    private final EffectService effectService;

    public PersonService(EffectService effectService) {
        this.effectService = effectService;
    }

    public void performAttack(Soldier attacker, Soldier target) {
        int totalAtk = calculateTotalAtk(attacker);
        int totalDef = calculateTotalDef(target);
        target.takeDamage(Math.max(totalAtk - totalDef, 0));
        notifyHealthChanged(target);
    }

    public void equipItem(Soldier soldier, IComponent equipment) {
        soldier.equip(equipment);
        notifyEquipmentChanged(soldier);
    }

    public int calculateTotalAtk(Soldier soldier) {
        return soldier.getTotalAtk();
    }

    public int calculateTotalDef(Soldier soldier) {
        return soldier.getTotalDef();
    }

    public void applyEffect(Soldier person, Effect effect) {
        effectService.applyEffect(person, effect);
        notifyStatusChanged(person);
    }

    private void notifyEquipmentChanged(Soldier person) {
    for (GameObserver obs : person.getObservers()) {
            obs.onEquipmentChanged(person);
        }
    }

    private void notifyStatusChanged(Soldier person) {
        for (GameObserver obs : person.getObservers()) {
            obs.onStatusChanged(person);
        }
    }
    private void notifyHealthChanged(Soldier person) {
        for (GameObserver obs : person.getObservers()) {
            obs.onHealthChanged(person);
        }
    }
}
