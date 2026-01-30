package utils;

import java.io.*;
import java.util.*;

/**
 * Config singleton (simile al file di input):
 * - carica un file properties testuale: conf/config.txt
 * - espone metodi tipizzati (int/boolean/String)
 * - salva modifiche persistenti
 *
 * Nel file conf/config.txt vengono memorizzati:
 * - costanti UI (dimensioni e font)
 * - stato player (coins, skin, nome, extraLives)
 * - impostazioni (soundEnabled)
 * - leaderboard (top 10) nello stesso file
 */
public class Config {

    private static Config instance = null;

    private Properties properties;
    private String configFilePath;

    //---------------------------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------------------------
    private Config() {
        this.configFilePath = getConfigFile();
        loadOrCreate();
    }


    //---------------------------------------------------------------
    // PRIVATE METHODS
    //---------------------------------------------------------------

    // Carica conf/config.txt; se non esiste lo crea con valori di default.
    private void loadOrCreate() {
        try {
            File f = new File(this.configFilePath);

            // Se non esiste: crea cartella conf/ e file con default
            if (!f.exists()) {
                File parent = f.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();

                this.properties = new Properties();
                setDefaults();
                save(); // crea fisicamente il file
                return;
            }

            // Se esiste: carica
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(f), "ISO-8859-1")
            );
            this.properties = new Properties();
            this.properties.load(br);
            br.close();

