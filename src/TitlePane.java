import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import acm.graphics.*;

public class TitlePane extends GraphicsPane {

    // ── UI objects ───────────────────────────────────────────────────────────
    private GLabel titleLabel;
    private GLabel titleShadow;
    private GLabel startLabel;
    private GLabel tagline;
    private GImage heroImage;

    // ── Title screen button ──────────────────────────────────────────────────
    private GRect startButton;
    private GRect startButtonGlow;
    private GRect startButtonInner;

    // ── Background layers ────────────────────────────────────────────────────
    private GRect bgBase;
    private GRect bgOverlay;

    // ── Particle system ──────────────────────────────────────────────────────
    private static final int PARTICLE_COUNT = 38;
    private GOval[] particles = new GOval[PARTICLE_COUNT];
    private double[] px = new double[PARTICLE_COUNT];
    private double[] py = new double[PARTICLE_COUNT];
    private double[] pvx = new double[PARTICLE_COUNT];
    private double[] pvy = new double[PARTICLE_COUNT];
    private float[] palpha = new float[PARTICLE_COUNT];
    private float[] palphaSpeed = new float[PARTICLE_COUNT];
    private double[] pradius = new double[PARTICLE_COUNT];
    private Color[] pcolor = new Color[PARTICLE_COUNT];

    // ── Decorative horizontal rule lines ────────────────────────────────────
    private GLine ruleLeft, ruleRight;
    private GLine ruleLeft2, ruleRight2;

    // ── Corner ornaments ─────────────────────────────────────────────────────
    private GPolygon[] diamonds = new GPolygon[4];

    // ── Animation state ──────────────────────────────────────────────────────
    private Timer animTimer;
    private int tick = 0;

    // ── Layout scaling like CutScene2Pane ───────────────────────────────────
    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;

    private double scale;
    private double offsetX;
    private double offsetY;

    // Title hue cycling
    private static final Color[] TITLE_COLORS = {
        new Color(255, 80, 80),
        new Color(255, 160, 40),
        new Color(255, 240, 60),
        new Color(80, 255, 120),
        new Color(60, 180, 255),
        new Color(180, 80, 255),
    };
    private int titleColorIdx = 0;
    private float titleColorT = 0f;

    // START button pulse
    private boolean startHovered = false;
    private float startPulse = 0f;
    private boolean pulseDir = true;

    // Palette
    private static final Color BG_DARK = new Color(10, 10, 18);
    private static final Color RULE_COLOR = new Color(120, 100, 200, 160);
    private static final Color DIAMOND_COL = new Color(200, 180, 255, 180);
    private static final Color TAGLINE_COL = new Color(160, 150, 210);
    private static final Color GOLD = new Color(220, 175, 60);

    public TitlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    
    
    @Override
    public void showContent() {
        updateLayoutScale();

        double W = screenW() + S(10);
        double H = screenH() + S(50);

        bgBase = new GRect(0, 0, W, H);
        bgBase.setFilled(true);
        bgBase.setFillColor(BG_DARK);
        bgBase.setColor(BG_DARK);
        addObj(bgBase);

        bgOverlay = new GRect(0, 0, W, H);
        bgOverlay.setFilled(true);
        bgOverlay.setFillColor(new Color(20, 10, 40, 60));
        bgOverlay.setColor(new Color(0, 0, 0, 0));
        addObj(bgOverlay);

        drawBattleFrame();

        Color[] particlePalette = {
            new Color(255, 80, 80),
            new Color(80, 160, 255),
            new Color(80, 255, 130),
            new Color(200, 100, 255),
            new Color(255, 210, 60),
            new Color(255, 255, 255),
        };

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            pradius[i] = 2 + Math.random() * 5;
            px[i] = Math.random() * W;
            py[i] = Math.random() * H;
            pvx[i] = (Math.random() - 0.5) * 0.6;
            pvy[i] = -0.2 - Math.random() * 0.5;
            palpha[i] = (float) Math.random();
            palphaSpeed[i] = 0.004f + (float) (Math.random() * 0.008);
            pcolor[i] = particlePalette[i % particlePalette.length];

            particles[i] = new GOval(px[i], py[i], pradius[i] * 2, pradius[i] * 2);
            particles[i].setFilled(true);
            particles[i].setFillColor(withAlpha(pcolor[i], palpha[i]));
            particles[i].setColor(withAlpha(pcolor[i], palpha[i]));
            addObj(particles[i]);
        }

