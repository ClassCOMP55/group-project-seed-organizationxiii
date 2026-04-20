import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import characters.Decima;
import characters.Hueman;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FirstBattlePane extends GraphicsPane {

    // ── Characters ────────────────────────────────────────────────────────────
    private Hueman h1;
    private Decima d1;
    private double huemanMaxHP;
    private double decimaMaxHP;

    // ── Timers ────────────────────────────────────────────────────────────────
    private javax.swing.Timer bombTimer;
    private javax.swing.Timer hpDrainTimer;
    private javax.swing.Timer shieldTimer;
    private javax.swing.Timer animTimer;
    private javax.swing.Timer flinchTimer;

    // ── Graphics objects ──────────────────────────────────────────────────────
    private ArrayList<GOval> bombParticles = new ArrayList<>();
    private GImage  huemanImage;
    private GImage  decimaImage;
    private GRect   shield;
    private GRect   skyPanel;

    // HUD — enemy
    private GRect   enemyHudPanel;
    private GRect   enemyHudAccent;
    private GLabel  decimaNameLabel;
    private GRect   decimaHealthBack;
    private GRect   decimaHealthBar;
    private GLabel  decimaHpLabel;

    // HUD — player
    private GRect   hudPanel;
    private GRect   hudAccent;
    private GLabel  huemanNameLabel;
    private GRect   huemanHealthBack;
    private GRect   huemanHealthBar;
    private GLabel  hpValueLabel;
    private GRect   superBack;
    private GRect   superBar;
    private GLabel  superReadyLabel;

    // Action menu
    private GRect   menuPanel;
    private GRect   menuInnerBorder;
    private GLabel  fightOption;
    private GRect   fightHighlight;
    private GLabel  superOption;
    private GRect   superHighlight;

    // Continue button
    private GRect   continueButton;
    private GLabel  continueLabel;
    private GRect   continueGlow;

    // Overlay
    private GRect   overlayShade;
    private GRect   overlayPanel;
    private GRect   overlayInnerBorder;
    private GLabel  overlayTitle;
    private GLabel  overlaySubtitle;
    private GImage  overlayImage;
    private GRect[] targetButtons = new GRect[3];
    private GLabel[] targetLabels = new GLabel[3];
    private String[] targetNames  = {"LEGS", "MIDDLE", "HEAD"};

    // Projectile & message
    private GOval   projectile;
    private GLabel  battleMessageLabel;

    // Overlay object lists
    private ArrayList<GObject> overlayObjects    = new ArrayList<>();
    private ArrayList<GObject> endOverlayObjects = new ArrayList<>();

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean playerTurn            = true;
    private boolean battleOver            = false;
    private boolean animating             = false;
    private boolean selectionOverlayOpen  = false;
    private boolean choosingPlayerAttack  = false;
    private boolean choosingPlayerDefense = false;
    private boolean superReady            = false;

    // ── Enemy AI state ────────────────────────────────────────────────────────
    private ArrayList<String> playerHistory = new ArrayList<>();
    private boolean decimaPhase2 = false;

    // ── Colours ───────────────────────────────────────────────────────────────
    private static final Color BG_TOP       = new Color(10, 12, 28);
    private static final Color PANEL_BG     = new Color(12, 15, 35, 220);
    private static final Color PANEL_BORDER = new Color(80, 100, 180);
    private static final Color ACCENT_GOLD  = new Color(212, 175, 55);
    private static final Color HP_RED       = new Color(220, 55, 55);
    private static final Color HP_GREEN     = new Color(55, 220, 100);
    private static final Color HP_BLUE      = new Color(55, 140, 255);
    private static final Color SUPER_COLOR  = new Color(130, 60, 255);
    private static final Color ENEMY_HP     = new Color(212, 175, 55);
    private static final Color TEXT_BRIGHT  = new Color(230, 235, 255);
    private static final Color MENU_BG      = new Color(8, 10, 25, 240);
    private static final Color HIGHLIGHT    = new Color(55, 75, 180, 80);

    // ── Layout ────────────────────────────────────────────────────────────────
    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;
    private double scale, offsetX, offsetY;

    // ── Super constants ───────────────────────────────────────────────────────
    private static final double SUPER_MAX        = 500.0;
    private static final double SUPER_GAIN_HIT   = 60.0;
    private static final double SUPER_GAIN_BLOCK = 40.0;
    private static final double SUPER_GAIN_DMGTAKEN = 30.0;

    // =========================================================================
    // Constructor
    // =========================================================================

    public FirstBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    // =========================================================================
    // showContent / hideContent
    // =========================================================================

    @Override
    public void showContent() {
        updateLayoutScale();

        h1 = mainScreen.getPlayer();
        d1 = new Decima(600, 20, 10, "Decima", Decima.Phase.FIRST);
        huemanMaxHP = h1.getHP();
        decimaMaxHP = d1.getHP();

        playerTurn            = true;
        battleOver            = false;
        animating             = false;
        selectionOverlayOpen  = false;
        choosingPlayerAttack  = false;
        choosingPlayerDefense = false;
        superReady            = false;
        decimaPhase2          = false;
        playerHistory.clear();
        h1.setSuperMeter(0);

        buildBackground();
        drawBattleFrame();
        buildSprites();
        buildEnemyHUD();
        buildPlayerHUD();
        buildActionMenu();
        buildContinueButton();
        buildBattleMessage();
        updateHealthBars();
        setBattleMessage("Choose FIGHT to start the duel.");
    }

    @Override
    public void hideContent() {
        stopAllTimers();
        clearOverlay();
        clearEndOverlay();
        for (GObject obj : contents) mainScreen.remove(obj);
        contents.clear();
    }

    private void stopAllTimers() {
        if (bombTimer    != null) { bombTimer.stop();    bombTimer    = null; }
        if (hpDrainTimer != null) { hpDrainTimer.stop(); hpDrainTimer = null; }
        if (shieldTimer  != null) { shieldTimer.stop();  shieldTimer  = null; }
        if (animTimer    != null) { animTimer.stop();    animTimer    = null; }
        if (flinchTimer  != null) { flinchTimer.stop();  flinchTimer  = null; }
    }

    // =========================================================================
    // Layout helpers
    // =========================================================================

    private void updateLayoutScale() {
        double aW = mainScreen.getWidth(),  aH = mainScreen.getHeight();
        if (aW <= 0) aW = MainApplication.WINDOW_WIDTH;
        if (aH <= 0) aH = MainApplication.WINDOW_HEIGHT;
        scale   = Math.min(aW / BASE_W, aH / BASE_H);
        offsetX = (aW - BASE_W * scale) / 2.0;
        offsetY = (aH - BASE_H * scale) / 2.0;
    }

    private double X(double v) { return offsetX + v * scale; }
    private double Y(double v) { return offsetY + v * scale; }
    private double S(double v) { return v * scale; }
    private double screenW() { double w = mainScreen.getWidth();  return w > 0 ? w : MainApplication.WINDOW_WIDTH;  }
    private double screenH() { double h = mainScreen.getHeight(); return h > 0 ? h : MainApplication.WINDOW_HEIGHT; }

    // =========================================================================
    // Body-part targeting
    // =========================================================================

    private double getTargetY(GImage sprite, String part) {
        double top = sprite.getY(), h = sprite.getHeight();
        if (part.equals("HEAD"))   return top + h * 0.15;
        if (part.equals("MIDDLE")) return top + h * 0.50;
        return top + h * 0.82;  // LEGS
    }

    private double getTargetX(GImage sprite) {
        return sprite.getX() + sprite.getWidth() / 2.0;
    }

    private void goToNextScreenAfterDelay() {
        javax.swing.Timer nextTimer = new javax.swing.Timer(4000, null); // 4 seconds
        nextTimer.setRepeats(false);
        nextTimer.addActionListener(e -> {
            nextTimer.stop();
            mainScreen.switchToCutScene2Screen();
        });
        nextTimer.start();
    }
    // =========================================================================
    // Background & frame
    // =========================================================================

    private void buildBackground() {
        double W = screenW(), H = screenH();
        skyPanel = new GRect(0, 0, W, H);
        skyPanel.setFilled(true); skyPanel.setFillColor(BG_TOP); skyPanel.setColor(BG_TOP);
        addObj(skyPanel);
        for (int i = 0; i < 2; i++) {
            GLine sl = new GLine(X(760+i*6), Y(150), X(840+i*24), Y(930));
            sl.setColor(new Color(60,80,160,35)); addObj(sl);
        }
    }

    private void drawBattleFrame() {
        double W = screenW(), H = screenH(), m1 = S(20), m2 = S(30);
        GRect outer = new GRect(m1,m1,W-2*m1,H-2*m1); outer.setFilled(false); outer.setColor(ACCENT_GOLD); addObj(outer);
        GRect inner = new GRect(m2,m2,W-2*m2,H-2*m2); inner.setFilled(false); inner.setColor(new Color(90,110,200)); addObj(inner);
        addCornerFrame(m2,m2,true,true); addCornerFrame(W-m2,m2,false,true);
        addCornerFrame(m2,H-m2,true,false); addCornerFrame(W-m2,H-m2,false,false);
    }

    private void addCornerFrame(double x, double y, boolean left, boolean top) {
        double ll=S(30),sl=S(15),g=S(7);
        GLine h1=new GLine(x,y,x+(left?ll:-ll),y); GLine v1=new GLine(x,y,x,y+(top?ll:-ll));
        GLine h2=new GLine(x+(left?g:-g),y+(top?g:-g),x+(left?sl:-sl),y+(top?g:-g));
        GLine v2=new GLine(x+(left?g:-g),y+(top?g:-g),x+(left?g:-g),y+(top?sl:-sl));
        h1.setColor(ACCENT_GOLD); v1.setColor(ACCENT_GOLD);
        h2.setColor(new Color(212,175,55,150)); v2.setColor(new Color(212,175,55,150));
        addObj(h1); addObj(v1); addObj(h2); addObj(v2);
    }

    // =========================================================================
    // Sprites
    // =========================================================================

    private void buildSprites() {
        huemanImage = new GImage(getHuemanImage()); decimaImage = new GImage("Decima.png");
        huemanImage.scale(0.52); decimaImage.scale(0.78);

        double hStoneW=S(170),hStoneH=S(35),hCX=X(340),hSY=Y(700);
        GOval hSh=new GOval(hCX-hStoneW*.45,hSY+hStoneH*.75,hStoneW*.9,hStoneH*.5);
        hSh.setFilled(true); hSh.setFillColor(new Color(0,0,0,70)); hSh.setColor(new Color(0,0,0,0)); addObj(hSh);
        GRect hSb2=new GRect(hCX-hStoneW/2-S(6),hSY+S(8),hStoneW+S(12),hStoneH*.55);
        hSb2.setFilled(true); hSb2.setFillColor(new Color(42,38,55)); hSb2.setColor(new Color(28,25,40)); addObj(hSb2);
        GRect hSb1=new GRect(hCX-hStoneW/2,hSY,hStoneW,hStoneH);
        hSb1.setFilled(true); hSb1.setFillColor(new Color(92,84,116)); hSb1.setColor(new Color(58,52,76)); addObj(hSb1);
        huemanImage.setLocation(hCX-huemanImage.getWidth()/2+S(110), hSY-huemanImage.getHeight()-S(8)); addObj(huemanImage);

        double dStoneW=S(190),dStoneH=S(36),dCX=X(1120),dSY=Y(480);
        GOval dSh=new GOval(dCX-dStoneW*.46,dSY+dStoneH*.75,dStoneW*.92,dStoneH*.5);
        dSh.setFilled(true); dSh.setFillColor(new Color(0,0,0,70)); dSh.setColor(new Color(0,0,0,0)); addObj(dSh);
        GRect dSb2=new GRect(dCX-dStoneW/2-S(6),dSY+S(8),dStoneW+S(12),dStoneH*.55);
        dSb2.setFilled(true); dSb2.setFillColor(new Color(55,44,22)); dSb2.setColor(new Color(38,30,12)); addObj(dSb2);
        GRect dSb1=new GRect(dCX-dStoneW/2,dSY,dStoneW,dStoneH);
        dSb1.setFilled(true); dSb1.setFillColor(new Color(120,95,42)); dSb1.setColor(new Color(76,60,22)); addObj(dSb1);
        decimaImage.setLocation(dCX-decimaImage.getWidth()/2, dSY-decimaImage.getHeight()+S(10)); addObj(decimaImage);
    }

    // =========================================================================
    // HUD — enemy
    // =========================================================================

    private void buildEnemyHUD() {
        double px=X(30),py=Y(38),pw=S(290),ph=S(85);
        GRect sh=new GRect(px+S(4),py+S(4),pw,ph); sh.setFilled(true); sh.setFillColor(new Color(0,0,0,120)); sh.setColor(new Color(0,0,0,0)); addObj(sh);
        enemyHudPanel=new GRect(px,py,pw,ph); enemyHudPanel.setFilled(true); enemyHudPanel.setFillColor(PANEL_BG); enemyHudPanel.setColor(PANEL_BORDER); addObj(enemyHudPanel);
        enemyHudAccent=new GRect(px,py,S(5),ph); enemyHudAccent.setFilled(true); enemyHudAccent.setFillColor(ACCENT_GOLD); enemyHudAccent.setColor(ACCENT_GOLD); addObj(enemyHudAccent);
        decimaNameLabel=new GLabel(d1.getName().toUpperCase()); decimaNameLabel.setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(28)))); decimaNameLabel.setColor(ACCENT_GOLD); decimaNameLabel.setLocation(px+S(18),py+ph*.35); addObj(decimaNameLabel);
        decimaHpLabel=new GLabel("HP"); decimaHpLabel.setFont(new Font("Courier New",Font.BOLD,Math.max(10,(int)S(14)))); decimaHpLabel.setColor(TEXT_BRIGHT); decimaHpLabel.setLocation(px+S(18),py+ph*.70); addObj(decimaHpLabel);
        double bx=px+pw*.18,by=py+ph*.52,bw=pw*.68,bh=ph*.18;
        decimaHealthBack=new GRect(bx,by,bw,bh); decimaHealthBack.setFilled(true); decimaHealthBack.setFillColor(new Color(30,30,50)); decimaHealthBack.setColor(new Color(60,60,90)); addObj(decimaHealthBack);
        decimaHealthBar=new GRect(bx,by,bw,bh); decimaHealthBar.setFilled(true); decimaHealthBar.setFillColor(ENEMY_HP); decimaHealthBar.setColor(ENEMY_HP.darker()); addObj(decimaHealthBar);
    }

    // =========================================================================
    // HUD — player
    // =========================================================================

    private void buildPlayerHUD() {
        double W=screenW(),H=screenH(),pw=S(400),ph=S(100),px=W-pw-S(460),py=H-ph-S(100);
        GRect sh=new GRect(px+S(4),py+S(4),pw,ph); sh.setFilled(true); sh.setFillColor(new Color(0,0,0,120)); sh.setColor(new Color(0,0,0,0)); addObj(sh);
        hudPanel=new GRect(px,py,pw,ph); hudPanel.setFilled(true); hudPanel.setFillColor(PANEL_BG); hudPanel.setColor(PANEL_BORDER); addObj(hudPanel);
        Color ac=getPlayerColor();
        hudAccent=new GRect(px,py,S(6),ph); hudAccent.setFilled(true); hudAccent.setFillColor(ac); hudAccent.setColor(ac); addObj(hudAccent);
        huemanNameLabel=new GLabel(h1.getName().toUpperCase()); huemanNameLabel.setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(28)))); huemanNameLabel.setColor(ac.brighter()); huemanNameLabel.setLocation(px+S(18),py+ph*.30); addObj(huemanNameLabel);
        GLabel hpTag=new GLabel("HP"); hpTag.setFont(new Font("Courier New",Font.BOLD,Math.max(10,(int)S(14)))); hpTag.setColor(TEXT_BRIGHT); hpTag.setLocation(px+S(18),py+ph*.58); addObj(hpTag);
        double bx=px+pw*.16,by=py+ph*.44,bw=pw*.62,bh=ph*.14;
        huemanHealthBack=new GRect(bx,by,bw,bh); huemanHealthBack.setFilled(true); huemanHealthBack.setFillColor(new Color(30,30,50)); huemanHealthBack.setColor(new Color(60,60,90)); addObj(huemanHealthBack);
        huemanHealthBar=new GRect(bx,by,bw,bh); huemanHealthBar.setFilled(true); huemanHealthBar.setFillColor(ac); huemanHealthBar.setColor(ac.darker()); addObj(huemanHealthBar);
        hpValueLabel=new GLabel((int)h1.getHP()+" / "+(int)huemanMaxHP); hpValueLabel.setFont(new Font("Courier New",Font.BOLD,Math.max(10,(int)S(14)))); hpValueLabel.setColor(TEXT_BRIGHT); hpValueLabel.setLocation(px+pw*.80,py+ph*.58); addObj(hpValueLabel);
        GLabel spTag=new GLabel("SP"); spTag.setFont(new Font("Courier New",Font.BOLD,Math.max(10,(int)S(14)))); spTag.setColor(new Color(180,140,255)); spTag.setLocation(px+S(18),py+ph*.82); addObj(spTag);
        double sxB=px+pw*.16,syB=py+ph*.68,sw=pw*.62,sh2=ph*.12;
        superBack=new GRect(sxB,syB,sw,sh2); superBack.setFilled(true); superBack.setFillColor(new Color(30,20,50)); superBack.setColor(new Color(70,50,100)); addObj(superBack);
        superBar=new GRect(sxB,syB,0,sh2); superBar.setFilled(true); superBar.setFillColor(SUPER_COLOR); superBar.setColor(SUPER_COLOR.darker()); addObj(superBar);
        superReadyLabel=new GLabel("★ SUPER READY ★"); superReadyLabel.setFont(new Font("Georgia",Font.BOLD,Math.max(10,(int)S(14)))); superReadyLabel.setColor(new Color(200,160,255,0)); superReadyLabel.setLocation(sxB,syB-S(4)); addObj(superReadyLabel);
    }

    // =========================================================================
    // Action menu  (FIGHT + SUPER)
    // =========================================================================

    private void buildActionMenu() {
        double H=screenH(),mw=S(210),mh=S(160),mx=X(30),my=H-mh-S(45);
        GRect sh=new GRect(mx+S(5),my+S(5),mw,mh); sh.setFilled(true); sh.setFillColor(new Color(0,0,0,130)); sh.setColor(new Color(0,0,0,0)); addObj(sh);
        menuPanel=new GRect(mx,my,mw,mh); menuPanel.setFilled(true); menuPanel.setFillColor(MENU_BG); menuPanel.setColor(PANEL_BORDER); addObj(menuPanel);
        menuInnerBorder=new GRect(mx+S(6),my+S(6),mw-S(12),mh-S(12)); menuInnerBorder.setFilled(false); menuInnerBorder.setColor(new Color(212,175,55,90)); addObj(menuInnerBorder);
        addCornerAccent(mx+S(4),my+S(4),true,true); addCornerAccent(mx+mw-S(4),my+S(4),false,true);
        addCornerAccent(mx+S(4),my+mh-S(4),true,false); addCornerAccent(mx+mw-S(4),my+mh-S(4),false,false);
        // FIGHT
        fightHighlight=new GRect(mx+S(10),my+S(18),mw-S(20),mh*0.22); fightHighlight.setFilled(true); fightHighlight.setFillColor(HIGHLIGHT); fightHighlight.setColor(new Color(0,0,0,0)); addObj(fightHighlight);
        fightOption=new GLabel("FIGHT"); fightOption.setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(22)))); fightOption.setColor(TEXT_BRIGHT); fightOption.setLocation(mx+S(20),my+S(18)+mh*0.17); addObj(fightOption);
        // SUPER (dimmed until meter full)
        superHighlight=new GRect(mx+S(10),my+S(18)+mh*0.42,mw-S(20),mh*0.22); superHighlight.setFilled(true); superHighlight.setFillColor(new Color(80,40,160,40)); superHighlight.setColor(new Color(0,0,0,0)); addObj(superHighlight);
        superOption=new GLabel("SUPER"); superOption.setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(22)))); superOption.setColor(new Color(130,60,255,90)); superOption.setLocation(mx+S(20),my+S(18)+mh*0.42+mh*0.17); addObj(superOption);
    }

    private void addCornerAccent(double x, double y, boolean left, boolean top) {
        double len=S(10); GLine h=new GLine(x,y,x+(left?len:-len),y); GLine v=new GLine(x,y,x,y+(top?len:-len));
        h.setColor(ACCENT_GOLD); v.setColor(ACCENT_GOLD); addObj(h); addObj(v);
    }

    // =========================================================================
    // Continue button
    // =========================================================================

    private void buildContinueButton() {
        double W=screenW(),H=screenH(),bw=S(180),bh=S(48),bx=W-bw-S(35),by=H-bh-S(42);
        continueGlow=new GRect(bx-S(4),by-S(4),bw+S(8),bh+S(8)); continueGlow.setFilled(true); continueGlow.setFillColor(new Color(212,175,55,28)); continueGlow.setColor(new Color(212,175,55,60)); addObj(continueGlow);
        continueButton=new GRect(bx,by,bw,bh); continueButton.setFilled(true); continueButton.setFillColor(new Color(55,40,10)); continueButton.setColor(ACCENT_GOLD); addObj(continueButton);
        continueLabel=new GLabel("CONTINUE  ▶"); continueLabel.setFont(new Font("Georgia",Font.BOLD,Math.max(11,(int)S(16)))); continueLabel.setColor(ACCENT_GOLD); continueLabel.setLocation(bx+bw*.12,by+bh*.65); addObj(continueLabel);
    }

    // =========================================================================
    // Battle message
    // =========================================================================

    private void buildBattleMessage() {
        battleMessageLabel=new GLabel(""); battleMessageLabel.setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(20)))); battleMessageLabel.setColor(TEXT_BRIGHT); battleMessageLabel.setLocation(X(450),Y(100)); addObj(battleMessageLabel);
    }

    private void setBattleMessage(String msg) {
        if (battleMessageLabel!=null) { battleMessageLabel.setLabel(msg); battleMessageLabel.setLocation(X(450),Y(100)); }
    }

    // =========================================================================
    // Super meter
    // =========================================================================

    private void gainSuper(double amount) {
        if (battleOver) return;
        double next = Math.min(SUPER_MAX, h1.getSuperMeter()+amount);
        h1.setSuperMeter((int)next);
        boolean wasFull = superReady;
        superReady = (next >= SUPER_MAX);
        updateSuperMeter();
        if (superReady && !wasFull) {
            superOption.setColor(new Color(210,170,255));
            pulseSuperLabel();
        }
    }

    private void pulseSuperLabel() {
        if (superReadyLabel==null) return;
        int[] fr={0}; int total=30;
        javax.swing.Timer t=new javax.swing.Timer(40,null);
        t.addActionListener(e->{
            fr[0]++;
            double p=(double)fr[0]/total;
            int alpha = p<0.3?(int)(255*(p/0.3)) : p<0.7?255:(int)(255*((1-p)/0.3));
            superReadyLabel.setColor(new Color(200,160,255,Math.max(0,Math.min(255,alpha))));
            if(fr[0]>=total){t.stop();superReadyLabel.setColor(new Color(200,160,255,superReady?160:0));}
        });
        t.start();
    }

    // =========================================================================
    // Enemy AI
    // =========================================================================

    /**
     * Attack choice: normal phase prefers HEAD; phase 2 (<40% HP) is more balanced.
     * Adaptation: if player defended HEAD twice in a row, attack LEGS instead.
     */
    private String aiChooseAttack() {
        decimaPhase2 = (double)d1.getHP()/decimaMaxHP < 0.40;
        if (playerHistory.size()>=2) {
            String l1=playerHistory.get(playerHistory.size()-1);
            String l2=playerHistory.get(playerHistory.size()-2);
            if (l1.equals("HEAD")&&l2.equals("HEAD")) return "LEGS";
        }
        double r=Math.random();
        if (!decimaPhase2) { if(r<0.60)return"HEAD"; if(r<0.85)return"MIDDLE"; return"LEGS"; }
        else               { if(r<0.40)return"HEAD"; if(r<0.80)return"MIDDLE"; return"LEGS"; }
    }

    /**
     * Defence choice: predict player's most frequent recent attack zone.
     * In phase 2 has 35% random chance to keep player guessing.
     */
    private String aiChooseDefense() {
        if (playerHistory.isEmpty()) return randPart();
        if (decimaPhase2&&Math.random()<0.35) return randPart();
        int head=0,mid=0,legs=0,look=Math.min(4,playerHistory.size());
        for (int i=playerHistory.size()-look;i<playerHistory.size();i++) {
            String s=playerHistory.get(i);
            if(s.equals("HEAD"))head++; else if(s.equals("MIDDLE"))mid++; else legs++;
        }
        if(head>=mid&&head>=legs)return"HEAD";
        if(mid>=head&&mid>=legs)return"MIDDLE";
        return"LEGS";
    }

    private String randPart() {
        double r=Math.random(); if(r<0.33)return"HEAD"; if(r<0.67)return"MIDDLE"; return"LEGS";
    }

    private void recordHistory(String part) {
        playerHistory.add(part);
        if (playerHistory.size()>6) playerHistory.remove(0);
    }

    // =========================================================================
    // Flinch animation
    // =========================================================================

    /** Shakes a sprite horizontally then calls onDone. */
    private void flinchSprite(GImage sprite, Runnable onDone) {
        double ox=sprite.getX(), oy=sprite.getY();
        int[] fr={0}; int total=18; double amp=S(10);
        flinchTimer=new javax.swing.Timer(16,null);
        flinchTimer.addActionListener(evt->{
            fr[0]++;
            double t=(double)fr[0]/total;
            sprite.setLocation(ox+Math.sin(fr[0]*1.8)*amp*(1-t), oy);
            if(fr[0]>=total){flinchTimer.stop();sprite.setLocation(ox,oy);if(onDone!=null)onDone.run();}
        });
        flinchTimer.start();
    }

    // =========================================================================
    // Overlay — build / clear
    // =========================================================================

    private void openAttackSelection()  { if(!battleOver) buildSelectionOverlay("ATTACK DECIMA","Choose where to strike.","Decima.png"); }
    private void openDefenseSelection() { if(!battleOver) buildSelectionOverlay("DEFEND HUEMAN","Choose what part to protect.",getHuemanImage()); }

    private void buildSelectionOverlay(String title, String subtitle, String img) {
        clearOverlay();
        double W=screenW(),H=screenH();
        overlayShade=new GRect(0,0,W,H); overlayShade.setFilled(true); overlayShade.setFillColor(new Color(0,0,0,165)); overlayShade.setColor(new Color(0,0,0,0)); addOverlayObj(overlayShade);
        double pw=S(520),ph=S(520),px=(W-pw)/2,py=(H-ph)/2;
        GRect sh=new GRect(px+S(8),py+S(8),pw,ph); sh.setFilled(true); sh.setFillColor(new Color(0,0,0,140)); sh.setColor(new Color(0,0,0,0)); addOverlayObj(sh);
        overlayPanel=new GRect(px,py,pw,ph); overlayPanel.setFilled(true); overlayPanel.setFillColor(PANEL_BG); overlayPanel.setColor(PANEL_BORDER); addOverlayObj(overlayPanel);
        overlayInnerBorder=new GRect(px+S(8),py+S(8),pw-S(16),ph-S(16)); overlayInnerBorder.setFilled(false); overlayInnerBorder.setColor(new Color(212,175,55,90)); addOverlayObj(overlayInnerBorder);
        overlayTitle=new GLabel(title); overlayTitle.setFont(new Font("Georgia",Font.BOLD,Math.max(14,(int)S(28)))); overlayTitle.setColor(ACCENT_GOLD); overlayTitle.setLocation(px+S(28),py+S(42)); addOverlayObj(overlayTitle);
        overlaySubtitle=new GLabel(subtitle); overlaySubtitle.setFont(new Font("Georgia",Font.PLAIN,Math.max(11,(int)S(16)))); overlaySubtitle.setColor(TEXT_BRIGHT); overlaySubtitle.setLocation(px+S(28),py+S(72)); addOverlayObj(overlaySubtitle);
        overlayImage=new GImage(img); double sc=Math.min(pw*.40/overlayImage.getWidth(),ph*.42/overlayImage.getHeight()); overlayImage.scale(sc); overlayImage.setLocation(px+(pw-overlayImage.getWidth())/2,py+S(105)); addOverlayObj(overlayImage);
        double btnW=pw-S(90),btnH=S(48),btnX=px+S(45),fy=py+ph-S(185),gap=S(18);
        for(int i=0;i<3;i++){
            targetButtons[i]=new GRect(btnX,fy+i*(btnH+gap),btnW,btnH); targetButtons[i].setFilled(true); targetButtons[i].setFillColor(new Color(55,75,180,80)); targetButtons[i].setColor(ACCENT_GOLD); addOverlayObj(targetButtons[i]);
            targetLabels[i]=new GLabel(targetNames[i]); targetLabels[i].setFont(new Font("Georgia",Font.BOLD,Math.max(12,(int)S(18)))); targetLabels[i].setColor(TEXT_BRIGHT); targetLabels[i].setLocation(btnX+S(22),fy+i*(btnH+gap)+btnH*.67); addOverlayObj(targetLabels[i]);
        }
        selectionOverlayOpen=true; choosingPlayerAttack=title.startsWith("ATTACK"); choosingPlayerDefense=title.startsWith("DEFEND");
    }

    private void clearOverlay() {
        for(GObject o:overlayObjects){mainScreen.remove(o);contents.remove(o);} overlayObjects.clear();
        for(int i=0;i<3;i++){targetButtons[i]=null;targetLabels[i]=null;}
        selectionOverlayOpen=false; choosingPlayerAttack=false; choosingPlayerDefense=false;
    }

    private void clearEndOverlay() {
        for(GObject o:endOverlayObjects){mainScreen.remove(o);contents.remove(o);} endOverlayObjects.clear();
    }

    // =========================================================================
    // Resolve turns
    // =========================================================================

    private void resolvePlayerAttack(String chosenTarget) {
        recordHistory(chosenTarget);
        String def = aiChooseDefense();
        clearOverlay();
        if (!chosenTarget.equals(def)) {
            animatePlayerProjectile(chosenTarget, def);
        } else {
            setBattleMessage("Decima is defending " + def + "...");
            animateShieldBlock(getTargetX(decimaImage), getTargetY(decimaImage,def), def, true);
        }
    }

    private void resolveEnemyAttack(String chosenDefense) {
        recordHistory(chosenDefense);
        String att = aiChooseAttack();
        clearOverlay();
        if (!chosenDefense.equals(att)) {
            animateEnemyProjectile(att, chosenDefense);
        } else {
            setBattleMessage("Hueman guarded the " + chosenDefense + "!");
            gainSuper(SUPER_GAIN_BLOCK);
            animateShieldBlock(getTargetX(huemanImage), getTargetY(huemanImage,att), att, false);
        }
    }

    private void resolvePlayerSuper() {
        if (!superReady||battleOver) return;
        clearOverlay();
        h1.setSuperMeter(0); superReady=false;
        superOption.setColor(new Color(130,60,255,90));
        if(superReadyLabel!=null) superReadyLabel.setColor(new Color(200,160,255,0));
        updateSuperMeter();
        setBattleMessage("★ SUPER ATTACK! ★");
        fireSuperSequence(0);
    }

    // =========================================================================
    // PLAYER ATTACKS — normal projectile
    // =========================================================================

    private void animatePlayerProjectile(String chosenTarget, String enemyDefense) {
        if(animating)return; animating=true;
        double sx=getTargetX(huemanImage), sy=huemanImage.getY()+huemanImage.getHeight()*.40;
        double ex=getTargetX(decimaImage), ey=getTargetY(decimaImage,chosenTarget);
        double r=S(14); projectile=new GOval(sx-r,sy-r,r*2,r*2); projectile.setFilled(true); projectile.setFillColor(new Color(255,80,80)); projectile.setColor(new Color(255,200,200)); mainScreen.add(projectile); contents.add(projectile);
        int steps=30; double dx=(ex-sx)/steps,dy=(ey-sy)/steps; int[] cnt={0};
        animTimer=new javax.swing.Timer(16,null);
        animTimer.addActionListener(e->{cnt[0]++;projectile.move(dx,dy);
            if(cnt[0]>=steps){animTimer.stop();mainScreen.remove(projectile);contents.remove(projectile);projectile=null;animating=false;
                setBattleMessage("Hit! Decima defended "+enemyDefense+".");
                explodeThenFlinch(getTargetX(decimaImage),getTargetY(decimaImage,chosenTarget), true, ()-> {
                    gainSuper(SUPER_GAIN_HIT);
                    int dmg=getOneFifthDamage(decimaMaxHP); d1.takeDamage(dmg);
                    animateEnemyHPDrain(decimaHealthBack.getWidth()*((d1.getHP()+dmg)/decimaMaxHP), decimaHealthBack.getWidth()*Math.max(0,(double)d1.getHP()/decimaMaxHP));
                });
            }
        });
        animTimer.start();
    }

    // =========================================================================
    // SUPER — three sequential projectiles
    // =========================================================================

    private final String[] SUPER_TARGETS = {"HEAD","MIDDLE","LEGS"};

    private void fireSuperSequence(int idx) {
        if(idx>=3){
            // All three hit — deal 60% of max HP
            int dmg=(int)Math.ceil(decimaMaxHP*0.60); d1.takeDamage(dmg);
            animateEnemyHPDrain(decimaHealthBack.getWidth()*((d1.getHP()+dmg)/decimaMaxHP), decimaHealthBack.getWidth()*Math.max(0,(double)d1.getHP()/decimaMaxHP));
            return;
        }
        String t=SUPER_TARGETS[idx];
        double sx=getTargetX(huemanImage), sy=huemanImage.getY()+huemanImage.getHeight()*.40;
        double ex=getTargetX(decimaImage), ey=getTargetY(decimaImage,t);
        double r=S(16); GOval p=new GOval(sx-r,sy-r,r*2,r*2); p.setFilled(true); p.setFillColor(new Color(200,100,255)); p.setColor(new Color(240,200,255)); mainScreen.add(p); contents.add(p);
        int steps=20; double pdx=(ex-sx)/steps,pdy=(ey-sy)/steps; int[] cnt={0};
        javax.swing.Timer t2=new javax.swing.Timer(12,null);
        t2.addActionListener(e->{cnt[0]++;p.move(pdx,pdy);
            if(cnt[0]>=steps){t2.stop();mainScreen.remove(p);contents.remove(p);
                miniBurst(ex,ey);
                javax.swing.Timer w=new javax.swing.Timer(180,e2->{((javax.swing.Timer)e2.getSource()).stop();fireSuperSequence(idx+1);});
                w.setRepeats(false);w.start();
            }
        });
        t2.start();
    }

    private void miniBurst(double cx, double cy) {
        for(int i=0;i<8;i++){
            double a=(2*Math.PI/8)*i,r=S(6); GOval p=new GOval(cx-r,cy-r,r*2,r*2); p.setFilled(true); p.setFillColor(new Color(200,100,255)); p.setColor(new Color(240,200,255)); mainScreen.add(p); contents.add(p);
            double vx=Math.cos(a)*S(5),vy=Math.sin(a)*S(5); int[] fr={0};
            javax.swing.Timer bt=new javax.swing.Timer(16,null); bt.addActionListener(e->{fr[0]++;p.move(vx,vy);p.setFillColor(new Color(200,100,255,Math.max(0,255-fr[0]*30)));if(fr[0]>=8){bt.stop();mainScreen.remove(p);contents.remove(p);}}); bt.start();
        }
    }

    // =========================================================================
    // SHARED explosion + flinch helper
    // =========================================================================

    private void explodeThenFlinch(double cx, double cy, boolean onDecima, Runnable onDone) {
        int n=12; bombParticles.clear();
        for(int i=0;i<n;i++){double r=S(8)+Math.random()*S(6);GOval p=new GOval(cx-r,cy-r,r*2,r*2);p.setFilled(true);p.setFillColor(new Color(200+(int)(Math.random()*55),(int)(Math.random()*120),0));p.setColor(new Color(255,220,0));mainScreen.add(p);contents.add(p);bombParticles.add(p);}
        GOval ring=new GOval(cx-S(30),cy-S(30),S(60),S(60));ring.setFilled(false);ring.setColor(new Color(255,220,50));mainScreen.add(ring);contents.add(ring);bombParticles.add(ring);
        double[] ang=new double[n]; for(int i=0;i<n;i++)ang[i]=(2*Math.PI/n)*i;
        int[] fr={0};int tot=25;
        bombTimer=new javax.swing.Timer(16,null);
        bombTimer.addActionListener(e->{fr[0]++;double pg=(double)fr[0]/tot;
            for(int i=0;i<n;i++){bombParticles.get(i).move(Math.cos(ang[i])*S(4)+Math.random()*S(2),Math.sin(ang[i])*S(4)+Math.random()*S(2));bombParticles.get(i).setFillColor(new Color(255,Math.max(0,(int)(120*(1-pg))),0,(int)(255*(1-pg))));}
            ring.setSize(S(60)+pg*S(80),S(60)+pg*S(80));ring.setLocation(cx-(S(30)+pg*S(40)),cy-(S(30)+pg*S(40)));ring.setColor(new Color(255,220,50,(int)(255*(1-pg))));
            if(fr[0]>=tot){bombTimer.stop();for(GOval p:bombParticles){mainScreen.remove(p);contents.remove(p);}bombParticles.clear();
                GImage sprite = onDecima?decimaImage:huemanImage;
                flinchSprite(sprite, onDone);
            }
        });
        bombTimer.start();
    }

    // =========================================================================
    // ENEMY ATTACKS — normal projectile
    // =========================================================================

    private void animateEnemyProjectile(String enemyAttack, String chosenDefense) {
        if(animating)return; animating=true;
        setBattleMessage("Decima fires at your "+enemyAttack+"!");
        double sx=getTargetX(decimaImage),sy=decimaImage.getY()+decimaImage.getHeight()*.40;
        double ex=getTargetX(huemanImage),ey=getTargetY(huemanImage,enemyAttack);
        double r=S(14); projectile=new GOval(sx-r,sy-r,r*2,r*2); projectile.setFilled(true); projectile.setFillColor(new Color(255,120,60)); projectile.setColor(new Color(255,220,180)); mainScreen.add(projectile); contents.add(projectile);
        int steps=30; double dx=(ex-sx)/steps,dy=(ey-sy)/steps; int[] cnt={0};
        animTimer=new javax.swing.Timer(16,null);
        animTimer.addActionListener(e->{cnt[0]++;projectile.move(dx,dy);
            if(cnt[0]>=steps){animTimer.stop();mainScreen.remove(projectile);contents.remove(projectile);projectile=null;animating=false;
                setBattleMessage("Decima hit your "+enemyAttack+"!");
                explodeThenFlinch(ex,ey,false,()->{
                    gainSuper(SUPER_GAIN_DMGTAKEN);
                    int dmg=getOneFifthDamage(huemanMaxHP); h1.takeDamage(dmg);
                    animatePlayerHPDrain(huemanHealthBack.getWidth()*((h1.getHP()+dmg)/huemanMaxHP), huemanHealthBack.getWidth()*Math.max(0,(double)h1.getHP()/huemanMaxHP));
                });
            }
        });
        animTimer.start();
    }

    // =========================================================================
    // HP drain animations
    // =========================================================================

    private void animateEnemyHPDrain(double sw, double ew) {
        int tot=40; int[] fr={0};
        hpDrainTimer=new javax.swing.Timer(20,null);
        hpDrainTimer.addActionListener(e->{fr[0]++;double p=(double)fr[0]/tot,ep=1-Math.pow(1-p,2);
            decimaHealthBar.setSize(Math.max(0,sw+(ew-sw)*ep),decimaHealthBack.getHeight());
            decimaHealthBar.setFillColor(fr[0]%4<2?new Color(255,255,100):ENEMY_HP);
            if(fr[0]>=tot){hpDrainTimer.stop();decimaHealthBar.setFillColor(ENEMY_HP);decimaHealthBar.setSize(Math.max(0,ew),decimaHealthBack.getHeight());
                checkBattleEnd();
                if(!battleOver){playerTurn=false;showEnemyTurnIntro();}
            }
        });
        hpDrainTimer.start();
    }

    private void animatePlayerHPDrain(double sw, double ew) {
        int tot=40; int[] fr={0};
        hpDrainTimer=new javax.swing.Timer(20,null);
        hpDrainTimer.addActionListener(e->{fr[0]++;double p=(double)fr[0]/tot,ep=1-Math.pow(1-p,2);
            huemanHealthBar.setSize(Math.max(0,sw+(ew-sw)*ep),huemanHealthBack.getHeight());
            huemanHealthBar.setFillColor(fr[0]%4<2?new Color(255,255,100):getPlayerColor());
            if(fr[0]>=tot){hpDrainTimer.stop();huemanHealthBar.setFillColor(getPlayerColor());huemanHealthBar.setSize(Math.max(0,ew),huemanHealthBack.getHeight());
                updateHealthBars();checkBattleEnd();
                if(!battleOver){playerTurn=true;setBattleMessage("Your turn.");}
            }
        });
        hpDrainTimer.start();
    }

    // =========================================================================
    // Shield block — shared for both characters
    // =========================================================================

    /**
     * @param onDecima  true = Decima is blocking (slides from right); false = Hueman (slides from left)
     */
    private void animateShieldBlock(double cx, double cy, String part, boolean onDecima) {
        final double targetY = onDecima 
                ? getTargetY(decimaImage, part)
                : getTargetY(huemanImage, part);

        double shW = S(80);
        double shH = S(100);
        double startX = onDecima ? cx + S(200) : cx - S(200);
        double targX = cx - shW / 2;
        double shY = targetY - shH / 2;

        shield = new GRect(startX, shY, shW, shH);
        shield.setFilled(true);
        shield.setFillColor(new Color(60, 80, 180, 180));
        shield.setColor(new Color(180, 200, 255));
        mainScreen.add(shield);
        contents.add(shield);

        GLabel lbl = new GLabel("BLOCK!");
        lbl.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(20))));
        lbl.setColor(Color.WHITE);
        lbl.setLocation(startX + S(5), targetY + S(8));
        mainScreen.add(lbl);
        contents.add(lbl);

        int sf = 15, hf = 20, ff = 15, tot = sf + hf + ff;
        int[] fr = {0};
        double sd = onDecima ? startX - targX : targX - startX;

        shieldTimer = new javax.swing.Timer(16, null);
        shieldTimer.addActionListener(e -> {
            fr[0]++;

            if (fr[0] <= sf) {
                double pg = (double) fr[0] / sf;
                double ea = 1 - Math.pow(1 - pg, 3);
                double curX = onDecima ? startX - sd * ea : startX + sd * ea;

                shield.setLocation(curX, shY);
                lbl.setLocation(curX + S(5), targetY + S(8));

            } else if (fr[0] <= sf + hf) {
                shield.setFillColor(fr[0] % 4 < 2
                        ? new Color(120, 150, 255, 200)
                        : new Color(60, 80, 180, 180));

            } else {
                double pg = (double) (fr[0] - sf - hf) / ff;
                int a = (int) (180 * (1 - pg));

                shield.setFillColor(new Color(60, 80, 180, Math.max(0, a)));
                lbl.setColor(new Color(255, 255, 255,
                        Math.max(0, (int) (255 * (1 - pg)))));
            }

            if (fr[0] >= tot) {
                shieldTimer.stop();

                mainScreen.remove(shield);
                contents.remove(shield);
                mainScreen.remove(lbl);
                contents.remove(lbl);
                shield = null;

                if (onDecima) {
                    setBattleMessage("Blocked! Decima defended " + part + ".");
                    updateHealthBars();
                    checkBattleEnd();
                    if (!battleOver) {
                        playerTurn = false;
                        showEnemyTurnIntro();
                    }
                } else {
                    setBattleMessage("Guarded! You blocked Decima's " + part + " attack.");
                    updateHealthBars();
                    checkBattleEnd();
                    if (!battleOver) {
                        playerTurn = true;
                        setBattleMessage("Your turn.");
                    }
                }
            }
        });

        shieldTimer.start();
    }

    // =========================================================================
    // Enemy turn intro  ← staged 2-step message
    // =========================================================================

    private void showEnemyTurnIntro() {
        setBattleMessage("Decima is preparing to strike...");
        javax.swing.Timer t=new javax.swing.Timer(750,null); t.setRepeats(false);
        t.addActionListener(e->{t.stop();setBattleMessage("Decima attacks — choose your defence!");openDefenseSelection();});
        t.start();
    }

    // =========================================================================
    // Battle end — victory / defeat
    // =========================================================================

    private void checkBattleEnd() {
        if (!d1.isAlive()) { battleOver=true; playerTurn=false; clearOverlay(); playVictorySequence(); }
        else if (!h1.isAlive()) { battleOver=true; playerTurn=false; clearOverlay(); playDefeatSequence(); }
    }

    // ── Victory ───────────────────────────────────────────────────────────────

    private void playVictorySequence() {
        // White flash
        GRect flash=new GRect(0,0,screenW(),screenH()); flash.setFilled(true); flash.setFillColor(new Color(255,255,255,0)); flash.setColor(new Color(0,0,0,0)); mainScreen.add(flash); contents.add(flash); endOverlayObjects.add(flash);
        int[] fr={0};
        javax.swing.Timer ft=new javax.swing.Timer(16,null);
        ft.addActionListener(e->{fr[0]++;int a=fr[0]<=8?fr[0]*30:Math.max(0,255-(fr[0]-8)*20);flash.setFillColor(new Color(255,255,255,Math.min(255,a)));
            if(fr[0]>=24){ft.stop();mainScreen.remove(flash);contents.remove(flash);endOverlayObjects.remove(flash);showVictoryBanner();}
        });
        ft.start();
    }

    private void showVictoryBanner() {
        double W=screenW(),H=screenH();
        GRect shade=new GRect(0,0,W,H); shade.setFilled(true); shade.setFillColor(new Color(0,0,0,160)); shade.setColor(new Color(0,0,0,0)); mainScreen.add(shade); contents.add(shade); endOverlayObjects.add(shade);
        double bw=S(700),bh=S(180),bx=(W-bw)/2,by=(H-bh)/2-S(40);
        GRect ban=new GRect(bx,by,bw,bh); ban.setFilled(true); ban.setFillColor(new Color(20,15,5)); ban.setColor(ACCENT_GOLD); mainScreen.add(ban); contents.add(ban); endOverlayObjects.add(ban);
        GRect banI=new GRect(bx+S(8),by+S(8),bw-S(16),bh-S(16)); banI.setFilled(false); banI.setColor(new Color(212,175,55,60)); mainScreen.add(banI); contents.add(banI); endOverlayObjects.add(banI);
        GLabel vic=new GLabel("VICTORY"); vic.setFont(new Font("Georgia",Font.BOLD,Math.max(24,(int)S(72)))); vic.setColor(ACCENT_GOLD); vic.setLocation(bx+(bw-vic.getWidth())/2,by+bh*.62); mainScreen.add(vic); contents.add(vic); endOverlayObjects.add(vic);
        GLabel sub=new GLabel("Decima has been defeated."); sub.setFont(new Font("Georgia",Font.PLAIN,Math.max(12,(int)S(22)))); sub.setColor(TEXT_BRIGHT); sub.setLocation(bx+(bw-sub.getWidth())/2,by+bh*.88); mainScreen.add(sub); contents.add(sub); endOverlayObjects.add(sub);
        spawnGoldParticles(getTargetX(decimaImage), decimaImage.getY()+decimaImage.getHeight()/2);
        setBattleMessage("Decima has fallen. Press CONTINUE.");
        goToNextScreenAfterDelay();
    }

    private void spawnGoldParticles(double cx, double cy) {
        for(int i=0;i<20;i++){
            double angle=Math.random()*2*Math.PI,speed=S(3)+Math.random()*S(5),r=S(5)+Math.random()*S(8);
            GOval p=new GOval(cx-r,cy-r,r*2,r*2); p.setFilled(true); p.setFillColor(ACCENT_GOLD); p.setColor(ACCENT_GOLD); mainScreen.add(p); contents.add(p); endOverlayObjects.add(p);
            double vx=Math.cos(angle)*speed,vy=Math.sin(angle)*speed; int[] fr={0};
            javax.swing.Timer pt=new javax.swing.Timer(16,null);
            pt.addActionListener(e->{fr[0]++;p.move(vx,vy-S(1));p.setFillColor(new Color(212,175,55,Math.max(0,255-fr[0]*8)));if(fr[0]>=32){pt.stop();mainScreen.remove(p);contents.remove(p);endOverlayObjects.remove(p);}});
            pt.start();
        }
    }

    // ── Defeat ────────────────────────────────────────────────────────────────

    private void playDefeatSequence() {
        GRect red=new GRect(0,0,screenW(),screenH()); red.setFilled(true); red.setFillColor(new Color(180,0,0,0)); red.setColor(new Color(0,0,0,0)); mainScreen.add(red); contents.add(red); endOverlayObjects.add(red);
        int[] fr={0};
        javax.swing.Timer ft=new javax.swing.Timer(20,null);
        ft.addActionListener(e->{fr[0]++;red.setFillColor(new Color(180,0,0,Math.min(140,fr[0]*5)));if(fr[0]>=30){ft.stop();showDefeatBanner();}});
        ft.start();
    }

    private void showDefeatBanner() {
        double W=screenW(),H=screenH(),bw=S(700),bh=S(180),bx=(W-bw)/2,by=(H-bh)/2-S(40);
        GRect ban=new GRect(bx,by,bw,bh); ban.setFilled(true); ban.setFillColor(new Color(25,5,5)); ban.setColor(HP_RED); mainScreen.add(ban); contents.add(ban); endOverlayObjects.add(ban);
        GRect banI=new GRect(bx+S(8),by+S(8),bw-S(16),bh-S(16)); banI.setFilled(false); banI.setColor(new Color(220,55,55,60)); mainScreen.add(banI); contents.add(banI); endOverlayObjects.add(banI);
        GLabel def=new GLabel("DEFEAT"); def.setFont(new Font("Georgia",Font.BOLD,Math.max(24,(int)S(72)))); def.setColor(HP_RED); def.setLocation(bx+(bw-def.getWidth())/2,by+bh*.62); mainScreen.add(def); contents.add(def); endOverlayObjects.add(def);
        GLabel sub=new GLabel("Hueman has fallen."); sub.setFont(new Font("Georgia",Font.PLAIN,Math.max(12,(int)S(22)))); sub.setColor(TEXT_BRIGHT); sub.setLocation(bx+(bw-sub.getWidth())/2,by+bh*.88); mainScreen.add(sub); contents.add(sub); endOverlayObjects.add(sub);
        setBattleMessage("Hueman was defeated.");
        goToNextScreenAfterDelay();
    }

    // =========================================================================
    // Health bars & super meter
    // =========================================================================

    private void updateHealthBars() {
        double hR=Math.max(0,(double)h1.getHP()/huemanMaxHP), dR=Math.max(0,(double)d1.getHP()/decimaMaxHP);
        huemanHealthBar.setSize(huemanHealthBack.getWidth()*hR,huemanHealthBack.getHeight());
        decimaHealthBar.setSize(decimaHealthBack.getWidth()*dR,decimaHealthBack.getHeight());
        Color ac=hR<0.25?HP_RED:getPlayerColor(); huemanHealthBar.setFillColor(ac); huemanHealthBar.setColor(ac.darker());
        hpValueLabel.setLabel(Math.max(0,(int)h1.getHP())+" / "+(int)huemanMaxHP);
        updateSuperMeter();
    }

    private void updateSuperMeter() {
        double r=Math.min(1.0,(double)h1.getSuperMeter()/SUPER_MAX);
        superBar.setSize(superBack.getWidth()*r,superBack.getHeight());
        superBar.setFillColor(r>=1.0?new Color(210,160,255):SUPER_COLOR);
    }

    // =========================================================================
    // Utility
    // =========================================================================

    private int getOneFifthDamage(double maxHp) { return (int)Math.ceil(maxHp/5.0); }

    private void addObj(GObject o) { mainScreen.add(o); contents.add(o); }
    private void addOverlayObj(GObject o) { mainScreen.add(o); contents.add(o); overlayObjects.add(o); }

    private Color getPlayerColor() {
        String c=mainScreen.getSelectedColor();
        if(c==null)return HP_GREEN; if(c.equals("red"))return HP_RED; if(c.equals("blue"))return HP_BLUE; return HP_GREEN;
    }
    private String getHuemanImage() {
        String c=mainScreen.getSelectedColor();
        if(c==null)return"redback.png"; if(c.equals("red"))return"redback.png"; if(c.equals("green"))return"greenback.png"; return"blueback.png";
    }

    // =========================================================================
    // Input
    // =========================================================================

    @Override
    public void mouseClicked(MouseEvent e) {
        double mx = e.getX();
        double my = e.getY();

        // ✅ CONTINUE BUTTON (ADD THIS)
        if (continueButton != null &&
            mx >= continueButton.getX() &&
            mx <= continueButton.getX() + continueButton.getWidth() &&
            my >= continueButton.getY() &&
            my <= continueButton.getY() + continueButton.getHeight()) {

            battleOver = true;
            stopAllTimers();
            clearOverlay();
            clearEndOverlay();

            mainScreen.switchToCutScene2Screen(); // this is YOUR correct next screen
            return;
        }

        // ===== EXISTING CODE =====

        if(selectionOverlayOpen){
            for(int i=0;i<3;i++){
                if(targetButtons[i]!=null &&
                   mx>=targetButtons[i].getX() &&
                   mx<=targetButtons[i].getX()+targetButtons[i].getWidth() &&
                   my>=targetButtons[i].getY() &&
                   my<=targetButtons[i].getY()+targetButtons[i].getHeight()){

                    if(choosingPlayerAttack) resolvePlayerAttack(targetNames[i]);
                    else if(choosingPlayerDefense) resolveEnemyAttack(targetNames[i]);
                    return;
                }
            }
            return;
        }

        GObject obj = mainScreen.getElementAt(mx, my);

        if (battleOver) return;

        if(playerTurn){
            if(obj==fightOption||obj==fightHighlight) openAttackSelection();
            else if((obj==superOption||obj==superHighlight)&&superReady) resolvePlayerSuper();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(huemanImage==null)return;
        int k=e.getKeyCode();
        if     (k==KeyEvent.VK_LEFT) huemanImage.move(-10,0);
        else if(k==KeyEvent.VK_RIGHT)huemanImage.move(10,0);
        else if(k==KeyEvent.VK_UP)   huemanImage.move(0,-10);
        else if(k==KeyEvent.VK_DOWN) huemanImage.move(0,10);
    }
}