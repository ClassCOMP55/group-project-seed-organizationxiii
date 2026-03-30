import acm.graphics.GImage;
import acm.graphics.GObject;
import acm.graphics.GRect;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class FirstBattlePane extends GraphicsPane {

    private GImage huemanImage;
    private GImage decimaImage;
    private GRect huemanHealthBar;
    private GRect huemanHealthBack;

    private GRect decimaHealthBar;
    private GRect decimaHealthBack;

    public FirstBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
    	huemanImage = new GImage(getHuemanImage());
    	decimaImage = new GImage("Decima.png");

    	huemanImage.scale(0.6);
    	decimaImage.scale(0.9);

    	// ✅ Position Hueman (LEFT SIDE)
    	double hx = MainApplication.WINDOW_WIDTH * 0.25 - huemanImage.getWidth() / 2 + 30;
    	double hy = 150 + 50;
    	huemanImage.setLocation(hx, hy);

    	// ✅ Position Decima (RIGHT SIDE)
    	double dx = MainApplication.WINDOW_WIDTH * 0.75 - decimaImage.getWidth() / 2;
    	double dy = 10;
    	decimaImage.setLocation(dx, dy);

    	mainScreen.add(huemanImage);
    	mainScreen.add(decimaImage);

    	contents.add(huemanImage);
    	contents.add(decimaImage);
    	
    	double barWidth = 120;
        double barHeight = 12;

        // Hueman health bar
        double hxBar = MainApplication.WINDOW_WIDTH - barWidth - 30;
        double hyBar = MainApplication.WINDOW_HEIGHT - barHeight - 30;

        huemanHealthBack = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBack.setFilled(true);
        huemanHealthBack.setColor(Color.DARK_GRAY);
        huemanHealthBack.setFillColor(Color.DARK_GRAY);

        huemanHealthBar = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBar.setFilled(true);
        huemanHealthBar.setColor(Color.GREEN);
        huemanHealthBar.setFillColor(Color.GREEN);

        mainScreen.add(huemanHealthBack);
        mainScreen.add(huemanHealthBar);

        contents.add(huemanHealthBack);
        contents.add(huemanHealthBar);

        // Decima health bar
        double dxBar = dxBar = 30;
        double dyBar = dyBar = 30;

        decimaHealthBack = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBack.setFilled(true);
        decimaHealthBack.setColor(Color.DARK_GRAY);
        decimaHealthBack.setFillColor(Color.DARK_GRAY);

        decimaHealthBar = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBar.setFilled(true);
        decimaHealthBar.setColor(Color.RED);
        decimaHealthBar.setFillColor(Color.RED);

        mainScreen.add(decimaHealthBack);
        mainScreen.add(decimaHealthBar);

        contents.add(decimaHealthBack);
        contents.add(decimaHealthBar);
    }

    
    @Override
    public void keyPressed(KeyEvent e) {
        if (huemanImage == null) return;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            huemanImage.move(-10, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            huemanImage.move(10, 0);
        } else if (key == KeyEvent.VK_UP) {
            huemanImage.move(0, -10);
        } else if (key == KeyEvent.VK_DOWN) {
            huemanImage.move(0, 10);
        }
    }
    
    
    private String getHuemanImage() {
        String selectedColor = mainScreen.getSelectedColor();

        if (selectedColor == null) {
            return "redback.png"; 
        } else if (selectedColor.equals("red")) {
            return "redback.png";
        } else if (selectedColor.equals("green")) {
            return "greenback.png";
        } else {
            return "blueback.png";
        }
    }
    @Override
    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}

//hello
//hellooooo