        double left = sceneLeft();
        double top = sceneTop();
        double width = sceneWidth();
        double height = sceneHeight();

        double titleY = top + height * 0.10;
        double ruleY = titleY + 10;

        ruleLeft = new GLine(left + width * 0.10, ruleY, left + width * 0.28, ruleY);
        ruleRight = new GLine(left + width * 0.72, ruleY, left + width * 0.90, ruleY);
        ruleLeft.setColor(RULE_COLOR);
        ruleRight.setColor(RULE_COLOR);
        addObj(ruleLeft);
        addObj(ruleRight);

        ruleLeft2 = new GLine(left + width * 0.12, ruleY + 5, left + width * 0.26, ruleY + 5);
        ruleRight2 = new GLine(left + width * 0.74, ruleY + 5, left + width * 0.88, ruleY + 5);
        ruleLeft2.setColor(new Color(120, 100, 200, 60));
        ruleRight2.setColor(new Color(120, 100, 200, 60));
        addObj(ruleLeft2);
        addObj(ruleRight2);

        titleShadow = new GLabel("Every Last Hue");
        titleShadow.setFont("Times New Roman-Bold-64");
        titleShadow.setColor(new Color(0, 0, 0, 160));
        titleShadow.setLocation((W - titleShadow.getWidth()) / 2.0 + 4, titleY + 4);
        addObj(titleShadow);

        titleLabel = new GLabel("Every Last Hue");
        titleLabel.setFont("Times New Roman-Bold-64");
        titleLabel.setColor(TITLE_COLORS[0]);
        titleLabel.setLocation((W - titleLabel.getWidth()) / 2.0, titleY);
        addObj(titleLabel);

        tagline = new GLabel("A  world  drained  of  colour  awaits  its  champion");
        tagline.setFont("Times New Roman-Italic-18");
        tagline.setColor(TAGLINE_COL);
        tagline.setLocation((W - tagline.getWidth()) / 2.0, titleY + 30);
        addObj(tagline);

        double[][] corners = {
            {left + 18, top + 18},
            {left + width - 18, top + 18},
            {left + 18, top + height - 18},
            {left + width - 18, top + height - 18}
        };

        for (int i = 0; i < 4; i++) {
            diamonds[i] = makeDiamond(corners[i][0], corners[i][1], 14);
            diamonds[i].setFilled(true);
            diamonds[i].setFillColor(new Color(160, 140, 230, 80));
            diamonds[i].setColor(DIAMOND_COL);
            addObj(diamonds[i]);
        }

        heroImage = new GImage("combined_blended.png");
        heroImage.scale(0.56);

        double imgX = (W - heroImage.getWidth()) / 2.0;
        double imgY = top + height * 0.20;
        heroImage.setLocation(imgX, imgY);
        addObj(heroImage);

        double imgW = heroImage.getWidth();
        double imgH = heroImage.getHeight();

        GRect frameGlow = new GRect(imgX - 6, imgY - 6, imgW + 12, imgH + 12);
        frameGlow.setColor(new Color(160, 80, 255, 90));
        frameGlow.setFilled(false);
        addObj(frameGlow);

        GRect frameBorder = new GRect(imgX - 3, imgY - 3, imgW + 6, imgH + 6);
        frameBorder.setColor(new Color(200, 160, 255, 200));
        frameBorder.setFilled(false);
        addObj(frameBorder);

        GRect frameInner = new GRect(imgX + 4, imgY + 4, imgW - 8, imgH - 8);
        frameInner.setColor(new Color(255, 255, 255, 25));
        frameInner.setFilled(false);
        addObj(frameInner);

        double[][] imgCorners = {
            {imgX, imgY},
            {imgX + imgW, imgY},
            {imgX, imgY + imgH},
            {imgX + imgW, imgY + imgH}
        };

        for (double[] corner : imgCorners) {
            GPolygon d = makeDiamond(corner[0], corner[1], 8);
            d.setFilled(true);
            d.setFillColor(new Color(180, 120, 255, 180));
            d.setColor(new Color(220, 180, 255, 220));
            addObj(d);
        }

        double btnW = 240;
        double btnH = 58;
        double startX = (W - btnW) / 2.0;
        double startY = top + height * 0.82;

