package dialogue.ui;

import dialogue.controller.DialogueController;
import dialogue.service.DialogueObserver;
import dialogue.service.DialogueService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogueUIServiceSwing implements DialogueObserver {
    private final JLabel speakerLabel;
    private final JTextArea dialogueText;
    private final JPanel optionsPanel;
    private final DialogueController controller;

    public DialogueUIServiceSwing(JLabel speakerLabel, JTextArea dialogueText, JPanel optionsPanel, DialogueController controller) {
        this.speakerLabel = speakerLabel;
        this.dialogueText = dialogueText;
        this.optionsPanel = optionsPanel;
        this.controller = controller;
    }

    @Override
    public void onDialogueUpdated(DialogueService service) {
        updateUI(service);
    }

    private void updateUI(DialogueService service) {
        var current = service.getCurrentDialogue();
        optionsPanel.removeAll();

        if (current == null) {
            speakerLabel.setText("Kết thúc hội thoại.");
            dialogueText.setText("");
            optionsPanel.revalidate();
            optionsPanel.repaint();
            return;
        }

        speakerLabel.setText("[" + current.getSpeaker() + "]");
        dialogueText.setText(current.getText());

        var options = service.getCurrentOptions();
        for (int i = 0; i < options.size(); i++) {
            var opt = options.get(i);
            JButton btn = new JButton(opt.getText());
            int idx = i;
            btn.addActionListener(e -> controller.onUserInput(idx));
            optionsPanel.add(btn);
        }
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
}