
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import java.awt.Color;

public class TitlePane extends GraphicsPane{
	private GLabel startLabel;
	
	public TitlePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addPicture();
		addDescriptionButton();
	}

	@Override
	public void hideContent() {
		for(GObject item : contents) {
			mainScreen.remove(item);
		}
		contents.clear();
	}
	
	private void addPicture(){
		GImage startImage = new GImage("combined_blended.png", 400, 200);
		startImage.scale(0.5, 0.5);
		startImage.setLocation((mainScreen.getWidth() - startImage.getWidth())/ 2, 70);
		
		contents.add(startImage);
		mainScreen.add(startImage);
	}
	
	private void addDescriptionButton() {
		startLabel = new GLabel("START");
		startLabel.setFont("Arial-Bold-36");
		startLabel.setColor(Color.BLACK);
		
		double x = (mainScreen.getWidth() - startLabel.getWidth()) / 2;
		double y = 490;
		startLabel.setLocation(x, y);
		
		contents.add(startLabel);
		mainScreen.add(startLabel);

	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());
		
		if (obj == startLabel) {
			startLabel.setColor(Color.MAGENTA);
		} else {
			startLabel.setColor(Color.BLACK);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (mainScreen.getElementAtLocation(e.getX(), e.getY()) == contents.get(1)) {
			mainScreen.switchToCutsceneScreen();
		}
	}

}
