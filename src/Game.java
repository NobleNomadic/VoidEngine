import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game extends JPanel implements Runnable {
  // ---- WINDOW SIZING ----
  // Pixel scale
  private int pixelScale = 4;
  // Scaled pixels
  private int scaledWidth = 200;
  private int scaledHeight = 150;
  // Final width
  private int width = pixelScale * scaledWidth;
  private int height = pixelScale * scaledHeight;
  // Buffer image for drawing
  private BufferedImage image;
  // Game loop state
  private boolean running = false;
  
  // Constructor
  public Game() {
    // Setup JFrame
    JFrame frame = new JFrame("Simple Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    setPreferredSize(new Dimension(width, height));
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    
    // Initialize BufferedImage
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }
  
  // Start the game loop
  public void start() {
    running = true;
    new Thread(this).start(); // Starts the game loop
  }
  
  @Override
  public void run() {
    // Call awake method
    awake();
    
    // Game loop
    while (running) {
      update();
      repaint(); // Trigger drawing
      try {
        Thread.sleep(16); // ~60 FPS (16ms per frame)
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
  
  // ---- GAME LOGIC FUNCTIONS ----
  // Awake method, runs once on startup
  private void awake() {
    clearScreen();
    
    // Test the GameObject with sprite loading
    GameObject player = new GameObject("../assets/player.bmp");
    
    // Draw the sprites if they loaded successfully
    if (player.spriteData != null) {
      drawSprite(player.spriteData, 10, 10);
    }
    return;
  }
  
  // Game loop, called once per frame
  private void update() {
    return;
  }
  
  // ---- DRAWING TO SCREEN ----
  // Draw a sprite (8x8 array) at position (x, y)
  private void drawSprite(int[][] sprite, int x, int y) {
    for (int sy = 0; sy < 8; sy++) {
      for (int sx = 0; sx < 8; sx++) {
        int pixelColor = sprite[sy][sx];
        // Skip black pixels as transparent
        if (pixelColor != Color.BLACK.getRGB()) {
          drawPixel((x + sx) * pixelScale, (y + sy) * pixelScale, pixelColor);
        }
      }
    }
  }
  
  // Draw a single pixel to a set x and y
  private void drawPixel(int x, int y, int rgb) {
    // Loop through the scaled area and set each pixel
    for (int i = 0; i < this.pixelScale; i++) {
      for (int j = 0; j < this.pixelScale; j++) {
        int pixelX = x + i;  // Offset x to draw the full block
        int pixelY = y + j;  // Offset y to draw the full block
        if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
          image.setRGB(pixelX, pixelY, rgb);  // Set the pixel color at each location
        }
      }
    }
  }
  
  // Clear the screen
  private void clearScreen() {
    Graphics2D g = image.createGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    g.dispose();
  }
  
  // Draw the image buffer to the screen
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, null); // Draw the whole image buffer
  }
  
  // Main method to start the game
  public static void main(String[] args) {
    Game game = new Game();
    game.start();  // Starts the game loop
  }

  // ---- GAME OBJECT CLASS ----
  class GameObject {
    // Properties for sprite colour data
    int[][] spriteData;  // Fixed syntax: int[][] not int[8][8]
    String spriteFilename;
    Boolean isTile;
    
    // ---- Constructor ----
    public GameObject(String spriteFilename) {
      this.spriteFilename = spriteFilename;
      // Load sprite file data into property
      this.spriteData = loadSprite(spriteFilename);
    }
    
    // Load sprite function embedded in GameObject
    private int[][] loadSprite(String filename) {
      try {
        // Load the image
        BufferedImage image = ImageIO.read(new File(filename));
        if (image == null) {
          System.err.println("Could not load: " + filename);
          // Create a default 8x8 red square if file not found
          return createDefaultSprite();
        }
        
        // Create 8x8 array
        int[][] sprite = new int[8][8];
        
        // Scale/crop the image to fit 8x8
        for (int y = 0; y < 8; y++) {
          for (int x = 0; x < 8; x++) {
            // Map 8x8 coordinates to original image coordinates
            int srcX = (x * image.getWidth()) / 8;
            int srcY = (y * image.getHeight()) / 8;
            
            // Get the pixel color
            sprite[y][x] = image.getRGB(srcX, srcY);
          }
        }
        
        System.out.println("Loaded sprite: " + filename);
        return sprite;
        
      } catch (IOException e) {
        System.err.println("Error loading " + filename + ": " + e.getMessage());
        // Return default sprite on error
        return createDefaultSprite();
      }
    }
    
    // Create a default sprite if file loading fails
    private int[][] createDefaultSprite() {
      int[][] defaultSprite = new int[8][8];
      for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++) {
          // Create a simple pattern
          if (x == 0 || x == 7 || y == 0 || y == 7) {
            defaultSprite[y][x] = Color.RED.getRGB(); // Border
          } else {
            defaultSprite[y][x] = Color.BLUE.getRGB(); // Fill
          }
        }
      }
      return defaultSprite;
    }
  }
}
