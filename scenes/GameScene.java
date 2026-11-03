
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
    private int isPlayerMalware = 0;
    private int isHackerMalware = 0;

    // ==========================================
    // 3. SYSTEM RESOURCE
    // ==========================================

    private int systemResource = 3;

    private static final int SR_PER_TURN = 1;

    private static final int MAX_SR = 8;

    // ==========================================
    // 4. ASSETS
    // ==========================================

    private BufferedImage background;
    private BufferedImage cardImage;
    private BufferedImage BackupImg;
    private BufferedImage AntivirusImg;
    private BufferedImage FirewallImg;
    private BufferedImage MalwareImg;
    private BufferedImage MFAImg;
    private BufferedImage PhishingImg;
    private BufferedImage RansomwareImg;
    private BufferedImage ScanVirusImg;
    private BufferedImage UpdatePatchImg;

    private BufferedImage hpFrameImage;
    private BufferedImage hpBarImage;

    private BufferedImage srBackground;

    // ==========================================
    // 5. CARD DATA
    // ==========================================

    // Player Deck
    private ArrayList<CardData> deck = new ArrayList<>();

    // Player Hand
    private ArrayList<CardData> hand = new ArrayList<>();

    // Hacker Deck
    private ArrayList<CardData> hackerDeck = new ArrayList<>();

    // Current Hacker Card
    private CardData hackerAttackCard;

    // Selected Card Index
    private int selectedIndex = -1;

    // ==========================================
    // 6. HAND POSITION
    // ==========================================

    private static final int CARD_WIDTH = 255;
    private static final int CARD_HEIGHT = 370;

    private static final int START_X = 380;
    private static final int START_Y = 690;

    private static final int CARD_GAP = -30;

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
    private static final int BOT_CARD_WIDTH = 340;
    private static final int BOT_CARD_HEIGHT = 460;

    // ==========================================
    // 9. HP BAR POSITION
    // ==========================================

    private static final int HP_BAR_WIDTH = 340;
    private static final int HP_BAR_HEIGHT = 50;

    private static final int HP_BAR_Y = 25;

    private static final int PLAYER_HP_X = 370;
    private static final int HACKER_HP_X = 1210;

    private static final int HP_INNER_X = 14;
    private static final int HP_INNER_Y = 6;

    private static final int HP_INNER_WIDTH = 312;
    private static final int HP_INNER_HEIGHT = 40;

    // ==========================================
    // 10. SR DISPLAY POSITION
    // ==========================================

    private static final int SR_X = 375;
    private static final int SR_Y = 650;
    private static final int SR_SIZE = 96;


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

            background = ImageIO.read(
                new File("assets/playing.png")
            );

            cardImage = ImageIO.read(
                new File("assets/cardMFA.png")
            );
            BackupImg = ImageIO.read(
                new File("assets/Backup_system.png")
            );

            AntivirusImg = ImageIO.read(
                new File("assets/Antivirus.png")
            );

            FirewallImg = ImageIO.read(
                new File("assets/Firewall.png")
            );

            MalwareImg = ImageIO.read(
                new File("assets/Malware.png")
            );

            MFAImg = ImageIO.read(
                new File("assets/MFA.png")
            );

            PhishingImg = ImageIO.read(
                new File("assets/Phishing.png")
            );

            RansomwareImg = ImageIO.read(
                new File("assets/Ransomeware.png")
            );

            ScanVirusImg = ImageIO.read(
                new File("assets/ScanVirus.png")
            );

            UpdatePatchImg = ImageIO.read(
                new File("assets/Update.png")
            );

            srBackground = ImageIO.read(
                new File("assets/SRbg.png")
            );

            BufferedImage frameSource = ImageIO.read(
                new File("assets/Hp_frame.png")
            );

            BufferedImage barSource = ImageIO.read(
                new File("assets/Hp_bar.png")
            );

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

        // Create Player Deck
        createDeck();

        Collections.shuffle(deck);

        // Create Hacker Deck
        createHackerDeck();

        Collections.shuffle(hackerDeck);

        // Starting Hand = 4 Cards
        for (int i = 0; i < 4; i++) {
            drawCard();
        }

        // Initial SR
        systemResource = 3;

        // Start First Turn
        startTurn();
    }

    // ==========================================
    // 15. CREATE PLAYER DECK
    // ==========================================

    private void createDeck() {

        // MFA Cards (1 copies)
        for (int i = 1; i <= 1; i++) {
            CardData card = new CardData(
                "MFA_" + String.format("%03d", i),
                "MFA",
                2,
                MFAImg
            );

            deck.add(card);
        }
        
        // Firewall Cards (3 copies)
        for (int i = 1; i <= 3; i++) {
            CardData card = new CardData(
                "FIREWALL_" + String.format("%03d", i),
                "Firewall",
                2,
                FirewallImg
            );

            deck.add(card);
        }

        // Update / Patch Cards (1 copies)
        for (int i = 1; i <= 1; i++) {
            CardData card = new CardData(
                "UPDATE_PATCH_" + String.format("%03d", i),
                "Update / Patch",
                3,
                UpdatePatchImg
            );

            deck.add(card);
        }

        // Backup system Cards (1 copies)
        for (int i = 1; i <= 1; i++) {
            CardData card = new CardData(
                "BACKUP_SYSTEM_" + String.format("%03d", i),
                "Backup system",
                3,
                BackupImg
            );

            deck.add(card);
        }

        // Antivirus Cards (1 copies)
        for (int i = 1; i <= 1; i++) {
            CardData card = new CardData(
                "ANTIVIRUS_" + String.format("%03d", i),
                "AntiVirus",
                2,
                AntivirusImg
            );

            deck.add(card);
        }

        // Phishing Cards (6 copies)
        for (int i = 1; i <= 6; i++) {
            CardData card = new CardData(
                "PHISHING_" + String.format("%03d", i),
                "Phishing",
                1,
                PhishingImg
            );

            deck.add(card);
        }

        // Malware Cards (4 copies)
        for (int i = 1; i <= 4; i++) {
            CardData card = new CardData(
                "MALWARE_" + String.format("%03d", i),
                "Malware",
                3,
                MalwareImg
            );

            deck.add(card);
        }

        // Ransomware Cards (2 copies)
        for (int i = 1; i <= 2; i++) {
            CardData card = new CardData(
                "RANSOMWARE_" + String.format("%03d", i),
                "Ransomware",
                4,
                RansomwareImg
            );

            deck.add(card);
        }

        // ScanVirus Cards (6 copies)
        for (int i = 1; i <= 6; i++) {
            CardData card = new CardData(
                "SCANVIRUS_" + String.format("%03d", i),
                "ScanVirus",
                0,
                ScanVirusImg
            );

            deck.add(card);
        }
    }

    // ==========================================
    // 16. CREATE HACKER DECK
    // ==========================================

    private void createHackerDeck() {

        // Clear old deck
        hackerDeck.clear();

        int cardId = 1;

        // ----------------------------------
        // ATTACK CARDS
        // ----------------------------------

        // Phishing = 7
        for (int i = 0; i < 7; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Phishing",
                    2,
                    PhishingImg
                )
            );
        }

        // Malware = 5
        for (int i = 0; i < 5; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Malware",
                    2,
                    MalwareImg
                )
            );
        }

        // Ransomware = 3
        for (int i = 0; i < 3; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Ransomware",
                    2,
                    RansomwareImg
                )
            );
        }

        // ----------------------------------
        // DEFENSE CARDS
        // ----------------------------------

        // MFA = 2
        for (int i = 0; i < 2; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "MFA",
                    2,
                    MFAImg
                )
            );
        }

        // Firewall = 2
        for (int i = 0; i < 2; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Firewall",
                    2,
                    FirewallImg
                )
            );
        }

        // Update / Patch = 2
        for (int i = 0; i < 2; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Update / Patch",
                    2,
                    UpdatePatchImg
                )
            );
        }

        // Backup system = 2
        for (int i = 0; i < 2; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Backup system",
                    2,
                    BackupImg
                )
            );
        }

        // Antivirus = 2
        for (int i = 0; i < 2; i++) {

            hackerDeck.add(
                new CardData(
                    "HACKER_" + cardId++,
                    "Antivirus",
                    2,
                    AntivirusImg
                )
            );
        }

        System.out.println(
            "Hacker Deck created: " + hackerDeck.size() + " cards"
        );

        // Check total cards
        if (hackerDeck.size() != 25) {

            System.out.println(
                "WARNING: Hacker Deck must contain 25 cards!"
            );
        }
    }

    // ==========================================
    // 17. DRAW PLAYER CARD
    // ==========================================

    private void drawCard() {

        if (hand.size() >= MAX_HAND) {

            System.out.println("Hand is full!");

            return;
        }

        if (deck.isEmpty()) {

            System.out.println("Player Deck is empty!");

            return;
        }

        CardData card = deck.remove(0);

        hand.add(card);

        System.out.println(
            "Player Draw: " + card.getId()
        );
    }

    // ==========================================
    // 18. DRAW HACKER CARD
    // ==========================================

    private CardData drawHackerCard() {

        // If deck is empty, create a new deck
        if (hackerDeck.isEmpty()) {

            System.out.println(
                "Hacker Deck is empty! Creating new deck..."
            );

            createHackerDeck();

            Collections.shuffle(hackerDeck);

            System.out.println(
                "Hacker Deck shuffled again!"
            );
        }

        // Draw top card
        CardData card = hackerDeck.remove(0);

        System.out.println(
            "Hacker Draw: " + card.getName()
        );

        System.out.println(
            "Hacker Cards Remaining: " + hackerDeck.size()
        );

        return card;
    }

    // ==========================================
    // 19. START TURN
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

        if (turnNumber > 1) {

            systemResource = Math.min(
                MAX_SR,
                systemResource + SR_PER_TURN
            );
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

        // ----------------------------------
        // AUTO DRAW PLAYER CARD
        // ----------------------------------

        drawCard();

        // ----------------------------------
        // HACKER WAITING
        // ----------------------------------

        currentPhase = GamePhase.HACKER_WAITING;

        System.out.println(
            "Hacker is preparing an attack..."
        );

        repaint();

        phaseTimer = new Timer(WAIT_DURATION, e -> {

            revealHackerCard();

        });

        phaseTimer.setRepeats(false);

        phaseTimer.start();
    }

    // ==========================================
    // 20. REVEAL HACKER CARD
    // ==========================================

    private void revealHackerCard() {

        if (gameOver) {
            return;
        }

        // Draw from Hacker Deck
        hackerAttackCard = drawHackerCard();

        // Allow Player Action
        currentPhase = GamePhase.PLAYER_ACTION;

        System.out.println(
            "Hacker used: " + hackerAttackCard.getName()
        );

        repaint();
    }

    // ==========================================
    // 21. STOP TIMER
    // ==========================================

    private void stopPhaseTimer() {

        if (phaseTimer != null) {

            phaseTimer.stop();

            phaseTimer = null;
        }
    }

    // ==========================================
    // 22. MOUSE LISTENER
    // ==========================================

    private void setupMouseListener() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (getWidth() <= 0 || getHeight() <= 0) {
                    return;
                }

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
    // 23. HANDLE MOUSE CLICK
    // ==========================================

    private void handleMouseClick(int mouseX, int mouseY) {

        if (currentPhase != GamePhase.PLAYER_ACTION) {
            return;
        }

        // USE CARD Button
        if (useCardButton.contains(mouseX, mouseY)) {

            confirmCardUse();

            return;
        }

        // Card Selection
        for (int i = hand.size() - 1; i >= 0; i--) {

            Rectangle bounds = getCardBounds(i);

            if (bounds.contains(mouseX, mouseY)) {

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
    // 24. GET CARD POSITION
    // ==========================================

    private Rectangle getCardBounds(int index) {

        int x =
            START_X + index * (CARD_WIDTH + CARD_GAP);

        int y = START_Y;

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
    // 25. CONFIRM CARD USE
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
        // SPEND SR
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
    // 26. DAMAGE CALCULATION
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

        int playerDamaged = 0;
        int hackerDamaged = 0;
        int playerDefensed = 0;
        int hackerDefensed = 0;

        // Player Damage Calculation
        if("ScanVirus".equals(playerCard.getName())) {
            systemResource += 1;
        }
        if("Firewall".equals(playerCard.getName())) {
            playerDefensed += 4;
        }
        if("Phishing".equals(hackerCard.getName())) {
            playerDamaged += 3;
            if("MFA".equals(playerCard.getName())) {
                playerDefensed += 2;
            }
        } else if("Malware".equals(hackerCard.getName())) {
            playerDamaged += 5;
            if(!"AntiVirus".equals(playerCard.getName())) {
                isPlayerMalware = 2;
            }
            
        } else if("Ransomware".equals(hackerCard.getName())) {
            System.out.println("Hacker used Ransomware!");
            playerDamaged += 8;
            if("Backup system".equals(playerCard.getName())) {
                playerDefensed += 3;
            } else if("Update / Patch".equals(playerCard.getName())) {
                playerDefensed += 8;
            }
        }
        int totalPlayerDamage = Math.max(playerDamaged - (isPlayerMalware == 1 ? playerDefensed/2 : playerDefensed),0);
        playerHP -= Math.min(totalPlayerDamage, playerHP);

        // Hacker Damage Calculation
        if("Firewall".equals(hackerCard.getName())) {
            hackerDefensed += 4;
        }
        if("Phishing".equals(playerCard.getName())) {
            hackerDamaged += 3;
            if("MFA".equals(hackerCard.getName())) {
                hackerDefensed += 2;
            }
        } else if("Malware".equals(playerCard.getName())) {
            hackerDamaged += 5;
            if(!"AntiVirus".equals(hackerCard.getName())) {
                isHackerMalware = 2;
            }
            
        } else if("Ransomware".equals(playerCard.getName())) {
            System.out.println("Hacker used Ransomware!");
            hackerDamaged += 8;
            if("Backup system".equals(hackerCard.getName())) {
                hackerDefensed += 3;
            } else if("Update / Patch".equals(hackerCard.getName())) {
                hackerDefensed += 8;
            }
        }
        int totalHackerDamage = Math.max(hackerDamaged - (isHackerMalware == 1 ? hackerDefensed/2 : hackerDefensed),0);
        hackerHP -= Math.min(totalHackerDamage, hackerHP);

        if(isPlayerMalware > 0){
            isPlayerMalware--;
        }
        if(isHackerMalware > 0){
            isHackerMalware--;
        }
    }

    // ==========================================
    // 27. SHOW DAMAGE SUMMARY
    // ==========================================

    private void showDamageSummary() {

        currentPhase = GamePhase.DAMAGE_SUMMARY;

        System.out.println(
            "========== DAMAGE SUMMARY =========="
        );

        repaint();

        phaseTimer = new Timer(SUMMARY_DURATION, e -> {

            stopPhaseTimer();

            if (playerHP <= 0 || hackerHP <= 0) {

                checkGameOver();

                repaint();

                return;
            }

            startTurn();

        });

        phaseTimer.setRepeats(false);

        phaseTimer.start();
    }

    // ==========================================
    // 28. CHECK GAME OVER
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
    // 29. DRAW PLAYER DECK
    // ==========================================

    private void drawDeck(Graphics2D g2) {

        if (cardImage == null) {
            return;
        }

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
    // 30. DRAW SYSTEM RESOURCE
    // ==========================================

    private void drawSystemResource(Graphics2D g2) {

        if (srBackground != null) {

            g2.drawImage(
                srBackground,
                SR_X,
                SR_Y,
                SR_SIZE,
                SR_SIZE,
                this
            );
        }

        String srText = String.valueOf(systemResource);

        g2.setFont(
            new Font("Tahoma", Font.BOLD, 30)
        );

        FontMetrics fm = g2.getFontMetrics();

        int textX =
            SR_X + (SR_SIZE - fm.stringWidth(srText)) / 2;

        int textY =
            SR_Y + (SR_SIZE - fm.getHeight()) / 2
                + fm.getAscent();

        // Shadow
        g2.setColor(Color.BLACK);

        g2.drawString(
            srText,
            textX + 2,
            textY + 2
        );

        // White Text
        g2.setColor(Color.WHITE);

        g2.drawString(
            srText,
            textX,
            textY
        );
    }

    // ==========================================
    // 31. DRAW HP BAR
    // ==========================================

    private void drawHP(Graphics2D g2) {

        drawSingleHPBar(
            g2,
            PLAYER_HP_X,
            HP_BAR_Y,
            playerHP,
            true
        );

        drawSingleHPBar(
            g2,
            HACKER_HP_X,
            HP_BAR_Y,
            hackerHP,
            false
        );
    }

    // ==========================================
    // 32. DRAW SINGLE HP BAR
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

        int hp = Math.max(0, Math.min(30, currentHP));

        double hpPercentage = hp / 30.0;

        int fillWidth = (int)(
            HP_INNER_WIDTH * hpPercentage
        );

        // ----------------------------------
        // DRAW HP FILL
        // ----------------------------------

        if (fillWidth > 0) {

            int barX = x + HP_INNER_X;
            int barY = y + HP_INNER_Y;

            int sourceWidth = (int)(
                hpBarImage.getWidth() * hpPercentage
            );

            int sourceX = 0;

            if (!fillFromLeft) {

                barX += HP_INNER_WIDTH - fillWidth;

                sourceX =
                    hpBarImage.getWidth() - sourceWidth;
            }

            g2.drawImage(
                hpBarImage,

                barX,
                barY,
                barX + fillWidth,
                barY + HP_INNER_HEIGHT,

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
        // DAMAGE SUMMARY
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
    // 33. DRAW TURN
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
    // 34. DRAW HAND CARD
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
    // 35. DRAW USE CARD BUTTON
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

        // Background
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

        // Text
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
    // 36. DRAW GAME OVER
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
    // 37. PAINT COMPONENT
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
        // HP
        // ----------------------------------

        drawHP(g2);

        // ----------------------------------
        // TURN
        // ----------------------------------

        drawTurn(g2);

        // ----------------------------------
        // PLAYER DECK
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

        // Draw unselected cards first
        for (int i = 0; i < hand.size(); i++) {

            if (i == selectedIndex) {
                continue;
            }

            drawCard(g2, i);
        }

        // Draw selected card last
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
