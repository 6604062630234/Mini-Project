package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import main.GamePanel;

public class KeyHandler implements KeyListener {

    public boolean Left, Right, Jump = false;

    GamePanel gp;

    public KeyHandler(GamePanel gp) {

        this.gp = gp; // Just for refference to attributes in GamePanel
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do not use this
    }

    @Override
    public void keyPressed(KeyEvent e) { // When player press any key
        
        int keyCode = e.getKeyCode(); // Get key input

        switch (keyCode) { 
            // Check key input and update information
            case KeyEvent.VK_A:
                Left = true;
                break;

            case KeyEvent.VK_D:
                Right = true;
                break;

            case KeyEvent.VK_SPACE:
                Jump = true;
                break;

            case KeyEvent.VK_P: // When player press 'P'
                if (gp.currentState == gp.playState) { // When in play state
                    gp.currentState = gp.pauseState; // Change state to pause state
                } else if (gp.currentState == gp.pauseState) { // When in pause state
                    gp.currentState = gp.playState; // Change state back to play state
                    // Start timer again
                    if(!gp.gameTimer.isRunning()){
                        gp.gameTimer.start();
                    }
                }
                break;
            case KeyEvent.VK_R: // When player press 'R'
                if (gp.currentState == gp.gameOverState) { // If game over
                    // Reset all components to default
                    gp.currentState = gp.playState;
                    gp.player.setDefault();
                    gp.setupMonsters();
                    gp.resetCoins();
                    gp.countdownTime = 180;
                    gp.StartGameTimer();
                }
                break;
            default:
                return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { 
        
        switch (e.getKeyCode()) { // Check key input
            // Check key input and update information
            case KeyEvent.VK_A:
                Left = false;
                break;

            case KeyEvent.VK_D:
                Right = false;
                break;

            case KeyEvent.VK_SPACE:
                Jump = false;
                break;

            default:
                return;
        }
    }

}
