import acm.graphics.GObject;
import acm.program.*;
import characters.Hueman;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class MainApplication extends GraphicsProgram{

	//Settings
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	//List of all the full screen panes
	private TitlePane titlePane;
	private CutscenePane cutscenePane;
	private LevelSelectPane levelSelectPane;
	private BattlePane battlePane;
	private VictoryPane victoryPane;
	private ColorSelectionPane colorSelectionPane;
	private GraphicsPane currentScreen;
	
	private Hueman player;

	public MainApplication() {
		super();
	}
	
	protected void setupInteractions() {
		requestFocus();
		addKeyListeners();
		addMouseListeners();
		addMouseListeners();
	}
	
	public void init() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	public void setPlayer(Hueman p) {
	    player = p;
	}

	public Hueman getPlayer() {
	    return player;
	}
	
	public void run() {
		System.out.println("Lets' Begin!");
		setupInteractions();
		
		//Initialize all Panes
		titlePane = new TitlePane(this);
		cutscenePane = new CutscenePane(this);
		levelSelectPane = new LevelSelectPane(this);
		battlePane = new BattlePane(this);
		victoryPane = new VictoryPane(this);
		colorSelectionPane = new ColorSelectionPane(this);

		//The Default Screen
		switchToScreen(titlePane);
	}
	
	public static void main(String[] args) {
		new MainApplication().start();
	}
	
	//Screen switching functions
	
	public void switchToTitleScreen() {
		switchToScreen(titlePane);
	}
	
	public void switchToCutsceneScreen() {
		switchToScreen(cutscenePane);
	}
	
	public void switchToLevelSelectScreen() {
		switchToScreen(levelSelectPane);
	}
	
	public void switchToBattleScreen() {
		switchToScreen(battlePane);
	}
	
	public void switchToVictoryScreen() {
		switchToScreen(victoryPane);
	}
	
	public void switchToColorSelectionScreen() {
	    switchToScreen(colorSelectionPane);
	}
	
	//Core screen switching logic
	
	protected void switchToScreen(GraphicsPane newScreen) {
		if(currentScreen != null) {
			currentScreen.hideContent();
		}
		newScreen.showContent();
		currentScreen = newScreen;
	}
	
	//Utility function for clicking objects
	
	public GObject getElementAtLocation(double x, double y) {
		return getElementAt(x, y);
	}
	
	//Input routing (DO NOT MODIFY)
	
	@Override
	public void mousePressed(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseReleased(e);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseClicked(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseDragged(e);
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		if(currentScreen != null) {
			currentScreen.mouseMoved(e);
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyPressed(e);
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyReleased(e);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(currentScreen != null) {
			currentScreen.keyTyped(e);
		}
	}
}
