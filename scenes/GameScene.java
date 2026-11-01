
package scenes;

import cards.CardData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameScene extends JPanel {

    // ==========================================
    // 1. GAME SETTINGS
    // ==========================================

    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    private static final int MAX_HAND = 5;

    private static final int WAIT_DURATION = 3000;
    private static final int SUMMARY_DURATION = 3000;

    // ==========================================
    // 2. GAME STATE
    // ==========================================

    private enum GamePhase {
        HACKER_WAITING,
        PLAYER_ACTION,
        DAMAGE_SUMMARY,
        GAME_OVER
    }

    private GamePhase currentPhase;

    private Timer phaseTimer;

    private int playerHP = 30;
    private int hackerHP = 30;

    private int turnNumber = 0;

    private boolean gameOver = false;

    // Damage Summary
    private int playerDamage = 0;
    private int hackerDamage = 0;

    // ==========================================
    // 3. SYSTEM RESOURCE (SR)
    // ==========================================

    private int systemResource = 3;

    private static final int SR_PER_TURN = 1;

    // ==========================================
    // 4. ASSETS
    // ==========================================

    private BufferedImage background;
    private BufferedImage cardImage;

    // HP Assets
    private BufferedImage hpFrameImage;
    private BufferedImage hpBarImage;

    // ==========================================
    // 5. CARD DATA
    // ==========================================

    // Player Deck
    private ArrayList<CardData> deck = new ArrayList<>();

    // Player Hand
    private ArrayList<CardData> hand = new ArrayList<>();

    // Hacker Attack Card
    private CardData hackerAttackCard;

    // Selected Card Index
    private int selectedIndex = -1;

    // Random
    private Random random = new Random();

    // Hacker Card Types
    private final String[] hackerCardTypes = {
        "Phishing",
        "Malware",
        "DDoS"
    };

    // ==========================================
    // 6. HAND POSITION
    // ==========================================

    private static final int CARD_WIDTH = 235;
    private static final int CARD_HEIGHT = 333;

    private static final int START_X = 420;
    private static final int START_Y = 690;

    private static final int CARD_GAP = -25;

    private static final int SELECT_OFFSET = 60;

    // ==========================================
    // 7. DECK POSITION
    // ==========================================

    private static final int DECK_X = 115;
    private static final int DECK_Y = 630;

    private static final int DECK_WIDTH = 205;
    private static final int DECK_HEIGHT = 290;

    // ==========================================
    // 8. HACKER CARD POSITION
    // ==========================================

    private static final int BOT_CARD_X = 800;
    private static final int BOT_CARD_Y = 145;

    private static final int BOT_CARD_WIDTH = 353;
    private static final int BOT_CARD_HEIGHT = 500;

    // ==========================================
    // 9. HP BAR POSITION
    // ==========================================

    private static final int HP_BAR_WIDTH = 340;
    private static final int HP_BAR_HEIGHT = 85;

    private static final int HP_BAR_Y = 25;

    private static final int PLAYER_HP_X = 220;
    private static final int HACKER_HP_X = 1360;

    private static final int HP_INNER_X = 14;
    private static final int HP_INNER_Y = 14;

    private static final int HP_INNER_WIDTH = 312;
    private static final int HP_INNER_HEIGHT = 57;

    // ==========================================
    // 10. SR DISPLAY POSITION
    // ==========================================

    private static final int SR_X = 325;
    private static final int SR_Y = 625;

    private static final int SR_WIDTH = 90;
    private static final int SR_HEIGHT = 52;

    // ==========================================
    // 11. USE CARD BUTTON
    // ==========================================

    private final Rectangle useCardButton =
        new Rectangle(1530, 955, 350, 85);

    // ==========================================
    // 12. CONSTRUCTOR
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
    // 13. LOAD ASSETS
    // ==========================================

    private void loadAssets() {

        try {

            // Background
            background = ImageIO.read(
                new File("assets/playing.png")
            );

            // Card
            cardImage = ImageIO.read(
                new File("assets/cardMFA.png")
            );

            // HP Frame
            BufferedImage frameSource = ImageIO.read(
                new File("assets/Hp_frame.png")
            );

            // HP Bar
            BufferedImage barSource = ImageIO.read(
                new File("assets/Hp_bar.png")
            );

            // Crop transparent area
            hpFrameImage = frameSource.getSubimage(
                64, 160, 384, 96
            );

            hpBarImage = barSource.getSubimage(
                80, 176, 352, 64
            );

            System.out.println("Assets loaded successfully!");

        } catch (IOException | RasterFormatException e) {

            System.out.println("Cannot load assets!");

            e.printStackTrace();
        }
    }

    // ==========================================
    // 14. INITIALIZE GAME
    // ==========================================

    private void initializeGame() {

        if (cardImage == null) {
            return;
        }

        // Create Deck
        createDeck();

        // Shuffle Deck
        Collections.shuffle(deck);

        // Starting Hand = 4 Cards
        for (int i = 0; i < 4; i++) {

            drawCard();
        }

        // Initial SR = 3
        systemResource = 3;

        // Start First Turn
        startTurn();
    }

    // ==========================================
    // 15. CREATE DECK
    // ==========================================

    private void createDeck() {

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
    // 16. DRAW CARD
    // ==========================================

    private void drawCard() {

        // Check Hand Limit
        if (hand.size() >= MAX_HAND) {

            System.out.println("Hand is full!");

            return;
        }

        // Check Deck
        if (deck.isEmpty()) {

            System.out.println("Deck is empty!");

            return;
        }

        // Draw Top Card
        CardData card = deck.remove(0);

        hand.add(card);

        System.out.println(
            "Draw Card: " + card.getId()
        );
    }

    // ==========================================
    // 17. START TURN
    // ==========================================

    private void startTurn() {

        if (gameOver) {
            return;
        }

        stopPhaseTimer();

        turnNumber++;

        System.out.println(
            "\n========== TURN " + turnNumber + " =========="
        );

        // ----------------------------------
        // SYSTEM RESOURCE
        // ----------------------------------

        // Turn 1 uses starting SR = 3
        // Turn 2 onwards gains +1 SR

        if (turnNumber > 1) {

            systemResource += SR_PER_TURN;
        }

        System.out.println(
            "System Resource: " + systemResource
        );

        // ----------------------------------
        // RESET STATUS
        // ----------------------------------

        selectedIndex = -1;

        playerDamage = 0;
        hackerDamage = 0;

        hackerAttackCard = null;

        // Auto Draw Card
        drawCard();

        // Set Phase
        currentPhase = GamePhase.HACKER_WAITING;

        System.out.println(
            "Hacker is preparing an attack..."
        );

        repaint();

        // Wait 3 seconds
        phaseTimer = new Timer(WAIT_DURATION, e -> {

            revealHackerCard();

        });

        phaseTimer.setRepeats(false);

        phaseTimer.start();
    }

    // ==========================================
    // 18. REVEAL HACKER CARD
    // ==========================================

    private void revealHackerCard() {

        if (gameOver) {
            return;
        }

        // Random Hacker Card
        int randomIndex =
            random.nextInt(hackerCardTypes.length);

        String selectedType =
            hackerCardTypes[randomIndex];

        // Create Hacker Card
        hackerAttackCard = new CardData(
            "HACKER_ATTACK_" + turnNumber,
            selectedType,
            2,
            cardImage
        );

        // Allow Player Action
        currentPhase = GamePhase.PLAYER_ACTION;

        System.out.println(
            "Hacker used: " + selectedType
        );

        repaint();
    }

    // ==========================================
    // 19. STOP TIMER
    // ==========================================

    private void stopPhaseTimer() {

        if (phaseTimer != null) {

            phaseTimer.stop();

            phaseTimer = null;
        }
    }

    // ==========================================
    // 20. MOUSE LISTENER
    // ==========================================

    private void setupMouseListener() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (getWidth() <= 0 || getHeight() <= 0) {
                    return;
                }

                // Convert Mouse Coordinate
                // to Game Coordinate

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
    // 21. HANDLE MOUSE CLICK
    // ==========================================

    private void handleMouseClick(int mouseX, int mouseY) {

        // Only Player Action can interact
        if (currentPhase != GamePhase.PLAYER_ACTION) {
            return;
        }

        // Use Card Button
        if (useCardButton.contains(mouseX, mouseY)) {

            confirmCardUse();

            return;
        }

        // Check Card Selection
        for (int i = hand.size() - 1; i >= 0; i--) {

            Rectangle bounds = getCardBounds(i);

            if (bounds.contains(mouseX, mouseY)) {

                // Click Same Card = Deselect
                if (selectedIndex == i) {

                    selectedIndex = -1;

                } else {

                    selectedIndex = i;
                }

                repaint();

                return;
            }
        }
    }

    // ==========================================
    // 22. GET CARD POSITION
    // ==========================================

    private Rectangle getCardBounds(int index) {

        int x =
            START_X + index * (CARD_WIDTH + CARD_GAP);

        int y = START_Y;

        // Move Selected Card Up
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
    // 23. CONFIRM CARD USE
    // ==========================================

    private void confirmCardUse() {

        if (selectedIndex == -1) {

            System.out.println(
                "Please select a card first!"
            );

            return;
        }

        if (hackerAttackCard == null) {
            return;
        }

        // Get Selected Card
        CardData playerCard = hand.get(selectedIndex);

        // ----------------------------------
        // CHECK SYSTEM RESOURCE
        // ----------------------------------

        int cardCost = playerCard.getCost();

        if (systemResource < cardCost) {

            System.out.println(
                "Not enough System Resource!"
            );

            System.out.println(
                "Required: " + cardCost
                + " | Available: " + systemResource
            );

            // ไม่เสียการ์ด
            // ไม่เปลี่ยนเทิร์น
            repaint();

            return;
        }

        System.out.println(
            "\n========== CARD USED =========="
        );

        System.out.println(
            "Player Card: " + playerCard.getName()
        );

        System.out.println(
            "Card Cost: " + cardCost
        );

        System.out.println(
            "Hacker Card: " + hackerAttackCard.getName()
        );

        // ----------------------------------
        // SPEND SYSTEM RESOURCE
        // ----------------------------------

        systemResource -= cardCost;

        System.out.println(
            "Remaining SR: " + systemResource
        );

        // ----------------------------------
        // STORE HP BEFORE DAMAGE
        // ----------------------------------

        int playerHPBefore = playerHP;
        int hackerHPBefore = hackerHP;

        // Calculate Damage
        calculateDamage(
            playerCard,
            hackerAttackCard
        );

        // Prevent Negative HP
        playerHP = Math.max(0, playerHP);
        hackerHP = Math.max(0, hackerHP);

        // Calculate Damage Summary
        playerDamage = Math.max(
            0,
            playerHPBefore - playerHP
        );

        hackerDamage = Math.max(
            0,
            hackerHPBefore - hackerHP
        );

        // Remove Used Card
        hand.remove(selectedIndex);

        selectedIndex = -1;

        System.out.println(
            "Player Damage: " + playerDamage
        );

        System.out.println(
            "Hacker Damage: " + hackerDamage
        );

        // Show Damage Summary
        showDamageSummary();
    }

    // ==========================================
    // 24. DAMAGE CALCULATION
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
         * ได้โดยตรงจากฟังก์ชันนี้
         */

        if (
            "MFA".equals(playerCard.getName()) &&
            "Phishing".equals(hackerCard.getName())
        ) {

            hackerHP -= 10;
        }

        if (
            "MFA".equals(playerCard.getName()) &&
            "Malware".equals(hackerCard.getName())
        ) {

            playerHP -= 5;
        }

        if (
            "MFA".equals(playerCard.getName()) &&
            "DDoS".equals(hackerCard.getName())
        ) {

            playerHP -= 2;
        }
    }

    // ==========================================
    // 25. SHOW DAMAGE SUMMARY
    // ==========================================

    private void showDamageSummary() {

        currentPhase = GamePhase.DAMAGE_SUMMARY;

        System.out.println(
            "========== DAMAGE SUMMARY =========="
        );

        repaint();

        // Show Damage for 3 Seconds
        phaseTimer = new Timer(SUMMARY_DURATION, e -> {

            stopPhaseTimer();

            // Check Game Over
            if (playerHP <= 0 || hackerHP <= 0) {

                checkGameOver();

                repaint();

                return;
            }

            // Start Next Turn
            startTurn();

        });

        phaseTimer.setRepeats(false);

        phaseTimer.start();
    }

    // ==========================================
    // 26. CHECK GAME OVER
    // ==========================================

    private void checkGameOver() {

        if (playerHP <= 0) {

            playerHP = 0;

            gameOver = true;

            currentPhase = GamePhase.GAME_OVER;

            stopPhaseTimer();

            System.out.println(
                "GAME OVER - HACKER WINS"
            );

        } else if (hackerHP <= 0) {

            hackerHP = 0;

            gameOver = true;

            currentPhase = GamePhase.GAME_OVER;

            stopPhaseTimer();

            System.out.println(
                "SYSTEM SECURED - PLAYER WINS"
            );
        }
    }

    // ==========================================
    // 27. DRAW DECK
    // ==========================================

    private void drawDeck(Graphics2D g2) {

        if (cardImage == null) {
            return;
        }

        // Draw Stacked Cards
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

        // Remaining Cards
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
    // 28. DRAW SYSTEM RESOURCE
    // ==========================================

    private void drawSystemResource(Graphics2D g2) {

        int x = SR_X;
        int y = SR_Y;

        int w = SR_WIDTH;
        int h = SR_HEIGHT;

        // Shadow
        g2.setColor(Color.BLACK);

        g2.fillRoundRect(
            x + 3,
            y + 4,
            w,
            h,
            12,
            12
        );

        // Background
        g2.setColor(new Color(45, 55, 95));

        g2.fillRoundRect(
            x,
            y,
            w,
            h,
            12,
            12
        );

        // Border
        g2.setColor(Color.BLACK);

        g2.setStroke(
            new BasicStroke(4)
        );

        g2.drawRoundRect(
            x,
            y,
            w,
            h,
            12,
            12
        );

        // SR Icon - Blue Diamond
        int centerX = x + 22;
        int centerY = y + 26;

        Polygon diamond = new Polygon();

        diamond.addPoint(centerX, centerY - 16);
        diamond.addPoint(centerX + 15, centerY);
        diamond.addPoint(centerX, centerY + 16);
        diamond.addPoint(centerX - 15, centerY);

        g2.setColor(new Color(95, 170, 255));

        g2.fillPolygon(diamond);

        g2.setColor(Color.WHITE);

        g2.setStroke(
            new BasicStroke(2)
        );

        g2.drawPolygon(diamond);

        // SR Label
        g2.setColor(Color.WHITE);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 14)
        );

        g2.drawString(
            "SR",
            x + 43,
            y + 19
        );

        // SR Value
        g2.setFont(
            new Font("Tahoma", Font.BOLD, 23)
        );

        g2.drawString(
            String.valueOf(systemResource),
            x + 44,
            y + 42
        );
    }

    // ==========================================
    // 29. DRAW HP BAR
    // ==========================================

    private void drawHP(Graphics2D g2) {

        // Player HP
        drawSingleHPBar(
            g2,
            PLAYER_HP_X,
            HP_BAR_Y,
            playerHP,
            true
        );

        // Hacker HP
        drawSingleHPBar(
            g2,
            HACKER_HP_X,
            HP_BAR_Y,
            hackerHP,
            false
        );
    }

    // ==========================================
    // 30. DRAW SINGLE HP BAR
    // ==========================================

    private void drawSingleHPBar(
        Graphics2D g2,
        int x,
        int y,
        int currentHP,
        boolean fillFromLeft
    ) {

        if (hpFrameImage == null || hpBarImage == null) {
            return;
        }

        // Clamp HP between 0 and 30
        int hp = Math.max(0, Math.min(30, currentHP));

        // Calculate HP Percentage
        double hpPercentage = hp / 30.0;

        // Calculate Filled Width
        int fillWidth = (int)(
            HP_INNER_WIDTH * hpPercentage
        );

        // ----------------------------------
        // DRAW HP BAR
        // ----------------------------------

        if (fillWidth > 0) {

            int barX = x + HP_INNER_X;
            int barY = y + HP_INNER_Y;

            int sourceWidth = (int)(
                hpBarImage.getWidth() * hpPercentage
            );

            int sourceX = 0;

            // Hacker HP decreases from right to left
            if (!fillFromLeft) {

                barX += HP_INNER_WIDTH - fillWidth;

                sourceX =
                    hpBarImage.getWidth() - sourceWidth;
            }

            g2.drawImage(
                hpBarImage,

                // Destination
                barX,
                barY,
                barX + fillWidth,
                barY + HP_INNER_HEIGHT,

                // Source
                sourceX,
                0,
                sourceX + sourceWidth,
                hpBarImage.getHeight(),

                this
            );
        }

        // ----------------------------------
        // DRAW FRAME
        // ----------------------------------

        g2.drawImage(
            hpFrameImage,
            x,
            y,
            HP_BAR_WIDTH,
            HP_BAR_HEIGHT,
            this
        );

        // ----------------------------------
        // DRAW HP TEXT
        // ----------------------------------

        g2.setColor(Color.WHITE);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 22)
        );

        String hpText = hp + " / 30";

        FontMetrics fm = g2.getFontMetrics();

        int textX =
            x + (HP_BAR_WIDTH - fm.stringWidth(hpText)) / 2;

        int textY =
            y + (HP_BAR_HEIGHT - fm.getHeight()) / 2
                + fm.getAscent();

        g2.drawString(
            hpText,
            textX,
            textY
        );

        // ----------------------------------
        // DRAW DAMAGE SUMMARY
        // ----------------------------------

        if (currentPhase == GamePhase.DAMAGE_SUMMARY) {

            int damage = fillFromLeft
                ? playerDamage
                : hackerDamage;

            if (damage > 0) {

                g2.setColor(new Color(230, 40, 40));

                g2.setFont(
                    new Font("Tahoma", Font.BOLD, 28)
                );

                String damageText = "-" + damage;

                FontMetrics damageFM = g2.getFontMetrics();

                int damageX =
                    x + (HP_BAR_WIDTH -
                    damageFM.stringWidth(damageText)) / 2;

                g2.drawString(
                    damageText,
                    damageX,
                    y + HP_BAR_HEIGHT + 35
                );
            }
        }
    }

    // ==========================================
    // 31. DRAW TURN
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
    // 32. DRAW HAND CARD
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
    // 33. DRAW USE CARD BUTTON
    // ==========================================

    private void drawUseCardButton(Graphics2D g2) {

        int x = useCardButton.x;
        int y = useCardButton.y;

        int w = useCardButton.width;
        int h = useCardButton.height;

        // Shadow
        g2.setColor(Color.BLACK);

        g2.fillRoundRect(
            x + 5,
            y + 6,
            w,
            h,
            35,
            35
        );

        // Button Background
        if (selectedIndex >= 0) {

            g2.setColor(new Color(190, 190, 190));

        } else {

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

        // Black Border
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

        // White Inner Border
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

        // Button Text
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
    // 34. DRAW GAME OVER
    // ==========================================

    private void drawGameOver(Graphics2D g2) {

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

    // ==========================================
    // 35. PAINT COMPONENT
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        // Pixel Art Rendering
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        // Scale Window
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
        // HP BAR
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

        if (
            hackerAttackCard != null &&
            hackerAttackCard.getImage() != null
        ) {

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

        // Draw Unselected Cards First
        for (int i = 0; i < hand.size(); i++) {

            if (i == selectedIndex) {
                continue;
            }

            drawCard(g2, i);
        }

        // Draw Selected Card Last
        if (
            selectedIndex >= 0 &&
            selectedIndex < hand.size()
        ) {

            drawCard(g2, selectedIndex);
        }

        // ----------------------------------
        // SYSTEM RESOURCE
        // ----------------------------------

        drawSystemResource(g2);

        // ----------------------------------
        // USE CARD BUTTON
        // ----------------------------------

        drawUseCardButton(g2);

        // ----------------------------------
        // GAME OVER
        // ----------------------------------

        if (currentPhase == GamePhase.GAME_OVER) {

            drawGameOver(g2);
        }

        g2.dispose();
    }
}
