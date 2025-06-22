package utils;

import javax.sound.sampled.*;
import java.io.InputStream;

public class BgmPlayer {
    private static final BgmPlayer INSTANCE = new BgmPlayer();
    private Clip clip;
    private String lastResource;

    private BgmPlayer() {}

    public static BgmPlayer getInstance() {
        return INSTANCE;
    }

    public void play(String resourcePath, boolean loop) {
        // Nếu đang phát đúng nhạc này thì không phát lại
        if (resourcePath.equals(lastResource) && clip != null && clip.isActive()) return;
        stop();
        try {
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Không tìm thấy file nhạc: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            lastResource = resourcePath;
            if (loop)
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            else
                clip.start();
        } catch (Exception e) {
            System.err.println("Không thể phát nhạc: " + e.getMessage());
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        lastResource = null;
    }
}