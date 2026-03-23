
import java.awt.event.MouseEvent;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import java.awt.Color;

public class TitlePane extends GraphicsPane{
	private GLabel startLabel;
	private GLabel titleLabel;
	
	public TitlePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}
	
	@Override
	public void showContent() {
		addTitle();
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
		startImage.setLocation((mainScreen.getWidth() - startImage.getWidth())/ 2, 110);
		
		contents.add(startImage);
		mainScreen.add(startImage);
	}
	
	private void addDescriptionButton() {
		startLabel = new GLabel("START");
		startLabel.setFont("Times New Roman-Bold-36");
		startLabel.setColor(Color.BLACK);
		
		double x = (mainScreen.getWidth() - startLabel.getWidth()) / 2;
		double y = 520;
		startLabel.setLocation(x, y);
		
		contents.add(startLabel);
		mainScreen.add(startLabel);

	}
	
	private void addTitle() {
	    titleLabel = new GLabel("Every Last Hue");
	    titleLabel.setFont("Times New Roman-Bold-48");
	    titleLabel.setColor(Color.BLACK);
	    
	    double x = (mainScreen.getWidth() - titleLabel.getWidth()) / 2;
	    double y = 100;
	    
	    titleLabel.setLocation(x, y);
	    
	    contents.add(titleLabel);
	    mainScreen.add(titleLabel);
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
	    GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

	    if (obj == startLabel) {
	        mainScreen.switchToColorSelectionScreen();
	    }
	}
}