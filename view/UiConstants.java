package view;

import utils.Config;

import java.awt.*;

/**
 * Costanti UI:
 * - la finestra è quadrata e ha una sola dimensione configurabile (ui.window_size)
 * - l'area di gioco (GAME_W/H) è calcolata automaticamente con aspect ratio fisso:
 *      GAME_H = WINDOW_SIZE
 *      GAME_W = WINDOW_SIZE / 1.778
 *
 * NOTA: 1.778 è una costante hard-coded (aspect ratio NON modificabile da config).
 */
public class UiConstants {
    private static final Config CFG = Config.getInstance();

    // Aspect ratio FISSO (non configurabile)
    // user requirement: game_w = window_w / 1.778  => (game_h / game_w = 1.778)
    public static final double GAME_ASPECT_RATIO_H_OVER_W = 1.778;

    // Dimensione finestra QUADRATA configurabile
    public static final int WINDOW_SIZE = CFG.getWindowSize();
    public static final int WINDOW_W = WINDOW_SIZE;
    public static final int WINDOW_H = WINDOW_SIZE;

    // Dimensioni logiche area di gioco (calcolate, NON modificabili direttamente)
    public static final int GAME_H = WINDOW_SIZE;
    public static final int GAME_W = (int) Math.round(WINDOW_SIZE / GAME_ASPECT_RATIO_H_OVER_W);

    // Font (configurabili)
    public static final Font TITLE_FONT = new Font(CFG.getFontFamily(), Font.BOLD, CFG.getFontTitleSize());
    public static final Font UI_FONT = new Font(CFG.getFontFamily(), Font.PLAIN, CFG.getFontUiSize());
}
