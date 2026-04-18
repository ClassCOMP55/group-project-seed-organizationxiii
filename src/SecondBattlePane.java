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

public class SecondBattlePane extends GraphicsPane {

    private Hueman h1;
    private MintSentinel m1;
    private double huemanMaxHP;
    private double mintMaxHP;

    private GImage huemanImage;
    private GImage mintImage;

    private GRect skyPanel;
    private GRect groundPanel;

    private GRect hudPanel;
    private GRect hudAccent;
    private GLabel huemanNameLabel;
    private GRect huemanHealthBack;
    private GRect huemanHealthBar;
    private GLabel hpValueLabel;

    private GRect superBack;
    private GRect superBar;

    private GRect enemyHudPanel;
    private GRect enemyHudAccent;
    private GLabel mintNameLabel;
    private GRect mintHealthBack;
    private GRect mintHealthBar;
    private GLabel mintHpLabel;

    private GRect menuPanel;
    private GRect menuInnerBorder;
    private GLabel fightOption;
    private GLabel abilityOption;
    private GRect fightHighlight;
    private GRect abilityHighlight;

    private GRect continueButton;
    private GLabel continueLabel;
    private GRect continueGlow;

    private boolean showingFightMenu = false;
    private boolean playerTurn = true;
    private boolean battleOver = false;

    private static final Color BG_TOP       = new Color(10, 12, 28);
    private static final Color BG_MID       = new Color(18, 22, 45);
    private static final Color GROUND_COLOR = new Color(22, 28, 55);
    private static final Color PANEL_BG     = new Color(12, 15, 35, 220);
    private static final Color PANEL_BORDER = new Color(80, 100, 180);
    private static final Color ACCENT_GOLD  = new Color(212, 175, 55);
    private static final Color HP_RED       = new Color(220, 55, 55);
    private static final Color HP_GREEN     = new Color(55, 220, 100);
    private static final Color HP_BLUE      = new Color(55, 140, 255);
    private static final Color SUPER_COLOR  = new Color(130, 60, 255);
    private static final Color ENEMY_HP     = new Color(90, 240, 210);
    private static final Color TEXT_BRIGHT  = new Color(230, 235, 255);
    private static final Color MENU_BG      = new Color(8, 10, 25, 240);
    private static final Color HIGHLIGHT    = new Color(55, 75, 180, 80);

    private static final double BASE_W = 1536.0;
    private static final double BASE_H = 991.0;

    private double scale;
    private double offsetX;
    private double offsetY;

    public SecondBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        updateLayoutScale();

        h1 = mainScreen.getPlayer();
        m1 = new MintSentinel(900, 15, 10, "Mint");

        huemanMaxHP = h1.getHP();
        mintMaxHP = m1.getHP();

        showingFightMenu = false;
        playerTurn = true;
        battleOver = false;

        buildBackground();
        drawBattleFrame();
        buildSprites();
        buildEnemyHUD();
        buildPlayerHUD();
        buildActionMenu();
        buildContinueButton();
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

    private double screenW() {
        double w = mainScreen.getWidth();
        return (w > 0) ? w : MainApplication.WINDOW_WIDTH;
    }

