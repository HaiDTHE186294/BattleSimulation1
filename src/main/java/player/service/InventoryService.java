package player.service;

import equipment.model.AbstractEquipment;
import player.model.Inventory;
import equipment.model.IComponent;

import java.util.List;
import java.util.Optional;

public class InventoryService {

    /**
     * Thêm một item vào inventory nếu còn chỗ trống.
     * @param inventory Inventory của người chơi
     * @param item Vật phẩm muốn thêm
     * @return true nếu thêm thành công, false nếu đầy
     */
    public boolean addItem(Inventory inventory, IComponent item) {
        return inventory.addItem(item);
    }

    /**
     * Xóa một item khỏi inventory
     * @param inventory Inventory của người chơi
     * @param item Vật phẩm muốn xóa
     * @return true nếu xóa thành công
     */
    public boolean removeItem(Inventory inventory, IComponent item) {
        return inventory.removeItem(item);
    }

    /**
     * Tìm item theo tên (không phân biệt hoa thường)
     * @param inventory Inventory của người chơi
     * @param name Tên vật phẩm
     * @return Optional<IComponent> nếu tìm thấy, Optional.empty() nếu không
     */
    public Optional<IComponent> findItemByName(Inventory inventory, String name) {
        return inventory.getItems().stream()
                .filter(i -> (i instanceof AbstractEquipment eq) && eq.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Lấy tất cả vật phẩm trong inventory
     * @param inventory Inventory
     * @return List các item
     */
    public List<IComponent> getAllItems(Inventory inventory) {
        return inventory.getItems();
    }

    /**
     * Lấy ra 1 item theo tên và xóa khỏi inventory (thường dùng khi trang bị)
     * @param inventory Inventory của người chơi
     * @param name tên vật phẩm
     * @return Optional<IComponent> nếu lấy được, Optional.empty() nếu không tìm thấy
     */
    public Optional<IComponent> takeItemByName(Inventory inventory, String name) {
        Optional<IComponent> found = findItemByName(inventory, name);
        found.ifPresent(inventory::removeItem);
        return found;
    }

    /**
     * Kiểm tra inventory đã đầy chưa
     * @param inventory Inventory
     * @return true nếu đầy
     */
    public boolean isFull(Inventory inventory) {
        return inventory.isFull();
    }

    /**
     * Kiểm tra inventory rỗng
     * @param inventory Inventory
     * @return true nếu rỗng
     */
    public boolean isEmpty(Inventory inventory) {
        return inventory.isEmpty();
    }

    /**
     * Xóa toàn bộ item khỏi inventory
     * @param inventory Inventory
     */
    public void clear(Inventory inventory) {
        inventory.clear();
    }

    /**
     * Trả về số lượng item hiện có
     * @param inventory Inventory
     * @return int số lượng item
     */
    public int getItemCount(Inventory inventory) {
        return inventory.getItems().size();
    }
}