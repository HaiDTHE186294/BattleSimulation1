package combat;

import equipment.model.IComponent;
import equipment.model.Weapon;
import equipment.service.EquipmentService;
import gamecore.GameContext;
import person.model.Soldier;
import person.service.PersonService;
import effect.service.EffectService;
import effect.model.Effect;

import java.util.List;

public class CombatManager {
    private final List<Soldier> playerTeam;
    private final List<Soldier> enemyTeam;
    private final PersonService personService;
    private final EffectService effectService;
    private final EquipmentService equipmentService;
    private int currentTurnIndex = 0;
    private boolean isPlayerTurn = true;
    private Soldier currentSoldier = null;

    public CombatManager(List<Soldier> playerTeam, List<Soldier> enemyTeam,
                         PersonService personService, EffectService effectService, EquipmentService equipmentService) {
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
        this.personService = personService;
        this.effectService = effectService;
        this.equipmentService = equipmentService;
    }

    public Soldier getCurrentSoldier() {
        if (currentSoldier != null) return currentSoldier;

        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        while (currentTurnIndex < team.size() && !team.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }
        if (currentTurnIndex < team.size()) {
            currentSoldier = team.get(currentTurnIndex);
            if (currentSoldier.isAlive()) {
                effectService.activateEffects(currentSoldier);
            }
            return currentSoldier;
        }
        return null;
    }

    public boolean useEquipment(Soldier target, IComponent weapon) {
        if (target == null || !target.isAlive()) {
            System.out.println("Target is not available.");
            return false;
        }
        equipmentService.triggerActionOnEquip(target, weapon);
        return true;
    }

    public boolean attack(Soldier attacker, Soldier target) {
        if (attacker == null || !attacker.isAlive() || target == null || !target.isAlive()) {
            System.out.println("Invalid attack: either attacker or target is not available.");
            return false;
        }
        personService.performAttack(attacker, target);
        return true;
    }

    public void endTurn() {
        // Cập nhật duration và xóa effect hết hạn cho người chơi hiện tại
        if (currentSoldier != null && currentSoldier.isAlive()) {
            effectService.updateEffects(currentSoldier);
        }

        currentSoldier = null; // Reset cho lượt mới
        currentTurnIndex++;

        // Nếu hết lượt cho toàn đội
        if (currentTurnIndex >= (isPlayerTurn ? playerTeam : enemyTeam).size()) {
            currentTurnIndex = 0;
            isPlayerTurn = !isPlayerTurn;
            cleanDead();
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

    public void startNewTurn(Soldier soldier) {
        if (soldier != null && soldier.isAlive()) {
            // Kích hoạt effect khi bắt đầu lượt
            for (Effect effect : soldier.getActiveEffects()) {
                effect.onTurnStart(soldier);
            }
        }
    }

    public EffectService getEffectService() {
        return effectService;
    }
}
