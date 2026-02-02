package view;

import utils.Config;

import java.awt.*;

/**
* Qui vengono memorizzate le costanti UI in modo da accedervi facilmente
* La costante 1.778 garantisce un aspect ratio fisso di 9:16
 */
public class UiConstants {
    private static final Config CFG = Config.getInstance();

    public static final double GAME_ASPECT_RATIO = 1.778;

    public static final int WINDOW_SIZE = CFG.getWindowSize();

    // Dimensioni area di gioco
    public static final int GAME_H = WINDOW_SIZE;
    public static final int GAME_W = (int) Math.round(WINDOW_SIZE / GAME_ASPECT_RATIO);

    // Font
    public static final Font TITLE_FONT = new Font(CFG.getFontFamily(), Font.BOLD, CFG.getFontTitleSize());
    public static final Font UI_FONT = new Font(CFG.getFontFamily(), Font.PLAIN, CFG.getFontUiSize());
}
