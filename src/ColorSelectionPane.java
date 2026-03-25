import acm.graphics.GImage;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

import java.awt.Color;
import java.awt.event.MouseEvent;

import characters.ColorType;
import characters.Hueman;

public class ColorSelectionPane extends GraphicsPane {

    private GLabel titleLabel;
    private GImage red;
    private GImage blue;
    private GImage green;
    
    private GRect hoverBox;
    

    public ColorSelectionPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void showContent() {
        addTitle();
        addColorOptions();
        
        hoverBox = new GRect(0, 0, 0, 0);
        hoverBox.setColor(Color.YELLOW);
        hoverBox.setLineWidth(3);
        hoverBox.setVisible(false);

        mainScreen.add(hoverBox);
    }
    
    
    

    public void hideContent() {
        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();
    }

    private void addTitle() {
        titleLabel = new GLabel("Choose your color");
        titleLabel.setFont("Times New Roman-Bold-40");
        titleLabel.setColor(Color.BLACK);

        double x = (mainScreen.getWidth() - titleLabel.getWidth()) / 2; //could be redundant
        double y = 125;

        titleLabel.setLocation(x, y);
        
        titleLabel.setLocation(
        	    (MainApplication.WINDOW_WIDTH - titleLabel.getWidth()) / 2,
        	    125
        	);

        contents.add(titleLabel);
        mainScreen.add(titleLabel);
    }
    
    private void addColorOptions() {
        blue = new GImage("blueow.png");
        red = new GImage("redow.png");
        green = new GImage("greenow.png");

        blue.scale(0.7, 0.7);
        red.scale(0.7, 0.7);
        green.scale(0.7, 0.7);

        double sectionWidth = MainApplication.WINDOW_WIDTH / 4;
        double y = MainApplication.WINDOW_HEIGHT / 2;

        blue.setLocation(sectionWidth - blue.getWidth() / 2, y);
        red.setLocation(2 * sectionWidth - red.getWidth() / 2, y);
        green.setLocation(3 * sectionWidth - green.getWidth() / 2, y);

        contents.add(blue);
        contents.add(red);
        contents.add(green);

        mainScreen.add(blue);
        mainScreen.add(red);
        mainScreen.add(green);
    }
    
    private void selectColor(ColorType color) {
        Hueman player = new Hueman(100, 20, 10, "Hueman", color);

        mainScreen.setPlayer(player); // store the chosen character

        System.out.println("Selected color");
        mainScreen.switchToCutsceneScreen(); // move forward in the game
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

       if (obj == red) {
            selectColor(ColorType.RED);
        } else if (obj == blue) {
            selectColor(ColorType.BLUE);
        } else if (obj == green) {
            selectColor(ColorType.GREEN);
       }
    }
    
   
    
    
}