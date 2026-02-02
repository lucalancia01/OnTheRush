package controller;

import controller.AppController;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppController().start());
    }

}   