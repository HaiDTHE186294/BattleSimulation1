package gamecore;

import person.model.Workable;
import java.util.ArrayList;
import java.util.List;

public class GameClock extends Thread {
    private List<Workable> workers = new ArrayList<>();
    private boolean running = true;

    public void register(Workable w) {
        workers.add(w);
    }

    public void run() {
        while (running) {
            try {
                Thread.sleep(1000); // mỗi giây
                for (Workable w : workers) {
                    w.tick();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopClock() {
        running = false;
    }
}
