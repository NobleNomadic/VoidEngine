// Game.java - Contains main game code
public class Game {
  public static void main(String[] args) {
    // Create game engine instance
    GameEngine engine = new GameEngine();

    // Create new object to add to engine
    GameEngine.GameObject object = engine.new GameObject(1);

    // Give object player controller and sprite
    object.addComponent(engine.new PlayerControllerComponent());
    object.addComponent(engine.new SpriteComponent("/home/nomad/Code/VoidEngine/src/assets/test.png"));

    engine.addGameObject(object);

    // Start
    engine.engineStart();
  }
}
