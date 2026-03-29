import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;

import java.awt.Color;
import java.awt.event.MouseEvent;

import characters.ColorType;
import characters.Hueman;

public class ColorSelectionPane extends GraphicsPane {

    private GLabel titleLabel;
    private GImage red;
    private GImage blue;
    private GImage green;

    private GImage hoveredImage = null;
    private final double HOVER_SCALE = 1.12;

    public ColorSelectionPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void showContent() {
    	mainScreen.getGCanvas().setBackground(Color.LIGHT_GRAY);
        addTitle();
        addColorOptions();
    }

    public void hideContent() {
        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();
        hoveredImage = null;
    }

    private void addTitle() {
        titleLabel = new GLabel("Choose your color");
        titleLabel.setFont("Times New Roman-Bold-40");
        titleLabel.setColor(Color.BLACK);

        titleLabel.setLocation(
            (MainApplication.WINDOW_WIDTH - titleLabel.getWidth()) / 2,
            125
        );

        contents.add(titleLabel);
        mainScreen.add(titleLabel);
    }

    private void addColorOptions() {
        blue = new GImage("blueow.png");
        red = new GImage("redow.png");
        green = new GImage("greenow.png");

        blue.scale(0.9, 0.9);
        red.scale(0.9, 0.9);
        green.scale(0.9, 0.9);

        double sectionWidth = MainApplication.WINDOW_WIDTH / 4.0;
        double y = MainApplication.WINDOW_HEIGHT / 2.0;

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

    private void selectColor(ColorType color) {
        Hueman player = new Hueman(100, 20, 10, "Hueman", color);
        mainScreen.setPlayer(player);

        if (color == ColorType.RED) {
            mainScreen.setSelectedColor("red");
        } else if (color == ColorType.BLUE) {
            mainScreen.setSelectedColor("blue");
        } else if (color == ColorType.GREEN) {
            mainScreen.setSelectedColor("green");
        }

        System.out.println("Selected color: " + color);
        mainScreen.switchToCutsceneScreen();
    }

    private void scaleFromCenter(GImage image, double factor) {
        double oldWidth = image.getWidth();
        double oldHeight = image.getHeight();
        double oldX = image.getX();
        double oldY = image.getY();

        image.scale(factor, factor);

        double newWidth = image.getWidth();
        double newHeight = image.getHeight();

        image.setLocation(
            oldX - (newWidth - oldWidth) / 2,
            oldY - (newHeight - oldHeight) / 2
        );
    }

    private void setHoveredImage(GImage newImage) {
        if (hoveredImage == newImage) {
            return;
        }

        if (hoveredImage != null) {
            scaleFromCenter(hoveredImage, 1.0 / HOVER_SCALE);
        }

        hoveredImage = newImage;

        if (hoveredImage != null) {
            scaleFromCenter(hoveredImage, HOVER_SCALE);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == red) {
            setHoveredImage(red);
        } else if (obj == blue) {
            setHoveredImage(blue);
        } else if (obj == green) {
            setHoveredImage(green);
        } else {
            setHoveredImage(null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == red) {
            selectColor(ColorType.RED);
        } else if (obj == blue) {
            selectColor(ColorType.BLUE);
        } else if (obj == green) {
            selectColor(ColorType.GREEN);
        }
    }
}
    
//hello
    