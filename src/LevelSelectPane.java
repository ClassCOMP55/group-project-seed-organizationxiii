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
    private GLabel[] levelNumbers;

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
    private ArrayList<GObject> ropeObjects    = new ArrayList<GObject>();
    private ArrayList<GObject> bgObjects      = new ArrayList<GObject>();

    // ─── Palette ────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(18,  20,  30);
    private static final Color BG_MID       = new Color(28,  32,  50);
    private static final Color PATH_FILL    = new Color(45,  50,  75);
    private static final Color PATH_BORDER  = new Color(70,  80, 120);
    private static final Color ROPE_MAIN    = new Color(180, 145,  80);
    private static final Color ROPE_DARK    = new Color(120,  90,  45);
    private static final Color STAR_GLOW    = new Color(255, 225, 100);
    private static final Color STAR_DONE    = new Color(255, 220,  60);  // overridden by player colour
    private static final Color STAR_LOCKED  = new Color(35,   38,  55);
    private static final Color STAR_BORDER  = new Color(90,  100, 140);

    // ─── Window dimensions (match MainApplication constants) ─────────────────
    private double W;
    private double H;

    public LevelSelectPane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  showContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void showContent() {
    	W = mainScreen.getWidth();
    	H = mainScreen.getHeight();
        showLevelScreen = false;
        clearPreviewScreen();

        Hueman player       = mainScreen.getPlayer();
        int   levelProgress = mainScreen.getCurrentLevel();

        removeAllStars();
        clearRopes();
        clearBackground();

        // ── 1. Star positions – winding S-curve from bottom-left to top-right ──
        // Layout (for a ~1920 × 1080 window; scale naturally with the coords):
        //   Level 1 (minion) : bottom-left  quadrant
        //   Level 2 (boss)   : lower-centre
        //   Level 3 (minion) : lower-right
        //   Level 4 (boss)   : mid-right
        //   Level 5 (minion) : mid-centre
        //   Level 6 (boss)   : mid-left
        //   Level 7 (minion) : upper-left
        //   Level 8 (boss)   : upper-right (final)

        pointX = new double[] {
        	    W * 0.125,  // 1
        	    W * 0.270,  // 2
        	    W * 0.427,  // 3
        	    W * 0.573,  // 4
        	    W * 0.500,  // 5
        	    W * 0.333,  // 6
        	    W * 0.188,  // 7
        	    W * 0.354   // 8
        	};

        	pointY = new double[] {
        	    H * 0.759,  // 1
        	    H * 0.722,  // 2
        	    H * 0.667,  // 3
        	    H * 0.630,  // 4
        	    H * 0.444,  // 5
        	    H * 0.370,  // 6
        	    H * 0.241,  // 7
        	    H * 0.167   // 8
        	};
        // ── 2. Rich background ────────────────────────────────────────────────
        drawBackground();

        // ── 3. Rope path ─────────────────────────────────────────────────────
        drawRopes();

        // ── 4. Stars ─────────────────────────────────────────────────────────
        stars        = new GPolygon[8];
        starGlows    = new GPolygon[8];
        levelNumbers = new GLabel[8];

        for (int i = 0; i < 8; i++) {
            boolean isBoss = ((i + 1) % 2 == 0);

            double outerGlow = isBoss ? 62 : 48;
            double innerGlow = isBoss ? 30 : 22;
            double outerStar = isBoss ? 52 : 40;
            double innerStar = isBoss ? 24 : 16;

            // Outer glow ring
            starGlows[i] = createStar(pointX[i], pointY[i], outerGlow, innerGlow);
            starGlows[i].setFilled(true);
            starGlows[i].setFillColor(STAR_GLOW);
            starGlows[i].setColor(STAR_GLOW);
            mainScreen.add(starGlows[i]);

            // Main star
            stars[i] = createStar(pointX[i], pointY[i], outerStar, innerStar);
            stars[i].setFilled(true);

            if (levelProgress > i) {          // already completed
                stars[i].setFillColor(getPlayerColor());
                stars[i].setColor(STAR_GLOW);
            } else if (levelProgress == i) {  // current / unlocked
                stars[i].setFillColor(new Color(60, 65, 90));
                stars[i].setColor(STAR_GLOW);
            } else {                          // locked
                stars[i].setFillColor(STAR_LOCKED);
                stars[i].setColor(STAR_BORDER);
            }

            mainScreen.add(stars[i]);

            // Level number label beneath each star
            GLabel num = new GLabel(String.valueOf(i + 1));
            num.setFont("Arial-Bold-16");
            num.setColor(levelProgress > i
                    ? new Color(30, 30, 30)
                    : new Color(120, 130, 160));
            num.setLocation(pointX[i] - num.getWidth() / 2.0,
                            pointY[i] + outerStar + 18);
            mainScreen.add(num);
            levelNumbers[i] = num;

            // Boss crown label
            if (isBoss) {
                GLabel bossTag = new GLabel("BOSS");
                bossTag.setFont("Arial-Bold-11");
                bossTag.setColor(new Color(220, 170, 50));
                bossTag.setLocation(pointX[i] - bossTag.getWidth() / 2.0,
                                    pointY[i] - outerStar - 20);
                mainScreen.add(bossTag);
                levelNumbers[i] = bossTag; // repurpose slot to track removal
            }
        }

        // ── 5. Player character ───────────────────────────────────────────────
        currentPoint = levelProgress - 1;
        if (currentPoint < 0) currentPoint = 0;
        if (currentPoint > 7) currentPoint = 7;

        huemanImage = player.getSprite("overworld");
        huemanImage.scale(0.65);

        if (currentPoint == 0 && levelProgress == 0) {
            // Haven't played yet — place just before star 1
            huemanImage.setLocation(pointX[0] - 70, pointY[0]);
        } else {
            int clamp = Math.min(currentPoint, 7);
            huemanImage.setLocation(pointX[clamp], pointY[clamp]);
        }

        mainScreen.add(huemanImage);
        huemanImage.sendToFront();
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Background
    // ════════════════════════════════════════════════════════════════════════
    private void drawBackground() {
        // Base dark fill
        GRect base = new GRect(0, 0, W, H);
        base.setFilled(true);
        base.setFillColor(BG_DARK);
        base.setColor(BG_DARK);
        mainScreen.add(base);
        bgObjects.add(base);

        // Subtle gradient panels (lighter towards the centre/top)
        GRect midPanel = new GRect(0, 0, W, H * 2 / 3);
        midPanel.setFilled(true);
        midPanel.setFillColor(new Color(22, 26, 42, 180));
        midPanel.setColor(new Color(22, 26, 42, 0));
        mainScreen.add(midPanel);
        bgObjects.add(midPanel);

        // Soft "ground" strip at the bottom
        GRect ground = new GRect(0, H - 120, W, 120);
        ground.setFilled(true);
        ground.setFillColor(new Color(15, 17, 28));
        ground.setColor(new Color(15, 17, 28));
        mainScreen.add(ground);
        bgObjects.add(ground);

        // Decorative star-field dots (small circles scattered in the background)
        int[][] dots = {
            {100, 80},  {340, 50},  {600, 120}, {900, 40},
            {1200, 90}, {1500, 60}, {1750, 130},{150, 200},
            {450, 300}, {750, 180}, {1050, 240},{1350, 160},
            {1650, 220},{80,  450}, {380, 500}, {680, 420},
            {980, 480}, {1280, 360},{1580, 440},{1820, 300}
        };
        for (int[] d : dots) {
            GOval dot = new GOval(d[0], d[1], 3, 3);
            dot.setFilled(true);
            dot.setFillColor(new Color(180, 190, 220, 100));
            dot.setColor(new Color(180, 190, 220, 100));
            mainScreen.add(dot);
            bgObjects.add(dot);
        }

        // "LEVEL SELECT" title banner at the top
        GLabel title = new GLabel("LEVEL SELECT");
        title.setFont("Arial-Bold-36");
        title.setColor(new Color(220, 215, 255));
        title.setLocation((W - title.getWidth()) / 2.0, 80);
        mainScreen.add(title);
        bgObjects.add(title);

        // Thin horizontal divider under the title
        GLine divider = new GLine((W / 2.0) - 160, 95, (W / 2.0) + 160, 95);
        divider.setColor(new Color(100, 100, 180, 160));
        mainScreen.add(divider);
        bgObjects.add(divider);

        // Key legend bottom-right
        drawLegend();
    }

    private void drawLegend() {
        double lx = W - 230;
        double ly = H - 110;

        // Completed star sample
        GPolygon sampleDone = createStar(lx + 12, ly + 14, 12, 5);
        sampleDone.setFilled(true);
        sampleDone.setFillColor(getPlayerColor());
        sampleDone.setColor(STAR_GLOW);
        mainScreen.add(sampleDone);
        bgObjects.add(sampleDone);

        GLabel lblDone = new GLabel("Completed");
        lblDone.setFont("Arial-14");
        lblDone.setColor(new Color(180, 185, 210));
        lblDone.setLocation(lx + 30, ly + 19);
        mainScreen.add(lblDone);
        bgObjects.add(lblDone);

        // Locked star sample
        GPolygon sampleLocked = createStar(lx + 12, ly + 46, 12, 5);
        sampleLocked.setFilled(true);
        sampleLocked.setFillColor(STAR_LOCKED);
        sampleLocked.setColor(STAR_BORDER);
        mainScreen.add(sampleLocked);
        bgObjects.add(sampleLocked);

        GLabel lblLocked = new GLabel("Locked");
        lblLocked.setFont("Arial-14");
        lblLocked.setColor(new Color(120, 125, 160));
        lblLocked.setLocation(lx + 30, ly + 51);
        mainScreen.add(lblLocked);
        bgObjects.add(lblLocked);

        // Controls hint
        GLabel hint = new GLabel("[A] / [D]  to move  ·  Click star to select");
        hint.setFont("Arial-13");
        hint.setColor(new Color(90, 100, 140));
        hint.setLocation(W - 320, H - 30);
        mainScreen.add(hint);
        bgObjects.add(hint);
    }

    private void clearBackground() {
        for (GObject obj : bgObjects) mainScreen.remove(obj);
        bgObjects.clear();
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Ropes  — curved catenary arcs instead of straight crossing lines
    // ════════════════════════════════════════════════════════════════════════
    private void drawRopes() {
        clearRopes();

        for (int i = 0; i < pointX.length - 1; i++) {
            drawRopeSegment(pointX[i], pointY[i], pointX[i + 1], pointY[i + 1]);
        }
    }

    /**
     * Draws a two-line "rope" between (x1,y1) and (x2,y2) using a
     * quadratic Bézier approximation rendered with small line segments.
     * The control point sags downward to mimic a hanging rope.
     */
    private void drawRopeSegment(double x1, double y1, double x2, double y2) {
        // Control point: mid-point + downward sag
        double mx  = (x1 + x2) / 2.0;
        double my  = (y1 + y2) / 2.0;
        double sag = 50.0;   // vertical droop in pixels
        double cx  = mx;
        double cy  = my + sag;

        int steps = 24;

        double prevX1 = x1, prevY1 = y1;
        double prevX2 = x1 + 2, prevY2 = y1 + 2;

        for (int k = 1; k <= steps; k++) {
            double t  = (double) k / steps;
            double bx = (1-t)*(1-t)*x1 + 2*(1-t)*t*cx + t*t*x2;
            double by = (1-t)*(1-t)*y1 + 2*(1-t)*t*cy + t*t*y2;

            GLine seg1 = new GLine(prevX1, prevY1, bx, by);
            seg1.setColor(ROPE_MAIN);
            mainScreen.add(seg1);
            ropeObjects.add(seg1);

            GLine seg2 = new GLine(prevX2, prevY2, bx + 2, by + 2);
            seg2.setColor(ROPE_DARK);
            mainScreen.add(seg2);
            ropeObjects.add(seg2);

            prevX1 = bx;  prevY1 = by;
            prevX2 = bx + 2; prevY2 = by + 2;
        }

        // Knots along the rope
        for (int k = 1; k <= 3; k++) {
            double t  = k / 4.0;
            double kx = (1-t)*(1-t)*x1 + 2*(1-t)*t*cx + t*t*x2;
            double ky = (1-t)*(1-t)*y1 + 2*(1-t)*t*cy + t*t*y2;

            GLine knot = new GLine(kx - 4, ky - 4, kx + 4, ky + 4);
            knot.setColor(ROPE_DARK);
            mainScreen.add(knot);
            ropeObjects.add(knot);
        }
    }

    private void clearRopes() {
        for (GObject obj : ropeObjects) mainScreen.remove(obj);
        ropeObjects.clear();
    }


    // ════════════════════════════════════════════════════════════════════════
    //  hideContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void hideContent() {
        if (huemanImage != null) mainScreen.remove(huemanImage);
        removeAllStars();
        clearRopes();
        clearBackground();
        clearPreviewScreen();
        showLevelScreen = false;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Input handling
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void keyPressed(KeyEvent e) {
        if (showLevelScreen) return;
        if (huemanImage == null) return;

        if (e.getKeyCode() == KeyEvent.VK_D) moveForward();
        if (e.getKeyCode() == KeyEvent.VK_A) moveBackward();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (showLevelScreen) {
            if (startButton != null && startButton.contains(e.getX(), e.getY())) {
                launchLevel(selectedLevel);
                return;
            }
            if (backButton != null && backButton.contains(e.getX(), e.getY())) {
                clearPreviewScreen();
                showLevelScreen = false;
                return;
            }
            return; // swallow clicks while level screen is open
        }

        int levelProgress = mainScreen.getCurrentLevel();
        for (int i = 0; i < 8; i++) {
            if (stars[i] != null
                    && stars[i].contains(e.getX(), e.getY())
                    && levelProgress >= i + 1) {
                currentPoint = i + 1;
                selectedLevel = i + 1;
                showLevelPreview();
                return;
            }
        }
    }

    private void launchLevel(int level) {
        switch (level) {
            case 1: mainScreen.switchToSecondBattleScreen();  break;
            case 2: mainScreen.switchToCutScene3Screen();     break;
            case 3: mainScreen.switchToFourthBattleScreen();  break;
            case 4: mainScreen.switchToFifthBattleScreen();   break;
            // future levels wired here
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Movement
    // ════════════════════════════════════════════════════════════════════════
    private void moveForward() {
        int maxReachable = mainScreen.getCurrentLevel();
        if (currentPoint < maxReachable && currentPoint < pointX.length) {
            currentPoint++;
            huemanImage.setLocation(pointX[currentPoint - 1], pointY[currentPoint - 1]);
            selectedLevel = currentPoint;
            showLevelPreview();
        }
    }

    private void moveBackward() {
        if (currentPoint > 1) {
            currentPoint--;
            huemanImage.setLocation(pointX[currentPoint - 1], pointY[currentPoint - 1]);
        } else if (currentPoint == 1) {
            currentPoint = 0;
            huemanImage.setLocation(pointX[0] - 70, pointY[0]);
        }
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Level preview panel  (replaces showLevelOneScreen)
    // ════════════════════════════════════════════════════════════════════════
    private void showLevelPreview() {
        clearPreviewScreen();
        showLevelScreen = true;

        // ── Colours per level ────────────────────────────────────────────
        Color mainColor, glowColor, darkColor;
        String enemyName;

        switch (selectedLevel) {
            case 1:  mainColor = new Color(  0, 255, 220); glowColor = new Color(  0, 200, 170); darkColor = new Color(  0, 80, 70); enemyName = "mint.png";        break;
            case 2:  mainColor = new Color( 11,  95,  90); glowColor = new Color( 19, 115, 109); darkColor = new Color(  5, 45, 42); enemyName = "slujupiter.png";  break;
            case 3:  mainColor = new Color(170, 100, 255); glowColor = new Color(130,  70, 220); darkColor = new Color( 60, 20,100); enemyName = "lavender.png";    break;
            case 4:  mainColor = new Color(190, 140, 255); glowColor = new Color(145, 100, 220); darkColor = new Color( 70, 30,110); enemyName = "effervena.png";   break;
            case 5:  mainColor = new Color(255, 120, 120); glowColor = new Color(210,  90,  90); darkColor = new Color(100, 25, 25); enemyName = "";                break;
            case 6:  mainColor = new Color(255,  80,  80); glowColor = new Color(200,  50,  50); darkColor = new Color( 90, 15, 15); enemyName = "";                break;
            case 7:  mainColor = new Color(120, 180, 255); glowColor = new Color( 80, 140, 220); darkColor = new Color( 20, 55,110); enemyName = "";                break;
            default: mainColor = new Color( 80, 120, 255); glowColor = new Color( 50,  90, 220); darkColor = new Color( 15, 35, 90); enemyName = "";                break;
        }

        boolean isBoss   = (selectedLevel % 2 == 0);
        String  typeText = isBoss ? "BOSS" : "MINION";

        // Panel dimensions — slim side-panel anchored to the right
        int panelW = 460;
        int panelH = 560;
      double panelX = W - panelW - 120;
      double panelY = (H - panelH) / 2;

        // ── Dark panel background ────────────────────────────────────────
        GRect shadow = new GRect(panelX + 6, panelY + 6, panelW, panelH);
        shadow.setFilled(true);
        shadow.setFillColor(new Color(0, 0, 0, 120));
        shadow.setColor(new Color(0, 0, 0, 0));
        addPreviewObject(shadow);

        GRect panel = new GRect(panelX, panelY, panelW, panelH);
        panel.setFilled(true);
        panel.setFillColor(darkColor);
        panel.setColor(mainColor);
        addPreviewObject(panel);

        // Accent top bar
        GRect topBar = new GRect(panelX, panelY, panelW, 6);
        topBar.setFilled(true);
        topBar.setFillColor(mainColor);
        topBar.setColor(mainColor);
        addPreviewObject(topBar);

        // ── Type badge (BOSS / MINION) ────────────────────────────────────
        GRect badge = new GRect(panelX + 20, panelY + 24, 90, 28);
        badge.setFilled(true);
        badge.setFillColor(mainColor);
        badge.setColor(mainColor);
        addPreviewObject(badge);

        GLabel badgeText = new GLabel(typeText);
        badgeText.setFont("Arial-Bold-13");
        badgeText.setColor(darkColor);
        badgeText.setLocation(panelX + 30, panelY + 43);
        addPreviewObject(badgeText);

        // ── Title ("Level N") ─────────────────────────────────────────────
        String titleStr = "Level  " + selectedLevel;
        GLabel titleGlow = new GLabel(titleStr);
        titleGlow.setFont("Arial-Bold-46");
        titleGlow.setColor(glowColor);
        titleGlow.setLocation(panelX + 18, panelY + 100);
        addPreviewObject(titleGlow);

        GLabel titleLabel = new GLabel(titleStr);
        titleLabel.setFont("Arial-Bold-46");
        titleLabel.setColor(mainColor);
        titleLabel.setLocation(panelX + 20, panelY + 98);
        addPreviewObject(titleLabel);

        // Divider
        GLine divLine = new GLine(panelX + 20, panelY + 112, panelX + panelW - 20, panelY + 112);
        divLine.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 80));
        addPreviewObject(divLine);

        // ── Enemy image or placeholder ────────────────────────────────────
        double imgAreaY = panelY + 126;
        int imgAreaH = 240;

        if (!enemyName.isEmpty()) {
            levelEnemyImage = new GImage(enemyName);
            levelEnemyImage.scale(0.7);
            double imgX = panelX + (panelW - levelEnemyImage.getWidth()) / 2.0;
            double imgY = imgAreaY + (imgAreaH - levelEnemyImage.getHeight()) / 2.0;
            levelEnemyImage.setLocation(imgX, imgY);
            addPreviewObject(levelEnemyImage);
        } else {
            // "COMING SOON" placeholder box
            GRect ph = new GRect(panelX + 100, imgAreaY + 30, 260, 180);
            ph.setFilled(true);
            ph.setFillColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 25));
            ph.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 80));
            addPreviewObject(ph);

            GLabel cs1 = new GLabel("COMING");
            cs1.setFont("Arial-Bold-28");
            cs1.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 120));
            cs1.setLocation(panelX + 150, imgAreaY + 108);
            addPreviewObject(cs1);

            GLabel cs2 = new GLabel("SOON");
            cs2.setFont("Arial-Bold-28");
            cs2.setColor(new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 120));
            cs2.setLocation(panelX + 168, imgAreaY + 145);
            addPreviewObject(cs2);
        }

        // ── Buttons ────────────────────────────────────────────────────────
        double btnX = panelX + 30;
        double btnW = panelW - 60;
        double btnH = 56;
        double startY = panelY + panelH - 155;
        double backY  = panelY + panelH - 85;

        addButton(btnX, startY, btnW, btnH, "START", mainColor, glowColor, darkColor, true);
        addButton(btnX, backY,  btnW, btnH, "BACK",  new Color(100, 110, 150), new Color(130, 140, 180), new Color(20, 22, 36), false);
    }

    /** Creates a styled button and registers startButton / backButton accordingly. */
    private void addButton(double x, double y, double w, double h,
                           String label, Color col, Color glow, Color dark,
                           boolean isPrimary) {
        // Glow halo
        GRect halo = new GRect(x - 3, y - 3, w + 6, h + 6);
        halo.setFilled(true);
        halo.setFillColor(new Color(col.getRed(), col.getGreen(), col.getBlue(), 60));
        halo.setColor(new Color(0, 0, 0, 0));
        addPreviewObject(halo);

        // Button body
        GRect btn = new GRect(x, y, w, h);
        btn.setFilled(true);
        btn.setFillColor(isPrimary ? new Color(dark.getRed(), dark.getGreen(), dark.getBlue()) : new Color(22, 24, 38));
        btn.setColor(col);
        addPreviewObject(btn);

        // Button text (with glow copy beneath)
        GLabel lblG = new GLabel(label);
        lblG.setFont("Arial-Bold-22");
        lblG.setColor(glow);
        double lx = x + (w - lblG.getWidth()) / 2.0;
        double ly = y + 36;
        lblG.setLocation(lx - 1, ly - 1);
        addPreviewObject(lblG);

        GLabel lbl = new GLabel(label);
        lbl.setFont("Arial-Bold-22");
        lbl.setColor(col);
        lbl.setLocation(lx, ly);
        addPreviewObject(lbl);

        if (isPrimary) {
            startButton = btn;
            startText   = lbl;
        } else {
            backButton = btn;
            backText   = lbl;
        }
    }

    private void addBrokenDivider(double centerX) {
        // Kept for backwards-compatibility but no longer used.
        Color lineColor = new Color(100, 100, 140);
        double[] yPoints = {0, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600};
        double[] offsets = {-4, 3, -6, 5, -3, 6, -5, 4, -2, 3, -4};
        for (int i = 0; i < yPoints.length - 1; i++) {
            GLine piece = new GLine(
                centerX + offsets[i], yPoints[i],
                centerX + offsets[i + 1], yPoints[i + 1]);
            piece.setColor(lineColor);
            addPreviewObject(piece);
        }
    }

    private void addPreviewObject(GObject obj) {
        mainScreen.add(obj);
        previewObjects.add(obj);
    }

    private void clearPreviewScreen() {
        for (GObject obj : previewObjects) mainScreen.remove(obj);
        previewObjects.clear();
        levelScreenBG  = null;
        levelTitle     = null;
        startButton    = null;
        startText      = null;
        backButton     = null;
        backText       = null;
        levelEnemyImage = null;
    }


    // ════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════════
    private void removeAllStars() {
        if (stars != null)
            for (GPolygon s : stars)     { if (s != null) mainScreen.remove(s); }
        if (starGlows != null)
            for (GPolygon s : starGlows) { if (s != null) mainScreen.remove(s); }
        if (levelNumbers != null)
            for (GLabel  l : levelNumbers){ if (l != null) mainScreen.remove(l); }
    }

    private GPolygon createStar(double cx, double cy, double outerR, double innerR) {
        GPolygon star = new GPolygon();
        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            double r     = (i % 2 == 0) ? outerR : innerR;
            star.addVertex(r * Math.cos(angle), r * Math.sin(angle));
        }
        star.setFilled(true);
        star.setFillColor(Color.BLACK);
        star.setColor(Color.DARK_GRAY);
        star.setLocation(cx, cy);
        return star;
    }

    private Color getPlayerColor() {
        String sel = mainScreen.getSelectedColor();
        if (sel == null)            return new Color(255, 220, 60);
        if (sel.equals("red"))      return new Color(255, 80, 80);
        if (sel.equals("green"))    return new Color(80, 255, 80);
        return new Color(80, 160, 255);
    }
}