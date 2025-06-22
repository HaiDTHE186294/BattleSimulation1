package teambuilder.controller;


import effect.service.EffectService;
import equipment.model.IComponent;
import person.model.PersonStatus;
import person.model.Soldier;
import teambuilder.model.MemberDef;
import teambuilder.model.TeamBuilderModel;


public class TeamBuilderController {
    private final TeamBuilderModel model;
    private final EffectService effectService;

    public TeamBuilderController(TeamBuilderModel model, EffectService effectService) {
        this.model = model;
        this.effectService = effectService;
    }

    public void addMember(MemberDef memberDef) {
        Soldier soldier = new Soldier(memberDef.name, memberDef.hp, PersonStatus.valueOf(memberDef.status), memberDef.atk, memberDef.def);
        model.addSoldier(soldier);
    }

    public void removeMember(Soldier soldier) {
        model.removeSoldier(soldier);
    }

    public void assignEquipment(Soldier soldier, IComponent equipment) {
        IComponent clone = teambuilder.ui.TeamBuilderFrame.cloneEquipment(equipment, effectService);
        model.assignEquipment(soldier, clone);
    }

    public void confirmTeam() {
        for (Soldier soldier : model.getChosenTeam()) {
            for (IComponent equip : model.getSoldierEquipmentMap().getOrDefault(soldier.getName(), new java.util.ArrayList<>())) {
                soldier.equip(equip);
            }
        }
    }
}