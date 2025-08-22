/*
 * VoidEngine - Simple component based Java game engine
 *
 * Copyright (C) 2025 NobleNomadic
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
// GameEngine.java - Minimal engine, with draw-to-screen and input support

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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

    // === Input setup ===
    canvas.setFocusable(true);
    canvas.requestFocus(); // Ensure canvas gets keyboard focus
    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        handleKeyEvent(e, true); // Set key down
      }

      @Override
      public void keyReleased(KeyEvent e) {
        handleKeyEvent(e, false); // Set key up
      }
    });
  }

  // Handle input key events
  private void handleKeyEvent(KeyEvent e, boolean isDown) {
    for (GameObject obj : gameObjects) {
      for (Component c : obj.components) {
        if (c instanceof PlayerControllerComponent) {
          PlayerControllerComponent pc = (PlayerControllerComponent) c;

          // Movement keys (WASD and Arrow Keys)
          switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
              pc.isLeftDown = isDown;
              break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
              pc.isRightDown = isDown;
              break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
              pc.isUpDown = isDown;
              break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
              pc.isDownDown = isDown;
              break;
          }
        }
      }
    }
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

      // Delay for simple fixed frame rate (~250 fps)
      try {
        Thread.sleep(4);
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
    } // End loadImageData

    // ==== Engine logic functions ====
    @Override
    public void awake() {
      return;
    }

    @Override
    public void update() {
      return;
    }
  } // End sprite component

  // Player controller component for handling input and position updates
  public class PlayerControllerComponent extends Component {
    // Input booleans
    boolean isLeftDown = false;
    boolean isRightDown = false;
    boolean isUpDown = false;
    boolean isDownDown = false;

    // Constructor
    public PlayerControllerComponent() {
      this.componentType = "player controller";
    }

    // ==== Engine logic functions ====
    @Override
    public void awake() {
      return;
    }

    @Override
    public void update() {
      // Movement logic
      float speed = 0.5f;

      if (isLeftDown)  owner.positionX -= speed;
      if (isRightDown) owner.positionX += speed;
      if (isUpDown)    owner.positionY -= speed;
      if (isDownDown)  owner.positionY += speed;
    }
  } // End player controller component

  // Collission component for preventing moving through other colliders
  public class ColliderComponent extends Component {
    // Collider size
    public int width;
    public int height;

    // Store starting position for collision rollback
    private float startX;
    private float startY;

    // Constructor
    public ColliderComponent(int width, int height) {
      this.width = width;
      this.height = height;
    }

    // Get the bounding rectangle of this collider
    public Rectangle getBounds() {
      return new Rectangle(
        (int) owner.positionX,
        (int) owner.positionY,
        width,
        height
      );
    }

    // ==== Engine logic functions ====

    @Override
    public void awake() {
      // Nothing special yet, but could register collider in a global list if needed
    }

    @Override
    public void update() {
      // Save starting position before any other component moves the object
      if (startX == 0 && startY == 0) {
        startX = owner.positionX;
        startY = owner.positionY;
      }

      // After all movement this frame, check for collisions
      for (GameObject other : gameObjects) {
        if (other == owner) continue; // Skip self

        ColliderComponent otherCol = null;
        for (Component c : other.components) {
          if (c instanceof ColliderComponent) {
            otherCol = (ColliderComponent) c;
            break;
          }
        }

        if (otherCol != null) {
          if (getBounds().intersects(otherCol.getBounds())) {
            // Collision detected — revert to starting position
            owner.positionX = startX;
            owner.positionY = startY;
          }
        }
      }

      // Reset starting position for the next frame
      startX = owner.positionX;
      startY = owner.positionY;
    }
  } // End collider component
}
