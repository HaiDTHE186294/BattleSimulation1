package equipment.model;

import effect.model.Effect;
import effect.service.EffectService;
import person.model.Person;
import utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;

public class LuckyStone extends AbstractCharm {

    private List<Effect> effects = new ArrayList<>();
    private EffectService effectService;

    public LuckyStone(IComponent component, EffectService effectService) {
        super(component);
        this.effectService = effectService;
    }

    public LuckyStone(IComponent component) {
        this(component, null);
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void setEffects(List<Effect> effects) {
        this.effects = effects;
    }

    @Override
    public String getName() {
        return "LuckyStone(" + component.getName() + ")";
    }

    @Override
    public void action(Person target) {
        super.action(target);

        // Apply all effects if any
        if (effects != null) {
            for (Effect effect : effects) {
                effect.apply(target);
            }
        }

        int bonusDamage = RandomUtils.randomBonus(5, 10);
        target.takeDamage(bonusDamage);

        System.out.println("LuckyStone action performed on " + target.getName());
    }

}