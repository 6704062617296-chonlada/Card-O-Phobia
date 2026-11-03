package loseScene;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * หน้า Game Over
 *
 * Assets:
 * assets/Gameover.png
 * assets/TryAgain.png
 *
 * เมื่อคลิกปุ่ม Try Again จะเรียก onTryAgain
 */
public class GameOverScene extends JPanel {

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    // ตำแหน่งปุ่มบนหน้าจอ 1920x1080
    private static final int BUTTON_WIDTH = 720;
    private static final int BUTTON_HEIGHT = 158;
    private static final int BUTTON_X = (GAME_WIDTH - BUTTON_WIDTH) / 2;
    private static final int BUTTON_Y = 790;

    private final Runnable onTryAgain;
    private final Rectangle buttonBounds =
            new Rectangle(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT);

    private BufferedImage backgroundImage;
    private BufferedImage tryAgainImage;

    public GameOverScene(Runnable onTryAgain) {
        this.onTryAgain = onTryAgain;

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setFocusable(true);

        loadAssets();
        setupMouseListener();
    }

    private void loadAssets() {
        try {
            backgroundImage = ImageIO.read(new File("assets/Gameover.png"));

            BufferedImage source =
                    ImageIO.read(new File("assets/TryAgain.png"));

            // ตัดพื้นที่โปร่งใสรอบปุ่มออกก่อนนำไปวาด
            tryAgainImage = cropTransparentArea(source);

        } catch (IOException e) {
            System.err.println("Cannot load Game Over assets.");
            e.printStackTrace();
        }
    }

    private BufferedImage cropTransparentArea(BufferedImage source) {
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int alpha = (source.getRGB(x, y) >>> 24) & 0xFF;

                if (alpha > 0) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return source;
        }

        return source.getSubimage(
                minX,
                minY,
                maxX - minX + 1,
                maxY - minY + 1
        );
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (getWidth() <= 0 || getHeight() <= 0) {
                    return;
                }

                int mouseX = (int) (
                        e.getX() * GAME_WIDTH / (double) getWidth()
                );
                int mouseY = (int) (
                        e.getY() * GAME_HEIGHT / (double) getHeight()
                );

                if (buttonBounds.contains(mouseX, mouseY)
                        && onTryAgain != null) {
                    onTryAgain.run();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // รักษาขอบแบบ Pixel Art
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.scale(
                getWidth() / (double) GAME_WIDTH,
                getHeight() / (double) GAME_HEIGHT
        );

        if (backgroundImage != null) {
            g2.drawImage(
                    backgroundImage,
                    0, 0,
                    GAME_WIDTH, GAME_HEIGHT,
                    this
            );
        }

        if (tryAgainImage != null) {
            g2.drawImage(
                    tryAgainImage,
                    BUTTON_X, BUTTON_Y,
                    BUTTON_WIDTH, BUTTON_HEIGHT,
                    this
            );
        }

        g2.dispose();
    }
}
