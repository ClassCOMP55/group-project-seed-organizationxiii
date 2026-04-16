package characters;

public class Lavender extends boss {

    public Lavender(int hp, int atk, int spd, String nm) {
        super(hp, atk, spd, nm);
    }

    public boolean fakeOut(Hueman player) {
        System.out.println("Lavender used Fake Out!");
        player.takeDamage((int)(attack * 0.5));
        System.out.println("Hueman flinched!");
        return true;
    }

    private void loadSprites() {
        sprites.put("main", "lavender.png");
    }

    @Override
    public void takeTurn(Hueman player) {
        if (Math.random() < 0.33) {
            fakeOut(player);
            return;
        }

        System.out.println("Lavender summoned a storm!");
        attackOpponent(player);
    }
}

//teegr