    private double screenH() {
        double h = mainScreen.getHeight();
        return (h > 0) ? h : MainApplication.WINDOW_HEIGHT;
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

//        GOval playerGlow = new GOval(X(275), Y(405), S(160), S(160));
//        playerGlow.setFilled(true);
//        playerGlow.setFillColor(new Color(30, 80, 30, 28));
//        playerGlow.setColor(new Color(0, 0, 0, 0));
//        addObj(playerGlow);
//
//        GOval enemyGlow = new GOval(X(930), Y(110), S(280), S(280));
//        enemyGlow.setFilled(true);
//        enemyGlow.setFillColor(new Color(50, 140, 120, 24));
//        enemyGlow.setColor(new Color(0, 0, 0, 0));
//        addObj(enemyGlow);
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
        mintImage = new GImage("mint.png");

        huemanImage.scale(0.52);
        mintImage.scale(0.60);

        double hStoneW = S(170);
        double hStoneH = S(35);
        double hCenterX = X(340);
        double hStoneY = Y(700);

        GOval hShadow = new GOval(
                hCenterX - hStoneW * 0.45,
                hStoneY + hStoneH * 0.75,
                hStoneW * 0.9,
                hStoneH * 0.5
        );
        hShadow.setFilled(true);
        hShadow.setFillColor(new Color(0, 0, 0, 70));
        hShadow.setColor(new Color(0, 0, 0, 0));
        addObj(hShadow);

        GRect hSlab2 = new GRect(
                hCenterX - hStoneW / 2 - S(6),
                hStoneY + S(8),
                hStoneW + S(12),
                hStoneH * 0.55
        );
        hSlab2.setFilled(true);
        hSlab2.setFillColor(new Color(42, 38, 55));
        hSlab2.setColor(new Color(28, 25, 40));
        addObj(hSlab2);

        GRect hSlab1 = new GRect(hCenterX - hStoneW / 2, hStoneY, hStoneW, hStoneH);
        hSlab1.setFilled(true);
        hSlab1.setFillColor(new Color(92, 84, 116));
        hSlab1.setColor(new Color(58, 52, 76));
        addObj(hSlab1);

        double hx = hCenterX - huemanImage.getWidth() / 2 + S(110);
        double hy = hStoneY - huemanImage.getHeight() - S(8);
        huemanImage.setLocation(hx, hy);
        addObj(huemanImage);

        double dStoneW = S(190);
        double dStoneH = S(36);
        double dCenterX = X(1120);
        double dStoneY = Y(480);

        GOval dShadow = new GOval(
                dCenterX - dStoneW * 0.46,
                dStoneY + dStoneH * 0.75,
                dStoneW * 0.92,
                dStoneH * 0.5
        );
        dShadow.setFilled(true);
        dShadow.setFillColor(new Color(0, 0, 0, 70));
        dShadow.setColor(new Color(0, 0, 0, 0));
        addObj(dShadow);

        GRect dSlab2 = new GRect(
                dCenterX - dStoneW / 2 - S(6),
                dStoneY + S(8),
                dStoneW + S(12),
                dStoneH * 0.55
        );
        dSlab2.setFilled(true);
        dSlab2.setFillColor(new Color(40, 70, 62));
        dSlab2.setColor(new Color(24, 44, 40));
        addObj(dSlab2);

        GRect dSlab1 = new GRect(dCenterX - dStoneW / 2, dStoneY, dStoneW, dStoneH);
        dSlab1.setFilled(true);
        dSlab1.setFillColor(new Color(95, 170, 150));
        dSlab1.setColor(new Color(55, 110, 98));
        addObj(dSlab1);

        double dx = dCenterX - mintImage.getWidth() / 2;
        double dy = dStoneY - mintImage.getHeight() + S(12);
        mintImage.setLocation(dx, dy);
        addObj(mintImage);
    }

