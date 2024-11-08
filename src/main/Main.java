package main;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // Create game window
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Mark The Emperor");

        // Create and add HomeScreenPanel to the window
        HomeScreenPanel homeScreen = new HomeScreenPanel(window);
        window.add(homeScreen);
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
