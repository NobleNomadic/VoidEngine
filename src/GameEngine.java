// GameEngine.java - Main engine, GameEngine class represents a full game structure

import java.util.ArrayList;
import java.util.List;

public class GameEngine {
  // === ENGINE STATE ===
  public List<GameObject> gameObjects = new ArrayList<>();

  // Add a game object
  public void addGameObject(GameObject obj) {
    gameObjects.add(obj);
  }

  // ==== GAME OBJECT ====
  public class GameObject {
    // Properties
    // Object ID
    public int objectID;
    // Array of components
    public List<Component> components = new ArrayList<>();

    // Basic transform properties
    float positionX;
    float positionY;

    float velocityX;
    float velocityY;

    // Constructor
    public GameObject(int id) {
      this.objectID = id;
    }

    public void addComponent(Component c) {
      c.owner = this;
      components.add(c);
    }
  }

  // ==== COMPONENTS ====
  // Base component
  public class Component {
    // Basic properties
    public String componentType = "root";
    public GameObject owner;
  }
}
