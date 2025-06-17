package person.model;

public interface Workable {
    void startWorking();
    void stopWorking();
    boolean isWorking();
    void tick(); // gọi mỗi giây để giảm máu, tăng tiền
}
