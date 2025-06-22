package ui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import effect.service.EffectService;
import equipment.model.IComponent;
import equipment.model.Weapon;
import equipment.model.Armor;
import equipment.model.LuckyStone;
import equipment.service.EquipmentLoader;
import person.model.PersonStatus;
import person.model.Soldier;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class TeamBuilderFrame extends JFrame {
    // Data loaded from JSON
    private List<TeamDef> allTeams;
    private List<IComponent> allEquipment;

    // UI
    private DefaultListModel<MemberDef> memberListModel = new DefaultListModel<>();
    private JList<MemberDef> memberList = new JList<>(memberListModel);
    private DefaultListModel<IComponent> equipListModel = new DefaultListModel<>();
    private JList<IComponent> equipList = new JList<>(equipListModel);
    private DefaultListModel<Soldier> chosenListModel = new DefaultListModel<>();
    private JList<Soldier> chosenList = new JList<>(chosenListModel);

    private JButton addBtn = new JButton("Thêm vào đội");
    private JButton removeBtn = new JButton("Bỏ khỏi đội");
    private JButton assignEquipBtn = new JButton("Gán trang bị cho thành viên");
    private JButton confirmBtn = new JButton("Xác nhận đội hình");

    // State
    private Map<String, List<IComponent>> soldierEquipmentMap = new HashMap<>();
    private final EffectService effectService;

    public TeamBuilderFrame(TeamSelectionListener callback, EffectService effectService) {
        this.effectService = effectService;
        setTitle("Chọn đội hình và trang bị");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));

        // Load data
        loadTeams();
        // Set EffectService cho EquipmentLoader trước khi load equipment
        EquipmentLoader.setEffectService(effectService);
        loadEquipment();

        // UI setup
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Tất cả thành viên có thể chọn"));
        memberList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(memberList), BorderLayout.CENTER);

        JPanel midPanel = new JPanel(new BorderLayout());
        midPanel.setBorder(BorderFactory.createTitledBorder("Đội hình đã chọn"));
        chosenList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        midPanel.add(new JScrollPane(chosenList), BorderLayout.CENTER);

        JPanel equipPanel = new JPanel(new BorderLayout());
        equipPanel.setBorder(BorderFactory.createTitledBorder("Tất cả trang bị"));
        equipList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipPanel.add(new JScrollPane(equipList), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new GridLayout(0,1,5,5));
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        btnPanel.add(assignEquipBtn);
        btnPanel.add(confirmBtn);

        add(leftPanel, BorderLayout.WEST);
        add(midPanel, BorderLayout.CENTER);
        add(equipPanel, BorderLayout.EAST);
        add(btnPanel, BorderLayout.SOUTH);

        // Populate member list
        for (TeamDef team : allTeams) {
            for (MemberDef mem : team.members) memberListModel.addElement(mem);
        }
        // Populate equipment list
        for (IComponent c : allEquipment) equipListModel.addElement(c);

        // Event: Thêm thành viên vào đội
        addBtn.addActionListener(e -> {
            MemberDef sel = memberList.getSelectedValue();
            if (sel == null) return;
            // Clone Soldier tránh trùng
            Soldier s = new Soldier(sel.name, sel.hp, PersonStatus.valueOf(sel.status), sel.atk, sel.def);
            chosenListModel.addElement(s);
            soldierEquipmentMap.put(s.getName(), new ArrayList<>());
        });

        // Event: Bỏ khỏi đội
        removeBtn.addActionListener(e -> {
            Soldier sel = chosenList.getSelectedValue();
            if (sel == null) return;
            chosenListModel.removeElement(sel);
            soldierEquipmentMap.remove(sel.getName());
        });

        // Gán trang bị cho thành viên
        assignEquipBtn.addActionListener(e -> {
            Soldier selSoldier = chosenList.getSelectedValue();
            IComponent selEquip = equipList.getSelectedValue();
            if (selSoldier == null || selEquip == null) return;
            List<IComponent> equips = soldierEquipmentMap.get(selSoldier.getName());
            if (equips == null) return;

            // Clone trang bị trước khi gán
            if (selEquip instanceof Weapon weapon) {
                Weapon clonedWeapon = new Weapon(weapon.getName(), weapon.getType(), weapon.getAtkPower(), effectService);
                clonedWeapon.setEffects(weapon.getEffects());
                equips.add(clonedWeapon);
            } else if (selEquip instanceof Armor armor) {
                Armor clonedArmor = new Armor(armor.getName(), armor.getArmorType(), armor.getDefPower());
                clonedArmor.setEffects(armor.getEffects());
                equips.add(clonedArmor);
            } else if (selEquip instanceof LuckyStone luckyStone) {
                // Clone wrapped component first
                IComponent wrappedClone = null;
                IComponent wrapped = luckyStone.getComponent();
                if (wrapped instanceof Weapon weapon) {
                    Weapon clonedWeapon = new Weapon(weapon.getName(), weapon.getType(), weapon.getAtkPower(), effectService);
                    clonedWeapon.setEffects(weapon.getEffects());
                    wrappedClone = clonedWeapon;
                } else if (wrapped instanceof Armor armor) {
                    Armor clonedArmor = new Armor(armor.getName(), armor.getArmorType(), armor.getDefPower());
                    clonedArmor.setEffects(armor.getEffects());
                    wrappedClone = clonedArmor;
                }
                if (wrappedClone != null) {
                    LuckyStone clonedStone = new LuckyStone(wrappedClone, effectService);
                    clonedStone.setEffects(luckyStone.getEffects());
                    equips.add(clonedStone);
                }
            }

            JOptionPane.showMessageDialog(this, "Đã gán "+selEquip.getName()+" cho "+selSoldier.getName());
        });

        // Xác nhận đội hình
        confirmBtn.addActionListener(e -> {
            // Gán trang bị vào Soldier object
            for (int i=0; i<chosenListModel.size(); ++i) {
                Soldier s = chosenListModel.get(i);
                List<IComponent> equips = soldierEquipmentMap.getOrDefault(s.getName(), new ArrayList<>());
                for (IComponent eq : equips) s.equip(eq);
            }
            List<Soldier> result = Collections.list(chosenListModel.elements());
            JOptionPane.showMessageDialog(this, "Đội hình đã xác nhận! Số thành viên: "+result.size());
            dispose();
            if (callback != null) {
                // Đảm bảo callback được gọi trong EDT
                SwingUtilities.invokeLater(() -> callback.onTeamSelected(result));
            }
        });

        setVisible(true);
    }

    // ===== DATA STRUCTS AND LOADERS =====
    public static class TeamDef {
        public String id;
        public String name;
        public List<MemberDef> members;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MemberDef {
        public String name;
        public int hp;
        public String status;
        public int atk;
        public int def;
        public List<Map<String,Object>> equipment;
        @Override public String toString() {
            return name + " (HP:"+hp+", ATK:"+atk+", DEF:"+def+")";
        }
    }

    private void loadTeams() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("soldier_teams.json");
            allTeams = mapper.readValue(is, new TypeReference<List<TeamDef>>() {});
        } catch (Exception e) {
            System.err.println("Lỗi khi tải đội hình: >" + e.getMessage());
            allTeams = new ArrayList<>();
        }
    }

    private void loadEquipment() {
        try {
            allEquipment = new ArrayList<>(EquipmentLoader.loadEquipmentMap("equipment.json").values());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải trang bị: >" + e.getMessage());
            allEquipment = new ArrayList<>();
        }
    }

}