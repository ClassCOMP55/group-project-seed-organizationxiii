import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import acm.graphics.*;

public class TitlePane extends GraphicsPane {

    // ── UI objects ───────────────────────────────────────────────────────────
    private GLabel  titleLabel;
    private GLabel  titleShadow;
    private GLabel  startLabel;
    private GImage  heroImage;

    // ── Background layers ────────────────────────────────────────────────────
    private GRect   bgBase;
    private GRect   bgOverlay;

    // ── Particle system ──────────────────────────────────────────────────────
    private static final int PARTICLE_COUNT = 38;
    private GOval[]   particles     = new GOval[PARTICLE_COUNT];
    private double[]  px            = new double[PARTICLE_COUNT];
    private double[]  py            = new double[PARTICLE_COUNT];
    private double[]  pvx           = new double[PARTICLE_COUNT];
    private double[]  pvy           = new double[PARTICLE_COUNT];
    private float[]   palpha        = new float[PARTICLE_COUNT];
    private float[]   palphaSpeed   = new float[PARTICLE_COUNT];
    private double[]  pradius       = new double[PARTICLE_COUNT];
    private Color[]   pcolor        = new Color[PARTICLE_COUNT];

    // ── Decorative horizontal rule lines ────────────────────────────────────
    private GLine   ruleLeft, ruleRight;
    private GLine   ruleLeft2, ruleRight2;

    // ── Subtitle / tagline ───────────────────────────────────────────────────
    private GLabel  tagline;

    // ── Corner ornaments ─────────────────────────────────────────────────────
    private GPolygon[] diamonds = new GPolygon[4];

    // ── Animation state ──────────────────────────────────────────────────────
    private Timer   animTimer;
    private int     tick = 0;

    // Title hue cycling
    private static final Color[] TITLE_COLORS = {
        new Color(255,  80,  80),
        new Color(255, 160,  40),
        new Color(255, 240,  60),
        new Color( 80, 255, 120),
        new Color( 60, 180, 255),
        new Color(180,  80, 255),
    };
    private int   titleColorIdx = 0;
    private float titleColorT   = 0f;

    // START button pulse
    private boolean startHovered = false;
    private float   startPulse   = 0f;
    private boolean pulseDir     = true;

    // Palette
    private static final Color BG_DARK     = new Color(10,  10,  18);
    private static final Color RULE_COLOR  = new Color(120, 100, 200, 160);
    private static final Color DIAMOND_COL = new Color(200, 180, 255, 180);
    private static final Color TAGLINE_COL = new Color(160, 150, 210);

    // ─────────────────────────────────────────────────────────────────────────

    public TitlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  showContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void showContent() {
        double W = mainScreen.getWidth();
        double H = mainScreen.getHeight();

        // ── 1. Background ────────────────────────────────────────────────────
        bgBase = new GRect(0, 0, W, H);
        bgBase.setFilled(true);
        bgBase.setFillColor(BG_DARK);
        bgBase.setColor(BG_DARK);
        add(bgBase);

        bgOverlay = new GRect(0, 0, W, H);
        bgOverlay.setFilled(true);
        bgOverlay.setFillColor(new Color(20, 10, 40, 60));
        bgOverlay.setColor(new Color(0, 0, 0, 0));
        add(bgOverlay);

        // ── 2. Particles ─────────────────────────────────────────────────────
        Color[] particlePalette = {
            new Color(255,  80,  80),
            new Color( 80, 160, 255),
            new Color( 80, 255, 130),
            new Color(200, 100, 255),
            new Color(255, 210,  60),
            new Color(255, 255, 255),
        };
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            pradius[i]     = 2 + Math.random() * 5;
            px[i]          = Math.random() * W;
            py[i]          = Math.random() * H;
            pvx[i]         = (Math.random() - 0.5) * 0.6;
            pvy[i]         = -0.2 - Math.random() * 0.5;
            palpha[i]      = (float) Math.random();
            palphaSpeed[i] = 0.004f + (float)(Math.random() * 0.008);
            pcolor[i]      = particlePalette[i % particlePalette.length];

            particles[i] = new GOval(px[i], py[i], pradius[i]*2, pradius[i]*2);
            particles[i].setFilled(true);
            particles[i].setFillColor(withAlpha(pcolor[i], palpha[i]));
            particles[i].setColor(withAlpha(pcolor[i], palpha[i]));
            add(particles[i]);
        }

