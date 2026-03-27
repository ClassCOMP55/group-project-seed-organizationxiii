import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.graphics.GObject;

public class CutscenePane extends GraphicsPane {
	
	private String[] dialogue = {
		    "Welcome to our world of Palletia.",
		    "I am the Elder Color Guardian, Hyperion.",
		    "I wish our introduction could be warmer...",
		    "But you are summoned here to save the world from evil.",
		    "Loathe, a fallen color seeks to rid the world",
		    "of color completely. Only you, as a foundational color,",
		    "can restore order by reclaiming the colors he corrupted",
		    "and put an  end to Loathe's madness.",
		    "But before that, you must defeat one of our own Color",
		    "Guardians. Loathe convinced him to join his color terrorism.",
		    "He's nearby and will attack at any moment",
		    "Brace yourself."
	};

	private int dialogueIndex = 0;
	private GLabel dialogueText;
	private GRect backButton;
	private GLabel backLabel;

	public CutscenePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {
	    GImage hyperion = new GImage("hyperioncutscene.png");

	    hyperion.scale(0.6, 0.6);

	    double x = (MainApplication.WINDOW_WIDTH - hyperion.getWidth()) / 2;
	    double y = (MainApplication.WINDOW_HEIGHT * 0.35) - (hyperion.getHeight() / 2);

	    hyperion.setLocation(x, y);

	    mainScreen.add(hyperion);
	    contents.add(hyperion);
	    
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
	    	
	    GLabel nameLabel = new GLabel("Hyperion");

	    nameLabel.setFont("Times New Roman-Bold-25");
	    nameLabel.setColor(Color.BLACK);

	    nameLabel.setLocation(
	    	    dialogueBox.getX() + 10,
	    	    dialogueBox.getY() - 5
	    );

	    mainScreen.add(nameLabel);
	    contents.add(nameLabel);
	    	
	    dialogueText = new GLabel(dialogue[0]);

	    dialogueText.setFont("Times New Roman-PLAIN-25");
	    dialogueText.setColor(Color.BLACK);

	    dialogueText.setLocation(
	    	    dialogueBox.getX() + 10,
	    	    dialogueBox.getY() + 40
	    );

	    mainScreen.add(dialogueText);
	    contents.add(dialogueText);

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
	
	private void advanceDialogue() {
	    dialogueIndex++;
	    System.out.println("Index: " + dialogueIndex);
	    if (dialogueIndex < dialogue.length) {
	        dialogueText.setLabel(dialogue[dialogueIndex]);
	    } else {
	        mainScreen.switchToBattleScreen();
	    }
	}

	@Override
	public void hideContent() {
	    for (GObject item : contents) {
	        mainScreen.remove(item);
	    }
	    contents.clear();
	}
	private boolean pressed = false;

	@Override
	public void mousePressed(MouseEvent e) {
	    if (pressed) return;
	    pressed = true;

	    double x = e.getX();
	    double y = e.getY();

	    if (backButton.contains(x, y) || backLabel.contains(x, y)) {
	        mainScreen.switchToColorSelectionScreen();
	        return;
	    }

	    advanceDialogue();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    pressed = false;
	}
}


//hello