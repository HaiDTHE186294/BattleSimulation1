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
import ui.TeamBuilderFrame;
import utils.BgmPlayer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class GameController extends Observable {
    private CombatManager cm;
    private GameView view;
    private final BgmPlayer bgmPlayer = new BgmPlayer();
    private List<Soldier> playerTeam;
    private final String enemyTeamId;


    public GameController(String enemyTeamId) {
        this.enemyTeamId = enemyTeamId;
    }


    

    public void startGame() {
        SwingUtilities.invokeLater(() -> {
            new TeamBuilderFrame(selectedTeam -> {
                this.playerTeam = selectedTeam;
                afterTeamSelected();
            });
        });
    }

    private void afterTeamSelected() {
        EffectService effectService = new EffectService();
        EquipmentService equipmentService = new EquipmentService();
        PersonService personService = new PersonService(effectService);

        Map<String, IComponent> equipmentMap = loadEquipmentMap("equipment.json");

        List<Soldier> enemyTeam = loadEnemyTeam(enemyTeamId);
        bgmPlayer.play("battle.wav", true);
        this.cm = new CombatManager(this.playerTeam, enemyTeam, personService, effectService, equipmentService, enemyTeamId);
        cm.setCombatEndListener((battleId, playerWin) -> {
            String nextDialogueId = battleId + (playerWin ? "Win" : "Lose");
            dialogue.service.DialogueService.getInstance().startDialogue(nextDialogueId);
            // Mở lại UI hội thoại sau trận đấu
            SwingUtilities.invokeLater(() -> {
                new dialogue.ui.DialogueAppSwing(
                        new dialogue.ui.SwingDialogueController(dialogue.service.DialogueService.getInstance()),
                        dialogue.service.DialogueService.getInstance(),
                        nextDialogueId
                );
            });
        });
        if (view != null) view.refresh();
        setChanged();
        notifyObservers();
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


    public void playerAttack(Soldier attacker, Soldier target) {
        if (cm.attack(attacker, target)) {
            notifyLog(attacker.getName() + " attacks " + target.getName() + "!");
            endTurnAndAutoEnemy();
        }
    }

    public void playerUseItem(Soldier user, Soldier target, IComponent w) {
        if (cm.useEquipment(target, w)) {
            // Áp dụng các effect (nếu có)
            if (w instanceof Weapon) {
                for (Effect effect : ((Weapon) w).getEffects()) {
                    cm.getEffectService().applyEffect(target, effect);
                }
            }
            if (w instanceof Armor) {
                for (Effect effect : ((Armor) w).getEffects()) {
                    cm.getEffectService().applyEffect(target, effect);
                }
            }
            if (w instanceof LuckyStone) {
                for (Effect effect : ((LuckyStone) w).getEffects()) {
                    cm.getEffectService().applyEffect(target, effect);
                }
            }
            notifyLog(user.getName() + " uses " + w.getName() + " on " + target.getName() + "!");
            endTurnAndAutoEnemy();
        }
    }

    public void playerBurn(Soldier target) {
        BurnEffect burnEffect = new BurnEffect(2, 3);
        cm.getEffectService().applyEffect(target, burnEffect);
        notifyLog(target.getName() + " bị thiêu đốt!");
        endTurnAndAutoEnemy();
    }

    public void playerBuff(Soldier target) {
        BuffEffect buffEffect = new BuffEffect("Buff ATK", 3, 10, 5);
        cm.getEffectService().applyEffect(target, buffEffect);
        notifyLog(target.getName() + " được buff!");
        endTurnAndAutoEnemy();
    }

    public void playerEndTurn() {
        cm.endTurn();
        notifyLog("Turn ended.");
        autoEnemyTurn();
        notifyAllState();
    }

    private void endTurnAndAutoEnemy() {
        cm.endTurn();
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
                cm.attack(cur, target);
                notifyLog(cur.getName() + " attacks " + target.getName() + "!");
            }
            cm.endTurn();
            cur = cm.getCurrentSoldier();
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


    public boolean isBuffItem(IComponent item) {
        // LuckyStone: kiểm tra cả wrapped
        if (item instanceof LuckyStone) {
            LuckyStone l = (LuckyStone) item;
            if (l.getEffects() != null) {
                for (Effect e : l.getEffects()) if (e instanceof BuffEffect) return true;
            }
            // Kiểm tra effect của wrapped (nếu có)
            if (l.getEffects() != null && isBuffItem((IComponent) l.getComponent())) return true;
        }
        // Weapon
        if (item instanceof Weapon w && w.getEffects() != null) {
            for (Effect e : w.getEffects()) if (e instanceof BuffEffect) return true;
        }
        // Armor
        if (item instanceof Armor a && a.getEffects() != null) {
            for (Effect e : a.getEffects()) if (e instanceof BuffEffect) return true;
        }
        return false;
    }

    public boolean isAttackItem(IComponent item) {
        // LuckyStone: kiểm tra cả wrapped
        if (item instanceof LuckyStone) {
            LuckyStone l = (LuckyStone) item;
            if (l.getEffects() != null) {
                for (Effect e : l.getEffects()) if (!(e instanceof BuffEffect)) return true;
            }
            if (l.getEffects() != null && isAttackItem((IComponent) l.getComponent()))  return true;
        }
        if (item instanceof Weapon w && w.getEffects() != null) {
            for (Effect e : w.getEffects()) if (!(e instanceof BuffEffect)) return true;
        }
        if (item instanceof Armor a && a.getEffects() != null) {
            for (Effect e : a.getEffects()) if (!(e instanceof BuffEffect)) return true;
        }
        return false;
    }
}