        // ── 3. Hero image ─────────────────────────────────────────────────────
        heroImage = new GImage("combined_blended.png");
        heroImage.scale(0.52);
        double imgX = (W - heroImage.getWidth()) / 2.0;
        double imgY = H * 0.18;
        heroImage.setLocation(imgX, imgY);
        add(heroImage);
        
     // ── Decorative frame around hero image ──────────────────────────────────
       
        double imgW = heroImage.getWidth();
        double imgH = heroImage.getHeight();

        // Outer glow rect (slightly larger, coloured)
        GRect frameGlow = new GRect(imgX - 6, imgY - 6, imgW + 12, imgH + 12);
        frameGlow.setColor(new Color(160, 80, 255, 90));
        frameGlow.setFilled(false);
        add(frameGlow);

        // Main border — thin, bright
        GRect frameBorder = new GRect(imgX - 3, imgY - 3, imgW + 6, imgH + 6);
        frameBorder.setColor(new Color(200, 160, 255, 200));
        frameBorder.setFilled(false);
        add(frameBorder);

        // Inner accent line — very subtle inset
        GRect frameInner = new GRect(imgX + 4, imgY + 4, imgW - 8, imgH - 8);
        frameInner.setColor(new Color(255, 255, 255, 25));
        frameInner.setFilled(false);
        add(frameInner);

        // Corner accent diamonds — one at each corner of the image
        double[][] imgCorners = {
            {imgX,        imgY},
            {imgX + imgW, imgY},
            {imgX,        imgY + imgH},
            {imgX + imgW, imgY + imgH},
        };
        for (double[] corner : imgCorners) {
            GPolygon cd = makeDiamond(corner[0], corner[1], 8);
            cd.setFilled(true);
            cd.setFillColor(new Color(180, 120, 255, 180));
            cd.setColor(new Color(220, 180, 255, 220));
            add(cd);
        }
        
        

        // ── 4. Decorative rules around the title area ─────────────────────────
        double titleY = H * 0.10;
        double ruleY  = titleY + 10;
        ruleLeft  = new GLine(W * 0.08, ruleY, W * 0.28, ruleY);
        ruleRight = new GLine(W * 0.72, ruleY, W * 0.92, ruleY);
        ruleLeft.setColor(RULE_COLOR);
        ruleRight.setColor(RULE_COLOR);
        add(ruleLeft);
        add(ruleRight);

        ruleLeft2  = new GLine(W * 0.10, ruleY + 5, W * 0.26, ruleY + 5);
        ruleRight2 = new GLine(W * 0.74, ruleY + 5, W * 0.90, ruleY + 5);
        ruleLeft2.setColor(new Color(120, 100, 200, 60));
        ruleRight2.setColor(new Color(120, 100, 200, 60));
        add(ruleLeft2);
        add(ruleRight2);

        // ── 5. Title shadow ───────────────────────────────────────────────────
        titleShadow = new GLabel("Every Last Hue");
        titleShadow.setFont("Times New Roman-Bold-64");
        titleShadow.setColor(new Color(0, 0, 0, 160));
        titleShadow.setLocation((W - titleShadow.getWidth()) / 2.0 + 4, titleY + 4);
        add(titleShadow);

        // ── 6. Title label ────────────────────────────────────────────────────
        titleLabel = new GLabel("Every Last Hue");
        titleLabel.setFont("Times New Roman-Bold-64");
        titleLabel.setColor(TITLE_COLORS[0]);
        titleLabel.setLocation((W - titleLabel.getWidth()) / 2.0, titleY);
        add(titleLabel);

        // ── 7. Tagline ────────────────────────────────────────────────────────
        tagline = new GLabel("A  world  drained  of  colour  awaits  its  champion");
        tagline.setFont("Times New Roman-Italic-18");
        tagline.setColor(TAGLINE_COL);
        tagline.setLocation((W - tagline.getWidth()) / 2.0, titleY + 30);
        add(tagline);

