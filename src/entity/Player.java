package entity;

import input.KeyHandler;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Player extends Entity {
    
    // To make these attributes private and can only access in this class
    private BufferedImage up1, up2, left1, left2, right1, right2;
    private long lastDamageTime = 0; // Time of last damage
    private final long damageCooldown = 1500; // 1.5 seconds cooldown
    
    public int score = 0;
    public int hp;
    public final int defaultSpeed = 5;
    public final int jumpStrength = - 15;
    public boolean isOnTheGround = false;
    public boolean stepOnMud = false;
    public int dy = 0;
    public boolean isInvulnerable = false; // To check if player is invulnerable

    GamePanel gp;
    KeyHandler key;

    public Player(GamePanel gp, KeyHandler key) {
        
        // Use for refferences
        this.gp = gp;
        this.key = key;

        setDefault(); // Method to set player's attribute to default
        getPlayerImg(); // Method to get player Image

        solidArea = new Rectangle(x, y, gp.TileSize, gp.TileSize); // Create player hitbox
    }

    public void setDefault() {

        x = 100;
        y = 200;
        hp = 5;
        speed = defaultSpeed;
        score = 0;
        direction = "Right";

    }
    
    private void updatePlayerSolidArea(){
        this.solidArea.x = x;
        this.solidArea.y = y;
    }

    public void loseHP() {

        if (gp.currentState != gp.pauseState) {

            long currentTime = System.currentTimeMillis();
            if (!isInvulnerable && (currentTime - lastDamageTime >= damageCooldown)) {
                hp--;
                lastDamageTime = currentTime;
                isInvulnerable = true; // Set invulnerability state

                if (hp <= 0) {
                    gp.currentState = gp.gameOverState;
                    System.out.println("You're ded!");
                }
            }
        }
    }

    public void getPlayerImg() {

        try {

            up1 = ImageIO.read(getClass().getResourceAsStream("/player/Jump1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/Jump2.png"));

            left1 = ImageIO.read(getClass().getResourceAsStream("/player/Left_Walk1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/Left_Walk2.png"));

            right1 = ImageIO.read(getClass().getResourceAsStream("/player/Right_Walk1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/Right_Walk2.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void jump() {
        if (!TopCollision && isOnTheGround) {
            dy = jumpStrength;
            isOnTheGround = false;
        }
    }

    public void update() {
        
        updatePlayerSolidArea();

        long currentTime = System.currentTimeMillis();
        if (isInvulnerable && (currentTime - lastDamageTime >= damageCooldown)) {
            isInvulnerable = false; // Reset invulnerability
        }

        //Check tile collision
        collisionDetect = false;
        gp.cod.checkCollision(this);
        gp.cod.checkCoinCollision(this);
        gp.checkMonsterCollision();
        gp.cod.checkSinking(gp);
        gp.cod.checkStepOnMud(gp);
        
        if (!isOnTheGround) { // When player is in the sky
            dy += gravityForce; // Player fall relate to gravity force
            y += dy;

            // Top boundary check
            if (y < 0) {
                y = 0;
                dy = 0;
            }
            gp.cod.TopCollisionChecks(this); // Check top collision
        }

        if (dy >= 0) { // When falls
            gp.cod.bottomCollisionChecks(this); // Check bottom collision
        }

        if (key.Jump) {
            direction = "Up";
            jump();
        }

        if (key.Left == true || key.Right == true) { // Check if event trigger
            if (key.Left == true) {
                direction = "Left";
            }
            if (key.Right == true) {
                direction = "Right";
            }
            //Collision false
            if (collisionDetect == false) {
                switch (direction) {

                    case "Left":
                        x -= speed;
                        break;
                    case "Right":
                        x += speed;
                        break;
                }
                // Horizontal boundary checks
                if (x < 0) {
                    x = 0;
                }
                if (x > gp.WorldWidth - gp.TileSize) {
                    x = gp.WorldWidth - gp.TileSize;
                }
            }
            // Handle player sprites
            spriteCount++;
            if (spriteCount > 12) {
                if (spriteNum == 1) {
                    spriteNum = 2;
                } else if (spriteNum == 2) {
                    spriteNum = 1;
                }
                spriteCount = 0;
            }
        }
        //update score
    }
    //Handle player animations

    public void draw(Graphics2D g2) {

        int scoreX = 800;
        int scoreY = 50;
        int hpX = 50;
        int hpY = 80;

        BufferedImage img = null;

        g2.setColor(Color.black);
        g2.setFont(new Font("Calibri", Font.BOLD, 25));
        g2.drawString("Score: " + score, scoreX + gp.cameraX, scoreY + gp.cameraY);
        g2.drawString("HP: " + hp, hpX + gp.cameraX, hpY + gp.cameraY);

        switch (direction) {

            case "Up":
                if (spriteNum == 1) {
                    img = up1;
                } else {
                    img = up2;
                }
                break;
            case "Left":
                if (spriteNum == 1) {
                    img = left1;
                } else {
                    img = left2;
                }
                break;
            case "Right":
                if (spriteNum == 1) {
                    img = right1;
                } else {
                    img = right2;
                }
                break;
        }
        g2.drawImage(img, x, y, gp.TileSize, gp.TileSize, null);
        g2.setColor(Color.red);
        g2.drawRect(solidArea.x, solidArea.y, solidArea.width, solidArea.height);
    }
}
