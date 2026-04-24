import acm.graphics.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;
import characters.Hueman;

public class LevelSelectPane extends GraphicsPane {

    // ── Stars / path ─────────────────────────────────────────────────────────
    private GPolygon[] stars;
    private GPolygon[] starGlows;
    private GLabel[]   levelNumbers;
    private GLabel[]   bossLabels;

    private double[] pointX;
    private double[] pointY;

    private int     currentPoint  = 0;
    private int     selectedLevel = 1;
    private boolean showLevelScreen = false;

    // ── Level preview panel ───────────────────────────────────────────────────
    private GRect  startButton;
    private GRect  backButton;
    private GImage levelEnemyImage;

    // ── Object lists ─────────────────────────────────────────────────────────
    private final ArrayList<GObject> previewObjects = new ArrayList<>();
    private final ArrayList<GObject> ropeObjects    = new ArrayList<>();
    private final ArrayList<GObject> bgObjects      = new ArrayList<>();

    // ── Animated background ───────────────────────────────────────────────────
    private Timer animTimer;
    private int   tick = 0;

    // Twinkling star-field
    private static final int BG_STAR_COUNT = 120;
    private GOval[]  bgStars     = new GOval[BG_STAR_COUNT];
    private float[]  bgStarAlpha = new float[BG_STAR_COUNT];
    private float[]  bgStarSpeed = new float[BG_STAR_COUNT];

    // Moon
    private GOval moonDisc;
    private GOval moonGlow;

    // Meteors
    private static final int METEOR_COUNT = 6;
    private GLine[]   meteorLines  = new GLine[METEOR_COUNT];
    private double[]  meteorX      = new double[METEOR_COUNT];
    private double[]  meteorY      = new double[METEOR_COUNT];
    private double[]  meteorLen    = new double[METEOR_COUNT];
    private double[]  meteorSpeed  = new double[METEOR_COUNT];
    private float[]   meteorAlpha  = new float[METEOR_COUNT];
    private boolean[] meteorActive = new boolean[METEOR_COUNT];

    // Current-star indicator ring
    private GOval currentIndicator = null;

    // ── Palette ───────────────────────────────────────────────────────────────
    private static final Color SKY_TOP   = new Color( 5,  7, 20);
    private static final Color SKY_BOT   = new Color(12, 18, 40);
    private static final Color ROPE_MAIN = new Color(180, 145, 80);
    private static final Color ROPE_DARK = new Color(120,  90, 45);
    private static final Color STAR_GLOW = new Color(255, 225, 100);
    private static final Color STAR_LOCK = new Color( 35,  38, 55);
    private static final Color STAR_BORD = new Color( 90, 100,140);
    private static final Color TRAIN_COL = new Color(100, 230, 220); // cyan for training star

    // ── Dimensions ───────────────────────────────────────────────────────────
    private double W, H;

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
        removeAllStars();
        clearRopes();
        clearBackground();

        // ── 9 star positions: index 0 = Training, 1-8 = original levels ───────
        pointX = new double[]{
            W * 0.080,  // 0  Training
            W * 0.125,  // 1  Level 1  (minion)
            W * 0.270,  // 2  Level 2  (boss)
            W * 0.427,  // 3  Level 3  (minion)
            W * 0.573,  // 4  Level 4  (boss)
            W * 0.500,  // 5  Level 5  (minion)
            W * 0.333,  // 6  Level 6  (boss)
            W * 0.188,  // 7  Level 7  (minion)
            W * 0.354   // 8  Level 8  (boss / final)
        };
        pointY = new double[]{
            H * 0.860,  // 0  Training
            H * 0.759,  // 1
            H * 0.722,  // 2
            H * 0.667,  // 3
            H * 0.630,  // 4
            H * 0.444,  // 5
            H * 0.370,  // 6
            H * 0.241,  // 7
            H * 0.167   // 8
        };

        drawBackground();
        drawRopes();
        drawLevelStars();

        currentPoint = Math.max(0, Math.min(8, mainScreen.getCurrentLevel()));
        highlightCurrentStar();

