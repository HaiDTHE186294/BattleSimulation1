package effect.model;

import person.model.Person;

public class BurnEffect extends AbstractEffect {

    private int damagePerTurn;

    public BurnEffect(int duration, int damagePerTurn) {
        super("Thiêu đốt", duration);
        this.damagePerTurn = damagePerTurn;
    }

    @Override
    public void apply(Person target) {
        System.out.println(target.getName() + " bị thiêu đốt trong " + duration + " lượt.");
    }

    @Override
    public void onTurnStart(Person target) {
        if (isExpired()) return;

        System.out.println(target.getName() + " nhận " + damagePerTurn + " sát thương từ thiêu đốt.");
        target.takeDamage(damagePerTurn);
        reduceDuration();
    }
}
