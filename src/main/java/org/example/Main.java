package org.example;

import gamecore.GameController;
import ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        GameController controller = new GameController("troll_solo");
        new MainFrame(controller);
    }
}