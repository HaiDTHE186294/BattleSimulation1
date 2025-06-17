package person.model;
import effect.model.Effect;
import observer.GameObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Person {
    private final String name;
    private int health;
    private PersonStatus status;


    private List<GameObserver> observers = new ArrayList<>();
    protected List<Effect> activeEffects = new ArrayList<>();


    public Person(String name, int health, PersonStatus status) {
        this.name = name;
        this.health = health;
        this.status = status;
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
            System.out.println(name + " đã chết.");
        } else {
            this.status = PersonStatus.INJURED;
            System.out.println(name + " đã mất " + amount + " máu. Máu còn lại: " + health);
        }
        notifyHealthChanged();
    }

    public void heal(int amount) {
        this.health += amount;
        if (health <= 0) {
            this.status = PersonStatus.DEAD;
            System.out.println(name + " đã chết.");
        } else if (health > 100) {
            this.health = 100; // Giới hạn máu tối đa
            this.status = PersonStatus.ALIVE;
            System.out.println(name + " đã hồi phục. Máu hiện tại: " + health);
        } else {
            this.status = PersonStatus.ALIVE;
            System.out.println(name + " đã hồi phục " + amount + " máu. Máu hiện tại: " + health);
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
        effect.apply(this);
    }

    public void removeEffect(Effect effect) {
        if (activeEffects.remove(effect)) {
            activeEffects.remove(this);
            System.out.println(getName() + " đã loại bỏ hiệu ứng " + effect.getName());
        }
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }

    public void startTurn() {
        Iterator<Effect> it = activeEffects.iterator();
        while (it.hasNext()) {
            Effect effect = it.next();
            effect.onTurnStart(this);
            if (effect.isExpired()) {
                System.out.println(getName() + " đã hết hiệu ứng " + effect.getName());
                it.remove();
            }
        }
    }




}
