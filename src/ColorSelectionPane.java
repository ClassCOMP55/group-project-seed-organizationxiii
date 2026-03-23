import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import java.awt.Color;

public class ColorSelectionPane extends GraphicsPane {

    private GLabel titleLabel;

    public ColorSelectionPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void showContent() {
        addTitle();
        addColorOptions();
    }

    public void hideContent() {
        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();
    }

    private void addTitle() {
        titleLabel = new GLabel("Choose your color");
        titleLabel.setFont("Times New Roman-Bold-40");
        titleLabel.setColor(Color.BLACK);

        double x = (mainScreen.getWidth() - titleLabel.getWidth()) / 2;
        double y = 125;

        titleLabel.setLocation(x, y);

        contents.add(titleLabel);
        mainScreen.add(titleLabel);
    }
    
    private void addColorOptions() {
        GImage blue = new GImage("blueow.png");
        GImage red = new GImage("redow.png");
        GImage green = new GImage("greenow.png");

        blue.scale(0.7, 0.7);
        red.scale(0.7, 0.7);
        green.scale(0.7, 0.7);

        double y = 100;

        double sectionWidth = mainScreen.getWidth() / 4;

        blue.setLocation(sectionWidth - blue.getWidth() / 2, y);
        red.setLocation(2 * sectionWidth - red.getWidth() / 2, y);
        green.setLocation(3 * sectionWidth - green.getWidth() / 2, y);

        contents.add(blue);
        contents.add(red);
        contents.add(green);

        mainScreen.add(blue);
        mainScreen.add(red);
        mainScreen.add(green);
    }
}