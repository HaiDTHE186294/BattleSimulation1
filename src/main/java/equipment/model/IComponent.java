package equipment.model;
import person.model.Person;

public interface IComponent {
    public String getName();
    public void action(Person target);
}
