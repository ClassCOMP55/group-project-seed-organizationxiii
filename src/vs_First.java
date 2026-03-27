


import acm.graphics.GObject;
import acm.graphics.GImage;

public class vs_First extends GraphicsPane {

    public vs_First(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        GImage enemy = new GImage("Decima.png");

        // size
        enemy.scale(0.6, 0.6);

        // center of screen
        double x = (MainApplication.WINDOW_WIDTH - enemy.getWidth()) / 2;
        double y = (MainApplication.WINDOW_HEIGHT - enemy.getHeight()) / 2 - 30;

        enemy.setLocation(x, y);

        mainScreen.add(enemy);
        contents.add(enemy);
    }

    @Override
    public void hideContent() {
        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();
    }
}