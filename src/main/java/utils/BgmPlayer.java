package utils;

import javax.sound.sampled.*;
import java.io.InputStream;

public class BgmPlayer {
    private Clip clip;

    public void play(String resourcePath, boolean loop) {
        stop();
        try {
            // Đọc file từ resources trong JAR/classpath
            InputStream audioSrc = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Không tìm thấy file nhạc: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            if (loop)
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            else
                clip.start();
        } catch (Exception e) {
            System.err.println("Không thể phát nhạc: " + e.getMessage());
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}