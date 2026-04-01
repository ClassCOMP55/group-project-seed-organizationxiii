import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
import characters.Decima;
import characters.Hueman;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class FirstBattlePane extends GraphicsPane {

    private GImage huemanImage;
    private GImage decimaImage;

    private GRect huemanHealthBar;
    private GRect huemanHealthBack;

    private GRect decimaHealthBar;
    private GRect decimaHealthBack;

    private double huemanMaxHP;
    private double decimaMaxHP;

    private GRect actionBox;
    private GLabel fightOption;
    private GLabel abilityOption;

    private boolean showingFightMenu = false;

    private Hueman h1;
    private Decima d1;

    private GLabel huemanName;
    private GLabel decimaName;

    public FirstBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {

        // =========================
        // 1. Create characters
        // =========================
        h1 = mainScreen.getPlayer();
        d1 = new Decima(600, 20, 10, "Decima", Decima.Phase.FIRST);

        huemanMaxHP = h1.getHP();
        decimaMaxHP = d1.getHP();

        // =========================
        // 2. Create images
        // =========================
        huemanImage = new GImage(getHuemanImage());
        decimaImage = new GImage("Decima.png");

        huemanImage.scale(0.6);
        decimaImage.scale(0.9);

        double hx = MainApplication.WINDOW_WIDTH * 0.25 - huemanImage.getWidth() / 2 + 30;
        double hy = 200;
        huemanImage.setLocation(hx, hy);

        double dx = MainApplication.WINDOW_WIDTH * 0.75 - decimaImage.getWidth() / 2;
        double dy = 10;
        decimaImage.setLocation(dx, dy);

        mainScreen.add(huemanImage);
        mainScreen.add(decimaImage);

        contents.add(huemanImage);
        contents.add(decimaImage);

        // =========================
        // 3. Create health bars
        // =========================
        double barWidth = 200;
        double barHeight = 12;

        // Hueman bar (bottom right)
        double hxBar = MainApplication.WINDOW_WIDTH - barWidth - 30;
        double hyBar = MainApplication.WINDOW_HEIGHT - barHeight - 30;

        huemanHealthBack = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBack.setFilled(true);
        huemanHealthBack.setFillColor(Color.DARK_GRAY);

        huemanHealthBar = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBar.setFilled(true);
        huemanHealthBar.setFillColor(Color.GREEN);

        mainScreen.add(huemanHealthBack);
        mainScreen.add(huemanHealthBar);

        contents.add(huemanHealthBack);
        contents.add(huemanHealthBar);

        // Decima bar (top left)
        double dxBar = 30;
        double dyBar = 30;

        decimaHealthBack = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBack.setFilled(true);
        decimaHealthBack.setFillColor(Color.DARK_GRAY);

        decimaHealthBar = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBar.setFilled(true);
        decimaHealthBar.setFillColor(Color.RED);

        mainScreen.add(decimaHealthBack);
        mainScreen.add(decimaHealthBar);

        contents.add(decimaHealthBack);
        contents.add(decimaHealthBar);

        // =========================
        // 4. Create names (AFTER bars exist)
        // =========================
        huemanName = new GLabel(h1.getName());
        huemanName.setFont("Arial-Bold-14");
        huemanName.setLocation(
                huemanHealthBack.getX(),
                huemanHealthBack.getY() - 5
        );

        decimaName = new GLabel(d1.getName());
        decimaName.setFont("Arial-Bold-14");
        decimaName.setLocation(
                decimaHealthBack.getX(),
                decimaHealthBack.getY() - 5
        );

        mainScreen.add(huemanName);
        mainScreen.add(decimaName);

        contents.add(huemanName);
        contents.add(decimaName);

        // =========================
        // 5. Action box
        // =========================
        double boxWidth = 300;
        double boxHeight = 120;

        double boxX = 50;
        double boxY = MainApplication.WINDOW_HEIGHT - boxHeight - 30;

        actionBox = new GRect(boxX, boxY, boxWidth, boxHeight);
        actionBox.setFilled(true);
        actionBox.setFillColor(Color.WHITE);
        actionBox.setColor(Color.BLACK);

        mainScreen.add(actionBox);
        contents.add(actionBox);

        fightOption = new GLabel("FIGHT");
        fightOption.setFont("Arial-Bold-18");
        fightOption.setLocation(boxX + 20, boxY + 40);

        mainScreen.add(fightOption);
        contents.add(fightOption);
    }

    // =========================
    // INPUT
    // =========================
    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (!showingFightMenu) {
            if (obj == fightOption) {
                openFightMenu();
            }
        } else {
            if (obj == fightOption) {
                h1.attackOpponent(d1);
                updateHealthBars();
            } else if (obj == abilityOption) {
                // ability logic here
                updateHealthBars();
            }
        }
    }

    private void openFightMenu() {
        showingFightMenu = true;

        mainScreen.remove(fightOption);
        contents.remove(fightOption);

        fightOption = new GLabel("Basic Attack");
        fightOption.setLocation(actionBox.getX() + 20, actionBox.getY() + 40);

        abilityOption = new GLabel("Superpower");
        abilityOption.setLocation(actionBox.getX() + 20, actionBox.getY() + 70);

        mainScreen.add(fightOption);
        mainScreen.add(abilityOption);

        contents.add(fightOption);
        contents.add(abilityOption);
    }

    // =========================
    // HEALTH UPDATE
    // =========================
    private void updateHealthBars() {
        double huemanRatio = (double) h1.getHP() / huemanMaxHP;
        double decimaRatio = (double) d1.getHP() / decimaMaxHP;

        huemanHealthBar.setSize(200 * huemanRatio, 12);
        decimaHealthBar.setSize(200 * decimaRatio, 12);
    }

    // =========================
    // MOVEMENT (debug)
    // =========================
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

    // =========================
    // IMAGE HELPER
    // =========================
    private String getHuemanImage() {
        String selectedColor = mainScreen.getSelectedColor();

        if (selectedColor == null) return "redback.png";
        if (selectedColor.equals("red")) return "redback.png";
        if (selectedColor.equals("green")) return "greenback.png";
        return "blueback.png";
    }

    // =========================
    // CLEANUP
    // =========================
    @Override
    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}