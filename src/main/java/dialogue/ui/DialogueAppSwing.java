package dialogue.ui;

import dialogue.controller.DialogueController;
import dialogue.service.DialogueLoader;
import dialogue.model.DialogueContext;
import dialogue.service.DialogueService;
import javax.swing.WindowConstants;


import javax.swing.*;
import java.awt.*;

public class DialogueAppSwing {
    private DialogueController controller;
    private DialogueUIServiceSwing uiService;
    private JLabel speakerLabel;
    private JTextArea dialogueText;
    private JPanel optionsPanel;

    public DialogueAppSwing() {
        try {
            System.out.println("Loading dialogues from JSON...");
            var dialogues = DialogueLoader.loadFromJson("dialogue_data.json");
            System.out.println("Loaded " + dialogues.size() + " dialogues");

            var context = new DialogueContext();
            var service = new DialogueService(dialogues, context);

            controller = new SwingDialogueController(service);

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

            controller.start("start");

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        } catch (Exception e) {
            System.err.println("Error starting application:");
            JOptionPane.showMessageDialog(null, "Failed to start application: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DialogueAppSwing::new);
    }
}