import acm.graphics.GObject;
import acm.graphics.GRect;
import acm.program.*;
import characters.Hueman;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class MainApplication extends GraphicsProgram{

	//Settings
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	
	//List of all the full screen panes
	private TitlePane titlePane;
	private CutscenePane cutscenePane;
	private LevelSelectPane levelSelectPane;
	private VictoryPane victoryPane;
	private ColorSelectionPane colorSelectionPane;
	private GraphicsPane currentScreen;
	private vs_First vsFirstPane;
	private HuemanStatsScreen huemanStatsScreen;
	private FirstBattlePane firstBattlePane;
	private String selectedColor;
	private Hueman player;
	private CutScene2Pane cutScene2Pane;
	private boolean isTransitioning = false;

	
	
	
	public void setSelectedColor(String color) {
	    selectedColor = color;
	}

	public String getSelectedColor() {
	    return selectedColor;
	}
	
	public MainApplication() {
		super();
	}
	
	protected void setupInteractions() {
		requestFocus();
		addKeyListeners();
		addMouseListeners();
	}
	
	public void init() {
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}
	
	public void setPlayer(Hueman p) {
	    player = p;
	    huemanStatsScreen = new HuemanStatsScreen(player, this);
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
		victoryPane = new VictoryPane(this);
		colorSelectionPane = new ColorSelectionPane(this);
		vsFirstPane = new vs_First(this);
		firstBattlePane = new FirstBattlePane(this);
		cutScene2Pane = new CutScene2Pane(this);

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
	
	
	public void switchToVictoryScreen() {
		switchToScreen(victoryPane);
	}
	
	public void switchToColorSelectionScreen() {
	    switchToScreen(colorSelectionPane);
	}
	
	public void switchToVsFirstScreen() {
	    switchToScreen(vsFirstPane);
	}
	
	public void switchToFirstBattleScreen() {
	    switchToScreen(firstBattlePane);
	}
	
	public void switchToCutScene2Screen() {
	    switchToScreen(cutScene2Pane);
	}
	
	
	public void switchToStatsScreen() {
		if(huemanStatsScreen != null) {
			switchToScreen(huemanStatsScreen);
		} else {
			System.out.println("No player assigned yet!");
		}
	}
	//Core screen switching logic
	


	protected void switchToScreen(GraphicsPane newScreen) {
	    if (isTransitioning) return;

	    isTransitioning = true;

	    new Thread(() -> {
	        GRect fadeRect = new GRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	        fadeRect.setFilled(true);
	        fadeRect.setColor(Color.BLACK);
	        fadeRect.setFillColor(new Color(40, 40, 40, 0));

	        add(fadeRect);
	        fadeRect.sendToFront();

	        // Fade to dark grey
	        for (int alpha = 0; alpha <= 255; alpha += 10) {
	            fadeRect.setFillColor(new Color(40, 40, 40, alpha));
	            fadeRect.sendToFront();
	            pause(20);
	        }

	        // Switch screen
	        if (currentScreen != null) {
	            currentScreen.hideContent();
	        }

	        newScreen.showContent();
	        currentScreen = newScreen;

	        fadeRect.sendToFront();

	        // Fade back in
	        for (int alpha = 255; alpha >= 0; alpha -= 10) {
	            fadeRect.setFillColor(new Color(40, 40, 40, alpha));
	            fadeRect.sendToFront();
	            pause(20);
	        }

	        remove(fadeRect);
	        isTransitioning = false;
	    }).start();
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
