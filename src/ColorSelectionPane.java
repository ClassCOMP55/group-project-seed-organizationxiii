import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GLine;

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

    // Clouds
    private GOval cloud1, cloud2;

    // Lightning
    private GLine bolt1, bolt2, bolt3;

    public ColorSelectionPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void showContent() {
        addClouds();
        animateClouds();
        lightningFlash();

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

    // ---------------- TITLE ----------------
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

    // ---------------- COLORS ----------------
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
        mainScreen.switchToCutsceneScreen();
    }

    // ---------------- HOVER ----------------
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
        if (hoveredImage == newImage) return;

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

    // ---------------- CLOUDS ----------------
    private void addClouds() {
        cloud1 = new GOval(0, 80, 250, 100);
        cloud1.setFilled(true);
        cloud1.setColor(new Color(200, 200, 200));

        cloud2 = new GOval(300, 140, 300, 120);
        cloud2.setFilled(true);
        cloud2.setColor(new Color(180, 180, 180));

        mainScreen.add(cloud1);
        mainScreen.add(cloud2);

        cloud1.sendToBack();
        cloud2.sendToBack();
    }

    private void animateClouds() {
        new Thread(() -> {
            try {
                while (true) {
                    cloud1.move(0.3, 0);
                    cloud2.move(0.2, 0);

                    if (cloud1.getX() > MainApplication.WINDOW_WIDTH) {
                        cloud1.setLocation(-250, cloud1.getY());
                    }

                    if (cloud2.getX() > MainApplication.WINDOW_WIDTH) {
                        cloud2.setLocation(-300, cloud2.getY());
                    }

                    Thread.sleep(20);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ---------------- LIGHTNING ----------------
    private void showLightningBolt() {
        bolt1 = new GLine(950, 80, 900, 170);
        bolt2 = new GLine(900, 170, 940, 170);
        bolt3 = new GLine(940, 170, 880, 280);

        bolt1.setColor(Color.YELLOW);
        bolt2.setColor(Color.YELLOW);
        bolt3.setColor(Color.YELLOW);

        mainScreen.add(bolt1);
        mainScreen.add(bolt2);
        mainScreen.add(bolt3);
    }

    private void hideLightningBolt() {
        if (bolt1 != null) mainScreen.remove(bolt1);
        if (bolt2 != null) mainScreen.remove(bolt2);
        if (bolt3 != null) mainScreen.remove(bolt3);
    }

    private void lightningFlash() {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(3000 + (int)(Math.random() * 3000));

                    // First flash
                    mainScreen.getGCanvas().setBackground(Color.WHITE);
                    showLightningBolt();
                    Thread.sleep(300);

                    hideLightningBolt();
                    mainScreen.getGCanvas().setBackground(new Color(220, 220, 220));
                    Thread.sleep(150);

                    // Second flash (realistic)
                    mainScreen.getGCanvas().setBackground(Color.WHITE);
                    showLightningBolt();
                    Thread.sleep(350);

                    hideLightningBolt();
                    mainScreen.getGCanvas().setBackground(new Color(220, 220, 220));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}