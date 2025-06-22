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
        System.out.println(target.getName() + " bắt đầu bị thiêu đốt trong " + duration + " lượt.");
        target.addEffect(this);
    }

    @Override
    public void onTurnStart(Person target) {
        if (isExpired()) return;
        System.out.println(target.getName() + " nhận " + damagePerTurn + " sát thương từ thiêu đốt.");
        target.takeDamage(damagePerTurn);
        duration--;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public String getName() {
        return "Burn";
    }

    @Override
    public Effect copyEffect() {
        return new BurnEffect(getDuration(), damagePerTurn);
    }
}