import acm.graphics.GLabel;
import acm.graphics.GObject;

public class FirstBattlePane extends GraphicsPane {

    public FirstBattlePane(MainApplication mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void showContent() {
        GLabel text = new GLabel("First Battle Screen");
        text.setFont("Times New Roman-Bold-30");
        text.setLocation(200, 300);

        mainScreen.add(text);
        contents.add(text);
    }

    @Override
    public void hideContent() {
        for (GObject obj : contents) {
            mainScreen.remove(obj);
        }
        contents.clear();
    }
}