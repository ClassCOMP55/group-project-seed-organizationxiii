import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
import characters.Decima;
import characters.Hueman;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class EighthBattlePane extends GraphicsPane {

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

    private GRect continueButton;
    private GLabel continueLabel;

    private boolean playerTurn = true;
    private boolean battleOver = false;

    private GRect superMeterBar;
    private GRect superMeterBack;
    private GLabel superLabel;

    public EighthBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {

        h1 = mainScreen.getPlayer();
        d1 = new Decima(600, 20, 10, "Decima", Decima.Phase.FIRST);

        huemanMaxHP = h1.getHP();
        decimaMaxHP = d1.getHP();

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

        double barWidth = 200;
        double barHeight = 12;

        double hxBar = MainApplication.WINDOW_WIDTH / 2.2 - barWidth / 2;
        double hyBar = MainApplication.WINDOW_HEIGHT - barHeight - 150;

        huemanHealthBack = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBack.setFilled(true);
        huemanHealthBack.setFillColor(Color.DARK_GRAY);

        huemanHealthBar = new GRect(hxBar, hyBar, barWidth, barHeight);
        huemanHealthBar.setFilled(true);

        String color = mainScreen.getSelectedColor();

        if (color != null) {
            if (color.equals("red")) {
                huemanHealthBar.setFillColor(new Color(220, 60, 60));
            } else if (color.equals("green")) {
                huemanHealthBar.setFillColor(new Color(60, 220, 100));
            } else if (color.equals("blue")) {
                huemanHealthBar.setFillColor(new Color(60, 140, 255));
            } else {
                huemanHealthBar.setFillColor(new Color(60, 220, 100));
            }
        } else {
            huemanHealthBar.setFillColor(new Color(60, 220, 100));
        }

        huemanHealthBar.setColor(huemanHealthBar.getFillColor().darker());

        mainScreen.add(huemanHealthBack);
        mainScreen.add(huemanHealthBar);

        contents.add(huemanHealthBack);
        contents.add(huemanHealthBar);

        double meterWidth = 120;
        double meterHeight = 12;
        double spacing = 15;

        double mx = huemanHealthBack.getX() + huemanHealthBack.getWidth() + spacing;
        double my = huemanHealthBack.getY();

        superMeterBack = new GRect(mx, my, meterWidth, meterHeight);
        superMeterBack.setFilled(true);
        superMeterBack.setFillColor(Color.DARK_GRAY);

        superMeterBar = new GRect(mx, my, 0, meterHeight);
        superMeterBar.setFilled(true);
        superMeterBar.setFillColor(Color.ORANGE);

        superLabel = new GLabel("SP");
        superLabel.setFont("Arial-Bold-10");
        superLabel.setLocation(mx, my - 2);

        mainScreen.add(superMeterBack);
        mainScreen.add(superMeterBar);
        mainScreen.add(superLabel);

        contents.add(superMeterBack);
        contents.add(superMeterBar);
        contents.add(superLabel);

        double dxBar = 30;
        double dyBar = 30;

        decimaHealthBack = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBack.setFilled(true);
        decimaHealthBack.setFillColor(Color.DARK_GRAY);

        decimaHealthBar = new GRect(dxBar, dyBar, barWidth, barHeight);
        decimaHealthBar.setFilled(true);
        decimaHealthBar.setFillColor(new Color(212, 175, 55));
        decimaHealthBar.setColor(new Color(150, 120, 30));

        mainScreen.add(decimaHealthBack);
        mainScreen.add(decimaHealthBar);

        contents.add(decimaHealthBack);
        contents.add(decimaHealthBar);

        huemanName = new GLabel(h1.getName());
        huemanName.setFont("Arial-Bold-14");
        huemanName.setLocation(huemanHealthBack.getX(), huemanHealthBack.getY() - 5);

        decimaName = new GLabel(d1.getName());
        decimaName.setFont("Arial-Bold-14");
        decimaName.setLocation(decimaHealthBack.getX(), decimaHealthBack.getY() - 5);

        mainScreen.add(huemanName);
        mainScreen.add(decimaName);

        contents.add(huemanName);
        contents.add(decimaName);

        double boxWidth = 300;
        double boxHeight = 120;

        double boxX = 240;
        double boxY = MainApplication.WINDOW_HEIGHT - boxHeight - 150;

        GRect actionBoxShadow = new GRect(boxX + 6, boxY + 6, boxWidth, boxHeight);
        actionBoxShadow.setFilled(true);
        actionBoxShadow.setFillColor(new Color(120, 120, 120));
        actionBoxShadow.setColor(new Color(120, 120, 120));
        mainScreen.add(actionBoxShadow);
        contents.add(actionBoxShadow);

        actionBox = new GRect(boxX, boxY, boxWidth, boxHeight);
        actionBox.setFilled(true);
        actionBox.setFillColor(Color.WHITE);
        actionBox.setColor(Color.BLACK);
        mainScreen.add(actionBox);
        contents.add(actionBox);

        GRect innerFrame = new GRect(boxX + 6, boxY + 6, boxWidth - 12, boxHeight - 12);
        innerFrame.setFilled(false);
        innerFrame.setColor(new Color(212, 175, 55));
        mainScreen.add(innerFrame);
        contents.add(innerFrame);

        fightOption = new GLabel("FIGHT");
        fightOption.setFont("Arial-Bold-18");
        fightOption.setLocation(boxX + 20, boxY + 40);

        mainScreen.add(fightOption);
        contents.add(fightOption);

        continueButton = new GRect(
            MainApplication.WINDOW_WIDTH - 180,
            MainApplication.WINDOW_HEIGHT - 160,
            160,
            25
        );

        continueButton.setFilled(true);
        continueButton.setFillColor(Color.LIGHT_GRAY);
        continueButton.setColor(Color.BLACK);

        mainScreen.add(continueButton);
        contents.add(continueButton);

        continueLabel = new GLabel("Continue");
        continueLabel.setFont("Arial-Bold-16");
        continueLabel.setLocation(continueButton.getX() + 35, continueButton.getY() + 18);

        mainScreen.add(continueLabel);
        contents.add(continueLabel);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (battleOver) return;

        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == continueButton || obj == continueLabel) {
            mainScreen.setCurrentLevel(8);
            mainScreen.switchToLevelSelectScreen();
            return;
        }

        if (!showingFightMenu) {
            if (obj == fightOption) {
                openFightMenu();
            }
        } else {
            if (obj == fightOption && playerTurn) {
                performPlayerAttack();
                checkBattleEnd();

                if (!battleOver) {
                    performEnemyTurn();
                    checkBattleEnd();
                }
            } else if (obj == abilityOption) {
                if (h1.getSuperMeter() > 0) {
                    h1.useAbility(1, d1);
                    updateHealthBars();
                }
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

    private void updateSuperMeter() {
        double ratio = (double) h1.getSuperMeter() / 500.0;
        superMeterBar.setSize(200 * ratio, 8);
    }

    private void performPlayerAttack() {
        if (!playerTurn || battleOver) return;

        h1.attackOpponent(d1);
        updateHealthBars();

        playerTurn = false;
    }

    private void performEnemyTurn() {
        if (battleOver) return;

        if (d1.isAlive()) {
            d1.takeTurn(h1);
            updateHealthBars();
        }

        playerTurn = true;
    }

    private void checkBattleEnd() {
        if (!d1.isAlive()) {
            battleOver = true;
            System.out.println("YOU WIN");
            mainScreen.switchToCutScene2Screen();
            return;
        }

        if (!h1.isAlive()) {
            battleOver = true;
            System.out.println("YOU LOSE");
        }
    }

    private void updateHealthBars() {
        double huemanRatio = (double) h1.getHP() / huemanMaxHP;
        double decimaRatio = (double) d1.getHP() / decimaMaxHP;

        huemanHealthBar.setSize(200 * huemanRatio, 12);
        decimaHealthBar.setSize(200 * decimaRatio, 12);

        updateSuperMeter();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (huemanImage == null) return;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) huemanImage.move(-10, 0);
        else if (key == KeyEvent.VK_RIGHT) huemanImage.move(10, 0);
        else if (key == KeyEvent.VK_UP) huemanImage.move(0, -10);
        else if (key == KeyEvent.VK_DOWN) huemanImage.move(0, 10);
    }

    private String getHuemanImage() {
        String selectedColor = mainScreen.getSelectedColor();

        if (selectedColor == null) return "redback.png";
        if (selectedColor.equals("red")) return "redback.png";
        if (selectedColor.equals("green")) return "greenback.png";
        return "blueback.png";
    }

    @Override
    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}