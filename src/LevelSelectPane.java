import acm.graphics.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import characters.Hueman;

public class LevelSelectPane extends GraphicsPane {

    private GImage huemanImage;

    private GPolygon[] stars;
    private GPolygon[] starGlows;

    private double[] pointX;
    private double[] pointY;

    private int currentPoint = 0;

    private boolean showLevelScreen = false;
    private int selectedLevel = 1;

    private GRect levelScreenBG;
    private GLabel levelTitle;
    private GRect startButton;
    private GLabel startText;
    private GRect backButton;
    private GLabel backText;
    private GImage levelEnemyImage;

    private ArrayList<GObject> previewObjects = new ArrayList<GObject>();

    public LevelSelectPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }
    

    @Override
    public void showContent() {
        showLevelScreen = false;
        clearPreviewScreen();

        Hueman player = mainScreen.getPlayer();
        int levelProgress = mainScreen.getCurrentLevel();

        removeAllStars();

        stars = new GPolygon[8];
        starGlows = new GPolygon[8];

        pointX = new double[] {
            400,  // star 1
            300,  // star 2
            1500,  // star 3
            1600,  // star 4
            400,  // star 5
            300,  // star 6
            1500, // star 7
            1600  // star 8
        };

        pointY = new double[] {
            900,  // star 1
            800,  // star 2
            700,  // star 3
            600,  // star 4
            500,  // star 5
            400,  // star 6
            300,  // star 7
            200   // star 8
        };

        drawRopes();

        for (int i = 0; i < 8; i++) {
            boolean isBoss = ((i + 1) % 2 == 0);

            double outerGlow = isBoss ? 58 : 44;
            double innerGlow = isBoss ? 28 : 20;

            double outerStar = isBoss ? 50 : 36;
            double innerStar = isBoss ? 24 : 16;

            starGlows[i] = createStar(pointX[i], pointY[i], outerGlow, innerGlow);
            starGlows[i].setFilled(true);
            starGlows[i].setFillColor(new Color(255, 230, 120));
            starGlows[i].setColor(new Color(255, 230, 120));
            mainScreen.add(starGlows[i]);

            stars[i] = createStar(pointX[i], pointY[i], outerStar, innerStar);
            stars[i].setFilled(true);

            if (levelProgress > i + 1) {
                stars[i].setFillColor(getPlayerColor());
            } else {
                stars[i].setFillColor(Color.BLACK);
            }

            stars[i].setColor(Color.DARK_GRAY);
            mainScreen.add(stars[i]);
        }

        currentPoint = levelProgress - 1;
        if (currentPoint < 0) currentPoint = 0;
        if (currentPoint > 8) currentPoint = 8;

        huemanImage = player.getSprite("overworld");
        huemanImage.scale(0.6);

        double playerStartX = 20;
        double playerStartY = 900;

        if (currentPoint == 0) {
            huemanImage.setLocation(playerStartX, playerStartY);
        } else {
            huemanImage.setLocation(pointX[currentPoint - 1], pointY[currentPoint - 1]);
        }

        mainScreen.add(huemanImage);
        huemanImage.sendToFront();
    }
    
    
    private ArrayList<GObject> ropeObjects = new ArrayList<GObject>();

    private void drawRopes() {
        clearRopes();

        Color ropeMain = new Color(120, 85, 45);
        Color ropeShadow = new Color(85, 55, 25);

        for (int i = 0; i < pointX.length - 1; i++) {
            double x1 = pointX[i];
            double y1 = pointY[i];
            double x2 = pointX[i + 1];
            double y2 = pointY[i + 1];

            GLine rope1 = new GLine(x1, y1, x2, y2);
            rope1.setColor(ropeMain);
            mainScreen.add(rope1);
            ropeObjects.add(rope1);

            GLine rope2 = new GLine(x1 + 2, y1 + 2, x2 + 2, y2 + 2);
            rope2.setColor(ropeShadow);
            mainScreen.add(rope2);
            ropeObjects.add(rope2);

            for (int k = 1; k <= 4; k++) {
                double t = k / 5.0;
                double mx = x1 + (x2 - x1) * t;
                double my = y1 + (y2 - y1) * t;

                GLine knot = new GLine(mx - 4, my - 4, mx + 4, my + 4);
                knot.setColor(ropeShadow);
                mainScreen.add(knot);
                ropeObjects.add(knot);
            }
        }
    }
    

    @Override
    public void hideContent() {
        if (huemanImage != null) mainScreen.remove(huemanImage);
        removeAllStars();
        clearRopes();
        clearPreviewScreen();
        showLevelScreen = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (showLevelScreen) return;
        if (huemanImage == null) return;

        if (e.getKeyCode() == KeyEvent.VK_D) {
            moveForward();
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            moveBackward();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (showLevelScreen) {
            if (startButton != null && startButton.contains(e.getX(), e.getY())) {
                if (selectedLevel == 1) {
                    mainScreen.switchToSecondBattleScreen();
                } else if (selectedLevel == 2) {
                    mainScreen.switchToCutScene3Screen();
                } else if (selectedLevel == 3) {
                    mainScreen.switchToFourthBattleScreen();
                } else if (selectedLevel == 4) {
                    mainScreen.switchToFifthBattleScreen();
                }
                return;
            }

            if (backButton != null && backButton.contains(e.getX(), e.getY())) {
                clearPreviewScreen();
                showLevelScreen = false;
                return;
            }
        }

        int levelProgress = mainScreen.getCurrentLevel();

        for (int i = 0; i < 8; i++) {
            if (stars[i] != null && stars[i].contains(e.getX(), e.getY()) && levelProgress >= i + 1) {
                currentPoint = i + 1;
                selectedLevel = i + 1;
                showLevelOneScreen();
                return;
            }
        }
    }

    private void moveForward() {
        int maxReachablePoint = mainScreen.getCurrentLevel();

        if (currentPoint < maxReachablePoint && currentPoint < pointX.length) {
            currentPoint++;

            double x = pointX[currentPoint - 1];
            double y = pointY[currentPoint - 1];

            huemanImage.setLocation(x, y);

            selectedLevel = currentPoint;
            showLevelOneScreen();
        }
    }

    private void moveBackward() {
        if (currentPoint > 0) {
            currentPoint--;

            if (currentPoint == 0) {
                double playerStartX = 20;
                double playerStartY = 500;
                huemanImage.setLocation(playerStartX, playerStartY);
            } else {
                double x = pointX[currentPoint - 1];
                double y = pointY[currentPoint - 1];
                huemanImage.setLocation(x, y);
            }
        }
    }

    private void removeAllStars() {
        if (stars != null) {
            for (GPolygon s : stars) {
                if (s != null) mainScreen.remove(s);
            }
        }

        if (starGlows != null) {
            for (GPolygon s : starGlows) {
                if (s != null) mainScreen.remove(s);
            }
        }
    }

    private void showLevelOneScreen() {
        clearPreviewScreen();
        showLevelScreen = true;

        Color bgGray = new Color(225, 225, 225);
        Color mainColor;
        Color glowColor;
        String enemyName = "";

        if (selectedLevel == 1) {
            mainColor = new Color(0, 255, 220);
            glowColor = new Color(0, 200, 170);
            enemyName = "mint.png";
        } 
        else if (selectedLevel == 2) {
            mainColor = new Color(11, 95, 90);
            glowColor = new Color(19, 115, 109);
            enemyName = "slujupiter.png";
        } 
        else if (selectedLevel == 3) {
            mainColor = new Color(170, 100, 255);
            glowColor = new Color(130, 70, 220);
            enemyName = "lavender.png";
        } 
        else if (selectedLevel == 4) {
            mainColor = new Color(190, 140, 255);
            glowColor = new Color(145, 100, 220);
            enemyName = "effervena.png";
        } 
        else if (selectedLevel == 5) {
            mainColor = new Color(255, 120, 120);
            glowColor = new Color(210, 90, 90);
            enemyName = "";
        } 
        else if (selectedLevel == 6) {
            mainColor = new Color(255, 80, 80);
            glowColor = new Color(200, 50, 50);
            enemyName = "";
        } 
        else if (selectedLevel == 7) {
            mainColor = new Color(120, 180, 255);
            glowColor = new Color(80, 140, 220);
            enemyName = "";
        } 
        else {
            mainColor = new Color(80, 120, 255);
            glowColor = new Color(50, 90, 220);
            enemyName = "";
        }

        levelScreenBG = new GRect(
            0,
            0,
            MainApplication.WINDOW_WIDTH,
            MainApplication.WINDOW_HEIGHT
        );
        levelScreenBG.setFilled(true);
        levelScreenBG.setFillColor(bgGray);
        levelScreenBG.setColor(bgGray);
        addPreviewObject(levelScreenBG);

        addBrokenDivider(MainApplication.WINDOW_WIDTH / 2.0);

        if (!enemyName.equals("")) {
            levelEnemyImage = new GImage(enemyName);
            levelEnemyImage.scale(0.7);

            double imgX = 325;
            double imgY = (MainApplication.WINDOW_HEIGHT - levelEnemyImage.getHeight()) / 2.0 + 10;
            levelEnemyImage.setLocation(imgX, imgY);
            addPreviewObject(levelEnemyImage);
        } else {
            GRect placeholder = new GRect(430, 180, 220, 220);
            placeholder.setFilled(true);
            placeholder.setFillColor(Color.BLACK);
            placeholder.setColor(mainColor);

            GLabel comingSoon = new GLabel("COMING SOON");
            comingSoon.setFont("Arial-Bold-26");
            comingSoon.setColor(mainColor);
            comingSoon.setLocation(455, 300);

            addPreviewObject(placeholder);
            addPreviewObject(comingSoon);
        }

        String labelText = ((selectedLevel % 2 == 1) ? "Minion " : "Boss ") + selectedLevel;

        levelTitle = new GLabel(labelText);
        levelTitle.setFont("Arial-Bold-38");
        levelTitle.setColor(mainColor);
        levelTitle.setLocation(120, 255);

        GLabel levelTitleGlow1 = new GLabel(labelText);
        levelTitleGlow1.setFont("Arial-Bold-38");
        levelTitleGlow1.setColor(glowColor);
        levelTitleGlow1.setLocation(118, 253);

        GLabel levelTitleGlow2 = new GLabel(labelText);
        levelTitleGlow2.setFont("Arial-Bold-38");
        levelTitleGlow2.setColor(glowColor);
        levelTitleGlow2.setLocation(122, 257);

        double btnX = 120;
        double btnY = 320;
        double btnW = 220;
        double btnH = 70;

        GRect startGlow = new GRect(btnX - 3, btnY - 3, btnW + 6, btnH + 6);
        startGlow.setFilled(true);
        startGlow.setFillColor(mainColor);
        startGlow.setColor(mainColor);

        startButton = new GRect(btnX, btnY, btnW, btnH);
        startButton.setFilled(true);
        startButton.setFillColor(Color.BLACK);
        startButton.setColor(mainColor);

        startText = new GLabel("START");
        startText.setFont("Arial-Bold-24");

        GLabel startTextGlow1 = new GLabel("START");
        startTextGlow1.setFont("Arial-Bold-24");
        startTextGlow1.setColor(glowColor);

        GLabel startTextGlow2 = new GLabel("START");
        startTextGlow2.setFont("Arial-Bold-24");
        startTextGlow2.setColor(glowColor);

        double startTextX = btnX + (btnW - startText.getWidth()) / 2.0;
        double startTextY = btnY + 45;

        startTextGlow1.setLocation(startTextX - 1, startTextY - 1);
        startTextGlow2.setLocation(startTextX + 1, startTextY + 1);

        startText.setColor(mainColor);
        startText.setLocation(startTextX, startTextY);

        double backY = 410;

        GRect backGlow = new GRect(btnX - 3, backY - 3, btnW + 6, btnH + 6);
        backGlow.setFilled(true);
        backGlow.setFillColor(mainColor);
        backGlow.setColor(mainColor);

        backButton = new GRect(btnX, backY, btnW, btnH);
        backButton.setFilled(true);
        backButton.setFillColor(Color.BLACK);
        backButton.setColor(mainColor);

        backText = new GLabel("BACK");
        backText.setFont("Arial-Bold-24");

        GLabel backTextGlow1 = new GLabel("BACK");
        backTextGlow1.setFont("Arial-Bold-24");
        backTextGlow1.setColor(glowColor);

        GLabel backTextGlow2 = new GLabel("BACK");
        backTextGlow2.setFont("Arial-Bold-24");
        backTextGlow2.setColor(glowColor);

        double backTextX = btnX + (btnW - backText.getWidth()) / 2.0;
        double backTextY = backY + 45;

        backTextGlow1.setLocation(backTextX - 1, backTextY - 1);
        backTextGlow2.setLocation(backTextX + 1, backTextY + 1);

        backText.setColor(mainColor);
        backText.setLocation(backTextX, backTextY);

        addPreviewObject(levelTitleGlow1);
        addPreviewObject(levelTitleGlow2);
        addPreviewObject(levelTitle);

        addPreviewObject(startGlow);
        addPreviewObject(startButton);
        addPreviewObject(startTextGlow1);
        addPreviewObject(startTextGlow2);
        addPreviewObject(startText);

        addPreviewObject(backGlow);
        addPreviewObject(backButton);
        addPreviewObject(backTextGlow1);
        addPreviewObject(backTextGlow2);
        addPreviewObject(backText);
    }

    private void addBrokenDivider(double centerX) {
        Color lineColor = new Color(120, 120, 120);

        double[] yPoints = {0, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600};
        double[] offsets = {-4, 3, -6, 5, -3, 6, -5, 4, -2, 3, -4};

        for (int i = 0; i < yPoints.length - 1; i++) {
            GLine piece = new GLine(
                centerX + offsets[i], yPoints[i],
                centerX + offsets[i + 1], yPoints[i + 1]
            );
            piece.setColor(lineColor);
            addPreviewObject(piece);
        }

        for (int i = 0; i < yPoints.length; i++) {
            GLine ripLeft = new GLine(
                centerX + offsets[i], yPoints[i],
                centerX - 8 + offsets[i], yPoints[i] + 8
            );
            ripLeft.setColor(lineColor);
            addPreviewObject(ripLeft);

            GLine ripRight = new GLine(
                centerX + offsets[i], yPoints[i],
                centerX + 8 + offsets[i], yPoints[i] - 8
            );
            ripRight.setColor(lineColor);
            addPreviewObject(ripRight);
        }
    }

    private void addPreviewObject(GObject obj) {
        mainScreen.add(obj);
        previewObjects.add(obj);
    }

    private void clearPreviewScreen() {
        for (GObject obj : previewObjects) {
            mainScreen.remove(obj);
        }
        previewObjects.clear();

        levelScreenBG = null;
        levelTitle = null;
        startButton = null;
        startText = null;
        backButton = null;
        backText = null;
        levelEnemyImage = null;
    }

    private GPolygon createStar(double centerX, double centerY, double outerR, double innerR) {
        GPolygon star = new GPolygon();

        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            double r = (i % 2 == 0) ? outerR : innerR;

            double x = centerX + r * Math.cos(angle);
            double y = centerY + r * Math.sin(angle);

            star.addVertex(x - centerX, y - centerY);
        }

        star.setFilled(true);
        star.setFillColor(Color.BLACK);
        star.setColor(Color.DARK_GRAY);
        star.setLocation(centerX, centerY);

        return star;
    }

    private Color getPlayerColor() {
        String selectedColor = mainScreen.getSelectedColor();

        if (selectedColor == null) return Color.BLACK;
        if (selectedColor.equals("red")) return Color.RED;
        if (selectedColor.equals("green")) return Color.GREEN;
        return Color.BLUE;
    }
    
    private void clearRopes() {
        for (GObject obj : ropeObjects) {
            mainScreen.remove(obj);
        }
        ropeObjects.clear();
    }
}

//rtbnyt