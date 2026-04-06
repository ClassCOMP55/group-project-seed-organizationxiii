import acm.graphics.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import characters.Hueman;

public class LevelSelectPane extends GraphicsPane {

    private GImage huemanImage;

    private GPolygon star1, star1Glow;
    private GPolygon star2, star2Glow;
    private GPolygon star3, star3Glow;
    private GPolygon star4, star4Glow;

    private GOval[] seg1Dots;
    private GOval[] seg2Dots;
    private GOval[] seg3Dots;
    private GOval[] seg4Dots;

    private GOval[] stopDots;

    private double[] pointX;
    private double[] pointY;
    private int currentPoint = 0;
    
    private boolean showLevelScreen = false;

    private GRect levelScreenBG;
    private GLabel levelTitle;
    private GRect startButton;
    private GLabel startText;
    private GImage levelEnemyImage;
    private GLine dividerLine;
    

    public LevelSelectPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        Hueman player = mainScreen.getPlayer();

        huemanImage = player.getSprite("overworld");
        huemanImage.scale(0.6);
        huemanImage.setLocation(60, 420);
        mainScreen.add(huemanImage);

        double startX = 95;
        double startY = 450;

        double star1X = 670;
        double star1Y = 360;

        double star2X = 260;
        double star2Y = 250;

        double star3X = 670;
        double star3Y = 140;

        double star4X = 260;
        double star4Y = 80;

        seg1Dots = addPathSegment(startX, startY, star1X, star1Y, 5);
        seg2Dots = addPathSegment(star1X, star1Y, star2X, star2Y, 5);
        seg3Dots = addPathSegment(star2X, star2Y, star3X, star3Y, 5);
        seg4Dots = addPathSegment(star3X, star3Y, star4X, star4Y, 5);

        star1Glow = createStar(star1X, star1Y, 55, 28);
        star1Glow.setFilled(true);
        star1Glow.setFillColor(new Color(255, 230, 120));
        star1Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star1Glow);

        star1 = createStar(star1X, star1Y, 48, 24);
        star1.setFilled(true);
        star1.setFillColor(Color.BLACK);
        star1.setColor(Color.DARK_GRAY);
        mainScreen.add(star1);

        star2Glow = createStar(star2X, star2Y, 55, 28);
        star2Glow.setFilled(true);
        star2Glow.setFillColor(new Color(255, 230, 120));
        star2Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star2Glow);

        star2 = createStar(star2X, star2Y, 48, 24);
        star2.setFilled(true);
        star2.setFillColor(Color.BLACK);
        star2.setColor(Color.DARK_GRAY);
        mainScreen.add(star2);

        star3Glow = createStar(star3X, star3Y, 55, 28);
        star3Glow.setFilled(true);
        star3Glow.setFillColor(new Color(255, 230, 120));
        star3Glow.setColor(new Color(255, 230, 120));
        mainScreen.add(star3Glow);

        star3 = createStar(star3X, star3Y, 48, 24);
        star3.setFilled(true);
        star3.setFillColor(Color.BLACK);
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

        stopDots = new GOval[] {
            null,
            seg1Dots[4],
            seg2Dots[4],
            seg3Dots[4],
            seg4Dots[4]
        };

        pointX = new double[] {
            60,
            seg1Dots[4].getX(),
            seg2Dots[4].getX(),
            seg3Dots[4].getX(),
            seg4Dots[4].getX()
        };

        pointY = new double[] {
            420,
            seg1Dots[4].getY(),
            seg2Dots[4].getY(),
            seg3Dots[4].getY(),
            seg4Dots[4].getY()
        };

        currentPoint = 0;
     
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
        if (star1 != null && star1.contains(e.getX(), e.getY())) {
            mainScreen.switchToFirstBattleScreen();
        }
    }

    private void moveForward() {
        if (currentPoint < pointX.length - 1) {
        	currentPoint++;

        	if (currentPoint == 1) {
        	    showLevelOneScreen();
        	    return;
        	}

            double x = pointX[currentPoint];
            double y = pointY[currentPoint];

            huemanImage.setLocation(x - 40, y + 30);

            if (stopDots[currentPoint] != null) {
                mainScreen.remove(stopDots[currentPoint]);
                stopDots[currentPoint] = null;
            }
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

    private GOval[] addPathSegment(double x1, double y1, double x2, double y2, int dots) {
        GOval[] segment = new GOval[dots];

        for (int i = 1; i <= dots; i++) {
            double t = (double) i / (dots + 1);
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t;

            GOval dot = new GOval(x, y, 18, 18);
            dot.setFilled(true);
            dot.setFillColor(new Color(140, 140, 140));
            dot.setColor(Color.DARK_GRAY);

            mainScreen.add(dot);
            segment[i - 1] = dot;
        }

        return segment;
    }

    private void removeDots(GOval[] dots) {
        if (dots == null) return;

        for (GOval dot : dots) {
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

        showLevelScreen = true;

        levelScreenBG = new GRect(0, 0,
            MainApplication.WINDOW_WIDTH,
            MainApplication.WINDOW_HEIGHT);
        levelScreenBG.setFilled(true);
        levelScreenBG.setFillColor(Color.WHITE);

        dividerLine = new GLine(
            MainApplication.WINDOW_WIDTH / 2.0, 40,
            MainApplication.WINDOW_WIDTH / 2.0, MainApplication.WINDOW_HEIGHT - 40
        );
        dividerLine.setColor(Color.GRAY);

        levelEnemyImage = new GImage("mint.png");
        levelEnemyImage.scale(0.7);

        double imgX = MainApplication.WINDOW_WIDTH * 0.4;
        double imgY = (MainApplication.WINDOW_HEIGHT - levelEnemyImage.getHeight()) / 2;
        levelEnemyImage.setLocation(imgX, imgY);

        levelTitle = new GLabel("Level 1");
        levelTitle.setFont("Arial-Bold-36");
        double titleX = 120;
        double titleY = 260;
        levelTitle.setLocation(titleX, titleY);

        startButton = new GRect(200, 60);
        startButton.setFilled(true);
        startButton.setFillColor(Color.LIGHT_GRAY);
        startButton.setColor(Color.DARK_GRAY);

        double btnX = 120;
        double btnY = 320;
        startButton.setLocation(btnX, btnY);

        startText = new GLabel("START");
        startText.setFont("Arial-Bold-20");
        double textX = btnX + (200 - startText.getWidth()) / 2;
        double textY = btnY + 38;
        startText.setLocation(textX, textY);

        mainScreen.add(levelScreenBG);
        mainScreen.add(dividerLine);
        mainScreen.add(levelEnemyImage);
        mainScreen.add(levelTitle);
        mainScreen.add(startButton);
        mainScreen.add(startText);
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
    

}