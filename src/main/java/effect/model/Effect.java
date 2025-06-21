package effect.model;

import person.model.Person;
import person.model.Soldier;

public interface Effect {
    String getName();
    void apply(Person target);
    void onTurnStart(Person target);
    boolean isExpired();
    void reduceDuration();
    void onExpire(Person target);
    int getDuration();


}
