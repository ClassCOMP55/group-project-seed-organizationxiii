import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;
import characters.Hueman;
import characters.MintSentinel;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SecondBattlePane extends GraphicsPane {

    private GImage huemanImage;
    private GImage mintImage;

    private GRect huemanHealthBar;
    private GRect huemanHealthBack;

    private GRect mintHealthBar;
    private GRect mintHealthBack;

    private double huemanMaxHP;
    private double mintMaxHP;

    private GRect actionBox;
    private GLabel fightOption;
    private GLabel abilityOption;

    private boolean showingFightMenu = false;

    private Hueman h1;
    private MintSentinel m1;

    private GLabel huemanName;
    private GLabel mintName;

    private GRect continueButton;
    private GLabel continueLabel;

    private boolean playerTurn = true;
    private boolean battleOver = false;

    private GRect superMeterBar;
    private GRect superMeterBack;
    private GLabel superLabel;

    public SecondBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {

        // =========================
        // 1. Create characters
        // =========================
        h1 = mainScreen.getPlayer();
        m1 = new MintSentinel(700, 24, 12, "Mint");

        huemanMaxHP = h1.getHP();
        mintMaxHP = m1.getHP();

        showingFightMenu = false;
        playerTurn = true;
        battleOver = false;

        // =========================
        // 2. Create images
        // =========================
        huemanImage = new GImage(getHuemanImage());
        mintImage = new GImage("mint.png");

        huemanImage.scale(0.6);
        mintImage.scale(0.7);

        double hx = MainApplication.WINDOW_WIDTH * 0.25 - huemanImage.getWidth() / 2 + 30;
        double hy = 200;
        huemanImage.setLocation(hx, hy);

        double mx = MainApplication.WINDOW_WIDTH * 0.75 - mintImage.getWidth() / 2;
        double my = 10;
        mintImage.setLocation(mx, my);

        mainScreen.add(huemanImage);
        mainScreen.add(mintImage);

        contents.add(huemanImage);
        contents.add(mintImage);

        // =========================
        // 3. Create health bars
        // =========================
        double barWidth = 200;
        double barHeight = 12;

        // Hueman bar
        double hxBar = MainApplication.WINDOW_WIDTH / 2.2 - barWidth / 2;
        double hyBar = MainApplication.WINDOW_HEIGHT - barHeight - 150;

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

        // ===== SUPER METER =====
        double meterWidth = 120;
        double meterHeight = 12;
        double spacing = 15;

        double smx = huemanHealthBack.getX() + huemanHealthBack.getWidth() + spacing;
        double smy = huemanHealthBack.getY();

        superMeterBack = new GRect(smx, smy, meterWidth, meterHeight);
        superMeterBack.setFilled(true);
        superMeterBack.setFillColor(Color.DARK_GRAY);

        superMeterBar = new GRect(smx, smy, 0, meterHeight);
        superMeterBar.setFilled(true);
        superMeterBar.setFillColor(Color.ORANGE);

        superLabel = new GLabel("SP");
        superLabel.setFont("Arial-Bold-10");
        superLabel.setLocation(smx, smy - 2);

        mainScreen.add(superMeterBack);
        mainScreen.add(superMeterBar);
        mainScreen.add(superLabel);

        contents.add(superMeterBack);
        contents.add(superMeterBar);
        contents.add(superLabel);

        // Mint bar
        double mxBar = 30;
        double myBar = 30;

        mintHealthBack = new GRect(mxBar, myBar, barWidth, barHeight);
        mintHealthBack.setFilled(true);
        mintHealthBack.setFillColor(Color.DARK_GRAY);

        mintHealthBar = new GRect(mxBar, myBar, barWidth, barHeight);
        mintHealthBar.setFilled(true);
        mintHealthBar.setFillColor(Color.RED);

        mainScreen.add(mintHealthBack);
        mainScreen.add(mintHealthBar);

        contents.add(mintHealthBack);
        contents.add(mintHealthBar);

        // =========================
        // 4. Create names
        // =========================
        huemanName = new GLabel(h1.getName());
        huemanName.setFont("Arial-Bold-14");
        huemanName.setLocation(
            huemanHealthBack.getX(),
            huemanHealthBack.getY() - 5
        );

        mintName = new GLabel(m1.getName());
        mintName.setFont("Arial-Bold-14");
        mintName.setLocation(
            mintHealthBack.getX(),
            mintHealthBack.getY() - 5
        );

        mainScreen.add(huemanName);
        mainScreen.add(mintName);

        contents.add(huemanName);
        contents.add(mintName);

        // =========================
        // 5. Action box
        // =========================
        double boxWidth = 300;
        double boxHeight = 120;

        double boxX = 240;
        double boxY = MainApplication.WINDOW_HEIGHT - boxHeight - 30;

        // Shadow behind box
        GRect actionBoxShadow = new GRect(boxX + 6, boxY + 6, boxWidth, boxHeight);
        actionBoxShadow.setFilled(true);
        actionBoxShadow.setFillColor(new Color(120, 120, 120));
        actionBoxShadow.setColor(new Color(120, 120, 120));
        mainScreen.add(actionBoxShadow);
        contents.add(actionBoxShadow);

        // Main white box
        actionBox = new GRect(boxX, boxY, boxWidth, boxHeight);
        actionBox.setFilled(true);
        actionBox.setFillColor(Color.WHITE);
        actionBox.setColor(Color.BLACK);
        mainScreen.add(actionBox);
        contents.add(actionBox);

        // Inner gold frame
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

        // =========================
        // 6. Continue button
        // =========================
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
        	mainScreen.setCurrentLevel(2); // you completed level 1
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
                if (h1.getSuperMeter() > 0 && playerTurn) {
                    h1.useAbility(1, m1);
                    updateHealthBars();
                    playerTurn = false;

                    checkBattleEnd();

                    if (!battleOver) {
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
        superMeterBar.setSize(120 * ratio, 12);
    }

    private void performPlayerAttack() {
        if (!playerTurn || battleOver) return;

        h1.attackOpponent(m1);
        updateHealthBars();

        playerTurn = false;
    }

    private void performEnemyTurn() {
        if (battleOver) return;

        if (m1.isAlive()) {
            m1.takeTurn(h1);
            updateHealthBars();
        }

        playerTurn = true;
    }

    private void checkBattleEnd() {
    	if (!m1.isAlive()) {
    	    battleOver = true;
    	    System.out.println("YOU WIN");

    	    mainScreen.setCurrentLevel(2);   // ⭐ THIS is the important line
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
        double mintRatio = (double) m1.getHP() / mintMaxHP;

        huemanHealthBar.setSize(200 * huemanRatio, 12);
        mintHealthBar.setSize(200 * mintRatio, 12);

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