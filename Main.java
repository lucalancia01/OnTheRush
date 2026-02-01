import controller.*;
import model.*;
import view.*;
import utils.Config;
import javax.swing.*;

/**
 * Entry point dell'applicazione.
 * Avvio su Event Dispatch Thread (best practice Swing).
 */
 class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppController().start());
    }
}
