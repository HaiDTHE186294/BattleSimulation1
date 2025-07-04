package person.model;
import effect.model.Effect;
import observer.GameObserver;
import java.util.ArrayList;
import java.util.List;

public abstract class Person {
    private final String name;
    private int health;
    private PersonStatus status;
    private int baseHealth; // Giả sử máu tối đa là 100


    private List<GameObserver> observers = new ArrayList<>();
    protected List<Effect> activeEffects = new ArrayList<>();


    public Person(String name, int health, PersonStatus status) {
        this.name = name;
        this.health = health;
        this.status = status;
        this.baseHealth = health; // Giả sử máu tối đa là 100
    }

    // Observer registration
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    protected void notifyHealthChanged() {
        for (GameObserver obs : observers) {
            obs.onHealthChanged(this);
        }
    }

    protected void notifyEquipmentChanged() {
        for (GameObserver obs : observers) {
            obs.onEquipmentChanged(this);
        }
    }


    protected void notifyStatusChanged() {
        for (GameObserver obs : observers) {
            obs.onStatusChanged(this);
        }
    }

    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
        if (health <= 0) {
            this.status = PersonStatus.DEAD;
            System.out.println(name + " dead.");
        } else {
            this.status = PersonStatus.INJURED;
            System.out.println(name + " lost " + amount + " HP. Remain: " + health);
        }
        notifyHealthChanged();
    }

    public void takeHeal(int amount) {
        this.health += amount;
        if (health <= 0) {
            this.status = PersonStatus.DEAD;
            System.out.println(name + " dead.");
        } else if (health > baseHealth) {
            this.health = baseHealth; // Giới hạn máu tối đa
            this.status = PersonStatus.ALIVE;
            System.out.println(name + " healed. Remain: " + health);
        } else {
            this.status = PersonStatus.ALIVE;
            System.out.println(name + " healed " + amount + " HP. Remain: " + health);
        }
        notifyHealthChanged();
    }

    public void setStatus(PersonStatus status) {
        this.status = status;
        notifyStatusChanged();
    }

    // Getters
    public String getName() { return name; }
    public int getHealth() { return health; }
    public PersonStatus getStatus() { return status; }


    // React method abstract
    public abstract void react(String order);

    public void addEffect(Effect effect) {
        activeEffects.add(effect);
    }

    public void removeEffect(Effect effect) {
        if (activeEffects.remove(effect)) {
            System.out.println(getName() + " removed " + effect.getName());
        }
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }


}
