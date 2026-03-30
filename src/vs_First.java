import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.graphics.GObject;



public class vs_First extends GraphicsPane {
    
	
    private String[] dialogue = {
        "Down with the Color Guardians!!",
        "Not every color is necessary for our world. We don't",
        "need to protect every single last one of them.",
        "Indeed, you'll have to go through me if you want to",
        "preserve what is ripe for destruction.",
        "I assure you, this fight won't be fair, but will be quick.",
    };

    private int dialogueIndex = 0;
    private GLabel dialogueText;
    private GRect backButton;
    private GLabel backLabel;
    private GRect continueButton;
    private GLabel continueLabel;
    private boolean backHovered = false;
    private boolean continueHovered = false;
    private boolean pressed = false;

    public vs_First(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        GImage enemy = new GImage("Decima.png");
        enemy.scale(0.6, 0.6);

        double x = (MainApplication.WINDOW_WIDTH - enemy.getWidth()) / 2;
        double y = (MainApplication.WINDOW_HEIGHT * 0.35) - (enemy.getHeight() / 2);

        enemy.setLocation(x, y);

        mainScreen.add(enemy);
        contents.add(enemy);

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

        GLabel nameLabel = new GLabel("Decima");
        nameLabel.setFont("Times New Roman-Bold-25");
        nameLabel.setColor(Color.BLACK);
        nameLabel.setLocation(dialogueBox.getX() + 10, dialogueBox.getY() - 5);

        mainScreen.add(nameLabel);
        contents.add(nameLabel);

        dialogueText = new GLabel(dialogue[0]);
        dialogueText.setFont("Times New Roman-PLAIN-25");
        dialogueText.setColor(Color.BLACK);
        dialogueText.setLocation(dialogueBox.getX() + 10, dialogueBox.getY() + 40);

        mainScreen.add(dialogueText);
        contents.add(dialogueText);

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

        backLabel = new GLabel("Back");
        backLabel.setFont("Times New Roman-Bold-16");
        backLabel.setColor(Color.BLACK);
        backLabel.setLocation(backButton.getX() + 22, backButton.getY() + 18);

        mainScreen.add(backLabel);
        contents.add(backLabel);

        continueButton = new GRect(
            dialogueBox.getX() + dialogueBox.getWidth() - 180,
            dialogueBox.getY() + dialogueBox.getHeight() - 35,
            160,
            25
        );
        continueButton.setFilled(true);
        continueButton.setFillColor(Color.LIGHT_GRAY);
        continueButton.setColor(Color.BLACK);

        mainScreen.add(continueButton);
        contents.add(continueButton);

        continueLabel = new GLabel("Click to continue");
        continueLabel.setFont("Times New Roman-Bold-16");
        continueLabel.setColor(Color.BLACK);
        continueLabel.setLocation(continueButton.getX() + 15, continueButton.getY() + 18);

        mainScreen.add(continueLabel);
        contents.add(continueLabel);
    }

    private void advanceDialogue() {
        dialogueIndex++;
        if (dialogueIndex < dialogue.length) {
            dialogueText.setLabel(dialogue[dialogueIndex]);
        } else {
            mainScreen.switchToFirstBattleScreen();
        }
    }

    private void scaleButton(GRect button, double scale) {
        double centerX = button.getX() + button.getWidth() / 2;
        double centerY = button.getY() + button.getHeight() / 2;

        double newWidth = button.getWidth() * scale;
        double newHeight = button.getHeight() * scale;

        button.setSize(newWidth, newHeight);
        button.setLocation(centerX - newWidth / 2, centerY - newHeight / 2);
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
        if (pressed) return;
        pressed = true;

        double x = e.getX();
        double y = e.getY();

        if (continueButton.contains(x, y) || continueLabel.contains(x, y)) {
            mainScreen.switchToFirstBattleScreen();
            return;
        }

        if (backButton.contains(x, y) || backLabel.contains(x, y)) {
            mainScreen.switchToCutsceneScreen();
            return;
        }

        advanceDialogue();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        boolean nowBackHovered = backButton.contains(x, y) || backLabel.contains(x, y);

        if (nowBackHovered && !backHovered) {
            backHovered = true;
            scaleButton(backButton, 1.1);
            backButton.setColor(Color.YELLOW);
        } else if (!nowBackHovered && backHovered) {
            backHovered = false;
            scaleButton(backButton, 1.0 / 1.1);
            backButton.setColor(Color.BLACK);
        }

        boolean nowContinueHovered = continueButton.contains(x, y) || continueLabel.contains(x, y);

        if (nowContinueHovered && !continueHovered) {
            continueHovered = true;
            scaleButton(continueButton, 1.1);
            continueButton.setColor(Color.YELLOW);
        } else if (!nowContinueHovered && continueHovered) {
            continueHovered = false;
            scaleButton(continueButton, 1.0 / 1.1);
            continueButton.setColor(Color.BLACK);
        }
        
        //test
    }
}