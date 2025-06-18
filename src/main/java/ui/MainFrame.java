package ui;

import combat.CombatManager;
import equipment.model.Weapon;
import gamecore.GameController;
import person.model.Soldier;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainFrame extends JFrame implements Observer {
    private final GameController controller;
    private final CombatManager cm;
    private JLabel turnLabel, combatStatusLabel;
    private JTextArea logArea;
    private JPanel playerPanel, enemyPanel;
    private JButton attackBtn, itemBtn, effectBtn, buffBtn, endTurnBtn;
    private JComboBox<String> targetBox, itemBox, buffTargetBox;

    public MainFrame(GameController controller) {
        this.controller = controller;
        this.cm = controller.getCombatManager();
        controller.addObserver(this);

        setTitle("RPG Combat Demo (Modern UI)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020, 650);
        setLayout(new BorderLayout(10, 10));

        // TOP: Turn label
        turnLabel = new JLabel();
        turnLabel.setFont(new Font("Arial", Font.BOLD, 28));
        turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(turnLabel, BorderLayout.NORTH);

        // CENTER: Main Info Panels
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        playerPanel = new JPanel();
        enemyPanel = new JPanel();
        playerPanel.setBorder(new TitledBorder("Player Team"));
        enemyPanel.setBorder(new TitledBorder("Enemy Team"));
        mainPanel.add(playerPanel);
        mainPanel.add(enemyPanel);
        add(mainPanel, BorderLayout.CENTER);

        // RIGHT: Combat log
        logArea = new JTextArea(13, 38);
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new TitledBorder("Combat Log"));
        add(logScroll, BorderLayout.EAST);

        // LEFT: Actions
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(new TitledBorder("Actions"));

        // Target selection
        JLabel targetLabel = new JLabel("Chọn mục tiêu:");
        targetLabel.setFont(new Font("Arial", Font.BOLD, 14));
        targetBox = new JComboBox<>();
        targetBox.setMaximumSize(new Dimension(160, 25));
        actionPanel.add(targetLabel);
        actionPanel.add(targetBox);
        actionPanel.add(Box.createVerticalStrut(12));

        // Buff target selection (đồng đội)
        JLabel buffTargetLabel = new JLabel("Chọn người buff:");
        buffTargetLabel.setFont(new Font("Arial", Font.BOLD, 14));
        buffTargetBox = new JComboBox<>();
        buffTargetBox.setMaximumSize(new Dimension(160, 25));
        actionPanel.add(buffTargetLabel);
        actionPanel.add(buffTargetBox);
        actionPanel.add(Box.createVerticalStrut(10));


        // Item selection
        JLabel itemLabel = new JLabel("Chọn trang bị:");
        itemLabel.setFont(new Font("Arial", Font.BOLD, 14));
        itemBox = new JComboBox<>();
        itemBox.setMaximumSize(new Dimension(160, 25));
        actionPanel.add(itemLabel);
        actionPanel.add(itemBox);
        actionPanel.add(Box.createVerticalStrut(12));

        // Action buttons
        attackBtn = new JButton("Tấn công");
        itemBtn = new JButton("Dùng trang bị");
        effectBtn = new JButton("Thiêu đốt");
        buffBtn = new JButton("Buff đồng đội");
        endTurnBtn = new JButton("Kết thúc lượt");

        attackBtn.setMaximumSize(new Dimension(160, 36));
        itemBtn.setMaximumSize(new Dimension(160, 36));
        effectBtn.setMaximumSize(new Dimension(160, 36));
        endTurnBtn.setMaximumSize(new Dimension(160, 36));
        attackBtn.setFont(new Font("Arial", Font.BOLD, 14));
        itemBtn.setFont(new Font("Arial", Font.BOLD, 14));
        effectBtn.setFont(new Font("Arial", Font.BOLD, 14));
        endTurnBtn.setFont(new Font("Arial", Font.BOLD, 14));
        buffBtn.setFont(new Font("Arial", Font.BOLD, 14));

        actionPanel.add(attackBtn);
        actionPanel.add(Box.createVerticalStrut(7));
        actionPanel.add(itemBtn);
        actionPanel.add(Box.createVerticalStrut(7));
        actionPanel.add(effectBtn);
        actionPanel.add(Box.createVerticalStrut(7));
        actionPanel.add(endTurnBtn);
        actionPanel.add(Box.createVerticalStrut(7));
        actionPanel.add(buffBtn);
        actionPanel.add(Box.createVerticalStrut(7));




        add(actionPanel, BorderLayout.WEST);

        // BOTTOM: Combat status
        combatStatusLabel = new JLabel();
        combatStatusLabel.setFont(new Font("Arial", Font.BOLD, 17));
        combatStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(combatStatusLabel, BorderLayout.SOUTH);

        // Action listeners
        attackBtn.addActionListener(this::onAttack);
        itemBtn.addActionListener(this::onUseItem);
        effectBtn.addActionListener(this::onBurnEffect);
        buffBtn.addActionListener(this::onBuff);
        endTurnBtn.addActionListener(e -> controller.playerEndTurn());

        update(null, null); // Initial update
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        // Nếu có log mới
        if (arg instanceof String && ((String) arg).length() > 0) {
            logArea.append((String) arg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
        // Update toàn bộ UI
        Soldier cur = cm.getCurrentSoldier();
        boolean isPlayer = (cur != null && cm.getPlayerTeam().contains(cur));

        // Update team panels
        updateTeamPanel(playerPanel, cm.getPlayerTeam(), true);
        updateTeamPanel(enemyPanel, cm.getEnemyTeam(), false);

        // Cập nhật targetBox đối phương
        targetBox.removeAllItems();
        if (cur != null) {
            List<Soldier> targets = cm.getPlayerTeam().contains(cur)
                    ? controller.getAlive(cm.getEnemyTeam())
                    : controller.getAlive(cm.getPlayerTeam());
            for (Soldier s : targets) targetBox.addItem(s.getName());
        }

        // Cập nhật buffTargetBox (đồng đội)
        buffTargetBox.removeAllItems();
        if (cur != null && cm.getPlayerTeam().contains(cur)) {
            List<Soldier> allies = controller.getAlive(cm.getPlayerTeam());
            for (Soldier s : allies) buffTargetBox.addItem(s.getName());
        }

        // Cập nhật itemBox
        itemBox.removeAllItems();
        if (cur != null && cm.getPlayerTeam().contains(cur)) {
            for (Weapon w : cur.getWeapons()) itemBox.addItem(w.getName());
        }

        // Update trạng thái trận đấu
        if (cm.isCombatEnded()) {
            attackBtn.setEnabled(false);
            itemBtn.setEnabled(false);
            effectBtn.setEnabled(false);
            endTurnBtn.setEnabled(false);
            if (cm.isPlayerWin()) {
                combatStatusLabel.setText("You win!");
            } else {
                combatStatusLabel.setText("You fck off.");
            }
            turnLabel.setText("Combat Ended.");
        } else {
            turnLabel.setText("Turn: " + (cur != null ? cur.getName() : "-"));
            combatStatusLabel.setText("");
            attackBtn.setEnabled(isPlayer);
            itemBtn.setEnabled(isPlayer);
            effectBtn.setEnabled(isPlayer);
            endTurnBtn.setEnabled(true);
        }
    }

    private void updateTeamPanel(JPanel panel, List<Soldier> team, boolean isPlayer) {
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Soldier s : team) {
            JLabel label = new JLabel(String.format("%s [HP: %d]", s.getName(), s.getHealth()));
            label.setFont(new Font("Arial", Font.BOLD, 15));
            if (!s.isAlive()) {
                label.setForeground(Color.GRAY);
                label.setText(label.getText() + " (DEAD)");
            } else {
                label.setForeground(isPlayer ? new Color(25, 94, 209) : new Color(192, 52, 36));
            }
            panel.add(label);
        }
        panel.revalidate();
        panel.repaint();
    }

    private Soldier getTarget() {
        String name = (String) targetBox.getSelectedItem();
        Soldier cur = cm.getCurrentSoldier();
        List<Soldier> targets = cm.getPlayerTeam().contains(cur)
                ? controller.getAlive(cm.getEnemyTeam())
                : controller.getAlive(cm.getPlayerTeam());
        for (Soldier s : targets) if (s.getName().equals(name)) return s;
        return null;
    }

    private void onBuff(ActionEvent e) {
        Soldier buffTarget = getBuffTarget();
        if (buffTarget != null) {
            controller.playerBuff(buffTarget);
        }
    }

    private Soldier getBuffTarget() {
        String name = (String) buffTargetBox.getSelectedItem();
        List<Soldier> allies = controller.getAlive(cm.getPlayerTeam());
        for (Soldier s : allies) if (s.getName().equals(name)) return s;
        return null;
    }


    private Weapon getSelectedWeapon(Soldier cur) {
        String itemName = (String) itemBox.getSelectedItem();
        if (itemName == null) return null;
        for (Weapon w : cur.getWeapons()) {
            if (w.getName().equals(itemName)) return w;
        }
        return null;
    }

    private void onAttack(ActionEvent e) {
        Soldier cur = cm.getCurrentSoldier();
        Soldier target = getTarget();
        if (cur != null && target != null) {
            controller.playerAttack(cur, target);
        }
    }

    private void onUseItem(ActionEvent e) {
        Soldier cur = cm.getCurrentSoldier();
        Soldier target = getTarget();
        if (cur == null || target == null) return;
        Weapon selected = getSelectedWeapon(cur);
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Item not found.");
            return;
        }
        controller.playerUseItem(cur, target, selected);
    }

    private void onBurnEffect(ActionEvent e) {
        Soldier target = getTarget();
        if (target != null) {
            controller.playerBurn(target);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(new GameController()));
    }
}