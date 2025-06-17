package combat;

import equipment.model.Weapon;
import equipment.service.EquipmentService;
import gamecore.GameContext;
import person.model.Soldier;
import person.service.PersonService;
import effect.service.EffectService;

import java.util.List;

public class CombatManager {
    private final List<Soldier> playerTeam;
    private final List<Soldier> enemyTeam;
    private final PersonService personService;
    private final EffectService effectService;
    private final EquipmentService equipmentService;
    private int currentTurnIndex = 0;
    private boolean isPlayerTurn = true;

    public CombatManager(List<Soldier> playerTeam, List<Soldier> enemyTeam,
                         PersonService personService, EffectService effectService, EquipmentService equipmentService) {
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
        this.personService = personService;
        this.effectService = effectService;
        this.equipmentService = equipmentService;
    }

    public Soldier getCurrentSoldier() {
        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        while (currentTurnIndex < team.size() && !team.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }
        return currentTurnIndex < team.size() ? team.get(currentTurnIndex) : null;
    }

    public boolean useEquipment(Soldier target, Weapon weapon) {
        if (!target.isAlive()) {
            return false;
        } else {
            equipmentService.triggerActionOnEquip(target, weapon);
            return true;
        }
    }

    public boolean attack(Soldier attacker, Soldier target) {
        if (!attacker.isAlive() || !target.isAlive()) return false;
        personService.performAttack(attacker, target);
        return true;
    }

    public void endTurn() {
        currentTurnIndex++;
        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;

        // Nếu hết lượt cho toàn đội
        if (currentTurnIndex >= team.size()) {
            currentTurnIndex = 0;
            isPlayerTurn = !isPlayerTurn;
            cleanDead();
            updateEffects();
        }
    }

    private void updateEffects() {
        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        for (Soldier s : team) {
            if (s.isAlive()) {
                effectService.updateEffects(s);
            }
        }
    }

    private void cleanDead() {
        // Remove người chết ra khỏi lượt hoặc gắn nhãn isAlive = false
    }

    public boolean isCombatEnded() {
        return playerTeam.stream().noneMatch(Soldier::isAlive) ||
                enemyTeam.stream().noneMatch(Soldier::isAlive);
    }

    public boolean isPlayerWin() {
        return playerTeam.stream().anyMatch(Soldier::isAlive)
                && enemyTeam.stream().noneMatch(Soldier::isAlive);
    }

    public void rewardIfWin() {
        if (isPlayerWin()) {
            GameContext.getInstance().earnMoney(500);
            System.out.println(GameContext.getInstance().getMoney());
        }
    }

    public List<Soldier> getPlayerTeam() {
        return playerTeam;
    }

    public List<Soldier> getEnemyTeam() {
        return enemyTeam;
    }
}
