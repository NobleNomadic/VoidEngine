import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Game extends JPanel implements Runnable {
  // ---- WINDOW SIZING ----
  private final int pixelScale = 4;
  private final int scaledWidth = 200;
  private final int scaledHeight = 150;
  private final int width = pixelScale * scaledWidth;
  private final int height = pixelScale * scaledHeight;

  private BufferedImage image;
  private boolean running = false;

  private List<GameObject> gameObjects = new ArrayList<>();

  // Constructor
  public Game() {
    JFrame frame = new JFrame("Simple Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    setPreferredSize(new Dimension(width, height));
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // Setup key listener for player control
    setFocusable(true);
    requestFocusInWindow();
  }

  public void start() {
    running = true;
    new Thread(this).start();
  }

  @Override
  public void run() {
    awake();

    while (running) {
      update();
      repaint();

      try {
        Thread.sleep(16); // ~60 FPS
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void awake() {
    clearScreen();

    GameObject player = new GameObject(0);
    SpriteComponent sprite = new SpriteComponent("../assets/sprites/player.png", 50, 100);
    player.components.add(sprite);

    PlayerControllerComponent controller = new PlayerControllerComponent(player);
    player.components.add(controller);

    gameObjects.add(player);

    // Add controller as key listener to the game panel
    addKeyListener(controller);
  }

  private void update() {
    clearScreen();

    for (GameObject obj : gameObjects) {
      // Call input handling if player controller present
      for (ObjectComponent comp : obj.components) {
        if (comp instanceof PlayerControllerComponent) {
          ((PlayerControllerComponent) comp).handleInput();
        }
      }
      // Render sprites
      for (ObjectComponent comp : obj.components) {
        if (comp instanceof SpriteComponent) {
          ((SpriteComponent) comp).renderSprite(this);
        }
      }
    }
  }

  // Draw  a pixel to an X and Y point
  public void drawPixel(int x, int y, int rgb) {
    for (int i = 0; i < pixelScale; i++) {
      for (int j = 0; j < pixelScale; j++) {
        int pixelX = x + i;
        int pixelY = y + j;
        if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
          image.setRGB(pixelX, pixelY, rgb);
        }
      }
    }
  }

  // Wipe the entire screen to a black screen
  private void clearScreen() {
    Graphics2D g = image.createGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    g.dispose();
  }

  // Paint component helper
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, null);
  }

  // Entry point
  public static void main(String[] args) {
    Game game = new Game();
    game.start();
  }

  // ---- GAME OBJECT ----
  class GameObject {
    // Have an object ID and a list of components
    int objectID;
    List<ObjectComponent> components = new ArrayList<>();

    public GameObject(int objectID) {
      this.objectID = objectID;
    }
  }

  // ---- ROOT COMPONENT ----
  class ObjectComponent {
    int componentType = 0;
    int positionX;
    int positionY;
  }

  // ---- SPRITE COMPONENT ----
  class SpriteComponent extends ObjectComponent {
    int[][] spriteData;
    String spriteFilename;

    public SpriteComponent(String spriteFilename, int positionX, int positionY) {
      this.componentType = 1;
      this.spriteFilename = spriteFilename;
      this.positionX = positionX;
      this.positionY = positionY;

      this.spriteData = loadSpriteFile();
    }

    private int[][] loadSpriteFile() {
      try {
        BufferedImage spriteImage = ImageIO.read(new File(spriteFilename));
        int width = spriteImage.getWidth();
        int height = spriteImage.getHeight();
        int[][] data = new int[height][width];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            data[y][x] = spriteImage.getRGB(x, y);
          }
        }
        return data;
      } catch (IOException e) {
        System.err.println("Error loading sprite: " + spriteFilename);
        e.printStackTrace();
        return new int[0][0];
      }
    }

    public void renderSprite(Game gameInstance) {
      for (int y = 0; y < spriteData.length; y++) {
        for (int x = 0; x < spriteData[0].length; x++) {
          int color = spriteData[y][x];
          gameInstance.drawPixel(
            positionX + x * gameInstance.pixelScale,
            positionY + y * gameInstance.pixelScale,
            color
          );
        }
      }
    }
  }

  // ---- PLAYER CONTROLLER COMPONENT ----
  public class PlayerControllerComponent extends ObjectComponent implements KeyListener {
    boolean isMoving = false;
    boolean inAir = false;

    boolean rightKeyDown = false;
    boolean leftKeyDown = false;
    boolean jumpKeyDown = false;
    boolean shootKeyDown = false;
    boolean meleeKeyDown = false;

    private final int speed = 4;
    private final int jumpStrength = 15;
    private int verticalVelocity = 0;
    private final int gravity = 1;

    GameObject owner;

    public PlayerControllerComponent(GameObject owner) {
      this.owner = owner;

      // Initialize position from SpriteComponent if exists
      this.positionX = owner.components.stream()
                          .filter(c -> c instanceof SpriteComponent)
                          .mapToInt(c -> c.positionX)
                          .findFirst().orElse(0);
      this.positionY = owner.components.stream()
                          .filter(c -> c instanceof SpriteComponent)
                          .mapToInt(c -> c.positionY)
                          .findFirst().orElse(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
      setKey(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
      setKey(e, false);
    }

    @Override
    public void keyTyped(KeyEvent e) {
      // Not used, but required by interface
    }

    private void setKey(KeyEvent e, boolean pressed) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
          rightKeyDown = pressed;
          break;
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_A:
          leftKeyDown = pressed;
          break;
        case KeyEvent.VK_W:
          jumpKeyDown = pressed;
          break;
        case KeyEvent.VK_ENTER:
          shootKeyDown = pressed;
          break;
        case KeyEvent.VK_E:
          meleeKeyDown = pressed;
          break;
      }
    }

    public void handleInput() {
      isMoving = false;

      // Horizontal movement
      if (rightKeyDown) {
        moveX(speed);
        isMoving = true;
      }
      if (leftKeyDown) {
        moveX(-speed);
        isMoving = true;
      }

      // Vertical movement (gravity + jump)
      if (inAir) {
        verticalVelocity -= gravity;
        moveY(-verticalVelocity);

        if (positionY >= 100) { // ground level (arbitrary)
          positionY = 100;
          verticalVelocity = 0;
          inAir = false;
          setSpriteY(positionY);
        }
      } else if (jumpKeyDown) {
        inAir = true;
        verticalVelocity = jumpStrength;
      }

      // Actions
      if (shootKeyDown) {
        System.out.println("Shoot action triggered");
        // TODO: Implement shooting logic
      }
      if (meleeKeyDown) {
        System.out.println("Melee action triggered");
        // TODO: Implement melee logic
      }
    }

    private void moveX(int dx) {
      positionX += dx;
      owner.components.stream()
        .filter(c -> c instanceof SpriteComponent)
        .forEach(c -> c.positionX += dx);
    }

    private void moveY(int dy) {
      positionY += dy;
      owner.components.stream()
        .filter(c -> c instanceof SpriteComponent)
        .forEach(c -> c.positionY += dy);
    }

    private void setSpriteY(int y) {
      owner.components.stream()
        .filter(c -> c instanceof SpriteComponent)
        .forEach(c -> c.positionY = y);
    }
  }
}
