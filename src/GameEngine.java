// GameEngine.java - Minimal engine, with draw-to-screen support

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import javax.imageio.ImageIO;

public class GameEngine {
  // === ENGINE STATE ===
  public List<GameObject> gameObjects = new ArrayList<>();

  private JFrame window;
  private Canvas canvas;
  private BufferStrategy bufferStrategy;
  private final int WIDTH = 800;
  private final int HEIGHT = 600;

  // Add a game object
  public void addGameObject(GameObject obj) {
    gameObjects.add(obj);
  }

  // Create window and canvas
  public void createWindow() {
    window = new JFrame("Minimal Game Engine");
    canvas = new Canvas();
    canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    window.add(canvas);
    window.pack();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setLocationRelativeTo(null);
    window.setVisible(true);
    window.setResizable(false);
    canvas.createBufferStrategy(2);
    bufferStrategy = canvas.getBufferStrategy();
  }

  // Engine logic
  public void engineStart() {
    createWindow();

    // Initialise each object by calling its awake method
    for (GameObject object : gameObjects) {
      object.objectAwake();
    }

    // Main loop
    while (true) {
      // Loop over each object and call its update method
      for (GameObject object : gameObjects) {
        object.objectUpdate();
      }

      // Draw everything to screen
      render();

      // Delay for simple fixed frame rate (~60fps)
      try {
        Thread.sleep(16);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  // Render all objects with sprite components
  private void render() {
    Graphics g = bufferStrategy.getDrawGraphics();

    // Clear screen
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    // Draw sprite components
    for (GameObject object : gameObjects) {
      for (Component c : object.components) {
        if (c instanceof SpriteComponent) {
          SpriteComponent sprite = (SpriteComponent) c;

          if (sprite.imageData.length > 0) {
            for (int y = 0; y < sprite.imageData.length; y++) {
              for (int x = 0; x < sprite.imageData[0].length; x++) {
                int rgb = sprite.imageData[y][x];
                g.setColor(new Color(rgb, true));
                g.fillRect((int) object.positionX + x, (int) object.positionY + y, 1, 1);
              }
            }
          }
        }
      }
    }

    g.dispose();
    bufferStrategy.show();
  }

  // ==== GAME OBJECT ====
  public class GameObject {
    public int objectID;
    public List<Component> components = new ArrayList<>();

    // Basic transform properties
    public float positionX;
    public float positionY;
    public float velocityX;
    public float velocityY;

    public GameObject(int id) {
      this.objectID = id;
    }

    public void addComponent(Component c) {
      c.owner = this;
      components.add(c);
    }

    // Called once on startup
    public void objectAwake() {
      for (Component component : components) {
        component.awake();
      }
    }

    // Called every frame
    public void objectUpdate() {
      for (Component component : components) {
        component.update();
      }
    }
  }

  // ==== COMPONENTS ====
  public class Component {
    public String componentType = "root";
    public GameObject owner;

    public void awake() {}
    public void update() {}
  }

  // Sprite component - handle rendering a 2D sprite
  public class SpriteComponent extends Component {
    // Properties
    String imageFilename;
    int[][] imageData;

    // Constructor
    public SpriteComponent(String filename) {
      this.componentType = "sprite";
      this.imageFilename = filename;
      loadImageData();
    }

    // Load image data into 2D array
    private void loadImageData() {
      try {
        BufferedImage image = ImageIO.read(new File(imageFilename));

        int width = image.getWidth();
        int height = image.getHeight();
        imageData = new int[height][width];

        for (int y = 0; y < height; y++) {
          for (int x = 0; x < width; x++) {
            imageData[y][x] = image.getRGB(x, y); // Stores ARGB packed int
          }
        }
      }
      // Catch errors reading file
      catch (Exception e) {
        System.out.println("[-] Error loading image: " + imageFilename);
        imageData = new int[0][0]; // Set to blank
      }
    }

    // ==== Engine logic functions ====
    @Override
    public void awake() {
      return;
    }

    @Override
    public void update() {
      return;
    }
  }
}
