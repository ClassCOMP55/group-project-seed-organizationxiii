import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

public class CutScene3Pane extends GraphicsPane {
	private String[] dialogue = {
			"We are getting closer to the next guardiam.",
			"Something feels wrong in this part of the world.",
			"Stay focused. Another battle is waiting ahead."
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
	
	public CutScene3Pane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		dialogueIndex = 0;
		
		GImage image = new GImage("hyperioncutscene.png");
		image.scale(0.6, 0.6);
		
		double x = (MainApplication.WINDOW_WIDTH - image.getWidth()) / 2;
		double y = (MainApplication.WINDOW_HEIGHT * 0.35) - (image.getHeight() / 2);
		image.setLocation(x, y);
		
		mainScreen.add(image);
		contents.add(image);
		
		GRect dialogueBox = new GRect(50,
				MainApplication.WINDOW_HEIGHT - 180,
				MainApplication.WINDOW_WIDTH - 100, 130);
		dialogueBox.setFilled(true);
		dialogueBox.setFillColor(Color.WHITE);
		dialogueBox.setFillColor(Color.BLACK);
		mainScreen.add(dialogueBox);
		contents.add(dialogueBox);
		
		GLabel nameLabel = new GLabel("Hyperion");
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
            dialogueBox.getY() + dialogueBox.getHeight() - 35, 80, 25);
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
            dialogueBox.getY() + dialogueBox.getHeight() - 35, 160, 25);
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
            mainScreen.switchToSecondBattleScreen();
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
        	advanceDialogue();
            return;
        }

        if (backButton.contains(x, y) || backLabel.contains(x, y)) {
            mainScreen.switchToLevelSelectScreen();
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
    }
}