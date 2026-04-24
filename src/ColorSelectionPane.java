import acm.graphics.*;

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.Timer;

import characters.ColorType;
import characters.Hueman;

public class ColorSelectionPane extends GraphicsPane {

    // ── Sprites ───────────────────────────────────────────────────────────────
    private GImage red;
    private GImage blue;
    private GImage green;
    private GImage hoveredImage = null;

    // ── Labels ────────────────────────────────────────────────────────────────
    private GLabel titleLabel;
    private GLabel titleShadow;
    private GLabel redLabel;
    private GLabel blueLabel;
    private GLabel greenLabel;

    // ── Per-card glow rects (animated) ────────────────────────────────────────
    private GRect  redGlow;
    private GRect  blueGlow;
    private GRect  greenGlow;

    // ── Card frames ───────────────────────────────────────────────────────────
    private GRect  redFrame,   redInner;
    private GRect  blueFrame,  blueInner;
    private GRect  greenFrame, greenInner;
    private GRect[] selectButtons = new GRect[3];
    private GLabel[] selectTexts = new GLabel[3];
    
 // ── Animated brick grid ───────────────────────────────────────────────────
    private GRect[][] brickGrid;
    private int       brickCols;
    private int       brickRows;
    private float[][] brickPhase;   // each brick's phase offset in the wave
    

    // ── Card colours ─────────────────────────────────────────────────────────
    private static final Color C_RED   = new Color(255,  70,  70);
    private static final Color C_BLUE  = new Color( 70, 150, 255);
    private static final Color C_GREEN = new Color( 60, 220,  90);
    private static final Color PURPLE  = new Color(180, 120, 255);
    private static final Color PURPLE_DIM = new Color(120,  80, 200, 120);
    private static final Color BG_DARK = new Color( 10,  10,  18);

    // ── Brick dimensions ─────────────────────────────────────────────────────
    private static final int BRICK_W = 72;
    private static final int BRICK_H = 28;
    private static final int BRICK_GAP = 3;

    // ── Hover scale ───────────────────────────────────────────────────────────
    private static final double HOVER_SCALE = 1.10;

    // ── Animation ────────────────────────────────────────────────────────────
    private Timer animTimer;
    private int   tick = 0;

    // per-card pulse phases offset from each other
    private static final float[] PHASE = {0f, (float)(Math.PI * 2/3), (float)(Math.PI * 4/3)};

    // card bounds (set during layout, used in animate())
    private double[] cardX = new double[3];
    private double[] cardY = new double[3];
    private double   cardW, cardH;

