import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
import characters.Hueman;
import characters.Lavender;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class FourthBattlePane extends GraphicsPane {

    private GImage huemanImage;
    private GImage lavenderImage;

    private GRect huemanHealthBar;
    private GRect huemanHealthBack;

    private GRect lavenderHealthBar;
    private GRect lavenderHealthBack;

    private double huemanMaxHP;
    private double lavenderMaxHP;

    private GRect actionBox;
    private GLabel fightOption;
    private GLabel abilityOption;

    private boolean showingFightMenu = false;

    private Hueman h1;
    private Lavender l1;

    private GLabel huemanName;
    private GLabel lavenderName;

    private GRect continueButton;
    private GLabel continueLabel;

    private boolean playerTurn = true;
    private boolean battleOver = false;

    private GRect superMeterBar;
    private GRect superMeterBack;
    private GLabel superLabel;

    public FourthBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        h1 = mainScreen.getPlayer();
        l1 = new Lavender(600, 20, 10, "Lavender");

        huemanMaxHP = h1.getHP();
        lavenderMaxHP = l1.getHP();

        huemanImage = new GImage(getHuemanImage());
        lavenderImage = new GImage("lavender.png");

        huemanImage.scale(0.6);
        lavenderImage.scale(0.9);

        double hx = MainApplication.WINDOW_WIDTH * 0.25 - huemanImage.getWidth() / 2 + 30;
        double hy = 200;
        huemanImage.setLocation(hx, hy);

        double lx = MainApplication.WINDOW_WIDTH * 0.75 - lavenderImage.getWidth() / 2;
        double ly = 10;
        lavenderImage.setLocation(lx, ly);

        mainScreen.add(huemanImage);
        mainScreen.add(lavenderImage);

        contents.add(huemanImage);
        contents.add(lavenderImage);

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

        double lxBar = 30;
        double lyBar = 30;

        lavenderHealthBack = new GRect(lxBar, lyBar, barWidth, barHeight);
        lavenderHealthBack.setFilled(true);
        lavenderHealthBack.setFillColor(Color.DARK_GRAY);

        lavenderHealthBar = new GRect(lxBar, lyBar, barWidth, barHeight);
        lavenderHealthBar.setFilled(true);
        lavenderHealthBar.setFillColor(new Color(190, 140, 255));
        lavenderHealthBar.setColor(new Color(145, 100, 220));

        mainScreen.add(lavenderHealthBack);
        mainScreen.add(lavenderHealthBar);

        contents.add(lavenderHealthBack);
        contents.add(lavenderHealthBar);

        huemanName = new GLabel(h1.getName());
        huemanName.setFont("Arial-Bold-14");
        huemanName.setLocation(
            huemanHealthBack.getX(),
            huemanHealthBack.getY() - 5
        );

        lavenderName = new GLabel(l1.getName());
        lavenderName.setFont("Arial-Bold-14");
        lavenderName.setLocation(
            lavenderHealthBack.getX(),
            lavenderHealthBack.getY() - 5
        );

        mainScreen.add(huemanName);
        mainScreen.add(lavenderName);

        contents.add(huemanName);
        contents.add(lavenderName);

        double boxWidth = 300;
        double boxHeight = 120;

        double boxX = 240;
        double boxY = MainApplication.WINDOW_HEIGHT - boxHeight - 30;

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
        innerFrame.setColor(new Color(190, 140, 255));
        mainScreen.add(innerFrame);
        contents.add(innerFrame);

        fightOption = new GLabel("FIGHT");
        fightOption.setFont("Arial-Bold-18");
        fightOption.setLocation(boxX + 20, boxY + 40);

        mainScreen.add(fightOption);
        contents.add(fightOption);

        continueButton = new GRect(
            MainApplication.WINDOW_WIDTH - 180,
            MainApplication.WINDOW_HEIGHT - 50,
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
        continueLabel.setLocation(
            continueButton.getX() + 35,
            continueButton.getY() + 18
        );

        mainScreen.add(continueLabel);
        contents.add(continueLabel);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (battleOver) return;

        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == continueButton || obj == continueLabel) {
            mainScreen.setCurrentLevel(4);
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
                    h1.useAbility(1, l1);
                    updateHealthBars();
                    checkBattleEnd();

                    if (!battleOver) {
                        playerTurn = false;
                        performEnemyTurn();
                        checkBattleEnd();
                    }
                }
            }
        }
    }

    private void openFightMenu() {
        showingFightMenu = true;

        mainScreen.remove(fightOption);
        contents.remove(fightOption);

        fightOption = new GLabel("Basic Attack");
        fightOption.setFont("Arial-Bold-18");
        fightOption.setLocation(actionBox.getX() + 20, actionBox.getY() + 40);

        abilityOption = new GLabel("Superpower");
        abilityOption.setFont("Arial-Bold-18");
        abilityOption.setLocation(actionBox.getX() + 20, actionBox.getY() + 70);

        mainScreen.add(fightOption);
        mainScreen.add(abilityOption);

        contents.add(fightOption);
        contents.add(abilityOption);
    }

    private void updateSuperMeter() {
        double ratio = (double) h1.getSuperMeter() / 500.0;
        superMeterBar.setSize(120 * ratio, 12);
    }

    private void performPlayerAttack() {
        if (!playerTurn || battleOver) return;

        h1.attackOpponent(l1);
        updateHealthBars();

        playerTurn = false;
    }

    private void performEnemyTurn() {
        if (battleOver) return;

        if (l1.isAlive()) {
            l1.takeTurn(h1);
            updateHealthBars();
        }

        playerTurn = true;
    }

    private void checkBattleEnd() {
        if (!l1.isAlive()) {
            battleOver = true;
            System.out.println("YOU WIN");
            mainScreen.setCurrentLevel(4);
            mainScreen.switchToLevelSelectScreen();
            return;
        }

        if (!h1.isAlive()) {
            battleOver = true;
            System.out.println("YOU LOSE");
        }
    }

    private void updateHealthBars() {
        double huemanRatio = (double) h1.getHP() / huemanMaxHP;
        double lavenderRatio = (double) l1.getHP() / lavenderMaxHP;

        huemanHealthBar.setSize(200 * huemanRatio, 12);
        lavenderHealthBar.setSize(200 * lavenderRatio, 12);

        updateSuperMeter();
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