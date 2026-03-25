import acm.graphics.GImage;

public class CutscenePane extends GraphicsPane {

	public CutscenePane(MainApplication mainScreen) {
		this.mainScreen = mainScreen;
	}

	public void showContent() {
		GImage hyperion = new GImage("hyperioncutscene.png");
		
		mainScreen.add(hyperion);
		hyperion.scale(0.6, 0.6);
	}

	public void hideContent() {
		
	}
}