    public ColorSelectionPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  showContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void showContent() {
        mainScreen.getGCanvas().setBackground(BG_DARK);

       double W = mainScreen.getWidth();
       double H = mainScreen.getHeight();

        // ── 1. Brick background ───────────────────────────────────────────────
       drawBrickBackground((int) W, (int) H);

        // ── 2. Title ──────────────────────────────────────────────────────────
        titleShadow = new GLabel("Choose Your Champion");
        titleShadow.setFont("Times New Roman-Bold-52");
        titleShadow.setColor(new Color(0, 0, 0, 180));
        titleShadow.setLocation((W - titleShadow.getWidth()) / 2.0 + 4, H * 0.14 + 4);
        add(titleShadow);

        titleLabel = new GLabel("Choose Your Champion");
        titleLabel.setFont("Times New Roman-Bold-52");
        titleLabel.setColor(PURPLE);
        titleLabel.setLocation((W - titleLabel.getWidth()) / 2.0, H * 0.14);
        add(titleLabel);

        // Decorative rules either side of title
        double ruleY  = H * 0.14 - titleLabel.getAscent() / 2.0 - 4;
        double ruleX1 = (W - titleLabel.getWidth()) / 2.0 - 20;
        double ruleX2 = ruleX1 + titleLabel.getWidth() + 40;
        GLine rl = new GLine(W * 0.06, ruleY, ruleX1, ruleY);
        GLine rr = new GLine(ruleX2, ruleY, W * 0.94, ruleY);
        rl.setColor(PURPLE_DIM); rr.setColor(PURPLE_DIM);
        add(rl); add(rr);

        GLabel sub = new GLabel("Your hue defines your power");
        sub.setFont("Times New Roman-Italic-20");
        sub.setColor(new Color(160, 140, 210));
        sub.setLocation((W - sub.getWidth()) / 2.0, H * 0.14 + 28);
        add(sub);

        // ── 3. Cards ──────────────────────────────────────────────────────────
        cardW = W * 0.22;
        cardH = H * 0.52;
        double cardYPos = H * 0.26;
        double gap      = (W - 3 * cardW) / 4.0;

        cardX[0] = gap;
        cardX[1] = gap * 2 + cardW;
        cardX[2] = gap * 3 + cardW * 2;
        cardY[0] = cardY[1] = cardY[2] = cardYPos;

        Color[] cardColors  = {C_BLUE,  C_RED,   C_GREEN};
        String[] cardNames  = {"BLUE",  "RED",   "GREEN"};
        String[] cardDescs  = {"SELECT", "SELECT", "SELECT"};
        GImage[] sprites    = new GImage[3];
        GLabel[] nameLabels = new GLabel[3];
        GRect[]  glows      = new GRect[3];
        GRect[]  frames     = new GRect[3];
        GRect[]  inners     = new GRect[3];

        String[] imgFiles = {"blueow.png", "redow.png", "greenow.png"};

        for (int i = 0; i < 3; i++) {
            Color cc = cardColors[i];
            double cx = cardX[i];
            double cy = cardY[i];

            // Outer glow
            GRect glow = new GRect(cx - 8, cy - 8, cardW + 16, cardH + 16);
            glow.setFilled(true);
            glow.setFillColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 18));
            glow.setColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 60));
            add(glow);
            glows[i] = glow;

            // Card body
            GRect frame = new GRect(cx, cy, cardW, cardH);
            frame.setFilled(true);
            frame.setFillColor(new Color(16, 12, 30));
            frame.setColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 180));
            add(frame);
            frames[i] = frame;

            // Inner accent line
            GRect inner = new GRect(cx + 5, cy + 5, cardW - 10, cardH - 10);
            inner.setFilled(false);
            inner.setColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 35));
            add(inner);
            inners[i] = inner;

            // Corner diamonds
            double[][] corners = {
                {cx,        cy},
                {cx + cardW, cy},
                {cx,        cy + cardH},
                {cx + cardW, cy + cardH},
            };
            for (double[] corner : corners) {
                GPolygon d = makeDiamond(corner[0], corner[1], 8);
                d.setFilled(true);
                d.setFillColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 160));
                d.setColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 220));
                add(d);
            }

            // Top colour stripe
            GRect stripe = new GRect(cx, cy, cardW, 6);
            stripe.setFilled(true);
            stripe.setFillColor(cc);
            stripe.setColor(cc);
            add(stripe);

            // Sprite image
            sprites[i] = new GImage(imgFiles[i]);
            sprites[i].scale(0.9);
            double imgX = cx + (cardW - sprites[i].getWidth())  / 2.0;
            double imgY = cy + cardH * 0.14;
            sprites[i].setLocation(imgX, imgY);
            add(sprites[i]);

            // Colour name label
            GLabel nameLbl = new GLabel(cardNames[i]);
            nameLbl.setFont("Times New Roman-Bold-28");
            nameLbl.setColor(cc);
            nameLbl.setLocation(cx + (cardW - nameLbl.getWidth()) / 2.0,
                                 cy + cardH * 0.74);
            add(nameLbl);
            nameLabels[i] = nameLbl;

            // Descriptor tag
         // Descriptor tag
         // SELECT button
         // SELECT button
            GRect tagBg = new GRect(cx + cardW * 0.25, cy + cardH * 0.80,
                                    cardW * 0.5, 32);
            tagBg.setFilled(true);
            tagBg.setFillColor(cc);
            tagBg.setColor(Color.WHITE);
            add(tagBg);

            GLabel descLbl = new GLabel(cardDescs[i]);
            descLbl.setFont("Arial-Bold-16");
            descLbl.setColor(Color.WHITE);
            descLbl.setLocation(cx + (cardW - descLbl.getWidth()) / 2.0,
                                 cy + cardH * 0.80 + 21);
            add(descLbl);

            selectButtons[i] = tagBg;
            selectTexts[i] = descLbl;
            
            add(descLbl);

        }

        // Store refs
        blue      = sprites[0]; blueLabel  = nameLabels[0];
        blueGlow  = glows[0];   blueFrame  = frames[0];  blueInner  = inners[0];

        red       = sprites[1]; redLabel   = nameLabels[1];
        redGlow   = glows[1];   redFrame   = frames[1];  redInner   = inners[1];

        green     = sprites[2]; greenLabel = nameLabels[2];
        greenGlow = glows[2];   greenFrame = frames[2];  greenInner = inners[2];

        // ── 4. Version strip ──────────────────────────────────────────────────
        GLabel version = new GLabel("Every Last Hue   ·   Select your champion to begin");
        version.setFont("Arial-12");
        version.setColor(new Color(70, 65, 100));
        version.setLocation((W - version.getWidth()) / 2.0, H - 16);
        add(version);

        // ── 5. Start animation ────────────────────────────────────────────────
        tick = 0;
        animTimer = new Timer(33, e -> animate());
        animTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Brick background
    // ════════════════════════════════════════════════════════════════════════
    private void drawBrickBackground(int W, int H) {
        GRect base = new GRect(0, 0, W, H);
        base.setFilled(true);
        base.setFillColor(new Color(8, 8, 14));
        base.setColor(new Color(8, 8, 14));
        add(base);

        brickCols = W / (BRICK_W + BRICK_GAP) + 3;
        brickRows = H / (BRICK_H + BRICK_GAP) + 2;

        brickGrid  = new GRect[brickRows][brickCols];
        brickPhase = new float[brickRows][brickCols];

        for (int row = 0; row < brickRows; row++) {
            int offset = (row % 2 == 0) ? 0 : (BRICK_W + BRICK_GAP) / 2;
            int y      = row * (BRICK_H + BRICK_GAP);

            for (int col = 0; col < brickCols; col++) {
                int x = col * (BRICK_W + BRICK_GAP) - offset;

                // Wave phase based on position — diagonal ripple
                brickPhase[row][col] = (float)((row * 0.4 + col * 0.7) % (Math.PI * 2));

                GRect brick = new GRect(x, y, BRICK_W, BRICK_H);
                brick.setFilled(true);
                brick.setFillColor(new Color(18, 14, 28));
                brick.setColor(new Color(80, 50, 140, 60));
                add(brick);
                brickGrid[row][col] = brick;
            }
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    //  Animation loop
    // ════════════════════════════════════════════════════════════════════════
    private void animate() {
        tick++;

        GRect[]  glows  = {blueGlow,  redGlow,  greenGlow};
        GRect[]  frames = {blueFrame, redFrame, greenFrame};
        Color[]  cols   = {C_BLUE,    C_RED,    C_GREEN};
        GImage[] imgs   = {blue,       red,      green};
        
     // ── Animated brick wave ───────────────────────────────────────────────────
        if (brickGrid != null) {
            for (int row = 0; row < brickRows; row++) {
                for (int col = 0; col < brickCols; col++) {
                    GRect brick = brickGrid[row][col];
                    if (brick == null) continue;

                    // Travelling wave: sine of (phase + time)
                    float wave = (float)(0.5 + 0.5 * Math.sin(brickPhase[row][col] + tick * 0.04));

                    // Glow colour cycles through purple → teal → purple
                    int r = (int)(60  + wave * 80);
                    int g = (int)(20  + wave * 40);
                    int b = (int)(140 + wave * 115);
                    int borderAlpha = (int)(40 + wave * 180);
                    int fillAlpha   = (int)(wave * 22);

                    brick.setFillColor(new Color(r / 4, g / 4, b / 4, 255));
                    brick.setColor(new Color(r, g, b, borderAlpha));
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (glows[i] == null || frames[i] == null) continue;

            boolean hovered = (imgs[i] == hoveredImage);
            float pulse = (float)(0.5 + 0.5 * Math.sin(tick * 0.05 + PHASE[i]));

            Color cc = cols[i];

            if (hovered) {
                // Bright solid glow when hovered
                glows[i].setFillColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 55));
                glows[i].setColor(    new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 200));
                frames[i].setColor(   new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), 240));
            } else {
                // Gentle breathing glow at rest
                int glowA  = 15 + (int)(pulse * 25);
                int frameA = 120 + (int)(pulse * 60);
                glows[i].setFillColor(new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), glowA));
                glows[i].setColor(    new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), (int)(pulse * 80)));
                frames[i].setColor(   new Color(cc.getRed(), cc.getGreen(), cc.getBlue(), frameA));
            }
        }

        // Title colour bob
        if (titleLabel != null) {
            float t = (float)(0.5 + 0.5 * Math.sin(tick * 0.035));
            int r = lerp(180, 220, t);
            int g = lerp(120, 160, t);
            int b = lerp(255, 255, t);
            titleLabel.setColor(new Color(r, g, b));

            // Gentle vertical bob
            double W = mainScreen.getWidth();
            double bob = Math.sin(tick * 0.04) * 2.5;
            titleLabel.setLocation((W - titleLabel.getWidth()) / 2.0,
                                    mainScreen.getHeight() * 0.14 + bob);
            if (titleShadow != null)
                titleShadow.setLocation((W - titleShadow.getWidth()) / 2.0 + 4,
                                         mainScreen.getHeight() * 0.14 + bob + 4);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  hideContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void hideContent() {
    	selectButtons = new GRect[3];
    	selectTexts = new GLabel[3];
    	
        if (animTimer != null) { animTimer.stop(); animTimer = null; }
        mainScreen.getGCanvas().setBackground(new Color(10, 10, 18));
        for (GObject item : contents) mainScreen.remove(item);
        contents.clear();
        hoveredImage = null;
        red = null; blue = null; green = null;
        redGlow = null; blueGlow = null; greenGlow = null;
        redFrame = null; blueFrame = null; greenFrame = null;
        redInner = null; blueInner = null; greenInner = null;
        titleLabel = null; titleShadow = null;
        redLabel = null; blueLabel = null; greenLabel = null;
        brickGrid = null; brickPhase = null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Hover / click
    // ════════════════════════════════════════════════════════════════════════
    private void scaleFromCenter(GImage image, double factor) {
        double oldW = image.getWidth(),  oldH = image.getHeight();
        double oldX = image.getX(),      oldY = image.getY();
        image.scale(factor, factor);
        image.setLocation(oldX - (image.getWidth()  - oldW) / 2,
                          oldY - (image.getHeight() - oldH) / 2);
    }

    private void setHoveredImage(GImage newImage) {
        if (hoveredImage == newImage) return;
        if (hoveredImage != null) scaleFromCenter(hoveredImage, 1.0 / HOVER_SCALE);
        hoveredImage = newImage;
        if (hoveredImage != null) scaleFromCenter(hoveredImage, HOVER_SCALE);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == blue || obj == blueFrame || obj == blueInner || obj == blueGlow
                || obj == selectButtons[0] || obj == selectTexts[0]) {
            setHoveredImage(blue);
        } 
        else if (obj == red || obj == redFrame || obj == redInner || obj == redGlow
                || obj == selectButtons[1] || obj == selectTexts[1]) {
            setHoveredImage(red);
        } 
        else if (obj == green || obj == greenFrame || obj == greenInner || obj == greenGlow
                || obj == selectButtons[2] || obj == selectTexts[2]) {
            setHoveredImage(green);
        } 
        else {
            setHoveredImage(null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == blue || obj == blueFrame || obj == blueInner || obj == blueGlow
                || obj == blueLabel || obj == selectButtons[0] || obj == selectTexts[0]) {
            selectColor(ColorType.BLUE);
        } 
        else if (obj == red || obj == redFrame || obj == redInner || obj == redGlow
                || obj == redLabel || obj == selectButtons[1] || obj == selectTexts[1]) {
            selectColor(ColorType.RED);
        } 
        else if (obj == green || obj == greenFrame || obj == greenInner || obj == greenGlow
                || obj == greenLabel || obj == selectButtons[2] || obj == selectTexts[2]) {
            selectColor(ColorType.GREEN);
        }
    }
    

    // ════════════════════════════════════════════════════════════════════════
    //  Colour selection (unchanged logic)
    // ════════════════════════════════════════════════════════════════════════
    private void selectColor(ColorType color) {
        Hueman player = new Hueman(600, 80, 12, "Hueman", color);
        mainScreen.setPlayer(player);

        if      (color == ColorType.RED)   mainScreen.setSelectedColor("red");
        else if (color == ColorType.BLUE)  mainScreen.setSelectedColor("blue");
        else if (color == ColorType.GREEN) mainScreen.setSelectedColor("green");

        System.out.println("Selected color: " + color);
        mainScreen.switchToCutsceneScreen();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════════
    private void add(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
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