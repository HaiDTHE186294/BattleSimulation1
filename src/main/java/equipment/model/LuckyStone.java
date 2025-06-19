package equipment.model;

import person.model.Person;

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

        // Gây thêm sát thương random 5-10
        int bonusDamage = ThreadLocalRandom.current().nextInt(5, 11); // [5, 10]
        target.takeDamage(bonusDamage);

        System.out.println("LuckyStone action performed on " + target.getName());
    }
}
