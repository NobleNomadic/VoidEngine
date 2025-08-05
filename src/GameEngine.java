// GameEngine.java - Main engine, GameEngine class represents a full game structure

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class GameEngine {
  // === ENGINE STATE ===
  public List<GameObject> gameObjects = new ArrayList<>();

  // Add a game object
  public void addGameObject(GameObject obj) {
    gameObjects.add(obj);
  }

  // Engine logic
  public void engineStart() {
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

      // Delay for simple fixed frame rate (~60fps)
      try {
        Thread.sleep(16);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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
        javax.imageio.ImageIO.setUseCache(false);
        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(new java.io.File(imageFilename));

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
      catch (java.io.IOException e) {
        System.out.println("[-] Error loading image: " + imageFilename);
        imageData = new int[0][0]; // Set to blank
      }
    }
  }
}
