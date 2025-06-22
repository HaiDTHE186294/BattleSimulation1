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
    private final String battelId;
    private CombatEndListener combatEndListener;


    public void setCombatEndListener(CombatEndListener listener) {
        this.combatEndListener = listener;
    }

    public CombatManager(List<Soldier> playerTeam, List<Soldier> enemyTeam,
                         PersonService personService, EffectService effectService, EquipmentService equipmentService, String BattleId) {
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
        this.personService = personService;
        this.effectService = effectService;
        this.equipmentService = equipmentService;
        this.battelId = BattleId;

    }

    public Soldier getCurrentSoldier() {
        if (currentSoldier != null) return currentSoldier;

        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        while (currentTurnIndex < team.size() && !team.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }
        if (currentTurnIndex < team.size()) {
            currentSoldier = team.get(currentTurnIndex);
//            if (currentSoldier.isAlive()) {
//                effectService.activateEffects(currentSoldier);
//            }
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

        // Kiểm tra xem có người chết không, nếu có thì loại bỏ khỏi danh sách
        cleanDead();

        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        // Nếu index vượt quá size do có người bị loại, reset index và chuyển lượt
        if (currentTurnIndex >= team.size()) {
            currentTurnIndex = 0;
            isPlayerTurn = !isPlayerTurn;
            // Sau khi chuyển lượt, lại cleanDead một lần nữa để đảm bảo danh sách mới
            cleanDead();
        }

        if (isCombatEnded() && combatEndListener != null) {
            combatEndListener.onCombatEnded(battelId, isPlayerWin());
        }
    }

    private void cleanDead() {
        playerTeam.removeIf(s -> !s.isAlive());
        enemyTeam.removeIf(s -> !s.isAlive());
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


    public EffectService getEffectService() {
        return effectService;
    }
}
