import scenes.*;
import winScene.WinScene;

import javax.swing.*;

import homeScenes.HomeScene;
import loadingScene.LoadingScene;
import loseScene.GameOverScene;

import java.awt.*;

public class Main extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

    private HomeScene homeScene;
    private LoadingScene loadingScene;
    private GameScene gameScene;

    private GameOverScene gameOverScene;
    private WinScene winScene;

    public Main() {
        setTitle("Card-O-Phobia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Home
        homeScene = new HomeScene(this::showLoading);

        // Loading
        loadingScene = new LoadingScene(this::startNewGame);

        container.add(homeScene, "HOME");
        container.add(loadingScene, "LOADING");

        add(container);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);

        cardLayout.show(container, "HOME");
    }

    // =========================
    // HOME -> LOADING
    // =========================

    private void showLoading() {
        cardLayout.show(container, "LOADING");
        loadingScene.startLoading();
    }

    // =========================
    // LOADING -> GAME
    // =========================

    private void startNewGame() {

        if (gameScene != null) {
            container.remove(gameScene);
        }

        // ส่ง Callback สำหรับแพ้และชนะ
        gameScene = new GameScene(
            this::showGameOver,
            this::showWin
        );

        container.add(gameScene, "GAME");

        cardLayout.show(container, "GAME");

        container.revalidate();
        container.repaint();
        gameScene.requestFocusInWindow();
    }

    // =========================
    // GAME -> GAME OVER
    // =========================

    private void showGameOver() {

        gameOverScene = new GameOverScene(
            this::startNewGame
        );

        container.add(gameOverScene, "GAME_OVER");

        cardLayout.show(container, "GAME_OVER");

        container.revalidate();
        container.repaint();
    }

    // =========================
    // GAME -> WIN
    // =========================

    private void showWin() {

        winScene = new WinScene(
            this::backHome
        );

        container.add(winScene, "WIN");

        cardLayout.show(container, "WIN");

        container.revalidate();
        container.repaint();
    }

    // =========================
    // WIN -> HOME
    // =========================

    private void backHome() {

        cardLayout.show(container, "HOME");

        container.revalidate();
        container.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}