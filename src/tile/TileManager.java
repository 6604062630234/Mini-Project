package tile;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import main.GamePanel;

public class TileManager {
    
    GamePanel gp;
    public Tile[] tiles;
    public int mapTileNum[][];
    
    public TileManager(GamePanel gp){
        this.gp = gp;
        tiles = new Tile[100]; //Create array to store varients of tile
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; 
        //This use to refference to tiles
        
        getTileImage(); //Method to get tile image
        loadmap("/maps/World_map.txt"); //Set mapTileNum refference by numbers in text file
    }
    
    public void loadmap(String filepath){
        
        try{
            InputStream is = getClass().getResourceAsStream(filepath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            
            int col = 0;
            int row = 0;
            
            while(col < gp.maxWorldCol && row < gp.maxWorldRow){
                String line = br.readLine(); //Read a line of the text
                
                while(col < gp.maxWorldCol){
                    String numbers[] = line.split(" ");
                    
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if(col == gp.maxWorldCol){
                    col = 0;
                    row++;
                }
            }
            br.close();
        }
        catch(Exception e){

        }
    }
    
    private void getTileImage(){
        
        try{
            tiles[10] = new Tile();
            tiles[10].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Sky.png"));
            
            tiles[11] = new Tile();
            tiles[11].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Grass.png"));
            tiles[11].collision = true;
            
            tiles[12] = new Tile();
            tiles[12].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Dirt.png"));
            tiles[12].collision = true;
            
            tiles[13] = new Tile();
            tiles[13].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Water_Surface.png"));
            
            tiles[14] = new Tile();
            tiles[14].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Water.png"));
            
            tiles[15] = new Tile();
            tiles[15].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Coin.png"));
            tiles[15].coinCollision = true;
            
            tiles[16] = new Tile();
            tiles[16].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Sky.png"));
            
            tiles[17] = new Tile();
            tiles[17].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Grass.png"));
            
            tiles[18] = new Tile();
            tiles[18].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Dirt.png"));
            
            //another tiles
            
            tiles[19] = new Tile();
            tiles[19].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Box.png"));
            tiles[19].collision = true;
            
            tiles[20] = new Tile();
            tiles[20].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Sky.png"));
            
            tiles[21] = new Tile();
            tiles[21].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Box.png"));
            
            tiles[22] = new Tile();
            tiles[22].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Bush.png"));
            
            tiles[23] = new Tile();
            tiles[23].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Tree.png"));
            
            tiles[24] = new Tile();
            tiles[24].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Stone.png"));
            
            tiles[25] = new Tile();
            tiles[25].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Mud.png"));
            tiles[25].collision = true;
            
            tiles[26] = new Tile();
            tiles[26].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/Sky.png"));
            tiles[26].collision = true;
            
            tiles[99] = new Tile();
            tiles[99].image = ImageIO.read(getClass().getResourceAsStream("/Items/TheEmperorCrown.png"));
            
            
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void draw(Graphics2D g2){
        
        int col = 0;
        int row = 0;
        
        while(col < gp.maxWorldCol && row < gp.maxWorldRow){
            
            int tileNum = mapTileNum[col][row];
            
            int ScreenX = col * gp.TileSize;
            int ScreenY = row * gp.TileSize;
            
            g2.drawImage(tiles[tileNum].image, ScreenX, ScreenY, gp.TileSize, gp.TileSize, null);
            col++;

            if(col == gp.maxWorldCol){
                col = 0;
                row++;
            }
        }
    }
}
