package teambuilder.ui;


import effect.service.EffectService;
import equipment.model.*;
import equipment.service.EquipmentLoader;
import person.model.Soldier;
import teambuilder.controller.TeamBuilderController;
import teambuilder.model.MemberDef;
import teambuilder.model.TeamBuilderModel;
import teambuilder.model.TeamDef;
import ui.TeamSelectionListener;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TeamBuilderFrame extends JFrame {
    private final TeamBuilderModel model;
    private final TeamBuilderController controller;

    private final DefaultListModel<MemberDef> memberListModel = new DefaultListModel<>();
    private final JList<MemberDef> memberList = new JList<>(memberListModel);
    private final DefaultListModel<IComponent> equipListModel = new DefaultListModel<>();
    private final JList<IComponent> equipList = new JList<>(equipListModel);
    private final DefaultListModel<Soldier> chosenListModel = new DefaultListModel<>();
    private final JList<Soldier> chosenList = new JList<>(chosenListModel);

    private final JButton addBtn = new JButton("Thêm vào đội");
    private final JButton removeBtn = new JButton("Bỏ khỏi đội");
    private final JButton assignEquipBtn = new JButton("Gán trang bị cho thành viên");
    private final JButton confirmBtn = new JButton("Xác nhận đội hình");

    public TeamBuilderFrame(TeamSelectionListener callback, TeamBuilderModel model, TeamBuilderController controller, EffectService effectService) {
        this.model = model;
        this.controller = controller;
        setTitle("Chọn đội hình và trang bị");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));

        loadTeams();
        EquipmentLoader.setEffectService(effectService);
        loadEquipment();

        initUI();

        // Populate lists
        model.getAllTeams().forEach(team -> team.members.forEach(memberListModel::addElement));
        model.getAllEquipment().forEach(equipListModel::addElement);

        addBtn.addActionListener(e -> {
            if (chosenListModel.size() >= 3) {
                // Giới hạn số lượng thành viên trong đội
                JOptionPane.showMessageDialog(this, "Chỉ được chọn tối đa 3 thành viên cho đội!", "Giới hạn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            MemberDef selected = memberList.getSelectedValue();
            if (selected == null) return;
            controller.addMember(selected);
            updateChosenList();
        });

        removeBtn.addActionListener(e -> {
            Soldier selected = chosenList.getSelectedValue();
            if (selected == null) return;
            controller.removeMember(selected);
            updateChosenList();
        });

        assignEquipBtn.addActionListener(e -> {
            Soldier soldier = chosenList.getSelectedValue();
            IComponent equip = equipList.getSelectedValue();
            if (soldier == null || equip == null) return;
            List<IComponent> equips = model.getSoldierEquipmentMap().get(soldier.getName());
            //Giới hạn mỗi thành viên chỉ được trang bị tối đa n vật phẩm
            if (equips != null && equips.size() >= 1) {
                JOptionPane.showMessageDialog(this, "Mỗi thành viên chỉ được trang bị tối đa 1 vật phẩm!", "Giới hạn", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.assignEquipment(soldier, equip);
            JOptionPane.showMessageDialog(this, "Đã gán " + equip.getName() + " cho " + soldier.getName());
        });

        confirmBtn.addActionListener(e -> {
            controller.confirmTeam();
            List<Soldier> result = model.getChosenTeam();
            JOptionPane.showMessageDialog(this, "Đội hình đã xác nhận! Số thành viên: " + result.size());
            dispose();
            if (callback != null) {
                SwingUtilities.invokeLater(() -> callback.onTeamSelected(result));
            }
        });

        setVisible(true);
    }

    private void initUI() {
        JPanel leftPanel = createTitledPanel("Tất cả thành viên có thể chọn", new JScrollPane(memberList));
        JPanel midPanel = createTitledPanel("Đội hình đã chọn", new JScrollPane(chosenList));
        JPanel equipPanel = createTitledPanel("Tất cả trang bị", new JScrollPane(equipList));
        JPanel btnPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(assignEquipBtn);
        btnPanel.add(confirmBtn);

        add(leftPanel, BorderLayout.WEST);
        add(midPanel, BorderLayout.CENTER);
        add(equipPanel, BorderLayout.EAST);
        add(btnPanel, BorderLayout.SOUTH);

        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chosenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel createTitledPanel(String title, JComponent content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void updateChosenList() {
        chosenListModel.clear();
        model.getChosenTeam().forEach(chosenListModel::addElement);
    }

    public static IComponent cloneEquipment(IComponent equip, EffectService effectService) {
        if (equip instanceof Weapon weapon) {
            Weapon clone = new Weapon(weapon.getName(), weapon.getType(), weapon.getAtkPower(), effectService);
            clone.setEffects(weapon.getEffects());
            return clone;
        } else if (equip instanceof Armor armor) {
            Armor clone = new Armor(armor.getName(), armor.getArmorType(), armor.getDefPower());
            clone.setEffects(armor.getEffects());
            return clone;
        } else if (equip instanceof LuckyStone luckyStone) {
            IComponent wrapped = luckyStone.getComponent();
            IComponent wrappedClone = cloneEquipment(wrapped, effectService);
            if (wrappedClone != null) {
                LuckyStone clone = new LuckyStone(wrappedClone, effectService);
                clone.setEffects(luckyStone.getEffects());
                return clone;
            }
        }
        return null;
    }

    private void loadTeams() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("soldier_teams.json")) {
            if (is != null) {
                ObjectMapper mapper = new ObjectMapper();
                List<TeamDef> loaded = mapper.readValue(is, new TypeReference<List<TeamDef>>() {});
                model.getAllTeams().clear();
                model.getAllTeams().addAll(loaded);
            } else {
                model.getAllTeams().clear();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải đội hình: >" + e.getMessage());
            model.getAllTeams().clear();
        }
    }

    private void loadEquipment() {
        try {
            List<IComponent> loaded = new ArrayList<>(EquipmentLoader.loadEquipmentMap("equipment.json").values());
            model.getAllEquipment().clear();
            model.getAllEquipment().addAll(loaded);
        } catch (Exception e) {
            System.err.println("Lỗi khi tải trang bị: >" + e.getMessage());
            model.getAllEquipment().clear();
        }
    }
}