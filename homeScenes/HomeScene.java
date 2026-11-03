package homeScenes;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
// import java.awt.Font;
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
 * หน้า Home ของเกม Card-O-Phobia
 *
 * Assets ที่ต้องมี:
 * assets/Home.png
 * assets/Start.png
 */
public class HomeScene extends JPanel {

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    // ปรับตำแหน่งและขนาดปุ่ม START ได้จากค่าชุดนี้
    private static final int START_X = 750;
    private static final int START_Y = 755;
    private static final int START_WIDTH = 420;
    private static final int START_HEIGHT = 250;

    private final Runnable onStart;

    private BufferedImage backgroundImage;
    private BufferedImage startButtonImage;

    private final Rectangle startButtonBounds =
            new Rectangle(START_X, START_Y, START_WIDTH, START_HEIGHT);

    public HomeScene(Runnable onStart) {
        this.onStart = onStart;

        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setFocusable(true);

        loadAssets();
        setupMouseListener();
    }

    private void loadAssets() {
        try {
            backgroundImage = ImageIO.read(
                    new File("assets/Home.png")
            );

            BufferedImage startSource = ImageIO.read(
                    new File("assets/Start.png")
            );

            /*
             * ตัดขอบสีขาวรอบภาพ Start.png ออก
             * เพื่อให้เหลือเฉพาะปุ่ม START และขอบ pixel art
             */
            startButtonImage = cropNonWhiteArea(startSource);

            System.out.println("Home assets loaded successfully.");

        } catch (IOException e) {
            System.err.println("Cannot load Home assets.");
            e.printStackTrace();
        }
    }

    private BufferedImage cropNonWhiteArea(BufferedImage source) {
        int minX = source.getWidth();
        int minY = source.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                int rgb = source.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // พิกเซลที่ไม่ใช่สีขาวเกือบทั้งหมด ถือเป็นส่วนหนึ่งของปุ่ม
                boolean isNotWhite = red < 245 || green < 245 || blue < 245;

                if (isNotWhite) {
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

                // แปลงพิกัดเมาส์จากขนาด JPanel กลับเป็น canvas 1920x1080
                double scaleX = (double) GAME_WIDTH / getWidth();
                double scaleY = (double) GAME_HEIGHT / getHeight();

                int mouseX = (int) (e.getX() * scaleX);
                int mouseY = (int) (e.getY() * scaleY);

                if (startButtonBounds.contains(mouseX, mouseY)) {
                    if (onStart != null) {
                        onStart.run();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // ใช้ Nearest Neighbor เพื่อรักษาขอบ pixel art
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
            g2.setColor(new Color(215, 215, 215));
            g2.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        }

        if (startButtonImage != null) {
            g2.drawImage(
                    startButtonImage,
                    START_X, START_Y,
                    START_WIDTH, START_HEIGHT,
                    this
            );
        }

        g2.dispose();
    }
}
