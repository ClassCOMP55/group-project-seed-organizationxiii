import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GPolygon;
import acm.graphics.GRect;

public class VictoryPane extends GraphicsPane {

    private GRect continueButton;
    private GLabel continueLabel;

    private GRect dialogueBox;
    private GRect dialogueGlow;
    private GLabel dialogueText;

    private GImage hyperionImage;
    private GPolygon hyperionStone;
    private GPolygon hyperionStoneShade;
    private GOval hyperionStoneShadow;

    private GLabel titleShadow;
    private GLabel titleLabel;

    private Timer animTimer;
    private int tick = 0;

    private static final int STAR_COUNT = 45;
    private GOval[] bgStars = new GOval[STAR_COUNT];
    private float[] starAlpha = new float[STAR_COUNT];
    private float[] starSpeed = new float[STAR_COUNT];

    private static final int ORB_COUNT = 16;
    private GOval[] orbs = new GOval[ORB_COUNT];
    private double[] orbX = new double[ORB_COUNT];
    private double[] orbY = new double[ORB_COUNT];
    private double[] orbVX = new double[ORB_COUNT];
    private double[] orbVY = new double[ORB_COUNT];
    private float[] orbAlpha = new float[ORB_COUNT];
    private double[] orbR = new double[ORB_COUNT];
    private Color[] orbColors = new Color[ORB_COUNT];

    private static final Color GOLD = new Color(220, 175, 60);
    private static final Color GOLD_DARK = new Color(140, 105, 25);
    private static final Color PANEL_FILL = new Color(18, 8, 28, 235);
    private static final Color PANEL_BORDER = new Color(180, 140, 50, 200);
    private static final Color TEXT_MAIN = new Color(235, 225, 200);

    private static final Color SKY_TOP = new Color(28, 10, 52);
    private static final Color SKY_MID = new Color(55, 18, 88);
    private static final Color SKY_HOR = new Color(105, 55, 155);

    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;

    private double scale;
    private double offsetX;
    private double offsetY;

    public VictoryPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        updateLayoutScale();

        drawBackground();
        drawFrame();
        drawStars();
        drawFloatingOrbs();
        drawTitle();
        drawHyperionStone();
        drawDialogueUI();
        drawVictorySparkles();

