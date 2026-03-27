import java.awt.Color;
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GRect;

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
		
	
		
		

	public CutscenePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {
	    GImage hyperion = new GImage("hyperioncutscene.png");

	    // 1. Scale FIRST
	    hyperion.scale(0.6, 0.6);

	    // 2. Set position BEFORE adding
	    double x = (MainApplication.WINDOW_WIDTH - hyperion.getWidth()) / 2;
	    double y = (MainApplication.WINDOW_HEIGHT * 0.35) - (hyperion.getHeight() / 2);

	    hyperion.setLocation(x, y);

	    // 3. Add LAST
	    mainScreen.add(hyperion);

	    contents.add(hyperion);
	    
	    //dialogueBox
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
	    	
	    //name of the box
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

	public void hideContent() {
		
	}
	
	
	private boolean pressed = false;

	@Override
	public void mousePressed(MouseEvent e) {
	    if (pressed) return;

	    pressed = true;
	    advanceDialogue();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	    pressed = false;
	}
}
