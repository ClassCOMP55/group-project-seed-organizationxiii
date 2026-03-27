
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GImage;

public class vs_First extends GraphicsPane {

    public vs_First(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
       

        GImage enemy = new GImage("Decima.png");

        System.out.println("Image width = " + enemy.getWidth());
        System.out.println("Image height = " + enemy.getHeight());

        enemy.scale(0.6, 0.6);

        double x = (MainApplication.WINDOW_WIDTH - enemy.getWidth()) / 2;
        double y = (MainApplication.WINDOW_HEIGHT - enemy.getHeight()) / 2;

        enemy.setLocation(x, y);

        mainScreen.add(enemy);
        contents.add(enemy);
    }
}