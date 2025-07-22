import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TestGameWorld extends JPanel implements ActionListener, KeyListener {

    private final int TILE_SIZE = 32;
    private final int WIDTH = 800;
    private final int HEIGHT = 576;
    private final int SPEED = 4;

    private int[][] kart = TestGameMap.getKart();
    private Timer timer;

    private int playerX = 18; // tile-posisjon
    private int playerY = 0;
    private int pixelX = playerX * TILE_SIZE;
    private int pixelY = playerY * TILE_SIZE;
    private int targetX = pixelX;
    private int targetY = pixelY;
    private boolean inTransition = false;

    private BufferedImage[][] animasjoner = new BufferedImage[4][3];
    private BufferedImage spillerStille;
    private BufferedImage nullBilde;
    private BufferedImage gressBilde;
    private BufferedImage byggningBilde;
    private BufferedImage traeBilde;
    private BufferedImage vinduBilde;
    private BufferedImage vinduRetningBilde;
    private BufferedImage dørBilde;
    private BufferedImage dørToBilde;
    private BufferedImage byggningStiBilde;
    private BufferedImage veiBilBilde;
    private BufferedImage veiBilToBilde;

    private int retning = 0;
    private int animasjonsFrame = 0;
    private int animasjonsTeller = 0;
    private boolean bevegerSeg = false;

    public TestGameWorld() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(1000 / 60, this); // 60 FPS for jevnhet
        timer.start();

        try {
            spillerStille = ImageIO.read(new File("sprites/spiller.png"));
            BufferedImage ned = ImageIO.read(new File("sprites/spillerNed.png"));
            BufferedImage venstre = ImageIO.read(new File("sprites/spillerVenstre.png"));
            BufferedImage hoyre = ImageIO.read(new File("sprites/spillerHøyre.png"));
            BufferedImage opp = ImageIO.read(new File("sprites/spillerOpp.png"));

            nullBilde = ImageIO.read(new File("sprites/0=0.png"));
            gressBilde = ImageIO.read(new File("sprites/gress.png"));
            byggningBilde = ImageIO.read(new File("sprites/byggning.png"));
            traeBilde = ImageIO.read(new File("sprites/trær.png"));
            vinduBilde = ImageIO.read(new File("sprites/vindu.png"));
            vinduRetningBilde = ImageIO.read(new File("sprites/vinduretning.png"));
            dørBilde = ImageIO.read(new File("sprites/dør.png"));
            dørToBilde = ImageIO.read(new File("sprites/dørto.png"));
            byggningStiBilde = ImageIO.read(new File("sprites/byggningsti.png"));
            veiBilBilde = ImageIO.read(new File("sprites/veiBil.png"));
            veiBilToBilde = ImageIO.read(new File("sprites/veiBilTo.png"));

            for (int i = 0; i < 3; i++) {
                animasjoner[0][i] = ned.getSubimage(i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
                animasjoner[1][i] = venstre.getSubimage(i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
                animasjoner[2][i] = hoyre.getSubimage(i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
                animasjoner[3][i] = opp.getSubimage(i * TILE_SIZE, 0, TILE_SIZE, TILE_SIZE);
            }

        } catch (IOException e) {
            System.out.println("Feil ved lasting av bilder");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < kart.length; y++) {
            for (int x = 0; x < kart[0].length; x++) {
                switch (kart[y][x]) {
                    case 0 -> g.drawImage(nullBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 1 -> g.drawImage(byggningBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 2 -> g.drawImage(gressBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 3 -> g.drawImage(traeBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 4 -> g.drawImage(vinduBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 5 -> g.drawImage(vinduRetningBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 6 -> g.drawImage(dørBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 7 -> g.drawImage(dørToBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 8 -> g.drawImage(byggningStiBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 9 -> g.drawImage(veiBilBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    case 10 -> g.drawImage(veiBilToBilde, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    default -> {
                        g.setColor(Color.MAGENTA);
                        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        BufferedImage bilde = bevegerSeg ? animasjoner[retning][animasjonsFrame] : spillerStille;
        g.drawImage(bilde, pixelX, pixelY, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inTransition) {
            if (pixelX < targetX) pixelX += SPEED;
            if (pixelX > targetX) pixelX -= SPEED;
            if (pixelY < targetY) pixelY += SPEED;
            if (pixelY > targetY) pixelY -= SPEED;

            if (Math.abs(pixelX - targetX) < SPEED && Math.abs(pixelY - targetY) < SPEED) {
                pixelX = targetX;
                pixelY = targetY;
                inTransition = false;
                bevegerSeg = false;
                animasjonsFrame = 0;
                animasjonsTeller = 0;
            } else {
                animasjonsTeller++;
                if (animasjonsTeller % 5 == 0) {
                    animasjonsFrame = (animasjonsFrame + 1) % 3;
                }
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inTransition) return;

        int nyX = playerX;
        int nyY = playerY;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> { nyY--; retning = 3; }
            case KeyEvent.VK_DOWN -> { nyY++; retning = 0; }
            case KeyEvent.VK_LEFT -> { nyX--; retning = 1; }
            case KeyEvent.VK_RIGHT -> { nyX++; retning = 2; }
            default -> { return; }
        }

        if (TestGameMap.erGaaBaar(nyX, nyY)) {
            playerX = nyX;
            playerY = nyY;
            targetX = playerX * TILE_SIZE;
            targetY = playerY * TILE_SIZE;
            inTransition = true;
            bevegerSeg = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Studentbolig – Spillverden");
        TestGameWorld world = new TestGameWorld();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(world);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

