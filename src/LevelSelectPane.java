import acm.graphics.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import characters.Hueman;

public class LevelSelectPane extends GraphicsPane {

    private GImage huemanImage;

    private GPolygon star1, star1Glow;
    private GPolygon star2, star2Glow;
    private GPolygon star3, star3Glow;
    private GPolygon star4, star4Glow;

    private GRect[] seg1Dots;
    private GRect[] seg2Dots;
    private GRect[] seg3Dots;
    private GRect[] seg4Dots;

    private GRect[] stopDots;

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

        double startX = 120;
        double startY = 530;

        double star1X = 640;
        double star1Y = 360;

        double star2X = 260;
        double star2Y = 250;

        double star3X = 670;
        double star3Y = 140;

        double star4X = 260;
        double star4Y = 80;

        double path1X = 610;
        double path1Y = 415;

        double path2X = 290;
        double path2Y = 305;

        double path3X = 640;
        double path3Y = 195;

        double path4X = 290;
        double path4Y = 135;

        seg1Dots = addPathSegment(startX, startY, path1X, path1Y, 12);

        if (levelProgress >= 2) {
            seg2Dots = addPathSegment(path1X, path1Y, path2X, path2Y, 10);
        } else {
            seg2Dots = null;
        }

        if (levelProgress >= 3) {
            seg3Dots = addPathSegment(path2X, path2Y, path3X, path3Y, 10);
        } else {
            seg3Dots = null;
        }

        if (levelProgress >= 4) {
            seg4Dots = addPathSegment(path3X, path3Y, path4X, path4Y, 10);
        } else {
            seg4Dots = null;
        }

