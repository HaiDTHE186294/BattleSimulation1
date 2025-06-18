package effect.model;

import person.model.Person;

public class BurnEffect extends AbstractEffect {

    private int damagePerTurn;

    public BurnEffect(int duration, int damagePerTurn) {
        super("Burn", duration);
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void apply(Person target) {
        System.out.println(target.getName() + " burning in" + duration + " move.");
    }

    @Override
    public void onTurnStart(Person target) {
        if (isExpired()) return;
        System.out.println(target.getName() + " take " + damagePerTurn + " damage from burning.");
        target.takeDamage(damagePerTurn);
    }


}
