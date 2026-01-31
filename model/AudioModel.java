package model; // meglio: service.audio o infrastructure.audio

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;


public class AudioModel {

    public enum Track {
        A, B
    }

    private Clip clipA;
    private Clip clipB;

    private Track currentTrack = null;

    // Posizione (per resume) in microsecondi
    private long posA = 0L;
    private long posB = 0L;

    private boolean muted = false;

    /**
     * I file devono stare in classpath: /audio/<nomefile>
     * quindi tipicamente: src/main/resources/audio/menu.wav
     */
    public AudioModel(String fileA,String fileB) throws IOException, UnsupportedAudioFileException {
        Properties props = System.getProperties();
        String userDir = props.getProperty("user.dir");
        
        this.clipA = loadClipFromResources(userDir + "\\resources\\audio\\" + Objects.requireNonNull(fileA));
        this.clipB = loadClipFromResources(userDir + "\\resources\\audio\\" + Objects.requireNonNull(fileB));

        // opzionale: parti “pronto” ma fermo
        stopInternal(this.clipA);
        stopInternal(this.clipB);
    }

    /**
     * Riproduce in loop continuo la traccia scelta, senza stacchi “strani”:
     * - salva posizione dell’altra
     * - ferma l’altra
     * - riparte dalla posizione salvata della nuova (o da 0 se mai partita)
     */
    public synchronized void play(Track track) {
        if (track == null) return;

        // se è già quella attiva e sta suonando, non fare nulla
        if (track == currentTrack && getCurrentClip() != null && getCurrentClip().isRunning()) {
            return;
        }

        // salva e stoppa la corrente
        saveCurrentPosition();
        stopCurrentClip();

        // attiva la nuova
        currentTrack = track;
        Clip clip = getCurrentClip();

        if (clip == null) return;

        // riposiziona al punto salvato e vai in loop
        long pos = (track == Track.A) ? posA : posB;
        pos = Math.max(0L, Math.min(pos, clip.getMicrosecondLength()));
        clip.setMicrosecondPosition(pos);

        applyMuteToClip(clip);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /** Pausa la traccia corrente salvando la posizione (utile quando cambi panel). */
    public synchronized void pause() {
        saveCurrentPosition();
        stopCurrentClip(); // stop mantiene la posizione corrente, ma noi la salviamo esplicitamente
    }

    /** Riprende la traccia corrente dallo stesso punto, in loop continuo. */
    public synchronized void resume() {
        if (currentTrack == null) return;
        play(currentTrack);
    }

    /** Muto/Unmuto senza chiudere: migliore di close() per un pulsante mute. */
    public synchronized void setMuted(boolean muted) {
        this.muted = muted;
        Clip c = getCurrentClip();
        if (c != null) applyMuteToClip(c);
    }

    public synchronized boolean isMuted() {
        return muted;
    }

    /** Ferma tutto e rilascia risorse. Dopo close() l’istanza non va riusata. */
    public synchronized void close() {
        if (clipA != null) {
            clipA.stop();
            clipA.close();
            clipA = null;
        }
        if (clipB != null) {
            clipB.stop();
            clipB.close();
            clipB = null;
        }
        currentTrack = null;
        posA = 0L;
        posB = 0L;
    }

    // -------------------- Helpers --------------------

    private Clip getCurrentClip() {
        if (currentTrack == Track.A) return clipA;
        if (currentTrack == Track.B) return clipB;
        return null;
    }

    private void stopCurrentClip() {
        Clip c = getCurrentClip();
        if (c != null && c.isOpen()) {
            c.stop();
        }
    }

    private void saveCurrentPosition() {
        Clip c = getCurrentClip();
        if (c == null || !c.isOpen()) return;

        long pos = c.getMicrosecondPosition();
        if (currentTrack == Track.A) posA = pos;
        if (currentTrack == Track.B) posB = pos;
    }

    private void stopInternal(Clip clip) {
        if (clip != null && clip.isOpen()) {
            clip.stop();
            clip.setMicrosecondPosition(0L);
        }
    }

    private void applyMuteToClip(Clip clip) {
        if (clip == null) return;

        // se c'è MASTER_GAIN, abbassiamo il volume; altrimenti fallback su MUTE (se disponibile)
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (muted) {
                gain.setValue(gain.getMinimum());
            } else {
                // 0 dB = volume “normale”
                float normal = Math.min(0f, gain.getMaximum());
                gain.setValue(normal);
            }
        } else if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl muteCtrl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            muteCtrl.setValue(muted);
        }
    }

    private Clip loadClipFromResources(String resourcePath) throws IOException, UnsupportedAudioFileException {
        // Carica dal classpath (quindi funziona anche se impacchetti in jar)
        try (AudioInputStream aisOriginal =
                     AudioSystem.getAudioInputStream(Objects.requireNonNull(
                             getClass().getResource(resourcePath),
                             "Risorsa non trovata: " + resourcePath
                     ))) {

            // A volte i wav non sono PCM e Clip può dare problemi: decodifica a PCM_SIGNED
            AudioFormat baseFormat = aisOriginal.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (AudioInputStream aisDecoded = AudioSystem.getAudioInputStream(decodedFormat, aisOriginal)) {
                DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat);
                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(aisDecoded);
                clip.setMicrosecondPosition(0L);
                return clip;
            } catch (LineUnavailableException e) {
                throw new IOException("Linea audio non disponibile", e);
            }
        }
    }
}