        tick = 0;
        animTimer = new Timer(33, e -> animate());
        animTimer.start();
    }

    private void updateLayoutScale() {
        double actualW = mainScreen.getWidth();
        double actualH = mainScreen.getHeight();

        if (actualW <= 0) actualW = MainApplication.WINDOW_WIDTH;
        if (actualH <= 0) actualH = MainApplication.WINDOW_HEIGHT;

        scale = Math.min(actualW / BASE_W, actualH / BASE_H);
        scale *= 0.90;

        double contentW = BASE_W * scale;
        double contentH = BASE_H * scale;

        offsetX = (actualW - contentW) / 2.0;
        offsetY = (actualH - contentH) / 2.0;
    }

    private double X(double baseX) {
        return offsetX + baseX * scale;
    }

    private double Y(double baseY) {
        return offsetY + baseY * scale;
    }

    private double S(double baseSize) {
        return baseSize * scale;
    }

    private double screenW() {
        double w = mainScreen.getWidth();
        return (w > 0) ? w : MainApplication.WINDOW_WIDTH;
    }

    private double screenH() {
        double h = mainScreen.getHeight();
        return (h > 0) ? h : MainApplication.WINDOW_HEIGHT;
    }

    private double frameInnerMargin() {
        return S(30);
    }

    private double sceneLeft() {
        return frameInnerMargin();
    }

    private double sceneTop() {
        return frameInnerMargin();
    }

    private double sceneRight() {
        return screenW() - frameInnerMargin();
    }

    private double sceneBottom() {
        return screenH() - frameInnerMargin();
    }

    private double sceneWidth() {
        return sceneRight() - sceneLeft();
    }

    private double sceneHeight() {
        return sceneBottom() - sceneTop();
    }

    private void drawBackground() {
        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();

        GRect skyTop = new GRect(left, top, width, height * 0.52);
        skyTop.setFilled(true);
        skyTop.setFillColor(SKY_TOP);
        skyTop.setColor(SKY_TOP);
        addObj(skyTop);

        GRect skyMid = new GRect(left, top + height * 0.26, width, height * 0.36);
        skyMid.setFilled(true);
        skyMid.setFillColor(SKY_MID);
        skyMid.setColor(SKY_MID);
        addObj(skyMid);

        GRect skyHor = new GRect(left, top + height * 0.54, width, height * 0.12);
        skyHor.setFilled(true);
        skyHor.setFillColor(SKY_HOR);
        skyHor.setColor(SKY_HOR);
        addObj(skyHor);

        GRect ground = new GRect(left, top + height * 0.66, width, height * 0.34);
        ground.setFilled(true);
        ground.setFillColor(new Color(24, 14, 36));
        ground.setColor(new Color(24, 14, 36));
        addObj(ground);
    }

    private void drawFrame() {
        double W = screenW();
        double H = screenH();

        double m1 = S(20);
        double m2 = S(30);

        GRect outer = new GRect(m1, m1, W - 2 * m1, H - 2 * m1);
        outer.setFilled(false);
        outer.setColor(GOLD);
        addObj(outer);

        GRect inner = new GRect(m2, m2, W - 2 * m2, H - 2 * m2);
        inner.setFilled(false);
        inner.setColor(new Color(90, 110, 200));
        addObj(inner);

        addCornerFrame(m2, m2, true, true);
        addCornerFrame(W - m2, m2, false, true);
        addCornerFrame(m2, H - m2, true, false);
        addCornerFrame(W - m2, H - m2, false, false);
    }

    private void addCornerFrame(double x, double y, boolean left, boolean top) {
        double longLen = S(30);
        double shortLen = S(15);
        double gap = S(7);

        GLine h1 = new GLine(x, y, x + (left ? longLen : -longLen), y);
        GLine v1 = new GLine(x, y, x, y + (top ? longLen : -longLen));
        GLine h2 = new GLine(
                x + (left ? gap : -gap),
                y + (top ? gap : -gap),
                x + (left ? shortLen : -shortLen),
                y + (top ? gap : -gap)
        );
        GLine v2 = new GLine(
                x + (left ? gap : -gap),
                y + (top ? gap : -gap),
                x + (left ? gap : -gap),
                y + (top ? shortLen : -shortLen)
        );

        h1.setColor(GOLD);
        v1.setColor(GOLD);
        h2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 150));
        v2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 150));

        addObj(h1);
        addObj(v1);
        addObj(h2);
        addObj(v2);
    }

    private void drawStars() {
        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();

        for (int i = 0; i < STAR_COUNT; i++) {
            double sx = left + Math.random() * width;
            double sy = top + Math.random() * height * 0.56;
            double sr = S(0.8 + Math.random() * 2.0);

            bgStars[i] = new GOval(sx - sr, sy - sr, sr * 2, sr * 2);
            bgStars[i].setFilled(true);

            starAlpha[i] = 0.3f + (float) Math.random() * 0.7f;
            starSpeed[i] = 0.01f + (float) Math.random() * 0.02f;

            Color c = new Color(255, 245, 220, Math.min(255, Math.max(0, (int) (starAlpha[i] * 255))));
            bgStars[i].setFillColor(c);
            bgStars[i].setColor(c);
            addObj(bgStars[i]);
        }
    }

    private void drawFloatingOrbs() {
        Color[] palette = {
                new Color(255, 120, 120),
                new Color(120, 180, 255),
                new Color(120, 255, 160),
                new Color(255, 230, 120),
                new Color(210, 140, 255),
                new Color(255, 255, 255)
        };

        for (int i = 0; i < ORB_COUNT; i++) {
            orbR[i] = S(4 + Math.random() * 10);
            orbX[i] = sceneLeft() + Math.random() * sceneWidth();
            orbY[i] = sceneTop() + Math.random() * sceneHeight() * 0.85;
            orbVX[i] = -0.4 + Math.random() * 0.8;
            orbVY[i] = -0.5 + Math.random() * 0.4;
            orbAlpha[i] = 0.2f + (float) Math.random() * 0.4f;
            orbColors[i] = palette[i % palette.length];

            orbs[i] = new GOval(orbX[i], orbY[i], orbR[i] * 2, orbR[i] * 2);
            orbs[i].setFilled(true);

            Color c = withAlpha(orbColors[i], orbAlpha[i]);
            orbs[i].setFillColor(c);
            orbs[i].setColor(c);
            addObj(orbs[i]);
        }
    }

    private void drawTitle() {
        titleShadow = new GLabel("YOU WIN");
        titleShadow.setFont(new Font("Times New Roman", Font.BOLD, (int) S(54)));
        titleShadow.setColor(new Color(0, 0, 0, 170));
        titleShadow.setLocation(X(700) + S(4), Y(90) + S(4));
        addObj(titleShadow);

        titleLabel = new GLabel("YOU WIN");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, (int) S(54)));
        titleLabel.setColor(new Color(255, 230, 150));
        titleLabel.setLocation(X(700), Y(90));
        addObj(titleLabel);

        GLabel subtitle = new GLabel("Palletia's colours have returned");
        subtitle.setFont(new Font("Times New Roman", Font.ITALIC, (int) S(22)));
        subtitle.setColor(new Color(230, 220, 255));
        subtitle.setLocation(X(690), Y(130));
        addObj(subtitle);
    }

    private void drawHyperionStone() {
        double stoneCX = X(330);
        double stoneTopY = Y(370);
        double stoneW = S(230);
        double stoneH = S(300);

        hyperionStoneShadow = new GOval(
                stoneCX - stoneW * 0.42,
                stoneTopY + stoneH * 0.78,
                stoneW * 0.95,
                S(42)
        );
        hyperionStoneShadow.setFilled(true);
        hyperionStoneShadow.setFillColor(new Color(0, 0, 0, 70));
        hyperionStoneShadow.setColor(new Color(0, 0, 0, 0));
        addObj(hyperionStoneShadow);

        hyperionStoneShade = new GPolygon();
        hyperionStoneShade.addVertex(stoneCX - stoneW * 0.46, stoneTopY + stoneH * 0.18);
        hyperionStoneShade.addVertex(stoneCX - stoneW * 0.18, stoneTopY + stoneH * 0.92);
        hyperionStoneShade.addVertex(stoneCX + stoneW * 0.24, stoneTopY + stoneH * 0.92);
        hyperionStoneShade.addVertex(stoneCX + stoneW * 0.48, stoneTopY + stoneH * 0.22);
        hyperionStoneShade.setFilled(true);
        hyperionStoneShade.setFillColor(new Color(44, 34, 58));
        hyperionStoneShade.setColor(new Color(60, 48, 76));
        addObj(hyperionStoneShade);

        hyperionStone = new GPolygon();
        hyperionStone.addVertex(stoneCX - stoneW * 0.40, stoneTopY);
        hyperionStone.addVertex(stoneCX - stoneW * 0.12, stoneTopY + stoneH * 0.78);
        hyperionStone.addVertex(stoneCX + stoneW * 0.16, stoneTopY + stoneH * 0.78);
        hyperionStone.addVertex(stoneCX + stoneW * 0.40, stoneTopY + stoneH * 0.06);
        hyperionStone.setFilled(true);
        hyperionStone.setFillColor(new Color(72, 60, 92));
        hyperionStone.setColor(new Color(96, 80, 120));
        addObj(hyperionStone);

        hyperionImage = new GImage("hyperion.png");
        hyperionImage.scale(0.72 * scale);

        double imgX = stoneCX - hyperionImage.getWidth() / 2.0 - S(25);
        double imgY = stoneTopY - hyperionImage.getHeight() + S(8);

        hyperionImage.setLocation(imgX, imgY);
        addObj(hyperionImage);

        GOval glow = new GOval(
                imgX - S(30),
                imgY - S(20),
                hyperionImage.getWidth() + S(60),
                hyperionImage.getHeight() + S(40)
        );
        glow.setFilled(true);
        glow.setFillColor(new Color(255, 245, 180, 25));
        glow.setColor(new Color(255, 245, 180, 25));
        addObj(glow);

        glow.sendToBack();
    }

    private void drawDialogueUI() {
        double boxX = X(565);
        double boxY = Y(305);
        double boxW = S(680);
        double boxH = S(290);

        dialogueGlow = new GRect(boxX - S(5), boxY - S(5), boxW + S(10), boxH + S(10));
        dialogueGlow.setFilled(true);
        dialogueGlow.setFillColor(new Color(255, 220, 120, 40));
        dialogueGlow.setColor(new Color(0, 0, 0, 0));
        addObj(dialogueGlow);

        dialogueBox = new GRect(boxX, boxY, boxW, boxH);
        dialogueBox.setFilled(true);
        dialogueBox.setFillColor(PANEL_FILL);
        dialogueBox.setColor(PANEL_BORDER);
        addObj(dialogueBox);

        GLabel nameLabel = new GLabel("HYPERION");
        nameLabel.setFont(new Font("Times New Roman", Font.BOLD, (int) S(24)));
        nameLabel.setColor(new Color(255, 220, 130));
        nameLabel.setLocation(boxX + S(22), boxY + S(38));
        addObj(nameLabel);

        GLine divider = new GLine(boxX + S(18), boxY + S(52), boxX + boxW - S(18), boxY + S(52));
        divider.setColor(new Color(170, 130, 60));
        addObj(divider);

        dialogueText = new GLabel("You have done it, brave one.");
        dialogueText.setFont(new Font("Times New Roman", Font.PLAIN, (int) S(23)));
        dialogueText.setColor(TEXT_MAIN);
        dialogueText.setLocation(boxX + S(26), boxY + S(95));
        addObj(dialogueText);

        GLabel line2 = new GLabel("Palletia is safe. The world has its colours back.");
        line2.setFont(new Font("Times New Roman", Font.PLAIN, (int) S(23)));
        line2.setColor(TEXT_MAIN);
        line2.setLocation(boxX + S(26), boxY + S(135));
        addObj(line2);

        GLabel line3 = new GLabel("You restored every last hue and saved us all.");
        line3.setFont(new Font("Times New Roman", Font.PLAIN, (int) S(23)));
        line3.setColor(TEXT_MAIN);
        line3.setLocation(boxX + S(26), boxY + S(175));
        addObj(line3);

        GLabel line4 = new GLabel("Hope has returned because of you.");
        line4.setFont(new Font("Times New Roman", Font.ITALIC, (int) S(23)));
        line4.setColor(new Color(255, 235, 180));
        line4.setLocation(boxX + S(26), boxY + S(215));
        addObj(line4);

        double buttonW = S(210);
        double buttonH = S(50);
        double buttonX = boxX + boxW - buttonW - S(24);
        double buttonY = boxY + boxH - buttonH - S(20);

        continueButton = new GRect(buttonX, buttonY, buttonW, buttonH);
        continueButton.setFilled(true);
        continueButton.setFillColor(new Color(92, 60, 140));
        continueButton.setColor(GOLD_DARK);
        addObj(continueButton);

        continueLabel = new GLabel("RETURN TO TITLE");
        continueLabel.setFont(new Font("Arial", Font.BOLD, (int) S(18)));
        continueLabel.setColor(new Color(255, 245, 220));
        continueLabel.setLocation(
                buttonX + buttonW / 2 - continueLabel.getWidth() / 2,
                buttonY + buttonH / 2 + S(6)
        );
        addObj(continueLabel);
    }

    private void drawVictorySparkles() {
        Color[] sparkColors = {
                new Color(255, 120, 120),
                new Color(120, 180, 255),
                new Color(120, 255, 160),
                new Color(255, 230, 120),
                new Color(210, 140, 255)
        };

        for (int i = 0; i < 14; i++) {
            double cx = X(250 + Math.random() * 980);
            double cy = Y(140 + Math.random() * 130);
            double size = S(8 + Math.random() * 12);

            GPolygon sparkle = createDiamond(cx, cy, size);
            sparkle.setFilled(true);
            sparkle.setFillColor(withAlpha(sparkColors[i % sparkColors.length], 0.35f));
            sparkle.setColor(withAlpha(new Color(255, 255, 255), 0.6f));
            addObj(sparkle);
        }
    }

    private GPolygon createDiamond(double cx, double cy, double r) {
        GPolygon diamond = new GPolygon();
        diamond.addVertex(cx, cy - r);
        diamond.addVertex(cx + r, cy);
        diamond.addVertex(cx, cy + r);
        diamond.addVertex(cx - r, cy);
        return diamond;
    }

    private void animate() {
        tick++;

        for (int i = 0; i < STAR_COUNT; i++) {
            starAlpha[i] += starSpeed[i];
            if (starAlpha[i] > 1.0f || starAlpha[i] < 0.2f) {
                starSpeed[i] *= -1;
                starAlpha[i] = Math.max(0.2f, Math.min(1.0f, starAlpha[i]));
            }

            Color c = new Color(255, 245, 220, Math.min(255, Math.max(0, (int) (starAlpha[i] * 255))));
            bgStars[i].setFillColor(c);
            bgStars[i].setColor(c);
        }

        for (int i = 0; i < ORB_COUNT; i++) {
            orbX[i] += orbVX[i];
            orbY[i] += orbVY[i] + Math.sin((tick + i * 8) * 0.05) * 0.25;

            if (orbX[i] < sceneLeft() - 40) orbX[i] = sceneRight() + 20;
            if (orbX[i] > sceneRight() + 20) orbX[i] = sceneLeft() - 40;
            if (orbY[i] < sceneTop() - 40) orbY[i] = sceneBottom() - 120;
            if (orbY[i] > sceneBottom()) orbY[i] = sceneTop() + 80;

            orbs[i].setLocation(orbX[i], orbY[i]);

            float a = (float) (0.18 + 0.12 * Math.sin((tick + i * 15) * 0.06));
            Color c = withAlpha(orbColors[i], a);
            orbs[i].setFillColor(c);
            orbs[i ].setColor(c);
        }

        if (hyperionImage != null) {
            double floatY = Math.sin(tick * 0.05) * S(3);
            double stoneCX = X(330);
            double stoneTopY = Y(370);
            double imgX = stoneCX - hyperionImage.getWidth() / 2.0 - S(25);
            double imgY = stoneTopY - hyperionImage.getHeight() + S(8) + floatY;
            hyperionImage.setLocation(imgX, imgY);
        }

        if (titleLabel != null) {
            float glow = (float) ((Math.sin(tick * 0.07) + 1) / 2.0);
            int r = (int) (235 + glow * 20);
            int g = (int) (210 + glow * 25);
            int b = (int) (120 + glow * 35);
            titleLabel.setColor(new Color(Math.min(r, 255), Math.min(g, 255), Math.min(b, 255)));
        }
    }

    private Color withAlpha(Color base, float alpha) {
        alpha = Math.max(0f, Math.min(1f, alpha));
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), (int) (alpha * 255));
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == continueButton || obj == continueLabel) {
            mainScreen.switchToTitleScreen();
        }
    }

    @Override
    public void hideContent() {
        if (animTimer != null) {
            animTimer.stop();
        }

        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}