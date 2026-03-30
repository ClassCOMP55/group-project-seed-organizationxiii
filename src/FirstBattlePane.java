import acm.graphics.GImage;
import acm.graphics.GObject;
import java.awt.event.KeyEvent;

public class FirstBattlePane extends GraphicsPane {

    private GImage huemanImage;
    private GImage decimaImage;

    public FirstBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
    	huemanImage = new GImage(getHuemanImage());
    	decimaImage = new GImage("Decima.png");

    	huemanImage.scale(0.5);
    	decimaImage.scale(1.0);

    	// ✅ Position Hueman (LEFT SIDE)
    	double hx = MainApplication.WINDOW_WIDTH * 0.25 - huemanImage.getWidth() / 2 + 20;
    	double hy = 150 + 50;
    	huemanImage.setLocation(hx, hy);

    	// ✅ Position Decima (RIGHT SIDE)
    	double dx = MainApplication.WINDOW_WIDTH * 0.75 - decimaImage.getWidth() / 2;
    	double dy = 150 - 60;
    	decimaImage.setLocation(dx, dy);

    	mainScreen.add(huemanImage);
    	mainScreen.add(decimaImage);

    	contents.add(huemanImage);
    	contents.add(decimaImage);
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