package main;

import entity.Monster;
import entity.Player;
import input.KeyHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    private final int OriginalTileSize = 16; // Default Objects are 16x16 pixels per tile
    private final int Scale = 3;
    private int FPS = 60; // Create game FPS
    private Thread gameThread; // Create game thread
    
    public final int TileSize = OriginalTileSize * Scale; //Scale to 48*48 pixels per tile

    // World setting
    public final int maxWorldCol = 151;
    public final int maxWorldRow = 12;
    public final int WorldWidth = TileSize * maxWorldCol; //768 pixels
    public final int WorldHeight = TileSize * maxWorldRow; //576 pixels

    // Create Player Views
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int ScreenWidth = TileSize * maxScreenCol; //960 pixels
    public final int ScreenHeight = TileSize * maxScreenRow; //576 pixels

    public int cameraX, cameraY = 0;

    //Game state
    public final int playState = 1;
    public final int pauseState = 2;
    public final int gameOverState = 3;
    public final int victoryState = 4;

    public int currentState = playState;

    public int countdownTime = 180; // 3 minutes
    public Timer gameTimer;

    public TileManager tile = new TileManager(this); //Instance create tile manager
    KeyHandler key = new KeyHandler(this); //Instance create key handler
    public Player player = new Player(this, key); //Instance create player
    public CollisionDetector cod = new CollisionDetector(this); //Instance create collision detector;
    public ArrayList<Monster> monsters = new ArrayList<>(); //Instance create monsters

    public GamePanel() {

        this.setPreferredSize(new Dimension(ScreenWidth, ScreenHeight)); //Set the size of JPanel
        this.setBackground(Color.black); //Set background color
        this.setDoubleBuffered(true); //This can improve game rendering performance
        this.addKeyListener(key); //Add key handler to game panel
        this.setFocusable(true); //Make this game panel can be focused to receive key input

        this.setupMonsters(); //Method to set up monsters on the map
        this.StartGameTimer(); //Method to start timer
    }

    public void setupMonsters() {
        monsters.clear(); // Clear existing monsters

        for (int row = 0; row < maxWorldRow; row++) {
            for (int col = 0; col < maxWorldCol; col++) {
                // Check if the current tile is a spawn point for monsters
                if (tile.mapTileNum[col][row] == 20) {
                    monsters.add(new Monster(col * TileSize, row * TileSize, TileSize));
                }
            }
        }
    }

    public void resetCoins() {
        // Respawn coins
        for (int row = 0; row < maxWorldRow; row++) {
            for (int col = 0; col < maxWorldCol; col++) {
                if (tile.mapTileNum[col][row] == 16) {
                    tile.mapTileNum[col][row] = 15;
                }
            }
        }
    }

    public void checkMonsterCollision() {
        for (Monster monster : monsters) {
            // Check if the player's hitbox intersects with the monster's hitbox
            if (player.solidArea.intersects(monster.solidArea)) {
                // Only process collision if the player is not already invulnerable
                if (!player.isInvulnerable) {
                    // Decrease player HP
                    player.loseHP();
                    // Add invulnerability after collision
                    player.isInvulnerable = true;
                }
            }
        }
    }

    public void StartGameTimer() {

        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (countdownTime > 0) {
                    countdownTime--;
                } else {
                    // Stop the timer and set game over state when time runs out
                    gameTimer.stop();
                    currentState = gameOverState;
                }
            }
        });
        gameTimer.start(); // Start the timer
    }

    public void StartGameThreads() {

        gameThread = new Thread(this); //Pass this class to Thread
        gameThread.start();
    }

    public void updateCamera() {
        int playerCenterX = player.x + (TileSize / 2);
        int playerCenterY = player.y + (TileSize / 2);

        // Calculate cameraX
        cameraX = playerCenterX - (ScreenWidth / 2);
        cameraY = playerCenterY - (ScreenHeight / 2);

        // Clamp the camera position
        if (cameraX < 0) {
            cameraX = 0;
        }
        if (cameraY < 0) {
            cameraY = 0;
        }
        if (cameraX > (WorldWidth - ScreenWidth)) {
            cameraX = WorldWidth - ScreenWidth;
        }
        if (cameraY > (WorldHeight - ScreenHeight)) {
            cameraY = WorldHeight - ScreenHeight;
        }
    }

    public void update() {

        if (currentState == playState) {
            player.update();
            updateCamera();
            for (Monster monster : monsters) {
                monster.update();
            }
        } else {
            // Stop the timer if the game is paused or in another state
            gameTimer.stop();
        }

        int playerTileCol = player.x / TileSize;
        int playerTileRow = player.y / TileSize;
        if (tile.mapTileNum[playerTileCol][playerTileRow] == 99) {
            currentState = victoryState;
            gameTimer.stop();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Draw components
        Graphics2D g2 = (Graphics2D) g;

        g2.translate(-cameraX, -cameraY); // Set draw area relate to player camera

        if (currentState == playState) { // UI when game state is play state
            tile.draw(g2); // Draw tiles
            for (Monster monster : monsters) {
                monster.draw(g2); // Draw monsters
            }
            player.draw(g2); // Draw player character
            // Display timer
            g2.setColor(Color.black);
            g2.setFont(new Font("Calibri", Font.BOLD, 25));
            g2.drawString("Time: " + countdownTime, 50 + cameraX, 50 + cameraY);
        } 
        
        else if (currentState == pauseState) { // UI when game state is play state
            tile.draw(g2);
            for (Monster monster : monsters) {
                monster.draw(g2);
            }
            player.draw(g2);
            popupOverlay(g2);
            drawPauseScreen(g2);
        } 
        
        else if (currentState == gameOverState) {
            tile.draw(g2);
            for (Monster monster : monsters) {
                monster.draw(g2);
            }
            player.draw(g2);
            popupOverlay(g2);
            drawGameOverScreen(g2);
        } 
        
        else if (currentState == victoryState) {
            tile.draw(g2);
            for (Monster monster : monsters) {
                monster.draw(g2);
            }
            player.draw(g2);
            g2.setColor(Color.black);
            g2.fillRect(0, 0, ScreenWidth + cameraX, ScreenHeight + cameraY);
            drawVictoryScreen(g2);
        }
        
        g2.dispose();
    }

    private void popupOverlay(Graphics2D g2) {
        Color overlayColor = new Color(0, 0, 0, 200);  // RGBA, where A (Alpha) controls transparency

        g2.setColor(overlayColor);
        g2.fillRect(0, 0, ScreenWidth + cameraX, ScreenHeight + cameraY);
    }

    private void drawPauseScreen(Graphics2D g2) {
        // Set texts
        String message = "Game Pause"; 
        String instruction = "Press 'P' to Resume";
        
        // Set font and color
        g2.setColor(Color.white);
        g2.setFont(new Font("Calibri", Font.BOLD, 55));
        
        // Setup message at the center of the screen
        FontMetrics metrics = g2.getFontMetrics();
        int xMessage = (ScreenWidth - metrics.stringWidth(message)) / 2;
        int yMessage = ScreenHeight / 2 - 50;
        
        // Draw text1
        g2.drawString(message, xMessage + cameraX, yMessage + cameraY);
        
        // Set new font 
        g2.setFont(new Font("Calibri", Font.PLAIN, 30));
        
        // Setup message at the center of the screen (relate to new font)
        metrics = g2.getFontMetrics();
        int xInstruction = (ScreenWidth - metrics.stringWidth(instruction)) / 2;
        int yInstruction = ScreenHeight / 2 + 30;
        
        // Draw text2
        g2.drawString(instruction, xInstruction + cameraX, yInstruction + cameraY);

        // Display timer
        g2.setColor(Color.black);
        g2.setFont(new Font("Calibri", Font.BOLD, 25));
        g2.drawString("Time: " + countdownTime, 50 + cameraX, 50 + cameraY);
    }

    private void drawGameOverScreen(Graphics2D g2) {
        // Set texts
        String message = "Game Over!";
        String instruction = "Press 'R' to Retry";
        
        // Set font and color
        g2.setColor(Color.white);
        g2.setFont(new Font("Calibri", Font.BOLD, 55));
        
        // Setup message at the center of the screen
        FontMetrics metrics = g2.getFontMetrics();
        int xMessage = (ScreenWidth - metrics.stringWidth(message)) / 2;
        int yMessage = ScreenHeight / 2 - 50;

        // Draw text1
        g2.drawString(message, xMessage + cameraX, yMessage + cameraY);

        // Set new font
        g2.setFont(new Font("Calibri", Font.PLAIN, 30));

        // Setup message at the center of the screen (relate to new font)
        metrics = g2.getFontMetrics();
        int xInstruction = (ScreenWidth - metrics.stringWidth(instruction)) / 2;
        int yInstruction = ScreenHeight / 2 + 30;

        // Draw text2
        g2.drawString(instruction, xInstruction + cameraX, yInstruction + cameraY);

        // Display timer
        g2.setColor(Color.black);
        g2.setFont(new Font("Calibri", Font.BOLD, 25));
        g2.drawString("Time: " + countdownTime, 50 + cameraX, 50 + cameraY);
    }

    private void drawVictoryScreen(Graphics2D g2) {
        // Set texts
        String message1 = "Congrat! You become the new Potato KING!";
        String message2 = "Your Score: "+player.score;
        String message3 = "Time Left: "+countdownTime;
        // Set image
        Image MarkV = new ImageIcon(getClass().getResource("/UI/MarkVictory.png")).getImage();
        // Set font and color
        g2.setColor(Color.yellow);
        g2.setFont(new Font("Calibri", Font.BOLD, 45));
        // Setup message at the center of the screen
        FontMetrics metrics = g2.getFontMetrics();
        int xMessage = (ScreenWidth - metrics.stringWidth(message1)) / 2;
        int yMessage = ScreenHeight / 2 - 170;
        g2.drawString(message1, xMessage + cameraX, yMessage + cameraY);
        // Set image position
        int imageWidth = 96;
        int imageHeight = 120;
        int xImage = (ScreenWidth - imageWidth) / 2 + cameraX;
        int yImage = yMessage + 70 + cameraY;
        // Draw the victory image
        g2.drawImage(MarkV, xImage, yImage, imageWidth, imageHeight, null);
        g2.setColor(Color.white);
        g2.setFont(new Font("Calibri", Font.BOLD, 30));
        metrics = g2.getFontMetrics();
        // Display time and score
        int xScoreM = (ScreenWidth - metrics.stringWidth(message2)) / 2;
        int yScoreM = ScreenHeight / 2 + 120;
        g2.drawString(message2, xScoreM + cameraX, yScoreM + cameraY);
        int xTimeM = (ScreenWidth - metrics.stringWidth(message3)) / 2;
        int yTimeM = ScreenHeight / 2 + 200;
        g2.drawString(message3, xTimeM + cameraX, yTimeM + cameraY);
    }

    @Override // Overridden run method from runnable interface
    public void run() {
        // Use for create game loop
        double drawInterval = 1000000000 / FPS; //Draw every 0.01666 sec
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            //Update game informations
            update();

            //Draw the screen with update informations
            repaint();

            //Sleep method
            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime /= 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException ex) {
                Logger.getLogger(GamePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
