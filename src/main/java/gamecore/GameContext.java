package gamecore;


import observer.GameObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class GameContext {

    private static final GameContext instance = new GameContext();
    private final List<GameObserver> observers = new ArrayList<>();


    private final AtomicInteger money = new AtomicInteger(1000);
    private final GameClock clock = new GameClock();

    private final Semaphore moneyLock = new Semaphore(1);

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    private GameContext() {
    }

    public static GameContext getInstance() {
        return instance;
    }

    public GameClock getClock() {
        return clock;
    }

    public int getMoney() {
        return money.get();
    }

    public boolean spendMoney(int amount) {
        try {
            moneyLock.acquire();
            if (money.get() >= amount) {
                money.addAndGet(-amount);
                notifyMoneyChanged();
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            moneyLock.release();
        }
    }

    public void earnMoney(int amount) {
        try {
            moneyLock.acquire();
            money.addAndGet(amount);
            notifyMoneyChanged();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            moneyLock.release();
        }
    }

    private void notifyMoneyChanged() {
        int currentMoney = money.get();
        for (GameObserver observer : observers) {
            observer.onMoneyChanged(currentMoney);
        }
    }
}
