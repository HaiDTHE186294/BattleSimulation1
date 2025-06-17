package org.example;

import combat.CombatManager;
import effect.model.BurnEffect;
import effect.model.Effect;
import effect.service.EffectService;
import equipment.model.Weapon;
import equipment.service.EquipmentService;
import person.model.PersonStatus;
import person.model.Soldier;
import person.service.PersonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Tạo 2 đội
        List<Soldier> playerTeam = new ArrayList<>();
        Soldier alice = new Soldier("Alice", 100, PersonStatus.ALIVE, 10, 5);
        Soldier bob = new Soldier("Bob", 90, PersonStatus.ALIVE, 8, 6);
        playerTeam.add(alice);
        playerTeam.add(bob);

        List<Soldier> enemyTeam = new ArrayList<>();
        enemyTeam.add(new Soldier("Orc", 80, PersonStatus.ALIVE, 15, 4));
        enemyTeam.add(new Soldier("Goblin", 70, PersonStatus.ALIVE, 12, 3));

        // Trang bị mẫu
        Weapon sword = new Weapon("Sword", "Kiếm", 5);
        Weapon staff = new Weapon("Staff", "Gậy", 7);
        Weapon axe = new Weapon("Axe", "Rìu", 10);

        BurnEffect effect = new BurnEffect(2, 3); // Hiệu ứng độc, kéo

        // Tạo services
        EquipmentService equipmentService = new EquipmentService();
        EffectService effectService = new EffectService();
        PersonService personService = new PersonService(effectService);

        // Trang bị
        equipmentService.equip(alice, sword);
        equipmentService.equip(bob, axe);
        equipmentService.equip(alice, staff);

        // Combat manager
        CombatManager cm = new CombatManager(playerTeam, enemyTeam, personService, effectService, equipmentService);

        System.out.println("=== Start Combat ===");

        while (!cm.isCombatEnded()) {
            Soldier current = cm.getCurrentSoldier();

            if (current == null) {
                cm.endTurn();
                continue;
            }

            System.out.println("\n=> Turn: " + current.getName());

            Soldier target;
            if (playerTeam.contains(current)) {
                target = chooseTarget(scanner, cm.getEnemyTeam());
            } else {
                target = findTarget(current, cm);
            }

            if (playerTeam.contains(current)) {
                // Người chơi chọn hành động
                System.out.println("1. Attack");
                System.out.println("2. Use Item");
                System.out.print("Choose action: ");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        cm.attack(current, target);
                        break;
                    case 2:
                        // Danh sách trang bị
                        List<Weapon> weapons = current.getWeapons();
                        if (weapons.isEmpty()) {
                            System.out.println("No Item.");
                        } else {
                            System.out.println("Choose Item:");
                            for (int i = 0; i < weapons.size(); i++) {
                                System.out.println((i + 1) + ". " + weapons.get(i).getName());
                            }
                            int wIndex = Integer.parseInt(scanner.nextLine()) - 1;
                            Weapon selected = weapons.get(wIndex);
                            cm.useEquipment(target, selected);
                        }
                        break;

                    default:
                        System.out.println("Not valid action.");
                }
            } else {
                // Địch tự động tấn công
                System.out.println(current.getName() + " attacks " + target.getName());
                cm.attack(current, target);
            }

            cm.endTurn();
        }

        // Kết thúc trận
        System.out.println("\n=== Combat Ended ===");
        if (cm.isPlayerWin()) {
            System.out.println("🎉 You win!");
            cm.rewardIfWin();
        } else {
            System.out.println("💀 You fck off.");
        }
    }

    private static Soldier findTarget(Soldier current, CombatManager cm) {
        List<Soldier> team = cm.getPlayerTeam().contains(current)
                ? cm.getEnemyTeam()
                : cm.getPlayerTeam();

        for (Soldier s : team) {
            if (s.isAlive()) return s;
        }
        return null;
    }

    private static Soldier chooseTarget(Scanner scanner, List<Soldier> opponents) {
        System.out.println("choose Target:");
        List<Soldier> aliveOpponents = new ArrayList<>();

        for (Soldier s : opponents) {
            if (s.isAlive()) {
                aliveOpponents.add(s);
            }
        }

        for (int i = 0; i < aliveOpponents.size(); i++) {
            Soldier s = aliveOpponents.get(i);
            System.out.printf("%d. %s (HP: %d)\n", i + 1, s.getName(), s.getHealth());
        }

        int choice;
        while (true) {
            System.out.print("Input enemy number: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= aliveOpponents.size()) {
                    return aliveOpponents.get(choice - 1);
                } else {
                    System.out.println("Invalid choose.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please Input.");
            }
        }
    }

}
