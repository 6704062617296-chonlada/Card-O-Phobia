package loadingScene;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * หน้า Loading ของเกม Card-O-Phobia
 *
 * Assets ที่ต้องมี:
 * assets/Loading.png
 *
 * เรียก startLoading() หลังจากเปลี่ยนมาแสดง LoadingScene
 */
public class LoadingScene extends JPanel {

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    // ระยะเวลาแสดงหน้า Loading (มิลลิวินาที)
    private static final int LOADING_DURATION = 2500;

    private final Runnable onFinished;

    private BufferedImage backgroundImage;
    private Timer loadingTimer;

    public LoadingScene(Runnable onFinished) {
        this.onFinished = onFinished;

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setFocusable(true);

        loadAssets();
    }

    private void loadAssets() {
        try {
            backgroundImage = ImageIO.read(
                    new File("assets/Loading.png")
            );

            System.out.println("Loading asset loaded successfully.");

        } catch (IOException e) {
            System.err.println("Cannot load Loading.png.");
            e.printStackTrace();
        }
    }

    /**
     * เริ่มนับเวลาหน้า Loading
     * ต้องเรียกเมธอดนี้หลังจากแสดง LoadingScene แล้ว
     */
    public void startLoading() {
        stopLoading();

        loadingTimer = new Timer(LOADING_DURATION, e -> {
            stopLoading();

            if (onFinished != null) {
                onFinished.run();
            }
        });

        loadingTimer.setRepeats(false);
        loadingTimer.start();
    }

    /**
     * หยุด Timer กรณีออกจากหน้า Loading ก่อนครบเวลา
     */
    public void stopLoading() {
        if (loadingTimer != null) {
            loadingTimer.stop();
            loadingTimer = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.scale(
                (double) getWidth() / GAME_WIDTH,
                (double) getHeight() / GAME_HEIGHT
        );

        if (backgroundImage != null) {
            g2.drawImage(
                    backgroundImage,
                    0, 0,
                    GAME_WIDTH, GAME_HEIGHT,
                    this
            );
        } else {
            // Fallback หากหาไฟล์ Loading.png ไม่เจอ
            g2.setColor(new Color(58, 58, 72));
            g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g2.setColor(new Color(240, 45, 45));
            g2.setFont(new Font("Tahoma", Font.BOLD, 64));
            g2.drawString("INCOMING THREAT DETECTED", 280, 220);
        }

        g2.dispose();
    }
}