        tick = 0;
        animTimer = new Timer(33, e -> animate());
        animTimer.start();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Background
    // ════════════════════════════════════════════════════════════════════════
    private void drawBackground() {
        GRect skyTop = new GRect(0, 0, W, H * 0.55);
        skyTop.setFilled(true); skyTop.setFillColor(SKY_TOP); skyTop.setColor(SKY_TOP);
        bg(skyTop);

        GRect skyBot = new GRect(0, H * 0.45, W, H * 0.55);
        skyBot.setFilled(true); skyBot.setFillColor(SKY_BOT); skyBot.setColor(SKY_BOT);
        bg(skyBot);

        GRect ground = new GRect(0, H * 0.92, W, H * 0.08);
        ground.setFilled(true); ground.setFillColor(new Color(8,10,20)); ground.setColor(new Color(8,10,20));
        bg(ground);

        // ── Moon ─────────────────────────────────────────────────────────────
        double moonX = W * 0.82, moonY = H * 0.10, moonR = 55;

        moonGlow = new GOval(moonX - moonR*1.6, moonY - moonR*1.6, moonR*3.2, moonR*3.2);
        moonGlow.setFilled(true);
        moonGlow.setFillColor(new Color(255, 245, 200, 18));
        moonGlow.setColor(new Color(0,0,0,0));
        bg(moonGlow);

        moonDisc = new GOval(moonX - moonR, moonY - moonR, moonR*2, moonR*2);
        moonDisc.setFilled(true);
        moonDisc.setFillColor(new Color(255, 248, 220));
        moonDisc.setColor(new Color(240, 230, 180, 160));
        bg(moonDisc);

        int[][] craters = {{-18,-10,14},{10,8,10},{-8,18,7},{20,-18,8}};
        for (int[] c : craters) {
            GOval cr = new GOval(moonX+c[0]-c[2]/2.0, moonY+c[1]-c[2]/2.0, c[2], c[2]);
            cr.setFilled(true);
            cr.setFillColor(new Color(220,212,185));
            cr.setColor(new Color(200,192,165,120));
            bg(cr);
        }

        // ── Twinkling star-field ──────────────────────────────────────────────
        for (int i = 0; i < BG_STAR_COUNT; i++) {
            double sx = Math.random() * W;
            double sy = Math.random() * H * 0.85;
            double sr = 0.8 + Math.random() * 1.8;
            bgStarAlpha[i] = (float) Math.random();
            bgStarSpeed[i] = 0.005f + (float)(Math.random() * 0.015);
            bgStars[i] = new GOval(sx-sr, sy-sr, sr*2, sr*2);
            bgStars[i].setFilled(true);
            Color sc = new Color(200, 210, 255, Math.max(0,Math.min(255,(int)(bgStarAlpha[i]*200))));
            bgStars[i].setFillColor(sc); bgStars[i].setColor(sc);
            bg(bgStars[i]);
        }

        // ── Meteors ───────────────────────────────────────────────────────────
        for (int i = 0; i < METEOR_COUNT; i++) {
            meteorLines[i] = new GLine(0,0,1,1);
            meteorLines[i].setColor(new Color(255,255,255,0));
            bg(meteorLines[i]);
            spawnMeteor(i, true);
        }

        // ── UI title ──────────────────────────────────────────────────────────
        GLabel titleShadow = new GLabel("LEVEL SELECT");
        titleShadow.setFont("Arial-Bold-40");
        titleShadow.setColor(new Color(0,0,0,160));
        titleShadow.setLocation((W - titleShadow.getWidth())/2.0 + 3, H*0.085 + 3);
        bg(titleShadow);

        GLabel title = new GLabel("LEVEL SELECT");
        title.setFont("Arial-Bold-40");
        title.setColor(new Color(220,215,255));
        title.setLocation((W - title.getWidth())/2.0, H*0.085);
        bg(title);

        GLine divider = new GLine(W/2.0-180, H*0.095, W/2.0+180, H*0.095);
        divider.setColor(new Color(120,110,200,140));
        bg(divider);

        drawLegend();
    }