        startButtonGlow = new GRect(startX - 6, startY - 6, btnW + 12, btnH + 12);
        startButtonGlow.setFilled(true);
        startButtonGlow.setFillColor(new Color(140, 80, 255, 35));
        startButtonGlow.setColor(new Color(180, 120, 255, 80));
        addObj(startButtonGlow);

        startButton = new GRect(startX, startY, btnW, btnH);
        startButton.setFilled(true);
        startButton.setFillColor(new Color(30, 20, 55));
        startButton.setColor(new Color(200, 160, 255, 200));
        addObj(startButton);

        startButtonInner = new GRect(startX + 4, startY + 4, btnW - 8, btnH - 8);
        startButtonInner.setFilled(false);
        startButtonInner.setColor(new Color(255, 255, 255, 18));
        addObj(startButtonInner);

        double[][] btnCorners = {
            {startX, startY},
            {startX + btnW, startY},
            {startX, startY + btnH},
            {startX + btnW, startY + btnH}
        };

        for (double[] corner : btnCorners) {
            GPolygon d = makeDiamond(corner[0], corner[1], 7);
            d.setFilled(true);
            d.setFillColor(new Color(180, 120, 255, 180));
            d.setColor(new Color(220, 180, 255, 220));
            addObj(d);
        }

        startLabel = new GLabel("S T A R T");
        startLabel.setFont("Times New Roman-Bold-32");
        startLabel.setColor(new Color(220, 200, 255));
        double labelX = startX + (btnW - startLabel.getWidth()) / 2.0;
        double labelY = startY + (btnH + startLabel.getAscent()) / 2.0 - 4;
        startLabel.setLocation(labelX, labelY);
        addObj(startLabel);

        GLabel version = new GLabel("v1.0   ·   Every Last Hue   ·   2025");
        version.setFont("Arial-12");
        version.setColor(new Color(70, 65, 100));
        version.setLocation((W - version.getWidth()) / 2.0, sceneBottom() - 6);
        addObj(version);

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

    
    
