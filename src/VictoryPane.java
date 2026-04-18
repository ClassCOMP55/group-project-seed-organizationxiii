import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

public class VictoryPane extends GraphicsPane {

    private GRect background;
    private GLabel winLabel;

    private GRect restartButton;
    private GLabel restartLabel;

    public VictoryPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        double W = mainScreen.getWidth();
        double H = mainScreen.getHeight();

        if (W <= 0) W = MainApplication.WINDOW_WIDTH;
        if (H <= 0) H = MainApplication.WINDOW_HEIGHT;

        background = new GRect(0, 0, W, H);
        background.setFilled(true);
        background.setFillColor(Color.BLACK);
        background.setColor(Color.BLACK);
        addObj(background);

        winLabel = new GLabel("YOU WON");
        winLabel.setFont(new Font("Georgia", Font.BOLD, 42));
        winLabel.setColor(Color.WHITE);
        winLabel.setLocation(
                W / 2 - winLabel.getWidth() / 2,
                H / 2 - 40
        );
        addObj(winLabel);

        double buttonW = 220;
        double buttonH = 55;
        double buttonX = W / 2 - buttonW / 2;
        double buttonY = H / 2 + 20;

        restartButton = new GRect(buttonX, buttonY, buttonW, buttonH);
        restartButton.setFilled(true);
        restartButton.setFillColor(new Color(30, 30, 30));
        restartButton.setColor(Color.WHITE);
        addObj(restartButton);

        restartLabel = new GLabel("RESTART");
        restartLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        restartLabel.setColor(Color.WHITE);
        restartLabel.setLocation(
                buttonX + buttonW / 2 - restartLabel.getWidth() / 2,
                buttonY + buttonH / 2 + 8
        );
        addObj(restartLabel);
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == restartButton || obj == restartLabel) {
            mainScreen.switchToTitleScreen();
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