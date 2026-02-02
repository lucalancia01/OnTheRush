package controller;

import view.MainFrame;

// Controller dedicato alla navigazione fra schermate (CardLayout)
public class NavigationController {
    private final MainFrame frame;

    public NavigationController(MainFrame frame) {
        this.frame = frame;
    }

    public void goTo(String screen) {
        frame.showScreen(screen);
    }
}
