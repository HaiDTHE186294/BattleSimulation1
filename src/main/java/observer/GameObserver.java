// file: observer/GameObserver.java
package observer;

import person.model.Person;

public interface GameObserver {
    void onHealthChanged(Person person);
    void onEquipmentChanged(Person person);
    void onStatusChanged(Person person);
    void onMoneyChanged(int money);
}
