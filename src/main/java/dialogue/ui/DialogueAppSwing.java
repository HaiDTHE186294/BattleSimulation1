package dialogue.ui;

import dialogue.controller.DialogueController;
import dialogue.service.DialogueService;
import utils.BgmPlayer;

import javax.swing.WindowConstants;
import javax.swing.*;
import java.awt.*;

public class DialogueAppSwing {
    private final DialogueController controller;
    private DialogueUIServiceSwing uiService;
    private JLabel speakerLabel;
    private JTextArea dialogueText;
    private JPanel optionsPanel;


    /**
     * Khởi tạo UI hội thoại với controller và service có sẵn (KHÔNG tự nạp lại JSON, không tạo DialogueService mới!)
     */
    public DialogueAppSwing(DialogueController controller, DialogueService service, String startDialogueId) {
        this.controller = controller;
        try {
            JFrame frame = new JFrame("Hội thoại Swing");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(480, 340);

            speakerLabel = new JLabel();
            dialogueText = new JTextArea(6, 32);
            dialogueText.setLineWrap(true);
            dialogueText.setWrapStyleWord(true);
            dialogueText.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(dialogueText);

            optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridLayout(0, 1, 4, 4)); // 1 cột, nhiều dòng, khoảng cách 4px

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout(10, 10));
            mainPanel.add(speakerLabel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            mainPanel.add(optionsPanel, BorderLayout.SOUTH);

            frame.setContentPane(mainPanel);

            uiService = new DialogueUIServiceSwing(speakerLabel, dialogueText, optionsPanel, controller);
            service.addObserver(uiService);
            controller.start(startDialogueId);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error starting application:");
            JOptionPane.showMessageDialog(null, "Failed to start application: " + e.getMessage());
        }
    }



    /**
     * KHÔNG dùng main này để chạy thật! Chỉ để test độc lập UI, không liên kết với game.
     */
    public static void main(String[] args) {
        // Test UI độc lập, không liên kết với game
        SwingUtilities.invokeLater(() -> {
            try {
                var dialogues = dialogue.service.DialogueLoader.loadFromJson("dialogue_data.json");
                var context = new dialogue.model.DialogueContext();
                DialogueService.init(dialogues, context);
                DialogueService service = DialogueService.getInstance();
                var controller = new dialogue.ui.SwingDialogueController(service);
                new DialogueAppSwing(controller, service, "start");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to start test UI: " + e.getMessage());
            }
        });
    }
}