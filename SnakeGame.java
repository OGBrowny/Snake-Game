import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener, MouseListener {
    static final int WIDTH = 600, HEIGHT = 600, SIZE = 25;
    int[] x = new int[576], y = new int[576];
    int parts = 5, apples = 0, appleX, appleY;
    char direction = 'R';
    boolean running = true, paused = false;
    Timer timer = new Timer(110, this);
    Random random = new Random();

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        for (int i = 0; i < parts; i++) {
            x[i] = 300 - i * SIZE;
            y[i] = 300;
        }
        newApple();
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawGrid(g2);
        drawApple(g2);
        drawSnake(g2);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Score: " + apples * 10, 15, 25);
        if (paused) showMessage(g2, "PAUSED", "Press P again to resume");
        if (!running) showMessage(g2, "GAME OVER", "Press R to restart");
    }

    void drawGrid(Graphics2D g) {
        g.setColor(new Color(35, 35, 35));
        for (int i = 0; i < WIDTH; i += SIZE) {
            g.drawLine(i, 0, i, HEIGHT);
            g.drawLine(0, i, WIDTH, i);
        }
    }

    void drawApple(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillOval(appleX + 2, appleY + 4, 21, 20);
        g.setColor(new Color(110, 70, 30));
        g.drawLine(appleX + 12, appleY + 5, appleX + 13, appleY);
        g.setColor(Color.GREEN);
        g.fillOval(appleX + 13, appleY, 9, 5);
    }

    void drawSnake(Graphics2D g) {
        for (int i = 0; i < parts; i++) {
            g.setColor(i == 0 ? Color.GREEN : new Color(0, 190, 70));
            g.fillRoundRect(x[i], y[i], 23, 23, 8, 8);
            if (i == 0) {
                g.setColor(Color.WHITE);
                if (direction == 'L' || direction == 'R') {
                    g.fillOval(x[i] + 13, y[i] + 4, 5, 5);
                    g.fillOval(x[i] + 13, y[i] + 14, 5, 5);
                } else {
                    g.fillOval(x[i] + 4, y[i] + 5, 5, 5);
                    g.fillOval(x[i] + 14, y[i] + 5, 5, 5);
                }
            }
        }
    }

    void showMessage(Graphics2D g, String title, String subtitle) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 42));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        fm = g.getFontMetrics();
        g.drawString(subtitle, (WIDTH - fm.stringWidth(subtitle)) / 2, HEIGHT / 2 + 40);
    }

    void newApple() {
        boolean valid;
        do {
            valid = true;
            appleX = random.nextInt(WIDTH / SIZE) * SIZE;
            appleY = random.nextInt(HEIGHT / SIZE) * SIZE;
            for (int i = 0; i < parts; i++)
                if (appleX == x[i] && appleY == y[i]) valid = false;
        } while (!valid);
    }

    void move() {
        for (int i = parts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= SIZE; break;
            case 'D': y[0] += SIZE; break;
            case 'L': x[0] -= SIZE; break;
            case 'R': x[0] += SIZE; break;
        }
    }

    void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            parts++;
            apples++;
            newApple();
        }
    }

    void checkCollision() {
        for (int i = parts - 1; i > 0; i--)
            if (x[0] == x[i] && y[0] == y[i]) running = false;
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT)
            running = false;
        if (!running) timer.stop();
    }

    void restart() {
        timer.stop();
        parts = 5;
        apples = 0;
        direction = 'R';
        for (int i = 0; i < parts; i++) {
            x[i] = 300 - i * SIZE;
            y[i] = 300;
        }
        newApple();
        running = true;
        paused = false;
        timer.start();
        requestFocusInWindow();
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_R && !running) { restart(); return; }
        if (!running) return;
        if (k == KeyEvent.VK_P) { paused = !paused; repaint(); return; }
        if (paused) return;
        if ((k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) && direction != 'R') direction = 'L';
        if ((k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) && direction != 'L') direction = 'R';
        if ((k == KeyEvent.VK_UP || k == KeyEvent.VK_W) && direction != 'D') direction = 'U';
        if ((k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) && direction != 'U') direction = 'D';
    }

    public void mouseClicked(MouseEvent e) {
        if (!running) restart();
        else { paused = !paused; repaint(); }
        requestFocusInWindow();
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}