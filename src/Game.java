// Game.java - Contains main game code
public class Game {
  public static void main(String[] args) {
    // Create game engine instance
    GameEngine engine = new GameEngine();

    // === Player object ===
    GameEngine.GameObject player = engine.new GameObject(1);
    player.positionX = 100;
    player.positionY = 100;
    player.addComponent(engine.new PlayerControllerComponent());
    player.addComponent(engine.new SpriteComponent("/home/nomad/Code/VoidEngine/src/assets/test.png"));
    player.addComponent(engine.new ColliderComponent(8, 8));
    engine.addGameObject(player);

    // === Static obstacle #1 ===
    GameEngine.GameObject wall1 = engine.new GameObject(2);
    wall1.positionX = 300;
    wall1.positionY = 200;
    wall1.addComponent(engine.new SpriteComponent("/home/nomad/Code/VoidEngine/src/assets/test.png"));
    wall1.addComponent(engine.new ColliderComponent(8, 8));
    engine.addGameObject(wall1);

    // === Static obstacle #2 ===
    GameEngine.GameObject wall2 = engine.new GameObject(3);
    wall2.positionX = 400;
    wall2.positionY = 350;
    wall2.addComponent(engine.new SpriteComponent("/home/nomad/Code/VoidEngine/src/assets/test.png"));
    wall2.addComponent(engine.new ColliderComponent(8, 8));
    engine.addGameObject(wall2);

    // Start the engine
    engine.engineStart();
  }
}
