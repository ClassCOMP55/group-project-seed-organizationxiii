import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import acm.graphics.*;

public class CutScene5Pane extends GraphicsPane {

	private final String[] dialogue = {
		    "You have done a great service to Palletia,",
		    "restoring the colors of the world. However, here",
		    "lies our main foe. Loathe cares not about fairness,",
		    "but merely of destruction and humiliation.",
		    "But don't take my word for it; vile words exit his",
		    "mouth like the air he breathes. The fate of Palletia",
		    "is in your hands now. Save the colors.",
		    "Go now. I will be watching."
		};

    private int dialogueIndex = 0;
    private boolean pressed = false;

    private GLabel dialogueText;
    private GRect continueButton;
    private GLabel continueLabel;
    private GRect backButton;
    private GLabel backLabel;
    private GRect dialogueBox;
    private GRect dialogueBoxGlow;
    private GOval[] progressDots;

    private GImage hyperionImage;
    private GPolygon hyperionStone;
    private GPolygon hyperionStoneShade;
    private GOval hyperionStoneShadow;

    private Timer animTimer;
    private int tick = 0;

    private static final int STAR_COUNT = 55;
    private GOval[] bgStars = new GOval[STAR_COUNT];
    private float[] starAlpha = new float[STAR_COUNT];
    private float[] starSpeed = new float[STAR_COUNT];

    private static final int DUST_COUNT = 18;
    private GOval[] dust = new GOval[DUST_COUNT];
    private double[] dustX = new double[DUST_COUNT];
    private double[] dustY = new double[DUST_COUNT];
    private double[] dustVX = new double[DUST_COUNT];
    private double[] dustVY = new double[DUST_COUNT];
    private float[] dustAlpha = new float[DUST_COUNT];
    private double[] dustR = new double[DUST_COUNT];

    private GOval planetAtmo;
    private GRect continueGlow;

    private boolean backHovered = false;
    private boolean continueHovered = false;

    private static final Color GOLD = new Color(220, 175, 60);
    private static final Color GOLD_DARK = new Color(140, 105, 25);
    private static final Color PANEL_FILL = new Color(18, 8, 28, 235);
    private static final Color PANEL_BORDER = new Color(180, 140, 50, 200);
    private static final Color TEXT_MAIN = new Color(235, 225, 200);
    private static final Color TEXT_DIM = new Color(160, 150, 130);

    private static final Color SKY_TOP = new Color(28, 10, 52);
    private static final Color SKY_MID = new Color(55, 18, 88);
    private static final Color SKY_HOR = new Color(80, 30, 110);

    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;

    private double scale;
    private double offsetX;
    private double offsetY;

    public CutScene5Pane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        updateLayoutScale();

        drawBackground();
        drawBattleFrame();
        drawStars();
        drawPlanets();
        drawTerrain();
        drawDust();
        drawHyperionStone();
        drawDialogueUI();

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

        GRect skyTop = new GRect(left, top, width, height * 0.55);
        skyTop.setFilled(true);
        skyTop.setFillColor(SKY_TOP);
        skyTop.setColor(SKY_TOP);
        addObj(skyTop);

        GRect skyMid = new GRect(left, top + height * 0.30, width, height * 0.35);
        skyMid.setFilled(true);
        skyMid.setFillColor(SKY_MID);
        skyMid.setColor(SKY_MID);
        addObj(skyMid);

