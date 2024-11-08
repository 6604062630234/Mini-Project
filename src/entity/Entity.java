package entity;

import java.awt.Rectangle;

public abstract class Entity {
    
    public int x, y; // Position for entities
    public int speed; // Speed for entities
    public int gravityForce = 1; // Gravity force
    
    public String direction; // Direction for entities
    public int spriteCount = 0; // Sprite counter
    public int spriteNum = 1; // Sprite number to refference sprites
    
    public Rectangle solidArea; // Hitbox for entities
    public boolean collisionDetect; // To check collision with entities
    public boolean TopCollision; // To check top collision
    public boolean BottomCollision; // To check bottom collision
}

