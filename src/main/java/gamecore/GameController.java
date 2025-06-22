package gamecore;

import combat.CombatManager;
import dialogue.service.DialogueService;
import effect.model.BuffEffect;
import effect.model.BurnEffect;
import effect.model.Effect;
import effect.service.EffectService;
import equipment.model.Armor;
import equipment.model.IComponent;
import equipment.model.LuckyStone;
import equipment.model.Weapon;
import equipment.service.EquipmentLoader;
import equipment.service.EquipmentService;
import person.model.PersonStatus;
import person.model.Soldier;
import person.service.PersonService;
import ui.GameView;
import ui.MainFrame;
import teambuilder.ui.TeamBuilderFrame;
import utils.BgmPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class GameController extends Observable {
    private CombatManager cm;
    private GameView view;
    private List<Soldier> playerTeam;
    private final String enemyTeamId;

    // Refactored: Services are now direct fields
    private EffectService effectService;
    private EquipmentService equipmentService;
    private PersonService personService;

    public GameController(String enemyTeamId) {
        this.enemyTeamId = enemyTeamId;
    }

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            effectService = new EffectService();
            teambuilder.model.TeamBuilderModel teamBuilderModel = new teambuilder.model.TeamBuilderModel();
            teambuilder.controller.TeamBuilderController teamBuilderController = new teambuilder.controller.TeamBuilderController(teamBuilderModel, effectService);

            // Tạo TeamBuilderFrame (View) và truyền callback nhận đội hình đã chọn
            new teambuilder.ui.TeamBuilderFrame(selectedTeam -> {
                this.playerTeam = selectedTeam;
                afterTeamSelected();
            }, teamBuilderModel, teamBuilderController, effectService);
        });
    }

    private void afterTeamSelected() {
        equipmentService = new EquipmentService(effectService);
        personService = new PersonService(effectService);

        EquipmentLoader.setEffectService(effectService);
        Map<String, IComponent> equipmentMap = loadEquipmentMap("equipment.json");

        List<Soldier> enemyTeam = loadEnemyTeam(enemyTeamId);
        BgmPlayer bgmPlayer = BgmPlayer.getInstance();
        bgmPlayer.play("battle.wav", true);

        this.cm = new CombatManager(this.playerTeam, enemyTeam, enemyTeamId);
        cm.setCombatEndListener((battleId, playerWin) -> {
            String nextDialogueId = battleId + (playerWin ? "Win" : "Lose");
            DialogueService.getInstance().startDialogue(nextDialogueId);
            // Mở lại UI hội thoại sau trận đấu
            SwingUtilities.invokeLater(() -> {
                new dialogue.ui.DialogueAppSwing(
                        new dialogue.ui.SwingDialogueController(DialogueService.getInstance()),
                        DialogueService.getInstance(),
                        nextDialogueId
                );
            });
        });
        SwingUtilities.invokeLater(() -> {
            new MainFrame(this);
            if (view != null) view.refresh();
            setChanged();
            notifyObservers();
        });
    }

    private Map<String, IComponent> loadEquipmentMap(String fileName) {
        try {
            return EquipmentLoader.loadEquipmentMap(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Soldier> loadEnemyTeam(String teamId) {
        List<CombatLevelLoader.EnemyTeamDef> allTeams;
        try {
            allTeams = CombatLevelLoader.loadEnemyTeams("enemy_teams.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CombatLevelLoader.EnemyTeamDef team = CombatLevelLoader.findTeamById(allTeams, teamId);
        return CombatLevelLoader.toSoldierList(team);
    }

    public void setView(GameView view) {
        this.view = view;
    }

    public CombatManager getCombatManager() {
        return cm;
    }

    public Soldier getCurrentSoldier() { return cm.getCurrentSoldier(); }
    public List<Soldier> getPlayerTeam() { return cm.getPlayerTeam(); }
    public List<Soldier> getEnemyTeam() { return cm.getEnemyTeam(); }
    public boolean isCombatEnded() { return cm.isCombatEnded(); }
    public boolean isPlayerWin() { return cm.isPlayerWin(); }
    public List<Soldier> getAlive(List<Soldier> l) {
        List<Soldier> a = new ArrayList<>();
        for (Soldier s : l) if (s.isAlive()) a.add(s);
        return a;
    }

    // --- Combat actions: Now only use services directly, not via CombatManager ---
    public void playerAttack(Soldier attacker, Soldier target) {
        if (attacker == null || !attacker.isAlive() || target == null || !target.isAlive()) return;
        personService.performAttack(attacker, target);
        notifyLog(attacker.getName() + " attacks " + target.getName() + "!");
        endTurnAndAutoEnemy();
    }

    public void playerUseItem(Soldier user, Soldier target, IComponent w) {
        if (target == null || !target.isAlive()) return;
        equipmentService.triggerActionOnEquip(target, w);
        notifyLog(user.getName() + " uses " + w.getName() + " on " + target.getName() + "!");
        endTurnAndAutoEnemy();
    }

    public void playerBurn(Soldier target) {
        if (target == null || !target.isAlive()) return;
        BurnEffect burnEffect = new BurnEffect(2, 3);
        effectService.applyEffect(target, burnEffect);
        notifyLog(target.getName() + " bị thiêu đốt!");
        endTurnAndAutoEnemy();
    }

    public void playerBuff(Soldier target) {
        if (target == null || !target.isAlive()) return;
        BuffEffect buffEffect = new BuffEffect("Buff ATK", 3, 10, 5);
        effectService.applyEffect(target, buffEffect);
        notifyLog(target.getName() + " được buff!");
        endTurnAndAutoEnemy();
    }

    // --- Turn flow: Explicitly call updateEffects & activateEffects in the right order ---
    public void playerEndTurn() {
        // 1. Update effects for the current soldier at end of their turn
        Soldier current = cm.getCurrentSoldier();
        if (current != null && current.isAlive()) {
            effectService.updateEffects(current);
        }
        cm.endTurn();

        // 2. Activate effects for the next soldier (if alive)
        Soldier next = cm.getCurrentSoldier();
        if (next != null && next.isAlive()) {
            effectService.activateEffects(next);
        }
        notifyLog("Turn ended.");
        autoEnemyTurn();
        notifyAllState();
    }

    private void endTurnAndAutoEnemy() {
        // 1. Update effects for the current soldier at end of their turn
        Soldier current = cm.getCurrentSoldier();
        if (current != null && current.isAlive()) {
            effectService.updateEffects(current);
        }
        cm.endTurn();

        // 2. Activate effects for the next soldier (if alive)
        Soldier next = cm.getCurrentSoldier();
        if (next != null && next.isAlive()) {
            effectService.activateEffects(next);
        }
        autoEnemyTurn();
        notifyAllState();
    }

    private void autoEnemyTurn() {
        Soldier cur = cm.getCurrentSoldier();
        boolean isPlayer = (cur != null && cm.getPlayerTeam().contains(cur));
        while (cur != null && !isPlayer && !cm.isCombatEnded()) {
            List<Soldier> targets = getAlive(cm.getPlayerTeam());
            if (!targets.isEmpty()) {
                Soldier target = targets.get(0);
                personService.performAttack(cur, target);
                notifyLog(cur.getName() + " attacks " + target.getName() + "!");
            }
            // Update & activate effects for enemy turn
            if (cur != null && cur.isAlive()) {
                effectService.updateEffects(cur);
            }
            cm.endTurn();
            cur = cm.getCurrentSoldier();
            if (cur != null && cur.isAlive()) {
                effectService.activateEffects(cur);
            }
            isPlayer = (cur != null && cm.getPlayerTeam().contains(cur));
        }
    }

    private void notifyLog(String log) {
        setChanged();
        notifyObservers(log);
        if (view != null) view.refresh();
    }

    private void notifyAllState() {
        setChanged();
        notifyObservers();
        if (view != null) view.refresh();
    }

    // --- Utility methods for item classification ---

    public boolean isBuffItem(IComponent item) {
        return EquipmentService.isBuffItem(item);
    }

    public boolean isAttackItem(IComponent item) {
        return EquipmentService.isAttackItem(item);
    }
}