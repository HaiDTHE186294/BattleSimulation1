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
        System.out.println(target.getName() + " nhận buff: " + name + " +" + bonusAtk + " ATK, +" + bonusDef + " DEF trong " + duration + " lượt.");
        target.addEffect(this);
    }

    @Override
    public void onTurnStart(Person target) {
        System.out.println(target.getName() + " duy trì buff: " + name);
        duration--;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Effect copyEffect() {
        return new BuffEffect(name, getDuration(), bonusAtk, bonusDef);
    }

}