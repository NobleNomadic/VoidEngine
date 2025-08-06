// Game.java - Contains main game code
public class Game {
  public static void main(String[] args) {
    // Create game engine instance
    GameEngine engine = new GameEngine();

    // Create new object and add to engine
    GameEngine.GameObject object = engine.new GameObject(1);
    engine.addGameObject(object);

    // Start
    engine.engineStart();
  }
}
