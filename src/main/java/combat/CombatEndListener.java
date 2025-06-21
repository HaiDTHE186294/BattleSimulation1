package combat;

public interface CombatEndListener {
    void onCombatEnded(String battleId, boolean playerWin);
}