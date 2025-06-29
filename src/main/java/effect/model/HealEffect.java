/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package effect.model;

import person.model.Person;

/**
 *
 * @author trant
 */
public class HealEffect extends AbstractEffect{

    private int healQuantity;

    public HealEffect(int duration, int healQuantity) {
        super("Heal", duration);
        this.healQuantity = healQuantity;
    }

    @Override
    public void apply(Person target) {
        System.out.println(target.getName() + " b?t ??u h?i ph?c trong " + duration + " l??t.");
        target.addEffect(this);
        System.out.println(target.getName() + " nh?n " + healQuantity + " ?i?m t? h?i ph?c.");
        target.takeHeal(healQuantity);
    }

    @Override
    public void onTurnStart(Person target) {
        if (isExpired()) return;
        duration--;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public Effect copyEffect() {
        return new HealEffect(getDuration(), healQuantity);
    }

    @Override
    public String getName() {
        return "Heal";
    }

}