        star1Glow = createStar(star1X, star1Y, 55, 28);
        star1Glow.setFilled(true);
        star1Glow.setFillColor(new Color(255, 230, 120));
        star1Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star1Glow);

        star1 = createStar(star1X, star1Y, 48, 24);
        star1.setFilled(true);
        star1.setFillColor(levelProgress >= 2 ? getPlayerColor() : Color.BLACK);
        star1.setColor(Color.DARK_GRAY);
        mainScreen.add(star1);

        star2Glow = createStar(star2X, star2Y, 55, 28);
        star2Glow.setFilled(true);
        star2Glow.setFillColor(new Color(255, 230, 120));
        star2Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star2Glow);

        star2 = createStar(star2X, star2Y, 48, 24);
        star2.setFilled(true);
        star2.setFillColor(levelProgress >= 3 ? getPlayerColor() : Color.BLACK);
        star2.setColor(Color.DARK_GRAY);
        mainScreen.add(star2);

        star3Glow = createStar(star3X, star3Y, 55, 28);
        star3Glow.setFilled(true);
        star3Glow.setFillColor(new Color(255, 230, 120));
        star3Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star3Glow);

        star3 = createStar(star3X, star3Y, 48, 24);
        star3.setFilled(true);
        star3.setFillColor(levelProgress >= 4 ? getPlayerColor() : Color.BLACK);
        star3.setColor(Color.DARK_GRAY);
        mainScreen.add(star3);

        star4Glow = createStar(star4X, star4Y, 55, 28);
        star4Glow.setFilled(true);
        star4Glow.setFillColor(new Color(255, 230, 120));
        star4Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star4Glow);

        star4 = createStar(star4X, star4Y, 48, 24);
        star4.setFilled(true);
        star4.setFillColor(Color.BLACK);
        star4.setColor(Color.DARK_GRAY);
        mainScreen.add(star4);

        pointX = new double[] {
            startX,
            path1X,
            path2X,
            path3X,
            path4X
        };

        pointY = new double[] {
            startY,
            path1Y,
            path2Y,
            path3Y,
            path4Y
        };

        stopDots = new GRect[] {
            null,
            seg1Dots[seg1Dots.length - 1],
            seg2Dots != null ? seg2Dots[seg2Dots.length - 1] : null,
            seg3Dots != null ? seg3Dots[seg3Dots.length - 1] : null,
            seg4Dots != null ? seg4Dots[seg4Dots.length - 1] : null
        };

        currentPoint = levelProgress - 1;
        if (currentPoint < 0) currentPoint = 0;
        if (currentPoint > 4) currentPoint = 4;

        huemanImage = player.getSprite("overworld");
        huemanImage.scale(0.6);
        huemanImage.setLocation(pointX[currentPoint] - 40, pointY[currentPoint] + 30);
        mainScreen.add(huemanImage);
        huemanImage.sendToFront();

        if (currentPoint >= 1 && stopDots[1] != null) {
            mainScreen.remove(stopDots[1]);
            stopDots[1] = null;
        }
        if (currentPoint >= 2 && stopDots[2] != null) {
            mainScreen.remove(stopDots[2]);
            stopDots[2] = null;
        }
        if (currentPoint >= 3 && stopDots[3] != null) {
            mainScreen.remove(stopDots[3]);
            stopDots[3] = null;
        }
        if (currentPoint >= 4 && stopDots[4] != null) {
            mainScreen.remove(stopDots[4]);
            stopDots[4] = null;
        }
    }

    @Override
    public void hideContent() {
        if (huemanImage != null) mainScreen.remove(huemanImage);

        removeDots(seg1Dots);
        removeDots(seg2Dots);
        removeDots(seg3Dots);
        removeDots(seg4Dots);

        removeStar(star1Glow);
        removeStar(star1);
        removeStar(star2Glow);
        removeStar(star2);
        removeStar(star3Glow);
        removeStar(star3);
        removeStar(star4Glow);
        removeStar(star4);

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
        	        mainScreen.switchToFifthBattleScreen(); // ✅ ADD THIS LINE
        	    }
        	    return;
        	}
        }

        int levelProgress = mainScreen.getCurrentLevel();

        if (star1 != null && star1.contains(e.getX(), e.getY()) && levelProgress >= 1) {
            currentPoint = 1;
            selectedLevel = 1;
            showLevelOneScreen();
        } else if (star2 != null && star2.contains(e.getX(), e.getY()) && levelProgress >= 2) {
            currentPoint = 2;
            selectedLevel = 2;
            showLevelOneScreen();
        } else if (star3 != null && star3.contains(e.getX(), e.getY()) && levelProgress >= 3) {
            currentPoint = 3;
            selectedLevel = 3;
            showLevelOneScreen();
        } else if (star4 != null && star4.contains(e.getX(), e.getY()) && levelProgress >= 4) {
            currentPoint = 4;
            selectedLevel = 4;
            showLevelOneScreen();
        }
    }

    private void moveForward() {
        int maxReachablePoint = mainScreen.getCurrentLevel();

        if (currentPoint < maxReachablePoint && currentPoint < pointX.length - 1) {
            currentPoint++;

            double x = pointX[currentPoint];
            double y = pointY[currentPoint];

            huemanImage.setLocation(x - 40, y + 30);

            if (stopDots != null && stopDots[currentPoint] != null) {
                mainScreen.remove(stopDots[currentPoint]);
                stopDots[currentPoint] = null;
            }

            selectedLevel = currentPoint;
            showLevelOneScreen();
        }
    }

    private void moveBackward() {
        if (currentPoint > 0) {
            currentPoint--;

            double x = pointX[currentPoint];
            double y = pointY[currentPoint];

            huemanImage.setLocation(x - 40, y + 30);
        }
    }

    private GRect[] addPathSegment(double x1, double y1, double x2, double y2, int steps) {
        GRect[] segment = new GRect[steps];

        for (int i = 1; i <= steps; i++) {
            double t = (double) (i - 1) / (steps - 1);
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t;

            GRect step = new GRect(x, y, 32, 12);
            step.setFilled(true);
            step.setFillColor(new Color(140, 140, 140));
            step.setColor(Color.DARK_GRAY);

            if (i == steps) {
                step.setSize(70, 16);
                step.setLocation(x - 5, y);
            }

            mainScreen.add(step);
            segment[i - 1] = step;
        }

        return segment;
    }

    private void removeDots(GRect[] dots) {
        if (dots == null) return;

        for (GRect dot : dots) {
            if (dot != null) {
                mainScreen.remove(dot);
            }
        }
    }

    private void removeStar(GPolygon s) {
        if (s != null) {
            mainScreen.remove(s);
        }
    }

    private void showLevelOneScreen() {
        clearPreviewScreen();

        showLevelScreen = true;

        Color bgGray = new Color(225, 225, 225);
        Color mainColor;
        Color glowColor;

        if (selectedLevel == 1) {
            mainColor = new Color(0, 255, 220);
            glowColor = new Color(0, 200, 170);
        } 
        else if (selectedLevel == 2) {
            mainColor = new Color(11, 95, 90);   // slujupiter color
            glowColor = new Color(19, 115, 109);
        } 
        else if (selectedLevel == 3) {
            mainColor = new Color(212, 175, 55);   // decima gold
            glowColor = new Color(150, 120, 30);
        } 
        else {
            mainColor = new Color(0, 255, 220);
            glowColor = new Color(0, 200, 170);
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

        if (selectedLevel == 1) {
            levelEnemyImage = new GImage("mint.png");
        } else if (selectedLevel == 2) {
            levelEnemyImage = new GImage("slujupiter.png");
        } else if (selectedLevel == 3) {
            levelEnemyImage = new GImage("Decima.png");
        } else if (selectedLevel == 4) {
            levelEnemyImage = new GImage("mint.png");
        } 

        levelEnemyImage.scale(0.7);

        double imgX = 325;
        double imgY = (MainApplication.WINDOW_HEIGHT - levelEnemyImage.getHeight()) / 2.0 + 10;
        levelEnemyImage.setLocation(imgX, imgY);

        // ===== TITLE =====
        levelTitle = new GLabel("Level " + selectedLevel);
        levelTitle.setFont("Arial-Bold-38");
        levelTitle.setColor(mainColor);
        levelTitle.setLocation(120, 255);

        GLabel levelTitleGlow1 = new GLabel("Level " + selectedLevel);
        levelTitleGlow1.setFont("Arial-Bold-38");
        levelTitleGlow1.setColor(glowColor);
        levelTitleGlow1.setLocation(118, 253);

        GLabel levelTitleGlow2 = new GLabel("Level " + selectedLevel);
        levelTitleGlow2.setFont("Arial-Bold-38");
        levelTitleGlow2.setColor(glowColor);
        levelTitleGlow2.setLocation(122, 257);

        double btnX = 120;
        double btnY = 320;
        double btnW = 220;
        double btnH = 70;

        // ===== START BUTTON =====
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

        // ===== BACK BUTTON =====
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

        addPreviewObject(levelEnemyImage);
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
}
//test