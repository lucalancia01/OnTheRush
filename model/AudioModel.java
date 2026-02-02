package model;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;
import java.io.InputStream;

// Gestisce il sottofondo musicale dell applicazione
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

    // Caricamento dei file audio dal classpath
    public AudioModel(String fileA,String fileB) throws IOException, UnsupportedAudioFileException {
        
        this.clipA = loadClipFromResources("/audio/" + Objects.requireNonNull(fileA));
        this.clipB = loadClipFromResources("/audio/" + Objects.requireNonNull(fileB));

        // opzionale: parti “pronto” ma fermo
        stopInternal(this.clipA);
        stopInternal(this.clipB);
    }

    // Riproduce in loop continuo la traccia scelta
    public synchronized void play(Track track) {
        if (track == null) return;

        // se è già quella attiva e sta suonando, non fare nulla
        if (track == currentTrack && getCurrentClip() != null && getCurrentClip().isRunning()) {
            return;
        }

        // salva e stoppa la traccia
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

    // Pausa la traccia corrente salvando la posizione
    public synchronized void pause() {
        saveCurrentPosition();
        stopCurrentClip();
    }

    // Riprende la traccia corrente dallo stesso punto
    public synchronized void resume() {
        if (currentTrack == null) return;
        play(currentTrack);
    }

    // Muto senza chiudere lo stream audio
    public synchronized void setMuted(boolean muted) {
        this.muted = muted;
        Clip c = getCurrentClip();
        if (c != null) applyMuteToClip(c);
    }

    public synchronized boolean isMuted() {
        return muted;
    }

    //Ferma tutto e rilascia risorse
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

    // getters
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

    // applica muto
    private void applyMuteToClip(Clip clip) {
        if (clip == null) return;

        if (clip.isControlSupported(BooleanControl.Type.MUTE)) {
            BooleanControl muteCtrl =
                    (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            muteCtrl.setValue(muted);
        }
    }

    // caricamento risorse audio
    private Clip loadClipFromResources(String resourcePath)
            throws IOException, UnsupportedAudioFileException {

        try (InputStream is = Objects.requireNonNull(
                getClass().getResourceAsStream(resourcePath),
                "Risorsa non trovata: " + resourcePath
        );
            AudioInputStream ais = AudioSystem.getAudioInputStream(is)) {

            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.setMicrosecondPosition(0L);
            return clip;

        } catch (LineUnavailableException e) {
            throw new IOException("Linea audio non disponibile", e);
        }
    }
}