    private double frameInnerMargin() {
        return S(8);
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

    private double screenW() {
        double w = mainScreen.getWidth();
        return (w > 0) ? w : MainApplication.WINDOW_WIDTH;
    }

    private double screenH() {
        double h = mainScreen.getHeight();
        return (h > 0) ? h : MainApplication.WINDOW_HEIGHT;
    }

    private void drawBattleFrame() {
    	double W = screenW() + S(10);
    	double H = screenH() + S(50);

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

    private void animate() {
        tick++;
        double W = screenW();
        double H = screenH();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            if (particles[i] == null) continue;

            px[i] += pvx[i];
            py[i] += pvy[i];
            pvx[i] += Math.sin(tick * 0.02 + i) * 0.01;

            palpha[i] += palphaSpeed[i];
            if (palpha[i] > 0.85f) {
                palpha[i] = 0.85f;
                palphaSpeed[i] = -Math.abs(palphaSpeed[i]);
            }
            if (palpha[i] < 0.05f) {
                palpha[i] = 0.05f;
                palphaSpeed[i] = Math.abs(palphaSpeed[i]);
            }

            if (py[i] < -10) {
                py[i] = H + 5;
                px[i] = Math.random() * W;
            }
            if (px[i] < -10) px[i] = W + 5;
            if (px[i] > W + 10) px[i] = -5;

            Color c = withAlpha(pcolor[i], palpha[i]);
            particles[i].setFillColor(c);
            particles[i].setColor(c);
            particles[i].setLocation(px[i], py[i]);
        }

        titleColorT += 0.012f;
        if (titleColorT >= 1f) {
            titleColorT -= 1f;
            titleColorIdx = (titleColorIdx + 1) % TITLE_COLORS.length;
        }

        int nextIdx = (titleColorIdx + 1) % TITLE_COLORS.length;
        Color c1 = TITLE_COLORS[titleColorIdx];
        Color c2 = TITLE_COLORS[nextIdx];
        float t = titleColorT;

        if (titleLabel != null) {
            titleLabel.setColor(new Color(
                lerp(c1.getRed(), c2.getRed(), t),
                lerp(c1.getGreen(), c2.getGreen(), t),
                lerp(c1.getBlue(), c2.getBlue(), t)
            ));
        }

        startPulse += pulseDir ? 0.04f : -0.04f;
        if (startPulse >= 1f) {
            startPulse = 1f;
            pulseDir = false;
        }
        if (startPulse <= 0f) {
            startPulse = 0f;
            pulseDir = true;
        }

        if (startLabel != null) {
            if (startHovered) {
                float hoverT = (float) ((Math.sin(tick * 0.08) + 1) / 2.0);
                int hoverIdx = (tick / 6) % TITLE_COLORS.length;
                int nextHoverIdx = (hoverIdx + 1) % TITLE_COLORS.length;

                Color hc1 = TITLE_COLORS[hoverIdx];
                Color hc2 = TITLE_COLORS[nextHoverIdx];

                startLabel.setColor(new Color(
                    lerp(hc1.getRed(), hc2.getRed(), hoverT),
                    lerp(hc1.getGreen(), hc2.getGreen(), hoverT),
                    lerp(hc1.getBlue(), hc2.getBlue(), hoverT)
                ));
            } else {
                int base = 200;
                int pulse = (int) (startPulse * 55);
                startLabel.setColor(new Color(base + pulse, base - 20 + pulse / 2, 255));
            }
        }

        if (startButton != null && !startHovered) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.06));
            startButton.setColor(new Color(200, 160, 255, (int) (120 + p * 100)));
        }

        if (startButtonGlow != null && !startHovered) {
            float p = (float) (0.5 + 0.5 * Math.sin(tick * 0.06));
            startButtonGlow.setFillColor(new Color(140, 80, 255, (int) (16 + p * 30)));
        }

        if (titleLabel != null) {
            double bob = Math.sin(tick * 0.04) * S(3);
            double titleY = sceneTop() + sceneHeight() * 0.10 + bob;
            double titleX = (W - titleLabel.getWidth()) / 2.0;
            titleLabel.setLocation(titleX, titleY);

            if (titleShadow != null) {
                titleShadow.setLocation(titleX + S(4), titleY + S(4));
            }

            if (tagline != null) {
                tagline.setLocation((W - tagline.getWidth()) / 2.0, titleY + S(30));
            }
        }

        for (int i = 0; i < 4; i++) {
            if (diamonds[i] == null) continue;
            float da = (float) (0.4 + 0.4 * Math.sin(tick * 0.03 + i * Math.PI / 2));
            diamonds[i].setFillColor(new Color(160, 140, 230, (int) (da * 80)));
            diamonds[i].setColor(new Color(200, 180, 255, (int) (da * 180)));
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

        titleLabel = null;
        titleShadow = null;
        startLabel = null;
        startButton = null;
        startButtonGlow = null;
        startButtonInner = null;
        heroImage = null;
        bgBase = null;
        bgOverlay = null;
        tagline = null;
        ruleLeft = null;
        ruleRight = null;
        ruleLeft2 = null;
        ruleRight2 = null;

        for (int i = 0; i < PARTICLE_COUNT; i++) particles[i] = null;
        for (int i = 0; i < 4; i++) diamonds[i] = null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (startButton == null || startLabel == null) return;

        boolean hovered = startButton.contains(e.getX(), e.getY()) || startLabel.contains(e.getX(), e.getY());
        startHovered = hovered;

        if (hovered) {
            startButton.setFillColor(new Color(50, 34, 75));
            startButton.setColor(new Color(240, 210, 255));
            if (startButtonGlow != null) {
                startButtonGlow.setFillColor(new Color(180, 120, 255, 45));
            }
        } else {
            startButton.setFillColor(new Color(30, 20, 55));
            startButton.setColor(new Color(200, 160, 255, 200));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (startButton != null &&
            (startButton.contains(e.getX(), e.getY()) || startLabel.contains(e.getX(), e.getY()))) {
            mainScreen.switchToColorSelectionScreen();
        }
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    private static Color withAlpha(Color c, float alpha) {
        return new Color(
            c.getRed(),
            c.getGreen(),
            c.getBlue(),
            Math.max(0, Math.min(255, (int) (alpha * 255)))
        );
    }

    private static int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    private static GPolygon makeDiamond(double cx, double cy, double r) {
        GPolygon d = new GPolygon();
        d.addVertex(0, -r);
        d.addVertex(r, 0);
        d.addVertex(0, r);
        d.addVertex(-r, 0);
        d.setLocation(cx, cy);
        return d;
    } //testt
    
    
    
}