    private void buildEnemyHUD() {
        double px = X(30);
        double py = Y(38);
        double pw = S(290);
        double ph = S(85);

        GRect shadow = new GRect(px + S(4), py + S(4), pw, ph);
        shadow.setFilled(true);
        shadow.setFillColor(new Color(0, 0, 0, 120));
        shadow.setColor(new Color(0, 0, 0, 0));
        addObj(shadow);

        enemyHudPanel = new GRect(px, py, pw, ph);
        enemyHudPanel.setFilled(true);
        enemyHudPanel.setFillColor(PANEL_BG);
        enemyHudPanel.setColor(PANEL_BORDER);
        addObj(enemyHudPanel);

        enemyHudAccent = new GRect(px, py, S(5), ph);
        enemyHudAccent.setFilled(true);
        enemyHudAccent.setFillColor(ENEMY_HP);
        enemyHudAccent.setColor(ENEMY_HP);
        addObj(enemyHudAccent);

        mintNameLabel = new GLabel(m1.getName().toUpperCase());
        mintNameLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(28))));
        mintNameLabel.setColor(ENEMY_HP);
        mintNameLabel.setLocation(px + S(18), py + ph * 0.35);
        addObj(mintNameLabel);

        mintHpLabel = new GLabel("HP");
        mintHpLabel.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        mintHpLabel.setColor(TEXT_BRIGHT);
        mintHpLabel.setLocation(px + S(18), py + ph * 0.70);
        addObj(mintHpLabel);

        double bx = px + pw * 0.18;
        double by = py + ph * 0.52;
        double bw = pw * 0.68;
        double bh = ph * 0.18;

        mintHealthBack = new GRect(bx, by, bw, bh);
        mintHealthBack.setFilled(true);
        mintHealthBack.setFillColor(new Color(30, 30, 50));
        mintHealthBack.setColor(new Color(60, 60, 90));
        addObj(mintHealthBack);

        mintHealthBar = new GRect(bx, by, bw, bh);
        mintHealthBar.setFilled(true);
        mintHealthBar.setFillColor(ENEMY_HP);
        mintHealthBar.setColor(ENEMY_HP.darker());
        addObj(mintHealthBar);
    }

    private void buildPlayerHUD() {
        double W = screenW();
        double H = screenH();

        double pw = S(400);
        double ph = S(100);
        double px = W - pw - S(460);
        double py = H - ph - S(55);

        GRect shadow = new GRect(px + S(4), py + S(4), pw, ph);
        shadow.setFilled(true);
        shadow.setFillColor(new Color(0, 0, 0, 120));
        shadow.setColor(new Color(0, 0, 0, 0));
        addObj(shadow);

        hudPanel = new GRect(px, py, pw, ph);
        hudPanel.setFilled(true);
        hudPanel.setFillColor(PANEL_BG);
        hudPanel.setColor(PANEL_BORDER);
        addObj(hudPanel);

        Color accentColor = getPlayerColor();

        hudAccent = new GRect(px, py, S(6), ph);
        hudAccent.setFilled(true);
        hudAccent.setFillColor(accentColor);
        hudAccent.setColor(accentColor);
        addObj(hudAccent);

        huemanNameLabel = new GLabel(h1.getName().toUpperCase());
        huemanNameLabel.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(28))));
        huemanNameLabel.setColor(accentColor.brighter());
        huemanNameLabel.setLocation(px + S(18), py + ph * 0.30);
        addObj(huemanNameLabel);

        GLabel hpTag = new GLabel("HP");
        hpTag.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        hpTag.setColor(TEXT_BRIGHT);
        hpTag.setLocation(px + S(18), py + ph * 0.58);
        addObj(hpTag);

        double bx = px + pw * 0.16;
        double by = py + ph * 0.44;
        double bw = pw * 0.62;
        double bh = ph * 0.14;

        huemanHealthBack = new GRect(bx, by, bw, bh);
        huemanHealthBack.setFilled(true);
        huemanHealthBack.setFillColor(new Color(30, 30, 50));
        huemanHealthBack.setColor(new Color(60, 60, 90));
        addObj(huemanHealthBack);

        huemanHealthBar = new GRect(bx, by, bw, bh);
        huemanHealthBar.setFilled(true);
        huemanHealthBar.setFillColor(accentColor);
        huemanHealthBar.setColor(accentColor.darker());
        addObj(huemanHealthBar);

        hpValueLabel = new GLabel((int) h1.getHP() + " / " + (int) huemanMaxHP);
        hpValueLabel.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        hpValueLabel.setColor(TEXT_BRIGHT);
        hpValueLabel.setLocation(px + pw * 0.80, py + ph * 0.58);
        addObj(hpValueLabel);

        GLabel spTag = new GLabel("SP");
        spTag.setFont(new Font("Courier New", Font.BOLD, Math.max(10, (int) S(14))));
        spTag.setColor(new Color(180, 140, 255));
        spTag.setLocation(px + S(18), py + ph * 0.82);
        addObj(spTag);

        double sxBar = px + pw * 0.16;
        double syBar = py + ph * 0.68;
        double sw = pw * 0.62;
        double sh = ph * 0.12;

        superBack = new GRect(sxBar, syBar, sw, sh);
        superBack.setFilled(true);
        superBack.setFillColor(new Color(30, 20, 50));
        superBack.setColor(new Color(70, 50, 100));
        addObj(superBack);

        superBar = new GRect(sxBar, syBar, 0, sh);
        superBar.setFilled(true);
        superBar.setFillColor(SUPER_COLOR);
        superBar.setColor(SUPER_COLOR.darker());
        addObj(superBar);
    }

    private void buildActionMenu() {
        double H = screenH();

        double mw = S(210);
        double mh = S(120);
        double mx = X(30);
        double my = H - mh - S(45);

        GRect shadow = new GRect(mx + S(5), my + S(5), mw, mh);
        shadow.setFilled(true);
        shadow.setFillColor(new Color(0, 0, 0, 130));
        shadow.setColor(new Color(0, 0, 0, 0));
        addObj(shadow);

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

        fightHighlight = new GRect(mx + S(10), my + S(18), mw - S(20), mh * 0.28);
        fightHighlight.setFilled(true);
        fightHighlight.setFillColor(HIGHLIGHT);
        fightHighlight.setColor(new Color(0, 0, 0, 0));
        addObj(fightHighlight);

        fightOption = new GLabel("FIGHT");
        fightOption.setFont(new Font("Georgia", Font.BOLD, Math.max(12, (int) S(22))));
        fightOption.setColor(TEXT_BRIGHT);
        fightOption.setLocation(mx + S(20), my + S(18) + mh * 0.22);
        addObj(fightOption);
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
        continueLabel.setLocation(bx + bw * 0.12, by + bh * 0.65);
        addObj(continueLabel);
    }

    private void addObj(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
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

        GObject obj = mainScreen.getElementAtLocation(e.getX(), e.getY());

        if (obj == continueButton || obj == continueLabel || obj == continueGlow) {
            mainScreen.setCurrentLevel(2);
            mainScreen.switchToLevelSelectScreen();
            return;
        }

        if (!showingFightMenu) {
            if (obj == fightOption || obj == fightHighlight) {
                openFightMenu();
            }
        } else {
            if ((obj == fightOption || obj == fightHighlight) && playerTurn) {
                performPlayerAttack();
                checkBattleEnd();

                if (!battleOver) {
                    performEnemyTurn();
                    checkBattleEnd();
                }
            } else if (obj == abilityOption || obj == abilityHighlight) {
                if (h1.getSuperMeter() > 0 && playerTurn) {
                    h1.useAbility(1, m1);
                    updateHealthBars();
                    playerTurn = false;

                    checkBattleEnd();

                    if (!battleOver) {
                        performEnemyTurn();
                        checkBattleEnd();
                    }
                }
            }
        }
    }

    private void openFightMenu() {
        showingFightMenu = true;

        mainScreen.remove(fightOption);
        contents.remove(fightOption);
        mainScreen.remove(fightHighlight);
        contents.remove(fightHighlight);

        double mx = menuPanel.getX();
        double my = menuPanel.getY();
        double mw = menuPanel.getWidth();

        fightHighlight = new GRect(mx + S(8), my + S(14), mw - S(16), S(30));
        fightHighlight.setFilled(true);
        fightHighlight.setFillColor(HIGHLIGHT);
        fightHighlight.setColor(new Color(0, 0, 0, 0));
        addObj(fightHighlight);

        fightOption = new GLabel("⚔  Basic Attack");
        fightOption.setFont(new Font("Georgia", Font.BOLD, Math.max(11, (int) S(14))));
        fightOption.setColor(TEXT_BRIGHT);
        fightOption.setLocation(mx + S(22), my + S(36));
        addObj(fightOption);

        abilityHighlight = new GRect(mx + S(8), my + S(50), mw - S(16), S(30));
        abilityHighlight.setFilled(true);
        abilityHighlight.setFillColor(new Color(80, 30, 160, 60));
        abilityHighlight.setColor(new Color(0, 0, 0, 0));
        addObj(abilityHighlight);

        abilityOption = new GLabel("✦  Superpower");
        abilityOption.setFont(new Font("Georgia", Font.BOLD, Math.max(11, (int) S(14))));
        abilityOption.setColor(new Color(200, 160, 255));
        abilityOption.setLocation(mx + S(22), my + S(72));
        addObj(abilityOption);
    }

    private void performPlayerAttack() {
        if (!playerTurn || battleOver) return;
        h1.attackOpponent(m1);
        updateHealthBars();
        playerTurn = false;
    }

    private void performEnemyTurn() {
        if (battleOver) return;
        if (m1.isAlive()) {
            m1.takeTurn(h1);
            updateHealthBars();
        }
        playerTurn = true;
    }

    private void checkBattleEnd() {
        if (!m1.isAlive()) {
            battleOver = true;
            mainScreen.setCurrentLevel(2);
            mainScreen.switchToLevelSelectScreen();
            return;
        }

        if (!h1.isAlive()) {
            battleOver = true;
        }
    }

    private void updateHealthBars() {
        double hRatio = Math.max(0, (double) h1.getHP() / huemanMaxHP);
        double mRatio = Math.max(0, (double) m1.getHP() / mintMaxHP);

        huemanHealthBar.setSize(
                huemanHealthBack.getWidth() * hRatio,
                huemanHealthBack.getHeight()
        );

        mintHealthBar.setSize(
                mintHealthBack.getWidth() * mRatio,
                mintHealthBack.getHeight()
        );

        if (hRatio < 0.25) {
            huemanHealthBar.setFillColor(HP_RED);
            huemanHealthBar.setColor(HP_RED.darker());
        } else {
            Color accentColor = getPlayerColor();
            huemanHealthBar.setFillColor(accentColor);
            huemanHealthBar.setColor(accentColor.darker());
        }

        hpValueLabel.setLabel(Math.max(0, (int) h1.getHP()) + " / " + (int) huemanMaxHP);
        updateSuperMeter();
    }

    private void updateSuperMeter() {
        double ratio = Math.min(1.0, (double) h1.getSuperMeter() / 500.0);
        superBar.setSize(superBack.getWidth() * ratio, superBack.getHeight());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (huemanImage == null) return;

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            huemanImage.move(-10, 0);
        } else if (key == KeyEvent.VK_RIGHT) {
            huemanImage.move(10, 0);
        } else if (key == KeyEvent.VK_UP) {
            huemanImage.move(0, -10);
        } else if (key == KeyEvent.VK_DOWN) {
            huemanImage.move(0, 10);
        }
    }

    @Override
    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}