package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Monster extends Entity {
    // To make these attributes private and can only access in this class
    private BufferedImage image;
    private BufferedImage Mleft1, Mleft2, Mleft3, Mleft4, Mleft5, Mleft6;
    private BufferedImage Mright1, Mright2, Mright3, Mright4, Mright5, Mright6;

    private int hitboxWidth; 
    private int hitboxHeight;

    private int patrolLimit = 3 * 48;  // Patrol limit in pixels
    private int currentPatrolDistance = 0; // Distance the monster has moved during the patrol
    private boolean movingLeft = true; // Direction of movement
    private int speed = 1; // Speed of movement
    
    private int animationFrame = 0; // Animation frame counter
    private long lastUpdateTime = System.nanoTime(); // For timing the frame updates
    private final long frameDelay = 200000000; // Delay in nanoseconds between frame changes

    public Monster(int x, int y, int tileSize) {
        // Set monster default position
        this.x = x;
        this.y = y;

        // Set size of monster hitbox
        this.hitboxWidth = tileSize;
        this.hitboxHeight = tileSize;

        this.solidArea = new Rectangle( x, y, hitboxWidth, hitboxHeight); // Create monster hitbox
        loadMonsterImages(); // Method to get monster image
    }
    
    private void solidAreaUpdate(){
        this.solidArea.x = x;
        this.solidArea.y = y;
    }

    // Load monster images for both left and right movements
    private void loadMonsterImages() {
        try {
            // Left-facing images
            Mleft1 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left1.png"));
            Mleft2 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left2.png"));
            Mleft3 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left3.png"));
            Mleft4 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left4.png"));
            Mleft5 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left5.png"));
            Mleft6 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Left6.png"));

            // Right-facing images
            Mright1 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right1.png"));
            Mright2 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right2.png"));
            Mright3 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right3.png"));
            Mright4 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right4.png"));
            Mright5 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right5.png"));
            Mright6 = ImageIO.read(getClass().getResourceAsStream("/monsters/Rimuru_Right6.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update method for patrolling logic
    public void update() {
        // Move the monster
        if (movingLeft) {
            x -= speed;  // Move left
            currentPatrolDistance += speed; // Increase patrol distance
        } else {
            x += speed;  // Move right
            currentPatrolDistance += speed; // Increase patrol distance
        }

        // Reverse direction if the patrol limit is reached
        if (currentPatrolDistance >= patrolLimit) {
            movingLeft = !movingLeft;  // Reverse the direction
            currentPatrolDistance = 0; // Reset the patrol distance
        }
        solidAreaUpdate();
        // Set the correct animation frame based on direction
        setMonsterImage();
    }

    private void setMonsterImage() {
        // Only update the animation every frameDelay nanoseconds
        long now = System.nanoTime();
        if (now - lastUpdateTime >= frameDelay) {
            animationFrame++;
            if (animationFrame > 5) { // We have 6 frames per direction
                animationFrame = 0; // Reset the animation loop
            }
            lastUpdateTime = now;
        }

        if (movingLeft) {
            switch (animationFrame) {
                case 0:
                    image = Mleft1;
                    break;
                case 1:
                    image = Mleft2;
                    break;
                case 2:
                    image = Mleft3;
                    break;
                case 3:
                    image = Mleft4;
                    break;
                case 4:
                    image = Mleft5;
                    break;
                case 5:
                    image = Mleft6;
                    break;
            }
        } else {
            switch (animationFrame) {
                case 0:
                    image = Mright1;
                    break;
                case 1:
                    image = Mright2;
                    break;
                case 2:
                    image = Mright3;
                    break;
                case 3:
                    image = Mright4;
                    break;
                case 4:
                    image = Mright5;
                    break;
                case 5:
                    image = Mright6;
                    break;
            }
        }
    }

    // Draw the monster
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, solidArea.width, solidArea.height, null);
        g2.setColor(Color.red);
        g2.drawRect(solidArea.x, solidArea.y, solidArea.width, solidArea.height);  // Optional hitbox display
    }
}
