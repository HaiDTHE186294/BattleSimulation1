package equipment.model;

import person.model.Person;
import utils.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

public class LuckyStone extends  AbstractCharm {

    public LuckyStone(IComponent component) {
        super(component);
    }

    @Override
    public String getName() {
        return "LuckyStone(" + component.getName() + ")";
    }

    @Override
    public void action(Person target) {
        super.action(target);

        int bonusDamage = RandomUtils.randomBonus(5, 10);
        target.takeDamage(bonusDamage);

        System.out.println("LuckyStone action performed on " + target.getName());
    }
}
