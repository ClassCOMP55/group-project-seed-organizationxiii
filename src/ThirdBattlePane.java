import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import characters.Hueman;
import characters.MintSentinel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ThirdBattlePane extends GraphicsPane {

    private Hueman h1;
    private MintSentinel s1;
    private double huemanMaxHP;
    private double enemyMaxHP;

    private javax.swing.Timer bombTimer;
    private javax.swing.Timer hpDrainTimer;
    private javax.swing.Timer shieldTimer;
    private javax.swing.Timer animTimer;
    private javax.swing.Timer flinchTimer;

    private ArrayList<GOval> bombParticles = new ArrayList<>();
    private GImage huemanImage;
    private GImage enemyImage;
    private GRect shield;
    private GRect skyPanel;

    private GRect enemyHudPanel;
    private GRect enemyHudAccent;
    private GLabel enemyNameLabel;
    private GRect enemyHealthBack;
    private GRect enemyHealthBar;
    private GLabel enemyHpLabel;

    private GRect hudPanel;
    private GRect hudAccent;
    private GLabel huemanNameLabel;
    private GRect huemanHealthBack;
    private GRect huemanHealthBar;
    private GLabel hpValueLabel;
    private GRect superBack;
    private GRect superBar;
    private GLabel superReadyLabel;

    private GRect menuPanel;
    private GRect menuInnerBorder;
    private GLabel fightOption;
    private GRect fightHighlight;
    private GLabel superOption;
    private GRect superHighlight;

    private GRect continueButton;
    private GLabel continueLabel;
    private GRect continueGlow;

    private GRect overlayShade;
    private GRect overlayPanel;
    private GRect overlayInnerBorder;
    private GLabel overlayTitle;
    private GLabel overlaySubtitle;
    private GImage overlayImage;
    private GRect[] targetButtons = new GRect[3];
    private GLabel[] targetLabels = new GLabel[3];
    private String[] targetNames = {"LEGS", "MIDDLE", "HEAD"};

    private GOval projectile;
    private GLabel battleMessageLabel;

    private ArrayList<GObject> overlayObjects = new ArrayList<>();
    private ArrayList<GObject> endOverlayObjects = new ArrayList<>();

    private boolean playerTurn = true;
    private boolean battleOver = false;
    private boolean animating = false;
    private boolean selectionOverlayOpen = false;
    private boolean choosingPlayerAttack = false;
    private boolean choosingPlayerDefense = false;
    private boolean superReady = false;

    private ArrayList<String> playerHistory = new ArrayList<>();
    private boolean enemyPhase2 = false;

    private static final Color BG_TOP       = new Color(10, 12, 28);
    private static final Color PANEL_BG     = new Color(12, 15, 35, 220);
    private static final Color PANEL_BORDER = new Color(80, 100, 180);
    private static final Color ACCENT_GOLD  = new Color(212, 175, 55);
    private static final Color HP_RED       = new Color(220, 55, 55);
    private static final Color HP_GREEN     = new Color(55, 220, 100);
    private static final Color HP_BLUE      = new Color(55, 140, 255);
    private static final Color SUPER_COLOR  = new Color(130, 60, 255);
    private static final Color TEXT_BRIGHT  = new Color(230, 235, 255);
    private static final Color MENU_BG      = new Color(8, 10, 25, 240);
    private static final Color HIGHLIGHT    = new Color(55, 75, 180, 80);

    private static final Color ENEMY_ACCENT = new Color(90, 255, 220);
    private static final Color ENEMY_HP     = new Color(70, 235, 210);

    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;
    private double scale, offsetX, offsetY;

    private static final double SUPER_MAX = 500.0;
    private static final double SUPER_GAIN_HIT = 60.0;
    private static final double SUPER_GAIN_BLOCK = 40.0;
    private static final double SUPER_GAIN_DMGTAKEN = 30.0;

    private final String[] SUPER_TARGETS = {"HEAD", "MIDDLE", "LEGS"};

    public ThirdBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        updateLayoutScale();
        
        h1.heal();
        h1 = mainScreen.getPlayer();
        s1 = new MintSentinel(700, 24, 12, "Slujupiter");
        huemanMaxHP = h1.getHP();
        enemyMaxHP = s1.getHP();

        playerTurn = true;
        battleOver = false;
        animating = false;
        selectionOverlayOpen = false;
        choosingPlayerAttack = false;
        choosingPlayerDefense = false;
        superReady = false;
        enemyPhase2 = false;
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
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }

    private void stopAllTimers() {
        if (bombTimer != null) {
            bombTimer.stop();
            bombTimer = null;
        }
        if (hpDrainTimer != null) {
            hpDrainTimer.stop();
            hpDrainTimer = null;
        }
        if (shieldTimer != null) {
            shieldTimer.stop();
            shieldTimer = null;
        }
        if (animTimer != null) {
            animTimer.stop();
            animTimer = null;
        }
        if (flinchTimer != null) {
            flinchTimer.stop();
            flinchTimer = null;
        }
    }

    private void updateLayoutScale() {
        double aW = mainScreen.getWidth();
        double aH = mainScreen.getHeight();

        if (aW <= 0) aW = MainApplication.WINDOW_WIDTH;
        if (aH <= 0) aH = MainApplication.WINDOW_HEIGHT;

        scale = Math.min(aW / BASE_W, aH / BASE_H);
        offsetX = (aW - BASE_W * scale) / 2.0;
        offsetY = (aH - BASE_H * scale) / 2.0;
    }

    private double X(double v) {
        return offsetX + v * scale;
    }

    private double Y(double v) {
        return offsetY + v * scale;
    }

    private double S(double v) {
        return v * scale;
    }

    private double screenW() {
        double w = mainScreen.getWidth();
        return w > 0 ? w : MainApplication.WINDOW_WIDTH;
    }

    private double screenH() {
        double h = mainScreen.getHeight();
        return h > 0 ? h : MainApplication.WINDOW_HEIGHT;
    }

    private void buildBackground() {
        double W = screenW();
        double H = screenH();

        skyPanel = new GRect(0, 0, W, H);
        skyPanel.setFilled(true);
        skyPanel.setFillColor(BG_TOP);
        skyPanel.setColor(BG_TOP);
        addObj(skyPanel);

        for (int i = 0; i < 2; i++) {
            GLine slash = new GLine(X(760 + i * 6), Y(150), X(840 + i * 24), Y(930));
            slash.setColor(new Color(60, 80, 160, 35));
            addObj(slash);
        }
    }

    private void drawBattleFrame() {
        double W = screenW();
        double H = screenH();
        double m1 = S(20);
        double m2 = S(30);

        GRect outer = new GRect(m1, m1, W - 2 * m1, H - 2 * m1);
        outer.setFilled(false);
        outer.setColor(ACCENT_GOLD);
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
        double ll = S(30);
        double sl = S(15);
        double g = S(7);

        GLine h1 = new GLine(x, y, x + (left ? ll : -ll), y);
        GLine v1 = new GLine(x, y, x, y + (top ? ll : -ll));
        GLine h2 = new GLine(x + (left ? g : -g), y + (top ? g : -g), x + (left ? sl : -sl), y + (top ? g : -g));
        GLine v2 = new GLine(x + (left ? g : -g), y + (top ? g : -g), x + (left ? g : -g), y + (top ? sl : -sl));

        h1.setColor(ACCENT_GOLD);
        v1.setColor(ACCENT_GOLD);
        h2.setColor(new Color(212, 175, 55, 150));
        v2.setColor(new Color(212, 175, 55, 150));

        addObj(h1);
        addObj(v1);
        addObj(h2);
        addObj(v2);
    }

    private void buildSprites() {
        huemanImage = new GImage(getHuemanImage());
        enemyImage = new GImage("slujupiter.png");

        huemanImage.scale(0.52);
        enemyImage.scale(0.72);

        double hStoneW = S(170);
        double hStoneH = S(35);
        double hCX = X(340);
        double hSY = Y(700);

        GOval hSh = new GOval(hCX - hStoneW * .45, hSY + hStoneH * .75, hStoneW * .9, hStoneH * .5);
        hSh.setFilled(true);
        hSh.setFillColor(new Color(0, 0, 0, 70));
        hSh.setColor(new Color(0, 0, 0, 0));
        addObj(hSh);

        GRect hSb2 = new GRect(hCX - hStoneW / 2 - S(6), hSY + S(8), hStoneW + S(12), hStoneH * .55);
        hSb2.setFilled(true);
        hSb2.setFillColor(new Color(42, 38, 55));
        hSb2.setColor(new Color(28, 25, 40));
        addObj(hSb2);

        GRect hSb1 = new GRect(hCX - hStoneW / 2, hSY, hStoneW, hStoneH);
        hSb1.setFilled(true);
        hSb1.setFillColor(new Color(92, 84, 116));
        hSb1.setColor(new Color(58, 52, 76));
        addObj(hSb1);

        huemanImage.setLocation(hCX - huemanImage.getWidth() / 2 + S(110), hSY - huemanImage.getHeight() - S(8));
        addObj(huemanImage);

        double eStoneW = S(190);
        double eStoneH = S(36);
        double eCX = X(1120);
        double eSY = Y(480);

        GOval eSh = new GOval(eCX - eStoneW * .46, eSY + eStoneH * .75, eStoneW * .92, eStoneH * .5);
        eSh.setFilled(true);
        eSh.setFillColor(new Color(0, 0, 0, 70));
        eSh.setColor(new Color(0, 0, 0, 0));
        addObj(eSh);

        GRect eSb2 = new GRect(eCX - eStoneW / 2 - S(6), eSY + S(8), eStoneW + S(12), eStoneH * .55);
        eSb2.setFilled(true);
        eSb2.setFillColor(new Color(24, 70, 60));
        eSb2.setColor(new Color(14, 40, 35));
        addObj(eSb2);

        GRect eSb1 = new GRect(eCX - eStoneW / 2, eSY, eStoneW, eStoneH);
        eSb1.setFilled(true);
        eSb1.setFillColor(new Color(55, 140, 120));
        eSb1.setColor(new Color(30, 85, 72));
        addObj(eSb1);

        enemyImage.setLocation(eCX - enemyImage.getWidth() / 2, eSY - enemyImage.getHeight() + S(10));
        addObj(enemyImage);
    }

    private void buildEnemyHUD() {
        double px = X(30);
        double py = Y(38);
        double pw = S(290);
        double ph = S(85);

        GRect sh = new GRect(px + S(4), py + S(4), pw, ph);
        sh.setFilled(true);
        sh.setFillColor(new Color(0, 0, 0, 120));
        sh.setColor(new Color(0, 0, 0, 0));
        addObj(sh);

        enemyHudPanel = new GRect(px, py, pw, ph);
        enemyHudPanel.setFilled(true);
        enemyHudPanel.setFillColor(PANEL_BG);
        enemyHudPanel.setColor(PANEL_BORDER);
        addObj(enemyHudPanel);

        enemyHudAccent = new GRect(px, py, S(5), ph);
        enemyHudAccent.setFilled(true);
        enemyHudAccent.setFillColor(ENEMY_ACCENT);
        enemyHudAccent.setColor(ENEMY_ACCENT);
        addObj(enemyHudAccent);

        enemyNameLabel = new GLabel(s1.getName().toUpperCase());
        enemyNameLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(28))));
        enemyNameLabel.setColor(ENEMY_ACCENT);
        enemyNameLabel.setLocation(px + S(18), py + ph * .35);
        addObj(enemyNameLabel);

        enemyHpLabel = new GLabel("HP");
        enemyHpLabel.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        enemyHpLabel.setColor(TEXT_BRIGHT);
        enemyHpLabel.setLocation(px + S(18), py + ph * .70);
        addObj(enemyHpLabel);

        double bx = px + pw * .18;
        double by = py + ph * .52;
        double bw = pw * .68;
        double bh = ph * .18;

        enemyHealthBack = new GRect(bx, by, bw, bh);
        enemyHealthBack.setFilled(true);
        enemyHealthBack.setFillColor(new Color(30, 30, 50));
        enemyHealthBack.setColor(new Color(60, 60, 90));
        addObj(enemyHealthBack);

        enemyHealthBar = new GRect(bx, by, bw, bh);
        enemyHealthBar.setFilled(true);
        enemyHealthBar.setFillColor(ENEMY_HP);
        enemyHealthBar.setColor(ENEMY_HP.darker());
        addObj(enemyHealthBar);
    }

    private void buildPlayerHUD() {
        double W = screenW();
        double H = screenH();
        double pw = S(400);
        double ph = S(100);
        double px = W - pw - S(460);
        double py = H - ph - S(55);

        GRect sh = new GRect(px + S(4), py + S(4), pw, ph);
        sh.setFilled(true);
        sh.setFillColor(new Color(0, 0, 0, 120));
        sh.setColor(new Color(0, 0, 0, 0));
        addObj(sh);

        hudPanel = new GRect(px, py, pw, ph);
        hudPanel.setFilled(true);
        hudPanel.setFillColor(PANEL_BG);
        hudPanel.setColor(PANEL_BORDER);
        addObj(hudPanel);

        Color ac = getPlayerColor();

        hudAccent = new GRect(px, py, S(6), ph);
        hudAccent.setFilled(true);
        hudAccent.setFillColor(ac);
        hudAccent.setColor(ac);
        addObj(hudAccent);

        huemanNameLabel = new GLabel(h1.getName().toUpperCase());
        huemanNameLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(28))));
        huemanNameLabel.setColor(ac.brighter());
        huemanNameLabel.setLocation(px + S(18), py + ph * .30);
        addObj(huemanNameLabel);

        GLabel hpTag = new GLabel("HP");
        hpTag.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        hpTag.setColor(TEXT_BRIGHT);
        hpTag.setLocation(px + S(18), py + ph * .58);
        addObj(hpTag);

        double bx = px + pw * .16;
        double by = py + ph * .44;
        double bw = pw * .62;
        double bh = ph * .14;

        huemanHealthBack = new GRect(bx, by, bw, bh);
        huemanHealthBack.setFilled(true);
        huemanHealthBack.setFillColor(new Color(30, 30, 50));
        huemanHealthBack.setColor(new Color(60, 60, 90));
        addObj(huemanHealthBack);

        huemanHealthBar = new GRect(bx, by, bw, bh);
        huemanHealthBar.setFilled(true);
        huemanHealthBar.setFillColor(ac);
        huemanHealthBar.setColor(ac.darker());
        addObj(huemanHealthBar);

        hpValueLabel = new GLabel((int) h1.getHP() + " / " + (int) huemanMaxHP);
        hpValueLabel.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        hpValueLabel.setColor(TEXT_BRIGHT);
        hpValueLabel.setLocation(px + pw * .80, py + ph * .58);
        addObj(hpValueLabel);

        GLabel spTag = new GLabel("SP");
        spTag.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        spTag.setColor(new Color(180, 140, 255));
        spTag.setLocation(px + S(18), py + ph * .82);
        addObj(spTag);

        double sxB = px + pw * .16;
        double syB = py + ph * .68;
        double sw = pw * .62;
        double sh2 = ph * .12;

        superBack = new GRect(sxB, syB, sw, sh2);
        superBack.setFilled(true);
        superBack.setFillColor(new Color(30, 20, 50));
        superBack.setColor(new Color(70, 50, 100));
        addObj(superBack);

        superBar = new GRect(sxB, syB, 0, sh2);
        superBar.setFilled(true);
        superBar.setFillColor(SUPER_COLOR);
        superBar.setColor(SUPER_COLOR.darker());
        addObj(superBar);

        superReadyLabel = new GLabel("★ SUPER READY ★");
        superReadyLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(10, (int) S(14))));
        superReadyLabel.setColor(new Color(200, 160, 255, 0));
        superReadyLabel.setLocation(sxB, syB - S(4));
        addObj(superReadyLabel);
    }

    private void buildActionMenu() {
        double H = screenH();
        double mw = S(210);
        double mh = S(160);
        double mx = X(30);
        double my = H - mh - S(45);

        GRect sh = new GRect(mx + S(5), my + S(5), mw, mh);
        sh.setFilled(true);
        sh.setFillColor(new Color(0, 0, 0, 130));
        sh.setColor(new Color(0, 0, 0, 0));
        addObj(sh);

        menuPanel = new GRect(mx, my, mw, mh);
        menuPanel.setFilled(true);
        menuPanel.setFillColor(MENU_BG);
        menuPanel.setColor(PANEL_BORDER);
        addObj(menuPanel);

        menuInnerBorder = new GRect(mx + S(6), my + S(6), mw - S(12), mh - S(12));
        menuInnerBorder.setFilled(false);
        menuInnerBorder.setColor(new Color(212, 175, 55, 90));
        addObj(menuInnerBorder);

        addCornerAccent(mx + S(4), my + S(4), true, true);
        addCornerAccent(mx + mw - S(4), my + S(4), false, true);
        addCornerAccent(mx + S(4), my + mh - S(4), true, false);
        addCornerAccent(mx + mw - S(4), my + mh - S(4), false, false);

        fightHighlight = new GRect(mx + S(10), my + S(18), mw - S(20), mh * .22);
        fightHighlight.setFilled(true);
        fightHighlight.setFillColor(HIGHLIGHT);
        fightHighlight.setColor(new Color(0, 0, 0, 0));
        addObj(fightHighlight);

        fightOption = new GLabel("FIGHT");
        fightOption.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(22))));
        fightOption.setColor(TEXT_BRIGHT);
        fightOption.setLocation(mx + S(20), my + S(18) + mh * .17);
        addObj(fightOption);

        superHighlight = new GRect(mx + S(10), my + S(18) + mh * .42, mw - S(20), mh * .22);
        superHighlight.setFilled(true);
        superHighlight.setFillColor(new Color(80, 40, 160, 40));
        superHighlight.setColor(new Color(0, 0, 0, 0));
        addObj(superHighlight);

        superOption = new GLabel("SUPER");
        superOption.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(22))));
        superOption.setColor(new Color(130, 60, 255, 90));
        superOption.setLocation(mx + S(20), my + S(18) + mh * .42 + mh * .17);
        addObj(superOption);
    }

    private void addCornerAccent(double x, double y, boolean left, boolean top) {
        double len = S(10);
        GLine h = new GLine(x, y, x + (left ? len : -len), y);
        GLine v = new GLine(x, y, x, y + (top ? len : -len));
        h.setColor(ACCENT_GOLD);
        v.setColor(ACCENT_GOLD);
        addObj(h);
        addObj(v);
    }

    private void buildContinueButton() {
        double W = screenW();
        double H = screenH();
        double bw = S(180);
        double bh = S(48);
        double bx = W - bw - S(35);
        double by = H - bh - S(42);

        continueGlow = new GRect(bx - S(4), by - S(4), bw + S(8), bh + S(8));
        continueGlow.setFilled(true);
        continueGlow.setFillColor(new Color(212, 175, 55, 28));
        continueGlow.setColor(new Color(212, 175, 55, 60));
        addObj(continueGlow);

        continueButton = new GRect(bx, by, bw, bh);
        continueButton.setFilled(true);
        continueButton.setFillColor(new Color(55, 40, 10));
        continueButton.setColor(ACCENT_GOLD);
        addObj(continueButton);

        continueLabel = new GLabel("CONTINUE  ▶");
        continueLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(11, (int) S(16))));
        continueLabel.setColor(ACCENT_GOLD);
        continueLabel.setLocation(bx + bw * .12, by + bh * .65);
        addObj(continueLabel);
    }

    private void buildBattleMessage() {
        battleMessageLabel = new GLabel("");
        battleMessageLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(20))));
        battleMessageLabel.setColor(TEXT_BRIGHT);
        battleMessageLabel.setLocation(X(450), Y(100));
        addObj(battleMessageLabel);
    }

    private void setBattleMessage(String msg) {
        if (battleMessageLabel != null) {
            battleMessageLabel.setLabel(msg);
            battleMessageLabel.setLocation(X(450), Y(100));
        }
    }

    private void gainSuper(double amount) {
        if (battleOver) return;

        double next = Math.min(SUPER_MAX, h1.getSuperMeter() + amount);
        h1.setSuperMeter((int) next);

        boolean wasFull = superReady;
        superReady = (next >= SUPER_MAX);
        updateSuperMeter();

        if (superReady && !wasFull) {
            superOption.setColor(new Color(210, 170, 255));
            pulseSuperLabel();
        }
    }

    private void pulseSuperLabel() {
        if (superReadyLabel == null) return;

        int[] fr = {0};
        int total = 30;
        javax.swing.Timer t = new javax.swing.Timer(40, null);
        t.addActionListener(e -> {
            fr[0]++;
            double p = (double) fr[0] / total;
            int alpha;

            if (p < 0.3) {
                alpha = (int) (255 * (p / 0.3));
            } else if (p < 0.7) {
                alpha = 255;
            } else {
                alpha = (int) (255 * ((1 - p) / 0.3));
            }

            superReadyLabel.setColor(new Color(200, 160, 255, Math.max(0, Math.min(255, alpha))));
            if (fr[0] >= total) {
                t.stop();
                superReadyLabel.setColor(new Color(200, 160, 255, superReady ? 160 : 0));
            }
        });
        t.start();
    }

    private double getTargetY(GImage sprite, String part) {
        double top = sprite.getY();
        double h = sprite.getHeight();

        if (part.equals("HEAD")) return top + h * 0.15;
        if (part.equals("MIDDLE")) return top + h * 0.50;
        return top + h * 0.82;
    }

    private double getTargetX(GImage sprite) {
        return sprite.getX() + sprite.getWidth() / 2.0;
    }

    private String aiChooseAttack() {
        enemyPhase2 = (double) s1.getHP() / enemyMaxHP < 0.40;

        if (playerHistory.size() >= 2) {
            String l1 = playerHistory.get(playerHistory.size() - 1);
            String l2 = playerHistory.get(playerHistory.size() - 2);
            if (l1.equals("HEAD") && l2.equals("HEAD")) return "LEGS";
        }

        double r = Math.random();
        if (!enemyPhase2) {
            if (r < 0.60) return "HEAD";
            if (r < 0.85) return "MIDDLE";
            return "LEGS";
        } else {
            if (r < 0.40) return "HEAD";
            if (r < 0.80) return "MIDDLE";
            return "LEGS";
        }
    }

    private String aiChooseDefense() {
        if (playerHistory.isEmpty()) return randPart();
        if (enemyPhase2 && Math.random() < 0.35) return randPart();

        int head = 0;
        int mid = 0;
        int legs = 0;
        int look = Math.min(4, playerHistory.size());

        for (int i = playerHistory.size() - look; i < playerHistory.size(); i++) {
            String s = playerHistory.get(i);
            if (s.equals("HEAD")) head++;
            else if (s.equals("MIDDLE")) mid++;
            else legs++;
        }

        if (head >= mid && head >= legs) return "HEAD";
        if (mid >= head && mid >= legs) return "MIDDLE";
        return "LEGS";
    }

    private String randPart() {
        double r = Math.random();
        if (r < 0.33) return "HEAD";
        if (r < 0.67) return "MIDDLE";
        return "LEGS";
    }

    private void recordHistory(String part) {
        playerHistory.add(part);
        if (playerHistory.size() > 6) {
            playerHistory.remove(0);
        }
    }

    private void flinchSprite(GImage sprite, Runnable onDone) {
        double ox = sprite.getX();
        double oy = sprite.getY();
        int[] fr = {0};
        int total = 18;
        double amp = S(10);

        flinchTimer = new javax.swing.Timer(16, null);
        flinchTimer.addActionListener(evt -> {
            fr[0]++;
            double t = (double) fr[0] / total;
            sprite.setLocation(ox + Math.sin(fr[0] * 1.8) * amp * (1 - t), oy);

            if (fr[0] >= total) {
                flinchTimer.stop();
                sprite.setLocation(ox, oy);
                if (onDone != null) onDone.run();
            }
        });
        flinchTimer.start();
    }

    private void openAttackSelection() {
        if (!battleOver) {
            buildSelectionOverlay("ATTACK SLUJUPITER", "Choose where to strike.", "slujupiter.png");
        }
    }

    private void openDefenseSelection() {
        if (!battleOver) {
            buildSelectionOverlay("DEFEND HUEMAN", "Choose what part to protect.", getHuemanImage());
        }
    }

    private void buildSelectionOverlay(String title, String subtitle, String img) {
        clearOverlay();

        double W = screenW();
        double H = screenH();

        overlayShade = new GRect(0, 0, W, H);
        overlayShade.setFilled(true);
        overlayShade.setFillColor(new Color(0, 0, 0, 165));
        overlayShade.setColor(new Color(0, 0, 0, 0));
        addOverlayObj(overlayShade);

        double pw = S(520);
        double ph = S(520);
        double px = (W - pw) / 2;
        double py = (H - ph) / 2;

        GRect sh = new GRect(px + S(8), py + S(8), pw, ph);
        sh.setFilled(true);
        sh.setFillColor(new Color(0, 0, 0, 140));
        sh.setColor(new Color(0, 0, 0, 0));
        addOverlayObj(sh);

        overlayPanel = new GRect(px, py, pw, ph);
        overlayPanel.setFilled(true);
        overlayPanel.setFillColor(PANEL_BG);
        overlayPanel.setColor(PANEL_BORDER);
        addOverlayObj(overlayPanel);

        overlayInnerBorder = new GRect(px + S(8), py + S(8), pw - S(16), ph - S(16));
        overlayInnerBorder.setFilled(false);
        overlayInnerBorder.setColor(new Color(212, 175, 55, 90));
        addOverlayObj(overlayInnerBorder);

        overlayTitle = new GLabel(title);
        overlayTitle.setFont(new Font("Georgia", Font.BOLD, Math.max(14, (int) S(28))));
        overlayTitle.setColor(ACCENT_GOLD);
        overlayTitle.setLocation(px + S(28), py + S(42));
        addOverlayObj(overlayTitle);

        overlaySubtitle = new GLabel(subtitle);
        overlaySubtitle.setFont(new Font("Georgia", Font.PLAIN, Math.max(11, (int) S(16))));
        overlaySubtitle.setColor(TEXT_BRIGHT);
        overlaySubtitle.setLocation(px + S(28), py + S(72));
        addOverlayObj(overlaySubtitle);

        overlayImage = new GImage(img);
        double sc = Math.min(pw * .40 / overlayImage.getWidth(), ph * .42 / overlayImage.getHeight());
        overlayImage.scale(sc);
        overlayImage.setLocation(px + (pw - overlayImage.getWidth()) / 2, py + S(105));
        addOverlayObj(overlayImage);

        double btnW = pw - S(90);
        double btnH = S(48);
        double btnX = px + S(45);
        double fy = py + ph - S(185);
        double gap = S(18);

        for (int i = 0; i < 3; i++) {
            targetButtons[i] = new GRect(btnX, fy + i * (btnH + gap), btnW, btnH);
            targetButtons[i].setFilled(true);
            targetButtons[i].setFillColor(new Color(55, 75, 180, 80));
            targetButtons[i].setColor(ACCENT_GOLD);
            addOverlayObj(targetButtons[i]);

            targetLabels[i] = new GLabel(targetNames[i]);
            targetLabels[i].setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(18))));
            targetLabels[i].setColor(TEXT_BRIGHT);
            targetLabels[i].setLocation(btnX + S(22), fy + i * (btnH + gap) + btnH * .67);
            addOverlayObj(targetLabels[i]);
        }

        selectionOverlayOpen = true;
        choosingPlayerAttack = title.startsWith("ATTACK");
        choosingPlayerDefense = title.startsWith("DEFEND");
    }

    private void clearOverlay() {
        for (GObject o : overlayObjects) {
            mainScreen.remove(o);
            contents.remove(o);
        }
        overlayObjects.clear();

        for (int i = 0; i < 3; i++) {
            targetButtons[i] = null;
            targetLabels[i] = null;
        }

        selectionOverlayOpen = false;
        choosingPlayerAttack = false;
        choosingPlayerDefense = false;
    }

    private void clearEndOverlay() {
        for (GObject o : endOverlayObjects) {
            mainScreen.remove(o);
            contents.remove(o);
        }
        endOverlayObjects.clear();
    }

    private void resolvePlayerAttack(String chosenTarget) {
        recordHistory(chosenTarget);
        String def = aiChooseDefense();
        clearOverlay();

        if (!chosenTarget.equals(def)) {
            animatePlayerProjectile(chosenTarget, def);
        } else {
            setBattleMessage("Slujupiter is defending " + def + "...");
            animateShieldBlock(getTargetX(enemyImage), getTargetY(enemyImage, def), def, true);
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
            animateShieldBlock(getTargetX(huemanImage), getTargetY(huemanImage, att), att, false);
        }
    }

    private void resolvePlayerSuper() {
        if (!superReady || battleOver) return;

        clearOverlay();
        h1.setSuperMeter(0);
        superReady = false;
        superOption.setColor(new Color(130, 60, 255, 90));
        if (superReadyLabel != null) {
            superReadyLabel.setColor(new Color(200, 160, 255, 0));
        }
        updateSuperMeter();
        setBattleMessage("★ SUPER ATTACK! ★");
        fireSuperSequence(0);
    }

    private void animatePlayerProjectile(String chosenTarget, String enemyDefense) {
        if (animating) return;
        animating = true;

        double sx = getTargetX(huemanImage);
        double sy = huemanImage.getY() + huemanImage.getHeight() * .40;
        double ex = getTargetX(enemyImage);
        double ey = getTargetY(enemyImage, chosenTarget);

        double r = S(14);
        projectile = new GOval(sx - r, sy - r, r * 2, r * 2);
        projectile.setFilled(true);
        projectile.setFillColor(new Color(255, 80, 80));
        projectile.setColor(new Color(255, 200, 200));
        mainScreen.add(projectile);
        contents.add(projectile);

        int steps = 30;
        double dx = (ex - sx) / steps;
        double dy = (ey - sy) / steps;
        int[] cnt = {0};

        animTimer = new javax.swing.Timer(16, null);
        animTimer.addActionListener(e -> {
            cnt[0]++;
            projectile.move(dx, dy);

            if (cnt[0] >= steps) {
                animTimer.stop();
                mainScreen.remove(projectile);
                contents.remove(projectile);
                projectile = null;
                animating = false;

                setBattleMessage("Hit! Slujupiter defended " + enemyDefense + ".");
                explodeThenFlinch(getTargetX(enemyImage), getTargetY(enemyImage, chosenTarget), true, () -> {
                    gainSuper(SUPER_GAIN_HIT);
                    int dmg = getOneFifthDamage(enemyMaxHP);
                    s1.takeDamage(dmg);
                    animateEnemyHPDrain(
                            enemyHealthBack.getWidth() * ((s1.getHP() + dmg) / enemyMaxHP),
                            enemyHealthBack.getWidth() * Math.max(0, (double) s1.getHP() / enemyMaxHP)
                    );
                });
            }
        });
        animTimer.start();
    }

    private void fireSuperSequence(int idx) {
        if (idx >= 3) {
            int dmg = (int) Math.ceil(enemyMaxHP * 0.60);
            s1.takeDamage(dmg);
            animateEnemyHPDrain(
                    enemyHealthBack.getWidth() * ((s1.getHP() + dmg) / enemyMaxHP),
                    enemyHealthBack.getWidth() * Math.max(0, (double) s1.getHP() / enemyMaxHP)
            );
            return;
        }

        String t = SUPER_TARGETS[idx];
        double sx = getTargetX(huemanImage);
        double sy = huemanImage.getY() + huemanImage.getHeight() * .40;
        double ex = getTargetX(enemyImage);
        double ey = getTargetY(enemyImage, t);

        double r = S(16);
        GOval p = new GOval(sx - r, sy - r, r * 2, r * 2);
        p.setFilled(true);
        p.setFillColor(new Color(200, 100, 255));
        p.setColor(new Color(240, 200, 255));
        mainScreen.add(p);
        contents.add(p);

        int steps = 20;
        double pdx = (ex - sx) / steps;
        double pdy = (ey - sy) / steps;
        int[] cnt = {0};

        javax.swing.Timer t2 = new javax.swing.Timer(12, null);
        t2.addActionListener(e -> {
            cnt[0]++;
            p.move(pdx, pdy);

            if (cnt[0] >= steps) {
                t2.stop();
                mainScreen.remove(p);
                contents.remove(p);
                miniBurst(ex, ey);

                javax.swing.Timer w = new javax.swing.Timer(180, e2 -> {
                    ((javax.swing.Timer) e2.getSource()).stop();
                    fireSuperSequence(idx + 1);
                });
                w.setRepeats(false);
                w.start();
            }
        });
        t2.start();
    }

    private void miniBurst(double cx, double cy) {
        for (int i = 0; i < 8; i++) {
            double a = (2 * Math.PI / 8) * i;
            double r = S(6);

            GOval p = new GOval(cx - r, cy - r, r * 2, r * 2);
            p.setFilled(true);
            p.setFillColor(new Color(200, 100, 255));
            p.setColor(new Color(240, 200, 255));
            mainScreen.add(p);
            contents.add(p);

            double vx = Math.cos(a) * S(5);
            double vy = Math.sin(a) * S(5);
            int[] fr = {0};

            javax.swing.Timer bt = new javax.swing.Timer(16, null);
            bt.addActionListener(e -> {
                fr[0]++;
                p.move(vx, vy);
                p.setFillColor(new Color(200, 100, 255, Math.max(0, 255 - fr[0] * 30)));
                if (fr[0] >= 8) {
                    bt.stop();
                    mainScreen.remove(p);
                    contents.remove(p);
                }
            });
            bt.start();
        }
    }

    private void explodeThenFlinch(double cx, double cy, boolean onEnemy, Runnable onDone) {
        int n = 12;
        bombParticles.clear();

        for (int i = 0; i < n; i++) {
            double r = S(8) + Math.random() * S(6);
            GOval p = new GOval(cx - r, cy - r, r * 2, r * 2);
            p.setFilled(true);
            p.setFillColor(new Color(200 + (int) (Math.random() * 55), (int) (Math.random() * 120), 0));
            p.setColor(new Color(255, 220, 0));
            mainScreen.add(p);
            contents.add(p);
            bombParticles.add(p);
        }

        GOval ring = new GOval(cx - S(30), cy - S(30), S(60), S(60));
        ring.setFilled(false);
        ring.setColor(new Color(255, 220, 50));
        mainScreen.add(ring);
        contents.add(ring);
        bombParticles.add(ring);

        double[] ang = new double[n];
        for (int i = 0; i < n; i++) {
            ang[i] = (2 * Math.PI / n) * i;
        }

        int[] fr = {0};
        int tot = 25;
        bombTimer = new javax.swing.Timer(16, null);
        bombTimer.addActionListener(e -> {
            fr[0]++;
            double pg = (double) fr[0] / tot;

            for (int i = 0; i < n; i++) {
                bombParticles.get(i).move(Math.cos(ang[i]) * S(4) + Math.random() * S(2), Math.sin(ang[i]) * S(4) + Math.random() * S(2));
                bombParticles.get(i).setFillColor(new Color(255, Math.max(0, (int) (120 * (1 - pg))), 0, (int) (255 * (1 - pg))));
            }

            ring.setSize(S(60) + pg * S(80), S(60) + pg * S(80));
            ring.setLocation(cx - (S(30) + pg * S(40)), cy - (S(30) + pg * S(40)));
            ring.setColor(new Color(255, 220, 50, (int) (255 * (1 - pg))));

            if (fr[0] >= tot) {
                bombTimer.stop();
                for (GOval p : bombParticles) {
                    mainScreen.remove(p);
                    contents.remove(p);
                }
                bombParticles.clear();

                GImage sprite = onEnemy ? enemyImage : huemanImage;
                flinchSprite(sprite, onDone);
            }
        });
        bombTimer.start();
    }

    private void animateEnemyProjectile(String enemyAttack, String chosenDefense) {
        if (animating) return;
        animating = true;

        setBattleMessage("Slujupiter fires at your " + enemyAttack + "!");
        double sx = getTargetX(enemyImage);
        double sy = enemyImage.getY() + enemyImage.getHeight() * .40;
        double ex = getTargetX(huemanImage);
        double ey = getTargetY(huemanImage, enemyAttack);

        double r = S(14);
        projectile = new GOval(sx - r, sy - r, r * 2, r * 2);
        projectile.setFilled(true);
        projectile.setFillColor(new Color(100, 255, 220));
        projectile.setColor(new Color(220, 255, 245));
        mainScreen.add(projectile);
        contents.add(projectile);

        int steps = 30;
        double dx = (ex - sx) / steps;
        double dy = (ey - sy) / steps;
        int[] cnt = {0};

        animTimer = new javax.swing.Timer(16, null);
        animTimer.addActionListener(e -> {
            cnt[0]++;
            projectile.move(dx, dy);

            if (cnt[0] >= steps) {
                animTimer.stop();
                mainScreen.remove(projectile);
                contents.remove(projectile);
                projectile = null;
                animating = false;

                setBattleMessage("Slujupiter hit your " + enemyAttack + "!");
                explodeThenFlinch(ex, ey, false, () -> {
                    gainSuper(SUPER_GAIN_DMGTAKEN);
                    int dmg = getOneFifthDamage(huemanMaxHP);
                    h1.takeDamage(dmg);
                    animatePlayerHPDrain(
                            huemanHealthBack.getWidth() * ((h1.getHP() + dmg) / huemanMaxHP),
                            huemanHealthBack.getWidth() * Math.max(0, (double) h1.getHP() / huemanMaxHP)
                    );
                });
            }
        });
        animTimer.start();
    }

    private void animateEnemyHPDrain(double sw, double ew) {
        int tot = 40;
        int[] fr = {0};

        hpDrainTimer = new javax.swing.Timer(20, null);
        hpDrainTimer.addActionListener(e -> {
            fr[0]++;
            double p = (double) fr[0] / tot;
            double ep = 1 - Math.pow(1 - p, 2);

            enemyHealthBar.setSize(Math.max(0, sw + (ew - sw) * ep), enemyHealthBack.getHeight());
            enemyHealthBar.setFillColor(fr[0] % 4 < 2 ? new Color(180, 255, 240) : ENEMY_HP);

            if (fr[0] >= tot) {
                hpDrainTimer.stop();
                enemyHealthBar.setFillColor(ENEMY_HP);
                enemyHealthBar.setSize(Math.max(0, ew), enemyHealthBack.getHeight());
                checkBattleEnd();
                if (!battleOver) {
                    playerTurn = false;
                    showEnemyTurnIntro();
                }
            }
        });
        hpDrainTimer.start();
    }

    private void animatePlayerHPDrain(double sw, double ew) {
        int tot = 40;
        int[] fr = {0};

        hpDrainTimer = new javax.swing.Timer(20, null);
        hpDrainTimer.addActionListener(e -> {
            fr[0]++;
            double p = (double) fr[0] / tot;
            double ep = 1 - Math.pow(1 - p, 2);

            huemanHealthBar.setSize(Math.max(0, sw + (ew - sw) * ep), huemanHealthBack.getHeight());
            huemanHealthBar.setFillColor(fr[0] % 4 < 2 ? new Color(255, 255, 100) : getPlayerColor());

            if (fr[0] >= tot) {
                hpDrainTimer.stop();
                huemanHealthBar.setFillColor(getPlayerColor());
                huemanHealthBar.setSize(Math.max(0, ew), huemanHealthBack.getHeight());
                updateHealthBars();
                checkBattleEnd();
                if (!battleOver) {
                    playerTurn = true;
                    setBattleMessage("Your turn.");
                }
            }
        });
        hpDrainTimer.start();
    }

    private void animateShieldBlock(double cx, double cy, String part, boolean onEnemy) {
        final double targetY = onEnemy ? getTargetY(enemyImage, part) : getTargetY(huemanImage, part);

        double shW = S(80);
        double shH = S(100);
        double startX = onEnemy ? cx + S(200) : cx - S(200);
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

        int sf = 15;
        int hf = 20;
        int ff = 15;
        int tot = sf + hf + ff;
        int[] fr = {0};
        double sd = onEnemy ? startX - targX : targX - startX;

        shieldTimer = new javax.swing.Timer(16, null);
        shieldTimer.addActionListener(e -> {
            fr[0]++;

            if (fr[0] <= sf) {
                double pg = (double) fr[0] / sf;
                double ea = 1 - Math.pow(1 - pg, 3);
                double curX = onEnemy ? startX - sd * ea : startX + sd * ea;

                shield.setLocation(curX, shY);
                lbl.setLocation(curX + S(5), targetY + S(8));
            } else if (fr[0] <= sf + hf) {
                shield.setFillColor(fr[0] % 4 < 2 ? new Color(120, 150, 255, 200) : new Color(60, 80, 180, 180));
            } else {
                double pg = (double) (fr[0] - sf - hf) / ff;
                int a = (int) (180 * (1 - pg));

                shield.setFillColor(new Color(60, 80, 180, Math.max(0, a)));
                lbl.setColor(new Color(255, 255, 255, Math.max(0, a)));
            }

            if (fr[0] >= tot) {
                shieldTimer.stop();
                mainScreen.remove(shield);
                contents.remove(shield);
                mainScreen.remove(lbl);
                contents.remove(lbl);
                shield = null;

                if (onEnemy) {
                    playerTurn = false;
                    showEnemyTurnIntro();
                } else {
                    playerTurn = true;
                    setBattleMessage("Your turn.");
                }
            }
        });
        shieldTimer.start();
    }

    private void showEnemyTurnIntro() {
        if (battleOver) return;

        setBattleMessage("Slujupiter is preparing an attack...");
        javax.swing.Timer t = new javax.swing.Timer(700, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            openDefenseSelection();
        });
        t.setRepeats(false);
        t.start();
    }

    private void updateHealthBars() {
        double enemyRatio = Math.max(0, Math.min(1, (double) s1.getHP() / enemyMaxHP));
        enemyHealthBar.setSize(enemyHealthBack.getWidth() * enemyRatio, enemyHealthBack.getHeight());

        double playerRatio = Math.max(0, Math.min(1, (double) h1.getHP() / huemanMaxHP));
        huemanHealthBar.setSize(huemanHealthBack.getWidth() * playerRatio, huemanHealthBack.getHeight());

        hpValueLabel.setLabel((int) h1.getHP() + " / " + (int) huemanMaxHP);
        hpValueLabel.setLocation(hudPanel.getX() + hudPanel.getWidth() * .80, hudPanel.getY() + hudPanel.getHeight() * .58);

        updateSuperMeter();
    }

    private void updateSuperMeter() {
        double ratio = Math.max(0, Math.min(1, h1.getSuperMeter() / SUPER_MAX));
        superBar.setSize(superBack.getWidth() * ratio, superBack.getHeight());
    }

    private int getOneFifthDamage(double maxHp) {
        return Math.max(1, (int) Math.ceil(maxHp / 5.0));
    }

    private void checkBattleEnd() {
        updateHealthBars();

        if (s1.getHP() <= 0) {
            battleOver = true;
            setBattleMessage("Slujupiter was defeated!");
            goToNextScreenAfterDelay();
        } else if (h1.getHP() <= 0) {
            battleOver = true;
            setBattleMessage("Hueman was defeated!");
            goToNextScreenAfterDelay();
        }
    }

    private void goToNextScreenAfterDelay() {
        javax.swing.Timer nextTimer = new javax.swing.Timer(4000, null);
        nextTimer.setRepeats(false);
        nextTimer.addActionListener(e -> {
            nextTimer.stop();
            mainScreen.setCurrentLevel(3);
            mainScreen.switchToLevelSelectScreen();
        });
        nextTimer.start();
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    private void addOverlayObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
        overlayObjects.add(obj);
    }

    private Color getPlayerColor() {
        String c = mainScreen.getSelectedColor();
        if (c == null) return HP_GREEN;
        if (c.equals("red")) return HP_RED;
        if (c.equals("blue")) return HP_BLUE;
        return HP_GREEN;
    }

    private String getHuemanImage() {
        String c = mainScreen.getSelectedColor();
        if (c == null) return "redback.png";
        if (c.equals("red")) return "redback.png";
        if (c.equals("green")) return "greenback.png";
        return "blueback.png";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (battleOver) return;
        if (animating) return;

        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == continueButton || obj == continueLabel || obj == continueGlow) {
            mainScreen.setCurrentLevel(3);
            mainScreen.switchToLevelSelectScreen();
            return;
        }

        if (selectionOverlayOpen) {
            for (int i = 0; i < 3; i++) {
                if (obj == targetButtons[i] || obj == targetLabels[i]) {
                    if (choosingPlayerAttack) {
                        resolvePlayerAttack(targetNames[i]);
                    } else if (choosingPlayerDefense) {
                        resolveEnemyAttack(targetNames[i]);
                    }
                    return;
                }
            }
        }

        if (playerTurn && !selectionOverlayOpen) {
            if (obj == fightOption || obj == fightHighlight) {
                openAttackSelection();
                return;
            }

            if (obj == superOption || obj == superHighlight) {
                if (superReady) {
                    resolvePlayerSuper();
                }
                return;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}