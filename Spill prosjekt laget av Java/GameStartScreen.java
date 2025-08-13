import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GameStartScreen extends JPanel {

    private boolean startPressed = false;
    private BufferedImage backgroundImage;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final Font TITTEL_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Font TEKST_FONT = new Font("Arial", Font.PLAIN, 24);

    public GameStartScreen() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);

        // Laster bildet
        try {
            File bildeFil = new File("bilder/ny_usn_bakgrunn.png");
            backgroundImage = ImageIO.read(bildeFil);
            if (backgroundImage == null) {
                System.out.println("OBS: Bildet ble ikke lastet.");
            }
        } catch (IOException e) {
            System.out.println("Feil under lasting av bilde.");
            e.printStackTrace();
        }

        // Tastaturkontroll
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startPressed = true;
                    repaint();

                    // Lukk startmeny
                    SwingUtilities.getWindowAncestor(GameStartScreen.this).dispose();

                    // Start spillverden
                    SwingUtilities.invokeLater(() -> {
                        JFrame gameFrame = new JFrame("Spillverden – Student hos USN");
                        GameWorld game = new GameWorld();
                        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gameFrame.getContentPane().add(game);
                        gameFrame.pack();
                        gameFrame.setLocationRelativeTo(null);
                        gameFrame.setVisible(true);
                    });
                }
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Bakgrunn
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        // Tekst
        g.setColor(Color.WHITE);
        g.setFont(TITTEL_FONT);
        g.drawString("Student hos USN i Bø", 120, 80);

        g.setFont(TEKST_FONT);
        if (!startPressed) {
            g.drawString("Trykk ENTER for å starte", 170, 420);
        } else {
            g.drawString("Starter...", 250, 420);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Startmeny");
        GameStartScreen menu = new GameStartScreen();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(menu);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}



