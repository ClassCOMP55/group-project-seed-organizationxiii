import acm.graphics.*;
import characters.Hueman;
import java.awt.Color;

public class HuemanStatsScreen extends GraphicsPane {

    private MainApplication app;
    private Hueman hueman;

    public HuemanStatsScreen(Hueman h, MainApplication app) {
        this.hueman = h;
        this.app = app;
    }

    @Override
    public void showContent() {
        drawBackground();
        drawCharacter();
        drawStats();
    }

    @Override
    public void hideContent() {
        app.removeAll();
    }

    private void drawBackground() {
        GRect bg = new GRect(0, 0, MainApplication.WINDOW_WIDTH, MainApplication.WINDOW_HEIGHT);
        bg.setFilled(true);
        bg.setFillColor(Color.BLACK);
        app.add(bg);
    }

    private void drawCharacter() {
        GImage sprite = hueman.getSprite("overworld");
        sprite.setLocation(50, 100);
        app.add(sprite);
    }

    private void drawStats() {
        double x = 250;
        double y = 120;

        GLabel title = new GLabel("Hueman Summary");
        title.setFont("Times New Roman-22");
        title.setColor(Color.WHITE);
        title.setLocation(x, y);
        app.add(title);

        y += 40;

        addLabel("Name: " + hueman.getName(), x, y);
        y += 25;

        addLabel("Color: " + huemanColor(), x, y);
        y += 25;

        addLabel("HP: " + hueman.getHP(), x, y);
        y += 25;

        addLabel("ATK: " + hueman.getBaseAttack(), x, y);
        y += 25;

        addLabel("SPD: " + hueman.calculateSpeed(), x, y);
        y += 25;

        addLabel("Super Meter: " + getMeter(), x, y);
        y += 40;

        addLabel("Ability:", x, y);
        y += 25;

        addLabel("SuperPower", x, y);
        y += 20;

        addLabel("Deals damage = stored meter", x, y);
        y += 20;

        addLabel("Resets meter to 0", x, y);
    }

    private void addLabel(String text, double x, double y) {
        GLabel label = new GLabel(text);
        label.setFont("Times New Roman-16");
        label.setColor(Color.WHITE);
        label.setLocation(x, y);
        app.add(label);
    }

    private String huemanColor() {
        return hueman.getColor().toString();
    }

    private String getMeter() {
        return hueman.getSuperMeter() + " / " + hueman.getMaxSuperMeter();
    }
}