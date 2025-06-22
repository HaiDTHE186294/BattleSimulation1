package teambuilder.model;


import equipment.model.IComponent;
import person.model.Soldier;

import java.util.*;

public class TeamBuilderModel {
    private final List<TeamDef> allTeams = new ArrayList<>();
    private final List<IComponent> allEquipment = new ArrayList<>();
    private final Map<String, List<IComponent>> soldierEquipmentMap = new HashMap<>();
    private final List<Soldier> chosenTeam = new ArrayList<>();

    public List<TeamDef> getAllTeams() { return allTeams; }
    public List<IComponent> getAllEquipment() { return allEquipment; }
    public List<Soldier> getChosenTeam() { return chosenTeam; }
    public Map<String, List<IComponent>> getSoldierEquipmentMap() { return soldierEquipmentMap; }

    public void addSoldier(Soldier soldier) {
        chosenTeam.add(soldier);
        soldierEquipmentMap.put(soldier.getName(), new ArrayList<>());
    }

    public void removeSoldier(Soldier soldier) {
        chosenTeam.remove(soldier);
        soldierEquipmentMap.remove(soldier.getName());
    }

    public void assignEquipment(Soldier soldier, IComponent equip) {
        soldierEquipmentMap.getOrDefault(soldier.getName(), new ArrayList<>()).add(equip);
    }

    public void clear() {
        chosenTeam.clear();
        soldierEquipmentMap.clear();
    }
}