            // Se mancano chiavi essenziali, le aggiunge senza rompere il file
            ensureDefaultsPresent();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            // In caso di errore: inizializza comunque
            this.properties = new Properties();
            setDefaults();
        }
    }

    /**
     * Imposta TUTTI i default (prima creazione).
     */
    private void setDefaults() {
        // UI: UNA sola dimensione (finestra quadrata)
        properties.setProperty("ui.window_size", "720");

        properties.setProperty("ui.font_family", "SansSerif");
        properties.setProperty("ui.font_title_size", "28");
        properties.setProperty("ui.font_ui_size", "16");

        // Player
        properties.setProperty("player.name", "Player");
        properties.setProperty("player.coins", "0");
        properties.setProperty("player.vehicleSkin", "DEFAULT");
        properties.setProperty("player.extraLives", "0");
        
        // Shop / Costi
        properties.setProperty("shop.skin.DEFAULT", "0");
        properties.setProperty("shop.skin.RED", "50");
        properties.setProperty("shop.skin.BLUE", "50");
        properties.setProperty("shop.skin.GOLD", "150");
        properties.setProperty("shop.extraLife.cost", "120");

        // Settings
        properties.setProperty("settings.soundEnabled", "true");

        // Leaderboard
        properties.setProperty("leaderboard.count", "0");
    }


    /**
     * Se il file esiste ma mancano alcune chiavi, le aggiunge con default.
     */
    private void ensureDefaultsPresent() {
        ensureKey("ui.window_size", "720");

        ensureKey("ui.font_family", "SansSerif");
        ensureKey("ui.font_title_size", "28");
        ensureKey("ui.font_ui_size", "16");

        ensureKey("player.name", "Player");
        ensureKey("player.coins", "0");
        ensureKey("player.vehicleSkin", "DEFAULT");
        ensureKey("player.extraLives", "0");

        ensureKey("shop.skin.DEFAULT", "0");
        ensureKey("shop.skin.RED", "50");
        ensureKey("shop.skin.BLUE", "50");
        ensureKey("shop.skin.GOLD", "150");
        ensureKey("shop.extraLife.cost", "120");

        ensureKey("settings.soundEnabled", "true");

        ensureKey("leaderboard.count", "0");
    }

    //serve per verificare se c'è il campo corrispondente e in caso negativo lo crea
    private void ensureKey(String key, String defaultValue) {
        if (properties.getProperty(key) == null) {
            properties.setProperty(key, defaultValue);
        }
    }

    //serve quando si ha bisogno del percorso relativo
    private String getConfigFile() {
        // Percorso relativo (portabile)
        return "conf" + File.separator + "config.txt";
    }

    // Ripristina SOLO impostazioni/UI (non tocca player/coins/skin/extraLives/leaderboard)
    public void resetUiAndSettingsToDefaults() {
        // UI
        setInt("ui.window_size", 720);
        setString("ui.font_family", "SansSerif");
        setInt("ui.font_title_size", 28);
        setInt("ui.font_ui_size", 16);

        setString("player.name", "Player");
        setInt("player.coins", 0);
        setString("player.vehicleSkin", "DEFAULT");
        setInt("player.extraLives", 0);

        setInt("shop.skin.DEFAULT", 0);
        setInt("shop.skin.RED", 50);
        setInt("shop.skin.BLUE", 50);
        setInt("shop.skin.GOLD", 150);
        setInt("shop.extraLife.cost", 120);

        // Settings
        setBool("settings.soundEnabled", true);

        // salva immediatamente
        save();
    }

    /**
     * Cancella SOLO la leaderboard (nel file unico).
     */
    public void resetLeaderboard() {
        int oldCount = getInt("leaderboard.count", 0);
        for (int i = 0; i < oldCount; i++) {
            properties.remove("leaderboard." + i);
        }
        properties.setProperty("leaderboard.count", "0");
        save();
    }

    //---------------------------------------------------------------
    // PUBLIC METHODS: SAVE/GETTERS/SETTERS
    //---------------------------------------------------------------

    /**
     * Salva su conf/config.txt.
     */
    public synchronized void save() {
        try {
            File f = new File(this.configFilePath);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            OutputStream os = new FileOutputStream(f);
            properties.store(os, "ON THE RUSH - CONFIG");
            os.close();
        } catch (IOException e) {
            throw new RuntimeException("Impossibile salvare config: " + e.getMessage(), e);
        }
    }

    // --- helper tipizzati ---
    public int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    public int getInt(String key, int def) {
        String v = properties.getProperty(key);
        return (v == null) ? def : Integer.parseInt(v);
    }

    public boolean getBool(String key, boolean def) {
        String v = properties.getProperty(key);
        return (v == null) ? def : v.trim().equalsIgnoreCase("true");
    }

    public String getString(String key, String def) {
        String v = properties.getProperty(key);
        return (v == null) ? def : v;
    }

    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }

    public void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }

    public void setBool(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }

    // -------------------------------------------------------------
    // API SPECIFICA DEL GIOCO (getter/setter "comodi")
    // -------------------------------------------------------------

    // UI
    public int getWindowSize() {
        int size = getInt("ui.window_size", 720);
        // sanity: minimo sensato per non rompere layout
        if (size < 400) size = 400;
        if (size > 2000) size = 2000;
        return size;
    }

    public void setWindowSize(int size) {
        if (size < 400) size = 400;
        if (size > 2000) size = 2000;
        setInt("ui.window_size", size);
    }

    public String getFontFamily() { return getString("ui.font_family", "SansSerif"); }
    public int getFontTitleSize() { return getInt("ui.font_title_size", 28); }
    public int getFontUiSize() { return getInt("ui.font_ui_size", 16); }

    // Player state
    public String getPlayerName() { return getString("player.name", "Player"); }
    public void setPlayerName(String name) {
        if (name == null) name = "Player";
        name = name.trim();
        if (name.isEmpty()) name = "Player";
        setString("player.name", name);
    }

    public int getPlayerCoins() { return getInt("player.coins", 0); }
    public void setPlayerCoins(int coins) { setInt("player.coins", Math.max(0, coins)); }

    public String getVehicleSkin() { return getString("player.vehicleSkin", "DEFAULT"); }
    public void setVehicleSkin(String skinName) { setString("player.vehicleSkin", skinName); }

    public int getExtraLives() { return getInt("player.extraLives", 0); }
    public void setExtraLives(int extraLives) { setInt("player.extraLives", Math.max(0, extraLives)); }

    // Costs
    /**
     * Ritorna il costo configurato di una skin (shop.skin.<NOME_ENUM>).
     * Se manca, ritorna 0.
     */
    public int getSkinCost(String skinEnumName) {
        if (skinEnumName == null) return 0;
        return getInt("shop.skin." + skinEnumName, 0);
    }
    
    /**
     * Costo configurato per l'upgrade vita extra.
     */
    public int getExtraLifeCost() {
        return getInt("shop.extraLife.cost", 120);
    }


    // Settings
    public boolean isSoundEnabled() { return getBool("settings.soundEnabled", true); }
    public void setSoundEnabled(boolean enabled) { setBool("settings.soundEnabled", enabled); }

    // -------------------------------------------------------------
    // LEADERBOARD dentro lo stesso file
    // -------------------------------------------------------------

    /**
     * Restituisce le entry salvate come lista di righe nel formato:
     * Nome;score;coinsRun;timestamp
     */
    public List<String> getLeaderboardLines() {
        int count = getInt("leaderboard.count", 0);
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String line = properties.getProperty("leaderboard." + i);
            if (line != null && !line.isBlank()) lines.add(line.trim());
        }
        return lines;
    }

    /**
     * Sovrascrive la leaderboard mantenendo i primi maxEntries.
     */
    private void setLeaderboardLines(List<String> lines, int maxEntries) {
        // pulizia vecchie chiavi
        int oldCount = getInt("leaderboard.count", 0);
        for (int i = 0; i < oldCount; i++) {
            properties.remove("leaderboard." + i);
        }

        int newCount = Math.min(lines.size(), maxEntries);
        for (int i = 0; i < newCount; i++) {
            properties.setProperty("leaderboard." + i, lines.get(i));
        }
        properties.setProperty("leaderboard.count", String.valueOf(newCount));
    }

    /**
     * Aggiunge una entry, riordina per score decrescente e mantiene TOP 10.
     */
    public void addLeaderboardEntry(String name, int score, int coinsRun, long timestamp) {
        // sanitizzazione nome per evitare rompere formato ';'
        if (name == null || name.isBlank()) name = "Player";
        name = name.trim().replace(";", " ");

        List<String> lines = getLeaderboardLines();
        lines.add(name + ";" + score + ";" + coinsRun + ";" + timestamp);

        // ordina per score desc
        lines.sort((a, b) -> {
            int sa = parseScore(a);
            int sb = parseScore(b);
            return Integer.compare(sb, sa);
        });

        // salva top 10
        setLeaderboardLines(lines, 10);
    }

    private int parseScore(String line) {
        try {
            String[] p = line.split(";");
            return Integer.parseInt(p[1]);
        } catch (Exception e) {
            return -1;
        }
    }

    //---------------------------------------------------------------
    // STATIC METHOD
    //---------------------------------------------------------------
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }
}