    private void drawLegend() {
        double lx = W - 240, ly = H - 130;

        // Training sample
        GPolygon st = createStar(lx+12, ly+14, 10, 5);
        st.setFilled(true); st.setFillColor(new Color(80,220,200)); st.setColor(TRAIN_COL);
        bg(st);
        GLabel lt = new GLabel("Training");
        lt.setFont("Arial-14"); lt.setColor(new Color(100,220,210));
        lt.setLocation(lx+30, ly+19); bg(lt);

        // Completed sample
        GPolygon sd = createStar(lx+12, ly+42, 12, 5);
        sd.setFilled(true); sd.setFillColor(getPlayerColor()); sd.setColor(STAR_GLOW);
        bg(sd);
        GLabel ld = new GLabel("Completed");
        ld.setFont("Arial-14"); ld.setColor(new Color(180,185,210));
        ld.setLocation(lx+30, ly+47); bg(ld);

        // Locked sample
        GPolygon sl = createStar(lx+12, ly+70, 12, 5);
        sl.setFilled(true); sl.setFillColor(STAR_LOCK); sl.setColor(STAR_BORD);
        bg(sl);
        GLabel ll = new GLabel("Locked");
        ll.setFont("Arial-14"); ll.setColor(new Color(120,125,160));
        ll.setLocation(lx+30, ly+75); bg(ll);

        GLabel hint = new GLabel("[A] / [D]  to move   ·   Click star to select");
        hint.setFont("Arial-13"); hint.setColor(new Color(90,100,140));
        hint.setLocation(W-340, H-16); bg(hint);
    }

    private void bg(GObject obj) { mainScreen.add(obj); bgObjects.add(obj); }