        // ── 8. Corner diamond ornaments ───────────────────────────────────────
        double[][] corners = {
            {W * 0.06, H * 0.06},
            {W * 0.94, H * 0.06},
            {W * 0.06, H * 0.94},
            {W * 0.94, H * 0.94},
        };
        for (int i = 0; i < 4; i++) {
            diamonds[i] = makeDiamond(corners[i][0], corners[i][1], 14);
            diamonds[i].setFilled(true);
            diamonds[i].setFillColor(new Color(160, 140, 230, 80));
            diamonds[i].setColor(DIAMOND_COL);
            add(diamonds[i]);
        }

     // ── START button frame + label ────────────────────────────────────────────
        double btnW   = 260;
        double btnH   = 62;
        double startX = (W - btnW) / 2.0;
        double startY = H * 0.82;

        // Outer glow frame
        GRect btnGlow = new GRect(startX - 6, startY - 6, btnW + 12, btnH + 12);
        btnGlow.setFilled(true);
        btnGlow.setFillColor(new Color(140, 80, 255, 35));
        btnGlow.setColor(new Color(180, 120, 255, 80));
        add(btnGlow);

        // Main button frame
        GRect btnFrame = new GRect(startX, startY, btnW, btnH);
        btnFrame.setFilled(true);
        btnFrame.setFillColor(new Color(30, 20, 55));
        btnFrame.setColor(new Color(200, 160, 255, 200));
        add(btnFrame);

        // Inner inset line
        GRect btnInner = new GRect(startX + 4, startY + 4, btnW - 8, btnH - 8);
        btnInner.setFilled(false);
        btnInner.setColor(new Color(255, 255, 255, 18));
        add(btnInner);

        // Corner diamonds on the button frame
        double[][] btnCorners = {
            {startX,        startY},
            {startX + btnW, startY},
            {startX,        startY + btnH},
            {startX + btnW, startY + btnH},
        };
        for (double[] corner : btnCorners) {
            GPolygon cd = makeDiamond(corner[0], corner[1], 7);
            cd.setFilled(true);
            cd.setFillColor(new Color(180, 120, 255, 180));
            cd.setColor(new Color(220, 180, 255, 220));
            add(cd);
        }

        // The label itself — centred inside the frame
        startLabel = new GLabel("S T A R T");
        startLabel.setFont("Times New Roman-Bold-32");
        startLabel.setColor(new Color(220, 200, 255));
        double labelX = startX + (btnW - startLabel.getWidth())  / 2.0;
        double labelY = startY + (btnH + startLabel.getAscent()) / 2.0 - 4;
        startLabel.setLocation(labelX, labelY);
        add(startLabel);

        // ── 10. Version strip ─────────────────────────────────────────────────
        GLabel version = new GLabel("v1.0   ·   Every Last Hue   ·   2025");
        version.setFont("Arial-12");
        version.setColor(new Color(70, 65, 100));
        version.setLocation((W - version.getWidth()) / 2.0, H - 16);
        add(version);

        // ── 11. Start animation ───────────────────────────────────────────────
        tick = 0;
        animTimer = new Timer(33, e -> animate());
        animTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Animation loop  (~30 fps via Swing Timer)
    // ════════════════════════════════════════════════════════════════════════
    private void animate() {
        tick++;
        double W = mainScreen.getWidth();
        double H = mainScreen.getHeight();

        // ── Particles ────────────────────────────────────────────────────────
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            if (particles[i] == null) continue;
            px[i] += pvx[i];
            py[i] += pvy[i];
            pvx[i] += Math.sin(tick * 0.02 + i) * 0.01;

            palpha[i] += palphaSpeed[i];
            if (palpha[i] > 0.85f) { palpha[i] = 0.85f; palphaSpeed[i] = -Math.abs(palphaSpeed[i]); }
            if (palpha[i] < 0.05f) { palpha[i] = 0.05f; palphaSpeed[i] =  Math.abs(palphaSpeed[i]); }

            if (py[i] < -10)    { py[i] = H + 5;  px[i] = Math.random() * W; }
            if (px[i] < -10)    { px[i] = W + 5; }
            if (px[i] > W + 10) { px[i] = -5; }

            Color c = withAlpha(pcolor[i], palpha[i]);
            particles[i].setFillColor(c);
            particles[i].setColor(c);
            particles[i].setLocation(px[i], py[i]);
        }

