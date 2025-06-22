package combat;

import person.model.Soldier;
import java.util.List;

public class CombatManager {
    private final List<Soldier> playerTeam;
    private final List<Soldier> enemyTeam;
    private int currentTurnIndex = 0;
    private boolean isPlayerTurn = true;
    private Soldier currentSoldier = null;
    private final String battleId;
    private CombatEndListener combatEndListener;

    public CombatManager(List<Soldier> playerTeam, List<Soldier> enemyTeam, String battleId) {
        this.playerTeam = playerTeam;
        this.enemyTeam = enemyTeam;
        this.battleId = battleId;
    }

    public void setCombatEndListener(CombatEndListener listener) {
        this.combatEndListener = listener;
    }

    public Soldier getCurrentSoldier() {
        if (currentSoldier != null) return currentSoldier;
        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        while (currentTurnIndex < team.size() && !team.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }
        if (currentTurnIndex < team.size()) {
            currentSoldier = team.get(currentTurnIndex);
            return currentSoldier;
        }
        return null;
    }

    // Chỉ chuyển lượt, không xử lý logic hiệu ứng, tấn công, dùng item ở đây
    public void endTurn() {
        currentSoldier = null;
        currentTurnIndex++;

        cleanDead();

        List<Soldier> team = isPlayerTurn ? playerTeam : enemyTeam;
        if (currentTurnIndex >= team.size()) {
            currentTurnIndex = 0;
            isPlayerTurn = !isPlayerTurn;
            cleanDead();
        }

        if (isCombatEnded() && combatEndListener != null) {
            combatEndListener.onCombatEnded(battleId, isPlayerWin());
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

    public List<Soldier> getPlayerTeam() {
        return playerTeam;
    }

    public List<Soldier> getEnemyTeam() {
        return enemyTeam;
    }
}