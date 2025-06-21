package equipment.model;

import person.model.Person;

public abstract class AbstractCharm implements IComponent {

    protected IComponent component;

    public AbstractCharm(IComponent component) {
        this.component = component;
    }

    @Override
    public void action(Person target) {
        component.action(target);
    }

    public IComponent getComponent() {
        return component;
    }
}
