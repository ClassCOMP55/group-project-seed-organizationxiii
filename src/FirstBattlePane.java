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
        String huemanFile = getHuemanImage();

        huemanImage = new GImage(huemanFile);
        decimaImage = new GImage("Decima.png");

        huemanImage.scale(0.8);
        decimaImage.scale(0.5);

        huemanImage.setLocation(120, 220);
        decimaImage.setLocation(500, 180);

        mainScreen.add(huemanImage);
        mainScreen.add(decimaImage);

        contents.add(huemanImage);
        contents.add(decimaImage);
        
        mainScreen.requestFocus();
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

        if (selectedColor.equals("red")) {
            return "redow.png";
        } else if (selectedColor.equals("green")) {
            return "greenow.png";
        } else {
            return "blueow.png";
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