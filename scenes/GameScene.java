
package scenes;

import cards.CardData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class GameScene extends JPanel {

    // ==========================================
    // 1. GAME SETTINGS
    // ==========================================

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    private static final int MAX_HAND = 5;

    // ==========================================
    // 2. GAME STATUS
    // ==========================================

    private int playerHP = 30;
    private int hackerHP = 30;

    private int turnNumber = 0;

    private boolean gameOver = false;

    // ==========================================
    // 3. ASSETS
    // ==========================================

    private BufferedImage background;
    private BufferedImage cardImage;

    // ==========================================
    // 4. CARD DATA
    // ==========================================

    // กองการ์ด Player
    private ArrayList<CardData> deck = new ArrayList<>();

    // การ์ดในมือ Player
    private ArrayList<CardData> hand = new ArrayList<>();

    // การ์ดโจมตีของ Hacker
    private CardData hackerAttackCard;

    // Index ของการ์ดที่เลือก
    private int selectedIndex = -1;

    // ==========================================
    // 5. HAND POSITION
    // ==========================================

    private static final int CARD_WIDTH = 235;
    private static final int CARD_HEIGHT = 333;

    private static final int START_X = 420;
    private static final int START_Y = 690;

    private static final int CARD_GAP = -25;

    private static final int SELECT_OFFSET = 60;

    // ==========================================
    // 6. DECK POSITION
    // ==========================================

    private static final int DECK_X = 115;
    private static final int DECK_Y = 630;

    private static final int DECK_WIDTH = 205;
    private static final int DECK_HEIGHT = 290;

    // ==========================================
    // 7. HACKER CARD POSITION
    // ==========================================

    private static final int BOT_CARD_X = 800;
    private static final int BOT_CARD_Y = 145;

    private static final int BOT_CARD_WIDTH = 353;
    private static final int BOT_CARD_HEIGHT = 500;

    // ==========================================
    // 8. USE CARD BUTTON
    // ==========================================

    private final Rectangle useCardButton =
        new Rectangle(1530, 955, 350, 85);

    // ==========================================
    // 9. CONSTRUCTOR
    // ==========================================

    public GameScene() {

        setPreferredSize(
            new Dimension(GAME_WIDTH, GAME_HEIGHT)
        );

        setBackground(Color.LIGHT_GRAY);

        loadAssets();

        initializeGame();

        setupMouseListener();
    }

    // ==========================================
    // 10. LOAD ASSETS
    // ==========================================

    private void loadAssets() {

        try {

            background = ImageIO.read(
                new File("assets/playing.png")
            );

            cardImage = ImageIO.read(
                new File("assets/cardMFA.png")
            );

            System.out.println("Assets loaded!");

        } catch (IOException e) {

            System.out.println("Cannot load assets!");

            e.printStackTrace();
        }
    }

    // ==========================================
    // 11. INITIALIZE GAME
    // ==========================================

    private void initializeGame() {

        if (cardImage == null) {
            return;
        }

        // สร้างกองการ์ด
        createDeck();

        // สุ่มการ์ด
        Collections.shuffle(deck);

        // เริ่มต้นมีการ์ด 4 ใบ
        for (int i = 0; i < 4; i++) {

            drawCard();
        }

        // เริ่มเทิร์นแรก
        // จั่วเพิ่มอัตโนมัติ 1 ใบ
        startTurn();
    }

    // ==========================================
    // 12. CREATE DECK
    // ==========================================

    private void createDeck() {

        // สร้างการ์ดตัวอย่าง 20 ใบ

        for (int i = 1; i <= 20; i++) {

            CardData card = new CardData(
                "MFA_" + String.format("%03d", i),
                "MFA",
                2,
                cardImage
            );

            deck.add(card);
        }
    }

    // ==========================================
    // 13. DRAW CARD
    // ==========================================

    private void drawCard() {

        // ตรวจสอบจำนวนการ์ดในมือ
        if (hand.size() >= MAX_HAND) {

            System.out.println("Hand is full!");

            return;
        }

        // ตรวจสอบกองการ์ด
        if (deck.isEmpty()) {

            System.out.println("Deck is empty!");

            return;
        }

        // หยิบการ์ดจากกอง
        CardData card = deck.remove(0);

        // เพิ่มเข้า Hand
        hand.add(card);

        System.out.println(
            "Draw Card: " + card.getId()
        );
    }

    // ==========================================
    // 14. START TURN
    // ==========================================

    private void startTurn() {

        if (gameOver) {
            return;
        }

        turnNumber++;

        System.out.println(
            "\n========== TURN " + turnNumber + " =========="
        );

        // จั่วการ์ดอัตโนมัติ
        drawCard();

        // Hacker ลงการ์ดโจมตี
        hackerAttackCard = new CardData(
            "HACKER_ATTACK_" + turnNumber,
            "Phishing",
            2,
            cardImage
        );

        // ยกเลิกการเลือกการ์ด
        selectedIndex = -1;

        System.out.println(
            "Hacker used: " + hackerAttackCard.getName()
        );

        repaint();
    }

    // ==========================================
    // 15. MOUSE LISTENER
    // ==========================================

    private void setupMouseListener() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (getWidth() <= 0 || getHeight() <= 0) {
                    return;
                }

                // แปลงพิกัด Mouse เป็นพิกัดเกม

                double scaleX =
                    (double) GAME_WIDTH / getWidth();

                double scaleY =
                    (double) GAME_HEIGHT / getHeight();

                int mouseX =
                    (int) (e.getX() * scaleX);

                int mouseY =
                    (int) (e.getY() * scaleY);

                handleMouseClick(mouseX, mouseY);
            }
        });
    }

    // ==========================================
    // 16. HANDLE MOUSE CLICK
    // ==========================================

    private void handleMouseClick(int mouseX, int mouseY) {

        if (gameOver) {
            return;
        }

        // ตรวจสอบการคลิกปุ่ม USE CARD
        if (useCardButton.contains(mouseX, mouseY)) {

            confirmCardUse();

            return;
        }

        // ตรวจสอบการคลิกการ์ด
        for (int i = hand.size() - 1; i >= 0; i--) {

            Rectangle bounds = getCardBounds(i);

            if (bounds.contains(mouseX, mouseY)) {

                // คลิกการ์ดเดิมเพื่อยกเลิก
                if (selectedIndex == i) {

                    selectedIndex = -1;

                } else {

                    // เลือกการ์ดใบใหม่
                    selectedIndex = i;
                }

                repaint();

                return;
            }
        }
    }

    // ==========================================
    // 17. GET CARD POSITION
    // ==========================================

    private Rectangle getCardBounds(int index) {

        int x =
            START_X + index * (CARD_WIDTH + CARD_GAP);

        int y = START_Y;

        // ถ้าการ์ดถูกเลือก ให้เลื่อนขึ้น
        if (selectedIndex == index) {

            y -= SELECT_OFFSET;
        }

        return new Rectangle(
            x,
            y,
            CARD_WIDTH,
            CARD_HEIGHT
        );
    }

    // ==========================================
    // 18. CONFIRM CARD USE
    // ==========================================

    private void confirmCardUse() {

        // ต้องเลือกการ์ดก่อน
        if (selectedIndex == -1) {

            System.out.println(
                "Please select a card first!"
            );

            return;
        }

        if (hackerAttackCard == null) {
            return;
        }

        // เก็บข้อมูลการ์ดที่เลือก
        CardData playerCard = hand.get(selectedIndex);

        System.out.println(
            "\n========== CARD USED =========="
        );

        System.out.println(
            "Player Card: " + playerCard.getName()
        );

        System.out.println(
            "Hacker Card: " + hackerAttackCard.getName()
        );

        // เรียกฟังก์ชันคำนวณ Damage
        calculateDamage(playerCard, hackerAttackCard);

        // นำการ์ดออกจากมือ
        hand.remove(selectedIndex);

        // ยกเลิกการเลือก
        selectedIndex = -1;

        // ตรวจสอบผู้ชนะ
        checkGameOver();

        if (gameOver) {

            repaint();

            return;
        }

        // เริ่มเทิร์นถัดไป
        startTurn();

        repaint();
    }

    // ==========================================
    // 19. DAMAGE CALCULATION
    // ==========================================

    private void calculateDamage(
        CardData playerCard,
        CardData hackerCard
    ) {

        /*
         * TODO:
         *
         * เขียน Logic คำนวณ Damage เอง
         *
         * playerCard = การ์ดที่ Player ใช้
         * hackerCard = การ์ดที่ Hacker ใช้
         *
         * สามารถแก้ไขค่า:
         *
         * playerHP
         * hackerHP
         *
         * ได้จากฟังก์ชันนี้
         */
        if(playerCard.getName() == "MFA" && hackerCard.getName() == "Phishing"){
            hackerHP -= 10;
        }


    }

    // ==========================================
    // 20. CHECK GAME OVER
    // ==========================================

    private void checkGameOver() {

        if (playerHP <= 0) {

            playerHP = 0;

            gameOver = true;

            System.out.println(
                "GAME OVER - HACKER WINS"
            );

        } else if (hackerHP <= 0) {

            hackerHP = 0;

            gameOver = true;

            System.out.println(
                "SYSTEM SECURED - PLAYER WINS"
            );
        }
    }

    // ==========================================
    // 21. DRAW DECK
    // ==========================================

    private void drawDeck(Graphics2D g2) {

        if (cardImage == null) {
            return;
        }

        // วาดการ์ดซ้อนกันเพื่อจำลองกองการ์ด

        for (int i = 2; i >= 0; i--) {

            int offset = i * 5;

            g2.drawImage(
                cardImage,
                DECK_X + offset,
                DECK_Y - offset,
                DECK_WIDTH,
                DECK_HEIGHT,
                this
            );
        }

        // แสดงจำนวนการ์ดที่เหลือ

        g2.setColor(Color.WHITE);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 30)
        );

        g2.drawString(
            "x" + deck.size(),
            DECK_X + 145,
            DECK_Y + 25
        );
    }

    // ==========================================
    // 22. DRAW HP
    // ==========================================

    private void drawHP(Graphics2D g2) {

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 28)
        );

        // Player HP
        g2.setColor(new Color(30, 100, 30));

        g2.drawString(
            "HP: " + playerHP + " / 30",
            260,
            120
        );

        // Hacker HP
        g2.setColor(new Color(180, 40, 40));

        g2.drawString(
            "HP: " + hackerHP + " / 30",
            1470,
            120
        );
    }

    // ==========================================
    // 23. DRAW TURN
    // ==========================================

    private void drawTurn(Graphics2D g2) {

        g2.setColor(Color.BLACK);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 24)
        );

        g2.drawString(
            "TURN " + turnNumber,
            900,
            80
        );
    }

    // ==========================================
    // 24. DRAW HAND CARDS
    // ==========================================

    private void drawCard(Graphics2D g2, int index) {

        CardData card = hand.get(index);

        if (card.getImage() == null) {
            return;
        }

        Rectangle bounds = getCardBounds(index);

        g2.drawImage(
            card.getImage(),
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height,
            this
        );
    }

    // ==========================================
    // 25. DRAW USE CARD BUTTON
    // ==========================================

    private void drawUseCardButton(Graphics2D g2) {

        int x = useCardButton.x;
        int y = useCardButton.y;

        int w = useCardButton.width;
        int h = useCardButton.height;

        // เงาด้านล่าง
        g2.setColor(Color.BLACK);

        g2.fillRoundRect(
            x + 5,
            y + 6,
            w,
            h,
            35,
            35
        );

        // สีพื้นหลังปุ่ม
        if (selectedIndex >= 0) {

            // มีการ์ดถูกเลือก
            g2.setColor(new Color(190, 190, 190));

        } else {

            // ยังไม่ได้เลือกการ์ด
            g2.setColor(new Color(150, 150, 150));
        }

        g2.fillRoundRect(
            x,
            y,
            w,
            h,
            35,
            35
        );

        // ขอบสีดำ
        g2.setColor(Color.BLACK);

        g2.setStroke(
            new BasicStroke(6)
        );

        g2.drawRoundRect(
            x,
            y,
            w,
            h,
            35,
            35
        );

        // ขอบสีขาวด้านใน
        g2.setColor(Color.WHITE);

        g2.setStroke(
            new BasicStroke(3)
        );

        g2.drawRoundRect(
            x + 7,
            y + 7,
            w - 14,
            h - 14,
            25,
            25
        );

        // ข้อความบนปุ่ม
        g2.setColor(Color.BLACK);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 32)
        );

        String text = "USE CARD";

        FontMetrics fm = g2.getFontMetrics();

        int textX =
            x + (w - fm.stringWidth(text)) / 2;

        int textY =
            y + (h - fm.getHeight()) / 2
                + fm.getAscent();

        g2.drawString(
            text,
            textX,
            textY
        );
    }

    // ==========================================
    // 26. PAINT COMPONENT
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // Pixel Art ไม่ให้เบลอ
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        // Scale ตามขนาดหน้าต่าง
        double scaleX =
            (double) getWidth() / GAME_WIDTH;

        double scaleY =
            (double) getHeight() / GAME_HEIGHT;

        g2.scale(scaleX, scaleY);

        // ----------------------------------
        // BACKGROUND
        // ----------------------------------

        if (background != null) {

            g2.drawImage(
                background,
                0,
                0,
                GAME_WIDTH,
                GAME_HEIGHT,
                this
            );
        }

        // ----------------------------------
        // HP
        // ----------------------------------

        drawHP(g2);

        // ----------------------------------
        // TURN
        // ----------------------------------

        drawTurn(g2);

        // ----------------------------------
        // DECK
        // ----------------------------------

        drawDeck(g2);

        // ----------------------------------
        // HACKER ATTACK CARD
        // ----------------------------------

        if (hackerAttackCard != null) {

            g2.drawImage(
                hackerAttackCard.getImage(),
                BOT_CARD_X,
                BOT_CARD_Y,
                BOT_CARD_WIDTH,
                BOT_CARD_HEIGHT,
                this
            );
        }

        // ----------------------------------
        // PLAYER HAND
        // ----------------------------------

        // วาดการ์ดที่ไม่ได้เลือกก่อน
        for (int i = 0; i < hand.size(); i++) {

            if (i == selectedIndex) {
                continue;
            }

            drawCard(g2, i);
        }

        // วาดการ์ดที่เลือกทีหลัง
        if (selectedIndex >= 0 &&
            selectedIndex < hand.size()) {

            drawCard(g2, selectedIndex);
        }

        // ----------------------------------
        // USE CARD BUTTON
        // ----------------------------------

        drawUseCardButton(g2);

        // ----------------------------------
        // GAME OVER
        // ----------------------------------

        if (gameOver) {

            g2.setColor(
                new Color(0, 0, 0, 150)
            );

            g2.fillRect(
                0,
                0,
                GAME_WIDTH,
                GAME_HEIGHT
            );

            g2.setColor(Color.WHITE);

            g2.setFont(
                new Font("Tahoma", Font.BOLD, 60)
            );

            String message =
                playerHP <= 0
                ? "GAME OVER"
                : "SYSTEM SECURED";

            FontMetrics fm = g2.getFontMetrics();

            int textX =
                (GAME_WIDTH - fm.stringWidth(message)) / 2;

            g2.drawString(
                message,
                textX,
                500
            );
        }

        g2.dispose();
    }
}
