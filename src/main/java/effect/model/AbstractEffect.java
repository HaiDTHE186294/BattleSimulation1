package effect.model;

import person.model.Person;

public abstract class AbstractEffect implements Effect {
    protected String name;
    protected int duration; // Số lượt còn lại

    public AbstractEffect(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isExpired() {
        return duration <= 0;
    }

    @Override
    public void reduceDuration() {
        duration--;
    }

    @Override
    public void onExpire(Person target) {
        target.removeEffect(this);
    }


}