    private void clearBackground() {
        for (GObject o : bgObjects) mainScreen.remove(o);
        bgObjects.clear();
        moonDisc = null; moonGlow = null;
        for (int i = 0; i < BG_STAR_COUNT; i++) bgStars[i]    = null;
        for (int i = 0; i < METEOR_COUNT;   i++) meteorLines[i] = null;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Level stars  (9 total: index 0 = Training)
    // ════════════════════════════════════════════════════════════════════════
    private void drawLevelStars() {
        int levelProgress = mainScreen.getCurrentLevel();

        stars        = new GPolygon[9];
        starGlows    = new GPolygon[9];
        levelNumbers = new GLabel[9];
        bossLabels   = new GLabel[9];

        for (int i = 0; i < 9; i++) {
            boolean isTraining = (i == 0);
            // After training: i=1 minion, i=2 boss, i=3 minion, i=4 boss …
            boolean isBoss = (!isTraining && (i % 2 == 0));

            double outerG = isTraining ? 40 : (isBoss ? 62 : 48);
            double innerG = isTraining ? 20 : (isBoss ? 30 : 22);
            double outerS = isTraining ? 32 : (isBoss ? 52 : 40);
            double innerS = isTraining ? 16 : (isBoss ? 24 : 16);

            // Glow ring
            starGlows[i] = createStar(pointX[i], pointY[i], outerG, innerG);
            starGlows[i].setFilled(true);

            if (isTraining) {
                starGlows[i].setFillColor(new Color(TRAIN_COL.getRed(), TRAIN_COL.getGreen(), TRAIN_COL.getBlue(), 70));
                starGlows[i].setColor(new Color(TRAIN_COL.getRed(), TRAIN_COL.getGreen(), TRAIN_COL.getBlue(), 70));
            } else {
                starGlows[i].setFillColor(STAR_GLOW);
                starGlows[i].setColor(STAR_GLOW);
            }
            mainScreen.add(starGlows[i]);

            // Main star fill
            stars[i] = createStar(pointX[i], pointY[i], outerS, innerS);
            stars[i].setFilled(true);

            if (isTraining) {
                // Distinct cyan teal — clearly different from boss gold or regular grey
                stars[i].setFillColor(levelProgress > i ? new Color(80,220,200) : new Color(25,65,75));
                stars[i].setColor(TRAIN_COL);
            } else if (levelProgress > i) {
                stars[i].setFillColor(getPlayerColor());
                stars[i].setColor(STAR_GLOW);
            } else if (levelProgress == i) {
                stars[i].setFillColor(new Color(60,65,90));
                stars[i].setColor(STAR_GLOW);
            } else {
                stars[i].setFillColor(STAR_LOCK);
                stars[i].setColor(STAR_BORD);
            }
            mainScreen.add(stars[i]);

            // Number / symbol beneath star
            GLabel num;
            if (isTraining) {
                num = new GLabel("✦");
                num.setFont("Arial-Bold-14");
                num.setColor(new Color(100,220,210));
            } else {
                num = new GLabel(String.valueOf(i)); // levels 1-8
                num.setFont("Arial-Bold-16");
                num.setColor(levelProgress > i ? new Color(200,210,230) : new Color(90,100,130));
            }
            num.setLocation(pointX[i] - num.getWidth()/2.0, pointY[i] + outerS + 20);
            mainScreen.add(num);
            levelNumbers[i] = num;

            // Tag above star
            if (isTraining) {
                GLabel tag = new GLabel("TRAINING");
                tag.setFont("Arial-Bold-11");
                tag.setColor(new Color(120, 200, 255)); // cyan-blue, distinct from gold BOSS
                tag.setLocation(pointX[i] - tag.getWidth()/2.0, pointY[i] - outerS - 20);
                mainScreen.add(tag);
                bossLabels[i] = tag;
            } else if (isBoss) {
                GLabel boss = new GLabel("BOSS");
                boss.setFont("Arial-Bold-11");
                boss.setColor(new Color(220,170,50));
                boss.setLocation(pointX[i] - boss.getWidth()/2.0, pointY[i] - outerS - 20);
                mainScreen.add(boss);
                bossLabels[i] = boss;
            }
        }
    }

    private void highlightCurrentStar() {
        if (currentIndicator != null) mainScreen.remove(currentIndicator);
        if (currentPoint < 0 || currentPoint > 8) return;
        double r = 70;
        currentIndicator = new GOval(pointX[currentPoint]-r, pointY[currentPoint]-r, r*2, r*2);
        currentIndicator.setFilled(false);
        currentIndicator.setColor(new Color(255,230,100,80));
        mainScreen.add(currentIndicator);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Ropes
    // ════════════════════════════════════════════════════════════════════════
    private void drawRopes() {
        clearRopes();
        for (int i = 0; i < pointX.length - 1; i++)
            drawRopeSegment(pointX[i], pointY[i], pointX[i+1], pointY[i+1]);
    }

    private void drawRopeSegment(double x1, double y1, double x2, double y2) {
        double mx = (x1+x2)/2.0, my = (y1+y2)/2.0;
        double cx = mx, cy = my + 50;
        int steps = 24;
        double px1=x1, py1=y1, px2=x1+2, py2=y1+2;
        for (int k = 1; k <= steps; k++) {
            double t  = (double)k/steps;
            double bx = (1-t)*(1-t)*x1 + 2*(1-t)*t*cx + t*t*x2;
            double by = (1-t)*(1-t)*y1 + 2*(1-t)*t*cy + t*t*y2;
            GLine s1 = new GLine(px1,py1,bx,by);       s1.setColor(ROPE_MAIN); mainScreen.add(s1); ropeObjects.add(s1);
            GLine s2 = new GLine(px2,py2,bx+2,by+2);   s2.setColor(ROPE_DARK); mainScreen.add(s2); ropeObjects.add(s2);
            px1=bx; py1=by; px2=bx+2; py2=by+2;
        }
        for (int k = 1; k <= 3; k++) {
            double t  = k/4.0;
            double kx = (1-t)*(1-t)*x1 + 2*(1-t)*t*cx + t*t*x2;
            double ky = (1-t)*(1-t)*y1 + 2*(1-t)*t*cy + t*t*y2;
            GLine knot = new GLine(kx-4,ky-4,kx+4,ky+4);
            knot.setColor(ROPE_DARK); mainScreen.add(knot); ropeObjects.add(knot);
        }
    }

    private void clearRopes() {
        for (GObject o : ropeObjects) mainScreen.remove(o);
        ropeObjects.clear();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Meteor helpers
    // ════════════════════════════════════════════════════════════════════════
    private void spawnMeteor(int i, boolean randomY) {
        meteorX[i]      = W * (0.1 + Math.random() * 0.8);
        meteorY[i]      = randomY ? Math.random() * H * 0.6 : -20;
        meteorLen[i]    = 60 + Math.random() * 100;
        meteorSpeed[i]  = 4  + Math.random() * 6;
        meteorAlpha[i]  = 0f;
        meteorActive[i] = randomY && Math.random() > 0.5;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Animation
    // ════════════════════════════════════════════════════════════════════════
    private void animate() {
        tick++;

        // Twinkling star-field
        for (int i = 0; i < BG_STAR_COUNT; i++) {
            if (bgStars[i] == null) continue;
            bgStarAlpha[i] += bgStarSpeed[i] * (i%2==0 ? 1 : -1);
            if (bgStarAlpha[i] > 1f)  { bgStarAlpha[i]=1f;  bgStarSpeed[i]=-Math.abs(bgStarSpeed[i]); }
            if (bgStarAlpha[i] < 0f)  { bgStarAlpha[i]=0f;  bgStarSpeed[i]= Math.abs(bgStarSpeed[i]); }
            int a = Math.max(0,Math.min(255,(int)(bgStarAlpha[i]*200)));
            Color sc = new Color(200,210,255,a);
            bgStars[i].setFillColor(sc); bgStars[i].setColor(sc);
        }

        // Moon glow pulse
        if (moonGlow != null) {
            float mp = (float)(0.5 + 0.5*Math.sin(tick*0.02));
            moonGlow.setFillColor(new Color(255,245,200,(int)(12+mp*20)));
        }

        // Meteors
        for (int i = 0; i < METEOR_COUNT; i++) {
            if (meteorLines[i] == null) continue;
            if (!meteorActive[i]) { if (Math.random()<0.003) meteorActive[i]=true; continue; }
            meteorX[i] += meteorSpeed[i] * 1.4;
            meteorY[i] += meteorSpeed[i] * 0.6;
            meteorAlpha[i] = Math.min(1f, meteorAlpha[i]+0.08f);
            if (meteorX[i] > W*0.95 || meteorY[i] > H*0.85)
                meteorAlpha[i] = Math.max(0f, meteorAlpha[i]-0.15f);
            if (meteorAlpha[i] <= 0f && (meteorX[i]>W || meteorY[i]>H)) {
                spawnMeteor(i,false); continue;
            }
            int ma = (int)(meteorAlpha[i]*200);
            meteorLines[i].setColor(new Color(220,230,255,ma));
            meteorLines[i].setStartPoint(meteorX[i], meteorY[i]);
            meteorLines[i].setEndPoint(meteorX[i]-meteorLen[i]*0.9, meteorY[i]-meteorLen[i]*0.4);
        }

        // Level star glow pulse
        if (starGlows != null) {
            for (int i = 0; i < 9; i++) {
                if (starGlows[i] == null) continue;
                float p = (float)(0.5 + 0.5*Math.sin(tick*0.04 + i*0.7));
                int a = (int)(60 + p*90);
                boolean isTraining = (i == 0);
                if (isTraining) {
                    starGlows[i].setFillColor(new Color(TRAIN_COL.getRed(), TRAIN_COL.getGreen(), TRAIN_COL.getBlue(), a));
                    starGlows[i].setColor(new Color(TRAIN_COL.getRed(), TRAIN_COL.getGreen(), TRAIN_COL.getBlue(), a));
                } else {
                    starGlows[i].setFillColor(new Color(STAR_GLOW.getRed(), STAR_GLOW.getGreen(), STAR_GLOW.getBlue(), a));
                    starGlows[i].setColor(new Color(STAR_GLOW.getRed(), STAR_GLOW.getGreen(), STAR_GLOW.getBlue(), a));
                }
            }
        }

        // Current star indicator pulse
        if (currentIndicator != null) {
            float p = (float)(0.5 + 0.5*Math.sin(tick*0.07));
            currentIndicator.setColor(new Color(255,230,100,(int)(40+p*100)));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  hideContent
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void hideContent() {
        if (animTimer != null) { animTimer.stop(); animTimer = null; }
        if (currentIndicator != null) { mainScreen.remove(currentIndicator); currentIndicator = null; }
        removeAllStars();
        clearRopes();
        clearBackground();
        clearPreviewScreen();
        showLevelScreen = false;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Input
    // ════════════════════════════════════════════════════════════════════════
    @Override
    public void keyPressed(KeyEvent e) {
        if (showLevelScreen) return;
        if (e.getKeyCode() == KeyEvent.VK_D) moveForward();
        if (e.getKeyCode() == KeyEvent.VK_A) moveBackward();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (showLevelScreen) {
            if (startButton != null && startButton.contains(e.getX(), e.getY())) {
                launchLevel(selectedLevel); return;
            }
            if (backButton != null && backButton.contains(e.getX(), e.getY())) {
                clearPreviewScreen(); showLevelScreen = false; return;
            }
            return;
        }
        int prog = mainScreen.getCurrentLevel();
        for (int i = 0; i < 9; i++) {
            if (stars[i] != null && stars[i].contains(e.getX(), e.getY()) && prog >= i) {
                currentPoint  = i;
                selectedLevel = i;
                highlightCurrentStar();
                showLevelPreview();
                return;
            }
        }
    }

    private void launchLevel(int level) {
        switch (level) {
            case 0:
                /* training */
                break;
            case 1:
                mainScreen.switchToSecondBattleScreen();
                break;
            case 2:
                mainScreen.switchToCutScene3Screen();
                break;
            case 3:
                mainScreen.switchToFourthBattleScreen();
                break;
            case 4:
                mainScreen.switchToCutScene4Screen();
                break;
            case 5:
                mainScreen.switchToSixthBattleScreen();
                break;
            case 6:
                mainScreen.switchToCutScene5Screen();
                break;
            case 7:
                mainScreen.switchToEighthBattleScreen();
                break;
            case 8:
                mainScreen.switchToCutScene6Screen();   // final preview -> CutScene6 -> NinethBattle
                break;
        }
    }
    
    // ════════════════════════════════════════════════════════════════════════
    //  Movement
    // ════════════════════════════════════════════════════════════════════════
    private void moveForward() {
        int max = mainScreen.getCurrentLevel();
        if (currentPoint < max && currentPoint < 8) {
            currentPoint++;
            selectedLevel = currentPoint;
            highlightCurrentStar();
            showLevelPreview();
        }
    }

    private void moveBackward() {
        if (currentPoint > 0) {
            currentPoint--;
            highlightCurrentStar();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Level preview panel
    // ════════════════════════════════════════════════════════════════════════
    private void showLevelPreview() {
        clearPreviewScreen();
        showLevelScreen = true;

        Color mainColor, glowColor, darkColor;
        String enemyName;
        String displayName;

        switch (selectedLevel) {
            case 0: mainColor=new Color(100,230,220); glowColor=new Color( 70,200,190); darkColor=new Color(10,50,48); enemyName="";             displayName="Training";  break;
            case 1: mainColor=new Color(  0,255,220); glowColor=new Color(  0,200,170); darkColor=new Color( 0,60,55); enemyName="mint.png";       displayName="Level  1";  break;
            case 2: mainColor=new Color( 11, 95, 90); glowColor=new Color( 19,115,109); darkColor=new Color( 4,35,32); enemyName="slujupiter.png"; displayName="Level  2";  break;
            case 3: mainColor=new Color(170,100,255); glowColor=new Color(130, 70,220); darkColor=new Color(50,15,90); enemyName="lavender.png";   displayName="Level  3";  break;
            case 4: mainColor=new Color(190,140,255); glowColor=new Color(145,100,220); darkColor=new Color(60,22,95); enemyName="effervena.png";  displayName="Level  4";  break;
            case 5: mainColor=new Color(255,120,120); glowColor=new Color(210, 90, 90); darkColor=new Color(90,18,18); enemyName="brick.png";               displayName="Level  5";  break;
            case 6: mainColor=new Color(255, 80, 80); glowColor=new Color(200, 50, 50); darkColor=new Color(80,12,12); enemyName="Decima.png";               displayName="Level  6";  break;
            case 7: mainColor=new Color(120,180,255); glowColor=new Color( 80,140,220); darkColor=new Color(15,45,100);enemyName="mint.png";               displayName="Level  7";  break;
            default:mainColor=new Color( 80,120,255); glowColor=new Color( 50, 90,220); darkColor=new Color(12,28,85); enemyName="loathe.png";               displayName="Level  8";  break;
        }

        boolean isTraining = (selectedLevel == 0);
        boolean isBoss     = (!isTraining && selectedLevel % 2 == 0);
        String  typeText   = isTraining ? "TRAINING" : (isBoss ? "BOSS" : "MINION");

        int    panelW = 420;
        int    panelH = 540;
        double panelX = W - panelW - 80;
        double panelY = (H - panelH) / 2.0;

        // Layered outer glow
        for (int g = 3; g >= 1; g--) {
            GRect glow = new GRect(panelX-g*5, panelY-g*5, panelW+g*10, panelH+g*10);
            glow.setFilled(true);
            glow.setFillColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),12-g*3));
            glow.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),60-g*15));
            addPrev(glow);
        }

        GRect shadow = new GRect(panelX+6,panelY+6,panelW,panelH);
        shadow.setFilled(true); shadow.setFillColor(new Color(0,0,0,140)); shadow.setColor(new Color(0,0,0,0));
        addPrev(shadow);

        GRect panel = new GRect(panelX,panelY,panelW,panelH);
        panel.setFilled(true); panel.setFillColor(darkColor); panel.setColor(mainColor);
        addPrev(panel);

        GRect inner = new GRect(panelX+5,panelY+5,panelW-10,panelH-10);
        inner.setFilled(false);
        inner.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),40));
        addPrev(inner);

        GRect topBar = new GRect(panelX,panelY,panelW,5);
        topBar.setFilled(true); topBar.setFillColor(mainColor); topBar.setColor(mainColor);
        addPrev(topBar);

        // Corner diamonds
        double[][] corners = {{panelX,panelY},{panelX+panelW,panelY},
                              {panelX,panelY+panelH},{panelX+panelW,panelY+panelH}};
        for (double[] c : corners) {
            GPolygon d = makeDiamond(c[0],c[1],9);
            d.setFilled(true);
            d.setFillColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),180));
            d.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),240));
            addPrev(d);
        }

        // Badge
        GRect badge = new GRect(panelX+18,panelY+20,isTraining?90:80,24);
        badge.setFilled(true); badge.setFillColor(mainColor); badge.setColor(mainColor);
        addPrev(badge);
        GLabel badgeTxt = new GLabel(typeText);
        badgeTxt.setFont("Arial-Bold-12");
        badgeTxt.setColor(darkColor);
        badgeTxt.setLocation(panelX+24, panelY+36);
        addPrev(badgeTxt);

        // Title
        GLabel tGlow = new GLabel(displayName);
        tGlow.setFont("Arial-Bold-44"); tGlow.setColor(glowColor);
        tGlow.setLocation(panelX+18, panelY+96); addPrev(tGlow);

        GLabel tLabel = new GLabel(displayName);
        tLabel.setFont("Arial-Bold-44"); tLabel.setColor(mainColor);
        tLabel.setLocation(panelX+20, panelY+94); addPrev(tLabel);

        GLine div = new GLine(panelX+18,panelY+106,panelX+panelW-18,panelY+106);
        div.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),70));
        addPrev(div);

        // Enemy image area
        double imgAreaX = panelX+20, imgAreaY = panelY+118;
        double imgAreaW = panelW-40, imgAreaH  = 230;

        GRect imgBg = new GRect(imgAreaX,imgAreaY,imgAreaW,imgAreaH);
        imgBg.setFilled(true);
        imgBg.setFillColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),12));
        imgBg.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),50));
        addPrev(imgBg);

        if (!enemyName.isEmpty()) {
            levelEnemyImage = new GImage(enemyName);
            double scaleX = (imgAreaW-20)/levelEnemyImage.getWidth();
            double scaleY = (imgAreaH-20)/levelEnemyImage.getHeight();
            double scale  = Math.min(scaleX, scaleY);
            if (scale < 1.0) levelEnemyImage.scale(scale);
            levelEnemyImage.setLocation(
                imgAreaX + (imgAreaW - levelEnemyImage.getWidth())  / 2.0,
                imgAreaY + (imgAreaH - levelEnemyImage.getHeight()) / 2.0);
            addPrev(levelEnemyImage);
        } else if (isTraining) {
            // Training placeholder: "PRACTICE" label
            GLabel pl = new GLabel("PRACTICE");
            pl.setFont("Arial-Bold-24");
            pl.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),100));
            pl.setLocation(imgAreaX+(imgAreaW-pl.getWidth())/2.0, imgAreaY+imgAreaH/2.0-10);
            addPrev(pl);
            GLabel pl2 = new GLabel("BATTLE");
            pl2.setFont("Arial-Bold-24");
            pl2.setColor(pl.getColor());
            pl2.setLocation(imgAreaX+(imgAreaW-pl2.getWidth())/2.0, imgAreaY+imgAreaH/2.0+24);
            addPrev(pl2);
        } else {
            GLabel cs1 = new GLabel("COMING");
            cs1.setFont("Arial-Bold-26");
            cs1.setColor(new Color(mainColor.getRed(),mainColor.getGreen(),mainColor.getBlue(),100));
            cs1.setLocation(imgAreaX+(imgAreaW-cs1.getWidth())/2.0, imgAreaY+imgAreaH/2.0-10);
            addPrev(cs1);
            GLabel cs2 = new GLabel("SOON");
            cs2.setFont("Arial-Bold-26"); cs2.setColor(cs1.getColor());
            cs2.setLocation(imgAreaX+(imgAreaW-cs2.getWidth())/2.0, imgAreaY+imgAreaH/2.0+26);
            addPrev(cs2);
        }

        // Buttons
        double btnX = panelX+25, btnW = panelW-50, btnH = 52;
        addButton(btnX, panelY+panelH-138, btnW, btnH, "START", mainColor, glowColor, darkColor, true);
        addButton(btnX, panelY+panelH- 74, btnW, btnH, "BACK",
                  new Color(100,110,150), new Color(130,140,180), new Color(18,20,34), false);
    }

    private void addButton(double x, double y, double w, double h,
                           String label, Color col, Color glow, Color dark, boolean isPrimary) {
        GRect halo = new GRect(x-4,y-4,w+8,h+8);
        halo.setFilled(true);
        halo.setFillColor(new Color(col.getRed(),col.getGreen(),col.getBlue(),50));
        halo.setColor(new Color(0,0,0,0));
        addPrev(halo);

        GRect btn = new GRect(x,y,w,h);
        btn.setFilled(true);
        btn.setFillColor(isPrimary ? dark : new Color(20,22,36));
        btn.setColor(col);
        addPrev(btn);

        GLabel lblG = new GLabel(label);
        lblG.setFont("Arial-Bold-20"); lblG.setColor(glow);
        double lx = x+(w-lblG.getWidth())/2.0, ly = y+34;
        lblG.setLocation(lx-1,ly-1); addPrev(lblG);

        GLabel lbl = new GLabel(label);
        lbl.setFont("Arial-Bold-20"); lbl.setColor(col);
        lbl.setLocation(lx,ly); addPrev(lbl);

        if (isPrimary) startButton = btn;
        else           backButton  = btn;
    }

    private void addPrev(GObject obj) { mainScreen.add(obj); previewObjects.add(obj); }

    private void clearPreviewScreen() {
        for (GObject o : previewObjects) mainScreen.remove(o);
        previewObjects.clear();
        startButton = null; backButton = null; levelEnemyImage = null;
    } //test

    // ════════════════════════════════════════════════════════════════════════
    //  Star removal
    // ════════════════════════════════════════════════════════════════════════
    private void removeAllStars() {
        if (stars        != null) for (GPolygon s : stars)        if (s!=null) mainScreen.remove(s);
        if (starGlows    != null) for (GPolygon s : starGlows)    if (s!=null) mainScreen.remove(s);
        if (levelNumbers != null) for (GLabel   l : levelNumbers) if (l!=null) mainScreen.remove(l);
        if (bossLabels   != null) for (GLabel   l : bossLabels)   if (l!=null) mainScreen.remove(l);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════════
    private GPolygon createStar(double cx, double cy, double outerR, double innerR) {
        GPolygon star = new GPolygon();
        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i*36);
            double r     = (i%2==0) ? outerR : innerR;
            star.addVertex(r*Math.cos(angle), r*Math.sin(angle));
        }
        star.setFilled(true);
        star.setFillColor(Color.BLACK);
        star.setColor(Color.DARK_GRAY);
        star.setLocation(cx, cy);
        return star;
    }

    private GPolygon makeDiamond(double cx, double cy, double r) {
        GPolygon d = new GPolygon();
        d.addVertex(0,-r); d.addVertex(r,0); d.addVertex(0,r); d.addVertex(-r,0);
        d.setLocation(cx, cy);
        return d;
    }

    private Color getPlayerColor() {
        String sel = mainScreen.getSelectedColor();
        if (sel == null)         return new Color(255,220,60);
        if (sel.equals("red"))   return new Color(255, 80,80);
        if (sel.equals("green")) return new Color( 80,255,80);
        return new Color(80,160,255);
    }
}

//tres30
