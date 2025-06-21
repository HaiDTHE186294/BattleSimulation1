package effect.model;

import person.model.Person;

public class BuffEffect extends AbstractEffect {
    private final int bonusAtk;
    private final int bonusDef;

    public BuffEffect(String name, int duration, int bonusAtk, int bonusDef) {
        super(name, duration);
        this.bonusAtk = bonusAtk;
        this.bonusDef = bonusDef;
    }

    public int getBonusAtk() {
        return bonusAtk;
    }

    public int getBonusDef() {
        return bonusDef;
    }

    @Override
    public void apply(Person target) {
        System.out.println(target.getName() + " gains " + bonusAtk + " attack and " + bonusDef + " defense from buff: " + name);
    }

    @Override
    public void onTurnStart(Person target) {
        System.out.println(target.getName() + " gets buff: " + name);
    }

    @Override
    public int getDuration() {
        return duration;
    }
}
