package person.model;


import java.util.Random;

import static person.model.PersonStatus.*;

public class Civilian extends Person implements Workable {

    private boolean working = false;
    private int workTimer = 0; // tổng thời gian làm
    private int moneyProduced = 0;

    public Civilian(String name, int health, PersonStatus status) {
        super(name, health, status);
    }

    @Override
    public void react(String order) {
        System.out.println(getName() + " không phản ứng.");
    }

    @Override
    public void startWorking() {
        if (!working) {
            working = true;
            System.out.println(getName() + " bắt đầu lao động.");
        }
    }

    @Override
    public void stopWorking() {
        if (working) {
            working = false;
            System.out.println(getName() + " đã ngừng lao động.");
        }
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public void tick() {
        if (working) {
            workTimer++;
            if (workTimer % 5 == 0) { // mỗi 5 giây sinh 1 tiền
                moneyProduced++;
                System.out.println(getName() + " đã kiếm được 1 tiền. Tổng: " + moneyProduced);
            }
            if (workTimer % 10 == 0) { // mỗi 10 giây mất 1 máu
                takeDamage(1);
                System.out.println(getName() + " mệt mỏi, mất 1 máu.");
            }
        }
    }

    public int collectMoney() {
        int earned = moneyProduced;
        moneyProduced = 0;
        return earned;
    }

    public Soldier transformToSoldier() {
        Random rand = new Random();
        int atk = rand.nextInt(10) + 5;
        int def = rand.nextInt(5) + 3;
        Soldier s = new Soldier(getName() + "_Upgraded", getHealth(), getStatus(), atk, def);
        System.out.println(getName() + " đã trở thành Soldier.");
        return s;
    }
}
