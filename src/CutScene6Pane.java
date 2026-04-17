import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.Timer;
import acm.graphics.*;

public class CutScene6Pane extends GraphicsPane {

    private final String[] dialogue = {
        "You defeated Loathe...",
        "But your journey is still not over.",
        "The air shifts again with something familiar.",
        "A presence you already fought once begins to return.",
        "Decima has come back stronger for one final battle.",
        "This is the end. Prepare yourself."
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

    private GOval planetAtmo;

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

    public CutScene6Pane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        double W = mainScreen.getWidth();
        double H = mainScreen.getHeight();

        GRect skyTop = new GRect(0, 0, W, H * 0.55);
        skyTop.setFilled(true); skyTop.setFillColor(SKY_TOP); skyTop.setColor(SKY_TOP);
        add(skyTop);

        GRect skyMid = new GRect(0, H * 0.30, W, H * 0.35);
        skyMid.setFilled(true); skyMid.setFillColor(SKY_MID); skyMid.setColor(SKY_MID);
        add(skyMid);

        GRect skyHor = new GRect(0, H * 0.52, W, H * 0.10);
        skyHor.setFilled(true); skyHor.setFillColor(SKY_HOR); skyHor.setColor(SKY_HOR);
        add(skyHor);

        for (int i = 0; i < STAR_COUNT; i++) {
            double sx = Math.random() * W;
            double sy = Math.random() * H * 0.58;
            double sr = 0.7 + Math.random() * 2.0;
            starAlpha[i] = (float) Math.random();
            starSpeed[i] = 0.004f + (float)(Math.random() * 0.012f);
            bgStars[i] = new GOval(sx - sr, sy - sr, sr * 2, sr * 2);
            bgStars[i].setFilled(true);
            Color sc = new Color(230, 210, 255, Math.max(0, Math.min(255, (int)(starAlpha[i] * 220))));
            bgStars[i].setFillColor(sc); bgStars[i].setColor(sc);
            add(bgStars[i]);
        }

        double sm_r = H * 0.085;
        double sm_x = W * 0.12;
        double sm_y = H * 0.10;

        GOval smAtmo = new GOval(sm_x - sm_r*1.5, sm_y - sm_r*1.5, sm_r*3, sm_r*3);
        smAtmo.setFilled(true);
        smAtmo.setFillColor(new Color(140, 60, 180, 30));
        smAtmo.setColor(new Color(0,0,0,0));
        add(smAtmo);

        GOval smDisc = new GOval(sm_x - sm_r, sm_y - sm_r, sm_r*2, sm_r*2);
        smDisc.setFilled(true);
        smDisc.setFillColor(new Color(185, 140, 210));
        smDisc.setColor(new Color(160, 110, 190, 180));
        add(smDisc);

        double p_r = H * 0.27;
        double p_x = W * 0.78;
        double p_y = H * 0.24;

        planetAtmo = new GOval(p_x - p_r*1.35, p_y - p_r*1.35, p_r*2.7, p_r*2.7);
        planetAtmo.setFilled(true);
        planetAtmo.setFillColor(new Color(200, 80, 160, 22));
        planetAtmo.setColor(new Color(0,0,0,0));
        add(planetAtmo);

        GOval pGlow = new GOval(p_x - p_r*1.12, p_y - p_r*1.12, p_r*2.24, p_r*2.24);
        pGlow.setFilled(true);
        pGlow.setFillColor(new Color(210, 90, 160, 35));
        pGlow.setColor(new Color(0,0,0,0));
        add(pGlow);

        GOval planet = new GOval(p_x - p_r, p_y - p_r, p_r*2, p_r*2);
        planet.setFilled(true);
        planet.setFillColor(new Color(210, 100, 160));
        planet.setColor(new Color(180, 70, 130, 200));
        add(planet);

        drawTerrain(W, H);

        for (int i = 0; i < DUST_COUNT; i++) {
            dustX[i] = Math.random() * W;
            dustY[i] = H * 0.55 + Math.random() * H * 0.20;
            dustVX[i] = (Math.random() - 0.5) * 0.4;
            dustVY[i] = -0.1 - Math.random() * 0.25;
            dustAlpha[i] = (float)(0.1 + Math.random() * 0.4);
            double dr = 1.5 + Math.random() * 3;
            dust[i] = new GOval(dustX[i]-dr, dustY[i]-dr, dr*2, dr*2);
            dust[i].setFilled(true);
            Color dc = new Color(200, 150, 230, Math.max(0,Math.min(255,(int)(dustAlpha[i]*200))));
            dust[i].setFillColor(dc); dust[i].setColor(dc);
            add(dust[i]);
        }

        double boxH = 170;
        double boxY2 = H - boxH - 18;
        double boxX2 = 38;
        double boxW2 = W - 76;

        dialogueBoxGlow = new GRect(boxX2-6, boxY2-6, boxW2+12, boxH+12);
        dialogueBoxGlow.setFilled(true);
        dialogueBoxGlow.setFillColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),14));
        dialogueBoxGlow.setColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),45));
        add(dialogueBoxGlow);

        dialogueBox = new GRect(boxX2, boxY2, boxW2, boxH);
        dialogueBox.setFilled(true);
        dialogueBox.setFillColor(PANEL_FILL);
        dialogueBox.setColor(PANEL_BORDER);
        add(dialogueBox);

        GRect dbInner = new GRect(boxX2+5, boxY2+5, boxW2-10, boxH-10);
        dbInner.setFilled(false);
        dbInner.setColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),28));
        add(dbInner);

        GRect topBar = new GRect(boxX2, boxY2, boxW2, 4);
        topBar.setFilled(true); topBar.setFillColor(GOLD); topBar.setColor(GOLD);
        add(topBar);

        double[][] boxCorners = {{boxX2,boxY2},{boxX2+boxW2,boxY2},{boxX2,boxY2+boxH},{boxX2+boxW2,boxY2+boxH}};
        for (double[] c : boxCorners) {
            GPolygon d = makeDiamond(c[0], c[1], 9);
            d.setFilled(true);
            d.setFillColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),180));
            d.setColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),240));
            add(d);
        }

        double npW = 170, npH = 34;
        double npX = boxX2 + 20, npY = boxY2 - npH + 2;
        GRect np = new GRect(npX, npY, npW, npH);
        np.setFilled(true); np.setFillColor(new Color(30,18,8)); np.setColor(PANEL_BORDER);
        add(np);
        GRect npBar = new GRect(npX, npY, 4, npH);
        npBar.setFilled(true); npBar.setFillColor(GOLD_DARK); npBar.setColor(GOLD_DARK);
        add(npBar);
        GLabel nameLabel = new GLabel("Hyperion");
        nameLabel.setFont("Times New Roman-Bold-20");
        nameLabel.setColor(GOLD);
        nameLabel.setLocation(npX+14, npY+23);
        add(nameLabel);

        GLabel quote = new GLabel("\u201C");
        quote.setFont("Times New Roman-Bold-48");
        quote.setColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),55));
        quote.setLocation(boxX2+10, boxY2+68);
        add(quote);

        dialogueText = new GLabel(dialogue[0]);
        dialogueText.setFont("Times New Roman-PLAIN-22");
        dialogueText.setColor(TEXT_MAIN);
        dialogueText.setLocation(boxX2+24, boxY2+52);
        add(dialogueText);

        int total = dialogue.length;
        double dotSpacing = 14;
        double dotsStartX = (W - (total-1)*dotSpacing) / 2.0;
        double dotsY = boxY2 + boxH - 22;
        progressDots = new GOval[total];
        for (int i = 0; i < total; i++) {
            GOval dot = new GOval(dotsStartX+i*dotSpacing-4, dotsY-4, 8, 8);
            dot.setFilled(true);
            dot.setFillColor(i==0 ? GOLD : new Color(60,55,40));
            dot.setColor(i==0 ? GOLD : new Color(90,80,55));
            add(dot);
            progressDots[i] = dot;
        }

        double btnH2 = 40, btnW3 = 110;
        double backX = boxX2 + 16, backY = boxY2 + boxH - btnH2 - 14;
        backButton = new GRect(backX, backY, btnW3, btnH2);
        backButton.setFilled(true);
        backButton.setFillColor(new Color(20,14,8));
        backButton.setColor(new Color(140,105,35,180));
        add(backButton);
        GRect backBar = new GRect(backX, backY, 3, btnH2);
        backBar.setFilled(true); backBar.setFillColor(GOLD_DARK); backBar.setColor(GOLD_DARK);
        add(backBar);
        backLabel = new GLabel("◀  Back");
        backLabel.setFont("Arial-Bold-15");
        backLabel.setColor(TEXT_DIM);
        backLabel.setLocation(backX+(btnW3-backLabel.getWidth())/2.0+2, backY+26);
        add(backLabel);

        double contW = 200;
        double contX = boxX2 + boxW2 - contW - 16;
        double contY = backY;
        GRect contGlow = new GRect(contX-4, contY-4, contW+8, btnH2+8);
        contGlow.setFilled(true);
        contGlow.setFillColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),22));
        contGlow.setColor(new Color(0,0,0,0));
        add(contGlow);
        continueButton = new GRect(contX, contY, contW, btnH2);
        continueButton.setFilled(true);
        continueButton.setFillColor(new Color(30,20,8));
        continueButton.setColor(PANEL_BORDER);
        add(continueButton);
        continueLabel = new GLabel("Continue  ▶");
        continueLabel.setFont("Arial-Bold-15");
        continueLabel.setColor(GOLD);
        continueLabel.setLocation(contX+(contW-continueLabel.getWidth())/2.0, contY+26);
        add(continueLabel);

        GLabel hint = new GLabel("Click anywhere to advance");
        hint.setFont("Arial-12");
        hint.setColor(new Color(100,88,65));
        hint.setLocation((W-hint.getWidth())/2.0, boxY2+boxH-5);
        add(hint);

        tick = 0;
        animTimer = new Timer(33, e -> animate());
        animTimer.start();
    }

    private void drawTerrain(double w, double h) {
        double groundY = h * 0.60;

        GRect farGround = new GRect(0, groundY, w, h - groundY);
        farGround.setFilled(true);
        farGround.setFillColor(new Color(45, 35, 60));
        farGround.setColor(new Color(45, 35, 60));
        add(farGround);

        GRect midGround = new GRect(0, groundY + h*0.06, w, h - (groundY + h*0.06));
        midGround.setFilled(true);
        midGround.setFillColor(new Color(38, 28, 52));
        midGround.setColor(new Color(38, 28, 52));
        add(midGround);

        GRect nearGround = new GRect(0, groundY + h*0.14, w, h - (groundY + h*0.14));
        nearGround.setFilled(true);
        nearGround.setFillColor(new Color(28, 20, 42));
        nearGround.setColor(new Color(28, 20, 42));
        add(nearGround);

        GLine horizGlow = new GLine(0, groundY, w, groundY);
        horizGlow.setColor(new Color(160, 80, 200, 80));
        add(horizGlow);
        GLine horizGlow2 = new GLine(0, groundY+1, w, groundY+1);
        horizGlow2.setColor(new Color(130, 60, 170, 50));
        add(horizGlow2);

        GPolygon rock1 = new GPolygon();
        rock1.addVertex(0, groundY + h*0.08);
        rock1.addVertex(w*0.18, groundY - h*0.04);
        rock1.addVertex(w*0.28, groundY + h*0.02);
        rock1.addVertex(w*0.35, groundY + h*0.10);
        rock1.addVertex(0, groundY + h*0.18);
        rock1.setFilled(true);
        rock1.setFillColor(new Color(52, 42, 65));
        rock1.setColor(new Color(70, 55, 88, 180));
        rock1.setLocation(0, 0);
        add(rock1);

        GPolygon rock2 = new GPolygon();
        rock2.addVertex(w*0.30, groundY + h*0.18);
        rock2.addVertex(w*0.36, groundY - h*0.01);
        rock2.addVertex(w*0.40, groundY + h*0.02);
        rock2.addVertex(w*0.46, groundY + h*0.18);
        rock2.setFilled(true);
        rock2.setFillColor(new Color(44, 34, 58));
        rock2.setColor(new Color(60, 48, 76, 160));
        rock2.setLocation(0, 0);
        add(rock2);

        GPolygon rock3 = new GPolygon();
        rock3.addVertex(w*0.60, groundY + h*0.18);
        rock3.addVertex(w*0.68, groundY + h*0.04);
        rock3.addVertex(w*0.76, groundY - h*0.02);
        rock3.addVertex(w*0.84, groundY + h*0.00);
        rock3.addVertex(w*0.90, groundY + h*0.06);
        rock3.addVertex(w*1.00, groundY + h*0.04);
        rock3.addVertex(w*1.00, groundY + h*0.18);
        rock3.setFilled(true);
        rock3.setFillColor(new Color(48, 38, 62));
        rock3.setColor(new Color(66, 52, 82, 160));
        rock3.setLocation(0, 0);
        add(rock3);

        GPolygon fore = new GPolygon();
        fore.addVertex(0, h);
        fore.addVertex(0, groundY + h*0.22);
        fore.addVertex(w*0.08, groundY + h*0.18);
        fore.addVertex(w*0.18, groundY + h*0.26);
        fore.addVertex(w*0.30, groundY + h*0.20);
        fore.addVertex(w*0.42, groundY + h*0.28);
        fore.addVertex(w*0.55, groundY + h*0.22);
        fore.addVertex(w*0.65, groundY + h*0.27);
        fore.addVertex(w*0.78, groundY + h*0.21);
        fore.addVertex(w*0.90, groundY + h*0.26);
        fore.addVertex(w*1.00, groundY + h*0.22);
        fore.addVertex(w*1.00, h);
        fore.setFilled(true);
        fore.setFillColor(new Color(18, 12, 28));
        fore.setColor(new Color(30, 20, 45, 160));
        fore.setLocation(0, 0);
        add(fore);
    }

    private void animate() {
        tick++;
        double W = mainScreen.getWidth();
        double H = mainScreen.getHeight();

        for (int i = 0; i < STAR_COUNT; i++) {
            if (bgStars[i] == null) continue;
            starAlpha[i] += starSpeed[i] * (i%2==0 ? 1 : -1);
            if (starAlpha[i] > 1f) { starAlpha[i]=1f; starSpeed[i]=-Math.abs(starSpeed[i]); }
            if (starAlpha[i] < 0f) { starAlpha[i]=0f; starSpeed[i]= Math.abs(starSpeed[i]); }
            int a = Math.max(0,Math.min(255,(int)(starAlpha[i]*220)));
            Color sc = new Color(230,210,255,a);
            bgStars[i].setFillColor(sc); bgStars[i].setColor(sc);
        }

        if (planetAtmo != null) {
            float p = (float)(0.5 + 0.5*Math.sin(tick*0.025));
            planetAtmo.setFillColor(new Color(200, 80, 160, (int)(14 + p*22)));
        }

        for (int i = 0; i < DUST_COUNT; i++) {
            if (dust[i] == null) continue;
            dustX[i] += dustVX[i] + Math.sin(tick*0.03 + i)*0.02;
            dustY[i] += dustVY[i];
            if (dustY[i] < H*0.40) { dustY[i]=H*0.72; dustX[i]=Math.random()*W; }
            if (dustX[i]<0) dustX[i]=W;
            if (dustX[i]>W) dustX[i]=0;
            Color dc = new Color(200,150,230,Math.max(0,Math.min(255,(int)(dustAlpha[i]*180))));
            dust[i].setFillColor(dc); dust[i].setColor(dc);
            dust[i].setLocation(dustX[i], dustY[i]);
        }

        if (dialogueBoxGlow != null) {
            float p = (float)(0.5 + 0.5*Math.sin(tick*0.04));
            dialogueBoxGlow.setFillColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),(int)(10+p*18)));
            dialogueBoxGlow.setColor(new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),(int)(35+p*55)));
        }

        if (continueButton != null && !continueHovered) {
            float p = (float)(0.5 + 0.5*Math.sin(tick*0.06));
            continueButton.setColor(new Color(
                PANEL_BORDER.getRed(), PANEL_BORDER.getGreen(), PANEL_BORDER.getBlue(),
                (int)(110 + p*145)));
        }

        if (progressDots != null && dialogueIndex < progressDots.length) {
            float p = (float)(0.5 + 0.5*Math.sin(tick*0.08));
            progressDots[dialogueIndex].setFillColor(
                new Color(GOLD.getRed(),GOLD.getGreen(),GOLD.getBlue(),(int)(160+p*95)));
        }
    }

    private void advanceDialogue() {
        if (progressDots != null && dialogueIndex < progressDots.length) {
            progressDots[dialogueIndex].setFillColor(new Color(60,55,40));
            progressDots[dialogueIndex].setColor(new Color(90,80,55));
        }
        dialogueIndex++;
        if (dialogueIndex < dialogue.length) {
            dialogueText.setLabel(dialogue[dialogueIndex]);
            if (progressDots != null && dialogueIndex < progressDots.length) {
                progressDots[dialogueIndex].setFillColor(GOLD);
                progressDots[dialogueIndex].setColor(GOLD);
            }
        } else {
            mainScreen.switchToLevelSelectScreen();
        }
    }

    @Override
    public void hideContent() {
        if (animTimer != null) { animTimer.stop(); animTimer = null; }
        for (GObject item : contents) mainScreen.remove(item);
        contents.clear();
        dialogueText=null; continueButton=null; continueLabel=null;
        dialogueBox=null; dialogueBoxGlow=null; progressDots=null; planetAtmo=null;
        for (int i=0; i<STAR_COUNT; i++) bgStars[i]=null;
        for (int i=0; i<DUST_COUNT; i++) dust[i]=null;
        dialogueIndex=0; pressed=false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (pressed) return;
        pressed = true;
        double x=e.getX(), y=e.getY();

        if (continueButton!=null && (continueButton.contains(x,y)||continueLabel.contains(x,y))) {
            mainScreen.switchToLevelSelectScreen();
            return;
        }
        if (backButton!=null && (backButton.contains(x,y)||backLabel.contains(x,y))) {
            mainScreen.switchToSixthBattleScreen();
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
        double x=e.getX(), y=e.getY();

        boolean nb = backButton!=null&&(backButton.contains(x,y)||backLabel.contains(x,y));
        if (nb && !backHovered) {
            backHovered=true;
            backButton.setColor(GOLD);
            backLabel.setColor(GOLD);
        } else if (!nb && backHovered) {
            backHovered=false;
            backButton.setColor(new Color(140,105,35,180));
            backLabel.setColor(TEXT_DIM);
        }

        boolean nc = continueButton!=null&&(continueButton.contains(x,y)||continueLabel.contains(x,y));
        if (nc && !continueHovered) {
            continueHovered=true;
            continueButton.setFillColor(new Color(50,34,10));
            continueButton.setColor(GOLD);
            continueLabel.setColor(new Color(255,240,140));
        } else if (!nc && continueHovered) {
            continueHovered=false;
            continueButton.setFillColor(new Color(30,20,8));
            continueButton.setColor(PANEL_BORDER);
            continueLabel.setColor(GOLD);
        }
    }

    private void add(GObject obj) {
        mainScreen.add(obj);
        contents.add(obj);
    }

    private static GPolygon makeDiamond(double cx, double cy, double r) {
        GPolygon d = new GPolygon();
        d.addVertex(0,-r);
        d.addVertex(r,0);
        d.addVertex(0,r);
        d.addVertex(-r,0);
        d.setLocation(cx, cy);
        return d;
    }
}