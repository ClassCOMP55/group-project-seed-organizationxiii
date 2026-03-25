import acm.graphics.GImage;

public class CutscenePane extends GraphicsPane {

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
	}

	public void hideContent() {
		
	}
}
