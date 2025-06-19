package gamecore;

import combat.CombatManager;
import effect.model.BuffEffect;
import effect.model.BurnEffect;
import effect.service.EffectService;
import equipment.model.IComponent;
import equipment.model.LuckyStone;
import equipment.model.Weapon;
import equipment.service.EquipmentService;
import person.model.PersonStatus;
import person.model.Soldier;
import person.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class GameController extends Observable {
    private final CombatManager cm;
    private ui.GameView view;

    public GameController() {
        // ... (Khởi tạo các team, service, trang bị như bạn đã có)
        List<Soldier> playerTeam = new ArrayList<>();
        Soldier alice = new Soldier("Alice", 100, PersonStatus.ALIVE, 10, 5);
        Soldier bob = new Soldier("Bob", 90, PersonStatus.ALIVE, 8, 6);
        Soldier charlie = new Soldier("Charlie", 85, PersonStatus.ALIVE, 9, 7);
        playerTeam.add(alice);
        playerTeam.add(bob);
        playerTeam.add(charlie);

        List<Soldier> enemyTeam = new ArrayList<>();
        enemyTeam.add(new Soldier("Orc", 80, PersonStatus.ALIVE, 15, 4));
        enemyTeam.add(new Soldier("Goblin", 70, PersonStatus.ALIVE, 12, 3));
        enemyTeam.add(new Soldier("Troll", 90, PersonStatus.ALIVE, 14, 5));
        Weapon sword = new Weapon("Sword", "Kiếm", 5);
        Weapon staff = new Weapon("Staff", "Gậy", 7);
        Weapon axe = new Weapon("Axe", "Rìu", 10);

        //Test Charm nhé
        IComponent luckyStaff = new LuckyStone(staff);


        EffectService effectService = new EffectService();
        EquipmentService equipmentService = new EquipmentService();
        PersonService personService = new PersonService(effectService);

        equipmentService.equip(alice, sword);
        equipmentService.equip(bob, axe);
        equipmentService.equip(alice, luckyStaff);

        this.cm = new CombatManager(playerTeam, enemyTeam, personService, effectService, equipmentService);
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
}