        GRect skyHor = new GRect(left, top + height * 0.52, width, height * 0.10);
        skyHor.setFilled(true);
        skyHor.setFillColor(SKY_HOR);
        skyHor.setColor(SKY_HOR);
        addObj(skyHor);
    }

    private void drawBattleFrame() {
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
            double sy = top + Math.random() * height * 0.58;
            double sr = S(0.7 + Math.random() * 2.0);

            starAlpha[i] = (float) Math.random();
            starSpeed[i] = 0.004f + (float) (Math.random() * 0.012f);

            bgStars[i] = new GOval(sx - sr, sy - sr, sr * 2, sr * 2);
            bgStars[i].setFilled(true);
            Color sc = new Color(230, 210, 255, Math.max(0, Math.min(255, (int) (starAlpha[i] * 220))));
            bgStars[i].setFillColor(sc);
            bgStars[i].setColor(sc);
            addObj(bgStars[i]);
        }
    }

    private void drawPlanets() {
        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();

        double sm_r = S(85);
        double sm_x = left + width * 0.16;
        double sm_y = top + height * 0.14;

        GOval smAtmo = new GOval(sm_x - sm_r * 1.5, sm_y - sm_r * 1.5, sm_r * 3, sm_r * 3);
        smAtmo.setFilled(true);
        smAtmo.setFillColor(new Color(140, 60, 180, 30));
        smAtmo.setColor(new Color(0, 0, 0, 0));
        addObj(smAtmo);

        GOval smDisc = new GOval(sm_x - sm_r, sm_y - sm_r, sm_r * 2, sm_r * 2);
        smDisc.setFilled(true);
        smDisc.setFillColor(new Color(185, 140, 210));
        smDisc.setColor(new Color(160, 110, 190, 180));
        addObj(smDisc);

        double p_r = S(180);
        double p_x = left + width * 0.78;
        double p_y = top + height * 0.24;

        planetAtmo = new GOval(p_x - p_r * 1.35, p_y - p_r * 1.35, p_r * 2.7, p_r * 2.7);
        planetAtmo.setFilled(true);
        planetAtmo.setFillColor(new Color(200, 80, 160, 22));
        planetAtmo.setColor(new Color(0, 0, 0, 0));
        addObj(planetAtmo);

        GOval pGlow = new GOval(p_x - p_r * 1.12, p_y - p_r * 1.12, p_r * 2.24, p_r * 2.24);
        pGlow.setFilled(true);
        pGlow.setFillColor(new Color(210, 90, 160, 35));
        pGlow.setColor(new Color(0, 0, 0, 0));
        addObj(pGlow);

        GOval planet = new GOval(p_x - p_r, p_y - p_r, p_r * 2, p_r * 2);
        planet.setFilled(true);
        planet.setFillColor(new Color(210, 100, 160));
        planet.setColor(new Color(180, 70, 130, 200));
        addObj(planet);
    }

    private void drawTerrain() {
        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();
        double right = sceneRight();
        double bottom = sceneBottom();

        double groundY = top + height * 0.60;

        GRect farGround = new GRect(left, groundY, width, bottom - groundY);
        farGround.setFilled(true);
        farGround.setFillColor(new Color(45, 35, 60));
        farGround.setColor(new Color(45, 35, 60));
        addObj(farGround);

        GRect midGround = new GRect(left, groundY + S(60), width, bottom - (groundY + S(60)));
        midGround.setFilled(true);
        midGround.setFillColor(new Color(38, 28, 52));
        midGround.setColor(new Color(38, 28, 52));
        addObj(midGround);

        GRect nearGround = new GRect(left, groundY + S(140), width, bottom - (groundY + S(140)));
        nearGround.setFilled(true);
        nearGround.setFillColor(new Color(28, 20, 42));
        nearGround.setColor(new Color(28, 20, 42));
        addObj(nearGround);

        GLine horizGlow = new GLine(left, groundY, right, groundY);
        horizGlow.setColor(new Color(160, 80, 200, 80));
        addObj(horizGlow);

        GLine horizGlow2 = new GLine(left, groundY + 1, right, groundY + 1);
        horizGlow2.setColor(new Color(130, 60, 170, 50));
        addObj(horizGlow2);

        GPolygon rock1 = new GPolygon();
        rock1.addVertex(left, top + height * 0.73);
        rock1.addVertex(left + width * 0.18, top + height * 0.56);
        rock1.addVertex(left + width * 0.28, top + height * 0.64);
        rock1.addVertex(left + width * 0.35, top + height * 0.73);
        rock1.addVertex(left, top + height * 0.81);
        rock1.setFilled(true);
        rock1.setFillColor(new Color(52, 42, 65));
        rock1.setColor(new Color(70, 55, 88, 180));
        addObj(rock1);

        GPolygon rock2 = new GPolygon();
        rock2.addVertex(left + width * 0.30, top + height * 0.81);
        rock2.addVertex(left + width * 0.36, top + height * 0.58);
        rock2.addVertex(left + width * 0.40, top + height * 0.62);
        rock2.addVertex(left + width * 0.46, top + height * 0.81);
        rock2.setFilled(true);
        rock2.setFillColor(new Color(44, 34, 58));
        rock2.setColor(new Color(60, 48, 76, 160));
        addObj(rock2);

        GPolygon rock3 = new GPolygon();
        rock3.addVertex(left + width * 0.60, top + height * 0.81);
        rock3.addVertex(left + width * 0.68, top + height * 0.66);
        rock3.addVertex(left + width * 0.76, top + height * 0.60);
        rock3.addVertex(left + width * 0.84, top + height * 0.62);
        rock3.addVertex(left + width * 0.90, top + height * 0.68);
        rock3.addVertex(right, top + height * 0.66);
        rock3.addVertex(right, top + height * 0.81);
        rock3.setFilled(true);
        rock3.setFillColor(new Color(48, 38, 62));
        rock3.setColor(new Color(66, 52, 82, 160));
        addObj(rock3);

        GPolygon fore = new GPolygon();
        fore.addVertex(left, bottom);
        fore.addVertex(left, top + height * 0.88);
        fore.addVertex(left + width * 0.12, top + height * 0.84);
        fore.addVertex(left + width * 0.22, top + height * 0.90);
        fore.addVertex(left + width * 0.34, top + height * 0.85);
        fore.addVertex(left + width * 0.46, top + height * 0.91);
        fore.addVertex(left + width * 0.58, top + height * 0.86);
        fore.addVertex(left + width * 0.70, top + height * 0.92);
        fore.addVertex(left + width * 0.82, top + height * 0.86);
        fore.addVertex(left + width * 0.92, top + height * 0.91);
        fore.addVertex(right, top + height * 0.87);
        fore.addVertex(right, bottom);
        fore.setFilled(true);
        fore.setFillColor(new Color(18, 12, 28));
        fore.setColor(new Color(30, 20, 45, 160));
        addObj(fore);
    }

    private void drawHyperionStone() {
        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();

        double stoneCX = left + width * 0.50;
        double stoneTopY = top + height * 0.63;

        double stoneW = S(210);
        double stoneH = S(95);

        hyperionStoneShadow = new GOval(
            stoneCX - stoneW * 0.42,
            stoneTopY + stoneH * 0.82,
            stoneW * 0.84,
            stoneH * 0.26
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

        hyperionImage = new GImage("Hyperion.png");
        hyperionImage.scale(0.72);

        double imgX = stoneCX - hyperionImage.getWidth() / 2.0 - S(25);
        double imgY = stoneTopY - hyperionImage.getHeight() + S(22);

        hyperionImage.setLocation(imgX, imgY);
        addObj(hyperionImage);
    }

    private void drawDust() {
        double left = sceneLeft();
        double width = sceneWidth();

        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] = left + Math.random() * width;
            dustY[i] = sceneTop() + sceneHeight() * 0.58 + Math.random() * sceneHeight() * 0.20;
            dustVX[i] = (Math.random() - 0.5) * 0.4;
            dustVY[i] = -0.1 - Math.random() * 0.25;
            dustAlpha[i] = (float) (0.1 + Math.random() * 0.4);
            dustR[i] = S(1.5 + Math.random() * 3);

            dust[i] = new GOval(dustX[i] - dustR[i], dustY[i] - dustR[i], dustR[i] * 2, dustR[i] * 2);
            dust[i].setFilled(true);
            Color dc = new Color(200, 150, 230, Math.max(0, Math.min(255, (int) (dustAlpha[i] * 200))));
            dust[i].setFillColor(dc);
            dust[i].setColor(dc);
            addObj(dust[i]);
        }
    }

    private void drawDialogueUI() {
        double W = screenW();

        double boxH = S(150);
        double boxW = sceneWidth() * 0.75;
        double boxX = sceneLeft() + (sceneWidth() - boxW) / 2;
        double boxY = sceneBottom() - boxH - S(60);

        dialogueBoxGlow = new GRect(boxX - S(6), boxY - S(6), boxW + S(12), boxH + S(12));
        dialogueBoxGlow.setFilled(true);
        dialogueBoxGlow.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 14));
        dialogueBoxGlow.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 45));
        addObj(dialogueBoxGlow);

        dialogueBox = new GRect(boxX, boxY, boxW, boxH);
        dialogueBox.setFilled(true);
        dialogueBox.setFillColor(PANEL_FILL);
        dialogueBox.setColor(PANEL_BORDER);
        addObj(dialogueBox);

        GRect dbInner = new GRect(boxX + S(5), boxY + S(5), boxW - S(10), boxH - S(10));
        dbInner.setFilled(false);
        dbInner.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 28));
        addObj(dbInner);

        GRect topBar = new GRect(boxX, boxY, boxW, S(4));
        topBar.setFilled(true);
        topBar.setFillColor(GOLD);
        topBar.setColor(GOLD);
        addObj(topBar);

        double[][] boxCorners = {
            {boxX, boxY},
            {boxX + boxW, boxY},
            {boxX, boxY + boxH},
            {boxX + boxW, boxY + boxH}
        };

        for (double[] c : boxCorners) {
            GPolygon d = makeDiamond(c[0], c[1], S(9));
            d.setFilled(true);
            d.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 180));
            d.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 240));
            addObj(d);
        }

        double npW = S(170);
        double npH = S(34);
        double npX = boxX + S(20);
        double npY = boxY - npH + S(2);

        GRect np = new GRect(npX, npY, npW, npH);
        np.setFilled(true);
        np.setFillColor(new Color(30, 18, 8));
        np.setColor(PANEL_BORDER);
        addObj(np);

        GRect npBar = new GRect(npX, npY, S(4), npH);
        npBar.setFilled(true);
        npBar.setFillColor(GOLD_DARK);
        npBar.setColor(GOLD_DARK);
        addObj(npBar);

        GLabel nameLabel = new GLabel("Hyperion");
        nameLabel.setFont(new Font("Times New Roman", Font.BOLD, Math.max(12, (int) S(20))));
        nameLabel.setColor(GOLD);
        nameLabel.setLocation(npX + S(14), npY + S(23));
        addObj(nameLabel);

        GLabel quote = new GLabel("\u201C");
        quote.setFont(new Font("Times New Roman", Font.BOLD, Math.max(20, (int) S(48))));
        quote.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 55));
        quote.setLocation(boxX + S(10), boxY + S(68));
        addObj(quote);

        dialogueText = new GLabel(dialogue[0]);
        dialogueText.setFont(new Font("Times New Roman", Font.PLAIN, Math.max(12, (int) S(22))));
        dialogueText.setColor(TEXT_MAIN);
        dialogueText.setLocation(boxX + S(24), boxY + S(52));
        addObj(dialogueText);

        int total = dialogue.length;
        double dotSpacing = S(14);
        double dotsStartX = (W - (total - 1) * dotSpacing) / 2.0;
        double dotsY = boxY + boxH - S(22);

        progressDots = new GOval[total];
        for (int i = 0; i < total; i++) {
            GOval dot = new GOval(dotsStartX + i * dotSpacing - S(4), dotsY - S(4), S(8), S(8));
            dot.setFilled(true);
            dot.setFillColor(i == 0 ? GOLD : new Color(60, 55, 40));
            dot.setColor(i == 0 ? GOLD : new Color(90, 80, 55));
            addObj(dot);
            progressDots[i] = dot;
        }

        double btnH = S(40);
        double btnW = S(110);
        double backX = boxX + S(16);
        double backY = boxY + boxH - btnH - S(14);

        backButton = new GRect(backX, backY, btnW, btnH);
        backButton.setFilled(true);
        backButton.setFillColor(new Color(20, 14, 8));
        backButton.setColor(new Color(140, 105, 35, 180));
        addObj(backButton);

        GRect backBar = new GRect(backX, backY, S(3), btnH);
        backBar.setFilled(true);
        backBar.setFillColor(GOLD_DARK);
        backBar.setColor(GOLD_DARK);
        addObj(backBar);

        backLabel = new GLabel("◀  Back");
        backLabel.setFont(new Font("Arial", Font.BOLD, Math.max(10, (int) S(15))));
        backLabel.setColor(TEXT_DIM);
        backLabel.setLocation(backX + (btnW - backLabel.getWidth()) / 2.0 + S(2), backY + S(26));
        addObj(backLabel);

        double contW = S(200);
        double contX = boxX + boxW - contW - S(16);
        double contY = backY;

        continueGlow = new GRect(contX - S(4), contY - S(4), contW + S(8), btnH + S(8));
        continueGlow.setFilled(true);
        continueGlow.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 22));
        continueGlow.setColor(new Color(0, 0, 0, 0));
        addObj(continueGlow);

        continueButton = new GRect(contX, contY, contW, btnH);
        continueButton.setFilled(true);
        continueButton.setFillColor(new Color(30, 20, 8));
        continueButton.setColor(PANEL_BORDER);
        addObj(continueButton);

        continueLabel = new GLabel("Continue  ▶");
        continueLabel.setFont(new Font("Arial", Font.BOLD, Math.max(10, (int) S(15))));
        continueLabel.setColor(GOLD);
        continueLabel.setLocation(contX + (contW - continueLabel.getWidth()) / 2.0, contY + S(26));
        addObj(continueLabel);

        GLabel hint = new GLabel("Click anywhere to advance");
        hint.setFont(new Font("Arial", Font.PLAIN, Math.max(9, (int) S(12))));
        hint.setColor(new Color(100, 88, 65));
        hint.setLocation((W - hint.getWidth()) / 2.0, boxY + boxH - S(5));
        addObj(hint);
    }

    private void animate() {
        tick++;

        for (int i = 0; i < STAR_COUNT; i++) {
            if (bgStars[i] == null) continue;

            starAlpha[i] += starSpeed[i] * (i % 2 == 0 ? 1 : -1);
            if (starAlpha[i] > 1f) {
                starAlpha[i] = 1f;
                starSpeed[i] = -Math.abs(starSpeed[i]);
            }
            if (starAlpha[i] < 0f) {
                starAlpha[i] = 0f;
                starSpeed[i] = Math.abs(starSpeed[i]);
            }

            int a = Math.max(0, Math.min(255, (int) (starAlpha[i] * 220)));
            Color sc = new Color(230, 210, 255, a);
            bgStars[i].setFillColor(sc);
            bgStars[i].setColor(sc);
        }

        if (planetAtmo != null) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.025));
            planetAtmo.setFillColor(new Color(200, 80, 160, (int) (14 + p * 22)));
        }

        for (int i = 0; i < DUST_COUNT; i++) {
            if (dust[i] == null) continue;

            dustX[i] += dustVX[i] + Math.sin(tick * 0.03 + i) * 0.02;
            dustY[i] += dustVY[i];

            double left = sceneLeft();
            double right = sceneRight();
            double top = sceneTop();

            if (dustY[i] < top + sceneHeight() * 0.40) {
                dustY[i] = top + sceneHeight() * 0.72;
                dustX[i] = left + Math.random() * sceneWidth();
            }
            if (dustX[i] < left) dustX[i] = right;
            if (dustX[i] > right) dustX[i] = left;

            dust[i].setLocation(dustX[i] - dustR[i], dustY[i] - dustR[i]);

            Color dc = new Color(200, 150, 230, Math.max(0, Math.min(255, (int) (dustAlpha[i] * 180))));
            dust[i].setFillColor(dc);
            dust[i].setColor(dc);
        }

        if (dialogueBoxGlow != null) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.04));
            dialogueBoxGlow.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), (int) (10 + p * 18)));
            dialogueBoxGlow.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), (int) (35 + p * 55)));
        }

        if (continueGlow != null && !continueHovered) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.06));
            continueGlow.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), (int) (10 + p * 20)));
        }

        if (continueButton != null && !continueHovered) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.06));
            continueButton.setColor(new Color(
                PANEL_BORDER.getRed(),
                PANEL_BORDER.getGreen(),
                PANEL_BORDER.getBlue(),
                (int) (110 + p * 145)
            ));
        }

        if (progressDots != null && dialogueIndex < progressDots.length) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.08));
            progressDots[dialogueIndex].setFillColor(
                new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), (int) (160 + p * 95))
            );
        }
    }

    private void advanceDialogue() {
        if (progressDots != null && dialogueIndex < progressDots.length) {
            progressDots[dialogueIndex].setFillColor(new Color(60, 55, 40));
            progressDots[dialogueIndex].setColor(new Color(90, 80, 55));
        }

        dialogueIndex++;

        if (dialogueIndex < dialogue.length) {
            dialogueText.setLabel(dialogue[dialogueIndex]);
            if (progressDots != null && dialogueIndex < progressDots.length) {
                progressDots[dialogueIndex].setFillColor(GOLD);
                progressDots[dialogueIndex].setColor(GOLD);
            }
        } else {
            mainScreen.switchToSeventhBattleScreen();
        }
    }

    @Override
    public void hideContent() {
        if (animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }

        for (GObject item : contents) {
            mainScreen.remove(item);
        }
        contents.clear();

        dialogueText = null;
        continueButton = null;
        continueLabel = null;
        continueGlow = null;
        backButton = null;
        backLabel = null;
        dialogueBox = null;
        dialogueBoxGlow = null;
        progressDots = null;
        planetAtmo = null;

        for (int i = 0; i < STAR_COUNT; i++) bgStars[i] = null;
        for (int i = 0; i < DUST_COUNT; i++) dust[i] = null;

        dialogueIndex = 0;
        pressed = false;
        backHovered = false;
        continueHovered = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (pressed) return;
        pressed = true;

        double x = e.getX();
        double y = e.getY();

        if (continueButton != null && (continueButton.contains(x, y) || continueLabel.contains(x, y))) {
            mainScreen.switchToSeventhBattleScreen();
            return;
        }

        if (backButton != null && (backButton.contains(x, y) || backLabel.contains(x, y))) {
            mainScreen.switchToLevelSelectScreen();
            return;
        }
        
        advanceDialogue();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        boolean nb = backButton != null && (backButton.contains(x, y) || backLabel.contains(x, y));
        if (nb && !backHovered) {
            backHovered = true;
            backButton.setColor(GOLD);
            backLabel.setColor(GOLD);
        } else if (!nb && backHovered) {
            backHovered = false;
            backButton.setColor(new Color(140, 105, 35, 180));
            backLabel.setColor(TEXT_DIM);
        }

        boolean nc = continueButton != null && (continueButton.contains(x, y) || continueLabel.contains(x, y));
        if (nc && !continueHovered) {
            continueHovered = true;
            continueButton.setFillColor(new Color(50, 34, 10));
            continueButton.setColor(GOLD);
            continueLabel.setColor(new Color(255, 240, 140));
            if (continueGlow != null) {
                continueGlow.setFillColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 35));
            }
        } else if (!nc && continueHovered) {
            continueHovered = false;
            continueButton.setFillColor(new Color(30, 20, 8));
            continueButton.setColor(PANEL_BORDER);
            continueLabel.setColor(GOLD);
        }
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    private static GPolygon makeDiamond(double cx, double cy, double r) {
        GPolygon d = new GPolygon();
        d.addVertex(0, -r);
        d.addVertex(r, 0);
        d.addVertex(0, r);
        d.addVertex(-r, 0);
        d.setLocation(cx, cy);
        return d;
    }
}