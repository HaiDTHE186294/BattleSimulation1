package player.model;

import equipment.model.IComponent;

import java.util.List;

public class Inventory {
    private final List<IComponent> items = new java.util.ArrayList<>();
    private final int maxSize;

    public Inventory(int maxSize) {
        this.maxSize = maxSize;
    }

    public boolean addItem(IComponent item) {
        if (items.size() < maxSize) {
            items.add(item);
            return true;
        }
        return false; // Không thể thêm nếu vượt quá kích thước tối đa
    }

    public boolean removeItem(IComponent item) {
        return items.remove(item);
    }

    public List<IComponent> getItems() {
        return items;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public boolean isFull() {
        return items.size() >= maxSize;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    
}