        // ── Title colour cycle ────────────────────────────────────────────────
        titleColorT += 0.012f;
        if (titleColorT >= 1f) {
            titleColorT -= 1f;
            titleColorIdx = (titleColorIdx + 1) % TITLE_COLORS.length;
        }
        int nextIdx = (titleColorIdx + 1) % TITLE_COLORS.length;
        Color c1 = TITLE_COLORS[titleColorIdx];
        Color c2 = TITLE_COLORS[nextIdx];
        float t  = titleColorT;
        if (titleLabel != null)
            titleLabel.setColor(new Color(lerp(c1.getRed(), c2.getRed(), t),
                                          lerp(c1.getGreen(), c2.getGreen(), t),
                                          lerp(c1.getBlue(), c2.getBlue(), t)));

        // ── START pulse ───────────────────────────────────────────────────────
        startPulse += pulseDir ? 0.04f : -0.04f;
        if (startPulse >= 1f) { startPulse = 1f; pulseDir = false; }
        if (startPulse <= 0f) { startPulse = 0f; pulseDir = true;  }

        if (startLabel != null) {
            if (startHovered) {
                startLabel.setColor(new Color(255, 240, 120));
            } else {
                int base  = 200;
                int pulse = (int)(startPulse * 55);
                startLabel.setColor(new Color(base + pulse, base - 20 + pulse / 2, 255));
            }
        }

        // ── Title vertical bob ────────────────────────────────────────────────
        if (titleLabel != null) {
            double bob    = Math.sin(tick * 0.04) * 3;
            double titleY = H * 0.10 + bob;
            double titleX = (W - titleLabel.getWidth()) / 2.0;
            titleLabel.setLocation(titleX, titleY);
            if (titleShadow != null) titleShadow.setLocation(titleX + 4, titleY + 4);
            if (tagline     != null) tagline.setLocation((W - tagline.getWidth()) / 2.0, titleY + 30);
        }

        // ── Diamonds pulse ────────────────────────────────────────────────────
        for (int i = 0; i < 4; i++) {
            if (diamonds[i] == null) continue;
            float da = (float)(0.4 + 0.4 * Math.sin(tick * 0.03 + i * Math.PI / 2));
            diamonds[i].setFillColor(new Color(160, 140, 230, (int)(da * 80)));
            diamonds[i].setColor(new Color(200, 180, 255, (int)(da * 180)));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  hideContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void hideContent() {
        if (animTimer != null) { animTimer.stop(); animTimer = null; }
        for (GObject item : contents) mainScreen.remove(item);
        contents.clear();
        titleLabel = null; titleShadow = null; startLabel = null;
        heroImage  = null; bgBase = null; bgOverlay = null; tagline = null;
        ruleLeft   = null; ruleRight = null; ruleLeft2 = null; ruleRight2 = null;
        for (int i = 0; i < PARTICLE_COUNT; i++) particles[i] = null;
        for (int i = 0; i < 4; i++)             diamonds[i]   = null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Mouse events
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void mouseMoved(MouseEvent e) {
        if (startLabel == null) return;
        startHovered = startLabel.contains(e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (startLabel != null && startLabel.contains(e.getX(), e.getY()))
            mainScreen.switchToColorSelectionScreen();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════════
    private void add(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    private static Color withAlpha(Color c, float alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(),
                         Math.max(0, Math.min(255, (int)(alpha * 255))));
    }

    private static int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }

    private static GPolygon makeDiamond(double cx, double cy, double r) {
        GPolygon d = new GPolygon();
        d.addVertex( 0, -r);
        d.addVertex( r,  0);
        d.addVertex( 0,  r);
        d.addVertex(-r,  0);
        d.setLocation(cx, cy);
        return d;
    }
}
