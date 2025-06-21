package gamecore;

import combat.CombatManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class GameController extends Observable {
    private final CombatManager cm;
    private ui.GameView view;

    public GameController(String enemyTeamId) {
        List<Soldier> playerTeam = createPlayerTeam();
        EffectService effectService = new EffectService();
        EquipmentService equipmentService = new EquipmentService();
        PersonService personService = new PersonService(effectService);

        Map<String, IComponent> equipmentMap = loadEquipmentMap("equipment.json");
        equipPlayerTeam(playerTeam, equipmentMap, equipmentService);

        List<Soldier> enemyTeam = loadEnemyTeam(enemyTeamId);

        this.cm = new CombatManager(playerTeam, enemyTeam, personService, effectService, equipmentService);
    }

    private List<Soldier> createPlayerTeam() {
        List<Soldier> team = new ArrayList<>();
        Soldier alice = new Soldier("Alice", 100, PersonStatus.ALIVE, 10, 5);
        Soldier bob = new Soldier("Bob", 90, PersonStatus.ALIVE, 8, 6);
        Soldier charlie = new Soldier("Charlie", 85, PersonStatus.ALIVE, 9, 7);
        team.add(alice);
        team.add(bob);
        team.add(charlie);
        return team;
    }

    private Map<String, IComponent> loadEquipmentMap(String fileName) {
        try {
            return EquipmentLoader.loadEquipmentMap(fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void equipPlayerTeam(List<Soldier> playerTeam, Map<String, IComponent> equipmentMap, EquipmentService equipmentService) {
        Soldier alice = playerTeam.get(0);
        Soldier bob = playerTeam.get(1);
        // Soldier charlie = playerTeam.get(2); // Nếu cần trang bị cho Charlie

        IComponent sword = equipmentMap.get("sword");
        IComponent staff = equipmentMap.get("staff");
        IComponent axe = equipmentMap.get("axe");
        IComponent armor = equipmentMap.get("armor");
        IComponent luckyStaff = equipmentMap.get("lucky_staff");

        equipmentService.equip(alice, sword);
        equipmentService.equip(alice, staff);
        equipmentService.equip(alice, armor);
        equipmentService.equip(bob, axe);
        equipmentService.equip(bob, sword);
        equipmentService.equip(alice, luckyStaff);
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

    public void setView(ui.GameView view) {
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
            if (w instanceof equipment.model.Weapon) {
                for (effect.model.Effect effect : ((equipment.model.Weapon) w).getEffects()) {
                    cm.getEffectService().applyEffect(target, effect);
                }
            }
            if (w instanceof equipment.model.Armor) {
                for (effect.model.Effect effect : ((equipment.model.Armor) w).getEffects()) {
                    cm.getEffectService().applyEffect(target, effect);
                }
            }
            if (w instanceof equipment.model.LuckyStone) {
                for (effect.model.Effect effect : ((equipment.model.LuckyStone) w).getEffects()) {
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