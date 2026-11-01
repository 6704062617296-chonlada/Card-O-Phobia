
import javax.swing.*;
import scenes.GameScene;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Card-O-Phobia");

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.setContentPane(new GameScene());

            frame.pack();

            frame.setLocationRelativeTo(null);

            frame.setResizable(false);

            frame.setVisible(true);
        });
    }
}
