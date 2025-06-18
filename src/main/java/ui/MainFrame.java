package ui;

import gamecore.GameController;
import person.model.Soldier;
import equipment.model.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainFrame extends JFrame implements GameView, Observer {
    private final GameController controller;
    private JLabel turnLabel, combatStatusLabel;
    private JTextArea logArea;
    private JPanel playerPanel, enemyPanel;
    private JButton attackBtn, itemBtn, effectBtn, buffBtn, endTurnBtn;
    private JComboBox<String> targetBox, itemBox, buffTargetBox;

    public MainFrame(GameController controller) {
        this.controller = controller;
        controller.addObserver(this);
        controller.setView(this);

        setTitle("RPG Combat Demo (MVC)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 650);
        setLayout(new BorderLayout(10, 10));

        // --- Khởi tạo các thành phần UI ---
        // Log area
        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        // Thông tin trạng thái
        turnLabel = new JLabel("Turn: ");
        combatStatusLabel = new JLabel("Status: ");

        // Các nút và combobox
        attackBtn = new JButton("Tấn công");
        itemBtn = new JButton("Dùng vật phẩm");
        effectBtn = new JButton("Thiêu đốt");
        buffBtn = new JButton("Buff");
        endTurnBtn = new JButton("Kết thúc lượt");

        targetBox = new JComboBox<>();
        itemBox = new JComboBox<>();
        buffTargetBox = new JComboBox<>();

        // Panels hiển thị đội
        playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder("Đội bạn"));
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

        enemyPanel = new JPanel();
        enemyPanel.setBorder(BorderFactory.createTitledBorder("Đội địch"));
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));

        // Panel điều khiển
        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        controlPanel.add(new JLabel("Chọn mục tiêu:"));
        controlPanel.add(targetBox);
        controlPanel.add(attackBtn);
        controlPanel.add(itemBtn);
        controlPanel.add(effectBtn);
        controlPanel.add(endTurnBtn);

        // Panel buff riêng (nếu muốn)
        JPanel buffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buffPanel.add(new JLabel("Buff cho đồng đội:"));
        buffPanel.add(buffTargetBox);
        buffPanel.add(buffBtn);

        // Panel dưới cùng: log và trạng thái
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(logScroll, BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(turnLabel);
        statusPanel.add(combatStatusLabel);
        bottomPanel.add(statusPanel, BorderLayout.NORTH);

        // Panel chính
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.add(playerPanel);
        centerPanel.add(enemyPanel);

        // Thêm các panel vào frame
        add(centerPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.NORTH);
        add(buffPanel, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Gắn sự kiện cho các nút ---
        attackBtn.addActionListener(e -> {
            Soldier cur = controller.getCurrentSoldier();
            Soldier target = getSelectedTarget();
            if (cur != null && target != null)
                controller.playerAttack(cur, target);
        });

        itemBtn.addActionListener(e -> {
            Soldier cur = controller.getCurrentSoldier();
            Soldier target = getSelectedTarget();
            Weapon w = getSelectedWeapon(cur);
            if (cur != null && target != null && w != null)
                controller.playerUseItem(cur, target, w);
        });

        effectBtn.addActionListener(e -> {
            Soldier target = getSelectedTarget();
            if (target != null)
                controller.playerBurn(target);
        });

        buffBtn.addActionListener(e -> {
            Soldier buffTarget = getSelectedBuffTarget();
            if (buffTarget != null)
                controller.playerBuff(buffTarget);
        });

        endTurnBtn.addActionListener(e -> controller.playerEndTurn());

        setVisible(true);
        refresh();
    }

    @Override
    public void update(Observable o, Object arg) {
        // Nếu có log mới
        if (arg instanceof String log) {
            logArea.append(log + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        refresh();
    }

    @Override
    public void refresh() {
        // Lấy dữ liệu từ controller, không truy cập model trực tiếp!
        Soldier cur = controller.getCurrentSoldier();
        boolean isPlayer = (cur != null && controller.getPlayerTeam().contains(cur));

        // Cập nhật thông tin lượt và trạng thái
        turnLabel.setText("Lượt: " + (cur != null ? cur.getName() : "Hết trận"));
        if (controller.isCombatEnded()) {
            combatStatusLabel.setText(controller.isPlayerWin() ? "Bạn thắng!" : "Bạn thua!");
        } else {
            combatStatusLabel.setText(isPlayer ? "Đến lượt bạn" : "Đến lượt địch");
        }

        // Cập nhật danh sách mục tiêu
        targetBox.removeAllItems();
        List<Soldier> targets = isPlayer
                ? controller.getAlive(controller.getEnemyTeam())
                : controller.getAlive(controller.getPlayerTeam());
        for (Soldier s : targets) targetBox.addItem(s.getName());

        // Cập nhật danh sách vật phẩm
        itemBox.removeAllItems();
        if (cur != null) {
            for (Weapon w : cur.getWeapons()) {
                itemBox.addItem(w.getName());
            }
        }

        // Cập nhật danh sách đồng minh cho buff
        buffTargetBox.removeAllItems();
        for (Soldier s : controller.getAlive(controller.getPlayerTeam())) {
            buffTargetBox.addItem(s.getName());
        }

        // Cập nhật panel đội bạn
        playerPanel.removeAll();
        for (Soldier s : controller.getPlayerTeam()) {
            JLabel label = new JLabel(s.getName() + " HP: " + s.getHealth() + " " + (s.isAlive() ? "" : "(X)"));
            playerPanel.add(label);
        }
        playerPanel.revalidate();
        playerPanel.repaint();

        // Cập nhật panel đội địch
        enemyPanel.removeAll();
        for (Soldier s : controller.getEnemyTeam()) {
            JLabel label = new JLabel(s.getName() + " HP: " + s.getHealth() + " " + (s.isAlive() ? "" : "(X)"));
            enemyPanel.add(label);
        }
        enemyPanel.revalidate();
        enemyPanel.repaint();

        // Enable/disable nút theo trạng thái trận đấu
        boolean enable = cur != null && isPlayer && !controller.isCombatEnded();
        attackBtn.setEnabled(enable);
        itemBtn.setEnabled(enable);
        effectBtn.setEnabled(enable);
        buffBtn.setEnabled(enable);
        endTurnBtn.setEnabled(enable);
    }

    private Soldier getSelectedTarget() {
        String name = (String) targetBox.getSelectedItem();
        List<Soldier> targets = isPlayerTurn()
                ? controller.getAlive(controller.getEnemyTeam())
                : controller.getAlive(controller.getPlayerTeam());
        for (Soldier s : targets) if (s.getName().equals(name)) return s;
        return null;
    }

    private Soldier getSelectedBuffTarget() {
        String name = (String) buffTargetBox.getSelectedItem();
        List<Soldier> allies = controller.getAlive(controller.getPlayerTeam());
        for (Soldier s : allies) if (s.getName().equals(name)) return s;
        return null;
    }

    private Weapon getSelectedWeapon(Soldier cur) {
        String itemName = (String) itemBox.getSelectedItem();
        if (itemName == null || cur == null) return null;
        for (Weapon w : cur.getWeapons()) {
            if (w.getName().equals(itemName)) return w;
        }
        return null;
    }

    private boolean isPlayerTurn() {
        Soldier cur = controller.getCurrentSoldier();
        return (cur != null && controller.getPlayerTeam().contains(cur));
    }
}