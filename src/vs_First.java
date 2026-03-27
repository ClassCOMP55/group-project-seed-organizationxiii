
import java.awt.Color;
import acm.graphics.GLabel;
import java.awt.event.MouseEvent;
import acm.graphics.GObject;
import acm.graphics.GImage;
import acm.graphics.GRect;

public class vs_First extends GraphicsPane {
	
	private GRect backButton;
	private GLabel backLabel;

    public vs_First(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        GImage enemy = new GImage("Decima.png");

        // same size as cutscene
        enemy.scale(0.6, 0.6);

        // same position style as cutscene
        double x = (MainApplication.WINDOW_WIDTH - enemy.getWidth()) / 2;
        double y = (MainApplication.WINDOW_HEIGHT * 0.35) - (enemy.getHeight() / 2);

        enemy.setLocation(x, y);

        mainScreen.add(enemy);
        contents.add(enemy);

        // same white text box as cutscene, but empty
        GRect dialogueBox = new GRect(
            50,
            MainApplication.WINDOW_HEIGHT - 180,
            MainApplication.WINDOW_WIDTH - 100,
            130
        );

        dialogueBox.setFilled(true);
        dialogueBox.setFillColor(Color.WHITE);
        dialogueBox.setColor(Color.BLACK);

        mainScreen.add(dialogueBox);
        contents.add(dialogueBox);
        
     // Back button
        backButton = new GRect(
            dialogueBox.getX() + 10,
            dialogueBox.getY() + dialogueBox.getHeight() - 35,
            80,
            25
        );

        backButton.setFilled(true);
        backButton.setFillColor(Color.LIGHT_GRAY);
        backButton.setColor(Color.BLACK);

        mainScreen.add(backButton);
        contents.add(backButton);

        // Back label
        backLabel = new GLabel("Back");
        backLabel.setFont("Times New Roman-Bold-16");
        backLabel.setColor(Color.BLACK);

        backLabel.setLocation(
            backButton.getX() + 22,
            backButton.getY() + 18
        );

        mainScreen.add(backLabel);
        contents.add(backLabel);

    }

    @Override
    public void hideContent() {
        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (backButton.contains(x, y) || backLabel.contains(x, y)) {
            mainScreen.switchToCutsceneScreen();
        }
    }
}




